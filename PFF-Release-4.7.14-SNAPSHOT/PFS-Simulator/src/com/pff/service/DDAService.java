package com.pff.service;

import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.DDAReply;
import com.pennant.interfaceservice.model.DDARequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.DDAProcess;
import com.pff.vo.PFFMQHeaderVo;

public class DDAService extends MQProcess {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public DDAService() {
		LOG = LogFactory.getLog(DDAService.class);
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
		DDARequest request =new DDARequest();
		boolean flag=false;
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/DDARegistrationRequest", requestData);

			request= (DDARequest)doUnMarshalling(detailElement, request);
			
			//Mandatory Fields
			if(request.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("ReferenceNum is Mandatory in the request");
			}
			if(request.getCustCIF().equalsIgnoreCase("")){
				throw new Exception("Customer CIF is Mandatory in the request");
			}
			if(request.getCustomerType().equalsIgnoreCase("")){
				throw new Exception("Customer Type is Mandatory in the request");
			}
			if(request.getIdType().equalsIgnoreCase("")){
				throw new Exception("Customer Id Type is Mandatory in the request");
			}
			if(String.valueOf(request.getIdNum()).equalsIgnoreCase("")){
				throw new Exception("Id Num is Mandatory in the request");
			}
			if(request.getCustomerName().equalsIgnoreCase("")){
				throw new Exception("Customer Name is Mandatory in the request");
			}
			if(request.getBankName().equalsIgnoreCase("")){
				throw new Exception("Paying Bank Name is Mandatory in the request");
			}
			if(request.getAccountType().equalsIgnoreCase("")){
				throw new Exception("Account Type is Mandatory in the request");
			}
			if(request.getIban().equalsIgnoreCase("")){
				throw new Exception("IBAN is Mandatory in the request");
			}
			if(request.getFinRefence().equalsIgnoreCase("")){
				throw new Exception("Finance Reference is Mandatory in the request");
			}
			if(request.getCommenceOn().equals("")){
				throw new Exception("First Repayment Date is Mandatory in the request");
			}
			if(String.valueOf(request.getAllowedInstances()).equalsIgnoreCase("")){
				throw new Exception("Allowed Instances is Mandatory in the request");
			}
			if(request.getCurrencyCode().equals("")){
				throw new Exception("Currency Code is Mandatory in the request");
			}
/*			if(request.getPaymentFreq().equalsIgnoreCase("")){
				throw new Exception("Payment Frequency is Mandatory in the request");
			}*/
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

				//Connection to the Database
				SqlConnection con=new SqlConnection();

				if(("DDA_REG_INITIATION").equalsIgnoreCase(this.headerVo.getMessageFormat())){
					flag=true;//process.saveUAEDDADetails(request,con.getConnection());
					reply.setReferenceNum(request.getReferenceNum());}
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

		LOG.exiting("generateResponseData", reply);

		return requestElement;
	}



}
