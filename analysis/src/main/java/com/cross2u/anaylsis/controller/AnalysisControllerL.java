//package com.cross2u.anaylsis.controller;
//
//import com.cross2u.anaylsis.util.BaseItemRecommender;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigInteger;
//import java.util.List;
//@RestController
//@RequestMapping("/analysis")
//public class AnalysisControllerL {
//
//    public static final String SENTIMENT_URL =
//            "http://api.bosonnlp.com/sentiment/analysis";
//
//    public String getEP(String message) throws JSONException, UnirestException,java.io.IOException
//    {
//        String body = new org.json.JSONArray(new String[]{message}).toString();
//        HttpResponse<JsonNode> jsonResponse = Unirest.post(SENTIMENT_URL)
//                .header("Accept", "application/json")
//                .header("X-Token", "qaI9lewh.17635.avLIFfMS46BN")
//                .body(body)
//                .asJson();
//
//        String[] result =  jsonResponse.getBody().toString().replace("[", "").split(",");
////		System.out.println(result[0]);//得出的是负面情绪指数
//        return result[0];
//    }
//
//    public HashMap<String,Object> getIndexOfManager(String account)
//    {
//        HashMap<String,Object> data=new HashMap<String,Object>();
//        String[] months={"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
//        Record rd=Db.findFirst("select cid from company where account=?",account);
//        long cid=rd.getLong("cid");
//        Date now=new Date();
//        Calendar cd=Calendar.getInstance();
//        cd.setTime(now);//今天的时间
//        int year=cd.get(Calendar.YEAR);
//        int month=cd.get(Calendar.MONTH)+1;
//        int day=cd.get(Calendar.DAY_OF_MONTH);
//
//        cd.set(Calendar.DATE,cd.get(Calendar.DATE) - 1);
//        int aheadyear=cd.get(Calendar.YEAR);
//        int aheadmonth=cd.get(Calendar.MONTH)+1;
//        int aheadday=cd.get(Calendar.DAY_OF_MONTH);
//        //本周客流量（只包括直接），以及变化趋势
//        int customerNumOfWeek=0;
//        int customerNumOfWeekBefore=0;
//        float customerNumOfWeekRate=0f;
//        Calendar cd1=Calendar.getInstance();
//        for(int i=0;i<7;i++)
//        {//本周数据
//            cd1.set(Calendar.DATE,cd1.get(Calendar.DATE) - 1);
//            int tempyear=cd1.get(Calendar.YEAR);
//            int tempmonth=cd1.get(Calendar.MONTH)+1;
//            int tempday=cd1.get(Calendar.DAY_OF_MONTH);
//            customerNumOfWeek=customerNumOfWeek+Db.find("select cid from dialogue,service where daccep=sid and cid=? and YEAR(dstart)=?"
//                    + " and MONTH(dstart)=? and DAY(dstart)=?",cid,tempyear,tempmonth,tempday).size();
//        }
//        cd1.set(Calendar.DATE,cd.get(Calendar.DATE));
//        cd1.set(Calendar.DATE,cd1.get(Calendar.DATE) - 7);
//        for(int i=0;i<7;i++)
//        {//上一周数据
//            cd1.set(Calendar.DATE,cd1.get(Calendar.DATE) - 1);
//            int tempyear=cd1.get(Calendar.YEAR);
//            int tempmonth=cd1.get(Calendar.MONTH)+1;
//            int tempday=cd1.get(Calendar.DAY_OF_MONTH);
//            customerNumOfWeekBefore=customerNumOfWeekBefore+Db.find("select cid from dialogue,service where daccep=sid and cid=? and YEAR(dstart)=?"
//                    + " and MONTH(dstart)=? and DAY(dstart)=?",cid,tempyear,tempmonth,tempday).size();
//        }
//        if(customerNumOfWeekBefore!=0)
//            customerNumOfWeekRate=(float)(customerNumOfWeek-customerNumOfWeekBefore)/customerNumOfWeekBefore;
//        else
//            customerNumOfWeekRate=1;
//        data.put("customerNumOfWeekRate",String.valueOf(customerNumOfWeekRate));
//        data.put("customerNumOfWeek",String.valueOf(customerNumOfWeek));
//        //会话总数(包括转接+直接)，以及变化趋势
//        int dialogueNum=0;
//        int dialogueNumBefore=0;
//        float dialogueNumRate=0f;
//        dialogueNum=Db.find("select cid from dialogue,service where dialogue.daccep=sid and cid=?"
//                + " and YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=? ",cid,year,month,day).size();
//        dialogueNum=dialogueNum+Db.find("select tid from transfer,service where sid=taccept and cid=?  and YEAR(ttime)=?"
//                + " and MONTH(ttime)=? and DAY(ttime)=?",cid,year,month,day).size();
//        dialogueNumBefore=Db.find("select cid from dialogue,service where dialogue.daccep=sid and cid=?"
//                + " and YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=? ",cid,aheadyear,aheadmonth,aheadday).size();
//        System.out.println("hhh");
//        dialogueNumBefore=dialogueNumBefore+Db.find("select tid from transfer,service where sid=taccept and cid=?  and YEAR(ttime)=?"
//                + " and MONTH(ttime)=? and DAY(ttime)=?",cid,aheadyear,aheadmonth,aheadday).size();
//        if(dialogueNumBefore!=0)
//            dialogueNumRate=(float)(dialogueNum-dialogueNumBefore)/dialogueNumBefore;
//        else
//            dialogueNumRate=1;
//        data.put("dialogueNumRate",String.valueOf(dialogueNumRate));
//        data.put("dialogueNum",String.valueOf(dialogueNum));
//
//        //客服在线时间比率，以及变化趋势
//		/*
//		*sorry不能得出结果
//		*/
//        float onlineRate=0.87f;
//        float onlineChangeRate=0.04f;
//        data.put("onlineRate",String.valueOf(onlineRate));
//        data.put("onlineChangeRate",String.valueOf(onlineChangeRate));
//        //用户平均会话时长
//        long averageTimeOut=0L;
//        List<Record> dialogues=Db.find("select did,dstart,dend from dialogue,service where daccep=sid and cid=? and YEAR(dstart)=?"
//                + " and MONTH(dstart)=? and DAY(dstart)=?",cid,year,month,day);
//        for(int i=0;dialogues!=null&&i<dialogues.size();i++)
//        {
//            Record rd1=dialogues.get(i);
//            Date start=rd1.getDate("dstart");
//            Date end=rd1.getDate("dend");
//            Calendar cd3=Calendar.getInstance();
//            Calendar cd2=Calendar.getInstance();
//            cd3.setTime(start);
//            cd2.setTime(end);
//            long difference=cd2.getTimeInMillis()-cd3.getTimeInMillis();
//            averageTimeOut=averageTimeOut+difference/(60*1000) ;
//        }
//        if(dialogues!=null&&dialogues.size()!=0)
//            averageTimeOut=averageTimeOut/dialogues.size();
//        else
//            averageTimeOut=0;
//        data.put("averageTimeOut",String.valueOf(averageTimeOut));
//        //一周内各天的客户发起会话数（不包括转接的会话）
//        int[] dialogueDetailOfWeek=new int[7];
//        String[][] sb=new String[2][7];
//        cd1.set(Calendar.DATE,cd.get(Calendar.DATE));
//        for(int i=0;i<7;i++)
//        {
//            int tempyear=cd1.get(Calendar.YEAR);
//            int tempmonth=cd1.get(Calendar.MONTH)+1;
//            int tempday=cd1.get(Calendar.DAY_OF_MONTH);
//            dialogueDetailOfWeek[i]=Db.find("select did from dialogue,service where daccep=sid and cid=? and "
//                    +" YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=?",cid,tempyear,tempmonth,tempday).size();
//            sb[0][i]=String.valueOf(cd1.get(Calendar.DAY_OF_MONTH)+" "+months[cd1.get(Calendar.MONTH)]);
//            sb[1][i]=String.valueOf(dialogueDetailOfWeek[i]);
//            cd1.set(Calendar.DATE,cd1.get(Calendar.DATE) - 1);
//        }
//        data.put("dialogueDetailOfWeek",sb);
//
//        //一周内各天的转接会话数
//        int[] transferDetailOfWeek=new int[7];
//        String[][] sb1=new String[2][7];
//        cd1.set(Calendar.DATE,cd.get(Calendar.DATE));
//        for(int i=0;i<7;i++)
//        {
//            int tempyear=cd1.get(Calendar.YEAR);
//            int tempmonth=cd1.get(Calendar.MONTH)+1;
//            int tempday=cd1.get(Calendar.DAY_OF_MONTH);
//            transferDetailOfWeek[i]=Db.find("select tid from transfer,service where taccept=sid and cid=? and "+
//                    "YEAR(ttime)=? and MONTH(ttime)=? and DAY(ttime)=?",cid,tempyear,tempmonth,tempday).size();
//            sb1[0][i]=String.valueOf(cd1.get(Calendar.DAY_OF_MONTH)+" "+months[cd1.get(Calendar.MONTH)]);
//            sb1[1][i]=String.valueOf(dialogueDetailOfWeek[i]);
//            cd1.set(Calendar.DATE,cd1.get(Calendar.DATE) - 1);
//        }
//        data.put("transferDetailOfWeek",sb1);
//        //客户发起会话数(直接)，以及变化趋势
//        int dialogueOfDay=0;
//        int dialogueOfDayBefore=0;
//        float dialogueOfDayRate=0f;
//        dialogueOfDay=Db.find("select did from dialogue,service where daccep=sid and cid=? and "
//                +" YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=?",cid,year,month,day).size();
//        dialogueOfDayBefore=Db.find("select did from dialogue,service where daccep=sid and cid=? and "
//                +" YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=?",cid,aheadyear,aheadmonth,aheadday).size();
//        if(dialogueOfDayBefore!=0)
//            dialogueOfDayRate=(float)(dialogueOfDay-dialogueOfDayBefore)/dialogueOfDayBefore;
//        else
//            dialogueOfDayRate=1;
//        data.put("dialogueOfDayRate",String.valueOf(dialogueOfDayRate));
//        //会话平均消息数，以及变化趋势
//        int messageNum=0;
//        int messageNumBefore=0;
//        float messageNumRate=0f;
//        messageNum=Db.find("select mid from mesdia,service where ssend=sid and cid=? and YEAR(mtime)=? and "+
//                "MONTH(mtime)=? and DAY(mtime)=?",cid,year,month,day).size();
//        messageNumBefore=Db.find("select mid from mesdia,service where ssend=sid and cid=? and YEAR(mtime)=? and "+
//                "MONTH(mtime)=? and DAY(mtime)=?",cid,aheadyear,aheadmonth,aheadday).size();
//        float averageMessageNum=0f;
//        if(dialogueOfDay!=0)
//            averageMessageNum=(float)messageNum/dialogueOfDay;
//        float averageMessageNumBefore=0f;
//        if(dialogueOfDayBefore!=0)
//            averageMessageNumBefore=(float)messageNumBefore/dialogueOfDayBefore;
//        if(averageMessageNumBefore!=0)
//            messageNumRate=(float)(averageMessageNum-averageMessageNumBefore)/averageMessageNumBefore;
//        data.put("averageMessageNum",String.valueOf(averageMessageNum));
//        data.put("messageNumRate",String.valueOf(messageNumRate));
//        //客户平均满意度，以及变化趋势
//        int sumGrade=0;
//        int sumGradeBefore=0;
//        float sumGradeRate=0f;
//        Record rd1=Db.findFirst("select AVG(grade) as average from service,dialogue where sid=daccep and cid=? and YEAR(dstart)=? and MONTH(dstart)=? and DAY(dstart)=?",cid,year,month,day);
//        if(rd1.getBigDecimal("average")!=null)
//            sumGrade=rd1.getBigDecimal("average").intValue();
//        rd1=Db.findFirst("select AVG(grade) as average from service,dialogue where sid=daccep and cid=? and YEAR(dstart)=? and"+
//                " MONTH(dstart)=? and DAY(dstart)=?",cid,aheadyear,aheadmonth,aheadday);
//        if(rd1.getBigDecimal("average")!=null)
//            sumGradeBefore=rd1.getBigDecimal("average").intValue();
//        if(sumGradeBefore!=0)
//            sumGradeRate=(float)(sumGrade-sumGradeBefore)/sumGradeBefore;
//        else
//            sumGradeRate=1f;
//        data.put("sumGrade",String.valueOf(sumGrade));
//        data.put("sumGradeRate",String.valueOf(sumGradeRate));
//        //客服平均在线时长，以及变化趋势
//		/*
//		*无法完成
//		*/
//        float onlineTime=8.1f;
//        float onlineTimeRate=8.1f;
//        data.put("onlineTime",String.valueOf(onlineTime));
//        data.put("onlineTimeRate",String.valueOf(onlineTimeRate));
//        //转接会话数，以及变化趋势
//        int tranferNum=0;
//        int tranferNumBefore=0;
//        float tranferNumRate=0f;
//        tranferNum=Db.find("select tid from transfer,service where taccept=sid and cid=? and "+
//                "YEAR(ttime)=? and MONTH(ttime)=? and DAY(ttime)=?",cid,year,month,day).size();
//        tranferNumBefore=Db.find("select tid from transfer,service where taccept=sid and cid=? and "+
//                "YEAR(ttime)=? and MONTH(ttime)=? and DAY(ttime)=?",cid,aheadyear,aheadmonth,aheadday).size();
//        if(tranferNumBefore!=0)
//            tranferNumRate=(float)(tranferNum-tranferNumBefore)/tranferNumBefore;
//        else
//            tranferNumRate=1f;
//        data.put("tranferNum",String.valueOf(tranferNum));
//        data.put("tranferNumRate",String.valueOf(tranferNumRate));
//        //会话时长分布，0-1,1-3,3-5,5-7,7-9,9-11,11以上
//        int[] dialogueTime=new int[]{0,0,0,0,0,0,0};
//        StringBuffer sb2=new StringBuffer();
//        List<Record> list=Db.find("select dstart,dend from dialogue,service where daccep=sid and cid=? and YEAR(dstart)=? and "+
//                "MONTH(dstart)=? and DAY(dstart)=?",cid,year,month,day);
//        for(int i=0;list!=null&&i<list.size();i++)
//        {
//            Record rd2=list.get(i);
//            Date start=rd2.getDate("dstart");
//            Date end=rd2.getDate("dend");
//            Calendar cd3=Calendar.getInstance();
//            Calendar cd2=Calendar.getInstance();
//            cd3.setTime(start);
//            cd2.setTime(end);
//            long difference=cd2.getTimeInMillis()-cd3.getTimeInMillis();
//            long timeOut=averageTimeOut+difference/(60*1000) ;
//            if(timeOut>=0&&timeOut<1)
//                dialogueTime[0]++;
//            if(timeOut>=1&&timeOut<3)
//                dialogueTime[1]++;
//            if(timeOut>=3&&timeOut<5)
//                dialogueTime[2]++;
//            if(timeOut>=5&&timeOut<7)
//                dialogueTime[3]++;
//            if(timeOut>=7&&timeOut<9)
//                dialogueTime[4]++;
//            if(timeOut>=9&&timeOut<11)
//                dialogueTime[5]++;
//            if(timeOut>=11)
//                dialogueTime[6]++;
//        }
//        for(int i=0;i<7;i++)
//        {
//            if(i!=6)
//                sb2.append(String.valueOf(dialogueTime[i])+"_");
//            else
//                sb2.append(String.valueOf(dialogueTime[i]));
//        }
//        data.put("dialogueOfWeek",sb2.toString());
//        return data;
//    }
//    //获取公司下所有客服（回传客服名字【sname】和id【sid】）
//    //用来管理员给客服发消息的时候选择客服。
//    public List<Record> getAllAgent(String account)
//    {
//        long cid=Db.find("select cid from company where account=?",account).get(0).getLong("cid");
//        List<Record> data=Db.find("select sid,sname from service where cid=?",cid);
//        return data;
//    }
//
//
//}
