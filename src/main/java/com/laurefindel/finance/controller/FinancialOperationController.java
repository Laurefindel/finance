package com.laurefindel.finance.controller;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.dto.FinancialOperationSearchCriteria;
import com.laurefindel.finance.service.FinancialOperationService;

import jakarta.validation.Valid;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/operations")
@Validated
@Tag(name = "Operations", description = "Financial operations endpoints")
public class FinancialOperationController {

    private final FinancialOperationService service;  
    public FinancialOperationController(FinancialOperationService service) {
        this.service = service;
    } 
    
    @GetMapping
    @Operation(summary = "Get operations or filter by sender user")
    public ResponseEntity<List<FinancialOperationResponseDto>> getAll(
        @Parameter(description = "Sender user id", example = "1")
        @RequestParam(required = false) Long senderUserId) {
        return (senderUserId != null)
            ? ResponseEntity.ok(service.getBySender(senderUserId))
            : ResponseEntity.ok(service.getAll());
    } 

    @PostMapping("/search")
    @Operation(summary = "Search operations with filters and pagination")
    public ResponseEntity<Page<FinancialOperationResponseDto>> search(
        @Valid @RequestBody FinancialOperationSearchCriteria criteria,
        @Parameter(description = "Query mode", example = "jpql")
        @RequestParam(defaultValue = "jpql") String queryType,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        boolean useNativeQuery = "native".equalsIgnoreCase(queryType);
        return ResponseEntity.ok(service.searchWithFilters(criteria, pageable, useNativeQuery));
    }
  
    @GetMapping("/{id}")
    @Operation(summary = "Get operation by id")
    public ResponseEntity<FinancialOperationResponseDto> getById(
        @Parameter(description = "Operation id", example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    } 
  
    @PostMapping
    @Operation(summary = "Create financial operation")
    public ResponseEntity<FinancialOperationResponseDto> create(@Valid @RequestBody FinancialOperationRequestDto dto) {
        FinancialOperationResponseDto operation = service.doOperation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }  

    @PostMapping("/bulk")
    @Operation(summary = "Create bulk financial operations")
    public ResponseEntity<List<FinancialOperationResponseDto>> createBulk(
        @RequestBody List<@Valid FinancialOperationRequestDto> operations
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.doBulkOperation(operations));
    }

    @PostMapping("/bulk/non-transactional")
    @Operation(summary = "Create bulk financial operations without transaction")
    public ResponseEntity<List<FinancialOperationResponseDto>> createBulkWithoutTransaction(
        @RequestBody List<FinancialOperationRequestDto> operations
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.doBulkOperationWithoutTransaction(operations));
    }
  
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete financial operation")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Operation id", example = "1") @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
