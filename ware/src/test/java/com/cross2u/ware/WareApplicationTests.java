package com.cross2u.ware;

import com.cross2u.ware.model.Attributeoption;
import com.cross2u.ware.model.Ware;
import com.cross2u.ware.service.WareServicesZ;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.ResultCodeEnum;
import com.cross2u.ware.util.Util;
import com.jfinal.json.Json;
import com.jfinal.plugin.activerecord.Record;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WareApplicationTests {

	@Autowired
	WareServicesZ ws;

	@Test
    public void test()
    {
        BigInteger wId=new BigInteger("2");
        Boolean response=ws.addOneWareBelong("1","",wId);
        System.out.println(response);
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
        Record record=ws.addFirstStep("352","355");
        System.out.println(record);
    }

}

