package com.laurefindel.finance.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.AsyncTaskStatusDto;

@Service
public class AsyncReplenishService {

    private final AsyncTaskRegistryService asyncTaskRegistryService;
    private final AsyncReplenishWorkerService asyncReplenishWorkerService;
    private final AsyncTaskCounterService asyncTaskCounterService;

    public AsyncReplenishService(AsyncTaskRegistryService asyncTaskRegistryService,
                                 AsyncReplenishWorkerService asyncReplenishWorkerService,
                                 AsyncTaskCounterService asyncTaskCounterService) {
        this.asyncTaskRegistryService = asyncTaskRegistryService;
        this.asyncReplenishWorkerService = asyncReplenishWorkerService;
        this.asyncTaskCounterService = asyncTaskCounterService;
    }

    public String startReplenish(Long accountId, BigDecimal amount) {
        String taskId = UUID.randomUUID().toString();
        asyncTaskRegistryService.putPending(taskId);
        asyncTaskCounterService.incrementSubmitted();
        asyncReplenishWorkerService.runTask(taskId, accountId, amount);
        return taskId;
    }

    public Optional<AsyncTaskStatusDto> getStatus(String taskId) {
        return asyncTaskRegistryService.getStatus(taskId);
    }
}
