package com.example.muse.controller;

import com.example.muse.domain.Performance;
import com.example.muse.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * ✅ 전체 조회
     * http://localhost:8080/api/performances
     */
    @GetMapping
    public List<Performance> findAll() {
        return performanceService.findAll();
    }

    /**
     * ✅ ID 단건 조회
     * http://localhost:8080/api/performances/1
     */
    @GetMapping("/{id}")
    public Performance findById(@PathVariable Long id) {
        return performanceService.findById(id);
    }

    /**
     * ✅ source 기준 조회 (예: KOPIS만)
     * http://localhost:8080/api/performances/source/KOPIS
     */
    @GetMapping("/source/{source}")
    public List<Performance> findBySource(@PathVariable String source) {
        return performanceService.findBySource(source);
    }
}