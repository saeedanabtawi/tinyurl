package com.example.tinyurl.tinyurl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ElementCollection;

@Entity
@Data
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String originalUrl;
    private String shortUrl;
    private String createdAt;
    private String status;
    private String lastAccessBrowser;
    private String lastAccessDevice;
    private String lastAccessIp;
    private Long clicks = 0L;
    
    @ElementCollection
    private List<String> clickTimestamps = new ArrayList<>();
} 