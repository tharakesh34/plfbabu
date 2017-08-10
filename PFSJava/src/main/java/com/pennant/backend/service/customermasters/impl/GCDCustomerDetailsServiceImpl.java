package com.pennant.backend.service.customermasters.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.customermasters.impl.GCDCustomerBajjajDAOImpl;
import com.pennant.backend.dao.finance.GCDCustomerDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.customermasters.GCDCustomerService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.resource.Literal;

public class GCDCustomerDetailsServiceImpl implements GCDCustomerService{
	private static final Logger logger = Logger.getLogger(GCDCustomerDetailsServiceImpl.class);

	private GCDCustomerDAO gCDCustomerDAO;
	private GCDCustomerBajjajDAOImpl gCDCustomerBajjajDAO;
	@Override
	public void processGcdCustomer(CustomerDetails custDetail, String method) {
		logger.debug(Literal.ENTERING);

		GcdCustomer customer =custDetail.getGcdCustomer();
		if (PennantConstants.CUSTOMER_DEDUP_INSERT.equals(method)) {
			customer.setInsertUpdateFlag(PennantConstants.CUSTOMER_DEDUP_INSERT);
		} else { 
			customer.setInsertUpdateFlag(PennantConstants.CUSTOMER_DEDUP_UPDATE);
		}
		customer = preparegcdCustomer(custDetail, customer);
		gCDCustomerDAO.save(customer);
		gCDCustomerBajjajDAO.callStoredProcedure(custDetail,customer);

		logger.debug(Literal.LEAVING);
	}

	private GcdCustomer preparegcdCustomer(CustomerDetails customerDetails, GcdCustomer gcdCustomer) {
		Customer customer = customerDetails.getCustomer();

		gcdCustomer.setCustId(customer.getCustID());
		gcdCustomer.setFinCustId(customer.getCustCoreBank());
		gcdCustomer.setCustomerName(customer.getCustShrtName());
		gcdCustomer.setConstId(Long.valueOf(customer.getCustTypeCode()));
		gcdCustomer.setIndustryId(Long.parseLong(customer.getCustIndustry()));

		if ("RETAIL".equalsIgnoreCase(customer.getCustCtgCode())) {
			gcdCustomer.setIndvCorpFlag("I");
			gcdCustomer.setDOB(customer.getCustDOB());// setting DOB for retailer

			for (CustomerEmploymentDetail custEmplymentDetail : customerDetails.getEmploymentDetailsList()) { // setting YearsOfCurrJob for retailer type
				if (custEmplymentDetail.isCurrentEmployer())
					gcdCustomer.setYearsOfCurrJob(custEmplymentDetail.getCustEmpFrom());
			}

			/*
			 * if (!financeDetail.getGurantorsDetailList().isEmpty()) { GuarantorDetail guarantorDetail =
			 * financeDetail.getGurantorsDetailList().get(0); gcdCustomer.setNomineeName(guarantorDetail.getName());
			 * gcdCustomer.setNomineeAddress(guarantorDetail.getAddrCountry() + comma + guarantorDetail.getAddrHNbr() +
			 * comma + guarantorDetail.getFlatNbr() + comma + guarantorDetail.getAddrStreet() + comma +
			 * guarantorDetail.getLovDescAddrCountryName() + comma + guarantorDetail.getLovDescAddrCityName() + comma +
			 * guarantorDetail.getAddrZIP()); }
			 */
		} else if ("CORP".equalsIgnoreCase(customer.getCustCtgCode())) {
			gcdCustomer.setIndvCorpFlag("C");
			gcdCustomer.setDOI(customer.getCustDOB()); // setting DOI for corporate
		}

		gcdCustomer.setfName(customer.getCustFName());
		gcdCustomer.setmName(customer.getCustMName());
		gcdCustomer.setLname(customer.getCustLName());
		gcdCustomer.setSex(customer.getCustGenderCode());
		gcdCustomer.setMakerDate(DateUtility.getAppDate());
		gcdCustomer.setAuthDate(DateUtility.getAppDate());
		gcdCustomer.setPassportNo(customer.getCustPassportNo());
		gcdCustomer.setPanNo(customer.getCustCRCPR());
		gcdCustomer.setCustSearchId(customer.getCustCRCPR());
		gcdCustomer.setSectorId(Long.parseLong(customer.getCustSector()));
		gcdCustomer.setAddressDetail(prepareGcdCustAddress(2, customerDetails));
		//gcdCustomer.setBankDetail(prepareGcdCustBankDetail(mandate, customer.getLovDescCustDftBranchName()));
		gcdCustomer.setFinnCustId(customer.getCustCoreBank());
		gcdCustomer.setSfdcCustomerId(customer.getCustID());
		gcdCustomer.setBranchId(Long.parseLong(customer.getCustDftBranch()));
		if(customer.getCustAddlDec1() != null) {
			gcdCustomer.setEmiCardElig(String.valueOf(PennantApplicationUtil.formateAmount(customer.getCustAddlDec1(),
					CurrencyUtil.getFormat(customer.getCustBaseCcy()))));
		}

		return gcdCustomer;
	}

