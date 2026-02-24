package com.laurefindel.finance.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "senderAccount")
    private List<FinancialOperation> outcomingOperations;

    @OneToMany(mappedBy = "receiverAccount")
    private List<FinancialOperation> incomingOperations;

    @Column(nullable = false)
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<FinancialOperation> getOutcomingOperations() {
        return outcomingOperations;
    }

    public void setOutcomingOperations(List<FinancialOperation> outcomingOperations) {
        this.outcomingOperations = outcomingOperations;
    }

    public List<FinancialOperation> getIncomingOperations() {
        return incomingOperations;
    }

    public void setIncomingOperations(List<FinancialOperation> incomingOperations) {
        this.incomingOperations = incomingOperations;
    }
}
