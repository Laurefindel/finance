package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.mapper.FinancialOperationMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import com.laurefindel.finance.repository.FinancialOperationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialOperationServiceTest {

    @Mock
    private FinancialOperationRepository repository;

    @Mock
    private AccountService accountService;

    @Mock
    private FinancialOperationMapper mapper;

    @InjectMocks
    private FinancialOperationService service;

    @Test
    void doOperation_shouldTransferMoneyAndReturnMappedResponse() {
        FinancialOperationRequestDto request = request(1L, 2L, "100.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "500.00", currency);
        Account receiver = account(2L, "50.00", currency);

        FinancialOperation operation = new FinancialOperation();
        operation.setId(20L);

        FinancialOperationResponseDto response = new FinancialOperationResponseDto();
        response.setId(20L);

        when(accountService.getEntityById(1L)).thenReturn(sender);
        when(accountService.getEntityById(2L)).thenReturn(receiver);
        when(mapper.toFinancialOperation(request, sender, receiver, currency)).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        FinancialOperationResponseDto result = service.doOperation(request);

        assertEquals(new BigDecimal("400.00"), sender.getBalance());
        assertEquals(new BigDecimal("150.00"), receiver.getBalance());
        assertEquals(response, result);
        verify(repository).save(operation);
    }

    @Test
    void doBulkOperation_shouldProcessAllItems() {
        FinancialOperationRequestDto first = request(1L, 2L, "10.00");
        FinancialOperationRequestDto second = request(1L, 2L, "15.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "100.00", currency);
        Account receiver = account(2L, "0.00", currency);

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();

        when(accountService.getEntityById(1L)).thenReturn(sender);
        when(accountService.getEntityById(2L)).thenReturn(receiver);
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        List<FinancialOperationResponseDto> result = service.doBulkOperation(List.of(first, second));

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("75.00"), sender.getBalance());
        assertEquals(new BigDecimal("25.00"), receiver.getBalance());
    }

    @Test
    void doBulkOperation_shouldThrowForEmptyList() {
        List<FinancialOperationRequestDto> emptyOperations = List.of();
        assertThrows(IllegalArgumentException.class, () -> service.doBulkOperation(emptyOperations));
    }

    @Test
    void doBulkOperation_shouldFailWholeBatchWhenAnyItemFails() {
        FinancialOperationRequestDto ok = request(1L, 2L, "10.00");
        FinancialOperationRequestDto broken = request(1L, 99L, "10.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "100.00", currency);
        Account receiver = account(2L, "0.00", currency);

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();

        when(accountService.getEntityById(1L)).thenReturn(sender);
        when(accountService.getEntityById(2L)).thenReturn(receiver);
        when(accountService.getEntityById(99L)).thenThrow(new IllegalArgumentException("Account not found"));
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        List<FinancialOperationRequestDto> operations = List.of(ok, broken);
        assertThrows(IllegalArgumentException.class, () -> service.doBulkOperation(operations));
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldSkipFailedItemsAndContinue() {
        FinancialOperationRequestDto ok = request(1L, 2L, "10.00");
        FinancialOperationRequestDto broken = request(1L, 99L, "10.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "100.00", currency);
        Account receiver = account(2L, "0.00", currency);

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();

        when(accountService.getEntityById(1L)).thenReturn(sender);
        when(accountService.getEntityById(2L)).thenReturn(receiver);
        when(accountService.getEntityById(99L)).thenThrow(new IllegalArgumentException("Account not found"));
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        List<FinancialOperationResponseDto> result = service.doBulkOperationWithoutTransaction(List.of(ok, broken));

        assertEquals(1, result.size());
        assertTrue(result.contains(mapped));
    }

    private FinancialOperationRequestDto request(Long senderId, Long receiverId, String amount) {
        FinancialOperationRequestDto dto = new FinancialOperationRequestDto();
        dto.setSenderAccountId(senderId);
        dto.setReceiverAccountId(receiverId);
        dto.setAmount(new BigDecimal(amount));
        dto.setDescription("bulk");
        return dto;
    }

    private Account account(Long id, String balance, Currency currency) {
        Account account = new Account();
        account.setId(id);
        account.setBalance(new BigDecimal(balance));
        account.setCurrency(currency);
        account.setStatus("ACTIVE");
        return account;
    }
}
