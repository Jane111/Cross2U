package com.cross2u.indent.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportevaluatereasons<M extends BaseReportevaluatereasons<M>> extends Model<M> implements IBean {

	public void setRerId(java.math.BigInteger rerId) {
		set("rerId", rerId);
	}

	public java.math.BigInteger getRerId() {
		return get("rerId");
	}

	public void setRerContent(String rerContent) {
		set("rerContent", rerContent);
	}

	public String getRerContent() {
		return get("rerContent");
	}

	public void setRerPunish(String rerPunish) {
		set("rerPunish", rerPunish);
	}

	public String getRerPunish() {
		return get("rerPunish");
	}

	public void setRerCreateTime(java.util.Date rerCreateTime) {
		set("rerCreateTime", rerCreateTime);
	}

	public java.util.Date getRerCreateTime() {
		return get("rerCreateTime");
	}

	public void setRerModifyTime(java.util.Date rerModifyTime) {
		set("rerModifyTime", rerModifyTime);
	}

	public java.util.Date getRerModifyTime() {
		return get("rerModifyTime");
	}

}
