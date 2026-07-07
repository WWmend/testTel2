package com.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

@Entity
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String holder;
    private double balance;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getHolder() {
        return holder;
    }

    public double getBalance() {
        return balance;
    }
}
