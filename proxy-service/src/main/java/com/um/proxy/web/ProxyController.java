package com.um.proxy.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

    @GetMapping("/proxy/health")
    public String health() {
        return "OK";
    }
}
