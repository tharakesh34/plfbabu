package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.util.Date;

public class InterfaceShareHolder implements Serializable {

	private static final long serialVersionUID = -6692665473168415871L;
	
	private String shareHolderIDType;
	private String shareHolderIDRef;
	private String shareHolderPerc;
	private String shareHolderRole;
	private String shareHolderName;
	private String shareHolderNation;
	private String shareHolderRisk;
	private Date shareHolderDOB;
	private String recordType;

	public InterfaceShareHolder() {
		
	}
	
	public String getShareHolderIDType() {
		return shareHolderIDType;
	}

	public void setShareHolderIDType(String shareHolderIDType) {
		this.shareHolderIDType = shareHolderIDType;
	}

	public String getShareHolderIDRef() {
		return shareHolderIDRef;
	}

	public void setShareHolderIDRef(String shareHolderIDRef) {
		this.shareHolderIDRef = shareHolderIDRef;
	}

	public String getShareHolderPerc() {
		return shareHolderPerc;
	}

	public void setShareHolderPerc(String shareHolderPerc) {
		this.shareHolderPerc = shareHolderPerc;
	}

	public String getShareHolderRole() {
		return shareHolderRole;
	}

	public void setShareHolderRole(String shareHolderRole) {
		this.shareHolderRole = shareHolderRole;
	}

	public String getShareHolderName() {
		return shareHolderName;
	}

	public void setShareHolderName(String shareHolderName) {
		this.shareHolderName = shareHolderName;
	}

	public String getShareHolderNation() {
		return shareHolderNation;
	}

	public void setShareHolderNation(String shareHolderNation) {
		this.shareHolderNation = shareHolderNation;
	}

	public String getShareHolderRisk() {
		return shareHolderRisk;
	}

	public void setShareHolderRisk(String shareHolderRisk) {
		this.shareHolderRisk = shareHolderRisk;
	}

	public Date getShareHolderDOB() {
		return shareHolderDOB;
	}

	public void setShareHolderDOB(Date shareHolderDOB) {
		this.shareHolderDOB = shareHolderDOB;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

}
