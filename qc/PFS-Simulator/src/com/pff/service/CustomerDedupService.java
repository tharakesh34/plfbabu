package com.pff.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.pennant.interfaceservice.model.CoreCustomerDedup;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.DateUtility;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CustomerDedupProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CustomerDedupService {
	private static Log LOG = null;
	private static String requestPath="/HB_EAI_REQUEST/Request/customerDuplicateCheckRequest/";
    private PFFMQHeaderVo headerVo=null;
    private String referenceNumber = null;
	public CustomerDedupService() {
		LOG = LogFactory.getLog(CustomerDedupService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		List<CoreCustomerDedup> detailsVo		=   new ArrayList<CoreCustomerDedup>();
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(requestData);
			responseElement = generateResponseData(detailsVo,factory);

		} catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);
			headerVo.setMessageReturnCode("9900");
			headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}
		returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private List<CoreCustomerDedup> setRequestDetails(OMElement requestData) {
		LOG.entering("setRequestDetails()");

		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		CoreCustomerDedup coreCustomerDedup = new CoreCustomerDedup();
		try {
			coreCustomerDedup.setFinReference(PFFXmlUtil.getStringValue(requestData,true, true,"ReferenceNum",requestPath));
			referenceNumber = coreCustomerDedup.getFinReference();
            this.headerVo.setTimeStamp(PFFXmlUtil.getStringValue(requestData,true, true,"TimeStamp",requestPath));
            coreCustomerDedup.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));
	        Date dob=DateUtility.getUtilDate(PFFXmlUtil.getStringValue(requestData,true, true,"DateOfBirth",requestPath), "dd/MM/yyyy");
	        coreCustomerDedup.setCustDOB(dob);
	        coreCustomerDedup.setCustPassportNo(PFFXmlUtil.getStringValue(requestData,false, false,"PassportNumber",requestPath));
	        coreCustomerDedup.setCustMotherMaiden(PFFXmlUtil.getStringValue(requestData,false, false,"MohterMaidenName",requestPath));
	        coreCustomerDedup.setCustCRCPR(PFFXmlUtil.getStringValue(requestData,true, true,"UAEID",requestPath));
	        coreCustomerDedup.setPhoneNumber(PFFXmlUtil.getStringValue(requestData,true, true,"CustomerMobileNumber",requestPath));
	        coreCustomerDedup.setCustFName(PFFXmlUtil.getStringValue(requestData,true, true,"CustomerName",requestPath));
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		List<CoreCustomerDedup> custDedupList = null;
		if (!errorFlag){
			CustomerDedupProcess process= new  CustomerDedupProcess();
			try {
				
				//Connection to the Database
				SqlConnection con=new SqlConnection();
				custDedupList =process.fetchCustDedupDetails(coreCustomerDedup,con.getConnection());
				if(custDedupList != null)  {
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("DUPLICATE FOUND");
				} else   {
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("DUPLICATE NOT FOUND"); }

			} catch (Exception e) {
				this.headerVo.setMessageReturnCode("9901");
				this.headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			} 

		} else {
			this.headerVo.setMessageReturnCode("9902");
			this.headerVo.setMessageReturnDesc("Error :"+errorMessage);
		}
		LOG.exiting("setRequestDetails()");
		return custDedupList;
	}


	public OMElement generateResponseData(List<CoreCustomerDedup> coreCustDedupList,OMFactory factory) throws ParseException {
		
		LOG.entering("generateResponseData()",coreCustDedupList,factory);		
		OMElement  responseBody=factory.createOMElement("Reply",null);         
		OMElement  createCIFRetailReply=PFFXmlUtil.setOMChildElement(factory, responseBody, "customerDuplicateCheckReply", "");
		PFFXmlUtil.getResponseStatus(factory,createCIFRetailReply, this.headerVo.getReturnCode(), this.headerVo.getMessageReturnDesc());
		PFFXmlUtil.setOMChildElement(factory, createCIFRetailReply, "ReferenceNum", referenceNumber);
		if(coreCustDedupList != null) {
			for(CoreCustomerDedup custDedup : coreCustDedupList) {
				OMElement duplicateCIF = PFFXmlUtil.setOMChildElement(factory, createCIFRetailReply, "DuplicateCIF", "");
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "CIF", custDedup.getCustCIF());
/*				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "DateOfBirth", custDedup.getCustDOB());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "FirstName", custDedup.getCustFName());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "LastName", custDedup.getCustLName());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "UAEID", custDedup.getCustCRCPR());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "PassportNumber", custDedup.getCustPassportNo());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "MobileNumber", custDedup.getMobileNumber());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "Nationality", custDedup.getCustNationality());
				PFFXmlUtil.setOMChildElement(factory, duplicateCIF, "DedupRule", custDedup.getDedupRule());*/
				createCIFRetailReply.addChild(duplicateCIF);
			}
		}
		
		LOG.exiting("generateResponseData()");
		
		return responseBody;
	}







}