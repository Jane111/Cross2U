package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.user.model.*;
import com.cross2u.user.util.Constant;
import com.cross2u.user.util.HttpClientUtil;
import com.cross2u.user.util.*;
import com.cross2u.user.service.businessServiceL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;



@RestController
@RequestMapping("/business")
//@GetMapping是一个组合注解，是@RequestMapping(method = RequestMethod.GET)的缩写。
//@PostMapping是一个组合注解，是@RequestMapping(method = RequestMethod.POST)的缩写。
public class bLj {

    @Autowired
    businessServiceL bs;
    JsonResult jr;

    //1、小程序用户授权
    @RequestMapping("/authorize")
    public JsonResult authorize(
            @RequestParam("code") String code,
            @RequestParam("vWeiXinIcon") String vWeiXinIcon,
            @RequestParam("vWeiXinName") String vWeiXinName)
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
            insert_visitor.setVWeiXinName(vWeiXinName);
            insert_visitor.setVWeiXinIcon(vWeiXinIcon);
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
    //2、首页显示商品（根据零售商所选的主营类别进行显示）
    @RequestMapping("/showWare")
    public JsonResult showWare(@RequestParam("bId") BigInteger bId)
    {
        JSONArray showWareList = bs.selectAllWare(bId);
        if(showWareList!=null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(showWareList);
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
        return jr;
    }
    //4、商品详情页
    @RequestMapping("/showWareBrief")
    public JsonResult showWareBrief(@RequestParam("wId") BigInteger wId)
    {
        JSONObject result = bs.selectWareBrief(wId);
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
//    7、查看商品的评价
    @RequestMapping("/showComment")
    public JsonResult showComment(@RequestParam("ewWId") BigInteger ewWId)
    {
        JSONArray result = bs.selectWareComment(ewWId);
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
//8、查看商品包含的单品
    @RequestMapping("/showProduct")
    public JsonResult showProduct(@RequestParam("ewWId") BigInteger pWare)
    {
        JSONArray result = bs.selectProductFromWare(pWare);
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

//    9、加入购物车
    @RequestMapping("/addStock")
    public JsonResult addStock(
            @RequestParam("sPId") BigInteger sPId,
            @RequestParam("sSId") BigInteger sSId,
            @RequestParam("sBid") BigInteger sBid,
            @RequestParam("sNumber") Integer sNumber,
            @RequestParam("sSum") Float sSum
            )
    {
        Stock stock = new Stock();
        stock.setSPId(sPId);
        stock.setSId(sSId);
        stock.setSBid(sBid);
        stock.setSNumber(sNumber);
        stock.setSSum(sSum);
        boolean result = bs.insertStock(stock);
        if(!result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return jr;
    }
    //10、购买商品（创建订单）
    @RequestMapping("/addIndent")
    public JsonResult addIndent(
            @RequestParam("inBusiness") BigInteger inBusiness,
            @RequestParam("inStore") BigInteger inStore,
            @RequestParam("inProduct") BigInteger inProduct,
            @RequestParam("inProductNum") Integer inProductNum,
            @RequestParam("inTotalMoney") Float inTotalMoney
    )
    {
        Indent indent = new Indent();
        indent.setInBusiness(inBusiness);
        indent.setInStore(inStore);
        indent.setInProduct(inProduct);
        indent.setInProductNum(inProductNum);
        indent.setInTotalMoney(inTotalMoney);
        //默认填充的东西
        indent.setInLeftNum(inProductNum);//订单单品分销的剩余量一开始默认为购买单品的数量
        indent.setInStatus(0);//创建时默认为未支付状态
        boolean result = bs.insertIndent(indent);
        if(!result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return jr;
    }
    //11、查看单个商品评价的详情
    @RequestMapping("/showCommentDetail")
    public JsonResult showCommentDetail(@RequestParam("ewId") BigInteger ewId)
    {
        JSONObject result = bs.selectCommentDetail(ewId);
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
    //12、回复商品评价
    @RequestMapping("/addCommentReply")
    public JsonResult addCommentReply(
            @RequestParam("berECId") BigInteger berECId,
            @RequestParam("berErId") BigInteger berErId,
            @RequestParam("berSpeaker") BigInteger berSpeaker,
            @RequestParam("berCotent") String berCotent

    )
    {
        Bevalreply bevalreply = new Bevalreply();
        bevalreply.setBerECId(berECId);
        bevalreply.setBerErId(berErId);
        bevalreply.setBerSpeaker(berSpeaker);
        bevalreply.setBerCotent(berCotent);
        boolean result = bs.insertBevalreply(bevalreply);
        if(!result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
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
        if(!result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
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








































    //35、首页显示一级目录
    @RequestMapping("/showFirstClass")
    public JsonResult showFirstClass()
    {
        JSONArray result = bs.selectFirstClass();
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
    //36、显示二级三级目录
    @RequestMapping("/showSecondClass")
    public JsonResult showSecondClass(@RequestParam("ewWId") BigInteger ctParentId)
    {
        JSONArray result = bs.selectSecondClass(ctParentId);
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
