package com.cross2u.manage.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.manage.model.Publicinfo;
import com.cross2u.manage.util.Constant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ManageServiceZ {

    //显示公告
    public List<Publicinfo>  showPublicInfo() {
        String sql="SELECT piId,piTitle,piContent,piCreateTime FROM publicinfo";
        List<Publicinfo> publicinfos= Publicinfo.dao.find(sql);
        JSONArray array=new JSONArray();
        //for (Publicinfo)
        return publicinfos;
    }

    /**
     * 显示待审核B列表
     * @param
     * @return

    public JSONArray showUncheckB(String bStatus) {
        JSONArray array=new JSONArray();
        String sql="select bId,bPhone,bEmail,bIdNumber,bMainBusiness from business where bStatus=2";
        List<Record> list= Db.find(sql);
        for (int i=0;i<array.size();i++){
            Record record=list.get(i);
            JSONObject object=new JSONObject();
            object.put("bId",record.get("bId"));
            object.put("bPhone",record.get("bPhone"));
            object.put("bEmail",record.get("bEmail"));
            object.put("bIdNumber",record.get("bIdNumber"));
            object.put("bMainBusiness",record.get("bMainBusiness"));

            array.add(object);
        }
        return array;
    }*/

    /*public boolean UpdateBStatus(String bId) {
        String sql="update business SET bStatus=1 WHERE bId=?";
        return Db.update(sql,bId)==1;
    }

    public boolean UpdateBText(String bId, String bFialReasonSelect, String bFailReasonText) {
        String sql="update business Set bStatus=3 ,bFialReasonSelect=?,bFailReasonText=? where bId=?";
        return Db.update(sql,bFialReasonSelect,bFailReasonText,bId)==1;
    }*/

    //计算待审核M的个数
    public Integer countM() {
        String sql="SELECT COUNT(mmId) " +
                "from mainmanufacturer " +
                "WHERE mmStatus="+ Constant.M_WAIT;
        Integer countM=Db.queryInt(sql);
        return countM;
    }
    //计算待审核B的个数
    public Integer countB() {
        String sql="SELECT COUNT(bId) " +
                "from business " +
                "WHERE bStatus= "+Constant.B_WAIT;
        Integer countB=Db.queryInt(sql);
        return countB;
    }

    //计算待审核异常商品数目
    public Integer countWare() {
        String sql="SELECT COUNT(*) " +
                "from abnormalgoodsinfo " +
                "WHERE agiResult= "+Constant.ABGOODS_WAIT;
        Integer countWare=Db.queryInt(sql);
        return countWare;
    }

    //计算待审核异常店铺数目
    public Integer countStore() {
        String sql="SELECT COUNT(*) " +
                "from abnormalminfo " +
                "WHERE amiResult="+Constant.ABS_WAIT;
        Integer countStore=Db.queryInt(sql);
        return countStore;
    }

    public Integer countEval() {
        String sql="SELECT count(*) " +
                "from abevalreport " +
                "where aerState=0 ";
        Integer countEval=Db.queryInt(sql);
        return countEval;
    }


}
