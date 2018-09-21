package com.pennant.backend.model.Notifications;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class SystemNotificationExecution implements Serializable {
	private static final long serialVersionUID = -1472467289111692722L;
	
	private long id;
	private long notificationId;
	private long instanceId;
	private Date createTime;
	private Timestamp startTime;
	private long totalCount;
	private long sucessCount;
	private long failedCount;
	private String status;
	private Timestamp endTime;

	public long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getSucessCount() {
		return sucessCount;
	}

	public void setSucessCount(long sucessCount) {
		this.sucessCount = sucessCount;
	}

	public long getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(long failedCount) {
		this.failedCount = failedCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
