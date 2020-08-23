package com.pennant.backend.model.agreement;

import java.math.BigDecimal;
import java.util.Date;

public class InterestCertificate {

	private String custName = "";
	private String custAddrHnbr = "";
	private String custAddrStreet = "";
	private String countryDesc = "";
	private String custAddrState = "";
	private String custAddrCity = "";
	private String custAddrZIP = "";
	private String finReference = "";
	private String custEmail = "";
	private String custPhoneNumber = "";
	private String finTypeDesc = "";
	private String coApplicant = "";
	private BigDecimal finAssetvalue = BigDecimal.ZERO;
	private BigDecimal effectiveRate = BigDecimal.ZERO;
	private String entityCode = "";
	private String entityDesc = "";
	private String entityPanNumber = "";
	private String entityAddrHnbr = "";
	private String entityFlatNbr = "";
	private String entityAddrStreet = "";
	private String entityState = "";
	private String entityCity = "";
	private String entityZip = "";
	private Date finPostDate;
	private BigDecimal finSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal finSchdPriPaid = BigDecimal.ZERO;
	private String schdPftPaid = "0.00";
	private String schdPriPaid = "0.00";
	private String totalPaid = "0.00";
	private String finCcy;
	private String finStartDate;
	private String finEndDate;
	private String appDate;
	private String finType = "";
	private String finAmount;
	private String addressType1;
	private String addressType2;
	private String addressType3;
	private String addressType4;
	private String addressType5;
	private String custFlatNbr = "";
	private String courseName;
	private String collegeName;
	private String universityName;
	private String country;
	private String totOustandingamt;
	private String totalPftBal;
	private String totalPriBal;
	private String custAddress = "";

	private BigDecimal emiAmount = BigDecimal.ZERO;
	private String pftSchd = "0.00";
	private String priSchd = "0.00";
	private String emiAmt = "0.00";

	private String pftSchdInWords = "";
	private String priSchdInWords = "";
	private String emiAmtInWords = "";

	private String schdPftPaidInWords = "";
	private String schdPriPaidInWords = "";
	private String totalPaidInWords = "";

	private String grcPft = "0.00";
	private String grcPftPaid = "0.00";

	// Getter and Setter
	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustAddrHnbr() {
		return custAddrHnbr;
	}

	public void setCustAddrHnbr(String custAddrHnbr) {
		this.custAddrHnbr = custAddrHnbr;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCountryDesc() {
		return countryDesc;
	}

	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}

	public String getCustAddrState() {
		return custAddrState;
	}

