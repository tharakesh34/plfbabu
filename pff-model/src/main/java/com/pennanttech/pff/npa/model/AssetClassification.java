package com.pennanttech.pff.npa.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pennanttech.pff.provision.model.NpaProvisionStage;

public class AssetClassification extends NpaProvisionStage {
	private static final long serialVersionUID = 1L;

	private Long npaClassID;
	private String npaClassCode;
	private String npaClassDesc;
	private String npaSubClassCode;
	private String npaSubClassDesc;
	private int npaPastDueDays;
	private Date npaPastDueDate;
	private boolean npaStage;
	private Long effNpaClassID;
	private String effNpaClassCode;
	private String effNpaClassDesc;
	private String effNpaSubClassCode;
	private String effNpaSubClassDesc;
	private int effNpaPastDueDays;
	private Date effNpaPastDueDate;
	private boolean effNpaStage;
	private Long linkedTranID;
	private Timestamp createdOn;
	private boolean npaChange;
	private boolean finIsActive;
	private Date npaTaggedDate;
	private Date npaUnTaggedDate;
	private Date classDate;
	private Map<String, AssetClassSetupHeader> assetClassSetup = new HashMap<>();

	public AssetClassification() {
		super();
	}

	public Long getNpaClassID() {
		return npaClassID;
	}

	public void setNpaClassID(Long npaClassID) {
		this.npaClassID = npaClassID;
	}

	public String getNpaClassCode() {
		return npaClassCode;
	}

	public void setNpaClassCode(String npaClassCode) {
		this.npaClassCode = npaClassCode;
	}

	public String getNpaClassDesc() {
		return npaClassDesc;
	}

	public void setNpaClassDesc(String npaClassDesc) {
		this.npaClassDesc = npaClassDesc;
	}

	public String getNpaSubClassCode() {
		return npaSubClassCode;
	}

	public void setNpaSubClassCode(String npaSubClassCode) {
		this.npaSubClassCode = npaSubClassCode;
	}

	public String getNpaSubClassDesc() {
		return npaSubClassDesc;
	}

	public void setNpaSubClassDesc(String npaSubClassDesc) {
		this.npaSubClassDesc = npaSubClassDesc;
	}

	public int getNpaPastDueDays() {
		return npaPastDueDays;
	}

	public void setNpaPastDueDays(int npaPastDueDays) {
		this.npaPastDueDays = npaPastDueDays;
	}

	public Date getNpaPastDueDate() {
		return npaPastDueDate;
	}

	public void setNpaPastDueDate(Date npaPastDueDate) {
		this.npaPastDueDate = npaPastDueDate;
	}

	public boolean isNpaStage() {
		return npaStage;
	}

	public void setNpaStage(boolean npaStage) {
		this.npaStage = npaStage;
	}

	public Long getEffNpaClassID() {
		return effNpaClassID;
	}

	public void setEffNpaClassID(Long effNpaClassID) {
		this.effNpaClassID = effNpaClassID;
	}

	public String getEffNpaClassCode() {
		return effNpaClassCode;
	}

	public void setEffNpaClassCode(String effNpaClassCode) {
		this.effNpaClassCode = effNpaClassCode;
	}

	public String getEffNpaClassDesc() {
		return effNpaClassDesc;
	}

	public void setEffNpaClassDesc(String effNpaClassDesc) {
		this.effNpaClassDesc = effNpaClassDesc;
	}

	public String getEffNpaSubClassCode() {
		return effNpaSubClassCode;
	}

	public void setEffNpaSubClassCode(String effNpaSubClassCode) {
		this.effNpaSubClassCode = effNpaSubClassCode;
	}

	public String getEffNpaSubClassDesc() {
		return effNpaSubClassDesc;
	}

	public void setEffNpaSubClassDesc(String effNpaSubClassDesc) {
		this.effNpaSubClassDesc = effNpaSubClassDesc;
	}

	public int getEffNpaPastDueDays() {
		return effNpaPastDueDays;
	}

	public void setEffNpaPastDueDays(int effNpaPastDueDays) {
		this.effNpaPastDueDays = effNpaPastDueDays;
	}

	public Date getEffNpaPastDueDate() {
		return effNpaPastDueDate;
	}

	public void setEffNpaPastDueDate(Date effNpaPastDueDate) {
		this.effNpaPastDueDate = effNpaPastDueDate;
	}

	public boolean isEffNpaStage() {
		return effNpaStage;
	}

	public void setEffNpaStage(boolean effNpaStage) {
		this.effNpaStage = effNpaStage;
	}

	public Long getLinkedTranID() {
		return linkedTranID;
	}

	public void setLinkedTranID(Long linkedTranID) {
		this.linkedTranID = linkedTranID;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public boolean isNpaChange() {
		return npaChange;
	}

	public void setNpaChange(boolean npaChange) {
		this.npaChange = npaChange;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public Date getNpaTaggedDate() {
		return npaTaggedDate;
	}

	public void setNpaTaggedDate(Date npaTaggedDate) {
		this.npaTaggedDate = npaTaggedDate;
	}

	public Date getNpaUnTaggedDate() {
		return npaUnTaggedDate;
	}

	public void setNpaUnTaggedDate(Date npaUnTaggedDate) {
		this.npaUnTaggedDate = npaUnTaggedDate;
	}

	public Date getClassDate() {
		return classDate;
	}

	public void setClassDate(Date classDate) {
		this.classDate = classDate;
	}

	public Map<String, AssetClassSetupHeader> getAssetClassSetup() {
		return assetClassSetup;
	}

	public void setAssetClassSetup(Map<String, AssetClassSetupHeader> assetClassSetup) {
		this.assetClassSetup = assetClassSetup;
	}

}
