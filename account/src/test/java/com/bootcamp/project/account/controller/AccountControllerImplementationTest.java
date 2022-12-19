package com.bootcamp.project.account.controller;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebFluxTest(AccountController.class)
public class AccountControllerImplementationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @Test
    public void save()
    {
        AccountEntity OE = new AccountEntity();
        Mono<AccountEntity> MTest = Mono.just(OE);
        when(accountService.save(OE)).thenReturn(MTest);
        webTestClient.post().uri("/Account/Save")
                .body(Mono.just(MTest),AccountEntity.class)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void update()
    {
        AccountEntity OE = new AccountEntity();
        Mono<AccountEntity> MTest = Mono.just(OE);
        when(accountService.update("ABC",100)).thenReturn(MTest);
        webTestClient.put().uri("/Account/Update/ABC/400")
                .body(Mono.just(MTest),AccountEntity.class)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void delete()
    {
        given(accountService.delete(any())).willReturn(Mono.empty());
        webTestClient.delete().uri("/Account/Delete/ABC")
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void getOne()
    {
        AccountEntity OE = new AccountEntity(null,"AAA",null,null,null,null,0,0,null,null,null,null,null,0,0,0,0,0,0,0,0,null,null);
        Mono<AccountEntity> MTest = Mono.just(OE);
        when(accountService.getOne(any())).thenReturn(MTest);
        Flux<AccountEntity> responseBody = webTestClient.get().uri("/Account/FindOne/AAA")
                .exchange()
                .expectStatus().isOk()
                .returnResult(AccountEntity.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getAccountNumber().equals("AAA"))
                .verifyComplete();
    }
    @Test
    public void getAll()
    {
        AccountEntity OE = new AccountEntity(null,"AAA",null,null,null,null,0,0,null,null,null,null,null,0,0,0,0,0,0,0,0,null,null);
        AccountEntity OE2 = new AccountEntity(null,"BBB",null,null,null,null,0,0,null,null,null,null,null,0,0,0,0,0,0,0,0,null,null);
        Flux<AccountEntity> MTest = Flux.just(OE,OE2);
        when(accountService.getAll()).thenReturn(MTest);
        Flux<AccountEntity> responseBody = webTestClient.get().uri("/Account/FindAll")
                .exchange()
                .expectStatus().isOk()
                .returnResult(AccountEntity.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(OE)
                .expectNext(OE2)
                .verifyComplete();
    }
}
