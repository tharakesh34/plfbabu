package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDACancelProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(DDACancelProcess.class);

	public DDACancelProcess() {
		super();
	}
	
	/**
	 * Process the DDACancel Request and send Response
	 * 
	 * @param ddaCancelReq
	 * @param msgFormat
	 * @return DDARequestReply
	 * @throws InterfaceException
	 */
	public DDACancellation cancelDDARegistration(DDACancellation ddaCancelReq, String msgFormat) throws InterfaceException  {
		logger.debug("Entering");

		if (ddaCancelReq == null) {
			throw new InterfaceException("PTI3001", "DDACancellation Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(ddaCancelReq, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setDDAReplyInfo(response, header);
	}

	/**
	 * Prepare DDACancellationRply Object with processed response XML file data
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 */
	private DDACancellation setDDAReplyInfo(OMElement responseElement, AHBMQHeader header) throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		DDACancellation ddaCancelReply = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/DDACancellationReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			ddaCancelReply = new DDACancellation();
			ddaCancelReply = (DDACancellation) doUnMarshalling(detailElement, ddaCancelReply);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return ddaCancelReply;
	}

	/**
	 * Prepare DDACancellation Request Element to send Interface through MQ
	 * 
	 * @param ddaCancelReq
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(DDACancellation ddaCancelReq, String referenceNum, OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("DDACancellationRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ISNumber", ddaCancelReq.getIsNumber());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "DDAReferenceNo", ddaCancelReq.getDdaReferenceNo());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "DDACancellationReasonCode", ddaCancelReq.getDdaCanResCode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CaptureMode", ddaCancelReq.getCaptureMode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", Long.valueOf(
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));
		
		requestElement.addChild(detailRequest);
		logger.debug("Leaving");
		
		return requestElement;
	}
}
