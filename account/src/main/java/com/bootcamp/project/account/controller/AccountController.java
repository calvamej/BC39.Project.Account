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
    @PutMapping(value = "/Update/{accountNumber}/{type}")
    public Mono<AccountEntity> Update(@PathVariable("accountNumber") String accountNumber,@PathVariable("type") String type){
        return accountService.update(accountNumber, type);
    }
    @DeleteMapping  (value = "/Delete/{accountNumber}")
    public Mono<Void> Delete(@PathVariable("accountNumber") String accountNumber){
        return accountService.delete(accountNumber);
    }

    @GetMapping(value = "/findAccountByClient/{client}/{accountType}")
    public Mono<ResponseEntity<AccountEntity>> findClientByDocument(@PathVariable("client") String client,@PathVariable("accountType") String accountType){
        return accountService.findAccountByClient(client,accountType).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping(value = "/registerAccount")
    public Mono<ResponseEntity<AccountEntity>> registerClient(@RequestBody AccountEntity col){
        return accountService.registerAccount(col).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    @PutMapping(value = "/depositBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> depositBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.depositBalance(accountNumber,balance);
    }
    @PutMapping(value = "/withdrawBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> withdrawBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.withdrawBalance(accountNumber,balance);
    }
    @GetMapping(value = "/getBalance/{accountNumber}")
    public double getBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.getBalance(accountNumber);
    }
}
