package com.pennanttech.model.lien;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class LienUpload extends UploadDetails {
	private static final long serialVersionUID = 5L;

	private long lienID = Long.MIN_VALUE;
	private String marking;
	private Date markingDate;
	private String markingReason;
	private String demarking;
	private String demarkingReason;
	private Date demarkingDate;
	private String lienReference;
	private Boolean lienstatus;
	private String source;
	private String accNumber;
	private String action;
	private String remarks;
	private String interfaceStatus;
	private transient FinanceMain financeMain;

	public LienUpload() {
		super();
	}

	public long getLienID() {
		return lienID;
	}

	public void setLienID(long lienID) {
		this.lienID = lienID;
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

	public String getDemarkingReason() {
		return demarkingReason;
	}

	public void setDemarkingReason(String demarkingReason) {
		this.demarkingReason = demarkingReason;
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

	public Boolean getLienstatus() {
		return lienstatus;
	}

	public void setLienstatus(Boolean lienstatus) {
		this.lienstatus = lienstatus;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(String interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

}