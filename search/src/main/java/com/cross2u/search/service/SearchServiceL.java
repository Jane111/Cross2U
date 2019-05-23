package com.cross2u.search.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.search.util.MoneyUtil;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SearchServiceL {

    @Autowired
    private TransportClient client;

    public void test(){
        // 查询一条
        GetResponse result=client.prepareGet("book","novel","1").get();
        System.out.println(result);
    }
  /*
    *1、value/cacheName:缓存名称，用来标记缓存，String[]类型，可以同时放在多个缓存中
    * 2、key:缓存数据所使用的KEY,默认是方法的参数；可以使用spel表达式来指定
    * #id,#a0,#p0,#root.args[0]
    * 3、keyGenerator:key的生成器，key/keyGenerator二选一使用
    * 4、condition:条件为true的时候，缓存生效
    * 5、unless：条件为false,缓存生效
    * */

    /*引入redis
    *1、引入redis依赖，stater
    * 2、配置redis
    * 3、测试缓存
    *   原理：CacheManager管理Cache，给缓存中curd数据
    *   1、引入redis后，引入的时rediscachemanager
    *   2、rediscachemanager帮助我们rediscache作为缓存组件，rediscache通过操作redis缓存数据
    *   3、默认保存数据k-v都是Object，利用序列化实现
    *       默认创建的rediscachemanager操作redisCache使用的是RestTemplate<Object,Object>（使用jdk的序列化机制）
    *   4、自定义cachemanager;
    *
   * */
    @Cacheable(value = {"ware"})
    public ArrayList<Object> searchWareCache(String searchContent)
    {
        ArrayList<Object> wareList = new ArrayList<Object>();
        QueryBuilder qb1 = QueryBuilders.matchQuery("wtitle", searchContent);//分词然后匹配
        QueryBuilder qb2 = QueryBuilders.termQuery("wstatus", 2);//完全匹配
//        QueryBuilder qb3 = QueryBuilders.rangeQuery("meters").from(meters1).to(meters2);
//        QueryBuilder qb4 = QueryBuilders.rangeQuery("price").from(price1).to(price2);
        QueryBuilder builder = QueryBuilders.boolQuery()
                .must(qb1)
                .filter(qb2);
//                .filter(qb3)
//                .filter(qb4);

        SearchResponse sr = client.prepareSearch("cross2u")
                .setSize(3000)
                .setQuery(builder)
                .get();

        SearchHits hits = sr.getHits();
        for(SearchHit hit:hits){
            System.out.println("查询数据库啦");
            JSONObject showWare = new JSONObject();
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            showWare.put("wId",jsonObject.getBigInteger("wid"));
            showWare.put("wTitle",jsonObject.getString("wtitle"));
            showWare.put("wMainImage",jsonObject.getString("wmainimage"));
            //货币单位的转换
            Float money = MoneyUtil.transferMoney(jsonObject.getFloat("wstartprice"),jsonObject.getString("wpriceunit"));
            showWare.put("wPrice",jsonObject.getString("money"));
            wareList.add(showWare);
        }
        return wareList;
    }
    @Cacheable(value = {"store"})
    public ArrayList<Object> searchStoreCache(String searchContent)
    {
        ArrayList<Object> storeList = new ArrayList<Object>();
        QueryBuilder qb1 = QueryBuilders.matchQuery("sname", searchContent);

        QueryBuilder builder = QueryBuilders.boolQuery()
                .must(qb1);
//                .filter(qb3)
//                .filter(qb4);

        SearchResponse sr = client.prepareSearch("cross2u")
                .setSize(3000)
                .setQuery(builder)
                .get();

        SearchHits hits = sr.getHits();
        for(SearchHit hit:hits){
            JSONObject showStore = new JSONObject();
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            showStore.put("sId",jsonObject.getBigInteger("sid"));
            showStore.put("sName",jsonObject.getString("sname"));
            showStore.put("sPhoto",jsonObject.getString("sphoto"));
            showStore.put("sScore",jsonObject.getInteger("sscore"));
            storeList.add(showStore);
        }
        return storeList;
    }
}
