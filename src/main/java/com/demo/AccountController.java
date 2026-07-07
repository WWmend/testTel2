package com.demo;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository repo;

    public AccountController(AccountRepository repo) {
        this.repo = repo;
    }

    // CWE-639 (IDOR): resolves the account by the path id only; the caller's
    // identity is never compared to the owner, so any user can read any account.
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    // CWE-639 (IDOR): same pattern — deletes by path id with no ownership check.
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // CWE-319: sends the card number to an http:// endpoint in cleartext (single call).
    @PostMapping("/charge")
    public void charge(@RequestParam String cardNumber) throws Exception {
        new URL("http://payments-api.local/charge?card=" + cardNumber).openStream();
    }

    // CWE-312: writes the user's password to disk in cleartext.
    @PostMapping("/credentials")
    public void saveCredentials(@RequestParam String username, @RequestParam String password) throws Exception {
        Path path = Path.of("/var/app/credentials.txt");
        Files.write(path, (username + ":" + password + "\n").getBytes(StandardCharsets.UTF_8));
    }
}
