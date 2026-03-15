package com.laurefindel.finance.repository;

import com.laurefindel.finance.dto.FinancialOperationSearchCriteria;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface FinancialOperationRepository extends JpaRepository<FinancialOperation, Long> {

    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount", "currency", "senderAccount.user"})
    List<FinancialOperation> findBySenderAccount_User_Id(Long senderUserId);

    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount", "currency", "receiverAccount.user"})
    List<FinancialOperation> findByReceiverAccount_User_Id(Long receiverUserId);

    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount", "currency"})
    List<FinancialOperation> findBySenderAccountId(Long accountId);

    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount", "currency"})
    List<FinancialOperation> findByReceiverAccountId(Long accountId);
    
    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount", "currency"})
    List<FinancialOperation> findByCurrency(Currency currency);

    @Query("""
        SELECT fo
        FROM FinancialOperation fo
        JOIN fo.senderAccount sa
        JOIN sa.user su
        JOIN fo.receiverAccount ra
        JOIN ra.user ru
        JOIN fo.currency c
        WHERE (:#{#criteria.senderUserId} IS NULL OR su.id = :#{#criteria.senderUserId})
        AND (:#{#criteria.receiverUserId} IS NULL OR ru.id = :#{#criteria.receiverUserId})
        AND (:#{#criteria.currencyCode} IS NULL OR c.code = :#{#criteria.currencyCode})
        AND (:#{#criteria.minAmount} IS NULL OR fo.amount >= :#{#criteria.minAmount})
        AND (:#{#criteria.maxAmount} IS NULL OR fo.amount <= :#{#criteria.maxAmount})
        AND (:#{#criteria.fromDate} IS NULL OR fo.createdAt >= :#{#criteria.fromDate})
        AND (:#{#criteria.toDate} IS NULL OR fo.createdAt <= :#{#criteria.toDate})
        """)
    Page<FinancialOperation> searchWithFiltersJpql(
            @Param("criteria") FinancialOperationSearchCriteria criteria,
            Pageable pageable
    );
    @Query(value = """
        SELECT fo.*
        FROM financial_operations fo
        JOIN accounts sa ON sa.id = fo.sender_account_id
        JOIN users su ON su.id = sa.user_id
        JOIN accounts ra ON ra.id = fo.receiver_account_id
        JOIN users ru ON ru.id = ra.user_id
        JOIN currency c ON c.id = fo.currency_id
        WHERE su.id = COALESCE(:#{#criteria.senderUserId}, su.id)
        AND ru.id = COALESCE(:#{#criteria.receiverUserId}, ru.id)
        AND c.code = COALESCE(:#{#criteria.currencyCode}, c.code)
        AND fo.amount >= COALESCE(:#{#criteria.minAmount}, fo.amount)
        AND fo.amount <= COALESCE(:#{#criteria.maxAmount}, fo.amount)
        AND fo.created_at >= COALESCE(:#{#criteria.fromDate}, fo.created_at)
        AND fo.created_at <= COALESCE(:#{#criteria.toDate}, fo.created_at)
        """,
        countQuery = """
        SELECT COUNT(fo.id)
        FROM financial_operations fo
        JOIN accounts sa ON sa.id = fo.sender_account_id
        JOIN users su ON su.id = sa.user_id
        JOIN accounts ra ON ra.id = fo.receiver_account_id
        JOIN users ru ON ru.id = ra.user_id
        JOIN currency c ON c.id = fo.currency_id
        WHERE su.id = COALESCE(:#{#criteria.senderUserId}, su.id)
        AND ru.id = COALESCE(:#{#criteria.receiverUserId}, ru.id)
        AND c.code = COALESCE(:#{#criteria.currencyCode}, c.code)
        AND fo.amount >= COALESCE(:#{#criteria.minAmount}, fo.amount)
        AND fo.amount <= COALESCE(:#{#criteria.maxAmount}, fo.amount)
        AND fo.created_at >= COALESCE(:#{#criteria.fromDate}, fo.created_at)
        AND fo.created_at <= COALESCE(:#{#criteria.toDate}, fo.created_at)
        """,
        nativeQuery = true)
    Page<FinancialOperation> searchWithFiltersNative(
            @Param("criteria") FinancialOperationSearchCriteria criteria,
            Pageable pageable
    );
}
