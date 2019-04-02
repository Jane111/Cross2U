package com.cross2u.user.controller;

import com.cross2u.user.model.Administrator;
import com.cross2u.user.service.AdminServiceL;
import com.cross2u.user.util.JsonResult;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@CrossOrigin
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
    //todo 24、显示举报评论---未审核
    @RequestMapping("/showBadCommentUnchecked")
    public JsonResult showBadCommentUnchecked(
            @RequestParam("aId") BigInteger aId)
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
    //todo 25、审核举报评论
    @RequestMapping("/checkedBadComment")
    public JsonResult checkedBadComment(
            @RequestParam("aId") BigInteger aId)
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
    //41、显示普通管理员
    @RequestMapping("/showUsualAdmin")
    public JsonResult showUsualAdmin()
    {

        List<Administrator> result = as.selectUsualAdmin();
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
    //42、修改普通管理员
    @RequestMapping("/updateUsualAdmin")
    public JsonResult updateUsualAdmin(
            @RequestParam("aId") BigInteger aId,
            @RequestParam(value = "aAccount",required = false) String aAccount,
            @RequestParam(value = "aPostion",required = false) Integer aPostion,
            @RequestParam(value = "aPassword",required = false) String aPassword,
            @RequestParam(value = "aStatus",required = false) Integer aStatus
    )
    {
        Administrator administrator = new Administrator();
        administrator.setAId(aId);
        if(aAccount!=null)
        {
            administrator.setAAccount(aAccount);
        }
        if(aPostion!=null)
        {
            administrator.setAPostion(aPostion);
        }
        if(aPassword!=null)
        {
            administrator.setAPassword(aPassword);
        }
        if(aStatus!=null)
        {
            administrator.setAStatus(aStatus);
        }
        boolean result = as.updateUsualAdmin(administrator);
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //43、添加普通管理员
    @RequestMapping("/addUsualAdmin")
    public JsonResult addUsualAdmin(
            @RequestParam("aAccount") String aAccount,
            @RequestParam("aPostion") Integer aPostion,
            @RequestParam("aPassword") String aPassword
    )
    {
        Administrator administrator = new Administrator();
        administrator.setAAccount(aAccount);
        administrator.setAPostion(aPostion);
        administrator.setAPassword(aPassword);
        administrator.setAStatus(1);//新建的账号默认是启用的
        boolean result = administrator.save();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //47、删除普通管理员
    @RequestMapping("/deleteUsualAdmin")
    public JsonResult deleteUsualAdmin(
            @RequestParam("aId") BigInteger aId
    )
    {
        Administrator administrator = new Administrator();
        administrator.setAId(aId);
        boolean result = administrator.delete();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //48、筛选普通管理员
    @RequestMapping("/pickUsualAdmin")
    public JsonResult pickUsualAdmin(
            @RequestParam("aPostion") Integer aPostion
    )
    {

        List<Administrator> result = as.selectUsualAdminByPosition(aPostion);
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
