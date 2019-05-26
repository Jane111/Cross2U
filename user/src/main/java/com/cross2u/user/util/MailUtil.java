package com.cross2u.user.util;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Properties;


public class MailUtil {

    static String HOST = "smtp.163.com"; // smtp服务器
    static String FROM = "cross2u@163.com"; // 发件人地址
    static String USER = "cross2u@163.com"; // 用户名
    static String PWD = "2019fuchuang"; // 163的授权码
    static String SUBJECT = "cross2u官方通知"; // 邮件标题


    public static void send(String context,String To) {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);//设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
        props.put("mail.smtp.auth", "true");  //需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        Session session = Session.getDefaultInstance(props);//用props对象构建一个session
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);//用session为参数定义消息对象
        try {
            message.setFrom(new InternetAddress(FROM));// 加载发件人地址
            InternetAddress[] sendTo =  new InternetAddress[1];
            sendTo[0]=new InternetAddress(To);
            message.addRecipients(Message.RecipientType.TO , sendTo);
            message.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(FROM));//设置在发送给收信人之前给自己（发送方）抄送一份，不然会被当成垃圾邮件，报554错
            message.setSubject(SUBJECT);//加载标题
            Multipart multipart = new MimeMultipart();//向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            BodyPart contentPart = new MimeBodyPart();//设置邮件的文本内容
            contentPart.setText(context);
            multipart.addBodyPart(contentPart);
            message.setContent(multipart);//将multipart对象放到message中
            message.saveChanges(); //保存邮件
            Transport transport = session.getTransport("smtp");//发送邮件
            transport.connect(HOST, USER, PWD);//连接服务器的邮箱
            transport.sendMessage(message, message.getAllRecipients());//把邮件发送出去
            transport.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void passMSend(String account,String mail) {
        System.out.println("-------------------sendMail--------------------------------");
        send("你好，\n"+
                " 感谢你注册cross2u智能化跨境电商平台。\n" +
                " 你的登录账号为："+account+"。请进入主页进行登录。",mail);
        System.out.println("-------------------sendMail--------------------------------");
    }


    public static void refuseMSend(String account,String failResons,String failSelect,String mail) {
        System.out.println("-------------------sendMail--------------------------------");
        String failSelectStr;
        switch (failSelect){
            case "1":
                failSelectStr="营业执照";
                break;
            case "2":
                failSelectStr="身份证";
                break;
            case "3":

            default:
                failSelectStr="其他";

        }
        send("你好，\n" +
                "感谢你注册cross2u智能化跨境电商平台。\n" +
                "你的账号为"+account+"申请失败，失败原因是"+failSelectStr+","+failResons+"。",mail);
        System.out.println("-------------------sendMail--------------------------------");
    }
    public static void passBSend(String account,String mail) {
        System.out.println("-------------------sendMail--------------------------------");
        send("你好，\n" +
                "感谢你注册cross2u智能化跨境电商平台。\n" +
                "你的微信认证账号为："+account+"。请进入小程序进行登录。",mail);
        System.out.println("-------------------sendMail--------------------------------");
    }
    public static void refuseBSend(String account,String failResons,String failSelect,String mail) {
        System.out.println("-------------------sendMail--------------------------------");
        String failSelectStr;
        switch (failSelect){
            case "1":
                failSelectStr="身份证";
                break;
            case "2":

            default:
                failSelectStr="其他";

        }
        send("你好，\n" +
                "感谢你注册cross2u智能化跨境电商平台。\n" +
                "你的账号为"+account+"申请失败，失败原因是"+failSelectStr+","+failResons+"。",mail);
        System.out.println("-------------------sendMail--------------------------------");
    }

}
