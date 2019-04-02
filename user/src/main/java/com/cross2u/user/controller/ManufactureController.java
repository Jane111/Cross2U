
package com.cross2u.user.controller;

import com.cross2u.user.model.Mainmanufacturer;
import com.cross2u.user.service.ManufactureServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.Constant;
import com.cross2u.user.util.CosStsClient;
import com.cross2u.user.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import com.alibaba.fastjson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

@RestController
@CrossOrigin
public class ManufactureController {

    @Autowired
    ManufactureServiceZ service;
    @Autowired
    BaseResponse baseResponse;


    /**
     * 获取cos临时密钥
     */
    @RequestMapping("/manufacturer/getTempKey")
    public String getTempKey()
    {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        org.json.JSONObject credential;
        try {
            // 固定密钥
            config.put("SecretId", Constant.Secret_id);
            // 固定密钥
            config.put("SecretKey", Constant.Secret_key);

            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", "examplebucket-appid");
            // 换成 bucket 所在地区
            config.put("region", "ap-guangzhou");

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的目录，例子：* 或者 a/* 或者 a.jpg
            config.put("allowPrefix", "*");

            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            credential = CosStsClient.getCredential(config);
            System.out.println(credential);
        } catch (Exception e) {
            throw new IllegalArgumentException("no valid secret !");
        }
        /*JSONObject object=new JSONObject();
        if (credential!=null&&credential.get("codeDesc").equals("Success")){
            object.put("codeDesc",credential.getString("codeDesc"));
            object.put("code",credential.get("code"));
            JSONObject object1=new JSONObject();
            object1.put("tmpSecretId",credential.get("data").get("").get("tmpSecretId"));
            object.put("data",credential.get("data"));

        }*/
        return credential.toString();
    }



    //验证手机是否已经被注册
    @RequestMapping("/register/phoneHasBeenRegister")
    public BaseResponse phoneHasBeenRegister(HttpServletRequest request){
        String mmPhone=request.getParameter("mmphone");
        if (service.havePhone(mmPhone)){//重复检测
            baseResponse.setResult(ResultCodeEnum.EXIST_USER_PHONE);//存在已注册联系方式
        }
        else {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }

    //验证邮箱是否已经被注册
    @RequestMapping("/register/emailHasBeenRegister")
    public BaseResponse emailHasBeenRegister(HttpServletRequest request){
        String mmEmail=request.getParameter("mmEmail");
        if (service.haveEmail(mmEmail)){
            baseResponse.setResult(ResultCodeEnum.EXIST_USER_EMAIL);//存在已注册邮箱
        }
        else {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }


    @RequestMapping("/register/first")
    @ResponseBody
    //注册第一步
     public BaseResponse first(HttpServletRequest request) {

        String mmName=request.getParameter("mmName");//m昵称
         String mmEmail=request.getParameter("mmEmail");//m邮箱
         String mmPhone=request.getParameter("mmPhone");//m联系方式
         String mmPassword=request.getParameter("mmPassword");

         BigInteger mmId=service.first(mmName,mmEmail,mmPhone,mmPassword);
         if(mmId!=BigInteger.valueOf(0L)){
             JSONObject json=new JSONObject();
             try {
                 json.put("mmId",mmId);
                 baseResponse.setData(json);
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

        //注册第二步
     @RequestMapping("/register/second")
     @ResponseBody()
     public BaseResponse second(HttpServletRequest request){


         String mmId=request.getParameter("mmId");
         Mainmanufacturer mainmanufacturer=service.findById(mmId);
         if (mainmanufacturer==null){
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

         try {
             String validityStart=request.getParameter("validityStart");
             String validityEnd=request.getParameter("validityEnd");
             SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd");
             Date validityS=sdf.parse(validityStart);
             Date validityE=sdf.parse(validityEnd);
             mainmanufacturer.setMmValidityS(validityS);
             mainmanufacturer.setMmValidityE(validityE);
         } catch (ParseException e) {
             e.printStackTrace();
             baseResponse.setResult(ResultCodeEnum.NET_ERROR);
         }
         mainmanufacturer.setMmLicence(mmLicence);//营业执照
         mainmanufacturer.setMmOwner(mmOwner);//法人姓名
         //----------图像识别2-------
         mainmanufacturer.setMmIdNumber(mmIdNumber);//身份证号
         mainmanufacturer.setMmIdUpImage(mmIdUpImage);//身份证正面
         mainmanufacturer.setMmIdDownImage(mmIdDownImage);//身份证反面

         if(service.second(mainmanufacturer)){
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


     //主账号登录
      @RequestMapping("/login/mainLogin")
     @ResponseBody()
     public BaseResponse mainLogin(HttpServletRequest request) {
         ManufactureServiceZ manufactureService = new ManufactureServiceZ();
         BaseResponse baseResponse = new BaseResponse();

         String mmPhone=request.getParameter("mmPhone");//手机号码登录
         String mmPassword=request.getParameter("mmPassword");//密码
         JSONObject login=manufactureService.mainLogin(mmPhone,mmPassword);
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


     //子账号登录
     @RequestMapping("/login/subLogin")
     @ResponseBody()
     public BaseResponse subLogin(HttpServletRequest request) {
         ManufactureServiceZ manufactureService = new ManufactureServiceZ();
         BaseResponse baseResponse = new BaseResponse();
         String mMainManuPhone=request.getParameter("mMainManuPhone");//父账号
         String mPhone=request.getParameter("mPhone");//子账号
         String mPassword=request.getParameter("mPassword");//密码

         JSONObject sublogin=manufactureService.subLogin(mMainManuPhone,mPhone,mPassword);
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

