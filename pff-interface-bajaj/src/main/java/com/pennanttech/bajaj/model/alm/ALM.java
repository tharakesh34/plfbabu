package com.pennanttech.bajaj.model.alm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.ProjectedAccrual;

public class ALM implements Serializable {
	private static final long serialVersionUID = 1L;

	private long agreementId;
	private String agreementNo;
	private String productFlag;
	private String npaStageId;
	private BigDecimal installment;
	private BigDecimal prinComp;
	private BigDecimal intComp;
	private Date dueDate;
	private BigDecimal accruedAmt;
	private Date accruedOn;
	private BigDecimal cumulativeAccrualAmt;
	private String advFlag;
	private Date finStartDate;
	private Date maturityDate;
	private BigDecimal ccyMinorCcyUnits;
	private int ccyEditField;
	private String	calRoundingMode;
	private int	roundingTarget;
	private String entityCode;

	private List<ProjectedAccrual> accrualList = new ArrayList<>();


	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public String getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}

	public String getProductFlag() {
		return productFlag;
	}

	public void setProductFlag(String productFlag) {
		this.productFlag = productFlag;
	}

	public String getNpaStageId() {
		return npaStageId;
	}

	public void setNpaStageId(String npaStageId) {
		this.npaStageId = npaStageId;
	}

	public BigDecimal getInstallment() {
		return installment;
	}

	public void setInstallment(BigDecimal installment) {
		this.installment = installment;
	}

	public BigDecimal getPrinComp() {
		return prinComp;
	}

	public void setPrinComp(BigDecimal prinComp) {
		this.prinComp = prinComp;
	}

	public BigDecimal getIntComp() {
		return intComp;
	}

	public void setIntComp(BigDecimal intComp) {
		this.intComp = intComp;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAccruedAmt() {
		return accruedAmt;
	}

	public void setAccruedAmt(BigDecimal accruedAmt) {
		this.accruedAmt = accruedAmt;
	}

	public Date getAccruedOn() {
		return accruedOn;
	}

	public void setAccruedOn(Date accruedOn) {
		this.accruedOn = accruedOn;
	}

	public BigDecimal getCumulativeAccrualAmt() {
		return cumulativeAccrualAmt;
	}

	public void setCumulativeAccrualAmt(BigDecimal cumulativeAccrualAmt) {
		this.cumulativeAccrualAmt = cumulativeAccrualAmt;
	}

	public String getAdvFlag() {
		return advFlag;
	}

	public void setAdvFlag(String advFlag) {
		this.advFlag = advFlag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public List<ProjectedAccrual> getAccrualList() {
		return accrualList;
	}

	public void setAccrualList(List<ProjectedAccrual> accrualList) {
		this.accrualList = accrualList;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public String getCalRoundingMode() {
		return calRoundingMode;
	}

	public void setCalRoundingMode(String calRoundingMode) {
		this.calRoundingMode = calRoundingMode;
	}

	public int getRoundingTarget() {
		return roundingTarget;
	}

	public void setRoundingTarget(int roundingTarget) {
		this.roundingTarget = roundingTarget;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}
}
