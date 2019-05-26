package com.cross2u.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.store.model.*;
import com.cross2u.store.service.StoreServiceZ;
import com.cross2u.store.util.BaseResponse;
import com.cross2u.store.util.Constant;
import com.cross2u.store.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.List;

@CrossOrigin//解决跨域问题
@RestController
public class StoreController {
    @Autowired
    StoreServiceZ service;
    @Autowired
    BaseResponse response;

    /**
     * 其他模块调用
     */
    @RequestMapping("/store/saveStore")
    public Boolean saveStore(HttpServletRequest request, HttpServletResponse responce){
        responce.setHeader("Access-Control-Allow-Origin","*");

        String mmId=request.getParameter("mmId");//店铺对应的mmid
        String sName=request.getParameter("sName");//店铺名称
        String mmLogo=request.getParameter("mmLogo");//企业logo
        Store store=new Store();
        store.setSMmId(new BigInteger(mmId));store.setSName(sName);
        store.setSPhoto(mmLogo);

        return service.saveStore(store);
    }


    /**
     * 添加商品到自定义分类
     * @param request
     * @return
     */
    @RequestMapping("/store/addOneWareBelong")
    @ResponseBody
    public BaseResponse addOneWareBelong(HttpServletRequest request,HttpServletResponse responce){
        responce.setHeader("Access-Control-Allow-Origin","*");

        String wId=request.getParameter("wId");
        String wbWFDId=request.getParameter("wbWFDId");
        String wbWSDId=request.getParameter("wbWSDId");
        if(service.addOneWareBelong(wId,wbWFDId,wbWSDId))
        {
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return response;
    }

    /**
     *其他模块调用-新建店铺分类
     * @param request
     * @return
     */
    @RequestMapping("/store/updateWareBelong")
    @ResponseBody
    public BaseResponse updateWareBelong(HttpServletRequest request)
    {
        String wbId=request.getParameter("wbId");
        String wbWFDId=request.getParameter("wbWFDId");
        String wbWSDId=request.getParameter("wbWSDId");
        if (service.updateWareBelong(wbId,wbWFDId,wbWSDId)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        return response;
    }

    /**
     * 显示店家自定义商品类别
     * @param request
     * @return
     */
    @RequestMapping("/store/showFDispatch")
    @ResponseBody
    public BaseResponse showFDispatch(HttpServletRequest request){
        String wfdSId=request.getParameter("wfdSId");//店铺id
        List<Warefdispatch> warefdispatchs=service.showFDispatch(wfdSId);
        if (warefdispatchs!=null)
        {
            response.setData(warefdispatchs);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.NOT_FIND);//空
        }
        return response;
    }

    /**
     * 显示自定义二级类别 按顺序
     * @param request
     * @return
     */
    @RequestMapping("/store/showSDispatch")
    @ResponseBody
    public BaseResponse showSDispatch(HttpServletRequest request){

        String wfdId=request.getParameter("wfdId");

        List<Waresdispatch> waresdispatches=service.showSDispatch(wfdId);
        if (waresdispatches!=null)
        {
            response.setData(waresdispatches);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.NOT_FIND);//空
        }
        return response;
    }

