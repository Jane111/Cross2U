package com.cross2u.ware.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.*;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.ResultCodeEnum;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.For;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    public JSONArray showWares(String sId, String operation) {
        String selectSql="SELECT wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit," +
                "sum(inProductNum) as wSale,sum(pStorage) as wStorage,wStatus,wCreateTime " +
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
        JSONArray array=new JSONArray();
        for (Record ware :wares){
            JSONObject object=new JSONObject();
            object.put("wId",ware.getBigInteger("wId"));
            object.put("wMainImage",ware.get("wMainImage"));
            object.put("wTitle",ware.get("wTitle"));
            object.put("wStartPrice",ware.get("wStartPrice"));
            object.put("wHighPrice",ware.get("wHighPrice"));
            object.put("wPriceUnit",ware.get("wPriceUnit"));
            object.put("wSale",ware.get("wSale"));
            object.put("wStorage",ware.get("wStorage"));
            object.put("wStatus",ware.get("wStatus"));
            object.put("wCreateTime",ware.get("wCreateTime"));
            JSONObject belong=getWBelong(ware.get("wId"));

            object.put("belong",belong);

            array.add(object);
        }
        return array;
    }
    private JSONObject getWBelong(String wId)
    {
        String sql="SELECT wbId,wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wbWFDId=wfdId) INNER JOIN waresdispatch on wbWSDId=wsdId " +
                " WHERE wbWId=?";
        Record record=Db.findFirst(sql,wId);
        JSONObject object=new JSONObject();
        object.put("wbWFDId",record.get("wbWFDId"));
        object.put("wfdName",record.get("wfdName"));
        object.put("wbWSDId",record.get("wbWSDId"));
        object.put("wsdName",record.get("wsdName"));
        return object;
    }

    public List<Category> addGetCata(String ctParantId) {
        String sql="select ctId,ctName from category WHERE ctParentId=?";
        return Category.dao.find(sql,ctParantId);
    }

    public JSONArray getAtr(String ctSId, String ctTId){
        //-------全部类别都有的Atr
        String allHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=0";
        List<Record> allAtr=Db.find(allHaveAtr);
        JSONArray allAtrArray=new JSONArray();
        for (Record allOne:allAtr){
            JSONObject object=new JSONObject();
            object.put("caId",allOne.get("caId"));
            object.put("atName",allOne.get("atName"));
            List<Attributeoption> options=getAtrOptions(allOne.get("caId").toString());
            object.put("atOption",options);

            allAtrArray.add(object);
        }

        //-------二级类别有的Atr
        String sHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> sAtr=Db.find(sHaveAtr,ctSId);
        for (Record sOne:sAtr) {
            JSONObject object=new JSONObject();
            object.put("caId",sOne.get("caId"));
            object.put("atName",sOne.get("atName"));
            List<Attributeoption> options = getAtrOptions(sOne.get("caId").toString());
            object.put("atOption", options);

            allAtrArray.add(object);
        }

        //-------三级类别有的Atr
        String tHaveAtr="SELECT caId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> tAtr=Db.find(tHaveAtr,ctTId);
        JSONArray tAtrArray=new JSONArray();
        for (Record tOne:tAtr){
            JSONObject object=new JSONObject();
            object.put("caId",tOne.get("caId"));
            object.put("atName",tOne.get("atName"));

            List<Attributeoption> options=getAtrOptions(tOne.get("caId").toString());
            object.put("atOption",options);
            allAtrArray.add(object);
        }


        return allAtrArray;
    }

    private JSONArray getFor(String ctSId,String ctTId)
    {
        //-----全部类别都有的format
        String allHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=0";
        List<Record> allFor=Db.find(allHaveFor);
        for (Record allOne:allFor){
            List<Formatoption> options=getFroOptions(allOne.get("fId").toString());
            allOne.set("fOption",options);
        }

        //-----二级类别有的format
        String sHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> sFor=Db.find(sHaveFor,ctSId);
        for (Record sOne:sFor){
            List<Formatoption> options=getFroOptions(sOne.get("fId").toString());
            sOne.set("fOption",options);
        }


        //-----三级类别有的format
        String tHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> tFor=Db.find(tHaveFor,ctTId);
        for (Record tOne:tFor){
            List<Formatoption> options=getFroOptions(tOne.get("fId").toString());
            tOne.set("fOption",options);
        }

        allFor.addAll(sFor);
        allFor.addAll(tFor);

        JSONArray array=new JSONArray();
        for (Record fName:allFor){
            JSONObject object=new JSONObject();
            object.put("fId",fName.get("fId"));
            object.put("fName",fName.get("fName"));
            array.add(object);
        }
        return  array;
    }
    public JSONObject addFirstStep(String ctSId, String ctTId) {
        JSONObject atrAndFor=new JSONObject();
        JSONArray allAtr=getAtr(ctSId,ctTId);
        JSONArray allFor=getFor(ctSId,ctTId);

        atrAndFor.put("attribute",allAtr);
        atrAndFor.put("format",allFor);
        return atrAndFor;
    }

    private List<Formatoption> getFroOptions(String fId) {
        String sql="SELECT foId,foName " +
                " from formatoption  " +
                " WHERE foFormat=?";
        return Formatoption.dao.find(sql,fId);
    }

    private List<Attributeoption> getAtrOptions(String atId)
    {
        String sql="SELECT aoId,aoName " +
                " from attributeoption " +
                " WHERE aoAttribute=? ";
        return Attributeoption.dao.find(sql,atId);
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

    public JSONObject editShow(String wId, String mId) {

        /**
         * ware的基本信息 record
         * ware belong
         * wareAtr id name option:{id name}
         * format id name option{id name}
         * product {fid fo} pMoney pImg pIdentifier pStorage pMoneyUnit
         *
         */
        JSONObject object=new JSONObject();
        String waresql="SELECT wStore,wMainImage,wImage1,wImage2,wImage3,wImage4,wTitle,wIdentifier,wDeliverArea,wStartNum,wHighNum,wDescription,wIsEnsure,wIsEnsureQuality,wDeliverHour,wIsReceipt,wReplaceDays " +
                " from ware " +
                " WHERE wId=?";
        Ware ware=Ware.dao.findFirst(waresql,wId);
        object.put("ware",ware);

        String wbelongSql="SELECT wbId,wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wfdId=wbWFDId ) INNER JOIN waresdispatch on wsdId=wbWSDId " +
                " WHERE wbWId=?";
        Record wareBelong=Db.findFirst(wbelongSql,wId);
        object.put("wbId",wareBelong.get("wbId"));
        object.put("wbWFDId",wareBelong.get("wbWFDId"));
        object.put("wfdName",wareBelong.get("wfdName"));
        object.put("wbWSDId",wareBelong.get("wbWSDId"));
        object.put("wsdName",wareBelong.get("wsdName"));

        String caSql="SELECT c.ctId as caFirst,c.ctName as caFName, a.ctId as caSecond," +
                "a.ctName as caSName,b.ctId as caThird,b.ctName as caTName " +
                "FROM ((category a INNER JOIN category b on a.ctId=b.ctParentId) INNER JOIN ware on b.ctId=wClass) INNER JOIN category c on c.ctId=a.ctParentId " +
                "WHERE wId=? ";
        Record category=Db.findFirst(caSql,wId);//父子id
        object.put("caFirst",wareBelong.get("caFirst"));
        object.put("caFName",wareBelong.get("caFName"));
        object.put("caSecond",wareBelong.get("caSecond"));
        object.put("caSName",wareBelong.get("caSName"));
        object.put("caThird",wareBelong.get("caThird"));
        object.put("caTName",wareBelong.get("caTName"));

        JSONArray atrNames=getAtr(category.getStr("caSecond"),category.getStr("caThird"));//属性名称

        for (int i=0;i<atrNames.size();i++)
        {
            JSONObject atrName=atrNames.getJSONObject(i);
            String atrOptionSql="SELECT aoId,aoName " +
                    "FROM attributeoption INNER JOIN wareattribute on aoId=waAttributeOption " +
                    "WHERE aoAttribute=？";//属性的选项 未选则为空
            Record atrOption=Db.findFirst(atrOptionSql,atrName.get("atId"));
            atrName.put("aoId",atrOption.get("aoId"));
            atrName.put("aoName",atrOption.get("aoName"));

        }
        //----规格
        JSONArray foNames=getFor(category.getStr("caSecond"),category.getStr("caThird"));//规格名称
        for (int i=0;i<foNames.size();i++){
            JSONObject foName=foNames.getJSONObject(i);

            JSONArray foOptions=editFoOptions(wId,foName.get("fId"));
            JSONArray defineOption=editDifineOption(foName.get("fId"),wId);
            foOptions.add(defineOption);

            foName.put("foOptions",foOptions);
        }
        //----单品
        String productSql="SELECT pId,pMoney,pImage,pIdentifier,pStorage,pMoneyUnit" +
                "from product" +
                "WHERE pWare=?";
        List<Record> products=Db.find(productSql,wId);
        for (Record product:products){
            JSONArray  productFormat=getProductFormat(product.get("pId"));
            product.set("productFormat",productFormat);
        }

        /*ware.setColumns(wareBelong);
        ware.set("category",category);//商品类别
        ware.set("attribute",atrNames);//商品属性 atId atName aoId aoName
        ware.set("format",foNames);//商品规格 fId fName foOptions:{foId,foName,isSelect}
        ware.set("products",products);//单品 pId pMoney pImage pIdentifier pStorage pMoneyUnit productFormat{fId,fName,{foId,fName}}*/
        return object;
    }

    private JSONArray editDifineOption(Object fId, String wId) {
        JSONArray array=new JSONArray();
        String defineOptionSql="SELECT 0 as foId ,pfDefineOption as foName,'1' as isSelect" +
                "from productformat inner join product on pfProduct=product.pId " +
                "where pfFormat=? and pWare=? and pfDefineOption is not NULL";

        List<Record> defineOptions=Db.find(defineOptionSql,fId,wId);//自定义选项
        for (Record defineOption:defineOptions){
            JSONObject object=new JSONObject();
            object.put("foId",defineOption.get("foId"));
            object.put("pfDefineOption",defineOption.get("pfDefineOption"));
            object.put("isSelect",defineOption.get("isSelect"));
            array.add(object);
        }
        return array;
    }

    private JSONArray editFoOptions(String wId, Object fId) {
        JSONArray array=new JSONArray();
        String  foOptionsSql="SELECT foId,foName, " +
                " CASE" +
                "  WHEN foId IN (SELECT pfFormatOption from productformat INNER JOIN product on pfProduct=pId WHERE pWare like '"+wId+"')  " +
                "  THEN 1 " +
                "  WHEN foId not IN (SELECT pfFormatOption from productformat INNER JOIN product on pfProduct=pId WHERE pWarelike '"+wId+"')  " +
                "  THEN 0 " +
                " END isSelect " +
                " from (format INNER JOIN formatoption on foFormat=fId)  " +
                " WHERE fId=? ";
        List<Record> records=Db.find(foOptionsSql,fId);
        for (Record record:records){
            JSONObject object=new JSONObject();
            object.put("foId",record.get("foId"));
            object.put("foName",record.get("foName"));
            object.put("isSelect",record.get("isSelect"));

            array.add(object);
        }
        return array;
    }

    private JSONArray  getProductFormat(Object pId) {
        JSONArray array=new JSONArray();
        String formatSql="SELECT fId,fName  " +
                "FROM productformat INNER JOIN format on pfFormat=fId  " +
                "WHERE pfProduct=?";
        List<Record> formats=Db.find(formatSql,pId);
        for (Record format: formats){
            JSONObject object=new JSONObject();
            object.put("fId",format.get("fId"));
            object.put("fName",format.get("fName"));

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
                object.put("foId",option.get("foId"));
                object.put("foName",option.get("foName"));
            }
            else {
                object.put("foId",define.get("foId"));
                object.put("foName",define.get("foName"));
            }
            array.add(object);
        }
        return array;
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

    public JSONArray showStoreWare(String wStore) {
        String sql=" SELECT wId, wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware " +
                "where ware.wStore=? ";
        List<Ware> wares=Ware.dao.find(sql,new BigInteger(wStore));
        JSONArray array=new JSONArray();
        for (Ware ware:wares){
            JSONObject json=new JSONObject();
            json.put("wId",ware.getWId());
            json.put("wMainImage",ware.getWMainImage());
            json.put("wTitle",ware.getWTitle());
            json.put("wStartNum",ware.getWStartNum());
            json.put("wHighNum",ware.getWHighNum());
            json.put("wStartPrice",ware.getWStartNum());
            json.put("wHighPrice",ware.getWHighPrice());
            json.put("wPriceUnit",ware.getWPriceUnit());

            BigInteger wId=ware.getWId();
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            json.put("wMonthSale",wMonthSale);
            array.add(json);
        }


        return array;
    }

    /**
     * 添加浏览记录
     * @param bId
     * @param wId
     * @return
     */
    public boolean addBrowseRecord(String bId,String wId)
    {
        BigInteger brOwner=new BigInteger(bId);
        BigInteger brWare=new BigInteger(wId);
        String sql="insert into browserecord(brOwner,brWare) values(?,?)";
        return Db.update(sql,brOwner,brWare)>=1;
    }
    //其他模块调用
    public JSONArray getTopFourWare(String sId) {
        String sql="SELECT sum(inProductNum) as wMonthSale,inWare " +
                "from indent " +
                "where inStore=? " +
                "GROUP BY inWare " +
                "ORDER BY wMonthSale DESC " +
                "LIMIT 0,4 ";
        List<Record> wares=Db.find(sql,sId);
        JSONArray array=new JSONArray();
        for (Record ware:wares){
            JSONObject object1=new JSONObject();
            String inWare=ware.getStr("inWare");
            Ware record=Ware.dao.findById(inWare);
            object1.put("wId",record.getWId());
            object1.put("wMainImage",record.getWMainImage());
            object1.put("wTitle",record.getWTitle());
            object1.put("wStartNum",record.getWStartNum());
            object1.put("wHighNum",record.getWHighNum());
            object1.put("wStartPrice",record.getWStartPrice());
            object1.put("wHighPrice",record.getWHighPrice());
            object1.put("wPriceUnit",record.getWPriceUnit());
            object1.put("wMonthSale",ware.get("wMonthSale"));
            array.add(object1);
        }
        return array;
    }
    //其他模块调用
    public JSONArray showStoreFClassWare(String wbWFDId) {
        JSONArray array=new JSONArray();
        String sql="select wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware INNER JOIN warebelong on ware.wId=warebelong.wbWId  " +
                "where warebelong.wbWFDId=?";
        List<Record> fWares=Db.find(sql,new BigInteger(wbWFDId));
        for (Record ware:fWares){
            JSONObject object=new JSONObject();
            object.put("wId",ware.getBigInteger("wId"));
            object.put("wMainImage",ware.get("wMainImage"));
            object.put("wTitle",ware.get("wTitle"));
            object.put("wStartPrice",ware.get("wStartPrice"));
            object.put("wHighPrice",ware.get("wHighPrice"));
            object.put("wPriceUnit",ware.get("wPriceUnit"));

            BigInteger wId=ware.getBigInteger("wId");
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            if (wMonthSale==null||wMonthSale.equals(0.0))
            {
                object.put("wMonthSale","0");
            }
            else {
                object.put("wMonthSale",wMonthSale.toString());
            }

            array.add(object);
        }
        return array;
    }


    //其他模块调用
    public JSONArray showStoreSClassWare(String wbWSDId) {
        JSONArray array=new JSONArray();
        String sql="select wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                " from ware JOIN warebelong on ware.wId=warebelong.wbWId  " +
                " where warebelong.wbWSDId like ?";
        List<Record> sWare=Db.find(sql,new BigInteger(wbWSDId));
        for (Record ware:sWare){
            JSONObject object=new JSONObject();
            object.put("wId",ware.getBigInteger("wId"));
            object.put("wMainImage",ware.get("wMainImage"));
            object.put("wTitle",ware.get("wTitle"));
            object.put("wStartPrice",ware.get("wStartPrice"));
            object.put("wHighPrice",ware.get("wHighPrice"));
            object.put("wPriceUnit",ware.get("wPriceUnit"));

            BigInteger wId=ware.getBigInteger("wId");
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            if (wMonthSale==null||wMonthSale.equals(0.0))
            {
                object.put("wMonthSale","0");
            }
            else {
                object.put("wMonthSale",wMonthSale.toString());
            }

            array.add(object);
        }
        return array;
    }


    public JSONArray getWFDWares(String wfdId) {
        JSONArray array= new JSONArray();
        String getWId="SELECT wbWId from warebelong where wbWFDId=? ";
        List<Record> wIds=Db.find(getWId);
        for (int i=0;i<wIds.size();i++){
            Record record=wIds.get(i);
            BigInteger wId=record.getBigInteger("wbWId");
            JSONObject ware=getDispatchWareById(wId);

            array.add(ware);
        }
        return array;
    }

    public JSONArray getWSDWares(String wsdId) {
        JSONArray array= new JSONArray();
        String getWId="SELECT wbWId from warebelong where wbWSDId=? ";
        List<Record> wIds=Db.find(getWId);
        for (int i=0;i<wIds.size();i++){
            Record record=wIds.get(i);
            BigInteger wId=record.getBigInteger("wbWId");
            JSONObject ware=getDispatchWareById(wId);

            array.add(ware);
        }
        return array;
    }

    public JSONObject getDispatchWareById(BigInteger wId)
    {
        String selectSql="SELECT wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit," +
                "sum(inProductNum) as wSale,sum(pStorage) as wStorage,wStatus,wCreateTime " +
                " from (ware INNER JOIN indent on inWare=wId)INNER JOIN product on wId=pWare " +
                " WHERE wId=? and inStatus!=0 and inStatus!=5 and inStatus!=6 ";
        Record ware=Db.findFirst(selectSql,wId);
        JSONObject object=new JSONObject();
        object.put("wId",ware.getBigInteger("wId"));
        object.put("wMainImage",ware.get("wMainImage"));
        object.put("wTitle",ware.get("wTitle"));
        object.put("wStartPrice",ware.get("wStartPrice"));
        object.put("wHighPrice",ware.get("wHighPrice"));
        object.put("wPriceUnit",ware.get("wPriceUnit"));
        object.put("wSale",ware.get("wSale"));
        object.put("wStorage",ware.get("wStorage"));
        object.put("wStatus",ware.get("wStatus"));
        object.put("wCreateTime",ware.get("wCreateTime"));
        JSONObject belong=getWBelong(wId.toString());

        object.put("belong",belong);
        return object;
    }

    public JSONArray showStoreWareBySale(String wStore, String rank) {
        String sql=" SELECT wId,wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit ,wMonthSale " +
                "from ( SELECT wId as xId,sum(inProductNum) as wMonthSale from ware LEFT  JOIN indent on wId=indent.inWare GROUP BY wId) x INNER JOIN ware on wId=xId  " +
                "where ware.wStore=？ " +
                "ORDER BY wMonthSale ";
        if (rank.equals("2")) sql=sql+"DESC";//降序 销量高的在上面
        List<Record> wares=Db.find(sql,new BigInteger(wStore));
        JSONArray array=new JSONArray();
        for (Record ware:wares){
            JSONObject json=new JSONObject();
            json.put("wId",ware.get("wId"));
            json.put("wMainImage",ware.get("wMainImage"));
            json.put("wTitle",ware.get("wTitle"));
            json.put("wStartNum",ware.get("wStartNum"));
            json.put("wHighNum",ware.get("wHighNum"));
            json.put("wStartPrice",ware.get("wStartPrice"));
            json.put("wHighPrice",ware.get("wHighPrice"));
            json.put("wPriceUnit",ware.get("wPriceUnit"));
            json.put("wMonthSale",ware.get("wMonthSale"));
            array.add(json);
        }
        return  array;
    }

    public JSONArray showStoreWareByPrice(String wStore, String rank) {
        String sql=" SELECT wId, wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware " +
                "where ware.wStore=?  ORDER BY wStartNum ";
        if (rank.equals("2")) sql=sql+"DESC"; //降序
        List<Ware> wares=Ware.dao.find(sql,new BigInteger(wStore));
        JSONArray array=new JSONArray();
        for (Ware ware:wares){
            JSONObject json=new JSONObject();
            json.put("wId",ware.getWId());
            json.put("wMainImage",ware.getWMainImage());
            json.put("wTitle",ware.getWTitle());
            json.put("wStartNum",ware.getWStartNum());
            json.put("wHighNum",ware.getWHighNum());
            json.put("wStartPrice",ware.getWStartNum());
            json.put("wHighPrice",ware.getWHighPrice());
            json.put("wPriceUnit",ware.getWPriceUnit());

            BigInteger wId=ware.getWId();
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale,wId  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            json.put("wMonthSale",wMonthSale);
            array.add(json);
        }
        return  array;
    }

    public JSONArray showStoreWareByTime(String wStore, String rank) {
        String sql=" SELECT wId, wMainImage,wTitle,wStartNum,wHighNum,wStartPrice,wHighPrice,wPriceUnit " +
                "from ware " +
                "where ware.wStore=?  ORDER BY wCreateTime  ";//默认是显示最久的
        if (rank.equals("2")) sql=sql+"DESC"; //降序 最新的在上面
        List<Ware> wares=Ware.dao.find(sql,new BigInteger(wStore));
        JSONArray array=new JSONArray();
        for (Ware ware:wares){
            JSONObject json=new JSONObject();
            json.put("wId",ware.getWId());
            json.put("wMainImage",ware.getWMainImage());
            json.put("wTitle",ware.getWTitle());
            json.put("wStartNum",ware.getWStartNum());
            json.put("wHighNum",ware.getWHighNum());
            json.put("wStartPrice",ware.getWStartNum());
            json.put("wHighPrice",ware.getWHighPrice());
            json.put("wPriceUnit",ware.getWPriceUnit());

            BigInteger wId=ware.getWId();
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale,wId  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            json.put("wMonthSale",wMonthSale);
            array.add(json);
        }
        return  array;
    }

    public boolean dispatchAddWare(String wfdId, String wsdId, String wId) {
        if (wfdId!=null&&!wfdId.equals("")){
            String sql="INSERT INTO warebelong SET wbWId=?,wbWFDId=? ";
            return Db.update(sql,wId,wfdId)==1;
        }
        else if (wsdId!=null&&!wsdId.equals("")){
            String sql="INSERT INTO warebelong SET wbWId=?,wbWSDId=? ";
            return Db.update(sql,wId,wsdId)==1;
        }
        return false;
    }
    public boolean dispatchDeleteWare(String wbId){
        String sql="delete from warebelong where wbId=?";
        return Db.update(sql,wbId)==1;
    }
}
