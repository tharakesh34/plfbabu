package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class ReleaseCIFProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(ReleaseCIFProcess.class);

	public ReleaseCIFProcess() {
		super();
	}

	/**
	 * Prepare Release CIF request and generate Response
	 * 
	 * @param customer
	 * @param msgFormat
	 * @param reserveRefNum 
	 * @return String
	 * @throws InterfaceException
	 */
	public String releaseCIF(InterfaceCustomer customer, String reserveRefNum, String msgFormat)throws InterfaceException {
		logger.debug("Entering");

		OMElement request = null;
		OMElement response = null;
		AHBMQHeader header = null;
		try {
			OMFactory factory = OMAbstractFactory.getOMFactory();
			MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
			header =  new AHBMQHeader(msgFormat);

			OMElement requestElement = getReleaseCIFRequestElement(customer, reserveRefNum, factory, msgFormat);
			request = PFFXmlUtil.generateRequest(header, factory, requestElement);

			if (request != null) {
				response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());
			}

		} catch (InterfaceException pfe) {
			logger.error("Exception: ", pfe);
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}

		logger.debug("Leaving");
		return processReleaseCIFResponse(response, header);
	}

	/**
	 * Process the Release CIF response element
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	private String processReleaseCIFResponse(OMElement responseElement, AHBMQHeader header) throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/releaseCIFReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);

		logger.debug("Leaving");
		
		if(StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			return header.getReturnCode();
		} else {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}
	}

	/**
	 * Prepare ReserveCIF Request Element to send Interface through MQ
	 * @param custCIF
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getReleaseCIFRequestElement(InterfaceCustomer customer, String reserveRefNum, 
			OMFactory factory, String msgFormat){
		logger.debug("Entering");

		String newReferenceNum = PFFXmlUtil.getReferenceNumber();

		OMElement requestElement= factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement releaseCIFRequest= factory.createOMElement("releaseCIFRequest",null);
		PFFXmlUtil.setOMChildElement(factory, releaseCIFRequest, "ReferenceNum",newReferenceNum);
		PFFXmlUtil.setOMChildElement(factory, releaseCIFRequest, "CIF",customer.getCustCIF());

		if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.RELEASE_CIF_HPS)) {
			PFFXmlUtil.setOMChildElement(factory, releaseCIFRequest, "ReserveReferenceNum",reserveRefNum);
		} else {
			PFFXmlUtil.setOMChildElement(factory, releaseCIFRequest, "ReserverReferenceNumber",reserveRefNum);
		}
		PFFXmlUtil.setOMChildElement(factory, releaseCIFRequest, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(releaseCIFRequest);

		logger.debug("Leaving");

		return requestElement;
	}
}
