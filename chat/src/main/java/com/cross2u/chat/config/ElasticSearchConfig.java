package com.cross2u.chat.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ElasticSearchConfig {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);
//    @Bean
    public static TransportClient client() {
        logger.info("初始化开始中...");
        TransportClient client = null;
        try {
            TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName("120.79.79.141"),
                    Integer.valueOf(9300));
            // 配置信息
            Settings esSetting = Settings.builder()
                    .put("cluster.name","elasticsearch")
                    .build();
            // 配置信息Settings自定义
            client= new PreBuiltTransportClient(esSetting);
            client.addTransportAddresses(transportAddress);
            System.out.println("client开始赋值");
        } catch (Exception e) {
            logger.error("elasticsearch TransportClient create error!!!", e);
        }
        return client;
    }
}
