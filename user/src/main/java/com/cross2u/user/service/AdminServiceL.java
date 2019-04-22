package com.cross2u.user.service;

import com.cross2u.user.model.Administrator;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AdminServiceL {

    /*
    * provider
    * */
    //Admin通过Id得到账号
    public Administrator selectAdminById(BigInteger aId)
    {
        return Administrator.dao.findFirst("select aAccount from Administrator where aId=",aId);
    }

}
