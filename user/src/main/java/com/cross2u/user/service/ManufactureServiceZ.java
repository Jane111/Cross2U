package com.cross2u.user.service;

import com.cross2u.user.model.Mainmanufacturer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ManufactureServiceZ {
    public BigInteger first(String mmEmail, String mmPhone, String mmPassword) {
        Mainmanufacturer mainmanufacturer=new Mainmanufacturer();
        mainmanufacturer.setMmEmail(mmEmail);
        mainmanufacturer.setMmPassword(mmPhone);
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

    public boolean third(String mmId, String sName, String mmLogo, String mmMajorBusiness, String mmFixedNum) {
        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findById(new BigInteger(mmId));
        mainmanufacturer.setMmLogo(mmLogo);
        Store store=new Store();
        store.setSMmId(mainmanufacturer.getMmId());
        store.setSName(sName);
        mainmanufacturer.setMmFixedNum(mmFixedNum);
        mainmanufacturer.setMmMajorBusiness(mmMajorBusiness);
        return store.save()&&mainmanufacturer.update();
    }

    public Record mainLogin(String mmPhone,String mmPassword) {

        Mainmanufacturer mainmanufacturer=Mainmanufacturer.dao.findByIdLoadColumns(mmPhone,"mmPhone");
        if(mainmanufacturer.getMmPassword().equals(mmPassword))
        {
            String sql="SELECT mmStatus ,mmName,mmLogo,sId,sStatus,sName,sScore " +
                    " FROM mainmanufacturer INNER JOIN store on mainmanufacturer.mmStore=store.sId " +
                    " WHERE mainmanufacturer.mmId=?";
            return Db.findFirst(sql,mainmanufacturer.getMmId());
        }
        return null;
    }

    public Record subLogin(String mMainManuPhone, String mPhone, String mPassword) {
        Mainmanufacturer mainInfo=Mainmanufacturer.dao.findByIdLoadColumns(mMainManuPhone,"mMainManuPhone");
        String sql="SELECT mMainManu,mPhone,mPassword,mManageWare,mManageIndent,mManageMessage,mManageClient" +
                " FROM manufacturer " +
                " WHERE mMainManu=? AND mPhone like ?'";
        Record manufacturer=Db.findFirst(sql,mainInfo.getMmId(),mPhone);
        if (manufacturer.get("mPassword").equals(mPassword)){
            String sql2="SELECT mmStatus ,mmName,mmLogo,sId,sStatus,sName,sScore " +
                    " FROM mainmanufacturer INNER JOIN store on mainmanufacturer.mmStore=store.sId " +
                    " WHERE mainmanufacturer.mmId=?";
            Record info=Db.findFirst(sql,mainInfo.getMmId());
            info.set("mManageWare ",manufacturer.getInt("mManageWare "));
            info.set("mManageIndent ",manufacturer.getInt("mManageIndent "));
            info.set("mManageMessage ",manufacturer.getInt("mManageMessage "));
            info.set("mManageClient ",manufacturer.getInt("mManageClient "));
            return info;
        }
        else {
            return null;
        }
    }
}
