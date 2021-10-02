package com.pennanttech.pff.model.mandate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MandateData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Long> mandateIdList;
	private long process_Id;
	private Date fromDate;
	private Date toDate;
	private long userId;
	private String userName;
	private String selectedBranchs;
	private String entity;
	private String type;
	private long partnerBankId = Long.MIN_VALUE;
	private String remarks;

	public MandateData() {
		super();
	}

	public long getProcess_Id() {
		return process_Id;
	}

	public void setProcess_Id(long process_Id) {
		this.process_Id = process_Id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSelectedBranchs() {
		return selectedBranchs;
	}

	public void setSelectedBranchs(String selectedBranchs) {
		this.selectedBranchs = selectedBranchs;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public List<Long> getMandateIdList() {
		return mandateIdList;
	}

	public void setMandateIdList(List<Long> mandateIdList) {
		this.mandateIdList = mandateIdList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
