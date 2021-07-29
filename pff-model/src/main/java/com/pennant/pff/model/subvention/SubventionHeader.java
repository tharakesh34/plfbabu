package com.pennant.pff.model.subvention;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineStatus;

@XmlAccessorType(XmlAccessType.NONE)
public class SubventionHeader {

	private long userId;
	private File file;
	private Media media;
	private String userBranch;
	private Long id;
	@XmlElement(name = "batchReference")
	private String batchRef;
	@XmlElement
	private String entityCode;
	private int totalRecords;
	private int sucessRecords;
	private int failureRecords;
	private String status;
	@XmlElement
	private List<Subvention> subventions = new ArrayList<>();
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

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
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

	public List<Subvention> getSubventions() {
		return subventions;
	}

	public void setSubventions(List<Subvention> subventions) {
		this.subventions = subventions;
	}

	public DataEngineStatus getDeStatus() {
		return deStatus;
	}

	public void setDeStatus(DataEngineStatus deStatus) {
		this.deStatus = deStatus;
	}
}
