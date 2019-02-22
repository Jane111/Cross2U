package com.cross2u.user.service;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.user.model.Indent;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class MServiceL {

    //（四）订单管理 6、7、9、10订单列表
    public JSONArray selectIndent(BigInteger sId,)
    //（四）订单管理 8、评价订单
    public boolean updateIndent(Indent indent)
    {
        return indent.update();
    }

}
