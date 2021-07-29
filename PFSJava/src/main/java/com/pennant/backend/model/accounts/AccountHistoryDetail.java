package com.pennant.backend.model.accounts;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class AccountHistoryDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8145823857134311984L;

	private String accountId;
	private Date postDate;
	private BigDecimal todayDebits = BigDecimal.ZERO;
	private BigDecimal todayCredits = BigDecimal.ZERO;
	private BigDecimal todayNet = BigDecimal.ZERO;
	private BigDecimal shadowBal = BigDecimal.ZERO;
	private BigDecimal acBalance = BigDecimal.ZERO;
	private BigDecimal openingBal = BigDecimal.ZERO;
	private String entityCode;
	private String postBranch;
	private String branchProvince;

	public AccountHistoryDetail() {
		super();
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public BigDecimal getTodayDebits() {
		return todayDebits;
	}

	public void setTodayDebits(BigDecimal todayDebits) {
		this.todayDebits = todayDebits;
	}

	public BigDecimal getTodayCredits() {
		return todayCredits;
	}

	public void setTodayCredits(BigDecimal todayCredits) {
		this.todayCredits = todayCredits;
	}

	public BigDecimal getTodayNet() {
		return todayNet;
	}

	public void setTodayNet(BigDecimal todayNet) {
		this.todayNet = todayNet;
	}

	public BigDecimal getShadowBal() {
		return shadowBal;
	}

	public void setShadowBal(BigDecimal shadowBal) {
		this.shadowBal = shadowBal;
	}

	public BigDecimal getAcBalance() {
		return acBalance;
	}

	public void setAcBalance(BigDecimal acBalance) {
		this.acBalance = acBalance;
	}

	public BigDecimal getOpeningBal() {
		return openingBal;
	}

	public void setOpeningBal(BigDecimal openingBal) {
		this.openingBal = openingBal;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getPostBranch() {
		return postBranch;
	}

	public void setPostBranch(String postBranch) {
		this.postBranch = postBranch;
	}

	public String getBranchProvince() {
		return branchProvince;
	}

	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}

}
