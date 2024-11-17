package com.example.tinyurl.tinyurl.controller;

import com.example.tinyurl.tinyurl.model.Url;
import com.example.tinyurl.tinyurl.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

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
    public ResponseEntity<?> redirectToOriginalUrl(
            @PathVariable String shortUrl,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest request) {
        
        String ipAddress = request.getRemoteAddr();
        Url url = urlService.getOriginalUrl(shortUrl, userAgent, ipAddress);
        
        if (url == null) {
            return new ResponseEntity<>("URL not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.getOriginalUrl())
                .build();
    }

    @GetMapping("/urls")
    public List<Url> getAllUrls() {
        return urlService.getAllUrls();
    }

    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortUrl) {
        try {
            urlService.deleteUrl(shortUrl);
            return new ResponseEntity<>("URL deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
} 