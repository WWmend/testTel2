package com.multicwedemo.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CredentialStore {

    public void save(String username, String password) throws Exception {
        Path path = Path.of("/var/app/credentials.txt");
        String record = username + ":" + password + "\n";
        Files.write(path, record.getBytes(StandardCharsets.UTF_8));
    }
}
