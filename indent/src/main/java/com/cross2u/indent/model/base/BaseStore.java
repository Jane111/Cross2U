package com.cross2u.indent.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStore<M extends BaseStore<M>> extends Model<M> implements IBean {

	public void setSId(java.math.BigInteger sId) {
		set("sId", sId);
	}

	public java.math.BigInteger getSId() {
		return get("sId");
	}

	public void setSMmId(java.math.BigInteger sMmId) {
		set("sMmId", sMmId);
	}

	public java.math.BigInteger getSMmId() {
		return get("sMmId");
	}

	public void setSStatus(Integer sStatus) {
		set("sStatus", sStatus);
	}

	public Integer getSStatus() {
		return get("sStatus");
	}

	public void setSName(String sName) {
		set("sName", sName);
	}

	public String getSName() {
		return get("sName");
	}

	public void setSPhoto(String sPhoto) {
		set("sPhoto", sPhoto);
	}

	public String getSPhoto() {
		return get("sPhoto");
	}

	public void setSDescribe(String sDescribe) {
		set("sDescribe", sDescribe);
	}

	public String getSDescribe() {
		return get("sDescribe");
	}

	public void setSScore(java.math.BigInteger sScore) {
		set("sScore", sScore);
	}

	public java.math.BigInteger getSScore() {
		return get("sScore");
	}

	public void setSAgentDegree(Integer sAgentDegree) {
		set("sAgentDegree", sAgentDegree);
	}

	public Integer getSAgentDegree() {
		return get("sAgentDegree");
	}

	public void setSDirectMoney(Integer sDirectMoney) {
		set("sDirectMoney", sDirectMoney);
	}

	public Integer getSDirectMoney() {
		return get("sDirectMoney");
	}

	public void setSReduceInventory(Integer sReduceInventory) {
		set("sReduceInventory", sReduceInventory);
	}

	public Integer getSReduceInventory() {
		return get("sReduceInventory");
	}

	public void setMmRegisterMoney(Float mmRegisterMoney) {
		set("mmRegisterMoney", mmRegisterMoney);
	}

	public Float getMmRegisterMoney() {
		return get("mmRegisterMoney");
	}

	public void setMmRegisterMoneyUnit(Integer mmRegisterMoneyUnit) {
		set("mmRegisterMoneyUnit", mmRegisterMoneyUnit);
	}

	public Integer getMmRegisterMoneyUnit() {
		return get("mmRegisterMoneyUnit");
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
