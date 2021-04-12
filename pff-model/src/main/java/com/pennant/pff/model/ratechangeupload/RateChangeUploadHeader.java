package com.pennant.pff.model.ratechangeupload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class RateChangeUploadHeader {

	private long userId;
	private File file;
	private Media media;
	private String userBranch;
	private Long id;
	private String batchRef;
	private int totalRecords;
	private int sucessRecords;
	private int failureRecords;
	private String status;
	private String entityCode;
	private String fileName;
	private String lovValue;

	private List<RateChangeUpload> rateChangeUpload = new ArrayList<>();
	private DataEngineStatus deStatus = new DataEngineStatus();
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	public String getUserBranch() {
		return userBranch;
	}
	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBatchRef() {
		return batchRef;
	}
	public void setBatchRef(String batchRef) {
		this.batchRef = batchRef;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getSucessRecords() {
		return sucessRecords;
	}
	public void setSucessRecords(int sucessRecords) {
		this.sucessRecords = sucessRecords;
	}
	public int getFailureRecords() {
		return failureRecords;
	}
	public void setFailureRecords(int failureRecords) {
		this.failureRecords = failureRecords;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public DataEngineStatus getDeStatus() {
		return deStatus;
	}
	public void setDeStatus(DataEngineStatus deStatus) {
		this.deStatus = deStatus;
	}
	public List<RateChangeUpload> getRateChangeUpload() {
		return rateChangeUpload;
	}
	public void setRateChangeUpload(List<RateChangeUpload> rateChangeUpload) {
		this.rateChangeUpload = rateChangeUpload;
	}
	public String getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

}
