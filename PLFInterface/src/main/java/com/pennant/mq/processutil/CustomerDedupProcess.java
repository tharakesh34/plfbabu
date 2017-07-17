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

import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerDedupProcess extends MQProcess  {

	private static final Logger logger = Logger.getLogger(CustomerDedupProcess.class);

	public CustomerDedupProcess() {
		super();
	}
	
	private String CUST_CTG_CODE = null;
	
	public List<CoreCustomerDedup> customerDedupCheck(CoreCustomerDedup dedupCustomer, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");

		if (dedupCustomer == null) {
			throw new InterfaceException("PTI3001", "Customer Cannot Be Blank");
		}

		CUST_CTG_CODE = dedupCustomer.getCustCtgCode();
		 
		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());

		OMElement response = null;

		try {
			OMElement request = PFFXmlUtil.generateRequest(header, factory,getRequestElement(dedupCustomer, referenceNum, factory));
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setCustomerDedupResponse(response, header);
	}

	/**
	 * prepare Customer BO object to return
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	private List<CoreCustomerDedup> setCustomerDedupResponse(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		
		String path = "/HB_EAI_REPLY/Reply/customerDuplicateCheckReply";
		
		if(StringUtils.equals(CUST_CTG_CODE, "SME")) {
			path = "/HB_EAI_REPLY/Reply/customerSMEDuplicateCheckReply";
		}

		OMElement detailElement = PFFXmlUtil.getOMElement(path, responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header, responseElement);
		
		logger.debug("Leaving");

		if(StringUtils.equals(PFFXmlUtil.DEDUP_NOTFOUND, header.getReturnCode())) {
			return null;
		} else if(StringUtils.equals(PFFXmlUtil.DEDUP_FOUND, header.getReturnCode())){
			return getCustDedupRecords(detailElement, path+"/DuplicateCustomer");
		} else {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}
	}

	private List<CoreCustomerDedup> getCustDedupRecords(OMElement detailElement, String absPath) throws JaxenException {
		logger.debug("Entering");

		if (detailElement == null) {
			return null;
		}

		List<CoreCustomerDedup> coreCustDedupList = new ArrayList<CoreCustomerDedup>();
		AXIOMXPath xpath = new AXIOMXPath(absPath);

		@SuppressWarnings("unchecked")
		List<OMElement> custDedupList = (List<OMElement>) xpath.selectNodes(detailElement);
		for (OMElement omElement : custDedupList) {
			
			if(StringUtils.equals(CUST_CTG_CODE, "SME")) {
				CoreCustomerDedup custDedup = new CoreCustomerDedup();
				custDedup.setCustCIF(PFFXmlUtil.getStringValue(omElement, "DuplicateCIF"));
				custDedup.setCustDOB(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(omElement, "DateOfincorporation"), 
						InterfaceMasterConfigUtil.SHORT_DATE));
				custDedup.setCustFName(PFFXmlUtil.getStringValue(omElement, "Name"));
				custDedup.setCustCRCPR(PFFXmlUtil.getStringValue(omElement, "TradeLicenseNumber"));
				custDedup.setCustPassportNo(PFFXmlUtil.getStringValue(omElement, "CommercialRegistrationNumber"));
				custDedup.setMobileNumber(PFFXmlUtil.getStringValue(omElement, "ChamberMemberNumber"));
				custDedup.setDedupRule(PFFXmlUtil.getStringValue(omElement, "DedupRule"));
				coreCustDedupList.add(custDedup);
			} else {
				CoreCustomerDedup custDedup = new CoreCustomerDedup();
				custDedup.setCustCIF(PFFXmlUtil.getStringValue(omElement, "DuplicateCIF"));
				custDedup.setCustDOB(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(omElement, "DateOfBirth"), 
						InterfaceMasterConfigUtil.SHORT_DATE));
				custDedup.setCustFName(PFFXmlUtil.getStringValue(omElement, "FirstName"));
				custDedup.setCustLName(PFFXmlUtil.getStringValue(omElement, "LastName"));
				custDedup.setCustCRCPR(PFFXmlUtil.getStringValue(omElement, "UAEID"));
				custDedup.setCustPassportNo(PFFXmlUtil.getStringValue(omElement, "PassportNumber"));
				custDedup.setMobileNumber(PFFXmlUtil.getStringValue(omElement, "MobileNumber"));
				custDedup.setCustNationality(PFFXmlUtil.getStringValue(omElement, "Nationality"));
				custDedup.setDedupRule(PFFXmlUtil.getStringValue(omElement, "DedupRule"));
				coreCustDedupList.add(custDedup);
			}
		}
		logger.debug("Leaving");

		return coreCustDedupList;
	}

	/**
	 * Prepare Customer Dedup Request Message
	 * 
	 * @param dedupCustomer
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(CoreCustomerDedup dedupCustomer,String referenceNum,OMFactory factory){
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement custDedupCheckReq = null;
		
		if(StringUtils.equals(CUST_CTG_CODE, "SME")) {
			custDedupCheckReq = factory.createOMElement("customerSMEDuplicateCheckRequest", null);
			
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "ReferenceNum",referenceNum);
			//PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CIF", dedupCustomer.getCustCIF());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CustomerName",dedupCustomer.getCustFName());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "TradeLicenseNumber",dedupCustomer.getCustCRCPR());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CommercialRegistrationNumber",dedupCustomer.getCustCRCPR());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "ChamberMemberNumber",dedupCustomer.getCustCRCPR());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "TimeStamp",
					PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));
		} else {
			custDedupCheckReq = factory.createOMElement("customerDuplicateCheckRequest", null);
			
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "ReferenceNum",referenceNum);
			//PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CIF", dedupCustomer.getCustCIF());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "DateOfBirth",DateUtility.formateDate(
					dedupCustomer.getCustDOB(), InterfaceMasterConfigUtil.MQDATE));
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "PassportNumber",dedupCustomer.getCustPassportNo());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "MohterMaidenName",dedupCustomer.getCustMotherMaiden());
			//PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "UAEID",dedupCustomer.getCustCRCPR());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CustomerMobileNumber",
					PFFXmlUtil.unFormatPhoneNumber(dedupCustomer.getMobileNumber()));
			//PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "CustomerName",dedupCustomer.getCustFName());
			PFFXmlUtil.setOMChildElement(factory, custDedupCheckReq, "TimeStamp",PFFXmlUtil.getTodayDateTime(null));
		}

		requestElement.addChild(custDedupCheckReq);

		logger.debug("Leaving");

		return requestElement;
	}
}
