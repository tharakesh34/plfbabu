package com.pennanttech.bajaj.process.collections.model;

import java.math.BigDecimal;

public class CollectionFinances {
	
	private long extractionId;
	private long custID;
	private String finReference;
	private BigDecimal oDPrincipal = BigDecimal.ZERO;
	private BigDecimal oDProfit = BigDecimal.ZERO;
	private int curODDays = 0;
	private String finType;
	private String entityCode;
	private String custCoreBank;
	private String branchSwiftBrnCde;
	private String bankRefNo;
	private String productCategory;
	private String finDivision;
	
	private BigDecimal totalPriBal = BigDecimal.ZERO;
	private BigDecimal foreClosureCharges = BigDecimal.ZERO;
	
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	
	/**
	 * default constructor
	 */
	public CollectionFinances() {
		super();
	}

	public long getExtractionId() {
		return extractionId;
	}

	public void setExtractionId(long extractionId) {
		this.extractionId = extractionId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int curODDays) {
		this.curODDays = curODDays;
	}

	public BigDecimal getODPrincipal() {
		return oDPrincipal;
	}

	public void setODPrincipal(BigDecimal odPrincipal) {
		this.oDPrincipal = odPrincipal;
	}

	public BigDecimal getODProfit() {
		return oDProfit;
	}

	public void setODProfit(BigDecimal odProfit) {
		this.oDProfit = odProfit;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}

	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}

	public String getBankRefNo() {
		return bankRefNo;
	}

	public void setBankRefNo(String bankRefNo) {
		this.bankRefNo = bankRefNo;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getForeClosureCharges() {
		return foreClosureCharges;
	}

	public void setForeClosureCharges(BigDecimal foreClosureCharges) {
		this.foreClosureCharges = foreClosureCharges;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

}
