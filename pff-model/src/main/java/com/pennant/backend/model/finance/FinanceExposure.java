/**
 * 
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FinanceExposure implements Serializable {

	private static final long serialVersionUID = -949056579779911379L;
	private String finType;
	private String finTypeDesc;
	private String custCif;
	private String jointCif;
	private String guarantorCif;
	private String custShrtName;
	private long finID;
	private String finReference;
	private Date finStartDate;
	private Date maturityDate;
	private String finCCY;
	private int ccyEditField;
	private BigDecimal financeAmt = BigDecimal.ZERO;
	private BigDecimal currentExpoSure = BigDecimal.ZERO;
	private boolean overdue;
	private String pastdueDays;
	private BigDecimal overdueAmt = BigDecimal.ZERO;
	private BigDecimal financeAmtinBaseCCY = BigDecimal.ZERO;
	private BigDecimal currentExpoSureinBaseCCY = BigDecimal.ZERO;
	private BigDecimal overdueAmtBaseCCY = BigDecimal.ZERO;
	private String status;
	private String worstStatus;
	private BigDecimal totalRepayAmt;
	private String toCcy;
	private Long custID;

	public FinanceExposure() {
	    super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("totalRepayAmt");
		excludeFields.add("toCcy");
		excludeFields.add("custID");
		return excludeFields;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public String getFinCCY() {
		return finCCY;
	}

	public void setFinCCY(String finCCY) {
		this.finCCY = finCCY;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public BigDecimal getFinanceAmt() {
		return financeAmt;
	}

	public void setFinanceAmt(BigDecimal financeAmt) {
		this.financeAmt = financeAmt;
	}

	public BigDecimal getCurrentExpoSure() {
		return currentExpoSure;
	}

	public void setCurrentExpoSure(BigDecimal currentExpoSure) {
		this.currentExpoSure = currentExpoSure;
	}

	public boolean isOverdue() {
		return overdue;
	}

	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}

	public String getPastdueDays() {
		return pastdueDays;
	}

	public void setPastdueDays(String pastdueDays) {
		this.pastdueDays = pastdueDays;
	}

	public BigDecimal getOverdueAmt() {
		return overdueAmt;
	}

	public void setOverdueAmt(BigDecimal overdueAmt) {
		this.overdueAmt = overdueAmt;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getJointCif() {
		return jointCif;
	}

	public void setJointCif(String jointCif) {
		this.jointCif = jointCif;
	}

	public String getGuarantorCif() {
		return guarantorCif;
	}

	public void setGuarantorCif(String guarantorCif) {
		this.guarantorCif = guarantorCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public BigDecimal getFinanceAmtinBaseCCY() {
		return financeAmtinBaseCCY;
	}

	public void setFinanceAmtinBaseCCY(BigDecimal financeAmtinBaseCCY) {
		this.financeAmtinBaseCCY = financeAmtinBaseCCY;
	}

	public BigDecimal getCurrentExpoSureinBaseCCY() {
		return currentExpoSureinBaseCCY;
	}

	public void setCurrentExpoSureinBaseCCY(BigDecimal currentExpoSureinBaseCCY) {
		this.currentExpoSureinBaseCCY = currentExpoSureinBaseCCY;
	}

	public BigDecimal getOverdueAmtBaseCCY() {
		return overdueAmtBaseCCY;
	}

	public void setOverdueAmtBaseCCY(BigDecimal overdueAmtBaseCCY) {
		this.overdueAmtBaseCCY = overdueAmtBaseCCY;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setWorstStatus(String worstStatus) {
		this.worstStatus = worstStatus;
	}

	public String getWorstStatus() {
		return worstStatus;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public BigDecimal getTotalRepayAmt() {
		return totalRepayAmt;
	}

	public void setTotalRepayAmt(BigDecimal totalRepayAmt) {
		this.totalRepayAmt = totalRepayAmt;
	}

	public String getToCcy() {
		return toCcy;
	}

	public void setToCcy(String toCcy) {
		this.toCcy = toCcy;
	}

	public Long getCustID() {
		return custID;
	}

	public void setCustID(Long custID) {
		this.custID = custID;
	}
}
