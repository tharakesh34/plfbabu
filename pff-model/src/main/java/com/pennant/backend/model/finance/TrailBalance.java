package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class TrailBalance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long headerId;
	private long seqNo;
	private String dimention;
	private int id;
	private long link;
	private BigDecimal transactionAmount = BigDecimal.ZERO;
	private String transactionAmountType;
	private String umskz;
	private String businessArea;
	private String businessUnit;
	private String profitCenter;
	private String costCenter;
	private String narration1;
	private String narration2;
	private String accountType;
	private String ledgerAccount;
	private String accountTypeDes;
	private BigDecimal openingBalance = BigDecimal.ZERO;
	private String openingBalanceType;
	private BigDecimal closingBalance = BigDecimal.ZERO;
	private String closingBalanceType;
	private BigDecimal creditAmount = BigDecimal.ZERO;
	private BigDecimal debitAmount = BigDecimal.ZERO;
	private String countryCode;
	private String stateCode;
	private String entity;
	private String account;
	private String finType;

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}
	
	public String getDimention() {
		return dimention;
	}

	public void setDimention(String dimention) {
		this.dimention = dimention;
	}

	public long getLink() {
		return link;
	}

	public void setLink(long link) {
		this.link = link;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionAmountType() {
		return transactionAmountType;
	}

	public void setTransactionAmountType(String transactionAmountType) {
		this.transactionAmountType = transactionAmountType;
	}

	public String getUmskz() {
		return umskz;
	}

	public void setUmskz(String umskz) {
		this.umskz = umskz;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	public String getProfitCenter() {
		return profitCenter;
	}

	public void setProfitCenter(String profitCenter) {
		this.profitCenter = profitCenter;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getNarration1() {
		return narration1;
	}

	public void setNarration1(String narration1) {
		this.narration1 = narration1;
	}

	public String getNarration2() {
		return narration2;
	}

	public void setNarration2(String narration2) {
		this.narration2 = narration2;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getLedgerAccount() {
		return ledgerAccount;
	}

	public void setLedgerAccount(String ledgerAccount) {
		this.ledgerAccount = ledgerAccount;
	}

	public String getAccountTypeDes() {
		return accountTypeDes;
	}

	public void setAccountTypeDes(String accountTypeDes) {
		this.accountTypeDes = accountTypeDes;
	}

	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getOpeningBalanceType() {
		return openingBalanceType;
	}

	public void setOpeningBalanceType(String openingBalanceType) {
		this.openingBalanceType = openingBalanceType;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getClosingBalanceType() {
		return closingBalanceType;
	}

	public void setClosingBalanceType(String closingBalanceType) {
		this.closingBalanceType = closingBalanceType;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

}