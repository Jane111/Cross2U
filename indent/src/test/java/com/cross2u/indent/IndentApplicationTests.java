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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.math.BigInteger;

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
		Web3j web3j = Web3j.build(new HttpService("http://localhost:30303/"));  // defaults to http://localhost:8545/ http://10.169.42.25:9090/
        Credentials credentials = WalletUtils.loadCredentials("123456", "E:/IDEAworkspace/private-geth/db/keystore/UTC--2019-03-20T02-11-43.416754100Z--8c4f06f1e155ea11ffa639fa848cc4f9ec71f16a");
        //部署智能合约
        Indent_sol_Indent contract = Indent_sol_Indent.deploy(
                web3j,credentials, GAS_PRICE, GAS,"11","22","33","2019-3-19").send();  // constructor params

        Indent_sol_Indent test=Indent_sol_Indent.load(contract.getContractAddress(),web3j,credentials, GAS_PRICE, GAS);

        //Transaction transaction=contract.

        System.out.println("address"+contract.getContractAddress()+"hash:"+contract.hashCode());
        System.out.println(contract.toString());
        //调用智能合约
        System.out.println(contract.getIndentNum().send());
        System.out.println(test.toString());
        //System.out.println(test.getIndentNum().send());

    }

}

