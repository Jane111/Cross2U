package com.cross2u.indent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.Service.IndentServiceL;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.jfinal.plugin.activerecord.Db.query;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndentApplicationTests {

	@Autowired
    IndentServiceL IS;
	@Test
	public void contextLoads() {
		//如果订单编号不为空，首先按照订单编号搜索，like
//		List<Record> orderList = Db.find("");
		//如果订单编号为空，按照商品名称/代理商姓名搜索
        List<Record> orderList = Db.find("select *");

	}

}

