package com.pennanttech.niyogin.legaldesk.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.niyogin.legaldesk.model.FormData;
import com.pennanttech.niyogin.legaldesk.model.LegalDeskRequest;
import com.pennanttech.niyogin.legaldesk.model.PartyAddress;
import com.pennanttech.niyogin.legaldesk.model.SignerDetails;
import com.pennanttech.niyogin.legaldesk.model.SignersInfo;
import com.pennanttech.niyogin.legaldesk.model.StampPaperData;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.LegalDeskService;
import com.pennanttech.pff.external.service.NiyoginService;

public class LegalDeskServiceImpl extends NiyoginService implements LegalDeskService {
	private static final Logger	logger				= Logger.getLogger(LegalDeskServiceImpl.class);
	private final String		extConfigFileName	= "legalDesk";
	private String				serviceUrl;

	/**
	 * Method for execute the LegalDesk.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader executeLegalDesk(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		LegalDeskRequest legalDeskRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;

		extendedFieldMap = post(serviceUrl, legalDeskRequest, extConfigFileName);

		try {
			validatedMap = validateExtendedMapValues(extendedFieldMap);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, legalDeskRequest);
			throw new InterfaceException("9999", e.getMessage());
		}
		prepareResponseObj(validatedMap, financeDetail);
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
		stampPaperData.setFirstPartyAddress(preparePartyAddress(addressList));

		List<FinFeeDetail> feeDetailsList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail finFee : feeDetailsList) {
			String stampFeeCode = (String) getSMTParameter("STAMPFEE", String.class);
			if(StringUtils.isNotBlank(stampFeeCode)) {
				if (StringUtils.equals(finFee.getFeeTypeCode(), stampFeeCode)) {
					stampPaperData.setStampAmount(finFee.getActualAmount());
					break;
				}
			}
		}

		stampPaperData.setStampDutyPaidBy(customer.getCustShrtName());
		logger.debug(Literal.LEAVING);
		return stampPaperData;
	}

	/**
	 * Method for prepare the PartyAddress request object.
	 * 
	 * @param addressList
	 * @return
	 */
	private PartyAddress preparePartyAddress(List<CustomerAddres> addressList) {
		logger.debug(Literal.ENTERING);
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
		City city = getCityDetails(address);

		PartyAddress partyAddress = new PartyAddress();
		partyAddress.setStreet(address.getCustAddrStreet());
		partyAddress.setLocality(city.getPCCityName());
		partyAddress.setCity(city.getPCCityName());
		partyAddress.setState(city.getLovDescPCProvinceName());
		partyAddress.setPincode(address.getCustAddrZIP());
		partyAddress.setCountry(city.getLovDescPCCountryName());
		logger.debug(Literal.LEAVING);
		return partyAddress;
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
		if (StringUtils.trimToEmpty(customer.getCustTypeCode()).equals("3")) {
			SignerDetails borrower = new SignerDetails();
			borrower.setName(customer.getCustShrtName());
			borrower.setSeqNumbOfSign(1);
			borrower.setCustID(customer.getCustID());
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
			if (!borrowersList.isEmpty()) {
				List<CustomerEMail> custEmails = getCustomersEmails(customerIds);
				NiyoginUtility.sortCustomerEmail(custEmails);
				for (SignerDetails borrower : borrowersList) {
					setCustomerEmail(custEmails, borrower);
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

		if (extendedMap != null) {
			for (Entry<String, Object> entry : extendedMap.entrySet()) {
				if (StringUtils.equals(entry.getKey(), "LOANPURPOSE")) {
					formData.setPurposeOfLoan(Objects.toString(entry.getValue(), null));
				}
			}
		}
		formData.setTenure(NiyoginUtility.getMonthsBetween(finMain.getFinStartDate(), finMain.getMaturityDate()));
		formData.setIntrestType(finMain.getRepayRateBasis());
		formData.setRateOfIntrest(finMain.getRepayProfitRate());
		formData.setInstalmentAmt(finMain.getFirstRepay());
		String instlmntDate = NiyoginUtility.formatDate(finMain.getNextRepayDate(), "dd/MM/yyyy");
		formData.setInstalmentStartdate(instlmntDate);
		formData.setInstalmentSchedule(finMain.getNumberOfTerms());
		formData.setProcessingFees("2%");
		formData.setPenaltyCharges("2%");
		formData.setDocumentationCharges("0.2%");
		formData.setForeclosure("5%");
		formData.setChargesForDihorner("750");
		formData.setDefaultEmiCharges("0");
		formData.setInsuranceGstAmt("2%");
		formData.setDisbursementOfLoan(finMain.getCurDisbursementAmt());
		formData.setLoanType(finMain.getFinType());
		logger.debug(Literal.LEAVING);
		return formData;
	}

	/*
	 * private String getResponse() { String response =
	 * "{ \"statusCode\": 200, \"message\": \"Agreement is send\", \"data\": { \"Doc_ID\": \"5a200d01eb6d461eda857c42\", \"Docket_ID\": \"5a200d01eb6d461eda857c41\", \"signer_ids\": [ { \"signer_id\": \"5a200d01eb6d461eda857c43\" }, { \"signer_id\": \"5a200d01eb6d461eda857c44\" } ] } } "
	 * ; return response; }
	 */

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
