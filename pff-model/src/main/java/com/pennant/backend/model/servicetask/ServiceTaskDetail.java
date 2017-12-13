package com.pennant.backend.model.servicetask;

import java.io.Serializable;
import java.sql.Timestamp;

import com.pennant.backend.model.Entity;

public class ServiceTaskDetail implements Serializable, Entity {

	private static final long	serialVersionUID	= 5808007043258253326L;

	private long 				taskExecutionId = Long.MIN_VALUE;
	private String				serviceModule;
	private String				reference;
	private String				serviceTaskId;
	private String				serviceTaskName;
	private long				userId;
	private Timestamp			executedTime;
	private String				status;
	private String				remarks;

	public ServiceTaskDetail() {
		super();
	}

	public String getServiceModule() {
		return serviceModule;
	}

	public void setServiceModule(String serviceModule) {
		this.serviceModule = serviceModule;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getServiceTaskId() {
		return serviceTaskId;
	}

	public void setServiceTaskId(String serviceTaskId) {
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

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return taskExecutionId;
	}

	@Override
	public void setId(long id) {
		this.taskExecutionId = id;
	}
	
	public long getTaskExecutionId() {
		return taskExecutionId;
	}

	public void setTaskExecutionId(long taskExecutionId) {
		this.taskExecutionId = taskExecutionId;
	}
}
