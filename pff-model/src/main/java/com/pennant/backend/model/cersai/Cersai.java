package com.pennant.backend.model.cersai;

public class Cersai {

	private String CERSAI_REFERENCE_NUMBER;
	private String CERSAI_SECURITY_ID;
	private String CERSAI_ASSET_ID; // CERSAI Number
	private String LOAN_REFERENCE_NUMBER; // Loan Reference Number
	private String COLLATERAL_REFERENCE_NUMBER; // Collateral Reference Number
	private String RESP_BATCH_ID;
	private String STATUS;
	private String REMARKS;

	public String getCERSAI_REFERENCE_NUMBER() {
		return CERSAI_REFERENCE_NUMBER;
	}

	public void setCERSAI_REFERENCE_NUMBER(String cERSAI_REFERENCE_NUMBER) {
		CERSAI_REFERENCE_NUMBER = cERSAI_REFERENCE_NUMBER;
	}

	public String getCERSAI_SECURITY_ID() {
		return CERSAI_SECURITY_ID;
	}

	public void setCERSAI_SECURITY_ID(String cERSAI_SECURITY_ID) {
		CERSAI_SECURITY_ID = cERSAI_SECURITY_ID;
	}

	public String getCERSAI_ASSET_ID() {
		return CERSAI_ASSET_ID;
	}

	public void setCERSAI_ASSET_ID(String cERSAI_ASSET_ID) {
		CERSAI_ASSET_ID = cERSAI_ASSET_ID;
	}

	public String getLOAN_REFERENCE_NUMBER() {
		return LOAN_REFERENCE_NUMBER;
	}

	public void setLOAN_REFERENCE_NUMBER(String lOAN_REFERENCE_NUMBER) {
		LOAN_REFERENCE_NUMBER = lOAN_REFERENCE_NUMBER;
	}

	public String getCOLLATERAL_REFERENCE_NUMBER() {
		return COLLATERAL_REFERENCE_NUMBER;
	}

	public void setCOLLATERAL_REFERENCE_NUMBER(String cOLLATERAL_REFERENCE_NUMBER) {
		COLLATERAL_REFERENCE_NUMBER = cOLLATERAL_REFERENCE_NUMBER;
	}

	public String getRESP_BATCH_ID() {
		return RESP_BATCH_ID;
	}

	public void setRESP_BATCH_ID(String rESP_BATCH_ID) {
		RESP_BATCH_ID = rESP_BATCH_ID;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

}
