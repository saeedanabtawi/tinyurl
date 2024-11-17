package com.example.tinyurl.tinyurl.service;

import com.example.tinyurl.tinyurl.model.Url;
import com.example.tinyurl.tinyurl.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    private static final int DEFAULT_SHORT_URL_LENGTH = 6;

    public Url generateShortUrl(String originalUrl) {
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setCreatedAt(LocalDateTime.now().toString());        
        url.setShortUrl(generateRandomString(DEFAULT_SHORT_URL_LENGTH));
        return urlRepository.save(url);
    }

    public Url getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }

    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        return random.ints(length, 0, chars.length())
                    .mapToObj(chars::charAt)
                    .map(Object::toString)
                    .collect(Collectors.joining());
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url != null) {
            urlRepository.delete(url);
        } else {
            throw new IllegalArgumentException("URL not found");
        }
    }
} 