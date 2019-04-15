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
@RestController
@RequestMapping("/manufacturer")
public class MContollerL {
    @Autowired
    MServiceL ms;
    @Autowired
    JsonResult jr;

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
