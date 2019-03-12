package com.cross2u.ware.util;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于商品的协同过滤算法
 */
@Component
public class BaseItemRecommender {

    public List<BigInteger> recommendBaseItem(BigInteger bId,BigInteger wId) throws Exception  {

        //1、准备数据 来自mySQL数据库
        DataSource dataSource = MyDataSource.getDataSource();
        String preferenceTable = "ratings";//分析表的表名
        String userIDColumn = "bId";//用户Id
        String itemIDColumn = "wId";//商品Id
        String preferenceColumn = "rating";//评分
        String timestampColumn = "";

        //2、定义数据模型
        DataModel dataModel = new MySQLJDBCDataModel(dataSource,preferenceTable,userIDColumn,itemIDColumn, preferenceColumn, timestampColumn);

        //3、计算相似度，相似度算法有很多种，欧几里得、皮尔逊等等。
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);

        //4、构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于物品的协同过滤推荐
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);

        //5、给用户ID推荐8与正在查看商品相似的商品
        List<BigInteger>  wList = new ArrayList<>();//返回的商品List

//        long start = System.currentTimeMillis();
        List<RecommendedItem> recommendedItemList = recommender.recommendedBecause(bId.longValue(), wId.longValue(), 8);
//        List<RecommendedItem> recommendedItemList = recommender.recommendedBecause(bId, wId, 8);
        //打印推荐的结果
        for (RecommendedItem recommendedItem : recommendedItemList) {
//            System.out.println(recommendedItem);
            wList.add(new BigInteger(recommendedItem.getItemID()+""));
        }
//        System.out.println(System.currentTimeMillis() -start);
        return wList;
    }
}