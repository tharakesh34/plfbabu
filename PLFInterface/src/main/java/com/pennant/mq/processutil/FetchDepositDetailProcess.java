package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchDepositDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(FetchDepositDetailProcess.class);

	public FetchDepositDetailProcess() {
		super();
	}
	
	public FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		if (fetchDepositDetail == null) {
			throw new InterfaceException("PTI3001", "FetchDeposit Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(fetchDepositDetail, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setFetchDepositDetailRply(response, header);
	}

	private FetchDepositDetail setFetchDepositDetailRply(OMElement responseElement, AHBMQHeader header) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		FetchDepositDetail fetchDepositDetail = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/GetInvestmentAccountDetailsReply", 
					responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			fetchDepositDetail = new FetchDepositDetail();
			fetchDepositDetail = (FetchDepositDetail) doUnMarshalling(detailElement, fetchDepositDetail);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return fetchDepositDetail;
	}

	private OMElement getRequestElement(FetchDepositDetail fetchDepositDetail, String referenceNum, OMFactory factory) 
			throws InterfaceException {
		logger.debug("Entering");
		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement("GetInvestmentAccountDetailsRequest", null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "InvestmentContractNumber", fetchDepositDetail.getInvstContractNo());
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		logger.debug("Leaving");
		requestElement.addChild(detailRequest);

		return requestElement;
	}

}
