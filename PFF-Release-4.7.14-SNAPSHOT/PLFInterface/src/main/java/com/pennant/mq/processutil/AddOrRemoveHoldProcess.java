package com.pennant.mq.processutil;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.account.AddOrRemoveHold;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class AddOrRemoveHoldProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(AddOrRemoveHoldProcess.class);

	public AddOrRemoveHoldProcess() {
		super();
	}

	/**
	 * Process the DDARequest Request and send Response
	 * 
	 * @param ddsRequest
	 * @param msgFormat
	 * @return DDARequestReply
	 * @throws InterfaceException
	 */
	public AddOrRemoveHold sendAddOrRemoveHoldReq(AddOrRemoveHold addHold, String msgFormat) throws InterfaceException  {
		logger.debug("Entering");

		if (addHold == null) {
			throw new InterfaceException("PTI3001", "AddHold Cannot Be Blank");
		}

		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {		
			OMElement requestElement = getRequestElement(addHold, referenceNum, factory, msgFormat);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setAddOrRemoveHoldReply(response, header, msgFormat);
	}

	/**
	 * Prepare DDARequestReply Object with processed response XML file data
	 * 
	 * @param responseElement
	 * @param header
	 * @param msgFormat 
	 * @return
	 */
	private AddOrRemoveHold setAddOrRemoveHoldReply(OMElement responseElement,	AHBMQHeader header, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		AddOrRemoveHold accHoldReply = null;

		try {
			String parentTag = "AddHoldReply";
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.REMOVE_HOLD)) {
				parentTag = "RemoveHoldReply";
			}

			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/"+parentTag, responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header,responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			OMElement rootElement = OMAbstractFactory.getOMFactory().createOMElement(new QName("AddOrRemoveHold"));
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iterator = detailElement.getChildElements();
			while(iterator.hasNext()) {
				rootElement.addChild(iterator.next());
			}
			accHoldReply = new AddOrRemoveHold();
			accHoldReply = (AddOrRemoveHold) doUnMarshalling(rootElement, accHoldReply);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return accHoldReply;
	}

	/**
	 * Prepare DDARequest Request Element to send Interface through MQ
	 * 
	 * @param ddsRequest
	 * @param referenceNum
	 * @param factory
	 * @param msgFormat 
	 * @return
	 * @throws InterfaceException 
	 */
	private OMElement getRequestElement(AddOrRemoveHold addHold, String referenceNum, OMFactory factory, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = null;
		addHold.setReferenceNum(referenceNum);
		OMElement element = doMarshalling(addHold);
		OMElement rootElement = factory.createOMElement(new QName("AddHoldRequest"));
		if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.REMOVE_HOLD)) {
			rootElement = factory.createOMElement(new QName("RemoveHoldRequest"));
		}
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iterator = element.getChildElements();
		while (iterator.hasNext()) {
			rootElement.addChild(iterator.next());
		}
		requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		requestElement.addChild(rootElement);

		logger.debug("Leaving");

		return requestElement;
	}

}
