package com.techzen.techlearn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "currencyService", url = "${application.urlClient}/currencies")
public interface CurrencyClient {

    @GetMapping
    ResponseEntity<?> findAll();
}
