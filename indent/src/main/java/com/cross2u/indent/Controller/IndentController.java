package com.cross2u.indent.Controller;

import com.cross2u.indent.Service.IndentServiceZ;
import com.cross2u.indent.util.BaseResponse;
import com.cross2u.indent.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class IndentController {

    @Autowired
    private IndentServiceZ service;

    @RequestMapping("/business/showCIndentList")
    @ResponseBody
    public BaseResponse showCIndentList(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        String bid = request.getParameter("bId");
        String outStatus = request.getParameter("outStatus");//订单状态
        List<Record> outindents = null;

        switch (outStatus) {
            case "1"://1：未发货
            case "2"://2：已发货
            case "3":
                outindents = service.showCIndentList(bid, outStatus);//3：已完成
                break;
            case "4":
                outindents = service.showCRturnIndent(bid, outStatus);//售后
                break;
            default:
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
                return baseResponse;
        }

        if (outindents!=null) {
            baseResponse.setData(outindents);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        } else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
        }
        return baseResponse;

    }

    /**
     * 显示与C的订单
     * @param request
     * @return
     */
    @RequestMapping("/business/showCIndent")
    @ResponseBody
    public BaseResponse showCIndent(HttpServletRequest request){
        BaseResponse baseResponse = new BaseResponse();
        String bId=request.getParameter("bId");
        String outStatus=request.getParameter("outStatus");
        String outId=request.getParameter("outId");

        Record outindent = null;

        switch (outStatus) {
            case "1"://1：未发货
            case "2"://2：已发货
            case "3":
                outindent = service.showCIndentInfo(bId, outStatus,outId);//3：已完成
                break;
            case "4":
                outindent = service.showCRturnInfo(bId,outStatus, outId);//售后
                break;
            default:
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
                return baseResponse;
        }

        if (outindent!=null) {
            baseResponse.setData(outindent);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        } else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
        }
        return baseResponse;

    }

    @RequestMapping("/business/showMIndentList")
    @ResponseBody
    public BaseResponse showMIndentList(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String bId=request.getParameter("bId");
        String inStatus=request.getParameter("inStatus");

        List<Record> mIndentList=new ArrayList<>();
        switch(inStatus){
            case "1"://待付款
                mIndentList=service.showMIndentList0(bId,inStatus);
                break;
            case "2"://合作中
                mIndentList=service.showMIndentList2(bId,inStatus);
                break;
            case "3"://申请退款
                mIndentList=service.showMIndentList3(bId,inStatus);
                break;
            case "4"://待评价

            case "5":
                mIndentList=service.showMIndentList0(bId,inStatus);
                break;
            default:
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查找失败
                return baseResponse;
        }

        if (mIndentList!=null) {
            baseResponse.setData(mIndentList);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        } else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
        }
        return baseResponse;
    }
    @RequestMapping("/business/showMReturnIndent")
    @ResponseBody
    public BaseResponse showMReturnIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        String inId=request.getParameter("inId");
        String diId=request.getParameter("diId");
        Record drawbackInfo=service.showMReturnIndent(inId,diId);
        if (drawbackInfo==null){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }


    @RequestMapping("/business/showMFinishIndent")
    @ResponseBody
    public BaseResponse showMFinishIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        String inId=request.getParameter("inId");
        Record drawbackInfo=service.showMFinishIndent(inId);
        if (drawbackInfo==null){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }
}
