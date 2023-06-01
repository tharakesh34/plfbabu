package com.pennant.pff.model.paymentmethodupload;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class PaymentMethodUploadHeader {
	private Long userId;
	private File file;
	private Media media;
	private String userBranch;
	private Long id;
	private String batchRef;
	private int totalRecords;
	private int sucessRecords;
	private int failureRecords;
	private String status;
	private List<PaymentMethodUpload> paymentmethodUpload = new ArrayList<>();
	private DataEngineStatus deStatus = new DataEngineStatus();
	private Date appDate;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
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

	public List<PaymentMethodUpload> getPaymentmethodUpload() {
		return paymentmethodUpload;
	}

	public void setPaymentmethodUpload(List<PaymentMethodUpload> paymentmethodUpload) {
		this.paymentmethodUpload = paymentmethodUpload;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

}
