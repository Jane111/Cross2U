package com.cross2u.indent.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.Service.IndentServiceZ;
import com.cross2u.indent.model.Drawbackinfo;
import com.cross2u.indent.util.BaseResponse;
import com.cross2u.indent.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IndentController {

    @Autowired
    private IndentServiceZ service;
    @Autowired
    private BaseResponse response;

    @RequestMapping("/indent/showCIndentList")
    @ResponseBody
    public BaseResponse showCIndentList(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        String bid = request.getParameter("bId");
        String outStatus = request.getParameter("outStatus");//订单状态
        JSONArray outindents = null;

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
    @RequestMapping("/indent/showCIndent")
    @ResponseBody
    public BaseResponse showCIndent(HttpServletRequest request){
        BaseResponse baseResponse = new BaseResponse();
        String bId=request.getParameter("bId");
        String outStatus=request.getParameter("outStatus");
        String outId=request.getParameter("outId");

        JSONObject outindent = null;

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

    @RequestMapping("/indent/showMIndentList")
    @ResponseBody
    public BaseResponse showMIndentList(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String bId=request.getParameter("bId");
        String inStatus=request.getParameter("inStatus");

        JSONArray mIndentList=new JSONArray();
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
    @RequestMapping("/indent/showMReturnIndent")
    @ResponseBody
    public BaseResponse showMReturnIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        String inId=request.getParameter("inId");
        String diId=request.getParameter("diId");
        JSONObject drawbackInfo=service.showMReturnIndent(inId,diId);
        if (drawbackInfo==null){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }


    @RequestMapping("/indent/showMFinishIndent")
    @ResponseBody
    public BaseResponse showMFinishIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        String inId=request.getParameter("inId");
        JSONObject drawbackInfo=service.showMFinishIndent(inId);
        if (drawbackInfo==null){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }

    /**
     * 该商品是否有正在进行的订单
     * @return
     */
    @RequestMapping("indent/hasINGIndent")
    @ResponseBody
    public Boolean hasINGIndent(HttpServletRequest request){
        String wId=request.getParameter("wId");
        return service.hasINGIndent(wId);
    }

    @RequestMapping("/indent/drawbackGetInfo")
    @ResponseBody
    public BaseResponse drawbackGetInfo(HttpServletRequest request) {
        String inId=request.getParameter("inId");
        JSONObject drawbackInfo=service.drawbackGetInfo(inId);
        if(drawbackInfo!=null){
            response.setData(drawbackInfo);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }


    @RequestMapping("/indent/drawback")
    @ResponseBody
    public BaseResponse drawback(HttpServletRequest request) {
        Drawbackinfo drawbackinfo=new Drawbackinfo();
        drawbackinfo.setDiType(new BigInteger(request.getParameter("diType")));//退款类型
        drawbackinfo.setDiReporter(new BigInteger(request.getParameter("diReporter")));//退款申请者
        drawbackinfo.setDiInId(new BigInteger(request.getParameter("diInId")));//订单id
        drawbackinfo.setDiNUmber(new Integer(request.getParameter("diNumber")));//申请数目
        drawbackinfo.setDiMoney(new Float(request.getParameter("diMoney")));//退款金额
        drawbackinfo.setDiReasons(request.getParameter("diReasons"));//申请理由
        drawbackinfo.setDiImg1(request.getParameter("diImg1"));//退款凭证1
        drawbackinfo.setDiImg2(request.getParameter("diImg2"));//退款凭证2
        drawbackinfo.setDiImg3(request.getParameter("diImg3"));//退款凭证3

        if(service.drawback(drawbackinfo))
        {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);
        }

        return response;

    }


}
