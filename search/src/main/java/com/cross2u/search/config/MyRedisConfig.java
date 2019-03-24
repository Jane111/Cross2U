package com.cross2u.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class MyRedisConfig {
//    @Bean
//    public RedisTemplate<Object,Ware> wareRedisTemplate(
//            RedisConnectionFactory redisConnectionFactory)
//        throws UnknownHostException{
//        RedisTemplate<Object,Ware> template = new RedisTemplate<Object,Ware>();
//        template.setConnectionFactory(redisConnectionFactory);
//        /*创建json2redisserialier序列器*/
//        Jackson2JsonRedisSerializer<Ware> ser = new Jackson2JsonRedisSerializer<Ware>(Ware.class);
//        /*为template设置默认序列器*/
//        template.setDefaultSerializer(ser);
//        return template;
//    }
//    @Bean
//    public RedisCacheManager cust_cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        //创建自定义序列化器
//        Jackson2JsonRedisSerializer<Ware> jsonSeria = new Jackson2JsonRedisSerializer<Ware>(Ware.class);
//        //包装成SerializationPair类型
//        RedisSerializationContext.SerializationPair serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSeria);
//        //redis默认配置文件,并且设置过期时间
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
//        //设置序列化器
//        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(serializationPair);
//        //RedisCacheManager 生成器创建
//        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisCacheConfiguration);
//        return builder.build();
//    }
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);

        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }




}
