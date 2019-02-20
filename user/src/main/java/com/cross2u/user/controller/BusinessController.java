package com.cross2u.user.controller;

import com.cross2u.user.model.Business;
import com.cross2u.user.model.Outindent;
import com.cross2u.user.model.Publicinfo;
import com.cross2u.user.model.Ware;
import com.cross2u.user.service.BusinessServiceZ;
import com.cross2u.user.util.BaseResponse;
import com.cross2u.user.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


public class BusinessController {

    @RequestMapping("/business/deleteSearchRecord")
    @ResponseBody
    /**
     * 删除搜索记录
     */
    public BaseResponse deleteSearchRecord(String bsrBusiness)
    {
        BusinessServiceZ businessService=new BusinessServiceZ();
        BaseResponse baseResponse=new BaseResponse();

        if(businessService.deleteSearchRecord(bsrBusiness))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }

        return baseResponse;
    }

    @RequestMapping("/business/addBusiness")
    @ResponseBody
    /**
     * B注册
     */
    public BaseResponse addBusiness(HttpServletRequest request)
    {
        //获取的参数
        String bOpenId=request.getParameter("bOpenId");
        String bName=request.getParameter("bName");
        String bPhone=request.getParameter("bPhone");//????
        String bEmail=request.getParameter("bEmail");
        String bIdNumber=request.getParameter("bIdNumber");//身份证号
        String bIdUpImage=request.getParameter("bIdUpImage");//可以调用api获得身份证号码
        String bIdDownImage=request.getParameter("bIdDownImage");
        String bMainBusiness=request.getParameter("bMainBusiness");
        String bOtherPlat1=request.getParameter("bOtherPlat1");//1-1-Ebay 2-亚马逊 3-速卖通
        String bOtherStore1=request.getParameter("bOtherStore1");//店铺名

        Business business=new Business();
        business.setBOpenId(bOpenId);
        business.setBName(bName);
        business.setBPhone(bPhone);
        business.setBEmail(bEmail);
        business.setBIdNumber(bIdNumber);
        business.setBIdUpImage(bIdUpImage);
        business.setBIdDownImage(bIdDownImage);
        business.setBMainBusiness(Integer.valueOf(bMainBusiness));
        business.setBOtherPlat1(Integer.valueOf(bOtherPlat1));
        business.setBOtherStore1(bOtherStore1);
        BusinessServiceZ businessService=new BusinessServiceZ();
        BaseResponse baseResponse=new BaseResponse();

        if(businessService.addBusiness(business)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);//添加成功
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);//添加失败
        }
        return baseResponse;
    }

    @RequestMapping("/business/addCollectStore")
    @ResponseBody
    /**
     * 收藏店铺
     */
    public BaseResponse addCollectStore(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String cOwner=request.getParameter("cOwner");//bID
        String cStore=request.getParameter("cStore");//店铺id
        if (businessService.addCollectStore(cOwner,cStore)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 显示店铺界面
     */
    @RequestMapping("/business/showStoreDetail")
    @ResponseBody
    public BaseResponse showStoreDetail(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String sId=request.getParameter("sId");//店铺id

        Record store =businessService.showStoreDetail(sId);
        if (!store.equals(null))
        {
            baseResponse.setData(store);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     *显示店铺的商品
     * @param request
     * @return
     */
    @RequestMapping("/business/showStoreWare")
    @ResponseBody
    public BaseResponse showStoreWare(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String wStore=request.getParameter("wStore");//店铺id
        Record ware=businessService.showStoreWare(wStore);
        if (!ware.equals(null)){
            baseResponse.setData(ware);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 显示店铺自定义分类
     */
    @RequestMapping("/business/showStoreClass")
    @ResponseBody
    public BaseResponse showStoreClass(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String wStore=request.getParameter("wStore");//店铺id

        List<Record> record=businessService.showStoreClass(wStore);
        if (!record.equals(null)){
            baseResponse.setData(record);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 显示一级或二级分类中的商品
     */
    @RequestMapping("/business/showStoreClassWare")
    @ResponseBody
    public BaseResponse showStoreClassWare(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        BusinessServiceZ businessService = new BusinessServiceZ();
        String wbWFDId = request.getParameter("wbWFDId");//一级id
        String wbWSDId = request.getParameter("wbWSDId");//二级id
        if (wbWSDId.equals(null))//如果是一级
        {
            List<Record> record=businessService.showStoreFClassWare(wbWFDId);
            baseResponse.setData(record);
        }
        else {
            List<Record> record=businessService.showStoreSClassWare(wbWSDId);
            baseResponse.setData(record);
        }
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 看系统通知
     */
    @RequestMapping("/business/showPublicInfo")
    @ResponseBody
    public BaseResponse showPublicInfo(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        List<Publicinfo> publicinfos=businessService.showPublicInfo();
        baseResponse.setData(publicinfos);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 查看浏览记录
     */
    @RequestMapping("/business/showBrowseRecord")
    @ResponseBody
    public BaseResponse showBrowseRecord(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String bid=request.getParameter("bid");
        List<Record> browserecords= businessService.showBrowseRecord(bid);
        baseResponse.setData(browserecords);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 删除浏览记录
     */
    @RequestMapping("/business/deleteBrowseRecord")
    @ResponseBody
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
    @ResponseBody
    public BaseResponse showCollectWare(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String bId=request.getParameter("bId");

        List<Record> collextwares=businessService.showCollectWare(bId);
        baseResponse.setData(collextwares);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 查看收藏的店铺
     */
    @RequestMapping("/business/showCollectStore")
    @ResponseBody
    public BaseResponse showCollectStore(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String bId=request.getParameter("bId");
        List<Record> collextstore=businessService.showCollectStore(bId);
        baseResponse.setData(collextstore);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 删除收藏记录
     */
    @RequestMapping("/business/showCollectStore")
    @ResponseBody
    public BaseResponse deleteCollect(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String cId=request.getParameter("cId");

        if(businessService.deleteCollect(cId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            baseResponse.setResult(ResultCodeEnum.DELETE_FAILURE);
        }
        return baseResponse;
    }

    /**
     *查看代理店铺
     */
    @RequestMapping("/business/showCopStore")
    @ResponseBody
    public BaseResponse showCopStore(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();

        String bId=request.getParameter("bId");
        String copState=request.getParameter("copState");

        List<Record> copStores=businessService.showCopStore(bId,copState);
        baseResponse.setData(copStores);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 终止代理
     */
    @RequestMapping("/business/deleteCop")
    @ResponseBody
    public BaseResponse deleteCop(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        BusinessServiceZ businessService=new BusinessServiceZ();
        String copId=request.getParameter("copId");

        if(businessService.deleteCop(copId))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            baseResponse.setResult(ResultCodeEnum.DELETE_FAILURE);
        }
        return baseResponse;
    }


}
