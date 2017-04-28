package com.pennanttech.interfacebajaj.model;

public class FileDownlaod {
	
	private int    Id;
	private String finReference;
	private String partnerBankCode;
	private String name;
	private String fileName;
	private String fileLocation;
	private String partnerBankName;
	private int    alwFileDownload;
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

	public int getAlwFileDownload() {
		return alwFileDownload;
	}

	public void setAlwFileDownload(int alwFileDownload) {
		this.alwFileDownload = alwFileDownload;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

}
