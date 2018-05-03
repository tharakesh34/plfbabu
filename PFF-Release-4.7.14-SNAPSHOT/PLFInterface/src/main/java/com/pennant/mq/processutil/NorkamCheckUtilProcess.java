package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.log4j.Logger;

import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class NorkamCheckUtilProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(NorkamCheckUtilProcess.class);

	public NorkamCheckUtilProcess() {
		super();
	}
	
	public InterfaceNorkamCheck doNorkamCheck(InterfaceNorkamCheck norkamCheck, String msgFormat) throws InterfaceException {
		// Set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header = new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;
		try {
			OMElement requestElement = getRequestElement(norkamCheck, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setBlackListDetails(response, header);
	}
	
	/**
	 * Prepare Blacklist Request Element to send Interface through MQ
	 * 
	 * @param norkamCheck
	 * @param referenceNum
	 * @param factory
	 * @return OMElement
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(InterfaceNorkamCheck norkamCheck, String referenceNum, OMFactory factory) 
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("customerBlacklistCheckRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerName", norkamCheck.getCustomerName());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerId", norkamCheck.getCustomerId());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerAddress", norkamCheck.getCustomerAddress());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerCountry", norkamCheck.getCustomerCountry());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerDOB",DateUtility.formateDate(
				norkamCheck.getCustomerDOB(), InterfaceMasterConfigUtil.MQDATE));
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerPOB", norkamCheck.getCustomerPOB());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerOrganization", norkamCheck.getCustomerOrganization());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		logger.debug("Leaving");
		requestElement.addChild(detailRequest);
		return requestElement;

	}
	
	/**
	 * Set Details 
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	private InterfaceNorkamCheck setBlackListDetails(OMElement responseElement, AHBMQHeader header) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		InterfaceNorkamCheck interfaceNorkamCheck = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/customerBlacklistCheckReply", 
					responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			switch (header.getReturnCode()) {
			case InterfaceConstants.BLACKLIST_HIT:
				break;
			case InterfaceConstants.BLACKLIST_NOHIT:
				break;
			case InterfaceConstants.SUCCESS_CODE:
				break;
			default:
				throw new InterfaceException(header.getReturnCode(), header.getErrorMessage());
			}	

			interfaceNorkamCheck = new InterfaceNorkamCheck();
			
			interfaceNorkamCheck.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			interfaceNorkamCheck.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			interfaceNorkamCheck.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
			interfaceNorkamCheck.setTimeStamp(Long.parseLong(PFFXmlUtil.getStringValue(detailElement, "TimeStamp")));

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return interfaceNorkamCheck;
	}
	
}
