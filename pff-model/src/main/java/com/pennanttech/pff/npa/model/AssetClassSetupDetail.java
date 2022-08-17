package com.pennanttech.pff.npa.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class AssetClassSetupDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long setupID;
	private int dpdMin;
	private int dpdMax;
	private long classID;
	private String classCode;
	private String classDescription;
	private long subClassID;
	private String subClassCode;
	private String subClassDescription;
	private boolean npaStage;
	private int npaAge;
	private boolean newRecord;
	private AssetClassSetupDetail befImage;
	private long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;
	private int keyvalue = 0;

	public AssetClassSetupDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("classCode");
		excludeFields.add("classDescription");
		excludeFields.add("subClassCode");
		excludeFields.add("subClassDescription");
		excludeFields.add("keyvalue");
		return excludeFields;
	}

	public AssetClassSetupDetail copyEntity() {
		AssetClassSetupDetail entity = new AssetClassSetupDetail();
		entity.setId(this.id);
		entity.setSetupID(this.setupID);
		entity.setDpdMin(this.dpdMin);
		entity.setDpdMax(this.dpdMax);
		entity.setClassID(this.classID);
		entity.setClassCode(this.classCode);
		entity.setClassDescription(this.classDescription);
		entity.setSubClassID(this.subClassID);
		entity.setSubClassCode(this.subClassCode);
		entity.setSubClassDescription(this.subClassDescription);
		entity.setNpaStage(this.npaStage);
		entity.setNpaAge(this.npaAge);
		entity.setNewRecord(this.newRecord);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());

		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());

		entity.setCreatedBy(this.createdBy);
		entity.setCreatedOn(this.createdOn);
		entity.setApprovedBy(this.approvedBy);
		entity.setApprovedOn(this.approvedOn);
		entity.setKeyvalue(this.keyvalue);

		return entity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSetupID() {
		return setupID;
	}

	public void setSetupID(long setupID) {
		this.setupID = setupID;
	}

	public int getDpdMin() {
		return dpdMin;
	}

	public void setDpdMin(int dpdMin) {
		this.dpdMin = dpdMin;
	}

	public int getDpdMax() {
		return dpdMax;
	}

	public void setDpdMax(int dpdMax) {
		this.dpdMax = dpdMax;
	}

	public long getClassID() {
		return classID;
	}

	public void setClassID(long classID) {
		this.classID = classID;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getClassDescription() {
		return classDescription;
	}

	public void setClassDescription(String classDescription) {
		this.classDescription = classDescription;
	}

	public long getSubClassID() {
		return subClassID;
	}

	public void setSubClassID(long subClassID) {
		this.subClassID = subClassID;
	}

	public String getSubClassCode() {
		return subClassCode;
	}

	public void setSubClassCode(String subClassCode) {
		this.subClassCode = subClassCode;
	}

	public String getSubClassDescription() {
		return subClassDescription;
	}

	public void setSubClassDescription(String subClassDescription) {
		this.subClassDescription = subClassDescription;
	}

	public boolean isNpaStage() {
		return npaStage;
	}

	public void setNpaStage(boolean npaStage) {
		this.npaStage = npaStage;
	}

	public int getNpaAge() {
		return npaAge;
	}

	public void setNpaAge(int npaAge) {
		this.npaAge = npaAge;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public AssetClassSetupDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(AssetClassSetupDetail befImage) {
		this.befImage = befImage;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public int getKeyvalue() {
		return keyvalue;
	}

	public void setKeyvalue(int keyvalue) {
		this.keyvalue = keyvalue;
	}

}
