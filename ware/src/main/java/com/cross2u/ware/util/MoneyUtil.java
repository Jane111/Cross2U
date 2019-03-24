package com.cross2u.ware.util;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import sun.net.www.protocol.http.HttpURLConnection;

import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 汇率转换工具
 * 现汇卖出价格来计算
 */
public class MoneyUtil {
    //@Autowired
    private static String APPKEY=Constant.APPKEY;

    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36";

    //获取当前汇率 美元转人民币
    public static Float transferMoney(Float origin,String unit)
    {
        if (unit.equals("1"))
        {
            return origin;
        }
        DecimalFormat df = new DecimalFormat("###.00");
        String rateStr= MoneyUtil.getRMB(unit);
        if(rateStr==null){
            return origin;
        }
        com.alibaba.fastjson.JSONObject object= JSON.parseObject(rateStr);
        Float rate=(object.getFloat("fSellPri")) /100;
        System.out.println("rate"+rate);
        String result=df.format(rate*origin);
        Float resultMoney=new Float(result);
        return resultMoney;
    }
    //2-美元
    public static String getRMB(String unitStr){
        if (unitStr.equals("1"))//1-代表人民币
            return null;

        JSONArray array=null;
        if (getRequest()!=null){
            Object request=getRequest().get(0);;//.getJSONArray(0);
            array=JSONArray.fromObject(request);
        }


        if (array==null){
            return "6.716";
        }
        System.out.println("array="+array);
        JSONObject object=array.getJSONObject(0);
        Integer unit=new Integer(unitStr)-1;
        String data="data"+unit;
        String result=object.get(data).toString();
        System.out.println("--------------getRMB result="+result);
        return result;
    }

    //1.人民币牌价
    public static JSONArray getRequest(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/exchange/rmbquot";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//APP Key
        params.put("type","");//两种格式(0或者1,默认为0)

        try {
            result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                System.out.println(object.get("result"));
                String code=object.get("result").toString();
                JSONArray array=JSONArray.fromObject(code);
                return array;
            }
            else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(urlencode(params));
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
