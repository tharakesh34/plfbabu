package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.corebanking.interfaces.CustomerInterfaceCall;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CustomerInterfaceData;

public class PFFCustomerPreparation {
	private static final String DEFAULT_CCY 		 = "BHD";
	private static final String DEFAULT_COUNTRY 	 = "BH";
	private static final String PHONE_TYEP_MOBILE 	 = "MOBILE";
	private static final String PHONE_TYEP_OFFICE 	 = "OFFICE";
	private static final String PHONE_TYEP_RESIDENCE = "WORK";
	private static final String PHONE_TYEP_OTHER 	 = "GENERAL";
	private static final String GENDER_MALE 	 	 = "MALE";
	private static final String GENDER_FEMALE 	 	 = "FEMALE";
	private static final String GENDER_OTHER 	 	 = "OTH";

	private CustomerInterfaceCall customerInterfaceCall;
	private CustomerDetailsService customerDetailsService;
	private final static Logger logger = Logger.getLogger(PFFCustomerPreparation.class);

	public CustomerDetails getCustomerByInterface(String custCIF, String custLoc) throws CustomerNotFoundException {
		try{
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewRecord(true);
		logger.debug("Before Customer Interface Call ");
		CustomerInterfaceData customerInterfaceData = customerInterfaceCall.getCustomerFullDetails(custCIF, custLoc);
		logger.debug("After Customer Interface Call ");
		if (customerInterfaceData != null) {

			//+++++++++++++++ Customer ++++++++++++++++
			Long custid=Long.parseLong(custCIF);
			Customer customer = new Customer();
			customer.setNewRecord(true);
			customer.setCustID(custid);
			customer.setCustCIF(custCIF);
			customer.setCustFName(customerInterfaceData.getCustFName());
			customer.setCustTypeCode(customerInterfaceData.getCustTypeCode());
			customer.setLovDescCustTypeCodeName(customerInterfaceData.getCustTypeCode());//lov
			customer.setCustIsBlocked(getBoolean(customerInterfaceData.getCustIsBlocked()));
			customer.setCustIsActive(getBoolean(customerInterfaceData.getCustIsActive()));
			customer.setCustDftBranch(customerInterfaceData.getCustDftBranch());
			customer.setLovDescCustDftBranchName(customerInterfaceData.getCustDftBranch());//lov
			customer.setCustGroupID(StringUtils.trimToEmpty(customerInterfaceData.getGroupName()).equals("") ? 0 : Long.parseLong(customerInterfaceData.getGroupName()));
			customer.setLovDescCustGroupCode(StringUtils.trimToEmpty(customerInterfaceData.getGroupName()));
			customer.setCustParentCountry(customerInterfaceData.getCustParentCountry());
			customer.setCustRiskCountry(customerInterfaceData.getCustRiskCountry());
			customer.setLovDescCustRiskCountryName(customerInterfaceData.getCustRiskCountry());
			customer.setCustDOB(getDMYDate(customerInterfaceData.getCustDOB()));
			customer.setCustSalutationCode(customerInterfaceData.getCustSalutationCode());
			customer.setLovDescCustSalutationCodeName(customerInterfaceData.getCustSalutationCode());
			String genderCode = "";
			if (StringUtils.trimToEmpty(customerInterfaceData.getCustGenderCode()).equals("M")) {
				genderCode = GENDER_MALE;
			} else if (StringUtils.trimToEmpty(customerInterfaceData.getCustGenderCode()).equals("F")) {
				genderCode = GENDER_FEMALE;
			}else{
				genderCode = GENDER_OTHER;
			}
			customer.setCustGenderCode(genderCode);
			//customer.setLovDescCustGenderCodeName(genderCode);//Not Required Changed to Combobox
			customer.setCustPOB(customerInterfaceData.getCustPOB());
			customer.setCustPassportNo(customerInterfaceData.getCustPassportNo());
			customer.setCustPassportExpiry(formatCYMDDate(customerInterfaceData.getCustPassportExpiry()));
			customer.setCustIsMinor(getBoolean(customerInterfaceData.getCustIsMinor()));
			customer.setCustTradeLicenceNum(customerInterfaceData.getTradeLicensenumber());
			customer.setCustTradeLicenceExpiry(formatCYMDDate(customerInterfaceData.getTradeLicenseExpiry()));
			customer.setCustVisaNum(customerInterfaceData.getVisaNumber());
			customer.setCustVisaExpiry(formatCYMDDate(customerInterfaceData.getVisaExpirydate()));
			customer.setCustCoreBank(customerInterfaceData.getCustCoreBank());
			customer.setLovDescCustCtgType(customerInterfaceData.getCustCtgCode());
			if (StringUtils.trimToEmpty(customerInterfaceData.getCustCtgCode()).equals(PennantConstants.INTERFACE_CUSTCTG_INDIV)) {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_INDIV);
			}else if (StringUtils.trimToEmpty(customerInterfaceData.getCustCtgCode()).equals(PennantConstants.INTERFACE_CUSTCTG_CORP)) {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_CORP);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_CORP);
			}else if (StringUtils.trimToEmpty(customerInterfaceData.getCustCtgCode()).equals(PennantConstants.INTERFACE_CUSTCTG_BANK)) {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_BANK);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_BANK);
            }
			customer.setCustShrtName(customerInterfaceData.getCustShrtName());
			customer.setCustFNameLclLng(customerInterfaceData.getCustFNameLclLng());
			customer.setCustShrtNameLclLng(customerInterfaceData.getCustShrtNameLclLng());
			customer.setCustCOB(customerInterfaceData.getCustCOB());
			customer.setCustRO1(customerInterfaceData.getCustRO1());
			customer.setLovDescCustRO1Name(customerInterfaceData.getCustRO1());
			customer.setCustIsClosed(getBoolean(customerInterfaceData.getCustIsClosed()));
			customer.setCustIsDecease(getBoolean(customerInterfaceData.getCustIsDecease()));
			customer.setCustIsTradeFinCust(getBoolean(customerInterfaceData.getCustIsTradeFinCust()));
			customer.setCustSector(customerInterfaceData.getCustSector());
			customer.setLovDescCustSectorName(customerInterfaceData.getCustSector());//lov
			customer.setCustSubSector(customerInterfaceData.getCustSubSector());
			customer.setLovDescCustSubSectorName(customerInterfaceData.getCustSubSector());//lov
			customer.setCustProfession("");//TODO
			customer.setLovDescCustProfessionName(customerInterfaceData.getCustProfession());//lov
			customer.setCustTotalIncome(customerInterfaceData.getCustTotalIncome() != null ? new BigDecimal(customerInterfaceData.getCustTotalIncome().toString()) : BigDecimal.ZERO);
			customer.setCustMaritalSts(customerInterfaceData.getCustMaritalSts());
			customer.setCustEmpSts(customerInterfaceData.getCustEmpSts());
			customer.setLovDescCustEmpStsName(customerInterfaceData.getCustEmpSts());//lov
			customer.setCustBaseCcy(StringUtils.trimToEmpty(customerInterfaceData.getCustBaseCcy()).equals("") ? DEFAULT_CCY : customerInterfaceData.getCustBaseCcy());
			customer.setLovDescCustBaseCcyName(customer.getCustBaseCcy());//lov
			customer.setCustResdCountry(customerInterfaceData.getCustResdCountry());
			customer.setLovDescCustResdCountryName(customerInterfaceData.getCustResdCountry());//lov
			customer.setCustNationality(customerInterfaceData.getCustResdCountry());
			customer.setLovDescCustNationalityName(customerInterfaceData.getCustResdCountry());//lov
			customer.setCustClosedOn(formatCYMDDate(customerInterfaceData.getCustClosedOn().toString()));
			customer.setCustStmtFrq(getPFFFrequecny(customerInterfaceData.getCustStmtFrq()));
			customer.setCustIsStmtCombined(getBoolean(customerInterfaceData.getCustIsStmtCombined()));
			//customer.setCustStmtLastDate(new Timestamp(formatCYMDDate(customerInterfaceData.getCustStmtLastDate().toString()).getTime()));
			//customer.setCustStmtNextDate(new Timestamp(formatCYMDDate(customerInterfaceData.getCustStmtNextDate().toString()).getTime()));
			customer.setCustFirstBusinessDate(formatCYMDDate(customerInterfaceData.getCustFirstBusinessDate().toString()));
			customer.setCustRelation(customerInterfaceData.getCustRelation());
			customerDetails.setCustomer(customer);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//<!-- Address Details-->
			
			customerDetails.setAddressList(new ArrayList<CustomerAddres>(1));
			if (!StringUtils.trimToEmpty(customerInterfaceData.getCustAddrType()).equals("")) {
				CustomerAddres customerAddres = new CustomerAddres();
				customerAddres.setRecordType(PennantConstants.RCD_ADD);
				customerAddres.setCustID(custid);
				customerAddres.setLovDescCustCIF(custCIF);
				customerAddres.setCustAddrType(customerInterfaceData.getCustAddrType());
				customerAddres.setLovDescCustAddrTypeName(customerInterfaceData.getCustAddrType());
				customerAddres.setCustAddrHNbr(customerInterfaceData.getCustAddrHNbr());
				customerAddres.setCustFlatNbr(customerInterfaceData.getCustFlatNbr());
				customerAddres.setCustAddrStreet(customerInterfaceData.getCustAddrStreet());
				customerAddres.setCustAddrLine1(customerInterfaceData.getCustAddrLine1());
				customerAddres.setCustAddrLine2(customerInterfaceData.getCustAddrLine2());
				//Length Mismatch from Equation//customerAddres.setCustPOBox(customerInterfaceData.getCustPOBox());
				//Length Mismatch from Equation//customerAddres.setCustAddrCity(StringUtils.trimToNull(customerInterfaceData.getCustAddrCity()));
				//Length Mismatch from Equation//customerAddres.setLovDescCustAddrCityName(customerInterfaceData.getCustAddrCity());
				//Length Mismatch from Equation//customerAddres.setCustAddrProvince(StringUtils.trimToNull(customerInterfaceData.getCustAddrProvince()));
				//Length Mismatch from Equation//customerAddres.setCustAddrCountry(StringUtils.trimToNull(customerInterfaceData.getCustAddrCountry()));
				//Length Mismatch from Equation//customerAddres.setLovDescCustAddrCountryName(customerInterfaceData.getCustAddrCountry());
				customerAddres.setCustAddrZIP(customerInterfaceData.getCustAddrZIP());
				customerAddres.setCustAddrPhone(customerInterfaceData.getCustAddrPhone());
				customerDetails.getAddressList().add(customerAddres);
			}
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//<!-- customer phone numbers  1,2,3,4-->	
			customerDetails.setCustomerPhoneNumList(new ArrayList<CustomerPhoneNumber>());
			String custOfficePhone=StringUtils.trimToEmpty(customerInterfaceData.getCustOfficePhone());
			//Length Mismatch from Equation
			if (!custOfficePhone.equals("") && custOfficePhone.length()<12) {
				CustomerPhoneNumber customerPhoneNumber1 = new CustomerPhoneNumber();
				customerPhoneNumber1.setRecordType(PennantConstants.RCD_ADD);
				customerPhoneNumber1.setPhoneCustID(custid);
				customerPhoneNumber1.setLovDescCustCIF(custCIF);
				customerPhoneNumber1.setPhoneTypeCode(PHONE_TYEP_OFFICE);
				customerPhoneNumber1.setPhoneCountryCode(DEFAULT_COUNTRY);
				customerPhoneNumber1.setPhoneAreaCode(DEFAULT_COUNTRY);
				customerPhoneNumber1.setPhoneNumber(custOfficePhone);
				customerDetails.getCustomerPhoneNumList().add(customerPhoneNumber1);
			}
			String custMobile=StringUtils.trimToEmpty(customerInterfaceData.getCustMobile());
			//Length Mismatch from Equation
			if (!custMobile.equals("") && custMobile.length()<12) {
				CustomerPhoneNumber customerPhoneNumber2 = new CustomerPhoneNumber();
				customerPhoneNumber2.setRecordType(PennantConstants.RCD_ADD);
				customerPhoneNumber2.setPhoneCustID(custid);
				customerPhoneNumber2.setLovDescCustCIF(custCIF);
				customerPhoneNumber2.setPhoneTypeCode(PHONE_TYEP_MOBILE);
				customerPhoneNumber2.setPhoneCountryCode(DEFAULT_COUNTRY);
				customerPhoneNumber2.setPhoneAreaCode(DEFAULT_COUNTRY);
				customerPhoneNumber2.setPhoneNumber(custMobile);
				customerDetails.getCustomerPhoneNumList().add(customerPhoneNumber2);
			}
			String custResPhone=StringUtils.trimToEmpty(customerInterfaceData.getCustResPhone());
			//Length Mismatch from Equation
			if (!custResPhone.equals("") && custResPhone.length()<12) {
				CustomerPhoneNumber customerPhoneNumber3 = new CustomerPhoneNumber();
				customerPhoneNumber3.setRecordType(PennantConstants.RCD_ADD);
				customerPhoneNumber3.setPhoneCustID(custid);
				customerPhoneNumber3.setLovDescCustCIF(custCIF);
				customerPhoneNumber3.setPhoneTypeCode(PHONE_TYEP_RESIDENCE);
				customerPhoneNumber3.setPhoneCountryCode(DEFAULT_COUNTRY);
				customerPhoneNumber3.setPhoneAreaCode(DEFAULT_COUNTRY);
				customerPhoneNumber3.setPhoneNumber(custResPhone);
				customerDetails.getCustomerPhoneNumList().add(customerPhoneNumber3);
			}
			String custOtherPhone=StringUtils.trimToEmpty(customerInterfaceData.getCustOtherPhone());
			//Length Mismatch from Equation
			if (!custOtherPhone.equals("") && custOtherPhone.length()<12) {
				CustomerPhoneNumber customerPhoneNumber4 = new CustomerPhoneNumber();
				customerPhoneNumber4.setRecordType(PennantConstants.RCD_ADD);
				customerPhoneNumber4.setPhoneCustID(custid);
				customerPhoneNumber4.setLovDescCustCIF(custCIF);
				customerPhoneNumber4.setPhoneTypeCode(PHONE_TYEP_OTHER);
				customerPhoneNumber4.setPhoneCountryCode(DEFAULT_COUNTRY);
				customerPhoneNumber4.setPhoneAreaCode(DEFAULT_COUNTRY);
				customerPhoneNumber4.setPhoneNumber(custOtherPhone);
				customerDetails.getCustomerPhoneNumList().add(customerPhoneNumber4);
			}
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//<!-- Email Details 1 and 2-->
			customerDetails.setCustomerEMailList(new ArrayList<CustomerEMail>());
			if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMail1()).equals("")) {
				CustomerEMail customerEMail1 = new CustomerEMail();
				customerEMail1.setRecordType(PennantConstants.RCD_ADD);
				customerEMail1.setCustID(custid);
				customerEMail1.setLovDescCustCIF(custCIF);
				customerEMail1.setLovDescCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode1());
				customerEMail1.setCustEMailPriority(1);
				customerEMail1.setCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode1());
				customerEMail1.setCustEMail(customerInterfaceData.getCustEMail1());
				customerDetails.getCustomerEMailList().add(customerEMail1);
			}
			if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMail2()).equals("")) {
				CustomerEMail customerEMail2 = new CustomerEMail();
				customerEMail2.setRecordType(PennantConstants.RCD_ADD);
				customerEMail2.setCustID(custid);
				customerEMail2.setLovDescCustCIF(custCIF);
				customerEMail2.setLovDescCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode2());
				customerEMail2.setCustEMailPriority(2);
				customerEMail2.setCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode2());
				customerEMail2.setCustEMail(customerInterfaceData.getCustEMail2());
				customerDetails.getCustomerEMailList().add(customerEMail2);
			}
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//		<!-- customer ratings-->	
			
			customerDetails.setRatingsList(new ArrayList<CustomerRating>());
			List<CustomerRating> customerRatings = new ArrayList<CustomerRating>();
			for (com.pennant.coreinterface.vo.CustomerInterfaceData.CustomerRating custRating : customerInterfaceData.getCustomerRatinglist()) {
				CustomerRating customerRating = new CustomerRating();
				customerRating.setRecordType(PennantConstants.RCD_ADD);
				customerRating.setCustID(custid);
				customerRating.setLovDescCustCIF(custCIF);
				customerRating.setCustRatingType(custRating.getCustRatingType());
				customerRating.setLovDescCustRatingTypeName(custRating.getCustRatingType());
				customerRating.setCustRatingCode(custRating.getCustLongRate());
				customerRating.setCustRating(custRating.getCustShortRate());
				customerRatings.add(customerRating);
			}
			customerDetails.getRatingsList().addAll(customerRatings);

			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//		<!-- customer Documents-->	
			
			customerDetails.setCustomerDocumentsList(new ArrayList<CustomerDocument>());
			List<CustomerDocument> customerDocuments = new ArrayList<CustomerDocument>();
			for (com.pennant.coreinterface.vo.CustomerInterfaceData.CustomerIdentity custRating : customerInterfaceData.getCustomerIdentitylist()) {
				CustomerDocument document = new CustomerDocument();
				document.setRecordType(PennantConstants.RCD_ADD);
				document.setCustID(custid);
				document.setLovDescCustCIF(custCIF);
				document.setCustDocCategory(custRating.getCustIDType());
				document.setCustDocType(custRating.getCustIDType());
				document.setLovDescCustDocCategory(custRating.getCustIDType());
				document.setCustDocTitle(custRating.getCustIDNumber());
				//Length Mismatch from Equation//document.setCustDocIssuedCountry(custRating.getCustIDCountry());
				//Length Mismatch from Equation//document.setLovDescCustDocIssuedCountry(custRating.getCustIDCountry());
				document.setCustDocIssuedOn(formatCYMDDate(custRating.getCustIDIssueDate().toString()));
				document.setCustDocExpDate(formatCYMDDate(custRating.getCustIDExpDate().toString()));
				customerDocuments.add(document);
			}
			customerDetails.getCustomerDocumentsList().addAll(customerDocuments);
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//		<!-- ShareHolder Details-->	
			
			customerDetails.setCustomerDirectorList(new ArrayList<DirectorDetail>());
			List<DirectorDetail> directorDetailList = new ArrayList<DirectorDetail>();
			for (com.pennant.coreinterface.vo.CustomerInterfaceData.ShareHolder shareHolder : customerInterfaceData.getShareHolderlist()) {
				DirectorDetail directorDetail = getCustomerDetailsService().getNewDirectorDetail();
				directorDetail.setRecordType(PennantConstants.RCD_ADD);
				directorDetail.setCustID(custid);
				directorDetail.setLovDescCustCIF(custCIF);
				directorDetail.setShareholder(true);
				directorDetail.setIdType(shareHolder.getShareHolderIDType().toString());
				directorDetail.setLovDescCustDocCategoryName(shareHolder.getShareHolderIDType().toString());
				directorDetail.setIdReference(shareHolder.getShareHolderIDRef());
				directorDetail.setSharePerc(shareHolder.getShareHolderPerc() != null ? new BigDecimal(shareHolder.getShareHolderPerc().toString()) : BigDecimal.ZERO);
				directorDetail.setLovDescDesignationName(shareHolder.getShareHolderRole());
				directorDetail.setShortName(shareHolder.getShareHolderName());
				directorDetail.setNationality(shareHolder.getShareHolderNation());
				directorDetail.setLovDescNationalityName(shareHolder.getShareHolderNation());
				directorDetail.setCustAddrCountry(shareHolder.getShareHolderRisk());
				directorDetail.setLovDescCustAddrCountryName(shareHolder.getShareHolderRisk());
				directorDetail.setDob(formatCYMDDate(shareHolder.getShareHolderDOB().toString()));
				directorDetailList.add(directorDetail);
			}
			customerDetails.getCustomerDirectorList().addAll(directorDetailList);
			
			
			//<!-- Employee Details-->
			
			CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
			//customerEmploymentDetail.setCustEmpName(customerInterfaceData.getCustEmpName());
			//customerEmploymentDetail.setCustEmpFrom(formatCYMDDate(customerInterfaceData.getCustEmpFrom().toString()));
			customerEmploymentDetail.setCustEmpDesg(customerInterfaceData.getCustEmpDesg());
			
