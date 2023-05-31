package com.pennanttech.external.app.config.model;

public class FileInterfaceConfig {
	private String interfaceName = "";
	private long NoOfRecords;
	private String fileLocation = "";
	private int hodlType;
	private String filePrepend;
	private String filePostpend;
	private String fileExtension;
	private String dateFormat;
	private String successIndicator;
	private String failIndicator;

	private String fileBackupLocation = "";
	private String fileLocalBackupLocation = "";
	private String sftpBucketLocation = "";
	private String fileSftpLocation = "";
	private String isSftp = "";
	private String accessKey = "";
	private String secretKey = "";
	private String hostName = "";
	private int port;
	private String privateKey = "";
	private String sseAlgo = "";
	private String sftpPrefix = "";

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public long getNoOfRecords() {
		return NoOfRecords;
	}

	public void setNoOfRecords(long noOfRecords) {
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

	public String getFileBackupLocation() {
		return fileBackupLocation;
	}

	public void setFileBackupLocation(String fileBackupLocation) {
		this.fileBackupLocation = fileBackupLocation;
	}

	public String getFileLocalBackupLocation() {
		return fileLocalBackupLocation;
	}

	public void setFileLocalBackupLocation(String fileLocalBackupLocation) {
		this.fileLocalBackupLocation = fileLocalBackupLocation;
	}

	public String getSftpBucketLocation() {
		return sftpBucketLocation;
	}

	public void setSftpBucketLocation(String sftpBucketLocation) {
		this.sftpBucketLocation = sftpBucketLocation;
	}

	public String getFileSftpLocation() {
		return fileSftpLocation;
	}

	public void setFileSftpLocation(String fileSftpLocation) {
		this.fileSftpLocation = fileSftpLocation;
	}

	public String getIsSftp() {
		return isSftp;
	}

	public void setIsSftp(String isSftp) {
		this.isSftp = isSftp;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getSseAlgo() {
		return sseAlgo;
	}

	public void setSseAlgo(String sseAlgo) {
		this.sseAlgo = sseAlgo;
	}

	public String getSftpPrefix() {
		return sftpPrefix;
	}

	public void setSftpPrefix(String sftpPrefix) {
		this.sftpPrefix = sftpPrefix;
	}

}
