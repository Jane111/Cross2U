package com.cross2u.store.util;

public class Constant {
    /**
     * 0-审核中
     * 1-已通过审核=代理中
     * 2-终止合作
     * 3-M拒绝合作
     * 4-B取消申请
     */
    public static final Integer COP_VERIFY=0;
    public static final Integer COP_ING=1;
    public static final Integer COP_END=2;
    public static final Integer COP_M_REFUSE=3;
    public static final Integer COP_B_CANCLE=4;

    /**
     * 店铺状态
     */
    public static final Integer STORE_ING=1;//店铺运行中
    public static final Integer STORE_CLOSE=0;//店铺被封，M待审核

    /**
     * 店铺到款状态
     * 0-未开通 7成给M，3成平台担保
     * 1-开通
     */
    public static final Integer PAY_UN=0;
    public static final Integer PAY_DIRECT=1;
    /**
     * 减少库存方式
     * 0-拍下减库存
     * 1-付款减库存
     */
    public static final Integer REDUCE_GET=0;//拍下减库存
    public static final Integer REDUCE_PAY=1;//
}