    /**
     *
     * @param request
     * @return
     */
    @RequestMapping("/store/deleteCop")
    @ResponseBody
    public BaseResponse deleteCop(HttpServletRequest request){
        String copId=request.getParameter("copId");
        System.out.println("store="+copId);
        if(service.deleteCop(copId)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        return response;
    }

    /**
     * 显示店铺自定义分类
     */
    @RequestMapping("/store/showStoreClass")
    public BaseResponse showStoreClass(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String wStore=request.getParameter("wStore");//店铺id

        JSONArray record=service.showStoreClass(wStore);
        System.out.println(record);
        if (!record.isEmpty()){
            baseResponse.setData(record);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/store/dispatchShowDispatchs")
    @ResponseBody
    public BaseResponse dispatchShowDispatchs(HttpServletRequest request){
        String sId=request.getParameter("sId");

        JSONArray array=service.dispatchShowDispatchs(sId);
        if(array==null){
            response.setResult(ResultCodeEnum.FIND_FAILURE);//
        }
        else {
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        return response;
    }

    @RequestMapping("/store/dispatchAddFDispatchs")
    @ResponseBody
    public BaseResponse dispatchAddFDispatchs(HttpServletRequest request)
    {
        String sId=request.getParameter("sId");
        String wfdName=request.getParameter("wfdName");
        String wfdSort=service.getWFDSort(sId);
        BigInteger wfdId=service.dispatchAddFDispatchs(sId,wfdName,wfdSort);
        if(wfdId!=null){
            response.setData(wfdId);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/dispatchAddSDispatchs")
    @ResponseBody
    public BaseResponse dispatchAddSDispatchs(HttpServletRequest request)
    {
        String sId=request.getParameter("sId");
        String wfdId=request.getParameter("wfdId");
        String wsdName=request.getParameter("wsdName");
        String wsdImg=request.getParameter("wsdImg");
        String wsdSort=service.getWSDSort(wfdId);

        BigInteger wsdId=service.dispatchAddSDispatchs(sId,wfdId,wsdName,wsdImg,new Integer(wsdSort));

        if(wsdId!=null)
        {
            response.setData(wsdId);
            if (wsdSort.equals("1")){//如果是第一个子类
                service.setFirstWSDWare(wfdId,wsdId);
            }
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/dispatchChangeInfo")
    @ResponseBody
    public BaseResponse dispatchChangeInfo(HttpServletRequest request)
    {
        String wfdId=request.getParameter("wfdId");
        String wsdId=request.getParameter("wsdId");
        String name=request.getParameter("name");
        String wsdImg=request.getParameter("wsdImg");
        if (!(wfdId==null||wfdId.equals(""))){
            if (name==null||name.equals("")){//父类名称为空
                response.setResult(ResultCodeEnum.UPDATE_FAILURE);
            }
            else if(service.changeWFDName(wfdId,name)){
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        else if (wsdId==null||wsdId.equals("")){//父类id 子类id都为空
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        else {
            if(service.chageWSDName(wsdId,name,wsdImg)){
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/dispatchChangeLoc")
    @ResponseBody
    public BaseResponse dispatchChangeLoc(HttpServletRequest request){
        String wfdId=request.getParameter("wfdId");
        String wsdId=request.getParameter("wsdId");
        String Operation=request.getParameter("Operation");
        if (!(wfdId==null||wfdId.equals(""))&&(wsdId==null||wsdId.equals("")))
        {
            System.out.println("??");
            if(service.changeDispatchFLoc(wfdId,Operation)){
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else response.setResult(ResultCodeEnum.NET_ERROR);
        }
        else if  ((wfdId==null||wfdId.equals(""))&&!(wsdId==null||wsdId.equals("")))
        {
            System.out.println("???");
            if(service.changeDispatchSLoc(wsdId,Operation))
            {
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else response.setResult(ResultCodeEnum.NET_ERROR);
        }
        else {
            System.out.println("????");
            response.setResult(ResultCodeEnum.NET_ERROR);
        }
        return response;
    }

    @RequestMapping("/store/dispatchDelete")
    @ResponseBody
    public BaseResponse dispatchDelete(HttpServletRequest request){
        String wfdId=request.getParameter("wfdId");
        String wsdId=request.getParameter("wsdId");
        if (wfdId!=null&&wsdId==null){
            if(service.deleteWFD(wfdId)){

            }
            else response.setResult(ResultCodeEnum.NET_ERROR);
        }
        else if (wfdId==null&&wsdId!=null){
            if(service.deleteWSD(wsdId)){

            }
            else response.setResult(ResultCodeEnum.NET_ERROR);
        }
        else {
            response.setResult(ResultCodeEnum.NET_ERROR);
        }
        return response;
    }

    /**
     * 1、显示（筛选）未审核代理商
     * @param request
     * @return
     */
    @RequestMapping("/store/showApproving")
    @ResponseBody
    public BaseResponse showApproving(HttpServletRequest request)
    {
        String sId=request.getParameter("sId");
        String bRank=request.getParameter("bRank");
        JSONArray array=service.showApproving(sId,bRank);
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }

    /**
     * 2、显示（筛选）已通过代理商
     * @param request
     * @return
     */
    @RequestMapping("/store/showApproved")
    @ResponseBody
    public BaseResponse showApproved(HttpServletRequest request)
    {
        String sId=request.getParameter("sId");
        String bRank=request.getParameter("bRank");
        String status=request.getParameter("status");
        JSONArray array=service.showApproved(sId,bRank,status);
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/showFinishApproved")
    @ResponseBody
    //3、显示（筛选）终止代理商
    public BaseResponse showFinishApproved(HttpServletRequest request)
    {
        String sId=request.getParameter("sId");
        String bRank=request.getParameter("bRank");
        JSONArray array=service.showFinishApproved(sId,bRank);
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/operateCooperation")
    @ResponseBody
    //5、操作代理商申请（通过 或拒绝）
    public BaseResponse operateCooperation(HttpServletRequest request)
    {
        String copId=request.getParameter("copId");
        String operate=request.getParameter("operate");

        if (service.operateCooperation(copId,operate)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }

    /**
     * B申请代理
     * @param request
     * @return
     */
    @RequestMapping("/store/applyCoop")
    @ResponseBody
    public BaseResponse applyCoop(HttpServletRequest request){
        String bId=request.getParameter("bId");
        String sId=request.getParameter("sId");

        if (service.hasRight(bId,sId)){
            response.setResult(ResultCodeEnum.DO_NOT_HAVE_RIGHT);
        }
        else {
            if (service.applyCoop(bId,sId))
            {
                response.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                response.setResult(ResultCodeEnum.ADD_FAILURE);
            }
        }

        return response;
    }

    /**
     * 显示店铺宣传图
     */
    @RequestMapping("/store/showSPhoto")
    @ResponseBody
    public BaseResponse showSPhoto(HttpServletRequest request){
        String sId=request.getParameter("sId");
        String [] array=service.showSPhoto(sId);
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }

    /**
     * 上传店铺宣传图
     */
    @RequestMapping("/store/updateSPhoto")
    @ResponseBody
    public BaseResponse updateSPhoto(HttpServletRequest request){
        String sId=request.getParameter("sId");
        String sPhoto=request.getParameter("sPhoto");
        if (service.updateSPhoto(sId,sPhoto)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);//20005修改失败
        }
        return response;
    }

    /**
     * 显示关键词设置情况
     */

    @RequestMapping("/store/showManuKeyWorld")
    @ResponseBody
    public BaseResponse showManuKeyWorld(HttpServletRequest request){
        response=new BaseResponse();
        String sId=request.getParameter("sId");
        List<Manukeyword> array=service.showManuKeyWorld(sId);
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }


    @RequestMapping("/store/updateManuKeyWorld")
    public BaseResponse updateManuKeyWorld(
            @RequestParam("mkId") BigInteger mkId,
            @RequestParam("mkText") String mkText,
            @RequestParam("mkReply") String mkReply){

        if(service.updateManuKeyWorld(mkId,mkText,mkReply)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.UPDATE_FAILURE);
        }
        return response;
    }

    @RequestMapping("/store/deleteManuKeyWorld")
    public BaseResponse deleteManuKeyWorld(
            @RequestParam("mkId") BigInteger mkId){
        if (service.deleteManuKeyWorld(mkId)){
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.DELETE_FAILURE);
        }
        return  response;
    }


    @RequestMapping("/store/addManuKeyWorld")
    public BaseResponse addManuKeyWorld(
            @RequestParam("sId") Long sId,
            @RequestParam("mkText") String mkText,
            @RequestParam("mkReply") String mkReply
    ){
        response=new BaseResponse();
        BigInteger mkId=service.addManuKeyWorld(sId,mkText,mkReply);
        if (!(mkId==null||mkId.equals(""))){
            response.setData(mkId);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.ADD_FAILURE);
        }
        return response;
    }

    /**
     * 显示A的关键词
     */

    @RequestMapping("/store/getAKW")
    public BaseResponse getAKW(HttpServletRequest request,HttpServletResponse responce){
        responce.setHeader("Access-Control-Allow-Origin","*");

        JSONArray array=service.getAKW();
        if (array!=null){
            response.setData(array);
            response.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            response.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return response;
    }
}
