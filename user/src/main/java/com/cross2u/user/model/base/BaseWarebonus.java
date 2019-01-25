package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseWarebonus<M extends BaseWarebonus<M>> extends Model<M> implements IBean {

	public void setWbId(Integer wbId) {
		set("wbId", wbId);
	}

	public Integer getWbId() {
		return get("wbId");
	}

	public void setWbRank(Integer wbRank) {
		set("wbRank", wbRank);
	}

	public Integer getWbRank() {
		return get("wbRank");
	}

	public void setWbBonus(Integer wbBonus) {
		set("wbBonus", wbBonus);
	}

	public Integer getWbBonus() {
		return get("wbBonus");
	}

	public void setWbCreateTime(java.util.Date wbCreateTime) {
		set("wbCreateTime", wbCreateTime);
	}

	public java.util.Date getWbCreateTime() {
		return get("wbCreateTime");
	}

	public void setWbModifyTime(java.util.Date wbModifyTime) {
		set("wbModifyTime", wbModifyTime);
	}

	public java.util.Date getWbModifyTime() {
		return get("wbModifyTime");
	}

}
