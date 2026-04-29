package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.search.FinancialOperationSearchKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FinancialOperationSearchCache {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialOperationSearchCache.class);

    private final Map<FinancialOperationSearchKey, Page<FinancialOperationResponseDto>> operationSearchIndex =
        new HashMap<>();

    public Page<FinancialOperationResponseDto> get(FinancialOperationSearchKey key) {
        return operationSearchIndex.get(key);
    }

    public void put(FinancialOperationSearchKey key, Page<FinancialOperationResponseDto> value) {
        operationSearchIndex.put(key, value);
    }

    public void invalidate() {
        int previousSize = operationSearchIndex.size();
        operationSearchIndex.clear();
        LOG.debug("CACHE INVALIDATED: operationSearchIndex cleared (previousSize={})", previousSize);
    }
}
