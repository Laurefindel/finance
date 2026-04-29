package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.dto.FinancialOperationSearchCriteria;
import com.laurefindel.finance.exceptions.PartialBulkOperationException;
import com.laurefindel.finance.mapper.FinancialOperationMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import com.laurefindel.finance.repository.FinancialOperationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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

    @Mock
    private FinancialOperationSearchCache operationSearchCache;

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

        when(accountService.getEntityById(1L)).thenReturn(Optional.of(sender));
        when(accountService.getEntityById(2L)).thenReturn(Optional.of(receiver));
        when(mapper.toFinancialOperation(request, sender, receiver, currency)).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        FinancialOperationResponseDto result = service.doOperation(request);

        assertEquals(new BigDecimal("400.00"), sender.getBalance());
        assertEquals(new BigDecimal("150.00"), receiver.getBalance());
        assertEquals(response, result);
        verify(repository).save(operation);
        verify(accountService).save(sender);
        verify(accountService).save(receiver);
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

        when(accountService.getEntityById(1L)).thenReturn(Optional.of(sender));
        when(accountService.getEntityById(2L)).thenReturn(Optional.of(receiver));
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

        when(accountService.getEntityById(1L)).thenReturn(Optional.of(sender));
        when(accountService.getEntityById(2L)).thenReturn(Optional.of(receiver));
        when(accountService.getEntityById(99L)).thenThrow(new IllegalArgumentException("Account not found"));
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        List<FinancialOperationRequestDto> operations = List.of(ok, broken);
        assertThrows(IllegalArgumentException.class, () -> service.doBulkOperation(operations));
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldPartiallySaveAndThrowDetailedException() {
        FinancialOperationRequestDto ok = request(1L, 2L, "10.00");
        FinancialOperationRequestDto broken = request(1L, 99L, "10.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "100.00", currency);
        Account receiver = account(2L, "0.00", currency);

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();

        when(accountService.getEntityById(1L)).thenReturn(Optional.of(sender));
        when(accountService.getEntityById(2L)).thenReturn(Optional.of(receiver));
        when(accountService.getEntityById(99L)).thenThrow(new IllegalArgumentException("Account not found"));
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);
        List<FinancialOperationRequestDto> operations = List.of(ok, broken);

        PartialBulkOperationException ex = assertThrows(
            PartialBulkOperationException.class,
            () -> service.doBulkOperationWithoutTransaction(operations)
        );

        assertEquals(1, ex.getSavedCount());
        assertEquals(1, ex.getFailedCount());
        assertEquals("Account not found", ex.getFailedOperations().get("operation_2"));
        verify(repository, times(1)).save(operation);
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldReturnAllResultsWhenNoFailures() {
        FinancialOperationRequestDto first = request(1L, 2L, "10.00");
        FinancialOperationRequestDto second = request(1L, 2L, "15.00");

        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("USD");

        Account sender = account(1L, "100.00", currency);
        Account receiver = account(2L, "0.00", currency);

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();

        when(accountService.getEntityById(1L)).thenReturn(Optional.of(sender));
        when(accountService.getEntityById(2L)).thenReturn(Optional.of(receiver));
        when(mapper.toFinancialOperation(any(FinancialOperationRequestDto.class), any(Account.class),
            any(Account.class), any(Currency.class))).thenReturn(operation);
        when(repository.save(operation)).thenReturn(operation);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        List<FinancialOperationResponseDto> result =
            service.doBulkOperationWithoutTransaction(List.of(first, second));

        assertEquals(2, result.size());
        verify(repository, times(2)).save(operation);
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldUseExceptionClassNameWhenMessageIsNull() {
        FinancialOperationRequestDto broken = request(1L, 99L, "10.00");
        List<FinancialOperationRequestDto> operations = List.of(broken);

        when(accountService.getEntityById(1L)).thenThrow(new RuntimeException());

        PartialBulkOperationException ex = assertThrows(
            PartialBulkOperationException.class,
            () -> service.doBulkOperationWithoutTransaction(operations)
        );

        assertEquals("RuntimeException", ex.getFailedOperations().get("operation_1"));
    }

    @Test
    void getAll_shouldMapAllOperations() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findAll()).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getById_shouldReturnMappedOperation() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findById(5L)).thenReturn(Optional.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        FinancialOperationResponseDto result = service.getById(5L).orElseThrow();

        assertEquals(response, result);
    }

    @Test
    void getBySender_shouldReturnMappedOperations() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findBySenderAccount_User_Id(1L)).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getBySender(1L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByReceiver_shouldReturnMappedOperations() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findByReceiverAccount_User_Id(2L)).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getByReceiver(2L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getBySenderAccount_shouldReturnMappedOperations() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findBySenderAccountId(10L)).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getBySenderAccount(10L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByReceiverAccount_shouldReturnMappedOperations() {
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findByReceiverAccountId(20L)).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getByReceiverAccount(20L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByCurrency_shouldReturnMappedOperations() {
        Currency currency = new Currency();
        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto response = new FinancialOperationResponseDto();

        when(repository.findByCurrency(currency)).thenReturn(List.of(operation));
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(response);

        List<FinancialOperationResponseDto> result = service.getByCurrency(currency);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void delete_shouldInvalidateCacheAndDeleteById() {
        service.delete(15L);
        verify(repository).deleteById(15L);
    }

    @Test
    void searchWithFilters_shouldUseJpqlAndCacheResult() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        criteria.setCurrencyCode(" usd ");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));

        FinancialOperation operation = new FinancialOperation();
        FinancialOperationResponseDto mapped = new FinancialOperationResponseDto();
        Page<FinancialOperation> operationPage = new PageImpl<>(List.of(operation));

        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(operationPage);
        when(mapper.toFinancialOperationResponseDto(operation)).thenReturn(mapped);

        Page<FinancialOperationResponseDto> first = service.searchWithFilters(criteria, pageable, false);
        Page<FinancialOperationResponseDto> second = service.searchWithFilters(criteria, pageable, false);

        assertEquals("USD", criteria.getCurrencyCode());
        assertEquals(1, first.getTotalElements());
        assertEquals(1, second.getTotalElements());
        verify(repository, times(1)).searchWithFiltersJpql(eq(criteria), any(Pageable.class));
    }

    @Test
    void searchWithFilters_shouldUseNativeAndFallbackSortProperty() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("unsupported"));
        Page<FinancialOperation> operationPage = new PageImpl<>(List.of());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(repository.searchWithFiltersNative(eq(criteria), any(Pageable.class))).thenReturn(operationPage);

        Page<FinancialOperationResponseDto> result = service.searchWithFilters(criteria, pageable, true);

        assertEquals(0, result.getTotalElements());
        verify(repository).searchWithFiltersNative(eq(criteria), pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().iterator().next();
        assertEquals("created_at", order.getProperty());
    }

    @Test
    void searchWithFilters_shouldNormalizeBlankCurrencyToNull() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        criteria.setCurrencyCode("   ");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("amount"));

        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.searchWithFilters(criteria, pageable, false);

        assertEquals(null, criteria.getCurrencyCode());
    }

    @Test
    void doOperation_shouldThrowWhenAmountIsNotPositive() {
        FinancialOperationRequestDto request = request(1L, 2L, "0.00");

        assertThrows(IllegalArgumentException.class, () -> service.doOperation(request));
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById(700L)).thenReturn(Optional.empty());

        assertTrue(service.getById(700L).isEmpty());
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldThrowForNullList() {
        assertThrows(IllegalArgumentException.class, () -> service.doBulkOperationWithoutTransaction(null));
    }

    @Test
    void doBulkOperationWithoutTransaction_shouldThrowForEmptyList() {
        List<FinancialOperationRequestDto> operations = List.of();
        assertThrows(IllegalArgumentException.class,
            () -> service.doBulkOperationWithoutTransaction(operations));
    }

    @Test
    void searchWithFilters_shouldUseCreatedAtForNativeSortAlias() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt"));
        Page<FinancialOperation> operationPage = new PageImpl<>(List.of());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(repository.searchWithFiltersNative(eq(criteria), any(Pageable.class))).thenReturn(operationPage);

        service.searchWithFilters(criteria, pageable, true);

        verify(repository).searchWithFiltersNative(eq(criteria), pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().iterator().next();
        assertEquals("created_at", order.getProperty());
    }

    @Test
    void searchWithFilters_shouldUseCreatedAtForJpqlSortAlias() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("created_at"));
        Page<FinancialOperation> operationPage = new PageImpl<>(List.of());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(operationPage);

        service.searchWithFilters(criteria, pageable, false);

        verify(repository).searchWithFiltersJpql(eq(criteria), pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().iterator().next();
        assertEquals("createdAt", order.getProperty());
    }

    @Test
    void searchWithFilters_shouldKeepNullCurrencyCodeWhenInitiallyNull() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.searchWithFilters(criteria, pageable, false);

        assertNull(criteria.getCurrencyCode());
    }

    @Test
    void searchWithFilters_shouldKeepUnsortedPageableAsIs() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.searchWithFilters(criteria, pageable, false);

        verify(repository).searchWithFiltersJpql(criteria, pageable);
    }

    @Test
    void searchWithFilters_shouldFallbackToCreatedAtForUnsupportedJpqlSort() {
        FinancialOperationSearchCriteria criteria = new FinancialOperationSearchCriteria();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("unsupported"));
        Page<FinancialOperation> operationPage = new PageImpl<>(List.of());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(repository.searchWithFiltersJpql(eq(criteria), any(Pageable.class))).thenReturn(operationPage);

        service.searchWithFilters(criteria, pageable, false);

        verify(repository).searchWithFiltersJpql(eq(criteria), pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().iterator().next();
        assertEquals("createdAt", order.getProperty());
    }

    @Test
    void normalizeSortProperty_shouldFallbackWhenPropertyIsNull() throws Exception {
        Method method = FinancialOperationService.class
            .getDeclaredMethod("normalizeSortProperty", String.class, boolean.class);
        method.setAccessible(true);

        String jpqlProperty = (String) method.invoke(service, null, false);
        String nativeProperty = (String) method.invoke(service, null, true);

        assertEquals("createdAt", jpqlProperty);
        assertEquals("created_at", nativeProperty);
    }

    @Test
    void toEffectivePageable_shouldReturnNullWhenPageableIsNull() throws Exception {
        Method method = FinancialOperationService.class
            .getDeclaredMethod("toEffectivePageable", Pageable.class, boolean.class);
        method.setAccessible(true);

        Pageable result = (Pageable) method.invoke(service, null, false);

        assertNull(result);
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
