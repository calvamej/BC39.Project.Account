package com.bootcamp.project.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
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
    private ObjectId id;
    private String accountNumber;
    private String client;
    private String accountType;
    private String clientType;
    private boolean maintenanceCost;
    private int maxOperations;
    private double balance;
    private Date insert_date;
}