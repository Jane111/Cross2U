package com.cross2u.anaylsis.util;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;

/**
 * Created by ahu_lichang on 2017/6/23.
 */
public class BaseUserRecommender {
    public static void main(String[] args) throws Exception {
        //1、准备数据 来自mySQL数据库
        DataSource dataSource = JfinalOrmConfig.getDataSource();
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
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);

        //5、构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于用户的协同过滤推荐
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);

        //给用户ID等于5的用户推荐10部电影
        List<RecommendedItem> recommendedItemList = recommender.recommend(5, 10);
        //打印推荐的结果
        System.out.println("使用基于用户的协同过滤算法");
        System.out.println("为用户5推荐10个商品");
        for (RecommendedItem recommendedItem : recommendedItemList) {
            System.out.println(recommendedItem);
        }
    }
}
