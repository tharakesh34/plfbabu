package com.pennant.mq.processutil;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.chequeverification.ChequeStatus;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class ChequeVerificationDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(ChequeVerificationDetailProcess.class);

	public ChequeVerificationDetailProcess() {
		super();
	}

	/**
	 * Process the FinanceMaintenance Request and send Response
	 * 
	 * @param chequeVerification
	 * @param msgFormat
	 * @return ChequeVerification
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	public ChequeVerification sendChequeVerificationReq(ChequeVerification chequeVerification, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");

		if (chequeVerification == null) {
			throw new InterfaceException("PTI3001", "Cheque Verification Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(chequeVerification, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return processChequeVerificationResponse(response, header);
	}

	/**
	 * Process the Cheque verification response file
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	private ChequeVerification processChequeVerificationResponse(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		ChequeVerification chequeVerificationRes = null;

		try {
			String absPath = "/HB_EAI_REPLY/Reply/ChequeVerificationReply";
			OMElement detailElement = PFFXmlUtil.getOMElement(absPath, responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			chequeVerificationRes = new ChequeVerification();
			chequeVerificationRes.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			chequeVerificationRes.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			chequeVerificationRes.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
			chequeVerificationRes.setTimeStamp(Long.parseLong(PFFXmlUtil.getStringValue(detailElement, "TimeStamp")));

			chequeVerificationRes.setChequeStsList(getChequeStsList(detailElement, absPath+"/ChequeStatus"));

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return chequeVerificationRes;
	}

	/**
	 * Method for preparing Cheque Status list
	 * 
	 * @param detailElement
	 * @param path
	 * @return
	 * @throws JaxenException
	 */
	private List<ChequeStatus> getChequeStsList(OMElement detailElement, String path) throws JaxenException {
		logger.debug("Entering");

		if (detailElement == null) {
			return null;
		}

		List<ChequeStatus> chequeStsList = new ArrayList<ChequeStatus>();
		AXIOMXPath xpath = new AXIOMXPath(path);
		@SuppressWarnings("unchecked")
		List<OMElement> statusList = (List<OMElement>) xpath.selectNodes(detailElement);
		for (OMElement omElement : statusList) {
			ChequeStatus status = new ChequeStatus();
			status.setChequeNo(PFFXmlUtil.getStringValue(omElement, "ChequeNo"));
			status.setValidity(PFFXmlUtil.getStringValue(omElement, "Validity"));

			chequeStsList.add(status);
		}
		logger.debug("Leaving");
		return chequeStsList;
	}

	/**
	 * Prepare Cheque Verification Request 
	 *  
	 * @param chequeVerification
	 * @param factory
	 * @return OMElement
	 */
	private OMElement getRequestElement(ChequeVerification chequeVerification, OMFactory factory)
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("ChequeVerificationRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", PFFXmlUtil.getReferenceNumber());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CIF", chequeVerification.getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "FinanceRef", chequeVerification.getFinanceRef());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ChequeRangeFrom", chequeVerification.getChequeRangeFrom());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ChequeRangeTo", chequeVerification.getChequeRangeTo());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "Remarks", chequeVerification.getRemarks());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "BranchCode", chequeVerification.getBranchCode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				Long.valueOf(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		requestElement.addChild(detailRequest);
		logger.debug("Leaving");

		return requestElement;
	}
}
