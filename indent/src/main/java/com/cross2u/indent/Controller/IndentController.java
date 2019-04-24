package com.cross2u.indent.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.indent.Blockchain.blockchain;
import com.cross2u.indent.Service.IndentServiceZ;
import com.cross2u.indent.model.Drawbackinfo;
import com.cross2u.indent.model.Outindent;
import com.cross2u.indent.util.BaseResponse;
import com.cross2u.indent.util.Constant;
import com.cross2u.indent.util.ResultCodeEnum;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfinal.plugin.activerecord.Record;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
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
        Integer pageIndex= Integer.valueOf(request.getParameter("pageIndex"));
        Integer pageSize= Integer.valueOf(request.getParameter("pageSize"));
        JSONArray outindents = null;

        switch (outStatus) {
            case "1"://1：未发货
            case "2"://2：已发货
            case "3":
                outindents = service.showCIndentList(bid, outStatus,pageIndex,pageSize);//3：已完成
                break;
            case "4":
                outindents = service.showCRturnIndent(bid, outStatus,pageIndex,pageSize);//售后
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
            case Constant.IN_WAIT_PAY://待付款
                mIndentList=service.showInWaitPayList(bId,inStatus);
                break;
            case Constant.IN_COOPERATION://合作中
                mIndentList=service.showInCooperate(bId,inStatus);//
                break;
            case Constant.IN_APPLICATION_DRAWBACK://申请退款
                mIndentList=service.showInDrawback(bId,inStatus);
                break;
            case Constant.IN_B_EVAL://待评价

            case Constant.IN_COMPLETE://已完成
                mIndentList=service.showInCompleteList(bId,inStatus);
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

    /**
     * 查看订单详情
     * @param request
     * @return
     */
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


    /**
     * 显示订单详情 除退款
     * @param request
     * @return
     */
    @RequestMapping("/indent/showMIndentInfo")
    @ResponseBody
    public BaseResponse showMIndentInfo(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        String inId=request.getParameter("inId");
        JSONObject drawbackInfo=service.showMIndentInfo(inId);
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
    @RequestMapping("/indent/hasINGIndent")
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



    /*获取可退款最高金额
    @RequestMapping("/indent/getAvailableMoney")
    @ResponseBody
    public BaseResponse getAvailableMoney(HttpServletRequest request) {
        String inId=request.getParameter("inId");
        String diType=request.getParameter("diType");
        Float availableMoney=service.getAvailableMoney(inId,diType);
        response.setResult(ResultCodeEnum.SUCCESS);
        response.setData(availableMoney);
        return response;
    }*/

    @RequestMapping("/indent/inDrawback")
    @ResponseBody
    public BaseResponse inDrawback(HttpServletRequest request) {
        Drawbackinfo drawbackinfo=new Drawbackinfo();
        String diType=request.getParameter("diType");
        String inId=request.getParameter("diInId");
        String diNumber=request.getParameter("diNumber");
        drawbackinfo.setDiType(new BigInteger(diType));//退款类型
        drawbackinfo.setDiReporter(new BigInteger(request.getParameter("diReporter")));//退款申请者
        drawbackinfo.setDiInId(new BigInteger(inId));//订单id
        drawbackinfo.setDiNUmber(new Integer(diNumber));//申请数目
        drawbackinfo.setDiMoney(new Float(request.getParameter("diMoney")));//退款金额
        if (!(request.getParameter("diReasons")==null||request.getParameter("diReasons").equals(""))){
            System.out.println("diReasons"+request.getParameter("diReasons")+"is not null ");
            drawbackinfo.setDiReasons(request.getParameter("diReasons"));//申请理由
        }
        if (!(request.getParameter("diImg1")==null||request.getParameter("diImg1").equals(""))){
            drawbackinfo.setDiImg1(request.getParameter("diImg1"));//退款凭证1
        }
        if (!(request.getParameter("diImg2")==null||request.getParameter("diImg2").equals(""))){
            drawbackinfo.setDiImg2(request.getParameter("diImg2"));//退款凭证2
        }
        if (!(request.getParameter("diImg3")==null||request.getParameter("diImg3").equals(""))){
            drawbackinfo.setDiImg3(request.getParameter("diImg3"));//退款凭证3
        }

        if (service.isOverDeadLine(inId)&&diType.equals(Constant.DRREASON_NO_REASON)){
            response.setResult(ResultCodeEnum.UNAVAILABLE);//available
            return response;
        }

        if(service.inDrawback(drawbackinfo))
        {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);
        }

        return response;

    }

    /**
     * B取消退款申请
     * @param request
     * @return
     */
    @RequestMapping("/indent/cancelDrawback")
    @ResponseBody
    public BaseResponse cancelDrawback(HttpServletRequest request) {
        String diId=request.getParameter("diId");
        if (!service.can_cancel(diId)){//是否可以删除
            response.setResult(ResultCodeEnum.DELETE_FAILURE);
        }
        else {
            if (service.cancelDrawback(diId)){
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                response.setResult(ResultCodeEnum.DELETE_FAILURE);
            }
        }
        return response;
    }
    /**
     * B拒绝C的退货申请
     */
    @RequestMapping("/indent/cancelCReturnGood")
    @ResponseBody
    public BaseResponse cancelCReturnGood(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String rgId=request.getParameter("rgId");
        if (service.cancelCReturnGood(rgId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return baseResponse;
    }
    /**
     * B同意C退货申请
    */
    @RequestMapping("/indent/agreeCReturnGood")
    @ResponseBody
    public BaseResponse agreeCReturnGood(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String rgId=request.getParameter("rgId");
        if (service.agreeCReturnGood(rgId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 取消订单
     * @param request
     * @return
     */
    @RequestMapping("/indent/cancelMIndent")
    @ResponseBody
    public BaseResponse cancelMIndent(HttpServletRequest request){
        String inId=request.getParameter("inId");
        if (service.cancelMIndent(inId)) {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    //生成二维码
    @RequestMapping("/indent/getQrcode")
    public BaseResponse getQrcode(HttpServletRequest request,HttpServletResponse resp) throws Exception {
        String outId=request.getParameter("outId");
        String outExpress=request.getParameter("outExpress");
        Outindent outindent=service.getOutIndentById(outId);

        String mId=service.getMMNameBySId(outindent.getOutSId());//品牌商名称
        String wId=outindent.getOutWIdentifier();//商品编号
        String indentNum=outExpress;//快递单号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        Date date=new Date();
        String time=sdf.format(date);//发货时间

        ServletOutputStream stream = null;
        //BufferedInputStream bis = null;//导出文件
        try {
            stream = resp.getOutputStream();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            //编码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //边框距
            hints.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            String contractAddr= blockchain.getContractAddr( mId,wId,indentNum,time);//获取合约地址
            String fileName=UUID.randomUUID().toString().substring(1,10);
            BitMatrix bm = qrCodeWriter.encode(contractAddr, BarcodeFormat.QR_CODE, 400, 400, hints);
            Path path=new java.io.File("C:/Users/lenovo/Desktop/cross2u/"+fileName+".png").toPath();
            MatrixToImageWriter.writeToStream(bm, "png", stream);


            /**导出文件
             * File file =new File("C:/Users/lenovo/Desktop/cross2u/"+fileName+".png");
            resp.setHeader("content-type", "application/octet-stream");
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=" + fileName+".png");
            byte[] buff = new byte[1024];
            OutputStream os = null;
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);

            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }**/

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
            /*if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
        response.setResult(ResultCodeEnum.FIND_FAILURE);
        return response;
    }


    @RequestMapping("/indent/getContractInfo")
    public BaseResponse getContractInfo(HttpServletRequest request)throws Exception{
        BaseResponse response=new BaseResponse();
        String contractAddr=request.getParameter("contractAddr");
        if(!contractAddr.contains("0x")){//不是符合要求的地址
            response.setResult(ResultCodeEnum.NO_THIS_ADDR);//无法根据该合约地址
            return response;
        }
        String result=blockchain.getContractInfo(contractAddr);
        if (result==null){
            response.setResult(ResultCodeEnum.NO_THIS_ADDR);//无法根据该合约地址
            return response;
        }
        String [] resultS=result.split("#");//String result=mId+"#"+wId+"#"+indentNum+"#"+time;
        if (resultS.length!=4){
            response.setResult(ResultCodeEnum.FIND_FAILURE);
            return response;
        }
        String mId=resultS[0];
        String wId=resultS[1];
        Record ware=service.getWareByWIdentifier(wId);
        String indentNum=resultS[2];
        String time=resultS[3];
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("mName",mId);//品牌商名称
        jsonObject.put("wIdentifier",wId);//商品编号
        jsonObject.put("wTitle",ware.get("wTitle"));
        jsonObject.put("wMainImage",ware.get("wMainImage"));
        jsonObject.put("expressNum",indentNum);//物流单号
        jsonObject.put("time",time);//发货时间
        response.setData(jsonObject);
        response.setResult(ResultCodeEnum.SUCCESS);
        return response;
    }




}
