package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseWareattribute<M extends BaseWareattribute<M>> extends Model<M> implements IBean {

	public void setWaId(java.math.BigInteger waId) {
		set("waId", waId);
	}

	public java.math.BigInteger getWaId() {
		return get("waId");
	}

	public void setWaWare(java.math.BigInteger waWare) {
		set("waWare", waWare);
	}

	public java.math.BigInteger getWaWare() {
		return get("waWare");
	}

	public void setWaAttribute(java.math.BigInteger waAttribute) {
		set("waAttribute", waAttribute);
	}

	public java.math.BigInteger getWaAttribute() {
		return get("waAttribute");
	}

	public void setWaAttributeOption(java.math.BigInteger waAttributeOption) {
		set("waAttributeOption", waAttributeOption);
	}

	public java.math.BigInteger getWaAttributeOption() {
		return get("waAttributeOption");
	}

	public void setWaCreateTime(java.util.Date waCreateTime) {
		set("waCreateTime", waCreateTime);
	}

	public java.util.Date getWaCreateTime() {
		return get("waCreateTime");
	}

	public void setWaModifyTime(java.util.Date waModifyTime) {
		set("waModifyTime", waModifyTime);
	}

	public java.util.Date getWaModifyTime() {
		return get("waModifyTime");
	}

}
