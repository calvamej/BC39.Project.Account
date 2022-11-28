package com.bootcamp.project.account.service;

import com.bootcamp.project.account.AccountApplication;
import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.log4j.Logger;
@Service
public class AccountServiceImplementation implements AccountService{
    private static Logger Log = Logger.getLogger(AccountServiceImplementation.class);
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Mono<AccountEntity> getOne(String accountNumber) {
        Log.info("Inicio método getOne.");
        Mono<AccountEntity> col = accountRepository.findAll().filter(x -> x.getAccountNumber().equals(accountNumber)).next();
        return col;
    }

    @Override
    public Flux<AccountEntity> getAll() {
        Log.info("Inicio método getAll.");
        Flux<AccountEntity> col = accountRepository.findAll();
        return col;
    }

    @Override
    public Mono<AccountEntity> save(AccountEntity colEnt) {
        Log.info("Inicio método save.");
        return accountRepository.save(colEnt);
    }

    @Override
    public Mono<AccountEntity> update(String accountNumber, String type) {
        Log.info("Inicio método update.");
        Mono<AccountEntity> col = getOne(accountNumber);
        AccountEntity newCol = col.block();
        newCol.setAccountType(type);
        return accountRepository.save(newCol);
    }

    @Override
    public Mono<Void> delete(String accountNumber) {
        Log.info("Inicio método delete.");
        Mono<AccountEntity> col = getOne(accountNumber);
        AccountEntity newCol = col.block();
        return accountRepository.delete(newCol);
    }
    /*
    MÉTODOS DE NEGOCIO
    */

    @Override
    public Mono<AccountEntity> registerAccount(AccountEntity colEnt) {
        Log.info("Inicio método registerAccount.");
        if(colEnt.getClientType().equals("P"))
        {
            return findAccountByClient(colEnt.getClient(), colEnt.getAccountType())
                    .switchIfEmpty(accountRepository.save(colEnt));
        }
        else if (colEnt.getClientType().equals("E"))
        {
            if(colEnt.getAccountType().equals("CC"))
            {
                return accountRepository.save(colEnt);
            }
            else
            {
                return Mono.just(new AccountEntity());
            }
        }
        else
        {
            return Mono.just(new AccountEntity());
        }
    }
    @Override
    public Mono<AccountEntity> findAccountByClient(String client, String accountType)
    {
        Log.info("Inicio método findAccountByClient.");
        Mono<AccountEntity> col = accountRepository.findAll().filter(x -> x.getClient().equals(client) && x.getAccountType().equals(accountType)).next();

        return col;
    }
    @Override
    public Mono<AccountEntity> depositBalance(String account, double balance)
    {
        Log.info("Inicio método depositBalance.");
        Mono<AccountEntity> col = accountRepository.findAll().filter(x -> x.getAccountNumber().equals(account)).next();
        AccountEntity newCol = col.block();
        if(newCol==null)
        {
            return Mono.just(new AccountEntity());
        }
        newCol.setBalance(newCol.getBalance() + balance);
        return accountRepository.save(newCol);
    }
    @Override
    public Mono<AccountEntity> withdrawBalance(String account, double balance)
    {
        Log.info("Inicio método withdrawBalance.");
        Mono<AccountEntity> col = accountRepository.findAll().filter(x -> x.getAccountNumber().equals(account)).next();
        AccountEntity newCol = col.block();
        if(newCol==null)
        {
            return Mono.just(new AccountEntity());
        }
        if(newCol.getBalance() >= balance)
        {
            newCol.setBalance(newCol.getBalance() - balance);
            return accountRepository.save(newCol);
        }
        else
        {
            return Mono.just(new AccountEntity());
        }
    }
    @Override
    public double getBalance(String accountNumber) {
        Log.info("Inicio método getBalance.");
        Mono<AccountEntity> col = getOne(accountNumber);
        AccountEntity newCol = col.block();
        return newCol.getBalance();
    }


}
