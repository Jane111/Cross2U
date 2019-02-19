package com.cross2u.indent.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFormat<M extends BaseFormat<M>> extends Model<M> implements IBean {

	public void setFId(java.math.BigInteger fId) {
		set("fId", fId);
	}

	public java.math.BigInteger getFId() {
		return get("fId");
	}

	public void setFChName(String fChName) {
		set("fChName", fChName);
	}

	public String getFChName() {
		return get("fChName");
	}

	public void setFEnName(String fEnName) {
		set("fEnName", fEnName);
	}

	public String getFEnName() {
		return get("fEnName");
	}

	public void setFCreateTime(java.util.Date fCreateTime) {
		set("fCreateTime", fCreateTime);
	}

	public java.util.Date getFCreateTime() {
		return get("fCreateTime");
	}

	public void setFModifyTime(java.util.Date fModifyTime) {
		set("fModifyTime", fModifyTime);
	}

	public java.util.Date getFModifyTime() {
		return get("fModifyTime");
	}

}
