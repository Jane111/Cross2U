package com.cross2u.store;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.store.service.StoreServiceZ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreApplicationTests {

	@Autowired
	StoreServiceZ service;

	@Test
	public void test() {
		JSONArray array=service.dispatchShowDispatchs("1");
		System.out.println(array);
	}

}

