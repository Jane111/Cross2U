package com.cross2u.indent.util;


import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

import java.io.IOException;

public class IpfsFile {

    public static String add(String data) throws IOException {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(data.getBytes());
        MerkleNode hash = ipfs.add(file).get(0);
        return hash.hash.toString();
    }
    public static String get(String hash) throws IOException {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        MerkleNode md = new MerkleNode(hash);
        byte[] data = ipfs.cat(md.hash);
        return new String(data);
    }

//	public static void main(String []argv) {
//		try {
//			String hash = add("\"name\":\"zhj\"");
//			System.out.println("hash:"+hash);
//
//			String data = get(hash);
//			System.out.println("data:"+data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

