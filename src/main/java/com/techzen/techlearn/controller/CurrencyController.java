package com.techzen.techlearn.controller;

import com.techzen.techlearn.client.CurrencyClient;
import com.techzen.techlearn.dto.response.ResponseData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/currencies")
@Slf4j
public class CurrencyController {

    CurrencyClient currencyClient;

    @GetMapping
    public ResponseData<?> findAll() {
        return ResponseData.builder()
                .result(currencyClient.findAll().getBody())
                .build();
    }

}
