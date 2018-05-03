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

import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitUtilProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(CustomerLimitUtilProcess.class);

	public CustomerLimitUtilProcess() {
		super();
	}

	/**
	 * Process the LimitUtilizationDetail Request and send Response
	 * 
	 * @param custLimitUtilReq
	 * @param msgType
	 * @return CustomerLimitUtilReply
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	public CustomerLimitUtilization getLimitUtilizationDetails(CustomerLimitUtilization limitUtilization,
			String msgType) throws JaxenException {
		logger.debug("Entering");

		if (limitUtilization == null) {
			throw new InterfaceException("PTI3001", "Customer Limit Details Cannot Be Blank");
		}

		// set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();

		AHBMQHeader header = new AHBMQHeader(msgType);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(limitUtilization, referenceNum, factory, msgType);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,	requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setLimitUtilDetailsResponse(response, header, msgType);
	}

	/**
	 * Prepare CustomerLimitUtilizationReply Object by Processing Response
	 * element
	 * 
	 * @param responseElement
	 * @param header
	 * @param msgType
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	private CustomerLimitUtilization setLimitUtilDetailsResponse(OMElement responseElement, AHBMQHeader header,
			String msgType) throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		CustomerLimitUtilization custLimitUtil = null;
		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/DealOnlineInquiryReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header,responseElement);

			custLimitUtil = new CustomerLimitUtilization();
			
			if (StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode()) || 
					StringUtils.equals(PFFXmlUtil.NOGO, header.getReturnCode())) {
				
				logger.info("ReturnStatus is Success");
				
				custLimitUtil.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
				custLimitUtil.setDealID(PFFXmlUtil.getStringValue(detailElement, "DealID"));
				custLimitUtil.setCustomerReference(PFFXmlUtil.getStringValue(detailElement, "CustomerReference"));
				custLimitUtil.setLimitRef(PFFXmlUtil.getStringValue(detailElement, "LimitRef"));
				
				if(StringUtils.equals(PFFXmlUtil.NOGO, header.getReturnText())) {
					setOverrides(detailElement, custLimitUtil);
				} else {
					custLimitUtil.setResponse(PFFXmlUtil.getStringValue(detailElement, "Response"));
					custLimitUtil.setErrMsg(PFFXmlUtil.getStringValue(detailElement, "ErrMsg"));
				}
				custLimitUtil.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
				custLimitUtil.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
				
				custLimitUtil.setRequestType(msgType);
				
			} else if(!StringUtils.isBlank(header.getReturnCode())){
				logger.info("ReturnStatus is Failure");
				
				custLimitUtil.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
				custLimitUtil.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
				custLimitUtil.setReturnText(header.getErrorMessage());
				
				custLimitUtil.setRequestType(msgType);
			} else {
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return custLimitUtil;
	}

	private void setOverrides(OMElement detailElement, CustomerLimitUtilization custLimitUtil) throws JaxenException {
		logger.debug("Entering");
		AXIOMXPath xpath = new AXIOMXPath("/HB_EAI_REPLY/Reply/DealOnlineInquiryReply/Overrides");
		@SuppressWarnings("unchecked")
		List<OMElement> overrideList = (List<OMElement>) xpath.selectNodes(detailElement);
		StringBuffer sb = new StringBuffer();

		for (OMElement omElement : overrideList) {
			sb.append(PFFXmlUtil.getStringValue(omElement, "MsgBreach"));
		}
		custLimitUtil.setMsgBreach(String.valueOf(sb));
		logger.debug("Leaving");
	}

	/**
	 * Prepare CustomerLimitUtilization Request Element to send Interface
	 * through MQ
	 * 
	 * @param custLimitUtilReq
	 * @param referenceNum
	 * @param factory
	 * @param msgType
	 * @return
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(CustomerLimitUtilization limitUtilization, String referenceNum,
			OMFactory factory, String msgType) throws InterfaceException {
		logger.debug("Entering");
		
		OMElement rootElement = null;
		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));

		rootElement = factory.createOMElement(new QName("DealOnlineInquiryRequest"));
				
		PFFXmlUtil.setOMChildElement(factory, rootElement, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, rootElement, "DealID", limitUtilization.getDealID());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "DealType", limitUtilization.getDealType());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "CustomerReference", limitUtilization.getCustomerReference());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "LimitRef", limitUtilization.getLimitRef());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "UserID", limitUtilization.getUserID());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "DealAmount", limitUtilization.getDealAmount());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "DealCcy", limitUtilization.getDealCcy());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "DealExpiry", 
				PFFXmlUtil.getTodayDate(InterfaceMasterConfigUtil.MQDATE));
		PFFXmlUtil.setOMChildElement(factory, rootElement, "MTM", limitUtilization.getMtm());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "Tenor", limitUtilization.getTenor());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "EffProfitRate", limitUtilization.getEffProfitRate());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "OS_Amount", limitUtilization.getOs_Amount());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "PastDueAmount", limitUtilization.getPastDueAmount());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "AmBuy", limitUtilization.getAmountBuy());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "AmSell", limitUtilization.getAmountSell());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "MarketPrice", limitUtilization.getMarketPrice());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "BranchCode", limitUtilization.getBranchCode());
		PFFXmlUtil.setOMChildElement(factory, rootElement, "TimeStamp",	
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		requestElement.addChild(rootElement);
			
		logger.debug("Leaving");

		return requestElement;

	}

}
