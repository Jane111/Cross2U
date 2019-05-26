
package com.cross2u.user.controller;

import com.cross2u.user.model.Mainmanufacturer;
import com.cross2u.user.service.BusinessServiceZ;
import com.cross2u.user.service.ManufactureServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.Constant;
import com.cross2u.user.util.CosStsClient;
import com.cross2u.user.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import com.alibaba.fastjson.*;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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

    @Autowired
    RestTemplate restTemplate;
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
            config.put("bucket", " cross2u-1258618180");
            // 换成 bucket 所在地区
            config.put("region", "ap-chengdu");

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

    /**
     *  后端上传图片
     * @return
     */
    /*@RequestMapping(value = "/tengxun",method = RequestMethod.POST)
    @ResponseBody
    public Object Upload(@RequestParam(value = "file") MultipartFile file, HttpSession session){
        if(file == null){
            return new UploadMsg(0,"文件为空",null);
        }
        String oldFileName = file.getOriginalFilename();
        String eName = oldFileName.substring(oldFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID()+eName;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DATE);
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(bucket));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = this.bucketName;

        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        File localFile = null;
        try {
            localFile = File.createTempFile("temp",null);
            file.transferTo(localFile);
            // 指定要上传到 COS 上的路径
            String key = "/"+this.qianzui+"/"+year+"/"+month+"/"+day+"/"+newFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            return new UploadMsg(1,"上传成功",this.path + putObjectRequest.getKey());
        } catch (IOException e) {
            return new UploadMsg(-1,e.getMessage(),null);
        }finally {
            // 关闭客户端(关闭后台线程)
            cosclient.shutdown();
        }
    }*/


    /*@RequestMapping("/util/uploadImg")
    public BaseResponse uploadImg(
            @RequestParam("test") MultipartFile file
    ){
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials("AKID0jJtXvZOlMn7RVnncuQtJn1zgOyIHFWK", "FzUgdXUmobAwSsIWtQG8l8HI3cy3A4jC");
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者接口文档 FAQ 中说明。
        ClientConfig clientConfig = new ClientConfig(new Region("ap-chengdu"));
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = "cross2u-1258618180";


        if (file.isEmpty()) {
            baseResponse.setResult(ResultCodeEnum.EMPTY_FILE);
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        String filePath = "E://test//";
        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return baseResponse;
    }*/



    /*//验证手机是否已经被注册
    @RequestMapping("/register/phoneHasBeenRegister")
    public BaseResponse phoneHasBeenRegister(HttpServletRequest request){
        String mmPhone=request.getParameter("mmPhone").trim();
        System.out.println("mmphone"+mmPhone);
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
        String mmEmail=request.getParameter("mmEmail").trim();
        System.out.println("email"+mmEmail);
        if (service.haveEmail(mmEmail)){
            baseResponse.setResult(ResultCodeEnum.EXIST_USER_EMAIL);//存在已注册邮箱
        }
        else {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }*/


    @RequestMapping("/register/first")
    @ResponseBody
    //注册第一步
     public BaseResponse first(HttpServletRequest request) {

         String mmEmail=request.getParameter("mmEmail");//m邮箱
         String mmPhone=request.getParameter("mmPhone");//m联系方式
         String mmPassword=request.getParameter("mmPassword");

        if (service.haveEmail(mmEmail)){
            baseResponse.setResult(ResultCodeEnum.EXIST_USER_EMAIL);//存在已注册邮箱
            return baseResponse;
        }
        if (service.havePhone(mmPhone)){//重复检测
            baseResponse.setResult(ResultCodeEnum.EXIST_USER_PHONE);//存在已注册联系方式
            return baseResponse;
        }
         BigInteger mmId=service.first(mmEmail,mmPhone,mmPassword);
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
             SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
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
         String mmName=sName;//m昵称
         String mmLogo = request.getParameter("mmLogo");//店铺logo
         String mmMajorBusiness = request.getParameter("mmMajorBusiness");//主营行业
         String mmFixedNum = request.getParameter("mmFixedNum");//服务电话
         if (manufactureService.third(mmId,sName,mmLogo,mmMajorBusiness,mmFixedNum,mmName))
         {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
         }
         else {
            baseResponse.setResult(ResultCodeEnum.NET_ERROR);
         }
         return baseResponse;
     }


     //主账号登录
      @RequestMapping("/login/loginIn")
     @ResponseBody()
     public BaseResponse loginIn(HttpServletRequest request) {
         ManufactureServiceZ manufactureService = new ManufactureServiceZ();
         BaseResponse baseResponse = new BaseResponse();

         String mPhone=request.getParameter("mPhone");//手机号码登录
         String mPassword=request.getParameter("mPassword");//密码
          Integer isForbiden=manufactureService.isForbiden(mPhone);
          switch (isForbiden){
              case 0://没有该账号
                  baseResponse.setResult(ResultCodeEnum.NOT_REGISTER);
                  return baseResponse;
              case 1://主账号且被禁用
                  baseResponse.setResult(ResultCodeEnum.MAIN_FORBIDDEN);
                  return baseResponse;
              case 2://子账号被禁用
                  baseResponse.setResult(ResultCodeEnum.SUB_FORBIDDEN);
                  return baseResponse;
              case 3://没有被禁用且有账号

                  default:
          }
         JSONObject login=manufactureService.loginIn(mPhone,mPassword);
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
     * 在M显示店铺界面店面
     */
    @RequestMapping("/manufacturer/MshowStoreDetail")
    public BaseResponse MshowStoreDetail(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String sId=request.getParameter("sId");//店铺id
        JSONObject store =businessService.MshowStoreDetail(sId);

        if (store!=null)
        {
            Object array=getTopFourWare(sId);
            store.put("sWares",array);
            baseResponse.setData(store);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    //获取店铺Top4商品
    private Object getTopFourWare(String sId) {
        System.out.println(sId+" "+(restTemplate==null));
        JSONObject array = restTemplate.getForObject("http://Ware/ware/getTopFourWare?sId="+sId,JSONObject.class);
        return array.get("data");
        //return null;
    }

     //子账号登录
    /* @RequestMapping("/login/subLogin")
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
     }*/
 }

