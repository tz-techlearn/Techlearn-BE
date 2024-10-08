package com.techzen.techlearn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pointService", url = "${application.urlClient}/points")
public interface PointClient {

    @GetMapping
    ResponseEntity<?> findAllPointsPackage(@RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "10") int pageSize);
}
