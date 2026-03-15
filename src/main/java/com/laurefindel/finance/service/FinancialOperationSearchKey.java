package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationSearchCriteria;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

public class FinancialOperationSearchKey {

    private final Long senderUserId;
    private final Long receiverUserId;
    private final String currencyCode;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;
    private final int pageNumber;
    private final int pageSize;
    private final String sort;
    private final boolean nativeQuery;

    public FinancialOperationSearchKey(
        FinancialOperationSearchCriteria criteria,
        Pageable pageable,
        boolean nativeQuery
    ) {
        this.senderUserId = criteria.getSenderUserId();
        this.receiverUserId = criteria.getReceiverUserId();
        this.currencyCode = criteria.getCurrencyCode();
        this.minAmount = criteria.getMinAmount();
        this.maxAmount = criteria.getMaxAmount();
        this.fromDate = criteria.getFromDate();
        this.toDate = criteria.getToDate();
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.sort = pageable.getSort().toString();
        this.nativeQuery = nativeQuery;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FinancialOperationSearchKey that = (FinancialOperationSearchKey) obj;
        return pageNumber == that.pageNumber
            && pageSize == that.pageSize
            && nativeQuery == that.nativeQuery
            && Objects.equals(senderUserId, that.senderUserId)
            && Objects.equals(receiverUserId, that.receiverUserId)
            && Objects.equals(currencyCode, that.currencyCode)
            && Objects.equals(minAmount, that.minAmount)
            && Objects.equals(maxAmount, that.maxAmount)
            && Objects.equals(fromDate, that.fromDate)
            && Objects.equals(toDate, that.toDate)
            && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            senderUserId,
            receiverUserId,
            currencyCode,
            minAmount,
            maxAmount,
            fromDate,
            toDate,
            pageNumber,
            pageSize,
            sort,
            nativeQuery
        );
    }
}