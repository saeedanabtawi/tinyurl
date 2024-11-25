package com.example.tinyurl.tinyurl.service;

import com.example.tinyurl.tinyurl.model.Url;
import com.example.tinyurl.tinyurl.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    private static final int DEFAULT_SHORT_URL_LENGTH = 6;
    private static final int TIMEOUT_MILLISECONDS = 5000;

    public Url generateShortUrl(String originalUrl) {
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setCreatedAt(LocalDateTime.now().toString());        
        url.setShortUrl(generateRandomString(DEFAULT_SHORT_URL_LENGTH));
        url.setStatus(checkUrlStatus(originalUrl));
        return urlRepository.save(url);
    }

    public Url getOriginalUrl(String shortUrl, String userAgent, String ipAddress) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url != null) {
            url.setClicks(url.getClicks() + 1);
            url.getClickTimestamps().add(LocalDateTime.now().toString());
            url.setLastAccessBrowser(parseUserAgent(userAgent));
            url.setLastAccessDevice(parseDevice(userAgent));
            url.setLastAccessIp(ipAddress);
            urlRepository.save(url);
        }
        return url;
    }

    private String parseUserAgent(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Other";
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile")) return "Mobile";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Desktop";
    }

    public Url getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url != null) {
            url.setClicks(url.getClicks() + 1);
            urlRepository.save(url);
        }
        return url;
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

    private String checkUrlStatus(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(TIMEOUT_MILLISECONDS);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400) ? "active" : "dead";
        } catch (Exception e) {
            return "dead";
        }
    }

    public void updateAllUrlStatuses() {
        List<Url> urls = urlRepository.findAll();
        for (Url url : urls) {
            url.setStatus(checkUrlStatus(url.getOriginalUrl()));
            urlRepository.save(url);
        }
    }

    @Scheduled(fixedRateString = "${url.status.check.interval:3600000}")
    public void scheduledStatusCheck() {
        log.info("Starting scheduled URL status check");
        updateAllUrlStatuses();
        log.info("Completed scheduled URL status check");
    }

    public long getTotalClicks() {
        return urlRepository.findAll().stream()
                .mapToLong(Url::getClicks)
                .sum();
    }

    public List<Url> getTopUrls(int limit) {
        return urlRepository.findAll().stream()
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getClicksByHour(String shortUrl, int hours) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url == null) return new HashMap<>();

        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        
        return url.getClickTimestamps().stream()
            .map(timestamp -> LocalDateTime.parse(timestamp))
            .filter(timestamp -> timestamp.isAfter(cutoff))
            .collect(Collectors.groupingBy(
                timestamp -> timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00")),
                Collectors.counting()
            ));
    }

} 