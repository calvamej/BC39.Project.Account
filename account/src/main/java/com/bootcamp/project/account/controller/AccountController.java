package com.bootcamp.project.account.controller;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.report.AccountDailyReportEntity;
import com.bootcamp.project.account.entity.report.AccountReportEntity;
import com.bootcamp.project.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequestMapping(value="/Account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping(value = "/FindAll")
    public Flux<AccountEntity> Get_All(){

        return accountService.getAll();
    }
    @GetMapping(value = "/FindOne/{accountNumber}")
    public Mono<AccountEntity> Get_One(@PathVariable("accountNumber") String accountNumber){
        return accountService.getOne(accountNumber);
    }
    @PostMapping(value = "/Save")
    public Mono<AccountEntity> Save(@RequestBody AccountEntity col){

        return accountService.save(col);
    }
    @PutMapping(value = "/Update/{accountNumber}/{balance}")
    public Mono<AccountEntity> Update(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.update(accountNumber, balance);
    }
    @DeleteMapping  (value = "/Delete/{accountNumber}")
    public Mono<Void> Delete(@PathVariable("accountNumber") String accountNumber){
        return accountService.delete(accountNumber);
    }
    @PostMapping(value = "/RegisterPersonal")
    public Mono<AccountEntity> registerPersonalAccount(@RequestBody AccountEntity col){
        return accountService.registerPersonalAccount(col);
    }
    @PostMapping(value = "/RegisterCompany")
    public Mono<AccountEntity> registerCompanyAccount(@RequestBody AccountEntity col){
        return accountService.registerCompanyAccount(col);
    }
    @GetMapping(value = "/GetByClient/{clientDocumentNumber}")
    public Flux<AccountEntity> getByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getByClient(clientDocumentNumber);
    }
    @GetMapping(value = "/GetBalance/{accountNumber}")
    public Mono<Double> getBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.getBalance(accountNumber);
    }
    @PutMapping(value = "/DepositBalance/{accountNumber}/{amount}")
    public Mono<AccountEntity> depositBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("amount") double amount){
        return accountService.depositBalance(accountNumber,amount);
    }
    @PutMapping(value = "/WithdrawBalance/{accountNumber}/{amount}")
    public Mono<AccountEntity> withdrawBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("amount") double amount){
        return accountService.withdrawBalance(accountNumber,amount);
    }
    @PutMapping(value = "/ApplyMaintenanceFee/{accountNumber}")
    public Mono<AccountEntity> applyMaintenanceFee(@PathVariable("accountNumber") String accountNumber){
        return accountService.applyMaintenanceFee(accountNumber);
    }
    @GetMapping(value = "/CheckIfCommissionApply/{accountNumber}")
    public Mono<Boolean> checkIfCommissionApply(@PathVariable("accountNumber") String accountNumber){
        return accountService.checkIfCommissionApply(accountNumber);
    }
    @GetMapping(value = "/CheckVIPMinimumBalanceCompliance/{accountNumber}")
    public Mono<Boolean> checkVIPMinimumDailyBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.checkVIPMinimumDailyBalance(accountNumber);
    }
    @GetMapping(value = "/GetAverageBalanceByClient/{clientDocumentNumber}")
    public Flux<AccountDailyReportEntity> getAverageBalanceByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getAverageBalanceByClient(clientDocumentNumber);
    }
    @GetMapping(value = "/GetAccountsByDates/{initialDate}/{finalDate}")
    public Flux<AccountReportEntity> getAccountsByDates(@PathVariable("initialDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date initialDate, @PathVariable("finalDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date finalDate){
        return accountService.getAccountsByDates(initialDate,finalDate);
    }
    @GetMapping(value = "/GetAccountsByClient/{clientDocumentNumber}")
    public Flux<AccountReportEntity> getAccountsByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getAccountsByClient(clientDocumentNumber);
    }
    @PutMapping(value = "/TransferBalance/{sourceAccountNumber}/{targetAccountNumber}/{amount}")
    public Mono<AccountEntity> transferBalance(@PathVariable("sourceAccountNumber") String sourceAccountNumber,@PathVariable("targetAccountNumber") String targetAccountNumber,@PathVariable("amount") double amount){
        return accountService.transferBalance(sourceAccountNumber,targetAccountNumber,amount);
    }
    @PutMapping(value = "/LinkDebitCardMainAccount/{accountNumber}/{debitCardNumber}")
    public Mono<AccountEntity> linkDebitCardMainAccount(@PathVariable("accountNumber") String accountNumber,@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.linkDebitCardMainAccount(accountNumber,debitCardNumber);
    }
    @GetMapping(value = "/GetDebitCardMainAccountBalance/{debitCardNumber}")
    public Mono<Double> getDebitCardMainAccountBalance(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.getDebitCardMainAccountBalance(debitCardNumber);
    }
    @GetMapping(value = "/GetDebitCardNextPriorityOrder/{debitCardNumber}")
    public Mono<Integer> getDebitCardNextPriorityOrder(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.getDebitCardNextPriorityOrder(debitCardNumber);
    }
    @PutMapping(value = "/LinkDebitCardSecondaryAccount/{accountNumber}/{debitCardNumber}/{debitCardPriorityOrder}")
    public Mono<AccountEntity> linkDebitCardSecondaryAccount(@PathVariable("accountNumber") String accountNumber,@PathVariable("debitCardNumber") String debitCardNumber,@PathVariable("debitCardPriorityOrder") Integer debitCardPriorityOrder){
        return accountService.linkDebitCardSecondaryAccount(accountNumber,debitCardNumber,debitCardPriorityOrder);
    }
    @PutMapping(value = "/AddDebitCardPayment/{debitCardNumber}/{amount}")
    public Mono<AccountEntity> addDebitCardPayment(@PathVariable("debitCardNumber") String debitCardNumber,@PathVariable("amount") double amount){
        return accountService.addDebitCardPayment(debitCardNumber,amount);
    }
}
