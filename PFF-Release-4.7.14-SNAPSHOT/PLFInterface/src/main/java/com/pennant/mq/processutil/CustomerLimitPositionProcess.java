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

import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitSummary;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitPositionProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(CustomerLimitPositionProcess.class);

	public CustomerLimitPositionProcess() {
		super();
	}
	
	/**
	 * Process the CustomerLimitPosition Request and send Response
	 * 
	 * @param limitPositionReq
	 * @param msgFormat
	 * @return CustomerLimitPositionReply
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	public CustomerLimitPosition getCustomerLimitSummary(CustomerLimitPosition limitPositionReq, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");

		if (limitPositionReq == null) {
			throw new InterfaceException("PTI3001", "Customer Limit Summary Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(limitPositionReq, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setLimitPositionResponse(response, header);
	}

	/**
	 * Prepare CustomerLimitPositionReply object with processed Element received from MQ
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	private CustomerLimitPosition setLimitPositionResponse(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		
		CustomerLimitPosition limitPosition = null;

		try {
			String path = "/HB_EAI_REPLY/Reply/CustomerLimitSummaryReply";
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/CustomerLimitSummaryReply", responseElement);
			OMElement limitElement = PFFXmlUtil.getOMElement(path+"/Limits", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			List<CustomerLimitSummary> limitSummaryList = getLimitSummary(limitElement, path+"/Limits/Summary");
			
			limitPosition = new CustomerLimitPosition();
			limitPosition = (CustomerLimitPosition) doUnMarshalling(detailElement, limitPosition);
			
			if(limitPosition != null) {
				limitPosition.setLimitSummary(limitSummaryList);
			}
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return limitPosition;
	}

	private List<CustomerLimitSummary> getLimitSummary(OMElement limitElement, String absPath) throws JaxenException {
		logger.debug("Entering");

		if (limitElement == null) {
			return null;
		}

		List<CustomerLimitSummary> limitSummaryList= new ArrayList<CustomerLimitSummary>();
		AXIOMXPath xpath = new AXIOMXPath(absPath);

		@SuppressWarnings("unchecked")
		List<OMElement> summaryList = (List<OMElement>) xpath.selectNodes(limitElement);
		for (OMElement omElement : summaryList) {
			CustomerLimitSummary custLimitSummary = new CustomerLimitSummary();
			custLimitSummary.setLimitReference(PFFXmlUtil.getStringValue(omElement, "LimitReference"));
			custLimitSummary.setLimitDesc(PFFXmlUtil.getStringValue(omElement, "LimitDesc"));
			custLimitSummary.setLimitCurrency(PFFXmlUtil.getStringValue(omElement, "LimitCurrency"));
			custLimitSummary.setLimitExpiryDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(omElement, "LimitExpiryDate"), 
					InterfaceMasterConfigUtil.MQDATE));
			custLimitSummary.setAppovedAmount(PFFXmlUtil.getBigDecimalValue(omElement, "AppovedAmount"));
			custLimitSummary.setBlocked(PFFXmlUtil.getBigDecimalValue(omElement, "Blocked"));
			custLimitSummary.setAvailable(PFFXmlUtil.getBigDecimalValue(omElement, "Available"));
			
			limitSummaryList.add(custLimitSummary);
		}
		logger.debug("Leaving");

		return limitSummaryList;
	}

	/**
	 * Create Customer LimitPosition Request Element to send Interface through MQ
	 * 
	 * @param limitPositionReq
	 * @param referenceNum
	 * @param factory
	 * @return OMElement
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(CustomerLimitPosition limitPositionReq, String referenceNum,
			OMFactory factory) throws InterfaceException {

		logger.debug("Entering");

		/*OMElement requestElement = null;
		limitPositionReq.setReferenceNum(referenceNum);
		limitPositionReq.setTimeStamp(Long.valueOf(PFFXmlUtil.getTodayDateTime(null)));

		OMElement element = doMarshalling(limitPositionReq);
		OMElement rootElement = factory.createOMElement(new QName("CustomerLimitSummaryRequest"));
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iteator = element.getChildElements();
		while (iteator.hasNext()) {
			rootElement.addChild(iteator.next());
		}
		requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		requestElement.addChild(rootElement);*/
		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("CustomerLimitSummaryRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerReference", limitPositionReq.getCustomerReference());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "BranchCode", limitPositionReq.getBranchCode());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		requestElement.addChild(detailRequest);
		logger.debug("Leaving");

		return requestElement;
	}
}
