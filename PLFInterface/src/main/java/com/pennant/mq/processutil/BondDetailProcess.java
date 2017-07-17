package com.pennant.mq.processutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.nbc.BondPurchaseDetail;
import com.pennant.coreinterface.model.nbc.BondTransferDetail;
import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class BondDetailProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(BondDetailProcess.class);

	public BondDetailProcess() {
		super();
	}

	/**
	 * Process the National bond Request and send Response
	 * 
	 * @param nationalBondDetail
	 * @param msgFormat
	 * @return NationalBondDetail
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	public NationalBondDetail doNationalBondProcess(NationalBondDetail nationalBondDetail, String msgFormat)
			throws JaxenException {
		logger.debug("Entering");

		if (nationalBondDetail == null) {
			throw new InterfaceException("PTI3001", "NationalBondDetail Cannot Be Blank");
		}

		//set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		AHBMQHeader header =  new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {
			OMElement requestElement = getRequestElement(nationalBondDetail, factory, msgFormat);
			OMElement request = PFFXmlUtil.generateRequest(header, factory,requestElement);
			response = client.getRequestResponse(request.toString(), getRequestQueue(),getResponseQueue(),getWaitTime());
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}
		logger.debug("Leaving");

		return processBondDetailResponse(response, header, msgFormat);
	}

	/**
	 * Process the Handling Instruction response file
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException 
	 */
	private NationalBondDetail processBondDetailResponse(OMElement responseElement, AHBMQHeader header,
			String msgFormat) throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		String responseType = "";
		switch (msgFormat) {
		case InterfaceMasterConfigUtil.BOND_PURCHASE_INSTANT:
			responseType = "BondPurchaseReply";
			break;
		case InterfaceMasterConfigUtil.BOND_TRANSFER_MAKER:
		case InterfaceMasterConfigUtil.BOND_TRANSFER_CHECKER:
			responseType = "BondTransferReply";
			break;
		case InterfaceMasterConfigUtil.BOND_CANCEL_PURCHASE:
		case InterfaceMasterConfigUtil.BOND_CANCEL_TRANSFER:
			responseType = "BondCancelReply";
			break;
		default:
			break;
		}
		
		NationalBondDetail nationalBondDetail = null;

		try {
			OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/"+responseType, responseElement);
			header = PFFXmlUtil.parseHeader(responseElement, header);
			header = getReturnStatus(detailElement, header, responseElement);

			if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
				logger.info("ReturnStatus is Failure");
				throw new InterfaceException("PTI3002", header.getErrorMessage());
			}

			nationalBondDetail = new NationalBondDetail();
			
			nationalBondDetail.setReferenceNum(PFFXmlUtil.getStringValue(detailElement, "ReferenceNum"));
			nationalBondDetail.setReturnCode(PFFXmlUtil.getStringValue(detailElement, "ReturnCode"));
			nationalBondDetail.setReturnText(PFFXmlUtil.getStringValue(detailElement, "ReturnText"));
			nationalBondDetail.setTimeStamp(Long.parseLong(PFFXmlUtil.getStringValue(detailElement, "TimeStamp")));
			
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_PURCHASE_INSTANT)) {
				String path = "/HB_EAI_REPLY/Reply/"+responseType+"/PurchaseDetails";
				List<BondPurchaseDetail> list = getBondPurchaseDetails(detailElement, path);
				nationalBondDetail.setPurchaseDetailList(list);
				
				
			} else if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_TRANSFER_MAKER)
					|| StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_TRANSFER_CHECKER)) {
				String path = "/HB_EAI_REPLY/Reply/"+responseType+"/TransferDetails";
				List<BondTransferDetail> list = getBondTransferDetails(detailElement, path);
				nationalBondDetail.setTransferDetailList(list);
				
			}

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");

		return nationalBondDetail;
	}

	private List<BondTransferDetail> getBondTransferDetails(OMElement detailElement, String path) throws JaxenException {
		logger.debug("Entering");

		if (detailElement == null) {
			return Collections.emptyList();
		}

		List<BondTransferDetail> transferDetailList = new ArrayList<BondTransferDetail>();
		AXIOMXPath xpath = new AXIOMXPath(path);
		@SuppressWarnings("unchecked")
		List<OMElement> elementList = (List<OMElement>) xpath.selectNodes(detailElement);
		for (OMElement omElement : elementList) {
			
			BondTransferDetail detail = new BondTransferDetail();
			detail.setUnitStart(PFFXmlUtil.getStringValue(omElement, "UnitStart"));
			detail.setUnitEnd(PFFXmlUtil.getStringValue(omElement, "UnitEnd"));
			detail.setSukukNo(Long.parseLong(PFFXmlUtil.getStringValue(omElement, "SukukNo")));
			detail.setPurchaseReceiptNo(PFFXmlUtil.getStringValue(omElement, "PurchaseReceiptNo"));
			detail.setPurchaseRemainBal(PFFXmlUtil.getBigDecimalValue(omElement, "PurchaseRemainingBalance"));
			detail.setSukukExpDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(omElement, "SukukExpDate"), 
						InterfaceMasterConfigUtil.SHORT_DATE));
			detail.setTitleCertificate(PFFXmlUtil.getStringValue(omElement, "Certificate").getBytes());
			detail.setInvoiceCertificate(PFFXmlUtil.getStringValue(omElement, "InvoiceCertificate").getBytes());
			
			transferDetailList.add(detail);
		}
		logger.debug("Leaving");

		return transferDetailList;
	}

	private List<BondPurchaseDetail> getBondPurchaseDetails(OMElement detailElement, String path) throws JaxenException {
		logger.debug("Entering");

		if (detailElement == null) {
			return Collections.emptyList();
		}

		List<BondPurchaseDetail> purchaseDetailList = new ArrayList<BondPurchaseDetail>();
		AXIOMXPath xpath = new AXIOMXPath(path);
		@SuppressWarnings("unchecked")
		List<OMElement> elementList = (List<OMElement>) xpath.selectNodes(detailElement);
		for (OMElement omElement : elementList) {
			
			BondPurchaseDetail detail = new BondPurchaseDetail();
			detail.setRefNumProvider(PFFXmlUtil.getStringValue(omElement, "ReferenceNumProvider"));
			detail.setProductName(PFFXmlUtil.getStringValue(omElement, "ProductName"));
			detail.setUnitStart(PFFXmlUtil.getStringValue(omElement, "UnitStart"));
			detail.setUnitEnd(PFFXmlUtil.getStringValue(omElement, "UnitEnd"));
			detail.setSukukNo(Long.parseLong(PFFXmlUtil.getStringValue(omElement, "SukukNo")));
			detail.setSukukExpDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(omElement, "SukukExpDate"), 
						InterfaceMasterConfigUtil.SHORT_DATE));
			detail.setBankInvoiceNo(Long.parseLong(PFFXmlUtil.getStringValue(omElement, "BankInvoiceNo")));
			detail.setPurchaseReceiptNo(PFFXmlUtil.getStringValue(omElement, "PurchaseReceiptNo"));
			detail.setBankInvCertificate(PFFXmlUtil.getStringValue(omElement, "BankInvoiceCertificate").getBytes());
			detail.setBankReceiptCertifcate(PFFXmlUtil.getStringValue(omElement, "BankReceiptCertifcate").getBytes());
			detail.setBankTitleCertifcate(PFFXmlUtil.getStringValue(omElement, "BankTitleCertifcate").getBytes());
			
			purchaseDetailList.add(detail);
		}
		logger.debug("Leaving");

		return purchaseDetailList;
	}

	/**
	 * The below method do the following actions<br>
	 *  Prepare the following request element based on message format
	 *  1. BondPurchase request
	 *  2. BondTransfer (checker/maker) request
	 *  4. BondCancel (transfer/purchase) request
	 *  
	 * @param nationalBondDetail
	 * @param factory
	 * @return OMElement
	 */
	private OMElement getRequestElement(NationalBondDetail nationalBondDetail, OMFactory factory, String msgFormat)
			throws InterfaceException {
		logger.debug("Entering");

		String requestType = "";
		
		switch (msgFormat) {
		case InterfaceMasterConfigUtil.BOND_PURCHASE_INSTANT:
			requestType = "BondPurchaseRequest";
			break;
		case InterfaceMasterConfigUtil.BOND_TRANSFER_MAKER:
			requestType = "BondTransferRequest";
			break;
		case InterfaceMasterConfigUtil.BOND_TRANSFER_CHECKER:
			requestType = "BondTransferRequest";
			break;
		case InterfaceMasterConfigUtil.BOND_CANCEL_PURCHASE:
			requestType = "BondCancelRequest";
			break;
		case InterfaceMasterConfigUtil.BOND_CANCEL_TRANSFER:
			requestType = "BondCancelRequest";
			break;
		default:
			break;
		}
		
		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement detailRequest = factory.createOMElement(requestType, null);

		PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNum", PFFXmlUtil.getReferenceNumber());

		if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_PURCHASE_INSTANT)) {
			
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNumConsumer", nationalBondDetail.getRefNumConsumer());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "Amount", nationalBondDetail.getAmount());
			
		} else if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_TRANSFER_MAKER) ||
				StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.BOND_TRANSFER_CHECKER)) {
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TransferLevel", nationalBondDetail.getTransferLevel());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNumProvider", nationalBondDetail.getRefNumProvider());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNumConsumer", nationalBondDetail.getRefNumConsumer());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "AccountTitle", nationalBondDetail.getAccountTitle());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "AccountNumber", nationalBondDetail.getCustIBAN());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "Amount", nationalBondDetail.getAmount());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerName", nationalBondDetail.getCustomerName());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "CustomerType", nationalBondDetail.getCustomerType());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "MobileNumber", 
					PFFXmlUtil.unFormatPhoneNumber(nationalBondDetail.getMobileNumber()));
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "EmailAddr", nationalBondDetail.getEmailAddr());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ProductName", nationalBondDetail.getProductName());
		} else {
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNumProvider", nationalBondDetail.getRefNumProvider());
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "ReferenceNumConsumer", nationalBondDetail.getRefNumConsumer());
		}
			PFFXmlUtil.setOMChildElement(factory, detailRequest, "TimeStamp", 
				Long.valueOf(PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME)));

		requestElement.addChild(detailRequest);
		logger.debug("Leaving");

		return requestElement;
	}
}