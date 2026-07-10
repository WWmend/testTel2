package com.example.cwe312.playcookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class SecondSecure {

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
    String username = String.valueOf(body.getOrDefault("username", ""));
    String password = String.valueOf(body.getOrDefault("password", ""));
    String apiKey = String.valueOf(body.getOrDefault("apiKey", ""));
    String payload = username + ":" + password + ":" + apiKey;
    String encoded =
        Base64.getUrlEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    ResponseCookie cookie =
        ResponseCookie.from("ACCOUNT_SESSION", encoded)
            .path("/")
            .httpOnly(false)
            .secure(false)
            .maxAge(3600)
            .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(Map.of("ok", true));
  }
}
