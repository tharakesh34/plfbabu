package com.pennanttech.external.app.config.model;

public class FileInterfaceConfig {
	private String interfaceName = "";
	private long noOfRecords;
	private String fileLocation = "";
	private int hodlType;
	private String filePrepend;
	private String filePostpend;
	private String fileExtension;
	private String dateFormat;
	private String successIndicator;
	private String failIndicator;
	private String fileLocalBackupLocation = "";
	private String fileTransfer = "";
	private String ficNames = "";

	private FileTransferConfig fileTransferConfig = new FileTransferConfig();

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public long getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(long noOfRecords) {
		this.noOfRecords = noOfRecords;
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

	public String getFileLocalBackupLocation() {
		return fileLocalBackupLocation;
	}

	public void setFileLocalBackupLocation(String fileLocalBackupLocation) {
		this.fileLocalBackupLocation = fileLocalBackupLocation;
	}

	public String getFileTransfer() {
		return fileTransfer;
	}

	public void setFileTransfer(String fileTransfer) {
		this.fileTransfer = fileTransfer;
	}

	public String getFicNames() {
		return ficNames;
	}

	public void setFicNames(String ficNames) {
		this.ficNames = ficNames;
	}

	public FileTransferConfig getFileTransferConfig() {
		return fileTransferConfig;
	}

	public void setFileTransferConfig(FileTransferConfig fileTransferConfig) {
		this.fileTransferConfig = fileTransferConfig;
	}

}
