package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.math.BigInteger;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStock<M extends BaseStock<M>> extends Model<M> implements IBean {

	public void setSkId(BigInteger skId) {
		set("skId", skId);
	}

	public BigInteger getSkId() {
		return get("skId");
	}

	public void setSkBid(BigInteger skBid) {
		set("skBid", skBid);
	}

	public BigInteger getSkBid() {
		return get("skBid");
	}

	public void setSkPId(BigInteger skPId) {
		set("skPId", skPId);
	}

	public BigInteger getSkPId() {
		return get("skPId");
	}

	public void setSkNumber(Integer skNumber) {
		set("skNumber", skNumber);
	}

	public Integer getSkNumber() {
		return get("skNumber");
	}

	public void setSkSId(BigInteger skSId) {
		set("skSId", skSId);
	}

	public BigInteger getSkSId() {
		return get("skSId");
	}

	public void setSkSum(Float skSum) {
		set("skSum", skSum);
	}

	public Float getSkSum() {
		return get("skSum");
	}

	public void setSkSumUnit(Integer skSumUnit) {
		set("skSumUnit", skSumUnit);
	}

	public Integer getSkSumUnit() {
		return get("skSumUnit");
	}

	public void setSkCreateTime(java.util.Date skCreateTime) {
		set("skCreateTime", skCreateTime);
	}

	public java.util.Date getSkCreateTime() {
		return get("skCreateTime");
	}

	public void setSkModifyTime(java.util.Date skModifyTime) {
		set("skModifyTime", skModifyTime);
	}

	public java.util.Date getSkModifyTime() {
		return get("skModifyTime");
	}

}
