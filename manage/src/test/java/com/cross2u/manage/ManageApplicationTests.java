package com.cross2u.manage;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.manage.service.ManageServiceZ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManageApplicationTests {

	@Autowired
	ManageServiceZ ms;

	@Test
	public void test() {
		Object countM=ms.countM();//待审核M的申请注册
		System.out.println("countM"+countM);
		Integer countB=ms.countB();//待审核B的申请注册
		System.out.println("countB"+countB);
		Integer countWare=ms.countWare();//待审核异常商品
		System.out.println("countWare"+countWare);
		Integer countStore=ms.countStore();//待审核异常店铺
		System.out.println("countStore"+countStore);

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("countM",countM);
		jsonObject.put("countB",countB);
		jsonObject.put("countWare",countWare);
		jsonObject.put("countStore",countStore);
		System.out.println(jsonObject);

	}

}

