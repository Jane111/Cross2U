package com.cross2u.ware.util;

import com.baidu.aip.nlp.AipNlp;
import com.baidu.aip.nlp.ESimnetType;
import org.json.JSONObject;

import java.util.HashMap;

public class Sample {
    //设置APPID/AK/SK
    public static final String APP_ID = "15773740";
    public static final String API_KEY = "uXUWOtNY2fCbNLF1OpnAH5fm";
    public static final String SECRET_KEY = "99qgSW4yNMk0ErbglEThTE0A97HWpoHL";

    public static void main(String[] args) {
        // 初始化一个AipNlp
        AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);

//        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000);
//        client.setSocketTimeoutInMillis(60000);

//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        // 调用接口

    }

    public void sample(AipNlp client) {
        HashMap<String, Object> options = null;

        // 获取美食评论情感属性
        JSONObject response = client.commentTag("味道很不错，很喜欢吃。味道很不错。服务也很好感觉很亲切，味道很不错。吃的很舒服，谢谢", ESimnetType.FOOD, options);
        System.out.println(response.toString());

        // 获取酒店评论情感属性
        response = client.commentTag("喜来登酒店不错", ESimnetType.HOTEL, options);
        System.out.println(response.toString());

    }
}
//    log_id	uint64	请求唯一标识码
//        prop	string	匹配上的属性词
//        adj	string	匹配上的描述词
//        sentiment	int	该情感搭配的极性（0表示消极，1表示中性，2表示积极）
//        begin_pos	int	该情感搭配在句子中的开始位置
//        end_pos	int	该情感搭配在句子中的结束位置
//abstract	string	对应于该情感搭配的短句摘要
