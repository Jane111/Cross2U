package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Service
public class MServiceL {

    //提取得到单品规格的代码，封装
    public JSONArray getFormatForProduct(BigInteger pId)
    {
        List<Productformat> pfList = Productformat.dao.find("select pfFormat,pfFormatOption,pfDefineOption" +
                "from productformat where pfProduct=?",pId);//得到该单品对应的规格
        JSONArray formatList = new JSONArray();//单品对应的规格数组
        for(Productformat pf:pfList)
        {
            JSONObject format=new JSONObject();
            format.put("fName",Format.dao.findById(pf.getPfFormat()).getFChName());//得到规格名
            format.put("foName", Formatoption.dao.findById(pf.getPfFormat()).getFoChName());//得到规格对应的选项名
            formatList.add(format);
        }
        return formatList;
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
            Ware ware = Ware.dao.findFirst("select wTitle from ware where wId=?",in.getInWare());
            indent.put("wTitle",in.getInTotalMoney());//商品名称
            Product product = Product.dao.findById(in.getInProduct());
            indent.put("pImage",product.getPImage());//单品图片
            indent.put("format",getFormatForProduct(in.getInProduct()));//调用封装函数，对单品对应的规格进行封装
            indent.put("pMoney",product.getPImage());//单品单价
            indent.put("pMoneyUnit",product.getPImage());//商品单价单位 1-人民币 2-美元
            indent.put("bId",in.getInBusiness());//代理商id
            Business business = Business.dao.findFirst("select bRank,bOpenId from business where bId=?",in.getInBusiness());
            indent.put("bName",Visitor.dao.findFirst("select vWeiXinName where vOpenId=?",business.getBOpenId()));//代理商昵称
            indent.put("bRank",business.getBRank());//代理商等级

            //2-待评价中特有的字段
            indent.put("inBtoM",in.getInBtoM());//b的评价
            //3-已完成订单中特有的字段
            indent.put("inMtoB",in.getInMtoB());//M对B的评价
            if(requestFlag==4)
            {
                Drawbackinfo drawbackinfo = Drawbackinfo.dao.findFirst("select diId,diType,diNUmber,diMoney,diAId" +
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
    //（四）订单管理 8、评价订单
    public boolean updateIndent(Indent indent)
    {
        return indent.update();
    }
    //11.M-B售后退款详情界面
    public JSONObject selectDrawbackDetail(BigInteger inId)
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
        Ware ware = Ware.dao.findFirst("select wTitle from ware where wId=?",in.getInWare());
        detail.put("wTitle",in.getInTotalMoney());//商品名称
        Product product = Product.dao.findById(in.getInProduct());
        detail.put("pImage",product.getPImage());//单品图片
        detail.put("format",getFormatForProduct(in.getInProduct()));//调用封装函数，对单品对应的规格进行封装
        detail.put("pMoney",product.getPImage());//单品单价
        detail.put("pMoneyUnit",product.getPImage());//商品单价单位 1-人民币 2-美元
        detail.put("bId",in.getInBusiness());//代理商id
        Business business = Business.dao.findFirst("select bRank,bOpenId from business where bId=?",in.getInBusiness());
        detail.put("bName",Visitor.dao.findFirst("select vWeiXinName where vOpenId=?",business.getBOpenId()));//代理商昵称
        detail.put("bRank",business.getBRank());//代理商等级

        Drawbackinfo drawbackinfo = Drawbackinfo.dao.findFirst("select * from drawbackinfo where diInId=?",in.getInId());
        detail.put("diId",drawbackinfo.getDiId()); //退款申请id
        detail.put("drReasons",drawbackinfo.getDiReasons()); //申请类型
        detail.put("diNUmber",drawbackinfo.getDiNUmber()); //退款件数
        detail.put("diMoney",drawbackinfo.getDiMoney()); //退款金额
        detail.put("diStatus",drawbackinfo.getDiStatus()); //申请退款状态
        detail.put("diImg1",drawbackinfo.getDiImg1()); //退款凭证1
        detail.put("diImg2",drawbackinfo.getDiImg2()); //退款凭证2
        detail.put("diImg3",drawbackinfo.getDiImg3()); //退款凭证3
        detail.put("aAccount",Administrator.dao.findFirst("select aAccount from Administrator where aId=" +
                "?",drawbackinfo.getDiAId()).getAAccount()); //管理员账号
        return  detail;
    }
    //12.供货商操作退款-修改退款申请的状态
    public boolean updateDrawbackInfo(Drawbackinfo drawbackinfo)
    {
        return drawbackinfo.update();
    }

    //查看下游买家的订单
    public JSONArray selectOutIndent(BigInteger sId,Integer requestFlag)
    {
        JSONArray outOrderList = new JSONArray();
        List<Outindent> outIndentList = Outindent.dao.find("select * from outindent " +
                "where outSId=? AND outStatus=?",sId,requestFlag);
        for(Outindent outindent:outIndentList)
        {
            JSONObject outorder = new JSONObject();
            //todo 外拉订单的创建
            outorder.put("outId",outindent.getOutId());//订单id
            outorder.put("outPlatform",outindent.getOutId());//来源平台
            outorder.put("outCreateTime",outindent.getOutCreateTime());//下单时间
            outorder.put("outNumber",outindent.getOutNumber());//订单编号
            outorder.put("outName",outindent.getOutCName());//买家昵称
            outorder.put("outCPhone",outindent.getOutCPhone());//买家联系方式
            outorder.put("outCAddress",outindent.getOutCAddress());//买家地址
            outorder.put("outCInfo",outindent.getOutCInfo());//买家个人信息照片--身份证正反面 ‘,’隔开 可能为null
            outorder.put("outStatus",outindent.getOutStatus());//状态 1：未发货，2：已发货=待收货，3：已完成，4：售后

            Business business = Business.dao.findFirst("select bName from business where bId=?",outindent.getOutBusiness());
            outorder.put("bName",business.getBName());//零售商姓名

//            List<Outorderware> outProductList = Outorderware.dao.find("select * from Outorderware where oowOutId=?",outindent.getOutId());
//            JSONArray productList = new JSONArray();
//            for(Outorderware outProduct:outProductList)
//            {
//                JSONObject productDetail = new JSONObject();
//                Product myProduct = Product.dao.findFirst("select * from product where pIdentifier=?",outProduct.getOowPIdentifier());
//                productDetail.put("pId",myProduct.getPId());//单品id
//                productDetail.put("pImage",myProduct.getPImage());//单品的图片
//                productDetail.put("oowPIdentifier",myProduct.getPIdentifier());//单品编号
//                productDetail.put("oowNumber",myProduct.getPStorage());//单品个数
//                productDetail.put("format",getFormatForProduct(myProduct.getPId()));//单品的规格
//                productList.add(productDetail);
//            }
            Ware ware = Ware.dao.findFirst("select wId,wMainImage,wName from Ware " +
                    "where outWIdentifier=?",outindent.getOutWIdentifier());//商品编号
            outorder.put("wId",ware.getWId());//商品id
            outorder.put("wMainImage",ware.getWMainImage());//商品图片
            outorder.put("wName",ware.getWTitle());//商品名称
            Product myProduct = Product.dao.findFirst("select * from product " +
                    "where pIdentifier=?",outindent.getOutPIdentifier());//单品编号
            outorder.put("OutPIdentifier",outindent.getOutPIdentifier());//单品编号
            outorder.put("pId",myProduct.getPId());//单品id
            outorder.put("pAtr",getFormatForProduct(myProduct.getPId()));//单品规格
            outorder.put("outAcount",outindent.getOutNumber());//单品个数

            //已经发货订单特有字段
            outorder.put("outExpress",outindent.getOutExpress());//快递单号
            outorder.put("outExpressCompany",outindent.getOutExpressCompany());//快递公司

            //已经收货订单特有字段
            outorder.put("outModifyTime",outindent.getOutModifyTime());//快递公司

            //售后订单特有状态
            Returngoods returngoods = Returngoods.dao.findFirst("select rgReasons,rgState from Returngoods " +
                    "where rgOOId=?",outindent.getOutId());
            outorder.put("rcCatalog",Returncatalog.dao.findById(Returngoodreasons.dao.findById
                    (returngoods.getRgType())).getRcCatalog());//退货catelog，退货或退款
            outorder.put("rgState",returngoods.getRgState());//退货状态

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
                "where outSId=?",outId);
        indentDetail.put("outId",outIndent.getOutId());//订单id
        indentDetail.put("outPlatform",outIndent.getOutId());//来源平台
        indentDetail.put("outCreateTime",outIndent.getOutCreateTime());//下单时间
        indentDetail.put("outNumber",outIndent.getOutNumber());//订单编号
        indentDetail.put("outName",outIndent.getOutCName());//买家昵称
        indentDetail.put("outCPhone",outIndent.getOutCPhone());//买家联系方式
        indentDetail.put("outCAddress",outIndent.getOutCAddress());//买家地址
        indentDetail.put("outCInfo",outIndent.getOutCInfo());//买家个人信息照片--身份证正反面 ‘,’隔开 可能为null
        indentDetail.put("outStatus",outIndent.getOutStatus());//状态 1：未发货，2：已发货=待收货，3：已完成，4：售后

        Business business = Business.dao.findFirst("select bName from business where bId=?",outIndent.getOutBusiness());
        indentDetail.put("bName",business.getBName());//零售商姓名

        Ware ware = Ware.dao.findFirst("select wId,wMainImage,wName from Ware " +
                "where outWIdentifier=?",outIndent.getOutWIdentifier());//商品编号
        indentDetail.put("wId",ware.getWId());//商品id
        indentDetail.put("wMainImage",ware.getWMainImage());//商品图片
        indentDetail.put("wName",ware.getWTitle());//商品名称
        Product myProduct = Product.dao.findFirst("select * from product " +
                "where pIdentifier=?",outIndent.getOutPIdentifier());//单品编号
        indentDetail.put("OutPIdentifier",outIndent.getOutPIdentifier());//单品编号
        indentDetail.put("pId",myProduct.getPId());//单品id
        indentDetail.put("pAtr",getFormatForProduct(myProduct.getPId()));//单品规格
        indentDetail.put("outAcount",outIndent.getOutNumber());//单品个数

        Returngoods returngoods = Returngoods.dao.findFirst("select * from Returngoods " +
                "where rgOOId=?",outIndent.getOutId());
        indentDetail.put("rgId",returngoods.getRgId());//退货ID
        indentDetail.put("rgrReasons",returngoods.getRgReasons());//退货详细原因

        Returngoodreasons returngoodreasons = Returngoodreasons.dao.findById(returngoods.getRgType());
        indentDetail.put("rgReasons",returngoodreasons.getRgrReasons());//退货原因，由类型去找
        indentDetail.put("rcCatalog",Returncatalog.dao.findById(returngoodreasons.getRgrRCId()).getRcCatalog());//退货catelog，退货或退款

        indentDetail.put("rgImg1",returngoods.getRgImg1());//退货凭证，最多三张,可能为null
        indentDetail.put("rgImg2",returngoods.getRgImg2());//退货凭证，最多三张,可能为null
        indentDetail.put("rgImg3",returngoods.getRgImg3());//退货凭证，最多三张,可能为null
        indentDetail.put("copCreate",returngoods.getRgCreateTime());//申请时间

        if(moreDetail)
        {
            Returngoodmould returngoodmould = Returngoodmould.dao.findById(returngoods.getRgMId());
            indentDetail.put("rgmName",returngoodmould.getRgmName());//M收货人姓名
            indentDetail.put("rgmPhone",returngoodmould.getRgmPhone());//M收货人联系方式
            indentDetail.put("rgmAddress",returngoodmould.getRgmAddress());//M收货地址
            indentDetail.put("rgiTrackNumber",returngoods.getRgiTrackNumber());//C上传的物流单号 可能为null
            indentDetail.put("rgiTrackCompany",returngoods.getrgTrackCompany());//物流快递公司
            indentDetail.put("rgiImg",returngoods.getRgImg());//快递凭证
            indentDetail.put("rgiTrakTime",returngoods.getRgiTrakTime());//c登记物流单号的时间
            indentDetail.put("rgModify",returngoods.getRgModifyTime());//returngoods表修改的时间

        }
        return indentDetail;
    }
    //11.M-C售后订单操作之M拒绝-->发通知B，12.M-C售后订单操作之M同意-->发通知B
    public boolean updateReturnGoods(Returngoods returngoods)
    {
        return returngoods.update();
    }

    //16.M查看退货模板列表
    public List<Returngoodmould> selectReturnGoodMould(BigInteger sId)
    {
        return Returngoodmould.dao.find("select rgmId,rgmName,rgmPhone,rgmAddress " +
                "from Returngoodmould where rgSId=?",sId);

    }
    //17.M增加退货地址模板
    public boolean insertReturnGoodMould(Returngoodmould returngoodmould)
    {
        return returngoodmould.save();
    }
    //18.M删除（改变状态字段），修改退货地址模板
    public boolean updateReturnGoodMould(Returngoodmould returngoodmould)
    {
        return returngoodmould.update();
    }

    //展示子账号列表
    public List<Manufacturer> selectMSubAccounts(BigInteger mmId)
    {
        return Manufacturer.dao.find("select mId,mStatus,mPhone,mName,mManageWare,mManageIndent,mManageMessage,mManageClient " +
                "from Manufacturer where mMainManu=? AND mRank=?",mmId,1);
    }

    //展示子账号详情
    public Manufacturer selectMSubAccount(BigInteger mId)
    {
        return Manufacturer.dao.findById(mId);
    }
    //编辑子账号
    public boolean updateMSubAccount(Manufacturer manufacturer)
    {
        return manufacturer.update();
    }
    //新建子账号
    public boolean insertMSubAccount(Manufacturer manufacturer)
    {
        return manufacturer.save();
    }
    //（七）我的收支明细
//    收入：代理商的订单-根据单品
//    支出：退款(DrawbackInfo)、交保证金(StoreBill)
    public JSONObject selectBill(BigInteger sId)
    {
        JSONObject billDetail = new JSONObject();

        //收入(Indent 只有B-M的订单 )
        List<Indent> indents= Indent.dao.find("select inId,inNum,inWare,inProduct,inProductNum,inTotalMoney,inPayTime " +
                "from indent where inStore=?",sId);
        JSONArray Indent = new JSONArray();
        for(Indent indent:indents)
        {
            JSONObject indentDetail = new JSONObject();
            indentDetail.put("inId",indent.getInId());//订单id
            indentDetail.put("inNum",indent.getInNum());//订单编号
            Ware ware = Ware.dao.findFirst("select wTitle from ware where wId=?",indent.getInWare());
            indentDetail.put("pName",ware.getWTitle());//单品对应的商品的title
            indentDetail.put("format",getFormatForProduct(indent.getInId()));//单品的规格
            indentDetail.put("inProductNum",indent.getInProductNum());//购买量（不考虑退款 退款部分在支出部分）
            indentDetail.put("inTotalMoney",indent.getInTotalMoney());//金额
            indentDetail.put("inPayTime",indent.getInPayTime());//付款时间
            Indent.add(indentDetail);
        }
        billDetail.put("Indent",Indent);

        //支出（StoreBill 缴纳保证金）
        List<Storebill> StoreBill = Storebill.dao.find("select sbId,sbNumber,sbInfo,sbMoney,sbTime " +
                "from Storebill where sbSId=?",sId);
        billDetail.put("StoreBill",StoreBill);

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

            Ware ware = Ware.dao.findFirst("select wTitle from ware where wId=?",drawbackinfo.getBigInteger("inWare"));
            drawBackDetail.put("pName",ware.getWTitle());//单品对应的商品的title
            drawBackDetail.put("format",drawbackinfo.getBigInteger("inProduct"));//单品的规格
            drawBackDetail.put("diNUmber",drawbackinfo.getInt("diNUmber"));//退款数目
            drawBackDetail.put("diMoney",drawbackinfo.getFloat("diMoney"));//退款金额
            drawBackDetail.put("diModify",drawbackinfo.getDate("diModify"));//时间
            DrawbackInfo.add(drawBackDetail);
        }
        billDetail.put("DrawbackInfo",DrawbackInfo);
        return billDetail;
    }
    //1、显示设置情况
    public JSONObject selectSet(BigInteger sId)
    {
        JSONObject setDetail = new JSONObject();
        Store store = Store.dao.findFirst("select sDirectMoney,sReduceInventory,sAgentDegree " +
                "from store where sId=?",sId);
        setDetail.put("sDirectMoney",store.getSDirectMoney());//直接到账 0-未开通，1-开通
        setDetail.put("sReduceInventory",store.getSReduceInventory());//减库存方式 0-拍下减，1-付款减
        setDetail.put("sAgentDegree",store.getSAgentDegree());//代理人级别 1~5
        Storebill storebill = Storebill.dao.findFirst("select sbInfo from storebill where sbSId=?",sId);
        setDetail.put("sbInfo",storebill.getSbInfo());//交易资金担保1-缴纳开店保证金（默认有7天包退换），2-缴纳14天包退换，3-缴纳21天，4-缴纳60天
        return setDetail;
    }
    //修改设置情况
    public boolean updateSet(Store store)
    {
        return store.update();
    }
    //修改交易资金担保的设置
    public boolean updateStoreBill(Storebill storeBill)
    {
        return storeBill.update();
    }
    //显示M的基本信息
    public Mainmanufacturer selectMDetail(BigInteger sId)
    {
        return Mainmanufacturer.dao.findFirst("select * from Mainmanufacturer where mmStore=?",sId);
    }







}
