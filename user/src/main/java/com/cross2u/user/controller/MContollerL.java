package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.cross2u.user.service.MServiceL;
import java.math.BigInteger;
import com.cross2u.user.util.JsonResult;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
@RestController
@RequestMapping("/manufacturer")
public class MContollerL {
    @Autowired
    MServiceL ms;
    JsonResult jr;

    //（四）订单管理 6、7、9、10订单列表
    @RequestMapping("/order/showOrders")
    public JsonResult showOrders(
            @RequestParam("sId") BigInteger sId,//店铺Id
            @RequestParam("requestFlag") Integer requestFlag)
    {
        JSONArray result = ms.selectIndent(sId,requestFlag);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }


    //（四）订单管理 8、评价订单
    @RequestMapping("/order/evaluateOrder")
    public JsonResult evaluateOrder(
            @RequestParam("inId") BigInteger inId,
            @RequestParam("inMtoB") Integer inMtoB)
    {
        Indent indent = new Indent();
        indent.setInId(inId);
        indent.setInMtoB(inMtoB);
        boolean result = ms.updateIndent(indent);
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
    //11.M-B售后退款详情界面
    @RequestMapping("/order/showDrawbackDetail")
    public JsonResult showDrawbackDetail(@RequestParam("inId") BigInteger inId)
    {
        JSONObject result = ms.selectDrawbackDetail(inId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //12.供货商操作退款
    @RequestMapping("/order/operateDrawback")
    public JsonResult operateDrawback(
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
        boolean result = ms.updateDrawbackInfo(drawbackinfo);
        if(result&succeed)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        return jr;
    }
    //下游买家订单管理
    //查看下游买家的订单
    @RequestMapping("/order/showOutOders")
    public JsonResult showOutOders(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("requestFlag") Integer requestFlag)
    {
        JSONArray result = ms.selectOutIndent(sId,requestFlag);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //发货,单发一件
    @RequestMapping("/order/deliverOne")
    public JsonResult deliverOne(
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

        boolean result = ms.updateOutIndent(outindent);
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
    //发货，操作多件
    @RequestMapping("/order/deliverMany")
    public JsonResult deliverMany(
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
                    result = (ms.updateOutIndent(outindent))&result;
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
        return jr;
    }
    //10.M-C售后订单操作之浏览待处理退货订单详情
    @RequestMapping("/order/showWaitReturn")
    public JsonResult showWaitReturn(
            @RequestParam("outId") BigInteger outId)
    {
        JSONObject result = ms.selectReturnIndent(outId,false);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //11.M-C售后订单操作之M拒绝-->发通知B，修改申请退货表中的状态
    @RequestMapping("/order/refuseReturn")
    public JsonResult refuseReturn(
            @RequestParam("rgId") BigInteger rgId)
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        //设置为M拒绝退货申请
        returngoods.setRgState(3);//修改状态
        boolean result = ms.updateReturnGoods(returngoods);
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
    //12.M-C售后订单操作之M同意-->发通知B，修改状态和退货模版
    @RequestMapping("/order/agreeReturn")
    public JsonResult agreeReturn(
            @RequestParam("rgId") BigInteger rgId,
            @RequestParam("rgRGMId") BigInteger rgRGMId

            )
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        returngoods.setRgMId(rgRGMId);//退货模版
        //设置为M同意退货申请
        returngoods.setRgState(4);//修改状态
        boolean result = ms.updateReturnGoods(returngoods);
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
    //13.M-C售后订单之浏览已同意订单
    @RequestMapping("/order/showAgreeReturn")
    public JsonResult showAgreeReturn(
            @RequestParam("outId") BigInteger outId)
    {
        JSONObject result = ms.selectReturnIndent(outId,true);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //14.M-C售后订单之M确认收货，改变ReturnGoods表中退货状态rgState为6-退货退款完成（M确认收货，删除）
    @RequestMapping("/order/receiveReturn")
    public JsonResult receiveReturn(
            @RequestParam("rgId") BigInteger rgId)
    {
        Returngoods returngoods = new Returngoods();
        returngoods.setRgId(rgId);//申请退货的Id
        returngoods.setRgState(6);
        Boolean result = ms.updateReturnGoods(returngoods);
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
    //16.M查看退货模板列表
    @RequestMapping("/order/showReturnMoulds")
    public JsonResult showReturnMoulds(@RequestParam("sId") BigInteger sId)
    {
        List<Returngoodmould> result = ms.selectReturnGoodMould(sId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //17.M增加退货地址模板
    @RequestMapping("/order/addReturnMould")
    public JsonResult addReturnMould(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("rgmName") String rgmName,
            @RequestParam("rgmPhone") String rgmPhone,
            @RequestParam("rgmAddress") String rgmAddress
            )
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgSId(sId);
        returngoodmould.setRgmName(rgmName);
        returngoodmould.setRgmPhone(rgmPhone);
        returngoodmould.setRgmAddress(rgmAddress);
        boolean result = ms.insertReturnGoodMould(returngoodmould);
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
    //18.M删除退货地址模板
    @RequestMapping("/order/deleteReturnMoulds")
    public JsonResult deleteReturnMoulds(@RequestParam("rgmId") BigInteger rgmId)
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgmIsDeleted(1);//设置是否删除字段为删除
        boolean result = ms.updateReturnGoodMould(returngoodmould);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        return jr;
    }
    //19.M修改退货地址模板
    @RequestMapping("/order/changeReturnMould")
    public JsonResult changeReturnMould(
            @RequestParam("rgmId") BigInteger rgmId,
            @RequestParam("rgmName") String rgmName,
            @RequestParam("rgmPhone") String rgmPhone,
            @RequestParam("rgmAddress") String rgmAddress
            )
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgmId(rgmId);
        returngoodmould.setRgmPhone(rgmPhone);
        returngoodmould.setRgmName(rgmName);
        returngoodmould.setRgmAddress(rgmAddress);
        boolean result = ms.updateReturnGoodMould(returngoodmould);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        return jr;
    }
    //子账号管理
    //1、显示子账号列表
    @RequestMapping("/backstage/showSubAccounts")
    public JsonResult showSubAccounts(@RequestParam("mmId") BigInteger mmId)
    {
        List<Manufacturer> result = ms.selectMSubAccounts(mmId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //2、显示子账号详情
    @RequestMapping("/backstage/showSubAccount")
    public JsonResult showSubAccount(@RequestParam("mmId") BigInteger mId)
    {
        Manufacturer result = ms.selectMSubAccount(mId);
        if(result!=null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //3、编辑子账号，6、停用/启用子账号
    @RequestMapping("/backstage/editSubAccount")
    public JsonResult editSubAccount(
        @RequestParam("mId") BigInteger mId,
        @RequestParam("mPhone") String mPhone,
        @RequestParam("mName") String mName,
        @RequestParam("mPassword") String mPassword,
        @RequestParam("mManageWare") Integer mManageWare,
        @RequestParam("mManageIndent") Integer mManageIndent,
        @RequestParam("mManageMessage") Integer mManageMessage,
        @RequestParam("mManageClient") Integer mManageClient,
        @RequestParam("mStatus") Integer mStatus
    )
    {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMId(mId);
        manufacturer.setMPhone(mPhone);
        manufacturer.setMName(mName);
        manufacturer.setMPassword(mPassword);
        manufacturer.setMManageWare(mManageWare);
        manufacturer.setMManageIndent(mManageIndent);
        manufacturer.setMManageMessage(mManageMessage);
        manufacturer.setMManageClient(mManageClient);
        manufacturer.setMStatus(mStatus);
        boolean result = ms.updateMSubAccount(manufacturer);
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
    //5、新建子账号
    @PostMapping("/backstage/addSubAccount")
    public JsonResult addSubAccount(
        @RequestParam("mPhone") String mPhone,
        @RequestParam("mName") String mName,
        @RequestParam("mPassword") String mPassword,
        @RequestParam("mManageWare") Integer mManageWare,
        @RequestParam("mManageIndent") Integer mManageIndent,
        @RequestParam("mManageMessage") Integer mManageMessage,
        @RequestParam("mManageClient") Integer mManageClient,
        @RequestParam("mStatus") Integer mStatus
    )
    {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMPhone(mPhone);
        manufacturer.setMName(mName);
        manufacturer.setMPassword(mPassword);
        manufacturer.setMManageWare(mManageWare);
        manufacturer.setMManageIndent(mManageIndent);
        manufacturer.setMManageMessage(mManageMessage);
        manufacturer.setMManageClient(mManageClient);
        manufacturer.setMStatus(mStatus);
        boolean result = ms.insertMSubAccount(manufacturer);
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
    //（七）我的收支明细
    @RequestMapping("/backstage/myBill")
    public JsonResult myBill(@RequestParam("sId") BigInteger sId)
    {
        JSONObject result = ms.selectBill(sId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }

    //设置
    //1、显示设置情况
    @RequestMapping("/manufacture/showSets")
    public JsonResult showSets(@RequestParam("sId") BigInteger sId)
    {
        JSONObject result = ms.selectSet(sId);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        return jr;
    }
    //修改设置情况
    @RequestMapping("/manufacture/updateSets")
    public JsonResult updateSets(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("setDirectMoney") Integer setDirectMoney,
            @RequestParam("setReduceInventory") Integer setReduceInventory,
            @RequestParam("setAgentDegree") Integer setAgentDegree
            )
    {
        Store store = new Store();
        store.setSId(sId);
        store.setSDirectMoney(setDirectMoney);
        store.setSReduceInventory(setReduceInventory);
        store.setSAgentDegree(setAgentDegree);
        boolean result = ms.updateSet(store);
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
    //修改交易资金担保的设置
    @RequestMapping("/manufacture/setGuarantee")
    public JsonResult setGuarantee(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("select") Integer select
    )
    {
        Storebill storeBill = new Storebill();
        storeBill.setSbSId(sId);
        storeBill.setSbMoney(select);
        boolean result = ms.updateStoreBill(storeBill);
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
    //个人中心
    //显示M的基本信息
    @RequestMapping("/manufacture/showMyself")
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
        return jr;
    }


}
