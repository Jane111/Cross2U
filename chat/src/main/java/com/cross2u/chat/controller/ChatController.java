package com.cross2u.chat.controller;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.io.IOException;
import java.util.*;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    static final float d = 0.85f;
    /**
     * 最大迭代次数
     */
    static final int max_iter = 200;
    static final float min_diff = 0.001f;

    //分词
    public String splitWord(String question,String account) throws IOException
    {
        String result = null;
        question = question.replaceAll("</?[a-zA-Z]+[^><]*>", "");
        System.out.println(question);
        if(question.contains("你好"))
        {
            return "你好，有什么可以帮助您的~";
        }

        /*为了不将商品名称分词分开*/
        String productName = "";
        if(account!=null)//如果是m，有对应的商品
        {
            /*获取询问的商品*/
            productName = getProductName(question,account);//获得已经提取出的产品名称
            question = question.replace(productName, "");
        }
        StringBuffer theWords = getKeyword("", question);//调用其他函数，得到keyword
        if(account!=null)
        {
            theWords.append(" "+productName);//加上产品名称
        }


        if(theWords.length()<1)
        {
            return "抱歉，没有找到答案，是否转为人工客服？";
        }
//        List<Record> list = findAnswer(theWords,account);
        List<String> list = new ArrayList<>();
        list.add("这个是从数据库中得到的答案，也就是机器人的答案");
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
        System.out.println(result);
        return result;
    }

    public static StringBuffer getKeyword(String title, String content)
    {
        Result termList = ToAnalysis.parse(title + content);
        List<String> wordList = new ArrayList<String>();//应该处理的wordList
        for (Term t : termList)
        {
            if (shouldInclude(t))
            {
                wordList.add(t.getName());
            }
        }
        Map<String, Set<String>> words = new HashMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList)//对发来语句的每个分词进行处理
        {
            if (!words.containsKey(w))
            {
                words.put(w, new HashSet<String>());
            }
            que.offer(w);//添加到队列
            if (que.size() > 5)
            {
                que.poll();//从队列中删除第一个元素
            }

            for (String w1 : que)
            {
                for (String w2 : que)
                {
                    if (w1.equals(w2))
                    {
                        continue;
                    }

                    words.get(w1).add(w2);//不相等时，对应的set中
                    words.get(w2).add(w1);
                }
            }
        }
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String other : value)
                {
                    int size = words.get(other).size();
                    if (key.equals(other) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        });
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < entryList.size(); ++i)
        {
            result.append(entryList.get(i).getKey()+" ");
        }
        System.out.println(result);
        return result;
    }
    /**
     * 是否应当将这个term纳入计算，词性属于名词、动词、副词、形容词
     * @param term
     * @return 是否应当
     */
    public static boolean shouldInclude(Term term)
    {
        if (
                term.getNatureStr().startsWith("n") ||
                        term.getNatureStr().startsWith("v") ||
                        term.getNatureStr().startsWith("d") ||
                        term.getNatureStr().startsWith("a")
                )
        {
            return true;
        }
        return false;
    }

    public String getProductName(String question,String mId)
    {
        String productName = null;

        //得到mId对应的主账号，对应的store,对应的ware名称
//        List<String> nameList =
//        for(int i=0;i<nameList.length;i++)
//        {
//            if(question.indexOf(nameList[0][i])!=-1)
//            {
//                productName = nameList[0][i];
//            }
//            else
//            {
//                productName="";
//            }
//        }
//        return productName;
        return "电子产品";
    }

    public List<Record> findAnswer(StringBuffer words, String saccount)
    {
        String string = new String(words);
        String[] str = string.split(" ");
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < str.length; i++)
        {
            sql.append(" and mkText like'%" + str[i] + "%'");
        }
        //公司的Id
//        long cid = Db.findFirst("select cid from company where cname='" + saccount + "'").getLong("cid");
//        //
        return Db.find("select answer from rule,question where company_id=? and question.answer_id=rule_id"
                + sql);
        //与store进行通信，得到问题的回答
//        Manukeyword.dao.find("select mkReply from manuKeyword where mkManu=? ");
    }

       /*
    * 去wiki百科找相关答案
    * */
    /*
    public String test(StringBuffer words)
    {
        StringBuffer resultStr = new StringBuffer();

        String string = new String(words);
        String[] str = string.split(" ");
        try
        {
            HttpRequester request = new HttpRequester();
            request.setDefaultContentEncoding("utf-8");
            HttpRespons hr = request.sendGet("https://www.wikidata.org/w/api.php?action=wbsearchentities&search="+str[0]+"&language=zh&limit=20&format=json");

            JSONObject dataJson = JSONObject.parseObject(hr.getContent());
            JSONArray jsonArray = dataJson.getJSONArray("search");

            int length = jsonArray.size();
            String[] idArray = null;
            String[] labelArray = null;
            String[] descriptionArray = null;

            if(length>5)
            {
                length=5;
            }

            idArray = new String[length];
            labelArray = new String[length];
            descriptionArray = new String[length];
            int labelLength = 0;
            for(int i = 0;i<length;i++)
            {
                if(jsonArray.getJSONObject(i)!=null)
                    idArray[i] = jsonArray.getJSONObject(i).getString("id");
                System.out.println(idArray[i]);
            }
            for(int i = 0;i<length;i++)
            {
                HttpRespons hr2 = request.sendGet("https://www.wikidata.org/w/api.php?action=wbgetentities&ids="+idArray[i]+"&format=json&languages=zh");
                JSONObject dataJson2 = JSONObject.parseObject(hr2.getContent()).getJSONObject("entities").getJSONObject(idArray[i]);
                JSONObject labelJson = dataJson2.getJSONObject("labels").getJSONObject("zh");
                String labelStirng = labelJson.toString();
                String label = null;
                if(!labelStirng.equals("null"))
                {
                    labelLength = i;
                    System.out.println(i);
                    label = labelJson.getString("value");
                    labelArray[i] = label;
                    System.out.println(labelArray[i]);
                    JSONObject descriptionJson = dataJson2.getJSONObject("descriptions").getJSONObject("zh");//.getJSONObject("zh").getString("value");
                    String desString = descriptionJson.toString();
                    String description = null;
                    if(!desString.equals("null"))
                    {
                        description = descriptionJson.getString("value");
                        if(description.equals("null"))
                        {
                            descriptionArray[i] = "抱歉，暂无相关描述";
                        }
                        else
                        {
                            descriptionArray[i] = description;
                        }
                        System.out.println(descriptionArray[i]);
                    }

                }

            }
            System.out.println(labelLength);

            for(int i = 0;i<labelLength;i++)
            {
                if(descriptionArray[i]==null)
                {
                    resultStr.append(labelArray[i] +" : "+ "抱歉，暂无相关描述" + ";"+"</br>");
                }
                else
                {
                    resultStr.append(labelArray[i] +" : "+ descriptionArray[i] + ";"+"</br>");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(resultStr.toString());
        return resultStr.toString();
    }*/


}
