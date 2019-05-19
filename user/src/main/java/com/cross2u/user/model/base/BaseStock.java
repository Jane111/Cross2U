package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

import java.math.BigInteger;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStock<M extends BaseStock<M>> extends Model<M> implements IBean {

	public void setSId(BigInteger sId) {
		set("sId", sId);
	}

	public BigInteger getSId() {
		return get("sId");
	}

	public void setSBid(BigInteger sBid) {
		set("sBid", sBid);
	}

	public BigInteger getSBid() {
		return get("sBid");
	}

	public void setSPId(BigInteger sPId) {
		set("sPId", sPId);
	}

	public BigInteger getSPId() {
		return get("sPId");
	}

	public void setSNumber(Integer sNumber) {
		set("sNumber", sNumber);
	}

	public Integer getSNumber() {
		return get("sNumber");
	}

	public void setSSId(BigInteger sSId) {
		set("sSId", sSId);
	}

	public BigInteger getSSId() {
		return get("sSId");
	}

	public void setSSum(Float sSum) {
		set("sSum", sSum);
	}

	public Float getSSum() {
		return get("sSum");
	}

	public void setSSumUnit(Integer sSumUnit) {
		set("sSumUnit", sSumUnit);
	}

	public Integer getSSumUnit() {
		return get("sSumUnit");
	}

	public void setSCreateTime(java.util.Date sCreateTime) {
		set("sCreateTime", sCreateTime);
	}

	public java.util.Date getSCreateTime() {
		return get("sCreateTime");
	}

	public void setSModifyTime(java.util.Date sModifyTime) {
		set("sModifyTime", sModifyTime);
	}

	public java.util.Date getSModifyTime() {
		return get("sModifyTime");
	}

}
