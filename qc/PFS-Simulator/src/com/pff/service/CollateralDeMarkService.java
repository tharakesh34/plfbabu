package com.pff.service;

import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.CollateralDeMarkingReply;
import com.pennant.interfaceservice.model.CollateralDeMarkingRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.process.CollateralDemarkProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CollateralDeMarkService extends MQProcess {

	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public CollateralDeMarkService() {
		LOG = LogFactory.getLog(CollateralDeMarkService.class);
	}

	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CollateralDeMarkingReply detailsVo      =   new CollateralDeMarkingReply();	
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


	private CollateralDeMarkingReply setRequestDetails(CollateralDeMarkingReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= false;
		String errorMessage			= null;
		CollateralDeMarkingRequest collateralMark	= new CollateralDeMarkingRequest();
		Boolean flag=false;
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/CollateralUnblockingRequest", requestData);
			//collateralMark= (CollateralDeMarkingRequest) doUnMarshalling(detailElement, collateralMark);
			
			//Mandatory Fields			
/*			if(collateralMark.getReferenceNum().equalsIgnoreCase("")){
				throw new Exception("ReferernceNum is Mandatory in the request");
			}				
			if(collateralMark.getAccountDetail().get(0).getAccNum().equalsIgnoreCase("")){
				throw new Exception("Account Number is Mandatory in the request");
			}			
			if(collateralMark.getDepositDetail().get(0).getDepositID().equals("")){
				throw new Exception("Deposit Id is Mandatory in the request");
			}
			if(collateralMark.getDepositDetail().get(0).getBlockingDate().equals("")){
				throw new Exception("Deposit Blocking Date is Mandatory in the request");
			}
			if(collateralMark.getDepositDetail().get(0).getReason().equals("")){
				throw new Exception("Reason is Mandatory in the request");
			}
			if(collateralMark.getBranchCode().equals("")){
				throw new Exception("BranchCode is Mandatory in the request");
			}*/
			/*if(String.valueOf(collateralMark.getTimeStamp()).equals("")){
				throw new Exception("Reason is Mandatory in the request");
			}*/
		}
		catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			CollateralDemarkProcess process = new CollateralDemarkProcess();
			try {
				//Connection to the Database
/*				SqlConnection con = new SqlConnection();
				
				if(!(null==collateralMark.getAccountDetail())){
					flag = process.demarkCollateralAccountDetails(collateralMark, con.getConnection());	
				}
				if(!(null==collateralMark.getDepositDetail())){
					flag = process.demarkCollateralDepositDetails(collateralMark, con.getConnection());
				}*/
		
				flag = true;
				if(flag){
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("SUCCESS");
				}
				else{
					throw new Exception("Collateral DeMarking  Is Failed");
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
		reply.setReferenceNum("987456123");
		reply.setTimeStamp(852369874);
		LOG.exiting("setRequestDetails()", reply);
		return reply;
	}


	public OMElement generateResponseData(CollateralDeMarkingReply reply,OMFactory factory) throws ParseException, PFFInterfaceException {

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
