package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanElgLtvElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "ALLOWED_HL_LOAN_AMOUNT")
	private String alwdHlLnAmt;
	@XmlElement(name = "ALLOWED_TOPUP_LOAN_AMOUNT")
	private String alwdTopUpLnAmt;
	@XmlElement(name = "ALLOWED_TOTAL_LOAN_AMOUNT")
	private String totallnAmt;
	@XmlElement(name = "COLLATERAL_ID")
	private String collateralId;
	@XmlElement(name = "HL_LTV_ON_AV")
	private String hlLtvOnAv;
	@XmlElement(name = "HL_LTV_ON_DV")
	private String hlLtvOnDv;
	@XmlElement(name = "HL_LTV_ON_MV")
	private String hlLtvOnMv;
	@XmlElement(name = "INSURANCE_LTV_ON_MV")
	private String insuranceLtvOnMv;
	@XmlElement(name = "PERC_OF_AMENITIES_ON_AV")
	private String percOfAminitiesOnMv;
	@XmlElement(name = "TOTAL_LOAN_LTV_ON_AV_GST")
	private String totalLnLtvOnAvGst;
	@XmlElement(name = "TOTAL_LOAN_LTV_ON_AV")
	private String totalLnLtvOnAv;
	@XmlElement(name = "TOTAL_LOAN_LTV_ON_DV")
	private String totalLnLtvOnDv;
	@XmlElement(name = "TOTAL_LOAN_LTV_ON_mV")
	private String totalLnLtvOnMv;
	@XmlElement(name = "LTV_AS_PER_NHB_POLICY")
	private String ltv_as_per_nhb_policy;

	public LoanElgLtvElement() {
	    super();
	}

	public String getAlwdHlLnAmt() {
		return alwdHlLnAmt;
	}

	public void setAlwdHlLnAmt(String alwdHlLnAmt) {
		this.alwdHlLnAmt = alwdHlLnAmt;
	}

	public String getAlwdTopUpLnAmt() {
		return alwdTopUpLnAmt;
	}

	public void setAlwdTopUpLnAmt(String alwdTopUpLnAmt) {
		this.alwdTopUpLnAmt = alwdTopUpLnAmt;
	}

	public String getTotallnAmt() {
		return totallnAmt;
	}

	public void setTotallnAmt(String totallnAmt) {
		this.totallnAmt = totallnAmt;
	}

	public String getCollateralId() {
		return collateralId;
	}

	public void setCollateralId(String collateralId) {
		this.collateralId = collateralId;
	}

	public String getHlLtvOnAv() {
		return hlLtvOnAv;
	}

	public void setHlLtvOnAv(String hlLtvOnAv) {
		this.hlLtvOnAv = hlLtvOnAv;
	}

	public String getHlLtvOnDv() {
		return hlLtvOnDv;
	}

	public void setHlLtvOnDv(String hlLtvOnDv) {
		this.hlLtvOnDv = hlLtvOnDv;
	}

	public String getHlLtvOnMv() {
		return hlLtvOnMv;
	}

	public void setHlLtvOnMv(String hlLtvOnMv) {
		this.hlLtvOnMv = hlLtvOnMv;
	}

	public String getInsuranceLtvOnMv() {
		return insuranceLtvOnMv;
	}

	public void setInsuranceLtvOnMv(String insuranceLtvOnMv) {
		this.insuranceLtvOnMv = insuranceLtvOnMv;
	}

	public String getPercOfAminitiesOnMv() {
		return percOfAminitiesOnMv;
	}

	public void setPercOfAminitiesOnMv(String percOfAminitiesOnMv) {
		this.percOfAminitiesOnMv = percOfAminitiesOnMv;
	}

	public String getTotalLnLtvOnAvGst() {
		return totalLnLtvOnAvGst;
	}

	public void setTotalLnLtvOnAvGst(String totalLnLtvOnAvGst) {
		this.totalLnLtvOnAvGst = totalLnLtvOnAvGst;
	}

	public String getTotalLnLtvOnAv() {
		return totalLnLtvOnAv;
	}

	public void setTotalLnLtvOnAv(String totalLnLtvOnAv) {
		this.totalLnLtvOnAv = totalLnLtvOnAv;
	}

	public String getTotalLnLtvOnDv() {
		return totalLnLtvOnDv;
	}

	public void setTotalLnLtvOnDv(String totalLnLtvOnDv) {
		this.totalLnLtvOnDv = totalLnLtvOnDv;
	}

	public String getTotalLnLtvOnMv() {
		return totalLnLtvOnMv;
	}

	public void setTotalLnLtvOnMv(String totalLnLtvOnMv) {
		this.totalLnLtvOnMv = totalLnLtvOnMv;
	}

	public String getLtv_as_per_nhb_policy() {
		return ltv_as_per_nhb_policy;
	}

	public void setLtv_as_per_nhb_policy(String ltv_as_per_nhb_policy) {
		this.ltv_as_per_nhb_policy = ltv_as_per_nhb_policy;
	}

}
