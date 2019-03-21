package com.cross2u.ware.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.*;
import com.cross2u.ware.service.WareServicesZ;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@CrossOrigin
@RestController
public class WareController  {

    @Autowired
    private WareServicesZ wareServices;

    @Autowired
    private BaseResponse baseResponse;

    @RequestMapping("/ware/demo")
    @ResponseBody
    public String demmo(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        return "tongxin!"+sId;
    }



    /**
     * 显示店铺前四个商品
     */
    @RequestMapping("/ware/getTopFourWare")
    public JSONObject  getTopFourWare(HttpServletRequest request){
        String sId=request.getParameter("sId");
        JSONObject object=new JSONObject();
        JSONArray array=wareServices.getTopFourWare(sId);
        if (array!=null){
            object.put("data",array);
            object.put("result",ResultCodeEnum.SUCCESS);
        }
        else {
            object.put("result",ResultCodeEnum.FIND_FAILURE);
        }
        return object;
    }



    /**
     *显示店铺的商品
     * @param request
     * @return
     */
    @RequestMapping("/ware/showStoreWare")
    public BaseResponse showStoreWare(HttpServletRequest request){
        String wStore=request.getParameter("wStore");//店铺id
        JSONArray ware=wareServices.showStoreWare(wStore);


        if (ware!=null){
            System.out.println(ware);
            baseResponse.setData(ware);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        System.out.println("baseResponse"+baseResponse);
        return baseResponse;
    }


    /**
     * 其他模块调用
     * 显示一级或二级分类中的商品
     */
    @RequestMapping("/ware/showStoreClassWare")

    public BaseResponse showStoreClassWare(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        String wbWFDId = request.getParameter("wbWFDId");//一级id
        String wbWSDId = request.getParameter("wbWSDId");//二级id
        if (wbWSDId==null||wbWSDId.equals(""))//如果是一级
        {
           JSONArray record=wareServices.showStoreFClassWare(wbWFDId);
            baseResponse.setData(record);
        }
        else {
            JSONArray record=wareServices.showStoreSClassWare(wbWSDId);
            baseResponse.setData(record);
        }
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }


    /**
     * 显示规格
     * @param request
     * @return
     */
    @RequestMapping("/ware/showForOptions")
    @ResponseBody
    public BaseResponse showForOptions(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();

        String fId=request.getParameter("fId");
        JSONArray formatoptions=wareServices.showForOptions(fId);
        if (formatoptions!=null)
        {
            baseResponse.setData(formatoptions);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NetERROR);//空
        }
        return baseResponse;
    }

