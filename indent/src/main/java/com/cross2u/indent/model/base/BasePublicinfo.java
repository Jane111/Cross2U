package com.cross2u.indent.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePublicinfo<M extends BasePublicinfo<M>> extends Model<M> implements IBean {

	public void setPiId(java.math.BigInteger piId) {
		set("piId", piId);
	}

	public java.math.BigInteger getPiId() {
		return get("piId");
	}

	public void setPiTitle(String piTitle) {
		set("piTitle", piTitle);
	}

	public String getPiTitle() {
		return get("piTitle");
	}

	public void setPiContent(String piContent) {
		set("piContent", piContent);
	}

	public String getPiContent() {
		return get("piContent");
	}

	public void setPiCreateTime(java.util.Date piCreateTime) {
		set("piCreateTime", piCreateTime);
	}

	public java.util.Date getPiCreateTime() {
		return get("piCreateTime");
	}

	public void setPiModifyTime(java.util.Date piModifyTime) {
		set("piModifyTime", piModifyTime);
	}

	public java.util.Date getPiModifyTime() {
		return get("piModifyTime");
	}

}
