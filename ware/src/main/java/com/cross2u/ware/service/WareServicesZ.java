package com.cross2u.ware.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.*;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.For;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WareServicesZ {
    @Autowired
    RestTemplate restTemplate;


    //根据id找ware
    public Ware getWareById(String wId) {
        return Ware.dao.findById(wId);
    }

    //下架商品
    public Boolean editUndercarriage(String wId) {
        Ware ware=getWareById(wId);
        ware.setWStatus(1);
        return ware.update();
    }

    //根据规格id显示选项
    public List<Record> showForOptions(String fId) {
        String sql="SELECT foId,foName " +
                " from formatoption  " +
                " WHERE foFormat=?";
        List<Record> formatoptions= Db.find(sql,fId);
        for (Record formatoption : formatoptions)
        {
            String sonSql="SELECT foId,foName " +
                    " from formatoption " +
                    " WHERE foParentOption=?";
            List<Formatoption> son=Formatoption.dao.find(sql,fId);
            formatoption.get("son",son);
        }
        return formatoptions;
    }

    //根据属性id显示属性的选项
    public List<Attributeoption> showAttrOptions(String atId) {
        String sql="select aoName,aoId from Attributeoption where aoAttribute=?";
        List<Attributeoption> attributeoptions=Attributeoption.dao.find(sql,atId);
        return attributeoptions;
    }

    public List<Record> outputExcelAll(String sId) {
        String sql="SELECT wId,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wDesScore,wIdentifier " +
                " from ware " +
                " WHERE wStore=?";
        List<Record> wares=Db.find(sql,sId);
        for (int i=0;i<wares.size();i++)
        {
            Record ware=wares.get(i);
            Record wSale=getSale(ware.get("wId"));
            ware.setColumns(wSale);
        }
        return wares;
    }
    private Record getSale(String wId)
    {
        String sql="SELECT sum(inProductNum) as wSale " +
                " from indent " +
                " WHERE inWare=?";
        return Db.findFirst(sql,wId);
    }

    public List<Record> outputExcel(String sId, String wIds) {
        List<Record> records=new ArrayList<>();
        String[] wId=wIds.split(",");
        for (String id :wId){
            Record base=getOneWare(id);
            Record sale=getSale(id);
            Record one=base.setColumns(sale);
            records.add(one);
        }
        return records;
    }
    private Record getOneWare(String wId)
    {
        String sql="SELECT wId,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wDesScore,wIdentifier " +
                " from ware " +
                " WHERE wId =?";
        return Db.findFirst(sql,wId);
    }

    public List<Record> showWares(String sId, String operation) {
        String selectSql="SELECT wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit,sum(inProductNum) as wSale,sum(pStorage) as wStorage,wStatus,wCreateTime " +
                " from (ware INNER JOIN indent on inWare=wId)INNER JOIN product on wId=pWare " +
                " WHERE wStore=? and inStatus!=0 and inStatus!=5 and inStatus!=6 ";
        String andSql="";
        switch (operation)
        {
            case "-1"://所有
                        break;
            case "1"://未上架的 放入库存+违规下架
                        andSql=" and (wStatus =1 or wStatus =4)";
                        break;
            case "2"://出售中
                andSql=" and wStatus = 2";
                break;
            case "3"://已售完
                andSql=" and wStatus =3";
                break;
                default: return null;
        }
        String sql=selectSql+andSql;
        List<Record> wares=Db.find(sql,sId);
        for (Record ware :wares){
            Record belong=getWBelong(ware.get("wId"));
            ware.setColumns(belong);
        }
        return wares;
    }
    private Record getWBelong(String wId)
    {
        String sql="SELECT wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wbWFDId=wfdId) INNER JOIN waresdispatch on wbWSDId=wsdId " +
                " WHERE wbWId=?";
        return Db.findFirst(sql,wId);
    }

    public List<Category> addGetCata(String ctParantId) {
        String sql="select ctId,ctName from category WHERE ctParentId=?";
        return Category.dao.find(sql,ctParantId);
    }

    public List<Record> getAtr(String ctSId, String ctTId){
        //-------全部类别都有的Atr
        String allHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=0";
        List<Record> allAtr=Db.find(allHaveAtr);
        for (Record allOne:allAtr){
            List<Record> options=getAtrOptions(allOne.get("caId").toString());
            allOne.set("atOption",options);
        }

        //-------二级类别有的Atr
        String sHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> sAtr=Db.find(sHaveAtr,ctSId);
        for (Record sOne:sAtr) {
            List<Record> options = getAtrOptions(sOne.get("caId").toString());
            sOne.set("atOption", options);
        }

        //-------三级类别有的Atr
        String tHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> tAtr=Db.find(tHaveAtr,ctTId);
        for (Record tOne:tAtr){
            List<Record> options=getAtrOptions(tOne.get("caId").toString());
            tOne.set("atOption",options);
        }

        allAtr.addAll(sAtr);
        allAtr.addAll(tAtr);
        return allAtr;
    }

    private List<Record> getFor(String ctSId,String ctTId)
    {
        //-----全部类别都有的format
        String allHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=0";
        List<Record> allFor=Db.find(allHaveFor);
        for (Record allOne:allFor){
            List<Record> options=getFroOptions(allOne.get("fId").toString());
            allOne.set("fOption",options);
        }


        //-----二级类别有的format
        String sHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> sFor=Db.find(sHaveFor,ctSId);
        for (Record sOne:sFor){
            List<Record> options=getFroOptions(sOne.get("fId").toString());
            sOne.set("fOption",options);
        }


        //-----三级类别有的format
        String tHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> tFor=Db.find(tHaveFor,ctTId);
        for (Record tOne:tFor){
            List<Record> options=getFroOptions(tOne.get("fId").toString());
            tOne.set("fOption",options);
        }



        allFor.addAll(sFor);
        allFor.addAll(tFor);
        return  allFor;
    }
    public Record addFirstStep(String ctSId, String ctTId) {
        Record atrAndFor=new Record();
        List<Record> allAtr=getAtr(ctSId,ctTId);
        List<Record> allFor=getFor(ctSId,ctTId);

        atrAndFor.set("attribute",allAtr);
        atrAndFor.set("format",allFor);
        return atrAndFor;
    }

    private List<Record> getFroOptions(String fId) {
        String sql="SELECT foId,foName " +
                " from formatoption INNER JOIN format on foFormat=fId " +
                " WHERE fId=?";
        return Db.find(sql,fId);
    }

    private List<Record> getAtrOptions(String atId)
    {
        String sql="SELECT aoId,aoName " +
                " from attributeoption " +
                " WHERE aoAttribute=? ";
        return Db.find(sql,atId);
    }

    public BigInteger addOneWare(Ware ware) {
        String random=UUID.randomUUID().toString().replaceAll("-","").substring(15);
       ware.setWIdentifier(random);
        if (ware.getWStatus().equals("2")){
            Date date = new Date();
            Timestamp nowdate = new Timestamp(date.getTime());//当前时刻
            ware.setWOnSaleTime(nowdate);
        }
        ware.save();
        return ware.getWId();
    }

    public boolean addOneWareBelong(String wbWFDId, String wbWSDId, BigInteger wId) {
        Record wbEntity=new Record();
        wbEntity.set("wbWFDId",wbWFDId);
        wbEntity.set("wbWSDId",wbWSDId);
        wbEntity.set("wId",wId);

        BaseResponse response = restTemplate.getForObject("http://Store/store/addOneWareBelong?wId="+wId+"&wbWFDId="+wbWFDId+"&wbWSDId="+wbWSDId,BaseResponse.class);
        System.out.println("ws response"+response);
        return (response.getResultCode()).equals("10000");
    }

    public boolean addOneProductFormat(String fId, String fo,BigInteger pId) {
        Productformat productformat=new Productformat();
        productformat.setPfFormat(new BigInteger(fId));
        productformat.setPfProduct(pId);
        if(isNumeric("fo"))//是数字
        {
            productformat.setPfFormatOption(new BigInteger(fo));
        }
        else {
            productformat.setPfDefineOption(fo);//自定义选项
        }
        return productformat.save();
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public BigInteger addOneProduct(Product product) {
        String random=UUID.randomUUID().toString().replaceAll("-","").substring(20);
        product.setPIdentifier(random);
        product.save();
        return product.getPId();
    }

    public boolean saveOneAtr(Wareattribute wareattribute) {
        return wareattribute.save();
    }


    public void updateWarePrice(Float wareStartPrice, Float wareHighPrice,BigInteger wId) {
        Ware ware=Ware.dao.findByIdLoadColumns(wId,"wId");
        ware.setWStartPrice(wareStartPrice);
        ware.setWHighPrice(wareHighPrice);
        ware.update();
    }

    public Record editShow(String wId, String mId) {

        /**
         * ware的基本信息 record
         * ware belong
         * wareAtr id name option:{id name}
         * format id name option{id name}
         * product {fid fo} pMoney pImg pIdentifier pStorage pMoneyUnit
         *
         */
        String waresql="SELECT wStore,wMainImage,wImage1,wImage2,wImage3,wImage4,wTitle,wIdentifier,wDeliverArea,wStartNum,wHighNum,wDescription,wIsEnsure,wIsEnsureQuality,wDeliverHour,wIsReceipt,wReplaceDays " +
                " from ware " +
                " WHERE wId=?";
        Record ware=Db.findFirst(waresql,wId);

        String wbelongSql="SELECT wbId,wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wfdId=wbWFDId ) INNER JOIN waresdispatch on wsdId=wbWSDId " +
                " WHERE wbWId=?";
        Record wareBelong=Db.findFirst(wbelongSql,wId);

        String caSql="SELECT c.ctId as caFirst,c.ctName as caFName, a.ctId as caSecond,a.ctName as caSName,b.ctId as caThird,b.ctName as caTName " +
                "FROM ((category a INNER JOIN category b on a.ctId=b.ctParentId) INNER JOIN ware on b.ctId=wClass) INNER JOIN category c on c.ctId=a.ctParentId " +
                "WHERE wId=? ";
        Record category=Db.findFirst(caSql,wId);//父子id

        List<Record> atrNames=getAtr(category.getStr("caSecond"),category.getStr("caThird"));//属性名称
        for (Record atrName :atrNames)
        {
            String atrOptionSql="SELECT aoId,aoName " +
                    "FROM attributeoption INNER JOIN wareattribute on aoId=waAttributeOption " +
                    "WHERE aoAttribute=？";//属性的选项 未选则为空
            Record atrOption=Db.findFirst(atrOptionSql,atrName.get("atId"));
            atrName.setColumns(atrOption);
        }
        //----规格
        List<Record> foNames=getFor(category.getStr("caSecond"),category.getStr("caThird"));//规格名称
        for (Record foName:foNames){
            String foOptionsSql="SELECT foId,foName, " +
                    " CASE" +
                    "  WHEN foId IN (SELECT pfFormatOption from productformat INNER JOIN product on pfProduct=pId WHERE pWare like '"+wId+"')  " +
                    "  THEN 1 " +
                    "  WHEN foId not IN (SELECT pfFormatOption from productformat INNER JOIN product on pfProduct=pId WHERE pWarelike '"+wId+"')  " +
                    "  THEN 0 " +
                    " END isSelect " +
                    " from (format INNER JOIN formatoption on foFormat=fId)  " +
                    " WHERE fId=? ";
            List<Record> foOptions=Db.find(foOptionsSql,foName.get("fId"));
            String defineOptionSql="SELECT 0 as foId pfDefineOption as foName,'1' as isSelect" +
                    "from productformat inner join product on pfProduct=product.pId " +
                    "where pfFormat=? and pWare=? and pfDefineOption is not NULL";
            List<Record> defineOption=Db.find(defineOptionSql,foName.get("fId"),wId);//自定义选项
            foOptions.addAll(defineOption);
            foName.set("foOptions",foOptions);
        }
        //----单品
        String productSql="SELECT pId,pMoney,pImage,pIdentifier,pStorage,pMoneyUnit" +
                "from product" +
                "WHERE pWare=?";
        List<Record> products=Db.find(productSql,wId);
        for (Record product:products){
            List<Record>  productFormat=getProductFormat(product.get("pId"));
            product.set("productFormat",productFormat);
        }

        ware.setColumns(wareBelong);
        ware.set("category",category);//商品类别
        ware.set("attribute",atrNames);//商品属性 atId atName aoId aoName
        ware.set("format",foNames);//商品规格 fId fName foOptions:{foId,foName,isSelect}
        ware.set("products",products);//单品 pId pMoney pImage pIdentifier pStorage pMoneyUnit productFormat{fId,fName,{foId,fName}}
        return ware;
    }

    private  List<Record>  getProductFormat(Object pId) {
        String formatSql="SELECT fId,fName  " +
                "FROM productformat INNER JOIN format on pfFormat=fId  " +
                "WHERE pfProduct=?";
        List<Record> formats=Db.find(formatSql,pId);
        for (Record format: formats){
            String productfosql="SELECT foId,foName  " +
                    "from (productformat INNER JOIN format on fId=pfFormat) INNER JOIN formatoption on foId=pfFormatOption " +
                    "WHERE pfProduct=? and fId=?";
            Record option=Db.findFirst(productfosql,pId,format.get("fId"));
            String defineSql="SELECT '0' as foId ,pfDefineOption as foName" +
                    "from productformat INNER JOIN format on fId=pfFormat " +
                    "WHERE pfProduct=? and fId=? and pfDefineOption is not NULL";
            Record define=Db.findFirst(defineSql,pId,format.get("fId"));//自定义
            if (define==null)
            {
                format.setColumns(option);
            }
            else {
                format.setColumns(define);
            }
        }
        return formats;
    }


    public boolean hasINGIndent(String wId) {
        boolean hasIndent = restTemplate.getForObject("http://Indent/indent/hasINGIndent?wId="+wId,Boolean.class);
        return hasIndent;
    }


    public boolean updateWareBelong(String wbId, String wbWFDId, String wbWSDId) {
        BaseResponse baseResponse = restTemplate.getForObject("http://Store/store/updateWareBelong?wbId="+wbId+"&wbWFDId="+wbWFDId+"&wbWFDId="+wbWSDId,BaseResponse.class);
        return baseResponse.getResultCode().equals("10000");
    }

    public boolean updateOneWare(Ware ware) {
        return ware.update();
    }

    public boolean editDelete(String wId) {
        Ware ware=Ware.dao.findById(wId);
        ware.setWStatus(0);
        return ware.update();
    }

    public boolean editupcarriage(String wId) {
        Ware ware=Ware.dao.findById(wId);
        ware.setWStatus(2);
        return ware.update();
    }

    public boolean batchSelectDispatchs(String wIds, String wfdId,String wsdId) {
        String[] widStr=wIds.split(",");
        for (String wId:widStr){
            if(!addOneWareBelong(wfdId, wsdId,new BigInteger (wId)))
            {
                return false;
            }
        }
        return true;
    }

    public boolean batchChangeMoney(String wIds, String money, String unit) {
        String[] wIdstr=wIds.split(",");
        String sql="select * from product where pWare=?";
        for (String wId:wIdstr){
            Ware ware=Ware.dao.findById(wId);
            ware.setWStartPrice(new Float(money));
            ware.setWHighPrice(new Float(money));
            ware.setWPriceUnit(new Integer(unit));
            List<Product> products=Product.dao.find(sql,wId);
            for (Product product:products)
            {
                product.setPMoney(new Float(money));
                product.setPMoneyUnit(new Integer(unit));
                if (!product.update())
                {
                    return false;
                }
            }
            if (!ware.update()){
                return false;
            }
        }
        return true;
    }


    public boolean mulMoney(String wIds, String number) {
        String[] wIdStr=wIds.split(",");
        Float change=new Float(number);
        String sql="select * from product where pWare=?";
        for (String wId:wIdStr){
            Ware ware=Ware.dao.findById(wId);
            Float originStartMoney=ware.getWStartPrice();
            Float originHighMoney=ware.getWHighPrice();
            Float startMoney=originStartMoney*change;
            Float highMoney=originHighMoney*change;
            ware.setWStartPrice(startMoney);
            ware.setWHighPrice(highMoney);
            List<Product> products=Product.dao.find(sql,wId);
            for (Product product:products){
                Float pOriginStart=product.getPMoney();
                Float pLaterStart=pOriginStart*change;
                product.setPMoney(pLaterStart);
                if (!product.update()){
                    return false;
                }
            }
            if (!ware.update()){
                return false;
            }
        }
        return true;
    }

    //在原价格基础上减去
    public boolean subMoney(String wIds, String number) {
        String[] wIdStr=wIds.split(",");
        Float change=new Float(number);
        String sql="select * from product where pWare=?";
        for (String wId:wIdStr){
            Ware ware=Ware.dao.findById(wId);
            Float originStartMoney=ware.getWStartPrice();
            Float originHighMoney=ware.getWHighPrice();
            Float startMoney=originStartMoney-change;
            if (startMoney<0) {return false;}
            Float highMoney=originHighMoney-change;
            ware.setWStartPrice(startMoney);
            ware.setWHighPrice(highMoney);
            List<Product> products=Product.dao.find(sql,wId);
            for (Product product:products){
                Float pOriginStart=product.getPMoney();
                Float pLaterStart=pOriginStart-change;
                product.setPMoney(pLaterStart);
                if (!product.update()){
                    return false;
                }
            }
            if (!ware.update()){
                return false;
            }
        }
        return true;
    }

    //在原价格上加上
    public boolean addMoney(String wIds, String number) {
        String[] wIdStr=wIds.split(",");
        Float change=new Float(number);
        String sql="select * from product where pWare=?";
        for (String wId:wIdStr){
            Ware ware=Ware.dao.findById(wId);
            Float originStartMoney=ware.getWStartPrice();
            Float originHighMoney=ware.getWHighPrice();
            Float startMoney=originStartMoney+change;
            Float highMoney=originHighMoney+change;
            ware.setWStartPrice(startMoney);
            ware.setWHighPrice(highMoney);
            List<Product> products=Product.dao.find(sql,wId);
            for (Product product:products){
                Float pOriginStart=product.getPMoney();
                Float pLaterStart=pOriginStart+change;
                product.setPMoney(pLaterStart);
                if (!product.update()){
                    return false;
                }
            }
            if (!ware.update()){
                return false;
            }
        }
        return true;
    }


    public boolean divideMoney(String wIds, String number) {
        String[] wIdStr=wIds.split(",");
        Float change=new Float(number);
        String sql="select * from product where pWare=?";
        for (String wId:wIdStr){
            Ware ware=Ware.dao.findById(wId);
            Float originStartMoney=ware.getWStartPrice();
            Float originHighMoney=ware.getWHighPrice();
            Float startMoney=originStartMoney/change;
            Float highMoney=originHighMoney/change;
            ware.setWStartPrice(startMoney);
            ware.setWHighPrice(highMoney);
            List<Product> products=Product.dao.find(sql,wId);
            for (Product product:products){
                Float pOriginStart=product.getPMoney();
                Float pLaterStart=pOriginStart/change;
                product.setPMoney(pLaterStart);
                if (!product.update()){
                    return false;
                }
            }
            if (!ware.update()){
                return false;
            }
        }
        return true;
    }
}
