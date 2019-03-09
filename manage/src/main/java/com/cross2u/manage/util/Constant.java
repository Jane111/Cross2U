package com.cross2u.manage.util;

public class Constant {

	//小程序相关信息
	public static final String LOGINURL="https://api.weixin.qq.com/sns/jscode2session";
	public static final String APPID="wx3c90b45c83bd8a1e";  //邮件的端口
	public static final String APPSECRET="020acbca530664df4a8bc81acf56304b";//官方发送邮箱号
	public static final String GRANTTYPE="authorization_code";//不是真实密码是激活码，用于登录邮箱


	//M的状态
	public static final Integer M_CLOSE=0;
	public static final Integer M_ING=1;
	public static final Integer M_WAIT=2;
	public static final Integer M_FALIURE=3;

	/**
	 *B的状态
	 * 0-禁用
	 * 1-在用
	 * 2-待审核
	 * 审核失败
	 */
	public static final Integer B__CLOSE=0;
	public static final Integer B_ING=1;
	public static final Integer B_WAIT=2;
	public static final Integer B_FALIURE=3;

	/**异常商品信息状态
	 * 0-未处理
	 * 1-处理通过
	 * 2-退回
	 */
	public static final Integer ABGOODS_WAIT=0;
	public static final Integer ABGOODS_AGREE=1;
	public static final Integer ABGOODS_DISAGREE=2;
	/**
	 * 异常店铺状态
	 * 0-未处理
	 * 1-处理通过
	 * 2-退回
	 */
	public static final Integer ABS_WAIT=0;
	public static final Integer ABS_AGREE=1;
	public static final Integer ABS_DISAGREE=2;
}