package com.pennant.backend.model;

import java.io.Serializable;
import java.util.Date;

public class ExecutionStatus implements Serializable {
    private static final long serialVersionUID = 4636563297252539705L;
	private String executionName = "";
	private int actualCount;
	private int processedCount;
	private Date startTime;
	private Date endTime;
	private String info;
	private String wait;
	private Date valueDate;
	private String status = "STARTING";

	public ExecutionStatus() {
		
	}
	
	public String getExecutionName() {
		return executionName;
	}

	public void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	public int getActualCount() {
		return actualCount;
	}

	public void setActualCount(int actualCount) {
		this.actualCount = actualCount;
	}

	public int getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}

	public Date getStartTime() {
    	return startTime;
    }

	public void setStartTime(Date startTime) {
    	this.startTime = startTime;
    }

	public Date getEndTime() {
		if ("EXECUTING".equals(this.status)) {
			return new Date(System.currentTimeMillis());
		}
		return endTime;
	}

	public void setEndTime(Date endTime) {
    	this.endTime = endTime;
    }

	public String getStatus() {
    	return status;
    }

	public void setStatus(String status) {
    	this.status = status;
    }

	public void setValueDate(Date valueDate) {
	    this.valueDate = valueDate;
    }

	public Date getValueDate() {
	    return valueDate;
    }

	public void setInfo(String info) {
	    this.info = info;
    }

	public String getInfo() {
	    return info;
    }

	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}
	
	

}
