package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSensitive<M extends BaseSensitive<M>> extends Model<M> implements IBean {

	public void setSenId(Integer senId) {
		set("senId", senId);
	}

	public Integer getSenId() {
		return get("senId");
	}

	public void setSenText(String senText) {
		set("senText", senText);
	}

	public String getSenText() {
		return get("senText");
	}

	public void setSenReason(String senReason) {
		set("senReason", senReason);
	}

	public String getSenReason() {
		return get("senReason");
	}

	public void setSenAdministrator(Long senAdministrator) {
		set("senAdministrator", senAdministrator);
	}

	public Long getSenAdministrator() {
		return get("senAdministrator");
	}

	public void setSenCreateTime(java.util.Date senCreateTime) {
		set("senCreateTime", senCreateTime);
	}

	public java.util.Date getSenCreateTime() {
		return get("senCreateTime");
	}

	public void setSenModifyTime(java.util.Date senModifyTime) {
		set("senModifyTime", senModifyTime);
	}

	public java.util.Date getSenModifyTime() {
		return get("senModifyTime");
	}

}
