package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    /*
    CRUD BÁSICO
*/
    public Mono<AccountEntity> getOne(String accountNumber);
    public Flux<AccountEntity> getAll();
    public Mono<AccountEntity> save(AccountEntity colEnt);
    public Mono<AccountEntity> update(String accountNumber, String type);
    public Mono<Void> delete(String accountNumber);

    /*
         MÉTODOS DE NEGOCIO
    */
    public Mono<AccountEntity> registerAccount(AccountEntity colEnt);
    public Mono<AccountEntity> findAccountByClient(String client, String accountType);
    public Mono<AccountEntity> depositBalance(String account, double balance);
    public Mono<AccountEntity> withdrawBalance(String account, double balance);
    public double getBalance(String account);
}
