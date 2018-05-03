package com.pff.service;

import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.ReversalPostingReply;
import com.pennant.interfaceservice.model.ReversalPostingRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.vo.PFFMQHeaderVo;

public class ReversalPostingsService extends MQProcess {

	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public ReversalPostingsService() {
		LOG = LogFactory.getLog(ReversalPostingsService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		ReversalPostingReply detailsVo           =   new ReversalPostingReply();	
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


	private ReversalPostingReply setRequestDetails(ReversalPostingReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= false;
		String errorMessage			= null;
		ReversalPostingRequest request = new ReversalPostingRequest();
		Boolean flag=true;
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/ReversalRequest", requestData);
			request= (ReversalPostingRequest) doUnMarshalling(detailElement, request);
		}
		catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			//AccountPostingProcess process=new AccountPostingProcess();
			try {
				
				//Connection to the Database
				//SqlConnection con = new SqlConnection();
				//reply = process.fetchDetails(request, con.getConnection());
				reply.setT24RefNum("99667788");
				reply.setHpsReference("1122334455");
				
				flag = true;
				if(flag){
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("SUCCESS");
				}
				else{
					throw new Exception("Reversal posting failed");
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
		reply.setReturnCode(this.headerVo.getMessageReturnCode());
		reply.setReturnText(this.headerVo.getMessageReturnDesc());
		reply.setReferenceNum(request.getReferenceNum());
		reply.setTimeStamp(System.currentTimeMillis());
		LOG.exiting("setRequestDetails()", reply);
		return reply;
	}


	public OMElement generateResponseData(ReversalPostingReply reply,OMFactory factory) throws ParseException, PFFInterfaceException {

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
