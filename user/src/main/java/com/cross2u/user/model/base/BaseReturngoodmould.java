package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReturngoodmould<M extends BaseReturngoodmould<M>> extends Model<M> implements IBean {

	public void setRgmId(java.math.BigInteger rgmId) {
		set("rgmId", rgmId);
	}

	public java.math.BigInteger getRgmId() {
		return get("rgmId");
	}

	public void setRgSId(java.math.BigInteger rgSId) {
		set("rgSId", rgSId);
	}

	public java.math.BigInteger getRgSId() {
		return get("rgSId");
	}

	public void setRgmAddress(String rgmAddress) {
		set("rgmAddress", rgmAddress);
	}

	public String getRgmAddress() {
		return get("rgmAddress");
	}

	public void setRgmName(String rgmName) {
		set("rgmName", rgmName);
	}

	public String getRgmName() {
		return get("rgmName");
	}

	public void setRgmPhone(String rgmPhone) {
		set("rgmPhone", rgmPhone);
	}

	public String getRgmPhone() {
		return get("rgmPhone");
	}

	public void setRgmIsDeleted(Integer rgmIsDeleted) {
		set("rgmIsDeleted", rgmIsDeleted);
	}

	public Integer getRgmIsDeleted() {
		return get("rgmIsDeleted");
	}

	public void setRgmCreateTime(java.util.Date rgmCreateTime) {
		set("rgmCreateTime", rgmCreateTime);
	}

	public java.util.Date getRgmCreateTime() {
		return get("rgmCreateTime");
	}

	public void setRgmModifyTime(java.util.Date rgmModifyTime) {
		set("rgmModifyTime", rgmModifyTime);
	}

	public java.util.Date getRgmModifyTime() {
		return get("rgmModifyTime");
	}

}
