package com.cross2u.user.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.cross2u.user.util.BaseResponse;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class BusinessServiceZ {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 删除浏览记录 真删
     * @param bsrBusiness
     * @return
     */
    public boolean deleteSearchRecord(String bsrBusiness) {
        String sql="DELETE from businesssearchrecord " +
                " WHERE bsrBusiness=? ";
        return Db.update(sql,bsrBusiness)>=1;
    }

    public boolean addBusiness(Business business) {
        business.setBStatus(2);//待审核状态
        business.setBRank(1);//等级 一级初始等级
        business.setBScore(0);//信誉分数
        business.setBOtherStatus1(1);//店铺设置在用
        return business.save();
    }

    public boolean addCollectStore(String cOwner, String cStore) {
        Collect collect=new Collect();
        collect.setCOwner(new BigInteger(cOwner));
        collect.setCStore(new BigInteger(cStore));
        return collect.save();
    }

    //店铺名称 照片 描述 评分 logo
    public Record showStoreDetail(String sId) {
        String sql="select sName,sPhoto,sDescribe,sScore,mmName,mmLogo,copBId " +
                "from (store INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore) INNER JOIN cooperation on store.sId=cooperation.copSId " +
                "where sId=? and copState =1";
        Record record= Db.findFirst(sql,new BigInteger(sId));
        String countSql="SELECT count(copBId) as copNumber  " +
                " FROM cooperation " +
                " WHERE copSId=?";
        Record count=Db.findFirst(countSql,sId);
        record.setColumns(count);
        return record;
    }

    public List<Record> showStoreWare(String wStore) {
        String sql=" SELECT wId, wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware " +
                "where ware.wStore=? ";

        List<Record> wares=Db.find(sql,new BigInteger(wStore));
        for (Record ware:wares){
            BigInteger wId=ware.getBigInteger("wId");
            System.out.println("wid="+wId);
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Record productNum=Db.findFirst(inProductNumSql);
            BigDecimal num=productNum.getBigDecimal("wMonthSale");
            if (num==null||num.equals(0.0))
            {
                ware.set("wMonthSale","0");
            }
            else {
                ware.set("wMonthSale",num.toString());
            }
        }
        return wares;
    }

    public List<Record> showStoreClass(String wStore) {

        String sql2="select wfdId,wfdName,wfdSort " +
                "from warefdispatch  " +
                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort";
        List<Record> fclass=Db.find(sql2,new BigInteger(wStore));//父类
        String sonSql="select wsdId,wsdName,wsdImg,wsdSort " +
                "from waresdispatch  " +
                "WHERE wsdWFDId=? ORDER BY wsdSort";

        for (Record record:fclass){//找子类
            List<Record> sonclass=Db.find(sonSql,new BigInteger(wStore));//有子类的父类
            record.set("son",sonclass);
        }

        return fclass;
    }

    public List<Record> showStoreFClassWare(String wbWFDId) {
        String sql="select wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
                "where warebelong.wbWFDId=?";
        List<Record> fWares=Db.find(sql,new BigInteger(wbWFDId));
        for (Record ware:fWares){
            BigInteger wId=ware.getBigInteger("wId");
            System.out.println("wid="+wId);
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Record productNum=Db.findFirst(inProductNumSql);
            BigDecimal num=productNum.getBigDecimal("wMonthSale");
            if (num==null||num.equals(0.0))
            {
                ware.set("wMonthSale","0");
            }
            else {
                ware.set("wMonthSale",num.toString());
            }
        }
        return fWares;
    }

    public List<Record> showStoreSClassWare(String wbWSDId) {
        String sql="select wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                " from ware JOIN warebelong on ware.wId=warebelong.wbWId  " +
                " where warebelong.wbWSDId like ?";
        List<Record> sWare=Db.find(sql,new BigInteger(wbWSDId));
        for (Record ware:sWare){
            BigInteger wId=ware.getBigInteger("wId");
            System.out.println("wid="+wId);
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Record productNum=Db.findFirst(inProductNumSql);
            BigDecimal num=productNum.getBigDecimal("wMonthSale");
            if (num==null||num.equals(0.0))
            {
                ware.set("wMonthSale","0");
            }
            else {
                ware.set("wMonthSale",num.toString());
            }
        }
        return sWare;
    }



    public List<Record> showBrowseRecord(String bid) {
        String sql="SELECT brId,brWare,wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                "from browserecord INNER JOIN ware on browserecord.brWare=ware.wId " +
                "where browserecord.brOwner=? and browserecord.brIsDelete=0 ";
        List<Record> browseRecords=Db.find(sql,new BigInteger(bid));
        for (Record record:browseRecords){
            Integer inProductNum=getInProductNum(record.getStr("wId"));
            record.set("inProductNum",inProductNum);
        }
        return browseRecords;
    }
    private Integer getInProductNum(String wId)
    {
        String sql="select sum(inProductNum) aswMonthSale " +
                "from indent " +
                "where inWare =? ";
        return Db.queryInt(sql,wId);
    }


    public boolean deleteBrowseRecord(String id) {
        Browserecord browserecord=Browserecord.dao.findById(new BigInteger(id));
        browserecord.setBrIsDelete(1);
        return browserecord.update();
    }

    public void deleteBrowseRecordRollback(String id) {
        Browserecord browserecord=Browserecord.dao.findById(new BigInteger(id));
        browserecord.setBrIsDelete(0);
    }

    public List<Record> showCollectWare(String bId) {
        String sql="SELECT cId,cWare,wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                " FROM collect INNER JOIN ware on collect.cWare=ware.wId " +
                " WHERE collect.cOwner=?";
        List<Record> collectwares=Db.find(sql,bId);
        for (Record record:collectwares){
            Integer inProductNum=getInProductNum(record.getStr("wId"));
            record.set("wMonthSale",inProductNum);
        }
        return collectwares;
    }

    public List<Record> showCollectStore(String bId) {
        String sql="SELECT cId,cStore,sName,sScore,mmLogo " +
                " from (collect INNER JOIN store on collect.cStore=store.sId) INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore " +
                " WHERE collect.cOwner=?";
        List<Record> collectStores=Db.find(sql,bId);
        return collectStores;
    }

    public boolean deleteCollect(String cId){
        return Collect.dao.deleteById(cId);
    }

    public List<Record> showCopStore(String bId, String copState) {
        String sql="SELECT copId,copSId,sName,sScore,mmLogo" +
                " from (cooperation INNER JOIN store on store.sId=cooperation.copSId) INNER JOIN mainmanufacturer on mainmanufacturer.mmStore=store.sId " +
                " WHERE cooperation.copBId=? and cooperation.copState=? ";
        List<Record> copStores=Db.find(sql,bId,copState);
        return copStores;
    }

    //终止合作
    public boolean deleteCop(String copId) {
        String sql="UPDATE cooperation " +
                " set copState=2 " +
                " WHERE copId=?";
        return Db.update(sql,copId)==1;
    }

    public List<Record> showCIndentList(String bId, String outStatus) {
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outBusiness=? and outindent.outStatus=? ORDER BY outCreateTime DESC ";
        List<Record> outindents=Db.find(sql,bId,outStatus);
        for (Record outindent: outindents){
            List<Record> proAtr=getProductAtr(outindent.getBigInteger("pId").toString());
            outindent.set("pAtr",proAtr);
        }
        return outindents;
    }

    private List<Record> getProductAtr(String pId){
        String sql="SELECT fName ,foName " +
                " from (productformat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
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


    public Record showCIndentInfo(String bId, String outStatus,String outId) {
        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime,outCName,outCPhone,outCAddress,outCInfo,outExpress,outExpressCompany " +
                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
                " WHERE outindent.outStatus=? and outId=? ORDER BY outCreateTime DESC ";
        return Db.findFirst(sql,outStatus,outId);
    }

    //退货类型 退货理由 退货状态
    private Record getReturnInfo(String outId){
        String sql="SELECT rgState,rgType,rgrReasons " +
                " FROM (returncatalog INNER JOIN returngoodreasons on rcId=rgrRCId) INNER JOIN returngoods  on rgType=rgrId " +
                " WHERE rgOOId=? ORDER BY rgCreateTime DESC ";//最近一次退货信息
        return Db.findFirst(sql,outId);
    }

    public Record showCRturnInfo(String bId, String outStatus,String outId){
        Record returnRecord=showCIndentInfo(bId, outStatus,outId);//订单基本情况
        Record returnInfo=getReturnInfo(outId);//退货理由 和列表一样
        returnRecord.setColumns(returnInfo);
        Record returnInfo2=showCReturn(outId);//下游买家退货理由 下游买家退货单号
        returnRecord.setColumns(returnInfo2);
        Record cExpressInfo=showCReturnExpress(outId);
        if (cExpressInfo.get("rgiTrackNumber")!=null)
        {
            returnRecord.set("cExpress",cExpressInfo);
        }
        else {
            returnRecord.set("cExpress",null);
        }
        if (returnInfo2.getBigInteger("rgmId")!=null){
            Record returnmould=showCReturnMould(returnInfo2.getBigInteger("rgmId").toString());//M的模板
            returnRecord.set("returnmould",returnmould);
        }
        else {
            returnRecord.set("returnmould",null);
        }
        return returnRecord;
    }

    //退货模板 下游买家退货理由 下游买家退货单号
    private Record showCReturn(String outId){
        String sql="SELECT rgReasons,rgImg1 ,rgImg2,rgImg3,rgRGMId" +
                " from returngoods " +
                " WHERE returngoods.rgOOId=? ";
        return Db.findFirst(sql,outId);
    }

    private Record showCReturnExpress(String outId){
        String sql=" select rgiTrackNumber,rgiTrakTime,rgTrackCompany from returngoods where rgOOId=?";
        return Db.findFirst(sql,outId);
    }

    private Record showCReturnMould(String rgmId) {
        String sql="SELECT rgmName,rgmAddress,rgmPhone " +
                "from  returngoodmould  " +
                "WHERE rgmId =?";
        return Db.findFirst(sql,rgmId);
    }



    public List<Record> showMIndentList0(String bId, String inStatus) {
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inStatus," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=?  ";
        List<Record> mIndentsO=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndentsO){
            List<Record> pAtr=getProductAtr(mIndent.getBigInteger("pId").toString());
            mIndent.set("pAtr",pAtr);
        }
        return mIndentsO;
    }

    //B已完成订单 B M已关闭订单 M异常而关闭 M待评价订单
    public List<Record> showMIndentList3(String bId, String inStatus) {
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inStatus," +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus in (3,5,6,7,8,9)";
        List<Record> mIndentsO=Db.find(sql,bId);
        for (Record mIndent :mIndentsO){
            List<Record> pAtr=getProductAtr(mIndent.getBigInteger("pId").toString());
            mIndent.set("pAtr",pAtr);
        }
        return mIndentsO;
    }

    public List<Record> showMIndentList1(String bId, String inStatus) {//多了一个合作中的剩余量
        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
                " WHERE indent.inBusiness=? and indent.inStatus=?";
        List<Record> mIndents1=Db.find(sql,bId,inStatus);
        for (Record mIndent :mIndents1){
            List<Record> pAtr=getProductAtr(mIndent.getBigInteger("pId").toString());
            mIndent.set("pAtr",pAtr);
        }
        return mIndents1;
    }

    public List<Record> showMIndentList4(String bId, String inStatus) {//申请退款
        List<Record> list2=showMIndentList1(bId,inStatus);
        String sql="SELECT diNumber,diMoney,diStatus,diNumber " +
                "FROM drawbackinfo " +
                " WHERE diInId=? ";
        for (Record indent :list2)
        {
            Record drawbackInfo=Db.findFirst(sql,indent.getBigInteger("pId").toString());
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
        List<Record> pAtr=getProductAtr(baserecord.getBigInteger("pId").toString());
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
        List<Record> pAtr=getProductAtr(baserecord.getBigInteger("pId").toString());
        baserecord.set("pAtr",pAtr);
        return  baserecord;
    }


    public Record intoMine(String openId) {
        String sql="select ";
        Record record=Db.findFirst(sql,openId);
        return record;
    }
}

