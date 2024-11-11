package com.example.tinyurl.tinyurl.repository;

import com.example.tinyurl.tinyurl.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByShortUrl(String shortUrl);
} 