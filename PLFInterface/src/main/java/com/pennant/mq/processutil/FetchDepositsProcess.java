package com.pennant.mq.processutil;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchDepositsProcess extends MQProcess {

	private static final Logger logger = LogManager.getLogger(FetchDepositsProcess.class);

	public FetchDepositsProcess() {
		super();
	}

	public FetchDeposit fetchCustomerDeposits(FetchDeposit fetchDeposit, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (fetchDeposit == null) {
			throw new InterfaceException("PTI3001", "FetchDeposit Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header = new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(fetchDeposit, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory, requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(),
					getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setFetchDepositRplyInfo(response, header);
	}

	/**
	 * Prepare FetchDeposit Object by processing response element
	 * 
	 * @param responseElement
	 * @param header
	 * @return FetchDeposit
	 * @throws InterfaceException
	 */
	private FetchDeposit setFetchDepositRplyInfo(OMElement responseElement, AHBMQHeader header)
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		FetchDeposit fetchDepositRply = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/GetInvestmentAccountReply",
					responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			fetchDepositRply = new FetchDeposit();
			fetchDepositRply = (FetchDeposit) doUnMarshalling(detailElement, fetchDepositRply);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return fetchDepositRply;
	}

	/**
	 * Prepare Fetch Deposits Request Element to send Interface through MQ
	 * 
	 * @param fetchDeposit
	 * @param referenceNum
	 * @param factory
	 * @return OMElement
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(FetchDeposit fetchDeposit, String referenceNum, OMFactory factory)
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = null;
		fetchDeposit.setReferenceNum(referenceNum);
		fetchDeposit.setTimeStamp(Long.parseLong(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		OMElement element = doMarshalling(fetchDeposit);
		OMElement rootElement = factory.createOMElement(new QName("GetInvestmentAccountRequest"));
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
