package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.Business;
import com.cross2u.user.model.Mainmanufacturer;
import com.cross2u.user.service.AdminServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.MailUtil;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class AdminController {

    @Autowired
    AdminServiceZ service;
    @Autowired
    BaseResponse response;

    /**
     * 自身应用
     */
    //1、登录
    @RequestMapping("/admin/loginIn")
    public BaseResponse loginIn(HttpServletRequest request,HttpServletResponse responce){
        responce.setHeader("Access-Control-Allow-Origin","*");
        String phone=request.getParameter("phone");
        String password=request.getParameter("password");
        if(!service.hasAccount(phone))//没有该账号
        {
            System.out.println("??? 没有账号");
            response.setResult(ResultCodeEnum.ADMIN_NO_ACCOUNT);
        }
        else {
            JSONObject administor=service.loginIn(phone,password);//1,2,3,4 对应用户管理、店铺管理、消息管理、系统管理
            if (administor!=null){
                if(service.isForbidden(phone))
                {
                    response.setResult(ResultCodeEnum.HAS_BEN_FORBIDDEN);
                }
                else {
                    response.setData(administor);
                    response.setResult(ResultCodeEnum.SUCCESS);
                }
            }
            else {
                response.setResult(ResultCodeEnum.ERROR_ACCOUNT_OR_PASSWORD);
            }
        }
        System.out.println(response);
        return  response;
    }

    @RequestMapping("/admin/showUncheckM")
    public BaseResponse showUncheckM(HttpServletRequest request){
        String mmStatus=request.getParameter("mmStatus");//2 未审核的M
        JSONArray array=service.showcheckM(mmStatus);
        if (array==null||mmStatus==null||!mmStatus.equals("2"))
        {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     *显示待审核M详细信息
     * @param request
     * @return
     */
    @RequestMapping("/admin/showUncheckMDetail")
    public BaseResponse showUncheckMDetail(HttpServletRequest request){
        String mmId=request.getParameter("mmId");
        JSONObject object=service.showcheckMDetail(mmId,"2");
        if (object==null||mmId==null||mmId.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else
        {
            response.setData(object);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 品牌商M通过
     *
     */
    @RequestMapping("/admin/UpdateMStatus")
    public BaseResponse UpdateMStatus(HttpServletRequest request){
        String mmId=request.getParameter("mmId");
        if (service.UpdateMStatus(mmId)){
            Mainmanufacturer m=service.getMainManufactureById(mmId);
            MailUtil.passMSend(m.getMmPhone(),m.getMmEmail());//发短信 发邮件
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    /**
     * 退回M
     */
    @RequestMapping("/admin/UpdateMText")
    public BaseResponse UpdateMText(HttpServletRequest request){
        String mmId=request.getParameter("mmId");
        String mmFialReasonSelect=request.getParameter("mmFialReasonSelect");
        String mmFailReasonText=request.getParameter("mmFailReasonText");
        if (service.UpdateMText(mmId,mmFialReasonSelect,mmFailReasonText)){
            Mainmanufacturer m=service.getMainManufactureById(mmId);
            MailUtil.refuseMSend(m.getMmPhone(),mmFialReasonSelect,mmFailReasonText,m.getMmEmail());//发短信 发邮件
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    /**
     *显示被退回的M的列表
     */
    @RequestMapping("/admin/showReturnM")
    public BaseResponse showReturnM(HttpServletRequest request){
        String mmStatus=request.getParameter("mmStatus");
        JSONArray array=service.showcheckM(mmStatus);
        if (array==null||mmStatus==null||mmStatus.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 显示被退回M的详细情况
     */

    @RequestMapping("/admin/showPassMDetail")
    public BaseResponse showPassMDetail(HttpServletRequest request){
        String mmId=request.getParameter("mmId");
        JSONObject mainmanufacturer=service.showcheckMDetail(mmId,"1");///1-正在运行
        if (mainmanufacturer==null||mmId==null||mmId.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(mainmanufacturer);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }
    /**
     *显示通过的M的列表
     */
    @RequestMapping("/admin/showPassM")
    public BaseResponse showPassM(HttpServletRequest request){
        String mmStatus=request.getParameter("mmStatus");
        JSONArray array=service.showcheckM(mmStatus);
        if (array==null||mmStatus==null||mmStatus.equals("")||!mmStatus.equals("1")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 显示被退回M的详细情况
     */

    @RequestMapping("/admin/showReturnMDetail")
    public BaseResponse showReturnMDetail(HttpServletRequest request){
        String mmId=request.getParameter("mmId");
        JSONObject mainmanufacturer=service.showcheckMDetail(mmId,"3");//3-拒绝申请
        if (mainmanufacturer==null||mmId==null||mmId.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(mainmanufacturer);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }


    /**
     * 显示待审核的B
     */
    @RequestMapping("/admin/showUncheckB")
    public BaseResponse showUncheckB(HttpServletRequest request){
        String bStatus=request.getParameter("bStatus");
        JSONArray array=service.showcheckB(bStatus);
        if (array==null|bStatus==null||bStatus.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 显示待审核B的详细信息
     */

    @RequestMapping("/admin/showUncheckBDetail")
    public BaseResponse showUncheckBDetail(HttpServletRequest request){
        String bId=request.getParameter("bId");
        JSONObject object=service.showcheckBDetail(bId);//2-待审核
        if (object==null||bId==null||bId.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(object);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     *通过B的注册申请
     * @param request
     * @return
     */
    @RequestMapping("/admin/UpdateBStatus")
    public BaseResponse UpdateBStatus(HttpServletRequest request){
        String bId=request.getParameter("bId");
        if (service.UpdateBStatus(bId)){
            Business b=service.getBusinessById(bId);
            MailUtil.passBSend(b.getBWeiXinName(),b.getBEmail());
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    /**
     * 退回B的申请
     */
    @RequestMapping("/admin/UpdateBText")
    public BaseResponse UpdateBText(HttpServletRequest request){
        String bId=request.getParameter("bId");
        String bFialReasonSelect=request.getParameter("bFialReasonSelect");
        String  bFailReasonText=request.getParameter("bFailReasonText");
        if (service.UpdateBText(bId,bFialReasonSelect,bFailReasonText)){
            Business b=service.getBusinessById(bId);
            MailUtil.refuseBSend(b.getBWeiXinName(),bFialReasonSelect,bFailReasonText,b.getBEmail());
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    /**
     * 显示退回B的列表
     */
    @RequestMapping("/admin/showReturnB")
    public BaseResponse showReturnB(HttpServletRequest request){
        String bStatus=request.getParameter("bStatus");
        JSONArray array=service.showcheckB(bStatus);
        if (array==null||bStatus==null||bStatus.equals("")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 显示退回B的详情
     */
    @RequestMapping("/admin/showReturnBDetail")
    public BaseResponse showReturnBDetail(HttpServletRequest request){
        String bId=request.getParameter("bId");
        JSONObject object=service.showReturnBDetail(bId);
        if (object==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(object);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 显示已通过B的列表
     */
    @RequestMapping("/admin/showPassB")
    public BaseResponse showPassB(HttpServletRequest request){
        String bStatus=request.getParameter("bStatus");
        JSONArray array=service.showcheckB(bStatus);
        if (array==null||bStatus==null||bStatus.equals("")||!bStatus.equals("1")){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }
    /**
     * 显示通过B的详情
     */
    @RequestMapping("/admin/showPassBDetail")
    public BaseResponse showPassBDetail(HttpServletRequest request){
        String bId=request.getParameter("bId");
        JSONObject object=service.showcheckBDetail(bId);
        if (object==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(object);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 查看保证金列表
     */
    @RequestMapping("/admin/showEnsureMoneyList")
    public BaseResponse showEnsureMoneyList(HttpServletRequest request){
        String Degree=request.getParameter("Degree");
        JSONArray array=service.showEnsureMoneyList(Degree);
        if (array==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }
    /**
     * 查看M积分
     */

    @RequestMapping("/admin/showMScore")
    public BaseResponse showMScore(HttpServletRequest request){
        String Degree=request.getParameter("Degree");
        JSONArray array=service.showMScore(Degree);
        if (array==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 查看B的积分
     */
    @RequestMapping("/admin/showBScore")
    public BaseResponse showBScore(HttpServletRequest request){
        String Degree=request.getParameter("Degree");
        JSONArray array=service.showBScore(Degree);
        if (array==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    /**
     * 查看M信誉详情
     */
    @RequestMapping("/admin/showEnsureMoneyDetail")
    public BaseResponse showEnsureMoneyDetail(HttpServletRequest request){
        String sId=request.getParameter("sId");
        JSONArray array=service.showEnsureMoneyDetail(sId);
        if (array==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

}
