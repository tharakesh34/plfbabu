package com.pennant.coreinterface.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class EquationIdentityType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -726560151676499162L;

	public EquationIdentityType() {
		super();
	}
	
	private String identityType = null;
	private String identityDesc;
	private boolean newRecord = false;
	private String lovValue;

	public boolean isNew() {
		return isNewRecord();
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return identityType;
	}
	public void setId(String id) {
		this.identityType = id;
	}

	public String getIdentityType() {
		return identityType;
	}
	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getIdentityDesc() {
		return identityDesc;
	}
	public void setIdentityDesc(String identityDesc) {
		this.identityDesc = identityDesc;
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
