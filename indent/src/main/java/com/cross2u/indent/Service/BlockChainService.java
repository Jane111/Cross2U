package com.cross2u.indent.Service;

import com.cross2u.indent.util.Indent_sol_Indent;
import com.cross2u.indent.util.IpfsFile;
import com.cross2u.user.util.Constant;
import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigInteger;

@Service
public class BlockChainService {

    // get hash form blockchain, then get data from ipfs using the hash
    public String getData(Web3j web3j) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials(Constant.PASSWORD, Constant.PATH);
        String address = Constant.ADDRESS;
        Indent_sol_Indent indent = Indent_sol_Indent.load(Constant.ADDRESS, web3j, credentials, Constant.GAS_PRICE,
                Constant.GAS_LIMIT);
        String ipfs_hash = indent.getContractAddress();
        System.out.println("ipfshash"+ipfs_hash);
        String data = IpfsFile.get(ipfs_hash);

        return data;
    }

    // set data to ipfs and get a hash, the save the hash to blockchain
    public Boolean setData(Web3j web3j, String data) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(Constant.PASSWORD,  Constant.PATH);
        String address = Constant.ADDRESS;
        Indent_sol_Indent indent = Indent_sol_Indent.load(Constant.ADDRESS, web3j, credentials, Constant.GAS_PRICE,
                Constant.GAS_LIMIT);
        String ipfs_hash = IpfsFile.add(data);
        //indent.setData(ipfs_hash, Constant.GAS_VALUE).send();
        return true;
    }

}
