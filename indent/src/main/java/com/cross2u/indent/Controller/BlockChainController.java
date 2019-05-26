package com.cross2u.indent.Controller;

import com.cross2u.indent.Service.BlockChainService;
import com.cross2u.indent.util.BaseResponse;
import com.cross2u.indent.util.Indent_sol_Indent;
import com.cross2u.user.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

@RestController("/blockchain")
public class BlockChainController {

    @Autowired
    BlockChainService service;
    @Autowired
    BaseResponse response;

    /**
     * 创建区块 返回区块地址
     */
    public BaseResponse createContract(
            @RequestParam("mId") String _mId,
            @RequestParam("wId")String _wId,
            @RequestParam("indentNum")String _indentNum,
            @RequestParam("time")String _time

    ) throws Exception {
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials("123456", "../private-geth/data/00/keystore/UTC--2019-03-19T00-46-51.796923300Z--6eaef1aa6f20eeaa24fc6cae979baccaee35aed6");

        Indent_sol_Indent contract = Indent_sol_Indent.deploy(
                web3j,credentials, Constant.GAS_PRICE, Constant.GAS_LIMIT,"1","2","3","2019-3-19").send();  // constructor params

        response.setData(contract.getOwner());
        return response;
    }

    /**
     * 生成二维码
     */

    /**
     * 导出图片
     */

    /**
     * 根据二维码显示商品信息
     */


}
