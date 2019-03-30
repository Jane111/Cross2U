package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAdministrator<M extends BaseAdministrator<M>> extends Model<M> implements IBean {

	public void setAId(java.math.BigInteger aId) {
		set("aId", aId);
	}

	public java.math.BigInteger getAId() {
		return get("aId");
	}

	public void setAAccount(String aAccount) {
		set("aAccount", aAccount);
	}

	public String getAAccount() {
		return get("aAccount");
	}

	public void setAPostion(Integer aPostion) {
		set("aPostion", aPostion);
	}

	public Integer getAPostion() {
		return get("aPostion");
	}

	public void setAPassword(String aPassword) {
		set("aPassword", aPassword);
	}

	public String getAPassword() {
		return get("aPassword");
	}

	public void setAStatus(Integer aStatus) {
		set("aStatus", aStatus);
	}

	public Integer getAStatus() {
		return get("aStatus");
	}

	public void setACreateTime(java.util.Date aCreateTime) {
		set("aCreateTime", aCreateTime);
	}

	public java.util.Date getACreateTime() {
		return get("aCreateTime");
	}

	public void setAModifyTime(java.util.Date aModifyTime) {
		set("aModifyTime", aModifyTime);
	}

	public java.util.Date getAModifyTime() {
		return get("aModifyTime");
	}

}
