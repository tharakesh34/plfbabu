package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.accountposting.AccountPostingDetail;
import com.pennant.coreinterface.model.accountposting.SecondaryDebitAccount;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountPostingDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(AccountPostingDetailProcess.class);

	public AccountPostingDetailProcess() {
		super();
	}

	public AccountPostingDetail doFillPostingDetails(AccountPostingDetail accPostingReq, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (accPostingReq == null) {
			throw new InterfaceException("PTI3001", "AccountPostingRequest Cannot Be Blank");
		}

		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;
		try {
			OMElement requestElement = getRequestElement(accPostingReq, referenceNum, factory, msgFormat);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.warn(pffe.getErrorCode() + ":" + pffe.getErrorMessage());
			throw pffe;
		}
		logger.debug("Leaving");

		return setAccountPostingReplyInfo(response, header, msgFormat);
	}

	/**
	 * Set AccountPostingReply object with processed Response Element
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException 
	 */
	private AccountPostingDetail setAccountPostingReplyInfo(OMElement responseElement, AHBMQHeader header, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		String parentNode = "WithinBankTransferReply";
		if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.ACCOUNT_REVERSAL)) {
			parentNode = "ReversalReply";
		}

		AccountPostingDetail accAccountPosting = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/"+parentNode, responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (StringUtils.isBlank(header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			accAccountPosting = new AccountPostingDetail();

			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.ACCOUNT_POSTING)) {

				// Posting Response
				accAccountPosting.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
				accAccountPosting.setHostReferenceNum(PFFXmlUtil.getStringValue(detailElement, "HostReferenceNum"));
				accAccountPosting.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			} else {

				// Reversal Posting response
				accAccountPosting.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
				accAccountPosting.setTransRefNum(PFFXmlUtil.getStringValue(detailElement, "TransactionReferenceNum"));
				accAccountPosting.setHostReferenceNum(PFFXmlUtil.getStringValue(detailElement, "HostReferenceNum"));
				accAccountPosting.setPaymentMode(PFFXmlUtil.getStringValue(detailElement, "PaymentMode"));
				accAccountPosting.setDebitAccountNumber(PFFXmlUtil.getStringValue(detailElement, "DebitAccountNumber"));
				accAccountPosting.setDebitedCIFID(PFFXmlUtil.getStringValue(detailElement, "DebitCIFID"));
				accAccountPosting.setDebitedCardNoFlag(PFFXmlUtil.getStringValue(detailElement, "DebitCardNumFlag"));
				accAccountPosting.setCreditAccountNumber(PFFXmlUtil.getStringValue(detailElement, "CreditAccountNumber"));
				accAccountPosting.setCreditedCIFID(PFFXmlUtil.getStringValue(detailElement, "CreditCIFID"));
				accAccountPosting.setCreditedCardNoFlag(PFFXmlUtil.getStringValue(detailElement, "CreditCardNumFlag"));
				accAccountPosting.setTransactionAmount(PFFXmlUtil.getBigDecimalValue(detailElement, "TransactionAmount"));
				accAccountPosting.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			}
		} catch (InterfaceException e) {
			logger.warn(e.getErrorCode() + ":" + e.getErrorMessage());
			throw e;
		}

		logger.debug("Leaving");

		return accAccountPosting;
	}

	/**
	 * Prepare AccountPosting Request Element to send Interface through MQ
	 * 
	 * @param accPostingReq
	 * @param referenceNum
	 * @param factory
	 * @return
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(AccountPostingDetail accPostingReq, String referenceNum, OMFactory factory, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));

		OMElement detailRequest = factory.createOMElement("WithinBankTransferRequest", null);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);

		if(!StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.ACCOUNT_REVERSAL)) {

			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DebitAccountNumber", accPostingReq.getDebitAccountNumber());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DebitCurrency", accPostingReq.getDebitCcy());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CreditAccountNumber", accPostingReq.getCreditAccountNumber());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CreditCurrency", accPostingReq.getCreditCcy());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransactionAmount", accPostingReq.getTransactionAmount());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransactionCurrency", accPostingReq.getTransactionCcy());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "PaymentMode", accPostingReq.getPaymentMode());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DealReferenceNumber", accPostingReq.getDealRefNum());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransactionNarration", accPostingReq.getTransNarration());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DealPurpose", accPostingReq.getDealPurpose());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DealType", accPostingReq.getDealType());

			// add secondary account
			if(accPostingReq.getScndDebitAccountList() != null) {
				for(SecondaryDebitAccount account: accPostingReq.getScndDebitAccountList()) {
					detailRequest.addChild(getSecondaryDebitAccounts(account));
				}
			}

		} else {
			detailRequest = factory.createOMElement(new QName("ReversalRequest"));

			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransactionReferenceNum", accPostingReq.getTransRefNum());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "HostReferenceNum", accPostingReq.getHostReferenceNum());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "PaymentMode", accPostingReq.getPaymentMode());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DebitAccountNumber", accPostingReq.getDebitAccountNumber());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DebitCIFID", accPostingReq.getDebitedCIFID());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "DebitCardNumFlag", accPostingReq.getDebitedCardNoFlag());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CreditAccountNumber", accPostingReq.getCreditAccountNumber());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CreditCIFID", accPostingReq.getCreditedCIFID());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CreditCardNumFlag", accPostingReq.getCreditedCardNoFlag());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransactionAmount", accPostingReq.getTransactionAmount());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReturnCode", accPostingReq.getReturnCode());
		}

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(detailRequest);

		logger.debug("Leaving");

		return requestElement;
	}

	/**
	 * Method for preparing Secondary Debit Account details
	 * 
	 * @param scndDebitAccount
	 * @return
	 */
	private OMElement getSecondaryDebitAccounts(SecondaryDebitAccount scndDebitAccount) {
		logger.debug("Entering");

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement scndDebitAccElement = factory.createOMElement(new QName("SecondaryDebitAccounts"));

		if(scndDebitAccount != null) {

			PFFXmlUtil.setOMChildElement(factory, scndDebitAccElement, "SecondaryDebitAccount", 
					scndDebitAccount.getSecondaryDebitAccount());
			PFFXmlUtil.setOMChildElement(factory, scndDebitAccElement, "ScheduleDate", 
					DateUtility.formatDate(scndDebitAccount.getScheduleDate(), InterfaceMasterConfigUtil.MQDATE));
			PFFXmlUtil.setOMChildElement(factory, scndDebitAccElement, "CIF", scndDebitAccount.getCustCIF());
		}

		logger.debug("Leaving");
		return scndDebitAccElement;
	}
}
