package com.pff.service;


import java.sql.SQLException;
import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.pennant.interfaceservice.model.CoreBankAccountDetail;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CreateAccountProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CreateAccountService {
	private static Log LOG = null;
	private static String requestPath="/HB_EAI_REQUEST/Request/createAccountRequest/";
	private PFFMQHeaderVo headerVo=null;


	public CreateAccountService() {
		LOG = LogFactory.getLog(CreateAccountService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CoreBankAccountDetail detailsVo			=   new CoreBankAccountDetail();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory);

		} catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);
			headerVo.setMessageReturnCode("9903");
			headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}
		returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private CoreBankAccountDetail setRequestDetails(CoreBankAccountDetail accountDetail,OMElement requestData) throws SQLException {
		LOG.entering("setRequestDetails()");

		boolean errorFlag			= 	false;
		String errorMessage			=	null;

		try {
			accountDetail.setReferenceNumber(PFFXmlUtil.getStringValue(requestData,false, false,"ReferenceNum",requestPath));	
			accountDetail.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));	
			accountDetail.setAcBranch(PFFXmlUtil.getStringValue(requestData,true, true,"BranchCode",requestPath));			
			accountDetail.setCustomerType(PFFXmlUtil.getStringValue(requestData,true, true,"CustomerType",requestPath));	
			accountDetail.setProductCode(PFFXmlUtil.getStringValue(requestData,true, true,"ProductCode",requestPath));	
			accountDetail.setAcCcy(PFFXmlUtil.getStringValue(requestData,false, true,"Currency",requestPath));	
			accountDetail.setAcFullName(PFFXmlUtil.getStringValue(requestData,false, false,"AccountName",requestPath));				
			accountDetail.setAccountOfficer(PFFXmlUtil.getStringValue(requestData,true, false,"AccountOfficer",requestPath));	
			accountDetail.setJointHolderID(PFFXmlUtil.getStringValue(requestData,false, false,"JointHolderID",requestPath+"/JointHolderID"));	
			accountDetail.setJointRelationCode(PFFXmlUtil.getStringValue(requestData,false, false,"JointRelationCode",requestPath+"/JointRelationCode"));	
			accountDetail.setRelationNotes(PFFXmlUtil.getStringValue(requestData,false, false,"RelationNotes",requestPath+"/RelationNotes"));				
			accountDetail.setModeOfOperation(PFFXmlUtil.getStringValue(requestData,false, false,"ModeOfOperation",requestPath));	
			accountDetail.setMinNoOfSignatory(PFFXmlUtil.getStringValue(requestData,false, false,"MinNoOfSignatory",requestPath));	
			accountDetail.setIntroducer(PFFXmlUtil.getStringValue(requestData,false, false,"Introducer",requestPath));	
			accountDetail.setPowerOfAttorneyFlag(Boolean.valueOf((PFFXmlUtil.getStringValue(requestData,true, true,"PowerOfAttorneyFlag",requestPath))));	
			accountDetail.setPowerOfAttorneyCIF(PFFXmlUtil.getStringValue(requestData,true, false,"PowerOfAttorneyCIF",requestPath));	
			accountDetail.setIBAN(PFFXmlUtil.getStringValue(requestData,false, false,"IBAN",requestPath));	
			accountDetail.setShoppingCardIssue(PFFXmlUtil.getStringValue(requestData,false, false,"ShoppingCardIssue",requestPath));	
		    this.headerVo.setTimeStamp(PFFXmlUtil.getStringValue(requestData,true, true,"TimeStamp",requestPath));	
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			    
			    //Connection to the Database
			    SqlConnection con=new SqlConnection();
		        CreateAccountProcess process=new CreateAccountProcess();
			try {
				accountDetail= process.createAccount(accountDetail, con.getConnection());
				headerVo.setMessageReturnCode("0000");
				headerVo.setMessageReturnDesc("SUCCESS");


			}catch(SQLException e){
				headerVo.setMessageReturnCode("8801");
				headerVo.setMessageReturnDesc("Error :"+ e.getMessage());
				throw e;
			}
			catch (Exception e) {
				headerVo.setMessageReturnCode("9900");
				headerVo.setMessageReturnDesc("Error :"+ e.getMessage());
			} 

		} else {
			headerVo.setMessageReturnCode("9901");
			headerVo.setMessageReturnDesc("Error :"+errorMessage);
		}
		LOG.exiting("setRequestDetails", accountDetail);
		return accountDetail;
	}


	public OMElement generateResponseData(CoreBankAccountDetail detailsVo,OMFactory factory) throws ParseException {
		
		LOG.entering("generateResponseData()",detailsVo,factory);	
		OMElement responseBody=factory.createOMElement("Reply",null);
		OMElement createAccountReply=PFFXmlUtil.setOMChildElement(factory, responseBody, "createAccountReply", this.headerVo.getRefNumber());
		PFFXmlUtil.setOMChildElement(factory, createAccountReply, "ReferenceNum", detailsVo.getReferenceNumber());
		PFFXmlUtil.setOMChildElement(factory, createAccountReply, "AccountNo", detailsVo.getAccountNumber());
		PFFXmlUtil.setOMChildElement(factory, createAccountReply, "IBAN", detailsVo.getIBAN());
		PFFXmlUtil.setOMChildElement(factory, createAccountReply, "CIN", detailsVo.getCIN());
		PFFXmlUtil.setOMChildElement(factory, createAccountReply, "UIN", detailsVo.getUIN());
		
		PFFXmlUtil.getResponseStatus(factory,createAccountReply,this.headerVo.getReturnCode(), this.headerVo.getMessageReturnDesc());
		
		LOG.exiting("generateResponseData()", detailsVo);
		return responseBody;
	}







}
