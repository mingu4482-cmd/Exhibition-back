package com.example.muse.service;

import com.example.muse.domain.Performance;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class KopisImportService {

    private final PerformanceService performanceService;

    private final RestClient restClient = RestClient.create();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Value("${exhibition.base-url}")
    private String baseUrl;

    @Value("${exhibition.service-key}")
    private String serviceKey;

    private static final DateTimeFormatter KOPIS_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // ✅ KOPIS로 적재된 데이터만 삭제(재적재용)
    public void clearImportedData() {
        performanceService.deleteBySource("KOPIS");
    }

    public int importKopis(String stdate, String eddate, int cpage, int rows, String signgucode) {

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("service", serviceKey)
                .queryParam("stdate", stdate)
                .queryParam("eddate", eddate)
                .queryParam("cpage", cpage)
                .queryParam("rows", rows)
                .queryParam("signgucode", signgucode)
                .build(true)
                .toUriString();

        System.out.println("KOPIS IMPORT URL = " + url);

        byte[] bodyBytes = restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.ALL)
                .retrieve()
                .body(byte[].class);

        if (bodyBytes == null || bodyBytes.length == 0) return 0;

        String xml = new String(bodyBytes, StandardCharsets.UTF_8);
        return parseAndSaveFromXml(xml);
    }

    // ✅ XML -> JsonNode 파싱 후 db 노드 찾아 저장
    private int parseAndSaveFromXml(String xml) {
        try {
            JsonNode root = xmlMapper.readTree(xml);

            // 1) 보통 구조: response -> dbs -> db
            JsonNode dbNode = root.findPath("dbs").path("db");

            // 2) 예외 대비: 어디든 있는 db 탐색
            if (dbNode.isMissingNode() || dbNode.isNull()) {
                dbNode = root.findPath("db");
            }

            if (dbNode.isMissingNode() || dbNode.isNull()) return 0;

            int saved = 0;

            if (dbNode.isArray()) {
                for (JsonNode db : dbNode) {
                    saved += upsertOne(db);
                }
            } else {
                saved += upsertOne(dbNode);
            }

            return saved;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ✅ 핵심: upsert()는 void라서 return 하면 안 됨
    private int upsertOne(JsonNode db) {
        String mt20id = text(db, "mt20id");
        if (mt20id == null || mt20id.isBlank()) return 0;

        Performance p = Performance.builder()
                .source("KOPIS")
                .sourceId(mt20id)
                .title(nvl(text(db, "prfnm")))
                .startDate(parseDate(text(db, "prfpdfrom")))
                .endDate(parseDate(text(db, "prfpdto")))
                .venueName(nvl(text(db, "fcltynm")))
                .area(nvl(text(db, "area")))
                .genre(nvl(text(db, "genrenm")))
                .posterUrl(nvl(text(db, "poster")))
                .build();

        performanceService.upsert(p);
        return 1;
    }

    private static String text(JsonNode parent, String field) {
        JsonNode n = parent.get(field);
        if (n == null || n.isNull()) return null;
        String v = n.asText();
        return v == null ? null : v.trim();
    }

    private static String nvl(String s) {
        return s == null ? "" : s.trim();
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim(), KOPIS_DATE);
    }
}