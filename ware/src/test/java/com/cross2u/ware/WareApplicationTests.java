package com.cross2u.ware;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.Stock;
import com.cross2u.ware.model.Ware;
import com.cross2u.ware.service.WareServiceL;
import com.cross2u.ware.util.BaseUserRecommender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WareApplicationTests {

    @Autowired
    RestTemplate restTemplate;
	@Autowired
    WareServiceL ws;
    @Autowired
    BaseUserRecommender ur;
	@Test
	public void contextLoads() throws Exception{
//        ws.selectWareInStock(new BigInteger("1"));
//        List<BigInteger> list = ur.recommendBaseUser(new BigInteger("1"));
//        System.out.println(list);
//        JSONArray list = ws.selectAllWare(new BigInteger("1"),1,2);
//        System.out.println(list);
        String searchContent = "小米";
        List<Ware> result = Ware.dao.find("select * from ware where wtitle like '%"+searchContent+"%'");
	    System.out.println(result);
	}

}

