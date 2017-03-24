package com.pennant.pff.channelsinterface;

public class MQProperties {
	private String hostname;
	private int port;
	private String channel;
	private String queueManagerName;
	private String queueName;
	private String replyQManagerName;
	private String replyQueueName;
	private String errorQueueName;
	private boolean sslRequired;
	private String sslTrustStore;
	private String sslTrustStorePassword;
	private String sslKeyStore;
	private String sslKeyStorePassword;
	private String sslCipherSuite;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getQueueManagerName() {
		return queueManagerName;
	}

	public void setQueueManagerName(String queueManagerName) {
		this.queueManagerName = queueManagerName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getReplyQueueName() {
		return replyQueueName;
	}

	public void setReplyQueueName(String replyQueueName) {
		this.replyQueueName = replyQueueName;
	}

	public String getErrorQueueName() {
		return errorQueueName;
	}

	public void setErrorQueueName(String errorQueueName) {
		this.errorQueueName = errorQueueName;
	}

	public String getReplyQManagerName() {
		return replyQManagerName;
	}

	public void setReplyQManagerName(String replyQManagerName) {
		this.replyQManagerName = replyQManagerName;
	}

	public boolean isSslRequired() {
		return sslRequired;
	}

	public void setSslRequired(boolean sslRequired) {
		this.sslRequired = sslRequired;
	}

	public String getSslTrustStore() {
		return sslTrustStore;
	}

	public void setSslTrustStore(String sslTrustStore) {
		this.sslTrustStore = sslTrustStore;
	}

	public String getSslTrustStorePassword() {
		return sslTrustStorePassword;
	}

	public void setSslTrustStorePassword(String sslTrustStorePassword) {
		this.sslTrustStorePassword = sslTrustStorePassword;
	}

	public String getSslKeyStore() {
		return sslKeyStore;
	}

	public void setSslKeyStore(String sslKeyStore) {
		this.sslKeyStore = sslKeyStore;
	}

	public String getSslKeyStorePassword() {
		return sslKeyStorePassword;
	}

	public void setSslKeyStorePassword(String sslKeyStorePassword) {
		this.sslKeyStorePassword = sslKeyStorePassword;
	}

	public String getSslCipherSuite() {
		return sslCipherSuite;
	}

	public void setSslCipherSuite(String sslCipherSuite) {
		this.sslCipherSuite = sslCipherSuite;
	}
}
