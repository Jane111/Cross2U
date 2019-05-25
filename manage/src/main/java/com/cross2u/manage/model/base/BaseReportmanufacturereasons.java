package com.cross2u.manage.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.math.BigInteger;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportmanufacturereasons<M extends BaseReportmanufacturereasons<M>> extends Model<M> implements IBean {

	public void setRmrId(BigInteger rmrId) {
		set("rmrId", rmrId);
	}

	public BigInteger getRmrId() {
		return get("rmrId");
	}

	public void setRmrContent(String rmrContent) {
		set("rmrContent", rmrContent);
	}

	public String getRmrContent() {
		return get("rmrContent");
	}

	public void setRmrMethod(Integer rmrMethod) {
		set("rmrMethod", rmrMethod);
	}

	public Integer getRmrMethod() {
		return get("rmrMethod");
	}

	public void setRmrPunish(Integer rmrPunish) {
		set("rmrPunish", rmrPunish);
	}

	public Integer getRmrPunish() {
		return get("rmrPunish");
	}

	public void setRmrCreateTime(java.util.Date rmrCreateTime) {
		set("rmrCreateTime", rmrCreateTime);
	}

	public java.util.Date getRmrCreateTime() {
		return get("rmrCreateTime");
	}

	public void setRmrModifyTime(java.util.Date rmrModifyTime) {
		set("rmrModifyTime", rmrModifyTime);
	}

	public java.util.Date getRmrModifyTime() {
		return get("rmrModifyTime");
	}

}
