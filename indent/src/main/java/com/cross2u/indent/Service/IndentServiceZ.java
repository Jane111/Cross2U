package com.cross2u.indent.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndentServiceZ {

    public List<Record> showCIndentList(String bId, String outStatus) {
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outBusiness=? and outindent.outStatus=? ORDER BY outCreateTime DESC ";
        List<Record> outindents= Db.find(sql,bId,outStatus);
        for (Record outindent: outindents){
            List<Record> proAtr=getProductAtr(outindent.get("pId"));
            outindent.set("pAtr",proAtr);
        }
        return outindents;
    }

    private List<Record> getProductAtr(String pId){
        String sql="SELECT fName ,foName " +
                " from (ProductFormat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
                " WHERE pfProduct=? ";
        return Db.find(sql,pId);
    }

    public List<Record> showCRturnIndent(String bId, String outStatus){
        List<Record> outindents=showCIndentList(bId,outStatus);
        for (Record outindent: outindents){
            Record returnInfo=getReturnInfo(outindent.get("outId"));
            outindent.set("rgState",returnInfo.getInt("rgState"));
            outindent.set("rgType",returnInfo.get("rgType"));
            outindent.set("rgrReasons",returnInfo.get("rgrReasons"));
        }
        return outindents;
    }

    private Record getReturnInfo(String outId){
        String sql="SELECT rgState,rgType,rgrReasons " +
                " FROM (returncatalog INNER JOIN returngoodreasons on rcId=rgrRCId) INNER JOIN ReturnGoods  on rgType=rgrId " +
                " WHERE rgOOId=? ORDER BY rgCreateTime DESC ";//最近一次退货信息
        return Db.findFirst(sql,outId);
    }

    public Record showCIndentInfo(String bId, String outStatus,String outId) {
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime,outCName,outCPhone,outCAddress,outCInfo,outExpress,outExpressCompany " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outStatus=? and outId=? ORDER BY outCreateTime DESC ";
        return Db.findFirst(sql,outStatus,outId);
    }

    public Record showCRturnInfo(String bId, String outStatus,String outId){
        Record returnRecord=showCIndentInfo(bId, outStatus,outId);
        Record returnInfo=getReturnInfo(outId);
        returnRecord.setColumns(returnInfo);
        Record returnInfo2=showCReturn(outId);
        returnRecord.setColumns(returnInfo2);
        return returnRecord;
    }
    private Record showCReturn(String outId){
        String sql="SELECT rgReasons,rgImg1 ,rgImg2,rgImg3,rgRGMId,rgmName,rgmAddress,rgmPhone,rgiTrackNumber,rgiTrakTime,rgTrackCompany " +
                " from returngoods INNER JOIN ReturnGoodMould on returngoods.rgRGMId=returngoodmould.rgmId " +
                " WHERE returngoods.rgOOId=? ";
        return Db.findFirst(sql,outId);
    }

    public List<Record> showMIndentList0(String bId, String inStatus) {
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=? ";
        List<Record> mIndentsO=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndentsO){
            List<Record> pAtr=getProductAtr(mIndent.get("pId"));
            mIndent.set("pAtr",pAtr);
        }
        return mIndentsO;
    }

    public List<Record> showMIndentList2(String bId, String inStatus) {//多了一个合作中的剩余量
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=1 and indent.inStatus=1";
        List<Record> mIndents1=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndents1){
            List<Record> pAtr=getProductAtr(mIndent.get("pId"));
            mIndent.set("pAtr",pAtr);
        }
        return mIndents1;
    }

    public List<Record> showMIndentList3(String bId, String inStatus) {//申请退款
        List<Record> list2=showMIndentList2(bId,inStatus);
        String sql="SELECT diNumber,diMoney,diStatus,diNumber " +
                "FROM DrawbackInfo " +
                " WHERE diInId=? ";
        for (Record indent :list2)
        {
            Record drawbackInfo=Db.findFirst(sql,indent.get("pId"));
            indent.setColumns(drawbackInfo);
        }
        return list2;
    }

    public Record showMReturnIndent(String inId, String diId) {
        String basesql="SELECT inId,inNum, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                " pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=? ";
        Record baserecord=Db.findFirst(basesql,inId);
        List<Record> pAtr=getProductAtr(baserecord.get("pId"));
        String sql="SELECT diNumber,diReasons,diImg1,diImg2,diImg3 " +
                " from drawbackinfo " +
                " WHERE diId=? ";
        Record record=Db.findFirst(sql,diId);
        baserecord.setColumns(record);
        baserecord.set("pAtr",pAtr);
        return baserecord;
    }

    public Record showMFinishIndent(String inId) {
        String basesql="SELECT inId,inNum,inStore,sName, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum,inMtoB,inBtoM, " +
                " pMoney,inProductNum,inTotalMoney,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE inId=?";
        Record baserecord=Db.findFirst(basesql,inId);
        List<Record> pAtr=getProductAtr(baserecord.get("pId"));
        baserecord.set("pAtr",pAtr);
        return  baserecord;
    }

}
