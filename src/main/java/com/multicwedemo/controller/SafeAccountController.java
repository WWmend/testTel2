package com.multicwedemo.controller;

import com.multicwedemo.entity.Account;
import com.multicwedemo.repository.AccountRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/safe/accounts")
public class SafeAccountController {

    private final AccountRepository repo;

    public SafeAccountController(AccountRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id, @AuthenticationPrincipal User principal) {
        Account acct = repo.findById(id).orElseThrow();
        if (!acct.getUserId().toString().equals(principal.getUsername())) {
            throw new AccessDeniedException("not yours");
        }
        return acct;
    }
}
