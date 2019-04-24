package com.cross2u.user.util;

import java.math.BigInteger;

public class Constant {

	//小程序相关信息
	public static final String LOGINURL="https://api.weixin.qq.com/sns/jscode2session";
    public static final String APPID1="wxca32ff89ecd55173";
    public static final String APPSECRET1="3c95f971bc8f430a2f454d4329f9c480";//官方发送邮箱号


    public static final String APPID2="wx3c90b45c83bd8a1e";
	public static final String APPSECRET2="4d76dc5a3c1f20a359d4e556a30b9843";//官方发送邮箱号
	public static final String GRANTTYPE="authorization_code";//不是真实密码是激活码，用于登录邮箱

	//图像存储相关信息
	public static  final String Secret_id="AKID0jJtXvZOlMn7RVnncuQtJn1zgOyIHFWK";
	public static final String Secret_key="FzUgdXUmobAwSsIWtQG8l8HI3cy3A4jC";
    public static final String PASSWORD = "123456";
	public static final String PATH = "E:/IDEAworkspace/cross2u/Cross2U/blockchain/keystore/UTC--2019-03-19T03-20-23.563424700Z--b3bc658eeee4972b29596e3441e9cfbab06fc651";

	public static final String ADDRESS = "http://localhost:9000";
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(1);
	public static final BigInteger GAS_LIMIT =BigInteger.valueOf(41000) ;
}