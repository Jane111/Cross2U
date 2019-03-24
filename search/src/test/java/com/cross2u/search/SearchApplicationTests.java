package com.cross2u.search;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.search.controller.SearchControllerL;
import com.cross2u.search.service.SearchServiceL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

	@Autowired
	SearchControllerL searchController;
    @Autowired
	SearchServiceL searchService;
	@Autowired
	StringRedisTemplate stringRedisTemplate;//操作字符串
	@Autowired
	RedisTemplate redisTemplate;//K-V都是字符串

    /*
    * 基本操作的数据类型
    * 1、字符串String
    * 2、列表list
    * 3、集合set
    * 4、散列hash
    * 5、有序集合zset
    * */
	@Test
	public void contextLoads() throws Exception{
//		searchController.mytest();
        /*
        * 操作字符串
        * */
        /*给redis中保存数据*/
//        stringRedisTemplate.opsForValue().append("msg","你好呀");
        /*得到redis中的数据*/
//        System.out.println(stringRedisTemplate.opsForValue().get("msg"));
        /*
        * 操作list
        * */
        /*给list中插入数据*/
          stringRedisTemplate.opsForList().leftPush("mylist","1");
          stringRedisTemplate.opsForList().leftPush("mylist","2");
          stringRedisTemplate.opsForList().leftPush("mylist","3");
          stringRedisTemplate.opsForList().leftPush("mylist","4");
//        stringRedisTemplate.opsForHash();

//        stringRedisTemplate.opsForZSet();
//        stringRedisTemplate.opsForSet();


        /*
        * 测试保存对象
        * */
	}
	@Test
	public void testSearch()
	{
	}



}

