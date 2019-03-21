package com.cross2u.ware.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.*;
import com.cross2u.ware.util.BaseResponse;
import com.cross2u.ware.util.MoneyUtil;
import com.cross2u.ware.util.ResultCodeEnum;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.For;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
    public String getWareTitleById(String wId){
        return Ware.dao.findById(wId).getWTitle();
    }
    //下架商品
    public Boolean editUndercarriage(String wId) {
        Ware ware=getWareById(wId);
        ware.setWStatus(1);//设置状态1-下架
        return ware.update();
    }

    //根据规格id显示选项
    public JSONArray showForOptions(String fId) {
        String sql="SELECT foId,foName " +
                " from formatoption  " +
                " WHERE foFormat=?";
        List<Record> formatoptions= Db.find(sql,fId);
        JSONArray array=new JSONArray();
        for (Record formatoption : formatoptions)
        {
            JSONObject object=new JSONObject();
            object.put("foId",formatoption.get("foId"));
            object.put("foName",formatoption.get("foName"));
            JSONArray son=getFormatoption(formatoption.get("foId").toString());
            object.put("son",son);

            array.add(object);
        }
        return array;
    }
    ///获取选项对应的子类选项
    private JSONArray getFormatoption(String foId){
        String sonSql="SELECT foId,foName " +
                " from formatoption " +
                " WHERE foParentOption=?";
        List<Formatoption> formatoptions=Formatoption.dao.find(sonSql,foId);
        JSONArray array=new JSONArray();
        for (Formatoption fo:formatoptions){
            JSONObject object=new JSONObject();
            object.put("foId",fo.getFoId());
            object.put("foName",fo.getFoName());

            array.add(object);
        }
        return array;
    }

    //根据属性id显示属性的选项
    public List<Attributeoption> showAttrOptions(String atId) {
        String sql="select aoName,aoId from attributeoption where aoAttribute=?";
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
            Record wSale=getSale(ware.get("wId").toString());
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

    public JSONArray showWares(String sId,String operation) {
        String selectSql="SELECT wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit," +
                "wStatus,wCreateTime  " +
                "from ware  " +
                "WHERE wStore=?  and wStatus !=0";
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
        List<Ware> wares=Ware.dao.find(sql,sId);
        JSONArray array=new JSONArray();
        for (Ware ware :wares){
            JSONObject object=new JSONObject();
            Integer wSale=getWSale(ware.getWId());
            Integer wStorage=getwStorage(ware.getWId());

            object.put("wId",ware.getWId());
            object.put("wMainImage",ware.getWMainImage());
            object.put("wTitle",ware.getWTitle());
            object.put("wStartPrice",ware.getWStartPrice());
            object.put("wHighPrice",ware.getWHighPrice());
            object.put("wPriceUnit",ware.getWPriceUnit());
            object.put("wSale",wSale);
            object.put("wStorage",wStorage);
            object.put("wStatus",ware.getWStatus());
            object.put("wCreateTime",ware.get("wCreateTime"));
            JSONObject belong=getWBelong(ware.get("wId").toString());

            object.put("belong",belong);

            array.add(object);
        }
        return array;
    }
    //获取库存
    private Integer getwStorage(BigInteger wId) {
        String sql="SELECT sum(pStorage) as wStorage " +
                "from product " +
                "WHERE pWare=? ";
        Integer wStorage=Db.queryInt(sql,wId);
        return wStorage;
    }
    //获取销量
    private Integer getWSale(BigInteger wId) {
        String sql="SELECT sum(inProductNum) as wSale " +
                "from indent  " +
                "WHERE inWare=? and inStatus not in (0,5,6) ";
        Integer wSale=Db.queryInt(sql,wId);
        return wSale;
    }

    private JSONObject getWBelong(String wId)
    {
        String sql="SELECT wbId,wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wbWFDId=wfdId) INNER JOIN waresdispatch on wbWSDId=wsdId " +
                " WHERE wbWId=?";
        Record record=Db.findFirst(sql,wId);
        if (record==null){
            return null;
        }
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
        String allHaveAtr="SELECT atId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=0";
        List<Record> allAtr=Db.find(allHaveAtr);
        JSONArray allAtrArray=new JSONArray();
        for (Record allOne:allAtr){
            JSONObject object=new JSONObject();
            object.put("atId",allOne.get("atId"));
            object.put("atName",allOne.get("atName"));
            List<Attributeoption> options=getAtrOptions(allOne.get("atId").toString());
            object.put("atOption",options);

            allAtrArray.add(object);
        }

        //-------二级类别有的Atr
        String sHaveAtr="SELECT atId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> sAtr=Db.find(sHaveAtr,ctSId);
        for (Record sOne:sAtr) {
            JSONObject object=new JSONObject();
            object.put("atId",sOne.get("atId"));
            object.put("atName",sOne.get("atName"));
            List<Attributeoption> options = getAtrOptions(sOne.get("atId").toString());
            object.put("atOption", options);

            allAtrArray.add(object);
        }

        //-------三级类别有的Atr
        String tHaveAtr="SELECT atId,atName " +
                " from atrribute INNER JOIN categoryattribute on caAtrribute=atId " +
                " WHERE caCategory=?";
        List<Record> tAtr=Db.find(tHaveAtr,ctTId);
        JSONArray tAtrArray=new JSONArray();
        for (Record tOne:tAtr){
            JSONObject object=new JSONObject();
            object.put("atId",tOne.get("atId"));
            object.put("atName",tOne.get("atName"));

            List<Attributeoption> options=getAtrOptions(tOne.get("atId").toString());
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
        JSONArray array=new JSONArray();
        for (Record allOne:allFor){
            JSONObject object=new JSONObject();
            object.put("fId",allOne.get("fId"));
            object.put("fName",allOne.get("fName"));
            List<Formatoption> options=getFroOptions(allOne.get("fId").toString());
            object.put("fOption",options);

            array.add(object);
        }

        //-----二级类别有的format
        String sHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> sFor=Db.find(sHaveFor,ctSId);
        for (Record sOne:sFor){
            JSONObject object=new JSONObject();
            object.put("fId",sOne.get("fId"));
            object.put("fName",sOne.get("fName"));
            List<Formatoption> options=getFroOptions(sOne.get("fId").toString());
            object.put("fOption",options);

            array.add(object);
        }

        //-----三级类别有的format
        String tHaveFor="SELECT fId,fName " +
                " from format INNER JOIN categoryformat on cfFormat=fId " +
                " WHERE cfCategory=?";
        List<Record> tFor=Db.find(tHaveFor,ctTId);
        for (Record tOne:tFor){
            JSONObject object=new JSONObject();
            object.put("fId",tOne.get("fId"));
            object.put("fName",tOne.get("fName"));
            List<Formatoption> options=getFroOptions(tOne.get("fId").toString());
            object.put("fOption",options);

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

    public boolean addOneProductFormat(String fId, String fo,String foname,BigInteger pId) {
        Productformat productformat=new Productformat();
        productformat.setPfFormat(new BigInteger(fId));
        productformat.setPfProduct(pId);
        if (foname==null||fo==null){
            System.out.println("addOneProductFormat foname null fo null");
            return false;
        }
        if(!(foname.equals(fo)))//如果相等 是自定义
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
         */
        JSONObject object=new JSONObject();
        String waresql="SELECT wStore,wMainImage,wImage1,wImage2,wImage3,wImage4,wTitle,wIdentifier,wDeliverArea,wStartNum,wHighNum,wDescription,wIsEnsure,wIsEnsureQuality,wDeliverHour,wIsReceipt,wReplaceDays " +
                " from ware " +
                " WHERE wId=?";
        Ware ware=Ware.dao.findFirst(waresql,wId);
        object.put("ware",ware);

        //---自定义类别
        String wbelongSql="SELECT wbId,wbWFDId,wfdName,wbWSDId,wsdName " +
                " FROM (warebelong INNER JOIN warefdispatch on wfdId=wbWFDId ) INNER JOIN waresdispatch on wsdId=wbWSDId " +
                " WHERE wbWId=?";
        Record wareBelong=Db.findFirst(wbelongSql,wId);
        object.put("wbId",wareBelong.get("wbId"));
        object.put("wbWFDId",wareBelong.get("wbWFDId"));
        object.put("wfdName",wareBelong.get("wfdName"));
        object.put("wbWSDId",wareBelong.get("wbWSDId"));
        object.put("wsdName",wareBelong.get("wsdName"));

        //--系统内部类别
        String caSql="SELECT c.ctId as caFirst,c.ctName as caFName, a.ctId as caSecond," +
                "a.ctName as caSName,b.ctId as caThird,b.ctName as caTName " +
                "FROM ((category a INNER JOIN category b on a.ctId=b.ctParentId) INNER JOIN ware on b.ctId=wClass) INNER JOIN category c on c.ctId=a.ctParentId " +
                "WHERE wId=? ";
        Record category=Db.findFirst(caSql,wId);//父子id
        object.put("caFirst",category.get("caFirst"));
        object.put("caFName",category.get("caFName"));
        object.put("caSecond",category.get("caSecond"));
        object.put("caSName",category.get("caSName"));
        object.put("caThird",category.get("caThird"));
        object.put("caTName",category.get("caTName"));

        JSONArray atrNames=getAtr(category.getStr("caSecond"),category.getStr("caThird"));//属性名称
        for (int i=0;i<atrNames.size();i++)
        {
            JSONObject atrName=atrNames.getJSONObject(i);
            String atrOptionSql="SELECT aoId,aoName " +
                    "FROM attributeoption INNER JOIN wareattribute on aoId=waAttributeOption " +
                    "WHERE aoAttribute=? and waWare=? ";//属性的选项 未选则为空
            Record atrOption=Db.findFirst(atrOptionSql,atrName.get("atId"),wId);
            atrName.put("aoId",atrOption.get("aoId"));
            atrName.put("aoName",atrOption.get("aoName"));
        }
        object.put("attribute",atrNames);

        //----规格
        JSONArray foNames=getFor(category.getStr("caSecond"),category.getStr("caThird"));//规格名称
       for (int i=0;i<foNames.size();i++){
            JSONObject foName=foNames.getJSONObject(i);

            JSONArray foOptions=editFoOptions(wId,foName.get("fId"));//选项
            JSONArray defineOption=editDifineOption(foName.get("fId"),wId);//自定义的选项
           foOptions.addAll(defineOption);

            foName.put("fOption",foOptions);
        }
        object.put("format",foNames);
        //----单品
        String productSql="SELECT pId,pMoney,pImage,pIdentifier,pStorage,pMoneyUnit  " +
                "from product  " +
                "WHERE pWare=? ";
        List<Record> products=Db.find(productSql,wId);
        JSONArray productArray=new JSONArray();

        for (Record product:products){
            JSONObject p=new JSONObject();
            JSONArray  productFormat=getProductFormat(product.getBigInteger("pId"));
            p.put("pId",product.getBigInteger("pId"));
            p.put("pMoney",product.get("pMoney"));
            p.put("pImage",product.get("pImage"));
            p.put("pIdentifier",product.get("pIdentifier"));
            p.put("pStorage",product.get("pStorage"));
            p.put("productFormat",productFormat);
            productArray.add(p);
        }
        object.put("products",productArray);
        if (products.size()>=1){
            Record oneproduct=products.get(0);
            object.put("pMoneyUnit",oneproduct.get("pMoneyUnit"));
        }
        else {
            object.put("pMoneyUnit","");
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
        String defineOptionSql="SELECT 0 as foId ,pfDefineOption as foName,'1' as isSelect  " +
                "from productformat inner join product on pfProduct=product.pId " +
                "where pfFormat=? and pWare=? and pfDefineOption is not NULL ";

        List<Record> defineOptions=Db.find(defineOptionSql,fId,wId);//自定义选项
        for (Record defineOption:defineOptions){
            JSONObject object=new JSONObject();
            object.put("foId",defineOption.get("foId"));
            object.put("foName",defineOption.get("foName"));
            object.put("isSelect",defineOption.get("isSelect"));
            object.put("isDefine","isDefine");
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
                "  WHEN foId not IN (SELECT pfFormatOption from productformat INNER JOIN product on pfProduct=pId WHERE pWare like '"+wId+"')  " +
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
            object.put("isDefine","notDefine");
            array.add(object);
        }
        return array;
    }

    private JSONArray  getProductFormat(BigInteger pId) {
        JSONArray array=new JSONArray();
        String formatSql="SELECT fId,fName  " +
                "FROM productformat INNER JOIN format on pfFormat=fId  " +
                "WHERE pfProduct=?  ";
        List<Record> formats=Db.find(formatSql,pId);
        for (Record format: formats){
            JSONObject object=new JSONObject();
            object.put("fId",format.get("fId"));
            object.put("fName",format.get("fName"));

            String productfosql="SELECT foId,foName  " +
                    "from (productformat INNER JOIN format on fId=pfFormat) INNER JOIN formatoption on foId=pfFormatOption " +
                    "WHERE pfProduct=? and fId=?  ";
            Record option=Db.findFirst(productfosql,pId,format.get("fId"));

            String defineSql="SELECT '0' as foId ,pfDefineOption as foName  " +
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
        ware.setWStatus(0);//状态设置为0-删除
        return ware.update();
    }

    public boolean editupcarriage(String wId) {
        Ware ware=Ware.dao.findById(wId);
        ware.setWStatus(2);//设置状态2-上架在售
        return ware.update();
    }

    /*public boolean batchSelectDispatchs(String wIds, String wfdId,String wsdId) {
        String[] widStr=wIds.split(",");
        for (String wId:widStr){
            if(!addOneWareBelong(wfdId, wsdId,new BigInteger (wId)))
            {
                return false;
            }
        }
        return true;
    }*/

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
                "where warebelong.wbWFDId=? ";
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
        String getWId="SELECT wbId,wbWId from warebelong where wbWFDId=? ";
        List<Record> wIds=Db.find(getWId,wfdId);
        for (int i=0;i<wIds.size();i++){
            Record record=wIds.get(i);
            BigInteger wbId=record.getBigInteger("wbId");
            BigInteger wId=record.getBigInteger("wbWId");
            JSONObject ware=getDispatchWareById(wId);
            ware.put("wbId",wbId);

            array.add(ware);
        }
        return array;
    }

    public JSONArray getWSDWares(String wsdId) {
        JSONArray array= new JSONArray();
        String getWId="SELECT wbId,wbWId from warebelong where wbWSDId=? ";
        List<Record> wIds=Db.find(getWId,wsdId);
        for (int i=0;i<wIds.size();i++){
            Record record=wIds.get(i);
            BigInteger wbId=record.getBigInteger("wbId");
            BigInteger wId=record.getBigInteger("wbWId");
            JSONObject ware=getDispatchWareById(wId);
            ware.put("wbId",wbId);

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
                "where ware.wStore like '"+wStore+"'  ORDER BY wMonthSale ";
        if (rank.equals("2")) sql=sql+"DESC";//降序 销量高的在上面
        List<Record> wares=Db.find(sql);
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
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
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
            String inProductNumSql="SELECT sum(inProductNum) as wMonthSale  " +
                    "from ware INNER JOIN indent on wId=indent.inWare  " +
                    "WHERE wId like '"+wId+"'";
            Integer wMonthSale=Db.queryInt(inProductNumSql);
            json.put("wMonthSale",wMonthSale);
            array.add(json);
        }
        return  array;
    }

    public boolean dispatchIsIn(String wfdId, String wsdId, String wId){
        String sql="select wbWId from warebelong where wbWFDId=?";
        List<Record> wIds=null;
        if (!(wsdId==null||wsdId.equals(""))){
            sql=sql+" and wbWSDId=? ";
            wIds=Db.find(sql,wfdId,wsdId);
        }
        else {//没有子类
            wIds=Db.find(sql,wfdId);
        }
        for (Record ware:wIds){
            System.out.println("ware-wId"+ware.get("wbWId"));
            if (ware.get("wbWId").toString().equals(wId)){
                return true;
            }
        }
        return false;
    }

    public Long dispatchAddWare(String wfdId, String wsdId, String wId) {
        Long wbId=null;
        if (!(wsdId==null||wsdId.equals(""))){//有子类
            String sql="INSERT INTO warebelong SET wbWId=?,wbWFDId=?,wbWSDId=? ";
            if (Db.update(sql,wId,wfdId,wsdId)==1){
                String findSql="select wbId from warebelong where wbWId=? and wbWFDId=? and wbWSDId=?";
                wbId=Db.queryLong(findSql,wId,wfdId,wsdId);
            }
            //return Db.update(sql,wId,wfdId,wsdId)==1;
        }
        else if (!(wfdId==null||wfdId.equals(""))){//没子类
            String sql="INSERT INTO warebelong SET wbWId=?,wbWFDId=? ";
            if(Db.update(sql,wId,wfdId)==1){
                String findSql="select wbId from warebelong where wbWId=? and wbWFDId=? ";
                wbId=Db.queryLong(findSql,wId,wfdId);
            }
        }
        return wbId;
    }
    public boolean dispatchDeleteWare(String wbId){
        String sql="delete from warebelong where wbId=?";
        return Db.delete(sql,wbId)==1;
    }

    public JSONArray showGoodEval(String sId){
        String sql="select ewId,ewCotent,ewRank,ewCommentator,ewInId,ewWId " +
                "from evalware " +
                "where  ewRank in(4,5) and  ewInId in (SELECT inId from indent where inStore=?) " +
                "and ewId not in (SELECT aerEWId from abevalreport) ";
        List<Record> records=Db.find(sql,sId);
        JSONArray array=getEvalArray(records);
        return array;
    }

    public JSONArray showBadEval(String sId){
        String sql="select ewId,ewCotent,ewRank,ewCommentator,ewInId,ewWId " +
                "from evalware " +
                "where  ewRank IN (1,2) and  ewInId in (SELECT inId from indent where inStore=?) " +
                "and ewId not in (SELECT aerEWId from abevalreport) ";
        List<Record> records=Db.find(sql,sId);
        JSONArray array=getEvalArray(records);
        return array;
    }

    public JSONArray showNormalEval(String sId){
        String sql="select ewId,ewCotent,ewRank,ewCommentator,ewInId,ewWId " +
                "from evalware " +
                "where  ewRank =3 and  ewInId in (SELECT inId from indent where inStore= ?) " +
                "and ewId not in (SELECT aerEWId from abevalreport) ";
        List<Record> records=Db.find(sql,sId);
        JSONArray array=getEvalArray(records);
        return array;
    }

    private JSONArray getEvalArray(List<Record> records) {
        JSONArray array=new JSONArray();
        for (Record record:records){
            JSONObject object=new JSONObject();

            BigInteger bId=record.getBigInteger("ewCommentator");
            String bSql="select bWeiXinName from business where bId=?";
            Record business=Db.findFirst(bSql,bId);//微信昵称

            BigInteger wId=record.getBigInteger("ewWId");
            String wSql="select wTitle from ware where wId=?";
            Record ware=Db.findFirst(wSql,wId);//商品情况

            BigInteger inId=record.getBigInteger("ewInId");
            String inSql="select inProductNum from indent where inId=?";
            Record indent=Db.findFirst(inSql,inId);//订单信息

            object.put("inProductNum",indent.get("inProductNum"));
            object.put("wTitle",ware.get("wTitle"));
            object.put("bWeiXinName",business.get("bWeiXinName"));//ewId,ewCotent,ewRank,
            object.put("ewId",record.get("ewId"));
            object.put("ewCotent",record.get("ewCotent"));
            object.put("ewRank",record.get("ewRank"));

            array.add(object);
        }
        return array;
    }

    //显示已举报的评论
    public JSONArray showComEval(String sId) {
        String sql="select ewId,ewCotent,ewRank,ewCommentator,ewInId,ewWId " +
                "from evalware INNER JOIN abevalreport on ewId=aerEWId " +
                "where  ewInId in (SELECT inId from indent where inStore= ? )";
        List<Record> records=Db.find(sql,sId);
        JSONArray array=getEvalArray(records);
        for (int i=0;i<array.size();i++){
            JSONObject object=array.getJSONObject(i);
            BigInteger ewId=object.getBigInteger("ewId");
            String aerStateSql="select aerState from abevalreport where aerEWId=?";
            Integer aerState=Db.queryInt(aerStateSql,ewId);
            object.put("aerState",aerState);//是否被审核
        }
        return array;
    }


    //显示评论详情
    public JSONObject showEvalInfo(String sId, String ewId) {
        String sql="SELECT ewId,ewCotent,ewRank,ewIsAnymous,wTitle,inProductNum,ewImg,ewImg2,ewImg3,inNum,ewReply,ewCommentator,ewPId  " +
                "from (evalware INNER JOIN indent on inId=ewInId) INNER JOIN ware on inWare=wId  " +
                "WHERE ewId=? ";
        Record record=Db.findFirst(sql,ewId);
        JSONObject object=new JSONObject();
        BigInteger bId=record.getBigInteger("ewCommentator");
        String getBWeiXinSql="select bWeiXinName from business where bId=? ";
        String bName=Db.queryStr(getBWeiXinSql,bId);
        object.put("bWeiXinName",bName);
        BigInteger pId=record.getBigInteger("ewPId");
        JSONArray pfDefineOption=getProductAtr(pId.toString());
        object.put("pfDefineOption",pfDefineOption );
        object.put("ewId",record.get("ewId"));
        object.put("ewCotent",record.get("ewCotent"));
        object.put("ewRank",record.get("ewRank"));
        object.put("ewIsAnymous",record.get("ewIsAnymous"));
        object.put("wTitle",record.get("wTitle"));
        object.put("inProductNum",record.get("inProductNum"));
        object.put("ewImg",record.get("ewImg"));
        object.put("ewImg2",record.get("ewImg2"));
        object.put("ewImg3",record.get("ewImg3"));
        object.put("inNum",record.get("inNum"));
        object.put("ewReply",record.get("ewReply"));

        return object;
    }

    private JSONArray getProductAtr(String pId){
        JSONArray array=new JSONArray();
        String sql="SELECT fName ,foName " +
                " from (productformat INNER JOIN format on pfFormat=fId) INNER JOIN formatoption on pfFormatOption=foId " +
                " WHERE pfProduct=? ";
        List<Record> records=Db.find(sql,pId);
        for (Record record:records){
            JSONObject object=new JSONObject();
            object.put("fName",record.get("fName"));
            object.put("foName",record.get("foName"));
            array.add(object);
        }
        return array;
    }

    //显示店铺动态评价
    public JSONObject showStaticEval(String sId) {
        JSONObject object=new JSONObject();
        JSONObject  oneWeek=getOneWeek(sId);//最近一周的评论情况
        JSONObject oneMouth=getOneMouth(sId);//最近一月的评论情况
        JSONObject sixMouth=getSixMouth(sId);//最近6个月的评论情况
        JSONObject beforSixMou=getBeforSixMou(sId);//6个月前的评论情况
        JSONObject total=getTota(sId);//所有的评论情况
        object.put("oneWeek",oneWeek);
        object.put("oneMouth",oneMouth);
        object.put("sixMouth",sixMouth);
        object.put("beforSixMou",beforSixMou);
        object.put("total",total);

        return object;
    }

    //所有的评价情况
    private JSONObject getTota(String sId) {
        JSONObject object=new JSONObject();
        String selectSql="SELECT COUNT(*) " +
                "from evalware " +
                "WHERE  ewInId in (SELECT inId from indent where inStore= ? ) ";
        String goodSql=selectSql+" and ewRank in(4,5)  ";
        Integer goodNum=Db.queryInt(goodSql,sId);
        object.put("good",goodNum);
        String normalSql=selectSql+" and  ewRank=3 ";
        Integer normalNum=Db.queryInt(normalSql,sId);
        object.put("normal",normalNum);
        String badSql=selectSql+" and  ewRank in (1,2)";
        Integer badNum=Db.queryInt(badSql,sId);
        object.put("bad",badNum);
        Integer total=Db.queryInt(selectSql,sId);
        object.put("total",total);
        return object;
    }

    //6个月前的评价情况
    private JSONObject getBeforSixMou(String sId) {
        JSONObject object=new JSONObject();
        String selectSql="SELECT COUNT(*) " +
                "from evalware " +
                "WHERE  ewInId in (SELECT inId from indent where inStore= ? ) " +
                "and DATE_SUB(CURDATE(), INTERVAL 180 DAY) > date(ewCreateTime)  ";
        String goodSql=selectSql+" and ewRank in(4,5)  ";
        Integer goodNum=Db.queryInt(goodSql,sId);
        object.put("good",goodNum);
        String normalSql=selectSql+" and  ewRank=3 ";
        Integer normalNum=Db.queryInt(normalSql,sId);
        object.put("normal",normalNum);
        String badSql=selectSql+" and  ewRank in (1,2)";
        Integer badNum=Db.queryInt(badSql,sId);
        object.put("bad",badNum);
        Integer total=Db.queryInt(selectSql,sId);
        object.put("total",total);
        return object;
    }

    //最近6个月的评价情况
    private JSONObject getSixMouth(String sId) {
        JSONObject object=new JSONObject();
        String selectSql="SELECT COUNT(*) " +
                "from evalware " +
                "WHERE  ewInId in (SELECT inId from indent where inStore= ? ) " +
                "and DATE_SUB(CURDATE(), INTERVAL 180 DAY) <= date(ewCreateTime)  and DATE_SUB(CURDATE(), INTERVAL 30 DAY) > date(ewCreateTime) ";
        String goodSql=selectSql+" and ewRank in(4,5)  ";
        Integer goodNum=Db.queryInt(goodSql,sId);
        object.put("good",goodNum);
        String normalSql=selectSql+" and  ewRank=3 ";
        Integer normalNum=Db.queryInt(normalSql,sId);
        object.put("normal",normalNum);
        String badSql=selectSql+" and  ewRank in (1,2)";
        Integer badNum=Db.queryInt(badSql,sId);
        object.put("bad",badNum);
        Integer total=Db.queryInt(selectSql,sId);
        object.put("total",total);
        return object;
    }

    //一个月的评价详情
    private JSONObject getOneMouth(String sId) {
        JSONObject object=new JSONObject();
        String selectSql="SELECT COUNT(*) " +
                "from evalware " +
                "WHERE  ewInId in (SELECT inId from indent where inStore= ? ) " +
                "and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(ewCreateTime) and  DATE_SUB(CURDATE(), INTERVAL 7 DAY) > date(ewCreateTime)";
        String goodSql=selectSql+" and ewRank in(4,5)  ";
        Integer goodNum=Db.queryInt(goodSql,sId);
        object.put("good",goodNum);
        String normalSql=selectSql+" and  ewRank=3 ";
        Integer normalNum=Db.queryInt(normalSql,sId);
        object.put("normal",normalNum);
        String badSql=selectSql+" and  ewRank in (1,2)";
        Integer badNum=Db.queryInt(badSql,sId);
        object.put("bad",badNum);
        Integer total=Db.queryInt(selectSql,sId);
        object.put("total",total);
        return object;
    }

    //一周的评论情况
    public JSONObject getOneWeek(String sId) {
        JSONObject object=new JSONObject();
        String selectSql="SELECT COUNT(*) " +
                "from evalware " +
                "WHERE  ewInId in (SELECT inId from indent where inStore= ? ) " +
                "and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(ewCreateTime)  ";
        String goodSql=selectSql+" and ewRank in(4,5)  ";
        Integer goodNum=Db.queryInt(goodSql,sId);
        object.put("good",goodNum);
        String normalSql=selectSql+" and  ewRank=3 ";
        Integer normalNum=Db.queryInt(normalSql,sId);
        object.put("normal",normalNum);
        String badSql=selectSql+" and  ewRank in (1,2)";
        Integer badNum=Db.queryInt(badSql,sId);
        object.put("bad",badNum);
        Integer total=Db.queryInt(selectSql,sId);
        object.put("total",total);
        return object;
    }


    public boolean reportStoreEval(String sId, String ewId, String aerType, String aerContent) {
        Abevalreport abevalreport=new Abevalreport();
        BigInteger mId=getMIdBySId(sId);
        abevalreport.setAerFReportId(mId);
        abevalreport.setAerEWId(new BigInteger(ewId));
        abevalreport.setAerType(new BigInteger(aerType));
        abevalreport.setAerContent(aerContent);
        abevalreport.setAerState(0);//未审核状态

        /*String aerId=abevalreport.getAerId().toString();
        Evalware evalware=Evalware.dao.findById(ewId);
        evalware.setEwAERId(new Long(aerId));*/
        return abevalreport.save();
    }

    //根绝sid获得mId
    private BigInteger getMIdBySId(String sId) {
        String sql="select mmId from mainmanufacturer where mmStore=? ";
        String mId=Db.queryLong(sql,sId).toString();
        return new BigInteger(mId);
    }

    //显示已举报订单的详情
    public JSONObject showComInfo(String sId, String ewId) {
        Evalware evalware=Evalware.dao.findById(ewId);
        JSONObject object=new JSONObject();
        BigInteger bId=evalware.getEwCommentator();//bid
        String getBWeiXinNameSql="select bWeiXinName from business where bId=? ";
        String bWeiXinName=Db.queryStr(getBWeiXinNameSql,bId);
        object.put("bWeiXinName",bWeiXinName);
        BigInteger wId=evalware.getEwWId();//wId
        String getWTitle="select wTitle from ware where wId=?";
        String wTitle=Db.queryStr(getWTitle,wId);
        object.put("wTitle",wTitle);
        BigInteger pId=evalware.getEwPId();//单品id
        JSONArray pAtr=getProductFormat(pId);
        object.put("pAtr",pAtr);
        BigInteger inId=evalware.getEwInId();//订单id
        String getInProductNumSql="select inNum,inProductNum from indent where inId=?";
        Record indent=Db.findFirst(getInProductNumSql,inId);
        object.put("inNum",indent.get("inNum"));
        object.put("inProductNum",indent.get("inProductNum"));

        String getAbSql="select * from abevalreport where aerEWId=?";
        Abevalreport abevalreport=Abevalreport.dao.findFirst(getAbSql,ewId);
        object.put("aerType",abevalreport.getAerType());
        String rerContentSql="SELECT  rerContent " +
                "from reportevaluatereasons  " +
                "where rerId=? ";
        String rerContent=Db.queryStr(rerContentSql,abevalreport.getAerType());
        object.put("rerContent",rerContent);//举报类型
        object.put("aerAId",abevalreport.getAerAId());
        BigInteger aId=abevalreport.getAerAId();//管理员id
        if (aId!=null){
            String aAccountSql="select aAccount  from administrator where aId =?";
            String aAccount=Db.queryStr(aAccountSql,aId);
            object.put("aAccount",aAccount);
        }
        else {
            object.put("aAccount",null);
        }
        object.put("aerContent",abevalreport.getAerContent());
        object.put("aerState",abevalreport.getAerState());

        object.put("ewId",evalware.getEwId());
        object.put("ewCotent",evalware.getEwCotent());
        object.put("ewRank",evalware.getEwRank());
        object.put("ewIsAnymous",evalware.getEwIsAnymous());
        object.put("ewImg",evalware.getEwImg());
        object.put("ewImg2",evalware.getEwImg2());
        object.put("ewImg3",evalware.getEwImg3());
        object.put("ewReply",evalware.getEwReply());
        return object;
    }

    //回复评论
    public boolean replyEval(String ewId, String ewReply) {
        Evalware evalware=Evalware.dao.findById(ewId);
        evalware.setEwReply(ewReply);
        return evalware.update();
    }

    public JSONArray showReportReasons() {
        String sql="SELECT * FROM `reportevaluatereasons`;";
        List<Record> records=Db.find(sql);
        JSONArray array=new JSONArray();
        for (Record record:records){
            JSONObject object=new JSONObject();
            object.put("rerContent",record.get("rerContent"));
            object.put("rerId",record.get("rerId"));

            array.add(object);
        }
        return array;
    }

    public JSONObject showStoreEval(String sId) {
        String wareRank=getWareRank(sId);
        String indentRank=getIndentRank(sId);
        Integer storeRank=getStoreRank(sId);
        Integer storeScore=getStoreScore(sId);
        JSONObject object=new JSONObject();

        object.put("wareRank",wareRank);
        object.put("indentRank",indentRank);
        object.put("storeRank",storeRank);
        object.put("storeScore",storeScore);

        return object;
    }

    //店铺积分
    private Integer getStoreScore(String sId) {
        String sql="SELECT sScore from store WHERE sId=?";
        Integer sScore=Db.queryInt(sql,sId);
        return sScore;
    }

    //店铺信用等级
    private Integer getStoreRank(String sId) {
        String sql="SELECT sScore from store WHERE sId=? ";
        Integer sScore=Db.queryInt(sql,sId);
        return sScore%100+1;
    }

    //计算订单评分平均分
    private String getIndentRank(String sId) {
        String sql="SELECT sum(inBtoM) FROM `indent` WHERE inStore=?";
        Integer sum=Db.queryInt(sql,sId);
        if (sum==null){
            sum=0;
        }
        Float sumFloat=sum+0.0f;
        String countSql="select count(*) from indent where inStore=?";
        Integer count=Db.queryInt(countSql,sId);
        DecimalFormat  mFormat = new DecimalFormat(".00");
        String formatNum = mFormat .format(sumFloat/count);
        if (sumFloat/count<1.0f){
            formatNum="0"+formatNum;
        }
        return formatNum;
    }

    //商品分数平均分
    private String getWareRank(String sId) {
        String sql="SELECT sum(ewRank) as sumNum FROM evalware WHERE ewInId in (SELECT inId from indent WHERE inStore=?);";
        Integer sum=Db.queryInt(sql,sId);
        Float sumFloat=sum+0.0f;
        String countSql="SELECT COUNT(*) FROM evalware WHERE ewInId in (SELECT inId from indent WHERE inStore=?) ";
        Integer count=Db.queryInt(countSql,sId);
        DecimalFormat  mFormat = new DecimalFormat(".00");
        String formatNum = mFormat .format(sumFloat/count);
        if (sumFloat/count<1.0f){
            formatNum="0"+formatNum;
        }
        return formatNum;
    }

    //检测是否存在敏感词
    public boolean hasSensi(String wTitle) {
        String sql="select senText FROM sensi";
        List<Record> records=Db.find(sql);
        for (Record record:records){
            String sensi=record.getStr("senText");
            if (wTitle.contains(sensi)){
                return true;
            }
        }
        return false;
    }
}
