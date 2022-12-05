package com.bootcamp.project.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@FeignClient(name = "client")
public interface ClientClient {
    @GetMapping(value = "/CheckClient/{documentNumber}")
    Mono<Boolean> checkClient(@PathVariable("documentNumber") String documentNumber);
}
