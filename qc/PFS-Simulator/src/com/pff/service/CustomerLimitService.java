package com.pff.service;
import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.CustomerLimitDetailReply;
import com.pennant.interfaceservice.model.CustomerLimitDetailRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CustomerLimitProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CustomerLimitService extends MQProcess {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public CustomerLimitService() {
		LOG = LogFactory.getLog(CustomerLimitService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CustomerLimitDetailReply detailsVo	    =   new CustomerLimitDetailReply();	
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


	private CustomerLimitDetailReply setRequestDetails(CustomerLimitDetailReply custLimitDetailReply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		CustomerLimitDetailRequest custLimitDetailReq =new CustomerLimitDetailRequest();
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/LimitDetailsRequest", requestData);;
			custLimitDetailReq= (CustomerLimitDetailRequest)doUnMarshalling(detailElement, custLimitDetailReq);
			if(custLimitDetailReq.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("Reference Number is Mandatory in the request");
			}
			if(custLimitDetailReq.getLimitRef().equalsIgnoreCase("")){
				throw new Exception("LimitRef is Mandatory in the request");
			}

		}
		catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
		        CustomerLimitProcess process=new CustomerLimitProcess();
			try {
				
				//Connection to the Database
			  	SqlConnection con=new SqlConnection();
			  	
			  	custLimitDetailReply=process.fetchCustomerLimitDetails(custLimitDetailReq.getLimitRef(),con.getConnection());
				if(custLimitDetailReply!=null){
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
		custLimitDetailReply.setReturnCode(this.headerVo.getMessageReturnCode());
		custLimitDetailReply.setReturnText(this.headerVo.getMessageReturnDesc());
		LOG.exiting("setRequestDetails()", custLimitDetailReply);
		return custLimitDetailReply;
	}


	public OMElement generateResponseData(CustomerLimitDetailReply custLimitDetailReply,OMFactory factory) throws ParseException, PFFInterfaceException {
		
		LOG.entering("generateResponseData()",custLimitDetailReply,factory);	

		OMElement requestElement = null;
		try {
			OMElement element= doMarshalling(custLimitDetailReply);
			requestElement = factory.createOMElement("Reply", null);
			requestElement.addChild(element);

		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}
		
		LOG.exiting("generateResponseData", custLimitDetailReply);

		return requestElement;
	}







}
