package com.pennanttech.niyogin.legaldesk.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.niyogin.legaldesk.model.CoBorrower;
import com.pennanttech.niyogin.legaldesk.model.FormData;
import com.pennanttech.niyogin.legaldesk.model.LegalDeskRequest;
import com.pennanttech.niyogin.legaldesk.model.PartyAddress;
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

	@Override
	public AuditHeader extecuteLegalDesk(AuditHeader auditHeader) throws InterfaceException {
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

	private LegalDeskRequest prepareRequestObj(FinanceDetail financeDetail) {
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		LegalDeskRequest legalDeskRequest = new LegalDeskRequest();
		legalDeskRequest.setStampPaperData(prepareStampPaperData(customerDetails));

		legalDeskRequest.setSignersInfo(prepareSignersInfo(financeDetail));

		legalDeskRequest.setFormData(prepareFormData(financeDetail));
		return legalDeskRequest;
	}

	private StampPaperData prepareStampPaperData(CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();
		StampPaperData stampPaperData = new StampPaperData();
		stampPaperData.setFirstParty(customer.getCustShrtName());
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		stampPaperData.setFirstPartyAddress(preparePartyAddress(addressList));
		//TODO:
		stampPaperData.setStampAmount("");
		stampPaperData.setStampDutyPaidBy("");
		return stampPaperData;
	}

	private PartyAddress preparePartyAddress(List<CustomerAddres> addressList) {
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
		City city = getCityDetails(address);

		PartyAddress partyAddress = new PartyAddress();
		partyAddress.setStreet(address.getCustAddrStreet());
		//TODO:
		partyAddress.setLocality(city.getPCCityName());
		partyAddress.setCity(city.getPCCityName());
		partyAddress.setState(city.getLovDescPCProvinceName());
		partyAddress.setPincode(address.getCustAddrZIP());
		partyAddress.setCountry(city.getLovDescPCCountryName());
		return partyAddress;
	}

	private SignersInfo prepareSignersInfo(FinanceDetail financeDetail) {
		SignersInfo signersInfo = new SignersInfo();
		return signersInfo;
	}

	private List<CoBorrower> prepareCoBorrowers(FinanceDetail financeDetail) {
		List<CoBorrower> coBorrowersList = new ArrayList<CoBorrower>(1);

		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		Set<Long> customerIds = new HashSet<Long>(1);
		if (coapplicants != null && !coapplicants.isEmpty()) {
			for (JointAccountDetail coApplicant : coapplicants) {
				if (coApplicant.isAuthoritySignatory()) {
					CoBorrower coBorrower = new CoBorrower();
					coBorrower.setName(coApplicant.getLovDescCIFName());
					coBorrower.setSeqNumbOfSign(coApplicant.getSequence());
					coBorrower.setCustID(coApplicant.getCustID());
					coBorrowersList.add(coBorrower);
					customerIds.add(coApplicant.getCustID());
				}
			}
			if (!coBorrowersList.isEmpty()) {

				List<CustomerEMail> custEmails = getCustomersEmails(customerIds);
				List<CustomerEMail> tempEmailList = new ArrayList<CustomerEMail>(1);
				for (long custId : customerIds) {
					tempEmailList.clear();
					for (CustomerEMail customerEMail : custEmails) {
						if (custId == customerEMail.getCustID()) {
							tempEmailList.add(customerEMail);
						}
					}
					String email = NiyoginUtility.getHignPriorityEmail(tempEmailList, 5);
					for (CoBorrower coBorrower : coBorrowersList) {
						if (custId == coBorrower.getCustID()) {
							coBorrower.setEmail(email);
						}
					}
				}

			}

		}
		return coBorrowersList;
	}

	private FormData prepareFormData(FinanceDetail financeDetail) {

		FormData formData = new FormData();
		formData.setCoBorrowers(prepareCoBorrowers(financeDetail));
		formData.setPurposeOfLoan("");
		formData.setTenure("");
		formData.setIntrestType("");
		formData.setRateOfIntrest("");
		formData.setInstalmentAmt("");
		formData.setInstalmentStartdate(new Date());
		formData.setInstalmentSchedule("");
		formData.setProcessingFees("");
		formData.setPenaltyCharges("");
		formData.setDocumentationCharges("");
		formData.setForeclosure("");
		formData.setChargesForDihorner("");
		formData.setDefaultEmiCharges("");
		formData.setInsuranceGstAmt("");
		formData.setDisbursementOfLoan("");
		formData.setLoanType("");

		return formData;
	}

	private String getResponse() {
		String response = "{ \"statusCode\": 200, \"message\": \"Agreement is send\", \"data\": { \"Doc_ID\": \"5a200d01eb6d461eda857c42\", \"Docket_ID\": \"5a200d01eb6d461eda857c41\", \"signer_ids\": [ { \"signer_id\": \"5a200d01eb6d461eda857c43\" }, { \"signer_id\": \"5a200d01eb6d461eda857c44\" } ] } } ";
		return response;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