	public void setCustAddrState(String custAddrState) {
		this.custAddrState = custAddrState;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustPhoneNumber() {
		return custPhoneNumber;
	}

	public void setCustPhoneNumber(String custPhoneNumber) {
		this.custPhoneNumber = custPhoneNumber;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getCoApplicant() {
		return coApplicant;
	}

	public void setCoApplicant(String coApplicant) {
		this.coApplicant = coApplicant;
	}

	public BigDecimal getFinAssetvalue() {
		return finAssetvalue;
	}

	public void setFinAssetvalue(BigDecimal finAssetvalue) {
		this.finAssetvalue = finAssetvalue;
	}

	public BigDecimal getEffectiveRate() {
		return effectiveRate;
	}

	public void setEffectiveRate(BigDecimal effectiveRate) {
		this.effectiveRate = effectiveRate;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getEntityPanNumber() {
		return entityPanNumber;
	}

	public void setEntityPanNumber(String entityPanNumber) {
		this.entityPanNumber = entityPanNumber;
	}

	public String getEntityAddrHnbr() {
		return entityAddrHnbr;
	}

	public void setEntityAddrHnbr(String entityAddrHnbr) {
		this.entityAddrHnbr = entityAddrHnbr;
	}

	public String getEntityFlatNbr() {
		return entityFlatNbr;
	}

	public void setEntityFlatNbr(String entityFlatNbr) {
		this.entityFlatNbr = entityFlatNbr;
	}

	public String getEntityAddrStreet() {
		return entityAddrStreet;
	}

	public void setEntityAddrStreet(String entityAddrStreet) {
		this.entityAddrStreet = entityAddrStreet;
	}

	public String getEntityState() {
		return entityState;
	}

	public void setEntityState(String entityState) {
		this.entityState = entityState;
	}

	public String getEntityCity() {
		return entityCity;
	}

	public void setEntityCity(String entityCity) {
		this.entityCity = entityCity;
	}

	public Date getFinPostDate() {
		return finPostDate;
	}

	public void setFinPostDate(Date finPostDate) {
		this.finPostDate = finPostDate;
	}

	public BigDecimal getFinSchdPftPaid() {
		return finSchdPftPaid;
	}

	public void setFinSchdPftPaid(BigDecimal finSchdPftPaid) {
		this.finSchdPftPaid = finSchdPftPaid;
	}

	public BigDecimal getFinSchdPriPaid() {
		return finSchdPriPaid;
	}

	public void setFinSchdPriPaid(BigDecimal finSchdPriPaid) {
		this.finSchdPriPaid = finSchdPriPaid;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getFinEndDate() {
		return finEndDate;
	}

	public void setFinEndDate(String finEndDate) {
		this.finEndDate = finEndDate;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(String schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public String getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(String schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public String getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(String totalPaid) {
		this.totalPaid = totalPaid;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getAddressType1() {
		return addressType1;
	}

	public void setAddressType1(String addressType1) {
		this.addressType1 = addressType1;
	}

	public String getAddressType2() {
		return addressType2;
	}

	public void setAddressType2(String addressType2) {
		this.addressType2 = addressType2;
	}

	public String getAddressType3() {
		return addressType3;
	}

	public void setAddressType3(String addressType3) {
		this.addressType3 = addressType3;
	}

	public String getAddressType4() {
		return addressType4;
	}

	public void setAddressType4(String addressType4) {
		this.addressType4 = addressType4;
	}

	public String getAddressType5() {
		return addressType5;
	}

	public void setAddressType5(String addressType5) {
		this.addressType5 = addressType5;
	}

	public String getEntityZip() {
		return entityZip;
	}

	public void setEntityZip(String entityZip) {
		this.entityZip = entityZip;
	}

	public String getCustFlatNbr() {
		return custFlatNbr;
	}

	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTotOustandingamt() {
		return totOustandingamt;
	}

	public void setTotOustandingamt(String totOustandingamt) {
		this.totOustandingamt = totOustandingamt;
	}

	public String getTotalPftBal() {
		return totalPftBal;
	}

	public void setTotalPftBal(String totalPftBal) {
		this.totalPftBal = totalPftBal;
	}

	public String getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(String totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(BigDecimal emiAmount) {
		this.emiAmount = emiAmount;
	}

	public String getPftSchd() {
		return pftSchd;
	}

	public void setPftSchd(String pftSchd) {
		this.pftSchd = pftSchd;
	}

	public String getPriSchd() {
		return priSchd;
	}

	public void setPriSchd(String priSchd) {
		this.priSchd = priSchd;
	}

	public String getEmiAmt() {
		return emiAmt;
	}

	public void setEmiAmt(String emiAmt) {
		this.emiAmt = emiAmt;
	}

	public String getPftSchdInWords() {
		return pftSchdInWords;
	}

	public void setPftSchdInWords(String pftSchdInWords) {
		this.pftSchdInWords = pftSchdInWords;
	}

	public String getPriSchdInWords() {
		return priSchdInWords;
	}

	public void setPriSchdInWords(String priSchdInWords) {
		this.priSchdInWords = priSchdInWords;
	}

	public String getEmiAmtInWords() {
		return emiAmtInWords;
	}

	public void setEmiAmtInWords(String emiAmtInWords) {
		this.emiAmtInWords = emiAmtInWords;
	}

	public String getSchdPftPaidInWords() {
		return schdPftPaidInWords;
	}

	public void setSchdPftPaidInWords(String schdPftPaidInWords) {
		this.schdPftPaidInWords = schdPftPaidInWords;
	}

	public String getSchdPriPaidInWords() {
		return schdPriPaidInWords;
	}

	public void setSchdPriPaidInWords(String schdPriPaidInWords) {
		this.schdPriPaidInWords = schdPriPaidInWords;
	}

	public String getTotalPaidInWords() {
		return totalPaidInWords;
	}

	public void setTotalPaidInWords(String totalPaidInWords) {
		this.totalPaidInWords = totalPaidInWords;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public String getGrcPft() {
		return grcPft;
	}

	public void setGrcPft(String grcPft) {
		this.grcPft = grcPft;
	}

	public String getGrcPftPaid() {
		return grcPftPaid;
	}

	public void setGrcPftPaid(String grcPftPaid) {
		this.grcPftPaid = grcPftPaid;
	}

}
