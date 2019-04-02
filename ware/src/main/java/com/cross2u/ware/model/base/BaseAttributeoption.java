package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAttributeoption<M extends BaseAttributeoption<M>> extends Model<M> implements IBean {

	public void setAoId(java.math.BigInteger aoId) {
		set("aoId", aoId);
	}

	public java.math.BigInteger getAoId() {
		return get("aoId");
	}

	public void setAoAttribute(java.math.BigInteger aoAttribute) {
		set("aoAttribute", aoAttribute);
	}

	public java.math.BigInteger getAoAttribute() {
		return get("aoAttribute");
	}

	public void setAoName(String aoName) {
		set("aoName", aoName);
	}

	public String getAoName() {
		return get("aoName");
	}

	public void setAoCreateTime(java.util.Date aoCreateTime) {
		set("aoCreateTime", aoCreateTime);
	}

	public java.util.Date getAoCreateTime() {
		return get("aoCreateTime");
	}

	public void setAoModifyTime(java.util.Date aoModifyTime) {
		set("aoModifyTime", aoModifyTime);
	}

	public java.util.Date getAoModifyTime() {
		return get("aoModifyTime");
	}

}
