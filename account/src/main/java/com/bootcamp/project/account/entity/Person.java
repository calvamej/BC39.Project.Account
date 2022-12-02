package com.bootcamp.project.account.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Person {
    @Id
    private String documentNumber;
    private String documentType;
}
