package com.cross2u.indent.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.model.Drawbackinfo;
import com.cross2u.indent.model.Indent;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.ObjectName;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class IndentServiceZ {

    @Autowired
    RestTemplate restTemplate;

    public JSONArray showCIndentList(String bId, String outStatus) {
        JSONArray array=new JSONArray();
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outBusiness=? and outindent.outStatus=? ORDER BY outCreateTime DESC ";
        List<Record> outindents= Db.find(sql,bId,outStatus);
        for (Record outindent: outindents){
            JSONObject object=new JSONObject();
            object.put("outId",outindent.get("outId"));
            object.put("outPlatform",outindent.get("outPlatform"));
            object.put("outStore",outindent.get("outStore"));
            object.put("pId",outindent.get("pId"));
            object.put("wTitle",outindent.get("wTitle"));
            object.put("wMainImage",outindent.get("wMainImage"));
            object.put("sId",outindent.get("sId"));
            object.put("sName",outindent.get("sName"));
            object.put("outCName",outindent.get("outCName"));
            object.put("outCreateTime",outindent.get("outCreateTime"));
            object.put("outModifyTime",outindent.get("outModifyTime"));

           JSONArray proAtr=getProductAtr(outindent.get("pId"));
            object.put("pAtr",proAtr);

            array.add(object);
        }
        return array;
    }

    private JSONArray getProductAtr(String pId){
        JSONArray array=new JSONArray();
        String sql="SELECT fName ,foName " +
                " from (ProductFormat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
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

    public JSONArray showCRturnIndent(String bId, String outStatus){
        JSONArray outindents=showCIndentList(bId,outStatus);
        for (int i=0;i<outindents.size();i++){
            JSONObject outindent=outindents.getJSONObject(i);
            JSONObject returnInfo=getReturnInfo(outindent.getBigInteger("outId").toString());
            outindent.put("rgState",returnInfo.getInteger("rgState"));
            outindent.put("rgType",returnInfo.get("rgType"));
            outindent.put("rgrReasons",returnInfo.get("rgrReasons"));

            outindents.add(outindent);
        }
        return outindents;
    }

    private JSONObject getReturnInfo(String outId){
        String sql="SELECT rgState,rgType,rgrReasons " +
                " FROM (returncatalog INNER JOIN returngoodreasons on rcId=rgrRCId) INNER JOIN ReturnGoods  on rgType=rgrId " +
                " WHERE rgOOId=? ORDER BY rgCreateTime DESC ";//最近一次退货信息

        Record record=Db.findFirst(sql,outId);
        JSONObject object=new JSONObject();
        object.put("rgState",record.get("rgState"));
        object.put("rgType",record.get("rgType"));
        object.put("rgrReasons",record.get("rgrReasons"));
        return object;
    }

    public JSONObject showCIndentInfo(String bId, String outStatus,String outId) {
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId," +
                "sName,outCName,outCreateTime,outModifyTime,outCPhone,outCAddress,outCInfo,outExpress,outExpressCompany " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outStatus=? and outId=? ORDER BY outCreateTime DESC ";
        Record record= Db.findFirst(sql,outStatus,outId);
        JSONObject object=new JSONObject();
        object.put("outId",record.get("outId"));
        object.put("outPlatform",record.get("outPlatform"));
        object.put("outStore",record.get("outStore"));
        object.put("pId",record.get("pId"));
        object.put("wTitle",record.get("wTitle"));
        object.put("wMainImage",record.get("wMainImage"));
        object.put("sId",record.get("sId"));
        object.put("sName",record.get("sName"));
        object.put("outCName",record.get("outCName"));
        object.put("outCreateTime",record.get("outCreateTime"));
        object.put("outModifyTime",record.get("outModifyTime"));
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
    private JSONObject showCReturn(String outId){
        String sql="SELECT rgReasons,rgImg1 ,rgImg2,rgImg3,rgRGMId,rgmName,rgmAddress,rgmPhone,rgiTrackNumber,rgiTrakTime,rgTrackCompany " +
                " from returngoods INNER JOIN ReturnGoodMould on returngoods.rgRGMId=returngoodmould.rgmId " +
                " WHERE returngoods.rgOOId=? ";
        Record record=Db.findFirst(sql,outId);
        JSONObject object=new JSONObject();
        object.put("rgReasons",record.get("rgReasons"));
        object.put("rgImg1",record.get("rgImg1"));
        object.put("rgImg2",record.get("rgImg2"));
        object.put("rgImg3",record.get("rgImg3"));
        object.put("rgRGMId",record.get("rgRGMId"));
        object.put("rgmName",record.get("rgmName"));
        object.put("rgmAddress",record.get("rgmAddress"));
        object.put("rgmPhone",record.get("rgmPhone"));
        object.put("rgiTrackNumber",record.get("rgiTrackNumber"));
        object.put("rgiTrakTime",record.get("rgiTrakTime"));
        object.put("rgTrackCompany",record.get("rgTrackCompany"));
        return object;
    }

    public JSONArray showMIndentList0(String bId, String inStatus) {
        JSONArray array=new JSONArray();
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=? ";
        List<Record> mIndentsO=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndentsO){
            JSONObject object=new JSONObject();
            object.put("inId",mIndent.get("inId"));
            object.put("inNum",mIndent.get("inNum"));
            object.put("inWare",mIndent.get("inWare"));
            object.put("wTitle",mIndent.get("wTitle"));
            object.put("pId",mIndent.get("pId"));
            object.put("pIdentifier",mIndent.get("pIdentifier"));
            object.put("pImage",mIndent.get("pImage"));
            object.put("pMoney",mIndent.get("pMoney"));
            object.put("inProductNum",mIndent.get("inProductNum"));
            object.put("inTotalMoney",mIndent.get("inTotalMoney"));
            object.put("inStore",mIndent.get("inStore"));
            object.put("sName",mIndent.get("sName"));
            object.put("inCreateTime",mIndent.get("inCreateTime"));
            object.put("inModifyTime",mIndent.get("inModifyTime"));
            JSONArray pAtr=getProductAtr(mIndent.get("pId"));
            object.put("pAtr",pAtr);

            array.add(object);
        }
        return array;
    }

    public JSONArray showMIndentList2(String bId, String inStatus) {//多了一个合作中的剩余量
        JSONArray array=new JSONArray();
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=1 and indent.inStatus=1";
        List<Record> mIndents1=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndents1){
            JSONObject object=new JSONObject();
            object.put("inId",mIndent.get("inId"));
            object.put("inNum",mIndent.get("inNum"));
            object.put("inWare",mIndent.get("inWare"));
            object.put("wTitle",mIndent.get("wTitle"));
            object.put("pId",mIndent.get("pId"));
            object.put("pIdentifier",mIndent.get("pIdentifier"));
            object.put("pImage",mIndent.get("pImage"));
            object.put("pMoney",mIndent.get("pMoney"));
            object.put("inProductNum",mIndent.get("inProductNum"));
            object.put("inTotalMoney",mIndent.get("inTotalMoney"));
            object.put("inStore",mIndent.get("inStore"));
            object.put("sName",mIndent.get("sName"));
            object.put("inCreateTime",mIndent.get("inCreateTime"));
            object.put("inModifyTime",mIndent.get("inModifyTime"));

            JSONArray pAtr=getProductAtr(mIndent.get("pId"));
            object.put("pAtr",pAtr);

            array.add(object);
        }
        return array;
    }

    public JSONArray showMIndentList3(String bId, String inStatus) {//申请退款
        JSONArray list2=showMIndentList2(bId,inStatus);
        String sql="SELECT diNumber,diMoney,diStatus " +
                "FROM DrawbackInfo " +
                " WHERE diInId=? ";
        for (int i=0 ;i<list2.size();i++)
        {
            JSONObject indent=list2.getJSONObject(i);
            Record drawbackInfo=Db.findFirst(sql,indent.get("pId"));
            indent.put("diNumber",drawbackInfo.get("diNumber"));
            indent.put("diMoney",drawbackInfo.get("diMoney"));
            indent.put("diStatus",drawbackInfo.get("diStatus"));
            list2.add(indent);
        }
        return list2;
    }

    public JSONObject showMReturnIndent(String inId, String diId) {
        String basesql="SELECT inId,inNum, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                " pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=? ";
        Record baserecord=Db.findFirst(basesql,inId);
        JSONArray pAtr=getProductAtr(baserecord.get("pId"));
        JSONObject object=new JSONObject();
        object.put("inId",baserecord.get("inId"));object.put("inNum",baserecord.get("inNum"));
        object.put("inStore",baserecord.get("inStore"));object.put("sName",baserecord.get("sName"));
        object.put("inWare",baserecord.get("inWare"));object.put("wTitle",baserecord.get("wTitle"));
        object.put("pId",baserecord.get("pId"));object.put("pIdentifier",baserecord.get("pIdentifier"));
        object.put("pImage",baserecord.get("pImage"));object.put("inLeftNum",baserecord.get("inLeftNum"));
        object.put("pMoney",baserecord.get("pMoney"));object.put("inProductNum",baserecord.get("inProductNum"));
        object.put("inTotalMoney",baserecord.get("inTotalMoney"));object.put("inCreateTime",baserecord.get("inCreateTime"));
        object.put("inModifyTime",baserecord.get("inModifyTime"));

        String sql="SELECT diNumber,diReasons,diImg1,diImg2,diImg3 " +
                " from drawbackinfo " +
                " WHERE diId=? ";
        Record record=Db.findFirst(sql,diId);
        JSONObject object1=new JSONObject();
        object1.put("diNumber",record.get("diNumber"));object1.put("diReasons",record.get("diReasons"));
        object1.put("diImg1",record.get("diImg1"));object1.put("diImg2",record.get("diImg2"));
        object1.put("diImg3",record.get("diImg3"));
        baserecord.set("pAtr",object1);
        return object;
    }

    public JSONObject showMFinishIndent(String inId) {
        String basesql="SELECT inId,inNum,inStore,sName, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum,inMtoB,inBtoM, " +
                " pMoney,inProductNum,inTotalMoney,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=?";
        Record baserecord=Db.findFirst(basesql,inId);

        JSONObject object=new JSONObject();
        object.put("inId",baserecord.get("inId"));object.put("inNum",baserecord.get("inNum"));
        object.put("inStore",baserecord.get("inStore"));object.put("sName",baserecord.get("sName"));
        object.put("inWare",baserecord.get("inWare"));object.put("wTitle",baserecord.get("wTitle"));
        object.put("pId",baserecord.get("pId"));object.put("pIdentifier",baserecord.get("pIdentifier"));
        object.put("pImage",baserecord.get("pImage"));object.put("inLeftNum",baserecord.get("inLeftNum"));
        object.put("inMtoB",baserecord.get("inMtoB"));object.put("inBtoM",baserecord.get("inBtoM"));
        object.put("pMoney",baserecord.get("pMoney"));object.put("inProductNum",baserecord.get("inProductNum"));
        object.put("inTotalMoney",baserecord.get("inTotalMoney"));object.put("inCreateTime",baserecord.get("inCreateTime"));
        object.put("inModifyTime",baserecord.get("inModifyTime"));

        JSONArray pAtr=getProductAtr(baserecord.get("pId"));
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
            sql=sql+" where drId not in (4,5)";
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
        drawbackInfo.put("indent",indent);
        return drawbackInfo;
    }
    public boolean isOverDeadLine(String inId)
    {
        String dateSql="SELECT wReplaceDays  " +
                "from ware  INNER JOIN indent on wId=inWare " +
                "where inId=?";
        int date=Db.queryInt(dateSql);//包退换的天数

        Date  currdate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Indent indent=Indent.dao.findById(inId);
            String payStr=sdf.format(indent.getInPayTime());
            Date payTime=sdf.parse(payStr);//

            Date now=new Date();
            String nowStr=sdf.format(now);
            currdate = sdf.parse(nowStr);
            System.out.println("现在的日期是：" + currdate);
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, date);// date为增加的天数，可以改变的
            currdate = ca.getTime();
            String enddatestr = sdf.format(currdate);
            System.out.println("增加天数以后的日期：" + enddatestr);
            Date enddate=sdf.parse(enddatestr);
            if (payTime.before(enddate)){
                return false;//在包退期限
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        return true;
    }

    public boolean drawback(Drawbackinfo drawbackinfo) {
        Indent indent=Indent.dao.findById(drawbackinfo.getDiInId());
        indent.setInStatus(4);
        drawbackinfo.setDiStatus(0);
        return drawbackinfo.save()&&indent.update();
    }
}
