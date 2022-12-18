package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.report.AccountDailyReportEntity;
import com.bootcamp.project.account.entity.report.AccountReportEntity;
import com.bootcamp.project.account.exception.CustomInformationException;
import com.bootcamp.project.account.exception.CustomNotFoundException;
import com.bootcamp.project.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.log4j.Logger;

import java.util.Comparator;
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
        return accountRepository.findAll().filter(x -> x.getAccountNumber() != null && x.getAccountNumber().equals(accountNumber)).next();
    }
    @Override
    public Mono<AccountEntity> save(AccountEntity colEnt) {
        colEnt.setCreateDate(new Date());
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
    public Mono<AccountEntity> getByClientAndProductCode(String clientDocumentNumber, String productCode)
    {
        return accountRepository.findAll().filter(x -> x.getClientDocumentNumber() != null &&
                x.getClientDocumentNumber().equals(clientDocumentNumber) && x.getProductCode() != null
                && x.getProductCode().equals(productCode)).next();
    }
    @Override
    public Mono<AccountEntity> registerPersonalAccount(AccountEntity colEnt) {
        if(colEnt.getMinimumOpeningBalance() > colEnt.getBalance())
        {
            return Mono.error(new CustomInformationException("The account requires a higher opening balance"));
        }
        colEnt.setCreateDate(new Date());
        return getByClientAndProductCode(colEnt.getClientDocumentNumber(), colEnt.getProductCode())
                .switchIfEmpty(accountRepository.findAll().filter(x -> x.getAccountNumber() != null && x.getAccountNumber().equals(colEnt.getAccountNumber())).next()
                .switchIfEmpty(accountRepository.save(colEnt)));
    }
    @Override
    public Mono<AccountEntity> registerCompanyAccount(AccountEntity colEnt) {
        //PRODUCT CODE: SA = SAVING ACCOUNT, CA = CURRENT ACCOUNT, FA = FIXED TERM ACCOUNT.
        if(colEnt.getProductCode().equals("CA") && colEnt.getMinimumOpeningBalance() <= colEnt.getBalance())
        {
            if (colEnt.getOwners() != null && colEnt.getOwners().size() > 0) {
                colEnt.setCreateDate(new Date());
                if (colEnt.getClientSubType() != null && colEnt.getClientSubType().equals("PYME")) {
                    colEnt.setMaintenanceCost(0);
                }
                return accountRepository.findAll().filter(x -> x.getAccountNumber() != null && x.getAccountNumber().equals(colEnt.getAccountNumber())).next()
                        .switchIfEmpty(accountRepository.save(colEnt));
            }
            else {
                return Mono.error(new CustomInformationException("Company accounts require at least 1 owner."));
            }
        }
        else {
            return Mono.error(new CustomInformationException("Company clients can only create CURRENT ACCOUNTS with "+ colEnt.getMinimumOpeningBalance() + " of minimum initial balance."));
        }
    }
    @Override
    public Flux<AccountEntity> getByClient(String clientDocumentNumber)
    {
        return accountRepository.findAll().filter(x -> x.getClientDocumentNumber() != null &&
                x.getClientDocumentNumber().equals(clientDocumentNumber))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("The client does not have an account")));
    }
    @Override
    public Mono<Double> getBalance(String accountNumber) {
        return getOne(accountNumber)
                .map(x -> x.getBalance())
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> depositBalance(String accountNumber, double amount)
    {
        //IF THE ACCOUNT DOES NOT HAVE FUNDS TO PAY maintenanceCost AT THE END OF THE MONTH, THE DEBT GOES TO OperationalDebt.
        //WHEN NEW BALANCE IS DEPOSITED IN THE ACCOUNT, THE AMOUNT NEEDED TO COVER OperationalDebt IS TAKEN.

        return getOne(accountNumber).flatMap(c -> {
            double operationalDebt = c.getOperationalDebt();
                if(operationalDebt > 0)
                {
                    if(operationalDebt < amount)
                    {
                        c.setBalance(c.getBalance() + (amount - operationalDebt));
                        c.setOperationalDebt(0);
                    } else {
                        c.setOperationalDebt(operationalDebt - amount);
                    }
                } else {
                    c.setBalance(c.getBalance() + amount);
                }
                c.setModifyDate(new Date());
            return accountRepository.save(c)
                    .then(applyCommissionFee(c.getAccountNumber(), amount));
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> withdrawBalance(String accountNumber, double amount)
    {
        return getOne(accountNumber).flatMap(c -> {
            if(c.getBalance() >= amount) {
                c.setBalance(c.getBalance() - amount);
                c.setModifyDate(new Date());
                return accountRepository.save(c)
                        .then(applyCommissionFee(c.getAccountNumber(), amount));
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
        //NOTE: ACCOUNT TYPES THAT DOES NOT HAVE MAINTENANCE FEES DON'T GET DEBITED BECAUSE THEY ARE REGISTERED WITH MaintenanceCost = 0.

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
                //MAINTENANCE FEES OCCUR AT THE END OF THE MONTH, SO THE COUNTER RESETS FOR NEXT MONTH
                c.setCurrentMonthOperations(0);
                return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> applyCommissionFee(String accountNumber, double amount)
    {
        return getOne(accountNumber).flatMap(c -> {
            if(c.getCurrentMonthOperations() >= c.getMaxMonthlyOperations())
            {
                if(c.getBalance() < (amount * c.getCommissionPercentage()))
                {
                    c.setOperationalDebt(c.getOperationalDebt() + ((amount * c.getCommissionPercentage())- c.getBalance()));
                    c.setBalance(0);
                }
                else
                {
                    c.setBalance(c.getBalance() - (amount * c.getCommissionPercentage()));
                }
            }
            c.setCurrentMonthOperations(c.getCurrentMonthOperations() + 1);
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    public Mono<Boolean> checkIfCommissionApply(String accountNumber)
    {
        return accountRepository.findAll().filter(x -> x.getAccountNumber() != null && x.getAccountNumber().equals(accountNumber)
                && x.getCurrentMonthOperations() >= x.getMaxMonthlyOperations())
                .hasElements()
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    public Mono<Boolean> checkVIPMinimumDailyBalance(String accountNumber)
    {
        return accountRepository.findAll().filter(x -> x.getAccountNumber() != null && x.getAccountNumber().equals(accountNumber)
                && x.getClientType() != null && x.getClientType().equals("P")
                && x.getClientSubType() != null && x.getClientSubType().equals("VIP"))
                .next()
                .flatMap(x -> {
                    if(x.getBalance() >= x.getMinimumDailyBalance())
                    {
                        return Mono.just(true);
                    }
                    else
                    {
                        return Mono.just(false);
                    }
                }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found or not a Personal VIP Account")));
    }
    public Flux<AccountDailyReportEntity> getAverageBalanceByClient(String clientDocumentNumber)
    {
        return  accountRepository.findAll().filter(x -> x.getClientDocumentNumber() != null &&
                        x.getClientDocumentNumber().equals(clientDocumentNumber))
                .groupBy(AccountEntity::getClientDocumentNumber)
                .flatMap(a -> a
                        .collectList().map(list ->
                                new AccountDailyReportEntity(a.key(), list.stream().count(),list.stream().collect(Collectors.averagingDouble(AccountEntity::getBalance)), new Date(), list)))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("The client does not have accounts")));
    }
    @Override
    public Flux<AccountReportEntity> getCreditsByDates(Date initialDate, Date finalDate)
    {
        return accountRepository.findAll()
                .filter(c -> c.getCreateDate() != null && c.getCreateDate().after(initialDate) && c.getCreateDate().before(finalDate))
                .groupBy(AccountEntity::getProductCode)
                .flatMap(a -> a
                        .collectList().map(list ->
                                new AccountReportEntity(a.key(), list)))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Accounts not found between the given dates.")));
    }
    @Override
    public Flux<AccountReportEntity> getCreditsByClient(String clientDocumentNumber)
    {
        return accountRepository.findAll()
                .filter(c -> c.getClientDocumentNumber() != null && c.getClientDocumentNumber().equals(clientDocumentNumber))
                .groupBy(AccountEntity::getProductCode)
                .flatMap(a -> a
                        .collectList().map(list ->
                                new AccountReportEntity(a.key(), list)))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("The client does not have accounts")));
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
    public Mono<AccountEntity> linkDebitCardMainAccount(String accountNumber, String debitCardNumber)
    {
        return getOne(accountNumber).flatMap(c -> {
            c.setDebitCardNumber(debitCardNumber);
            c.setDebitCardPriorityOrder(0);
            c.setDebitCardMainAccount(true);
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> linkDebitCardSecondaryAccount(String accountNumber, String debitCardNumber)
    {
        return getOne(accountNumber).flatMap(c -> {
            c.setDebitCardNumber(debitCardNumber);

                AccountEntity temp = accountRepository.findAll().filter(x -> x.getDebitCardNumber().equals(debitCardNumber))
                        .toStream()
                        .max(Comparator.comparing(AccountEntity::getDebitCardPriorityOrder))
                        .get();

            c.setDebitCardPriorityOrder(temp.getDebitCardPriorityOrder() + 1);

            c.setDebitCardMainAccount(false);
            c.setModifyDate(new Date());
            return accountRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<Boolean> checkDebitCardMainAccount(String debitCardNumber)
    {
        return accountRepository.findAll().filter(x -> x.getDebitCardNumber().equals(debitCardNumber)
        && x.getDebitCardMainAccount().equals(true)).hasElements();
    }
    @Override
    public Flux<AccountEntity> linkDebitCardSecondaryAccounts(String clientDocumentNumber, String debitCardNumber)
    {
        return accountRepository.findAll().filter(x -> x.getClientDocumentNumber().equals(clientDocumentNumber)
        && x.getDebitCardNumber().isEmpty() && x.getDebitCardMainAccount().equals(false) ).flatMap(c -> linkDebitCardSecondaryAccount(c.getAccountNumber(),debitCardNumber));
    }
    @Override
    public Mono<Double> getBalanceByDebitCard(String debitCardNumber) {
        return accountRepository.findAll().filter(x -> x.getDebitCardNumber().equals(debitCardNumber) && x.getDebitCardMainAccount().equals(true))
                .next()
                .map(x -> x.getBalance())
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
    @Override
    public Mono<AccountEntity> addDebitCardPayment(String debitCardNumber, double amount)
    {
        return accountRepository.findAll().filter(x -> x.getDebitCardNumber().equals(debitCardNumber))
                .filter(x -> x.getBalance() >= amount)
                .sort(Comparator.comparing(AccountEntity::getDebitCardPriorityOrder))
                .next()
                .flatMap(c -> withdrawBalance(c.getAccountNumber(),amount))
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Account not found")));
    }
}
