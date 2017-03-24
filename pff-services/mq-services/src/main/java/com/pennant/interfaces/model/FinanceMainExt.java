package com.pennant.interfaces.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class FinanceMainExt implements Serializable {

	private static final long serialVersionUID = 4713471747955559893L;

	private String finReference;
	private String repayIBAN;
	private String nstlAccNum;
	private boolean processFlag;
	private String hostRef;
	private BigDecimal sukukAmount = BigDecimal.ZERO;

	public FinanceMainExt() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRepayIBAN() {
		return repayIBAN;
	}

	public void setRepayIBAN(String repayIBAN) {
		this.repayIBAN = repayIBAN;
	}
	
	public String getNstlAccNum() {
		return nstlAccNum;
	}

	public void setNstlAccNum(String nstlAccNum) {
		this.nstlAccNum = nstlAccNum;
	}

	public boolean isProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(boolean processFlag) {
		this.processFlag = processFlag;
	}
	
	public String getHostRef() {
		return hostRef;
	}

	public void setHostRef(String hostRef) {
		this.hostRef = hostRef;
	}

	public BigDecimal getSukukAmount() {
		return sukukAmount;
	}

	public void setSukukAmount(BigDecimal sukukAmount) {
		this.sukukAmount = sukukAmount;
	}

}
