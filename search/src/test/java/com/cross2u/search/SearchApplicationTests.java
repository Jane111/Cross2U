package com.cross2u.search;

import com.cross2u.search.controller.SearchController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

	@Test
	public void contextLoads() throws Exception{
        SearchController sc =new SearchController();
        sc.searchEntity();
	}

}

