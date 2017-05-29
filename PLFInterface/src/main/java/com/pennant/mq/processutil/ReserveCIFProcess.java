package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;

public class ReserveCIFProcess extends MQProcess {

	private final static Logger logger = Logger.getLogger(ReserveCIFProcess.class);

	public ReserveCIFProcess() {
		super();
	}

	private String ReserveCIFRefNum = null;
	/**
	 * Prepare Reserve CIF request and generate Response
	 * 
	 * @param customer
	 * @param msgFormat
	 * @return String
	 * @throws PFFInterfaceException
	 */
	public String reserveCIF(InterfaceCustomer customer, String msgFormat)throws PFFInterfaceException {
		logger.debug("Entering");

		if (customer == null) {
			throw new PFFInterfaceException("PTI3001", new String[]{"Customer"}," Cannot be Empty");	
		}

		// Set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			logger.info("Checking point 3");
			OMElement requestElement = getReserveCIFRequestElement(customer, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory, requestElement);

			if (request != null) {
				ReserveCIFRefNum = referenceNum;
				response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());
			}

		} catch (PFFInterfaceException pfe) {
			logger.error("Exception: ", pfe);
			throw new PFFInterfaceException(pfe.getErrorCode(), pfe.getErrorMessage());
		}

		logger.debug("Leaving");
		return processReserveCIFResponse(response, header);
	}

	/**
	 * 
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws PFFInterfaceException
	 */
	private String processReserveCIFResponse(OMElement responseElement, AHBMQHeader header) throws PFFInterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/reserveCIFReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);

		logger.debug("Leaving");

		if(StringUtils.equals(PFFXmlUtil.CUST_CIF_EXISTS, header.getReturnCode())) {
			return PFFXmlUtil.CUST_CIF_EXISTS;
		} else if(StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())){
			return ReserveCIFRefNum;
		} else {
			throw new PFFInterfaceException("PTI3002", header.getErrorMessage());
		}
	}

	/**
	 * Prepare ReserveCIF Request Element to send Interface through MQ
	 * @param custCIF
	 * @param referenceNum
	 * @param factory
	 * @return
	 * @throws PFFInterfaceException 
	 */
	private OMElement getReserveCIFRequestElement(InterfaceCustomer customer, String referenceNum,OMFactory factory) 
			throws PFFInterfaceException{
		logger.debug("Entering");

		if(customer == null) {
			throw new PFFInterfaceException("PTI3001", "Customer can not be null");
		}
		OMElement requestElement= factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement reserveCIFRequest= factory.createOMElement("reserveCIFRequest",null);

		PFFXmlUtil.setOMChildElement(factory, reserveCIFRequest, "ReferenceNum",referenceNum);
		PFFXmlUtil.setOMChildElement(factory, reserveCIFRequest, "CIF",customer.getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, reserveCIFRequest, "FullName",customer.getCustFName());
		PFFXmlUtil.setOMChildElement(factory, reserveCIFRequest, "BranchCode",customer.getCustDftBranch());
		PFFXmlUtil.setOMChildElement(factory, reserveCIFRequest, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(reserveCIFRequest);

		logger.debug("Leaving");

		return requestElement;
	}
}
