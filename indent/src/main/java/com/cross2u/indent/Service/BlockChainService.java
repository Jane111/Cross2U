package com.cross2u.indent.Service;

import com.cross2u.indent.Blockchain.Indent;
import com.cross2u.indent.util.IpfsFile;
import com.cross2u.indent.util.Constant;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Service
public class BlockChainService {

    // get hash form blockchain, then get data from ipfs using the hash
    public String getData(Web3j web3j) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials(Constant.PASSWORD, Constant.PATH);
        String address = Constant.ADDRESS;
        Indent indent = Indent.load(Constant.ADDRESS, web3j, credentials, Constant.GAS_PRICE,
                Constant.GAS);
        String ipfs_hash = indent.getContractAddress();
        System.out.println("ipfshash"+ipfs_hash);
        String data = IpfsFile.get(ipfs_hash);

        return data;
    }

    // set data to ipfs and get a hash, the save the hash to blockchain
    public String setData(String _mid, String _wId,String _indentNum,String _time) throws Exception {
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/ http://10.169.42.25:9090/

        Credentials credentials = WalletUtils.loadCredentials(Constant.PASSWORD,  Constant.PATH);
        String address = Constant.ADDRESS;
        Indent contract = Indent.deploy(web3j,credentials, Constant.GAS_PRICE, Constant.GAS,_mid,_indentNum,_indentNum,_time).send();  // constructor params

        return contract.getDeployedAddress(Constant.NETWORKID);
    }

}
