package com.cross2u.user.controller;

import com.cross2u.user.util.HttpClientUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@ConfigurationProperties(prefix = "weixin")
@RequestMapping("/business")
public class bLj {


}
