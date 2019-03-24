package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.cross2u.user.service.*;
import com.cross2u.user.util.*;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
@CrossOrigin
@RestController
@RequestMapping("/manufacturer")
public class MContollerL {
    @Autowired
    MServiceL ms;
    @Autowired
    JsonResult jr;
    /*
    * 面向其他模块
    * */
    //1、根据Id得到M的logo
    @RequestMapping("/findMMLogoDetail/{mmId}")
    public JsonResult findMMLogoDetail(
            @PathVariable("mmId") BigInteger mmId)
    {
        JSONObject result = ms.selectMMLogoDetail(mmId);
        if(result!=null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    /*
    * 面向前端
    * */
    //个人中心
    //显示M的基本信息
    @RequestMapping("/showMyself")
    public JsonResult showMyself(@RequestParam("sId") BigInteger sId)
    {
        Mainmanufacturer result = ms.selectMDetail(sId);
        if(result!=null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }


}
