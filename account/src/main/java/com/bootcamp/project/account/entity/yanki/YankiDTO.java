package com.bootcamp.project.account.entity.yanki;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YankiDTO {
    private String debitCardNumber;
    private String type;
    private Double amount;
}
