package com.boosting.code.auth_gateway_resolver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class GatewayResolverController {

    @GetMapping()
    public ResponseEntity<String> getResource(){
        String message="Hello Protected World";
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
}
