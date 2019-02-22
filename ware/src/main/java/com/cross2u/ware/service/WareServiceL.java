package com.cross2u.ware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.ware.model.*;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.web.client.RestTemplate;


@Service
public class WareServiceL {

    @Autowired
    RestTemplate restTemplate;
    /*
    * 与其他模块进行通信consumer
    * */
    //1、得到business的具体信息
    public JSONObject getBusinessDetail(BigInteger bId)
    {
        JSONObject response = restTemplate.getForObject("http://User/business/findBusinessDetailByBId/"+bId,JSONObject.class);
        return response.getJSONObject("data");

    }
    //2、得到商店的store的detail
    public JSONObject getStoreDetail(BigInteger sId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findStoreDetail/"+sId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //3、得到mm的logo
    public JSONObject getMMLogoDetail(BigInteger mmId)
    {
        JSONObject response = restTemplate.getForObject("http://User/manufacturer/findStoreDetail/"+mmId,JSONObject.class);
        return response.getJSONObject("data");
    }
    /*
     * 与其他模块进行通信provider
    * */
    //根据Id，找ware
    public Ware selectWareById(BigInteger wId)
    {
        return Ware.dao.findById(wId);
    }
    //根据Identifier，找ware
    public Ware selectWareByIdentifier(String wIdentifier)
    {
        return Ware.dao.findFirst("select * from ware where wIdentifier=?",wIdentifier);
    }
    //根据Id，找product
    public JSONObject selectProductById(BigInteger pId)
    {
        JSONObject product = new JSONObject();
        Product aPro = Product.dao.findById(pId);
        product.put("pImage",aPro.getPImage());
        product.put("format",getFormatForProduct(pId));
        product.put("pMoney",aPro.getPMoney());
        product.put("pMoneyUnit",aPro.getPMoneyUnit());
        return product;
    }
    //根据Identifier，找product
    public JSONObject selectProductByIdentifier(String pIdentifier)
    {
        JSONObject product = new JSONObject();
        Product aPro = Product.dao.findFirst("select * from product where pIdentifier=?",pIdentifier);
        product.put("pId",aPro.getPId());
        product.put("pImage",aPro.getPImage());
        product.put("format",getFormatForProduct(aPro.getPId()));
        product.put("pMoney",aPro.getPMoney());
        product.put("pMoneyUnit",aPro.getPMoneyUnit());
        return product;
    }
    /*
   * service
   * */
    //封装方法，得到商品的月销量
    public Integer getMonthSale(BigInteger wId)
    {
        return Db.queryInt( "select sum(inProductNum) from indent WHERE inWare=? AND inCreateTime<NOW() " +
                "AND inCreateTime>DATE_SUB(NOW(),INTERVAL 30 day)",wId);
    }

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
    //根据评价封装得到评价在页面的显示信息
    public JSONObject getLookFromEvalWare(Evalware ew)
    {
        JSONObject aComment=new JSONObject();
        //对应单品规格信息format
        JSONArray formatList = getFormatForProduct(ew.getEwPId());//单品对应的规格数组
        aComment.put("format",formatList);//评价内容ID
        //评价回复数 ReplyNum
        Integer ReplyNum = Db.queryInt("select count(*) from bevalreply where berECId=?",ew.getEwId());
        aComment.put("ReplyNum",ReplyNum);//评价回复数

        aComment.put("ewId",ew.getEwId());//评价内容ID
        aComment.put("ewCommentatorId",ew.getEwCommentator());//评论者Id

//        Visitor commentator=Visitor.dao.findById(Business.dao.findById(ew.getEwCommentator()).getBOpenId());
//        aComment.put("ewCommentatorIcon",commentator.getVWeiXinIcon());//评论者头像
//        aComment.put("ewCommentatorName",commentator.getVWeiXinName());//评论者昵称

        /*与其他模块通信*/
        JSONObject business = getBusinessDetail(ew.getEwCommentator());
        aComment.put("ewCommentatorName",business.getString("vWeiXinName"));//代理商昵称
        aComment.put("ewCommentatorIcon",business.getInteger("vWeiXinIcon"));//评论者头像
        /*与其他模块通信*/

        aComment.put("ewRank",ew.getEwRank());//商品描述等级
        aComment.put("ewCotent",ew.getEwCotent());//评价文字内容
        aComment.put("ewImg",ew.getEwImg());//附加图片
        aComment.put("ewImg2",ew.getEwImg2());//附加图片2
        aComment.put("ewImg3",ew.getEwImg3());//附加图片3
        aComment.put("ewCreate",ew.getEwCreateTime());//创建时间
        aComment.put("mepCotent",ew.getEwReply());//回复文字内容
        return aComment;
    }

    //得到商品类别的一级目录
    public JSONArray selectFirstClass()
    {
        //1、dao中的sql语句也必须写表名
        //2、得到的类只有select出来的内容
        //3、如果不用jason封装，则表字段名为期jason中的名字
        JSONArray showCategory=new JSONArray();
        List<Category> caList=Category.dao.find("select ctId,ctChName from category where ctParentId=?",0);
        for(Category ca:caList)
        {
            JSONObject aCate=new JSONObject();
            aCate.put("ctId",ca.getCtId());
//            aCate.put("ctChName",ca.getCtChName());//中文名
            showCategory.add(aCate);
        }
        return showCategory;
    }
    //根据一级目录得到二级和三级目录
    public JSONArray selectSecondClass(BigInteger ctParentId)
    {
        JSONArray showSecondCate=new JSONArray();
        List<Category> secondCate=Category.dao.find("select ctId,ctChName from Category where ctParentId=?",ctParentId);
        for(Category ca:secondCate)
        {
            JSONObject aCate=new JSONObject();
            aCate.put("ct2Id",ca.getCtId());
//            aCate.put("ct2ChName",ca.getCtChName());//中文名
            BigInteger bSecondId=ca.getCtId();
            List<Category> thirdList=Category.dao.find("select ctId,ctChName from Category where ctParentId=?",bSecondId);
            aCate.put("ct2",thirdList);//三级目录中的内容
            showSecondCate.add(aCate);
        }
        return showSecondCate;
    }
    //首页显示商品，游客身份和登录身份
    public JSONArray selectAllWare(BigInteger bId)
    {
        JSONArray showWareList=new JSONArray();
        List<Ware> wareList = new ArrayList<>();

        if(bId!=null)
        {
            //得到business的主营行业，类别处于一级

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(bId);
            Integer bMainBusiness = business.getInteger("bMainBusiness");
            /*与其他模块通信*/

//            Integer bMainBusiness = Business.dao.findById(bId).getBMainBusiness();
            //得到其对应的商品类别
            List<Category> secondList=Category.dao.find("select ctId from Category where ctParentId=?",bMainBusiness);
            List<Category> thirdList = new ArrayList<>();
            for(Category ca:secondList)
            {
                BigInteger bSecondId=ca.getCtId();
                List<Category> thirdListPart=Category.dao.find("select ctId from Category where ctParentId=?",bSecondId);
                thirdList.addAll(thirdListPart);//List进行合并
            }
            for(Category thirdca:thirdList)
            {
                //显示出商品,没有考虑货币单位和商品的数量
                List<Ware> wareListPart = Ware.dao.find("select wId,wMainImage,wTitle,wStartPrice," +
                        "wHighPrice,wPriceUnit from ware where wClass=?",thirdca.getCtId());
                wareList.addAll(wareListPart);
            }
        }
        else
        {
            // todo 显示出商品,没有考虑货币单位和商品的数量
            wareList=Ware.dao.find("select wId,wMainImage,wTitle,wStartPrice," +
                    "wHighPrice,wPriceUnit from ware");
            //todo 分页的语句limit pageSize offset (pageIndex-1)*pageSize
        }

        for(Ware w:wareList)
        {
            JSONObject showWare=new JSONObject();
            showWare.put("wId",w.getWId());
            showWare.put("wMainImage",w.getWMainImage());
            showWare.put("wTitle",w.getWTitle());
            showWare.put("wStartPrice",w.getWStartPrice());
            showWare.put("wHighPrice",w.getWHighPrice());
            showWare.put("wPriceUnit",w.getWPriceUnit());
            //调用封装的方法，查询商品的月销量
            Integer wMonthSale= getMonthSale(w.getWId());

            showWare.put("wMonthSale", wMonthSale);
            showWareList.add(showWare);
        }
        return showWareList;
    }
    //显示商品详情
    public JSONObject selectWareBrief(BigInteger wId)
    {
        JSONObject wareBrief=new JSONObject();

        //商品的基本信息
        Ware w = Ware.dao.findById(wId);
        JSONObject baseInfo=new JSONObject();
        baseInfo.put("wId",w.getWId());
        baseInfo.put("wMainImage",w.getWMainImage());
        baseInfo.put("wImage1",w.getWImage1());
        baseInfo.put("wImage2",w.getWImage2());
        baseInfo.put("wImage3",w.getWImage3());
        baseInfo.put("wImage4",w.getWImage4());
        baseInfo.put("wTitle",w.getWTitle());
        baseInfo.put("wStartPrice",w.getWStartPrice());
        baseInfo.put("wHighPrice",w.getWHighPrice());
        baseInfo.put("wPriceUnit",w.getWPriceUnit());
        baseInfo.put("wStartNum",w.getWStartNum());
        baseInfo.put("wHighNum",w.getWHighNum());
        baseInfo.put("wIsReceipt",w.getWIsReceipt());
        baseInfo.put("wIsEnsure",w.getWIsEnsure());
        baseInfo.put("wIsEnsureQuality",w.getWIsEnsureQuality());
        baseInfo.put("wReplaceDays",w.getWReplaceDays());
        baseInfo.put("wDeliverHour",w.getWDeliverHour());
        baseInfo.put("wDescription",w.getWDescription());
        baseInfo.put("wMonthSale",getMonthSale(wId));//通过函数得到商品的月销量
        baseInfo.put("wDeliverArea",w.getWDeliverArea());//得到配送区域的内容
        wareBrief.put("baseInfo",baseInfo);

        //评价内容,按时间进行排序
        JSONObject comment=new JSONObject();

        Integer ewCount = Db.queryInt( "select count(*) from evalware WHERE ewWId=?",wId);
        comment.put("ewCount",ewCount);
        Evalware com= Evalware.dao.findFirst("select * from evalware where ewWId=?",wId);
        comment.put("ewId",com.getEwId());
        comment.put("ewCommentatorId",com.getEwCommentator());//评论者Id

//        Visitor commentator=Visitor.dao.findById(Business.dao.findById(com.getEwCommentator()).getBOpenId());
//        comment.put("ewCommentatorIcon",commentator.getVWeiXinIcon());//评论者头像
//        comment.put("ewCommentatorName",commentator.getVWeiXinName());//评论者昵称

         /*与其他模块通信*/
        JSONObject business = getBusinessDetail(com.getEwCommentator());
        comment.put("ewCommentatorName",business.getString("vWeiXinName"));//代理商昵称
        comment.put("ewCommentatorIcon",business.getString("vWeiXinIcon"));//评论者头像
        /*与其他模块通信*/

        comment.put("ewCotent",com.getEwCotent());

        wareBrief.put("comment",comment);

        //讨论区内容，可能删除
//        JSONObject talkAera=new JSONObject();
//
//        Integer qCount = Db.queryInt( "select count(*) from question WHERE qWare=?",wId);
//        talkAera.put("qCount",qCount);//问题数qCount
//        List<Question> questList = Question.dao.find("SELECT qId,qContent FROM question" +
//                "where qWare=? Limit 2");
//        talkAera.put("q1Id",questList.get(0).getQId());//问题一ID	q1Id
//        talkAera.put("q1Content",questList.get(0).getQContent());//问题一内容	q1Content
////        问题一的回答数（计数） q1AnswerNum
////        问题二的回答数（计数） q2AnswerNum
//        talkAera.put("q2Id",questList.get(1).getQId());//问题二ID	q2Id
//        talkAera.put("q2Content",questList.get(1).getQContent());//问题二内容	q2Content
//        wareBrief.put("talkAera",talkAera);

        //店铺信息
        JSONObject storeInfo=new JSONObject();

//        Store store = Store.dao.findById(w.getWStore());
//        storeInfo.put("sName",store.getSName());//店铺名称
//        storeInfo.put("sScore",store.getSScore());//店铺评分

        /*与其他模块通信*/
        JSONObject store = getStoreDetail(w.getWStore());
        storeInfo.put("sName",store.getString("sName"));
        storeInfo.put("sScore",store.getInteger("sScore"));
        storeInfo.putAll(getMMLogoDetail(store.getBigInteger("sMmId")));//供货商公司Logo
//        String mmLogo = Mainmanufacturer.dao.findById(store.getSMmId()).getMmLogo();
//        storeInfo.put("mmLogo",mmLogo);//供货商公司Logo
        /*与其他模块通信*/

        JSONObject test = new JSONObject();
        storeInfo.putAll(test);

        wareBrief.put("storeInfo",storeInfo);

        //店铺商品推荐,推荐规则未定？？
//        TODO  店铺推荐规则有待完善
        JSONObject recommend=new JSONObject();
        wareBrief.put("recommend",recommend);
        return wareBrief;
    }
    //显示商品评价
// todo 如果评价太多，是否考虑分页
    public JSONArray selectWareComment(BigInteger ewWId)
    {
        JSONArray commentList=new JSONArray();
        List<Evalware> evalList = Evalware.dao.find("select * from evalware where ewWId=?",ewWId);
        for(Evalware ew:evalList)
        {
            JSONObject aComment=getLookFromEvalWare(ew);
            commentList.add(aComment);
        }
        return commentList;
    }
    //根据商品找到对应的单品
    public JSONArray selectProductFromWare(BigInteger pWare)
    {
        JSONArray productList=new JSONArray();
        List<Product> pList = Product.dao.find("select pId,pMoney,pMoneyUnit,pStorage,pImage from product where pWare=?",pWare);
        for(Product p:pList)
        {
            JSONObject aProduct=new JSONObject();
            aProduct.put("pId",p.getPId());//单品ID
            aProduct.put("pMoney",p.getPMoney());//单品价格
            aProduct.put("pMoneyUnit",p.getPMoneyUnit());//单品价格单位
            aProduct.put("pStorage",p.getPStorage());//单品库存量
            aProduct.put("pImage",p.getPImage());//单品图片

            JSONArray formatList = getFormatForProduct(p.getPId());//单品对应的规格数组

            aProduct.put("format",formatList);//规格
            productList.add(aProduct);
        }
        return productList;
    }

    //11、查看单个商品评价的详情
    public JSONObject selectCommentDetail(BigInteger ewId)
    {
        JSONObject commentDetail=new JSONObject();

        //评价内容
        JSONObject aComment=getLookFromEvalWare(Evalware.dao.findById(ewId));
        commentDetail.putAll(aComment);//评价内容封装

        //回复评价内容
        JSONArray replyList = new JSONArray();
        List<Bevalreply> rePartList= Bevalreply.dao.find("select berSpeaker,berCotent,berCreate " +
                "from bevalreply where berECId=?",ewId);//得到该评论的回复列表
        for(Bevalreply rePart:rePartList)
        {
            JSONObject aReply = new JSONObject();

//            Visitor commentator=Visitor.dao.findById(Business.dao.findById(rePart.getBerSpeaker()).getBOpenId());
//            aReply.put("berSpeakerIcon",commentator.getVWeiXinIcon());//评论者头像
//            aReply.put("berSpeakerName",commentator.getVWeiXinName());//评论者昵称

            aReply.put("berSpeakerId",rePart.getBerSpeaker());//评论者id

            /*与其他模块通信*/
            JSONObject business = getBusinessDetail(rePart.getBerSpeaker());
            aReply.put("berSpeakerName",business.getString("vWeiXinName"));//代理商昵称
            aReply.put("berSpeakerIcon",business.getInteger("vWeiXinIcon"));//评论者头像
           /*与其他模块通信*/

            aReply.put("berCotent",rePart.getBerCotent());//回复文字内容
            aReply.put("berCreate",rePart.getBerCreateTime());//创建时间

            replyList.add(aReply);
        }
        commentDetail.put("replyList",replyList);
        return commentDetail;
    }
    //12、回复商品评价
    public boolean insertBevalreply(Bevalreply bevalreply)
    {
        return bevalreply.save();
    }




}
