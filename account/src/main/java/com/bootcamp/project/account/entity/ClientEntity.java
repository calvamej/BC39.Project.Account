package com.bootcamp.project.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {
    @Id
    private String id;
    private String documentNumber;
    private String documentType;
    private String name;
    private String lastname;
    private String companyName;
    private String companyDescription;
    private String type;
    private String subType;
    private Date createDate;
    private Date modifyDate;
}
