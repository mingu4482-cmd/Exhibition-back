package com.example.muse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SeoulApiService {

    private final RestClient restClient = RestClient.create();

    @Value("${seoul.base-url}")
    private String baseUrl;

    @Value("${seoul.service-key}")
    private String serviceKey;

    @Value("${seoul.service-name}")
    private String serviceName;

    @Value("${seoul.start-index}")
    private int startIndex;

    @Value("${seoul.end-index}")
    private int endIndex;

    public String getSeoulData() {

        String url = baseUrl
                + "/" + serviceKey
                + "/json/"
                + serviceName
                + "/" + startIndex
                + "/" + endIndex;

        System.out.println("서울 API 요청 URL = " + url);

        try {
            ResponseEntity<String> res = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class);

            return res.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "서울 API ERROR: " + e.getMessage();
        }
    }
}
