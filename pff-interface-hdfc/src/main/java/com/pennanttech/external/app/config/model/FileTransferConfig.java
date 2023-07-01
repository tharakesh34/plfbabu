package com.pennanttech.external.app.config.model;

public class FileTransferConfig {
	private String ficName = "";
	private String accessKey = "";
	private String secretKey = "";
	private String hostName = "";
	private int port;
	private String privateKey = "";
	private String sseAlgo = "";
	private String sftpPrefix = "";
	private String sftpLocation = "";
	private String sftpBackupLocation = "";
	private String protocol = "";

	public String getFicName() {
		return ficName;
	}

	public void setFicName(String ficName) {
		this.ficName = ficName;
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

	public String getSftpLocation() {
		return sftpLocation;
	}

	public void setSftpLocation(String sftpLocation) {
		this.sftpLocation = sftpLocation;
	}

	public String getSftpBackupLocation() {
		return sftpBackupLocation;
	}

	public void setSftpBackupLocation(String sftpBackupLocation) {
		this.sftpBackupLocation = sftpBackupLocation;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
