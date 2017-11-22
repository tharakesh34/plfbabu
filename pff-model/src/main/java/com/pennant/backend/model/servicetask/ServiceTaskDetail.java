package com.pennant.backend.model.servicetask;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServiceTaskDetail implements Serializable {

	private static final long	serialVersionUID	= 5808007043258253326L;

	private String				module;
	private String				reference;
	private int					serviceTaskId;
	private String				serviceTaskName;
	private long				userId;
	private Timestamp			executedTime;
	private String				status;
	private String				remarks;

	public ServiceTaskDetail() {
		super();
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

	public int getServiceTaskId() {
		return serviceTaskId;
	}

	public void setServiceTaskId(int serviceTaskId) {
		this.serviceTaskId = serviceTaskId;
	}

	public String getServiceTaskName() {
		return serviceTaskName;
	}

	public void setServiceTaskName(String serviceTaskName) {
		this.serviceTaskName = serviceTaskName;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Timestamp getExecutedTime() {
		return executedTime;
	}

	public void setExecutedTime(Timestamp executedTime) {
		this.executedTime = executedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
