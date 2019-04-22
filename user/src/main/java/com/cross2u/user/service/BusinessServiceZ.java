//package com.cross2u.user.service;
//
//import com.cross2u.user.model.*;
//import com.jfinal.plugin.activerecord.Db;
//import com.jfinal.plugin.activerecord.Record;
//import org.springframework.stereotype.Service;
//
//import java.math.BigInteger;
//import java.util.List;
//
//@Service
//public class BusinessServiceZ {
//    /**
//     * 删除浏览记录 真删
//     * @param bsrBusiness
//     * @return
//     */
//    public boolean deleteSearchRecord(String bsrBusiness) {
//        return Browserecord.dao.deleteById(bsrBusiness);
//    }
//
//    public boolean addBusiness(Business business) {
//        business.setBStatus(2);//待审核状态
//        business.setBRank(1);//等级 一级初始等级
//        business.setBScore(0);//信誉分数
//
//        return business.save();
//    }
//
//    public boolean addCollectStore(String cOwner, String cStore) {
//        Collect collect=new Collect();
//        collect.setCOwner(new BigInteger(cOwner));
//        collect.setCStore(new BigInteger(cStore));
//        return collect.save();
//    }
//
//    //店铺名称 照片 描述 评分 logo
//    public Record showStoreDetail(String sId) {
//        String sql="select sName,sPhoto,sDescribe,sScore,mmName,mmLogo,copBId,count(*) as copNumber " +
//                "from (store INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore) INNER JOIN cooperation on store.sId=cooperation.copSId " +
//                "where sId=? and copState =1";
//        Record record= Db.findFirst(sql,new BigInteger(sId));
//        return record;
//    }
//
//    public Record showStoreWare(String wStore) {
//        String sql=" SELECT  wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
//                " from ware INNER JOIN indent on ware.wId=indent.inWare " +
//                " where ware.wStore=? ";
//        Record ware=Db.findFirst(sql,new BigInteger(wStore));
//        return ware;
//    }
//
//    public List<Record> showStoreClass(String wStore) {
//        String sql1="select wfdId,wfdName,wfdSort,wsdId,wsdName,wsdImg,wsdSort " +
//                "from warefdispatch INNER JOIN waresdispatch ON warefdispatch.wfdId=waresdispatch.wsdWFDId " +
//                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort,wsdSort";
//        List<Record> fsclass=Db.find(sql1,new BigInteger(wStore));//有子类的父类
//        String sql2="select wfdId,wfdName,wfdSort " +
//                "from warefdispatch  " +
//                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort";
//        List<Record> fclass=Db.find(sql2,new BigInteger(wStore));//两者叠加
//        if (fsclass.addAll(fclass)){
//            return fsclass;
//        }
//        return null;
//    }
//
//    public List<Record> showStoreFClassWare(String wbWFDId) {
//        String sql="select wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
//                "from (ware INNER JOIN indent on ware.wId=indent.inWare) INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
//                "where warebelong.wbWFDId=?";
//        List<Record> fWare=Db.find(sql,new BigInteger(wbWFDId));
//        return fWare;
//    }
//
//    public List<Record> showStoreSClassWare(String wbWSDId) {
//        String sql="select wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
//                "from (ware INNER JOIN indent on ware.wId=indent.inWare) INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
//                "where warebelong.wbWSDId=?";
//        List<Record> sWare=Db.find(sql,new BigInteger(wbWSDId));
//        return sWare;
//    }
//
//    public List<Publicinfo> showPublicInfo() {
//        String sql="SELECT piId,piTitle,piContent,piCreateTime FROM PublicInfo;";
//        List<Publicinfo> publicinfos=Publicinfo.dao.find(sql);
//        return publicinfos;
//    }
//
//    public List<Record> showBrowseRecord(String bid) {
//        String sql="SELECT brId,brWare,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) aswMonthSale " +
//                "from (browserecord INNER JOIN ware on browserecord.brWare=ware.wId) INNER JOIN indent on ware.wId = indent.inWare " +
//                "where browserecord.brOwner=? and browserecord.brIsDelete=1";
//        List<Record> browseRecords=Db.find(sql,new BigInteger(bid));
//        return browseRecords;
//    }
//
////    public boolean deleteBrowseRecord(String id) {
////        Browserecord browserecord=Browserecord.dao.findById(new BigInteger(id));
////        browserecord.setBrIsDelete(1);
////        return browserecord.update();
////    }
////
////    public void deleteBrowseRecordRollback(String id) {
////        Browserecord browserecord=Browserecord.dao.findById(new BigInteger(id));
////        browserecord.setBrIsDelete(0);
////    }
//
//    public List<Record> showCollectWare(String bId) {
//        String sql="SELECT cId,cWare,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
//                " FROM (collect INNER JOIN ware on collect.cWare=ware.wId) INNER JOIN indent on ware.wId=indent.inWare " +
//                " WHERE collect.cOwner=?";
//        List<Record> collectwares=Db.find(sql,bId);
//        return collectwares;
//    }
//
//    public List<Record> showCollectStore(String bId) {
//        String sql="SELECT cId,cStore,sName,sScore,mmLogo " +
//                " from (collect INNER JOIN store on collect.cStore=store.sId) INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore " +
//                " WHERE collect.cOwner=?";
//        List<Record> collectStores=Db.find(sql,bId);
//        return collectStores;
//    }
//
//    public boolean deleteCollect(String cId){
//        return Collect.dao.deleteById(cId);
//    }
//
//    public List<Record> showCopStore(String bId, String copState) {
//        String sql="SELECT copId,copSId,sName,sScore,mmLogo" +
//                " from (cooperation INNER JOIN store on store.sId=cooperation.copSId) INNER JOIN mainmanufacturer on mainmanufacturer.mmStore=store.sId " +
//                " WHERE cooperation.copBId=? and cooperation.copState=? ";
//        List<Record> copStores=Db.find(sql,bId,copState);
//        return copStores;
//    }
//
//    public boolean deleteCop(String copId) {
//        Cooperation cooperation=Cooperation.dao.findById(copId);
//        cooperation.setCopState(2);//终止合作
//        return cooperation.save();
//    }
//
//    public List<Record> showCIndentList(String bId, String outStatus) {
//        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime " +
//                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
//                " WHERE outindent.outBusiness=? and outindent.outStatus=? ORDER BY outCreateTime DESC ";
//        List<Record> outindents=Db.find(sql,bId,outStatus);
//        for (Record outindent: outindents){
//            List<Record> proAtr=getProductAtr(outindent.get("pId"));
//            outindent.set("pAtr",proAtr);
//        }
//        return outindents;
//    }
//
//    private List<Record> getProductAtr(String pId){
//        String sql="SELECT fName ,foName " +
//                " from (ProductFormat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
//                " WHERE pfProduct=? ";
//        return Db.find(sql,pId);
//    }
//
//    public List<Record> showCRturnIndent(String bId, String outStatus){
//        List<Record> outindents=showCIndentList(bId,outStatus);
//        for (Record outindent: outindents){
//            Record returnInfo=getReturnInfo(outindent.get("outId"));
//            outindent.set("rgState",returnInfo.getInt("rgState"));
//            outindent.set("rgType",returnInfo.get("rgType"));
//            outindent.set("rgrReasons",returnInfo.get("rgrReasons"));
//        }
//        return outindents;
//    }
//
//    private Record getReturnInfo(String outId){
//        String sql="SELECT rgState,rgType,rgrReasons " +
//                " FROM (returncatalog INNER JOIN returngoodreasons on rcId=rgrRCId) INNER JOIN ReturnGoods  on rgType=rgrId " +
//                " WHERE rgOOId=? ORDER BY rgCreateTime DESC ";//最近一次退货信息
//        return Db.findFirst(sql,outId);
//    }
//
//    public Record showCIndentInfo(String bId, String outStatus,String outId) {
//        String sql="SELECT outId,outPlatform,outStore,pId,wTitle,wMainImage,sId,sName,outCName,outCreateTime,outModifyTime,outCName,outCPhone,outCAddress,outCInfo,outExpress,outExpressCompany " +
//                " from ((outindent INNER JOIN  product on outindent.outPIdentifier = product.pIdentifier) INNER JOIN ware on wId=pWare)INNER JOIN store on ware.wStore=store.sId " +
//                " WHERE outindent.outStatus=? and outId=? ORDER BY outCreateTime DESC ";
//        return Db.findFirst(sql,outStatus,outId);
//    }
//
//    public Record showCRturnInfo(String bId, String outStatus,String outId){
//        Record returnRecord=showCIndentInfo(bId, outStatus,outId);
//        Record returnInfo=getReturnInfo(outId);
//        returnRecord.setColumns(returnInfo);
//        Record returnInfo2=showCReturn(outId);
//        returnRecord.setColumns(returnInfo2);
//        return returnRecord;
//    }
//    private Record showCReturn(String outId){
//        String sql="SELECT rgReasons,rgImg1 ,rgImg2,rgImg3,rgRGMId,rgmName,rgmAddress,rgmPhone,rgiTrackNumber,rgiTrakTime,rgTrackCompany " +
//                " from returngoods INNER JOIN ReturnGoodMould on returngoods.rgRGMId=returngoodmould.rgmId " +
//                " WHERE returngoods.rgOOId=? ";
//        return Db.findFirst(sql,outId);
//    }
//
//    public List<Record> showMIndentList0(String bId, String inStatus) {
//        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage," +
//                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
//                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
//                " WHERE indent.inBusiness=? and indent.inStatus=? ";
//        List<Record> mIndentsO=Db.find(sql,bId,inStatus);
//        for (Record mIndent :mIndentsO){
//            List<Record> pAtr=getProductAtr(mIndent.get("pId"));
//            mIndent.set("pAtr",pAtr);
//        }
//        return mIndentsO;
//    }
//
//    public List<Record> showMIndentList2(String bId, String inStatus) {//多了一个合作中的剩余量
//        String sql="SELECT inId,inNum,inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
//                "pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
//                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
//                " WHERE indent.inBusiness=1 and indent.inStatus=1";
//        List<Record> mIndents1=Db.find(sql,bId,inStatus);
//        for (Record mIndent :mIndents1){
//            List<Record> pAtr=getProductAtr(mIndent.get("pId"));
//            mIndent.set("pAtr",pAtr);
//        }
//        return mIndents1;
//    }
//
//    public List<Record> showMIndentList3(String bId, String inStatus) {//申请退款
//        List<Record> list2=showMIndentList2(bId,inStatus);
//        String sql="SELECT diNumber,diMoney,diStatus,diNumber " +
//                "FROM DrawbackInfo " +
//                " WHERE diInId=? ";
//        for (Record indent :list2)
//        {
//            Record drawbackInfo=Db.findFirst(sql,indent.get("pId"));
//            indent.setColumns(drawbackInfo);
//        }
//        return list2;
//    }
//
//    public Record showMReturnIndent(String inId, String diId) {
//        String basesql="SELECT inId,inNum, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum, " +
//                " pMoney,inProductNum,inTotalMoney,inStore,sName,inCreateTime,inModifyTime " +
//                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
//                " WHERE inId=? ";
//        Record baserecord=Db.findFirst(basesql,inId);
//        List<Record> pAtr=getProductAtr(baserecord.get("pId"));
//        String sql="SELECT diNumber,diReasons,diImg1,diImg2,diImg3 " +
//                " from drawbackinfo " +
//                " WHERE diId=? ";
//        Record record=Db.findFirst(sql,diId);
//        baserecord.setColumns(record);
//        baserecord.set("pAtr",pAtr);
//        return baserecord;
//    }
//
//    public Record showMFinishIndent(String inId) {
//        String basesql="SELECT inId,inNum,inStore,sName, inWare,wTitle,pId,pIdentifier,pImage,inLeftNum,inMtoB,inBtoM, " +
//                " pMoney,inProductNum,inTotalMoney,inCreateTime,inModifyTime " +
//                " FROM ((indent INNER JOIN product ON inProduct=pId )INNER JOIN ware ON pWare=wId)INNER JOIN store ON wStore=sId " +
//                " WHERE inId=?";
//        Record baserecord=Db.findFirst(basesql,inId);
//        List<Record> pAtr=getProductAtr(baserecord.get("pId"));
//        baserecord.set("pAtr",pAtr);
//        return  baserecord;
//    }
//}
//
