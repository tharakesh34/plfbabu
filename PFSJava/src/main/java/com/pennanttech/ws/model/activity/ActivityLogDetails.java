package com.pennanttech.ws.model.activity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.activity.log.Activity;

@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityLogDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Activity> activityLogList = null;
	private WSReturnStatus returnStatus;

	public ActivityLogDetails() {
		super();
	}

	public List<Activity> getActivityLogList() {
		return activityLogList;
	}

	public void setActivityLogList(List<Activity> activityLogList) {
		this.activityLogList = activityLogList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
