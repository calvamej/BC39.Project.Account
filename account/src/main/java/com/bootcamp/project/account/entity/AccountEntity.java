package com.bootcamp.project.account.entity;

import com.bootcamp.project.account.entity.person.PersonEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Account")
public class AccountEntity {
    @Id
    private String id;
    private String accountNumber;
    //PRODUCT CODE: SA = SAVING ACCOUNT, CA = CURRENT ACCOUNT, FA = FIXED TERM ACCOUNT.
    private String productCode;
    private String clientDocumentNumber;
    //CLIENT TYPE: BUSINESS (B), PERSONAL (P).
    private String clientType;
    //CLIENT SUBTYPE: PYME, VIP.
    private String clientSubType;
    private double balance;
    private double operationalDebt;
    private List<PersonEntity> owners;
    private List<PersonEntity> signatories;

    //TRUE = HAS A DEBIT CARD ASSOCIATED WITH THE ACCOUNT.
    private Boolean hasDebitCard;
    //DEBIT CARD NUMBER
    private String debitCardNumber;
    //TRUE = THIS IS THE DEBIT CARD MAIN ACCOUNT.
    private Boolean debitCardMainAccount;
    //ORDER OF THIS ACCOUNT IN ALL THE ACCOUNTS ASSOCIATED WITH THE DEBIT CARD. (1 = MAIN ACCOUNT).
    private int debitCardPriorityOrder;

    private double minimumOpeningBalance;
    private double MinimumDailyBalance;
    private double maintenanceCost;
    private int maxMonthlyOperations;
    private int currentMonthOperations;
    private double commissionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date modifyDate;
}