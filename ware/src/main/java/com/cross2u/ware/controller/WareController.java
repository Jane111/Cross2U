//package com.cross2u.ware.controller;
//
//import com.cross2u.ware.service.WareServicesZ;
//import com.cross2u.ware.util.BaseResponse;
//import com.cross2u.ware.util.ResultCodeEnum;
//import com.jfinal.plugin.activerecord.Record;
//import org.json.JSONArray;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
//public class WareController  {
//
//    @RequestMapping("/ware/demo")
//    @ResponseBody
//    public BaseResponse demmo(HttpServletRequest request){
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//
//        return baseResponse;
//    }
//
//    @RequestMapping("/ware/showFDispatch")
//    @ResponseBody
//    public BaseResponse showFDispatch(HttpServletRequest request){
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//
//        String wfdSId=request.getParameter("wfdSId");//店铺id
//        List<Warefdispatch> warefdispatchs=wareServices.showFDispatch(wfdSId);
//        if (warefdispatchs!=null)
//        {
//            baseResponse.setData(warefdispatchs);
//            baseResponse.setResult(ResultCodeEnum.SUCCESS);
//        }
//        else {
//            baseResponse.setResult(ResultCodeEnum.NOT_FIND);//空
//        }
//        return baseResponse;
//    }
//
//    @RequestMapping("/ware/showSDispatch")
//    @ResponseBody
//    public BaseResponse showSDispatch(HttpServletRequest request){
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//        String wfdId=request.getParameter("wfdId");
//
//        List<Waresdispatch> waresdispatches=wareServices.showSDispatch(wfdId);
//        if (waresdispatches!=null)
//        {
//            baseResponse.setData(waresdispatches);
//            baseResponse.setResult(ResultCodeEnum.SUCCESS);
//        }
//        else {
//            baseResponse.setResult(ResultCodeEnum.NOT_FIND);//空
//        }
//        return baseResponse;
//    }
//*/
//    /**
//     * 显示规格
//     * @param request
//     * @return
//     */
//    @RequestMapping("/ware/showForOptions")
//    @ResponseBody
//    public BaseResponse showForOptions(HttpServletRequest request){
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//
//        String fId=request.getParameter("fId");
//        List<Record> formatoptions=wareServices.showForOptions(fId);
//        if (formatoptions!=null)
//        {
//            baseResponse.setData(formatoptions);
//            baseResponse.setResult(ResultCodeEnum.SUCCESS);
//        }
//        else {
//            baseResponse.setResult(ResultCodeEnum.NetERROR);//空
//        }
//        return baseResponse;
//    }
//
//    /**
//     * 显示属性的下拉框
//     * @param request
//     * @return
//     */
//    @RequestMapping("/ware/showAttrOptions")
//    @ResponseBody
//    public BaseResponse showAttrOptions(HttpServletRequest request){
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//
//        return baseResponse;
//    }
//
//    @RequestMapping("/ware/addCatalog")
//    @ResponseBody
//    public BaseResponse addCatalog(HttpServletRequest request) {
//        BaseResponse baseResponse=new BaseResponse();
//        WareServicesZ wareServices=new WareServicesZ();
//        String jsons=request.getParameter("jsonArray");
//        JSONArray jsonArray=null;
//        try {
//            jsonArray= new JSONArray(jsons);
//            String fatherid=request.getParameter("fatherId");
//            String rank=request.getParameter("rank");
//            for (int i=0;i<jsonArray.length();i++){
//                org.json.JSONObject json=jsonArray.getJSONObject(i);
//                String name=json.getString("name");
//                wareServices.addCatalog(name,fatherid,rank);
//            }
//            baseResponse.setResult(ResultCodeEnum.SUCCESS);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e);
//            baseResponse.setResult(ResultCodeEnum.NetERROR);
//        }
//
//        return baseResponse;
//    }
//}
//
