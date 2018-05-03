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
import com.pff.process.ReserveCIFProcess;
import com.pff.vo.PFFMQHeaderVo;

public class ReserveCIFService {
	private static Log LOG = null;

	private static String requestPath="/HB_EAI_REQUEST/Request/reserveCIFRequest/";
    PFFMQHeaderVo   headerVo=null;
	public ReserveCIFService() {
		LOG = LogFactory.getLog(ReserveCIFService.class);
	}
	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		InterfaceCustomer detailsVo	        	=   new InterfaceCustomer();	
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
			customerDetails.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));
			//FIXME: field is not available in Interface Customer
			customerDetails.setCustFName(PFFXmlUtil.getStringValue(requestData,true, true,"FullName",requestPath));
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
			ReserveCIFProcess process=new ReserveCIFProcess();
			try {
				boolean flag=process.reserveCIF(customerDetails,con.getConnection());
				if(flag){
					headerVo.setMessageReturnCode("0000");
					headerVo.setMessageReturnDesc("SUCCESS");
				}
			}
			catch (Exception e) {
				this.headerVo.setMessageReturnCode("9991");
				this.headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			}
		} else {
			headerVo.setMessageReturnCode("9902");
			headerVo.setMessageReturnDesc("Error :"+errorMessage);

		}
		LOG.exiting("setRequestDetails()", customerDetails);
		return customerDetails;
	}


	public OMElement generateResponseData(InterfaceCustomer detailsVo,OMFactory factory) throws ParseException {

		LOG.entering("generateResponseData()",detailsVo,factory);	

		OMElement  responseBody=factory.createOMElement("Reply",null);         
		OMElement  createCIFRetailReply=PFFXmlUtil.setOMChildElement(factory, responseBody, "reserveCIFReply", "");
		PFFXmlUtil.setOMChildElement(factory, createCIFRetailReply, "ReferenceNum",this.headerVo.getRefNumber());
		PFFXmlUtil.getResponseStatus(factory,createCIFRetailReply, this.headerVo.getReturnCode(),this.headerVo.getMessageReturnDesc());

		LOG.exiting("generateResponseData()",detailsVo);
		
		return responseBody;
	}

}
