package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;;

import java.math.BigInteger;
import java.util.List;
@Service
public class businessServiceL {

    @Autowired
    RestTemplate restTemplate;
    /*
    * consumer
    * */

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
    //2、一次得到多个B的头像和昵称
    public JSONArray selectManyBusinessByBId(String bId)
    {
        JSONArray manyBusiness =  new JSONArray();
        String[] bList = bId.split(",");
        for(String b:bList)
        {
            JSONObject business = new JSONObject();
            Business aBusiness = Business.dao.findFirst("select bWeiXinName,bWeiXinIcon " +
                    "from business where bId=?",new BigInteger(b));
            business.put("vWeiXinName",aBusiness.getBWeiXinName());
            business.put("vWeiXinIcon",aBusiness.getBWeiXinIcon());
            manyBusiness.add(business);
        }
        return manyBusiness;
    }
    //3、某用户是否收藏某商品
    public Collect selectBIsCollectW(BigInteger bId,BigInteger wId)
    {
        return Collect.dao.findFirst("select * from collect where cOwner=? AND cWare=?",bId,wId);
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


    //15、收藏商品
    public boolean insertCollectWare(Collect collect)
    {
        return collect.save();
    }
    //16、显示用户搜索记录
    public List<Businesssearchrecord> selectSearchRecord(BigInteger bsrBusiness)
    {
        return Businesssearchrecord.dao.find("select bsrContent " +
                "from businesssearchrecord where bsrBusiness=? limit 10",bsrBusiness);
    }
    //todo 搜索词的提示和添加搜索记录



}
