package com.study.controller.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-email")
@RequestMapping("/api/email")
public interface EmailServiceFeignClient {

    @GetMapping("/{email}/{code}")
    public Boolean sendCode(@PathVariable(name = "email") String email,
                            @PathVariable(name = "code") String code);
}
