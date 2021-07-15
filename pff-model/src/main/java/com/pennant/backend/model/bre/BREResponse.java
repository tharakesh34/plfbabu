package com.pennant.backend.model.bre;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.zkoss.json.JSONObject;

import com.pennant.backend.model.WSReturnStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author shinde.b
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BREResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal bureauObligation = BigDecimal.ZERO;
	private BigDecimal perfiosObligation = BigDecimal.ZERO;
	private BigDecimal declaredObligation = BigDecimal.ZERO;
	private BigDecimal finalObligation = BigDecimal.ZERO;
	private BigDecimal largeSalaryCredit = BigDecimal.ZERO;
	private BigDecimal annualNetSalary = BigDecimal.ZERO;
	private BigDecimal bonus = BigDecimal.ZERO;
	private BigDecimal finalNetSalary = BigDecimal.ZERO;
	private BigDecimal perfiosIncome = BigDecimal.ZERO;
	private BigDecimal declaredMaxBonus = BigDecimal.ZERO;
	private BigDecimal calculatedNetSalary = BigDecimal.ZERO;
	private BigDecimal declardeOtherIncome = BigDecimal.ZERO;
	private BigDecimal declaredMaxotherIncome = BigDecimal.ZERO;
	private BigDecimal finalIncome = BigDecimal.ZERO;
	private BigDecimal calculatedAnnualNetSalary = BigDecimal.ZERO;
	private String bureauDelinquency;
	private boolean emiBounceflag;
	private boolean salaryNotCreditFlag;
	@XmlElement
	private BigDecimal riskScore = BigDecimal.ZERO;
	private BigDecimal totalPoint = BigDecimal.ZERO;
	private BigDecimal calculatedGrossRecepit = BigDecimal.ZERO;
	private BigDecimal finalGrossRecepit = BigDecimal.ZERO;
	@XmlElement
	private String scoringGroup = "";
	@XmlElement
	private WSReturnStatus returnStatus;

	@XmlElement
	private JSONObject result;

	private Map<String, Object> dataMap;

	public BREResponse() {
		super();
	}

	public BigDecimal getBureauObligation() {
		return bureauObligation;
	}

	public void setBureauObligation(BigDecimal bureauObligation) {
		this.bureauObligation = bureauObligation;
	}

	public BigDecimal getPerfiosObligation() {
		return perfiosObligation;
	}

	public void setPerfiosObligation(BigDecimal perfiosObligation) {
		this.perfiosObligation = perfiosObligation;
	}

	public BigDecimal getDeclaredObligation() {
		return declaredObligation;
	}

	public void setDeclaredObligation(BigDecimal declaredObligation) {
		this.declaredObligation = declaredObligation;
	}

	public BigDecimal getFinalObligation() {
		return finalObligation;
	}

	public void setFinalObligation(BigDecimal finalObligation) {
		this.finalObligation = finalObligation;
	}

	public BigDecimal getLargeSalaryCredit() {
		return largeSalaryCredit;
	}

	public void setLargeSalaryCredit(BigDecimal largeSalaryCredit) {
		this.largeSalaryCredit = largeSalaryCredit;
	}

	public BigDecimal getAnnualNetSalary() {
		return annualNetSalary;
	}

	public void setAnnualNetSalary(BigDecimal annualNetSalary) {
		this.annualNetSalary = annualNetSalary;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	public BigDecimal getFinalNetSalary() {
		return finalNetSalary;
	}

	public void setFinalNetSalary(BigDecimal finalNetSalary) {
		this.finalNetSalary = finalNetSalary;
	}

	public BigDecimal getPerfiosIncome() {
		return perfiosIncome;
	}

	public void setPerfiosIncome(BigDecimal perfiosIncome) {
		this.perfiosIncome = perfiosIncome;
	}

	public BigDecimal getDeclaredMaxBonus() {
		return declaredMaxBonus;
	}

	public void setDeclaredMaxBonus(BigDecimal declaredMaxBonus) {
		this.declaredMaxBonus = declaredMaxBonus;
	}

	public BigDecimal getCalculatedNetSalary() {
		return calculatedNetSalary;
	}

	public void setCalculatedNetSalary(BigDecimal calculatedNetSalary) {
		this.calculatedNetSalary = calculatedNetSalary;
	}

	public BigDecimal getDeclardeOtherIncome() {
		return declardeOtherIncome;
	}

	public void setDeclardeOtherIncome(BigDecimal declardeOtherIncome) {
		this.declardeOtherIncome = declardeOtherIncome;
	}

	public BigDecimal getDeclaredMaxotherIncome() {
		return declaredMaxotherIncome;
	}

	public void setDeclaredMaxotherIncome(BigDecimal declaredMaxotherIncome) {
		this.declaredMaxotherIncome = declaredMaxotherIncome;
	}

	public BigDecimal getFinalIncome() {
		return finalIncome;
	}

	public void setFinalIncome(BigDecimal finalIncome) {
		this.finalIncome = finalIncome;
	}

	public BigDecimal getCalculatedAnnualNetSalary() {
		return calculatedAnnualNetSalary;
	}

	public void setCalculatedAnnualNetSalary(BigDecimal calculatedAnnualNetSalary) {
		this.calculatedAnnualNetSalary = calculatedAnnualNetSalary;
	}

	public String getBureauDelinquency() {
		return bureauDelinquency;
	}

	public void setBureauDelinquency(String bureauDelinquency) {
		this.bureauDelinquency = bureauDelinquency;
	}

	public boolean isEmiBounceflag() {
		return emiBounceflag;
	}

	public void setEmiBounceflag(boolean emiBounceflag) {
		this.emiBounceflag = emiBounceflag;
	}

	public boolean isSalaryNotCreditFlag() {
		return salaryNotCreditFlag;
	}

	public void setSalaryNotCreditFlag(boolean salaryNotCreditFlag) {
		this.salaryNotCreditFlag = salaryNotCreditFlag;
	}

	public BigDecimal getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(BigDecimal riskScore) {
		this.riskScore = riskScore;
	}

	public BigDecimal getTotalPoint() {
		return totalPoint;
	}

	public void setTotalPoint(BigDecimal totalPoint) {
		this.totalPoint = totalPoint;
	}

	public BigDecimal getCalculatedGrossRecepit() {
		return calculatedGrossRecepit;
	}

	public void setCalculatedGrossRecepit(BigDecimal calculatedGrossRecepit) {
		this.calculatedGrossRecepit = calculatedGrossRecepit;
	}

	public BigDecimal getFinalGrossRecepit() {
		return finalGrossRecepit;
	}

	public void setFinalGrossRecepit(BigDecimal finalGrossRecepit) {
		this.finalGrossRecepit = finalGrossRecepit;
	}

	public String getScoringGroup() {
		return scoringGroup;
	}

	public void setScoringGroup(String scoringGroup) {
		this.scoringGroup = scoringGroup;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

}
