package com.cross2u.store.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.store.model.*;
import com.cross2u.store.util.Constant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class StoreServiceZ {

    public List<Warefdispatch> showFDispatch(String wfdSId) {
        String sql="SELECT wfdName,wfdId " +
                " FROM warefdispatch " +
                " WHERE wfdSId=? ORDER BY wfdSort ";
        List<Warefdispatch> warefdispatches=Warefdispatch.dao.find(sql,wfdSId);
        return warefdispatches;
    }

    public List<Waresdispatch> showSDispatch(String wfdId) {
        String sql="SELECT wsdName,wsdId,wsdImg " +
                " from waresdispatch " +
                " WHERE wsdWFDId=? ORDER BY wsdSort";
        List<Waresdispatch> waresdispatches= Waresdispatch.dao.find(sql,wfdId);
        return waresdispatches;
    }

    public boolean addOneWareBelong(String wId,String wbWFDId,String wbWSDId) {
        Warebelong warebelong=new Warebelong();
        System.out.println(wId);
        warebelong.setWbWId(Long.valueOf(wId));
        warebelong.setWbWFDId(Long.valueOf(wbWFDId));
        if (!wbWSDId.equals("")){
            warebelong.setWbWSDId(Long.valueOf(wbWSDId));
        }
        else {
            warebelong.setWbWSDId(null);
        }
        return warebelong.save();
    }

    public boolean updateWareBelong(String wbId,String wbWFDId,String wbWSDId) {
        Warebelong warebelong=Warebelong.dao.findById(wbId);
        warebelong.setWbWFDId(Long.valueOf(wbWFDId));
        if (!wbWSDId.equals("")){
            warebelong.setWbWSDId(Long.valueOf(wbWSDId));
        }
        else {
            warebelong.setWbWSDId(null);
        }
        return warebelong.update();
    }

    public boolean deleteCop(String copId) {
        Cooperation cooperation=Cooperation.dao.findById(copId);
        cooperation.setCopState(2);//终止合作
        return cooperation.update();
    }

    public JSONArray showStoreClass(String wStore) {
        JSONArray array=new JSONArray();
        String sql2="select wfdId,wfdName,wfdSort " +
                "from warefdispatch  " +
                "WHERE warefdispatch.wfdSId=? ORDER BY wfdSort";
        List<Warefdispatch> fclass= Warefdispatch.dao.find(sql2,new BigInteger(wStore));//父类

        String sonSql="select wsdId,wsdName,wsdImg,wsdSort " +
                "from waresdispatch  " +
                "WHERE wsdWFDId=? ORDER BY wsdSort";

        for (Warefdispatch record:fclass){//找子类
            JSONObject object=new JSONObject();
            object.put("wfdId",record.getWfdId());
            object.put("wfdName",record.getWfdName());
            object.put("wfdSort",record.getWfdSort());
            JSONArray sonArray=new JSONArray();
            List<Waresdispatch> sonclass=Waresdispatch.dao.find(sonSql,record.getWfdId());//有子类的父类
            /*for (Waresdispatch son:sonclass){
                JSONObject object1=new JSONObject();
                object1.put("wsdId",son.getWsdId());
            }*/
            object.put("son",sonclass);
            array.add(object);
        }

        return array;
    }

    public JSONArray dispatchShowDispatchs(String sId) {
        JSONArray array=new JSONArray();
        String sql="SELECT wfdId,wfdName,wfdCreateTime " +
                " from warefdispatch " +
                " WHERE wfdSId=? ";
        List<Record> wfds=Db.find(sql,sId);
        String wfdWaresSql="SELECT COUNT(*) as wfdWares " +
                " from warebelong  " +
                "WHERE warebelong.wbWFDId=?";
        for (int i=0;i<wfds.size();i++){
            JSONObject object=new JSONObject();
            Record warefdispatch=wfds.get(i);
            Integer wfdWares=Db.queryInt(wfdWaresSql,warefdispatch.getBigInteger("wfdId"));//个数

            object.put("wfdId",warefdispatch.getBigInteger("wfdId"));
            object.put("wfdName",warefdispatch.getStr("wfdName"));
            object.put("wfdCreateTime",warefdispatch.getTimestamp("wfdCreateTime"));
            object.put("wfdWares",wfdWares);
            JSONArray sons=getSonDispatch(warefdispatch.getBigInteger("wfdId"));
            object.put("sons",sons);

            array.add(object);//
        }
        return array;
    }

    private JSONArray getSonDispatch(Object wfdId) {
        JSONArray sons=new JSONArray();
        String sql="SELECT wsdId,wsdName,wsdImg,wsdCreateTime " +
                " from waresdispatch " +
                " WHERE wsdWFDId=?";
        String wfdWaresSql="SELECT COUNT(*) as wsdWares " +
                "from warebelong " +
                "WHERE warebelong.wbWSDId=?";
        List<Waresdispatch> waresdispatches=Waresdispatch.dao.find(sql,wfdId);
        for (Waresdispatch waresdispatch:waresdispatches){
            JSONObject object=new JSONObject();
            Integer wsdWares=Db.queryInt(wfdWaresSql,waresdispatch.getWsdId());

            object.put("wsdId",waresdispatch.getWsdId());
            object.put("wsdName",waresdispatch.getWsdName());
            object.put("wsdImg",waresdispatch.getWsdImg());
            object.put("wsdCreateTime",waresdispatch.getWsdCreateTime());
            object.put("wsdWares",wsdWares);

            sons.add(object);
        }
        return sons;
    }

    public BigInteger dispatchAddFDispatchs(String sId, String wfdName,String wfdSort) {
        Warefdispatch warefdispatch=new Warefdispatch();
        warefdispatch.setWfdName(wfdName);
        warefdispatch.setWfdSId(new Long(sId));
        warefdispatch.setWfdSort(new Integer(wfdSort));
        if(warefdispatch.save()){
            return warefdispatch.getWfdId();
        }
        return null;
    }

    public BigInteger dispatchAddSDispatchs(String sId, String wfdId, String wsdName, String wsdImg, Integer wsdSort) {
        Waresdispatch waresdispatch=new Waresdispatch();
        waresdispatch.setWsdName(wsdName);
        waresdispatch.setWsdWFDId(new Long(wfdId));
        waresdispatch.setWsdImg(wsdImg);
        waresdispatch.setWsdSort(wsdSort);
        if (waresdispatch.save()){
            return waresdispatch.getWsdId();
        }
        return null;
    }

    public boolean changeWFDName(String wfdId, String wfdName) {
        Warefdispatch warefdispatch=Warefdispatch.dao.findById(new BigInteger(wfdId));
        warefdispatch.setWfdName(wfdName);
        return warefdispatch.update();
    }

    public boolean chageWSDName(String wsdId, String name,String wsdImg) {
        Waresdispatch waresdispatch=Waresdispatch.dao.findById(new BigInteger(wsdId));
        if (name!=null) waresdispatch.setWsdName(name);
        if (wsdImg!=null) waresdispatch.setWsdImg(wsdImg);
        return waresdispatch.update();
    }

    public boolean changeWSDImg(String wsdId, String wsdImg) {
        Waresdispatch waresdispatch=Waresdispatch.dao.findById(new BigInteger(wsdId));
        waresdispatch.setWsdImg(wsdImg);
        return waresdispatch.update();
    }

    public Boolean changeDispatchFLoc(String wfdId, String operation) {
        Warefdispatch thisone=Warefdispatch.dao.findById(wfdId);
        Integer sort=thisone.getWfdSort();
        String sql="SELECT * " +
                "from warefdispatch " +
                "WHERE wfdSId in ( SELECT wfdSId from warefdispatch WHERE wfdId=?) " +
                "ORDER BY wfdSort ";
        List<Warefdispatch> warefdispatches=Warefdispatch.dao.find(sql,wfdId);
        for (Warefdispatch warefdispatch:warefdispatches){
            if (operation.equals("0")&&(warefdispatch.getWfdSort()==sort-1))//上移
            {
                thisone.setWfdSort(sort-1);
                warefdispatch.setWfdSort(sort);
                return warefdispatch.update()&&thisone.update();
            }
            else if (operation.equals("1")&&(warefdispatch.getWfdSort()==sort+1)){//下移
                thisone.setWfdSort(sort+1);
                warefdispatch.setWfdSort(sort);
                return warefdispatch.update()&&thisone.update();
            }
        }
        return false;
    }

    public boolean changeDispatchSLoc(String wsdId, String operation) {
        Waresdispatch thisone=Waresdispatch.dao.findById(wsdId);
        Integer sort=thisone.getWsdSort();
        String sql="SELECT * " +
                "from waresdispatch " +
                "WHERE wsdWFDId in (SELECT wsdWFDId from waresdispatch WHERE wsdId =?) " +
                "ORDER BY wsdSort";
        List<Waresdispatch> waresdispatches=Waresdispatch.dao.find(sql,wsdId);
        for (Waresdispatch waresdispatch:waresdispatches){
            if(operation.equals("0")&&(waresdispatch.getWsdSort()==sort-1))
            {
                thisone.setWsdSort(sort-1);
                waresdispatch.setWsdSort(sort);
                return thisone.update()&&waresdispatch.update();
            }
            else if (operation.equals("1")&&(waresdispatch.getWsdSort()==sort+1)){
                thisone.setWsdSort(sort+1);
                waresdispatch.setWsdSort(sort);
                return thisone.update()&&waresdispatch.update();
            }
        }
        return false;
    }

    public boolean deleteWFD(String wfdId) {
        Boolean signal= changeWFDSort(wfdId);
        String sql="DELETE from warebelong WHERE wbWFDId=?";
        String sql2="DELETE from warefdispatch where wfdId=?";
        return  (Db.update(sql2,wfdId)==1)&&signal||(Db.update(sql,wfdId)>=0);
    }
    public boolean changeWFDSort(String wfdId){
        Warefdispatch thisone=Warefdispatch.dao.findById(wfdId);
        Integer sort=thisone.getWfdSort();
        String sql="SELECT * " +
                "from warefdispatch " +
                "WHERE wfdSId in ( SELECT wfdSId from warefdispatch WHERE wfdId=?) " +
                "ORDER BY wfdSort ";
        List<Warefdispatch> warefdispatches=Warefdispatch.dao.find(sql,wfdId);
        for (Warefdispatch warefdispatch:warefdispatches){
            if (warefdispatch.getWfdSort()>sort){
                int tempSort=warefdispatch.getWfdSort();
                warefdispatch.setWfdSort(tempSort-1);
                if(!warefdispatch.update()) return false;
            }
        }
        return true;
    }

    public boolean deleteWSD(String wsdId) {
        Boolean signal= changeWSDSort(wsdId);
        String sql="DELETE from warebelong WHERE wbWSDId=?";
        String sql2="DELETE from waresdispatch where wsdId=?";
        return  (Db.update(sql2,wsdId)==1)&&signal||(Db.update(sql,wsdId)>=0);
    }
    public boolean changeWSDSort(String wsdId){
        Waresdispatch thisone=Waresdispatch.dao.findById(wsdId);
        Integer sort=thisone.getWsdSort();
        String sql="SELECT * " +
                "from waresdispatch " +
                "WHERE wsdWFDId in (SELECT wsdWFDId from waresdispatch WHERE wsdId =?) " +
                "ORDER BY wsdSort";
        List<Waresdispatch> waresdispatches=Waresdispatch.dao.find(sql,wsdId);
        for (Waresdispatch waresdispatch:waresdispatches){
            if (waresdispatch.getWsdSort()>sort){
                int tempSort=waresdispatch.getWsdSort();
                waresdispatch.setWsdSort(tempSort-1);
                if(!waresdispatch.update()) return false;
            }
        }
        return true;
    }

    public JSONArray showApproving(String sId, String bRank) {
        JSONArray array=new JSONArray();
        String copSql="select copId, copBId,copCreateTime from cooperation where copSId=? and copState=0";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Cooperation> cops=Cooperation.dao.find(copSql,sId);
        for (Cooperation cop:cops){
            BigInteger bId=cop.getCopBId();
            JSONObject object=getBCopInfo(bId,bRank);
            System.out.println("bId"+bId);
            if (object==null) break;
            object.put("copId",cop.getCopId());
            object.put("copBId",cop.getCopBId());
            Date copCreateTime=cop.getCopCreateTime();
            String time=sdf.format(copCreateTime);
            object.put("copCreateTime",time);
            array.add(object);
        }
        return array;
    }
    private JSONObject getBCopInfo(BigInteger bId ,String bRank){
        String sql="select bName,bPhone,bRank,bScore,bOtherPlat1,bOtherStore1,bOtherStatus1," +
                "bOtherPlat2,bOtherStore2,bOtherStatus2,bOtherPlat3,bOtherStore3,bOtherStatus3 from business where bId=? ";
        if (!bRank.equals("-1"))
        {
            sql=sql+" and bRank="+bRank;
        }
        Record record=Db.findFirst(sql,bId);

        if (record==null) return null;//没有数据

        JSONObject object=new JSONObject();
        object.put("bName",record.get("bName"));
        object.put("bPhone",record.get("bPhone"));
        object.put("bRank",record.get("bRank"));
        object.put("bScore",record.get("bScore"));
        object.put("bOtherPlat1",record.get("bOtherPlat1"));//1-Ebay 2-亚马逊 3-速卖通
        object.put("bOtherStore1",record.get("bOtherStore1"));
        object.put("bOtherStatus1",record.get("bOterStatus1"));//0-已删除 1-使用中 2-停用中 3-已过期
        object.put("bOtherPlat2",record.get("bOtherPlat2"));
        object.put("bOtherStore2",record.get("bOtherStore2"));
        object.put("bOtherStatus2",record.get("bOterStatus2"));//0-已删除 1-使用中 2-停用中 3-已过期
        object.put("bOtherPlat3",record.get("bOtherPlat3"));
        object.put("bOtherStore3",record.get("bOtherStore3"));
        object.put("bOtherStatus3",record.get("bOterStatus3"));//0-已删除 1-使用中 2-停用中 3-已过期

        return object;
    }

    public JSONArray showApproved(String sId, String bRank,String status) {
        JSONArray array=new JSONArray();
        String copSql="select copId, copBId,copCreateTime,copModifyTime from cooperation where copSId=? and copState=?";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Cooperation> cops=Cooperation.dao.find(copSql,sId,status);
        for (Cooperation cop:cops){
            BigInteger bId=cop.getCopBId();
            System.out.println("bId"+bId);
            JSONObject object=getBCopInfo(bId,bRank);
            if (object==null) break;
            object.put("copId",cop.getCopId());
            object.put("copBId",cop.getCopBId());
            Date createTime=cop.getCopCreateTime();
            String copCreateTime=sdf.format(createTime);
            object.put("copCreateTime",copCreateTime);
            Date modifyTime=cop.getCopModifyTime();
            String copHandleTime=sdf.format(modifyTime);
            object.put("copHandleTime",copHandleTime);
            array.add(object);
        }
        return array;
    }

    public JSONArray showFinishApproved(String sId, String bRank) {
        JSONArray array=new JSONArray();
        String copSql="select copId, copBId,copCreateTime,copState,copModifyTime from cooperation where copSId=? and copState=2";
        //2-终止合作
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Cooperation> cops=Cooperation.dao.find(copSql,sId);
        for (Cooperation cop:cops){
            BigInteger bId=cop.getCopBId();
            JSONObject object=getBCopInfo(bId,bRank);
            if (object==null) break;
            object.put("copId",cop.getCopId());
            object.put("copBId",cop.getCopBId());
            Date createTime=cop.getCopCreateTime();
            String copCreateTime=sdf.format(createTime);
            object.put("copCreateTime",copCreateTime);
            Date copModifyTime=cop.getCopModifyTime();
            String copHandleTime=sdf.format(copModifyTime);
            object.put("copHandleTime",copHandleTime);

            array.add(object);
        }
        return array;
    }

    public boolean operateCooperation(String copId, String operate) {
        System.out.println("copId=="+copId+"operate=="+operate);
        String sql="UPDATE cooperation SET copState=? WHERE copId=? ";
        return Db.update(sql,operate,copId)==1;
    }

    public boolean applyCoop(String bId, String sId) {
        Cooperation cooperation=new Cooperation();
        cooperation.setCopBId(new BigInteger(bId));
        cooperation.setCopSId(new BigInteger(sId));
        cooperation.setCopState(0);//
        return cooperation.save();
    }

    /**
     * 保存店铺信息
     * @param store
     * @return
     */
    public Boolean saveStore(Store store) {
        store.setSStatus(Constant.STORE_CLOSE);//M待审核的时候 店铺状态默认关闭
        store.setSScore(0);//店铺初始分0
        store.setSDirectMoney(Constant.PAY_UN);//不开通 直接到账
        store.setSReduceInventory(Constant.REDUCE_GET);//拍下减库存 默认
        return store.save();
    }

    public boolean updateSPhoto(String sId, String sPhoto) {
        Store store=Store.dao.findById(sId);
        store.setSPhoto(sPhoto);
        return store.update();
    }

    public String [] showSPhoto(String sId) {
        Store store=Store.dao.findById(sId);
        String sPhotos=store.getSPhoto();
        String []photo=sPhotos.split(",");
        return photo;
    }

    //显示关键词设置列表
    public  List<Manukeyword> showManuKeyWorld(String sId) {
        String sql="SELECT mkId,mkText,mkReply,mkCreateTime " +
                "from manukeyword " +
                "where mkStore=? ";
        List<Manukeyword> manukeywords=Manukeyword.dao.find(sql,sId);
        return manukeywords;
    }

    //
}
