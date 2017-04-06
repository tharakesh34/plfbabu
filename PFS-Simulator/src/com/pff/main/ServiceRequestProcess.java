package com.pff.main;
import java.lang.reflect.Method;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.MessageProperty;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.ServiceProperties;
import com.pff.vo.PFFMQHeaderVo;

public class ServiceRequestProcess implements Runnable {

	private static Log LOG = null;
	private MQQueueManager mqManager = null;
	private String replyToQName = null;
	private byte[] messagID = null;
	private String replyToQManagerName = null;

	private void setProperties() throws MQException {
		LOG.entering("setProperties");

		MQEnvironment.hostname = ServiceProperties.getServerIP();
		MQEnvironment.port = ServiceProperties.getServerPort();
		MQEnvironment.channel = ServiceProperties.getChannel();
		MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_MQSERIES_CLIENT);
		mqManager = new MQQueueManager(ServiceProperties.getQManager());

		LOG.exiting("setProperties");

	}

	private void processMessageStart() throws MQException {
		LOG.entering("processMessageStart");
		String requestMessage = "";

		try {
			setProperties();

			while (true) {
				MessageProperty messageProperty = null;
				OMElement requestData = null;
				PFFMQHeaderVo headerVo = null;
				OMElement responseData = null;
				
				requestMessage = processMessage();
				
				if (!StringUtils.trimToEmpty(requestMessage).equals("")) {
					try {
						requestData = AXIOMUtil.stringToOM(StringUtils.trimToEmpty(requestMessage));
						headerVo = PFFXmlUtil.retrieveHeader(requestData);
					} catch (Exception e) {
						LOG.info("processMessageStart()--->Invalid requestData");
						LOG.error("processMessageStart()--->"+e.getMessage());
						continue;
					}

				headerVo.setMqMessageId(this.messagID);
					messageProperty = ServiceProperties.getMessageProperties(headerVo.getMessageFormat());

				} else {
					LOG.info("Blank Message");
				}
				if (messageProperty == null) {
				//	headerVo.setMessageReturnCode("8585");
				//	headerVo.setMessageReturnDesc("TransactionType : '"+ headerVo.gettType() + "'");
				//	OMElement responseBody=(OMElement) PFFXmlUtil.getResponseStatus(factory, headerVo.getMessageReturnCode(), headerVo.getMessageReturnDesc());
				//	responseData = PFFXmlUtil.generateReturnElement(headerVo,factory, responseBody);
				
					//System.out.println(responseData.toString());
				//	LOG.info(responseData.toString());
					mqManager.backout();
				} else {
					try {
						Object object = messageProperty.getProcessingClass().newInstance();
						Object [] parms = new Object[2];
						parms[0] =requestData;
						parms[1] =headerVo;
						Method method = object.getClass().getMethod("processRequest",OMElement.class,PFFMQHeaderVo.class);			
						responseData = (OMElement) method.invoke(object, parms);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String responseMessage = null;
				if(responseData!=null){
					responseMessage = responseData.toString();
					System.out.println(responseMessage);
				}
				putMessage(replyToQName, responseMessage);
				
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			mqManager.backout();

		} finally {
			try {
				if(mqManager!=null){
					mqManager.disconnect();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		LOG.exiting("processMessageStart");
	}

	public void run() {
		LOG = LogFactory.getLog(ServiceRequestProcess.class);
		LOG.entering("run");
		LOG.info("run");
		try {
			LOG.info("Starting ");
			processMessageStart();
		} catch (MQException e) {
			LOG.error(e.getMessage(), e);
		}		
		LOG.exiting("run");
	}

	/* Method to Process the first message picked up by the java application.
	 * <p>
	 * This method would pick up the first message from queue mentioned in the
	 * config file. The message would be validated for the xml field MsgFormat.
	 * The message is put on the error queue if it doesnt contain a valid
	 * message format.
	 * <p>
	 * 
	 * @param
	 *         none
	 * 
	 * @return
	 *         none
	 *
	 */
	private String processMessage() {
		LOG.entering("processMessage");
		String receivedMessage=null;
		LOG.info("Connected to Queue Manager " + mqManager.isConnected());
		MQQueue queue=null;
		try {
			int inputOpenOptions = MQC.MQOO_FAIL_IF_QUIESCING + MQC.MQOO_INPUT_SHARED;
			queue = mqManager.accessQueue(ServiceProperties.getReqQ(), inputOpenOptions);//q2

			MQGetMessageOptions getOpts = new MQGetMessageOptions();
			getOpts.options += MQC.MQGMO_SYNCPOINT + MQC.MQGMO_CONVERT + MQC.MQGMO_WAIT;
			getOpts.waitInterval = MQC.MQWI_UNLIMITED;
			LOG.info("Waiting for the message............");

			MQMessage mqMessage = new MQMessage();
			queue.get(mqMessage, getOpts); 
			mqManager.commit();

			receivedMessage = mqMessage.readStringOfByteLength(mqMessage.getDataLength());
			LOG.debug("Message: " + receivedMessage);

			this.messagID = mqMessage.messageId;
			this.replyToQName = mqMessage.replyToQueueName;
			this.replyToQManagerName= mqMessage.replyToQueueManagerName;

		} catch (MQException mqExcep) {
			if (mqExcep.reasonCode != MQException.MQRC_NO_MSG_AVAILABLE) {
				LOG.error(mqExcep.getMessage());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			cleanUp(queue);
		}

		LOG.exiting("processMessage");

		return receivedMessage;
	}

	/**
	 * Method to put in message on the specified queue.
	 * <p>
	 * This method would take in the queue name and the message as parameters. 
	 * The message taken in would be put on the queue specified as parameter.
	 * <p>
	 * 
	 * @param putQName
	 *             <code>String</code> variable indicating the name of the queue
	 *             on which the message has to be put
	 * @param outMsg
	 *             <code>String</code> variable which is the message which needs
	 *             to be put in the queue.
	 * 
	 * @return
	 * none
	 */

	private void putMessage(String putQName, String outMsg)
			throws MQException, Exception {

		LOG.entering("putMessage");
		LOG.info("Connected to Queue Manager" + mqManager.isConnected());
		int inputOpenOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

		MQQueue queue= mqManager.accessQueue(putQName, inputOpenOptions);
		MQPutMessageOptions putOpts = new MQPutMessageOptions();
		MQMessage mqMessage = new MQMessage();
		mqMessage.format=MQC.MQFMT_STRING;

		//Set the correlation ID as input message id
		mqMessage.correlationId = this.messagID;
		mqMessage.writeString(outMsg);
		mqMessage.replyToQueueName = this.replyToQName;
		
		if(StringUtils.trimToEmpty(replyToQManagerName).equals("")){
			mqMessage.replyToQueueManagerName=ServiceProperties.getReplayQManager();
		}else{
			mqMessage.replyToQueueManagerName=this.replyToQManagerName;	
		}
		
		
		if(StringUtils.trimToEmpty(replyToQManagerName).equals("")){
			mqMessage.replyToQueueManagerName=ServiceProperties.getReplayQManager();
		}else{
			mqMessage.replyToQueueManagerName=this.replyToQManagerName;	
		}
		
		
		queue.put(mqMessage, putOpts);

		mqManager.commit();
		LOG.info("Message Put to Queue");
		LOG.exiting("putMessage");

		cleanUp(queue);
	}

	/**
	 * Method to close the open MQ Objects
	 * <p>
	 * This method is used by methods connecting to MQ and opening MQ Objects.
	 * The MQ Objects are closed but the queue manager is not disconnected. The
	 * disconnect from the queue manager happens at the end of execution of this
	 * class.
	 * <p>
	 * 
	 * @param
	 *         none
	 * @return
	 *         none
	 */

	private boolean cleanUp(MQQueue queue) {
		LOG.entering("private method:  cleanUp");
		try {
			if (queue != null) {
				queue.close();
			}
		} catch (MQException e) {
			LOG.error("Unable to release WMQ objects", e);
		}
		LOG.exiting("private method:  cleanUp");
		return true;
	}

}
