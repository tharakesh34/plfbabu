package com.pennant.mq.processutil;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchAccountDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(FetchAccountDetailProcess.class);

	public FetchAccountDetailProcess() {
		super();
	}
	
	/**
	 * Process the Fetch Account details Request and send response
	 * 
	 * @param accountDetail
	 * @param msgFormat
	 * @throws JaxenException
	 * @throws InterfaceException
	 */
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail accountDetail, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");
		
		if (accountDetail == null || StringUtils.isBlank(accountDetail.getAccountNumber())) {
			throw new InterfaceException("PTI3001", "Account Number Cannot be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement fetchAccountReq = getRequestElement(accountDetail, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory, fetchAccountReq);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		
		logger.debug("Leaving");
		return prepareAccountDetails(response, header);

	}


	/**
	 * Prepare Account Details
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws JaxenException 
	 * @throws InterfaceException
	 */
	private List<CoreBankAccountDetail> prepareAccountDetails(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		List<CoreBankAccountDetail> accSumaryList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = new CoreBankAccountDetail();		

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/AccountDetailReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);

		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			accountDetail.setErrorCode(header.getReturnCode());
			if(StringUtils.isBlank(header.getReturnText())) {
				header.setReturnText("Unable to fetch Account details");
			}
			accountDetail.setErrorMessage(header.getReturnText());
		}
		
		accountDetail.setErrorCode(header.getReturnCode());
		accountDetail.setErrorMessage(header.getReturnText());
		accountDetail.setReferenceNumber(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
		accountDetail.setCustCIF(PFFXmlUtil.getStringValue(detailElement, "CustomerNumber"));
		accountDetail.setAccountNumber(PFFXmlUtil.getStringValue(detailElement, "AccountNumber"));
		accountDetail.setIBAN(PFFXmlUtil.getStringValue(detailElement, "IBAN"));
		accountDetail.setAcType(PFFXmlUtil.getStringValue(detailElement, "AccountType"));
		accountDetail.setAcBranch(PFFXmlUtil.getStringValue(detailElement, "BranchId"));
		accountDetail.setCustShrtName(PFFXmlUtil.getStringValue(detailElement, "AccountTitle"));
		accountDetail.setAcCcy(PFFXmlUtil.getStringValue(detailElement, "AccountCurrency"));
		accountDetail.setAcBal(PFFXmlUtil.getBigDecimalValue(detailElement, "AvailableBalance"));
		accountDetail.setJointHolderID(PFFXmlUtil.getStringValue(detailElement, "AccountJointHolder"));
		accountDetail.setJointRelationCode(PFFXmlUtil.getStringValue(detailElement, "AccountReleationcode"));
		accountDetail.setRelationNotes(PFFXmlUtil.getStringValue(detailElement, "AccountJointNotes"));
		accountDetail.setMinNoOfSignatory(PFFXmlUtil.getStringValue(detailElement, "AccountMinNoOfSignatory"));
		accountDetail.setIntroducer(PFFXmlUtil.getStringValue(detailElement, "AccountIntroducer"));
		accountDetail.setPowerOfAttorneyFlag(PFFXmlUtil.getStringValue(detailElement, "POAFlag"));
		accountDetail.setPowerOfAttorneyCIF(PFFXmlUtil.getStringValue(detailElement, "POACIF"));
		accSumaryList.add(accountDetail);

		logger.debug("Leaving");
		return accSumaryList;
	}

	/**
	 * Prepare Fetch Account Detail Request Element to send Interface through MQ
	 * @param accountDetail
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(CoreBankAccountDetail accountDetail,String referenceNum, OMFactory factory){
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement fetchAccDetailReq = factory.createOMElement("AccountDetailRequest", null);

		PFFXmlUtil.setOMChildElement(factory, fetchAccDetailReq, "ReferenceNum",referenceNum);
		PFFXmlUtil.setOMChildElement(factory, fetchAccDetailReq, "AccountNumber",accountDetail.getAccountNumber());
		
		PFFXmlUtil.setOMChildElement(factory, fetchAccDetailReq, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		requestElement.addChild(fetchAccDetailReq);

		logger.debug("Leaving");
		return requestElement;
	}

}