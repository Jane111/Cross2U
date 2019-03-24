package com.cross2u.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.chat.model.Abchart;
import com.cross2u.chat.model.Amchart;
import com.cross2u.chat.model.Mbchat;
import com.jfinal.plugin.activerecord.Db;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
@Service
public class MChatService {

    @Autowired
    private TransportClient client;

    //1、得到M对应的所有客服账号
    public List<String> getMChatAccount(BigInteger sId) {
        //与store模块通信
        //在manu表中得到客服(mManageMessage==1)账号mId

        List<String> mIdList = new ArrayList<>();
        mIdList.add("1");
        mIdList.add("2");
        mIdList.add("3");
        return mIdList;
    }
    //2、得到A对应的客服账号
    public List<String> getAChatAccount() {
        List<String> aIdList = new ArrayList<>();
        aIdList.add("1");
        aIdList.add("2");
        aIdList.add("3");
        return aIdList;
    }
    // 存储会话消息
    public void saveBMDialogue(BigInteger bId, BigInteger mId, String msg, BigInteger from)
    {
        Mbchat mbchat = new Mbchat();
        mbchat.setMbchBusiness(bId);
        mbchat.setMbchManu(mId);
        mbchat.setMbchContent(msg);
        if(bId==from)//说话者为b
        {
            mbchat.setMbchSpeaker(2);
        }
        else//说话者为M
        {
            mbchat.setMbchSpeaker(1);
        }
        mbchat.save();
    }
    public void saveBADialogue(BigInteger bId, BigInteger aId, String msg, BigInteger from)
    {
        Abchart abchart = new Abchart();
        abchart.setAbchAdministrator(aId);
        abchart.setAbchBusiness(bId);
        abchart.setAbchContent(msg);
        if(bId==from)//说话者为b
        {
            abchart.setAbchSpeaker(1);
        }
        else//说话者为A
        {
            abchart.setAbchSpeaker(2);
        }
        abchart.save();
    }
    public void saveMADialogue(BigInteger mId, BigInteger aId, String msg, BigInteger from)
    {
        Amchart amchart = new Amchart();
        amchart.setAmchManu(mId);
        amchart.setAmchAdministrator(aId);
        amchart.setAmchContent(msg);
        if(mId==from)//说话者为m
        {
            amchart.setAmchSpeaker(1);
        }
        else//说话者为A
        {
            amchart.setAmchSpeaker(2);
        }
        amchart.save();
    }
    //搜索M的KeyWord
    @Cacheable(value = "manukeyword")
    public List<Object> searchMKeyWordCache(StringBuffer searchContent,String account)
    {
        List<Object> mKeyList = new ArrayList<Object>();
        //得到mId
        BigInteger mId = new BigInteger(account.substring(1));
        //根据mId得到店铺sId
        BigInteger sId = Db.findFirst("select mStore from manufacturer " +
                "where mId=?",mId).getBigInteger("mStore");

        QueryBuilder qb1 = QueryBuilders.matchQuery("mktext", searchContent);
        QueryBuilder qb2 = QueryBuilders.termQuery("mkstore", sId);

        QueryBuilder builder = QueryBuilders.boolQuery()
                .must(qb1).filter(qb2);

        SearchResponse sr = client.prepareSearch("cross2u")
                .setSize(3000)
                .setQuery(builder)
                .get();

        SearchHits hits = sr.getHits();
        for(SearchHit hit:hits){
            JSONObject showReply = new JSONObject();
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            showReply.put("mkId",jsonObject.getBigInteger("mkid"));
            showReply.put("mkText",jsonObject.getString("mktext"));
            showReply.put("mkReply",jsonObject.getString("mkreply"));
            mKeyList.add(showReply);
        }
        return mKeyList;
    }




}
