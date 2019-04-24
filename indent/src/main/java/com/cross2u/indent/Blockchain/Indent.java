package com.cross2u.indent.Blockchain;

/**
 * 区块链
 */

import java.math.BigInteger;
import java.util.Arrays;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class Indent extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60405161052638038061052683398101604052808051820191906020018051820191906020018051820191906020018051909101905060008480516100589291602001906100ba565b50600183805161006c9291602001906100ba565b5060028280516100809291602001906100ba565b5060038180516100949291602001906100ba565b505060048054600160a060020a03191633600160a060020a031617905550610155915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100fb57805160ff1916838001178555610128565b82800160010185558215610128579182015b8281111561012857825182559160200191906001019061010d565b50610134929150610138565b5090565b61015291905b80821115610134576000815560010161013e565b90565b6103c2806101646000396000f300606060405263ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416635326a4bb8114610068578063557ed1ba146100f2578063893d20e8146101055780639298c94514610141578063d73beb581461015457600080fd5b341561007357600080fd5b61007b610167565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156100b757808201518382015260200161009f565b50505050905090810190601f1680156100e45780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156100fd57600080fd5b61007b61020f565b341561011057600080fd5b610118610282565b60405173ffffffffffffffffffffffffffffffffffffffff909116815260200160405180910390f35b341561014c57600080fd5b61007b61029e565b341561015f57600080fd5b61007b610311565b61016f610384565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102055780601f106101da57610100808354040283529160200191610205565b820191906000526020600020905b8154815290600101906020018083116101e857829003601f168201915b5050505050905090565b610217610384565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102055780601f106101da57610100808354040283529160200191610205565b60045473ffffffffffffffffffffffffffffffffffffffff1690565b6102a6610384565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102055780601f106101da57610100808354040283529160200191610205565b610319610384565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102055780601f106101da57610100808354040283529160200191610205565b602060405190810160405260008152905600a165627a7a723058202cff3b7b67a3338383b0f3c28571cd1735b226134d02767bd04fe78c62d9ae960029";

    public static final String FUNC_GETMID = "getmId";

    public static final String FUNC_GETTIME = "getTime";

    public static final String FUNC_GETOWNER = "getOwner";

    public static final String FUNC_GETWID = "getwId";

    public static final String FUNC_GETINDENTNUM = "getIndentNum";

    protected Indent(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Indent(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> getmId() {
        final Function function = new Function(FUNC_GETMID,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getTime() {
        final Function function = new Function(FUNC_GETTIME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getOwner() {
        final Function function = new Function(FUNC_GETOWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getwId() {
        final Function function = new Function(FUNC_GETWID,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getIndentNum() {
        final Function function = new Function(FUNC_GETINDENTNUM,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<Indent> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _mId, String _wId, String _indentNum, String _time) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_mId),
                new org.web3j.abi.datatypes.Utf8String(_wId),
                new org.web3j.abi.datatypes.Utf8String(_indentNum),
                new org.web3j.abi.datatypes.Utf8String(_time)));
        return deployRemoteCall(Indent.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Indent> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _mId, String _wId, String _indentNum, String _time) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_mId),
                new org.web3j.abi.datatypes.Utf8String(_wId),
                new org.web3j.abi.datatypes.Utf8String(_indentNum),
                new org.web3j.abi.datatypes.Utf8String(_time)));
        return deployRemoteCall(Indent.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static Indent load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Indent(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Indent load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Indent(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
