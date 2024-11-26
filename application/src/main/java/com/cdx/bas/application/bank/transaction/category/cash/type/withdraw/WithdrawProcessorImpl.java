package com.cdx.bas.application.bank.transaction.category.cash.type.withdraw;

import com.cdx.bas.application.bank.transaction.category.cash.CashAmountService;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionValidator;
import com.cdx.bas.domain.bank.transaction.category.cash.CashTransactionProcessingDetails;
import com.cdx.bas.domain.bank.transaction.category.cash.type.withdraw.WithdrawAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.message.MessageFormatter;

import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.EMITTER_AMOUNT_AFTER_KEY;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.EMITTER_AMOUNT_BEFORE_KEY;

public class WithdrawProcessorImpl extends CashAmountService {

    private final WithdrawAmountServiceImpl withdrawAmountService;

    public WithdrawProcessorImpl(TransactionValidator transactionValidator, TransactionStatusServicePort transactionStatusService, TransactionServicePort transactionService, BankAccountServicePort bankAccountService, WithdrawAmountServiceImpl withdrawAmountService) {
        super(transactionValidator, transactionStatusService, transactionService, bankAccountService);
        this.withdrawAmountService = withdrawAmountService;
    }

    @Override
    protected Transaction processType(CashTransactionProcessingDetails cashTransactionProcessingDetails) {
        cashTransactionProcessingDetails.getMetadata().put(EMITTER_AMOUNT_BEFORE_KEY, cashTransactionProcessingDetails.getEmitterBankAccount().getBalance().getAmount().toString());
        withdrawAmountService.applyToAccount(cashTransactionProcessingDetails);
        cashTransactionProcessingDetails.getMetadata().put(EMITTER_AMOUNT_AFTER_KEY, cashTransactionProcessingDetails.getEmitterBankAccount().getBalance().getAmount().toString());
        return cashTransactionProcessingDetails.getTransaction();
    }

    @Override
    protected String formatError(Transaction transaction, String errorStatus, Exception exception) {
        return MessageFormatter.format(WITHDRAW_TRANSACTION_CONTEXT, WITHDRAW_ACTION, errorStatus,
                Optional.of(DOMAIN_ERROR),
                List.of(TRANSACTION_ID_DETAIL + transaction.getId(),
                        ERROR_DETAIL + exception.getMessage()));
    }
}
