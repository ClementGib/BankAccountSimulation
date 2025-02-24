package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.group.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validator;

import java.util.HashMap;
import java.util.Map;


@RequestScoped
public class TransactionValidator {

    Validator validator;

    @Inject
    public TransactionValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateNewDigitalTransaction(Transaction transaction) throws TransactionException {
        validateTransaction(transaction, NewTransactionGroup.class, DigitalTransactionGroup.class);
    }

    public void validateExistingDigitalTransaction(Transaction transaction) throws TransactionException {
        validateTransaction(transaction, ExistingTransactionGroup.class, DigitalTransactionGroup.class);
    }


    public void validateCashTransaction(Transaction transaction) throws TransactionException {
        validateTransaction(transaction, NewTransactionGroup.class, PhysicalCashTransactionGroup.class);
    }

    private void validateTransaction(Transaction transaction, Class<?> stateGroup, Class<?> typeGroup) throws TransactionException {
        Map<String, String> violationsByFields = new HashMap<>();
        validator.validate(transaction).forEach(violation -> violationsByFields.put(violation.getPropertyPath().toString(), violation.getMessage()));
        validator.validate(transaction, AdvancedGroup.class).forEach(violation -> violationsByFields.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage()));
        validator.validate(transaction, stateGroup).forEach(violation -> violationsByFields.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage()));
        validator.validate(transaction, typeGroup).forEach(violation -> violationsByFields.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage()));
        checkConstraintViolation(violationsByFields);
    }

    private static void checkConstraintViolation(Map<String, String> violationsByFields) {
        if (!violationsByFields.isEmpty()) {
            throw new TransactionException(concatViolations(violationsByFields));
        }
    }

    private static String concatViolations(Map<String, String> violationsByFields) {
        StringBuilder violationBuilder = new StringBuilder();
        for (String errorMessage : violationsByFields.values()) {
            violationBuilder.append(errorMessage);
            violationBuilder.append("\n");
        }
        return violationBuilder.toString();
    }
}