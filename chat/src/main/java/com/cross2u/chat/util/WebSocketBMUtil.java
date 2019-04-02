package com.cross2u.chat.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.cross2u.chat.controller.ChatController;
import com.cross2u.chat.service.MChatService;
import org.springframework.stereotype.Component;

/**
 * 双工通信websocket工具类
 *
 */
@ServerEndpoint(value="/webSocketM/{from}/{to}")
@Component
public class WebSocketBMUtil {
//    @Autowired
//    ChatController cc = new ChatController();
//    @Autowired
//    MChatService mcs;
    ChatController cc = new ChatController();
    MChatService mcs = new MChatService();

    // 储存所有建立连接的用户信息，考虑到线程安全，选取支持多线程的ConcurrentHashMap
    private static ConcurrentHashMap<String, Session> clientlist = new ConcurrentHashMap<String, Session>();

    //from, company + "_" + smallAccount   <来源用户，公司客服名称>
    private static Map<String, String> serviceMap = new LinkedHashMap<String, String>();

    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("from") String from, @PathParam("to") String to) {

        // 将连接存入session,如果是客户就存入随机的ID，客服就存入账号
        session.getUserProperties().put("from", from);
        session.getUserProperties().put("to", to);
        // 将创建连接者加入建立连接用户列表中
        clientlist.put(from, session);

        /*
        * 建立连接的几种不同身份
        * 1、M的客服 from:m+store表编号-----to:b*，可以主动建立连接ws://localhost:8005/webSocketM/m2/b*，回复时需要指明回复哪个b(如：msg-b1)
        * 2、M的机器人 from:m+#+store编号-----to:b*
        * 3、B本人 from:b+bId-----to:am*，可以主动建立连接ws://localhost:8005/webSocketM/b2/s1输入s+sId转客服
        * 4、A的客服 from:a+a编号-----to:bm*，可以主动建立连接
        * 5、A的机器人 from:a#------to:bm*
        * */
        try {
            if (to.equals("b*")) {
                System.out.println("我是M的人工客服");
                clientlist.get(from).getBasicRemote().sendText("欢迎您登陆该系统！");
            }
            else if(to.charAt(0)=='s')
            {//todo 根据store设置的关键词进行修改
                String sId = to.substring(1);
                System.out.println("我是客户,进入机器人咨询页面");
                clientlist.get(from).getBasicRemote().sendText("根据每个store设置的关键词进行欢迎");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param msg 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String msg, @PathParam("from") String from)throws IOException {

        String str1 = null, str2 = null;
        String str[] = msg.split("-");
        System.out.println(str.length);
        if (str.length == 1)
            str1 = str[0];// 消息
        else {
            str1 = str[0];// 消息
            str2 = str[1];// 客户id
        }

        if (msg.charAt(0) == 's')// 客户发来转m人工的请求，以s开头+sId
        {
            String sId = msg.substring(1);

            // todo 进行实际化 从数据库获取组内所有的客服账号
            List<String> accounts = mcs.getMChatAccount(new BigInteger(sId));

            int small = 0;
            boolean isFlag=false;//是否有对应的客服在
            String smallAccount = new String();//现在连接最少的客服账号
            boolean flag = false;//为了第一个在线客服相关信息的赋值

        /*
        * 并取出人数最少的客服账号以及当前人数,默认为0人
        * */
            for (int i = 0; i < accounts.size(); i++) {
                String account = accounts.get(i);
                if (clientlist.containsKey("m"+account)) {// 该客服当前在线
                    isFlag=true;
                    //计算该客服现在有多少个service
                    int count = 0;
                    Iterator<String> iter = serviceMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String val = serviceMap.get(key);
                        if (val.equals(sId + "_" + account))
                            count++;
                    }
                    //第一个在线的客服
                    if (!flag) {
                        small = count;//最少的数量为第一个客服对应的数值，初始化
                        smallAccount = account;
                        flag = true;
                    }
                    if (count == 0) {
                        smallAccount = account;
                        small = 0;
                        break;
                    }
                    if (small > count) {//有连接数更少的客服
                        smallAccount = account;
                        small = count;
                    }
                }
            }

            if(isFlag)//有客服
            {
                serviceMap.put(from, "s"+sId + "_m" + smallAccount);// 同时在serviceMap里面存入分配到的公司名字_客服账号
                // 给chat页面返回数据
//                clientlist.get(from).getBasicRemote().sendText("s"+sId + "_m" + smallAccount);
                clientlist.get(from).getBasicRemote().sendText("在线客服为您服务，请问有什么可以帮助您的~");
                // 如果位次<2,给被找到的客服账号发送新接入的客户id
                if (small < 2)
                {
                    clientlist.get("m"+smallAccount).getBasicRemote().sendText(from);
                }
            }
            else//无客服
            {
                // 给chat页面返回数据
                clientlist.get(from).getBasicRemote().sendText("目前尚无客服在线，建议留言");
            }

        }
       else {// 需要显示的消息
            System.out.println("需要显示的消息");
            // 获取消息发送对象信息
            String to = serviceMap.get(from);
            if (to == null) {//servicemap中没有对应from的数据，可能是客服发的消息，也可能是b给机器人发的消息
                // ②客服给客户
                if (str2 != null) {
                    System.out.println("客服给客户回复");
//                    clientlist.get(from).getBasicRemote().sendText(msg);//给客服发消息
                    clientlist.get(str2).getBasicRemote().sendText(str1);//给客户发消息
                    mcs.saveBMDialogue(new BigInteger(str2.substring(1)),new BigInteger(from.substring(1)), str1, new BigInteger(from.substring(1)));
                }
                else {// ①机器人
                    String session = (String)clientlist.get(from).getUserProperties().get("to");
                    String string = cc.splitWord(msg, (String) clientlist.get(from).getUserProperties().get("to"));
//                    clientlist.get(from).getBasicRemote().sendText(msg);
                    clientlist.get(from).getBasicRemote().sendText(string);//机器人回答问题
                }
            } else {
                // 客户向人工客服发消息
                System.out.println("客户向人工客服发消息");
                to = serviceMap.get(from);
                to = to.split("_")[1];
                clientlist.get(to).getBasicRemote().sendText(msg);
//                clientlist.get(from).getBasicRemote().sendText(msg);
                mcs.saveBMDialogue(new BigInteger(to.substring(1)),new BigInteger(from.substring(1)), msg, new BigInteger(from.substring(1)));
            }
        }
    }


    /**
     * Websocket出错时触发的事件
     *
     * @param t
     */
    @OnError
    public void OnError(Throwable t) {
        System.out.println("WebSocket出错：" + t.getMessage());
        // log.warn("WebSocket出错：" + t.getMessage());
    }

    /**
     * Websocket连接关闭时触发的事件
     *
     * @throws IOException
     */
    @OnClose
    public void OnClose(Session session, @PathParam("from") String from, @PathParam("to") String to)
            throws IOException {
        System.out.println("关闭Websocket连接");
        // 移除关闭连接的session
        clientlist.remove(from);

        // 若是客户（from）与客服(to)的会话，从map中移除对应的key-value值
        // 目前只能有客户关闭会话
        if (serviceMap.containsKey(from)) {
            boolean flag = false;
            String account = serviceMap.get(from).split("_")[1];
            serviceMap.remove(from);
            System.out.println(account + "=============客服账号");
            // 向客服发送一个会话已经结束的消息
            clientlist.get(account).getBasicRemote().sendText("#" + from);
            int ahead = 0;
            Iterator<String> iter = serviceMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String val = serviceMap.get(key);
                if (val.equals(to)) {
                    ahead++;
                    if (ahead == 2) {
                        clientlist.get(to).getBasicRemote().sendText("n" + from);
//                        as.startDialogue(account, from);
                        flag = true;
                    }
                    if (flag && ahead != 2) {// 将正在排队的人员，向前移动，向正在等待的客户发送消息
                        clientlist.get(key).getBasicRemote().sendText("$" + val + "*" + (ahead - 2));
                    }
                }
            }
        }
        if (clientlist.get(to) != null) {
            clientlist.get(to).getBasicRemote().sendText("t系统出错了");
        }
    }

}
