package com.cross2u.indent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.Service.IndentServiceZ;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndentApplicationTests {

	@Autowired
	IndentServiceZ iw;

	@Test
	public void contextLoads() {
		Boolean array=iw.isOverDeadLine("1");
		System.out.println(array);
	}

}

