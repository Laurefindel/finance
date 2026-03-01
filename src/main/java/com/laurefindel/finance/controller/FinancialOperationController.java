package com.laurefindel.finance.controller;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.service.FinancialOperationService;
import java.util.List;
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
public class FinancialOperationController {

    private final FinancialOperationService service;  
    public FinancialOperationController(FinancialOperationService service) {
        this.service = service;
    } 
    
    @GetMapping
    public List<FinancialOperationResponseDto> getAll(@RequestParam(required = false) Long senderUserId) {
        return (senderUserId != null)
            ? service.getBySender(senderUserId)
            : service.getAll();
    } 
  
    @GetMapping("/{id}")
    public FinancialOperationResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    } 
  
    @PostMapping
    public FinancialOperationResponseDto create(@RequestBody FinancialOperationRequestDto dto) {
        return service.doOperation(dto);
    } 

    @PostMapping("no-transactional/")
    public FinancialOperationResponseDto createWithoutTransactional(@RequestBody FinancialOperationRequestDto dto) {
        return service.doOperationWithoutTransactional(dto);
    } 
    
  
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
