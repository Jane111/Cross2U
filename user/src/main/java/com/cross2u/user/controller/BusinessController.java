package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.Business;
import com.cross2u.user.model.Visitor;
import com.cross2u.user.service.BusinessServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.Constant;
import com.cross2u.user.util.CosStsClient;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.TreeMap;


@RestController
public class BusinessController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/business/deleteSearchRecord")
    /**
     * 删除搜索记录
     */
    public BaseResponse deleteSearchRecord(HttpServletRequest request)
    {
        BusinessServiceZ businessService=new BusinessServiceZ();
        BaseResponse baseResponse=new BaseResponse();
        String bsrBusiness=request.getParameter("bsrBusiness");
        if(businessService.deleteSearchRecord(bsrBusiness))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.DELETE_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/business/isAuthorize")
    @ResponseBody
    public BaseResponse isAuthorize(HttpServletRequest request){
        BaseResponse response=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String openId=request.getParameter("openId");
        if(businessService.isArthorise(openId))//已授权
        {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);//未授权
        }
        return response;
    }

    /**
     * B注册步骤一
     */
    @RequestMapping("/business/addBusinessStep1")
    public BaseResponse addBusinessStep1(HttpServletRequest request)
    {
        //获取的参数
        String bOpenId=request.getParameter("bOpenId");
        String bName=request.getParameter("bName");
        String bPhone=request.getParameter("bPhone");//
        String bEmail=request.getParameter("bEmail");

        BusinessServiceZ businessService=new BusinessServiceZ();

        Business business=new Business();
        business.setBOpenId(bOpenId);
        business.setBRank(1);
        business.setBScore(0);
        business.setBStatus(2);
        business.setBName(bName);
        business.setBPhone(bPhone);
        business.setBEmail(bEmail);

        Visitor visitor=businessService.getVisitorByOpenId(bOpenId);//是否已经授权
        if (visitor!=null){
            String bWeiXinIcon=visitor.getVWeiXinIcon();
            String bWeiXinName=visitor.getVWeiXinName();
            business.setBWeiXinIcon(bWeiXinIcon);
            business.setBWeiXinName(bWeiXinName);
        }

        BaseResponse baseResponse=new BaseResponse();
        BigInteger bId=businessService.addBusinessStep1(business);
        if(bId!=null){
            baseResponse.setData(bId);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);//添加成功
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);//添加失败
        }
        return baseResponse;
    }


    @RequestMapping("/business/addBusinessStep2")
    public BaseResponse addBusinessStep2(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ service=new BusinessServiceZ();

        String bId=request.getParameter("bId");//零售商id
        String bIdNumber=request.getParameter("bIdNumber");//身份证号
        String bIdUpImage=request.getParameter("bIdUpImage");//可以调用api获得身份证号码
        String bIdDownImage=request.getParameter("bIdDownImage");

        Business business=service.findById(bId);
        business.setBIdNumber(bIdNumber);
        business.setBIdUpImage(bIdUpImage);
        business.setBIdDownImage(bIdDownImage);
        if(service.addBusinessStep23(business))
        {
         baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);//20001
        }
        return baseResponse;
    }

    @RequestMapping("/business/addBusinessStep3")
    
    public BaseResponse addBusinessStep3(HttpServletRequest request) {
        BaseResponse response=new BaseResponse();
        BusinessServiceZ service=new BusinessServiceZ();

        String bId=request.getParameter("bId");//零售商id
        String bMainBusiness=request.getParameter("bMainBusiness");
        String bOtherPlat1=request.getParameter("bOtherPlat1");//1-1-Ebay 2-亚马逊 3-速卖通
        String bOtherStore1=request.getParameter("bOtherStore1");//店铺名

        Business business=service.findById(bId);
        business.setBMainBusiness(new BigInteger(bMainBusiness));
        business.setBOtherPlat1(Integer.valueOf(bOtherPlat1));
        business.setBOtherStore1(bOtherStore1);
        if(service.addBusinessStep23(business))
        {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);//20001
        }
        return response;
    }



    @RequestMapping("/business/addCollectStore")
    
    /**
     * 收藏店铺
     */
    public BaseResponse addCollectStore(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String cOwner=request.getParameter("cOwner");//bID
        String cStore=request.getParameter("cStore");//店铺id
        if(cOwner==null||cOwner.equals(""))
        {
            baseResponse.setResult(ResultCodeEnum.NOT_REGISTER);//未注册
        }
        else {
            if (businessService.addCollectStore(cOwner,cStore)){
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);
            }
        }

        return baseResponse;
    }

    /**
     * 显示店铺界面
     */
    @RequestMapping("/business/showStoreDetail")
    public BaseResponse showStoreDetail(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String sId=request.getParameter("sId");//店铺id
        String openId=request.getParameter("openId");
        JSONObject store =businessService.showStoreDetail(sId,openId);

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
        JSONObject array = restTemplate.getForObject("http://localhost:8003/ware/getTopFourWare?sId="+sId,JSONObject.class);
        return array.get("data");
        //return null;
    }

    /*@RequestMapping("")
    @ResponseBody*/






    /**
     * 查看浏览记录
     */
    @RequestMapping("/business/showBrowseRecord")
    
    public BaseResponse showBrowseRecord(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String bid=request.getParameter("bId");
        JSONArray browserecords= businessService.showBrowseRecord(bid);
        if (!browserecords.isEmpty()){
            baseResponse.setData(browserecords);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 删除浏览记录
     */
    @RequestMapping("/business/deleteBrowseRecord")
    
    public BaseResponse deleteBrowseRecord(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String brIdstr=request.getParameter("brId");
        String []brIds=brIdstr.split(",");
        for (String id : brIds){
            if(!businessService.deleteBrowseRecord(id))
            {
                deleteBrowseRecordRollback(brIdstr,id);//回滚
                baseResponse.setResult(ResultCodeEnum.DELETE_FAILURE);
                return baseResponse;
            }
        }
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    private void deleteBrowseRecordRollback(String brIdstr,String badId) {
        BusinessServiceZ businessService=new BusinessServiceZ();
        String []ids=brIdstr.split(",");
        for (String id:ids){
            if(id.equals(badId))
            {
                return;
            }
            businessService.deleteBrowseRecordRollback(id);
        }
    }

    /**
     * 查看收藏的商品
     */
    @RequestMapping("/business/showCollectWare")
    
    public BaseResponse showCollectWare(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String bId=request.getParameter("bId");

        JSONArray collextwares=businessService.showCollectWare(bId);
        if (!collextwares.isEmpty()){
            baseResponse.setData(collextwares);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 查看收藏的店铺
     */
    @RequestMapping("/business/showCollectStore")
    
    public BaseResponse showCollectStore(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String bId=request.getParameter("bId");
        JSONArray collextstore=businessService.showCollectStore(bId);
        if (!collextstore.isEmpty()){
            baseResponse.setData(collextstore);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 删除收藏记录
     */
    @RequestMapping("/business/deleteCollect")
    
    public BaseResponse deleteCollect(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String cId=request.getParameter("cId");
        String [] cIds=cId.split(",");
        for (String id :cIds){
            if(businessService.deleteCollect(id)){
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else
            {
                baseResponse.setResult(ResultCodeEnum.DELETE_FAILURE);
                break;
            }
        }

        return baseResponse;
    }

    /**
     *查看代理店铺
     */
    @RequestMapping("/business/showCopStore")
    
    public BaseResponse showCopStore(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String bId=request.getParameter("bId");
        String copState=request.getParameter("copState");

       JSONArray copStores=businessService.showCopStore(bId,copState);
       if (!copStores.isEmpty())
       {
           baseResponse.setData(copStores);
           baseResponse.setResult(ResultCodeEnum.SUCCESS);
       }
       else
       {
           baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
       }
        return baseResponse;
    }
    /**
     * 取消申请
     */
    @RequestMapping("/business/cancelCop")
    @ResponseBody
    public  BaseResponse cancelCop(HttpServletRequest request){
        BusinessServiceZ service=new BusinessServiceZ();
        BaseResponse response=new BaseResponse();
        String copId=request.getParameter("copId");
        if (service.cancelCop(copId)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        return response;
    }

    /**
     * 终止代理
     */
    @RequestMapping("/business/deleteCop")
    
    public BaseResponse deleteCop(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String copId=request.getParameter("copId");
        //System.out.println(copId+"???? null?");
        if(businessService.deleteCop(copId))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            baseResponse.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        return baseResponse;
    }


    @RequestMapping("/business/intoMine")
    public BaseResponse intoMine(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        BusinessServiceZ service=new BusinessServiceZ();

        String openId=request.getParameter("openId");
        if (!service.isArthorise(openId)){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//未授权
            return baseResponse;
        }
        JSONObject mine=service.intoMine(openId);

        if (mine==null){
            Visitor visitor=service.getVisitorByOpenId(openId);
            baseResponse.setData(visitor);
            baseResponse.setData(visitor);
            baseResponse.setResult(ResultCodeEnum.NOT_REGISTER);//未注册 已授权
        }
        else {//已注册 已授权
            baseResponse.setData(mine);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }

    @RequestMapping("/business/getWeixinInfo")
    public BaseResponse getWeixinInfo(HttpServletRequest request){
        String openId=request.getParameter("openId");
        String weixinName=request.getParameter("weixinName");
        String weixinIcon=request.getParameter("weixinIcon");
        BaseResponse response=new BaseResponse();
        BusinessServiceZ service=new BusinessServiceZ();
        service.addVisitor(openId,weixinName,weixinIcon);
        response.setResult(ResultCodeEnum.SUCCESS);
        return response;
    }


    /**
     * 获取cos临时密钥
     */
    @RequestMapping("/business/getTempKey")
    @ResponseBody
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




    /*放到indent
    @RequestMapping("/business/showCIndentList")
    
    public BaseResponse showCIndentList(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        BusinessServiceZ businessService = new BusinessServiceZ();
        String bid = request.getParameter("bId");
        String outStatus = request.getParameter("outStatus");//订单状态
        List<Record> outindents = null;

        switch (outStatus) {
            case "1"://1：未发货
            case "2"://2：已发货
            case "3":
                outindents = businessService.showCIndentList(bid, outStatus);//3：已完成
                break;
            case "4":
                outindents = businessService.showCRturnIndent(bid, outStatus);//售后
                break;
            default:
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
                return baseResponse;
        }

        if (!outindents.equals(null)) {
            baseResponse.setData(outindents);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        } else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);//查询失败
        }
        return baseResponse;

    }

    *//**
     * 显示与C的订单
     * @param request
     * @return
     *//*
    @RequestMapping("/business/showCIndent")
    
    public BaseResponse showCIndent(HttpServletRequest request){
        BaseResponse baseResponse = new BaseResponse();
        BusinessServiceZ businessService = new BusinessServiceZ();
        String bId=request.getParameter("bId");
        String outStatus=request.getParameter("outStatus");
        String outId=request.getParameter("outId");

        Record outindent = null;

        switch (outStatus) {
            case "1"://1：未发货
            case "2"://2：已发货
            case "3":
                outindent = businessService.showCIndentInfo(bId, outStatus,outId);//3：已完成
                break;
            case "4":
                outindent = businessService.showCRturnInfo(bId,outStatus, outId);//售后
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
    
    public BaseResponse showMIndentList(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String bId=request.getParameter("bId");
        String inStatus=request.getParameter("inStatus");

        List<Record> mIndentList=new ArrayList<>();
        switch(inStatus){
            case "0"://待付款
            case "2"://待评价
                mIndentList=businessService.showMIndentList0(bId,inStatus);
                break;
            case "3"://已完成
                mIndentList=businessService.showMIndentList3(bId,inStatus);
                break;
            case "1"://合作中
                mIndentList=businessService.showMIndentList1(bId,inStatus);
                break;
            case "4"://申请退款
                mIndentList=businessService.showMIndentList4(bId,inStatus);
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
    
    public BaseResponse showMReturnIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String inId=request.getParameter("inId");
        String diId=request.getParameter("diId");
        Record drawbackInfo=businessService.showMReturnIndent(inId,diId);
        if (drawbackInfo.equals(null)){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }


    @RequestMapping("/business/showMFinishIndent")
    
    public BaseResponse showMFinishIndent(HttpServletRequest request) {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String inId=request.getParameter("inId");
        Record drawbackInfo=businessService.showMFinishIndent(inId);
        if (drawbackInfo.equals(null)){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            baseResponse.setData(drawbackInfo);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        return baseResponse;
    }*/




}
