package com.cross2u.chat.util;


import com.jfinal.kit.JsonKit;
import org.springframework.stereotype.Component;

@Component
public class BaseResponse
{
	private Object data;
	private String resultCode;
	private String resultDesc;
	private Object data1;
	public Object getData()
	{
		return data;
	}
	public void setData(Object data)
	{
		this.data = data;

    }
	public Object getData1()
	{
		return data1;
	}
	public void setData1(Object data)
	{
		this.data1 = data;
	}
	public String getResultCode()
	{
		return resultCode;
	}
	public void setResultCode(String resultCode)
	{
		this.resultCode = resultCode;
	}
	public String getResultDesc()
	{
		return resultDesc;
	}
	public void setResultDesc(String resultDesc)
	{
		this.resultDesc = resultDesc;
	}
	public void setResult(ResultCodeEnum code)
	{
		this.resultCode = code.getCode();
		this.resultDesc = code.getDesc();
	}
	
	public String toString()
	{ //JsonKit 是Jfinal 封装的一个工具类里面的
		return JsonKit.toJson(this);
	}
	
}
