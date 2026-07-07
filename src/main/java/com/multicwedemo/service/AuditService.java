package com.multicwedemo.service;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
public class AuditService {

    public void emit(String username, String token) throws Exception {
        try (Socket socket = new Socket("audit-host.local", 514)) {
            OutputStream out = socket.getOutputStream();
            String record = "user=" + username + " token=" + token;
            out.write(record.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }
}
