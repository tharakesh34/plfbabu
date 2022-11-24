package com.pennant.pff.presentment.model;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class RePresentmentUploadDetail extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private Date dueDate;
	private String strDueDate;
	private String acBounce;
	private FinanceMain fm;

	public RePresentmentUploadDetail() {
		super();
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getStrDueDate() {
		return strDueDate;
	}

	public void setStrDueDate(String strDueDate) {
		this.strDueDate = strDueDate;
	}

	public String getAcBounce() {
		return acBounce;
	}

	public void setAcBounce(String acBounce) {
		this.acBounce = acBounce;
	}

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

}
