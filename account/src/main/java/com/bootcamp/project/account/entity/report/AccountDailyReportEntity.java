package com.bootcamp.project.account.entity.report;

import com.bootcamp.project.account.entity.AccountEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDailyReportEntity {
    private String clientDocumentNumber;
    private long numberOfAccounts;
    private Double averageBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date currentDate;
    List<AccountEntity> creditList;
}
