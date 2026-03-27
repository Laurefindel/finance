package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.dto.FinancialOperationSearchCriteria;
import com.laurefindel.finance.mapper.FinancialOperationMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import com.laurefindel.finance.repository.FinancialOperationRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class FinancialOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(FinancialOperationService.class);
    private static final String SORT_PROPERTY_CREATED_AT_JPQL = "createdAt";
    private static final String SORT_PROPERTY_CREATED_AT_NATIVE = "created_at";

    private final FinancialOperationRepository repository;
    private final AccountService accountService;
    private final FinancialOperationMapper mapper;
    private final Map<FinancialOperationSearchKey, Page<FinancialOperationResponseDto>> operationSearchIndex =
        new HashMap<>();

    public FinancialOperationService(FinancialOperationRepository repository,
         AccountService accountService, FinancialOperationMapper mapper) {
        this.repository = repository;
        this.accountService = accountService;
        this.mapper = mapper;
    }

    public List<FinancialOperationResponseDto> getAll() {
        List<FinancialOperationResponseDto> operations = repository.findAll()
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
        LOG.debug("Fetched all operations count={}", operations.size());
        return operations;
    }

    public FinancialOperationResponseDto getById(Long id) {
        LOG.debug("Fetching operation by id");
        return mapper.toFinancialOperationResponseDto(repository.findById(id).orElseThrow());
    }

    public List<FinancialOperationResponseDto> getBySender(Long senderUserId) {
        List<FinancialOperationResponseDto> operations = repository.findBySenderAccount_User_Id(senderUserId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
        LOG.debug("Fetched operations by sender user id, count={}", operations.size());
        return operations;
    }

    public void delete(Long id) {
        LOG.info("Deleting operation");
        repository.deleteById(id);
        invalidateSearchIndex();
    }

    public List<FinancialOperationResponseDto> getByReceiver(Long receiverUserId) {
        List<FinancialOperationResponseDto> operations = repository.findByReceiverAccount_User_Id(receiverUserId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
        LOG.debug("Fetched operations by receiver user id, count={}", operations.size());
        return operations;
    }

    public List<FinancialOperationResponseDto> getBySenderAccount(Long accountId) {
        List<FinancialOperationResponseDto> operations = repository.findBySenderAccountId(accountId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
        LOG.debug("Fetched operations by sender account id, count={}", operations.size());
        return operations;
    }

    public List<FinancialOperationResponseDto> getByReceiverAccount(Long accountId) {
        List<FinancialOperationResponseDto> operations = repository.findByReceiverAccountId(accountId)
            .stream()
            .map(mapper::toFinancialOperationResponseDto)
            .toList();
        LOG.debug("Fetched operations by receiver account id, count={}", operations.size());
        return operations;
    }

    public List<FinancialOperationResponseDto> getByCurrency(Currency currency) {
        List<FinancialOperationResponseDto> operations = repository.findByCurrency(currency)
                .stream()
                .map(mapper::toFinancialOperationResponseDto)
                .toList();
        LOG.debug("Fetched operations by currency, count={}", operations.size());
        return operations;
    }

    public Page<FinancialOperationResponseDto> searchWithFilters(
        FinancialOperationSearchCriteria criteria,
        Pageable pageable,
        boolean useNativeQuery
    ) {
        LOG.debug("Searching operations with filters");

        String normalizedCurrencyCode = criteria.getCurrencyCode() == null
            ? null
            : criteria.getCurrencyCode().trim().toUpperCase();
        if (normalizedCurrencyCode != null && normalizedCurrencyCode.isBlank()) {
            normalizedCurrencyCode = null;
        }
        criteria.setCurrencyCode(normalizedCurrencyCode);
        Pageable effectivePageable = toEffectivePageable(pageable, useNativeQuery);
        FinancialOperationSearchKey key = new FinancialOperationSearchKey(
            criteria,
            effectivePageable,
            useNativeQuery
        );

        Page<FinancialOperationResponseDto> cached = operationSearchIndex.get(key);
        if (cached != null) {
            LOG.debug("CACHE HIT: searchWithFilters");
            return cached;
        }

        LOG.debug("CACHE MISS: searchWithFilters");

        Page<FinancialOperation> operationsPage = useNativeQuery
            ? repository.searchWithFiltersNative(criteria, effectivePageable)
            : repository.searchWithFiltersJpql(criteria, effectivePageable);

        Page<FinancialOperationResponseDto> resultPage = operationsPage
            .map(mapper::toFinancialOperationResponseDto);
        operationSearchIndex.put(key, resultPage);
        LOG.debug("Search completed totalElements={} totalPages={}",
            resultPage.getTotalElements(), resultPage.getTotalPages());
        return resultPage;
    }

    private Pageable toEffectivePageable(Pageable pageable, boolean useNativeQuery) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
            .map(order -> new Sort.Order(
                order.getDirection(),
                normalizeSortProperty(order.getProperty(), useNativeQuery)
            ))
            .toList();

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(safeOrders));
    }

    private String normalizeSortProperty(String property, boolean useNativeQuery) {
        String normalized = property == null ? "" : property.trim();

        return switch (normalized) {
            case "id" -> "id";
            case "amount" -> "amount";
            case SORT_PROPERTY_CREATED_AT_JPQL, SORT_PROPERTY_CREATED_AT_NATIVE -> useNativeQuery
                ? SORT_PROPERTY_CREATED_AT_NATIVE
                : SORT_PROPERTY_CREATED_AT_JPQL;
            default -> {
                LOG.warn("Unsupported sort property. Fallback to createdAt");
                yield useNativeQuery ? SORT_PROPERTY_CREATED_AT_NATIVE : SORT_PROPERTY_CREATED_AT_JPQL;
            }
        };
    }

    @Transactional
    public FinancialOperationResponseDto doOperation(FinancialOperationRequestDto dto) {
        LOG.info("Starting financial operation");

        Account sender = accountService.getEntityById(dto.getSenderAccountId());
        Account receiver = accountService.getEntityById(dto.getReceiverAccountId());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            LOG.warn("Rejected operation with non-positive amount");
            throw new IllegalArgumentException("Incorrect amount");
        }

        receiver.setBalance(
            receiver.getBalance().add(dto.getAmount())
        );
        Currency currency = sender.getCurrency();
        FinancialOperation operation = mapper.toFinancialOperation(
            dto,
            sender,
            receiver,
            currency
        );
        repository.save(operation);
        invalidateSearchIndex();
        LOG.info("Operation completed id={}", operation.getId());
        return mapper.toFinancialOperationResponseDto(operation);
    }

    @Transactional
    public List<FinancialOperationResponseDto> doBulkOperation(List<FinancialOperationRequestDto> operations) {
        List<FinancialOperationRequestDto> safeOperations = Optional.ofNullable(operations)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException("Operations list cannot be empty"));

        LOG.info("Starting bulk financial operation size={}", safeOperations.size());
        List<FinancialOperationResponseDto> result = safeOperations.stream()
            .map(this::doOperation)
            .toList();
        LOG.info("Bulk financial operation completed size={}", result.size());
        return result;
    }

    public List<FinancialOperationResponseDto> doBulkOperationWithoutTransaction(
        List<FinancialOperationRequestDto> operations
    ) {
        List<FinancialOperationRequestDto> safeOperations = Optional.ofNullable(operations)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException("Operations list cannot be empty"));

        LOG.info("Starting bulk financial operation WITHOUT transaction size={}", safeOperations.size());
        List<FinancialOperationResponseDto> result = safeOperations.stream()
            .map(dto -> {
                try {
                    return doOperation(dto);
                } catch (RuntimeException ex) {
                    LOG.warn("Skipping failed operation in non-transactional bulk: {}", ex.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
        LOG.info("Bulk financial operation WITHOUT transaction completed size={}", result.size());
        return result;
    }

    public void invalidateSearchIndex() {
        int previousSize = operationSearchIndex.size();
        operationSearchIndex.clear();
        LOG.debug("CACHE INVALIDATED: operationSearchIndex cleared (previousSize={})", previousSize);
    }
}
