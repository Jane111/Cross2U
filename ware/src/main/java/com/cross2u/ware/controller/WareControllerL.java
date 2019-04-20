package com.cross2u.ware.controller;

import com.cross2u.ware.model.*;
import com.cross2u.ware.service.WareServiceL;
import com.cross2u.ware.util.ResultCodeEnum;
import com.cross2u.ware.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin
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
    //加多个
//    9、加入购物车
    @RequestMapping("/addStock")
    public BaseResponse addStock(
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
                    stock.setSkPId(sPId[index]);
                    stock.setSkSId(sSId);
                    stock.setSkBid(sBid);
                    stock.setSkNumber(sNumber[index]);
                    stock.setSkSum(sSum[index]);
                    stock.setSkSumUnit(sSumUnit[index]);
                    result = ws.insertStock(stock) & result;
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
    //45、“看了又看”中基于item的推荐
    @RequestMapping("/showWareFromRecommender")
    public BaseResponse showWareFromRecommender(
            @RequestParam("wId") BigInteger wId,
            @RequestParam(value = "bId",required = false) BigInteger bId
    )
    {
        JSONArray result;
        if(bId==null)
        {
            result = ws.selectWareFromClass(wId,0);//推荐同类型的商品
        }
        else
        {
            result = ws.selectWareFromRecommender(bId,wId);//使用推荐算法
        }
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
    //46、显示购物车中的商品
    @RequestMapping("/showWareInStock")
    public BaseResponse showWareInStock(
            @RequestParam("bId") BigInteger bId
    )
    {
        JSONArray result=ws.selectWareInStock(bId);
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
    //48、购物车中单品数量的修改
    @RequestMapping("/UpdateStockProductNum")
    public BaseResponse UpdateStockProductNum(
            @RequestParam("skId") BigInteger skId,
            @RequestParam("skNumber") Integer skNumber
    )
    {
        Stock stock = new Stock();
        stock.setSkId(skId);
        stock.setSkNumber(skNumber);
        boolean result = stock.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        return jr;
    }
    //49、商品首页的找相似（同一类商品）
    @RequestMapping("/showSameClassWare")
    public BaseResponse showSameClassWare(
            @RequestParam("wId") BigInteger wId
    )
    {
        JSONArray result =  ws.selectWareFromClass(wId,1);//推荐同类型的商品
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
    //51、b查看库存,按照单品分类
    @RequestMapping("/showIndentLeftProductNum")
    public BaseResponse showIndentLeftProductNum(
            @RequestParam("bId") BigInteger bId,
            @RequestParam("inLeftStatus") Integer inLeftStatus)
    {
        JSONArray result = ws.showIndentLeftProductNum(bId,inLeftStatus);
        if(result==null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else if (result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //53、评价订单中的商品（打分+文字评论）
    @RequestMapping("/addEvalWare")
    public BaseResponse addEvalWare(
            @RequestParam("ewInId") BigInteger ewInId,
            @RequestParam("ewCommentator") BigInteger ewCommentator,
            @RequestParam("ewWId") BigInteger ewWId,
            @RequestParam("ewPId") BigInteger ewPId,
            @RequestParam("ewRank") Integer ewRank,
            @RequestParam("ewCotent") String ewCotent,
            @RequestParam(value = "ewImg",required = false) String ewImg,
            @RequestParam(value = "ewImg2",required = false) String ewImg2,
            @RequestParam(value = "ewImg3",required = false) String ewImg3)
    {
        Evalware evalware = new Evalware();
        evalware.setEwInId(ewInId);
        evalware.setEwCommentator(ewCommentator);
        evalware.setEwWId(ewWId);
        evalware.setEwPId(ewPId);
        evalware.setEwRank(ewRank);
        evalware.setEwCotent(ewCotent);
        evalware.setEwImg(ewImg);
        evalware.setEwImg2(ewImg2);
        evalware.setEwImg3(ewImg3);
        boolean result = ws.insertEvalWare(evalware);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //A-18、显示举报商品
    @RequestMapping("/showBadWare")
    public BaseResponse showBadWare(
            @RequestParam("agiType") BigInteger agiType,
            @RequestParam("agiResult") Integer agiResult
    )
    {
        JSONArray result = ws.selectAbnormalGoodInfo(agiType,agiResult);
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
    //A-19、审核举报商品
    @RequestMapping("/checkBadWare")
    public BaseResponse checkBadWare(
            @RequestParam("agiId") BigInteger agiId,
            @RequestParam("agiAId") BigInteger agiAId,
            @RequestParam("agiResult") Integer agiResult
    )
    {
        Abnormalgoodsinfo abNormalGood = new Abnormalgoodsinfo();
        abNormalGood.setAgiId(agiId);
        abNormalGood.setAgiAId(agiAId);
        abNormalGood.setAgiResult(agiResult);
        boolean result = abNormalGood.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //A-24、显示举报评论
    @RequestMapping("/showBadComment")
    public BaseResponse showBadComment(
            @RequestParam("aerType") BigInteger aerType,
            @RequestParam("aerState") Integer aerState
    )
    {
        JSONArray result = ws.selectBadComment(aerType,aerState);
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
    //A-25、审核举报评论
    @RequestMapping("/checkBadComment")
    public BaseResponse checkBadComment(
            @RequestParam("aerId") BigInteger aerId,
            @RequestParam("aerAId") BigInteger aerAId,
            @RequestParam("aerState") Integer aerState
    )
    {
        Abevalreport abevalreport = new Abevalreport();
        abevalreport.setAerId(aerId);
        abevalreport.setAerAId(aerAId);
        abevalreport.setAerState(aerState);
        boolean result = abevalreport.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //......搜索
    @RequestMapping("/searchWare")
    public BaseResponse searchWare(
            @RequestParam("searchContent") String searchContent
    )
    {
        List<Ware> result = Ware.dao.find("select * from ware where wtitle like '%"+searchContent+"%'");
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(result);
        return jr;
    }


}
