package com.cross2u.indent.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cross2u.indent.model.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

    @Service
    public class IndentServiceL {

    @Autowired
    RestTemplate restTemplate;

    /*
    * 与其他模块通信，得到需要的数据consumer
    * */
    //1、得到returngoodmould,下游买家退货地址模版
    public JSONObject getReturngoodmouldFromOther(BigInteger rgmId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findReturngoodmould/"+rgmId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //2、得到ware,通过Id
    public JSONObject getWareFromOther(BigInteger wId)
    {
        JSONObject response = restTemplate.getForObject("http://Ware/ware/findWareById/"+wId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //3、得到ware,通过Identifier
    public JSONObject getWareFromOther(String wIdentifier)
    {
        JSONObject response = restTemplate.getForObject("http://Ware/ware/findWareByIdentifier/"+wIdentifier,JSONObject.class);
        return response.getJSONObject("data");
    }
    //4、得到product,通过Id
    public JSONObject getProductFromOther(BigInteger pId)
    {
        JSONObject response = restTemplate.getForObject("http://Ware/ware/findProductById/"+pId,JSONObject.class);
         return response.getJSONObject("data");
    }
    //5、得到product,通过Identifier
    public JSONObject getProductFromOther(String pIdentifier)
    {
        JSONObject response = restTemplate.getForObject("http://Ware/ware/findProductByIdentifier/"+pIdentifier,JSONObject.class);
        return response.getJSONObject("data");
    }
    //6、得到business的具体信息
    public JSONObject getBusinessDetail(BigInteger bId)
    {
        JSONObject response = restTemplate.getForObject("http://User/business/findBusinessDetailByBId/"+bId,JSONObject.class);
         return response.getJSONObject("data");
    }
    //7、得到管理员的账号信息
    public JSONObject getAdminDetail(BigInteger aId)
    {
        JSONObject response = restTemplate.getForObject("http://User/admin/findAdminById/"+aId,JSONObject.class);
         return response.getJSONObject("data");
    }
    //得到storebill
    public JSONArray getStorebill(BigInteger sId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findStorebill/"+sId,JSONObject.class);
        return response.getJSONArray("data");
    }
    //没有代理信息
    public JSONObject getStoreDetail(BigInteger sId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findStoreDetail/"+sId,JSONObject.class);
        return response.getJSONObject("data");
    }

    /*
    service
    * */
    //10、创建新订单
    public BigInteger insertIndent(Indent indent)
    {
        indent.save();
        return indent.getInId();
    }
    //39、b评价订单
    public boolean updateIndent(Indent indent)
    {
        indent.setInStatus(9);//更新为M待评价
        return indent.update();
    }
    //40、与M退款订单找管理员介入
    public boolean updateDrawBackInfo(Drawbackinfo drawbackinfo)
    {
        return drawbackinfo.update();
    }
    //42、B申请退款
    public boolean insertDrawBackInfo(Drawbackinfo drawbackinfo)
    {
        return drawbackinfo.save();
    }

    //（四）订单管理 6、7、9、10订单列表
    public JSONArray selectIndent(BigInteger sId,Integer requestFlag)
    {
        JSONArray orderList = new JSONArray();
        List<Indent> indentList = Indent.dao.find("select * from indent " +
                "where inStore=? AND inStatus=?",sId,requestFlag);
        for(Indent in:indentList)
        {
            JSONObject indent = new JSONObject();
            indent.put("inId",in.getInId());//订单id
            indent.put("inNum",in.getInNum());//订单编号
            indent.put("inCreateTime",in.getInCreateTime());//下单时间
            indent.put("inPayTime",in.getInPayTime());//下单时间
            indent.put("inTotalProduct",in.getInProductNum());//订单单品总数
            indent.put("iwLeftNum",in.getInLeftNum());//订单剩余总数
            indent.put("inTotalMoney",in.getInTotalMoney());//付款总价
            JSONObject ware = getWareFromOther(in.getInWare());

            indent.put("wTitle",ware.getString("wtitle"));//商品名称

            JSONObject product = getProductFromOther(in.getInProduct());
            indent.put("pImage",product.getString("pImage"));//单品图片
            indent.put("format",product.getString("format"));//调用封装函数，对单品对应的规格进行封装
            indent.put("pMoney",product.getFloat("pMoney"));//单品单价
            indent.put("pMoneyUnit",product.getInteger("pMoneyUnit"));//商品单价单位 1-人民币 2-美元

            indent.put("bId",in.getInBusiness());//代理商id

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(in.getInBusiness());
            indent.put("bName",business.getString("vWeiXinName"));//代理商昵称
            indent.put("bRank",business.getInteger("bRank"));//代理商等级
            /*与其他模块通信*/

            //2-待评价中特有的字段
            indent.put("inBtoM",in.getInBtoM());//b的评价
            //3-已完成订单中特有的字段
            indent.put("inMtoB",in.getInMtoB());//M对B的评价
            if(requestFlag==4)
            {
                Drawbackinfo drawbackinfo = Drawbackinfo.dao.findFirst("select diId,diType,diNUmber,diMoney,diAId " +
                        "from drawbackinfo where diInId=?",in.getInId());
                indent.put("diId",drawbackinfo.getDiId()); //退款申请id
                indent.put("drReasons",drawbackinfo.getDiReasons()); //申请类型
                indent.put("diNUmber",drawbackinfo.getDiNUmber()); //退款件数
                indent.put("diMoney",drawbackinfo.getDiMoney()); //退款金额
                indent.put("diStatus",drawbackinfo.getDiStatus()); //申请退款状态
            }
            //5-已关闭订单列表--等页面出来检查特有字段
            indent.put("inStatus",in.getInStatus());//
            orderList.add(indent);
        }
        return orderList;

    }
    //2、筛选订单
    public JSONArray pickIndent(BigInteger sId,Integer requestFlag,String sql)
    {
        JSONArray orderList = new JSONArray();
        List<Record> indentList = Db.find("select * from indent,ware,business " +
                "WHERE indent.inWare = ware.wId AND indent.inBusiness=business.bId AND inStore=? AND inStatus=? "+sql,sId,requestFlag);
        for(Record in:indentList)
        {
            JSONObject indent = new JSONObject();
            indent.put("inId",in.getBigInteger("inId"));//订单id
            indent.put("inNum",in.getStr("inNum"));//订单编号
            indent.put("inCreateTime",in.getDate("inCreateTime"));//下单时间
            indent.put("inPayTime",in.getDate("inPayTime"));//下单时间
            indent.put("inTotalProduct",in.getInt("inProductNum"));//订单单品总数
            indent.put("iwLeftNum",in.getInt("inLeftNum"));//订单剩余总数
            indent.put("inTotalMoney",in.getFloat("inTotalMoney"));//付款总价
            JSONObject ware = getWareFromOther(in.getBigInteger("inWare"));

            indent.put("wTitle",ware.getString("wtitle"));//商品名称

            JSONObject product = getProductFromOther(in.getBigInteger("inProduct"));
            indent.put("pImage",product.getString("pImage"));//单品图片
            indent.put("format",product.getString("format"));//调用封装函数，对单品对应的规格进行封装
            indent.put("pMoney",product.getFloat("pMoney"));//单品单价
            indent.put("pMoneyUnit",product.getInteger("pMoneyUnit"));//商品单价单位 1-人民币 2-美元

            indent.put("bId",in.getBigInteger("inBusiness"));//代理商id

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(in.getBigInteger("inBusiness"));
            indent.put("bName",business.getString("vWeiXinName"));//代理商昵称
            indent.put("bRank",business.getInteger("bRank"));//代理商等级
            /*与其他模块通信*/

            //2-待评价中特有的字段
            indent.put("inBtoM",in.getInt("inBtoM"));//b的评价
            //3-已完成订单中特有的字段
            indent.put("inMtoB",in.getInt("inMtoB"));//M对B的评价
            if(requestFlag==4)
            {
                Drawbackinfo drawbackinfo = Drawbackinfo.dao.findFirst("select diId,diType,diNUmber,diMoney,diAId " +
                        "from drawbackinfo where diInId=?",in.getBigInteger("inId"));
                indent.put("diId",drawbackinfo.getDiId()); //退款申请id
                indent.put("drReasons",drawbackinfo.getDiReasons()); //申请类型
                indent.put("diNUmber",drawbackinfo.getDiNUmber()); //退款件数
                indent.put("diMoney",drawbackinfo.getDiMoney()); //退款金额
                indent.put("diStatus",drawbackinfo.getDiStatus()); //申请退款状态
            }
            //5-已关闭订单列表--等页面出来检查特有字段
            indent.put("inStatus",in.getInt("inStatus"));//
            orderList.add(indent);
        }
        return orderList;

    }
    //（四）订单管理 8、评价订单
//    public boolean updateIndent(Indent indent)
//    {
//        return indent.update();
//    }
    //11.M-B售后退款详情界面
    public JSONObject selectIndentDetail(BigInteger inId,Integer isDrawback)
    {
        JSONObject detail = new JSONObject();
        Indent in = Indent.dao.findById(inId);
        detail.put("inId",in.getInId());//订单id
        detail.put("inNum",in.getInNum());//订单编号
        detail.put("inCreateTime",in.getInCreateTime());//下单时间
        detail.put("inPayTime",in.getInPayTime());//下单时间
        detail.put("inTotalProduct",in.getInProductNum());//订单单品总数
        detail.put("iwLeftNum",in.getInLeftNum());//订单剩余总数
        detail.put("inTotalMoney",in.getInTotalMoney());//付款总价

        JSONObject ware = getWareFromOther(in.getInWare());

        detail.put("wTitle",ware.getString("wtitle"));//商品名称
//            Product product = Product.dao.findById(in.getInProduct());

        JSONObject product = getProductFromOther(in.getInProduct());
        detail.put("pImage",product.getString("pImage"));//单品图片
        detail.put("format",product.getString("format"));//调用封装函数，对单品对应的规格进行封装
        detail.put("pMoney",product.getFloat("pMoney"));//单品单价
        detail.put("pMoneyUnit",product.getInteger("pMoneyUnit"));//商品单价单位 1-人民币 2-美元

        detail.put("bId",in.getInBusiness());//代理商id

        /*与其他模块通信*/
        JSONObject business = getBusinessDetail(in.getInBusiness());
        detail.put("bName",business.getString("vWeiXinName"));//代理商昵称
        detail.put("bRank",business.getInteger("bRank"));//代理商等级
        detail.put("bPhone",business.getString("bPhone"));//代理商电话号码
        /*与其他模块通信*/

        if(isDrawback==1)
        {
            Drawbackinfo drawbackinfo = Drawbackinfo.dao.findFirst("select * from drawbackinfo where diInId=?",in.getInId());
            detail.put("diId",drawbackinfo.getDiId()); //退款申请id
            detail.put("drReasons",drawbackinfo.getDiReasons()); //申请类型
            detail.put("diNUmber",drawbackinfo.getDiNUmber()); //退款件数
            detail.put("diMoney",drawbackinfo.getDiMoney()); //退款金额
            detail.put("diStatus",drawbackinfo.getDiStatus()); //申请退款状态
            detail.put("diImg1",drawbackinfo.getDiImg1()); //退款凭证1
            detail.put("diImg2",drawbackinfo.getDiImg2()); //退款凭证2
            detail.put("diImg3",drawbackinfo.getDiImg3()); //退款凭证3

             /*与其他模块通信*/
            JSONObject admin = getAdminDetail(drawbackinfo.getDiAId());
            detail.put("aAccount",admin.getInteger("aAccount"));//管理员账号
            /*与其他模块通信*/
        }


        return  detail;
    }
    //12.供货商操作退款-修改退款申请的状态
    public boolean updateDrawbackInfo(Drawbackinfo drawbackinfo)
    {
        return drawbackinfo.update();
    }

    //查看下游买家的订单
    public JSONArray selectOutIndent(BigInteger sId, Integer requestFlag)
    {
        JSONArray outOrderList = new JSONArray();
        List<Outindent> outIndentList;
        if(requestFlag==null)
        {
            outIndentList = Outindent.dao.find("select * from outindent " +
                    "where outSId=?",sId);
        }else
        {
            outIndentList = Outindent.dao.find("select * from outindent " +
                    "where outSId=? AND outStatus=?",sId,requestFlag);
        }

        for(Outindent outindent:outIndentList)
        {
            JSONObject outorder = new JSONObject();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            outorder.put("outId",outindent.getOutId());//订单id
//            outorder.put("outPlatform",outindent.getOutId());//来源平台
            outorder.put("outCreateTime",sdf.format(outindent.getOutCreateTime()));//下单时间
            outorder.put("outNumber",outindent.getOutNumber());//订单编号
            outorder.put("outName",outindent.getOutCName());//买家昵称
            outorder.put("outCPhone",outindent.getOutCPhone());//买家联系方式
            outorder.put("outCAddress",outindent.getOutCAddress());//买家地址
            outorder.put("outCInfo",outindent.getOutCInfo().split(","));//买家个人信息照片--身份证正反面 ‘,’隔开 可能为null
            outorder.put("outStatus",outindent.getOutStatus());//状态 1：未发货，2：已发货=待收货，3：已完成，4：售后

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(outindent.getOutBusiness());
            outorder.put("bName",business.getString("bName"));//零售商姓名
            /*与其他模块通信*/

            /*与其他模块通信*/
            JSONObject ware = getWareFromOther(outindent.getOutWIdentifier());
            outorder.put("wId",ware.getBigInteger("wid"));//商品id
            outorder.put("wMainImage",ware.getString("wmainImage"));//商品图片
            outorder.put("wName",ware.getString("wtitle"));//商品名称
            JSONObject myProduct = getProductFromOther(outindent.getOutPIdentifier());//单品编号
            outorder.put("OutPIdentifier",outindent.getOutPIdentifier());//单品编号
            outorder.put("pId",myProduct.getBigInteger("pId"));//单品id
            outorder.put("pAtr",myProduct.getString("format"));//单品规格
            outorder.put("outAmount",outindent.getOutAmount());//单品个数
            /*与其他模块通信*/

            //已经发货订单特有字段
            if(requestFlag>=2)
            {
                outorder.put("outExpress",outindent.getOutExpress());//快递单号
                outorder.put("outExpressCompany",outindent.getOutExpressCompany());//快递公司
            }
            
            //已经收货订单特有字段
            if(requestFlag>=3)
            {
                outorder.put("outModifyTime",sdf.format(outindent.getOutModifyTime()));//收货时间
            }

            //售后订单特有状态
            if(requestFlag==4)
            {
                Returngoods returngoods = Returngoods.dao.findFirst("select * from returngoods " +
                        "where rgOOId=?",outindent.getOutId());
                outorder.put("rcCatalog",Returncatalog.dao.findById(Returngoodreasons.dao.findById
                        (returngoods.getRgType()).getRgrRCId()).getRcCatalog());//退货catelog，退货或退款
                outorder.put("rgId",returngoods.getRgId());//退货Id
                outorder.put("rgState",returngoods.getRgState());//退货状态
                Returngoodreasons returngoodreasons = Returngoodreasons.dao.findById(returngoods.getRgType());
                outorder.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
                outorder.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
                outorder.put("copCreate",sdf.format(returngoods.getRgCreateTime()));//申请时间
            }

            outOrderList.add(outorder);
        }
        return outOrderList;
    }

    //筛选下游买家的订单
    public JSONArray pickOutIndent(BigInteger sId, Integer requestFlag,String sql)
    {
        JSONArray outOrderList = new JSONArray();
        List<Record> outIndentList;
        if(requestFlag==null)
        {
            outIndentList = Db.find("select * FROM outindent,ware,business " +
                    "where outindent.outWIdentifier = ware.wIdentifier AND outindent.outBusiness=business.bId AND outSId=? "+sql,sId);
        }else
        {
            outIndentList = Db.find("select * FROM outindent,ware,business " +
                    "where outindent.outWIdentifier = ware.wIdentifier AND outindent.outBusiness=business.bId AND outSId=? AND outStatus=? "+sql,sId,requestFlag);
        }

        for(Record outindent:outIndentList)
        {
            JSONObject outorder = new JSONObject();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            outorder.put("outId",outindent.getBigInteger("outId"));//订单id
//            outorder.put("outPlatform",outindent.getOutId());//来源平台
            outorder.put("outCreateTime",sdf.format(outindent.getDate("outCreateTime")));//下单时间
            outorder.put("outNumber",outindent.getStr("outNumber"));//订单编号
            outorder.put("outName",outindent.getStr(""));//买家昵称
            outorder.put("outCPhone",outindent.getStr("outCPhone"));//买家联系方式
            outorder.put("outCAddress",outindent.getStr("outCAddress"));//买家地址
            outorder.put("outCInfo",outindent.getStr("outCInfo").split(","));//买家个人信息照片--身份证正反面 ‘,’隔开 可能为null
            outorder.put("outStatus",outindent.getInt("outStatus"));//状态 1：未发货，2：已发货=待收货，3：已完成，4：售后

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(outindent.getBigInteger("outBusiness"));
            outorder.put("bName",business.getString("bName"));//零售商姓名
             /*与其他模块通信*/

            /*与其他模块通信*/
            JSONObject ware = getWareFromOther(outindent.getStr("outWIdentifier"));
            outorder.put("wId",ware.getBigInteger("wid"));//商品id
            outorder.put("wMainImage",ware.getString("wmainImage"));//商品图片
            outorder.put("wName",ware.getString("wtitle"));//商品名称
            JSONObject myProduct = getProductFromOther(outindent.getStr("outPIdentifier"));//单品编号
            outorder.put("OutPIdentifier",outindent.getStr("outPIdentifier"));//单品编号
            outorder.put("pId",myProduct.getBigInteger("pId"));//单品id
            outorder.put("pAtr",myProduct.getString("format"));//单品规格
            outorder.put("outAmount",outindent.getInt("outAmount"));//单品个数
            /*与其他模块通信*/

            //已经发货订单特有字段
            if(requestFlag>=2)
            {
                outorder.put("outExpress",outindent.getStr("outExpress"));//快递单号
                outorder.put("outExpressCompany",outindent.getStr("outExpressCompany"));//快递公司
            }

            //已经收货订单特有字段
            if(requestFlag>=3)
            {
                outorder.put("outModifyTime",sdf.format(outindent.getDate("outModifyTime")));//收货时间
            }

            //售后订单特有状态
            if(requestFlag==4)
            {
                Returngoods returngoods = Returngoods.dao.findFirst("select * from returngoods " +
                        "where rgOOId=?",outindent.getBigInteger("outId"));
                outorder.put("rcCatalog",Returncatalog.dao.findById(Returngoodreasons.dao.findById
                        (returngoods.getRgType()).getRgrRCId()).getRcCatalog());//退货catelog，退货或退款
                outorder.put("rgId",returngoods.getRgId());//退货Id
                outorder.put("rgState",returngoods.getRgState());//退货状态
                Returngoodreasons returngoodreasons = Returngoodreasons.dao.findById(returngoods.getRgType());
                outorder.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
                outorder.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
                outorder.put("copCreate",sdf.format(returngoods.getRgCreateTime()));//申请时间
            }

            outOrderList.add(outorder);
        }
        return outOrderList;
    }
    //更新外拉订单列表，添加货运公司名称和地址
    public boolean updateOutIndent(Outindent outindent)
    {
        return outindent.update();
    }

    //10.M-C售后订单操作之浏览待处理退货订单详情，moreDetail==false
    //13.M-C售后订单之浏览已同意订单，moreDetail==true
    public JSONObject selectReturnIndent(BigInteger outId,boolean moreDetail)
    {
        JSONObject indentDetail = new JSONObject();
        Outindent outIndent = Outindent.dao.findFirst("select * from outindent " +
                "where outId=?",outId);
        indentDetail.put("outId",outIndent.getOutId());//订单id
        indentDetail.put("outPlatform",outIndent.getOutId());//来源平台
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        indentDetail.put("outCreateTime",sdf.format(outIndent.getOutCreateTime()));//下单时间
        indentDetail.put("outNumber",outIndent.getOutNumber());//订单编号
        indentDetail.put("outName",outIndent.getOutCName());//买家昵称
        indentDetail.put("outCPhone",outIndent.getOutCPhone());//买家联系方式
        indentDetail.put("outCAddress",outIndent.getOutCAddress());//买家地址
        indentDetail.put("outCInfo",outIndent.getOutCInfo().split(","));//买家个人信息照片--身份证正反面 ‘,’隔开 可能为null
        indentDetail.put("outStatus",outIndent.getOutStatus());//状态 1：未发货，2：已发货=待收货，3：已完成，4：售后

//        Business business = Business.dao.findFirst("select bName from business where bId=?",outIndent.getOutBusiness());
//        indentDetail.put("bName",business.getBName());//零售商姓名

        /*与其他模块通信*/
        JSONObject business = getBusinessDetail(outIndent.getOutBusiness());
        indentDetail.put("bName",business.getString("bName"));//零售商姓名
            /*与其他模块通信*/

        /*与其他模块通信*/
        JSONObject ware = getWareFromOther(outIndent.getOutWIdentifier());
        indentDetail.put("wId",ware.getBigInteger("wid"));//商品id
        indentDetail.put("wMainImage",ware.getString("wmainImage"));//商品图片
        indentDetail.put("wName",ware.getString("wtitle"));//商品名称
//            Product myProduct = Product.dao.findFirst("select * from product " +
//                    "where pIdentifier=?",outindent.getOutPIdentifier());
        JSONObject myProduct = getProductFromOther(outIndent.getOutPIdentifier());//单品编号
        indentDetail.put("pId",myProduct.getBigInteger("pId"));//单品id
        indentDetail.put("pAtr",myProduct.getString("format"));//单品规格
        indentDetail.put("outAmount",outIndent.getOutAmount());//单品个数
        /*与其他模块通信*/

        Returngoods returngoods = Returngoods.dao.findFirst("select * from returngoods " +
                "where rgOOId=?",outIndent.getOutId());
        indentDetail.put("rgId",returngoods.getRgId());//退货ID
        indentDetail.put("rgrReasons",returngoods.getRgReasons());//退货详细原因

        Returngoodreasons returngoodreasons = Returngoodreasons.dao.findById(returngoods.getRgType());
        indentDetail.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
        indentDetail.put("rcCatalog",Returncatalog.dao.findById(returngoodreasons.getRgrRCId()).getRcCatalog());//退货catelog，退货或退款

        indentDetail.put("rgImg1",returngoods.getRgImg1());//退货凭证，最多三张,可能为null
        indentDetail.put("rgImg2",returngoods.getRgImg2());//退货凭证，最多三张,可能为null
        indentDetail.put("rgImg3",returngoods.getRgImg3());//退货凭证，最多三张,可能为null
        indentDetail.put("copCreate",sdf.format(returngoods.getRgCreateTime()));//申请时间

        if(moreDetail)
        {
            /*与其他模块通信*/
            JSONObject returngoodmould = getReturngoodmouldFromOther(returngoods.getRgRGMId());
            indentDetail.put("rgmName",returngoodmould.getString("rgmName"));//M收货人姓名
            indentDetail.put("rgmPhone",returngoodmould.getString("rgmPhone"));//M收货人联系方式
            indentDetail.put("rgmAddress",returngoodmould.getString("rgmAddress"));//M收货地址
            /*与其他模块通信*/

            indentDetail.put("rgiTrackNumber",returngoods.getRgiTrackNumber());//C上传的物流单号 可能为null
            indentDetail.put("rgiTrackCompany",returngoods.getrgTrackCompany());//物流快递公司
            indentDetail.put("rgiImg",returngoods.getRgImg());//快递凭证
            indentDetail.put("rgiTrakTime",sdf.format(returngoods.getRgiTrakTime()));//c登记物流单号的时间
            indentDetail.put("rgModify",sdf.format(returngoods.getRgModifyTime()));//returngoods表修改的时间
            indentDetail.put("rgState",returngoods.getRgState());//returngoods表中rgState


        }
        return indentDetail;
    }
    //11.M-C售后订单操作之M拒绝-->发通知B，12.M-C售后订单操作之M同意-->发通知B
    public boolean updateReturnGoods(Returngoods returngoods)
    {
        return returngoods.update();
    }

    //（七）我的收支明细
//    收入：代理商的订单-根据单品
//    支出：退款(DrawbackInfo)、交保证金(StoreBill)
    public JSONObject selectBill(BigInteger sId)
    {
        JSONObject billDetail = new JSONObject();

        //收入(Indent 只有B-M的订单 )
        List<Indent> indents= Indent.dao.find("select inId,inNum,inWare,inProduct,inProductNum,inTotalMoney,inPayTime " +
                "from indent where inStore=? and inPayTime is not null",sId);//324 inPayTime 不是null时才是收入
        JSONArray Indent = new JSONArray();
        for(Indent indent:indents)
        {
            JSONObject indentDetail = new JSONObject();
            indentDetail.put("inId",indent.getInId());//订单id
            indentDetail.put("inNum",indent.getInNum());//订单编号

            /*与其他模块通信*/
            JSONObject ware = getWareFromOther(indent.getInWare());
            indentDetail.put("wName",ware.getString("wtitle"));//商品名称
            JSONObject myProduct = getProductFromOther(indent.getInProduct());
            indentDetail.put("format",myProduct.getString("format"));//单品规格
            /*与其他模块通信*/

            indentDetail.put("inProductNum",indent.getInProductNum());//购买量（不考虑退款 退款部分在支出部分）
            indentDetail.put("inTotalMoney",indent.getInTotalMoney());//金额
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            indentDetail.put("inPayTime",sdf.format(indent.getInPayTime()));//付款时间
            Indent.add(indentDetail);
        }
        billDetail.put("Indent",Indent);

        //支出（StoreBill 缴纳保证金）
//        List<Storebill> StoreBill = Storebill.dao.find("select sbId,sbNumber,sbInfo,sbMoney,sbTime " +
//                "from Storebill where sbSId=?",sId);

        /*与store模块进行通信*/
        billDetail.put("StoreBill",getStorebill(sId));
        /*与store模块进行通信*/

        //支出（退款 DrawbackInfo）

        List<Record> records = Db.find("select diId,inNum,diType,inWare,inProduct,diNumber,diMoney,diModifyTime " +
                "from drawbackinfo inner join indent on drawbackinfo.diInId=indent.inId where inStore=?",sId);
        JSONArray DrawbackInfo = new JSONArray();
        for(Record drawbackinfo:records)
        {
            JSONObject drawBackDetail = new JSONObject();
            drawBackDetail.put("diId",drawbackinfo.getBigInteger("diId"));//退款id
            drawBackDetail.put("inNum",drawbackinfo.getStr("inNum"));//订单编号
            drawBackDetail.put("drType",Returncatalog.dao.findById(drawbackinfo.getBigInteger("diType")).getRcCatalog());//退款类型

//            Ware ware = Ware.dao.findFirst("select wTitle from ware where wId=?",drawbackinfo.getBigInteger("inWare"));
//            drawBackDetail.put("pName",ware.getWTitle());//单品对应的商品的title

             /*与其他模块通信*/
            JSONObject ware = getWareFromOther(drawbackinfo.getBigInteger("inWare"));
            drawBackDetail.put("wName",ware.getString("wtitle"));//单品对应的商品的title
            /*与其他模块通信*/

            drawBackDetail.put("format",drawbackinfo.getBigInteger("inProduct"));//单品的规格
            drawBackDetail.put("diNUmber",drawbackinfo.getInt("diNumber"));//退款数目
            drawBackDetail.put("diMoney",drawbackinfo.getFloat("diMoney"));//退款金额
            drawBackDetail.put("diModify",drawbackinfo.getDate("diModifyTime"));//时间
            DrawbackInfo.add(drawBackDetail);
        }
        billDetail.put("DrawbackInfo",DrawbackInfo);
        return billDetail;
    }

}
