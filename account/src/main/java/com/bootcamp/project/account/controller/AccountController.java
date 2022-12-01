package com.bootcamp.project.account.controller;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping(value = "/FindOne/{accountNumber}")
    public Mono<AccountEntity> Get_One(@PathVariable("accountNumber") String accountNumber){
        return accountService.getOne(accountNumber);
    }
    @GetMapping(value = "/FindAll")
    public Flux<AccountEntity> Get_All(){

        return accountService.getAll();
    }
    @PostMapping(value = "/Save")
    public Mono<AccountEntity> Save(@RequestBody AccountEntity col){

        return accountService.save(col);
    }
    @PutMapping(value = "/Update/{accountNumber}/{balance}")
    public Mono<AccountEntity> Update(@PathVariable("accountNumber") String accountNumber,@PathVariable("type") double balance){
        return accountService.update(accountNumber, balance);
    }
    @DeleteMapping  (value = "/Delete/{accountNumber}")
    public Mono<Void> Delete(@PathVariable("accountNumber") String accountNumber){
        return accountService.delete(accountNumber);
    }

    @GetMapping(value = "/GetByClientAndProduct/{clientDocumentNumber}/{productCode}")
    public Mono<AccountEntity> getByClientAndProduct(@PathVariable("clientDocumentNumber") String clientDocumentNumber,@PathVariable("productCode") String productCode){
        return accountService.getByClientAndProduct(clientDocumentNumber,productCode);
    }
    @GetMapping(value = "/GetBalance/{accountNumber}")
    public Mono<Double> getBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.getBalance(accountNumber);
    }
    @PutMapping(value = "/DepositBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> depositBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.depositBalance(accountNumber,balance);
    }
    @PutMapping(value = "/WithdrawBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> withdrawBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.withdrawBalance(accountNumber,balance);
    }
    @PutMapping(value = "/ApplyMaintenanceFee/{accountNumber}")
    public Mono<AccountEntity> applyMaintenanceFee(@PathVariable("accountNumber") String accountNumber){
        return accountService.applyMaintenanceFee(accountNumber);
    }
    @PostMapping(value = "/RegisterAccount")
    public Mono<AccountEntity> registerClient(@RequestBody AccountEntity col){
        return accountService.registerAccount(col);
    }
}
