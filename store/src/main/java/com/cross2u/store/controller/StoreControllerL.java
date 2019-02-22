package com.cross2u.store.controller;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.store.model.Manufacturer;
import com.cross2u.store.model.Returngoodmould;
import com.cross2u.store.model.Store;
import com.cross2u.store.model.Storebill;
import com.cross2u.store.service.StoreServiceL;
import com.cross2u.store.util.BaseResponse;
import com.cross2u.store.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreControllerL {
    @Autowired
    StoreServiceL storeServiceL;
    @Autowired
    BaseResponse jr;
    /*
    *面向其他模块的controller
    * */
    //1、得到店铺退货地址模版
    @RequestMapping("/findReturngoodmould/{rgmId}")
    public BaseResponse findReturngoodmould(
            @PathVariable("rgmId") BigInteger rgmId)
    {
        Returngoodmould result = storeServiceL.selectReturngoodmould(rgmId);
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
    //2、得到店铺的基本信息
    @RequestMapping("/findStoreDetail/{sId}")
    public BaseResponse findStore(
            @PathVariable("sId") BigInteger sId)
    {
        JSONObject result = storeServiceL.selectStoreDetail(sId);
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
    //3、得到storebill
    @RequestMapping("/findStorebill/{sId}")
    public BaseResponse findStorebill(
            @PathVariable("sId") BigInteger sId)
    {
        List<Storebill> result = storeServiceL.selectStorebill(sId);
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
    //16.M查看退货模板列表
    @RequestMapping("/showReturnMoulds")
    public BaseResponse showReturnMoulds(@RequestParam("sId") BigInteger sId)
    {
        List<Returngoodmould> result = storeServiceL.selectReturnGoodMould(sId);
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
    @RequestMapping("/addReturnMould")
    public BaseResponse addReturnMould(
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
        boolean result = storeServiceL.insertReturnGoodMould(returngoodmould);
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
    @RequestMapping("/deleteReturnMoulds")
    public BaseResponse deleteReturnMoulds(@RequestParam("rgmId") BigInteger rgmId)
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgmIsDeleted(1);//设置是否删除字段为删除
        boolean result = storeServiceL.updateReturnGoodMould(returngoodmould);
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
    @RequestMapping("/changeReturnMould")
    public BaseResponse changeReturnMould(
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
        boolean result = storeServiceL.updateReturnGoodMould(returngoodmould);
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
    @RequestMapping("/showSubAccounts")
    public BaseResponse showSubAccounts(@RequestParam("mmId") BigInteger mmId)
    {
        List<Manufacturer> result = storeServiceL.selectMSubAccounts(mmId);
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
    @RequestMapping("/showSubAccount")
    public BaseResponse showSubAccount(@RequestParam("mmId") BigInteger mId)
    {
        Manufacturer result = storeServiceL.selectMSubAccount(mId);
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
    @RequestMapping("/editSubAccount")
    public BaseResponse editSubAccount(
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
        boolean result = storeServiceL.updateMSubAccount(manufacturer);
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
    @PostMapping("/addSubAccount")
    public BaseResponse addSubAccount(
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
        boolean result = storeServiceL.insertMSubAccount(manufacturer);
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
    @RequestMapping("/showSets")
    public BaseResponse showSets(@RequestParam("sId") BigInteger sId)
    {
        JSONObject result = storeServiceL.selectSet(sId);
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
    @RequestMapping("/updateSets")
    public BaseResponse updateSets(
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
        boolean result = storeServiceL.updateSet(store);
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
    @RequestMapping("/setGuarantee")
    public BaseResponse setGuarantee(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("select") Integer select
    )
    {
        Storebill storeBill = new Storebill();
        storeBill.setSbSId(sId);
        storeBill.setSbMoney(select);
        boolean result = storeServiceL.updateStoreBill(storeBill);
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
}
