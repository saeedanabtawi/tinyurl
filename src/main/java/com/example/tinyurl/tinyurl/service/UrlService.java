package com.example.tinyurl.tinyurl.service;

import com.example.tinyurl.tinyurl.model.Url;
import com.example.tinyurl.tinyurl.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    public Url generateShortUrl(String originalUrl) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setCreatedAt(LocalDateTime.now().toString());
        url = urlRepository.save(url);
        
        String shortUrl = Base64.getEncoder().encodeToString(String.valueOf(url.getId()).getBytes());
        url.setShortUrl(shortUrl);
        return urlRepository.save(url);
    }

    public Url getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }
} 