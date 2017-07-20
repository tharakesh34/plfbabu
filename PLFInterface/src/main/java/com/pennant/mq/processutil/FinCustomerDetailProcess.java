package com.pennant.mq.processutil;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class FinCustomerDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(FinCustomerDetailProcess.class);

	public FinCustomerDetailProcess() {
		super();
	}

	public FinanceCustomerDetails fetchFinCustomerDetails(FinanceCustomerDetails financeCustomerDetails, 
			String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (financeCustomerDetails == null) {
			throw new InterfaceException("PTI3001", "financeCustomerDetails Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(financeCustomerDetails, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setFetchFinCustomerDetailReply(response, header);
	}

	private FinanceCustomerDetails setFetchFinCustomerDetailReply(OMElement responseElement, AHBMQHeader header) throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		FinanceCustomerDetails financeCustomerDetails = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/FetchFinanceCustomerDetailReply", 
					responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			financeCustomerDetails = new FinanceCustomerDetails();
			financeCustomerDetails = (FinanceCustomerDetails) doUnMarshalling(detailElement, financeCustomerDetails);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return financeCustomerDetails;
	}

	private OMElement getRequestElement(FinanceCustomerDetails financeCustomerDetails, String referenceNum,
			OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = null;
		financeCustomerDetails.setReferenceNum(referenceNum);
		financeCustomerDetails.setTimeStamp(Long.parseLong(PFFXmlUtil
				.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		OMElement element = doMarshalling(financeCustomerDetails);
		OMElement rootElement = factory.createOMElement(new QName("FetchFinanceCustomerDetailsRequest"));
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iteator = element.getChildElements();
		while (iteator.hasNext()) {
			rootElement.addChild(iteator.next());
		}
		requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		requestElement.addChild(rootElement);
		logger.debug("Leaving");

		return requestElement;
	}

}
