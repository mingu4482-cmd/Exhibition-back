package com.example.muse.controller;

import com.example.muse.service.KopisImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final KopisImportService kopisImportService;

    /**
     * ✅ KOPIS import (GET)
     * 예) http://localhost:8080/api/import/kopis?stdate=20250101&eddate=20261231&rows=100&signgucode=11
     * 예) http://localhost:8080/api/import/kopis   (기본값으로 실행)
     */
    @GetMapping("/kopis")
    public String importKopis(
            @RequestParam(defaultValue = "20250101") String stdate,
            @RequestParam(defaultValue = "20261231") String eddate,
            @RequestParam(defaultValue = "1") int cpage,
            @RequestParam(defaultValue = "100") int rows,
            @RequestParam(defaultValue = "11") String signgucode
    ) {
        int saved = kopisImportService.importKopis(stdate, eddate, cpage, rows, signgucode);
        return "KOPIS import OK. saved=" + saved;
    }

    /**
     * ✅ 재적재용: clear=true면 KOPIS 데이터 삭제 후 다시 import
     * 예) http://localhost:8080/api/import/kopis/reload?clear=true
     * 예) http://localhost:8080/api/import/kopis/reload?clear=true&rows=200
     */
    @GetMapping("/kopis/reload")
    public String reloadKopis(
            @RequestParam(defaultValue = "false") boolean clear,
            @RequestParam(defaultValue = "20250101") String stdate,
            @RequestParam(defaultValue = "20261231") String eddate,
            @RequestParam(defaultValue = "1") int cpage,
            @RequestParam(defaultValue = "100") int rows,
            @RequestParam(defaultValue = "11") String signgucode
    ) {
        if (clear) {
            kopisImportService.clearImportedData();
        }
        int saved = kopisImportService.importKopis(stdate, eddate, cpage, rows, signgucode);
        return "KOPIS reload OK. cleared=" + clear + ", saved=" + saved;
    }

    // (선택) POST 유지하고 싶으면 남겨도 됨
    @PostMapping("/kopis")
    public String importKopisPost(
            @RequestParam String stdate,
            @RequestParam String eddate,
            @RequestParam(defaultValue = "1") int cpage,
            @RequestParam(defaultValue = "100") int rows,
            @RequestParam(defaultValue = "11") String signgucode
    ) {
        int saved = kopisImportService.importKopis(stdate, eddate, cpage, rows, signgucode);
        return "KOPIS import OK. saved=" + saved;
    }
}