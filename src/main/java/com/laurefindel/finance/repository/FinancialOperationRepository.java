package com.laurefindel.finance.repository;

import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


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
}
