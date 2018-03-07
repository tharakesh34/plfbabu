package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Caste extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3568541212131628117L;
	
	private long casteId = Long.MIN_VALUE;
	private String casteCode;
	private String casteDesc;
	private boolean casteIsActive;
	private boolean newRecord;
	private String lovValue;
	private Caste befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Caste() {
		super();
	}

	public Caste(long casteId) {
		super();
		this.setId(casteId);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return this.casteId;
	}
	public void setId (long casteId) {
		this.casteId = casteId;
	}
	
	public String getCasteCode() {
		return casteCode;
	}
	public void setCasteCode(String casteCode) {
		this.casteCode = casteCode;
	}
	
	public String getCasteDesc() {
		return casteDesc;
	}
	public void setCasteDesc(String casteDesc) {
		this.casteDesc = casteDesc;
	}
	
	public boolean isCasteIsActive() {
		return casteIsActive;
	}
	public void setCasteIsActive(boolean casteIsActive) {
		this.casteIsActive = casteIsActive;
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

	public Caste getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Caste beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getCasteId() {
		return casteId;
	}

	public void setCasteId(long casteId) {
		this.casteId = casteId;
	}

}
