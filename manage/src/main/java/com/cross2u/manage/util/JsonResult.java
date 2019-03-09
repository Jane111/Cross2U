package com.cross2u.manage.util;

import com.jfinal.kit.JsonKit;
import org.springframework.stereotype.Component;

@Component
public class JsonResult
{
	private Object data;
	private String resultCode;
	private String resultDesc;

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
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
	// 返回json
	public String toString()
	{
		return JsonKit.toJson(this);
	}

}
