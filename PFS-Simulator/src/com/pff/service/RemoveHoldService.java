package com.pff.service;
import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.RemoveHoldReply;
import com.pennant.interfaceservice.model.RemoveHoldRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.AccountHoldProcess;
import com.pff.vo.PFFMQHeaderVo;

public class RemoveHoldService extends MQProcess {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public RemoveHoldService() {
		LOG = LogFactory.getLog(RemoveHoldService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		RemoveHoldReply  detailsVo	            =   new RemoveHoldReply();	
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


	private RemoveHoldReply setRequestDetails(RemoveHoldReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		RemoveHoldRequest removeHoldRequest =new RemoveHoldRequest();
		Boolean flag=false;
		try {
		
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/RemoveHoldRequest", requestData);
			removeHoldRequest= (RemoveHoldRequest)doUnMarshalling(detailElement, removeHoldRequest);
			
			//Mandatory Fields
			if(removeHoldRequest.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("ReferenceNum is Mandatory in the request");
			}
			if(removeHoldRequest.getAccountDetail().get(0).getAccNum().equalsIgnoreCase("")){
				throw new Exception("Account Number is Mandatory in the request");
			}
			if(removeHoldRequest.getAccountDetail().get(0).getDescription().equalsIgnoreCase("")){
				throw new Exception("Description is Mandatory in the request");
			}
			if(removeHoldRequest.getBranchCode().equalsIgnoreCase("")){
				throw new Exception("BranchCode is Mandatory in the request");
			}
			/*if(String.valueOf(addHoldRequest.getTimeStamp()).equalsIgnoreCase("")){
				throw new Exception("TimeStamp is Mandatory in the request");
			}*/
		}
		catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
		        AccountHoldProcess process=new AccountHoldProcess();
			try {
				//Connection to the Database
			  	SqlConnection con=new SqlConnection();
		
			  		flag=process.unblockAccount(removeHoldRequest,con.getConnection());
			  	
			
				if(flag){
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("SUCCESS");
				}
				else{
					throw new Exception("CustomerLimit Details Not Found");
				}				
			}

			catch (Exception e) {
				this.headerVo.setMessageReturnCode("9901");
				this.headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			} 

		} else {
			this.headerVo.setMessageReturnCode("9902");
			this.headerVo.setMessageReturnDesc("Error :"+errorMessage);
		}
		reply.setReferenceNum(removeHoldRequest.getReferenceNum());
		reply.setReturnCode(this.headerVo.getMessageReturnCode());
		reply.setReturnText(this.headerVo.getMessageReturnDesc());
		reply.setTimeStamp(System.currentTimeMillis());
		LOG.exiting("setRequestDetails()", reply);
		return reply;
	}


	public OMElement generateResponseData(RemoveHoldReply reply,OMFactory factory) throws ParseException, PFFInterfaceException {
		
		LOG.entering("generateResponseData()",reply,factory);	
		OMElement requestElement = null;
		try {
			OMElement element= doMarshalling(reply);
			requestElement = factory.createOMElement("Reply", null);
			requestElement.addChild(element);

		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}
		
		LOG.exiting("generateResponseData()", reply);

		return requestElement;
	}







}
