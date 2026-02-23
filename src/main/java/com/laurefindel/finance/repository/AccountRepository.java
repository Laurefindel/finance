package com.laurefindel.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    List<Account> findByCurrency(Currency currency);
    List<Account> findByUserIdAndCurrency(Long userId, Currency currency);
}
