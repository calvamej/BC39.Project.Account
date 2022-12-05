package com.bootcamp.project.account.service;

import com.bootcamp.project.account.AccountApplication;
import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.exception.CustomInformationException;
import com.bootcamp.project.account.exception.CustomNotFoundException;
import com.bootcamp.project.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.stream.Collectors;

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
    public Mono<AccountEntity> getByClientAndProduct(String clientDocumentNumber, String productCode)
    {
        return accountRepository.findAll().filter(x -> x.getClientDocumentNumber().equals(clientDocumentNumber)
                && x.getProductCode().equals(productCode)).next();
    }
    @Override
    public Mono<AccountEntity> depositBalance(String accountNumber, double balance)
    {
        return getOne(accountNumber).flatMap(c -> {
            double debt = c.getOperationalDebt();
            if(debt > 0)
            {
                if(debt < balance)
                {
                    c.setBalance(c.getBalance() + (balance - debt));
                    c.setOperationalDebt(0);
                }
                else
                {
                    c.setOperationalDebt(debt - balance);
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
                c.setOperationalDebt(c.getOperationalDebt() + (c.getMaintenanceCost() - c.getBalance()));
                c.setBalance(0);
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
    public Mono<AccountEntity> applyCommissionFee(String accountNumber, double amount)
    {
        return getOne(accountNumber).flatMap(c -> {
            if(c.getBalance() < amount)
            {
                c.setOperationalDebt(amount - c.getBalance());
                c.setBalance(0);
            }
            else
            {
                c.setBalance(c.getBalance() - amount);
            }
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> registerPersonalAccount(AccountEntity colEnt) {

        if(colEnt.getMinimumOpeningAmount() > colEnt.getBalance())
        {
            return Mono.error(new CustomInformationException("The account requires a higher opening balance"));
        }
            return getByClientAndProduct(colEnt.getClientDocumentNumber(), colEnt.getProductCode())
                    .switchIfEmpty(accountRepository.save(colEnt));
    }
    @Override
    public Mono<AccountEntity> registerCompanyAccount(AccountEntity colEnt) {

        if(colEnt.getMinimumOpeningAmount() > colEnt.getBalance())
        {
            return Mono.error(new CustomInformationException("The account requires a higher opening balance"));
        }

            if(colEnt.getProductCode().equals("CC"))
            {
                if (colEnt.getOwners().size() > 0) {
                    return accountRepository.save(colEnt);
                }
                else
                {
                    return Mono.error(new CustomInformationException("Company accounts require at least 1 owner"));
                }

            }
            else
            {
                return Mono.error(new CustomInformationException("Company clients can only create the following type of account: Cuenta Corriente"));
            }

    }
    @Override
    public Mono<AccountEntity> transferBalance(String sourceAccountNumber, String targetAccountNumber ,double balance)
    {
        return getOne(sourceAccountNumber).switchIfEmpty(Mono.error(new CustomNotFoundException("Source account not found")))
                .then(getOne(targetAccountNumber).switchIfEmpty(Mono.error(new CustomNotFoundException("Target account not found"))))
                .then(withdrawBalance(sourceAccountNumber, balance).then(depositBalance(targetAccountNumber, balance)))
                .then(getOne(sourceAccountNumber));
    }
    @Override
    public Mono<Boolean> checkMinimumDailyBalance(String accountNumber) {
        return getOne(accountNumber).filter(x -> x.getBalance() >= x.getMinimumDailyAmount()).hasElement()
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<Double> getAverageBalance(String clientDocumentNumber) {
        Flux<AccountEntity> count = accountRepository.findAll().filter(x -> x.getClientDocumentNumber().equals(clientDocumentNumber))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("The client does not have an account")));

        return count.collect(Collectors.averagingDouble(AccountEntity::getBalance));

    }
}
