package com.cross2u.ware.controller;

import com.alibaba.fastjson.JSON;
import com.cross2u.ware.model.*;
import com.cross2u.ware.service.WareServicesZ;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Record;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

public class WareController  {

    @Autowired
    private WareServicesZ wareServices=new WareServicesZ();

    @RequestMapping("/ware/demo")
    @ResponseBody
    public BaseResponse demmo(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();

        return baseResponse;
    }

    @RequestMapping("/ware/showFDispatch")
    @ResponseBody
    public BaseResponse showFDispatch(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();

        String wfdSId=request.getParameter("wfdSId");//店铺id
        List<Warefdispatch> warefdispatchs=wareServices.showFDispatch(wfdSId);
        if (warefdispatchs!=null)
        {
            baseResponse.setData(warefdispatchs);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NOT_FIND);//空
        }
        return baseResponse;
    }

    @RequestMapping("/ware/showSDispatch")
    @ResponseBody
    public BaseResponse showSDispatch(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();

        String wfdId=request.getParameter("wfdId");

        List<Waresdispatch> waresdispatches=wareServices.showSDispatch(wfdId);
        if (waresdispatches!=null)
        {
            baseResponse.setData(waresdispatches);
            baseResponse.setResult(ResultCodeEnum.SUCCESS);
        }
        else {
            baseResponse.setResult(ResultCodeEnum.NOT_FIND);//空
        }
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
        List<Record> formatoptions=wareServices.showForOptions(fId);
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
            String wStartNum=info.get("wStartNum");//起批数目
            cell.setCellValue(wStartNum);

            cell=row.createCell(3);
            String wHighNum=info.get("wHighNum");//最高批发数目
            cell.setCellValue(wHighNum);

            //商品最低价格 商品最高价格
            cell=row.createCell(4);
            String wStartPrice=info.get("wStartPrice");//最低价格数目
            cell.setCellValue(wStartPrice);

            cell=row.createCell(5);
            String wHighPrice=info.get("wHighPrice");//最高价格数目
            cell.setCellValue(wHighPrice);

            //// 销量 商品描述评分 商品编号
            cell=row.createCell(6);
            String wSale=info.get("wSale");//最高价格数目
            cell.setCellValue(wSale);

            cell=row.createCell(7);
            String wDesScore=info.get("wDesScore");//商品描述评分
            cell.setCellValue(wDesScore);

            cell=row.createCell(8);
            String wIdentifier=info.get("wIdentifier");//商品编码
            cell.setCellValue(wIdentifier);
            rowNum++;
        }

        String fileName= UUID.randomUUID().toString();

        try {
            //生成excel文件
            buildExcelFile(fileName, workbook);
            //浏览器下载excel
            buildExcelDocument(fileName,workbook,response);
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
    @RequestMapping("/ware/showWares")
    @ResponseBody
    public BaseResponse showWares(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String sId=request.getParameter("sId");
        String operation=request.getParameter("operation");

        List<Record>wares=wareServices.showWares(sId,operation);
        return baseResponse;
    }


    @RequestMapping(" /ware/addGetFirstCata")
    @ResponseBody
    public BaseResponse addGetFirstCata(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        List<Category> first=wareServices.addGetCata("0");
        baseResponse.setData(first);
        baseResponse.setResult(ResultCodeEnum.SUCCESS);
        return baseResponse;
    }

    @RequestMapping(" /ware/addGetSonCata")
    @ResponseBody
    public BaseResponse addGetSonCata(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();
        String ctParentId=request.getParameter("ctId");
        List<Category> first=wareServices.addGetCata(ctParentId);
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

        Record atrAndFor=wareServices.addFirstStep(ctSId,ctTId);
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

        Ware ware=new Ware();
        ware.setWClass(new BigInteger(request.getParameter("ctId")));//类别id
        ware.setWStore(new BigInteger(request.getParameter("sId")));//商品所属店铺
        ware.setWTitle(request.getParameter("wTitle"));
        ware.setWMainImage(request.getParameter("wMainImage"));//主照片
        ware.setWImage1(request.getParameter("wImage1"));
        ware.setWImage2(request.getParameter("wImage2"));
        ware.setWImage3(request.getParameter("wImage3"));
        ware.setWImage4(request.getParameter("wImage4"));
        ware.setWDescription(request.getParameter("wDescription"));
        ware.setWStartNum(new Long(request.getParameter("wStartNum")));
        ware.setWHighNum(new Long(request.getParameter("wHighNum")));
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

        JSONArray attriArray=new JSONArray(request.getParameter("attribute"));
        for (int m=0;m<attriArray.length();m++){
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
        Warebelong warebelong=new Warebelong();
        warebelong.setWbWFDId(new Long (request.getParameter("wbWFDId")));
        warebelong.setWbWSDId(new Long(request.getParameter("wbWSDId")));
        warebelong.setWbWId(Long.valueOf(wId.toString()));
        if(!wareServices.addOneWareBelong(warebelong))
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
        JSONArray productArray=new JSONArray(products);
        for (int i=0;i<productArray.length();i++){
            JSONObject oneProduct=productArray.getJSONObject(i);
            Product product=new Product();
            product.setPImage(oneProduct.getString("pImage"));
            Float pMoney=new Float(oneProduct.getString("pMoney"));
            product.setPMoney(pMoney);
            product.setPStorage(new BigInteger(oneProduct.getString("pStorage")));
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
            if(saveProductFormat(format,pId)){
                System.out.println("productformat 创建失败");
                return false;
            }
        }
        wareServices.updateWarePrice(wareStartPrice,wareHighPrice,wId);//更新商品最高最低价格
        return true;
    }
    private boolean saveProductFormat(JSONArray formats,BigInteger pId){
        for (int i=0;i<formats.length();i++){
            JSONObject format=formats.getJSONObject(i);
            String fId=format.getString("fId");
            String fo=format.getString("fo");
            if(!wareServices.addOneProductFormat(fId,fo,pId))
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
        Record ware=wareServices.editShow(wId,mId);
        return baseResponse;
    }


    @RequestMapping("/ware/editSubmit")
    @ResponseBody
    public BaseResponse editSubmit(HttpServletRequest request){
        BaseResponse baseResponse=new BaseResponse();

        return baseResponse;
    }
}
