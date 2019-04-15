package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import org.springframework.stereotype.Service;
import com.jfinal.plugin.activerecord.Db;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
@Service
public class businessServiceL {

    //得到商品类别的一级目录
    public JSONArray selectFirstClass()
    {
        //1、dao中的sql语句也必须写表名
        //2、得到的类只有select出来的内容
        //3、如果不用jason封装，则表字段名为期json中的名字
        JSONArray showCategory=new JSONArray();
        List<Category> caList=Category.dao.find("select ctId,ctChName from category where ctParentId=?",0);
        for(Category ca:caList)
        {
            JSONObject aCate=new JSONObject();
            aCate.put("ctId",ca.getCtId());
            aCate.put("ctChName",ca.getCtChName());//中文名
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
            aCate.put("ct2ChName",ca.getCtChName());//中文名
            BigInteger bSecondId=ca.getCtId();
            List<Category> thirdList=Category.dao.find("select ctId,ctChName from Category where ctParentId=?",bSecondId);
            aCate.put("ct2",thirdList);//三级目录中的内容
            showSecondCate.add(aCate);
        }
        return showSecondCate;
    }
    //用于授权时，查找openid对应的记录
    public Visitor selectByOpenId(String OpenId) {
        return Visitor.dao.findFirst("select vOpenId,vWeiXinIcon,vWeiXinName " +
                "from visitor where vOpenId=?",OpenId);
    }

    //添加游客
    public boolean insertVisitor(Visitor vs){
        return vs.save();
    }

    //插入浏览记录
    public boolean insertBrowseRecord(Browserecord browserecord)
    {
        return browserecord.save();
    }

    //9、加入购物车
    public boolean insertStock(Stock stock)
    {
        return stock.save();
    }

    //12、回复商品评价
    public boolean insertBevalreply(Bevalreply bevalreply)
    {
        return bevalreply.save();
    }
    //15、收藏商品
    public boolean insertCollectWare(Collect collect)
    {
        return collect.save();
    }
    //16、显示用户搜索记录
    public List<Businesssearchrecord> selectSearchRecord(BigInteger bsrBusiness)
    {
        return Businesssearchrecord.dao.find("select bsrContent from Businesssearchrecord limit 10");
    }
    //todo 搜索词的提示和添加搜索记录

}
