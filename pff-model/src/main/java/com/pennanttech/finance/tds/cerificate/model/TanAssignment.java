package com.pennanttech.finance.tds.cerificate.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class TanAssignment extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6234931333270161797L;

	private long id;
	private long custID;
	private String finReference;
	private long tanID;
	private String custCIF;

	private LoggedInUser userDetails;
	private String lovValue;
	private TanAssignment befImage;
	private boolean newRecord = false;
	private int keyValue = 0;

	private TanDetail tanDetail;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("keyValue");
		excludeFields.add("tanDetail");
		excludeFields.add("custCIF");
		excludeFields.add("lovValue");
		return excludeFields;
	}

	public TanAssignment() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getTanID() {
		return tanID;
	}

	public void setTanID(long tanID) {
		this.tanID = tanID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public TanAssignment getBefImage() {
		return befImage;
	}

	public void setBefImage(TanAssignment befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public TanDetail getTanDetail() {
		return tanDetail;
	}

	public void setTanDetail(TanDetail tanDetail) {
		this.tanDetail = tanDetail;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}