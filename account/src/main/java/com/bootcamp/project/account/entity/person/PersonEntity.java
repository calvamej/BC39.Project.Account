package com.bootcamp.project.account.entity.person;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class PersonEntity {
    @Id
    private String documentNumber;
    private String documentType;
    private String name;
    private String lastName;
}
