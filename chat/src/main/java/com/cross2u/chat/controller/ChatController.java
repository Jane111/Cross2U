package com.cross2u.chat.controller;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.chat.model.Abchart;
import com.cross2u.chat.model.Amchart;
import com.cross2u.chat.model.Mbchat;
import com.cross2u.chat.util.BaseResponse;
import com.cross2u.chat.util.ResultCodeEnum;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    BaseResponse jr;
    /*1、查看b和m的聊天记录*///对b来说，一个店铺的M是相同的概念
    @RequestMapping("/getbmChatRecord")
    public BaseResponse getbmChatRecord(
            @RequestParam("mbchBusiness") BigInteger mbchBusiness,
            @RequestParam("sId") BigInteger sId
    )
    {
        List<Record> mListFromS = Db.find("select mId from manufacturer where mStore=?",sId);
        JSONArray result = new JSONArray();
        for(Record mbchat:mListFromS)
        {
            result.addAll(Mbchat.dao.find("select mbchSpeaker,mbchContent,mbchCreateTime " +
                    "from mbchat where mbchBusiness=? AND mbchManu=?",mbchBusiness,mbchat.getBigInteger("mId")));
        }
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
    /*2、查看b和a的聊天记录*///不需要aId,对b来说a是整体
    @RequestMapping("/getabChatRecord")
    public BaseResponse getabChatRecord(
            @RequestParam("abchBusiness") BigInteger abchBusiness
//            , @RequestParam("abchAdministrator") BigInteger abchAdministrator
    )
    {

        List<Abchart> result = Abchart.dao.find("select abchSpeaker,abchContent,abchCreateTime " +
                "from abchart where abchBusiness=?",abchBusiness);
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
    /*3、查看m和a的聊天记录*///不需要aId,对m来说a是整体
    @RequestMapping("/getamChatRecord")
    public BaseResponse getamChatRecord(
            @RequestParam("amchManu") BigInteger amchManu
//            ,@RequestParam("amchAdministrator") BigInteger amchAdministrator
    )
    {

        List<Amchart> result = Amchart.dao.find("select amchSpeaker,amchContent,amchCreateTime " +
                "from amchart where amchManu=?",amchManu);
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

}
