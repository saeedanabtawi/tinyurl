package com.example.tinyurl.tinyurl.controller;

import com.example.tinyurl.tinyurl.service.UrlService;
import com.example.tinyurl.tinyurl.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    
    @Autowired
    private UrlService urlService;

    @GetMapping("/clicks")
    public long getTotalClicks() {
        return urlService.getTotalClicks();
    }

    @GetMapping("/top")
    public List<Url> getTopUrls() {
        return urlService.getTopUrls(5); // Return top 5 URLs
    }
} 