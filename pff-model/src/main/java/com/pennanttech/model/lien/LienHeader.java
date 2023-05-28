package com.pennanttech.model.lien;

import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class LienHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long lienID = Long.MIN_VALUE;
	private String source;
	private String reference;
	private String accountNumber;
	private String marking;
	private Date markingDate;
	private String demarking;
	private Date demarkingDate;
	private String lienReference;
	private boolean lienStatus;
	private String interfaceStatus;
	private String interfaceRemarks;
	private long referenceLienID;

	public LienHeader() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLienID() {
		return lienID;
	}

	public void setLienID(long lienID) {
		this.lienID = lienID;
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

	public String getInterfaceRemarks() {
		return interfaceRemarks;
	}

	public void setInterfaceRemarks(String interfaceRemarks) {
		this.interfaceRemarks = interfaceRemarks;
	}

	public long getReferenceLienID() {
		return referenceLienID;
	}

	public void setReferenceLienID(long referenceLienID) {
		this.referenceLienID = referenceLienID;
	}

}
