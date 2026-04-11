package com.laurefindel.finance.controller;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laurefindel.finance.dto.AsyncTaskMetricsDto;
import com.laurefindel.finance.dto.AsyncTaskStatusDto;
import com.laurefindel.finance.dto.AsyncTaskSubmissionDto;
import com.laurefindel.finance.service.AsyncTaskCounterService;
import com.laurefindel.finance.service.AsyncReplenishService;

@RestController
@RequestMapping("/async/replenish")
@Tag(name = "Async Replenish", description = "One async business operation for account replenish")
public class AsyncReplenishController {

    private final AsyncReplenishService asyncReplenishService;
    private final AsyncTaskCounterService asyncTaskCounterService;

    public AsyncReplenishController(AsyncReplenishService asyncReplenishService,
                                    AsyncTaskCounterService asyncTaskCounterService) {
        this.asyncReplenishService = asyncReplenishService;
        this.asyncTaskCounterService = asyncTaskCounterService;
    }

    @PostMapping
    @Operation(summary = "Start async replenish operation")
    public ResponseEntity<AsyncTaskSubmissionDto> start(
        @Parameter(description = "Account id", example = "1") @RequestParam Long accountId,
        @Parameter(description = "Amount", example = "100.00") @RequestParam BigDecimal amount
    ) {
        String taskId = asyncReplenishService.startReplenish(accountId, amount);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AsyncTaskSubmissionDto(taskId));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get async operation status")
    public ResponseEntity<AsyncTaskStatusDto> status(@PathVariable String taskId) {
        return ResponseEntity.ok(asyncReplenishService.getStatus(taskId).orElseThrow());
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get async task counters")
    public ResponseEntity<AsyncTaskMetricsDto> metrics() {
        return ResponseEntity.ok(asyncTaskCounterService.getMetrics());
    }
}
