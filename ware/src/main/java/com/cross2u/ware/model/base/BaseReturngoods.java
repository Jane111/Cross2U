package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReturngoods<M extends BaseReturngoods<M>> extends Model<M> implements IBean {

	public void setRgId(java.math.BigInteger rgId) {
		set("rgId", rgId);
	}

	public java.math.BigInteger getRgId() {
		return get("rgId");
	}

	public void setRgBId(Long rgBId) {
		set("rgBId", rgBId);
	}

	public Long getRgBId() {
		return get("rgBId");
	}

	public void setRgMId(Long rgMId) {
		set("rgMId", rgMId);
	}

	public Long getRgMId() {
		return get("rgMId");
	}

	public void setRgOOId(Long rgOOId) {
		set("rgOOId", rgOOId);
	}

	public Long getRgOOId() {
		return get("rgOOId");
	}

	public void setRgState(Integer rgState) {
		set("rgState", rgState);
	}

	public Integer getRgState() {
		return get("rgState");
	}

	public void setRgRGMId(Long rgRGMId) {
		set("rgRGMId", rgRGMId);
	}

	public Long getRgRGMId() {
		return get("rgRGMId");
	}

	public void setRgiTrackNumber(String rgiTrackNumber) {
		set("rgiTrackNumber", rgiTrackNumber);
	}

	public String getRgiTrackNumber() {
		return get("rgiTrackNumber");
	}

	public void setrgTrackCompany(String rgTrackCompany) {
		set("rgTrackCompany",  rgTrackCompany);
	}

	public String getrgTrackCompany() {
		return get("rgTrackCompany");
	}

	public void setRgImg(String rgImg) {
		set("rgImg", rgImg);
	}

	public String getRgImg() {
		return get("rgImg");
	}

	public void setRgiTrakTime(java.util.Date rgiTrakTime) {
		set("rgiTrakTime", rgiTrakTime);
	}

	public java.util.Date getRgiTrakTime() {
		return get("rgiTrakTime");
	}

	public void setRgType(Long rgType) {
		set("rgType", rgType);
	}

	public Long getRgType() {
		return get("rgType");
	}

	public void setRgReasons(String rgReasons) {
		set("rgReasons", rgReasons);
	}

	public String getRgReasons() {
		return get("rgReasons");
	}

	public void setRgImg1(String rgImg1) {
		set("rgImg1", rgImg1);
	}

	public String getRgImg1() {
		return get("rgImg1");
	}

	public void setRgImg2(String rgImg2) {
		set("rgImg2", rgImg2);
	}

	public String getRgImg2() {
		return get("rgImg2");
	}

	public void setRgImg3(String rgImg3) {
		set("rgImg3", rgImg3);
	}

	public String getRgImg3() {
		return get("rgImg3");
	}

	public void setRgCreateTime(java.util.Date rgCreateTime) {
		set("rgCreateTime", rgCreateTime);
	}

	public java.util.Date getRgCreateTime() {
		return get("rgCreateTime");
	}

	public void setRgModifyTime(java.util.Date rgModifyTime) {
		set("rgModifyTime", rgModifyTime);
	}

	public java.util.Date getRgModifyTime() {
		return get("rgModifyTime");
	}

}
