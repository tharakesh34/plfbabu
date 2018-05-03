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

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchAllAccountsProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(FetchAllAccountsProcess.class);

	public FetchAllAccountsProcess() {
		super();
	}
	
	/**
	 * Process the GetCustomerAccounts Request and send response
	 * 
	 * @param accountDetail
	 * @param msgFormat
	 * @throws JaxenException
	 * @throws InterfaceException
	 */
	public List<CoreBankAccountDetail> fetchCustomerAccounts(CoreBankAccountDetail accountDetail, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");
		
		if (accountDetail == null || StringUtils.isBlank(accountDetail.getCustCIF())) {
			throw new InterfaceException("PTI3001", "Customer Number Cannot be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement fetchAccountReq = getRequestElement(accountDetail, msgFormat, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory, fetchAccountReq);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		
		logger.debug("Leaving");
		return prepareCustomerAccounts(response, header);

	}


	/**
	 * Prepare Customer Accounts Information
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws JaxenException 
	 */
	private List<CoreBankAccountDetail> prepareCustomerAccounts(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/CustomerAccountsReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);
		
		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}
		
		String accSumaryPath = "/HB_EAI_REPLY/Reply/CustomerAccountsReply/AccountSummaryReply";

		//Prepare Account Summary information
		List<CoreBankAccountDetail> accountDetailList = setAccountSummary(detailElement, accSumaryPath);

		logger.debug("Leaving");
		return accountDetailList;
	}

	/**
	 * Set CoreBankAccountDetail object from processed Response Element 
	 * 
	 * @param detailElement
	 * @param targetPath
	 * @return
	 * @throws JaxenException
	 */
	private List<CoreBankAccountDetail> setAccountSummary(OMElement detailElement, String targetPath) throws JaxenException {
		logger.debug("Entering");

		if (detailElement == null) {
			return null;
		}
		List<CoreBankAccountDetail> accSumaryList = new ArrayList<CoreBankAccountDetail>();
		AXIOMXPath xpath = new AXIOMXPath(targetPath);
		@SuppressWarnings("unchecked")
		List<OMElement> accSumary = (List<OMElement>) xpath.selectNodes(detailElement);
		for (OMElement omElement : accSumary) {
			CoreBankAccountDetail accountDetail = new CoreBankAccountDetail();
			
			accountDetail.setReferenceNumber(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			accountDetail.setCustCIF(PFFXmlUtil.getStringValue(detailElement, "CustomerNumber"));
			accountDetail.setAccountNumber(PFFXmlUtil.getStringValue(omElement, "AccountNumber"));
			accountDetail.setIBAN(PFFXmlUtil.getStringValue(omElement, "IBAN"));
			accountDetail.setAcType(PFFXmlUtil.getStringValue(omElement, "AccountType"));
			accountDetail.setAcBranch(PFFXmlUtil.getStringValue(omElement, "BranchCode"));
			accountDetail.setCustShrtName(PFFXmlUtil.getStringValue(omElement, "AccountName"));
			accountDetail.setAcCcy(PFFXmlUtil.getStringValue(omElement, "Currency"));
			accountDetail.setAcBal(PFFXmlUtil.getBigDecimalValue(omElement, "WorkingBalance"));
			accountDetail.setJointHolderID(PFFXmlUtil.getStringValue(omElement, "AccountJointHolder"));
			accountDetail.setJointRelationCode(PFFXmlUtil.getStringValue(omElement, "AccountReleationcode"));
			accountDetail.setRelationNotes(PFFXmlUtil.getStringValue(omElement, "AccountJointNotes"));
			accountDetail.setMinNoOfSignatory(PFFXmlUtil.getStringValue(omElement, "AccountMinNoOfSignatory"));
			accountDetail.setIntroducer(PFFXmlUtil.getStringValue(omElement, "AccountIntroducer"));
			accountDetail.setPowerOfAttorneyFlag(PFFXmlUtil.getStringValue(omElement, "POAFlag"));
			accountDetail.setPowerOfAttorneyCIF(PFFXmlUtil.getStringValue(omElement, "POACIF"));
			
			accSumaryList.add(accountDetail);
		}
		logger.debug("Leaving");

		return accSumaryList;
	}


	/**
	 * Prepare Fetch All Accounts Request Element to send Interface through MQ
	 * @param accountDetail
	 * @param referenceNum
	 * @param referenceNum2 
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(CoreBankAccountDetail accountDetail,String msgFormat,String referenceNum, OMFactory factory){
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement fetchAccReq = factory.createOMElement("CustomerAccountsRequest", null);

		PFFXmlUtil.setOMChildElement(factory, fetchAccReq, "ReferenceNum",referenceNum);
		PFFXmlUtil.setOMChildElement(factory, fetchAccReq, "CustomerNumber",accountDetail.getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, fetchAccReq, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		requestElement.addChild(fetchAccReq);

		logger.debug("Leaving");
		return requestElement;
	}

}
