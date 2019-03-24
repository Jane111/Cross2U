package com.cross2u.store.service;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.store.model.*;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class StoreServiceL {
    /*
    *面向其他模块的service,provider
    * */
    //1、根据Id，找Returngoodmould
    public Returngoodmould selectReturngoodmould(BigInteger rgmId)
    {
        return Returngoodmould.dao.findById(rgmId);
    }
    //2、根据Id，得到代理的相关信息
    public JSONObject selectStoreDetailCoop(BigInteger sId,BigInteger bId)
    {
        JSONObject storeDetail = new JSONObject();
        Store store = Store.dao.findById(sId);
        storeDetail.put("sName",store.getSName());//店铺名称
        storeDetail.put("sScore",store.getSScore());//店铺评分
        storeDetail.put("sMmId",store.getSMmId());//店铺对应的mmId
        //用户代理该店铺的状态
        Cooperation cooperation = Cooperation.dao.findFirst("select copState from cooperation " +
                "where copBId=? AND copSId=?",bId,sId);
        Integer isCoop=0;
        if(cooperation!=null)
        {
            isCoop = cooperation.getCopState();
            if(isCoop==1)
            {
                storeDetail.put("isCoop",1);//该用户代理了该店铺
            }
        }
        storeDetail.put("isCoop",isCoop);//该用户代理该店铺
        return storeDetail;
    }
    public JSONObject selectStoreDetail(BigInteger sId)
    {
        JSONObject storeDetail = new JSONObject();
        Store store = Store.dao.findById(sId);
        storeDetail.put("sName",store.getSName());//店铺名称
        storeDetail.put("sScore",store.getSScore());//店铺评分
        storeDetail.put("sMmId",store.getSMmId());//店铺对应的mmId
        return storeDetail;
    }
    //3、得到storebill
    public List<Storebill> selectStorebill(BigInteger sId)
    {
        return Storebill.dao.find("select sbId,sbNumber,sbInfo,sbMoney,sbTime from storebill where sbSId=?",sId);
    }

    /*
    * 面向前端页面
    * */
    //16.M查看退货模板列表
    public List<Returngoodmould> selectReturnGoodMould(BigInteger sId)
    {
        return Returngoodmould.dao.find("select rgmId,rgmName,rgmPhone,rgmAddress " +
                "from returngoodmould where rgSId=? and rgmIsDeleted!=1",sId);
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
    public List<Manufacturer> selectMSubAccounts(BigInteger sId)
    {
        return Manufacturer.dao.find("select mId,mStatus,mPassword,mPhone,mName,mManageWare,mManageIndent,mManageMessage,mManageClient " +
                "from manufacturer where mStore=? AND mRank=?",sId,1);
    }
    //筛选子账号
    public List<Manufacturer> pickMSubAccounts(BigInteger sId,String keyPointString,Integer keyPointInt)
    {
        return Manufacturer.dao.find("select mId,mStatus,mPhone,mName,mManageWare,mManageIndent,mManageMessage,mManageClient " +
                "from manufacturer where mStore=? AND mRank=? AND "+keyPointString+"=?",sId,1,keyPointInt);
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
    //新增交易资金担保的设置
    public boolean insertStoreBill(Storebill storeBill)
    {
        return storeBill.save();
    }

    //A-18、显示举报商品
    public JSONArray selectAbnormalM(BigInteger amiType, Integer amiResult)
    {
        JSONArray badStoreList = new JSONArray();
        List<Abnormalminfo> abList;
        if(amiType.equals(new BigInteger("0")))
        {
            abList = Abnormalminfo.dao.find("select * from abnormalminfo " +
                    "where amiResult=?",amiResult);
        }
        else
        {
            abList = Abnormalminfo.dao.find("select * from abnormalminfo " +
                    "where amiType=? and amiResult=?",amiType,amiResult);
        }
        for(Abnormalminfo ab:abList)
        {
            JSONObject badStore = new JSONObject();
            badStore.put("amiId",ab.getAmiId());//异常店家信息ID
            Store store = Store.dao.findById(ab.getAmiSId());
            badStore.put("amiSName",store.getSName());//amiMId
            badStore.put("amiType",ab.getAmiType());//举报类型
            badStore.put("amiReasons",ab.getAmiReasons());//举报原因
            badStore.put("amiImg",ab.getAmiImg());//举报凭证
            badStoreList.add(badStore);
        }
        return badStoreList;
    }

}
