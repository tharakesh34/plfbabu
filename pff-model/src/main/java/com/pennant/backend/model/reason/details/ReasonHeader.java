package com.pennant.backend.model.reason.details;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.Entity;

public class ReasonHeader implements Serializable, Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String module;
	private String reference;
	private String remarks;
	private String roleCode;
	private String activity;
	private long toUser;
	private Timestamp logTime;
	private List<ReasonDetails> detailsList = new ArrayList<ReasonDetails>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public long getToUser() {
		return toUser;
	}

	public void setToUser(long toUser) {
		this.toUser = toUser;
	}

	public Timestamp getLogTime() {
		return logTime;
	}

	public void setLogTime(Timestamp logTime) {
		this.logTime = logTime;
	}

	public List<ReasonDetails> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<ReasonDetails> detailsList) {
		this.detailsList = detailsList;
	}

	@Override
	public boolean isNew() {
		return false;
	}

}
