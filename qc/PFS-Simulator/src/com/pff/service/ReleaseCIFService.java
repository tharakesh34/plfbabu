package com.pff.service;


import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.ReleaseCIFProcess;
import com.pff.vo.PFFMQHeaderVo;

public class ReleaseCIFService {
	private static Log LOG = null;
	private static String requestPath="/HB_EAI_REQUEST/Request/releaseCIFRequest/";
    private PFFMQHeaderVo headerVo=null;

	public ReleaseCIFService() {
		LOG = LogFactory.getLog(ReleaseCIFService.class);
	}
	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		InterfaceCustomer  detailsVo			=   new InterfaceCustomer();	
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


	private InterfaceCustomer setRequestDetails(InterfaceCustomer customerDetails,OMElement requestData) {
		LOG.entering("setRequestDetails()");

		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		
		try {
			
			//customerDetails.setCustDftBranch(PFFXmlUtil.getStringValue(requestData,true, true,"ReferenceNum",requestPath));		
			customerDetails.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));
			//FIXME:Customer FullName field is not available in Interface Customer
			//PFFXmlUtil.getStringValue(requestData,true, true,"FullName",requestPath);
			customerDetails.setCustDftBranch(PFFXmlUtil.getStringValue(requestData,true, true,"BranchCode",requestPath));
			//FIXME: BranchCodeHPS field is not available in Interface Customer
			//PFFXmlUtil.getStringValue(requestData,true, false,"BranchCodeHPS",requestPath);
			this.headerVo.setTimeStamp(PFFXmlUtil.getStringValue(requestData,true, true,"TimeStamp",requestPath));
			
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			
			    //Connection to the Database
		      	 SqlConnection con=new SqlConnection(); 	
		         ReleaseCIFProcess process=new ReleaseCIFProcess();
		         try {

		        	 boolean flag=process.releaseCIF(customerDetails.getCustCIF(),con.getConnection());
		        	 if(flag)
		        	 {
		        		 headerVo.setMessageReturnCode("0000");
		        		 headerVo.setMessageReturnDesc("SUCCESS");
		        	 }
		        	 else{
		        		 throw new Exception("");
		        	 }

		         }catch (Exception e) {
		        	 headerVo.setMessageReturnCode("9901");
		        	 headerVo.setMessageReturnDesc("Error :"+e.getMessage());
		         } 

		} else {
			headerVo.setMessageReturnCode("9902");
			headerVo.setMessageReturnDesc("Error :"+errorMessage);

		}
		LOG.exiting("setRequestDetails()", customerDetails);
		LOG.exiting("setRequestDetails", customerDetails);
		return customerDetails;
	}


	public OMElement generateResponseData(InterfaceCustomer details,OMFactory factory) throws ParseException {
		
		LOG.entering("generateResponseData()",details,factory);			
		OMElement  responseBody=factory.createOMElement("Reply",null);         
		OMElement  createCIFRetailReply=PFFXmlUtil.setOMChildElement(factory, responseBody, "releaseCIFReply", "");
		PFFXmlUtil.setOMChildElement(factory, createCIFRetailReply, "ReferenceNum", "");
		PFFXmlUtil.getResponseStatus(factory,createCIFRetailReply, this.headerVo.getReturnCode(),this.headerVo.getMessageReturnDesc());
		LOG.exiting("generateResponseData()",details);
		
		return responseBody;
	}







}
