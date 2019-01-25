package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAmchart<M extends BaseAmchart<M>> extends Model<M> implements IBean {

	public void setAmchId(Long amchId) {
		set("amchId", amchId);
	}

	public Long getAmchId() {
		return get("amchId");
	}

	public void setAmchManu(Long amchManu) {
		set("amchManu", amchManu);
	}

	public Long getAmchManu() {
		return get("amchManu");
	}

	public void setAmchAdministrator(Long amchAdministrator) {
		set("amchAdministrator", amchAdministrator);
	}

	public Long getAmchAdministrator() {
		return get("amchAdministrator");
	}

	public void setAmchSpeaker(Integer amchSpeaker) {
		set("amchSpeaker", amchSpeaker);
	}

	public Integer getAmchSpeaker() {
		return get("amchSpeaker");
	}

	public void setAmchContent(String amchContent) {
		set("amchContent", amchContent);
	}

	public String getAmchContent() {
		return get("amchContent");
	}

	public void setAmchCreateTime(java.util.Date amchCreateTime) {
		set("amchCreateTime", amchCreateTime);
	}

	public java.util.Date getAmchCreateTime() {
		return get("amchCreateTime");
	}

	public void setAmchModifyTime(java.util.Date amchModifyTime) {
		set("amchModifyTime", amchModifyTime);
	}

	public java.util.Date getAmchModifyTime() {
		return get("amchModifyTime");
	}

}
