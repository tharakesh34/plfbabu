package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class PoliceAcceptanceUtilProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(PoliceAcceptanceUtilProcess.class);

	public PoliceAcceptanceUtilProcess() {
		super();
	}

	public InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail, String msgFormat) throws InterfaceException {
		// Set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header = new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {

			// Generate Request Element for MQ Call
			OMElement request = PFFXmlUtil.generateRequest(header, factory, getMortgageRequestElement(mortgageDetail, referenceNum, factory));

			// Fetch Response element from Client using MQ call
			response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());

		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		
		return setMortgageDetails(response, header);
	}

	private OMElement getMortgageRequestElement(InterfaceMortgageDetail mortgageDetail, String referenceNum, OMFactory factory) {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement mortgageRequest = factory.createOMElement("MaintainVehicleMortgageRequest", null);

		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "TransactionType", mortgageDetail.getTransactionType());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "MortgageRefNo", mortgageDetail.getMortgageRefNo());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "MortgageSourceCode", mortgageDetail.getMortgageSourceCode());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "ChassisNo", mortgageDetail.getChassisNo());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "TcfNo", mortgageDetail.getTrafficProfileNo());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "EmiratesId", mortgageDetail.getCustCRCPR());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "ApprovedBy", mortgageDetail.getApprovedBy());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "ApprovedDate", PFFXmlUtil.getTodayDate(null));
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "Remarks", mortgageDetail.getRemarks());
		PFFXmlUtil.setOMChildElement(factory, mortgageRequest, "TimeStamp",	
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(mortgageRequest);
		logger.debug("Leaving");
		return requestElement;
	}
	
	private InterfaceMortgageDetail setMortgageDetails(OMElement responseElement, AHBMQHeader header)
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement;

		InterfaceMortgageDetail interfaceMortgageDetail = new InterfaceMortgageDetail();

		detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/MaintainVehicleMortgageReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header,responseElement);
		
		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}
		
		interfaceMortgageDetail.setTransactionId(PFFXmlUtil.getStringValue(detailElement, "TransactionId"));
		interfaceMortgageDetail.setReturncode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
		interfaceMortgageDetail.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
		logger.debug("Leaving");
		
		return interfaceMortgageDetail;
	}
	
	/**
	 * Cancel Mortgage
	 * 
	 * @param transactionId
	 * @param msgFormat
	 * @return
	 * @throws InterfaceException
	 */
	public InterfaceMortgageDetail cancelMortage(String transactionId, String msgFormat) throws InterfaceException {
		// Set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header = new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {

			// Generate Request Element for MQ Call
			OMElement request = PFFXmlUtil.generateRequest(header, factory, cancelMortgageRequestElement(transactionId, referenceNum, factory));

			// Fetch Response element from Client using MQ call
			response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());

		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}

		return cancelMortgageResponse(response, header);
	}
	
	/**
	 * Cancel Mortgage
	 * 
	 * @param transactionId
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement cancelMortgageRequestElement(String transactionId, String referenceNum, OMFactory factory) {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement cancelMortgageRequest = factory.createOMElement("CancelVehicleMortgageRequest", null);

		PFFXmlUtil.setOMChildElement(factory, cancelMortgageRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, cancelMortgageRequest, "TransactionId", transactionId);
		PFFXmlUtil.setOMChildElement(factory, cancelMortgageRequest, "TimeStamp",	PFFXmlUtil.getTodayDateTime(null));

		requestElement.addChild(cancelMortgageRequest);
		logger.debug("Leaving");
		return requestElement;
	}
	
	private InterfaceMortgageDetail cancelMortgageResponse(OMElement responseElement, AHBMQHeader header)
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement;

		InterfaceMortgageDetail interfaceMortgageDetail = new InterfaceMortgageDetail();

		detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/CancelVehicleMortgageReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);
		interfaceMortgageDetail.setTransactionId(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
		interfaceMortgageDetail.setReturncode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
		interfaceMortgageDetail.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
		
		if (!interfaceMortgageDetail.getReturncode().equals(InterfaceMasterConfigUtil.SUCCESS_RETURN_CODE)) {
			throw new InterfaceException(interfaceMortgageDetail.getReturncode(), interfaceMortgageDetail.getReturnText());
		}
		
		logger.debug("Leaving");
		return interfaceMortgageDetail;
	}

	

}
