package com.cross2u.user;


import com.cross2u.user.service.ManufactureServiceZ;
import com.cross2u.user.service.businessServiceL;
import com.cross2u.user.util.MailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {


	@Autowired
	RestTemplate restTemplate;

	@Autowired
	businessServiceL bs;

	@Autowired
	ManufactureServiceZ ms;

	@Test
	public void contextLoads() {
		System.out.println(1+" "+(restTemplate==null));
		JSONObject array = restTemplate.getForObject("http://Ware/ware/getTopFourWare?sId="+1,JSONObject.class);
	}
	@Test
	public  void test(){
		Boolean sign=ms.haveEmail("879748195@qq.com");
		System.out.println("sign"+sign);
	}

}

