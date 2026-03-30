package com.example.demo.geoCode;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/geocode")
public class GeoCodeController {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&accept-language=fr";
    private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search?format=json&q=%s&limit=5&countrycodes=tn";


    @GetMapping("/reverse")
    public ResponseEntity<String> reverseGeocode(@RequestParam double lat, @RequestParam double lon) {
        try {
            String url = String.format(NOMINATIM_REVERSE_URL, lat, lon);
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        } catch (RestClientException e) {
            return ResponseEntity.status(503).body("{\"error\":\"Service unavailable\"}");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchAddress(@RequestParam String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format(NOMINATIM_SEARCH_URL, encodedQuery);
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        } catch (RestClientException e) {
            return ResponseEntity.status(503).body("{\"error\":\"Service unavailable\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid query\"}");
        }
    }
}