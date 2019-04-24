package com.cross2u.indent.util;

import org.web3j.tx.Contract;

import java.math.BigInteger;

public class Constant {
    /**
     * 订单状态
     * 0：未支付 1：已支付=合作 2：B待评价 3：已完成
     * 4：申请退款 5：B已关闭的 6:M已关闭
     * 7：商品异常关闭（强制停止） 9: M待评价
     */

    public static final  BigInteger GAS = Contract.GAS_LIMIT;
    public static final BigInteger GAS_PRICE = Contract.GAS_PRICE;
    public static final String PASSWORD="123456";
    public static final String PATH="E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-23T01-43-06.760528400Z--6edf076703ec6dd2dd1e14b416b93ebe1320ee4e";
    public static final String ADDRESS="";
    public static final String NETWORKID="82200";

    public static final String IN_WAIT_PAY="0";
    public static final String IN_COOPERATION="1";
    public static final String IN_B_EVAL="2";
    public static final String IN_COMPLETE="3";
    public static final String IN_APPLICATION_DRAWBACK="4";
    public static final String IN_B_CLOSE="5";
    public static final String IN_M_CLOSE="6";
    public static final String IN_ABNOMAL_COMMODITY="7";
    public static final String IN_M_EVAL="9";

    /**
     * 退款申请状态
     * 0-等待供货商处理
     * 1-等待管理员处理
     * 2-供货商同意
     * 3-供货商拒绝
     * 4-管理员同意
     * 5-管理员拒绝
     * 6-B取消退款
     */
    public static final Integer DRSTATUS_WAIT_M=0;
    public static final Integer DRSTATUS_WAIT_A=1;
    public static final Integer DRSTATUS_M_AGREE=2;
    public static final Integer DRSTATUS_M_REFUSE=3;
    public static final Integer DRSTATUS_A_AGREE=4;
    public static final Integer DRSTATUS_A_REFUSE=5;
    public static final Integer DRSTATUS_B_CANCEL=6;

    /**
     * 退款原因
     *1-虚假发货
     * 2-质量问题
     * 3-未按时发货/未履行协议
     * 4-无理由
     * 5-协商退款
     * 6-自身原因
     * 
     * 1-3管理员通过后系统强制退款,全款
     * 4-5全款
     * 6自己输入
     * 7 3M:7B
     */
    public static Integer DRREASON_FALSE_DELIVERY=1;
    public static Integer DRREASON_QUALITY_PROBLEM=2;
    public static Integer DRREASON_DISHONEST=3;
    public static Integer DRREASON_NO_REASON=4;
    public static Integer DRREASON_CONSULT=5;
    public static Integer DRREASON_B_REASON=6;
}
