package com.enigma.crimson.api.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiInfoController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "name", "crimson",
                "status", "ok",
                "frontend", "http://localhost:3000",
                "endpoints", new String[]{
                        "/api/v1/customers",
                        "/api/v1/accounts",
                        "/api/v1/transfers"
                }
        );
    }
}
