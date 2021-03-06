package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCategory<M extends BaseCategory<M>> extends Model<M> implements IBean {

	public void setCtId(java.math.BigInteger ctId) {
		set("ctId", ctId);
	}

	public java.math.BigInteger getCtId() {
		return get("ctId");
	}

	public void setCtParentId(Long ctParentId) {
		set("ctParentId", ctParentId);
	}

	public Long getCtParentId() {
		return get("ctParentId");
	}

	public void setCtName(String ctName) {
		set("ctName", ctName);
	}

	public String getCtName() {
		return get("ctName");
	}

    public void setCtImg(String ctImg) {
        set("ctImg", ctImg);
    }

    public String getCtImg() {
        return get("ctImg");
    }

	public void setCtCreateTime(java.util.Date ctCreateTime) {
		set("ctCreateTime", ctCreateTime);
	}

	public java.util.Date getCtCreateTime() {
		return get("ctCreateTime");
	}

	public void setCtModifyTime(java.util.Date ctModifyTime) {
		set("ctModifyTime", ctModifyTime);
	}

	public java.util.Date getCtModifyTime() {
		return get("ctModifyTime");
	}

}
