package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.user.model.*;
import com.cross2u.user.util.Constant;
import com.cross2u.user.util.HttpClientUtil;
import com.cross2u.user.util.*;
import com.cross2u.user.service.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/business")
//@GetMapping是一个组合注解，是@RequestMapping(method = RequestMethod.GET)的缩写。
//@PostMapping是一个组合注解，是@RequestMapping(method = RequestMethod.POST)的缩写。
public class BusinessControllerL {

    @Autowired
    businessServiceL bs;
    @Autowired
    JsonResult jr;

    /*
    * 面向其他模块
    * */
    //1、根据Id得到business的头像，昵称，级别，主营行业,姓名
    @RequestMapping("/findBusinessDetailByBId/{bId}")
    public JsonResult findBusinessDetailByBId(
            @PathVariable("bId") BigInteger bId)
    {
        JSONObject result = bs.selectBusinessDetailByBId(bId);
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
    //2、一次得到多个B的头像和昵称
    @RequestMapping("/findManyBusinessByBId")
    public JsonResult findManyBusinessByBId(HttpServletRequest request)
    {
        String bId=request.getParameter("bId");
        JSONArray result = bs.selectManyBusinessByBId(bId);
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
    //3、查看某个用户是否收藏某个商品
    @RequestMapping("/findBIsCollectW")
    public Integer findBIsCollectW(
            @RequestParam("bId") BigInteger bId,
            @RequestParam("wId") BigInteger wId)
    {
        Collect result = bs.selectBIsCollectW(bId,wId);
        if(result!=null)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }




    /*
    * 面向前端的controller
    * */

    //1、小程序用户授权eCross
    @RequestMapping("/authorize1")
    public JsonResult authorize1(
            @RequestParam(value="code",required = false) String code)
//            @RequestParam("vWeiXinIcon") String vWeiXinIcon,
//            @RequestParam("vWeiXinName") String vWeiXinName
    {
        // 配置请求参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", Constant.APPID1);
        param.put("secret", Constant.APPSECRET1);
        param.put("js_code", code);
        param.put("grant_type", Constant.GRANTTYPE);
        // 发送请求
        System.out.println("code="+code);
        String wxResult = HttpClientUtil.doGet(Constant.LOGINURL, param);
        System.out.println(wxResult);
        JSONObject jsonObject = JSONObject.parseObject(wxResult);
        // 获取参数返回的
        String session_key = jsonObject.get("session_key").toString();
        String open_id = jsonObject.get("openid").toString();
        // 封装返回小程序
        Map<String, String> result = new HashMap<>();
        result.put("session_key", session_key);
        result.put("open_id", open_id);
        // 根据返回的user实体类，判断用户是否是新用户，是的话，将用户信息存到数据库
        Visitor vs = bs.selectByOpenId(open_id);
        if(vs == null){

            Visitor insert_visitor = new Visitor();
//            insert_visitor.setVWeiXinName(vWeiXinName);
//            insert_visitor.setVWeiXinIcon(vWeiXinIcon);
            insert_visitor.setVOpenId(open_id);

            // 添加到数据库
            Boolean flag = bs.insertVisitor(insert_visitor);
            if(!flag)
            {
                jr.setResult(ResultCodeEnum.ADD_ERROR);
            }
        }
        else{
            Business business = Business.dao.findFirst("select bId from business where bOpenId=?",open_id);
            if(business==null)//游客身份
            {
                result.put("bId","");
            }
            else//business身份
            {
                result.put("bId", business.getBId()+"");
            }
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        jr.setData(result);
        return jr;
    }
    //1、小程序用户授权-cross2u
    @RequestMapping("/authorize2")
    public JsonResult authorize2(
            @RequestParam(value="code",required = false) String code)
//            @RequestParam("vWeiXinIcon") String vWeiXinIcon,
//            @RequestParam("vWeiXinName") String vWeiXinName
    {
        // 配置请求参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", Constant.APPID2);
        param.put("secret", Constant.APPSECRET2);
        param.put("js_code", code);
        param.put("grant_type", Constant.GRANTTYPE);
        // 发送请求
        System.out.println("code="+code);
        String wxResult = HttpClientUtil.doGet(Constant.LOGINURL, param);
        System.out.println(wxResult);
        JSONObject jsonObject = JSONObject.parseObject(wxResult);
        // 获取参数返回的
        String session_key = jsonObject.get("session_key").toString();
        String open_id = jsonObject.get("openid").toString();
        // 封装返回小程序
        Map<String, String> result = new HashMap<>();
        result.put("session_key", session_key);
        result.put("open_id", open_id);
        // 根据返回的user实体类，判断用户是否是新用户，是的话，将用户信息存到数据库
        Visitor vs = bs.selectByOpenId(open_id);
        if(vs == null){

            Visitor insert_visitor = new Visitor();
//            insert_visitor.setVWeiXinName(vWeiXinName);
//            insert_visitor.setVWeiXinIcon(vWeiXinIcon);
            insert_visitor.setVOpenId(open_id);

            // 添加到数据库
            Boolean flag = bs.insertVisitor(insert_visitor);
            if(!flag)
            {
                jr.setResult(ResultCodeEnum.ADD_ERROR);
            }
        }
        else{
            Business business = Business.dao.findFirst("select bId from business where bOpenId=?",open_id);
            if(business==null)//游客身份
            {
                result.put("bId","");
            }
            else//business身份
            {
                result.put("bId", business.getBId()+"");
            }
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        jr.setData(result);
        return jr;
    }

    //3、添加浏览记录（当用户点进商品详情时）
    @RequestMapping("/addBrowseRecord")
    public JsonResult addBrowseRecord(
            @RequestParam("brOwner") BigInteger brOwner,
            @RequestParam("brWare") BigInteger brWare)
    {
        Browserecord browserecord = new Browserecord().set("brOwner",brOwner).set("brWare",brWare);

        /*动态维护用户-商品表，浏览为3,因为之后的收藏、加入购物车、购买都是在浏览的记录上完成，所以这里是insert*/
        Db.update("INSERT INTO ratings(bId,wId,rating) VALUES (?,?,3)",brOwner,brWare);

        boolean result = bs.insertBrowseRecord(browserecord);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }

    //15、收藏商品
    @RequestMapping("/addCollectWare")
    public JsonResult addCollectWare(
            @RequestParam("cOwner") BigInteger cOwner,
            @RequestParam("cWare") BigInteger cWare
    )
    {
        Collect collect = new Collect();
        collect.setCOwner(cOwner);
        collect.setCWare(cWare);
        boolean result = bs.insertCollectWare(collect);

        /*动态维护用户-商品表，收藏 4,因为原来浏览过，所以这里是update*/
        Db.update("UPDATE ratings SET rating=4 WHERE bId=? AND wId=?",cOwner,cWare);

        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //16、显示用户搜索记录
    @RequestMapping("/showSearchRecord")
    public JsonResult showSearchRecord(
            @RequestParam("bsrBusiness") BigInteger bsrBusiness
    )
    {
        List<Businesssearchrecord> result = bs.selectSearchRecord(bsrBusiness);
        if(!result.isEmpty())
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
    //56、添加用户搜索记录
    @RequestMapping("/addSearchRecord")
    public JsonResult addSearchRecord(
            @RequestParam("bsrBusiness") BigInteger bsrBusiness,
            @RequestParam("bsrContent") String bsrContent
    )
    {
        List<Businesssearchrecord> recordList = Businesssearchrecord.dao.find("select * from businesssearchrecord " +
                "where bsrBusiness=?",bsrBusiness);
        boolean flag = false;//该搜索记录在数据库中是没有的
        boolean result = false;
        for(Businesssearchrecord br:recordList)
        {
            if(br.getBsrContent().equals(bsrContent))
            {
                flag = true;
            }
        }
        if(!flag)
        {
            Businesssearchrecord searchrecord = new Businesssearchrecord();
            searchrecord.setBsrBusiness(bsrBusiness);
            searchrecord.setBsrContent(bsrContent);
            result = searchrecord.save();
        }
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }


}
