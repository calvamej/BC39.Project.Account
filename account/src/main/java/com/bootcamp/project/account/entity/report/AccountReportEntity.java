package com.bootcamp.project.account.entity.report;

import com.bootcamp.project.account.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountReportEntity {
    private String productCode;
    List<AccountEntity> creditList;
}
