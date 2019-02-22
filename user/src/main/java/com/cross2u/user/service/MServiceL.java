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
    public Mainmanufacturer selectMMLogoDetail(BigInteger mmId)
    {
        return Mainmanufacturer.dao.findFirst("select mmlogo from Mainmanufacturer where mmId=?",mmId);
    }

    /*
    * service
    * */
    //显示M的基本信息
    public Mainmanufacturer selectMDetail(BigInteger sId)
    {
        return Mainmanufacturer.dao.findFirst("select * from Mainmanufacturer where mmStore=?",sId);
    }



}
