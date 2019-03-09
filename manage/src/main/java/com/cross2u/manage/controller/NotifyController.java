/*
package com.cross2u.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class NotifyController {
    */
/**
     * 获取小程序用户信息
     *
     * @param codeDto
     * @return
     *//*

    @RequestMapping(value = "miniprogram")
    @ResponseBody
    @Transactional(readOnly = false)
    public Object getMiniProgramUserInfo(HttpServletResponse response, HttpServletRequest request, Model model, @RequestBody WxCodeDto codeDto) {
        //response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            logger.info("miniprogram  WxCodeDto  >>  " + codeDto);
            Map<String, String> unionIdForXgx = WeixinKit.getUnionIdForXgx(codeDto.getEncryptedData(), codeDto.getIv(), codeDto.getCode(), "wx4533db2ce8b7c8ff", "f0037f13c01b49f1ae66d25e8a5df79f");
            logger.info("miniprogram  userinfo   >>  " + unionIdForXgx);
            if(Integer.parseInt(unionIdForXgx.get("status"))==1) {
                return DtoUtils.setSuccess("录入数据成功", unionIdForXgx);
            }else {
                return DtoUtils.setError("解密失败");
            }
        } catch (Exception e) {
            return DtoUtils.setError("请求微信服务端异常");
        }

    }
}
*/
