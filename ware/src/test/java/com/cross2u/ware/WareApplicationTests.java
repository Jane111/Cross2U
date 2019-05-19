package com.cross2u.ware;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.service.WareServiceL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WareApplicationTests {

    @Autowired
    RestTemplate restTemplate;
	@Autowired
    WareServiceL ws;
	@Test
	public void contextLoads() {
        ws.selectWareBrief(new BigInteger("1"),new BigInteger("1"));
	}

}

