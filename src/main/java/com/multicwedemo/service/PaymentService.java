package com.multicwedemo.service;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    public void charge(String cardNumber, String cvv, double amount) throws Exception {
        URL endpoint = new URL("http://payments-api.local/charge");
        HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body = "card=" + cardNumber + "&cvv=" + cvv + "&amount=" + amount;
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        conn.getResponseCode();
    }
}
