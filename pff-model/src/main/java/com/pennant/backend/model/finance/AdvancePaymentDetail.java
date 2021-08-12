package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class AdvancePaymentDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	private String finReference;
	private long instructionUID = Long.MIN_VALUE;
	private BigDecimal advInt = BigDecimal.ZERO;
	private BigDecimal advIntTds = BigDecimal.ZERO;
	private BigDecimal advEMI = BigDecimal.ZERO;
	private BigDecimal advEMITds = BigDecimal.ZERO;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public AdvancePaymentDetail() {
		super();
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

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public BigDecimal getAdvInt() {
		return advInt;
	}

	public void setAdvInt(BigDecimal advInt) {
		this.advInt = advInt;
	}

	public BigDecimal getAdvIntTds() {
		return advIntTds;
	}

	public void setAdvIntTds(BigDecimal advIntTds) {
		this.advIntTds = advIntTds;
	}

	public BigDecimal getAdvEMI() {
		return advEMI;
	}

	public void setAdvEMI(BigDecimal advEMI) {
		this.advEMI = advEMI;
	}

	public BigDecimal getAdvEMITds() {
		return advEMITds;
	}

	public void setAdvEMITds(BigDecimal advEMITds) {
		this.advEMITds = advEMITds;
	}

}
