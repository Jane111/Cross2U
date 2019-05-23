package com.cross2u.ware.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.nlp.AipNlp;
import com.baidu.aip.nlp.ESimnetType;
import com.cross2u.ware.model.Evalware;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;

@Component
public class CommentUtil {

    String APP_ID = "15773740";
    String API_KEY = "uXUWOtNY2fCbNLF1OpnAH5fm";
    String SECRET_KEY = "99qgSW4yNMk0ErbglEThTE0A97HWpoHL";
    AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
    HashMap<String, Object> options = null;

    public JSONObject getCommentTag(BigInteger wId)
    {
        JSONObject showTags = new JSONObject();
        List<Evalware> commentList = Evalware.dao.find("select ewCotent,ewImg,ewImg2,ewImg3 " +
                "from evalware where ewWId=?",wId);
        String commentString = "";
        Integer hasImg = 0;
        for(Evalware evalware:commentList)
        {
            commentString += evalware.getEwCotent();
            if(evalware.getEwImg()!=null || evalware.getEwImg2()!=null || evalware.getEwImg3()!=null)
            {
                hasImg++;
            }
        }
        //有图评论的个数
        JSONObject Img = new JSONObject();
        showTags.put("img",hasImg);
        // 获取购物评论情感属性
        org.json.JSONObject response = client.commentTag(commentString, ESimnetType.BUSINESS, options);
        System.out.println(response.toString());
        org.json.JSONArray items = response.getJSONArray("items");
        JSONArray comList = new JSONArray();
        for(int i=0;i<items.length();i++)
        {
            org.json.JSONObject itemJsonObject = items.getJSONObject(i);
            JSONObject aTag =  new JSONObject();
            aTag.put("tag",itemJsonObject.getString("prop")+itemJsonObject.getString("adj"));
            aTag.put("sentiment",itemJsonObject.getInt("sentiment"));
            comList.add(aTag);

        }
        showTags.put("list",comList);
        System.out.println(showTags.toString());
        return showTags;
    }


}
