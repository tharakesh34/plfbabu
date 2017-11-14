package com.pennant.backend.model.finance;

import java.io.Serializable;

public class FinanceMainExt implements Serializable {

	private static final long serialVersionUID = 4713471747955559893L;

	private String finReference;
	private String repayIBAN;
	private String nstlAccNum;
	private boolean processFlag;
	private String ifscCode;

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

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

}
