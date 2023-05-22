package com.pennanttech.model.lien;

import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class LienDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long iD;
	private long lienID = Long.MIN_VALUE;
	private long headerID;
	private String source;
	private String reference;
	private String accountNumber;
	private String marking;
	private Date markingDate;
	private String markingReason;
	private String demarking;
	private Date demarkingDate;
	private String demarkingReason;
	private String lienReference;
	private boolean lienStatus;
	private String interfaceStatus;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;

	public LienDetails() {
		super();
	}

	public long getiD() {
		return iD;
	}

	public void setiD(long iD) {
		this.iD = iD;
	}

	public long getLienID() {
		return lienID;
	}

	public void setLienID(long lienID) {
		this.lienID = lienID;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public Date getMarkingDate() {
		return markingDate;
	}

	public void setMarkingDate(Date markingDate) {
		this.markingDate = markingDate;
	}

	public String getMarkingReason() {
		return markingReason;
	}

	public void setMarkingReason(String markingReason) {
		this.markingReason = markingReason;
	}

	public String getDemarking() {
		return demarking;
	}

	public void setDemarking(String demarking) {
		this.demarking = demarking;
	}

	public Date getDemarkingDate() {
		return demarkingDate;
	}

	public void setDemarkingDate(Date demarkingDate) {
		this.demarkingDate = demarkingDate;
	}

	public String getDemarkingReason() {
		return demarkingReason;
	}

	public void setDemarkingReason(String demarkingReason) {
		this.demarkingReason = demarkingReason;
	}

	public String getLienReference() {
		return lienReference;
	}

	public void setLienReference(String lienReference) {
		this.lienReference = lienReference;
	}

	public boolean isLienStatus() {
		return lienStatus;
	}

	public void setLienStatus(boolean lienStatus) {
		this.lienStatus = lienStatus;
	}

	public String getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(String interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
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

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

}
