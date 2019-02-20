package com.cross2u.user.service;

import com.cross2u.user.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class BusinessServiceZ {
    /**
     * 删除浏览记录 真删
     * @param bsrBusiness
     * @return
     */
    public boolean deleteSearchRecord(String bsrBusiness) {
        return Browserecord.dao.deleteById(bsrBusiness);
    }

    public boolean addBusiness(Business business) {
        business.setBStatus(2);//待审核状态
        business.setBRank(1);//等级 一级初始等级
        business.setBScore(0);//信誉分数

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
        String sql="select sName,sPhoto,sDescribe,sScore,mmName,mmLogo,copBId,count(*) as copNumber " +
                "from (store INNER JOIN mainmanufacturer on store.sId=mainmanufacturer.mmStore) INNER JOIN cooperation on store.sId=cooperation.copSId " +
                "where sId=? and copState =1";
        Record record= Db.findFirst(sql,new BigInteger(sId));
        return record;
    }

    public Record showStoreWare(String wStore) {
        String sql=" SELECT  wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
                " from ware INNER JOIN indent on ware.wId=indent.inWare " +
                " where ware.wStore=? ";
        Record ware=Db.findFirst(sql,new BigInteger(wStore));
        return ware;
    }

    public List<Record> showStoreClass(String wStore) {
        String sql1="select wfdId,wfdName,wfdSort,wsdId,wsdName,wsdImg,wsdSort " +
                "from warefdispatch INNER JOIN waresdispatch ON warefdispatch.wfdId=waresdispatch.wsdWFDId " +
                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort,wsdSort";
        List<Record> fsclass=Db.find(sql1,new BigInteger(wStore));//有子类的父类
        String sql2="select wfdId,wfdName,wfdSort " +
                "from warefdispatch  " +
                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort";
        List<Record> fclass=Db.find(sql2,new BigInteger(wStore));//两者叠加
        if (fsclass.addAll(fclass)){
            return fsclass;
        }
        return null;
    }

    public List<Record> showStoreFClassWare(String wbWFDId) {
        String sql="select wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
                "from (ware INNER JOIN indent on ware.wId=indent.inWare) INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
                "where warebelong.wbWFDId=?";
        List<Record> fWare=Db.find(sql,new BigInteger(wbWFDId));
        return fWare;
    }

    public List<Record> showStoreSClassWare(String wbWSDId) {
        String sql="select wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
                "from (ware INNER JOIN indent on ware.wId=indent.inWare) INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
                "where warebelong.wbWSDId=?";
        List<Record> sWare=Db.find(sql,new BigInteger(wbWSDId));
        return sWare;
    }

    public List<Publicinfo> showPublicInfo() {
        String sql="SELECT piId,piTitle,piContent,piCreateTime FROM PublicInfo;";
        List<Publicinfo> publicinfos=Publicinfo.dao.find(sql);
        return publicinfos;
    }

    public List<Record> showBrowseRecord(String bid) {
        String sql="SELECT brId,brWare,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) aswMonthSale " +
                "from (browserecord INNER JOIN ware on browserecord.brWare=ware.wId) INNER JOIN indent on ware.wId = indent.inWare " +
                "where browserecord.brOwner=? and browserecord.brIsDelete=1";
        List<Record> browseRecords=Db.find(sql,new BigInteger(bid));
        return browseRecords;
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
        String sql="SELECT cId,cWare,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wMonthSale " +
                " FROM (collect INNER JOIN ware on collect.cWare=ware.wId) INNER JOIN indent on ware.wId=indent.inWare " +
                " WHERE collect.cOwner=?";
        List<Record> collectwares=Db.find(sql,bId);
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

    public boolean deleteCop(String copId) {
        Cooperation cooperation=Cooperation.dao.findById(copId);
        cooperation.setCopState(2);//终止合作
        return cooperation.save();
    }


}

