package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Service
public class MServiceL {

    /*
    * 面向其他模块 provider
    * */
    public JSONObject selectMMLogoDetail(BigInteger mmId)
    {
        JSONObject mmLogoDetail = new JSONObject();
        Mainmanufacturer mainmanufacturer = Mainmanufacturer.dao.findFirst("select mmLogo from mainmanufacturer where mmId=?",mmId);
        mmLogoDetail.put("mmlogo",mainmanufacturer.getMmLogo());
        return mmLogoDetail;

    }

    /*
    * service
    * */
    //显示M的基本信息
    public Mainmanufacturer selectMDetail(BigInteger sId)
    {
        return Mainmanufacturer.dao.findFirst("select * from mainmanufacturer where mmStore=?",sId);
    }



}
