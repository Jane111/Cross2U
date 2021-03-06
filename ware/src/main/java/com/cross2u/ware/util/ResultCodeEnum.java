package com.cross2u.ware.util;


public enum ResultCodeEnum
{
	SUCCESS("10000", "Success"),
	NetERROR("10001","网络错误,请稍后再试"),
	NOT_FIND("10002","访问资源不存在"),
	DATABASE_ERROR("10003","数据库异常，请稍后重试"),
	UPDATE_ERROR("10004","修改失败"),
	ADD_ERROR("10005","添加失败"),
	DELETE_ERROR("10006","删除失败"),
	FIND_ERROR("10007","查询失败"),
	NOT_COMPLETE("10008","参数为空"),
	NOT_ENOUGH_PARA("10009", "请将信息填写完整"),
	DATA_ERROR("10010","参数格式错误"),
	EXIST_USER("10011","该学号已存在"),
	EXIST_USER_EMAIL("10012","邮箱已经被注册"),
	EXIST_USER_PHONE("10013","手机号已经被注册"),
	ERROR_ACCOUNT_OR_PASSWORD("10014","账户或密码错误"),
	NOT_LOGIN("10015", "您还未登录，请先登录！"),
	ADD_FAILURE("20001","添加失败"),
	ADD_PRODUCT_FAILURE("20010","单品添加失败"),
	ADD_WARE_FAILURE("20011","商品添加失败"),
	WARE_CLASS_FAILURE("20012","商品分类失败"),
	DISPATCH_IS_IN("20013","已经加入该分类中"),
	HAS_ING_INDENT("20005","存在正在执行的订单"),
	FIND_FAILURE("20004","查询失败"),
	HAS_SENSI("200014","存在敏感词"),
	S_NOT_BELONG_F("200015","该子类不属于该父类");
    private String code;
    private String desc;

    ResultCodeEnum(String code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }

    public String getCode()
    {
        return code;
    }

    public String getDesc()
    {
        return desc;
    }


}
