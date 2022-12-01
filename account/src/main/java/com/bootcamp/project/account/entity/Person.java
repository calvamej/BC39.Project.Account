package com.bootcamp.project.account.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Person {
    @Id
    private String id;
    private String documentType;
    private String documentNumber;
}
