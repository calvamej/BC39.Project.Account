package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    public Mono<AccountEntity> getOne(String accountNumber);
    public Flux<AccountEntity> getAll();
    public Mono<AccountEntity> save(AccountEntity colEnt);
    public Mono<AccountEntity> update(String accountNumber, double balance);
    public Mono<Void> delete(String accountNumber);


    public Mono<AccountEntity> getByClientAndProduct(String clientDocumentNumber, String productName);
    public Mono<Double> getBalance(String account);
    public Mono<AccountEntity> depositBalance(String accountNumber, double balance);
    public Mono<AccountEntity> withdrawBalance(String accountNumber, double balance);
    public Mono<AccountEntity> registerAccount(AccountEntity colEnt);
    public Mono<AccountEntity> applyMaintenanceFee(String accountNumber);



}
