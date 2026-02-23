package com.laurefindel.finance.repository;

import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface FinancialOperationRepository extends JpaRepository<FinancialOperation, Long> {
    List<FinancialOperation> findBySenderAccount_User_Id(Long senderUserId);
    List<FinancialOperation> findByReceiverAccount_User_Id(Long receiverUserId);
    List<FinancialOperation> findBySenderAccountId(Long accountId);
    List<FinancialOperation> findByReceiverAccountId(Long accountId);
    List<FinancialOperation> findBySenderAccount_User_IdAndReceiverAccount_User_Id(Long senderUserId, 
        Long receiverUserId);
    List<FinancialOperation> findBySenderAccountIdAndReceiverAccountId(Long senderAccountId, Long receiverAccountId);
    List<FinancialOperation> findByCurrency(Currency currency);
}
