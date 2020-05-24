package com.pennanttech.pff.model;

import java.io.Serializable;
import java.util.Date;

public class DownloadHeader implements Serializable {
	private static final long serialVersionUID = -3987420734399844625L;

	private long headerId;
	private String downloadCode;
	private String downloadApp;
	private String processType;
	private String appStatus;
	private long appCount;
	private Date appStartTime;
	private Date appEndTime;
	private String portalStatus;
	private long portalcount;
	private Date portalStartTime;
	private Date portalEndTime;

	public DownloadHeader() {
		super();
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getDownloadCode() {
		return downloadCode;
	}

	public void setDownloadCode(String downloadCode) {
		this.downloadCode = downloadCode;
	}

	public String getDownloadApp() {
		return downloadApp;
	}

	public void setDownloadApp(String downloadApp) {
		this.downloadApp = downloadApp;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public long getAppCount() {
		return appCount;
	}

	public void setAppCount(long appCount) {
		this.appCount = appCount;
	}

	public Date getAppStartTime() {
		return appStartTime;
	}

	public void setAppStartTime(Date appStartTime) {
		this.appStartTime = appStartTime;
	}

	public Date getAppEndTime() {
		return appEndTime;
	}

	public void setAppEndTime(Date appEndTime) {
		this.appEndTime = appEndTime;
	}

	public String getPortalStatus() {
		return portalStatus;
	}

	public void setPortalStatus(String portalStatus) {
		this.portalStatus = portalStatus;
	}

	public long getPortalcount() {
		return portalcount;
	}

	public void setPortalcount(long portalcount) {
		this.portalcount = portalcount;
	}

	public Date getPortalStartTime() {
		return portalStartTime;
	}

	public void setPortalStartTime(Date portalStartTime) {
		this.portalStartTime = portalStartTime;
	}

	public Date getPortalEndTime() {
		return portalEndTime;
	}

	public void setPortalEndTime(Date portalEndTime) {
		this.portalEndTime = portalEndTime;
	}
}
