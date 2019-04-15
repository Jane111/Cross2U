package com.cross2u.user.controller;

import com.cross2u.user.model.Mainmanufacturer;
import com.cross2u.user.service.ManufactureServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import com.alibaba.fastjson.*;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManufactureController {


    @RequestMapping("/register/first")
    @ResponseBody
    /**
     * 注册第一步
     */
    public BaseResponse first(HttpServletRequest request) {
        ManufactureServiceZ manufactureService=new ManufactureServiceZ();
        BaseResponse baseResponse=new BaseResponse();
        String mmEmail=request.getParameter("mmEmail");
        String mmPhone=request.getParameter("mmPhone");
        String mmPassword=request.getParameter("mmPassword");
        BigInteger mmId=manufactureService.first(mmEmail,mmPhone,mmPassword);
        if(mmId!=BigInteger.valueOf(0L)){
            JSONObject json=new JSONObject();
            try {
                json.put("mmId",mmId);
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e);
                baseResponse.setResult(ResultCodeEnum.NET_ERROR);
            }
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NET_ERROR);
        }
        return baseResponse;
    }

    /**
     * 注册第二步
     */
    @RequestMapping("/register/second")
    @ResponseBody()
    public BaseResponse second(HttpServletRequest request){
        ManufactureServiceZ manufactureService=new ManufactureServiceZ();
        BaseResponse baseResponse=new BaseResponse();

        String mmId=request.getParameter("mmId");
        Mainmanufacturer mainmanufacturer=manufactureService.findById(mmId);
        if (mainmanufacturer.equals(null)){
            System.out.println("id不存在"+mmId);
            baseResponse.setResult(ResultCodeEnum.NET_ERROR);
            return baseResponse;
        }

        String mmType=request.getParameter("mmType");
        String mmCompany=request.getParameter("mmCompany");
        String mmCompanyPlace=request.getParameter("mmCompanyPlace");
        String mmCoIdentity=request.getParameter("mmCoIdentity");

        String validityTime=request.getParameter("validityTime");
        String mmLicence=request.getParameter("mmLicence");
        String mmOwner=request.getParameter("mmOwner");//法人姓名
        String mmIdNumber=request.getParameter("mmIdNumber");
        String mmIdUpImage=request.getParameter("mmIdUpImage");
        String mmIdDownImage=request.getParameter("mmIdDownImage");

        mainmanufacturer.setMmId(new BigInteger(mmId));
        mainmanufacturer.setMmtype(Integer.valueOf(mmType));//类型
        //-----------图片识别------------
        mainmanufacturer.setMmCompany(mmCompany);//公司名称
        mainmanufacturer.setMmCompanyPlace(mmCompanyPlace);//公司地址
        mainmanufacturer.setMmCoIdentity(mmCoIdentity);//公司注册号
        String [] times=validityTime.split("至");
        String starttime=times[0].replaceAll("自","");
        starttime=starttime.replaceAll("年|月","-");
        starttime=starttime.replaceAll("日","");


        Timestamp start= strToSqlDate(starttime,"yyyy-MM-dd");
        mainmanufacturer.setMmValidityS(start);
        mainmanufacturer.setMmValidityS(start);
        Timestamp endtime=strToSqlDate(times[1],"yyyy-MM-dd");
        mainmanufacturer.setMmValidityE(endtime);
        mainmanufacturer.setMmLicence(mmLicence);//营业执照
        mainmanufacturer.setMmOwner(mmOwner);//法人姓名
        //----------图像识别2-------
        mainmanufacturer.setMmIdNumber(mmIdNumber);//身份证号
        mainmanufacturer.setMmIdUpImage(mmIdUpImage);//身份证正面
        mainmanufacturer.setMmIdDownImage(mmIdDownImage);//身份证反面

        if(manufactureService.second(mainmanufacturer)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NET_ERROR);
        }
        return baseResponse;
    }
    //调用图像识别接口识别信息
    private Mainmanufacturer getLicenceInfo(String imgURL,Mainmanufacturer mainmanufacturer){
        //https://cloud.tencent.com/document/product/866/17730
        return mainmanufacturer;
    }
    private  Mainmanufacturer getIdentyInfo(String imgURL,Mainmanufacturer mainmanufacturer)
    {
        return mainmanufacturer;
    }

    public static Timestamp strToSqlDate(String strDate, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp dateSQL = new Timestamp(date.getTime());
        return dateSQL;
    }

    @RequestMapping("/register/third")
    @ResponseBody()
    public BaseResponse third(HttpServletRequest request) {
        ManufactureServiceZ manufactureService = new ManufactureServiceZ();
        BaseResponse baseResponse = new BaseResponse();

        String mmId = request.getParameter("mmId");
        String sName = request.getParameter("sName");//店铺名称
        String mmLogo = request.getParameter("mmLogo");//店铺logo
        String mmMajorBusiness = request.getParameter("mmMajorBusiness");//主营行业
        String mmFixedNum = request.getParameter("mmFixedNum");//服务电话
        if (manufactureService.third(mmId,sName,mmLogo,mmMajorBusiness,mmFixedNum))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NET_ERROR);
        }
        return baseResponse;
    }

    @RequestMapping("/login/mainLogin")
    @ResponseBody()
    /**
     * 主账号登录
     */
    public BaseResponse mainLogin(HttpServletRequest request) {
        ManufactureServiceZ manufactureService = new ManufactureServiceZ();
        BaseResponse baseResponse = new BaseResponse();

        String mmPhone=request.getParameter("mmPhone");//手机号码登录
        String mmPassword=request.getParameter("mmPassword");//密码
        Record login=manufactureService.mainLogin(mmPhone,mmPassword);
        if(login!=null)
        {
            baseResponse.setData(login);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ERROR_ACCOUNT_OR_PASSWORD);//账号或密码错误
        }
        return baseResponse;
    }

    /**
     * 子账号登录
     */
    @RequestMapping("/login/subLogin")
    @ResponseBody()
    public BaseResponse subLogin(HttpServletRequest request) {
        ManufactureServiceZ manufactureService = new ManufactureServiceZ();
        BaseResponse baseResponse = new BaseResponse();
        String mMainManuPhone=request.getParameter("mMainManuPhone");//父账号
        String mPhone=request.getParameter("mPhone");//子账号
        String mPassword=request.getParameter("mPassword");//密码

        Record sublogin=manufactureService.subLogin(mMainManuPhone,mPhone,mPassword);
        if (sublogin!=null){
            baseResponse.setData(sublogin);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ERROR_ACCOUNT_OR_PASSWORD);//账号或密码错误
        }
        return baseResponse;
    }
}
