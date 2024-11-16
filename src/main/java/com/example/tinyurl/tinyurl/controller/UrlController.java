package com.example.tinyurl.tinyurl.controller;

import com.example.tinyurl.tinyurl.model.Url;
import com.example.tinyurl.tinyurl.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortUrl(@RequestBody String originalUrl) {
        Url url = urlService.generateShortUrl(originalUrl);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortUrl) {
        Url url = urlService.getOriginalUrl(shortUrl);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/urls")
    public List<Url> getAllUrls() {
        return urlService.getAllUrls();
    }
} 