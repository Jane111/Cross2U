package com.cross2u.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
public class demo {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/readIndent")
    public String readIndent(){
        String s = restTemplate.getForObject("http://Indent/getIndent",String.class);
        return "my indent？"+s;
    }
}
