package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAnswer<M extends BaseAnswer<M>> extends Model<M> implements IBean {

	public void setAnId(java.math.BigInteger anId) {
		set("anId", anId);
	}

	public java.math.BigInteger getAnId() {
		return get("anId");
	}

	public void setAnQuest(Long anQuest) {
		set("anQuest", anQuest);
	}

	public Long getAnQuest() {
		return get("anQuest");
	}

	public void setAnBusiness(Long anBusiness) {
		set("anBusiness", anBusiness);
	}

	public Long getAnBusiness() {
		return get("anBusiness");
	}

	public void setAnIsM(Integer anIsM) {
		set("anIsM", anIsM);
	}

	public Integer getAnIsM() {
		return get("anIsM");
	}

	public void setAnContent(String anContent) {
		set("anContent", anContent);
	}

	public String getAnContent() {
		return get("anContent");
	}

	public void setAnCreateTime(java.util.Date anCreateTime) {
		set("anCreateTime", anCreateTime);
	}

	public java.util.Date getAnCreateTime() {
		return get("anCreateTime");
	}

	public void setAnModifyTime(java.util.Date anModifyTime) {
		set("anModifyTime", anModifyTime);
	}

	public java.util.Date getAnModifyTime() {
		return get("anModifyTime");
	}

}