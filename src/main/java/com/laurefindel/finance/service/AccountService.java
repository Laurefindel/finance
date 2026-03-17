package com.laurefindel.finance.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.mapper.AccountMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.AccountRepository;
import com.laurefindel.finance.repository.CurrencyRepository;
import com.laurefindel.finance.repository.UserRepository;

@Service
public class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, AccountMapper mapper,
         CurrencyRepository currencyRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDto getById(Long id) {
        LOG.debug("Fetching account by id");
        return mapper.toAccountResponseDto(accountRepository
            .findById(id)
            .orElseThrow());
    }

    public AccountResponseDto save(AccountRequestDto account) {
        LOG.info("Creating account");
        User user = userRepository.findById(account.getUserId()).orElseThrow();
        Account accountEntity = mapper.toAccount(account);
        Currency currency = currencyRepository.findById(account.getCurrencyId()).orElseThrow();
        accountEntity.setCurrency(currency);
        accountEntity.setUser(user);
        accountEntity.setStatus("ACTIVE");
        accountEntity.setBalance(BigDecimal.ZERO);
        user.getAccounts().add(accountEntity);
        userRepository.save(user);
        LOG.info("Account created id={} for userId={}", accountEntity.getId(), user.getId());
        return mapper.toAccountResponseDto(accountEntity);
    }

    public void delete(Long id) {
        LOG.info("Deleting account");
        accountRepository.deleteById(id);
    }

    public List<AccountResponseDto> getByUserId(Long userId) {
        List<AccountResponseDto> accounts = accountRepository.findByUserId(userId).stream()
            .map(mapper::toAccountResponseDto).toList();
        LOG.debug("Fetched accounts by user id, count={}", accounts.size());
        return accounts;
    }

    public List<AccountResponseDto> getByCurrency(Currency currency) {
        List<AccountResponseDto> accounts = accountRepository.findByCurrency(currency).stream()
            .map(mapper::toAccountResponseDto).toList();
        LOG.debug("Fetched accounts by currency, count={}", accounts.size());
        return accounts;
    }

    public List<AccountResponseDto> getByUserIdAndCurrency(Long userId, Currency currency) {
        List<AccountResponseDto> accounts = accountRepository.findByUserIdAndCurrency(userId, currency)
            .stream().map(mapper::toAccountResponseDto).toList();
        LOG.debug("Fetched accounts by user id and currency, count={}", accounts.size());
        return accounts;
    }

    public List<AccountResponseDto> getAll() {
        List<AccountResponseDto> accounts = accountRepository.findAll().stream()
            .map(mapper::toAccountResponseDto).toList();
        LOG.debug("Fetched all accounts count={}", accounts.size());
        return accounts;
    }

    public Account getEntityById(Long id) {
        LOG.debug("Fetching account entity by id");
        return accountRepository.findById(id).orElseThrow();
    }

    public AccountResponseDto replenish(Long id, BigDecimal amount) {
        LOG.info("Replenishing account");
        Account account = accountRepository.findById(id).orElseThrow();
        account.setBalance(account.getBalance().add(amount));
        Account savedAccount = accountRepository.save(account);
        LOG.info("Account replenished id={} newBalance={}", savedAccount.getId(), savedAccount.getBalance());
        return mapper.toAccountResponseDto(savedAccount);
    }

    public Account save(Account account) {
        LOG.debug("Saving account entity");
        return accountRepository.save(account);
    }
}
