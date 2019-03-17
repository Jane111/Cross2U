package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class BusinessServiceZ {




    //根据openid 查找business中的bId
    private String getBIdByOpenId(String openId) {
        String sql="SELECT bOpenId,bId from business WHERE bOpenId  like '"+openId+"'";
        Record business=Db.findFirst(sql);
        if (business!=null)
        {
            return business.getBigInteger("bId").toString();
        }
        return null;
    }

    //根据bId判断是否代理
    private Integer isCooperation(String bId,String sId) {
        String sql="SELECT copId from cooperation  WHERE copState=1 and copBId=? and copSId=?";
        Record isCooperation=Db.findFirst(sql,bId,sId);
        return isCooperation==null?0:1;
    }

    //根据bId判断是否收藏
    private Integer isCollect(String bId,String sId) {
        String sql="SELECT cId from collect WHERE cOwner=? and cStore=? and cWare is NULL";
        Record isCollect=Db.findFirst(sql,bId,sId);
        System.out.println(isCollect);
        return isCollect==null?0:1;
    }



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

    public BigInteger addBusinessStep1(Business business) {
        business.setBStatus(2);//待审核状态
        business.setBRank(1);//等级 一级初始等级
        business.setBScore(0);//信誉分数
        business.setBOtherStatus1(1);//店铺设置在用
        business.save();
        return business.getBId();
    }
    public boolean addBusinessStep23(Business business) {
        return business.update();
    }


    public boolean addCollectStore(String cOwner, String cStore) {
        Collect collect=new Collect();
        collect.setCOwner(new BigInteger(cOwner));
        collect.setCStore(new BigInteger(cStore));
        return collect.save();
    }

    //店铺名称 照片 描述 评分 logo
    public JSONObject showStoreDetail(String sId,String openId) {
        JSONObject jsonObject=new JSONObject();
        String sql="select sName,sPhoto,sDescribe,sScore,mmName,mmLogo " +
                "from store INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore " +
                "where sId=? ";
        Record record= Db.findFirst(sql,new BigInteger(sId));
        jsonObject.put("sName",record.get("sName"));
        String sPhoto=record.get("sPhoto");
        String[] photos=sPhoto.split(",");
        jsonObject.put("sPhoto",photos);
        jsonObject.put("sDescribe",record.get("sDescribe"));
        jsonObject.put("sScore",record.get("sScore"));
        jsonObject.put("mmName",record.get("mmName"));
        jsonObject.put("mmLogo",record.get("mmLogo"));
        jsonObject.put("copBId",record.get("copBId"));

        String countSql="SELECT count(copBId) as copNumber  " +
                " FROM cooperation " +
                " WHERE copSId=?";
        Integer count=Db.queryInt(countSql,sId);
        jsonObject.put("copNumber",count);
        /**
         * 收藏人数 collectNumber
         * 是否收藏 isCollect
         * 是否代理 isCooperation
         * 前四个商品 sWares
         */
        String collectSql="SELECT SUM(cOwner) as collectNumber FROM `collect` WHERE cStore=? and cWare is NULL;";
        Integer collect=Db.queryInt(collectSql,sId);
        jsonObject.put("collectNumber",collect);

        String bId=getBIdByOpenId(openId);
        if (bId!=null){
            System.out.println("bID"+bId);
            jsonObject.put("isCollect",isCollect(bId,sId));
            jsonObject.put("isCooperation",isCooperation(bId,sId));
        }
        else{//没有授权认证
            System.out.println("null????");
            jsonObject.put("isCollect",0);
            jsonObject.put("isCooperation",0);
        }

        System.out.println(jsonObject);
        return jsonObject;
    }



    public JSONArray showBrowseRecord(String bid) {
        JSONArray jsonArray=new JSONArray();
        String sql="SELECT brId,wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                "from browserecord INNER JOIN ware on browserecord.brWare=ware.wId " +
                "where browserecord.brOwner=? and browserecord.brIsDelete=0 ";
        List<Record> browseRecords=Db.find(sql,new BigInteger(bid));
        for (Record record:browseRecords){
            JSONObject object=new JSONObject();
            Integer inProductNum=getInProductNum(record.getStr("wId"));
            object.put("brId",record.getBigInteger("brId"));
            object.put("wId",record.getBigInteger("wId"));
            object.put("wMainImage",record.get("wMainImage"));
            object.put("wTitle",record.get("wTitle"));
            object.put("wStartPrice",record.getFloat("wStartPrice"));
            object.put("wHighPrice",record.getFloat("wHighPrice"));
            object.put("wPriceUnit",record.getInt("wPriceUnit"));
            object.put("inProductNum",inProductNum);

            jsonArray.add(object);
        }
        return jsonArray;
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

    public JSONArray showCollectWare(String bId) {
        JSONArray jsonArray=new JSONArray();
        String sql="SELECT cId,wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                " FROM collect INNER JOIN ware on collect.cWare=ware.wId " +
                " WHERE collect.cOwner=?";
        List<Record> collectwares=Db.find(sql,bId);
        for (Record record:collectwares){
            JSONObject object=new JSONObject();
            Integer inProductNum=getInProductNum(record.getStr("wId"));
            object.put("cId",record.getBigInteger("cId"));
            object.put("wId",record.getBigInteger("wId"));
            object.put("wMainImage",record.get("wMainImage"));
            object.put("wTitle",record.get("wTitle"));
            object.put("wStartPrice",record.getFloat("wStartPrice"));
            object.put("wHighPrice",record.getFloat("wHighPrice"));
            object.put("wPriceUnit",record.getInt("wPriceUnit"));
            object.put("inProductNum",inProductNum);

            jsonArray.add(object);
        }
        return jsonArray;
    }

    public JSONArray showCollectStore(String bId) {
        String sql="SELECT cId,sId,sName,sScore,mmLogo " +
                " from (collect INNER JOIN store on collect.cStore=store.sId) INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore " +
                " WHERE collect.cOwner=?";
        List<Record> collectStores=Db.find(sql,bId);
        JSONArray array=new JSONArray();
        for (Record collect:collectStores){
            JSONObject object=new JSONObject();
            object.put("cId",collect.getBigInteger("cId"));
            object.put("sId",collect.getBigInteger("sId"));
            object.put("sName",collect.get("sName"));
            object.put("sScore",collect.get("sScore"));
            object.put("mmLogo",collect.get("mmLogo"));

            array.add(object);
        }
        return array;
    }

    public boolean deleteCollect(String cId){
        String sql="delete from collect where cId=? ";
        return Db.update(sql,cId)>=1;
    }

    public JSONArray showCopStore(String bId, String copState) {
        JSONArray array=new JSONArray();
        String sql="SELECT copId,sId,sName,sScore,mmLogo,copState" +
                " from (cooperation INNER JOIN store on store.sId=cooperation.copSId) INNER JOIN mainmanufacturer on mainmanufacturer.mmStore=store.sId " +
                " WHERE cooperation.copBId=? and cooperation.copState=? ";
        if(copState.equals("2")){sql=sql+" or cooperation.copState=3 or cooperation.copState=4";}
        List<Record> copStores=Db.find(sql,bId,copState);
        for (Record store: copStores){
            JSONObject object=new JSONObject();
            object.put("copId",store.getBigInteger("copId"));
            object.put("sId",store.getBigInteger("sId"));
            object.put("sName",store.get("sName"));
            object.put("sScore",store.getInt("sScore"));
            object.put("mmLogo",store.get("mmLogo"));
            object.put("copState",store.get("copState"));
            array.add(object);
        }
        return array;
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


/*
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
    }*/

    /*//B已完成订单 B M已关闭订单 M异常而关闭 M待评价订单
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
    }*/

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

   /* public List<Record> showMIndentList4(String bId, String inStatus) {//申请退款
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
    }*/

    public boolean isArthorise(String openId){//是否授权
        String checkSql="select vWeiXinIcon from visitor where vOpenId like '"+openId+"'";
        Visitor check=Visitor.dao.findFirst(checkSql);
        System.out.println(check);
        if (check.get("vWeiXinIcon")==null){
            return false;//未授权
        }
        return true;
    }
    public Visitor getVisitorByOpenId(String openId)
    {
        String sql="select vWeiXinIcon as bWeiXinIcon,vWeiXinName as bWeiXinName from visitor where vOpenId like '"+openId+"'";
        Visitor visitor=Visitor.dao.findFirst(sql);
        if (visitor.getVWeiXinIcon()==null||visitor.getVWeiXinIcon().equals("")) {
            return null;
        }
        return visitor;
    }
    public JSONObject intoMine(String openId) {
        String updateS="select * from business where bOpenId=?";
        Business updateR=Business.dao.findFirst(updateS,openId);
        if (updateR==null){
            return null;
        }
        System.out.println("--------------update:"+updateR);
        BigInteger bId=updateR.getBId();
        updatBRank(updateR);
        String sql="select bWeiXinIcon ,bWeiXinName,bScore,bRank from business where bId=?";
        Business record=Business.dao.findFirst(sql,bId);
        JSONObject object=new JSONObject();
        object.put("business",record);
        object.put("collectStore",getCollectStore(bId));
        object.put("collectWare",getCollectWare(bId));
        object.put("browseWare",getBrowseWare(bId));
        object.put("cooperation",getCooperation(bId));
        return object;
    }

    private Integer updatBRank(Business business) {
        int bRank=0;
        Integer bScore=business.getBScore();
        System.out.println("bScore"+bScore);
        if(bScore==null)
        {
            return null;
        }
        if (bScore>=0 && bScore<100) {
            bRank=1;
        } else if (bScore>=100 && bScore<200) {
            bRank=2;
        } else if (bScore>=200 && bScore<300) {
            bRank=3;
        }
        else if (bScore>=300 && bScore<400) {
            bRank=4;
        }
        else if (bScore>=400){
            bRank=5;
        }

        business.setBRank(bRank);
        business.update();
        return bRank;
    }

    /**
     * collectStore
     * 收藏商品个数 collectWare
     * 浏览足迹个数 browseWare
     * 代理品牌个数 cooperation
     * @param bId
     * @return
     */

    private Integer getCollectStore(BigInteger bId){
        String sql="select count(cId) as count " +
                "from collect INNER JOIN store on cStore=sId " +
                "where cOwner=? and cStore is not null";
        return Db.queryInt(sql,bId);
    }
    private Integer getCollectWare(BigInteger bId){
        String sql="select count(cId) as count  " +
                "from collect INNER JOIN ware on cWare=wId " +
                "where cOwner=? and cWare is not null";
        return Db.queryInt(sql,bId);
    }
    private Integer getBrowseWare(BigInteger bId){
        String sql="select count(brId) as count " +
                "from browserecord INNER JOIN ware on browserecord.brWare=ware.wId " +
                "where brOwner=? and browserecord.brIsDelete=0 ";
        return Db.queryInt(sql,bId);
    }
    private Integer getCooperation(BigInteger bId){
        String sql="select count(copBId) as count " +
                "from cooperation INNER JOIN store on sId=copSId " +
                "where copBId=?  ";
        return Db.queryInt(sql,bId);
    }
    public Business findById(String bId) {
        return Business.dao.findById(bId);
    }

    public boolean addVisitor(String openId, String weixinName, String weixinIcon) {
        String sql="select * from  visitor where vOpenId like '"+openId+"'";
        Visitor visitor=Visitor.dao.findFirst(sql);
        visitor.setVWeiXinIcon(weixinIcon);
        visitor.setVWeiXinName(weixinName);
        return visitor.update();
    }

    public boolean cancelCop(String copId) {
        String sql="update cooperation set copState=4 where copId=?";
        return Db.update(sql,copId)==1;
    }

    public JSONObject MshowStoreDetail(String sId) {
        JSONObject jsonObject=new JSONObject();
        String sql="select sName,sPhoto,sDescribe,sScore,mmName,mmLogo " +
                "from store INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore " +
                "where sId=? ";
        Record record= Db.findFirst(sql,new BigInteger(sId));
        jsonObject.put("sName",record.get("sName"));
        String sPhoto=record.get("sPhoto");
        String[] photos=sPhoto.split(",");
        jsonObject.put("sPhoto",photos);
        jsonObject.put("sDescribe",record.get("sDescribe"));
        jsonObject.put("sScore",record.get("sScore"));
        jsonObject.put("mmName",record.get("mmName"));
        jsonObject.put("mmLogo",record.get("mmLogo"));
        jsonObject.put("copBId",record.get("copBId"));

        String countSql="SELECT count(copBId) as copNumber  " +
                " FROM cooperation " +
                " WHERE copSId=?";
        Integer count=Db.queryInt(countSql,sId);
        jsonObject.put("copNumber",count);
        /**
         * 收藏人数 collectNumber
         * 是否收藏 isCollect
         * 是否代理 isCooperation
         * 前四个商品 sWares
         */
        String collectSql="SELECT SUM(cOwner) as collectNumber FROM `collect` WHERE cStore=? and cWare is NULL;";
        Integer collect=Db.queryInt(collectSql,sId);
        jsonObject.put("collectNumber",collect);

        System.out.println(jsonObject);
        return jsonObject;
    }
}

