package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.masters.MasterDefService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MasterDefinition.DocumentTypes;
import com.pennant.backend.util.MasterDefinition.MasterTypes;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class FetchDedupDetails {
	private static final Logger logger = LogManager.getLogger(FetchDedupDetails.class);

	private int userAction = -1;

	private String FINDEDUPLABELSWBG = "CustCIF,FinLimitRef,CustCRCPR,CustShrtName,MobileNumber,DupReference,StartDate,"
			+ "FinanceAmount,FinanceType,ProfitAmount,StageDesc,DedupeRule,OverrideUser";

	private String FINDEDUPLABELSPBG = "CustCIF,CustCRCPR,CustShrtName,MobileNumber,DupReference,StartDate,"
			+ "FinanceAmount,FinanceType,ProfitAmount,StageDesc,DedupeRule,OverrideUser";

	private CustomerDetails customerDetails;
	private FinanceDetail financeDetail;
	private List<FinanceDedup> financeDedupList;

	private static DedupParmService dedupParmService;
	private static MasterDefService masterDefService;

	public FetchDedupDetails() {
		super();
	}

	public static CustomerDetails getCustomerDedup(String userRole, CustomerDetails aCustomerDetails,
			Component parent) {
		return new FetchDedupDetails(userRole, aCustomerDetails, parent, dedupParmService).getCustomerDetails();
	}

	public static FinanceDetail getLoanDedup(String userRole, FinanceDetail aFinanceDetail, Component parent,
			String curLoginUser) {
		return new FetchDedupDetails(userRole, aFinanceDetail, parent, curLoginUser, masterDefService)
				.getFinanceDetail();
	}

	/**
	 * Method of Dedup Check for Customer Details
	 * 
	 * @param userRole
	 * @param aCustomerDetails
	 * @param parent
	 */
	private FetchDedupDetails(String userRole, CustomerDetails aCustomerDetails, Component parent,
			DedupParmService dedupParmService) {
		super();

		setCustomerDetails(aCustomerDetails);

		List<CustomerDedup> customerDedup = dedupParmService.fetchCustomerDedupDetails(userRole, aCustomerDetails);
		ShowDedupListBox details = null;

		if (customerDedup.size() > 0) {
			String compareFileds[] = new String[2];
			compareFileds[0] = PennantConstants.CUST_DEDUP_LISTFILED2;
			compareFileds[1] = PennantConstants.CUST_DEDUP_LISTFILED3;
			Object dataObject = ShowDedupListBox.show(parent, customerDedup, PennantConstants.CUST_DEDUP_LIST_FIELDS,
					aCustomerDetails.getCustomer(), aCustomerDetails.getCustomerDocumentsList(), compareFileds, null);
			details = (ShowDedupListBox) dataObject;

			if (details != null) {
				logger.debug("The User Action is " + details.getUserAction());
				userAction = details.getUserAction();
			}
		} else {
			userAction = -1;
		}

		if (userAction == -1) {
			aCustomerDetails.getCustomer().setDedupFound(false);
			aCustomerDetails.getCustomer().setSkipDedup(false);
		} else {
			aCustomerDetails.getCustomer().setDedupFound(true);

			if (userAction == 1) {
				aCustomerDetails.getCustomer().setSkipDedup(true);
			} else {
				aCustomerDetails.getCustomer().setSkipDedup(false);
			}
		}

		setCustomerDetails(aCustomerDetails);
	}

	/**
	 * Method of Dedup Check for Finance Details
	 * 
	 * @param userRole
	 * @param aFinanceDetail
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	private FetchDedupDetails(String userRole, FinanceDetail aFinanceDetail, Component parent, String curLoginUser,
			MasterDefService masterDefService) {
		super();

		// Data Preparation for Rule Executions
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		FinanceDedup financeDedup = new FinanceDedup();
		financeDedup.setCustId(customer.getCustID());
		financeDedup.setCustCRCPR(customer.getCustCRCPR());
		financeDedup.setCustCIF(customer.getCustCIF());
		financeDedup.setCustFName(customer.getCustFName());
		financeDedup.setCustMName(customer.getCustMName());
		financeDedup.setCustLName(customer.getCustLName());
		financeDedup.setCustShrtName(customer.getCustShrtName());
		financeDedup.setCustMotherMaiden(customer.getCustMotherMaiden());
		financeDedup.setCustNationality(customer.getCustNationality());
		financeDedup.setCustParentCountry(customer.getCustParentCountry());
		financeDedup.setCustDOB(customer.getCustDOB());
		financeDedup.setMobileNumber(getCustMobileNum(aFinanceDetail));
		financeDedup.setTradeLicenceNo(customer.getCustTradeLicenceNum());
		financeDedup.setMotherName(customer.getCustShrtNameLclLng());
		financeDedup.setFatherName(customer.getCustMotherMaiden());

		// Check Customer is Existing or New Customer Object
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// finance data to set in to finance dedup
		financeDedup.setFinanceAmount(aFinanceMain.getFinAmount());
		financeDedup.setProfitAmount(aFinanceMain.getTotalGrossPft());
		financeDedup.setFinanceType(aFinanceMain.getFinType());
		financeDedup.setStartDate(aFinanceMain.getFinStartDate());
		financeDedup.setFinLimitRef(aFinanceMain.getFinLimitRef());
		String masterType = MasterTypes.DOC_TYPE.toString();
		Map<String, String> map = masterDefService.getMasterDef(masterType);
		if (aFinanceDetail.getCustomerDetails() != null) {
			for (CustomerDocument document : aFinanceDetail.getCustomerDetails().getCustomerDocumentsList()) {
				if (StringUtils.equals(map.get(DocumentTypes.AADHAAR.toString()), document.getCustDocCategory())) {
					financeDedup.setAadharNumber(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.PAN.toString()), document.getCustDocCategory())) {
					financeDedup.setPanNumber(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.VOTER_ID.toString()),
						document.getCustDocCategory())) {
					financeDedup.setVoterID(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.RATION_CARD.toString()),
						document.getCustDocCategory())) {
					financeDedup.setRationCard(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.LPG_NUMBER.toString()),
						document.getCustDocCategory())) {
					financeDedup.setLpgNumber(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.PASSPORT.toString()),
						document.getCustDocCategory())) {
					financeDedup.setCustPassportNo(document.getCustDocTitle());
				} else if (StringUtils.equals(map.get(DocumentTypes.DRIVING_LICENCE.toString()),
						document.getCustDocCategory())) {
					financeDedup.setDrivingLicenceNo(document.getCustDocTitle());
				}
			}
		}

		financeDedup.setFinReference(aFinanceMain.getFinReference());
		financeDedup
				.setLikeCustFName(financeDedup.getCustFName() != null ? "%" + financeDedup.getCustFName() + "%" : "");
		financeDedup
				.setLikeCustMName(financeDedup.getCustMName() != null ? "%" + financeDedup.getCustMName() + "%" : "");
		financeDedup
				.setLikeCustLName(financeDedup.getCustLName() != null ? "%" + financeDedup.getCustLName() + "%" : "");

		// For Existing Customer/ New Customer
		List<FinanceDedup> loanDedup = new ArrayList<FinanceDedup>();
		List<FinanceDedup> dedupeRuleData = dedupParmService.fetchFinDedupDetails(userRole, financeDedup, curLoginUser,
				aFinanceMain.getFinType());
		loanDedup.addAll(dedupeRuleData);

		ShowDedupListBox details = null;
		Object dataObject;
		if (!loanDedup.isEmpty()) {
			if (FinanceConstants.FIN_DIVISION_CORPORATE
					.equals(aFinanceDetail.getFinScheduleData().getFinanceType().getFinDivision())) {

				dataObject = ShowDedupListBox.show(parent, loanDedup, FINDEDUPLABELSWBG, financeDedup, curLoginUser);
			} else {
				dataObject = ShowDedupListBox.show(parent, loanDedup, FINDEDUPLABELSPBG, financeDedup, curLoginUser);
			}
			details = (ShowDedupListBox) dataObject;

			if (details != null) {
				logger.debug("The User Action is " + details.getUserAction());
				userAction = details.getUserAction();
				setFinanceDedupList((List<FinanceDedup>) details.getObject());
			}
		} else {
			userAction = -1;
		}

		aFinanceDetail.setFinDedupDetails(null);
		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(false);
		} else if (userAction == 1) {
			aFinanceDetail.setFinDedupDetails(getFinanceDedupList());
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(true);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(true);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(false);
		}

		setFinanceDetail(aFinanceDetail);
	}

	private String getCustMobileNum(FinanceDetail aFinanceDetail) {
		String custMobileNumber = "";
		if (aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
				if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
					custMobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		return custMobileNumber;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public List<FinanceDedup> getFinanceDedupList() {
		return financeDedupList;
	}

	public void setFinanceDedupList(List<FinanceDedup> financeDedupList) {
		this.financeDedupList = financeDedupList;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchDedupDetails.dedupParmService = dedupParmService;
	}

	public void setMasterDefService(MasterDefService masterDefService) {
		FetchDedupDetails.masterDefService = masterDefService;
	}

}
