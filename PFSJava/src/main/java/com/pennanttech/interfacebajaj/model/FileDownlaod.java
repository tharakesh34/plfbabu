package com.pennanttech.interfacebajaj.model;

public class FileDownlaod {
	
	private String name;
	private String fileName;
	private String fileLocation;
	private String partnerBankName;
	private int    allowFileDownload;
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public int getAllowFileDownload() {
		return allowFileDownload;
	}

	public void setAllowFileDownload(int allowFileDownload) {
		this.allowFileDownload = allowFileDownload;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
