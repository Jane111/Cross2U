package com.cross2u.indent.Blockchain;

import com.cross2u.indent.Blockchain.Indent;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class blockchain {

    public static Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
    public static final BigInteger GAS = Contract.GAS_LIMIT;
    public static final BigInteger GAS_PRICE = Contract.GAS_PRICE;
    public static final String accountAddr="0x2022c5953c3dde8ea1e0366976d8334ef3b7b5b2";
    public static final String accountPassword="123456";
    public static final String accountPath="E:/IDEAworkspace/private-geth/keystore/UTC--2019-04-29T21-31-28.875499400Z--2022c5953c3dde8ea1e0366976d8334ef3b7b5b2";

    public static void main(String []args) throws Exception {
        String addr=getContractAddr("1","11111","20190308045620200001","2019-4-30");
        //String result=getContractInfo(addr);
        System.out.println(addr);

    }

    public static void getBalance()throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(accountPassword, accountPath);

        EthGetBalance ethGetBalance = web3j.ethGetBalance(accountAddr, DefaultBlockParameterName.LATEST).send();

        if(ethGetBalance!=null){
            // 打印账户余额
            System.out.println(ethGetBalance.getBalance());
            // 将单位转为以太，方便查看
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));
        }
    }

    public static String getContractAddr(String mId,String wId,String indentNum,String time) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(accountPassword, accountPath);

        EthGetBalance ethGetBalance = web3j.ethGetBalance(accountAddr, DefaultBlockParameterName.LATEST).send();

        //部署智能合约
        RemoteCall<Indent> contract = Indent.deploy(web3j,credentials, GAS_PRICE, GAS,mId,wId,indentNum,time);  // constructor params
        Indent indent=contract.send();
        String contractAddress=indent.getContractAddress();

        String from ="0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e";


        /*System.out.println("deploy addr"+indent.getTransactionReceipt()+"\n address"+indent.getContractAddress()+"\n hash:"+contract.hashCode()+
                "\n contract.getIndentNum()"+indent.getIndentNum()+"\nvalid"+indent.isValid());
        System.out.println("deploy cont getIndentNum="+indent.getIndentNum().send()+"\n getOwner"+indent.getOwner().send());*/

        return contractAddress;
    }

    //获取合约内容
    public static String getContractInfo(String contractAddress) throws Exception{

        Credentials credentials = WalletUtils.loadCredentials(accountPassword, accountPath);

        /*加载合约实例*/
        Indent loadcontract = Indent.load(
                contractAddress,web3j,credentials, GAS_PRICE, GAS);
        System.out.println("isValid=="+loadcontract.isValid());
        if(!loadcontract.isValid()){
            return null;
        }

        String indentNum=loadcontract.getIndentNum().send();//String mId,String wId,String indentNum,String time
        String mId=loadcontract.getmId().send();
        String wId=loadcontract.getwId().send();
        String time=loadcontract.getTime().send();
        String result=mId+"#"+wId+"#"+indentNum+"#"+time;
        return result;
    }


    //交易
    public void trans() throws Exception{

        Credentials credentials = WalletUtils.loadCredentials("123456", "E:\\IDEAworkspace\\private-geth\\keystore\\UTC--2019-03-23T01-43-06.760528400Z--6edf076703ec6dd2dd1e14b416b93ebe1320ee4e");

        Admin admin = Admin.build(new HttpService());

        String from = "0x2ec3becaf7519976f72a099600360e53d18747bf";//一号节点上的一个账户
        String to = "0x643ad5ce39a548a424af2506d702fd8b31986197";//二号节点上的一个账户
        PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(from, "123456").send();
        if (personalUnlockAccount.accountUnlocked()) {
            // send a transaction
        }
        //完成交易
        Transaction transaction1 = Transaction.createEthCallTransaction(from,to, "刘静是猪");
        //System.out.println("tranc: "+transaction1.getData()+"/n tranc addr");

        //交易类型2
        try {
            //addressHexString： String - 要获得交易数的地址。 0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e
            EthGetTransactionCount getNonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();

            if (getNonce == null){
                throw new RuntimeException("net error");
            }
            BigInteger nonce= getNonce.getTransactionCount();


            //System.out.println("2 trans  "+transaction2.getData());
        } catch (Exception e) {
            throw new RuntimeException("net error");
        }
    }

    BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).sendAsync().get();

        return ethGetTransactionCount.getTransactionCount();
    }
}
