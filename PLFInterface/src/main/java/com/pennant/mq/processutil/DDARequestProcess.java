package com.pennant.mq.processutil;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDARequestProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(DDARequestProcess.class);

	//private String DDA_REQ_TYPE = "REGISTRATION";
			
	public DDARequestProcess() {
		super();
	}
	
	/**
	 * Process the DDARequest Request and send Response
	 * 
	 * @param ddsRequest
	 * @param msgFormat
	 * @return DDARequestReply
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	public DDARegistration sendDDARequest(DDARegistration ddsRequest, String msgFormat) throws JaxenException {
		logger.debug("Entering");

		if (ddsRequest == null) {
			throw new InterfaceException("PTI3001", "DDARequest Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
/*		if(StringUtils.equals(ddsRequest.getPurpose(), DDA_REQ_TYPE)) {
			referenceNum = ddsRequest.getReferenceNum();
		}*/
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(ddsRequest, referenceNum, factory);
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
	 * Prepare DDARequestReply Object with processed response XML file data
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws JaxenException 
	 */
	private DDARegistration setDDAReplyInfo(OMElement responseElement, AHBMQHeader header) throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		DDARegistration ddsReply = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/DDARegistrationReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			ddsReply = new DDARegistration();
			ddsReply.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			ddsReply.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			ddsReply.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
			ddsReply.setTimeStamp(Long.parseLong(PFFXmlUtil.getStringValue(detailElement, "TimeStamp")));
			
			AXIOMXPath xpath = new AXIOMXPath("/HB_EAI_REPLY/Reply/DDARegistrationReply/Validation/Error");

			StringBuilder builder = new StringBuilder();
			@SuppressWarnings("unchecked")
			List<OMElement> ddsReplyList = (List<OMElement>) xpath.selectNodes(detailElement);
			for (OMElement omElement : ddsReplyList) {
				if(!StringUtils.isBlank(builder.toString())) {
					builder.append(",");
				}
				builder.append(PFFXmlUtil.getStringValue(omElement, "ErrorCode"));
				builder.append("-");
				builder.append(PFFXmlUtil.getStringValue(omElement, "ErrorDescription"));
				
				ddsReply.setErrorCode(PFFXmlUtil.getStringValue(omElement, "ErrorCode"));
				ddsReply.setErrorDesc(PFFXmlUtil.getStringValue(omElement, "ErrorDescription"));
				}
			
			ddsReply.setValidation(builder.toString());
			
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return ddsReply;
	}

	/**
	 * Prepare DDARequest Request Element to send Interface through MQ
	 * 
	 * @param ddsRequest
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(DDARegistration ddsRequest, String referenceNum, OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("DDARegistrationRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "Purpose", ddsRequest.getPurpose());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerType", ddsRequest.getCustomerType());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CIF", ddsRequest.getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerName", ddsRequest.getCustomerName());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "Mobile", ddsRequest.getMobileNum());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "Email", ddsRequest.getEmailID());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "IDType", ddsRequest.getIdType());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "IDNumber", ddsRequest.getIdNum());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "BankName", ddsRequest.getBankName());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "AccountType", ddsRequest.getAccountType());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "IBAN", ddsRequest.getIban());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "FinanceRef", ddsRequest.getFinRefence());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CommenceOn", 
				DateUtility.formateDate(ddsRequest.getCommenceOn(), InterfaceMasterConfigUtil.XML_DATE));
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "AllowedInstances", ddsRequest.getAllowedInstances());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "MaxAmount", ddsRequest.getMaxAmount());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CurrencyCode", ddsRequest.getCurrencyCode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "PaymentFrequency", ddsRequest.getPaymentFreq());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "OriginatorIdentificationCode", ddsRequest.getOic());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "DDAIssuedFor", ddsRequest.getDdaIssuedFor());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "DDAFormName", ddsRequest.getDdaRegFormName());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "DDAFormData", ddsRequest.getDdaRegFormData());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				Long.valueOf(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));
		
		requestElement.addChild(detailRequest);
		logger.debug("Leaving");
		
		return requestElement;
	}
}
