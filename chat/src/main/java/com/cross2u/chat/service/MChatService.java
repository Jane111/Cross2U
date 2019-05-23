package com.cross2u.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.chat.config.ElasticSearchConfig;
import com.cross2u.chat.model.Abchart;
import com.cross2u.chat.model.Amchart;
import com.cross2u.chat.model.Mbchat;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
@Service
public class MChatService {

    /*m的机器人聊天*/
    public String mFindAnswer(String question,String account) throws IOException
    {
        String result = "";
        question = question.replaceAll("</?[a-zA-Z]+[^><]*>", "");
        System.out.println(question);
        if(question.contains("你好"))
        {
            return "你好，有什么可以帮助您的~";
        }

        //elasticsearch搜索关键词得到，关键词的回答
        List<String> list = searchMKeyWordCache(question,account);

        if(list.isEmpty())
        {
            result = "抱歉，没有找到答案，是否转为人工客服？";
        }
        else
        {
            StringBuffer answerStr = new StringBuffer();
            for (int i = 0; i < list.size(); i++)
            {
                answerStr.append(list.get(i)+" ");
            }
            result = answerStr.toString();
        }
        return result;
    }

    //1、得到M对应的所有客服账号
    public List<String> getMChatAccount(BigInteger sId) {

        List<String> mIdList = new ArrayList<>();
        //在manu表中得到客服(mManageMessage==1)账号mId
        List<Record> mList = Db.find("select mId from manufacturer " +
                "where mStore=? and mManageMessage=?",sId,1);
        for(Record record:mList)
        {
            mIdList.add(record.getStr("mId"));
        }
        return mIdList;
    }

    // 存储会话消息
    public void saveBMDialogue(BigInteger bId, BigInteger mId, String msg, Integer from)
    {
        Mbchat mbchat = new Mbchat();
        mbchat.setMbchBusiness(bId);
        mbchat.setMbchManu(mId);
        mbchat.setMbchContent(msg);
        mbchat.setMbchSpeaker(from);
        mbchat.save();
    }

    //搜索M的KeyWord
    @Cacheable(value = "manukeyword" ,key="#a0")//以第一个参数作为key
    public List<String> searchMKeyWordCache(String searchContent,String account)
    {
        TransportClient client = ElasticSearchConfig.client();
        List<String> mKeyList = new ArrayList<String>();

        //得到mId
        BigInteger mId = new BigInteger(account.substring(1));
        //根据mId得到店铺sId
        Integer sId = Db.findFirst("select mStore from manufacturer " +
                "where mId=?",mId).getInt("mStore");

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
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            mKeyList.add(jsonObject.getString("mkreply"));
        }
        return mKeyList;
    }
}
