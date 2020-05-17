package com.study.controller.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-code")
@RequestMapping("/api/code")
public interface CodeServiceFeignClient {

    @GetMapping("/validate/{email}/{code}")
    public Integer validate(@PathVariable(name = "email") String email,
                            @PathVariable(name = "code") String code);
}
