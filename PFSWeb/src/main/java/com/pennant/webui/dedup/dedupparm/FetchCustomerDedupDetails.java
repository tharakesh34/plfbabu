package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FetchCustomerDedupDetails {

	private static final Logger logger = LogManager.getLogger(FetchCustomerDedupDetails.class);

	private static String CUSTOMERDEDUP_LABELS = "custCIF,custDOB,custFName,custLName,custCRCPR,custEMail,mobileNumber,aadharNumber,"
			+ "custNationality,dedupRule,override";

	private static final String CUSTOMERDEDUP_LABELS2 = "custCIF,custShrtName,custDOB,custCRCPR,aadharNumber,custEMail,mobileNumber,custCoreBank";

	private static DedupParmService dedupParmService;
	private static CustomerDedupDAO customerDedupDAO;
	private static MasterDefDAO masterDefDAO;

	public FetchCustomerDedupDetails() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static CustomerDetails getCustomerDedup(String userRole, CustomerDetails customerDetails,
			Window parentWindow, String curLoginUser, String finType) throws InterfaceException {
		List<CustomerDedup> customerDedupList = null;

		boolean alwExtCustDedup = SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP);

		if (customerDetails != null && customerDetails.getCustomer() != null) {

			CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);

			Customer customer = customerDetails.getCustomer();

			List<CustomerDedup> custDedupData = fetchCustomerDedupDetails(userRole, customerDedup, curLoginUser,
					finType);

			if (custDedupData != null && !custDedupData.isEmpty()) {
				customer.setDedupFound(true);
				if (ImplementationConstants.SHOW_CUSTOM_BLACKLIST_FIELDS) {
					CUSTOMERDEDUP_LABELS = "custCIF,custDOB,custShrtName,custCRCPR,custEMail,mobileNumber,aadharNumber,"
							+ "custNationality,dedupRule,override";
				}

				customer.setDedupFound(true);
				Object dataObject = null;

				if (alwExtCustDedup) {
					dataObject = ShowExtCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS2,
							customerDedup, curLoginUser, getDedupParmService());

					if (dataObject != null) {
						ShowExtCustomerDedupListBox details = (ShowExtCustomerDedupListBox) dataObject;

						logger.debug("The User Action is " + details.getUserAction());
						int userAction = details.getUserAction();

						customerDedupList = (List<CustomerDedup>) details.getObject();

						if (userAction == 1) {
							customerDetails.setCustomerDedupList(customerDedupList);
							CustomerDedup dedup = customerDedupList.get(0);
							if (dedup != null && PennantConstants.UCIC_NEW.equals(dedup.getUcicType())) {
								MessageUtil.showInfo("label_External_Dedup_UCIC_Msg", dedup.getCustCoreBank(),
										customer.getCustShrtName());
							}
							customer.setCustCoreBank(dedup.getCustCoreBank());
							customer.setSkipDedup(true);
						} else if (userAction == 0) {
							customer.setSkipDedup(false);
						}
					}

				} else {
					dataObject = ShowCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS,
							customerDedup, curLoginUser);

					if (dataObject != null) {
						ShowCustomerDedupListBox details = (ShowCustomerDedupListBox) dataObject;

						logger.debug("The User Action is " + details.getUserAction());
						int userAction = details.getUserAction();

						customerDedupList = (List<CustomerDedup>) details.getObject();

						if (userAction == 1) {
							customerDetails.setCustomerDedupList(customerDedupList);
							customer.setSkipDedup(true);
						} else if (userAction == 0) {
							customer.setSkipDedup(false);
						}
					}

				}

			} else {
				customer.setDedupFound(false);
				customerDetails.setCustomerDedupList(null);
			}
			logger.debug("Leaving");

		}
		return customerDetails;

	}

	public static List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDedup customerDedup,
			String curLoginUser, String finType) throws InterfaceException {
		List<CustomerDedup> overridedCustDedupList = new ArrayList<>();
		List<CustomerDedup> customerDedupList = new ArrayList<>();
		FinanceReferenceDetail referenceDetail = new FinanceReferenceDetail();
		List<FinanceReferenceDetail> queryCodeList = new ArrayList<>();

		if (StringUtils.isNotEmpty(finType)) {
			referenceDetail.setMandInputInStage(userRole + ",");
			referenceDetail.setFinType(finType);
			queryCodeList = dedupParmService.getQueryCodeList(referenceDetail, "_ACDView");
		}

		if (StringUtils.isNotEmpty(finType) && CollectionUtils.isEmpty(queryCodeList)) {
			return customerDedupList;
		}

		String custDedup = FinanceConstants.DEDUP_CUSTOMER;
		List<DedupParm> list = dedupParmService.getDedupParmByModule(custDedup, customerDedup.getCustCtgCode(), "");

		List<DedupParm> finDedupParmList = new ArrayList<>();

		boolean alwExtCustDedup = SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP);

		String custCIF = customerDedup.getCustCIF();
		if (StringUtils.isNotEmpty(finType) && CollectionUtils.isNotEmpty(queryCodeList)
				&& CollectionUtils.isNotEmpty(list)) {
			for (FinanceReferenceDetail queryCode : queryCodeList) {

				// to get previously overridden data
				List<CustomerDedup> custDedupList = customerDedupDAO.fetchOverrideCustDedupData(custCIF,
						queryCode.getLovDescNamelov(), custDedup);

				custDedupList.forEach(l1 -> l1.setOverridenby(l1.getOverrideUser()));
				overridedCustDedupList.addAll(custDedupList);

				if (!alwExtCustDedup) {
					for (DedupParm parm : list) {
						if (StringUtils.equals(parm.getQueryCode(), queryCode.getLovDescNamelov())) {
							finDedupParmList.add(parm);
						}
					}
					// to get the de dup details based on the de dup parameters i.e query's list from both application
					// and core
					// banking
				} else {
					customerDedupList.addAll(getDedupParmService().getCustomerDedup(customerDedup, list));

				}
			}

		} else {
			if (CollectionUtils.isNotEmpty(list)) {
				for (DedupParm dedupParm : list) {
					// to get previously overridden data
					List<CustomerDedup> custDedupList = customerDedupDAO.fetchOverrideCustDedupData(custCIF,
							dedupParm.getQueryCode(), custDedup);
					custDedupList.forEach(l1 -> l1.setOverridenby(l1.getOverrideUser()));
					overridedCustDedupList.addAll(custDedupList);
				}
				// to get the de dup details based on the de dup parameters i.e query's list from both application and
				// core banking
				customerDedupList.addAll(dedupParmService.getCustomerDedup(customerDedup, list));
			}
		}
		customerDedupList = doSetCustomerDeDupGrouping(customerDedupList);

		boolean newUser = false;
		// Checking for duplicate records in overrideBlacklistCustomers and currentBlacklistCustomers
		try {
			if (!overridedCustDedupList.isEmpty() && !customerDedupList.isEmpty()) {

				for (CustomerDedup previousDedup : overridedCustDedupList) {
					for (CustomerDedup currentDedup : customerDedupList) {
						if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
							currentDedup.setOverridenby(previousDedup.getOverrideUser());
							if (previousDedup.getOverrideUser().contains(curLoginUser)) {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser());
								newUser = false;
							} else {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser()
										+ PennantConstants.DELIMITER_COMMA + curLoginUser);
								newUser = true;
							}
							// Checking for New Rule
							if (isRuleChanged(previousDedup.getDedupRule(), currentDedup.getDedupRule())) {
								currentDedup.setNewRule(true);
								if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
									currentDedup.setNewCustDedupRecord(false);
								} else {
									currentDedup.setNewCustDedupRecord(true);
									currentDedup.setOverride(false);
								}
							} else {
								currentDedup.setNewCustDedupRecord(false);
							}

							if (newUser) {
								currentDedup.setOverride(previousDedup.isOverride());
							}
						}
					}
				}
			} else if (!overridedCustDedupList.isEmpty() && customerDedupList.isEmpty()) {
				customerDedupList.addAll(overridedCustDedupList);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return customerDedupList;
	}

	public static CustomerDetails getCustomerDedupDetails(String userRole, CustomerDetails customerDetails,
			Window parentWindow, String curLoginUser, String finType) throws InterfaceException {

		if (customerDetails == null || customerDetails.getCustomer() == null) {
			return customerDetails;
		}

		boolean alwExtCustDedup = SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP);

		CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);
		Customer customer = customerDetails.getCustomer();

		List<CustomerDedup> custDedupData = fetchCustomerDedupDetails(userRole, customerDedup, curLoginUser, finType);

		if (CollectionUtils.isEmpty(custDedupData)) {
			customer.setDedupFound(false);
			customerDetails.setCustomerDedupList(null);
			return customerDetails;
		}

		customer.setDedupFound(true);
		customerDedup.setFinType(finType);
		Object dataObject = null;
		if (alwExtCustDedup) {
			dataObject = ShowExtCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS2,
					customerDedup, curLoginUser, getDedupParmService());
		} else {
			dataObject = ShowCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS, customerDedup,
					curLoginUser);
		}

		if (dataObject == null) {
			return customerDetails;
		}

		ShowCustomerDedupListBox details = (ShowCustomerDedupListBox) dataObject;
		logger.debug("The User Action is " + details.getUserAction());
		int userAction = details.getUserAction();

		@SuppressWarnings("unchecked")
		List<CustomerDedup> customerDedupList = (List<CustomerDedup>) details.getObject();

		if (userAction == 1) {
			customerDetails.setCustomerDedupList(customerDedupList);
			customer.setSkipDedup(true);
		} else if (userAction == 0) {
			customer.setSkipDedup(false);
		}

		return customerDetails;
	}

	/**
	 * Checking for Rule weather it is added or removed
	 * 
	 * @param overrideListRule
	 * @param newListRule
	 * @return
	 */
	private static boolean isRuleChanged(String overrideListRule, String newListRule) {
		String[] exeRuleList = overrideListRule.split(",");
		String[] newRuleList = newListRule.split(",");
		if (exeRuleList.length != newRuleList.length) {
			return true;
		} else {
			for (String newRule : newRuleList) {
				if (!Arrays.toString(exeRuleList).contains(newRule)) {
					return true;
				}
			}
		}
		return false;
	}

	private static List<CustomerDedup> doSetCustomerDeDupGrouping(List<CustomerDedup> customerDedupList) {
		logger.debug("Entering");
		List<CustomerDedup> groupedList = new ArrayList<CustomerDedup>();
		try {

			for (CustomerDedup customerDedup : customerDedupList) {
				customerDedup.setModule(FinanceConstants.DEDUP_CUSTOMER);
				customerDedup.setOverride(true);
				if (groupedList.isEmpty()) {
					groupedList.add(customerDedup);
				} else {
					CustomerDedup custDedup = checkRecordinList(customerDedup, groupedList);
					if (custDedup != null) {
						groupedList.add(custDedup);
					}
				}

			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return groupedList;
	}

	private static CustomerDedup checkRecordinList(CustomerDedup customerDedupcheck, List<CustomerDedup> groupedList) {
		for (CustomerDedup customerDedup : groupedList) {
			if (customerDedup.getCustCIF().equals(customerDedupcheck.getCustCIF())) {
				customerDedup.setQueryField(customerDedup.getQueryField() + PennantConstants.DELIMITER_COMMA
						+ customerDedupcheck.getQueryField());
				if (!customerDedup.getDedupRule().contains(customerDedupcheck.getDedupRule())) {
					customerDedup.setDedupRule(customerDedup.getDedupRule() + PennantConstants.DELIMITER_COMMA
							+ customerDedupcheck.getDedupRule());
				}
				return null;
			}
		}
		return customerDedupcheck;
	}

	private static CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		String mobileNumber = "";
		String emailid = "";
		String aadharId = "";
		String passPortNo = "";
		String voterId = "";
		String drivingLicenseNo = "";
		String aadhar = masterDefDAO.getMasterCode("DOC_TYPE", "AADHAAR");
		String passPort = masterDefDAO.getMasterCode("DOC_TYPE", "PASSPORT");
		String panNumber = masterDefDAO.getMasterCode("DOC_TYPE", "PAN");
		StringBuilder custAddress = new StringBuilder("");
		String custAddress1 = "";

		String phoneType = "";
		String emailType = "";
		String city = "";
		String state = "";
		String country = "";
		String pincode = "";
		String addressType = "";
		String drivingLicenceNo = "";

		String voterIdCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, PennantConstants.VOTER_ID);
		String drivingLicenseCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE,
				PennantConstants.DRIVING_LICENCE);

		Customer customer = customerDetails.getCustomer();
		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
				if (String.valueOf(custPhone.getPhoneTypePriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					phoneType = custPhone.getPhoneTypeCode();
					break;
				}
			}
		}
		if (customerDetails.getCustomerEMailList() != null) {
			for (CustomerEMail email : customerDetails.getCustomerEMailList()) {
				if (String.valueOf(email.getCustEMailPriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					emailid = email.getCustEMail();
					break;
				}
			}
		}
		// Aadhar
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(aadhar)) {
					aadharId = document.getCustDocTitle();
					break;
				}
			}
		}
		// Passport
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(passPort)) {
					passPortNo = document.getCustDocTitle();
					break;
				}
			}
		}

		// Driving License
		if (customerDetails.getCustomerDocumentsList() != null && StringUtils.isNotEmpty(drivingLicenseCode)) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (StringUtils.equals(drivingLicenseCode, document.getCustDocCategory())) {
					drivingLicenseNo = document.getCustDocTitle();
					break;
				}
			}
		}
		// VoterId
		if (customerDetails.getCustomerDocumentsList() != null && StringUtils.isNotEmpty(voterIdCode)) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (StringUtils.equals(voterIdCode, document.getCustDocCategory())) {
					voterId = document.getCustDocTitle();
					break;
				}
			}
		}

		// Address
		if (customerDetails.getAddressList() != null) {
			for (CustomerAddres address : customerDetails.getAddressList()) {
				if (String.valueOf(address.getCustAddrPriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					custAddress.append(address.getCustAddrHNbr()).append(", ");
					custAddress.append(StringUtils.isNotBlank(address.getCustAddrStreet())
							? address.getCustAddrStreet().concat(", ")
							: "");
					custAddress.append(address.getCustAddrCity()).append(", ");
					custAddress.append(address.getCustAddrProvince()).append(", ");
					custAddress.append(address.getCustAddrZIP()).append(", ");
					custAddress.append(address.getCustAddrCountry());

					custAddress1 = StringUtils.isNotBlank(address.getCustAddrStreet()) ? address.getCustAddrStreet()
							: "";
					break;
				}
			}
		}
		// PANCard
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(panNumber)) {
					panNumber = document.getCustDocTitle();
					break;
				}
			}
		}

		boolean alwExtCustDedup = SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP);
		// Address
		if (customerDetails.getAddressList() != null) {
			for (CustomerAddres address : customerDetails.getAddressList()) {
				if (String.valueOf(address.getCustAddrPriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					custAddress.append(
							StringUtils.isNotBlank(address.getCustAddrLine3()) ? address.getCustAddrLine3().concat(", ")
									: "");
					custAddress.append(
							StringUtils.isNotBlank(address.getCustAddrHNbr()) ? address.getCustAddrHNbr().concat(", ")
									: "");
					custAddress.append(
							StringUtils.isNotBlank(address.getCustFlatNbr()) ? address.getCustFlatNbr().concat(", ")
									: "");
					custAddress.append(StringUtils.isNotBlank(address.getCustAddrStreet())
							? address.getCustAddrStreet().concat(", ")
							: "");
					custAddress.append(
							StringUtils.isNotBlank(address.getCustAddrLine1()) ? address.getCustAddrLine1().concat(", ")
									: "");

					if (!alwExtCustDedup) {
						custAddress.append(StringUtils.isNotBlank(address.getCustAddrLine2())
								? address.getCustAddrLine2().concat(", ")
								: "");
						custAddress.append(address.getCustAddrCity()).append(", ");
						custAddress.append(address.getCustAddrProvince()).append(", ");
						custAddress.append(address.getCustAddrZIP()).append(", ");
						custAddress.append(address.getCustAddrCountry());
					} else {
						custAddress.append(
								StringUtils.isNotBlank(address.getCustAddrLine2()) ? address.getCustAddrLine2() : "");
					}
					city = address.getCustAddrCity();
					state = address.getCustAddrProvince();
					country = address.getCustAddrCountry();
					pincode = address.getCustAddrZIP();
					addressType = address.getCustAddrType();
					break;
				}
			}
		}

		CustomerDedup customerDedup = new CustomerDedup();
		customerDedup.setFinReference(customer.getCustCIF());
		customerDedup.setCustId(customer.getCustID());
		customerDedup.setCustCIF(customer.getCustCIF());
		customerDedup.setCustFName(customer.getCustFName());
		customerDedup.setCustLName(customer.getCustLName());
		customerDedup.setCustShrtName(customer.getCustShrtName());
		customerDedup.setCustDOB(customer.getCustDOB());
		customerDedup.setCustCRCPR(customer.getCustCRCPR());
		customerDedup.setAadharNumber(aadharId);
		customerDedup.setPanNumber(panNumber);
		customerDedup.setCustCtgCode(customer.getCustCtgCode());
		customerDedup.setCustDftBranch(customer.getCustDftBranch());
		customerDedup.setCustSector(customer.getCustSector());
		customerDedup.setCustSubSector(customer.getCustSubSector());
		customerDedup.setCustNationality(customer.getCustNationality());
		customerDedup.setCustPassportNo(passPortNo);
		customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
		customerDedup.setCustVisaNum(customer.getCustVisaNum());
		customerDedup.setMobileNumber(mobileNumber);
		customerDedup.setCustPOB(customer.getCustPOB());
		customerDedup.setCustResdCountry(customer.getCustResdCountry());
		customerDedup.setCustEMail(emailid);
		customerDedup.setVoterID(voterId);
		customerDedup.setDrivingLicenceNo(drivingLicenseNo);
		customerDedup.setAddress(custAddress.toString());
		customerDedup.setAddress1(custAddress1);
		if (alwExtCustDedup) {
			customerDedup.setGender(customer.getCustGenderCode());
		} else {
			customerDedup.setGender(customer.getLovDescCustGenderCodeName());
		}
		customerDedup.setCity(city);
		customerDedup.setState(state);
		customerDedup.setCountry(country);
		customerDedup.setPincode(pincode);
		customerDedup.setPhoneType(phoneType);
		customerDedup.setEmailType(emailType);
		customerDedup.setLikeCustMName(customer.getCustMName());
		customerDedup.setAddressType(addressType);
		customerDedup.setDrivingLicenceNo(drivingLicenceNo);
		customerDedup.setCustMotherMaiden(customer.getCustMotherMaiden());

		if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP && customerDetails.getExtendedFieldRender() != null) {
			Map<String, Object> mapValues = customerDetails.getExtendedFieldRender().getMapValues();
			if (mapValues != null && mapValues.get("UCIC") != null) {
				customerDedup.setUcic(mapValues.get("UCIC").toString());
			}
		}

		logger.debug("Leaving");
		return customerDedup;

	}

	public static DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchCustomerDedupDetails.dedupParmService = dedupParmService;
	}

	public static CustomerDedupDAO getCustomerDedupDAO() {
		return customerDedupDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		FetchCustomerDedupDetails.customerDedupDAO = customerDedupDAO;
	}

	public static MasterDefDAO getMasterDefDAO() {
		return masterDefDAO;
	}

	public static void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		FetchCustomerDedupDetails.masterDefDAO = masterDefDAO;
	}

}
