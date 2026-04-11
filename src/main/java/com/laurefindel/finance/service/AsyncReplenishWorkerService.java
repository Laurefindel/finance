package com.laurefindel.finance.service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.config.AsyncProperties;

@Service
public class AsyncReplenishWorkerService {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncReplenishWorkerService.class);

    private final AccountService accountService;
    private final AsyncTaskRegistryService asyncTaskRegistryService;
    private final AsyncTaskCounterService asyncTaskCounterService;
    private final long replenishDelayMs;

    public AsyncReplenishWorkerService(AccountService accountService,
                                       AsyncTaskRegistryService asyncTaskRegistryService,
                                       AsyncTaskCounterService asyncTaskCounterService,
                                       AsyncProperties asyncProperties) {
        this.accountService = accountService;
        this.asyncTaskRegistryService = asyncTaskRegistryService;
        this.asyncTaskCounterService = asyncTaskCounterService;
        this.replenishDelayMs = asyncProperties.getReplenishDelayMs();
    }

    @Async
    public CompletableFuture<Void> runTask(String taskId, Long accountId, BigDecimal amount) {
        asyncTaskRegistryService.markRunning(taskId, "Task is running");
        asyncTaskCounterService.incrementRunning();
        try {
            Thread.sleep(replenishDelayMs);
            accountService.replenishBalanceOnly(accountId, amount);
            asyncTaskRegistryService.markSuccess(taskId, "Account replenished successfully");
            asyncTaskCounterService.incrementSucceeded();
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOG.error("Async replenish interrupted for taskId={} accountId={}", taskId, accountId, ex);
            asyncTaskRegistryService.markFailed(taskId, "Task interrupted");
            asyncTaskCounterService.incrementFailed();
            return CompletableFuture.failedFuture(ex);
        } catch (Exception ex) {
            LOG.error("Async replenish failed for taskId={} accountId={}", taskId, accountId, ex);
            asyncTaskRegistryService.markFailed(taskId, ex.getMessage());
            asyncTaskCounterService.incrementFailed();
            return CompletableFuture.failedFuture(ex);
        } finally {
            asyncTaskCounterService.decrementRunning();
        }
    }
}
