package com.pennant.backend.model.finance.manual.schedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ManualScheduleHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -5638265928026794694L;

	private long id = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private String fileName;
	private Date transactionDate;
	private int totalSchedules = 0;
	private String finEvent;
	private boolean validSchdUpload = true;
	private boolean manualSchdChange = false;
	private int numberOfTerms = 0;
	private BigDecimal totPrincipleAmt = BigDecimal.ZERO;
	private BigDecimal curPOSAmt = BigDecimal.ZERO;
	private List<ManualScheduleDetail> manualSchedules = new ArrayList<>(1);
	private Date prvSchdDate;
	private Date maturityDate;
	private String moduleDefiner;

	public ManualScheduleHeader() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public int getTotalSchedules() {
		return totalSchedules;
	}

	public void setTotalSchedules(int totalSchedules) {
		this.totalSchedules = totalSchedules;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public boolean isValidSchdUpload() {
		return validSchdUpload;
	}

	public void setValidSchdUpload(boolean validSchdUpload) {
		this.validSchdUpload = validSchdUpload;
	}

	public boolean isManualSchdChange() {
		return manualSchdChange;
	}

	public void setManualSchdChange(boolean manualSchdChange) {
		this.manualSchdChange = manualSchdChange;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public BigDecimal getTotPrincipleAmt() {
		return totPrincipleAmt;
	}

	public void setTotPrincipleAmt(BigDecimal totPrincipleAmt) {
		this.totPrincipleAmt = totPrincipleAmt;
	}

	public BigDecimal getCurPOSAmt() {
		return curPOSAmt;
	}

	public void setCurPOSAmt(BigDecimal curPOSAmt) {
		this.curPOSAmt = curPOSAmt;
	}

	public List<ManualScheduleDetail> getManualSchedules() {
		return manualSchedules;
	}

	public void setManualSchedules(List<ManualScheduleDetail> manualSchedules) {
		this.manualSchedules = manualSchedules;
	}

	public Date getPrvSchdDate() {
		return prvSchdDate;
	}

	public void setPrvSchdDate(Date prvSchdDate) {
		this.prvSchdDate = prvSchdDate;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

}
