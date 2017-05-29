package com.pff.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.FetchDepositDetailReply;
import com.pennant.interfaceservice.model.FetchDepositDetailRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.vo.PFFMQHeaderVo;

public class FetchDepositService extends MQProcess {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public FetchDepositService() {
		LOG = LogFactory.getLog(FetchDepositService.class);
	}


	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		FetchDepositDetailReply detailsVo	   	=   new FetchDepositDetailReply();	
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


	private FetchDepositDetailReply setRequestDetails(FetchDepositDetailReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		FetchDepositDetailRequest request =new FetchDepositDetailRequest();
		boolean flag=false;
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/GetInvestmentAccountDetailsRequest", requestData);

			request= (FetchDepositDetailRequest)doUnMarshalling(detailElement, request);
			

		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
			try {
				reply.setReferenceNum(request.getReferenceNum());
				reply.setInvstContractNo(request.getInvstContractNo());
				if(!flag){
					this.headerVo.setMessageReturnCode("0000");
					this.headerVo.setMessageReturnDesc("SUCCESS");
				}

			} catch (Exception e) {
				this.headerVo.setMessageReturnCode("9901");
				this.headerVo.setMessageReturnDesc(e.getMessage());

			} 

		reply.setTimeStamp(System.currentTimeMillis());
		reply.setReturnCode(this.headerVo.getMessageReturnCode());
		reply.setReturnText(this.headerVo.getMessageReturnDesc());
		
		LOG.exiting("setRequestDetails", reply);
		return reply;
	}


	public OMElement generateResponseData(FetchDepositDetailReply reply,OMFactory factory,PFFMQHeaderVo headerVo) throws ParseException, PFFInterfaceException {

		LOG.entering("generateResponseData()",reply,factory);	
		OMElement requestElement = null;
		try {
			
			reply.setReturnCode("0000");
			reply.setReturnText("SUCESS");
			reply.setTimeStamp(System.currentTimeMillis());
			
			reply.setCustCIF("PC1234");
			reply.setBranchCode("1001");
			reply.setCurrencyCode("AED");
			reply.setInvstAmount(new BigDecimal(100000));
			reply.setOpenDate(new Date());
			reply.setMaturityDate(new Date());
			reply.setAccountType("SA");
			reply.setAccountName("SBACCC");
			reply.setDepositTenor(10);
			reply.setFinalMaturityDate(new Date());
			reply.setAutoRollOverTenor(new BigDecimal(5));
			reply.setTotalReceivedProfit(new BigDecimal(1000));
			reply.setProfitRate(new BigDecimal(5));
			reply.setPrincipleLiquidAccount("PLACC");
			reply.setPftLiquidationAccount("PFLACC");
			reply.setLienBalance(new BigDecimal(1000));
			reply.setLienDesc("Insufficient Balance");
			reply.setStatus("ACT");
			
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
