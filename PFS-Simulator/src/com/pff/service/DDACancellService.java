package com.pff.service;

import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.penapp.interfaceexception.PFFInterfaceException;
import com.pennant.ahb.process.MQProcess;
import com.pennant.interfaceservice.model.DDACancellReply;
import com.pennant.interfaceservice.model.DDACancellRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.DDAProcess;
import com.pff.vo.PFFMQHeaderVo;

public class DDACancellService extends MQProcess  {
	private static Log LOG = null;
	PFFMQHeaderVo headerVo=null;

	public DDACancellService() {
		LOG = LogFactory.getLog(DDACancellService.class);
	}


	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		DDACancellReply detailsVo	   			=   new DDACancellReply();	
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


	private DDACancellReply setRequestDetails(DDACancellReply reply,OMElement requestData) {
		LOG.entering("setRequestDetails()");
		OMElement detailElement = null;
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		DDACancellRequest request =new DDACancellRequest();
		boolean flag=false;
		try {
			detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/DDACancellationRequest", requestData);

			request= (DDACancellRequest)doUnMarshalling(detailElement, request);
			
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

				if(("DDA.CANCELLATION").equalsIgnoreCase(this.headerVo.getMessageFormat())){
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


	public OMElement generateResponseData(DDACancellReply reply,OMFactory factory,PFFMQHeaderVo headerVo) throws ParseException, PFFInterfaceException {

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
