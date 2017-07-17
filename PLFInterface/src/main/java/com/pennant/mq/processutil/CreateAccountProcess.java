package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.mq.dao.MQInterfaceDAO;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CreateAccountProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(CreateAccountProcess.class);

	public CreateAccountProcess() {
		super();
	}
	private MQInterfaceDAO mqInterfaceDAO;
	
	/**
	 * Process the CreateAccount Request and send Response
	 *  
	 * @param accountdetail
	 * @param msgFormat
	 * @return CoreBankAccountDetail
	 * @throws InterfaceException
	 */
	public InterfaceAccount createAccount(InterfaceAccount accountdetail, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (accountdetail == null) {
			throw new InterfaceException("PTI3001", "Customer Cannot Be Blank");
		}

		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement request = PFFXmlUtil.generateRequest(header, factory,getRequestElement(accountdetail, referenceNum, factory));
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setCreateAccResponse(response, header);
	}

	/**
	 * Prepare Create Account Request Element to send Interface through MQ
	 * @param custCIF
	 * @param referenceNum
	 * @param factory
	 * @return OMElement
	 * @throws InterfaceException 
	 */
	private OMElement getRequestElement(InterfaceAccount accountdetail,String referenceNum,OMFactory factory) throws InterfaceException{
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement createAccReq = factory.createOMElement("createAccountRequest", null);

		PFFXmlUtil.setOMChildElement(factory, createAccReq, "ReferenceNum",referenceNum);
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "CIF",accountdetail.getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "BranchCode",accountdetail.getBranchCode());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "BranchCodeISO",getMqInterfaceDAO().getPFFCode(accountdetail.getBranchCode(), "mcm_rmtbranches"));
		//PFFXmlUtil.setOMChildElement(factory, createAccReq, "CustomerType",accountdetail.getCustomerType());
		//PFFXmlUtil.setOMChildElement(factory, createAccReq, "CustomerTypeISO",getMqInterfaceDAO().grtPFFCode(accountdetail.getCustomerType(), "mcm_rmtcusttypes"));
		//PFFXmlUtil.setOMChildElement(factory, createAccReq, "ProductCode",accountdetail.getProductCode());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "customerType","Retail");
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "customerTypeISO","102468");
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "ProductCode","1001");
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "ProductCodeISO","103646"); 
		
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "Currency",accountdetail.getCurrency());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "CurrencyISO",getMqInterfaceDAO().getPFFCode(accountdetail.getCurrency(), "mcm_rmtcurrencies"));
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "AccountName",accountdetail.getAccountName());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "AccountOfficer",accountdetail.getAccountOfficer());

		OMElement jointHolderElement = factory.createOMElement("jointHolders", null);

		//PFFXmlUtil.setOMChildElement(factory, jointHolderElement, "JointHolderID",accountdetail.getJointHolderID());
		//PFFXmlUtil.setOMChildElement(factory, jointHolderElement, "JointRelationCode",accountdetail.getJointRelationCode());
		//PFFXmlUtil.setOMChildElement(factory, jointHolderElement, "RelationNotes",accountdetail.getRelationNotes());

		createAccReq.addChild(jointHolderElement);

		PFFXmlUtil.setOMChildElement(factory, createAccReq, "ModeOfOperation",accountdetail.getModeOfOperation());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "MinNoOfSignatory",accountdetail.getMinNoOfSignatory());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "Introducer",accountdetail.getIntroducer());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "PowerOfAttorneyFlag",accountdetail.getPowerOfAttorneyFlag());
		//PFFXmlUtil.setOMChildElement(factory, createAccReq, "PowerOfAttorneyCIF",accountdetail.getPowerOfAttorneyCIF());
		//PFFXmlUtil.setOMChildElement(factory, createAccReq, "IBAN",accountdetail.getIban());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "ShoppingCardIssue",accountdetail.getShoppingCardIssue());
		PFFXmlUtil.setOMChildElement(factory, createAccReq, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(createAccReq);

		logger.debug("Leaving");

		return requestElement;
	}

	/**
	 * prepare CoreBankAccountDetail object to return
	 * @param responseElement
	 * @param header
	 * @return CoreBankAccountDetail
	 * @throws InterfaceException
	 */
	private InterfaceAccount setCreateAccResponse(OMElement responseElement,AHBMQHeader header) throws InterfaceException{
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/createAccountReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header= getReturnStatus(detailElement, header, responseElement);

		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}

		InterfaceAccount accountDetail = new InterfaceAccount();
		accountDetail.setAccountNumber(PFFXmlUtil.getStringValue(detailElement, "AccountNo"));
		accountDetail.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
		accountDetail.setIban(PFFXmlUtil.getStringValue(detailElement, "IBAN"));

		logger.debug("Leaving");

		return accountDetail;
	}
	public MQInterfaceDAO getMqInterfaceDAO() {
		return mqInterfaceDAO;
	}

	public void setMqInterfaceDAO(MQInterfaceDAO mqInterfaceDAO) {
		this.mqInterfaceDAO = mqInterfaceDAO;
	}

}
