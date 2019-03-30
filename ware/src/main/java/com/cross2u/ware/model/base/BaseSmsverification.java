package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSmsverification<M extends BaseSmsverification<M>> extends Model<M> implements IBean {

	public void setSmsId(Long smsId) {
		set("smsId", smsId);
	}

	public Long getSmsId() {
		return get("smsId");
	}

	public void setSmsMobile(String smsMobile) {
		set("smsMobile", smsMobile);
	}

	public String getSmsMobile() {
		return get("smsMobile");
	}

	public void setSmsCode(String smsCode) {
		set("smsCode", smsCode);
	}

	public String getSmsCode() {
		return get("smsCode");
	}

	public void setSmsCreateTime(java.util.Date smsCreateTime) {
		set("smsCreateTime", smsCreateTime);
	}

	public java.util.Date getSmsCreateTime() {
		return get("smsCreateTime");
	}

	public void setSmsModifyTime(java.util.Date smsModifyTime) {
		set("smsModifyTime", smsModifyTime);
	}

	public java.util.Date getSmsModifyTime() {
		return get("smsModifyTime");
	}

}
