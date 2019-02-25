package com.cross2u.user;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.service.BusinessServiceZ;
import com.cross2u.user.util.BaseResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {

	@Autowired
	BusinessServiceZ bs;
	@Autowired
	RestTemplate restTemplate;

	@Test
	public void contextLoads() {
//		bs.showStoreDetail("1","12121");
		String sId="1";
		JSONObject array = restTemplate.getForObject("http://localhost:8003/ware/getTopFourWare?sId="+sId,JSONObject.class);
		System.out.println(array.get("data"));

	}

	@Test
	public void test() {
		System.out.println("restTemplete+"+(restTemplate==null));
		String sId="1";
		String array = restTemplate.getForObject("http://localhost:8003/ware/demo?sId="+sId,String.class);
		System.out.println(array);

	}

}

