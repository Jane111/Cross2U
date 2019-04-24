package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.Administrator;
import com.cross2u.user.model.Business;
import com.cross2u.user.model.Mainmanufacturer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.bouncycastle.util.Times;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceZ {
    public boolean hasAccount(String phone) {
        String sql="select aId from administrator where aAccount=?";
        List<Administrator> administrators=Administrator.dao.find(sql,phone);
        System.out.println("???"+administrators.size());
        return administrators.size()==1?true:false;
    }

    public JSONObject loginIn(String phone, String password) {
        String sql="select * from administrator where aAccount=?";
        Administrator administrator=Administrator.dao.findFirst(sql,phone);
        String aPassword=administrator.getAPassword();
        Integer aPostion=administrator.getAPostion();
        if (aPassword!=null&&aPassword.equals(password)){
            JSONObject object=new JSONObject();
            object.put("position",aPostion);
            object.put("aId",administrator.getAId());
            return object;
        }
        else {
            return null;
        }
    }

    public boolean isForbidden(String phone) {//true =已禁用
        String sql="select aStatus from administrator where aAccount=? and aStatus!=0";
        return Db.queryInt(sql,phone)==0?true:false;
    }

    public JSONArray showcheckM(String mmStatus) {//显示待审核M列表
        JSONArray result=new JSONArray();
        String sql="SELECT mmId,mmPhone,mmEmail,mmCompany,mmOwner,mmIdNumber,mmMajorBusiness,mmCompanyPlace " +
                "from mainmanufacturer " +
                "WHERE mainmanufacturer.mmStatus=? ";
        List<Mainmanufacturer> mainmanufacturers=Mainmanufacturer.dao.find(sql,mmStatus);
        for (Mainmanufacturer mainmanufacturer:mainmanufacturers){
            JSONObject object=new JSONObject();
            object.put("mmId",mainmanufacturer.getMmId());
            String sName=getSNameByMId(mainmanufacturer.getMmId());
            object.put("sName",sName);
            object.put("mmPhone",mainmanufacturer.getMmPhone());
            object.put("mmEmail",mainmanufacturer.getMmEmail());
            object.put("mmCompany",mainmanufacturer.getMmCompany());
            object.put("mmOwner",mainmanufacturer.getMmOwner());
            object.put("mmIdNumber",mainmanufacturer.getMmIdNumber());
            if(mainmanufacturer.getMmMajorBusiness()!=null&&!mainmanufacturer.getMmMajorBusiness().equals("")){
                String mainMajorId=mainmanufacturer.getMmMajorBusiness().toString();
                String mainMajor=getMainBusiness(new BigInteger(mainMajorId));
                object.put("mmMajorBusiness",mainMajor);
            }
            else {
                object.put("mmMajorBusiness","未选");
            }

            object.put("mmCompanyPlace",mainmanufacturer.getMmCompanyPlace());
            if (!mmStatus.equals("2")){
                String aOperateTime=getMMAOperateTime(mainmanufacturer.getMmId());
                object.put("mmAOperateTime",aOperateTime);
            }
            result.add(object);
        }
        return result;
    }

    private String getSNameByMId(BigInteger mmId) {
        String sql="SELECT sName " +
                "from store " +
                "WHERE sMmId=?";
        String sName=Db.queryStr(sql,mmId);
        return sName;
    }

    //显示待审核M详细信息
    public JSONObject showcheckMDetail(String mmId,String mmStatus) {
        String sql="SELECT mmId,mmPhone,mmEmail,mmCompany,mmCompanyPlace,mmOwner," +
                "mmIdNumber,mmMajorBusiness,mmtype,mmLicence,mmIdUpImage,mmIdDownImage " +
                "from mainmanufacturer " +
                "WHERE mmId=? and mmStatus=?";
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findFirst(sql,mmId,mmStatus);
        JSONObject object=new JSONObject();
        BigInteger id=mainmanufacturer.getMmId();
        String sName=getSNameByMId(id);
        object.put("sName",sName);
        object.put("mmPhone",mainmanufacturer.getMmPhone());
        object.put("mmEmail",mainmanufacturer.getMmEmail());
        object.put("mmCompany",mainmanufacturer.getMmCompany());
        object.put("mmCompanyPlace",mainmanufacturer.getMmCompanyPlace());
        object.put("mmOwner",mainmanufacturer.getMmOwner());
        object.put("mmIdNumber",mainmanufacturer.getMmIdNumber());
        String mainMajorId=mainmanufacturer.getMmMajorBusiness().toString();
        String mainMajor=getMainBusiness(new BigInteger(mainMajorId));
        object.put("mmMajorBusiness",mainMajor);
        object.put("mmtype",mainmanufacturer.getMmtype());
        object.put("mmLicence",mainmanufacturer.getMmLicence());
        object.put("mmIdUpImage",mainmanufacturer.getMmIdUpImage());
        object.put("mmIdDownImage",mainmanufacturer.getMmIdDownImage());
        if (!mmStatus.equals("2")){
            String aOperateTime=getMMAOperateTime(mainmanufacturer.getMmId());
            object.put("mmAOperateTime",aOperateTime);
        }
        return object;
    }

    //获取B操作时间
    private String getMMAOperateTime(BigInteger mmId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql="select mmAOperateTime from mainmanufacturer where mmId=?";
        Timestamp mmAopTime=Db.queryTimestamp(sql,mmId);
        if (mmAopTime==null)
            return null;

        String time=sdf.format(mmAopTime);
        System.out.println(mmAopTime+"----"+time);
        return time;
    }

    //通过M的注册
    public boolean UpdateMStatus(String mmId) {
        Date now=new Date();
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findById(mmId);
        mainmanufacturer.setMmStatus(1);//1-在用
        mainmanufacturer.setMmAOpeateTime(now);
        BigInteger sId=getSIdByMId(mmId);
        String sql="UPDATE `manufacturer` set mStatus=1 where mStore=1";
        return mainmanufacturer.update()&&Db.update(sql)>=1;
    }

    private BigInteger getSIdByMId(String mId){
        String sql="select sId from store where sMmId=?";
        String sId=Db.queryStr(sql,mId);
        return new BigInteger(sId);
    }


    //退回M的申请
    public boolean UpdateMText(String mmId, String mmFialReasonSelect, String mmFailReasonText) {
        Date now=new Date();
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findById(mmId);
        mainmanufacturer.setMmStatus(3);//审核失败
        mainmanufacturer.setMmFialReasonSelect(new Integer(mmFialReasonSelect));
        mainmanufacturer.setMmFailReasonText(mmFailReasonText);
        mainmanufacturer.setMmAOpeateTime(now);
        return mainmanufacturer.update();
    }

    /*//显示退回M的列表
    public JSONArray showReturnM(String mmStatus) {
        String sql="SELECT mmId,mmPhone,mmEmail,mmCompany,mmOwner,mmIdNumber,mmMajorBusiness,mmCompanyPlace " +
                "from mainmanufacturer " +
                "WHERE mainmanufacturer.mmStatus=? ";
        List<Mainmanufacturer> mainmanufacturers=Mainmanufacturer.dao.find(sql,mmStatus);
        JSONArray result=new JSONArray();
        for (Mainmanufacturer mainmanufacturer:mainmanufacturers){
            JSONObject object=new JSONObject();
            object.put("mmId",mainmanufacturer.getMmId());
            object.put("mmPhone",mainmanufacturer.getMmPhone());
            object.put("mmEmail",mainmanufacturer.getMmEmail());
            object.put("mmCompany",mainmanufacturer.getMmCompany());
            object.put("mmOwner",mainmanufacturer.getMmOwner());
            object.put("mmIdNumber",mainmanufacturer.getMmIdNumber());
            object.put("mmMajorBusiness",mainmanufacturer.getMmMajorBusiness());
            object.put("mmCompanyPlace",mainmanufacturer.getMmCompanyPlace());

            result.add(object);
        }
        return result;
    }*/
    //显示退回M的详情
    public JSONObject showReturnMDetail(String mmId) {
        String sql="SELECT mmPhone,mmEmail,mmCompany,mmCompanyPlace,mmOwner,mmIdNumber,mmMajorBusiness," +
                "mmtype,mmLicence,mmIdUpImage,mmIdDownImage,mmFialReasonSelect,mmFailReasonText " +
                "from mainmanufacturer " +
                "WHERE mmId=? and mmStatus=3";
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findFirst(sql,mmId);
        JSONObject object=new JSONObject();
        object.put("mmPhone",mainmanufacturer.getMmPhone());
        object.put("mmEmail",mainmanufacturer.getMmEmail());
        object.put("mmCompany",mainmanufacturer.getMmCompany());
        object.put("mmOwner",mainmanufacturer.getMmOwner());
        object.put("mmIdNumber",mainmanufacturer.getMmIdNumber());
        String mainManu=mainmanufacturer.getMmMajorBusiness().toString();
        String mmMajorBusiness=getMainBusiness(new BigInteger(mainManu));
        object.put("mmMajorBusiness",mmMajorBusiness);
        object.put("mmtype",mainmanufacturer.getMmtype());
        object.put("mmLicence",mainmanufacturer.getMmLicence());
        object.put("mmIdUpImage",mainmanufacturer.getMmIdUpImage());
        object.put("mmIdDownImage",mainmanufacturer.getMmIdDownImage());
        object.put("mmFialReasonSelect",mainmanufacturer.getMmFialReasonSelect());
        object.put("mmFailReasonText",mainmanufacturer.getMmFailReasonText());
        return object;
    }




    //显示待审核B的列表
    public JSONArray showcheckB(String bStatus) {
        String sql="SELECT bId,bName,bPhone,bEmail,bIdNumber,bMainBusiness,bOtherStore1 " +
                "FROM business " +
                "WHERE bStatus=? ";
        JSONArray array=new JSONArray();
        List<Business> businesses=Business.dao.find(sql,bStatus);
        for (Business business:businesses){
            JSONObject object=new JSONObject();
            object.put("bId",business.getBId());
            object.put("bPhone",business.getBPhone());
            object.put("bEmail",business.getBEmail());
            object.put("bIdNumber",business.getBIdNumber());
            String name=getMainBusiness(business.getBMainBusiness());
            object.put("bMainBusiness",name);
            object.put("bName",business.getBName());
            object.put("bOtherStore1",business.getBOtherStore1());
            if (!bStatus.equals("2")){
                String bAOperateTime=getBAOperateTime(business.getBId());
                object.put("bAOperateTime",bAOperateTime);
            }
            array.add(object);
        }
        return array;
    }

    //获取B操作时间
    private String getBAOperateTime(BigInteger bId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql="select bAOperateTime from business where bId=?";
        Timestamp bAopTime=Db.queryTimestamp(sql,bId);
        if (bAopTime!=null){
            String time=sdf.format(bAopTime);
            System.out.println(bAopTime+"----"+time);
            return time;
        }
        return null;
    }


    //显示申请B的详情
    public JSONObject showcheckBDetail(String bId) {
        String sql="SELECT bId,bName,bPhone,bEmail,bIdNumber,bMainBusiness,bIdUpImage,bIdDownImage,bOtherPlat1,bOtherStore1,bOtherStatus1 " +
                "FROM business " +
                "WHERE  bId=? ";
        Business business=Business.dao.findFirst(sql,bId);
        JSONObject object=new JSONObject();
        object.put("bId",business.getBId());
        object.put("bNane",business.getBName());
        object.put("bPhone",business.getBPhone());
        object.put("bEmail",business.getBEmail());
        object.put("bIdNumber",business.getBIdNumber());
        String bMainBusiness=getMainBusiness(business.getBMainBusiness());
        object.put("bMainBusiness",bMainBusiness);
        object.put("bIdUpImage",business.getBIdNumber());
        object.put("bOtherPlat1",business.getBOtherPlat1());
        object.put("bOtherStore1",business.getBOtherStore1());
        object.put("bOtherStatus1",business.getBOtherStatus1());
        String bAOperateTime=getBAOperateTime(business.getBId());
        object.put("bAOperateTime",bAOperateTime);
        return object;
    }

    //根据id 找到主营行业
    private String getMainBusiness(BigInteger bMainBusiness) {
        String sql="select ctName from category where ctId=? and ctRank=0";
        String name=Db.queryStr(sql,bMainBusiness);
        return name;
    }

    //通过零售商B
    public boolean UpdateBStatus(String bId) {
        Date now=new Date();
        Business business=Business.dao.findById(bId);
        business.setBStatus(1);//审核通过 账号在用
        business.setBAOperateTime(now);
        return business.update();
    }

    //退回零售商B
    public boolean UpdateBText(String bId, String bFialReasonSelect, String bFailReasonText) {
        Business business=Business.dao.findById(bId);
        Date now=new Date();
        business.setBStatus(3);//审核通过 账号审核失败
        business.setBFialReasonSelect(new Integer(bFialReasonSelect));
        business.setBFailReasonText(bFailReasonText);
        business.setBAOperateTime(now);
        return business.update();
    }

    //被退回B的详情
    public JSONObject showReturnBDetail(String bId) {
        String sql="SELECT bId,bName,bPhone,bEmail,bIdNumber,bMainBusiness,bIdUpImage,bIdDownImage,bOtherPlat1,bOtherStore1,bOtherStatus1," +
                "bFialReasonSelect,bFailReasonText " +
                "FROM business " +
                "WHERE bId=? ";
        Business business=Business.dao.findFirst(sql,bId);
        JSONObject object=new JSONObject();
        object.put("bId",business.getBId());
        object.put("bName",business.getBName());
        object.put("bPhone",business.getBPhone());
        object.put("bEmail",business.getBEmail());
        object.put("bIdNumber",business.getBIdNumber());
        String bMainBusiness=getMainBusiness(business.getBMainBusiness());
        object.put("bMainBusiness",bMainBusiness);
        object.put("bIdUpImage",business.getBIdNumber());
        object.put("bOtherPlat1",business.getBOtherPlat1());
        object.put("bOtherStore1",business.getBOtherStore1());
        object.put("bOtherStatus1",business.getBOtherStatus1());
        object.put("bFialReasonSelect",business.getBFialReasonSelect());
        object.put("bFailReasonText",business.getBFailReasonText());
        String bAOperateTime=getBAOperateTime(business.getBId());
        object.put("bAOperateTime",bAOperateTime);
        return object;
    }

    //显示保证金列表
    public JSONArray showEnsureMoneyList(String degreeStr) {
        String sql="SELECT sId,sMmId,sName,sCreateTime,sScore " +
                "from store INNER JOIN mainmanufacturer ON mmStore=sId ";
        Integer degree=new Integer(degreeStr);
        Integer score=degree*100;
        if (!degreeStr.equals("0")){
            sql=sql+" where sScore>="+(score-100)+" and sScore<"+score;
        }
        JSONArray array=new JSONArray();
        List<Record> moneyList=Db.find(sql);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Record money:moneyList){
            JSONObject object=new JSONObject();
            object.put("sId",money.getBigInteger("sId"));
            Float leftMoney=getLeftEnsureMoney(money.getBigInteger("sId"));
            object.put("leftEnsureMoney",leftMoney);
            object.put("sMmId",money.get("sMmId"));
            object.put("sName",money.get("sName"));
            Timestamp sCreateTime=money.getTimestamp("sCreateTime");
            String sCreateTimeStr=sdf.format(sCreateTime);
            object.put("sCreateTime",sCreateTimeStr);
            object.put("mmEnsureMoney",money.get("mmEnsureMoney"));
            object.put("mmEnsureMoneyUnit",money.get("mmEnsureMoneyUnit"));
            Integer sScore=money.get("sScore");
            Integer sRank=sScore/100+1;//0-100 一级
            object.put("sRank",sRank);//等级
            array.add(object);
        }
        return array;
    }

    //获取店铺剩余保证金的金额
    private Float getLeftEnsureMoney(BigInteger sId) {
        Float result=0.0f;
        String sql="select sbMoney,sbBalance " +
                " from storebill " +
                "where sbSId=? ";
        List<Record> list=Db.find(sql,sId);
        for (Record record:list)
        {
            Float sbMoney=record.getFloat("sbMoney");
            if (record.get("sbBalance").toString().equals("1"))//缴纳
            {
                result=result+sbMoney;
            }
            else if (record.get("sbBalance").toString().equals("0"))//扣除
            {
                result=result-sbMoney;
            }
        }
        System.out.println("sid"+sId+"left"+result);
        return result;
    }

    //查看M的积分
    public JSONArray showMScore(String degreeStr) {
        String sql="SELECT sId,sName,sMmId,sName,sCreateTime,sScore " +
                "FROM store ";
        Integer degree=new Integer(degreeStr);
        Integer score=degree*100;
        List<Record> list=Db.find(sql);
        if (!degreeStr.equals("0")){
            sql=sql+" where sScore>=?  and sScore<=?";
            list=Db.find(sql,(score-100),score);
        }
        JSONArray array=new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Record record:list){
            JSONObject object=new JSONObject();
            object.put("sId",record.get("sId"));
            object.put("sMmId",record.get("sMmId"));
            object.put("sName",record.get("sName"));
            Timestamp sCreateTime=record.getTimestamp("sCreateTime");
            String sCreateTimeStr=sdf.format(sCreateTime);
            object.put("sCreateTime",sCreateTimeStr);
            object.put("sScore",record.get("sScore"));
            Integer sScore=record.getInt("sScore");
            Integer sRank=sScore/100+1;
            object.put("sRank",sRank);
            array.add(object);
        }
        return array;
    }

    public JSONArray showBScore(String degreeStr) {
        String sql="SELECT bId,bName,bCreateTime,bScore,bRank " +
                "from business ";
        Integer degree=new Integer(degreeStr);
        Integer score=degree*100;
        if (!degreeStr.equals("0")){
            sql=sql+" where bScore>="+(score-100)+" and bScore<="+score;
        }
        List<Business> list=Business.dao.find(sql);
        JSONArray array=new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Business business:list){
            JSONObject object=new JSONObject();
            object.put("bName",business.getBName());
            Timestamp createTime=business.getBCreateTime();
            String bCreateTime=sdf.format(createTime);
            System.out.println("======="+createTime+"-----bCreateTime:"+bCreateTime+"=========");
            object.put("bCreateTime",bCreateTime);
            object.put("bScore",business.getBScore());
            Integer bRank=updateBRank(business.getBId());
            object.put("bRank",bRank);

            array.add(object);
        }
        return array;
    }
    //更新b的等级
    private Integer updateBRank(BigInteger bId) {
        Business business=Business.dao.findById(bId);
        if (business.getBScore()==null){
            business.setBScore(0);
            business.update();
            return 1;
        }
        Integer bRank=business.getBScore()/100+1;
        business.setBRank(bRank);
        business.update();
        return bRank;
    }

    public JSONArray showEnsureMoneyDetail(String sId) {
        String sql="select sbId,sbInfo,sbMoney,sbBalance,sbNumber,sbTime,sbCreateTime " +
                " from storebill " +
                "where sbSId=? ";
        List<Record> list=Db.find(sql,sId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONArray array=new JSONArray();
        for (Record record:list){
            JSONObject object=new JSONObject();
            object.put("sbId",record.get("sbId"));
            object.put("sbInfo",record.get("sbInfo"));
            object.put("sbMoney",record.get("sbMoney"));
            if (record.get("sbBalance").toString().equals("1")){
                object.put("sbBalance","支付");
            }
            else {
                object.put("sbBalance","支出");
            }
            object.put("sbNumber",record.get("sbNumber"));//订单编号
            if (record.get("sbTime")!=null){
                Timestamp sbTime=record.get("sbTime");
                String subStr=sdf.format(sbTime);
                object.put("sbTime",subStr);
            }
            else {
                Timestamp sbCreate=record.get("sbCreateTime");
                String sbCr=sdf.format(sbCreate);
                object.put("sbTime",sbCr);
            }

            array.add(object);
        }
        System.out.println(array);
        return array;
    }

    public Mainmanufacturer getMainManufactureById(String mmId) {
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findById(mmId);
        return mainmanufacturer;
    }

    public Business getBusinessById(String bId) {
        Business business=Business.dao.findById(bId);
        return business;
    }
}
