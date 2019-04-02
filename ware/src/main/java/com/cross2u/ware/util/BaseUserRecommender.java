package com.cross2u.ware.util;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
/*
* 基于用户的协同过滤算法
* */
@Component
public class BaseUserRecommender {

    public List<BigInteger> recommendBaseUser(BigInteger bId) throws Exception{
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
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        //4、计算最近邻域，邻居有两种算法，基于固定数量的邻居和基于相似度的邻居，这里使用基于固定数量的邻居
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(10, similarity, dataModel);

        //5、构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于用户的协同过滤推荐
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);

        //给用户推荐64个商品
        List<RecommendedItem> recommendedItemList = recommender.recommend(bId.longValue(), 64);

        //返回推荐的结果
        List<BigInteger>  wList = new ArrayList<>();//返回的商品List
        for (RecommendedItem recommendedItem : recommendedItemList) {
            wList.add(new BigInteger(recommendedItem.getItemID()+""));
        }
        return wList;
    }
}
