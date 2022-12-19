package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

class AccountServiceImplementationTest {

    @InjectMocks
    AccountServiceImplementation accountServiceImplementation;

    @Mock
    AccountRepository accountRepository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void linkDebitCardMainAccount() {
    }

    @Test
    void linkDebitCardSecondaryAccount() {

    }

    @Test
    void checkDebitCardMainAccountSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setDebitCardNumber("12345");
        accountEntity.setBalance(12.0);
        accountEntity.setDebitCardMainAccount(true);
        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountEntityList.add(accountEntity);
        Mockito.when(accountRepository.findAll()).thenReturn(Flux.fromIterable(accountEntityList));
        Mono<Boolean> result = accountServiceImplementation.checkDebitCardMainAccount("12345");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(true, result.block().booleanValue());
    }
    @Test
    void getBalanceByDebitCard() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setDebitCardNumber("12345");
        accountEntity.setBalance(12.0);
        accountEntity.setDebitCardMainAccount(true);
        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountEntityList.add(accountEntity);
        Mockito.when(accountRepository.findAll()).thenReturn(Flux.fromIterable(accountEntityList));
        Mono<Double> result = accountServiceImplementation.getDebitCardMainAccountBalance("78910");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(12.0, result.block());
    }

    @Test
    void addDebitCardPayment() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setDebitCardNumber("12345");
        accountEntity.setBalance(12.0);
        accountEntity.setDebitCardMainAccount(true);
        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountEntityList.add(accountEntity);
        Mockito.when(accountRepository.findAll()).thenReturn(Flux.fromIterable(accountEntityList));
        Mono<AccountEntity> result = accountServiceImplementation.addDebitCardPayment("78910",12.0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(12.0, result.block());
    }

    @Test
    void getByClient() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setDebitCardNumber("12345");
        accountEntity.setBalance(12.0);
        accountEntity.setDebitCardMainAccount(true);
        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountEntityList.add(accountEntity);
        Mockito.when(accountRepository.findAll()).thenReturn(Flux.fromIterable(accountEntityList));
        Flux<AccountEntity> result = accountServiceImplementation.getByClient("17593382");
        Assertions.assertNotNull(result);
    }
}