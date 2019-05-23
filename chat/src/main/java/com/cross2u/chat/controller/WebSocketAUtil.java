package com.cross2u.chat.controller;

import com.cross2u.chat.service.AChatService;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A双工通信websocket工具类
 */
@ServerEndpoint(value="/webSocketA/{from}/{to}")
@Component
public class WebSocketAUtil {

    AChatService acs = new AChatService();

    // 储存所有建立连接的用户信息，考虑到线程安全，选取支持多线程的ConcurrentHashMap
    private static ConcurrentHashMap<String, Session> clientlist = new ConcurrentHashMap<String, Session>();

    //from, company + "_" + smallAccount   <来源用户，公司客服名称>
    private static Map<String, String> serviceMap = new LinkedHashMap<String, String>();

    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("from") String from, @PathParam("to") String to) {

        // 将连接存入session,如果是客户就存入ID，客服就存入账号
        session.getUserProperties().put("from", from);
        session.getUserProperties().put("to", to);
        // 将创建连接者加入建立连接用户列表中
        clientlist.put(from, session);

        /*
        * 建立连接的几种不同身份
        * 1、M from:m+mId----to:a*，可以主动建立连接ws://localhost:8005/webSocketA/m1/a*,输入a转客服
        * 2、B from:b+bId-----to:a*，可以主动建立连接ws://localhost:8005/webSocketA/b2/a*,输入a转客服
        * 3、A的客服 from:a+aId-----to:bm*，可以主动建立连接ws://localhost:8005/webSocketA/a1/bm*,回复时需要指明回复哪个(如：msg-b1,msg-m1)
        * */
        try {
            if (to.equals("a*")) {
                System.out.println("我是B/M");
                clientlist.get(from).getBasicRemote().sendText("欢迎您访问cross2u，有什么可以帮助您的~");
            }
            else if(to.charAt(0)=='b'&to.charAt(1)=='m')
            {
                System.out.println("我是A的客服");
                clientlist.get(from).getBasicRemote().sendText("欢迎您访问cross2u,祝您工作愉快~");
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
            str2 = str[1];// 客户的标识,b+bId/m+mId
        }

        if (msg.equals("a"))// 客户发来转a人工的请求，输入a
        {
            //进行实际化 从数据库获取a所有的客服账号
            List<String> accounts = acs.getAChatAccount();

            int small = 0;
            boolean isFlag=false;//是否有对应的客服在
            String smallAccount = new String();//现在连接最少的客服账号
            boolean flag = false;//为了第一个在线客服相关信息的赋值

            /*
            * 并取出人数最少的客服账号以及当前人数,默认为0人
            * */
            for (int i = 0; i < accounts.size(); i++) {
                String account = accounts.get(i);
                if (clientlist.containsKey("a"+account)) {// 该客服当前在线
                    isFlag=true;
                    //计算该客服现在有多少个service
                    int count = 0;
                    Iterator<String> iter = serviceMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String val = serviceMap.get(key);
                        if (val.equals("a"+account))
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
                serviceMap.put(from,"a"+smallAccount);// 同时在serviceMap里面存入a+aId
                // 给chat页面返回数据
                clientlist.get(from).getBasicRemote().sendText("在线客服为您服务，请问有什么可以帮助您的~");
                clientlist.get("a"+smallAccount).getBasicRemote().sendText(from);//向客服发送聊天人的信息b1/m1
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
            if (to == null) {//servicemap中没有对应from的数据，可能是客服发的消息，也可能是a、m给机器人发的消息
                // ②客服给客户
                if (str2 != null) {
                    if(str2.charAt(0)=='b')/*a客服给b发消息,内容的最后加上-b+bId*/
                    {
                        clientlist.get(str2).getBasicRemote().sendText(str1);//给客户发消息
                        acs.saveBADialogue(new BigInteger(str2.substring(1)),new BigInteger(from.substring(1)),str1,2);
                    }
                    if(str2.charAt(0)=='m')/*a客服给m发消息,内容的最后加上-m+mId*/
                    {
                        clientlist.get(str2).getBasicRemote().sendText(str1);//给客户发消息
                        acs.saveMADialogue(new BigInteger(str2.substring(1)),new BigInteger(from.substring(1)),str1,2);
                    }

                }
                else {// ①机器人
                    String session = (String)clientlist.get(from).getUserProperties().get("to");
                    String string = acs.aFindAnswer(msg);
                    clientlist.get(from).getBasicRemote().sendText(string);//机器人回答问题
                }
            } else {
                // 客户向人工客服发消息
                System.out.println("客户向人工客服发消息");
                to = serviceMap.get(from);
                System.out.println("from"+from);
                System.out.println("to"+to);
                if(from.charAt(0)=='b')/*b给a客服发消息*/
                {
                    clientlist.get(to).getBasicRemote().sendText(str1);//给客户发消息
                    acs.saveBADialogue(new BigInteger(from.substring(1)),new BigInteger(to.substring(1)),msg,1);
                }
                if(from.charAt(0)=='m')/*m给a客服发消息*/
                {
                    clientlist.get(to).getBasicRemote().sendText(str1);//给客户发消息
                    acs.saveMADialogue(new BigInteger(from.substring(1)),new BigInteger(to.substring(1)),msg,1);
                }
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
            String account = serviceMap.get(from);
            serviceMap.remove(from);
            System.out.println(account + "=============客服账号");
            // 向客服发送一个会话已经结束的消息
            clientlist.get(account).getBasicRemote().sendText("#" + from);
        }
        if (clientlist.get(to) != null) {
            clientlist.get(to).getBasicRemote().sendText("t系统出错了");
        }
    }
}
