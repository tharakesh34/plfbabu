package com.pennant.pff.channelsinterface;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.pennant.exception.PFFEngineException;
import com.pennant.interfaces.model.AmortizationSchedulePeriod;
import com.pennant.interfaces.model.BondRedeemDetail;
import com.pennant.interfaces.model.Categories;
import com.pennant.interfaces.model.CustomerAddres;
import com.pennant.interfaces.model.CustomerDetails;
import com.pennant.interfaces.model.CustomerEMail;
import com.pennant.interfaces.model.CustomerPhoneNumber;
import com.pennant.interfaces.model.DDAUpdateRequest;
import com.pennant.interfaces.model.DDAUpdateStatusRequest;
import com.pennant.interfaces.model.FetchCustomerFinancesRequest;
import com.pennant.interfaces.model.FetchFinCustDetailRequest;
import com.pennant.interfaces.model.FetchFinCustDetailResponse;
import com.pennant.interfaces.model.FetchFinanceDetailsRequest;
import com.pennant.interfaces.model.FetchFinanceDetailsResponse;
import com.pennant.interfaces.model.FetchFinanceRepayDetailsRequest;
import com.pennant.interfaces.model.FetchFinanceScheduleRequest;
import com.pennant.interfaces.model.FetchFinanceTransactionHistoryRequest;
import com.pennant.interfaces.model.Finance;
import com.pennant.interfaces.model.FinanceMainExt;
import com.pennant.interfaces.model.Guarantor;
import com.pennant.interfaces.model.Header;
import com.pennant.interfaces.model.JointBorrower;
import com.pennant.interfaces.model.LimitActivationRequest;
import com.pennant.interfaces.model.LimitDetails;
import com.pennant.interfaces.model.ProductCodes;
import com.pennant.interfaces.model.Repayment;
import com.pennant.interfaces.model.Transaction;
import com.pennant.pff.interfaces.util.FinanceConstants;
import com.pennant.pff.interfaces.util.XmlUtil;

public class RequestProcessor implements Runnable {
	final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

	private MQMessage reqMessage;
	private String queueManagerName;
	private PFFDataAccess dataAccess;

	private String bodyPath = "/HB_EAI_REQUEST/Request/";

	public RequestProcessor(MQMessage mqRequest, String queueManagerName,
			PFFDataAccess dataAccess) {
		logger.info("Entering");
		setReqMessage(mqRequest);
		setQueueManagerName(queueManagerName);
		setDataAccess(dataAccess);
		logger.info("leaving");
	}

