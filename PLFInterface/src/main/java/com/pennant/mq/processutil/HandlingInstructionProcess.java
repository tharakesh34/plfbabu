package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class HandlingInstructionProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(HandlingInstructionProcess.class);

	public HandlingInstructionProcess() {
		super();
	}

	/**
	 * Process the FinanceMaintenance Request and send Response
	 * 
	 * @param handlingInstruction
	 * @param msgFormat
	 * @return DDARegistration
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	public HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");

		if (handlingInstruction == null) {
			throw new InterfaceException("PTI3001", "HandlingInstruction Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(handlingInstruction, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch(InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return processHandlInstResponse(response, header);
	}

	/**
	 * Process the Handling Instruction response file
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	private HandlingInstruction processHandlInstResponse(OMElement responseElement, AHBMQHeader header)
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		HandlingInstruction handlingInstResponse = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/FinanceMaintenanceReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			handlingInstResponse = new HandlingInstruction();
			handlingInstResponse.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			handlingInstResponse.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			handlingInstResponse.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
			handlingInstResponse.setTimeStamp(Long.parseLong(PFFXmlUtil.getStringValue(detailElement, "TimeStamp")));

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return handlingInstResponse;
	}

	/**
	 * Prepare Handling Instruction Request for FinanceMaitenance 
	 *  
	 * @param handlingInstruction
	 * @param factory
	 * @return OMElement
	 */
	private OMElement getRequestElement(HandlingInstruction handlingInstruction, OMFactory factory)
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("FinanceMaintenanceRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", PFFXmlUtil.getReferenceNumber());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "MaintenanceCode", handlingInstruction.getMaintenanceCode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "FinanceRef", handlingInstruction.getFinanceRef());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "InstallmentDate", 
				DateUtility.formateDate(handlingInstruction.getInstallmentDate(), InterfaceMasterConfigUtil.XML_DATE));
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "NewMaturityDate", 
				DateUtility.formateDate(handlingInstruction.getNewMaturityDate(), InterfaceMasterConfigUtil.XML_DATE));
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "Remarks", handlingInstruction.getRemarks());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				Long.valueOf(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		requestElement.addChild(detailRequest);
		logger.debug("Leaving");

		return requestElement;
	}
}
