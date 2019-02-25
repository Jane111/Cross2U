package com.cross2u.user.controller;

import com.cross2u.user.model.Administrator;
import com.cross2u.user.model.Visitor;
import com.cross2u.user.service.AdminServiceL;
import com.cross2u.user.service.businessServiceL;
import com.cross2u.user.util.JsonResult;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

@RestController
@RequestMapping("/admin")
public class AdminControllerL {

    @Autowired
    AdminServiceL as;
    @Autowired
    JsonResult jr;
    /*
    * 面向其他模块provider
    * */
    //1、Admin根据Id得到账号
    @RequestMapping("/findAdminById/{aId}")
    public JsonResult findVisitorById(
            @PathVariable("aId") BigInteger aId)
    {
        Administrator result = as.selectAdminById(aId);
        if(result!=null)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }




}