    /**
     * 显示属性的下拉框
     * @param request
     * @return
     */
    @RequestMapping("/ware/showAttrOptions")
    @ResponseBody
    public BaseResponse showAttrOptions(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();


        String atId=request.getParameter("atId");
        List<Attributeoption>attributeoptions=wareServices.showAttrOptions(atId);
        if (attributeoptions!=null){
            baseResponse.setData(attributeoptions);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/outputExcels")
    @ResponseBody
    public BaseResponse outputExcels(HttpServletResponse response,HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");//店铺id
        String wIds=request.getParameter("wIds");
        List<Record> infos =new ArrayList<>();
        if (wIds.equals("0")){
            infos = wareServices.outputExcelAll(sId);
        }
        else {
            infos=wareServices.outputExcel(sId,wIds);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("wares");
        createTitle(workbook,sheet);

        int rowNum=1;
        for (Record info:infos){
            HSSFRow row = sheet.createRow(rowNum);
            //序号 商品名称 商品起批量 商品最高批量
            HSSFCell cell = row.createCell(0);//序号
            cell.setCellValue(rowNum);

            cell=row.createCell(1);//名称
            String wTitle=info.get("wTitle");
            cell.setCellValue(wTitle);

            cell=row.createCell(2);
            String wStartNum=info.get("wStartNum").toString();//起批数目
            cell.setCellValue(wStartNum);

            cell=row.createCell(3);
            String wHighNum=info.get("wHighNum").toString();//最高批发数目
            cell.setCellValue(wHighNum);

            //商品最低价格 商品最高价格
            cell=row.createCell(4);
            Float wStartPrice=info.get("wStartPrice");//最低价格数目
            cell.setCellValue(wStartPrice);

            cell=row.createCell(5);
            Float wHighPrice=info.get("wHighPrice");//最高价格数目
            cell.setCellValue(wHighPrice);

            //// 销量 商品描述评分 商品编号
            cell=row.createCell(6);
            String wSale=info.get("wSale").toString();//最高价格数目
            cell.setCellValue(wSale);

            cell=row.createCell(7);
            Integer wDesScore=info.get("wDesScore");//商品描述评分
            cell.setCellValue(wDesScore);

            cell=row.createCell(8);
            String wIdentifier=info.get("wIdentifier");//商品编码
            cell.setCellValue(wIdentifier);
            rowNum++;
        }

        String fileName= UUID.randomUUID().toString()+".xls";

        try {
            //生成excel文件
            buildExcelFile(fileName, workbook);
            //浏览器下载excel
            buildExcelDocument(fileName,workbook,response);
            //删除本地文件
            deleteExcelFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            baseResponse.setResult(ResultCodeEnum.NetERROR);
            return baseResponse;
        }

        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }
    //创建表头
    //序号 商品名称 商品起批量 商品最高批量 商品最低价格 商品最高价格
    //销量 商品描述评分 商品编号
    private void createTitle(HSSFWorkbook workbook, HSSFSheet sheet){
        HSSFRow row = sheet.createRow(0);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("商品名称");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("商品起批量");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("商品最高批量");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("商品最低价格");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("商品最高价格");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("销量");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("商品描述评分");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("商品编号");
        cell.setCellStyle(style);
    }

    //生成excel文件
    protected void buildExcelFile(String filename,HSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }
    //删除本地文档
    protected boolean deleteExcelFile(String fileName)throws Exception{
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    //浏览器下载excel
    protected void buildExcelDocument(String filename,HSSFWorkbook workbook,HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 筛选商品 全部出售中 售完下架 已下架
     * @param request
     * @return
     */
    @RequestMapping("/ware/showAllWares")
    @ResponseBody
    public BaseResponse showAllWares(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");

        JSONArray wares=wareServices.showWares(sId,"-1");
        if (wares!=null){
            baseResponse.setData(wares);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/showWaitUpWares")
    @ResponseBody
    public BaseResponse showWaitUpWares(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");

        JSONArray wares=wareServices.showWares(sId,"1");
        if (wares!=null){
            baseResponse.setData(wares);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/showSaleWares")
    @ResponseBody
    public BaseResponse showSaleWares(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");

        JSONArray wares=wareServices.showWares(sId,"2");
        if (wares!=null){
            baseResponse.setData(wares);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/showSaleOutWares")
    @ResponseBody
    public BaseResponse showSaleOutWares(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");

        JSONArray wares=wareServices.showWares(sId,"3");
        if (wares!=null){
            baseResponse.setData(wares);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/addGetFirstCata")
    @ResponseBody
    public BaseResponse addGetFirstCata(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        List<Category> first=wareServices.addGetCata("0");
        baseResponse.setData(first);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    @RequestMapping("/ware/addGetSonCata")
    @ResponseBody
    public BaseResponse addGetSonCata(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String ctParentId=request.getParameter("ctId");
        List<Category> first=wareServices.addGetCata(ctParentId);
        baseResponse.setData(first);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     * 根据类别返回 attr 和 format
     * @param request
     * @return
     */
    @RequestMapping("/ware/addFirstStep")
    @ResponseBody
    public BaseResponse addFirstStep(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String ctSId=request.getParameter("ctSId");
        String ctTId=request.getParameter("ctTId");

        JSONObject atrAndFor=wareServices.addFirstStep(ctSId,ctTId);
        baseResponse.setData(atrAndFor);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    /**
     *创建商品
     */
    @RequestMapping("/ware/addSecondStep")
    @ResponseBody
    public BaseResponse addSecondStep(HttpServletRequest request)
    {
        BaseResponse baseResponse=new BaseResponse();
        if (wareServices.hasSensi(request.getParameter("wTitle"))){
            baseResponse.setResult(ResultCodeEnum.HAS_SENSI);
            return baseResponse;
        }
        Ware ware=new Ware();
        ware.setWClass(new BigInteger(request.getParameter("ctId")));//类别id
        ware.setWStore(new BigInteger(request.getParameter("sId")));//商品所属店铺
        ware.setWTitle(request.getParameter("wTitle"));
        ware.setWMainImage(request.getParameter("wMainImage"));//主照片
        String img1=request.getParameter("wImage1");
        if (!(img1==null||img1.equals(""))){
            ware.setWImage1(img1);
        }
        String img2=request.getParameter("wImage2");
        if (!(img2==null||img2.equals(""))){
            ware.setWImage1(img2);
        }
        String img3=request.getParameter("wImage3");
        if (!(img3==null||img3.equals(""))){
            ware.setWImage1(img3);
        }
        String img4=request.getParameter("wImage4");
        if (!(img4==null||img4.equals(""))){
            ware.setWImage1(img4);
        }
        ware.setWDescription(request.getParameter("wDescription"));
        ware.setWStartNum(new Integer(request.getParameter("wStartNum")));
        ware.setWHighNum(new Integer(request.getParameter("wHighNum")));
        ware.setWStatus(new Integer(request.getParameter("wStatus")));
        ware.setWIsReceipt(new Integer(request.getParameter("wIsReceipt")));
        ware.setWIsEnsure(new Integer(request.getParameter("wIsEnsure")));
        ware.setWIsEnsureQuality(new Integer(request.getParameter("wIsEnsureQuality")));
        ware.setWReplaceDays(new Integer(request.getParameter("wReplaceDays")));
        ware.setWDeliverHour(new Integer(request.getParameter("wDeliverHour")));
        ware.setWDeliverArea(request.getParameter("wDeliverArea"));
        ware.setWPriceUnit(Integer.valueOf(request.getParameter("pMoneyUnit")));//价格单位

        BigInteger wId=wareServices.addOneWare(ware);//保存一件商品
        if(wId==null)
        {
            baseResponse.setResult(ResultCodeEnum.ADD_WARE_FAILURE);//商品添加失败
            return baseResponse;
        }

        String attribute=request.getParameter("attribute");
        JSONObject attrObject=JSONObject.parseObject(attribute);
        System.out.println(attribute);
        JSONArray attriArray=attrObject.getJSONArray("attribute");//JSONArray.parseArray();
        for (int m=0;m<attriArray.size();m++){
            JSONObject oneAtr=attriArray.getJSONObject(m);
            Wareattribute wareattribute=new Wareattribute();
            wareattribute.setWaWare(wId);
            wareattribute.setWaAttribute(oneAtr.getBigInteger("atId"));
            wareattribute.setWaAttributeOption(oneAtr.getBigInteger("aoId"));
            if(!wareServices.saveOneAtr(wareattribute))
            {
                System.out.println("商品属性添加失败");
                //22删除以保存商品信息 删除之前商品属性信息
                baseResponse.setResult(ResultCodeEnum.ADD_WARE_FAILURE);//商品添加失败
                return baseResponse;
            }
        }

        //-----保存单品
        String products=request.getParameter("products");
        String pMoneyUnit=request.getParameter("pMoneyUnit");
        if(!saveWareProduct(products,pMoneyUnit,wId))
        {
            baseResponse.setResult(ResultCodeEnum.ADD_PRODUCT_FAILURE);//单品添加失败
            return baseResponse;
        }
        //-----商品分类
        String wbWFDId=request.getParameter("wbWFDId");
        String wbWSDId=request.getParameter("wbWSDId");
        if(!wareServices.addOneWareBelong(wbWFDId,wbWSDId,wId))
        {
            baseResponse.setResult(ResultCodeEnum.WARE_CLASS_FAILURE);//商品分类错误
            return baseResponse;
        }
        baseResponse.setData(wId);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    private boolean saveWareProduct(String products,String pMoneyUnit,BigInteger wId)
    {
        Float wareStartPrice= 0.0f;
        Float wareHighPrice= 0.0f;
        System.out.println("saveWareProduct"+products);
        JSONObject object=JSONObject.parseObject(products);

        JSONArray productArray=object.getJSONArray("products");
        for (int i=0;i<productArray.size();i++){
            JSONObject oneProduct=productArray.getJSONObject(i);
            Product product=new Product();
            product.setPWare(wId);//设置对应的wid
            product.setPImage(oneProduct.getString("pImage"));
            Float pMoney=new Float(oneProduct.getString("pMoney"));
            product.setPMoney(pMoney);
            product.setPStorage(new Integer(oneProduct.getString("pStorage")));
            product.setPMoneyUnit(new Integer(pMoneyUnit));
            BigInteger pId=wareServices.addOneProduct(product);
            if (pId==null){
                System.out.println("product 添加失败");
                return false;
            }

            //---商品最高价格&最低价格
            wareStartPrice=wareStartPrice>pMoney?pMoney:wareStartPrice;
            wareHighPrice=wareHighPrice<pMoney?pMoney:wareHighPrice;

            //---单品规格
            JSONArray format=oneProduct.getJSONArray("format");
            if(!saveProductFormat(format,pId)){
                System.out.println("productformat 创建失败");
                return false;
            }
        }
        wareServices.updateWarePrice(wareStartPrice,wareHighPrice,wId);//更新商品最高最低价格
        return true;
    }
    private boolean saveProductFormat(JSONArray formats,BigInteger pId){
        for (int i=0;i<formats.size();i++){
            JSONObject format=formats.getJSONObject(i);
            String fId=format.getString("fid");
            String fo=format.getString("foid");
            String foName=format.getString("foname");
            if(!wareServices.addOneProductFormat(fId,fo,foName,pId))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 编辑商品信息 显示商品初始信息
     * @param request
     * @return
     */
    @RequestMapping("/ware/editShow")
    @ResponseBody
    public BaseResponse editShow(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String wId=request.getParameter("wId");//商品id
        String mId=request.getParameter("mId");//店铺id
        JSONObject ware=wareServices.editShow(wId,mId);
        if (ware!=null)
        {
            if (wareServices.hasINGIndent(wId))
            {
                baseResponse.setResult(ResultCodeEnum.HAS_ING_INDENT);//存在正在进行的订单
                baseResponse.setData(ware);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
                baseResponse.setData(ware);
            }
        }
        return baseResponse;
    }


    @RequestMapping("/ware/editSubmit")
    @ResponseBody
    public BaseResponse editSubmit(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();

        String wIdOld=request.getParameter("wId");
        Boolean undercarriage = wareServices.editUndercarriage(wIdOld);//下架原来的商品

        baseResponse=addSecondStep(request);//创建一个新的
        return baseResponse;
    }

    @RequestMapping("/ware/editSubmitOld")
    @ResponseBody
    public BaseResponse editSubmitOld(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();

        String sId=request.getParameter("sId");
        String wId=request.getParameter("wId");
        Ware ware=wareServices.getWareById(wId);
        ware.setWTitle(request.getParameter("wTitle"));
        ware.setWMainImage(request.getParameter("wMainImage"));
        ware.setWImage1(request.getParameter("wImage1"));
        ware.setWImage2(request.getParameter("wImage2"));
        ware.setWImage3(request.getParameter("wImage3"));
        ware.setWImage4(request.getParameter("wImage4"));
        ware.setWDescription(request.getParameter("wDescription"));
        ware.setWStartNum(Integer.valueOf(request.getParameter("wStartNum")));
        ware.setWHighNum(Integer.valueOf(request.getParameter("wHighNum")));
        ware.setWStatus(Integer.valueOf(request.getParameter("wStatus")));
        String wbWFDId=request.getParameter("wbWFDId");
        String wbWSDId=request.getParameter("wbWSDId");
        String wbId=request.getParameter("wbId");
        if(!wareServices.updateWareBelong(wbId,wbWFDId,wbWSDId))
        {
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        else {
            if(!wareServices.updateOneWare(ware))
            {
                baseResponse.setResult(ResultCodeEnum.NetERROR);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
        }

        return baseResponse;
    }


    @RequestMapping("/ware/editDelete")
    @ResponseBody
    public BaseResponse editDelete(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        String wId=request.getParameter("wId");
        if(wareServices.hasINGIndent(wId))
        {
            baseResponse.setResult(ResultCodeEnum.HAS_ING_INDENT);
        }
        else {
            if(wareServices.editDelete(wId))
            {
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.DELETE_ERROR);
            }
        }
        return baseResponse;
    }

    @RequestMapping("/ware/editUndercarriage")
    @ResponseBody
    public BaseResponse editUndercarriage(HttpServletRequest request)
    {
        BaseResponse baseResponse = new BaseResponse();
        String wId=request.getParameter("wId");
        if (wareServices.editUndercarriage(wId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }


    @RequestMapping("/ware/editupcarriage")
    @ResponseBody
    public BaseResponse editupcarriage(HttpServletRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        String wId=request.getParameter("wId");
        if (wareServices.editupcarriage(wId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/batchSelectDispatchs")
    @ResponseBody
    public BaseResponse batchSelectDispatchs(HttpServletRequest request )
    {
        BaseResponse baseResponse=new BaseResponse();
        String wIds=request.getParameter("wIds");
        String wsdId=request.getParameter("wsdId");
        String wfdId=request.getParameter("wfdId");
        String [] wIdStrs=wIds.split(",");
        String hasInDis="";
        for (String wId:wIdStrs){
            if (wareServices.dispatchIsIn(wfdId,wsdId,wId)){//已经存在
                hasInDis=hasInDis+wareServices.getWareTitleById(wId)+"   ";
                continue;
            }
            else {
                if (wareServices.dispatchAddWare(wfdId,wsdId,wId)==null){//没有加进去
                    hasInDis=hasInDis+wareServices.getWareTitleById(wId)+"   ";
                    continue;
                }
            }
        }
        if(hasInDis.equals("")){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setData(hasInDis);
            baseResponse.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return baseResponse;
    }


    @RequestMapping("/ware/batchChangeMoney")
    @ResponseBody
    public BaseResponse batchChangeMoney(HttpServletRequest request )
    {
        BaseResponse baseResponse=new BaseResponse();
        String wIds=request.getParameter("wIds");

        String[] wIdStr=wIds.split(",");//判断是否有正在执行的订单
        for (String wId:wIdStr){
            if(wareServices.hasINGIndent(wId)){
                baseResponse.setResult(ResultCodeEnum.HAS_ING_INDENT);
                return baseResponse;
            }
        }

        String money=request.getParameter("money");
        String unit=request.getParameter("unit");
        if(wareServices.batchChangeMoney(wIds,money,unit))
        {
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/batchChangeOriginMoney")
    @ResponseBody
    public BaseResponse batchChangeOriginMoney(HttpServletRequest request )
    {
        BaseResponse baseResponse=new BaseResponse();
        String wIds=request.getParameter("wIds");

        String[] wIdStr=wIds.split(",");//判断是否有正在执行的订单
        for (String wId:wIdStr){
            if(wareServices.hasINGIndent(wId)){
                baseResponse.setResult(ResultCodeEnum.HAS_ING_INDENT);
                return baseResponse;
            }
        }

        String calculate=request.getParameter("calculate");
        String number=request.getParameter("number");
        switch (calculate){
            case "1"://加
                if(!wareServices.addMoney(wIds,number))
                    baseResponse.setResult(ResultCodeEnum.NetERROR);
                else
                    baseResponse.setResult(ResultCodeEnum.SUCCESS);
                break;
            case "2"://减
                if(!wareServices.subMoney(wIds,number))
                    baseResponse.setResult(ResultCodeEnum.NetERROR);
                else
                    baseResponse.setResult(ResultCodeEnum.SUCCESS);
                break;
            case "3"://乘
                if(!wareServices.mulMoney(wIds,number))
                    baseResponse.setResult(ResultCodeEnum.NetERROR);
                else
                    baseResponse.setResult(ResultCodeEnum.SUCCESS);
                break;
            case "4"://除
                if (!wareServices.divideMoney(wIds,number))
                    baseResponse.setResult(ResultCodeEnum.NetERROR);
                else
                    baseResponse.setResult(ResultCodeEnum.SUCCESS);
                break;
            default:
                baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }


    @RequestMapping("/ware/batchDeleteMany")
    @ResponseBody
    public BaseResponse batchDeleteMany(HttpServletRequest request ) {
        BaseResponse baseResponse = new BaseResponse();
        String wIds=request.getParameter("wIds");
        String[] wIdStr=wIds.split(",");//判断是否有正在执行的订单
        for (String wId:wIdStr){
            if(wareServices.hasINGIndent(wId)){
                baseResponse.setResult(ResultCodeEnum.HAS_ING_INDENT);
                return baseResponse;
            }
        }

        for (String wId:wIdStr){
            if (!wareServices.editDelete(wId)){
                baseResponse.setData(wId);
                baseResponse.setResult(ResultCodeEnum.DELETE_ERROR);
                return baseResponse;
            }
        }
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }


    @RequestMapping("/ware/batchUndercarriage")
    @ResponseBody
    public BaseResponse batchUndercarriage(HttpServletRequest request ) {
        BaseResponse baseResponse = new BaseResponse();
        String wIds=request.getParameter("wIds");
        String[] wIdStr=wIds.split(",");
        for (String wId:wIdStr){
            if(!wareServices.editUndercarriage(wId))
            {
                baseResponse.setResult(ResultCodeEnum.NetERROR);
                baseResponse.setData(wId);
                return baseResponse;
            }
        }
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    @RequestMapping("/ware/dispatchShowWares")
    @ResponseBody
    public BaseResponse dispatchShowWares(HttpServletRequest request)
    {
        String wfdId=request.getParameter("wfdId");
        String wsdId=request.getParameter("wsdId");
        System.out.println("wfdId"+wfdId+"wsdId"+wsdId);
        if (wfdId!=null && (wsdId==null||wsdId.equals(""))){
            JSONArray array=wareServices.getWFDWares(wfdId);
            System.out.println("wfdId不是null"+array);
            if(!array.isEmpty())
            {
                baseResponse.setData(array);
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
            }
        }
        else if((wfdId==null||wfdId.equals("")) && wsdId!=null)
        {
            JSONArray array=wareServices.getWSDWares(wsdId);
            System.out.println("wsdId不是null"+array);
            if(!array.isEmpty())
            {
                baseResponse.setData(array);
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else {
                baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
            }
        }
        else {
            System.out.println("都是null");
            baseResponse.setResult(ResultCodeEnum.NetERROR);
        }
        return baseResponse;
    }

    /**
     * 显示店铺 商品排序
     */
    @RequestMapping("/ware/showStoreWareRank")
    @ResponseBody
    public BaseResponse showStoreWareRank(HttpServletRequest request){
        String wStore=request.getParameter("wStore");
        String operation=request.getParameter("operation");
        String rank=request.getParameter("rank");
        JSONArray ware=null;
        switch (operation){
            case "1"://sale
                ware=wareServices.showStoreWareBySale(wStore,rank);
                break;
            case "2"://price
                ware=wareServices.showStoreWareByPrice(wStore,rank);
                break;
            case "3"://上新时间
                ware=wareServices.showStoreWareByTime(wStore,rank);
                break;
                default:baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);

        }
        if(ware!=null){
            baseResponse.setData(ware);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/dispatchAddWare")
    @ResponseBody
    public BaseResponse dispatchAddWare(HttpServletRequest request)
    {
        String wfdId=request.getParameter("wfdId");
        String wsdId=request.getParameter("wsdId");
        String wId=request.getParameter("wId");
        if (wareServices.dispatchIsIn(wfdId,wsdId,wId)){//在分类中已经有这个商品
            baseResponse.setResult(ResultCodeEnum.DISPATCH_IS_IN);
        }
        else {
            Long wbId=wareServices.dispatchAddWare(wfdId,wsdId,wId);
            if (wbId!=null){
                baseResponse.setData(wbId);
                baseResponse.setResult(ResultCodeEnum.SUCCESS);
            }
            else baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);
        }

        return baseResponse;
    }

    @RequestMapping("/ware/dispatchDeleteWare")
    @ResponseBody
    public BaseResponse dispatchDeleteWare(HttpServletRequest request)
    {
        String wbId=request.getParameter("wbId");
        if (wareServices.dispatchDeleteWare(wbId)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else baseResponse.setResult(ResultCodeEnum.DELETE_ERROR);
        return baseResponse;
    }


    @RequestMapping("/ware/showGoodEval")
    @ResponseBody
    public BaseResponse showGoodEval(HttpServletRequest request)
    {
        System.out.println("--------------------good eval--------------");
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        JSONArray array=wareServices.showGoodEval(sId);
        if (!array.isEmpty()){
            System.out.println("goodEval"+array);
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        System.out.println("--------------------good eval--------------");
        return baseResponse;
    }

    /**
     * 显示中评价列表（3星）
     * @param request
     * @return
     */
    @RequestMapping("/ware/showNormalEval")
    public BaseResponse showNormalEval(HttpServletRequest request){
        System.out.println("--------------------normal eval--------------");
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        JSONArray array=wareServices.showNormalEval(sId);
        if (array==null||array.isEmpty()){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            System.out.println("normal"+array);
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        System.out.println("--------------------normal eval--------------");
        return baseResponse;
    }

    @RequestMapping("/ware/showBadEval")
    public BaseResponse showBadEval(HttpServletRequest request){
        System.out.println("--------------------bad eval--------------");
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        JSONArray array=wareServices.showBadEval(sId);
        if (array==null||array.isEmpty()){
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        else {
            System.out.println("bad"+array);
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        System.out.println("--------------------bad eval--------------");
        return baseResponse;
    }


    /**
     * 显示已被举报的评论
     * @param request
     * @return
     */
    @RequestMapping("/ware/showComEval")
    public BaseResponse showComEval(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        JSONArray array=wareServices.showComEval(sId);
        if(array!=null){
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 显示评论详情
     * @param request
     * @return
     */
    @RequestMapping("/ware/showEvalInfo")
    public BaseResponse showEvalInfo(HttpServletRequest request){
        String sId=request.getParameter("sId");
        String ewId=request.getParameter("ewId");
        JSONObject object=wareServices.showEvalInfo(sId,ewId);
        if (object!=null){
            baseResponse.setData(object);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 店铺动态评分
     * /ware/showStoreEval
     */
    @RequestMapping("/ware/showStoreEval")
    public BaseResponse showStoreEval(HttpServletRequest request){
        String sId=request.getParameter("sId");
        JSONObject object=wareServices.showStoreEval(sId);
        if (object!=null){
            baseResponse.setData(object);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }
    /**
     * 店铺评分统计
     */

    @RequestMapping("/ware/showStaticEval")
    public BaseResponse showStaticEval(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        JSONObject object=wareServices.showStaticEval(sId);
        if (object!=null){
            baseResponse.setData(object);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 举报异常评价
     * @param request
     * @return
     */
    @RequestMapping("/ware/reportStoreEval")
    public BaseResponse reportStoreEval(HttpServletRequest request){
        String sId=request.getParameter("sId");
        String ewId=request.getParameter("ewId");
        String aerType=request.getParameter("aerType");
        String aerContent=request.getParameter("aerContent");

        if (wareServices.reportStoreEval(sId,ewId,aerType,aerContent)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.ADD_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 显示已举报评论的详情
     */

    @RequestMapping("/ware/showComInfo")
    public BaseResponse showComInfo(HttpServletRequest request){
        String sId=request.getParameter("sId");
        String ewId=request.getParameter("ewId");
        JSONObject object=wareServices.showComInfo(sId,ewId);
        if (object!=null){
            baseResponse.setData(object);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }

    /**
     * 回复评论
     */

    @RequestMapping("/ware/replyEval")
    public BaseResponse replyEval(HttpServletRequest request){
        String ewId=request.getParameter("ewId");
        String ewReply=request.getParameter("ewReply");
        if (wareServices.replyEval(ewId,ewReply)){
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        return baseResponse;
    }

    @RequestMapping("/ware/showReportReasons")
    public BaseResponse showReportReasons(HttpServletRequest request){
        JSONArray array=wareServices.showReportReasons();
        if (array!=null){
            baseResponse.setData(array);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.FIND_FAILURE);
        }
        return baseResponse;
    }
}
