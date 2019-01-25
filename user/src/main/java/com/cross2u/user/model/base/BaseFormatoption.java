package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFormatoption<M extends BaseFormatoption<M>> extends Model<M> implements IBean {

	public void setFoId(java.math.BigInteger foId) {
		set("foId", foId);
	}

	public java.math.BigInteger getFoId() {
		return get("foId");
	}

	public void setFoFormat(java.math.BigInteger foFormat) {
		set("foFormat", foFormat);
	}

	public java.math.BigInteger getFoFormat() {
		return get("foFormat");
	}

	public void setFoParentOption(java.math.BigInteger foParentOption) {
		set("foParentOption", foParentOption);
	}

	public java.math.BigInteger getFoParentOption() {
		return get("foParentOption");
	}

	public void setFoChName(String foChName) {
		set("foChName", foChName);
	}

	public String getFoChName() {
		return get("foChName");
	}

	public void setFoEnName(String foEnName) {
		set("foEnName", foEnName);
	}

	public String getFoEnName() {
		return get("foEnName");
	}

	public void setFoCreateTime(java.util.Date foCreateTime) {
		set("foCreateTime", foCreateTime);
	}

	public java.util.Date getFoCreateTime() {
		return get("foCreateTime");
	}

	public void setFoModifyTime(java.util.Date foModifyTime) {
		set("foModifyTime", foModifyTime);
	}

	public java.util.Date getFoModifyTime() {
		return get("foModifyTime");
	}

}
