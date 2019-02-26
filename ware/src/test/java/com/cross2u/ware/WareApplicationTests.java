package com.cross2u.ware;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.Attributeoption;
import com.cross2u.ware.service.WareServiceL;
import com.cross2u.ware.service.WareServicesZ;
import com.jfinal.plugin.activerecord.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WareApplicationTests {

	@Autowired
    WareServiceL wsL;

    @Autowired
    WareServicesZ ws;

	@Test
    public void test()
    {
        JSONObject object=wsL.getStoreDetail(new BigInteger("1"));
        System.out.println(object);
    }

	@Test
	public void attributeoptions() {
        List<Attributeoption> attributeoptions=ws.showAttrOptions("2");
        System.out.println("?????"+attributeoptions.size());
        for (int i=0;i<attributeoptions.size();i++)
        {
            Attributeoption attributeoption=attributeoptions.get(i);
            System.out.println(attributeoption);
        }
	}

    @Test
    public void outputExcels() {
        List<Record> wares=ws.outputExcelAll("1");//店铺所有商品
        System.out.println("?????"+wares.size());
        for (int i=0;i<wares.size();i++)
        {
            Record ware=wares.get(i);
            System.out.println(ware);
        }
    }

    @Test
    public void addFirstStep(){
        JSONObject record=ws.addFirstStep("352","355");
        System.out.println(record);
    }

}

