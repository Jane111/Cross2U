package com.cross2u.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.cross2u.user.model.Indent;
import com.cross2u.user.service.MServiceL;
import java.math.BigInteger;
import com.cross2u.user.util.JsonResult;
import com.cross2u.user.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manufacturer")
public class MContollerL {
    @Autowired
    MServiceL ms;
    JsonResult jr;

    //（四）订单管理 6、7、9、10订单列表
    @RequestMapping("/order/showOrders")
    public JsonResult showOrders(
            @RequestParam("sId") BigInteger sId,
            @RequestParam("requestFlag") Integer requestFlag)
    {
        JSONArray result = ms.updateIndent(indent);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return jr;
    }


    //（四）订单管理 8、评价订单
    @RequestMapping("/order/evaluateOrder")
    public JsonResult evaluateOrder(
            @RequestParam("inId") BigInteger inId,
            @RequestParam("inMtoB") Integer inMtoB)
    {
        Indent indent = new Indent();
        indent.setInId(inId);
        indent.setInMtoB(inMtoB);
        boolean result = ms.updateIndent(indent);
        if(!result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        return jr;
    }
}
