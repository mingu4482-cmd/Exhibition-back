package com.example.muse.controller;


import com.example.muse.service.ExhibitionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final ExhibitionService exhibitionService;

    public HelloController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @GetMapping
    public String getExhibitions() {
        return exhibitionService.getAllExhibitions();
    }
}
