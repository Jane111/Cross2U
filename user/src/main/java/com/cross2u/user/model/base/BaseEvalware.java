package com.cross2u.user.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseEvalware<M extends BaseEvalware<M>> extends Model<M> implements IBean {

	public void setEwId(java.math.BigInteger ewId) {
		set("ewId", ewId);
	}

	public java.math.BigInteger getEwId() {
		return get("ewId");
	}

	public void setEwInId(Long ewInId) {
		set("ewInId", ewInId);
	}

	public Long getEwInId() {
		return get("ewInId");
	}

	public void setEwCommentator(Long ewCommentator) {
		set("ewCommentator", ewCommentator);
	}

	public Long getEwCommentator() {
		return get("ewCommentator");
	}

	public void setEwWId(Long ewWId) {
		set("ewWId", ewWId);
	}

	public Long getEwWId() {
		return get("ewWId");
	}

	public void setEwPId(Long ewPId) {
		set("ewPId", ewPId);
	}

	public Long getEwPId() {
		return get("ewPId");
	}

	public void setEwRank(Integer ewRank) {
		set("ewRank", ewRank);
	}

	public Integer getEwRank() {
		return get("ewRank");
	}

	public void setEwCotent(String ewCotent) {
		set("ewCotent", ewCotent);
	}

	public String getEwCotent() {
		return get("ewCotent");
	}

	public void setEwImg(String ewImg) {
		set("ewImg", ewImg);
	}

	public String getEwImg() {
		return get("ewImg");
	}

	public void setEwImg2(String ewImg2) {
		set("ewImg2", ewImg2);
	}

	public String getEwImg2() {
		return get("ewImg2");
	}

	public void setEwImg3(String ewImg3) {
		set("ewImg3", ewImg3);
	}

	public String getEwImg3() {
		return get("ewImg3");
	}

	public void setEwIsAnymous(Integer ewIsAnymous) {
		set("ewIsAnymous", ewIsAnymous);
	}

	public Integer getEwIsAnymous() {
		return get("ewIsAnymous");
	}

	public void setEwReply(String ewReply) {
		set("ewReply", ewReply);
	}

	public String getEwReply() {
		return get("ewReply");
	}

	public void setEwAERId(Long ewAERId) {
		set("ewAERId", ewAERId);
	}

	public Long getEwAERId() {
		return get("ewAERId");
	}

	public void setEwCreateTime(java.util.Date ewCreateTime) {
		set("ewCreateTime", ewCreateTime);
	}

	public java.util.Date getEwCreateTime() {
		return get("ewCreateTime");
	}

	public void setEwModifyTime(java.util.Date ewModifyTime) {
		set("ewModifyTime", ewModifyTime);
	}

	public java.util.Date getEwModifyTime() {
		return get("ewModifyTime");
	}

}
