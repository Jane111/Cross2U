package com.cross2u.user;

import com.cross2u.user.service.BusinessServiceZ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {

	@Autowired
	BusinessServiceZ bs;
	@Test
	public void contextLoads() {
		bs.deleteCollect("2");

	}

}

