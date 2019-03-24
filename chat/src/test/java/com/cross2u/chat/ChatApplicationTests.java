package com.cross2u.chat;

import com.cross2u.chat.service.MChatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatApplicationTests {

    @Autowired
	MChatService mChatService;
	@Test
	public void contextLoads() throws Exception{
		mChatService.searchMKeyWordCache(new StringBuffer("快递"),"1");
	}

}

