package com.pennanttech.external.config;

import java.math.BigDecimal;

public class ExternalConfig {
	private String interfaceName = "";
	private BigDecimal NoOfRecords;
	private String fileLocation = "";
	private int hodlType;

	private String filePrepend;
	private String filePostpend;
	private String fileExtension;
	private String dateFormat;

	private String successIndicator;
	private String failIndicator;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public BigDecimal getNoOfRecords() {
		return NoOfRecords;
	}

	public void setNoOfRecords(BigDecimal noOfRecords) {
		NoOfRecords = noOfRecords;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public int getHodlType() {
		return hodlType;
	}

	public void setHodlType(int hodlType) {
		this.hodlType = hodlType;
	}

	public String getFilePrepend() {
		return filePrepend;
	}

	public void setFilePrepend(String filePrepend) {
		this.filePrepend = filePrepend;
	}

	public String getFilePostpend() {
		return filePostpend;
	}

	public void setFilePostpend(String filePostpend) {
		this.filePostpend = filePostpend;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getSuccessIndicator() {
		return successIndicator;
	}

	public void setSuccessIndicator(String successIndicator) {
		this.successIndicator = successIndicator;
	}

	public String getFailIndicator() {
		return failIndicator;
	}

	public void setFailIndicator(String failIndicator) {
		this.failIndicator = failIndicator;
	}

}
