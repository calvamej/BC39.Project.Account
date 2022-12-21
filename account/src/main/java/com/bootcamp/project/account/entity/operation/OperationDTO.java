package com.bootcamp.project.account.entity.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDTO {
    private String operationType;
    private String clientDocumentNumber;
    private String productCode;
    private String accountNumber;
    private String creditNumber;
    private String debitCardNumber;
    private String creditCardNumber;
    private double amount;
}
