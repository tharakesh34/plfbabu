package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;

public class FetchBlackListDetails {

	private static final Logger logger = LogManager.getLogger(FetchBlackListDetails.class);
	private static DedupParmService dedupParmService;
	private List<BlackListCustomers> blackListCustomers;
	private List<FinBlacklistCustomer> finBlacklistCustomer;
	private FinanceDetail financeDetail;
	private int userAction = -1;
	private static MasterDefDAO masterDefDAO;

	String BLACKLIST_FIELDS = "custCIF,custDOB,custFName,custLName,custCRCPR,"
			+ "custPassportNo,mobileNumber,custNationality,employer,watchListRule,override,overridenby";

	public FetchBlackListDetails() {
		super();
	}

	public static FinanceDetail getBlackListCustomers(String userRole, FinanceDetail tFinanceDetail, Window parent,
			String curLoginUser) {
		List<FinBlacklistCustomer> finBlacklistCustomer = new ArrayList<FinBlacklistCustomer>();
		FinanceDetail detail = new FetchBlackListDetails(userRole, tFinanceDetail, parent, curLoginUser,
				tFinanceDetail.getCustomerDetails()).getFinanceDetail();

		if (detail.getFinBlacklistCustomer() != null) {
			finBlacklistCustomer.addAll(detail.getFinBlacklistCustomer());
		}

		if (ImplementationConstants.DEDUP_BLACKLIST_COAPP) {
			if (detail.getFinScheduleData().getFinanceMain().isBlacklisted()
					&& detail.getFinScheduleData().getFinanceMain().isBlacklistOverride()
					|| !detail.getFinScheduleData().getFinanceMain().isBlacklisted()) {
				if (CollectionUtils.isNotEmpty(detail.getJountAccountDetailList())) {
					for (JointAccountDetail coapplicant : detail.getJountAccountDetailList()) {
						CustomerDetails customerDetails = coapplicant.getCustomerDetails();
						detail = new FetchBlackListDetails(userRole, tFinanceDetail, parent, curLoginUser,
								customerDetails).getFinanceDetail();
						if (detail.getFinScheduleData().getFinanceMain().isBlacklisted()
								&& !detail.getFinScheduleData().getFinanceMain().isBlacklistOverride()) {
							return detail;
						}
						if (detail.getFinBlacklistCustomer() != null) {
							finBlacklistCustomer.addAll(detail.getFinBlacklistCustomer());
						}
					}
				}
			}
		}
		detail.setFinBlacklistCustomer(finBlacklistCustomer);
		return detail;
	}

	/**
	 * 
	 * @param role
	 * @param custCIF
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	private FetchBlackListDetails(String userRole, FinanceDetail aFinanceDetail, Window parent, String curLoginUser,
			CustomerDetails customerDetails) {
		super();
		logger.debug("Entering");

		Customer customer = null;
		String mobileNumber = "";
		StringBuilder custAddress = new StringBuilder("");

		if (customerDetails.getCustomer() != null) {
			customer = customerDetails.getCustomer();
			if (customerDetails.getCustomerPhoneNumList() != null) {
				for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
					if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
						mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
								custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
						break;
					}
				}
			}
		}

		if (customerDetails.getCustomer() != null) {
			customer = customerDetails.getCustomer();
			if (customerDetails.getAddressList() != null) {
				for (CustomerAddres address : customerDetails.getAddressList()) {
					if (address.getCustAddrPriority() == Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						custAddress.append(address.getCustAddrHNbr()).append(", ");
						custAddress.append(StringUtils.isNotBlank(address.getCustAddrStreet())
								? address.getCustAddrStreet().concat(", ") : "");
						if (ImplementationConstants.CUSTOM_BLACKLIST_PARAMS) {
							custAddress.append(StringUtils.isNotEmpty(address.getCustAddrLine2())
									? address.getCustAddrLine2().concat(", ") : "");
							custAddress.append(StringUtils.isNotEmpty(address.getCustAddrLine1())
									? address.getCustAddrLine1().concat(", ") : "");
							custAddress.append(address.getLovDescCustAddrCityName()).append(", ");
							custAddress.append(address.getLovDescCustAddrProvinceName()).append(", ");
							custAddress.append(address.getLovDescCustAddrCountryName()).append(", ");
							custAddress.append(address.getCustAddrZIP());
						} else {
							custAddress.append(address.getCustAddrCity()).append(", ");
							custAddress.append(address.getCustAddrProvince()).append(", ");
							custAddress.append(address.getCustAddrCountry());
						}
						break;
					}
				}
			}
		}
		for (CustomerDocument cd : customerDetails.getCustomerDocumentsList()) {
			if (cd.getCustDocCategory().equals("01")) {
				customer.setAadhaarNo(cd.getCustDocTitle());
			}
		}

		String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
		String finReference = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference();

		BlackListCustomers blackListCustData = doSetCustDataToBlackList(customer, finReference, mobileNumber);
		if (blackListCustData != null) {
			blackListCustData.setAddress(custAddress.toString());
		}
		// setting the customer documents data
		doSetCustomerDocumentsData(aFinanceDetail, blackListCustData, customerDetails);

		setBlackListCustomers(
				getDedupParmService().fetchBlackListCustomers(userRole, finType, blackListCustData, curLoginUser));

		ShowBlackListDetailBox details = null;
		if (getBlackListCustomers() != null && getBlackListCustomers().size() > 0) {

			if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
				BLACKLIST_FIELDS = "custCIF,custDOB,custFName,custCRCPR,"
						+ "custPassportNo,mobileNumber,custNationality,employer,address,custAadhaar,watchListRule,override,overridenby";
			}

			Object dataObject = ShowBlackListDetailBox.show(parent, getBlackListCustomers(), BLACKLIST_FIELDS,
					blackListCustData, curLoginUser);
			details = (ShowBlackListDetailBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::" + details.getUserAction());
				logger.debug("The User Action is " + details.getUserAction());
				userAction = details.getUserAction();
				setFinBlacklistCustomer((List<FinBlacklistCustomer>) details.getObject());
			}
		} else {
			userAction = -1;
		}

		aFinanceDetail.setFinBlacklistCustomer(null);

		/**
		 * userAction represents Clean or Blacklisted actions if user click on Clean button userAction = 1 if user click
		 * on Blacklisted button userAction = 0 if no customer found as a blacklist customer then userAction = -1
		 */
		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(true);

