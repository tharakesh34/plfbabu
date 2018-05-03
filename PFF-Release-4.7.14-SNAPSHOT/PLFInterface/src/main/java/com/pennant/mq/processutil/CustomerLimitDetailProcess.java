package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(CustomerLimitDetailProcess.class);

	public CustomerLimitDetailProcess() {
		super();
	}
	
	/**
	 * Process the CustomerLimitDetail Request and send Response
	 * 
	 * @param limitDetail
	 * @param msgFormat
	 * @return CustomerLimitDetailReply
	 * @throws InterfaceException
	 */
	public CustomerLimitDetail getCustomerLimitDetails(CustomerLimitDetail limitDetail, String msgFormat) 
			throws InterfaceException {
		logger.debug("Entering");

		if (limitDetail == null) {
			throw new InterfaceException("PTI3001", "Customer Limit Details Cannot Be Blank");
		}

		//set MQ Message configuration details 
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(limitDetail, referenceNum, factory);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return setLimitDetailsResponse(response, header);
	}

	/**
	 * Prepare CustomerLimitDetails VO Class object with generated Response
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	private CustomerLimitDetail setLimitDetailsResponse(OMElement responseElement, AHBMQHeader header) 
			throws InterfaceException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}
		CustomerLimitDetail custLimitDetail = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/LimitDetailsReply", responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			custLimitDetail = new CustomerLimitDetail();
			//custLimitDetail = (CustomerLimitDetail) doUnMarshalling(detailElement, custLimitDetail);
			custLimitDetail.setCustCIF(PFFXmlUtil.getStringValue(detailElement, "CustRef"));
			custLimitDetail.setLimitRef(PFFXmlUtil.getStringValue(detailElement, "LimitRef"));
			custLimitDetail.setLimitDesc(PFFXmlUtil.getStringValue(detailElement, "LimitDesc"));
			custLimitDetail.setRevolvingType(PFFXmlUtil.getStringValue(detailElement, "Rev_Nrev"));
			custLimitDetail.setLimitExpiryDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(detailElement, "LimitExpiryDate"), 
					InterfaceMasterConfigUtil.SHORT_DATE));
			custLimitDetail.setLimitCcy(PFFXmlUtil.getStringValue(detailElement, "Currency"));
			custLimitDetail.setApprovedLimitCcy(PFFXmlUtil.getStringValue(detailElement, "ApprovedLimitCurrency"));
			custLimitDetail.setApprovedLimit(PFFXmlUtil.getBigDecimalValue(detailElement, "ApprovedLimit"));
			custLimitDetail.setOutstandingAmtCcy(PFFXmlUtil.getStringValue(detailElement, "OutstandingAmountCurrency"));
			custLimitDetail.setOutstandingAmt(PFFXmlUtil.getBigDecimalValue(detailElement, "OutstandingAmount"));
			custLimitDetail.setBlockedAmtCcy(PFFXmlUtil.getStringValue(detailElement, "BlockedAmountCurrency"));
			custLimitDetail.setBlockedAmt(PFFXmlUtil.getBigDecimalValue(detailElement, "BlockedAmount"));
			custLimitDetail.setReservedAmtCcy(PFFXmlUtil.getStringValue(detailElement, "ReservedAmountCurrency"));
			custLimitDetail.setReservedAmt(PFFXmlUtil.getBigDecimalValue(detailElement, "ReservedAmount"));
			custLimitDetail.setAvailableAmtCcy(PFFXmlUtil.getStringValue(detailElement, "AvailableAmountCurrency"));
			custLimitDetail.setAvailableAmt(PFFXmlUtil.getBigDecimalValue(detailElement, "AvailableAmount"));
			custLimitDetail.setTenor(PFFXmlUtil.getIntValue(detailElement, "Tenor"));
			custLimitDetail.setTenorUnit(PFFXmlUtil.getStringValue(detailElement, "TenorUnit"));
			custLimitDetail.setRepricingFrequency(PFFXmlUtil.getStringValue(detailElement, "RepricingFrequency"));
			custLimitDetail.setRepaymentTerm(PFFXmlUtil.getStringValue(detailElement, "RepaymentTerm"));
			custLimitDetail.setLimitAvailabilityPeriod(PFFXmlUtil.getStringValue(detailElement, "LimitAvailibilityPeriod"));
			custLimitDetail.setPricingIndex(PFFXmlUtil.getStringValue(detailElement, "PricingIndex"));
			custLimitDetail.setSpread(PFFXmlUtil.getBigDecimalValue(detailElement, "Spread"));
			custLimitDetail.setMinimumPrice(PFFXmlUtil.getBigDecimalValue(detailElement, "MinimumPrice"));
			custLimitDetail.setMaximumPricing(PFFXmlUtil.getBigDecimalValue(detailElement, "MaximumPricing"));
			custLimitDetail.setPricingSchema(PFFXmlUtil.getStringValue(detailElement, "PricingSchema"));
			custLimitDetail.setCommissionPercent(PFFXmlUtil.getBigDecimalValue(detailElement, "CommissionPercent"));
			custLimitDetail.setCommissionAmount(PFFXmlUtil.getBigDecimalValue(detailElement, "CommissionAmount"));
			custLimitDetail.setCommissionFreq(PFFXmlUtil.getStringValue(detailElement, "CommissionFreq"));
			custLimitDetail.setStudyFee(PFFXmlUtil.getStringValue(detailElement, "StudyFee"));
			custLimitDetail.setMargin(PFFXmlUtil.getBigDecimalValue(detailElement, "Margin"));
			custLimitDetail.setHamJad(PFFXmlUtil.getBigDecimalValue(detailElement, "HamJad"));
			custLimitDetail.setOtherFeeAmount(PFFXmlUtil.getBigDecimalValue(detailElement, "OtherFeeAmount"));
			custLimitDetail.setOtherFeePercent(PFFXmlUtil.getBigDecimalValue(detailElement, "OtherFeePercent"));
			custLimitDetail.setCovenant(PFFXmlUtil.getStringValue(detailElement, "Covenant"));
			custLimitDetail.setTermsConditions(PFFXmlUtil.getStringValue(detailElement, "TermsConditions"));
			custLimitDetail.setNotes(PFFXmlUtil.getStringValue(detailElement, "Note"));
						
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return custLimitDetail;
	}

	/**
	 * Prepare Customer LimitDetail Request Element to send Interface through MQ
	 * 
	 * @param limitDetail
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(CustomerLimitDetail limitDetail, String referenceNum, 
			OMFactory factory) throws InterfaceException {
		logger.debug("Entering");
		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement limitDetailRequest = factory.createOMElement("LimitDetailsRequest", null);

		PFFXmlUtil.setOMChildElement(factory, limitDetailRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, limitDetailRequest, "LimitRef", limitDetail.getLimitRef());
		PFFXmlUtil.setOMChildElement(factory, limitDetailRequest, "BranchCode", limitDetail.getBranchCode());
		PFFXmlUtil.setOMChildElement(factory, limitDetailRequest, "TimeStamp",	
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(limitDetailRequest);

		logger.debug("Leaving");

		return requestElement;
	}

}
