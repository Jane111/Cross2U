package com.cross2u.manage.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.manage.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.jfinal.plugin.activerecord.Db.query;

@Service
public class ManageServiceL {


    //52、读取系统的通知
    public JSONArray selectPublicInfo()
    {
        JSONArray infoList = new JSONArray();
        List<Publicinfo> publicinfos = Publicinfo.dao.find("select * from publicinfo");
        for(Publicinfo publicinfo:publicinfos)
        {
            JSONObject info = new JSONObject();
            info.put("piId",publicinfo.getPiId());//通知ID
            info.put("piType",publicinfo.getPiType());//通知类型
            info.put("piTitle",publicinfo.getPiTitle());//通知标题
            info.put("piContent",publicinfo.getPiContent());//通知内容
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            info.put("piCreate",sdf.format(publicinfo.getPiCreateTime()));//创建时间
            infoList.add(info);
        }
        return infoList;
    }


    //27、显示已经设置的关键词
    public JSONArray selectKeyWord()
    {
        JSONArray showKeyWordList = new JSONArray();

        List<Adminkeyword> keywordList = Adminkeyword.dao.find("select akId,akText,akReply from adminkeyword");
        for(Adminkeyword adminkeyword:keywordList)
        {
            JSONObject showKeyWord = new JSONObject();
            showKeyWord.put("akId",adminkeyword.getAkId());
            showKeyWord.put("akText",adminkeyword.getAkText());
            showKeyWord.put("akReply",adminkeyword.getAkReply());
            showKeyWordList.add(showKeyWord);
        }
        return showKeyWordList;
    }
    //31、显示已经设置的敏感词
    public JSONArray selectSensitive()
    {
        JSONArray showSenList = new JSONArray();
        List<Sensi> senList = Sensi.dao.find("select senId,senText from sensi");
        for(Sensi sensitive:senList)
        {
            JSONObject showSen = new JSONObject();
            showSen.put("senId",sensitive.getSenId());
            showSen.put("senText",sensitive.getSenText());
            showSenList.add(showSen);
        }
        return showSenList;
    }
    //49、显示系统通知每一类的数量
    public JSONArray selectPublicInfoNumByClass()
    {
        JSONArray showList = new JSONArray();
        List<Record> recordList = Db.find("select piType,count(*) as totalNum " +
                "from publicinfo GROUP BY piType");
        for(Record record:recordList)
        {
            JSONObject showNum = new JSONObject();
            showNum.put("piType",record.getInt("piType"));
            showNum.put("totalNum",record.getInt("totalNum"));
            showList.add(showNum);
        }
        return showList;
    }

    //50、按类显示系统通知
    public JSONArray selectPublicInfoByClass(Integer piType)
    {
        JSONArray infoList = new JSONArray();
        List<Publicinfo> publicinfos = Publicinfo.dao.find("select * from publicinfo " +
                "where piType=?",piType);
        for(Publicinfo publicinfo:publicinfos)
        {
            JSONObject info = new JSONObject();
            info.put("piId",publicinfo.getPiId());//通知ID
            info.put("piTitle",publicinfo.getPiTitle());//通知标题
            info.put("piContent",publicinfo.getPiContent());//通知内容
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            info.put("piCreate",sdf.format(publicinfo.getPiCreateTime()));//创建时间
            infoList.add(info);
        }
        return infoList;
    }
    //33、显示参数扣分信息
    public JSONObject selectScoreInfo()
    {
        JSONObject scoreList = new JSONObject();
        List<Drawbackreasons> drawbackreasons = selectDrawbackReasons();
        scoreList.put("drawback",drawbackreasons);
        List<Reportgoodreasons> reportgoods = selectReportGoodReasons();
        scoreList.put("reportgoods",reportgoods);
        List<Reportmanufacturereasons> reportm= selectReportManufactureReasons();
        scoreList.put("reportm",reportm);
        List<Reportevaluatereasons> reportevalware = selectReportEvaluateReasons();
        scoreList.put("reportevalware",reportevalware);
        return scoreList;
    }
    //34、修改退货原因减分参数

    //查看退款原因
    public List<Drawbackreasons> selectDrawbackReasons()
    {
        return Drawbackreasons.dao.find("select drId,drReasons,drDeduction from drawbackreasons");
    }
    //44、查看举报商品原因
    public List<Reportgoodreasons> selectReportGoodReasons()
    {
        return Reportgoodreasons.dao.find("select rgrId,rgrContent,rgrPunish from reportgoodreasons");
    }
    //45、查看举报店铺原因
    public List<Reportmanufacturereasons> selectReportManufactureReasons()
    {
        return Reportmanufacturereasons.dao.find("select rmrId,rmrContent,rmrPunish from reportmanufacturereasons");
    }
    //46、查看举报评论原因
    public List<Reportevaluatereasons> selectReportEvaluateReasons()
    {
        return Reportevaluatereasons.dao.find("select rerId,rerContent,rerPunish from reportevaluatereasons");
    }



}
