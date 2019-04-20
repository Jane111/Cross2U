package com.cross2u.manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.manage.model.Publicinfo;
import com.cross2u.manage.service.ManageServiceZ;
import com.cross2u.manage.util.BaseResponse;
import com.cross2u.manage.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

@CrossOrigin
@RestController
public class ManageController {

    @Autowired
    BaseResponse baseResponse;
    @Autowired
    ManageServiceZ service;
    /**
     * 看系统通知
     */
//    @RequestMapping("/manage/showPublicInfo")
//    @ResponseBody
//    public BaseResponse showPublicInfo(HttpServletRequest request){
//
//        List<Publicinfo> publicinfos=service.showPublicInfo();
//        baseResponse.setData(publicinfos);
//        baseResponse.setResult(ResultCodeEnum.SUCCESS);
//        return baseResponse;
//    }

    /**
     * 显示统计信息
     * 待审核品牌商countM
     * 待审核代理商countB
     * 待审核异常商品countWare
     * 待审核异常商铺countStore
     * 待审核异常评论countComment
     */
    @RequestMapping("/manage/showCount")
    @ResponseBody
    public BaseResponse showCount(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();
        Integer countManu=service.countM();//待审核M的申请注册
        Integer countBus=service.countB();//待审核B的申请注册
        Integer countWares=service.countWare();//待审核异常商品
        Integer countStores=service.countStore();//待审核异常店铺
        Integer countEvaluation=service.countEval();//待审核异常评论
        jsonObject.put("countM",countManu);
        jsonObject.put("countB",countBus);
        jsonObject.put("countWare",countWares);
        jsonObject.put("countStore",countStores);
        jsonObject.put("countEvaluation",countEvaluation);
        baseResponse.setData(jsonObject);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 显示待审核B的列表
    @RequestMapping("/manage/showUncheckB")
    @ResponseBody
    public BaseResponse showUncheckB(HttpServletRequest request){
        String bStatus=request.getParameter("bStatus");
        JSONArray array=service.showUncheckB(bStatus);
        if (array==null||array.isEmpty()){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }*/

    /**
     * 通过零售商


    @RequestMapping("/manage/UpdateBStatus")
    @ResponseBody
    public BaseResponse UpdateBStatus(HttpServletRequest request){
        String bId=request.getParameter("bId");
        if (service.UpdateBStatus(bId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.UPADTE_FALIURE);
        }
        return baseResponse;
    }*/
    /**
     * 退回零售商
     *

    @RequestMapping("/manage/UpdateBText")
    @ResponseBody
    public BaseResponse UpdateBText(HttpServletRequest request){
        String bId=request.getParameter("bId");
        String bFialReasonSelect=request.getParameter("bFialReasonSelect");
        String bFailReasonText=request.getParameter("bFailReasonText");
        if (service.UpdateBText(bId,bFialReasonSelect,bFailReasonText)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.UPADTE_FALIURE);
        }
        return baseResponse;
    }*/
}
