package com.cross2u.ware.controller;

import com.cross2u.ware.model.Bevalreply;
import com.cross2u.ware.model.Product;
import com.cross2u.ware.model.Ware;
import com.cross2u.ware.service.WareServiceL;
import com.cross2u.ware.util.ResultCodeEnum;
import com.cross2u.ware.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;


@RestController
@RequestMapping("/ware")
public class WareControllerL {
    @Autowired
    WareServiceL ws;
    @Autowired
    BaseResponse jr;

    /*
   *面向其他模块的controller
   * */
    //1、根据Id得到ware
    @RequestMapping("/findWareById/{wId}")
    public BaseResponse findWareById(
            @PathVariable("wId") BigInteger wId)
    {
        Ware result = ws.selectWareById(wId);
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
    //2、根据商品编码得到ware
    @RequestMapping("/findWareByIdentifier/{wIdentifier}")
    public BaseResponse findWareByIdentifier(
            @PathVariable("wIdentifier") String wIdentifier)
    {
        Ware result = ws.selectWareByIdentifier(wIdentifier);
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
    //3、根据Id得到product
    @RequestMapping("/findProductById/{pId}")
    public BaseResponse findProductById(
            @PathVariable("pId") BigInteger pId)
    {
        JSONObject result = ws.selectProductById(pId);
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
    //4、根据单品编码得到product
    @RequestMapping("/findProductByIdentifier/{pIdentifier}")
    public BaseResponse findProductByIdentifier(
            @PathVariable("pIdentifier") String pIdentifier)
    {
        JSONObject result = ws.selectProductByIdentifier(pIdentifier);
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
    * 面向前端的controller
    * */
    //2、首页显示商品（根据零售商所选的主营类别进行显示）
    @RequestMapping("/showWare")
    public BaseResponse showWare(
            @RequestParam(value = "bId", required = false) BigInteger bId,
            @RequestParam(value = "pageIndex", required = true) Integer pageIndex,
            @RequestParam(value = "pageSize", required = true) Integer pageSize
            )
    {
        JSONArray showWareList = ws.selectAllWare(bId,pageIndex,pageSize);
        if(!showWareList.isEmpty())
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

    //4、商品详情页
    @RequestMapping("/showWareBrief")
    public BaseResponse showWareBrief(
            @RequestParam("wId") BigInteger wId,
            @RequestParam(value = "bId",required = false) BigInteger bId)
    {
        JSONObject result = ws.selectWareBrief(wId,bId);
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
    //5、查看商品参数
    @RequestMapping("/showWareAttribute")
    public BaseResponse showWareAttribute(@RequestParam("wId") BigInteger wId)
    {
        JSONArray result = ws.selectWareAttributeByWId(wId);
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
    //7、查看商品的评价
    @RequestMapping("/showComment")
    public BaseResponse showComment(
            @RequestParam("ewWId") BigInteger ewWId,
            @RequestParam("comStar") Integer comStar,
            @RequestParam("pageIndex") Integer pageIndex,
            @RequestParam("pageSize") Integer pageSize)
    {
        JSONArray result = ws.selectWareComment(ewWId,comStar,pageIndex,pageSize);
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
    public BaseResponse showProduct(@RequestParam("pWare") BigInteger pWare)
    {
        JSONArray result = ws.selectProductFromWare(pWare);
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
    public BaseResponse addCommentReply(
            @RequestParam("berECId") BigInteger berECId,
            @RequestParam(value="berErId",required = false) BigInteger berErId,
            @RequestParam("berSpeaker") BigInteger berSpeaker,
            @RequestParam("berCotent") String berCotent

    )
    {
        Bevalreply bevalreply = new Bevalreply();
        bevalreply.setBerECId(berECId);
        bevalreply.setBerErId(berErId);
        bevalreply.setBerSpeaker(berSpeaker);
        bevalreply.setBerCotent(berCotent);
        boolean result = ws.insertBevalreply(bevalreply);
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
    //11、查看单个商品评价的详情
    @RequestMapping("/showCommentDetail")
    public BaseResponse showCommentDetail(@RequestParam("ewId") BigInteger ewId)
    {
        JSONObject result = ws.selectCommentDetail(ewId);
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
    //43、首页显示一级目录
    @RequestMapping("/showFirstClass")
    public BaseResponse showFirstClass()
    {
        JSONArray result = ws.selectFirstClass();
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
    //44、显示二级三级目录
    @RequestMapping("/showSecondClass")
    public BaseResponse showSecondClass(@RequestParam("ctParentId") BigInteger ctParentId)
    {
        JSONArray result = ws.selectSecondClass(ctParentId);
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
