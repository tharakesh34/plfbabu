package com.pennanttech.niyogin.legaldesk.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.legaldesk.model.FormData;
import com.pennanttech.niyogin.legaldesk.model.LegalDeskRequest;
import com.pennanttech.niyogin.legaldesk.model.PartyAddress;
import com.pennanttech.niyogin.legaldesk.model.SignerDetails;
import com.pennanttech.niyogin.legaldesk.model.SignersInfo;
import com.pennanttech.niyogin.legaldesk.model.StampPaperData;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.LegalDeskService;
import com.pennanttech.pff.external.service.NiyoginService;

public class LegalDeskServiceImpl extends NiyoginService implements LegalDeskService {
	private static final Logger	logger					= Logger.getLogger(LegalDeskServiceImpl.class);
	private final String		extConfigFileName		= "legalDesk.properties";
	private String				serviceUrl;

	//Form Fields
	public static final String	FORM_FLDS_LOANPURPOSE	= "LOANPURPOSE";

	public static final String	STAMP_FEE				= "STAMP_FEE";
	public static final String	INSURANCE_FEE			= "INSURANCE_FEE";

	/**
	 * Method for execute the LegalDesk.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader executeLegalDesk(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		
		if (StringUtils.isBlank(serviceUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Map<String, Object> appplicationdata = new HashMap<>();
		LegalDeskRequest legalDeskRequest = prepareRequestObj(financeDetail);

		//send request and log
		String reference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;

		try {
			reuestString = client.getRequestString(legalDeskRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);
			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//add to final
				appplicationdata.putAll(mapvalidData);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);
		}
		prepareResponseObj(appplicationdata, financeDetail);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for prepare the LegalDeskRequest request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private LegalDeskRequest prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		LegalDeskRequest legalDeskRequest = new LegalDeskRequest();
		legalDeskRequest.setStampPaperData(prepareStampPaperData(financeDetail));
		legalDeskRequest.setSignersInfo(prepareSignersInfo(financeDetail));
		legalDeskRequest.setFormData(prepareFormData(financeDetail));
		logger.debug(Literal.LEAVING);
		return legalDeskRequest;
	}

	/**
	 * Method for prepare the StampPaperData request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private StampPaperData prepareStampPaperData(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();

		StampPaperData stampPaperData = new StampPaperData();
		stampPaperData.setFirstParty(customer.getCustShrtName());
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		PartyAddress partyAddress = null;
		if (addressList != null && !addressList.isEmpty()) {
			if (addressList.size() > 1) {
				NiyoginUtility.sortCustomerAddres(addressList);
			}
			CustomerAddres address = addressList.get(0);
			City city = getCityDetails(address);
			partyAddress = new PartyAddress();
			partyAddress.setStreet(address.getCustAddrStreet());
			partyAddress.setLocality(address.getCustAddrLine2());
			partyAddress.setCity(city.getPCCityName());
			partyAddress.setState(city.getLovDescPCProvinceName());
			partyAddress.setPincode(address.getCustAddrZIP());
			partyAddress.setCountry(city.getLovDescPCCountryName());
		}
		stampPaperData.setFirstPartyAddress(partyAddress);
		List<FinFeeDetail> feeDetailsList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		stampPaperData.setStampAmount(getFeeAmount(feeDetailsList, STAMP_FEE));
		stampPaperData.setStampDutyPaidBy(customer.getCustShrtName());
		logger.debug(Literal.LEAVING);
		return stampPaperData;
	}

	private BigDecimal getFeeAmount(List<FinFeeDetail> feeDetailsList, String Code) {
		BigDecimal feeAmount=BigDecimal.ZERO;
		if (feeDetailsList != null && !feeDetailsList.isEmpty()) {
			for (FinFeeDetail finFee : feeDetailsList) {
				String feeCode = (String) getSMTParameter(Code, String.class);
				if (StringUtils.isNotBlank(feeCode)) {
					if (StringUtils.equals(finFee.getFeeTypeCode(), feeCode)) {
						feeAmount= finFee.getActualAmount();
						break;
					}
				}
			}
		}

		return feeAmount;
	}

	/**
	 * Method for prepare the SignersInfo request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private SignersInfo prepareSignersInfo(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		SignersInfo signersInfo = new SignersInfo();
		signersInfo.setLenders(null);
		signersInfo.setBorrowers(prepareBorrowersList(financeDetail));
		logger.debug(Literal.LEAVING);
		return signersInfo;
	}

	/**
	 * Method for prepare the borrower list request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private List<SignerDetails> prepareBorrowersList(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<SignerDetails> borrowersList = new ArrayList<>(1);

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		if (StringUtils.trimToEmpty(customer.getCustTypeCode()).equals(InterfaceConstants.CUSTTYPE_SOLEPRO)) {
			SignerDetails borrower = new SignerDetails();
			borrower.setName(customer.getCustShrtName());
			borrower.setSeqNumbOfSign(1);
			borrower.setCustID(customer.getCustID());
			borrower.setPan(getPanNumber(customerDetails.getCustomerDocumentsList()));
			List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
			NiyoginUtility.sortCustomerEmail(customerEMailList);
			setCustomerEmail(customerEMailList, borrower);
			borrowersList.add(borrower);
		}

		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		Set<Long> customerIds = new HashSet<>(1);
		if (coapplicants != null && !coapplicants.isEmpty()) {
			for (JointAccountDetail coApplicant : coapplicants) {
				if (coApplicant.isAuthoritySignatory()) {
					SignerDetails borrower = new SignerDetails();
					borrower.setName(coApplicant.getLovDescCIFName());
					borrower.setSeqNumbOfSign(coApplicant.getSequence());
					borrower.setCustID(coApplicant.getCustID());
					borrowersList.add(borrower);
					customerIds.add(coApplicant.getCustID());
				}
			}
			if (!borrowersList.isEmpty() && !customerIds.isEmpty()) {
				List<CustomerEMail> custEmails = getCustomersEmails(customerIds);
				NiyoginUtility.sortCustomerEmail(custEmails);
				for (SignerDetails borrower : borrowersList) {
					setCustomerEmail(custEmails, borrower);
					List<CustomerDocument> custDocuments = getCustomersDocuments(borrower.getCustID());
					if (!custDocuments.isEmpty()) {
						borrower.setPan(getPanNumber(custDocuments));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return borrowersList;
	}

	/**
	 * Method for set the High priority Email to the given Borrower.
	 * 
	 * @param custEmails
	 * @param borrower
	 */
	private void setCustomerEmail(List<CustomerEMail> custEmails, SignerDetails borrower) {
		for (CustomerEMail customerEMail : custEmails) {
			if (customerEMail.getCustID() != borrower.getCustID()) {
				continue;
			}
			borrower.setEmail(customerEMail.getCustEMail());
			break;
		}
	}

