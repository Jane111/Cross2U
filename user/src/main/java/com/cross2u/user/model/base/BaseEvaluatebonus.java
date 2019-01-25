package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseEvaluatebonus<M extends BaseEvaluatebonus<M>> extends Model<M> implements IBean {

	public void setEbId(java.math.BigInteger ebId) {
		set("ebId", ebId);
	}

	public java.math.BigInteger getEbId() {
		return get("ebId");
	}

	public void setEbRank(Integer ebRank) {
		set("ebRank", ebRank);
	}

	public Integer getEbRank() {
		return get("ebRank");
	}

	public void setEbBouns(Integer ebBouns) {
		set("ebBouns", ebBouns);
	}

	public Integer getEbBouns() {
		return get("ebBouns");
	}

	public void setEbCreateTime(java.util.Date ebCreateTime) {
		set("ebCreateTime", ebCreateTime);
	}

	public java.util.Date getEbCreateTime() {
		return get("ebCreateTime");
	}

	public void setEbModifyTime(java.util.Date ebModifyTime) {
		set("ebModifyTime", ebModifyTime);
	}

	public java.util.Date getEbModifyTime() {
		return get("ebModifyTime");
	}

}
