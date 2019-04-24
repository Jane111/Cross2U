package com.cross2u.indent;

import com.cross2u.indent.Service.IndentServiceL;
import com.cross2u.indent.Service.IndentServiceZ;
import com.cross2u.indent.Blockchain.Indent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.jfinal.plugin.activerecord.Db.query;

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
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials("123456", "E:\\IDEAworkspace\\private-geth\\keystore\\UTC--2019-03-23T01-43-06.760528400Z--6edf076703ec6dd2dd1e14b416b93ebe1320ee4e");

        String address = "0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e";

        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();

        if(ethGetBalance!=null){
            // 打印账户余额
            System.out.println(ethGetBalance.getBalance());
            // 将单位转为以太，方便查看
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));
        }

        //部署智能合约
        Indent contract = Indent.deploy(web3j,credentials, GAS_PRICE, GAS,"11","22","33","2019-3-22").send();  // constructor params
        String contractAddress=contract.getContractAddress();

        //Indent_sol_Indent test=Indent_sol_Indent.load(contract.getContractAddress(),web3j,credentials, GAS_PRICE, GAS);

        //Transaction transaction=contract.

        System.out.println("deploy addr"+contract.getTransactionReceipt()+"\n address"+contract.getContractAddress()+"\n hash:"+contract.hashCode()+
                "\n contract.getIndentNum()"+contract.getIndentNum()+"\nvalid"+contract.isValid());
        System.out.println(contract.toString());
        //调用智能合约
        Transaction transaction = Transaction.createEthCallTransaction(
                "0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e","0xbc2a97fe18cd01a713eef360c008a895f118823f",
               "刘静是猪");
        System.out.println("tranc: "+transaction.getData());

    }

    @Test
    public void getTest() throws Exception{
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/ http://10.169.42.25:9090/
        Credentials credentials = WalletUtils.loadCredentials("123456", "E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-22T09-07-59.250376800Z--618a0456af5142c9c97797825d2a5f5b2a8a67ae");
        String contractAddress="0xDB90D99FBCA5e81a36307131D87aC48753A61C0e";
        Indent contract = Indent.load(contractAddress, web3j, credentials, GAS_PRICE, GAS);
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
    public void  transfer() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/ http://10.169.42.25:9090/
        Credentials credentials = WalletUtils.loadCredentials("123456", new File("E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-23T01-43-06.760528400Z--6edf076703ec6dd2dd1e14b416b93ebe1320ee4e"));//转出地址密码 钱包地址
        String fromAddress = credentials.getAddress();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        Address address = new Address("0xbc2a97fe18cd01a713eef360c008a895f118823f");
        Uint256 value = new Uint256(11);
        List<Type> parametersList = new ArrayList<>();
        parametersList.add(address);
        parametersList.add(value);
        List<TypeReference<?>> outList = new ArrayList<>();
        Function function = new Function("transfer", parametersList, outList);
        String encodedFunction = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger("10"), new BigInteger("22000"), "0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e", encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

    }

    @Test
    public void tzx(){
        String nodeUrl="http://192.168.191.1:8454";	//节点地址
        String wallet="E:/IDEAworkspace/private-geth/keystore/UTC--2019-03-23T01-43-06.760528400Z--6edf076703ec6dd2dd1e14b416b93ebe1320ee4e";		//钱包地址
        String password="123456";	//钱包密码
        String address="0x6edf076703ec6dd2dd1e14b416b93ebe1320ee4e";		//机构账户地址
        String status="01";
        System.out.println(getIdentityInfo(nodeUrl, wallet, password, address, status));

    }

    public List<List<Type>> getIdentityInfo(String nodeUrl,String wallet,String password,String address,String status)
    {
        /*List<List<Type>> result=new ArrayList<>();
        try
        {
            //平台区块链地址
            Address plateformAddr=new Address(address);
            //开始用区块链记录下来
            Web3j web3j = Web3j.build(new HttpService(nodeUrl));
            //加载该平台的钱包
            Credentials credentials = WalletUtils.loadCredentials(password,Constant.WALLET_ADDRESS+wallet);
            //智能合约地址
            String contractAddress=Constant.SMART_CONTRACT_ADDRESS;
            Blockchain blockchain=Blockchain.load(contractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
            //对应记录的条数
            int size=0;
            if(status.equals("01"))
            {
                blockchain.addIdentityProcessSize(plateformAddr);
                size=blockchain.size().get().getValue().intValue();
                for(Integer i=0;i<size;i++)
                {
                    result.add(blockchain.addIdentityProcess(plateformAddr, new Uint256(new BigInteger(i.toString()))).get());
                }
            }
            else if(status.equals("02"))
            {
                blockchain.modifyIdentityProcessSize(plateformAddr);
                size=blockchain.size().get().getValue().intValue();
                for(Integer i=0;i<size;i++)
                {
                    result.add(blockchain.modifyIdentityProcess(plateformAddr, new Uint256(new BigInteger(i.toString()))).get());
                }
            }
            else if(status.equals("03"))
            {
                blockchain.revokeIdentityProcessSize(plateformAddr);
                size=blockchain.size().get().getValue().intValue();
                for(Integer i=0;i<size;i++)
                {
                    result.add(blockchain.revokeIdentityProcess(plateformAddr, new Uint256(new BigInteger(i.toString()))).get());
                }
            }
            else if(status.equals("04"))
            {
                blockchain.lookIdentityProcessSize(plateformAddr);
                size=blockchain.size().get().getValue().intValue();
                for(Integer i=0;i<size;i++)
                {
                    result.add(blockchain.lookIdentityProcess(plateformAddr, new Uint256(new BigInteger(i.toString()))).get());
                }
            }
            else
            {
                return null;
            }
            System.out.println("size大小："+size);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;*/
        return null;
    }
    @Test
    public void indentTsest()
    {
    }



}

