package com.pennant.coreinterface.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;


/**
 * Model class for the <b>TransactionCode table</b>.<br>
 *
 */
public class EquationTransactionCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -2481059822866956040L;
	
	public EquationTransactionCode() {
		super();
	}
	
	private String tranCode = null;
	private String tranDesc;
	private String tranType;
	private boolean tranIsActive;
	private boolean newRecord=false;
	private String lovValue;

	public boolean isNew() {
		return isNewRecord();
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return tranCode;
	}
	public void setId (String id) {
		this.tranCode = id;
	}
	
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	
	public String getTranDesc() {
		return tranDesc;
	}
	public void setTranDesc(String tranDesc) {
		this.tranDesc = tranDesc;
	}
	
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	
	public boolean isTranIsActive() {
		return tranIsActive;
	}
	public void setTranIsActive(boolean tranIsActive) {
		this.tranIsActive = tranIsActive;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}
}
