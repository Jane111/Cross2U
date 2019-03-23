package com.cross2u.indent;

import com.cross2u.indent.Service.IndentServiceL;
import com.cross2u.indent.Service.IndentServiceZ;
import com.cross2u.indent.util.Indent_sol_Indent;
import com.cross2u.user.util.Constant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndentApplicationTests {

	@Autowired
    IndentServiceZ is;

	@Autowired
    IndentServiceL IS;
	/*@Test
	public void contextLoads() {
		try {
			is.createContract("1","2","111","2019-3-18");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("异常"+e);
		}
	}
	@Test
	public void getContract(){
		try {
			is.getContractInfo("0xed817e5546f172176b3d94b69783d4f906153dd5");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("异常"+e);
		}
	}*/

    BigInteger GAS = Contract.GAS_LIMIT;
    BigInteger GAS_PRICE = Contract.GAS_PRICE;

	@Test
    public void test() throws Exception {
		Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/ http://10.169.42.25:9090/
        Credentials credentials = WalletUtils.loadCredentials("123456", "E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-22T09-07-59.250376800Z--618a0456af5142c9c97797825d2a5f5b2a8a67ae");

        String address = "0x618a0456af5142c9c97797825d2a5f5b2a8a67ae";

        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();

        if(ethGetBalance!=null){
        // 打印账户余额
            System.out.println(ethGetBalance.getBalance());
        // 将单位转为以太，方便查看
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));
        }

        //部署智能合约
        Indent_sol_Indent contract = Indent_sol_Indent.deploy(web3j,credentials, GAS_PRICE, GAS,"11","22","33","2019-3-22").send();  // constructor params

        //Indent_sol_Indent test=Indent_sol_Indent.load(contract.getContractAddress(),web3j,credentials, GAS_PRICE, GAS);

        //Transaction transaction=contract.

        System.out.println("address"+contract.getContractAddress()+"     hash:"+contract.hashCode()+"  contract.getIndentNum()"+contract.getIndentNum()+" valid"+contract
        .isValid());
        System.out.println(contract.toString());
        //调用智能合约
        System.out.println(contract.getIndentNum().send());
        //System.out.println(test.toString());
        //System.out.println(test.getIndentNum().send());

    }

    @Test
    public void getTest() throws Exception{
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/ http://10.169.42.25:9090/
        Credentials credentials = WalletUtils.loadCredentials("123456", "E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-22T09-07-59.250376800Z--618a0456af5142c9c97797825d2a5f5b2a8a67ae");
        String contractAddress="0xDB90D99FBCA5e81a36307131D87aC48753A61C0e";
        Indent_sol_Indent contract = Indent_sol_Indent.load(contractAddress, web3j, credentials, GAS_PRICE, GAS);
        if (contract.isValid()){
            String wId = contract.getwId().send();
            String owner=contract.getOwner().send();
            String getIndentNum=contract.getIndentNum().send();
            System.out.println("wId"+wId+"owner"+owner+"getIndentNum"+getIndentNum);
        }
        else {
            System.out.println("not valid");
        }
    }


    @Test
    public void testWeb3j() throws Exception {
        Web3j web3 = Web3j.build(new HttpService("http://localhost:5201314/"));

        try {
            Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println(clientVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 创建钱包地址
        String filePath = "E:/IDEAworkspace/private-geth";
        String fileName = "";
        String path="E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-22T09-07-59.250376800Z--618a0456af5142c9c97797825d2a5f5b2a8a67ae";
        Credentials ALICE = WalletUtils.loadCredentials("123456", path);

        //eth-密码需要自己管理，自己设置哦！
        fileName = WalletUtils.generateNewWalletFile("123456", new File(filePath), false);
        System.out.println(fileName);//保存你的加密文件信息
        System.out.println(ALICE.getAddress());//钱包地址
        System.out.println(ALICE.getEcKeyPair().getPrivateKey());//私钥
        System.out.println(ALICE.getEcKeyPair().getPublicKey());//公钥

        BigInteger nonce = getNonce("E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-22T09-07-59.250376800Z--618a0456af5142c9c97797825d2a5f5b2a8a67ae");
        System.out.println("nonce"+nonce);
    }

    private static BigInteger getNonce(String address) throws Exception {
        Web3j web3 = Web3j.build(new HttpService("http://localhost:5201314/"));
        EthGetTransactionCount ethGetTransactionCount =
                web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }


    }

