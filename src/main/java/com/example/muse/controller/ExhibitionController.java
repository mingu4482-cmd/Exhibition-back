package com.example.muse.controller;

import com.example.muse.service.ExhibitionService;
import com.example.muse.service.SeoulApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;
    private final SeoulApiService seoulApiService;

    /**
     * ✅ KOPIS 원본(XML) 그대로 반환
     * 기본값으로도 실행 가능
     *
     * 예)
     * http://localhost:8080/api/exhibitions/kopis
     * http://localhost:8080/api/exhibitions/kopis?stdate=20250101&eddate=20261231&signgucode=11
     */
    @GetMapping(value = "/kopis", produces = "application/xml;charset=UTF-8")
    public String kopisRaw(
            @RequestParam(defaultValue = "20250101") String stdate,
            @RequestParam(defaultValue = "20261231") String eddate,
            @RequestParam(defaultValue = "11") String signgucode
    ) {
        return exhibitionService.getKopisXml(stdate, eddate, signgucode);
    }

    /**
     * ✅ 서울 열린데이터광장 원본(JSON) 그대로 반환
     * seoulApiService.getSeoulData()가 String(JSON) 반환이면 그대로 OK
     */
    @GetMapping(value = "/seoul", produces = "application/json;charset=UTF-8")
    public String seoulRaw() {
        return seoulApiService.getSeoulData();
    }

    /**
     * ✅ (선택) getAllExhibitions() 확인용 엔드포인트
     * getAllExhibitions() 에러가 나면 이 엔드포인트는 쓰지 말고 위 /kopis로 테스트하면 됨
     */
    @GetMapping(value = "/kopis/default", produces = "application/xml;charset=UTF-8")
    public String kopisDefaultRaw() {
        return exhibitionService.getAllExhibitions();
    }
}