package com.pennant.pff.upload.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FileUploadHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private String entityCode;
	private String type;
	private String fileName;
	private long executionID;
	private int totalRecords;
	private int successRecords;
	private int failureRecords;
	private int progress;
	private String remarks;
	private Long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;

	private FileUploadHeader befImage;
	private LoggedInUser userDetails;
	private transient Media media;
	private transient Workbook workBook;
	private File file;
	private Date appDate;
	private DataEngineStatus deStatus = new DataEngineStatus();
	private String stage;
	private List<DataEngineLog> dataEngineLog = new ArrayList<>();
	private boolean downloadReq;

	public FileUploadHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("media");
		excludeFields.add("workBook");
		excludeFields.add("file");
		excludeFields.add("appDate");
		excludeFields.add("deStatus");
		excludeFields.add("stage");
		excludeFields.add("downloadReq");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getExecutionID() {
		return executionID;
	}

	public void setExecutionID(long executionID) {
		this.executionID = executionID;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}

	public int getFailureRecords() {
		return failureRecords;
	}

	public void setFailureRecords(int failureRecords) {
		this.failureRecords = failureRecords;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public FileUploadHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FileUploadHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public Workbook getWorkBook() {
		return workBook;
	}

	public void setWorkBook(Workbook workBook) {
		this.workBook = workBook;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public DataEngineStatus getDeStatus() {
		return deStatus;
	}

	public void setDeStatus(DataEngineStatus deStatus) {
		this.deStatus = deStatus;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public List<DataEngineLog> getDataEngineLog() {
		return dataEngineLog;
	}

	public void setDataEngineLog(List<DataEngineLog> dataEngineLog) {
		this.dataEngineLog = dataEngineLog;
	}

	public boolean isDownloadReq() {
		return downloadReq;
	}

	public void setDownloadReq(boolean downloadReq) {
		this.downloadReq = downloadReq;
	}

}
