package com.cross2u.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.store.model.*;
import com.cross2u.store.service.StoreServiceL;
import com.cross2u.store.util.BaseResponse;
import com.cross2u.store.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
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
    public BaseResponse findStoreDetail(@PathVariable("sId") BigInteger sId)
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
    @RequestMapping("/findStoreDetailCoop")
    public BaseResponse findStoreDetailCoop(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("bId") BigInteger bId)
    {
        JSONObject result = storeServiceL.selectStoreDetailCoop(sId,bId);
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
    //M-(九)2、显示品牌商的店铺情况(信用等级，信用分数，店铺名称）
    @RequestMapping("/showMStore")
    public BaseResponse showMStore(@RequestParam("sId") BigInteger sId)
    {
        Store store = Store.dao.findById(sId);
        JSONObject result = new JSONObject();
        result.put("sName",store.getSName());
        result.put("sScore",store.getSScore());
        //得到店铺最后的保证金,根据storeBill中的内容进行计算
        List<Storebill> storebillList = Storebill.dao.find("select sbMoney,sbBalance " +
                "from storebill where sbSId=?",sId);
        Float totalMoney = 0f;
        Float payMoney = 0f;
        for(Storebill storebill:storebillList)
        {
            if(storebill.getSbBalance()==1)
            {
                totalMoney+=storebill.getSbMoney();
                payMoney+=storebill.getSbMoney();
            }else
            {
                totalMoney-=storebill.getSbMoney();
            }
        }
        result.put("totalMoney",totalMoney);
        result.put("payMoney",payMoney);
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
        jr.setData(result);
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
        returngoodmould.setRgmIsDeleted(0);
        boolean result = storeServiceL.insertReturnGoodMould(returngoodmould);
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
    //18.M删除退货地址模板
    @RequestMapping("/deleteReturnMoulds")
    public BaseResponse deleteReturnMoulds(@RequestParam("rgmId") BigInteger rgmId)
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgmId(rgmId);
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
        jr.setData(null);
        return jr;
    }
    //19.M修改退货地址模板
    @RequestMapping("/changeReturnMould")
    public BaseResponse changeReturnMould(
            @RequestParam("rgmId") BigInteger rgmId,
            @RequestParam(value = "rgmName",required = false) String rgmName,
            @RequestParam(value = "rgmPhone",required = false) String rgmPhone,
            @RequestParam(value = "rgmAddress",required = false) String rgmAddress
    )
    {
        Returngoodmould returngoodmould = new Returngoodmould();
        returngoodmould.setRgmId(rgmId);
        if(rgmName!=null)
        {
            returngoodmould.setRgmName(rgmName);
        }
        if(rgmPhone!=null)
        {
            returngoodmould.setRgmPhone(rgmPhone);
        }
        if(rgmAddress!=null)
        {
            returngoodmould.setRgmAddress(rgmAddress);
        }
        boolean result = storeServiceL.updateReturnGoodMould(returngoodmould);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        jr.setData(null);
        return jr;
    }

    //子账号管理
    //1、显示子账号列表
    @RequestMapping("/showSubAccounts")
    public BaseResponse showSubAccounts(@RequestParam("sId") BigInteger sId)
    {
        List<Manufacturer> result = storeServiceL.selectMSubAccounts(sId);
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
    //2、显示子账号详情
    @RequestMapping("/showSubAccount")
    public BaseResponse showSubAccount(@RequestParam("mId") BigInteger mId)
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
        jr.setData(result);
        return jr;
    }
    //3、编辑子账号，6、停用/启用子账号
    @RequestMapping("/editSubAccount")
    public BaseResponse editSubAccount(
            @RequestParam("mId") BigInteger mId,
            @RequestParam(value = "mPhone",required = false) String mPhone,
            @RequestParam(value = "mName",required = false) String mName,
            @RequestParam(value = "mPassword",required = false) String mPassword,
            @RequestParam(value = "mManageWare",required = false) Integer mManageWare,
            @RequestParam(value = "mManageIndent",required = false) Integer mManageIndent,
            @RequestParam(value = "mManageMessage",required = false) Integer mManageMessage,
            @RequestParam(value = "mManageClient",required = false) Integer mManageClient,
            @RequestParam(value = "mStatus",required = false) Integer mStatus
    )
    {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMId(mId);
        if(mPhone!=null)
        {
            manufacturer.setMPhone(mPhone);
        }
        if(mName!=null)
        {
            manufacturer.setMName(mName);
        }
        if(mPassword!=null)
        {
            manufacturer.setMPassword(mPassword);
        }
        if(mManageWare!=null)
        {
            manufacturer.setMManageWare(mManageWare);
        }
        if(mManageIndent!=null)
        {
            manufacturer.setMManageIndent(mManageIndent);
        }
        if(mManageMessage!=null)
        {
            manufacturer.setMManageMessage(mManageMessage);
        }
        if(mManageClient!=null)
        {
            manufacturer.setMManageClient(mManageClient);
        }
        if(mStatus!=null)
        {
            manufacturer.setMStatus(mStatus);
        }
        boolean result = storeServiceL.updateMSubAccount(manufacturer);
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
    //5、新建子账号
    @RequestMapping("/addSubAccount")
    public BaseResponse addSubAccount(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("mPhone") String mPhone,
            @RequestParam("mName") String mName,
            @RequestParam("mPassword") String mPassword,
            @RequestParam("mManageWare") Integer mManageWare,
            @RequestParam("mManageIndent") Integer mManageIndent,
            @RequestParam("mManageMessage") Integer mManageMessage,
            @RequestParam("mManageClient") Integer mManageClient
    )
    {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMStore(sId);
        manufacturer.setMPhone(mPhone);
        manufacturer.setMName(mName);
        manufacturer.setMPassword(mPassword);
        manufacturer.setMManageWare(mManageWare);
        manufacturer.setMManageIndent(mManageIndent);
        manufacturer.setMManageMessage(mManageMessage);
        manufacturer.setMManageClient(mManageClient);
        manufacturer.setMRank(1);//设置为子账号
        manufacturer.setMStatus(1);//设置子账号的状态为在用
        boolean result = storeServiceL.insertMSubAccount(manufacturer);
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
    //7、删除子账号
    @RequestMapping("/deleteSubAccount")
    public BaseResponse deleteSubAccount(
            @RequestParam("mId") BigInteger mId
    )
    {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMId(mId);
        boolean result = manufacturer.delete();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //8、筛选子账号
    @RequestMapping("/pickSubAccounts")
    public BaseResponse pickSubAccounts(
            @RequestParam("sId") BigInteger sId,
            @RequestParam(value = "mManageWare",required = false) Integer mManageWare,
            @RequestParam(value = "mManageIndent",required = false) Integer mManageIndent,
            @RequestParam(value = "mManageMessage",required = false) Integer mManageMessage,
            @RequestParam(value = "mManageClient",required = false) Integer mManageClient)
    {
        String keyPointString ="";
        Integer keyPointInt = 0;
        if(mManageWare!=0)
        {
            keyPointString = "mManageWare";
            keyPointInt = mManageWare;
        }
        if(mManageIndent!=0)
        {
            keyPointString = "mManageIndent";
            keyPointInt = mManageIndent;
        }
        if(mManageMessage!=0)
        {
            keyPointString = "mManageMessage";
            keyPointInt = mManageMessage;
        }
        if(mManageClient!=0)
        {
            keyPointString = "mManageClient";
            keyPointInt = mManageClient;
        }
        List<Manufacturer> result = storeServiceL.pickMSubAccounts(sId,keyPointString,keyPointInt);
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
        jr.setData(result);
        return jr;
    }
    //修改设置情况
    @RequestMapping("/updateSets")
    public BaseResponse updateSets(
            @RequestParam("sId") BigInteger sId,
            @RequestParam(value = "setDirectMoney",required = false) Integer setDirectMoney,
            @RequestParam(value = "setReduceInventory",required = false) Integer setReduceInventory,
            @RequestParam(value = "setAgentDegree",required = false) Integer setAgentDegree
    )
    {
        Store store = new Store();
        store.setSId(sId);
        if(setDirectMoney!=null)
        {
            store.setSDirectMoney(setDirectMoney);
        }
        if(setReduceInventory!=null)
        {
            store.setSReduceInventory(setReduceInventory);
        }
        if(setAgentDegree!=null)
        {
            store.setSAgentDegree(setAgentDegree);
        }
        boolean result = storeServiceL.updateSet(store);
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
    //修改交易资金担保的设置
    @RequestMapping("/setGuarantee")
    public BaseResponse setGuarantee(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("select") Float select
    )
    {
        Storebill storeBill = new Storebill();
        storeBill.setSbSId(sId);//店铺Id
        storeBill.setSbMoney(select);//选择
        //金额
        boolean result = storeServiceL.insertStoreBill(storeBill);
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
    //50、申请代理
    @RequestMapping("/addCooperation")
    public BaseResponse addCooperation(
            @RequestParam("copBId") BigInteger copBId,
            @RequestParam("copSId") BigInteger copSId
    )
    {
        Cooperation cooperation = new Cooperation();
        cooperation.setCopBId(copBId);
        cooperation.setCopSId(copSId);
        cooperation.setCopState(0);//设置代理状态为正在审核，表示申请代理
        boolean result = cooperation.save();
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
    //A-21、显示举报店铺
    @RequestMapping("/showBadStore")
    public BaseResponse showBadStore(
            @RequestParam("amiType") BigInteger amiType,
            @RequestParam("amiResult") Integer amiResult
    )
    {
        JSONArray result = storeServiceL.selectAbnormalM(amiType,amiResult);
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
    //A-22、审核举报店铺
    @RequestMapping("/checkBadStore")
    public BaseResponse checkBadStore(
            @RequestParam("amiId") BigInteger amiId,
            @RequestParam("amiAId") BigInteger amiAId,
            @RequestParam("amiResult") Integer amiResult
    )
    {
        Abnormalminfo abNormalM = new Abnormalminfo();
        abNormalM.setAmiId(amiId);
        abNormalM.setAmiAId(amiAId);
        abNormalM.setAmiResult(amiResult);
        boolean result = abNormalM.update();
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
    //b-61、举报店铺
    @RequestMapping("/addAbnormalM")
    public BaseResponse addAbnormalM(
            @RequestParam("amiReporter") BigInteger amiReporter,
            @RequestParam("amiSId") BigInteger amiSId,
            @RequestParam("amiType") BigInteger amiType,
            @RequestParam("amiReasons") String amiReasons,
            @RequestParam("amiImg") String amiImg
    )
    {
        Abnormalminfo abNormalM = new Abnormalminfo();
        abNormalM.setAmiReporter(amiReporter);//举报者Id
        abNormalM.setAmiSId(amiSId);//举报店铺Id
        abNormalM.setAmiType(amiType);//举报类型
        abNormalM.setAmiReasons(amiReasons);//举报原因
        abNormalM.setAmiImg(amiImg);//举报凭证
        boolean result = abNormalM.save();
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
