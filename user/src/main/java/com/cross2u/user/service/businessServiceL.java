package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import org.springframework.stereotype.Service;;

import java.math.BigInteger;
import java.util.List;
@Service
public class businessServiceL {

    /*
    * provider
    * */
    //内部1、得到business的信息
    public JSONObject selectBusinessDetailByBId(BigInteger bId)
    {
        JSONObject bDetail = new JSONObject();
        Business business = Business.dao.findFirst("select bRank,bOpenId,bName,bMainBusiness,bWeiXinName,bWeiXinIcon " +
                "from business where bId=?",bId);
        bDetail.put("bRank",business.getBRank());
        bDetail.put("bName",business.getBName());
        bDetail.put("bMainBusiness",business.getBMainBusiness());
        bDetail.put("vWeiXinName",business.getBWeiXinName());
        bDetail.put("vWeiXinIcon",business.getBWeiXinIcon());
        return bDetail;
    }
    /*
    * sevice
    * */

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
