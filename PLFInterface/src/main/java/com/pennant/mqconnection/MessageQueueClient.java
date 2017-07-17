package com.pennant.mqconnection;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class MessageQueueClient {
	private static final Logger logger = Logger.getLogger(MessageQueueClient.class);
	
	MQQueueManager queueManager;
	private String hostName;
	private int port;
	private String channel;
	private String qManagerName;
	private boolean remote=false;
	private int req_CCSID=0;
	private int res_CCSID=0;
	private byte[] messagID = null;
	private String sSL_REQUIRED;
	private String sslTrustStore;
	private String sslTrustStorePassword;
	private String sslKeyStore;
	private String sslKeyStorePassword;
	private String sslCipherSuite;
	
	public MessageQueueClient(String configKey) throws InterfaceException {
		
		try {

			this.hostName = InterfacePropertiesUtil.getProperty(configKey+ "_SERVERIP");
			this.port = InterfacePropertiesUtil.getIntProperty(configKey+ "_SERVERPORT");
			this.qManagerName=InterfacePropertiesUtil.getProperty(configKey+ "_QMGRNAME");
			this.channel=InterfacePropertiesUtil.getProperty(configKey+ "_CHANNEL");	
			this.sSL_REQUIRED = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_REQUIRED");
			this.sslTrustStore = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_TRUST_STORE");
			this.sslTrustStorePassword = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_TRUST_STORE_PASSWORD");
			this.sslKeyStore = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_KEY_STORE");
			this.sslKeyStorePassword = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_KEY_STORE_PASSWORD");
			this.sslCipherSuite = InterfacePropertiesUtil.getProperty(configKey+ "_SSL_CIPHER_SUITE");

			if ("1".equals(StringUtils.trimToEmpty(this.sSL_REQUIRED))) {
				System.setProperty("javax.net.ssl.trustStore",
						this.sslTrustStore);
				System.setProperty("javax.net.ssl.keyStore",
						this.sslKeyStore);
				System.setProperty("javax.net.ssl.keyStorePassword",
						this.sslKeyStorePassword);
				System.setProperty("javax.net.ssl.trustStorePassword",
						this.sslTrustStorePassword);
			}

			if(StringUtils.equals("Y",InterfacePropertiesUtil.getProperty(configKey+ "_REMOTE"))){
				remote=true;
			}
			
			this.req_CCSID= InterfacePropertiesUtil.getIntProperty(configKey+ "_REQ_CCSID");
			this.res_CCSID= InterfacePropertiesUtil.getIntProperty(configKey+ "_RES_CCSID");
			
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9001", "Failed to load MQ configuration");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private MQQueueManager getMQQueueManager() throws MQException,
			InterfaceException {
		logger.info("getMQQueueManager()");

		try {
			if (queueManager == null) {
				MQEnvironment.hostname = this.hostName;
				MQEnvironment.port = this.port;
				MQEnvironment.channel = this.channel;
				MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);

				if (StringUtils.equals(this.sSL_REQUIRED, "1")) {
					MQEnvironment.properties.put(CMQC.SSL_CIPHER_SUITE_PROPERTY, this.sslCipherSuite);
					MQEnvironment.properties.put(CMQC.SSL_FIPS_REQUIRED_PROPERTY, true);
				}

				queueManager = new MQQueueManager(this.qManagerName);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("PTI9000", e.getMessage());
		}
		logger.info("getMQQueueManager()");

		return queueManager;
	} 

	public boolean setRequest(String request, String requestQueue) throws InterfaceException {
		logger.info("Entering");

		try {
			sendRequest(request, requestQueue, null);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			try {
				queueManager.disconnect();
			} catch (Exception e2) {
				logger.error("Exception: ", e2);
			}
			queueManager = null;
		}
		logger.info("leaving");
		return true;
	}
	
	/**
	 * Send Request and get Response message from MQ
	 * 
	 * @param request
	 * @param requestQueue
	 * @param responseQueue
	 * @param waitTime
	 * @return
	 * @throws InterfaceException
	 */
	public OMElement getRequestResponse(String request, String requestQueue,
										String responseQueue, int waitTime) throws InterfaceException {
		logger.info("entering");

		OMElement responseElement = null;
		String response = "";
		
		try {
			logger.debug(request);

			byte[] messageId = sendRequest(request, requestQueue, responseQueue);
			response = receiveResponse(responseQueue, waitTime, messageId);
			logger.debug(response);

			responseElement = AXIOMUtil.stringToOM(StringUtils.trimToEmpty(response));

		} catch (XMLStreamException e) {
			logger.error("Exception: ", e);
			throw new InterfaceException(String.valueOf("PTI9999"), "Exception while parsing response xml");
		} finally {
			try {
				queueManager.disconnect();
			} catch (Exception e2) {
				logger.error("Exception: ", e2);
			}
			queueManager = null;
		}

		return responseElement;
	}

	/**
	 * Send Request Message to MQ
	 * 
	 * @param content
	 * @param requestQueue
	 * @param responseQueue
	 * @return
	 * @throws IOException
	 * @throws MQException
	 * @throws Exception
	 */
	protected byte[] sendRequest(String content,String requestQueue,String responseQueue) throws InterfaceException{
		logger.info("sendRequest()");
		
		MQQueue queue=null;
		
		try {
			// Establish access to the queue
			int inputOpenOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_OUTPUT;
			
			if(remote){
				queue = getMQQueueManager().accessQueue(requestQueue,CMQC.MQOO_OUTPUT);
			}else{
				queue = getMQQueueManager().accessQueue(requestQueue, inputOpenOptions);
			}
			
			logger.debug("Response Queue ::::::: "+requestQueue);
			logger.debug("Request XML :::::: "+content);
			
			// Prepare the message
			MQMessage mqMessage = new MQMessage();
			//mqMessage.format = CMQC.MQFMT_STRING;
			
			if(this.req_CCSID!=0){
				mqMessage.characterSet= this.req_CCSID;
			}
			
			mqMessage.writeString(content);
			
			if(StringUtils.isNotEmpty(responseQueue)){
				
				mqMessage.replyToQueueName = responseQueue;	
			}
			
			// Place the message onto the queue
			MQPutMessageOptions putOptions = new MQPutMessageOptions();
			queue.put(mqMessage, putOptions);
			
			// Prepare the result with correlation id
			mqMessage.correlationId = mqMessage.messageId;
			messagID = mqMessage.correlationId;
			queueManager.commit();
			mqMessage = null;
		} catch (MQException e) {
			if (e.reasonCode != MQConstants.MQRC_NO_MSG_AVAILABLE) {
				logger.error("Exception: ", e);
			}
			throw new InterfaceException(String.valueOf(e.reasonCode), e.getMessage());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException(String.valueOf("PTI9999"), "MQ Exception, Failed to send request");
		} finally {
			closeQueue(queue);
		}

		logger.info("Leaving");
		return messagID;
	}


	protected String receiveResponse(String responseQueue,int waitTime, byte[] messageId) throws InterfaceException{
		logger.info("receiveResponse()"+responseQueue);

		String result = null;
		MQQueue queue=null;
		int inputOpenOptions=0;
		
		try {
			inputOpenOptions = CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_FAIL_IF_QUIESCING;
			queue = getMQQueueManager().accessQueue(responseQueue,inputOpenOptions);
						
			MQMessage message = new MQMessage();
			if(this.res_CCSID!=0){
				message.characterSet= this.res_CCSID;
			}

			MQGetMessageOptions getOptions = new MQGetMessageOptions();
			getOptions.options = CMQC.MQGMO_SYNCPOINT + CMQC.MQGMO_WAIT;

			getOptions.waitInterval = waitTime;
			
			logger.info("Waiting for the Response...");
			
			message.correlationId = messageId;
			queue.get(message, getOptions);

			result = message.readStringOfCharLength(message.getMessageLength());
			
			queueManager.commit();
			
			logger.debug("Message: " + result);
			
		} catch (MQException e) {
			logger.error("Exception: ", e);
			throw new InterfaceException(String.valueOf(e.reasonCode), e.getMessage());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException(String.valueOf("PTI9999"), "MQ Exception, Failed to receive response");
		} finally {
			closeQueue(queue);
		}

		logger.info("sendRequest()"+result);
		return result;
	}

	protected void closeQueue(MQQueue queue) {
		logger.info("private method:  closeQueue");

		try {
			if (queue != null) {
				queue.close();
			}
		} catch (Exception e) {
			logger.error("Unable to release WMQ objects", e);
		} finally {
			queue = null;
		}

		logger.info("private method:  closeQueue");
	}

}