	@Override
	public void run() {
		logger.info("Designated Message ID: {}", getReqMessage().messageId);
		String request;
		
		try {
			request = getReqMessage().readStringOfByteLength(
					getReqMessage().getDataLength());

			logger.info("Request: " + request);

			String messageFormat = XmlUtil.getMessageFormat(request);

			// process valid Request messages
			String response = process(request, messageFormat);

			logger.info("Placing the response...");
			putMessage(response);
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PFFEngine.usedThreads.decrementAndGet();
		logger.info("leaving");
	}

	/**
	 * Method for process Request message and send response
	 * 
	 * @param request
	 * @param messageFormat
	 * @return
	 * @throws Exception
	 */
	public String process(String request, String messageFormat)
			throws Exception {
		logger.info("Request Processor execution started.");

		OMFactory factory = null;
		OMElement requestElement = null;
		OMElement responseBody = null;

		Header header = null;

		try {
			try {
				factory = OMAbstractFactory.getOMFactory();
			} catch (Exception e) {
				e.printStackTrace();
			}

			requestElement = AXIOMUtil.stringToOM(StringUtils
					.trimToEmpty(request));
			header = XmlUtil.retrieveHeader(requestElement);
			header.setMsgFormat(messageFormat);
			// REQMODE aReqMode = REQMODE.valueOf(header.getMsgFormat());

			PFFDataAccessService dataAccessService = new PFFDataAccessService();

			switch (messageFormat) {
			case "FETCH_CUST_FINANCES":
				logger.info("Request Processor FETCH_CUST_FINANCES started.");
				String fetchCustFinRoot = "/HB_EAI_REQUEST/Request/FetchCustomerFinancesRequest";
				OMElement custElement = XmlUtil.getOMElement(fetchCustFinRoot,
						requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(custElement, true, true, "ReferenceNum",
						fetchCustFinRoot + "/");
				XmlUtil.getStringValue(custElement, true, true, "CustomerNo",
						fetchCustFinRoot + "/");
				XmlUtil.getStringValue(custElement, true, true, "TimeStamp",
						fetchCustFinRoot + "/");

				FetchCustomerFinancesRequest custBean = (FetchCustomerFinancesRequest) doUnMarshalling(
						custElement, new FetchCustomerFinancesRequest());
				List<Finance> custFinances = dataAccessService
						.getCustomerFinanceList(custBean.getCustomerNo(),
								getDataAccess());

				if (custFinances == null || custFinances.isEmpty()) {
					throw new PFFEngineException(FinanceConstants.NO_RECORDS,
							"No records found.");
				}
				responseBody = generateResponseData(requestElement,
						custFinances, factory, header, header.getMsgFormat());
				break;

			case "FETCH_FINANCE_DETAIL":
				logger.info("Request Processor FETCH_FINANCE_DETAIL started.");
				String fetchFinDetRoot = "/HB_EAI_REQUEST/Request/FetchFinanceDetailsRequest";
				OMElement finElement = XmlUtil.getOMElement(fetchFinDetRoot,
						requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(finElement, true, true, "ReferenceNum",
						fetchFinDetRoot + "/");
				XmlUtil.getStringValue(finElement, true, true, "FinanceRef",
						fetchFinDetRoot + "/");
				XmlUtil.getStringValue(finElement, true, true, "TimeStamp",
						fetchFinDetRoot + "/");

				FetchFinanceDetailsRequest finBean = (FetchFinanceDetailsRequest) doUnMarshalling(
						finElement, new FetchFinanceDetailsRequest());
				String financeRef = finBean.getFinanceRef();

				FetchFinanceDetailsResponse finDetails = dataAccessService
						.getFinanceDetails(financeRef, getDataAccess());

				if (null != finDetails) {
					finDetails.setGuarantor(dataAccessService
							.getGuarantorDetails(financeRef, getDataAccess()));
					finDetails.setJointBorrower(dataAccessService
							.getJointBorrowerDetails(financeRef,
									getDataAccess()));
				} else {
					throw new PFFEngineException(FinanceConstants.NO_RECORDS,
							"No records found.");
				}
				responseBody = generateResponseData(requestElement, finDetails,
						factory, header, header.getMsgFormat());

				break;

			case "FETCH_FIN_SCHEDULE":
				logger.info("Request Processor FETCH_FIN_SCHEDULE started.");
				String fetchFinSchRoot = "/HB_EAI_REQUEST/Request/FetchFinanceAmortizationScheduleRequest";
				OMElement schdElement = XmlUtil.getOMElement(fetchFinSchRoot,
						requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(schdElement, true, true, "ReferenceNum",
						fetchFinSchRoot + "/");
				XmlUtil.getStringValue(schdElement, true, true, "FinanceRef",
						fetchFinSchRoot + "/");
				XmlUtil.getStringValue(schdElement, true, true, "TimeStamp",
						fetchFinSchRoot + "/");

				FetchFinanceScheduleRequest schdBean = (FetchFinanceScheduleRequest) doUnMarshalling(
						schdElement, new FetchFinanceScheduleRequest());
				List<AmortizationSchedulePeriod> scheduleList = dataAccessService
						.getFinanceScheduleDetails(schdBean.getFinanceRef(),
								getDataAccess());

				if (scheduleList == null || scheduleList.isEmpty()) {
					throw new PFFEngineException(FinanceConstants.NO_RECORDS,
							"No records found.");
				}
				responseBody = generateResponseData(requestElement,
						scheduleList, factory, header, header.getMsgFormat());

				break;

			case "FETCH_FINANCE_TRANS_HIST":
				logger.info("Request Processor FETCH_FINANCE_TRANS_HIST started.");
				String fetchFinTrnsRoot = "/HB_EAI_REQUEST/Request/FetchFinanceTransactionHistoryRequest";
				OMElement histElement = XmlUtil.getOMElement(fetchFinTrnsRoot,
						requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(histElement, true, true, "ReferenceNum",
						fetchFinTrnsRoot + "/");
				XmlUtil.getStringValue(histElement, true, true, "FinanceRef",
						fetchFinTrnsRoot + "/");
				XmlUtil.getStringValue(histElement, true, true, "TimeStamp",
						fetchFinTrnsRoot + "/");

				FetchFinanceTransactionHistoryRequest histBean = (FetchFinanceTransactionHistoryRequest) doUnMarshalling(
						histElement,
						new FetchFinanceTransactionHistoryRequest());
				List<Transaction> transactionList = dataAccessService
						.getFinTransactionDetails(histBean.getFinanceRef(),
								histBean.getTransactionFromDate(),
								histBean.getTransactionToDate(),
								histBean.getTransactionFromAmount(),
								histBean.getTransactionToAmount(),
								getDataAccess());

				if (transactionList == null || transactionList.isEmpty()) {
					throw new PFFEngineException(FinanceConstants.NO_RECORDS,
							"No records found.");
				}
				responseBody = generateResponseData(requestElement,
						transactionList, factory, header, header.getMsgFormat());

				break;

			case "FIN_REPAY_DET":
				logger.info("Request Processor FIN_REPAY_DET started.");
				String finRepayDetRoot = "/HB_EAI_REQUEST/Request/FetchFinanceRepayDetailsRequest";
				OMElement repayReqElement = XmlUtil.getOMElement(
						finRepayDetRoot, requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(repayReqElement, true, true,
						"ReferenceNum", finRepayDetRoot + "/");
				XmlUtil.getStringValue(repayReqElement, true, true, "AcctNum",
						finRepayDetRoot + "/");
				XmlUtil.getStringValue(repayReqElement, true, true,
						"TimeStamp", finRepayDetRoot + "/");

				FetchFinanceRepayDetailsRequest repayReqBean = (FetchFinanceRepayDetailsRequest) doUnMarshalling(
						repayReqElement, new FetchFinanceRepayDetailsRequest());
				List<Repayment> repaymentsList = dataAccessService
						.getFinRepayDetails(repayReqBean.getAcctNum(),
								getDataAccess());

				if (repaymentsList == null || repaymentsList.isEmpty()) {
					throw new PFFEngineException(FinanceConstants.NO_RECORDS,
							"No records found.");
				}
				responseBody = generateResponseData(requestElement,
						repaymentsList, factory, header, header.getMsgFormat());

				break;

			case "LIMIT_ACTIVATION":
				logger.info("Request Processor LIMIT_ACTIVATION started.");
				String limitActRoot = "/HB_EAI_REQUEST/Request/LimitActivationRequest";
				OMElement lmtActReqElement = XmlUtil.getOMElement(limitActRoot,
						requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"ReferenceNum", limitActRoot + "/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"CustomerReference", limitActRoot + "/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"LimitRef", limitActRoot + "/" + "LimitDetails/");
				XmlUtil.getStringValue(lmtActReqElement, true, true, "Level",
						limitActRoot + "/" + "LimitDetails/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"ParentLimitRef", limitActRoot + "/" + "LimitDetails/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"Rev_Nrev", limitActRoot + "/" + "LimitDetails/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"LimitDesc", limitActRoot + "/" + "LimitDetails/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"BranchCode", limitActRoot + "/");
				XmlUtil.getStringValue(lmtActReqElement, true, true,
						"TimeStamp", limitActRoot + "/");

				LimitActivationRequest lmtActReqBean = (LimitActivationRequest) doUnMarshalling(
						lmtActReqElement, new LimitActivationRequest());

				validateMandFields(lmtActReqBean);

				boolean limitActivation = false;
				if (lmtActReqBean != null) {
					limitActivation = dataAccessService.saveLimitActivation(
							lmtActReqBean, getDataAccess());
				}

				logger.info("Debugging step 7: Value of status:"
						+ limitActivation);
				responseBody = generateResponseData(requestElement,
						limitActivation, factory, header, header.getMsgFormat());

				break;

			case "FETCH_FIN_CUST_DETAILS":
				logger.info("Request Processor FETCH_FIN_CUST_DETAILS started.");
				String fetchFinRoot = "/HB_EAI_REQUEST/Request/FetchFinCustDetailRequest";
				OMElement finCustReqElement = XmlUtil.getOMElement(
						fetchFinRoot, requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(finCustReqElement, true, true,
						"ReferenceNum", fetchFinRoot + "/");
				XmlUtil.getStringValue(finCustReqElement, true, true,
						"ISNumber", fetchFinRoot + "/");
				XmlUtil.getStringValue(finCustReqElement, true, true,
						"TimeStamp", fetchFinRoot + "/");

				FetchFinCustDetailRequest finCustReqBean = (FetchFinCustDetailRequest) doUnMarshalling(
						finCustReqElement, new FetchFinCustDetailRequest());

				FetchFinCustDetailResponse detailResponse = dataAccessService
						.getFinCustDetails(finCustReqBean.getFinReference(),
								getDataAccess());
				if (detailResponse != null) {
					detailResponse.setMobileNo(XmlUtil
							.unFormatPhoneNumber(detailResponse.getMobileNo()));
				}
				responseBody = generateResponseData(requestElement,
						detailResponse, factory, header, header.getMsgFormat());

				break;

			case "UPDATE_DDA_REF":
				logger.info("Request Processor UPDATE_DDA_REF started.");
				String ddaUpdateRoot = "/HB_EAI_REQUEST/Request/DDAUpdateRequest";
				OMElement updateDDAReqElement = XmlUtil.getOMElement(
						ddaUpdateRoot, requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(updateDDAReqElement, true, true,
						"ReferenceNum", ddaUpdateRoot + "/");
				XmlUtil.getStringValue(updateDDAReqElement, true, true,
						"ISNumber", ddaUpdateRoot + "/");
				XmlUtil.getStringValue(updateDDAReqElement, true, true, "Type",
						ddaUpdateRoot + "/");
				XmlUtil.getStringValue(updateDDAReqElement, true, true,
						"DDAReferenceNo", ddaUpdateRoot + "/");
				XmlUtil.getStringValue(updateDDAReqElement, true, true,
						"Action", ddaUpdateRoot + "/");
				XmlUtil.getStringValue(updateDDAReqElement, true, true,
						"TimeStamp", ddaUpdateRoot + "/");

				DDAUpdateRequest ddaUpdate = (DDAUpdateRequest) doUnMarshalling(
						updateDDAReqElement, new DDAUpdateRequest());

				boolean isDDAUpdated = dataAccessService.updateDDAReference(
						ddaUpdate, getDataAccess());
				responseBody = generateResponseData(requestElement,
						isDDAUpdated, factory, header, header.getMsgFormat());

				break;

			case "UPDATE_DDA_STATUS":
				logger.info("Request Processor UPDATE_ACK_NAK started.");
				String updateACK_NAK = "/HB_EAI_REQUEST/Request/DDAUpdateStatusRequest";
				OMElement updateStatusReqElement = XmlUtil.getOMElement(
						updateACK_NAK, requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"ReferenceNum", updateACK_NAK + "/");
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"ResponseType", updateACK_NAK + "/");
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"FinanceRef", updateACK_NAK + "/");
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"ResponseCode", updateACK_NAK + "/");
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"ResponseDescription", updateACK_NAK + "/");
				XmlUtil.getStringValue(updateStatusReqElement, true, true,
						"TimeStamp", updateACK_NAK + "/");

				DDAUpdateStatusRequest ddaUpdateStatus = (DDAUpdateStatusRequest) doUnMarshalling(
						updateStatusReqElement, new DDAUpdateStatusRequest());

				boolean isDDAStsUpdated = dataAccessService.updateDDAStatus(
						ddaUpdateStatus, getDataAccess());
				responseBody = generateResponseData(requestElement,
						isDDAStsUpdated, factory, header, header.getMsgFormat());

				break;

			case "NOTIFY_CUST_CHANGES":
				logger.info("Request Processor NOTIFY_CUST_CHANGES started.");
				String updateCIFRoot = "/HB_EAI_REQUEST/Request/updateCIFRetailRequest";
				OMElement updateCIFElement = XmlUtil.getOMElement(
						updateCIFRoot, requestElement);

				FetchCustomerInfo fetchCustomerInfo = new FetchCustomerInfo();
				CustomerDetails customerDetails = fetchCustomerInfo
						.getCustomerInfo(updateCIFElement, getDataAccess());
				boolean isCustomerUpdated = false;

				if (customerDetails != null) {

					// Update Customer Info
					if (customerDetails.getCustomer() != null) {
						customerDetails.getCustomer().setCustID(
								Long.valueOf(customerDetails.getCustCIF()));
						isCustomerUpdated = dataAccessService.updateCustomer(
								customerDetails.getCustomer(), "",
								getDataAccess());
					}

					// Update Customer Document details
					/*
					 * for(CustomerDocument customerDocument
					 * :customerDetails.getCustomerDocumentsList()) {
					 * customerDocument
					 * .setCustID(Long.valueOf(customerDetails.getCustCIF()));
					 * isCustomerUpdated =
					 * dataAccessService.updateCustDocuments(customerDocument,
					 * "", getDataAccess()); }
					 */

					// Update Customer employee details
					if (customerDetails.getCustEmployeeDetail() != null) {
						customerDetails.getCustEmployeeDetail().setCustID(
								Long.valueOf(customerDetails.getCustCIF()));
						isCustomerUpdated = dataAccessService
								.updateCustEmployee(
										customerDetails.getCustEmployeeDetail(),
										"", getDataAccess());
					}

					// Update Customer Address details
					for (CustomerAddres custAddress : customerDetails
							.getAddressList()) {
						if (custAddress != null) {
							custAddress.setCustID(Long.valueOf(customerDetails
									.getCustCIF()));
							isCustomerUpdated = dataAccessService
									.updateCustAddress(custAddress, "",
											getDataAccess());
						}
					}

					// Update Customer Phone number details
					for (CustomerPhoneNumber custPhoneNumber : customerDetails
							.getCustomerPhoneNumList()) {
						if (custPhoneNumber != null) {
							custPhoneNumber.setPhoneCustID(Long
									.valueOf(customerDetails.getCustCIF()));
							isCustomerUpdated = dataAccessService
									.updateCustPhonenumber(custPhoneNumber, "",
											getDataAccess());
						}
					}

					// Update Customer Email details
					for (CustomerEMail custEmail : customerDetails
							.getCustomerEMailList()) {
						if (custEmail != null) {
							custEmail.setCustID(Long.valueOf(customerDetails
									.getCustCIF()));
							isCustomerUpdated = dataAccessService
									.updateCustEmail(custEmail, "",
											getDataAccess());
						}
					}
				}
				if (!isCustomerUpdated) {
					header.setReturnCode("9007");
					header.setReturnDesc("Customer Updatation Failed");
				}
				responseBody = generateResponseData(requestElement,
						isCustomerUpdated, factory, header,
						header.getMsgFormat());
				break;

			case "SUKUK_REDEEM_POSTINGS":
				logger.info("Request Processor SUKUK_REDEEM_POSTINGS started.");
				String redeemRequest = "/HB_EAI_REQUEST/Request/SukukRedeemPostingRequest";
				OMElement redeemReqElement = XmlUtil.getOMElement(
						redeemRequest, requestElement);

				// validate Mandatory fields
				XmlUtil.getStringValue(redeemReqElement, true, true,
						"ReferenceNum", redeemRequest + "/");
				XmlUtil.getStringValue(redeemReqElement, true, true,
						"PurchaseRef", redeemRequest + "/");
				XmlUtil.getStringValue(redeemReqElement, true, true, "HostRef",
						redeemRequest + "/");
				XmlUtil.getStringValue(redeemReqElement, true, true,
						"SukukAmount", redeemRequest + "/");
				XmlUtil.getStringValue(redeemReqElement, true, true,
						"TimeStamp", redeemRequest + "/");

				BondRedeemDetail detail = (BondRedeemDetail) doUnMarshalling(
						redeemReqElement, new BondRedeemDetail());

				if (validateRedeemRequest(detail)) {
					logger.info("Debugging step 6");
					boolean bondStatus = dataAccessService
							.saveOrUpdateBondDetails(detail, getDataAccess());
					// do Postings here i.e Debit from Bank account and credit
					// to Customer account
					// FIXME: postings
					responseBody = generateResponseData(requestElement,
							bondStatus, factory, header, header.getMsgFormat());
				}
				break;

			case "PFF_SERVICE_STOP":
				logger.info("Skip processing");

				break;

			default:
				logger.error("Unable to process, Invalid Message type.");
				header.setReturnCode(FinanceConstants.INVALID_MSGTYPE);
				header.setReturnDesc("Invalid MessageType : '"
						+ header.getMsgFormat() + "'");
				responseBody = generateResponseData(requestElement, null,
						factory, header, header.getMsgFormat());
				break;
			}

		} catch (PFFEngineException pffe) {
			header.setReturnCode(pffe.getErrorCode());
			header.setReturnDesc(pffe.getErrorMessage());
			responseBody = generateResponseData(requestElement, null, factory,
					header, header.getMsgFormat());
			logger.error("Exception: {}", pffe.getErrorMessage());
		} catch (Exception e) {
			header.setReturnCode(FinanceConstants.PROCESS_FAILED);
			header.setReturnDesc("Internal error occured unable to process the request");
			responseBody = generateResponseData(requestElement, null, factory,
					header, header.getMsgFormat());
			logger.error("Exception: {}", e.getMessage());
		} finally {
			responseBody = XmlUtil.generateReturnElement(header, factory,
					requestElement, responseBody);
		}
		logger.info("Response: {}", responseBody.toString());

		return responseBody.toString();
	}

	/**
	 * Validate limit category and product codes in the request
	 * 
	 * @param lmtActReqBean
	 * @throws PFFEngineException
	 */
	private void validateMandFields(LimitActivationRequest lmtActReqBean)
			throws PFFEngineException {
		logger.debug("Entering");

		if (lmtActReqBean != null && lmtActReqBean.getLimitDetails() != null) {
			for (LimitDetails limitDetails : lmtActReqBean.getLimitDetails()) {
				// validate limit categories
				if (limitDetails.getCategories() != null
						&& !limitDetails.getCategories().isEmpty()) {
					for (Categories categories : limitDetails.getCategories()) {
						if (StringUtils.isBlank(categories.getCategory())) {
							throw new PFFEngineException(
									FinanceConstants.ATTRIBUTE_BLANK,
									"Category code could not be Blank");
						}
					}
				} else {
					throw new PFFEngineException(
							FinanceConstants.ATTRIBUTE_NOTFOUND,
							"Categories is Mandatory in the request");
				}

				// validate limit products
				if (limitDetails.getProductCodes() != null
						&& !limitDetails.getProductCodes().isEmpty()) {
					for (ProductCodes productCodes : limitDetails
							.getProductCodes()) {
						if (StringUtils.isBlank(productCodes.getProductCode())) {
							throw new PFFEngineException(
									FinanceConstants.ATTRIBUTE_BLANK,
									"Product code could not be Blank");
						}
					}
				} else {
					throw new PFFEngineException(
							FinanceConstants.ATTRIBUTE_NOTFOUND,
							"Productcodes is Mandatory in the request");
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for validate Bond Redeemtion request and throw appropriate error
	 * message
	 * 
	 * @param bondDetail
	 * @param dataAccessService
	 * @return
	 * @throws PFFEngineException
	 */
	private boolean validateRedeemRequest(BondRedeemDetail bondDetail)
			throws PFFEngineException {
		logger.debug("Entering");

		// validate
		PFFDataAccessService dataAccessService = new PFFDataAccessService();
		BondRedeemDetail detail = dataAccessService.getBondDetails(bondDetail,
				getDataAccess());

		if (detail != null && detail.isRedeemStatus()) {
			throw new PFFEngineException(FinanceConstants.SUKUK_REDEEMED,
					"Sukuk already redeemed");
		}

		FinanceMainExt financeMainExt = dataAccessService
				.getFinanceBondDetails(bondDetail.getPurchaseRef(),
						getDataAccess());

		if (financeMainExt == null) {
			throw new PFFEngineException(FinanceConstants.FINREF_NOT_EXISTS,
					"Finance Reference does not exist");
		} else if (!StringUtils.equals(financeMainExt.getHostRef(),
				bondDetail.getHostRef())) {
			throw new PFFEngineException(FinanceConstants.HOSTREF_MISSMATCH,
					"Host Reference does not match");
		} else if (financeMainExt.getSukukAmount().compareTo(
				bondDetail.getSukukAmount()) != 0) {
			throw new PFFEngineException(FinanceConstants.SUKUK_AMT_MISMATCH,
					"The value of Sukuk does not match with the purchase request");
		}

		logger.debug("Leaving");
		return true;
	}

	public OMElement generateResponseData(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {

		logger.debug("generateResponseData()", reply, factory);
		try {
			switch (processtype) {

			case "FETCH_CUST_FINANCES":
				return customerFinancesResponse(requestElement, reply, factory,
						header, processtype);

			case "FETCH_FINANCE_DETAIL":
				return financeDetailsResponse(requestElement, reply, factory,
						header, processtype);

			case "FETCH_FIN_SCHEDULE":
				return financeScheduleResponse(requestElement, reply, factory,
						header, processtype);

			case "FETCH_FINANCE_TRANS_HIST":
				return transactionHistoryResponse(requestElement, reply,
						factory, header, processtype);

			case "FIN_REPAY_DET":
				return repaymentDetailsResponse(requestElement, reply, factory,
						header, processtype);

			case "LIMIT_ACTIVATION":
				return limitActivationResponse(requestElement, reply, factory,
						header, processtype);

			case "FETCH_FIN_CUST_DETAILS":
				return fetchfinCustInstResponse(requestElement, reply, factory,
						header, processtype);

			case "UPDATE_DDA_REF":
				return updateDDARefResponce(requestElement, reply, factory,
						header, processtype);

			case "UPDATE_DDA_STATUS":
				return updateDDAStatusResponce(requestElement, reply, factory,
						header, processtype);

			case "NOTIFY_CUST_CHANGES":
				return updateCIFRetailResponce(requestElement, reply, factory,
						header, processtype);

			case "SUKUK_REDEEM_POSTINGS":
				return updateBondRedeemResponce(requestElement, reply, factory,
						header, processtype);

			default:
				return invalidMessageResponse(requestElement, reply, factory,
						header, processtype);
			}

		} catch (Exception e) {
			logger.debug("generateResponseData() Exception");
			throw new Exception(e.getMessage());
		}
	}

	private OMElement invalidMessageResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Details
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement("ErrorReply", null);

		OMElement reqElement = requestElement.getFirstChildWithName(new QName(
				"Request"));
		OMElement RefElement = reqElement.getFirstElement().getFirstElement();
		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				RefElement.getText());

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement limitActivationResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Details
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"LimitActivationReply", null);

		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "LimitActivationRequest/"));

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			if (!(Boolean) reply) {
				header.setReturnCode(FinanceConstants.PROCESS_FAILED);
				header.setReturnDesc("Internal error occured unable to process the request");
				addFailureDesc(factory, responseBody, header, processtype, true);
			} else {
				addSuccessDesc(factory, responseBody, header);
			}
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement fetchfinCustInstResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Details
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"FetchFinCustDetailReply", null);

		XmlUtil.setOMChildElement(
				factory,
				responseBody,
				"ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "FetchFinCustDetailRequest/"));
		FetchFinCustDetailResponse finCustResponse = (FetchFinCustDetailResponse) reply;
		if (finCustResponse == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			int editField = finCustResponse.getCcyEditField();
			XmlUtil.setOMChildElement(factory, responseBody, "CustomerType",
					finCustResponse.getCustomerType());
			if (StringUtils.equals(finCustResponse.getCustCtgCode(), "RETAIL")) {
				XmlUtil.setOMChildElement(factory, responseBody, "CustomerID",
						"01");
				XmlUtil.setOMChildElement(factory, responseBody,
						"CustomerIDNum", finCustResponse.getCustCRCPR());
			} else {
				XmlUtil.setOMChildElement(factory, responseBody, "CustomerID",
						"15");
				XmlUtil.setOMChildElement(factory, responseBody,
						"CustomerIDNum", finCustResponse.getTradeNumber());
			}
			XmlUtil.setOMChildElement(factory, responseBody, "CustomerName",
					finCustResponse.getCustomerName());
			XmlUtil.setOMChildElement(factory, responseBody, "MobileNo",
					finCustResponse.getMobileNo());
			XmlUtil.setOMChildElement(factory, responseBody, "EmailID",
					finCustResponse.getEmailID());
			XmlUtil.setOMChildElement(factory, responseBody, "InstallmentDate",
					XmlUtil.formatDate(finCustResponse.getInstallmentDate()));
			XmlUtil.setOMChildElement(factory, responseBody,
					"NoOfInstallments", finCustResponse.getNoOfInstallments());
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"EMIAmount",
					XmlUtil.getFormattedDecimalValue(
							finCustResponse.getEMIAmount(), editField));
			addSuccessDesc(factory, responseBody, header);
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement updateDDARefResponce(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Details
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory
				.createOMElement("DDAUpdateReply", null);

		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "DDAUpdateRequest/"));

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			if (!(Boolean) reply) {
				addFailureDesc(factory, responseBody, header, processtype, true);
			} else {
				addSuccessDesc(factory, responseBody, header);
			}
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement updateDDAStatusResponce(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for DDA Status update
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"DDAUpdateStatusReply", null);

		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "DDAUpdateStatusRequest/"));

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			if (!(Boolean) reply) {
				addFailureDesc(factory, responseBody, header, processtype, true);
			} else {
				addSuccessDesc(factory, responseBody, header);
			}
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement updateCIFRetailResponce(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");

		// Response for Update CIF Retail service
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"updateCIFRetailT24Reply", null);

		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "updateCIFRetailRequest/"));

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			if (!(Boolean) reply) {
				addFailureDesc(factory, responseBody, header, processtype, true);
			} else {
				addSuccessDesc(factory, responseBody, header);
			}
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	private OMElement updateBondRedeemResponce(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");

		// Response for Update CIF Retail service
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"SukukRedeemPostingReply", null);