	public String prepareGcdCustAddress(int noOfAddress, CustomerDetails customerDetails) {
		String addressDetails = "";
		final char separator = '~';
		String phoneNo = "";
		String phoneAreaCode = "";
		String eMail = "";
		int noOfAddressFlag = 1;
		List<CustomerAddres> custAddressList = customerDetails.getAddressList();
		if (custAddressList != null) {
			Collections.sort(custAddressList, new Comparator<CustomerAddres>() {
				public int compare(CustomerAddres o1, CustomerAddres o2) {
					return (int) (o2.getCustAddrPriority() - o1.getCustAddrPriority());
				}
			});

			for (CustomerAddres address : custAddressList) {
				if (noOfAddress >= noOfAddressFlag) {
					char isPriorityVeryHigh = 'N';
					if (address.getCustAddrPriority() == 5) {
						isPriorityVeryHigh = 'Y';
					}
					phoneNo = "";
					phoneAreaCode = "";
					eMail = "";

					for (CustomerPhoneNumber phone : customerDetails.getCustomerPhoneNumList()) {
						if (phone.getPhoneTypePriority() == address.getCustAddrPriority()) {
							phoneNo = phone.getPhoneNumber();
							phoneAreaCode = phone.getPhoneAreaCode();
						}
					}

					for (CustomerEMail mail : customerDetails.getCustomerEMailList()) {
						if (mail.getCustEMailPriority() == address.getCustAddrPriority()) {
							eMail = mail.getCustEMail();
						}
					}

					addressDetails += address.getCustAddrType() + separator + address.getCustAddrCity() + separator
							+ address.getCustAddrCountry() + separator + address.getCustAddrProvince() + separator
							+ address.getCustAddrZIP() + separator + phoneNo + separator + isPriorityVeryHigh
							+ separator + address.getCustAddrHNbr() + separator + address.getCustFlatNbr() + separator
							+ address.getCustAddrStreet() + separator + address.getCustAddrLine1() + separator
							+ address.getCustAddrLine2() + separator + phoneAreaCode + separator + separator + eMail
							+ ";";
					noOfAddressFlag++;
				}
			}
		}
		return addressDetails;

	}

	public String prepareGcdCustBankDetail(Mandate mandate, String bankBranchName) {
		final char separator = '~';
		String bankDetails = mandate.getBankBranchID() + "~" + mandate.getBankName() + separator + bankBranchName
				+ separator + (StringUtils.equals(mandate.getAccType(), "10") ? "Saving" : "Current") + separator
				+ mandate.getAccNumber() + ";";
		return bankDetails;
	}
	
	public void setGCDCustomerDAO(GCDCustomerDAO gCDCustomerDAO) {
		this.gCDCustomerDAO = gCDCustomerDAO;
	}

	public void setGCDCustomerBajjajDAO(GCDCustomerBajjajDAOImpl gCDCustomerBajjajDAO) {
		this.gCDCustomerBajjajDAO = gCDCustomerBajjajDAO;
	}

}