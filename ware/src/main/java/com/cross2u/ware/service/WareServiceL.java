package com.cross2u.ware.service;

import com.alibaba.fastjson.JSON;
import com.cross2u.ware.util.BaseItemRecommender;
import com.cross2u.ware.util.MoneyUtil;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
    //得到多个business的信息
    public JSONArray getManyBusinessByBId(String bId)
    {
        JSONObject response = restTemplate.getForObject("http://User/business/findManyBusinessByBId/"+bId,JSONObject.class);
        return response.getJSONArray("data");
    }
    //2、得到商店的store的detail,含有代理信息
    public JSONObject getStoreDetailCoop(BigInteger bId,BigInteger sId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findStoreDetailCoop?bId="+bId+"&sId="+sId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //没有代理信息
    public JSONObject getStoreDetail(BigInteger sId)
    {
        JSONObject response = restTemplate.getForObject("http://Store/store/findStoreDetail/"+sId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //3、得到mm的logo
    public JSONObject getMMLogoDetail(BigInteger mmId)
    {
        JSONObject response = restTemplate.getForObject("http://User/manufacturer/findMMLogoDetail/"+mmId,JSONObject.class);
        return response.getJSONObject("data");
    }
    //4、得到某个用户是否收藏某个商品
    public Integer getBIsCollectW(BigInteger bId,BigInteger wId)
    {
        return restTemplate.getForObject("http://User/business/findBIsCollectW?wId="+wId+"&bId="+bId,Integer.class);
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
    public String getFormatForProduct(BigInteger pId)
    {
        List<Productformat> pfList = Productformat.dao.find("select pfFormat,pfFormatOption,pfDefineOption " +
                "from productformat where pfProduct=?",pId);//得到该单品对应的规格
        String formatList = "";//单品对应的规格数组
        for(Productformat pf:pfList)
        {
//            JSONObject format=new JSONObject();
            String format = Format.dao.findById(pf.getPfFormat()).getFName()+":"+Formatoption.dao.findById(pf.getPfFormatOption()).getFoName();
            formatList+=format+" ";
            //            format.put("fName",Format.dao.findById(pf.getPfFormat()).getFName());//得到规格名
//            format.put("foName", Formatoption.dao.findById(pf.getPfFormatOption()).getFoName());//得到规格对应的选项名
//            formatList.add(format);
        }
        return formatList;
    }
    //根据评价封装得到评价在页面的显示信息
    public JSONObject getLookFromEvalWare(Evalware ew)
    {
        JSONObject aComment=new JSONObject();
        //对应单品规格信息format
        String formatList = getFormatForProduct(ew.getEwPId());//单品对应的规格数组
        aComment.put("format",formatList);//评价内容ID
        //评价回复数 ReplyNum
        Integer ReplyNum = Db.queryInt("select count(*) from bevalreply where berECId=?",ew.getEwId());
        aComment.put("ReplyNum",ReplyNum);//评价回复数

        aComment.put("ewId",ew.getEwId());//评价内容ID
        aComment.put("ewCommentatorId",ew.getEwCommentator());//评论者Id

        /*与其他模块通信*/
        JSONObject business = getBusinessDetail(ew.getEwCommentator());
        aComment.put("ewCommentatorName",business.getString("vWeiXinName"));//代理商昵称
        aComment.put("ewCommentatorIcon",business.getString("vWeiXinIcon"));//评论者头像
        /*与其他模块通信*/

        aComment.put("ewRank",ew.getEwRank());//商品描述等级
        aComment.put("ewCotent",ew.getEwCotent());//评价文字内容

        //图片以数组的形式发送
        JSONArray Img = new JSONArray();
        Img.add(ew.getEwImg());
        Img.add(ew.getEwImg2());
        Img.add(ew.getEwImg3());
        aComment.put("ewImg",Img);//附加图片

        //时间的形式格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        aComment.put("ewCreate",sdf.format(ew.getEwCreateTime()));//创建时间
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
        List<Category> caList=Category.dao.find("select ctId,ctName from category where ctParentId=?",0);
        for(Category ca:caList)
        {
            JSONObject aCate=new JSONObject();
            aCate.put("ctId",ca.getCtId());
            aCate.put("ctName",ca.getCtName());//中文名
            showCategory.add(aCate);
        }
        return showCategory;
    }
    //根据一级目录得到二级和三级目录
    public JSONArray selectSecondClass(BigInteger ctParentId)
    {
        JSONArray showSecondCate=new JSONArray();
        List<Category> secondCate=Category.dao.find("select ctId,ctName from category where ctParentId=?",ctParentId);
        for(Category ca:secondCate)
        {
            JSONObject aCate=new JSONObject();
            aCate.put("ct2Id",ca.getCtId());
            aCate.put("ct2Name",ca.getCtName());//中文名
            BigInteger bSecondId=ca.getCtId();
            List<Category> thirdList=Category.dao.find("select ctId,ctName,ctImg from category where ctParentId=?",bSecondId);
            aCate.put("ct2",thirdList);//三级目录中的内容
            showSecondCate.add(aCate);
        }
        return showSecondCate;
    }
    //2、首页显示商品，游客身份和登录身份
    public JSONArray selectAllWare(BigInteger bId,Integer pageIndex,Integer pageSize)
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
            List<Category> secondList=Category.dao.find("select ctId from category where ctParentId=?",bMainBusiness);
            List<Category> thirdList = new ArrayList<>();
            for(Category ca:secondList)
            {
                BigInteger bSecondId=ca.getCtId();
                List<Category> thirdListPart=Category.dao.find("select ctId from category where ctParentId=?",bSecondId);
                thirdList.addAll(thirdListPart);//List进行合并
            }
            for(Category thirdca:thirdList)
            {
                //显示出商品,考虑货币单位和商品的数量
                List<Ware> wareListPart = Ware.dao.find("select wId,wMainImage,wTitle,wStartPrice,wHighPrice,wPriceUnit " +
                        "from ware where wClass=? limit "+pageSize+" offset "+(pageIndex-1)*pageSize,thirdca.getCtId());
                wareList.addAll(wareListPart);
            }
        }
        else
        {
            //显示出商品,考虑货币单位和商品的数量
            wareList=Ware.dao.find("select wId,wMainImage,wTitle,wStartPrice," +
                    "wHighPrice,wPriceUnit from ware limit "+pageSize+" offset "+(pageIndex-1)*pageSize);
            //分页的语句limit pageSize offset (pageIndex-1)*pageSize
        }

        for(Ware w:wareList)
        {
            JSONObject showWare=new JSONObject();
            showWare.put("wId",w.getWId());
            showWare.put("wMainImage",w.getWMainImage());
            showWare.put("wTitle",w.getWTitle());
            Float rmb=MoneyUtil.transferMoney(w.getWStartPrice(),w.getWPriceUnit().toString());
            showWare.put("wStartPrice",rmb);
//            showWare.put("wHighPrice",w.getWHighPrice());
//            showWare.put("wPriceUnit",w.getWPriceUnit());
            //调用封装的方法，查询商品的月销量
            Integer wMonthSale= getMonthSale(w.getWId());

            showWare.put("wMonthSale", wMonthSale);
            showWareList.add(showWare);
        }
        return showWareList;
    }
    //4、显示商品详情
    public JSONObject selectWareBrief(BigInteger wId,BigInteger bId)
    {
        JSONObject wareBrief=new JSONObject();

        //商品的基本信息
        Ware w = Ware.dao.findById(wId);
        JSONObject baseInfo=new JSONObject();
        baseInfo.put("wId",w.getWId());
        JSONArray imageList = new JSONArray();
        imageList.add(w.getWMainImage());
        imageList.add(w.getWImage1());
        imageList.add(w.getWImage2());
        imageList.add(w.getWImage3());
        imageList.add(w.getWImage4());
//        baseInfo.put("wMainImage",w.getWMainImage());
//        baseInfo.put("wImage1",w.getWImage1());
//        baseInfo.put("wImage2",w.getWImage2());
//        baseInfo.put("wImage3",w.getWImage3());
//        baseInfo.put("wImage4",w.getWImage4());
        baseInfo.put("imageList",imageList);
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
        baseInfo.put("wDescription",w.getWDescription().split(","));
        baseInfo.put("wMonthSale",getMonthSale(wId));//通过函数得到商品的月销量
        baseInfo.put("wDeliverArea",w.getWDeliverArea());//得到配送区域的内容

        Float rmbStartPrice= w.getWStartPrice();
        Float rmbHighPrice=w.getWStartPrice();
        if (!w.getWPriceUnit().toString().equals("1"))//不是rmb单位 进行汇率转换
        {
            rmbStartPrice=MoneyUtil.transferMoney(w.getWStartPrice(),w.getWPriceUnit().toString());
            rmbHighPrice=MoneyUtil.transferMoney(w.getWHighPrice(),w.getWPriceUnit().toString());
        }
        baseInfo.put("rmbStartPrice",rmbStartPrice);
        baseInfo.put("rmbHighPrice",rmbHighPrice);

        if(bId!=null)
        {
            baseInfo.put("isCollect",getBIsCollectW(bId,wId));//是否收藏该商品
        }
        else
        {
            baseInfo.put("isCollect",0);//是否收藏该商品
        }
        wareBrief.put("baseInfo",baseInfo);

        //评价内容,按时间进行排序
        JSONObject comment=new JSONObject();

        Integer ewCount = Db.queryInt( "select count(*) from evalware WHERE ewWId=?",wId);
        comment.put("ewCount",ewCount);
        JSONArray commentList = new JSONArray();
        List<Evalware> comList= Evalware.dao.find("select * from evalware where ewWId=? limit 4",wId);

        String manyBId = "";
        for(Evalware com:comList)//
        {
            if(com!=null)
            {
                manyBId+=com.getEwCommentator()+",";
            }
        }
        /*与其他模块通信*/
        JSONArray manyBusiness = getManyBusinessByBId(manyBId);
        /*与其他模块通信*/
        int index=0;
        //如果小于4个
        for(Evalware com:comList)
        {
            if(com!=null)
            {
                JSONObject aCom = new JSONObject();
                aCom.put("ewId",com.getEwId());
                aCom.put("ewCommentatorId",com.getEwCommentator());//评论者Id

                JSONObject business = manyBusiness.getJSONObject(index);
                aCom.put("ewCommentatorName",business.getString("vWeiXinName"));//代理商昵称
                aCom.put("ewCommentatorIcon",business.getString("vWeiXinIcon"));//评论者头像

                aCom.put("ewCotent",com.getEwCotent());
                commentList.add(aCom);
                index++;
            }
        }

        comment.put("commentList",commentList);//多个评论

        wareBrief.put("comment",comment);

        //店铺信息
        JSONObject storeInfo=new JSONObject();
        BigInteger storeId = w.getWStore();
        if(bId!=null)
        {
            /*与其他模块通信*/
            JSONObject store = getStoreDetailCoop(bId,storeId);
            storeInfo.put("sId",storeId);//店铺Id
            storeInfo.put("sName",store.getString("sName"));
            storeInfo.put("sScore",store.getInteger("sScore"));
            storeInfo.put("isCoop",store.getInteger("isCoop"));//是否收藏该商品
            storeInfo.putAll(getMMLogoDetail(store.getBigInteger("sMmId")));//供货商公司Logo
            /*与其他模块通信*/
        }
        else
        {
            /*与其他模块通信*/
            JSONObject store = getStoreDetail(storeId);
            storeInfo.put("sId",storeId);
            storeInfo.put("sName",store.getString("sName"));
            storeInfo.put("sScore",store.getInteger("sScore"));
            storeInfo.put("isCoop",0);//该用户没有代理该店铺
            storeInfo.putAll(getMMLogoDetail(store.getBigInteger("sMmId")));//供货商公司Logo
            /*与其他模块通信*/
        }

        wareBrief.put("storeInfo",storeInfo);

        //店铺商品推荐,推荐规则未定,推荐最新添加的六个商品
//        JSONObject recommend=new JSONObject();
        List<Ware> wareList= Ware.dao.find("select wId,wMainImage,wTitle,wStartPrice,wPriceUnit " +
                "from ware where wStore=? order by wCreateTime limit 6",storeId);//店铺最新添加的六个商品
        wareBrief.put("recommend",wareList);

        return wareBrief;
    }
    //5、查看单个商品参数
    public JSONArray selectWareAttributeByWId(BigInteger wId)
    {
        JSONArray attrDetailList = new JSONArray();
        List<Wareattribute> attrList = Wareattribute.dao.find("select waAttribute,waAttributeOption " +
                "from wareattribute where waWare=?",wId);
        for(Wareattribute attr:attrList)
        {
            JSONObject attrDetail = new JSONObject();
            attrDetail.put("WareAttribute",Atrribute.dao.findById(attr.getWaAttribute()).getAtName());
            attrDetail.put("WareAttributeOption",Attributeoption.dao.findById(attr.getWaAttributeOption()).getAoName());
            attrDetailList.add(attrDetail);
        }
        return attrDetailList;
    }

    //7、显示商品评价
// 评价太多分页
    public JSONArray selectWareComment(BigInteger ewWId,Integer comStar,Integer pageIndex,Integer pageSize)
    {
        JSONArray commentList=new JSONArray();
        List<Evalware> evalList;
        if(comStar==0)
        {
            evalList = Evalware.dao.find("select * from evalware " +
                    "where ewWId=? limit "+pageSize+" offset "+(pageIndex-1)*pageSize,ewWId);
        }
        else
        {
            evalList = Evalware.dao.find("select * from evalware " +
                    "where ewWId=? AND ewRank =? limit "+pageSize+" offset "+(pageIndex-1)*pageSize,ewWId,comStar);
        }
        for(Evalware ew:evalList)
        {
            JSONObject aComment=getLookFromEvalWare(ew);
            commentList.add(aComment);
        }
        return commentList;
    }
    //9、加入购物车
    public boolean insertStock(Stock stock)
    {
        return stock.save();
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

            String formatList = getFormatForProduct(p.getPId());//单品对应的规格数组

            aProduct.put("format",formatList);//规格
            productList.add(aProduct);
        }
        return productList;
    }

    //11、查看单个商品评价的详情
    public JSONObject selectCommentDetail(BigInteger ewId)
    {
        JSONObject commentDetail=new JSONObject();
        Evalware evalware = Evalware.dao.findById(ewId);
        //评价内容
        JSONObject aComment=getLookFromEvalWare(Evalware.dao.findById(ewId));
        commentDetail.putAll(aComment);//评价内容封装

        //添加单品的信息
        Ware ware = Ware.dao.findById(evalware.getEwWId());
        Product product = Product.dao.findById(evalware.getEwPId());
        commentDetail.put("wId",ware.getWId());//商品的Id
        commentDetail.put("wTitle",ware.getWTitle());//商品的标题
        commentDetail.put("wMonthSale", getMonthSale(ware.getWId()));//商品的月销量
        commentDetail.put("pImage",product.getPImage());//单品的图片
        commentDetail.put("pMoney",product.getPMoney());//单品的价格
        commentDetail.put("pMoneyUnit",product.getPMoneyUnit());//单品的价格单位

        //回复评价内容
        JSONArray replyList = new JSONArray();
        List<Bevalreply> rePartList= Bevalreply.dao.find("select berSpeaker,berCotent,berCreateTime " +
                "from bevalreply where berECId=?",ewId);//得到该评论的回复列表
        String manyBId = "";
        for(Bevalreply rePart:rePartList)
        {
            manyBId+=rePart.getBerSpeaker()+",";
        }

        if(!manyBId.equals(""))
        {
            /*与其他模块通信*/
            JSONArray manyBusiness = getManyBusinessByBId(manyBId);
        /*与其他模块通信*/

            int index=0;
            for(Bevalreply rePart:rePartList)
            {
                JSONObject aReply = new JSONObject();

                aReply.put("berSpeakerId",rePart.getBerSpeaker());//评论者id
                //
                JSONObject business = manyBusiness.getJSONObject(index);
                aReply.put("ewCommentatorName",business.getString("vWeiXinName"));//代理商昵称
                aReply.put("ewCommentatorIcon",business.getString("vWeiXinIcon"));//评论者头像

                aReply.put("berCotent",rePart.getBerCotent());//回复文字内容
                //时间的形式格式化
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                aReply.put("berCreate",sdf.format(rePart.getBerCreateTime()));//创建时间

                replyList.add(aReply);
                index++;
            }
            commentDetail.put("replyList",replyList);
        }else
        {
            commentDetail.put("replyList",null);
        }

        return commentDetail;
    }
    //12、回复商品评价
    public boolean insertBevalreply(Bevalreply bevalreply)
    {
        return bevalreply.save();
    }

    //商品详情页，显示看了又看，使用基于物品的协同过滤算法
    public JSONArray selectWareFromRecommender(BigInteger bId,BigInteger wId)
    {
        JSONArray showWareList = new JSONArray();
        List<BigInteger> wIdList = new ArrayList<>();
        BaseItemRecommender recommender = new BaseItemRecommender();
        try {
            wIdList = recommender.recommendBaseItem(bId, wId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //封装商品的信息
        for(BigInteger recommendWId:wIdList)
        {
            Ware w = Ware.dao.findById(recommendWId);
            JSONObject showWare = new JSONObject();
            showWare.put("wId",recommendWId);
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

    //商品详情页"看了又看",推荐同一类型的商品
    public JSONArray selectWareFromClass(BigInteger wId,Integer status)
    {
        JSONArray showWareList = new JSONArray();
        BigInteger wClass = Ware.dao.findFirst("select wClass from ware where wId=?",wId).getWClass();
        List<Ware> wList = new ArrayList<>();
        if(status==0)
        {
            wList = Ware.dao.find("select * from ware where wClass=? limit 8",wClass);
        }else
        {
            wList = Ware.dao.find("select * from ware where wClass=?",wClass);
        }

        //封装商品的信息
        for(Ware ware:wList)
        {
            JSONObject showWare = new JSONObject();
            showWare.put("wId",ware.getWId());
            showWare.put("wMainImage",ware.getWMainImage());
            showWare.put("wTitle",ware.getWTitle());
            showWare.put("wStartPrice",ware.getWStartPrice());
            showWare.put("wHighPrice",ware.getWHighPrice());
            showWare.put("wPriceUnit",ware.getWPriceUnit());
            //调用封装的方法，查询商品的月销量
            Integer wMonthSale= getMonthSale(ware.getWId());

            showWare.put("wMonthSale", wMonthSale);
            showWareList.add(showWare);
        }
        return showWareList;
    }
    //46、显示购物车中的商品（按照店铺进行组织）
    public JSONArray selectWareInStock(BigInteger bId)
    {
        JSONArray showStockList = new JSONArray();
        //得到对应的店铺Id
        List<Stock> storeList = Stock.dao.find("SELECT skSId from stock WHERE skBid=? GROUP BY skSId",bId);
        for(Stock store:storeList)
        {
            JSONObject showStock = new JSONObject();
            /*与其他模块进行通信*/
            JSONObject showStore = getStoreDetail(store.getSkSId());
            /*与其他模块进行通信*/

            //得到对应店铺的信息
            showStock.put("sSId",store.getSkSId());//店铺ID
            showStock.put("sName",showStore.getString("sName"));//店铺名称
            showStock.putAll(getMMLogoDetail(showStore.getBigInteger("sMmId")));//供货商公司Logo

            List<Stock> stockList = Stock.dao.find("select skId,skPId,skNumber,skSum,skSumUnit " +
                    "from stock where skBid=? AND skSId=?",bId,store.getSkSId());

            JSONArray wareList = new JSONArray();
            //获得该店铺对应的所有的商品信息
            for(Stock storeStock:stockList)
            {
                JSONObject wareDetail = new JSONObject();
                wareDetail.put("skId",storeStock.getSkId());//进货ID
                wareDetail.put("skNumber",storeStock.getSkNumber());//进货数目

                wareDetail.put("skFormat",getFormatForProduct(storeStock.getSkPId()));//单品规格

                Product product = Product.dao.findById(storeStock.getSkPId());
                wareDetail.put("pId",storeStock.getSkPId());//单品Id
                wareDetail.put("skPImg",product.getPImage());//单品的图片
                wareDetail.put("skProductMoney",product.getPMoney());//单品价格
                wareDetail.put("skProductMoneyUnit",product.getPMoneyUnit());//单品价格单位
                //得到单品对应的商品信息
                BigInteger wareId = product.getPWare();
                wareDetail.put("wId",wareId);//商品Id
                wareDetail.put("wName",Ware.dao.findById(wareId).getWTitle());//商品名称
                wareList.add(wareDetail);
            }
            showStock.put("data",wareList);//商品数组
            showStockList.add(showStock);
        }
        return showStockList;
    }

    //51、b查看库存,按照单品分类//已经完成的订单,即是无库存的订单
    //按照库存数量进行分类，首先计算数量
    public JSONArray showIndentLeftProductNum(BigInteger bId,Integer inLeftStatus)
    {
        List<Record> list = Db.find("SELECT inProduct,inStore,inWare,sum(inLeftNum) as sumLeft " +
                "from indent WHERE inBusiness=? GROUP BY inProduct,inStore,inWare",bId);
        //进行不同库存的区分
        List<Record> targetList = new ArrayList<>();
        if(inLeftStatus==0)//无库存的情况
        {
            for(Record record:list)
            {
                if(record.getInt("sumLeft")==0)
                {
                    targetList.add(record);
                }
            }
        }
        if(inLeftStatus==1)//库存较少的情况
        {
            for(Record record:list)
            {
                if(record.getInt("sumLeft")<=10 && record.getInt("sumLeft")>0)
                {
                    targetList.add(record);
                }
            }
        }
        if(inLeftStatus==2)//库存充足的情况
        {
            for(Record record:list)
            {
                if(record.getInt("sumLeft")>10)
                {
                    targetList.add(record);
                }
            }
        }
        if(targetList.isEmpty())
        {
            return null;
        }
        //收集得到所有的店铺Id
        List<BigInteger> storeIdList = new ArrayList<>();
        storeIdList.add(targetList.get(0).getBigInteger("inStore"));
        for(Record record:targetList)
        {
            boolean flag = false;//是否有相同的
            BigInteger recordStoreId = record.getBigInteger("inStore");
            for(BigInteger storeId:storeIdList)
            {
                if(recordStoreId.equals(storeId))
                {
                    flag = true;
                    break;
                }
            }
            if(!flag)
            {
                storeIdList.add(recordStoreId);
            }
        }
        JSONArray showStoreList = new JSONArray();
        for(BigInteger storeId:storeIdList)
        {
            JSONObject showStoreDetail = new JSONObject();
            /*与其他模块进行通信*/
            JSONObject showStore = getStoreDetail(storeId);
            /*与其他模块进行通信*/
            //得到对应店铺的信息
            showStoreDetail.put("sId",storeId);//店铺ID
            showStoreDetail.put("sName",showStore.getString("sName"));//店铺名称
            showStoreDetail.putAll(getMMLogoDetail(showStore.getBigInteger("sMmId")));//供货商公司Logo

            JSONArray showStoreProductDetail = new JSONArray();
            for(Record record:targetList)
            {
                if(record.getBigInteger("inStore").equals(storeId))
                {
                    //得到对应的商品Id，商品名称
                    JSONObject productDetail = new JSONObject();
                    productDetail.put("wId",record.getBigInteger("inWare"));
                    Ware ware = Ware.dao.findById(record.getBigInteger("inWare"));
                    productDetail.put("wName",ware.getWTitle());//商品名称
                    productDetail.put("wStartNum",ware.getWStartNum());//商品的起批量
                    productDetail.put("wHighNum",ware.getWHighNum());//商品的最高批量

                    Product product = Product.dao.findById(record.getBigInteger("inProduct"));

                    productDetail.put("pId",record.getBigInteger("inProduct"));//单品的图片
                    productDetail.put("pImage",product.getPImage());//单品的图片
                    productDetail.put("format",getFormatForProduct(record.getBigInteger("inProduct")));//单品规格
                    productDetail.put("pMoney",product.getPMoney());//单品价格
                    productDetail.put("sumLeft",record.getInt("sumLeft"));//总的剩余量

                    showStoreProductDetail.add(productDetail);
                }
            }
            showStoreDetail.put("showStoreProductDetail",showStoreProductDetail);
            showStoreList.add(showStoreDetail);
        }
        return showStoreList;
    }
    //53、评价订单中的商品（打分+文字评论）
    public boolean insertEvalWare(Evalware evalware)
    {
        return evalware.save();
    }
    //A-18、显示举报商品
    public JSONArray selectAbnormalGoodInfo(BigInteger agiType, Integer agiResult)
    {
        JSONArray badWareList = new JSONArray();
        List<Abnormalgoodsinfo> abList;
        if(agiType.equals(new BigInteger("0")))
        {
            abList = Abnormalgoodsinfo.dao.find("select * from abnormalgoodsinfo " +
                    "where agiResult=?",agiResult);
        }
        else
        {
            abList = Abnormalgoodsinfo.dao.find("select * from abnormalgoodsinfo " +
                    "where agiType=? and agiResult=?",agiType,agiResult);
        }
        for(Abnormalgoodsinfo ab:abList)
        {
            JSONObject badWare = new JSONObject();
            badWare.put("agiId",ab.getAgiId());//异常商品信息ID
            Ware ware = Ware.dao.findById(ab.getAgiWId());//举报商品详细信息
            badWare.put("agiWName",ware.getWTitle());//举报的商品名称
            /*与其他模块通信*/
            JSONObject store = getStoreDetail(ware.getWStore());
            /*与其他模块通信*/
            badWare.put("agiSName",store.getString("sName"));//举报的商品所在店铺的名称
            badWare.put("agiType",ab.getAgiType());//举报类型
            badWare.put("agiReasons",ab.getAgiReasons());//举报原因
            badWare.put("agiImg",ab.getAgiImg());//举报凭证
            badWareList.add(badWare);
        }
        return badWareList;
    }
    //A-24、显示举报评论
    public JSONArray selectBadComment(BigInteger aerType, Integer aerState)
    {
        JSONArray badCommentList = new JSONArray();
        List<Abevalreport> abList;
        if(aerType.equals(new BigInteger("0")))
        {
            abList = Abevalreport.dao.find("select * from abevalreport " +
                    "where aerState=?",aerState);
        }
        else
        {
            abList = Abevalreport.dao.find("select * from abevalreport " +
                    "where aerType=? and aerState=?",aerType,aerState);
        }
        for(Abevalreport ab:abList)
        {
            JSONObject badComment = new JSONObject();
            badComment.put("aerId",ab.getAerId());//异常评价ID
            Evalware evalware = Evalware.dao.findById(ab.getAerEWId());
            badComment.put("aerContent",evalware.getEwCotent());//异常评价的内容
            //得到对应的店铺
            /*与其他模块通信*/
            JSONObject store = getStoreDetail(Db.findFirst("select mStore from manufacturer where mId=?",ab.getAerFReportId()).getBigInteger("mStore"));
            /*与其他模块通信*/
            badComment.put("aerSName",store.getString("sName"));//举报的商品所在店铺的名称
            badComment.put("aerType",ab.getAerType());//举报类型
            badComment.put("aerReason",ab.getAerContent());//举报原因
            badCommentList.add(badComment);
        }
        return badCommentList;
    }
}
