package com.cross2u.user.service;

import com.cross2u.user.model.Administrator;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class AdminServiceL {

    /*
    * provider
    * */
    //Admin通过Id得到账号
    public Administrator selectAdminById(BigInteger aId)
    {
        return Administrator.dao.findById(aId);
    }

    /*
    * service
    * */
    //41、显示普通管理员
    public List<Administrator> selectUsualAdmin()
    {
        return Administrator.dao.find("select aId,aAccount,aPostion,aPassword,aStatus " +
                "from administrator");
    }
    //42、修改普通管理员
    public boolean updateUsualAdmin(Administrator administrator)
    {
        return administrator.update();
    }
    public List<Administrator> selectUsualAdminByPosition(Integer aPostion)
    {
        return Administrator.dao.find("select aId,aAccount,aPostion,aPassword,aStatus " +
                "from administrator where aPostion=?",aPostion);
    }




}
