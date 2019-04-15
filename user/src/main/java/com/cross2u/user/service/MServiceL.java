package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Service
public class MServiceL {


    //16.M查看退货模板列表
    public List<Returngoodmould> selectReturnGoodMould(BigInteger sId)
    {
        return Returngoodmould.dao.find("select rgmId,rgmName,rgmPhone,rgmAddress " +
                "from Returngoodmould where rgSId=?",sId);

    }
    //17.M增加退货地址模板
    public boolean insertReturnGoodMould(Returngoodmould returngoodmould)
    {
        return returngoodmould.save();
    }
    //18.M删除（改变状态字段），修改退货地址模板
    public boolean updateReturnGoodMould(Returngoodmould returngoodmould)
    {
        return returngoodmould.update();
    }

    //展示子账号列表
    public List<Manufacturer> selectMSubAccounts(BigInteger mmId)
    {
        return Manufacturer.dao.find("select mId,mStatus,mPhone,mName,mManageWare,mManageIndent,mManageMessage,mManageClient " +
                "from Manufacturer where mMainManu=? AND mRank=?",mmId,1);
    }

    //展示子账号详情
    public Manufacturer selectMSubAccount(BigInteger mId)
    {
        return Manufacturer.dao.findById(mId);
    }
    //编辑子账号
    public boolean updateMSubAccount(Manufacturer manufacturer)
    {
        return manufacturer.update();
    }
    //新建子账号
    public boolean insertMSubAccount(Manufacturer manufacturer)
    {
        return manufacturer.save();
    }

    //1、显示设置情况
    public JSONObject selectSet(BigInteger sId)
    {
        JSONObject setDetail = new JSONObject();
        Store store = Store.dao.findFirst("select sDirectMoney,sReduceInventory,sAgentDegree " +
                "from store where sId=?",sId);
        setDetail.put("sDirectMoney",store.getSDirectMoney());//直接到账 0-未开通，1-开通
        setDetail.put("sReduceInventory",store.getSReduceInventory());//减库存方式 0-拍下减，1-付款减
        setDetail.put("sAgentDegree",store.getSAgentDegree());//代理人级别 1~5
        Storebill storebill = Storebill.dao.findFirst("select sbInfo from storebill where sbSId=?",sId);
        setDetail.put("sbInfo",storebill.getSbInfo());//交易资金担保1-缴纳开店保证金（默认有7天包退换），2-缴纳14天包退换，3-缴纳21天，4-缴纳60天
        return setDetail;
    }
    //修改设置情况
    public boolean updateSet(Store store)
    {
        return store.update();
    }
    //修改交易资金担保的设置
    public boolean updateStoreBill(Storebill storeBill)
    {
        return storeBill.update();
    }
    //显示M的基本信息
    public Mainmanufacturer selectMDetail(BigInteger sId)
    {
        return Mainmanufacturer.dao.findFirst("select * from Mainmanufacturer where mmStore=?",sId);
    }







}
