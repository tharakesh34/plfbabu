package com.pennant.pff.channelsinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class MessageQueue {
	private final static Logger logger = LoggerFactory
			.getLogger(MessageQueue.class);
	protected static MQProperties mqProperties;
	protected MQQueueManager manager;
	protected MQQueueManager resManager;
	protected MQQueue queue;
	protected MQGetMessageOptions getOptions;
	protected static MQPutMessageOptions putOptions;
	protected String queueManagerName;
	protected String queueName;
	protected String errorQueueName;

	@SuppressWarnings("unchecked")
	protected void setMQEnvironment() {
		logger.debug("Entering");

		// Set the system properties that are required for MQ secure connection.
		if (mqProperties.isSslRequired()) {
			System.setProperty("javax.net.ssl.trustStore",
					mqProperties.getSslTrustStore());
			System.setProperty("javax.net.ssl.keyStore",
					mqProperties.getSslKeyStore());
			System.setProperty("javax.net.ssl.keyStorePassword",
					mqProperties.getSslKeyStorePassword());
			System.setProperty("javax.net.ssl.trustStorePassword",
					mqProperties.getSslTrustStorePassword());
		}

		// Set the MQ environment.
		MQEnvironment.hostname = mqProperties.getHostname();
		MQEnvironment.port = mqProperties.getPort();
		MQEnvironment.channel = mqProperties.getChannel();
		MQEnvironment.properties.put(CMQC.TRANSPORT_PROPERTY,
				CMQC.TRANSPORT_MQSERIES_CLIENT);
		if (mqProperties.isSslRequired()) {
			MQEnvironment.properties.put(CMQC.SSL_CIPHER_SUITE_PROPERTY,
					mqProperties.getSslCipherSuite());
			MQEnvironment.properties.put(CMQC.SSL_FIPS_REQUIRED_PROPERTY, true);
		}

		this.queueManagerName = mqProperties.getQueueManagerName();
		this.queueName = mqProperties.getQueueName();
		this.errorQueueName = mqProperties.getErrorQueueName();

		getOptions = new MQGetMessageOptions();
		getOptions.options += CMQC.MQGMO_SYNCPOINT + CMQC.MQGMO_WAIT;
		getOptions.waitInterval = CMQC.MQWI_UNLIMITED;

		putOptions = new MQPutMessageOptions();

		logger.debug("Leaving");
	}

	protected MQMessage getMessage(String queueManagerName,
			String accessQueueName) {
		MQMessage message = new MQMessage();

		int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED;

		try {
			if (manager == null || !manager.isConnected()) {
				manager = new MQQueueManager(queueManagerName);
			}

			queue = manager.accessQueue(accessQueueName, openOptions);
			queue.get(message, getOptions);

			logger.info("Message ID: {}", message.messageId);
			manager.commit();
		} catch (Exception e) {
			try {
				if (manager != null) {
					manager.backout();
				}
			} catch (MQException e1) {
				logger.error("MQException:" + e.getMessage());
			}
			message = null;

			logger.error("Exception: {}", e);
		} finally {
			closeQueue();
		}

		return message;
	}

	protected void putMessage(String content, MQMessage reqMessage) {
		logger.debug("Entering Placing response ");
		MQMessage message = new MQMessage();
		int openOptions = CMQC.MQOO_OUTPUT | CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue mqQueueReply = null;

		try {
			logger.debug("Response Queue Manager Connecting :: ");
			System.out.println("Request Manager is connected :: "
					+ manager.isConnected());
			mqQueueReply = manager
					.accessQueue(reqMessage.replyToQueueName, openOptions,
							reqMessage.replyToQueueManagerName, null, null);

			logger.debug("Response Queue Manager Connected :: ");

			message.format = CMQC.MQFMT_STRING;

			// Set the correlation ID as input message id
			message.correlationId = reqMessage.messageId;

			message.writeString(content);

			message.replyToQueueName = reqMessage.replyToQueueName;

			message.replyToQueueManagerName = reqMessage.replyToQueueManagerName;

			logger.debug("Response Message ID :: " + reqMessage.messageId);

			mqQueueReply.put(message, putOptions);

		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			message = null;
			// Close Queue
			try {
				if (mqQueueReply != null) {
					mqQueueReply.close();
				}
			} catch (MQException e) {
				logger.error("MQException:" + e.getMessage());
			}

			// Close Manager
			try {
				if (resManager != null) {
					resManager.disconnect();
				}
			} catch (MQException e) {
				logger.error("MQException:" + e.getMessage());
			} finally {
				resManager = null;
			}
		}
	}

	/**
	 * Method for put dummy message into the queue to stop processing
	 * 
	 */
	protected static void putDummyMessage() {
		logger.info("Placing dummy message into the queue...");
		String content = "<HB_EAI_REQUEST><HB_EAI_HEADER><MsgFormat>PFF_SERVICE_STOP</MsgFormat><MsgVersion/><RequestorId/>"
				+ "<RequestorChannelId/><RequestorUserId/><RequestorLanguage/><RequestorSecurityInfo/><EaiReference/>"
				+ "<ReturnCode/></HB_EAI_HEADER></HB_EAI_REQUEST>";

		int openOptions = CMQC.MQOO_OUTPUT | CMQC.MQOO_FAIL_IF_QUIESCING;
		MQMessage message = null;
		MQQueueManager manager = null;
		MQQueue queue = null;

		try {
			message = new MQMessage();
			manager = new MQQueueManager(mqProperties.getQueueManagerName());
			queue = manager.accessQueue(mqProperties.getQueueName(),
					openOptions);

			message.format = CMQC.MQFMT_STRING;
			message.writeString(content);
			queue.put(message, putOptions);

			manager.commit();
			logger.info("Dummy Message: " + content);
			logger.info("Placed dummy message in the queue.");
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			try {
				if (queue != null) {
					queue.close();
				}
			} catch (MQException e) {
				logger.error("MQException: {}", e);
			} finally {
				queue = null;
			}

			try {
				if (manager != null) {
					manager.disconnect();
				}
			} catch (MQException e) {
				logger.error("MQException: {}", e);
			} finally {
				manager = null;
			}

			message = null;
			putOptions = null;
		}
	}

	protected void closeQueue() {
		try {
			if (queue != null) {
				queue.close();
			}
		} catch (MQException e) {
			logger.error("MQException:" + e.getMessage());
		} finally {
			queue = null;
		}
	}

	protected void disconnectManager() {
		try {
			if (manager != null) {
				manager.disconnect();
			}
		} catch (MQException e) {
			logger.error("MQException:" + e.getMessage());
		} finally {
			manager = null;
		}
	}

	public static MQProperties getMqProperties() {
		return mqProperties;
	}

	public static void setMqProperties(MQProperties mqProperties) {
		MessageQueue.mqProperties = mqProperties;
	}
}
