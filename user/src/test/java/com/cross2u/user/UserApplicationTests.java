package com.cross2u.user;

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
		bs.deleteCollect("2");

	}
	@Test
	public void test(){
		BaseResponse response = restTemplate.getForObject("http://Store/store/deleteCop?copId=5",BaseResponse.class);
		System.out.println(response);
	}

}

