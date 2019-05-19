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
    @RequestMapping("/findManyBusinessByBId/{bId}")
    public JsonResult findManyBusinessByBId(
            @PathVariable("bId") String bId)
    {
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
    //1、小程序用户授权
    @RequestMapping("/authorize")
    public JsonResult authorize(
            @RequestParam("code") String code)
//            @RequestParam("vWeiXinIcon") String vWeiXinIcon,
//            @RequestParam("vWeiXinName") String vWeiXinName
    {
        // 配置请求参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", Constant.APPID);
        param.put("secret", Constant.APPSECRET);
        param.put("js_code", code);
        param.put("grant_type", Constant.GRANTTYPE);
        // 发送请求
        String wxResult = HttpClientUtil.doGet(Constant.LOGINURL, param);
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
//todo 加多个
//    9、加入购物车
    @RequestMapping("/addStock")
    public JsonResult addStock(
            @RequestParam("sPId") BigInteger[] sPId,
            @RequestParam("sSId") BigInteger sSId,
            @RequestParam("sBid") BigInteger sBid,
            @RequestParam("sNumber") Integer[] sNumber,
            @RequestParam("sSum") Float[] sSum,
            @RequestParam("sSumUnit") Integer[] sSumUnit
            )
    {
        boolean succeed = Db.tx(new IAtom(){
            int index;
            boolean result = true;
            public boolean run() throws SQLException {
                for(index=0;index<sPId.length;index++)
                {
                    Stock stock = new Stock();
                    stock.setSPId(sPId[index]);
                    stock.setSSId(sSId);
                    stock.setSBid(sBid);
                    stock.setSNumber(sNumber[index]);
                    stock.setSSum(sSum[index]);
                    stock.setSSumUnit(sSumUnit[index]);
                    result = bs.insertStock(stock) & result;
                }
                return result;
            }
        });
        if(succeed)
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

}
