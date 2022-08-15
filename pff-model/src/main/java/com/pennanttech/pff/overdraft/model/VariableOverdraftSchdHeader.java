package com.pennanttech.pff.overdraft.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class VariableOverdraftSchdHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String finReference;
	private String finEvent;
	private Date transactionDate;
	private String fileName;
	private int totalSchedules = 0;
	private BigDecimal totDropLineAmt = BigDecimal.ZERO;
	private List<VariableOverdraftSchdDetail> variableOverdraftSchdDetails = new ArrayList<>(1);
	private boolean validSchdUpload = true;
	private int numberOfTerms = 0;
	private BigDecimal totPrincipleAmt = BigDecimal.ZERO;
	private BigDecimal curPOSAmt = BigDecimal.ZERO;

	public VariableOverdraftSchdHeader() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalSchedules() {
		return totalSchedules;
	}

	public void setTotalSchedules(int totalSchedules) {
		this.totalSchedules = totalSchedules;
	}

	public BigDecimal getTotDropLineAmt() {
		return totDropLineAmt;
	}

	public void setTotDropLineAmt(BigDecimal totDropLineAmt) {
		this.totDropLineAmt = totDropLineAmt;
	}

	public List<VariableOverdraftSchdDetail> getVariableOverdraftSchdDetails() {
		return variableOverdraftSchdDetails;
	}

	public void setVariableOverdraftSchdDetails(List<VariableOverdraftSchdDetail> variableOverdraftSchdDetails) {
		this.variableOverdraftSchdDetails = variableOverdraftSchdDetails;
	}

	public boolean isValidSchdUpload() {
		return validSchdUpload;
	}

	public void setValidSchdUpload(boolean validSchdUpload) {
		this.validSchdUpload = validSchdUpload;
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

}