			if (userAction == 1) {
				aFinanceDetail.setFinBlacklistCustomer(getFinBlacklistCustomer());
				aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(true);
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
			}
		}
		setFinanceDetail(aFinanceDetail);
		logger.debug("Leaving");
	}

	/**
	 * Prepare Black List Customer Object Data
	 * 
	 * @param blackListCustomer
	 * @param customer
	 * @return
	 */
	private BlackListCustomers doSetCustDataToBlackList(Customer customer, String finReference, String mobileNumber) {
		logger.debug("Entering");

		if (customer != null) {
			BlackListCustomers blackListCustomer = new BlackListCustomers();

			blackListCustomer.setCustCIF(customer.getCustCIF());
			blackListCustomer.setCustShrtName(customer.getCustShrtName());
			blackListCustomer.setCustFName(customer.getCustFName());
			if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
				blackListCustomer.setCustFName(customer.getCustShrtName());
				blackListCustomer.setCustCompName(customer.getCustShrtName());
			}
			blackListCustomer.setCustLName(customer.getCustLName());
			blackListCustomer.setCustCRCPR(customer.getCustCRCPR());
			blackListCustomer.setCustPassportNo(customer.getCustPassportNo());
			blackListCustomer.setMobileNumber(mobileNumber);
			blackListCustomer.setCustNationality(customer.getCustNationality());
			blackListCustomer.setCustDOB(customer.getCustDOB());
			blackListCustomer.setCustCtgCode(customer.getCustCtgCode());
			blackListCustomer.setFinReference(finReference);
			blackListCustomer.setCustAadhaar(customer.getAadhaarNo());

			blackListCustomer.setLikeCustFName(
					blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
			blackListCustomer.setLikeCustLName(
					blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
			// setting additional details data
			blackListCustomer = FetchBlackListCustomerAdditionalDetails.doSetCustDataToBlackList(customer,
					blackListCustomer);
			logger.debug("Leaving");

			return blackListCustomer;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param aFinanceDetail
	 * @param blackListCustData
	 */
	private void doSetCustomerDocumentsData(FinanceDetail aFinanceDetail, BlackListCustomers blackListCustData,
			CustomerDetails acustomerDetails) {
		logger.debug(Literal.ENTERING);
		String aadharCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.AADHAAR.name());
		String passPortCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.PASSPORT.name());
		String voterIdCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.VOTER_ID.name());
		String drivingLicenseCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE,
				DocType.DRIVING_LICENCE.name());
		String panCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.PAN.name());
		CustomerDetails customerDetails = acustomerDetails;
		if (customerDetails != null && customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (StringUtils.equals(aadharCode, document.getCustDocCategory())) { // Aadhar
					blackListCustData.setCustAadhaar(document.getCustDocTitle());
				} else if (StringUtils.equals(passPortCode, document.getCustDocCategory())) { // Passport
					blackListCustData.setCustPassportNo(document.getCustDocTitle());
				} else if (StringUtils.equals(drivingLicenseCode, document.getCustDocCategory())) {// Driving License
					blackListCustData.setDl(document.getCustDocTitle());
				} else if (StringUtils.equals(voterIdCode, document.getCustDocCategory())) {// VoterId
					blackListCustData.setVid(document.getCustDocTitle());
				} else if (StringUtils.equals(panCode, document.getCustDocCategory())) {// PAN
					blackListCustData.setCustCRCPR(document.getCustDocTitle());
				}

			}
		}
		logger.debug(Literal.LEAVING);
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchBlackListDetails.dedupParmService = dedupParmService;
	}

	public List<BlackListCustomers> getBlackListCustomers() {
		return blackListCustomers;
	}

	public void setBlackListCustomers(List<BlackListCustomers> blackListCustomers) {
		this.blackListCustomers = blackListCustomers;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<FinBlacklistCustomer> getFinBlacklistCustomer() {
		return finBlacklistCustomer;
	}

	public void setFinBlacklistCustomer(List<FinBlacklistCustomer> finBlacklistCustomer) {
		this.finBlacklistCustomer = finBlacklistCustomer;
	}

	public static MasterDefDAO getMasterDefDAO() {
		return masterDefDAO;
	}

	public static void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		FetchBlackListDetails.masterDefDAO = masterDefDAO;
	}

}
