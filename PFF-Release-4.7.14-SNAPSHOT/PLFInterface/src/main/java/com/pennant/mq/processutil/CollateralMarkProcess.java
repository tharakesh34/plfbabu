package com.pennant.mq.processutil;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.account.AccountDetail;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.model.collateral.DepositDetail;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CollateralMarkProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(CollateralMarkProcess.class);

	private final String COLLATERAL_MARKREQ = "CollateralBlockingRequest";
	private final String COLLATERAL_MARKREPLY = "CollateralBlockingReply";
	private final String COLLATERAL_DEMARKREQ = "CollateralUnblockingRequest";
	private final String COLLATERAL_DEMARKREPLY = "CollateralUnblockingReply";
	private final String COLLATERAL_MARK = "CollateralMark";

	public CollateralMarkProcess() {
		super();
	}

	public CollateralMark markCollateral(CollateralMark collateralMarking, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		if (collateralMarking == null) {
			throw new InterfaceException("PTI3001", "CollateralMark Cannot Be Blank");
		}

		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;
		try {
			OMElement requestElement = getRequestElement(collateralMarking, referenceNum, factory, msgFormat);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setCollateralMarkReplyInfo(response, header, msgFormat);
	}

	/**
	 * Set CollateralMarkingReply object with processed Response Element
	 * 
	 * @param responseElement
	 * @param header
	 * @param msgFormat 
	 * @return
	 * @throws InterfaceException 
	 */
	private CollateralMark setCollateralMarkReplyInfo(OMElement responseElement, AHBMQHeader header, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		CollateralMark collateralReply = null;

		try {
			String parentNode = COLLATERAL_MARKREPLY;
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.COLLATERAL_DEMARKING)) {
				parentNode = COLLATERAL_DEMARKREPLY;
			}
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/"+parentNode, responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			OMElement rootElement = OMAbstractFactory.getOMFactory().createOMElement(new QName(COLLATERAL_MARK));
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iteator = detailElement.getChildElements();
			while (iteator.hasNext()) {
				rootElement.addChild(iteator.next());
			}
			collateralReply = new CollateralMark();
			collateralReply = (CollateralMark) doUnMarshalling(rootElement, collateralReply);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return collateralReply;
	}

	/**
	 * Prepare CollateralMarking Request Element to send Interface through MQ
	 * 
	 * @param collateralMark
	 * @param referenceNum
	 * @param factory
	 * @param msgFormat 
	 * @return
	 * @throws InterfaceException
	 */
	private OMElement getRequestElement(CollateralMark collateralMark, String referenceNum, OMFactory factory, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));

		OMElement detailRequest = factory.createOMElement(new QName(COLLATERAL_MARKREQ));
		if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.COLLATERAL_DEMARKING)) {
			detailRequest = factory.createOMElement(new QName(COLLATERAL_DEMARKREQ));
		}

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "BranchCode", collateralMark.getBranchCode());
		//detailRequest.addChild(getAccountDeatails(collateralMark, msgFormat));
		detailRequest.addChild(getDepositDeatails(collateralMark, msgFormat));
		PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp",
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(detailRequest);

		logger.debug("Leaving");

		return requestElement;
	}

	/**
	 * Method for preparing DepositDetails request element
	 * 
	 * @param collateralMark
	 * @param msgFormat
	 * @return
	 */
	private OMNode getDepositDeatails(CollateralMark collateralMark, String msgFormat) {
		logger.debug("Entering");

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement accDetailElement = null;
		for(DepositDetail depositDetail:collateralMark.getDepositDetail()) {
			accDetailElement = factory.createOMElement(new QName("DepositDetails"));
			PFFXmlUtil.setOMChildElement(factory, accDetailElement, "DepositID", depositDetail.getDepositID());
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.COLLATERAL_MARKING)) {
				PFFXmlUtil.setOMChildElement(factory, accDetailElement, "InsAmount", depositDetail.getInsAmount());
				PFFXmlUtil.setOMChildElement(factory, accDetailElement, "Reason", depositDetail.getReason());
			}
			PFFXmlUtil.setOMChildElement(factory, accDetailElement, "BlockingDate", DateUtility.formateDate(
					depositDetail.getBlockingDate(), InterfaceMasterConfigUtil.MQDATE));
		}
		logger.debug("Leaving");

		return accDetailElement;
	}

	/**
	 * Method for preparing Account Details request element
	 * 
	 * @param collateralMark
	 * @param msgFormat
	 * @return
	 */
	@SuppressWarnings("unused")
	private OMNode getAccountDeatails(CollateralMark collateralMark, String msgFormat) {
		logger.debug("Entering");

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement accDetailElement = null;
		accDetailElement = factory.createOMElement(new QName("AccountDetails"));
		if(collateralMark.getAccountDetail() != null) {
			for(AccountDetail accountDetail:collateralMark.getAccountDetail()) {
				PFFXmlUtil.setOMChildElement(factory, accDetailElement, "AccNum", accountDetail.getAccNum());
				if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.COLLATERAL_MARKING)) {
					PFFXmlUtil.setOMChildElement(factory, accDetailElement, "Description", accountDetail.getDescription());
					PFFXmlUtil.setOMChildElement(factory, accDetailElement, "InsAmount", accountDetail.getInsAmount());
					PFFXmlUtil.setOMChildElement(factory, accDetailElement, "BlockingDate", DateUtility.formateDate(
							accountDetail.getBlockingDate(), InterfaceMasterConfigUtil.MQDATE));
				}
			}
		}
		logger.debug("Leaving");

		return accDetailElement;
	}
}
