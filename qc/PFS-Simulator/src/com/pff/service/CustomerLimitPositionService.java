package com.pff.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.CustomerLimitPositionReply;
import com.pennant.interfaceservice.model.CustomerLimitPositionRequest;
import com.pennant.interfaceservice.model.CustomerLimitSummary;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CustomerLimitPositionProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CustomerLimitPositionService extends MQProcess {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public CustomerLimitPositionService() {
		LOG = LogFactory.getLog(CustomerLimitPositionService.class);
	}
	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CustomerLimitPositionReply detailsVo    =   new CustomerLimitPositionReply();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory);

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


	private CustomerLimitPositionReply setRequestDetails(CustomerLimitPositionReply customerLimitPositionReply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		CustomerLimitPositionRequest custLimitPositionReq =null;
		
		try {
			
			//detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/CustomerLimitSummaryRequest", requestData);
			//custLimitPositionReq= (CustomerLimitPositionRequest)doUnMarshalling(detailElement, ReferenceNum) ;
/*			
			//Mandatory Fields
			if(custLimitPositionReq.getCustomerReference().equalsIgnoreCase("")){
				throw new Exception("CustomerReference is Mandatory in the request");
			}
			if(custLimitPositionReq.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("Reference Number is Mandatory in the request");
			}
			if(custLimitPositionReq.getBranchCode().equalsIgnoreCase("")){
				throw new Exception("Branch Code is Mandatory in the request");
			}*/
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
		        CustomerLimitPositionProcess process=new CustomerLimitPositionProcess();
			try {			
				
				//Connection to the Database
			  	SqlConnection con=new SqlConnection();
			  
				customerLimitPositionReply.setCustRef("200000");
				customerLimitPositionReply.setGroupRef("GR1234");
				customerLimitPositionReply.setBranchCode("1001");
				
				List<CustomerLimitSummary> summaryList = new ArrayList<CustomerLimitSummary>();
				CustomerLimitSummary limitSummary1 = new CustomerLimitSummary();
				limitSummary1.setLimitReference("LMT12345");
				limitSummary1.setLimitDesc("Customer Limits");
				limitSummary1.setLimitCurrency("AED");
				limitSummary1.setLimitExpiryDate(new Date());
				limitSummary1.setAppovedAmount(new BigDecimal(10000000));
				limitSummary1.setBlocked(new BigDecimal(10000000));
				limitSummary1.setAvailable(new BigDecimal(10000000));
				summaryList.add(limitSummary1);
				
				CustomerLimitSummary limitSummary2 = new CustomerLimitSummary();
				limitSummary2.setLimitReference("LMT999");
				limitSummary2.setLimitDesc("Customer Limits");
				limitSummary2.setLimitCurrency("AED");
				limitSummary2.setLimitExpiryDate(new Date());
				limitSummary2.setAppovedAmount(new BigDecimal(10000000));
				limitSummary2.setBlocked(new BigDecimal(10000000));
				limitSummary2.setAvailable(new BigDecimal(8965245));
				summaryList.add(limitSummary2);
				
				customerLimitPositionReply.setLimitSummary(summaryList);
			  	//customerLimitPositionReply=process.fetchCustomerLimitPositionDetails(custLimitPositionReq.getCustomerReference(),con.getConnection());
				if(customerLimitPositionReply!=null){
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
		LOG.exiting("setRequestDetails()", customerLimitPositionReply);
		return customerLimitPositionReply;
	}


	public OMElement generateResponseData(CustomerLimitPositionReply custLimitDetailReply,OMFactory factory) throws ParseException, PFFInterfaceException {
		
		LOG.entering("generateResponseData()",custLimitDetailReply,factory);	

		OMElement requestElement = null;
		try {
			OMElement element= doMarshalling(custLimitDetailReply);
			requestElement = factory.createOMElement("Reply", null);
			requestElement.addChild(element);
		
		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}
		
		LOG.exiting("generateResponseData()", custLimitDetailReply);

		return requestElement;
	}


}
