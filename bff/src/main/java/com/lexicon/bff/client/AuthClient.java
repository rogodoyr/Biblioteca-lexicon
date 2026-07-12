package com.lexicon.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "auth")
public interface AuthClient {

    @PostMapping("/api/v1/auth/login")
    Map<String, Object> login(@RequestBody Map<String, Object> request);
}
