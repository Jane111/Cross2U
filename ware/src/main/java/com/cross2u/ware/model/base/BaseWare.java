package com.cross2u.ware.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseWare<M extends BaseWare<M>> extends Model<M> implements IBean {

	public void setWId(java.math.BigInteger wId) {
		set("wId", wId);
	}

	public java.math.BigInteger getWId() {
		return get("wId");
	}

	public void setWIdentifier(String wIdentifier) {
		set("wIdentifier", wIdentifier);
	}

	public String getWIdentifier() {
		return get("wIdentifier");
	}

	public void setWClass(java.math.BigInteger wClass) {
		set("wClass", wClass);
	}

	public java.math.BigInteger getWClass() {
		return get("wClass");
	}

	public void setWDeliverArea(String wDeliverArea) {
		set("wDeliverArea", wDeliverArea);
	}

	public String getWDeliverArea() {
		return get("wDeliverArea");
	}

	public void setWStore(java.math.BigInteger wStore) {
		set("wStore", wStore);
	}

	public java.math.BigInteger getWStore() {
		return get("wStore");
	}

	public void setWTitle(String wTitle) {
		set("wTitle", wTitle);
	}

	public String getWTitle() {
		return get("wTitle");
	}

	public void setWDescription(String wDescription) {
		set("wDescription", wDescription);
	}

	public String getWDescription() {
		return get("wDescription");
	}

	public void setWDesScore(Integer wDesScore) {
		set("wDesScore", wDesScore);
	}

	public Integer getWDesScore() {
		return get("wDesScore");
	}

	public void setWMainImage(String wMainImage) {
		set("wMainImage", wMainImage);
	}

	public String getWMainImage() {
		return get("wMainImage");
	}

	public void setWImage1(String wImage1) {
		set("wImage1", wImage1);
	}

	public String getWImage1() {
		return get("wImage1");
	}

	public void setWImage2(String wImage2) {
		set("wImage2", wImage2);
	}

	public String getWImage2() {
		return get("wImage2");
	}

	public void setWImage3(String wImage3) {
		set("wImage3", wImage3);
	}

	public String getWImage3() {
		return get("wImage3");
	}

	public void setWImage4(String wImage4) {
		set("wImage4", wImage4);
	}

	public String getWImage4() {
		return get("wImage4");
	}

	public void setWStatus(Integer wStatus) {
		set("wStatus", wStatus);
	}

	public Integer getWStatus() {
		return get("wStatus");
	}

	public void setWOnSaleTime(java.util.Date wOnSaleTime) {
		set("wOnSaleTime", wOnSaleTime);
	}

	public java.util.Date getWOnSaleTime() {
		return get("wOnSaleTime");
	}

	public void setWOffSaleTime(java.util.Date wOffSaleTime) {
		set("wOffSaleTime", wOffSaleTime);
	}

	public java.util.Date getWOffSaleTime() {
		return get("wOffSaleTime");
	}

	public void setWIsReceipt(Integer wIsReceipt) {
		set("wIsReceipt", wIsReceipt);
	}

	public Integer getWIsReceipt() {
		return get("wIsReceipt");
	}

	public void setWIsEnsure(Integer wIsEnsure) {
		set("wIsEnsure", wIsEnsure);
	}

	public Integer getWIsEnsure() {
		return get("wIsEnsure");
	}

	public void setWIsEnsureQuality(Integer wIsEnsureQuality) {
		set("wIsEnsureQuality", wIsEnsureQuality);
	}

	public Integer getWIsEnsureQuality() {
		return get("wIsEnsureQuality");
	}

	public void setWReplaceDays(Integer wReplaceDays) {
		set("wReplaceDays", wReplaceDays);
	}

	public Integer getWReplaceDays() {
		return get("wReplaceDays");
	}

	public void setWDeliverHour(Integer wDeliverHour) {
		set("wDeliverHour", wDeliverHour);
	}

	public Integer getWDeliverHour() {
		return get("wDeliverHour");
	}

	public void setWStartNum(Integer wStartNum) {
		set("wStartNum", wStartNum);
	}

	public Long getWStartNum() {
		return get("wStartNum");
	}

	public void setWHighNum(Integer wHighNum) {
		set("wHighNum", wHighNum);
	}

	public Long getWHighNum() {
		return get("wHighNum");
	}

	public void setWStartPrice(Float wStartPrice) {
		set("wStartPrice", wStartPrice);
	}

	public Float getWStartPrice() {
		return get("wStartPrice");
	}

	public void setWHighPrice(Float wHighPrice) {
		set("wHighPrice", wHighPrice);
	}

	public Float getWHighPrice() {
		return get("wHighPrice");
	}

	public void setWPriceUnit(Integer wPriceUnit) {
		set("wPriceUnit", wPriceUnit);
	}

	public Integer getWPriceUnit() {
		return get("wPriceUnit");
	}

	public void setWCreateTime(java.util.Date wCreateTime) {
		set("wCreateTime", wCreateTime);
	}

	public java.util.Date getWCreateTime() {
		return get("wCreateTime");
	}

	public void setWModifyTime(java.util.Date wModifyTime) {
		set("wModifyTime", wModifyTime);
	}

	public java.util.Date getWModifyTime() {
		return get("wModifyTime");
	}

}
