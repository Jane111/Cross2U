package com.cross2u.indent.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.model.Drawbackinfo;
import com.cross2u.indent.model.Indent;
import com.cross2u.indent.model.Outindent;
import com.cross2u.indent.model.Returngoods;
import com.cross2u.indent.util.Constant;
import com.cross2u.indent.util.CreateCInfo;
import com.cross2u.indent.util.TimeUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import javax.management.ObjectName;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

@Service
public class IndentServiceZ {

    @Autowired
    RestTemplate restTemplate;

    public JSONArray showCIndentList(String bId, String outStatus,Integer pageIndex,Integer pageSize) {
        JSONArray array=new JSONArray();
        String sql="SELECT outId,outPlatform,outStore,outStatus,outPrice,outUnit,outAmount," +
                "pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outBusiness=? and outindent.outStatus ";
        if (outStatus.equals("1")) sql=sql+" in (1,2) ";
        else if(outStatus.equals("3"))sql=sql+"= 3 ";
        else if(outStatus.equals("4"))sql=sql+"= 4 ";

        sql=sql+" limit "+pageSize+" offset "+(pageIndex-1)*pageSize+" ";//服务端分页

        List<Record> outindents= Db.find(sql,bId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        for (Record outindent: outindents){
            JSONObject object=new JSONObject();
            object.put("outId",outindent.get("outId"));
            object.put("outPlatform",outindent.get("outPlatform"));
            object.put("outStore",outindent.get("outStore"));
            object.put("outStatus",outindent.get("outStatus"));
            object.put("outPrice",outindent.get("outPrice"));
            object.put("outUnit",outindent.get("outUnit"));
            object.put("outAmount",outindent.get("outAmount"));
            object.put("pId",outindent.get("pId"));
            object.put("wTitle",outindent.get("wTitle"));
            object.put("wMainImage",outindent.get("wMainImage"));
            object.put("sId",outindent.get("sId"));
            object.put("sName",outindent.get("sName"));
            object.put("outCName",outindent.get("outCName"));
            Date outCreateTime=outindent.getDate("outCreateTime");
            String outCreateTimeStr=sdf.format(outCreateTime);
            Date outModifyTime=outindent.getDate("outModifyTime");
            String outModifyTimeStr=sdf.format(outModifyTime);
            object.put("outCreateTime",outCreateTimeStr);
            object.put("outModifyTime",outModifyTimeStr);

           JSONArray proAtr=getProductAtr(outindent.get("pId").toString());
            object.put("pAtr",proAtr);

            array.add(object);
        }
        return array;
    }

    private JSONArray getProductAtr(String pId){
        JSONArray array=new JSONArray();
        String sql="SELECT fName ,foName " +
                " from (productformat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
                " WHERE pfProduct=? ";
        List<Record> records=Db.find(sql,pId);
        for (Record record:records){
            JSONObject object=new JSONObject();
            object.put("fName",record.get("fName"));
            object.put("foName",record.get("foName"));
            array.add(object);
        }
        return array;
    }

    private JSONObject getReTypeNameById(String rgType){
        String sql="select rgrReasons ,rcCatalog from returngoodreasons inner join returncatalog on rgrRCId=rcId where rgrId=?";
        Record record=Db.findFirst(sql,rgType);
        JSONObject object=new JSONObject();
        object.put("rgrReasons",record.get("rgrReasons"));
        object.put("rcCatalog",record.get("rcCatalog"));
        System.out.println("getReTypeNameById"+object);
        return object;
    }

    public JSONArray showCRturnIndent(String bId, String outStatus,Integer pageIndex,Integer pageSize){
        JSONArray outindents=showCIndentList(bId,outStatus,pageIndex,pageSize);
        for (int i=0;i<outindents.size();i++){
            JSONObject outindent=outindents.getJSONObject(i);
            System.out.println(outindent.getBigInteger("outId"));
            JSONObject returnInfo=getReturnInfo(outindent.getBigInteger("outId").toString());
            outindent.put("rcCatalog",returnInfo.get("rcCatalog"));
            outindent.put("rgrReasons",returnInfo.get("rgrReasons"));
            outindent.put("rgState",returnInfo.getInteger("rgState"));
            outindent.put("rgId",returnInfo.get("rgId"));
        }
        return outindents;
    }

    private JSONObject getReturnInfo(String outId){
        String sql="SELECT rgId,rgState,rgType,rgrReasons " +
                " FROM (returncatalog INNER JOIN returngoodreasons on rcId=rgrRCId) INNER JOIN returngoods  on rgType=rgrId " +
                " WHERE rgOOId like '"+outId+"' ORDER BY rgCreateTime DESC ";//最近一次退货信息

        Record record=Db.findFirst(sql);
        JSONObject object=new JSONObject();
        object.put("rgState",record.get("rgState"));
        object.put("rgId",record.get("rgId"));
        JSONObject reasons=getReTypeNameById(record.get("rgType").toString());
        object.put("rgrReasons",reasons.get("rgrReasons"));
        object.put("rcCatalog",reasons.get("rcCatalog"));
        return object;
    }

    public JSONObject showCIndentInfo(String bId, String outStatus,String outId) {
        String sql="SELECT outId,outPlatform,outStore,outStatus,outPrice,outAmount,outUnit,pId,wTitle,wMainImage,sId," +
                "sName,outCName,outCreateTime,outModifyTime,outCPhone,outCAddress,outCInfo,outExpress,outExpressCompany " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outStatus=? and outId=? ORDER BY outCreateTime DESC ";
        Record record= Db.findFirst(sql,outStatus,outId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String outCreateTime=sdf.format(record.getDate("outCreateTime"));
        String outModifyTime=sdf.format(record.getDate("outModifyTime"));
        JSONObject object=new JSONObject();
        object.put("outId",record.get("outId"));
        object.put("outPlatform",record.get("outPlatform"));
        object.put("outStore",record.get("outStore"));
        object.put("outStatus",record.get("outStatus"));
        object.put("outPrice",record.get("outPrice"));
        object.put("outUnit",record.get("outUnit"));
        object.put("outAmount",record.get("outAmount"));
        object.put("pId",record.get("pId"));
        JSONArray pAtr=getProductAtr(record.get("pId").toString());
        object.put("pAtr",pAtr);
        object.put("wTitle",record.get("wTitle"));
        object.put("wMainImage",record.get("wMainImage"));
        object.put("sId",record.get("sId"));
        object.put("sName",record.get("sName"));
        object.put("outCName",record.get("outCName"));
        object.put("outCreateTime",outCreateTime);
        object.put("outModifyTime",outModifyTime);
        object.put("outCAddress",record.get("outCAddress"));
        object.put("outCPhone",record.get("outCPhone"));
        object.put("outCInfo",record.get("outCInfo"));
        object.put("outExpress",record.get("outExpress"));
        object.put("outExpressCompany",record.get("outExpressCompany"));
        return object;

    }

    public JSONObject showCRturnInfo(String bId, String outStatus,String outId){
        JSONObject returnRecord=showCIndentInfo(bId, outStatus,outId);
        JSONObject returnInfo=getReturnInfo(outId);
        returnRecord.putAll(returnInfo);
        JSONObject returnInfo2=showCReturn(outId);
        returnRecord.putAll(returnInfo2);
       // returnRecord.setColumns(returnInfo2);
        return returnRecord;
    }
    public JSONObject showCReturn(String outId){
        String sql="SELECT rgReasons,rgImg1 ,rgImg2,rgImg3,rgRGMId,rgiTrackNumber,rgiTrakTime,rgTrackCompany " +
                " from returngoods " +
                " WHERE returngoods.rgOOId=? ";
        Record record=Db.findFirst(sql,outId);

        JSONObject object=new JSONObject();
        object.put("rgReasons",record.get("rgReasons"));
        object.put("rgImg1",record.get("rgImg1"));
        object.put("rgImg2",record.get("rgImg2"));
        object.put("rgImg3",record.get("rgImg3"));
        object.put("rgRGMId",record.get("rgRGMId"));
        if (record.get("rgRGMId")!=null){
            String mouldSql="select rgmName,rgmAddress,rgmPhone from returngoodmould where rgmId=?";
            Long id=record.getLong("rgRGMId");
            Record mould=Db.findFirst(mouldSql,id);
            object.put("rgmName",mould.get("rgmName"));
            object.put("rgmAddress",mould.get("rgmAddress"));
            object.put("rgmPhone",mould.get("rgmPhone"));
        }
        else {
            object.put("rgmName","");
            object.put("rgmAddress","");
            object.put("rgmPhone","");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        if (record.getDate("rgiTrakTime")!=null){
            Date rgiTrakTime=record.getDate("rgiTrakTime");
            String rgiTrakTimeStr=sdf.format(rgiTrakTime);
            object.put("rgiTrackNumber",record.get("rgiTrackNumber"));
            object.put("rgiTrakTime",rgiTrakTimeStr);
            object.put("rgTrackCompany",record.get("rgTrackCompany"));
        }
        else {
            object.put("rgiTrackNumber","");
            object.put("rgiTrakTime","");
            object.put("rgTrackCompany","");
        }
        return object;
    }

    public JSONArray showInWaitPayList(String bId, String inStatus) {
        JSONArray array=new JSONArray();
        String sql="SELECT inId,inNum,inWare,inStatus,wTitle,pId,pIdentifier,pImage," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=? ";
        List<Record> mIndentsO=Db.find(sql,bId,inStatus);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        for (Record mIndent :mIndentsO){
            JSONObject object=new JSONObject();
            object.put("inId",mIndent.get("inId"));
            object.put("inNum",mIndent.get("inNum"));
            object.put("inWare",mIndent.get("inWare"));
            object.put("inStatus",mIndent.get("inStatus"));
            object.put("wTitle",mIndent.get("wTitle"));
            object.put("pId",mIndent.get("pId"));
            object.put("pIdentifier",mIndent.get("pIdentifier"));
            object.put("pImage",mIndent.get("pImage"));
            object.put("pMoney",mIndent.get("pMoney"));
            object.put("inProductNum",mIndent.get("inProductNum"));
            object.put("inTotalMoney",mIndent.get("inTotalMoney"));
            object.put("inStore",mIndent.get("inStore"));
            object.put("sName",mIndent.get("sName"));
            String inCreateTime=sdf.format(mIndent.getDate("inCreateTime"));
            String inModifyTime=sdf.format(mIndent.getDate("inModifyTime"));
            object.put("inCreateTime",inCreateTime);
            object.put("inModifyTime",inModifyTime);
            JSONArray pAtr=getProductAtr(mIndent.get("pId").toString());
            object.put("pAtr",pAtr);

            array.add(object);
        }
        return array;
    }


    public JSONArray showInCompleteList(String bId,String inStatus) {//已完成订单
        JSONArray array=new JSONArray();
        String sql="SELECT inId,inNum,inWare,inStatus,wTitle,pId,pIdentifier,pImage," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inPayTime,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? ";

        if (inStatus.equals(Constant.IN_B_EVAL)) sql=sql+" and instatus ="+inStatus;//待评价
        else sql=sql+"and indent.inStatus in (3,5,6,7,9)";//已完成 3已完成订单 5B关闭 6M关闭 7商品异常 9M待评价

        List<Record> mIndentsO=Db.find(sql,bId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        for (Record mIndent :mIndentsO){
            JSONObject object=new JSONObject();
            object.put("inId",mIndent.get("inId"));
            object.put("inNum",mIndent.get("inNum"));
            object.put("inWare",mIndent.get("inWare"));
            object.put("inStatus",mIndent.get("inStatus"));
            object.put("wTitle",mIndent.get("wTitle"));
            object.put("pId",mIndent.get("pId"));
            object.put("pIdentifier",mIndent.get("pIdentifier"));
            object.put("pImage",mIndent.get("pImage"));
            object.put("pMoney",mIndent.get("pMoney"));
            object.put("inProductNum",mIndent.get("inProductNum"));
            object.put("inTotalMoney",mIndent.get("inTotalMoney"));
            object.put("inStore",mIndent.get("inStore"));
            object.put("sName",mIndent.get("sName"));
            String inCreateTime=sdf.format(mIndent.getDate("inCreateTime"));
            String inModifyTime=sdf.format(mIndent.getDate("inModifyTime"));

            if (!(mIndent.getDate("inPayTime")==null||mIndent.getDate("inPayTime").equals(""))){
                String inPayTime=sdf.format(mIndent.getDate("inPayTime"));
                object.put("inPayTime",inPayTime);
            }
            object.put("inCreateTime",inCreateTime);
            object.put("inModifyTime",inModifyTime);
            JSONArray pAtr=getProductAtr(mIndent.get("pId").toString());
            object.put("pAtr",pAtr);

            array.add(object);
        }
        return array;
    }

    public JSONArray showInCooperate(String bId, String inStatus) {//合作中list
        JSONArray array=new JSONArray();
        String sql="SELECT inId,inNum,inWare,inStatus,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inPayTime,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=?";
        List<Record> mIndents1=Db.find(sql,bId,inStatus);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        for (Record mIndent :mIndents1){
            JSONObject object=new JSONObject();
            object.put("inId",mIndent.get("inId"));object.put("inNum",mIndent.get("inNum"));
            object.put("inWare",mIndent.get("inWare"));object.put("inStatus",mIndent.get("inStatus"));
            object.put("inLeftNum",mIndent.get("inLeftNum"));object.put("wTitle",mIndent.get("wTitle"));
            object.put("pId",mIndent.get("pId"));object.put("pIdentifier",mIndent.get("pIdentifier"));
            object.put("pImage",mIndent.get("pImage"));object.put("pMoney",mIndent.get("pMoney"));
            object.put("inProductNum",mIndent.get("inProductNum"));object.put("inTotalMoney",mIndent.get("inTotalMoney"));
            object.put("inStore",mIndent.get("inStore"));object.put("sName",mIndent.get("sName"));
            String inCreateTime=sdf.format(mIndent.getDate("inCreateTime"));
            String inModifyTime=sdf.format(mIndent.getDate("inModifyTime"));
            String inPayTime=sdf.format(mIndent.getDate("inPayTime"));
            object.put("inCreateTime",inCreateTime);object.put("inModifyTime",inModifyTime);
            object.put("inPayTime",inPayTime);
            JSONArray pAtr=getProductAtr(mIndent.get("pId").toString());
            object.put("pAtr",pAtr);

            array.add(object);
        }
        return array;
    }

    public JSONArray showInDrawback(String bId, String inStatus) {//申请退款
        JSONArray list2=showInCooperate(bId,inStatus);
        String sql="SELECT diId,diNumber,diMoney,diStatus " +
                "FROM drawbackinfo " +
                " WHERE diInId=? and diStatus !=6 ";//B取消申请
        for (int i=0 ;i<list2.size();i++)
        {
            JSONObject indent=list2.getJSONObject(i);
            Record drawbackInfo=Db.findFirst(sql,indent.get("inId"));
            indent.put("diId",drawbackInfo.get("diId"));
            indent.put("diNumber",drawbackInfo.get("diNumber"));
            indent.put("diMoney",drawbackInfo.get("diMoney"));
            indent.put("diStatus",drawbackInfo.get("diStatus"));
            //list2.add(indent);
        }
        return list2;
    }

    public JSONObject showMReturnIndent(String inId, String diId) {
        String basesql="SELECT inId,inNum, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                " pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=? ";
        Record baserecord=Db.findFirst(basesql,inId);
        JSONArray pAtr=getProductAtr(baserecord.get("pId").toString());
        JSONObject object=new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        object.put("inId",baserecord.get("inId"));object.put("inNum",baserecord.get("inNum"));
        object.put("inStore",baserecord.get("inStore"));object.put("sName",baserecord.get("sName"));
        object.put("inWare",baserecord.get("inWare"));object.put("wTitle",baserecord.get("wTitle"));
        object.put("pId",baserecord.get("pId"));object.put("pIdentifier",baserecord.get("pIdentifier"));
        object.put("pImage",baserecord.get("pImage"));object.put("inLeftNum",baserecord.get("inLeftNum"));
        object.put("pMoney",baserecord.get("pMoney"));object.put("inProductNum",baserecord.get("inProductNum"));
        object.put("inTotalMoney",baserecord.get("inTotalMoney"));
        String inCreateTime=sdf.format(baserecord.getDate("inCreateTime"));
        String inModifyTime=sdf.format(baserecord.getDate("inModifyTime"));
        object.put("inCreateTime",inCreateTime);
        object.put("inModifyTime",inModifyTime);
        object.put("pAtr",pAtr);

        String sql="SELECT diNumber,diReasons,diImg1,diImg2,diImg3,diType,diMoney,diStatus,diAId " +
                " from drawbackinfo " +
                " WHERE diId=? and diStatus!=6";//6 B取消申请
        Record record=Db.findFirst(sql,diId);
       //JSONObject object1=new JSONObject();
        object.put("diNumber",record.get("diNumber"));object.put("diReasons",record.get("diReasons"));
        object.put("diImg1",record.get("diImg1"));object.put("diImg2",record.get("diImg2"));
        object.put("diImg3",record.get("diImg3"));object.put("diMoney",record.get("diMoney"));
        object.put("diStatus",record.get("diStatus"));//退货状态
        //baserecord.set("pAtr",object1);
        BigInteger diType=record.getBigInteger("diType");
        JSONObject diInfo=getDrawbackInfo(diType);
        object.put("drReasons",diInfo.get("drReasons"));
        return object;
    }

    private JSONObject getDrawbackInfo(BigInteger diType) {
        String sql="Select * from drawbackreasons where drId=?";
        JSONObject object=new JSONObject();
        Record record=Db.findFirst(sql,diType);
        object.put("drReasons",record.get("drReasons"));
        return object;
    }

    public JSONObject showMIndentInfo(String inId) {
        String basesql="SELECT inId,inNum,inStore,sName, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum,inMtoB,inBtoM, " +
                " pMoney,inProductNum,inTotalMoney,inPayTime,inCreateTime,inModifyTime,inStatus " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=?";
        Record baserecord=Db.findFirst(basesql,inId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String inCreateTime=sdf.format(baserecord.get("inCreateTime"));
        String inModifyTime=sdf.format(baserecord.get("inModifyTime"));

        JSONObject object=new JSONObject();

        object.put("inId",baserecord.get("inId"));object.put("inNum",baserecord.get("inNum"));
        object.put("inStore",baserecord.get("inStore"));object.put("sName",baserecord.get("sName"));
        object.put("inWare",baserecord.get("inWare"));object.put("wTitle",baserecord.get("wTitle"));
        object.put("pId",baserecord.get("pId"));object.put("pIdentifier",baserecord.get("pIdentifier"));
        object.put("pImage",baserecord.get("pImage"));object.put("inLeftNum",baserecord.get("inLeftNum"));
        object.put("inStatus",baserecord.get("inStatus"));
        if(!(baserecord.getDate("inPayTime")==null||baserecord.getDate("inPayTime").equals("")))
        {
            String inPayTime=sdf.format(baserecord.getDate("inPayTime"));
            object.put("inPayTime",inPayTime);
        }

        if(!(baserecord.get("inMtoB")==null||baserecord.get("inMtoB").equals("")))
        {
            object.put("inMtoB",baserecord.get("inMtoB"));
        }
        else
        {
            object.put("inMtoB","");
        }
        if (!(baserecord.get("inBtoM")==null||baserecord.get("inBtoM").equals("")))
        {
            object.put("inBtoM",baserecord.get("inBtoM"));
        }
        else {
            object.put("inBtoM","");
        }
        object.put("pMoney",baserecord.get("pMoney"));object.put("inProductNum",baserecord.get("inProductNum"));
        object.put("inTotalMoney",baserecord.get("inTotalMoney"));object.put("inCreateTime",inCreateTime);
        object.put("inModifyTime",inModifyTime);

        JSONArray pAtr=getProductAtr(baserecord.get("pId").toString());
        object.put("pAtr",pAtr);
        return  object;
    }

    public Boolean hasINGIndent(String wId) {
        String sql="SELECT count(*) as num " +
                "FROM `indent` " +
                "WHERE inWare=? and inStatus not in (2,3,5,6,7,8,9)";//已完成订单
        Record indentNum=Db.findFirst(sql,wId);
        return indentNum.getInt("num")>0?true:false;//有订单true 没有 false
    }

    public JSONObject drawbackGetInfo(String inId) {
        JSONObject drawbackInfo=new JSONObject();
        String sql="select drId,drReasons  from drawbackreasons ";
        if (isOverDeadLine(inId)){//超过期限
            sql=sql+" where drId !=4";
        }
        List<Record> drawbackTypes=Db.find(sql);
        JSONArray array=new JSONArray();
        for (Record drawbackType:drawbackTypes){
            JSONObject object=new JSONObject();
            object.put("drId",drawbackType.get("drId"));
            object.put("drReasons",drawbackType.get("drReasons"));
            array.add(object);
        }
        String indentsql="select inLeftNum, (inTotalMoney/inProductNum)*inLeftNum as inLeftMoney,(inTotalMoney/inProductNum) as pMoney " +
                " from indent INNER JOIN product on inProduct = product.pId " +
                " WHERE inId=? ";//订单信息 inLeftMoney订单可退金额 inLeft 订单剩余数目
        Record indent=Db.findFirst(indentsql,inId);
        JSONObject indentObject=new JSONObject();
        indentObject.put("inLeftNum",indent.get("inLeftNum"));
        indentObject.put("inLeftMoney",indent.get("inLeftMoney"));
        indentObject.put("pMoney",indent.get("pMoney"));

        drawbackInfo.put("drawbackInfo",array);
        drawbackInfo.put("indent",indentObject);
        return drawbackInfo;
    }
    public boolean isOverDeadLine(String inId)
    {
        String dateSql="SELECT wReplaceDays  " +
                "from ware  INNER JOIN indent on wId=inWare " +
                "where inId=?";
        int date=Db.queryInt(dateSql,inId);//包退换的天数
        System.out.println("包退换的天数：" + date);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Indent indent=Indent.dao.findById(inId);
            String payStr=sdf.format(indent.getInPayTime());
            String nowStr=sdf.format(new Date()); System.out.println("nowStr"+nowStr);
            Date now=sdf.parse(nowStr);
            String lastStr=TimeUtil.dateAddNDay(payStr,date);
            Date lastDay=sdf.parse(lastStr);
            System.out.println("lastDay"+lastDay+"now"+now);

            if (!now.after(lastDay)){
                return false;//在包退期限
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        return true;
    }

    public boolean inDrawback(Drawbackinfo drawbackinfo) {
        Indent indent=Indent.dao.findById(drawbackinfo.getDiInId());
        Integer inStatus=new Integer(Constant.IN_APPLICATION_DRAWBACK);
        indent.setInStatus(inStatus);
        drawbackinfo.setDiStatus(Constant.DRSTATUS_WAIT_M);
        return drawbackinfo.save()&&indent.update();
    }

    public boolean can_cancel(String diId) {
        Drawbackinfo drawbackinfo=Drawbackinfo.dao.findById(diId);
        if (drawbackinfo.getDiStatus().equals(Constant.DRSTATUS_WAIT_M)||drawbackinfo.getDiStatus().equals(Constant.DRSTATUS_WAIT_A)){
            return true;//可以申请退款
        }
        return false;//不可以申请退款
    }

    public boolean cancelDrawback(String diId) {
        Drawbackinfo drawbackinfo=Drawbackinfo.dao.findById(diId);
        BigInteger inId=drawbackinfo.getDiInId();//订单id
        Indent indent=Indent.dao.findById(inId);//订单
        indent.setInStatus(new Integer(Constant.IN_COOPERATION));//合作中
        return indent.update()&&drawbackinfo.delete();
    }

    /**
     * B拒绝C的退款申请
     * @param rgId
     * @return
     */
    public boolean cancelCReturnGood(String rgId) {
        Returngoods returngoods=Returngoods.dao.findById(rgId);
        returngoods.setRgState(2);//B拒绝C的退款申请
        BigInteger outId=returngoods.getRgOOId();//B订单状态
        String sql="update outindent  set outStatus=3 where outId=?";//C的订单变成已完成
        return returngoods.update()&&Db.update(sql,outId)==1;
    }

    /**
     * B同意C的退货申请
     * @param rgId
     * @return
     */
    public boolean agreeCReturnGood(String rgId) {
        Returngoods returngoods=Returngoods.dao.findById(rgId);
        returngoods.setRgState(2);//B拒绝C的退款申请
        BigInteger outId=returngoods.getRgOOId();//B订单状态
        String sql="update outindent  set outStatus=2 where outId=?";//C的订单变成已完成
        return returngoods.update()&&Db.update(sql,outId)==1;
    }



    /*public Float getAvailableMoney(String inId,String diType) {
        Indent indent=Indent.dao.findById(inId);
        Date payTime=indent.getInPayTime();//付款时间
        System.out.println("payTime"+payTime);
        Ware ware=Ware.dao.findById(indent.getInWare());
        Float inTotalMoney=indent.getInTotalMoney();
        if (diType.equals(Constant.DRREASON_B_REASON)){//B自身原因
            return inTotalMoney*0.7f;
        }
        return indent.getInTotalMoney();//返回原价
    }*/

   /* public boolean isNOT_IN_NO_REASON(String inId) {//不在包退期限
        Indent indent=Indent.dao.findById(inId);
        Date payTime=indent.getInPayTime();//付款时间
        Ware ware=Ware.dao.findById(indent.getInWare());
        String payTimeStr= TimeUtil.toOnlyDateString(payTime);//yyyy-MM-dd
        Integer promiseDay=ware.getWReplaceDays();//包退天数
        Date lastDay=new Date(TimeUtil.dateAddNDay(payTimeStr,promiseDay));
        if (!payTime.before(lastDay))//在包退日期之后
        {
            return true;
        }
        return false;//在包退
    }*/

    //定时任务 每周24点 检测是否有满足待评价订单
    /**
     * 秒 分 时 日 月 周几
    */
    @Scheduled(cron = "59 59 23 * * ?")
    public void updateIndentMtoB(){//5天默认好评
        String sql="SELECT inId,inMtoB " +
                "from indent " +
                "WHERE DATE_SUB(CURDATE(), INTERVAL 5 DAY) >= date(inCreateTime) and inMtoB is NULL and inStatus=9 and inBtoM is not null";
        List<Indent> indents= Indent.dao.find(sql);
        for (Indent indent:indents){
            indent.setInMtoB(5);
            indent.setInStatus(3);//已完成
            indent.update();
        }
    }

    //默认对B评价
    @Scheduled(cron = "59 59 23 * * ?")
    public void updateIndentBtoM(){//默认好评
        String sql="SELECT inId,inBtoM " +
                "from indent " +
                "WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) >= date(inCreateTime) and inBtoM is NULL and inStatus =2";
        List<Indent> indents= Indent.dao.find(sql);
        for (Indent indent:indents){
            indent.setInBtoM(5);//默认好评
            indent.setInStatus(9);//M待评价
            indent.update();
        }
    }

    //清除24小时内不付款的订单
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearWaitPayIndent(){
        String sql="SELECT * FROM indent where inStatus=0 and inPayTime is NULL and DATE_SUB(CURDATE(), INTERVAL 1 DAY) >= date(inCreateTime) ";
        List<Indent> list=Indent.dao.find(sql);
        for (Indent indent:list){
            indent.delete();//删除
        }
    }

    //清除违禁商品3-23
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearUnnormalWare(){
        String sql="select * from sensi";
        List<Record> list=Db.find(sql);
        List<Record> wares=Db.find("select wId,wName,wStore from ware");
        String updateSql="update ware set wStatus=4 where wId=?";
        String storeSql="select sScore from store where sId=?";
        String updateStoreSql="update store set sScore=? where sId =?";

        for (Record sensi:list){
            String sensiv=sensi.getStr("senText");
            for (Record ware:wares){
                String name=ware.getStr("wName");
                if (name.contains(sensiv)){
                    Db.update(updateSql,ware.getBigInteger("wId"));
                    updateIndentStatus(ware.getBigInteger("wId"));
                    Integer sScore=Db.queryInt(storeSql,ware.getBigInteger("wStore"));
                    sScore=sScore-10;//扣十分
                    if (sScore<0){
                        sScore=0;
                    }
                    Db.update(updateStoreSql,sScore,ware.getBigInteger("wStore"));
                }
            }
        }
    }

    private void updateIndentStatus(BigInteger wId) {//有违规商品更新indent订单状态 3-23
        String updateProductSql="select inId from indent where inWare=?";
        List<Indent> list=Indent.dao.find(updateProductSql,wId);
        for (Indent indent:list){
            indent.setInStatus(7);
        }
    }

    //创建外拉订单
    @Scheduled(cron = "0 0/30 * * * ?")
    public void createOutIndent(){
        String sql="SELECT * " +
                "from indent " +
                "WHERE inStatus=1";
        List<Indent> indents=Indent.dao.find(sql);
        String getProductSql="select * from product where pId=?";
        for (Indent indent:indents){
            Outindent outindent=new Outindent();
            Integer inLeftNum=indent.getInLeftNum();
            BigInteger inId=indent.getInId();//订单id
            BigInteger pId=indent.getInProduct();//单品id
            BigInteger wId=indent.getInWare();//商品id
            BigInteger sId=indent.getInStore();//店铺id
            BigInteger bId=indent.getInBusiness();//借卖方id

            //确定订单编号
            String str = "out";
            Calendar c = Calendar.getInstance();
            str += c.get(Calendar.YEAR);//四位
            str += String.format("%02d", c.get(Calendar.MONTH));//两位
            str += String.format("%02d", c.get(Calendar.DATE));//两位
            str += String.format("%02d", c.get(Calendar.HOUR));//两位
            str += String.format("%02d", c.get(Calendar.MINUTE));//两位
            str += String.format("%02d", c.get(Calendar.SECOND));//两位
            str += String.format("%02d", c.get(Calendar.SECOND));//两位
            str += String.format("%04d", inId.mod(new BigInteger("10000")));//bId最后四位
            String outNumber=str;
            outindent.setOutNumber(outNumber);//订单编号

            //bId
            outindent.setOutBusiness(bId);
            //sId
            outindent.setOutSId(sId);
            //ware的identifier
            String wareSql="select * from ware where wId=?";
            Record ware=Db.findFirst(wareSql,wId);
            String wIdentifier=ware.getStr("wIdentifier");
            outindent.setOutWIdentifier(wIdentifier);

            //C购买数量
            Integer outAmount=(int)(Math.random()*inLeftNum);
            outindent.setOutAmount(outAmount);

            //订单money 单品identifier 价格单位
            Record product=Db.findFirst(getProductSql,pId);
            Float pMoney=product.getFloat("pMoney");
            String outPIdentifier=product.getStr("pIdentifier");
            outindent.setOutPrice(pMoney*1.2f);
            outindent.setOutPIdentifier(outPIdentifier);
            Integer outUnit=Integer.parseInt(String.valueOf(Math.round(Math.random())+1));//随机生成1或2的数 四舍五入取整
            outindent.setOutUnit(outUnit);

            //平台id
            Integer outPlatform=Integer.parseInt(String.valueOf(Math.round(Math.random())*2+1));//随机生成1或2的数 四舍五入取整
            outindent.setOutPlatform(outPlatform);

            //平台的店铺名称
            String outStore=getStringRandom(4)+"的店";
            outindent.setOutStore(outStore);

            //发货人的信息
            JSONObject outCInfo= CreateCInfo.getAddress();
            String outCPhone=outCInfo.getString("tel");
            String outCName=outCInfo.getString("name");
            String outCAddress=outCInfo.getString("road");
            String cLi="https://cross2u-1258618180.cos.ap-chengdu.myqcloud.com/cInfo/b_license_before.jpg,https://cross2u-1258618180.cos.ap-chengdu.myqcloud.com/cInfo/b_license_down.jpg";
            outindent.setOutCPhone(outCPhone);
            outindent.setOutCName(outCName);
            outindent.setOutCAddress(outCAddress);
            outindent.setOutCInfo(cLi);

            outindent.setOutStatus(1);//未发货的订单

            outindent.save();
        }
    }

    //自动生成名字（中文）
    public static String getRandomJianHan(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

    //生成随机用户名，数字和字母组成,
    public String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    //B关闭
    public boolean cancelMIndent(String inId) {
        Indent indent=Indent.dao.findById(inId);
        indent.setInStatus(5);//状态更新为B关闭
        return indent.update();
    }

    //根据outId找订单
    public Outindent getOutIndentById(String outId) {
        return Outindent.dao.findById(outId);
    }
    //根据sid找mid
    public String getMMNameBySId(BigInteger outSId) {
        String sql="SELECT mmName from mainmanufacturer WHERE mmStore=?";
        Record record=Db.findFirst(sql,outSId);
        return record.get("mmName");
    }
    //根据商品编号找商品信息
    public Record getWareByWIdentifier(String wId) {
        String sql="SELECT wTitle,wMainImage from ware where wIdentifier=?";
        Record ware=Db.findFirst(sql,wId);
        return ware;
    }

    /*public void createContract(String _mId, String _wId, String _indentNum, String _time) throws Exception {
        // 创建一个 web3j 的连接
        Web3j web3j = Web3j.build(new HttpService("http://10.169.102.247:8989/"));//http://10.169.102.247:8989/

        // 部署的时候需要用到该账户的 gas，务必保证该账户余额充足
        Credentials credentials = WalletUtils.loadCredentials(
                "",
                "E:/AtomWorkSpace/testzy/data1/keystore/UTC--2019-03-18T06-38-13.471704100Z--d95dabd97705df625d00d808fa2b427bdf81b5f4");

        // 部署合约
        Smart5_6_sol_Indent compute_sol_compute
                = Smart5_6_sol_Indent.deploy(web3j, credentials, BigInteger.valueOf(1), BigInteger.valueOf(200000)).send();

        String transactionReceipt = compute_sol_compute.CreateIndent(_mId, _wId,_indentNum,_time).send();

        System.out.println("transactionReceipt"+transactionReceipt);

        // 部署完成后打印合约地址
        //System.out.println(compute_sol_compute.getContractAddress());//0x287bb6e3223ed016ff5ccefe68c894f2b06bb8f2
    }

    public void setBlock(String _mId, String _wId, String _indentNum, String _time){


    }

    public void getContractInfo(String address) throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://10.169.102.247:8989/"));
        //String address = "0xa6fd2ebac389773f5bd34d0738bc5fdbd1bea01b";

        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();

        if (ethGetBalance != null) {
        // 打印账户余额
            System.out.println(ethGetBalance.getBalance());
        // 将单位转为以太，方便查看
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));
        }
    }

    public void createBlockChainAddr() throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:8989/"));  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials("123",
                "E:/AtomWorkSpace/testzy/data1/keystore/UTC--2019-03-18T06-50-32.630015500Z--96eed7fc306f562c7b44bfc02376d9fc8eda8bfb");
        Smart5_6_sol_Indent contract = Smart5_6_sol_Indent.deploy(web3j,credentials, GAS_PRICE, BigInteger.valueOf(200000)).send();


    }

    public Smart5_6_sol_Indent getBlockChainAddr(String addr)throws Exception{
        Web3j web3j = Web3j.build(new HttpService("http://localhost:8989/"));  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials("123",
                "E:/AtomWorkSpace/testzy/data1/keystore/UTC--2019-03-18T06-50-32.630015500Z--96eed7fc306f562c7b44bfc02376d9fc8eda8bfb");
        Smart5_6_sol_Indent contract = Smart5_6_sol_Indent.load(
                "0x<address>|<ensName>", web3j,credentials,GAS_PRICE, BigInteger.valueOf(200000));
        return contract;
    }*/



}
