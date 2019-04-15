package com.cross2u.store.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStorebill<M extends BaseStorebill<M>> extends Model<M> implements IBean {

	public void setSbId(java.math.BigInteger sbId) {
		set("sbId", sbId);
	}

	public java.math.BigInteger getSbId() {
		return get("sbId");
	}

	public void setSbSId(java.math.BigInteger sbSId) {
		set("sbSId", sbSId);
	}

	public java.math.BigInteger getSbSId() {
		return get("sbSId");
	}

	public void setSbInfo(Integer sbInfo) {
		set("sbInfo", sbInfo);
	}

	public Integer getSbInfo() {
		return get("sbInfo");
	}

	public void setSbMoney(Integer sbMoney) {
		set("sbMoney", sbMoney);
	}

	public Integer getSbMoney() {
		return get("sbMoney");
	}

	public void setSbBalance(Float sbBalance) {
		set("sbBalance", sbBalance);
	}

	public Float getSbBalance() {
		return get("sbBalance");
	}

	public void setSbNumber(String sbNumber) {
		set("sbNumber", sbNumber);
	}

	public String getSbNumber() {
		return get("sbNumber");
	}

	public void setSbTime(java.util.Date sbTime) {
		set("sbTime", sbTime);
	}

	public java.util.Date getSbTime() {
		return get("sbTime");
	}

	public void setSbCreateTime(java.util.Date sbCreateTime) {
		set("sbCreateTime", sbCreateTime);
	}

	public java.util.Date getSbCreateTime() {
		return get("sbCreateTime");
	}

	public void setSbModifyTime(java.util.Date sbModifyTime) {
		set("sbModifyTime", sbModifyTime);
	}

	public java.util.Date getSbModifyTime() {
		return get("sbModifyTime");
	}

}