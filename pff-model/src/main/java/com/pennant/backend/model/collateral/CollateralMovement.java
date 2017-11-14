package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class CollateralMovement implements Entity {
	
	private long		movementSeq;
	private String		module;
	private String		reference;
	private String		collateralRef;
	private BigDecimal	assignPerc;
	private Date		valueDate;
	private String		process;
	
	public long getMovementSeq() {
		return movementSeq;
	}
	public void setMovementSeq(long movementSeq) {
		this.movementSeq = movementSeq;
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getCollateralRef() {
		return collateralRef;
	}
	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}
	
	public BigDecimal getAssignPerc() {
		return assignPerc;
	}
	public void setAssignPerc(BigDecimal assignPerc) {
		this.assignPerc = assignPerc;
	}
	
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	
	@Override
	public boolean isNew() {
		return false;
	}
	@Override
	public long getId() {
		return 0;
	}
	@Override
	public void setId(long id) {
	}
	
}