		XmlUtil.setOMChildElement(
				factory,
				responseBody,
				"ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath + "SukukRedeemPostingRequest/"));

		if (reply == null) {
			addFailureDesc(factory, responseBody, header, processtype, true);
		} else {
			if (!(Boolean) reply) {
				addFailureDesc(factory, responseBody, header, processtype, true);
			} else {
				addSuccessDesc(factory, responseBody, header);
			}
		}

		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());

		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	@SuppressWarnings("unchecked")
	private OMElement customerFinancesResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Entering");
		// Response for Customer Finances
		OMElement root = factory.createOMElement("Reply", null);

		OMElement finResponse = factory.createOMElement(
				"FetchCustomerFinancesResponse", null);

		XmlUtil.setOMChildElement(factory, finResponse, "ReferenceNum", XmlUtil
				.getStringValue(requestElement, true, true, "ReferenceNum",
						bodyPath + "FetchCustomerFinancesRequest/"));

		if (null == reply) {
			addFailureDesc(factory, finResponse, header, processtype, true);
		} else {
			List<Finance> custFinances = (List<Finance>) reply;
			if (custFinances.size() > 0) {

				addSuccessDesc(factory, finResponse, header);

				XmlUtil.setOMChildElement(factory, finResponse, "CustomerNo",
						XmlUtil.getStringValue(requestElement, true, true,
								"CustomerNo", bodyPath
										+ "FetchCustomerFinancesRequest/"));

				for (Finance finance : custFinances) {
					int editField = finance.getCcyEditField();
					OMElement responseBody = factory.createOMElement("Finance",
							null);
					XmlUtil.setOMChildElement(factory, responseBody,
							"FinanceRef", finance.getFinanceRef());
					XmlUtil.setOMChildElement(factory, responseBody, "Branch",
							finance.getBranch());
					XmlUtil.setOMChildElement(factory, responseBody,
							"Currency", finance.getCurrency());
					XmlUtil.setOMChildElement(factory, responseBody,
							"StartDate",
							XmlUtil.formatDate(finance.getStartDate()));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"InstallmentAmount",
							XmlUtil.getFormattedDecimalValue(
									finance.getInstallmentAmount(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"OutstandingAmount",
							XmlUtil.getFormattedDecimalValue(
									finance.getOutstandingAmount(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"FinanceAmount",
							XmlUtil.getFormattedDecimalValue(
									finance.getFinanceAmount(), editField));
					XmlUtil.setOMChildElement(factory, responseBody, "DueDate",
							XmlUtil.formatDate(finance.getDueDate()));
					XmlUtil.setOMChildElement(factory, responseBody,
							"RemainingInstallments",
							finance.getRemainingInstallments());
					XmlUtil.setOMChildElement(factory, responseBody,
							"TotalInstallments", finance.getTotalInstallments());
					XmlUtil.setOMChildElement(factory, responseBody,
							"FinanceType", finance.getFinanceType());
					XmlUtil.setOMChildElement(factory, responseBody,
							"DaysPastDue", finance.getDaysPastDue());
					XmlUtil.setOMChildElement(factory, responseBody,
							"CustomerName", finance.getCustomerName());
					XmlUtil.setOMChildElement(factory, responseBody,
							"ProfitRate", finance.getProfitRate());
					XmlUtil.setOMChildElement(factory, responseBody,
							"ProductType", finance.getProductType());
					XmlUtil.setOMChildElement(factory, responseBody,
							"MarginRate", finance.getMarginRate());
					XmlUtil.setOMChildElement(factory, responseBody,
							"BaseRate", finance.getBaseRate());
					XmlUtil.setOMChildElement(factory, responseBody,
							"AllInRate", finance.getAllInRate());
					XmlUtil.setOMChildElement(factory, responseBody, "MinRate",
							finance.getMinRate());
					XmlUtil.setOMChildElement(factory, responseBody,
							"BankRatio", finance.getBankRatio());
					XmlUtil.setOMChildElement(factory, responseBody,
							"CustRatio", finance.getCustRatio());
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"BankProfit",
							XmlUtil.getFormattedDecimalValue(
									finance.getBankProfit(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"PrincipalPaid",
							XmlUtil.getFormattedDecimalValue(
									finance.getPrincipalPaid(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"ProfitPaid",
							XmlUtil.getFormattedDecimalValue(
									finance.getProfitPaid(), editField));
					XmlUtil.setOMChildElement(factory, responseBody,
							"MaturityDate",
							XmlUtil.formatDate(finance.getMaturityDate()));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"RepaymentAccount",
							StringUtils.trimToEmpty(
									finance.getRepaymentAccount()).equals("") ? ""
									: finance.getRepaymentAccount().substring(
											0, 12));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"ProfitAmount",
							XmlUtil.getFormattedDecimalValue(
									finance.getProfitAmount(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"TotalAmount",
							XmlUtil.getFormattedDecimalValue(
									finance.getTotalAmount(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"UnearnedProfit",
							XmlUtil.getFormattedDecimalValue(
									finance.getUnearnedProfit(), editField));
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"OutstandingBalance",
							XmlUtil.getFormattedDecimalValue(
									finance.getOutstandingBalance(), editField));
					XmlUtil.setOMChildElement(factory, responseBody,
							"FinanceStatus",
							finance.getFinanceStatus().length() > 7 ? finance
									.getFinanceStatus().substring(0, 7)
									: finance.getFinanceStatus());
					finResponse.addChild(responseBody);
				}
			} else {
				addFailureDesc(factory, finResponse, header, processtype, false);
				/*
				 * XmlUtil.setOMChildElement(factory, finResponse, "CustomerNo",
				 * XmlUtil.getStringValue(requestElement,true, true,
				 * "CustomerNo", bodyPath+"FetchCustomerFinancesRequest/"));
				 * XmlUtil.setOMChildElement(factory, finResponse,
				 * "Finance",null);
				 */
			}
		}
		XmlUtil.setOMChildElement(factory, finResponse, "TimeStamp",
				getCurrentTime());

		root.addChild(finResponse);

		logger.debug("Leaving");
		return root;
	}

	private OMElement financeDetailsResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Details
		OMElement root = factory.createOMElement("Reply", null);
		OMElement responseBody = factory.createOMElement(
				"FetchFinanceDetailsResponse", null);

		XmlUtil.setOMChildElement(factory, responseBody, "ReferenceNum",
				XmlUtil.getStringValue(requestElement, false, false,
						"ReferenceNum", bodyPath
								+ "FetchFinanceDetailsRequest/"));

		if (null == reply) {
			addFailureDesc(factory, responseBody, header, processtype, false);
		} else {
			FetchFinanceDetailsResponse financeDetails = (FetchFinanceDetailsResponse) reply;

			int editField = financeDetails.getCcyEditField();

			addSuccessDesc(factory, responseBody, header);

			XmlUtil.setOMChildElement(factory, responseBody, "CustomerNo",
					financeDetails.getCustomerNo());
			XmlUtil.setOMChildElement(factory, responseBody, "FinanceRef",
					financeDetails.getFinanceRef());
			XmlUtil.setOMChildElement(factory, responseBody, "FinanceType",
					financeDetails.getFinanceType());
			XmlUtil.setOMChildElement(factory, responseBody, "Branch",
					financeDetails.getBranch());
			XmlUtil.setOMChildElement(factory, responseBody, "Currency",
					financeDetails.getCurrency());
			XmlUtil.setOMChildElement(factory, responseBody, "StartDate",
					XmlUtil.formatDate(financeDetails.getStartDate()));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"InstallmentAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getInstallmentAmount(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"OutstandingAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getOutstandingAmount(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"FinanceAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getFinanceAmount(), editField));
			XmlUtil.setOMChildElement(factory, responseBody, "DueDate",
					XmlUtil.formatDate(financeDetails.getDueDate()));
			XmlUtil.setOMChildElement(factory, responseBody,
					"RemainingInstallments",
					financeDetails.getRemainingInstallments());
			XmlUtil.setOMChildElement(factory, responseBody,
					"TotalInstallments", financeDetails.getTotalInstallments());
			XmlUtil.setOMChildElement(factory, responseBody, "DaysPastDue",
					financeDetails.getDaysPastDue());
			XmlUtil.setOMChildElement(factory, responseBody, "CustomerName",
					financeDetails.getCustomerName());
			XmlUtil.setOMChildElement(factory, responseBody, "ContractDate",
					XmlUtil.formatDate(financeDetails.getContractDate()));
			XmlUtil.setOMChildElement(factory, responseBody, "FinanceStatus",
					financeDetails.getFinanceStatus());
			XmlUtil.setOMChildElement(factory, responseBody, "ModelName",
					financeDetails.getModelName());
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"DisbursedAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getDisbursedAmount(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"DownPaymentAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getDownPaymentAmount(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"RepaidAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getRepaidAmount(), editField));
			XmlUtil.setOMChildElement(factory, responseBody,
					"LastInstallmentDate",
					XmlUtil.formatDate(financeDetails.getLastInstallmentDate()));
			XmlUtil.setOMChildElement(factory, responseBody,
					"LastInstallmentAmount", XmlUtil.getFormattedDecimalValue(
							financeDetails.getLastInstallmentAmount(),
							editField));
			XmlUtil.setOMChildElement(factory, responseBody, "FinanceTenor",
					financeDetails.getFinanceTenor());

			// Guarantor Details
			OMElement guarantor = factory.createOMElement("Guarantor", null);
			for (Guarantor detail : financeDetails.getGuarantor()) {
				XmlUtil.setOMChildElement(factory, guarantor, "CustomerNo",
						detail.getCustomerNo());
				XmlUtil.setOMChildElement(factory, guarantor, "CustomerName",
						detail.getCustomerName());
			}
			responseBody.addChild(guarantor);

			// JointBorrower Deatils
			OMElement jointBorrower = factory.createOMElement("JointBorrower",
					null);
			for (JointBorrower detail : financeDetails.getJointBorrower()) {
				XmlUtil.setOMChildElement(factory, jointBorrower, "CustomerNo",
						detail.getCustomerNo());
				XmlUtil.setOMChildElement(factory, jointBorrower,
						"CustomerName", detail.getCustomerName());
			}
			responseBody.addChild(jointBorrower);

			XmlUtil.setOMChildElement(factory, responseBody,
					"RepaymentFrequency",
					financeDetails.getRepaymentFrequency());
			XmlUtil.setOMChildElement(factory, responseBody, "ProfitRate",
					financeDetails.getProfitRate());
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"PastDueAmount",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getPastDueAmount(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"RepaymentAccount",
					StringUtils.trimToEmpty(
							financeDetails.getRepaymentAccount()).equals("") ? ""
							: financeDetails.getRepaymentAccount().substring(0,
									12));
			XmlUtil.setOMChildElement(factory, responseBody, "ProductType",
					financeDetails.getProductType());
			XmlUtil.setOMChildElement(factory, responseBody, "MarginRate",
					financeDetails.getMarginRate());
			XmlUtil.setOMChildElement(factory, responseBody, "BaseRate",
					financeDetails.getBaseRate());
			XmlUtil.setOMChildElement(factory, responseBody, "AllInRate",
					financeDetails.getAllInRate());
			XmlUtil.setOMChildElement(factory, responseBody, "MinRate",
					financeDetails.getMinRate());
			XmlUtil.setOMChildElement(factory, responseBody, "BankRatio",
					financeDetails.getBankRatio());
			XmlUtil.setOMChildElement(factory, responseBody, "CustRatio",
					financeDetails.getCustRatio());
			XmlUtil.setOMChildElement(factory, responseBody, "BankProfit",
					financeDetails.getBankProfit());
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"PrincipalPaid",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getPrincipalPaid(), editField));
			XmlUtil.setOMChildElement(
					factory,
					responseBody,
					"ProfitPaid",
					XmlUtil.getFormattedDecimalValue(
							financeDetails.getProfitPaid(), editField));
		}
		XmlUtil.setOMChildElement(factory, responseBody, "TimeStamp",
				getCurrentTime());
		root.addChild(responseBody);
		logger.debug("Leaving");
		return root;
	}

	@SuppressWarnings("unchecked")
	private OMElement financeScheduleResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Schedule Details
		OMElement root = factory.createOMElement("Reply", null);

		OMElement finResponse = factory.createOMElement(
				"FetchFinanceAmortizationScheduleResponse", null);

		XmlUtil.setOMChildElement(factory, finResponse, "ReferenceNum", XmlUtil
				.getStringValue(requestElement, true, true, "ReferenceNum",
						bodyPath + "FetchFinanceAmortizationScheduleRequest/"));

		if (null == reply) {
			addFailureDesc(factory, finResponse, header, processtype, true);
		} else {

			List<AmortizationSchedulePeriod> scheduleDetails = (List<AmortizationSchedulePeriod>) reply;
			if (scheduleDetails.size() > 0) {
				addSuccessDesc(factory, finResponse, header);
				for (AmortizationSchedulePeriod schedule : scheduleDetails) {
					int editField = schedule.getCcyEditField();
					OMElement responseBody = factory.createOMElement(
							"AmortizationSchedulePeriod", null);
					XmlUtil.setOMChildElement(factory, responseBody,
							"InstallmentNo", schedule.getInstallmentNo());
					XmlUtil.setOMChildElement(factory, responseBody,
							"ScheduleDate",
							XmlUtil.formatDate(schedule.getScheduleDate()));

					if (StringUtils.equals(header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_RIB)) {
						XmlUtil.setOMChildElement(factory, responseBody,
								"OpeningBalance",
								XmlUtil.getFormattedDecimalValue(
										schedule.getOpeningBalance().add(
												schedule.getProfitAmount()),
										editField));
					} else {
						XmlUtil.setOMChildElement(factory, responseBody,
								"OpeningBalance", XmlUtil
										.getFormattedDecimalValue(
												schedule.getOpeningBalance(),
												editField));
					}

					if (StringUtils.equals(schedule.getStatus(),
							FinanceConstants.INSTALLMENT_STATUS_DISBURSE)) {
						XmlUtil.setOMChildElement(
								factory,
								responseBody,
								"InstallmentAmount",
								XmlUtil.getFormattedDecimalValue(
										schedule.getInstallmentAmount(),
										editField).multiply(new BigDecimal(-1)));
					} else {
						XmlUtil.setOMChildElement(factory, responseBody,
								"InstallmentAmount",
								XmlUtil.getFormattedDecimalValue(
										schedule.getInstallmentAmount(),
										editField));
					}

					if (StringUtils.equals(header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_CIB)) {
						if (StringUtils.equals(schedule.getStatus(),
								FinanceConstants.INSTALLMENT_STATUS_DISBURSE)) {
							if (StringUtils.equals(schedule.getFinCategory(),
									FinanceConstants.PRODUCT_IJARAH)) {
								XmlUtil.setOMChildElement(
										factory,
										responseBody,
										"PrincipalAmount",
										XmlUtil.getFormattedDecimalValue(
												schedule.getInstallmentAmount(),
												editField).multiply(
												new BigDecimal(-1)));
							} else if (StringUtils.equals(
									schedule.getFinCategory(),
									FinanceConstants.PRODUCT_MURABAHA)) {
								XmlUtil.setOMChildElement(
										factory,
										responseBody,
										"PrincipalAmount",
										XmlUtil.getFormattedDecimalValue(
												schedule.getInstallmentAmount()
														.add(schedule
																.getProfitAmount()),
												editField));
							}
						} else if (StringUtils.equals(
								schedule.getFinCategory(),
								FinanceConstants.PRODUCT_MURABAHA)) {
							XmlUtil.setOMChildElement(factory, responseBody,
									"PrincipalAmount", XmlUtil
											.getFormattedDecimalValue(schedule
													.getInstallmentAmount(),
													editField));
						} else {
							XmlUtil.setOMChildElement(factory, responseBody,
									"PrincipalAmount", XmlUtil
											.getFormattedDecimalValue(schedule
													.getPrincipalAmount(),
													editField));
						}
					} else {
						XmlUtil.setOMChildElement(factory, responseBody,
								"PrincipalAmount", XmlUtil
										.getFormattedDecimalValue(
												schedule.getPrincipalAmount(),
												editField));
					}

					if (StringUtils.equals(header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_CIB)) {
						if (StringUtils.equals(schedule.getStatus(),
								FinanceConstants.INSTALLMENT_STATUS_DISBURSE)) {
							if (StringUtils.equals(schedule.getFinCategory(),
									FinanceConstants.PRODUCT_IJARAH)) {
								XmlUtil.setOMChildElement(factory,
										responseBody, "ProfitAmount", XmlUtil
												.getFormattedDecimalValue(
														BigDecimal.ZERO,
														editField));
							} else if (StringUtils.equals(
									schedule.getFinCategory(),
									FinanceConstants.PRODUCT_MURABAHA)) {
								XmlUtil.setOMChildElement(factory,
										responseBody, "ProfitAmount",
										XmlUtil.getFormattedDecimalValue(
												schedule.getProfitAmount(),
												editField));
							}
						} else if (StringUtils.equals(
								schedule.getFinCategory(),
								FinanceConstants.PRODUCT_MURABAHA)) {
							XmlUtil.setOMChildElement(factory, responseBody,
									"ProfitAmount", XmlUtil
											.getFormattedDecimalValue(
													BigDecimal.ZERO, editField));
						} else {
							XmlUtil.setOMChildElement(factory, responseBody,
									"ProfitAmount", XmlUtil
											.getFormattedDecimalValue(
													schedule.getProfitAmount(),
													editField));
						}
					} else {
						XmlUtil.setOMChildElement(
								factory,
								responseBody,
								"ProfitAmount",
								XmlUtil.getFormattedDecimalValue(
										schedule.getProfitAmount(), editField));
					}

					if (StringUtils.equals(header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_CIB)) {
						if (StringUtils.equals(schedule.getFinCategory(),
								FinanceConstants.PRODUCT_MURABAHA)) {
							XmlUtil.setOMChildElement(factory, responseBody,
									"PastDueAmount", null);
						} else {
							XmlUtil.setOMChildElement(factory, responseBody,
									"PastDueAmount",
									XmlUtil.getFormattedDecimalValue(
											schedule.getPastDueAmount(),
											editField));
						}
					} else if (StringUtils.equals(
							header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_RIB)) {
						XmlUtil.setOMChildElement(
								factory,
								responseBody,
								"PastDueAmount",
								XmlUtil.getFormattedDecimalValue(
										schedule.getPastDueAmount(), editField));
					}

					XmlUtil.setOMChildElement(factory, responseBody,
							"ChargeAmount", XmlUtil.getFormattedDecimalValue(
									BigDecimal.ZERO, editField));

					if (StringUtils.equals(header.getRequestorChannelId(),
							FinanceConstants.CHANNEL_RIB)) {
						XmlUtil.setOMChildElement(factory, responseBody,
								"ClosingBalance",
								XmlUtil.getFormattedDecimalValue(
										schedule.getClosingBalance().add(
												schedule.getProfitAmount()),
										editField));
					} else {
						if (StringUtils.equals(schedule.getStatus(),
								FinanceConstants.INSTALLMENT_STATUS_DISBURSE)) {
							if (StringUtils.equals(schedule.getFinCategory(),
									FinanceConstants.PRODUCT_IJARAH)) {
								XmlUtil.setOMChildElement(
										factory,
										responseBody,
										"ClosingBalance",
										XmlUtil.getFormattedDecimalValue(
												schedule.getInstallmentAmount(),
												editField).multiply(
												new BigDecimal(-1)));
							} else if (StringUtils.equals(
									schedule.getFinCategory(),
									FinanceConstants.PRODUCT_MURABAHA)) {
								XmlUtil.setOMChildElement(
										factory,
										responseBody,
										"ClosingBalance",
										XmlUtil.getFormattedDecimalValue(
												schedule.getInstallmentAmount()
														.add(schedule
																.getProfitAmount()),
												editField));
							}
						} else {
							XmlUtil.setOMChildElement(factory, responseBody,
									"ClosingBalance", XmlUtil
											.getFormattedDecimalValue(schedule
													.getClosingBalance(),
													editField));
						}
					}
					XmlUtil.setOMChildElement(factory, responseBody, "Status",
							schedule.getStatus());
					finResponse.addChild(responseBody);
				}
			} else {
				addFailureDesc(factory, finResponse, header, processtype, false);
			}
		}
		XmlUtil.setOMChildElement(factory, finResponse, "TimeStamp",
				getCurrentTime());

		root.addChild(finResponse);

		logger.debug("Leaving");
		return root;
	}

	@SuppressWarnings("unchecked")
	private OMElement transactionHistoryResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Transaction Details
		OMElement root = factory.createOMElement("Reply", null);

		OMElement finResponse = factory.createOMElement(
				"FetchFinanceTransactionHistoryResponse", null);

		XmlUtil.setOMChildElement(factory, finResponse, "ReferenceNum", XmlUtil
				.getStringValue(requestElement, true, true, "ReferenceNum",
						bodyPath + "FetchFinanceTransactionHistoryRequest/"));

		if (null == reply) {
			addFailureDesc(factory, finResponse, header, processtype, true);
		} else {
			List<Transaction> txnHistory = (List<Transaction>) reply;
			if (txnHistory.size() > 0) {
				addSuccessDesc(factory, finResponse, header);
				for (Transaction transaction : txnHistory) {
					int editField = transaction.getCcyEditField();
					OMElement responseBody = factory.createOMElement(
							"Transaction", null);
					XmlUtil.setOMChildElement(factory, responseBody,
							"TransactionDate", XmlUtil.formatDate(transaction
									.getTransactionDate()));
					XmlUtil.setOMChildElement(factory, responseBody,
							"AccountNumber", transaction.getAccountNumber());
					XmlUtil.setOMChildElement(factory, responseBody,
							"Currency", transaction.getCurrency());
					XmlUtil.setOMChildElement(factory, responseBody,
							"TransactionRef", transaction.getTransactionRef());
					if (StringUtils.equals(transaction.getDrOrCr(), "C")) {
						XmlUtil.setOMChildElement(factory, responseBody,
								"DebitAmount", null);
						XmlUtil.setOMChildElement(
								factory,
								responseBody,
								"CreditAmount",
								XmlUtil.getFormattedDecimalValue(
										transaction.getTxnAmount(), editField));
					} else {
						XmlUtil.setOMChildElement(
								factory,
								responseBody,
								"DebitAmount",
								XmlUtil.getFormattedDecimalValue(
										transaction.getTxnAmount(), editField));
						XmlUtil.setOMChildElement(factory, responseBody,
								"CreditAmount", null);
					}
					XmlUtil.setOMChildElement(factory, responseBody,
							"TransactionNarration",
							transaction.getTransactionNarration());
					XmlUtil.setOMChildElement(
							factory,
							responseBody,
							"RunningBalance",
							XmlUtil.getFormattedDecimalValue(
									transaction.getRunningBalance(), editField));
					XmlUtil.setOMChildElement(factory, responseBody,
							"OutstandingBalance",
							XmlUtil.getFormattedDecimalValue(
									transaction.getOutstandingBalance(),
									editField));
					finResponse.addChild(responseBody);
				}
			} else {
				addFailureDesc(factory, finResponse, header, processtype, false);
			}
		}
		XmlUtil.setOMChildElement(factory, finResponse, "TimeStamp",
				getCurrentTime());

		root.addChild(finResponse);

		logger.debug("Leaving");
		return root;
	}

	@SuppressWarnings("unchecked")
	private OMElement repaymentDetailsResponse(OMElement requestElement,
			Object reply, OMFactory factory, Header header, String processtype)
			throws Exception {
		logger.debug("Leaving");
		// Response for Finance Repayment Details
		OMElement root = factory.createOMElement("Reply", null);

		OMElement finResponse = factory.createOMElement(
				"FetchFinanceRepayDetailsResponse", null);

		XmlUtil.setOMChildElement(factory, finResponse, "ReferenceNum", XmlUtil
				.getStringValue(requestElement, true, true, "ReferenceNum",
						bodyPath + "FetchFinanceRepayDetailsRequest/"));

		if (null == reply) {
			addFailureDesc(factory, finResponse, header, processtype, true);
		} else {
			List<Repayment> repaymentsList = (List<Repayment>) reply;
			if (repaymentsList.size() > 0) {
				addSuccessDesc(factory, finResponse, header);
				for (Repayment repayment : repaymentsList) {
					int editField = repayment.getCcyEditField();
					OMElement responseBody = factory.createOMElement(
							"Repayment", null);
					XmlUtil.setOMChildElement(factory, responseBody,
							"FinanceType", repayment.getFinanceType());
					XmlUtil.setOMChildElement(factory, responseBody,
							"FinanceRef", repayment.getFinanceRef());
					XmlUtil.setOMChildElement(factory, responseBody,
							"ScheduleDate",
							XmlUtil.formatDate(repayment.getScheduleDate()));
					XmlUtil.setOMChildElement(factory, responseBody,
							"Currency", repayment.getCurrency());
					XmlUtil.setOMChildElement(factory, responseBody,
							"InstallmentAmount", XmlUtil
									.getFormattedDecimalValue(
											repayment.getInstallmentAmount(),
											editField));
					finResponse.addChild(responseBody);
				}
			} else {
				addFailureDesc(factory, finResponse, header, processtype, false);
			}
		}
		XmlUtil.setOMChildElement(factory, finResponse, "TimeStamp",
				getCurrentTime());

		root.addChild(finResponse);

		logger.debug("Leaving");
		return root;
	}

	private void addSuccessDesc(OMFactory factory, OMElement responseBody,
			Header header) throws ParseException {
		logger.debug("Entering");
		header.setReturnCode(FinanceConstants.SUCCESS);
		header.setReturnDesc("Success");
		XmlUtil.getResponseStatus(factory, responseBody,
				header.getReturnCode(), header.getReturnDesc());
		logger.debug("Leaving");
	}

	private void addFailureDesc(OMFactory factory, OMElement responseBody,
			Header header, String processtype, boolean failed)
			throws ParseException {
		logger.debug("Entering");

		switch (processtype) {

		case "FETCH_CUST_FINANCES":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "FETCH_FINANCE_DETAIL":
		case "FETCH_FIN_SCHEDULE":
		case "FETCH_FINANCE_TRANS_HIST":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "FIN_REPAY_DET":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "LIMIT_ACTIVATION":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "FETCH_FIN_CUST_DETAILS":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "UPDATE_DDA_REF":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "UPDATE_DDA_STATUS":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "NOTIFY_CUST_CHANGES":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		case "SUKUK_REDEEM_POSTINGS":
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;

		default:
			header.setReturnCode(FinanceConstants.INVALID_MSGTYPE);
			header.setReturnDesc("Invalid MessageType : '"
					+ header.getMsgFormat() + "'");
			XmlUtil.getResponseStatus(factory, responseBody,
					header.getReturnCode(), header.getReturnDesc());
			break;
		}
		logger.debug("Leaving");
	}

	/**
	 * Marshalling OBJECT to XML Element
	 * 
	 * @param request
	 * @return OMElement
	 * @throws PFFInterfaceException
	 */
	public OMElement doMarshalling(Object request) throws PFFEngineException {

		if (request == null) {
			throw new PFFEngineException(FinanceConstants.EMPTY_REQUEST,
					"Request Element is Empty");
		}
		StringWriter writer = new StringWriter();
		OMElement element = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(request.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(request, writer);
			element = AXIOMUtil.stringToOM(writer.toString());
		} catch (Exception e) {
			throw new PFFEngineException(FinanceConstants.INVALID_REQUEST,
					"Invalid Request element, Unable to parse");
		}
		return element;
	}

	/**
	 * UnMarshalling XML Element to Object
	 * 
	 * @param request
	 * @param classType
	 * @return Object
	 * @throws PFFInterfaceException
	 */
	public Object doUnMarshalling(OMElement request, Object classType)
			throws PFFEngineException {

		if (request == null) {
			throw new PFFEngineException(FinanceConstants.EMPTY_REQUEST,
					"Response Element is Empty");
		}
		Object resObject = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(classType.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			resObject = unmarshaller.unmarshal(request.getXMLStreamReader());
		} catch (Exception e) {
			throw new PFFEngineException(FinanceConstants.INVALID_REQUEST,
					e.getMessage());
		}
		return resObject;
	}

	protected void putMessage(String content) {
		logger.debug("Entering Placing response ");
		MQMessage message = new MQMessage();
		int openOptions = CMQC.MQOO_OUTPUT | CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue mqQueueReply = null;
		MQQueueManager manager = null;

		try {
			logger.debug("Response Queue Manager Connecting :: ");
			manager = new MQQueueManager(getQueueManagerName());
			mqQueueReply = manager.accessQueue(
					getReqMessage().replyToQueueName, openOptions,
					getReqMessage().replyToQueueManagerName, null, null);

			logger.debug("Response Queue Manager Connected :: ");

			// message.format = CMQC.MQFMT_STRING;

			// Set the correlation ID as input message id
			message.correlationId = getReqMessage().messageId;

			message.writeString(content);

			message.replyToQueueName = getReqMessage().replyToQueueName;

			message.replyToQueueManagerName = getReqMessage().replyToQueueManagerName;

			logger.debug("Response Message ID :: " + message.correlationId);

			long reqPlacingTime = System.currentTimeMillis();

			logger.debug("Placing request at :: " + reqPlacingTime);

			mqQueueReply.put(message, new MQPutMessageOptions());

			long reqPlacedTime = System.currentTimeMillis();

			logger.debug("Placed request at :: " + reqPlacedTime);

			logger.debug("Time taken to place response :: "
					+ (reqPlacedTime - reqPlacingTime));

		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			message = null;
			// Close Queue
			try {
				if (mqQueueReply != null) {
					logger.info(" closing queue ");
					mqQueueReply.close();
					logger.info(" closed queue ");
				}
			} catch (MQException e) {
				e.printStackTrace();
			} finally {
				mqQueueReply = null;
			}
			try {
				if (manager != null) {
					manager.disconnect();
				}
			} catch (MQException e) {
				e.printStackTrace();
			} finally {
				manager = null;
			}
		}
	}

	/***
	 * method to return current time
	 * 
	 * @return
	 */
	private String getCurrentTime() {
		logger.debug("Entering");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		java.util.Date uDate = new Date(System.currentTimeMillis());
		logger.debug("Leaving");
		return df.format(uDate);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public MQMessage getReqMessage() {
		return reqMessage;
	}

	public void setReqMessage(MQMessage reqMessage) {
		this.reqMessage = reqMessage;
	}

	public String getQueueManagerName() {
		return queueManagerName;
	}

	public void setQueueManagerName(String queueManagerName) {
		this.queueManagerName = queueManagerName;
	}

	public PFFDataAccess getDataAccess() {
		return dataAccess;
	}

	public void setDataAccess(PFFDataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}
}
