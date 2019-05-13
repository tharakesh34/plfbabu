package com.pff.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.CustomerLimitUtilizationReply;
import com.pennant.interfaceservice.model.CustomerLimitUtilizationRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CustomerLimitUtilizationProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CustomerLimitUtilizationService  extends MQProcess{
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public CustomerLimitUtilizationService() {
		LOG = LogFactory.getLog(CustomerLimitUtilizationService.class);
	}


	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {

		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CustomerLimitUtilizationReply detailsVo	 =   new CustomerLimitUtilizationReply();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			headerVo = PFFXmlUtil.retrieveHeader(requestData); 
			String msgFormat = headerVo.getMessageFormat();
			detailsVo = setRequestDetails(detailsVo, requestData, msgFormat);
			responseElement = generateResponseData(detailsVo,factory,this.headerVo,msgFormat);

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


	private CustomerLimitUtilizationReply setRequestDetails(CustomerLimitUtilizationReply custLimitUtilizationReply,
			OMElement requestData, String msgFormat) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		CustomerLimitUtilizationRequest request =new CustomerLimitUtilizationRequest() ;
		String parentNode = "";
		switch (msgFormat) {
		case "DEAL.ONLINE.INQUIRY":
			parentNode = "DealOnlineInquiryRequest";
			break;
		case "RESERVE":
			parentNode = "ReserveUtilizationRequest";
			break;
		case "OVERRIDE_RESERVE":
			parentNode = "OverrideReserveUtilizationRequest";
			break;
		case "CONFIRM":
			parentNode = "ConfirmReservationRequest";
			break;
		case "CANCEL_RESERVE":
			parentNode = "CancelReservationRequest";
			break;
		case "CANCEL_UTILIZATION":
			parentNode = "CancelUtilizationRequest";
			break;

		default:
			break;
		}
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/"+parentNode, requestData);
			OMElement rootElement = OMAbstractFactory.getOMFactory().createOMElement(new QName("CustomerLimitUtilization"));
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iteator = detailElement.getChildElements();
			while (iteator.hasNext()) {
				rootElement.addChild(iteator.next());
			}

			request= (CustomerLimitUtilizationRequest)doUnMarshalling(rootElement, request);
			if(request.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("ReferernceNum is Mandatory in the request");
			}
			if(request.getLimitRef().equalsIgnoreCase("")){
				throw new Exception("Limit Reference is Mandatory in the request");
			}

		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			CustomerLimitUtilizationProcess process=new CustomerLimitUtilizationProcess();
			try {

				//Connection to the Database
				SqlConnection con=new SqlConnection();

				if(("DEAL.ONLINE.INQUIRY").equalsIgnoreCase(headerVo.getMessageFormat())){
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setOverrides("NOGO");
					custLimitUtilizationReply.setMsgBreach("NO MsgBreach");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
					return custLimitUtilizationReply;
					//custLimitUtilizationReply=process.fetchCustomerLimitDetails(request,con.getConnection());	
				}
				else if("RESERVE".equalsIgnoreCase(headerVo.getMessageFormat())){
					//request.setStatus(PFFUtil.RESERVE); 
					//process.reserveUtilization(request, con.getConnection());
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setResponse("0000");
					custLimitUtilizationReply.setErrMsg("SUCESS");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
					return custLimitUtilizationReply;
				}
				else if(("CONFIRM").equalsIgnoreCase(headerVo.getMessageFormat())){
					//request.setStatus(PFFUtil.CONFIRM);
					//process.confirmUtilization(request, con.getConnection());  
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setResponse("0000");
					custLimitUtilizationReply.setErrMsg("SUCESS");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
				}  
				else if(("CANCEL_RESERVE").equalsIgnoreCase(headerVo.getMessageFormat())){
					//request.setStatus(PFFUtil.CANCEL_RESERVE);
					//process.cancelReservation(request, con.getConnection());
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setResponse("0000");
					custLimitUtilizationReply.setErrMsg("SUCESS");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
					return custLimitUtilizationReply;
				}

				else if(("CANCEL_UTILIZATION").equalsIgnoreCase(headerVo.getMessageFormat())){
					//request.setStatus(PFFUtil.CANCEL_UTILIZATION);
					//process.cancelReservation(request, con.getConnection());
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setResponse("0000");
					custLimitUtilizationReply.setErrMsg("SUCESS");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
					return custLimitUtilizationReply;
					
					//process.cancelUtilization(request, con.getConnection());
				}
				else if(("OVERRIDE_RESERVE").equalsIgnoreCase(headerVo.getMessageFormat())){
					//request.setStatus(PFFUtil.OVERRIDE_RESERVE);
					//process.reserveUtilization(request, con.getConnection());
					custLimitUtilizationReply.setReferenceNum("123456789");
					custLimitUtilizationReply.setDealID(request.getDealID());
					custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
					custLimitUtilizationReply.setLimitRef(request.getLimitRef());
					custLimitUtilizationReply.setResponse("0000");
					custLimitUtilizationReply.setErrMsg("SUCESS");
					custLimitUtilizationReply.setReturnCode("0000");
					custLimitUtilizationReply.setReturnText("SUCESS");
				}
				else{
					throw new Exception("INVALID REQUEST");
				}

				if(custLimitUtilizationReply!=null){
					headerVo.setMessageReturnCode("0000");
					headerVo.setMessageReturnDesc("SUCCESS");  
				}

			} catch (Exception e) {
				headerVo.setMessageReturnCode("9900");
				headerVo.setMessageReturnDesc(e.getMessage());
				custLimitUtilizationReply.setErrMsg(e.getMessage());
			} 

		} else {
			headerVo.setMessageReturnCode("");
			headerVo.setMessageReturnDesc(errorMessage);
		}
		custLimitUtilizationReply.setReferenceNum(request.getReferenceNum());
		custLimitUtilizationReply.setCustomerReference(request.getCustomerReference());
		custLimitUtilizationReply.setDealID(request.getDealID());
		custLimitUtilizationReply.setLimitRef(request.getLimitRef());
		custLimitUtilizationReply.setReturnCode(headerVo.getMessageReturnCode());
		custLimitUtilizationReply.setReturnText(headerVo.getMessageReturnDesc());
		custLimitUtilizationReply.setTimeStamp(Long.valueOf(PFFXmlUtil.getDateFormat(new Date(System.currentTimeMillis()))));
		LOG.exiting("setRequestDetails()", custLimitUtilizationReply);
		return custLimitUtilizationReply;
	}


	public OMElement generateResponseData(CustomerLimitUtilizationReply custLimitUtilizationReply,
			OMFactory factory,PFFMQHeaderVo headerVo, String msgFormat) throws ParseException, PFFInterfaceException {

		LOG.entering("generateResponseData()",custLimitUtilizationReply,factory);	

		OMElement requestElement = null;
		try {
			OMElement element= doMarshalling(custLimitUtilizationReply);
			OMElement rootElement = null;

			switch (msgFormat) {
			case "DEAL.ONLINE.INQUIRY":
				rootElement = factory.createOMElement(new QName("DealOnlineInquiryReply"));
				break;
			case "RESERVE":
				rootElement = factory.createOMElement(new QName("ReserveUtilizationReply"));
				break;
			case "OVERRIDE_RESERVE":
				rootElement = factory.createOMElement(new QName("OverrideReserveUtilizationReply"));
				break;
			case "CONFIRM":
				rootElement = factory.createOMElement(new QName("ConfirmReservationReply"));
				break;
			case "CANCEL_RESERVE":
				rootElement = factory.createOMElement(new QName("CancelReservationReply"));
				break;
			case "CANCEL_UTILIZATION":
				rootElement = factory.createOMElement(new QName("CancelUtilizationReply"));
				break;

			default:
				break;
			}
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iteator = element.getChildElements();
			while (iteator.hasNext()) {
				rootElement.addChild(iteator.next());
			}
			
			requestElement = factory.createOMElement("Reply", null);
			requestElement.addChild(rootElement);

		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}

		LOG.exiting("generateResponseData()", custLimitUtilizationReply);

		return requestElement;
	}
}
