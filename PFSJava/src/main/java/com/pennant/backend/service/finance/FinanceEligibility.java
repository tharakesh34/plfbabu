package com.pennant.backend.service.finance;

import java.io.Serializable;
import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinanceDetail;

public class FinanceEligibility implements Serializable {

	private static final long serialVersionUID = 1480102644001246839L;

	private String custCIF;
	private String product;
	private String productDesc;
	private String promotionCode;
	private String promotionDesc;
	private String finCategory;
	private String finAssetType;
	private String finPurpose;
	private String lovDescFinPurposeName;
	private String ruleReturnType;
	private BigDecimal elgAmount;
	private int numberOfTerms = 0;
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	private String productFeature;
	private FinanceDetail financeDetail;

	public FinanceEligibility() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getPromotionDesc() {
		return promotionDesc;
	}

	public void setPromotionDesc(String promotionDesc) {
		this.promotionDesc = promotionDesc;
	}

	public void setRuleReturnType(String ruleReturnType) {
		this.ruleReturnType = ruleReturnType;
	}

	public String getRuleReturnType() {
		return ruleReturnType;
	}

	public BigDecimal getElgAmount() {
		return elgAmount;
	}

	public void setElgAmount(BigDecimal elgAmount) {
		this.elgAmount = elgAmount;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public String getProductFeature() {
		return productFeature;
	}

	public void setProductFeature(String productFeature) {
		this.productFeature = productFeature;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getFinAssetType() {
		return finAssetType;
	}

	public void setFinAssetType(String finAssetType) {
		this.finAssetType = finAssetType;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getLovDescFinPurposeName() {
		return lovDescFinPurposeName;
	}

	public void setLovDescFinPurposeName(String lovDescFinPurposeName) {
		this.lovDescFinPurposeName = lovDescFinPurposeName;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
