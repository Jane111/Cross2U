
package com.cross2u.user.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.Mainmanufacturer;
import com.cross2u.user.util.Constant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Service
public class ManufactureServiceZ {

    @Autowired
    RestTemplate restTemplate;
    //注册第一步
    public BigInteger first(String mmEmail, String mmPhone, String mmPassword) {
        Mainmanufacturer mainmanufacturer=new Mainmanufacturer();
        mainmanufacturer.setMmEmail(mmEmail);
        mainmanufacturer.setMmPhone(mmPhone);
        mainmanufacturer.setMmPassword(mmPassword);
        mainmanufacturer.setMmStatus(2);
        if(mainmanufacturer.save())
        {
            return mainmanufacturer.getMmId();
        }
        return new BigInteger("0");
    }

    public boolean second(Mainmanufacturer mainmanufacturer) {
        return mainmanufacturer.update();
    }

    public Mainmanufacturer findById(String mmId) {
        return Mainmanufacturer.dao.findById(mmId);
    }

    public boolean third(String mmId, String sName, String mmLogo, String mmMajorBusiness, String mmFixedNum,String mmName) {
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findById(new BigInteger(mmId));
        mainmanufacturer.setMmLogo(mmLogo);
        mainmanufacturer.setMmName(mmName);
        BigInteger sId=saveStore(mainmanufacturer.getMmId(),sName,mmLogo);
        if (sId!=null){
            mainmanufacturer.setMmStore(sId);//更新对应的店铺id
        }
        mainmanufacturer.setMmFixedNum(mmFixedNum);
        mainmanufacturer.setMmMajorBusiness(new Long(mmMajorBusiness));

        String sql="Insert into manufacturer set mStore=? , mStatus=0, mRank=1, mPhone=?,mName=?,mPassword=?," +
                "mManageWare=1,mManageIndent=1,mManageMessage=1,mManageClient=1";//0是禁用
        Db.update(sql,sId,mainmanufacturer.getMmPhone(),mainmanufacturer.getMmName(),mainmanufacturer.getMmPassword());
        return sId!=null&&mainmanufacturer.update();
    }

    public BigInteger saveStore(BigInteger mmId,String sName,String mmLogo){
        String sql="insert into store set sStatus=0,sScore=0,sDirectMoney=1,sReduceInventory=0,sMmId=?,sName=? ,sAgentDegree=1";
        if(Db.update(sql,mmId,sName,mmLogo)==1)
        {
            String selectSql="select sId from store where sMmId=?";
            BigInteger id=new BigInteger(Db.query(selectSql,mmId).toString());
            return id;
        }
        return null;
    }

    public JSONObject mainLogin(String mmPhone, String mmPassword) {
        String selectSql="select * from mainmanufacturer where mmPhone=?";
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findFirst(selectSql,mmPhone);
        if (mainmanufacturer==null) return null;//不存在

        System.out.println("phone"+mmPhone+"pass"+mmPassword+"mmPa"+mainmanufacturer.getMmPassword());
        if(mainmanufacturer.getMmPassword().equals(mmPassword))
        {
        String sql="SELECT mmStatus ,mmName,mmLogo,sId,sStatus,sName,sScore " +
        " FROM mainmanufacturer INNER JOIN store on mainmanufacturer.mmStore=store.sId " +
        " WHERE mainmanufacturer.mmId=?";
        Record record= Db.findFirst(sql,mainmanufacturer.getMmId());
        if (record==null) return null;
        JSONObject object=new JSONObject();
        object.put("mmStatus",record.get("mmStatus"));
        object.put("mmName",record.get("mmName"));
        object.put("mmLogo",record.get("mmLogo"));
        object.put("sId",record.get("sId"));
        object.put("sStatus",record.get("sStatus"));
        object.put("sScore",record.get("sScore"));
        return object;
        }
        return null;
    }

