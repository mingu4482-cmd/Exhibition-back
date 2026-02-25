package com.example.muse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Service
public class ExhibitionService {

    private final RestClient restClient = RestClient.create();

    @Value("${exhibition.base-url}")
    private String baseUrl;

    @Value("${exhibition.service-key}")
    private String serviceKey;

    @Value("${exhibition.default-page:1}")
    private int defaultPage;

    @Value("${exhibition.default-size:5}")
    private int defaultSize;

    /**
     * ✅ 기본값으로 KOPIS 원본(XML)을 가져오는 메서드
     * (이 메서드에서 에러가 난다고 했으니, 컨트롤러에서 getKopisXml(...)을 직접 호출하는 방식도 같이 제공했어)
     */
    public String getAllExhibitions() {
        return getKopisXml("20250101", "20261231", "11");
    }

    /**
     * ✅ KOPIS 공연/전시 목록 원본(XML) 호출
     * @param stdate yyyyMMdd
     * @param eddate yyyyMMdd
     * @param signgucode 지역코드(서울=11). 전체면 null/"" 가능
     */
    public String getKopisXml(String stdate, String eddate, String signgucode) {

        if (baseUrl == null || baseUrl.isBlank()) {
            return "ERROR: exhibition.base-url is empty";
        }
        if (serviceKey == null || serviceKey.isBlank()) {
            return "ERROR: exhibition.service-key is empty";
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("service", serviceKey)
                .queryParam("stdate", stdate)
                .queryParam("eddate", eddate)
                .queryParam("cpage", defaultPage)
                .queryParam("rows", defaultSize);

        if (signgucode != null && !signgucode.isBlank()) {
            builder.queryParam("signgucode", signgucode);
        }

        String url = builder.build().toUriString();

        System.out.println("KOPIS 요청 URL = " + url);
        System.out.println("serviceKey 길이 = " + (serviceKey == null ? "null" : serviceKey.length()));

        try {
            ResponseEntity<byte[]> res = restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.ALL)
                    .retrieve()
                    .toEntity(byte[].class);

            byte[] bodyBytes = res.getBody();
            if (bodyBytes == null || bodyBytes.length == 0) {
                return "EMPTY_BODY_BYTES (status=" + res.getStatusCode() + ")";
            }

            return new String(bodyBytes, StandardCharsets.UTF_8);

        } catch (RestClientResponseException e) {
            System.out.println("KOPIS 오류 status=" + e.getStatusCode());
            System.out.println("KOPIS 오류 body=" + e.getResponseBodyAsString());
            return "ERROR status=" + e.getStatusCode() + "\n" + e.getResponseBodyAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}