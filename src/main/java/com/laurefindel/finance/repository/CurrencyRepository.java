package com.laurefindel.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laurefindel.finance.model.entity.Currency;
import java.util.List;



public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findByCode(String code);
    List<Currency> findByName(String name);

}
