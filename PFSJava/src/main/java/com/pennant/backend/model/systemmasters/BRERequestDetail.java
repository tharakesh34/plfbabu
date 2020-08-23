package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BRERequestDetail {
	@XmlElement
	private String custEmpType;
	@XmlElement
	private String custType;
	@XmlElement
	private String custCity;
	@XmlElement
	private String ruleCode;
	@XmlElement
	private String scoreRuleCode;
	@XmlElement
	private String custEmpsts;
	@XmlElement
	private BigDecimal cibilScore;
	@XmlElement
	private BigDecimal custAge;
	@XmlElement
	private long custId;
	@XmlElement
	private BigDecimal hlAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal creditCardOutstanding = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal autoLoanAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal twoWheelerLoanAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal blLoanAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal plLoanAmount = BigDecimal.ZERO;
	@XmlElement
	private int noofaccountsin30dpdinL12M;
	@XmlElement
	private int noofaccountspastdueinL6M;
	@XmlElement
	private BigDecimal declaredAnnualEMI;
	@XmlElement
	private BigDecimal declaredAnnualNet;
	@XmlElement
	private BigDecimal declaredNetBonus;
	@XmlElement
	private BigDecimal declaredRentalIncome;
	@XmlElement
	private BigDecimal declaredIntersetIncome;
	@XmlElement
	private BigDecimal declaredGrossReceipt;
	@XmlElement
	private BigDecimal riskScore;
	@XmlElement
	private BigDecimal finalSal;
	@XmlElement
	private String profession;
	@XmlElement
	private String qualification;
	@XmlElement
	private BigDecimal YearOfExp;
	@XmlElement
	private String segmentRule;

	@XmlElement
	private List<Perfois> perfoisData;

	@XmlElement
	private BigDecimal finalIncome;
	@XmlElement
	private BigDecimal finalObligation;
	@XmlElement
	private BigDecimal tenure;
	@XmlElement
	private BigDecimal roi;
	@XmlElement
	private BigDecimal foir;
	@XmlElement
	private BigDecimal propertyValue;
	@XmlElement
	private BigDecimal approvedLtv;

	@XmlElement
	private List<FieldDataMap> fieldDatas;

	@XmlElement
	private ApplicantData applicantData;
	@XmlElement
	private List<ApplicantData> coApplicantData;

	private Map<String, Object> map;

	public BigDecimal getCustAge() {
		return custAge;
	}

	public void setCustAge(BigDecimal custAge) {
		this.custAge = custAge;
	}

	@XmlElement
	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public BigDecimal getHlAmount() {
		return hlAmount;
	}

	public void setHlAmount(BigDecimal hlAmount) {
		this.hlAmount = hlAmount;
	}

	public BigDecimal getCreditCardOutstanding() {
		return creditCardOutstanding;
	}

	public void setCreditCardOutstanding(BigDecimal creditCardOutstanding) {
		this.creditCardOutstanding = creditCardOutstanding;
	}

	public BigDecimal getAutoLoanAmount() {
		return autoLoanAmount;
	}

	public void setAutoLoanAmount(BigDecimal autoLoanAmount) {
		this.autoLoanAmount = autoLoanAmount;
	}

	public BigDecimal getTwoWheelerLoanAmount() {
		return twoWheelerLoanAmount;
	}

	public void setTwoWheelerLoanAmount(BigDecimal twoWheelerLoanAmount) {
		this.twoWheelerLoanAmount = twoWheelerLoanAmount;
	}

	public BigDecimal getBlLoanAmount() {
		return blLoanAmount;
	}

	public void setBlLoanAmount(BigDecimal blLoanAmount) {
		this.blLoanAmount = blLoanAmount;
	}

	public BigDecimal getPlLoanAmount() {
		return plLoanAmount;
	}

	public void setPlLoanAmount(BigDecimal plLoanAmount) {
		this.plLoanAmount = plLoanAmount;
	}

	public int getNoofaccountsin30dpdinL12M() {
		return noofaccountsin30dpdinL12M;
	}

	public void setNoofaccountsin30dpdinL12M(int noofaccountsin30dpdinL12M) {
		this.noofaccountsin30dpdinL12M = noofaccountsin30dpdinL12M;
	}

	public BigDecimal getDeclaredAnnualEMI() {
		return declaredAnnualEMI;
	}

	public void setDeclaredAnnualEMI(BigDecimal declaredAnnualEMI) {
		this.declaredAnnualEMI = declaredAnnualEMI;
	}

	public BigDecimal getDeclaredAnnualNet() {
		return declaredAnnualNet;
	}

	public void setDeclaredAnnualNet(BigDecimal declaredAnnualNet) {
		this.declaredAnnualNet = declaredAnnualNet;
	}

	public BigDecimal getDeclaredNetBonus() {
		return declaredNetBonus;
	}

	public void setDeclaredNetBonus(BigDecimal declaredNetBonus) {
		this.declaredNetBonus = declaredNetBonus;
	}

	public BigDecimal getDeclaredRentalIncome() {
		return declaredRentalIncome;
	}

	public void setDeclaredRentalIncome(BigDecimal declaredRentalIncome) {
		this.declaredRentalIncome = declaredRentalIncome;
	}

	public BigDecimal getDeclaredIntersetIncome() {
		return declaredIntersetIncome;
	}

	public void setDeclaredIntersetIncome(BigDecimal declaredIntersetIncome) {
		this.declaredIntersetIncome = declaredIntersetIncome;
	}

	public BigDecimal getDeclaredGrossReceipt() {
		return declaredGrossReceipt;
	}

	public void setDeclaredGrossReceipt(BigDecimal declaredGrossReceipt) {
		this.declaredGrossReceipt = declaredGrossReceipt;
	}

	public List<Perfois> getPerfoisData() {
		return perfoisData;
	}

	public void setPerfoisData(List<Perfois> perfoisData) {
		this.perfoisData = perfoisData;
	}

	public BigDecimal getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(BigDecimal cibilScore) {
		this.cibilScore = cibilScore;
	}

	public String getCustEmpsts() {
		return custEmpsts;
	}

	public void setCustEmpsts(String custEmpsts) {
		this.custEmpsts = custEmpsts;
	}

	public String getScoreRuleCode() {
		return scoreRuleCode;
	}

	public void setScoreRuleCode(String scoreRuleCode) {
		this.scoreRuleCode = scoreRuleCode;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public int getNoofaccountspastdueinL6M() {
		return noofaccountspastdueinL6M;
	}

	public void setNoofaccountspastdueinL6M(int noofaccountspastdueinL6M) {
		this.noofaccountspastdueinL6M = noofaccountspastdueinL6M;
	}

	public BigDecimal getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(BigDecimal riskScore) {
		this.riskScore = riskScore;
	}

	public BigDecimal getFinalSal() {
		return finalSal;
	}

	public void setFinalSal(BigDecimal finalSal) {
		this.finalSal = finalSal;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public BigDecimal getYearOfExp() {
		return YearOfExp;
	}

	public void setYearOfExp(BigDecimal yearOfExp) {
		YearOfExp = yearOfExp;
	}

	public String getSegmentRule() {
		return segmentRule;
	}

	public void setSegmentRule(String segmentRule) {
		this.segmentRule = segmentRule;
	}

	public BigDecimal getTenure() {
		return tenure;
	}

	public void setTenure(BigDecimal tenure) {
		this.tenure = tenure;
	}

	public BigDecimal getFoir() {
		return foir;
	}

	public void setFoir(BigDecimal foir) {
		this.foir = foir;
	}

	public BigDecimal getRoi() {
		return roi;
	}

	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}

	public BigDecimal getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(BigDecimal propertyValue) {
		this.propertyValue = propertyValue;
	}

	public BigDecimal getApprovedLtv() {
		return approvedLtv;
	}

	public void setApprovedLtv(BigDecimal approvedLtv) {
		this.approvedLtv = approvedLtv;
	}

	public BigDecimal getFinalIncome() {
		return finalIncome;
	}

	public void setFinalIncome(BigDecimal finalIncome) {
		this.finalIncome = finalIncome;
	}

	public BigDecimal getFinalObligation() {
		return finalObligation;
	}

	public void setFinalObligation(BigDecimal finalObligation) {
		this.finalObligation = finalObligation;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public void setFieldData(String fieldName, Object value) {
		if (this.map == null) {
			this.map = new HashMap<String, Object>();
		}
		this.map.put(fieldName, value);
	}

	public List<FieldDataMap> getFieldDatas() {
		return fieldDatas;
	}

	public void setFieldDatas(List<FieldDataMap> fieldDatas) {
		this.fieldDatas = fieldDatas;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public ApplicantData getApplicantData() {
		return applicantData;
	}

	public void setApplicantData(ApplicantData applicantData) {
		this.applicantData = applicantData;
	}

	public List<ApplicantData> getCoApplicantData() {
		return coApplicantData;
	}

	public void setCoApplicantData(List<ApplicantData> coApplicantData) {
		this.coApplicantData = coApplicantData;
	}

}
