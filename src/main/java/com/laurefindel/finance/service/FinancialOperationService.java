package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.mapper.FinancialOperationMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import com.laurefindel.finance.repository.FinancialOperationRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinancialOperationService {

    private final FinancialOperationRepository repository;
    private final AccountService accountService;
    private final FinancialOperationMapper mapper;

    public FinancialOperationService(FinancialOperationRepository repository,
         AccountService accountService, FinancialOperationMapper mapper) {
        this.repository = repository;
        this.accountService = accountService;
        this.mapper = mapper;
    }

    public List<FinancialOperationResponseDto> getAll() {
        return repository.findAll()
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
    }

    public FinancialOperationResponseDto getById(Long id) {
        return mapper.toFinancialOperationResponseDto(repository.findById(id).orElseThrow());
    }

    public List<FinancialOperationResponseDto> getBySender(Long senderUserId) {
        return repository.findBySenderAccount_User_Id(senderUserId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<FinancialOperationResponseDto> getByReceiver(Long receiverUserId) {
        return repository.findByReceiverAccount_User_Id(receiverUserId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
    }

    public List<FinancialOperationResponseDto> getBySenderAccount(Long accountId) {
        return repository.findBySenderAccountId(accountId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
    }

    public List<FinancialOperationResponseDto> getByReceiverAccount(Long accountId) {
        return repository.findByReceiverAccountId(accountId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
    }

    public List<FinancialOperationResponseDto> getByCurrency(Currency currency) {
        return repository.findByCurrency(currency)
                .stream()
                .map(mapper::toFinancialOperationResponseDto)
                .toList();
    }

    @Transactional
    public FinancialOperationResponseDto doOperation(FinancialOperationRequestDto dto) {
        Account sender = accountService.getEntityById(dto.getSenderAccountId());
        Account receiver = accountService.getEntityById(dto.getReceiverAccountId());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Incorrect amount");
        }

        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        Currency currency = sender.getCurrency();
        FinancialOperation operation = mapper.toFinancialOperation(dto, sender, receiver, currency);
        repository.save(operation);
        return mapper.toFinancialOperationResponseDto(operation);
    }

    public FinancialOperationResponseDto doOperationWithoutTransactional(FinancialOperationRequestDto dto) {
        Account sender = accountService.getEntityById(dto.getSenderAccountId());
        Account receiver = accountService.getEntityById(dto.getReceiverAccountId());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        accountService.save(sender);

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Incorrect amount");
        }

        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        accountService.save(receiver);
        Currency currency = sender.getCurrency();
        FinancialOperation operation = mapper.toFinancialOperation(dto, sender, receiver, currency);
        repository.save(operation);
        return mapper.toFinancialOperationResponseDto(operation);
    }
}
