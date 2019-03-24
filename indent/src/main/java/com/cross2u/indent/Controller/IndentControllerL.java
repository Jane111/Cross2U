package com.cross2u.indent.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cross2u.indent.util.*;
import com.cross2u.indent.Service.*;
import com.cross2u.indent.model.*;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/indent")
public class IndentControllerL {
    @Autowired
    IndentServiceL indentServiceL;
    @Autowired
    BaseResponse jr;
    //10、购买商品（创建订单）
    @RequestMapping("/addIndent")
    public BaseResponse addIndent(
            @RequestParam("inBusiness") BigInteger inBusiness,
            @RequestParam("inStore") BigInteger inStore,
            @RequestParam("inProduct") BigInteger inProduct,
            @RequestParam("inWare") BigInteger inWare,
            @RequestParam("inProductNum") Integer inProductNum,
            @RequestParam("inTotalMoney") Float inTotalMoney
    )
    {
        Indent indent = new Indent();
        indent.setInBusiness(inBusiness);
        indent.setInStore(inStore);
        indent.setInProduct(inProduct);
        indent.setInWare(inWare);
        indent.setInProductNum(inProductNum);
        indent.setInTotalMoney(inTotalMoney);
        //默认填充的东西

        //确定订单编号
        String str = "";
        Calendar c = Calendar.getInstance();
        str += c.get(Calendar.YEAR);//四位
        str += String.format("%02d", c.get(Calendar.MONTH));//两位
        str += String.format("%02d", c.get(Calendar.DATE));//两位
        str += String.format("%02d", c.get(Calendar.HOUR));//两位
        str += String.format("%02d", c.get(Calendar.MINUTE));//两位
        str += String.format("%02d", c.get(Calendar.SECOND));//两位
        str += String.format("%02d", c.get(Calendar.SECOND));//两位
        str += String.format("%04d", inBusiness.mod(new BigInteger("10000")));//bId最后四位
        indent.setInNum(str);

        indent.setInLeftNum(inProductNum);//订单单品分销的剩余量一开始默认为购买单品的数量
        indent.setInStatus(0);//创建时默认为未支付状态
        BigInteger result = indentServiceL.insertIndent(indent);
        if(result!=null)
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

    //39、B评价订单
    @RequestMapping("/updateB2MIndent")
    public BaseResponse updateB2MIndent(
            @RequestParam("inId") BigInteger inId,
            @RequestParam("inBtoM") Integer inBtoM
    )
    {
        Indent indent = new Indent();
        indent.setInId(inId);//订单Id
        indent.setInBtoM(inBtoM);//B对应M的评价
        boolean result = indentServiceL.updateIndent(indent);
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
    //40、与M退款订单找管理员介入
    @RequestMapping("/updateDrawBackInfo")
    public BaseResponse updateDrawBackInfo(
            @RequestParam("diId") BigInteger diId,
            @RequestParam("diStatus") Integer diStatus
    )
    {
        Drawbackinfo drawbackinfo = new Drawbackinfo();
        drawbackinfo.setDiId(diId);//退款申请信息ID
        drawbackinfo.setDiStatus(diStatus);//退款状态
        boolean result = indentServiceL.updateDrawBackInfo(drawbackinfo);
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
    //42、B申请退款
    @RequestMapping("/addDrawBackInfo")
    public BaseResponse addDrawBackInfo(
            @RequestParam("diReporter") BigInteger diReporter,
            @RequestParam("diInId") BigInteger diInId,
            @RequestParam("diType") BigInteger diType,
            @RequestParam("diNumber") Integer diNumber,
            @RequestParam("diMoney") Float diMoney,
            @RequestParam("diReasons") String diReasons,
            @RequestParam("diImg1") String diImg1,
            @RequestParam("diImg2") String diImg2,
            @RequestParam("diImg3") String diImg3
    )
    {
        Drawbackinfo drawbackinfo = new Drawbackinfo();
        drawbackinfo.setDiReporter(diReporter);
        drawbackinfo.setDiInId(diInId);
        drawbackinfo.setDiType(diType);
        drawbackinfo.setDiNUmber(diNumber);
        drawbackinfo.setDiMoney(diMoney);
        drawbackinfo.setDiReasons(diReasons);
        drawbackinfo.setDiImg1(diImg1);
        drawbackinfo.setDiImg2(diImg2);
        drawbackinfo.setDiImg3(diImg3);
        boolean result = indentServiceL.insertDrawBackInfo(drawbackinfo);
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


    //M中关于订单的部分
    //（四）订单管理 6、7、9、10订单列表
    @RequestMapping("/showOrders")
    public BaseResponse showOrders(
            @RequestParam("sId") BigInteger sId,//店铺Id
            @RequestParam("requestFlag") Integer requestFlag)
    {
        JSONArray result = indentServiceL.selectIndent(sId,requestFlag);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(null);
        return jr;
    }


    //（四）订单管理 8、评价订单
    @RequestMapping("/evaluateOrder")
    public BaseResponse evaluateOrder(
            @RequestParam("inId") BigInteger inId,
            @RequestParam("inMtoB") Integer inMtoB)
    {
        Indent indent = new Indent();
        indent.setInId(inId);
        indent.setInMtoB(inMtoB);
        boolean result = indentServiceL.updateIndent(indent);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //11.M-B售后退款详情界面
    @RequestMapping("/showDrawbackDetail")
    public BaseResponse showDrawbackDetail(@RequestParam("inId") BigInteger inId)
    {
        JSONObject result = indentServiceL.selectDrawbackDetail(inId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //12.供货商操作退款
    @RequestMapping("/operateDrawback")
    public BaseResponse operateDrawback(
            @RequestParam("inId") BigInteger inId,
            @RequestParam("diId") BigInteger diId,
            @RequestParam("operation") Integer operation)
    {
        boolean succeed=true;
        //operation为同意退款，退款，减少B的库存，增加M的库存，修改订单退款请求状态
        if(operation==2)
        {
            //todo 退款
            succeed = Db.tx(new IAtom(){
                public boolean run() throws SQLException {
                    //增加M该单品的库存
                    BigInteger productId= Indent.dao.findFirst("select inProduct from Indent where inId=?",inId).getInProduct();
                    Integer drawbackProductNum = Drawbackinfo.dao.findFirst("select diNUmber from drawbackinfo " +
                            "where diId=?",diId).getDiNUmber();
                    Integer addStorage = Db.update("update product set pStorage=pStorage+? where pId=?",productId,drawbackProductNum);
                    //减少B的库存
                    Integer reduceStorage = Db.update("update Indent set inLeftNum=inLeftNum-? where inId=?",inId,drawbackProductNum);
                    return addStorage>0 & reduceStorage>0 ;}
            });
        }
        //operation为拒绝退款，修改订单退款请求状态
        Drawbackinfo drawbackinfo = new Drawbackinfo();
        drawbackinfo.setDiStatus(operation);
        boolean result = indentServiceL.updateDrawbackInfo(drawbackinfo);
        if(result&succeed)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //下游买家订单管理
    //查看下游买家的订单
    @RequestMapping("/showOutOders")
    public BaseResponse showOutOders(
            @RequestParam("sId") BigInteger sId,
            @RequestParam(value = "requestFlag",required = false) Integer requestFlag)
    {
        JSONArray result = indentServiceL.selectOutIndent(sId,requestFlag);
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
    //发货,单发一件
    @RequestMapping("/deliverOne")
    public BaseResponse deliverOne(
            @RequestParam("outId") BigInteger outId,
            @RequestParam("outExpress") String outExpress,
            @RequestParam("outExpressCompany") String outExpressCompany)
    {
        Outindent outindent = new Outindent();
        outindent.setOutId(outId);
        outindent.setOutExpress(outExpress);
        outindent.setOutExpressCompany(outExpressCompany);
        //将外拉订单的状态改为已经发货
        outindent.setOutStatus(2);

        boolean result = indentServiceL.updateOutIndent(outindent);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //发货，操作多件
    @RequestMapping("/deliverManyOne")
    public BaseResponse deliverManyOne(
            @RequestParam("outId") BigInteger[] outId,
            @RequestParam("outExpress") String outExpress,
            @RequestParam("outExpressCompany") String outExpressCompany)
    {

        boolean succeed = Db.tx(new IAtom(){
            public boolean run() throws SQLException {
                Boolean result = true;
                for(BigInteger outindentId:outId)
                {
                    Outindent outindent = new Outindent();
                    outindent.setOutId(outindentId);
                    outindent.setOutExpress(outExpress);
                    outindent.setOutExpressCompany(outExpressCompany);
                    result = (indentServiceL.updateOutIndent(outindent))&result;
                }
                return result;}
        });
        if(succeed)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //10.M-C售后订单操作之浏览待处理退货订单详情
    @RequestMapping("/showWaitReturn")
    public BaseResponse showWaitReturn(
            @RequestParam("outId") BigInteger outId)
    {
        JSONObject result = indentServiceL.selectReturnIndent(outId,false);
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
    //11.M-C售后订单操作之M拒绝-->发通知B，修改申请退货表中的状态
    @RequestMapping("/refuseReturn")
    public BaseResponse refuseReturn(
            @RequestParam("rgId") BigInteger rgId)
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        //设置为M拒绝退货申请
        returngoods.setRgState(3);//修改状态
        boolean result = indentServiceL.updateReturnGoods(returngoods);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //12.M-C售后订单操作之M同意-->发通知B，修改状态和退货模版
    @RequestMapping("/agreeReturn")
    public BaseResponse agreeReturn(
            @RequestParam("rgId") BigInteger rgId,
            @RequestParam("rgRGMId") BigInteger rgRGMId

    )
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        returngoods.setRgRGMId(rgRGMId);//退货模版
        //设置为M同意退货申请
        returngoods.setRgState(4);//修改状态
        boolean result = indentServiceL.updateReturnGoods(returngoods);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //15.15.M-C售后订单之已完成订单详情
    @RequestMapping("/showReciveReturn")
    public BaseResponse showReciveReturn(
            @RequestParam("outId") BigInteger outId)
    {
        JSONObject result = indentServiceL.selectReturnIndent(outId,true);
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
    //14.M-C售后订单之M确认收货，改变ReturnGoods表中退货状态rgState为6-退货退款完成（M确认收货，删除）
    @RequestMapping("/receiveReturn")
    public BaseResponse receiveReturn(
            @RequestParam("rgId") BigInteger rgId)
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        returngoods.setRgState(6);
        boolean result = indentServiceL.updateReturnGoods(returngoods);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //13.M-C售后订单之浏览已同意订单
    @RequestMapping("/showAgreeReturn")
    public BaseResponse showAgreeReturn(
            @RequestParam("outId") BigInteger outId)
    {
        JSONObject result = indentServiceL.selectReturnIndent(outId,true);
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
    //（七）我的收支明细
    @RequestMapping("/myBill")
    public BaseResponse myBill(@RequestParam("sId") BigInteger sId)
    {
        JSONObject result = indentServiceL.selectBill(sId);
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
    //B-M 的订单，B支付（订单中状态改变）商品支付
    @RequestMapping("/payForIndent")
    public BaseResponse payForIndent(@RequestParam("inId") BigInteger inId)
    {
        Indent indent = new Indent();
        indent.setInId(inId);
        indent.setInStatus(1);//将状态改为已支付的状态
        boolean result = indent.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }



}