	/**
	 * Method for prepare the FormData request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private FormData prepareFormData(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FormData formData = new FormData();
		Map<String, Object> extendedMap = financeDetail.getExtendedFieldRender().getMapValues();
		String valueDesc = "";
		if (extendedMap != null) {
			valueDesc = getLovFieldDetailByCode(String.valueOf(extendedMap.get(FORM_FLDS_LOANPURPOSE)));
		}
		formData.setPurposeOfLoan(valueDesc);
		formData.setTenure(NiyoginUtility.getMonthsBetween(finMain.getFinStartDate(), finMain.getMaturityDate()));
		String instType = "";
		if (StringUtils.equalsIgnoreCase(finMain.getRepayRateBasis(), "R")) {
			instType = App.getLabel("label_Reduce");
		} else if (StringUtils.equalsIgnoreCase(finMain.getRepayRateBasis(), "F")) {
			instType = App.getLabel("label_Flat");
		} else if (StringUtils.equalsIgnoreCase(finMain.getRepayRateBasis(), "C")) {
			instType = App.getLabel("label_Flat_Convert_Reduce");
		}
		formData.setBorrowerPan(getPanNumber(financeDetail.getCustomerDetails().getCustomerDocumentsList()));
		formData.setSactionAmt(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount());
		formData.setIntrestType(instType);
		formData.setRateOfIntrest(String.valueOf(extendedMap.get("RATE_LEGALDESK")));
		extendedMap.remove("RATE_LEGALDESK");
		formData.setInstalmentAmt(finMain.getFirstRepay());
		String instlmntDate = NiyoginUtility.formatDate(finMain.getNextRepayDate(), "dd/MM/yyyy");
		formData.setInstalmentStartdate(instlmntDate);
		formData.setInstalmentSchedule(finMain.getNumberOfTerms());
		formData.setProcessingFees(App.getLabel("label_LegalDesk_ProcessingFees"));
		formData.setPenaltyCharges(App.getLabel("label_LegalDesk_PenaltyCharges"));
		formData.setDocumentationCharges(App.getLabel("label_LegalDesk_DocumentationCharges"));
		formData.setForeclosure(App.getLabel("label_LegalDesk_Foreclosure"));
		formData.setChargesForDihorner(App.getLabel("label_LegalDesk_ChargesForDihorner"));
		formData.setDefaultEmiCharges(App.getLabel("label_LegalDesk_DefaultEmiCharges"));
		formData.setInsuranceGstAmt(App.getLabel("label_LegalDesk_InsuranceGstAmt"));
		formData.setDisbursementOfLoan(finMain.getCurDisbursementAmt());
		formData.setLoanType(finMain.getFinType());
		List<FinFeeDetail> fereedetails = financeDetail.getFinScheduleData().getFinFeeDetailList();;
		formData.setInsuranceAmount(getFeeAmount(fereedetails, INSURANCE_FEE));
		logger.debug(Literal.LEAVING);
		return formData;
	}

	/**
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for failure logging.
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}