    public JSONObject subLogin(String mMainManuPhone, String mPhone, String mPassword) {
        String selectsql="select * from mainmanufacturer where mmPhone=?";
        Mainmanufacturer mainInfo=Mainmanufacturer.dao.findFirst(selectsql,mMainManuPhone);
        if (mainInfo==null)return null;
        String sql="SELECT mStore,mPhone,mPassword,mManageWare,mManageIndent,mManageMessage,mManageClient" +
        " FROM manufacturer " +
        " WHERE mStore=? AND mPhone like '"+mPhone+"'";
        Record manufacturer=Db.findFirst(sql,mainInfo.getMmStore());
        if (manufacturer==null) return null;
        if (manufacturer.get("mPassword").equals(mPassword)){
            String sql2="SELECT mmStatus ,mmName,mmLogo,sId,sStatus,sName,sScore " +
            " FROM mainmanufacturer INNER JOIN store on mainmanufacturer.mmStore=store.sId " +
            " WHERE mainmanufacturer.mmId=?";
            Record info=Db.findFirst(sql2,mainInfo.getMmId());
            if (info==null) return null;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("mManageWare",manufacturer.getInt("mManageWare "));
            jsonObject.put("mManageIndent",manufacturer.getInt("mManageIndent "));
            jsonObject.put("mManageMessage",manufacturer.getInt("mManageMessage "));
            jsonObject.put("mManageClient",manufacturer.getInt("mManageClient "));
            jsonObject.put("mmStatus",info.get("mmStatus"));jsonObject.put("mmName",info.get("mmName"));
            jsonObject.put("mmLogo",info.get("mmLogo"));jsonObject.put("sName",info.get("sName"));
            jsonObject.put("sId",info.get("sId"));jsonObject.put("sStatus",info.get("sStatus"));
            jsonObject.put("sScore",info.get("sScore"));
            return jsonObject;
        }
        else {
            return null;
        }
    }

    public boolean havePhone(String mmPhone) {
        String sql="select * from mainmanufacturer where mmPhone = ? ";
        return Mainmanufacturer.dao.find(sql,mmPhone).size()>=1;
    }

    public boolean haveEmail(String mmEmail) {
        String sql="select * from mainmanufacturer where mmEmail =? ";
        return Mainmanufacturer.dao.find(sql,mmEmail).size()>=1;
    }

    public Integer isForbiden(String mPhone){
        String selectSql="select * from manufacturer where mPhone=?";
        Record m=Db.findFirst(selectSql,mPhone);
        if (m==null) return 0;//不存在

        if (m.get("mRank").equals("0")&&m.get("mStatus").equals("0")){//主账号且被禁用
            return 1;
        }
        else if (m.get("mRank").equals("1")&&m.get("mStatus").equals("0")){//子账号且被禁用
            return 2;
        }
        return 3;
    }

    public JSONObject loginIn(String mPhone, String mPassword) {
        String selectSql="select * from manufacturer where mPhone=?";
        Record m=Db.findFirst(selectSql,mPhone);
        if (m==null) return null;//不存在

        System.out.println("phone"+mPhone+"pass"+mPassword+"mPa"+m.get("mPassword"));
        String mRank=m.get("mRank").toString();
        if(m.get("mPassword").equals(mPassword))
        {
            String sql="SELECT mStatus,mName,mmLogo,sId,sStatus,sName,sScore,mManageWare,mManageIndent,mManageMessage,mManageClient " +
                    "FROM (manufacturer INNER JOIN store on manufacturer.mStore=store.sId) INNER JOIN mainmanufacturer ON mmStore=mStore  " +
                    "WHERE manufacturer.mId=? ";
            Record record= Db.findFirst(sql,m.getBigInteger("mId"));
            if (record==null) return null;
            JSONObject object=new JSONObject();
            object.put("mRank",mRank);//是否为主账号
            object.put("mStatus",record.get("mStatus"));
            object.put("mName",record.get("mName"));
            object.put("mmLogo",record.get("mmLogo"));
            object.put("sId",record.get("sId"));
            object.put("sStatus",record.get("sStatus"));
            object.put("sName",record.get("sName"));
            object.put("sScore",record.get("sScore"));
            Integer sRank=record.getInt("sScore")/100+1;
            object.put("sRank",sRank);
            object.put("mManageWare",record.get("mManageWare"));
            object.put("mManageIndent",record.get("mManageIndent"));
            object.put("mManageMessage",record.get("mManageMessage"));
            object.put("mManageClient",record.get("mManageClient"));
            return object;
        }
        return null;
    }
}

