package com.cross2u.user;


import com.cross2u.user.model.Administrator;
import com.cross2u.user.service.businessServiceL;
import com.cross2u.user.util.HttpClientUtil;
import com.jfinal.plugin.activerecord.Db;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.alibaba.fastjson.JSONObject;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {

	@Autowired
	businessServiceL bs;
	@Test
	public void contextLoads() {

//        JSONArray ja = bs.selectFirstClass();
//        JSONObject FI = ja.getJSONObject(0);
//        JSONObject FI2 = ja.getJSONObject(1);
//		System.out.println(ja);
//		String str = "";
//		Calendar c = Calendar.getInstance();
//		str += c.get(Calendar.YEAR);
//		str += String.format("%02d", c.get(Calendar.MONTH));
//		str += String.format("%02d", c.get(Calendar.DATE));
//		str += String.format("%02d", c.get(Calendar.HOUR));
//		str += String.format("%02d", c.get(Calendar.MINUTE));
//		str += String.format("%02d", c.get(Calendar.SECOND));
//		System.out.println(str);
//		System.out.println(1%10000);
//		System.out.println(String.format("%04d", 345%10000));
//		Db.update("INSERT INTO ratings(bId,wId,rating) VALUES (1,4,8)");
		Administrator a = Administrator.dao.findById(2);
        System.out.println(a);
    }

}

