package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.report.AccountDailyReportEntity;
import com.bootcamp.project.account.entity.report.AccountReportEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface AccountService {

    public Mono<AccountEntity> getOne(String accountNumber);
    public Flux<AccountEntity> getAll();
    public Mono<AccountEntity> save(AccountEntity colEnt);
    public Mono<AccountEntity> update(String accountNumber, double balance);
    public Mono<Void> delete(String accountNumber);
    public Mono<AccountEntity> getByClientAndProductCode(String clientDocumentNumber, String productCode);
    public Mono<AccountEntity> registerPersonalAccount(AccountEntity colEnt);
    public Mono<AccountEntity> registerCompanyAccount(AccountEntity colEnt);
    public Flux<AccountEntity> getByClient(String clientDocumentNumber);
    public Mono<Double> getBalance(String account);
    public Mono<AccountEntity> depositBalance(String accountNumber, double amount);
    public Mono<AccountEntity> withdrawBalance(String accountNumber, double amount);
    public Mono<AccountEntity> applyMaintenanceFee(String accountNumber);
    public Mono<AccountEntity> applyCommissionFee(String accountNumber, double amount);
    public Mono<Boolean> checkIfCommissionApply(String accountNumber);
    public Mono<Boolean> checkVIPMinimumDailyBalance(String accountNumber);
    public Flux<AccountDailyReportEntity> getAverageBalanceByClient(String clientDocumentNumber);
    public Flux<AccountReportEntity> getAccountsByDates(Date initialDate, Date finalDate);
    public Flux<AccountReportEntity> getAccountsByClient(String clientDocumentNumber);
    public Mono<AccountEntity> transferBalance(String sourceAccountNumber, String targetAccountNumber ,double amount);
    public Mono<Boolean> checkDebitCardMainAccount(String debitCardNumber);
    public Mono<AccountEntity> linkDebitCardMainAccount(String accountNumber, String debitCardNumber);
    public Mono<Double> getDebitCardMainAccountBalance(String debitCardNumber);
    public Mono<Integer> getDebitCardNextPriorityOrder(String debitCardNumber);
    public Mono<AccountEntity> linkDebitCardSecondaryAccount(String accountNumber, String debitCardNumber,Integer DebitCardPriorityOrder);
    public Mono<AccountEntity> addDebitCardPayment(String debitCardNumber, double amount);
    public void publishToTopic(String creditNumber, String operationType, Double amount, String clientDocumentNumber, String productCode, String creditCardNumber);
}
