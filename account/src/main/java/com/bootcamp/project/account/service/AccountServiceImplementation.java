package com.bootcamp.project.account.service;

import com.bootcamp.project.account.AccountApplication;
import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.exception.CustomInformationException;
import com.bootcamp.project.account.exception.CustomNotFoundException;
import com.bootcamp.project.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.log4j.Logger;

import java.util.Date;

@Service
public class AccountServiceImplementation implements AccountService{
    private static Logger Log = Logger.getLogger(AccountServiceImplementation.class);
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Flux<AccountEntity> getAll() {
        return accountRepository.findAll().switchIfEmpty(Mono.error(new CustomNotFoundException("Accounts not found")));
    }
    @Override
    public Mono<AccountEntity> getOne(String accountNumber) {
        return accountRepository.findAll().filter(x -> x.getAccountNumber().equals(accountNumber)).next();
    }
    @Override
    public Mono<AccountEntity> save(AccountEntity colEnt) {
        return accountRepository.save(colEnt);
    }

    @Override
    public Mono<AccountEntity> update(String accountNumber, double balance) {
        return getOne(accountNumber).flatMap(c -> {
            c.setBalance(balance);
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }

    @Override
    public Mono<Void> delete(String accountNumber) {
        return getOne(accountNumber)
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")))
                .flatMap(c -> {
                    return accountRepository.delete(c);
                });
    }
    @Override
    public Mono<Double> getBalance(String accountNumber) {
        return getOne(accountNumber)
                .map(x -> x.getBalance())
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> getByClientAndProduct(String clientDocumentNumber, String productName)
    {
        return accountRepository.findAll().filter(x -> x.getClientDocumentNumber().equals(clientDocumentNumber)
                && x.getProductName().equals(productName)).next();
    }
    @Override
    public Mono<AccountEntity> depositBalance(String accountNumber, double balance)
    {
        return getOne(accountNumber).flatMap(c -> {
            double debt = c.getMaintenanceDebt();
            if(debt > 0)
            {
                if(debt < balance)
                {
                    c.setBalance(c.getBalance() + (balance - debt));
                    c.setMaintenanceDebt(0);
                }
                else
                {
                    c.setMaintenanceDebt(debt - balance);
                }
            }
            else {
                c.setBalance(c.getBalance() + balance);

            }
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> withdrawBalance(String accountNumber, double balance)
    {
        return getOne(accountNumber).flatMap(c -> {
            if(c.getBalance() >= balance) {
                c.setBalance(c.getBalance() - balance);
                c.setModifyDate(new Date());
                return accountRepository.save(c);
            }
            else
            {
                return Mono.error(new CustomInformationException("The account does not have enough funds"));
            }
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> applyMaintenanceFee(String accountNumber)
    {
        return getOne(accountNumber).flatMap(c -> {
            if(c.getBalance() < c.getMaintenanceCost())
            {
                c.setBalance(0);
                c.setMaintenanceDebt(c.getMaintenanceCost() - c.getBalance());
            }
            else
            {
                c.setBalance(c.getBalance() - c.getMaintenanceCost());
            }
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> registerAccount(AccountEntity colEnt) {

        if(colEnt.getClientType().equals("P"))
        {
            return getByClientAndProduct(colEnt.getClientDocumentNumber(), colEnt.getProductName())
                    .switchIfEmpty(accountRepository.save(colEnt));
        }
        else if (colEnt.getClientType().equals("E"))
        {
            if(colEnt.getProductName().equals("Cuenta Corriente"))
            {
                if (colEnt.getOwners().size() > 0) {
                    return accountRepository.save(colEnt);
                }
                else
                {
                    return Mono.error(new CustomInformationException("Business accounts require at least 1 owner"));
                }

            }
            else
            {
                return Mono.error(new CustomInformationException("Business clients can only create the following type of account: Cuenta Corriente"));
            }
        }
        else
        {
            return Mono.error(new CustomInformationException("Invalid type of client"));
        }

    }
}
