package com.cross2u.user.util;

/**
 * 存储图片到云上
 */

public class Scope {

	private String action;
	private String bucket; 
	private String region;
	private String sourcePrefix;

	public Scope(String action, String bucket, String region, String sourcePrefix) {
		this.action = action;
		this.bucket = bucket;
		this.region = region;
		this.sourcePrefix = sourcePrefix;
	}
	
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setResourcePrefix(String sourcePrefix) {
		this.sourcePrefix = sourcePrefix;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public String getResource() {
		int index = bucket.lastIndexOf('-');
		String appid = bucket.substring(index + 1).trim();
		String bucketName = bucket.substring(0, index).trim();
		if(!sourcePrefix.startsWith("/")) {
			sourcePrefix = '/' + sourcePrefix;
		}
		StringBuilder resource = new StringBuilder();
		resource.append("qcs::cos")
		.append(':')
		.append(region)
		.append(':')
		.append("uid/").append(appid)
		.append(':')
		.append("prefix//").append(appid).append('/').append(bucketName)
		.append(sourcePrefix);
		return resource.toString();
	}
	
}
