package com.cross2u.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.cross2u.chat.config.ElasticSearchConfig;
import com.cross2u.chat.model.Abchart;
import com.cross2u.chat.model.Amchart;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class AChatService {

    /*a的机器人聊天*/
    public String aFindAnswer(String question) throws IOException
    {
        String result = "";
        question = question.replaceAll("</?[a-zA-Z]+[^><]*>", "");
        System.out.println(question);
        if(question.contains("你好"))
        {
            return "你好，有什么可以帮助您的~";
        }

        //elasticsearch搜索关键词得到，关键词的回答
        List<String> list = searchAKeyWordCache(question);

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
    //2、得到A对应的客服账号
    public List<String> getAChatAccount() {
        List<String> aIdList = new ArrayList<>();

        //在admin表中得到所有的客服账号
        List<Record> aList = Db.find("select aId from administrator " +
                "where aPostion=? and aStatus=?",4,1);
        for(Record record:aList)
        {
            aIdList.add(record.getStr("aId"));
            System.out.println("aId"+record.getStr("aId"));
        }
        return aIdList;
    }
    //存储对话消息
    public void saveBADialogue(BigInteger bId, BigInteger aId, String msg, Integer from)
    {
        Abchart abchart = new Abchart();
        abchart.setAbchAdministrator(aId);
        abchart.setAbchBusiness(bId);
        abchart.setAbchContent(msg);
        abchart.setAbchSpeaker(from);//1-b,2-a
        abchart.save();
    }
    //存储对话消息
    public void saveMADialogue(BigInteger mId, BigInteger aId, String msg, Integer from)
    {
        Amchart amchart = new Amchart();
        amchart.setAmchManu(mId);
        amchart.setAmchAdministrator(aId);
        amchart.setAmchContent(msg);
        amchart.setAmchSpeaker(from);//1-m,2-a
        amchart.save();
    }
    //搜索a的KeyWord
    @Cacheable(value = "adminkeyword")//以第一个参数作为key
    public List<String> searchAKeyWordCache(String searchContent)
    {
        TransportClient client = ElasticSearchConfig.client();
        List<String> mKeyList = new ArrayList<String>();

        QueryBuilder qb1 = QueryBuilders.matchQuery("aktext", searchContent);

        QueryBuilder builder = QueryBuilders.boolQuery()
                .must(qb1);

        SearchResponse sr = client.prepareSearch("cross2u")
                .setSize(3000)
                .setQuery(builder)
                .get();

        SearchHits hits = sr.getHits();
        for(SearchHit hit:hits){
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            mKeyList.add(jsonObject.getString("akreply"));
        }
        return mKeyList;
    }

}
