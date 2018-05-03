package com.pff.service;

import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.DDAAmendmentRequest;
import com.pennant.interfaceservice.model.DDAReply;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.DDAProcess;
import com.pff.vo.PFFMQHeaderVo;

public class DDAAmendmentService extends MQProcess{
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public DDAAmendmentService() {
		LOG = LogFactory.getLog(DDAAmendmentService.class);
	}


	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		DDAReply detailsVo	   				=   new DDAReply();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory,this.headerVo);

		} catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);
			headerVo.setMessageReturnCode("");
			headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}
		returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private DDAReply setRequestDetails(DDAReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		DDAAmendmentRequest ammendmentReq=new DDAAmendmentRequest();
		boolean flag=false;
		try {

			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/DDAAmendmentRequest", requestData);
			ammendmentReq= (DDAAmendmentRequest) doUnMarshalling(detailElement, ammendmentReq);
						
			//Mandatory Fields
			if(ammendmentReq.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("Reference Number is Mandatory in the request");
			}
			if(ammendmentReq.getCIF().equalsIgnoreCase("")){
				throw new Exception("Customer CIF is Mandatory in the request");
			}
			if(ammendmentReq.getDDAReferenceNo().equalsIgnoreCase("")){
				throw new Exception("Registered DDA Reference is Mandatory in the request");
			}
			if(ammendmentReq.getFinRef().equalsIgnoreCase("")){
				throw new Exception("Finance Reference is Mandatory in the request");
			}
			if(ammendmentReq.getCommenceOn().equals("")){
				throw new Exception("First Repayment Date is Mandatory in the request");
			}
			if(ammendmentReq.getExpiresOn().equals("")){
				throw new Exception("Finance Maturity Date is Mandatory in the request");
			}
			if(ammendmentReq.getPaymentFreq().equalsIgnoreCase("")){
				throw new Exception("Payment Frequency is Mandatory in the request");
			}
			/*	if(String.valueOf(collateralMark.getTimeStamp()).equals("")){
			throw new Exception("Reason is Mandatory in the request");
		}*/
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			DDAProcess process=new DDAProcess();
			try {
				SqlConnection con=new SqlConnection();

				flag=process.updateDDAAmendmentDetails(ammendmentReq,con.getConnection());	
				ammendmentReq.setReferenceNum(ammendmentReq.getReferenceNum());

				if(flag){
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("SUCCESS");
				}

			} catch (Exception e) {
				this.headerVo.setMessageReturnCode("9901");
				this.headerVo.setMessageReturnDesc(e.getMessage());

			} 

		} else {
			this.headerVo.setMessageReturnCode("9902");
			this.headerVo.setMessageReturnDesc(errorMessage);

		}

		reply.setTimeStamp(System.currentTimeMillis());
		reply.setReturnCode(this.headerVo.getMessageReturnCode());
		reply.setReturnText(this.headerVo.getMessageReturnDesc());
		LOG.exiting("setRequestDetails", reply);
		return reply;
	}


	public OMElement generateResponseData(DDAReply reply,OMFactory factory,PFFMQHeaderVo headerVo) throws ParseException, PFFInterfaceException {

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
