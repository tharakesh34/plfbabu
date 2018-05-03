package com.pennant.mq.processutil;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAAmendmentProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(DDAAmendmentProcess.class);

	public DDAAmendmentProcess() {
		super();
	}
	
	/**
	 * Process the DDA_Amendment Request and send Response
	 * 
	 * @param ddaAmendmentReq
	 * @param msgFormat
	 * @return DDAAmendmentReply
	 * @throws InterfaceException
	 */
	public DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (ddaAmendmentReq == null) {
			throw new InterfaceException("PTI3001", "UAEDDSRequest Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(ddaAmendmentReq, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setDDAAmendmentReplyInfo(response, header);
	}

	/**
	 * Set DDAAmendmentReply object with processed Response Element
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException 
	 */
	private DDAAmendment setDDAAmendmentReplyInfo(OMElement responseElement, AHBMQHeader header) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		DDAAmendment ddaAmendmentReply = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/DDAAmendmentReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			ddaAmendmentReply = new DDAAmendment();
			ddaAmendmentReply = (DDAAmendment) doUnMarshalling(detailElement, ddaAmendmentReply);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return ddaAmendmentReply;
	}

	/**
	 * Prepare DDA Amendment Request Element to send Interface through MQ
	 * 
	 * @param ddaAmendmentReq
	 * @param referenceNum
	 * @param factory
	 * @return
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(DDAAmendment ddaAmendmentReq, String referenceNum, OMFactory factory) 
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = null;
		ddaAmendmentReq.setReferenceNum(referenceNum);
		ddaAmendmentReq
				.setTimeStamp(Long.parseLong(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		OMElement element = doMarshalling(ddaAmendmentReq);
		OMElement rootElement = factory.createOMElement(new QName("DDAAmendmentRequest"));
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iteator = element.getChildElements();
		while (iteator.hasNext()) {
			rootElement.addChild(iteator.next());
		}
		requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		requestElement.addChild(rootElement);
		logger.debug("Leaving");
		
		return requestElement;
	}

}
