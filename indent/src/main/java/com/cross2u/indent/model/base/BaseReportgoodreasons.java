package com.cross2u.indent.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportgoodreasons<M extends BaseReportgoodreasons<M>> extends Model<M> implements IBean {

	public void setRgrId(java.math.BigInteger rgrId) {
		set("rgrId", rgrId);
	}

	public java.math.BigInteger getRgrId() {
		return get("rgrId");
	}

	public void setRgrContent(String rgrContent) {
		set("rgrContent", rgrContent);
	}

	public String getRgrContent() {
		return get("rgrContent");
	}

	public void setRgrMethod(Integer rgrMethod) {
		set("rgrMethod", rgrMethod);
	}

	public Integer getRgrMethod() {
		return get("rgrMethod");
	}

	public void setRgrPunish(Integer rgrPunish) {
		set("rgrPunish", rgrPunish);
	}

	public Integer getRgrPunish() {
		return get("rgrPunish");
	}

	public void setRgrCreateTime(java.util.Date rgrCreateTime) {
		set("rgrCreateTime", rgrCreateTime);
	}

	public java.util.Date getRgrCreateTime() {
		return get("rgrCreateTime");
	}

	public void setRgrModifyTime(java.util.Date rgrModifyTime) {
		set("rgrModifyTime", rgrModifyTime);
	}

	public java.util.Date getRgrModifyTime() {
		return get("rgrModifyTime");
	}

}