//			customerEmploymentDetail.setCustEmpHNbr(customerInterfaceData.getCustEmpHNbr());
//			customerEmploymentDetail.setCustEMpFlatNbr(customerInterfaceData.getCustEMpFlatNbr());
//			customerEmploymentDetail.setCustEmpAddrStreet(customerInterfaceData.getCustEmpAddrStreet());
//			customerEmploymentDetail.setCustEMpAddrLine1(customerInterfaceData.getCustEMpAddrLine1());
//			customerEmploymentDetail.setCustEMpAddrLine2(customerInterfaceData.getCustEMpAddrLine2());
//			customerEmploymentDetail.setCustEmpPOBox(customerInterfaceData.getCustEmpPOBox());
//			customerEmploymentDetail.setCustEmpAddrCity(customerInterfaceData.getCustEmpAddrCity());
//			customerEmploymentDetail.setCustEmpAddrProvince(customerInterfaceData.getCustEmpAddrProvince());
//			customerEmploymentDetail.setCustEmpAddrCountry(customerInterfaceData.getCustEmpAddrCountry());
//			customerEmploymentDetail.setCustEmpAddrZIP(customerInterfaceData.getCustEmpAddrZIP());
//			customerEmploymentDetail.setCustEmpAddrPhone(customerInterfaceData.getCustEmpAddrPhone());
			//customerDetails.setCustomerEmploymentDetail(customerEmploymentDetail);
			
			//+++++++++++++++++++++++++++++++++++++++++++++++++

			//<!-- customer Income-->	
			
			customerDetails.setCustomerIncomeList(new ArrayList<CustomerIncome>());
			CustomerIncome customerIncome = new CustomerIncome();
			customerIncome.setCustIncomeType(customerInterfaceData.getCustIncomeType());
			customerIncome.setCustIncome(customerInterfaceData.getCustIncome() != null ? new BigDecimal(customerInterfaceData.getCustIncome()) : BigDecimal.ZERO);
			//customerDetails.getCustomerIncomeList().add(customerIncome);
			//++++++++++++++++++++++++++++++++
		
			customerDetails.setEmploymentDetailsList(new ArrayList<CustomerEmploymentDetail>());
			setCustomerStatus(customerDetails);
			return customerDetails;
		}
		}catch (Exception e) {
			logger.debug(e);
			throw new CustomerNotFoundException(e.getMessage());
		}
		return null;
	}

	public void setCustomerInterfaceCall(CustomerInterfaceCall customerInterfaceCall) {
		this.customerInterfaceCall = customerInterfaceCall;
	}

	public CustomerInterfaceCall getCustomerInterfaceCall() {
		return customerInterfaceCall;
	}

	private boolean getBoolean(String string) {
		if (StringUtils.trimToEmpty(string).equals("Y")) {
			return true;
		} else {
			return false;
		}

	}

	private String getPFFFrequecny(String custStmtFrq) {
		StringBuilder frquecy = new StringBuilder("");
		if (!StringUtils.trimToEmpty(custStmtFrq).equals("") && StringUtils.trimToEmpty(custStmtFrq).length() == 3) {
			String firstIndex = custStmtFrq.substring(0, 1);
			String remaining = custStmtFrq.substring(1, 3);

			char first = (firstIndex.toUpperCase()).charAt(0);
			switch (first) {
			case 'A':
				frquecy.append("Y01");
				frquecy.append(remaining);
				break;
			case 'B':
				frquecy.append("Y02");
				frquecy.append(remaining);
				break;
			case 'C':
				frquecy.append("Y03");
				frquecy.append(remaining);
				break;
			case 'D':
				frquecy.append("Y04");
				frquecy.append(remaining);
				break;
			case 'E':
				frquecy.append("Y05");
				frquecy.append(remaining);
				break;
			case 'F':
				frquecy.append("Y06");
				frquecy.append(remaining);
				break;
			case 'G':
				frquecy.append("Y07");
				frquecy.append(remaining);
				break;
			case 'H':
				frquecy.append("Y08");
				frquecy.append(remaining);
				break;
			case 'I':
				frquecy.append("Y09");
				frquecy.append(remaining);
				break;
			case 'J':
				frquecy.append("Y10");
				frquecy.append(remaining);
				break;
			case 'K':
				frquecy.append("Y11");
				frquecy.append(remaining);
				break;
			case 'L':
				frquecy.append("Y12");
				frquecy.append(remaining);
				break;
			case 'M':
				frquecy.append("H01");
				frquecy.append(remaining);
				break;
			case 'N':
				frquecy.append("H02");
				frquecy.append(remaining);
				break;
			case 'O':
				frquecy.append("H03");
				frquecy.append(remaining);
				break;
			case 'P':
				frquecy.append("H04");
				frquecy.append(remaining);
				break;
			case 'Q':
				frquecy.append("H06");
				frquecy.append(remaining);
				break;
			case 'R':
				frquecy.append("H01");
				frquecy.append(remaining);
				break;
			case 'S':
				frquecy.append("Q01");
				frquecy.append(remaining);
				break;
			case 'T':
				frquecy.append("Q02");
				frquecy.append(remaining);
				break;
			case 'U':
				frquecy.append("Q03");
				frquecy.append(remaining);
				break;
			case 'V':
				frquecy.append("M00");
				frquecy.append(remaining);
				break;
			case 'W':
				//Weekly
				frquecy.append("W00");
				frquecy.append(remaining);
				break;
			case 'Y':
				//Fortnightly
				frquecy.append("F00");
				frquecy.append(remaining);
				break;
			case 'Z':
				//Daily
				frquecy.append("D0000");
				break;
			default:
				break;
			}
		}
		return frquecy.toString();
	}

	private Date formatCYMDDate(String date) {
		try {
			return 	DateUtility.convertDateFromAS400(new BigDecimal(date));
		} catch (Exception e) {
			return null;
		}

	}

	private Date getDMYDate(String date) {
		try {
			if (StringUtils.trimToEmpty(date).length()!=8 && StringUtils.trimToEmpty(date).length()!=7) {
				return null;
            }
			if (StringUtils.trimToEmpty(date).length()==7) {
				date="0"+StringUtils.trimToEmpty(date);
            }
	        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
	        java.util.Date uDate = null;
	        try {
		        uDate = df.parse(date);
	        } catch (ParseException pe) {
		        pe.printStackTrace();
	        }
	        return new Date(uDate.getTime());
        } catch (Exception e) {
    		return null;
        }
	}
	private void setCustomerStatus(CustomerDetails customerDetails) {
		try {
			if (StringUtils.trimToEmpty(customerDetails.getCustomer().getCustSts()).equals("")) {
				CustomerStatusCode customerStatusCode = getCustomerDetailsService().getCustStatusByMinDueDays();
				if (customerStatusCode != null) {
					customerDetails.getCustomer().setCustSts(customerStatusCode.getCustStsCode());
					customerDetails.getCustomer().setLovDescCustStsName(customerStatusCode.getCustStsDescription());
				}
			}
		} catch (Exception e) {
			logger.debug("Customer Status by Min Due Days " + e);
		}
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
	    this.customerDetailsService = customerDetailsService;
    }

	public CustomerDetailsService getCustomerDetailsService() {
	    return customerDetailsService;
    }

}
