package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SubHeadRule implements Serializable {

	private static final long serialVersionUID = 734402688459795510L;

	private String custCOB;
	private String custCtgCode;
	private String custNationality;
	private String custSector;
	private String custSubSector;
	private String custTypeCode;
	private String reqProduct;
	private BigDecimal REFUNDPFT = BigDecimal.ZERO;
	private BigDecimal TOTALPFT = BigDecimal.ZERO;
	private BigDecimal TOTALPFTBAL = BigDecimal.ZERO;
	private BigDecimal ACCRUE = BigDecimal.ZERO;
	private boolean isProcessed = false;
	private String reqFinType;
	private String reqFinPurpose;
	private String reqFinDivision;
	private BigDecimal CALFEE = BigDecimal.ZERO;
	private BigDecimal WAVFEE = BigDecimal.ZERO;
	private BigDecimal PAIDFEE = BigDecimal.ZERO;
	private int tenure = 0;
	private int remTenure = 0;

	private String insAccount;

	/*
	 * private String custIndustry; private String custCIF; private boolean custIsStaff = false; private String
	 * custParentCountry; private String custResdCountry; private String custRiskCountry; private String debitOrCredit;
	 * private String reqCampaign; private String reqFinBranch; private String reqFinCcy; private String reqFinType;
	 * private String reqGLHead;
	 */

	public SubHeadRule() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCOB() {
		return custCOB;
	}

	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getReqProduct() {
		return reqProduct;
	}

	public void setReqProduct(String reqProduct) {
		this.reqProduct = reqProduct;
	}

	public void setREFUNDPFT(BigDecimal rEFUNDPFT) {
		REFUNDPFT = rEFUNDPFT;
	}

	public BigDecimal getREFUNDPFT() {
		return REFUNDPFT;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getReqFinType() {
		return reqFinType;
	}

	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
	}

	public void setReqFinPurpose(String reqFinPurpose) {
		this.reqFinPurpose = reqFinPurpose;
	}

	public String getReqFinPurpose() {
		return reqFinPurpose;
	}

	public BigDecimal getCALFEE() {
		return CALFEE;
	}

	public void setCALFEE(BigDecimal cALFEE) {
		CALFEE = cALFEE;
	}

	public BigDecimal getWAVFEE() {
		return WAVFEE;
	}

	public void setWAVFEE(BigDecimal wAVFEE) {
		WAVFEE = wAVFEE;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public int getRemTenure() {
		return remTenure;
	}

	public void setRemTenure(int remTenure) {
		this.remTenure = remTenure;
	}

	public void setReqFinDivision(String reqFinDivision) {
		this.reqFinDivision = reqFinDivision;
	}

	public String getReqFinDivision() {
		return reqFinDivision;
	}

	public BigDecimal getTOTALPFT() {
		return TOTALPFT;
	}

	public void setTOTALPFT(BigDecimal tOTALPFT) {
		TOTALPFT = tOTALPFT;
	}

	public BigDecimal getTOTALPFTBAL() {
		return TOTALPFTBAL;
	}

	public void setTOTALPFTBAL(BigDecimal tOTALPFTBAL) {
		TOTALPFTBAL = tOTALPFTBAL;
	}

	public BigDecimal getACCRUE() {
		return ACCRUE;
	}

	public void setACCRUE(BigDecimal aCCRUE) {
		ACCRUE = aCCRUE;
	}

	public BigDecimal getPAIDFEE() {
		return PAIDFEE;
	}

	public void setPAIDFEE(BigDecimal pAIDFEE) {
		PAIDFEE = pAIDFEE;
	}

	// Set values into Map
	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> subHeadRuleMap = new HashMap<String, Object>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				subHeadRuleMap.put(this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return subHeadRuleMap;
	}

	public String getInsAccount() {
		return insAccount;
	}

	public void setInsAccount(String insAccount) {
		this.insAccount = insAccount;
	}

}
