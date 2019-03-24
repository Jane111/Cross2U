package com.cross2u.search.controller;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.search.service.SearchServiceL;
import com.cross2u.search.util.BaseResponse;
import com.cross2u.search.util.HttpClientUtil;
import com.cross2u.search.util.ResultCodeEnum;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping("/search")
public class SearchControllerL {

    @Autowired
    SearchServiceL searchServiceL;
    @Autowired
    BaseResponse jr;

    //1、搜索建议
    @RequestMapping("/searchSuggest")
    public BaseResponse searchSuggest(@RequestParam("searchContent") String searchContent){

        String url="https://suggest.taobao.com/sug?code=utf-8&q="+searchContent;
        String resultString = HttpClientUtil.doGet(url);
        JSONObject result = JSONObject.parseObject(resultString);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;

    }
    //2、根据关键词利用elasticsearch搜索商品,使用redis作为缓存
    @RequestMapping("/searchWare")//搜索的时候调用两个接口，这个和添加搜索记录
    public BaseResponse searchWare(@RequestParam("searchContent") String searchContent){
        ArrayList<Object> result = searchServiceL.searchWareCache(searchContent);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;

    }
    //3、根据关键词利用elasticsearch搜索店铺，使用redis作为缓存
    @RequestMapping("/searchStore")
    public BaseResponse searchStore(@RequestParam("searchContent") String searchContent){
        ArrayList<Object> result = searchServiceL.searchStoreCache(searchContent);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }

/*elasticsearch中mapping的配置*/
//    PUT /cross2u
//    {
//        "mappings": {
//        "ware": {
//            "properties": {
//                "wId":{
//                    "type": "integer"
//                },
//                "wTitle":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                },
//                "wMainImage":{
//                    "type": "text"
//                },
//                "wStatus":{
//                    "type": "integer"
//                },
//                "wStartPrice":{
//                    "type":"float"
//                },
//                "wPriceUnit":{
//                    "type": "integer"
//                }
//            }
//        },
//        "store": {
//            "properties": {
//                "sId":{
//                    "type": "integer"
//                },
//                "sName":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                },
//                "sPhoto":{
//                    "type": "text"
//                },
//                "sScore":{
//                    "type": "integer"
//                }
//            }
//        },
//        "manukeyword": {
//            "properties": {
//                "mkId":{
//                    "type": "integer"
//                },
//                "mkStore":{
//                    "type": "integer"
//                },
//                "mkText":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                },
//                "mkReply":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                }
//            }
//        },
//        "adminkeyword": {
//            "properties": {
//                "akId":{
//                    "type": "integer"
//                },
//                "akText":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                },
//                "akReply":{
//                    "type": "text",
//                            "analyzer":"ik_smart"
//                }
//            }
//        }
//    }
//    }
}


