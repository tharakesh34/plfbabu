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
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CustomerInterfaceData;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.coreinterface.service.CustomerDataProcess;
import com.pennant.equation.dao.CoreInterfaceDAO;

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

	private CustomerDataProcess customerDataProcess;
	private CustomerDetailsService customerDetailsService;
	private CoreInterfaceDAO coreInterfaceDAO;
	
	List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
	
	private final static Logger logger = Logger.getLogger(PFFCustomerPreparation.class);

	public CustomerDetails getCustomerByInterface(String custCIF, String custLoc) throws CustomerNotFoundException {
		try{
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewRecord(true);
		logger.debug("Before Customer Data Process Call ");
		CustomerInterfaceData customerInterfaceData = getCustomerDataProcess().getCustomerFullDetails(custCIF, custLoc);
		logger.debug("After Customer Data Process Call ");
		if (customerInterfaceData != null) {
			CustomerDetails cDetails = processCustomerDetails(customerInterfaceData);
			if(cDetails != null){
				setCustomerStatus(cDetails);
			}
			return cDetails;
		}
		}catch (Exception e) {
			logger.debug(e);
			throw new CustomerNotFoundException(e.getMessage());
		}
		return null;
	}

	
	/**Processing Customer Data and its child data 
	 * @param customerInterfaceData
	 * @return
	 */
	public CustomerDetails processCustomerDetails(CustomerInterfaceData customerInterfaceData){
		logger.debug("Entering");
		if (customerInterfaceData != null) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setNewRecord(true);
			//+++++++++++++++ Customer ++++++++++++++++
			String custCIF = customerInterfaceData.getCustCIF();
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
				customerAddres.setCustPOBox(customerInterfaceData.getCustPOBox());
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
			if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMailTypeCode1()).equals("")) {
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
			if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMailTypeCode2()).equals("")) {
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
			for (com.pennant.coreinterface.model.CustomerInterfaceData.CustomerRating custRating : customerInterfaceData.getCustomerRatinglist()) {
				CustomerRating customerRating = new CustomerRating();
				customerRating.setRecordType(PennantConstants.RCD_ADD);
				customerRating.setCustID(custid);
				customerRating.setLovDescCustCIF(custCIF);
				customerRating.setCustRatingType(custRating.getCustRatingType());
				customerRating.setLovDescCustRatingTypeName(custRating.getCustRatingType());
				customerRating.setCustRatingCode(custRating.getCustLongRate());
				customerRating.setCustRating(custRating.getCustShortRate());
				customerDetails.getRatingsList().add(customerRating);
			}

			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//		<!-- customer Documents-->	
			
			customerDetails.setCustomerDocumentsList(new ArrayList<CustomerDocument>());
			for (com.pennant.coreinterface.model.CustomerInterfaceData.CustomerIdentity custIdentity : customerInterfaceData.getCustomerIdentitylist()) {
				CustomerDocument document = new CustomerDocument();
				document.setRecordType(PennantConstants.RCD_ADD);
				document.setCustID(custid);
				document.setLovDescCustCIF(custCIF);
				document.setCustDocCategory(custIdentity.getCustIDType());
				document.setCustDocType(custIdentity.getCustIDType());
				document.setLovDescCustDocCategory(custIdentity.getCustIDType());
				document.setCustDocTitle(custIdentity.getCustIDNumber());
				//Length Mismatch from Equation//document.setCustDocIssuedCountry(custRating.getCustIDCountry());
				//Length Mismatch from Equation//document.setLovDescCustDocIssuedCountry(custRating.getCustIDCountry());
				document.setCustDocIssuedOn(formatCYMDDate(custIdentity.getCustIDIssueDate().toString()));
				document.setCustDocExpDate(formatCYMDDate(custIdentity.getCustIDExpDate().toString()));
				customerDetails.getCustomerDocumentsList().add(document);
			}
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//		<!-- ShareHolder Details-->	
			
			customerDetails.setCustomerDirectorList(new ArrayList<DirectorDetail>());
			for (com.pennant.coreinterface.model.CustomerInterfaceData.ShareHolder shareHolder : customerInterfaceData.getShareHolderlist()) {
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
				customerDetails.getCustomerDirectorList().add(directorDetail);
			}
			
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
			logger.debug("Leaving");
			return customerDetails;
		}
		logger.debug("Leaving");
		return null;
	}
	
	
	/**This method will validate whether the customer fields exists in their respective 
	 * master tables or not
	 * @param customerDetails
	 * @param dateValueDate
	 * @return
	 */
	public List<CustomerDetails> validateMasterFieldDetails(List<CustomerDetails> customerDetails,Date dateValueDate){
		logger.debug("Entering");
		
		masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
		
		List<CustomerDetails> saveCustomerDetailsList = new ArrayList<CustomerDetails>();
		CustomerDetails saveCustomerDetail;
	    EquationMasterMissedDetail masterMissedDetail;
	  
	    List<Long> exisitingCustomerList = getCoreInterfaceDAO().fetchCustomerIdDetails();
	    
	    //Fetching customer related Master details
	    List<String> branchCodeMasterList = fetchBranchCodes();
	    List<Long> custGrpCodeMasterList = fetchCustomerGroupCodes();
		List<String> countryCodeMasterList = fetchCountryCodes();
		List<String> salutationCodeMasterList = fetchSalutationCodes();
		List<String> rShipOfficerCodeMasterList = fetchRelationshipOfficerCodes();
		List<SubSector> subSectorCodeMasterList = fetchSubSectorCodes();
		List<String> maritalStatusCodeMasterList = fetchMaritalStatusCodes();
		List<String> custEmpStsCodeMasterList = fetchEmpStsCodes();
		List<String> currencyCodeMasterList = fetchCurrencyCodes();
		List<String> custTypeCodeMasterList = fetchCustTypeCodes();
		
		List<String> addressTypeMasterList = getCoreInterfaceDAO().fetchAddressTypes();
		List<String> emailTypeMasterList = getCoreInterfaceDAO().fetchEMailTypes();
		
		for (CustomerDetails cDetails : customerDetails) {
			saveCustomerDetail = new CustomerDetails();
			saveCustomerDetail.setCustomer(cDetails.getCustomer());
			saveCustomerDetail.setCustomerPhoneNumList(cDetails.getCustomerPhoneNumList());
			Customer customer = cDetails.getCustomer();
			if(customer != null){
				masterMissedDetail = new EquationMasterMissedDetail();
				masterMissedDetail.setModule("Customers");
				masterMissedDetail.setLastMntOn(dateValueDate);
				if(customer.getCustDftBranch().equals("")){
					customer.setCustDftBranch(null);
				}else if(!valueExistInMaster(customer.getCustDftBranch(),branchCodeMasterList)){
					masterMissedDetail.setFieldName("CustDftBranch");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustDftBranch()+"' Value Does Not Exist In Master RMTBranches Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustDftBranch(""); //Making it empty to ignore the empty field updates in query while updating the record 
				}
				if(customer.getCustTypeCode().equals("")){
					customer.setCustTypeCode(null);
				}else if(!valueExistInMaster(customer.getCustTypeCode(),custTypeCodeMasterList)){
					masterMissedDetail.setFieldName("CustTypeCode");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustTypeCode()+"' Value Does Not Exist In Master RMTCustTypes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustTypeCode(""); 
				}
				if(customer.getCustGroupID() != 0 && !valueExistInMaster(customer.getCustGroupID(),custGrpCodeMasterList)){
					masterMissedDetail.setFieldName("CustGroupID");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustGroupID()+"' Value Does Not Exist In Master CustomerGroups Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustGroupID(-1);
				}
				if(customer.getCustCOB().equals("")){
					customer.setCustCOB(null); 
				}else if(!valueExistInMaster(customer.getCustCOB(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustCOB");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustCOB()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustCOB("");
				}
				if(customer.getCustParentCountry().equals("")){
					customer.setCustParentCountry(null);
				}else if(!valueExistInMaster(customer.getCustParentCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustParentCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustParentCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustParentCountry("");
				}
				if(customer.getCustRiskCountry().equals("")){
					customer.setCustRiskCountry(null);
				}else if(!valueExistInMaster(customer.getCustRiskCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustRiskCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRiskCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustRiskCountry("");
				}
				if(customer.getCustResdCountry().equals("")){
					customer.setCustResdCountry(null);
				}else if(!valueExistInMaster(customer.getCustResdCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustResdCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustResdCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustResdCountry("");
				}
				if(customer.getCustNationality().equals("")){
					customer.setCustNationality(null);
				}else if(!valueExistInMaster(customer.getCustNationality(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustNationality");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustNationality()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustNationality("");
				}
				if(customer.getCustSalutationCode().equals("")){
					customer.setCustSalutationCode(null);
				}else if(!valueExistInMaster(customer.getCustSalutationCode(),salutationCodeMasterList)){
					masterMissedDetail.setFieldName("CustSalutationCode");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustSalutationCode()+"' Value Does Not Exist In Master BMTSalutations Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustSalutationCode("");
				}
				if(customer.getCustRO1().equals("")){
					customer.setCustRO1(null);
				}else if(!valueExistInMaster(customer.getCustRO1(),rShipOfficerCodeMasterList)){
					masterMissedDetail.setFieldName("CustRO1");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRO1()+"' Value Does Not Exist In Master RelationshipOfficers Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustRO1("");
				}
				if(customer.getCustSector().equals("")  ||
						customer.getCustSubSector().equals("")){
					customer.setCustSector(null);
					customer.setCustSubSector(null);
				}else if(!valueExistInMaster(customer,subSectorCodeMasterList)){
					masterMissedDetail.setFieldName("CustSector/CustSubSector");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , CustSector:'"+customer.getCustSector()+
							"' and CustSubSector:'"+customer.getCustSubSector()+"' Values Does Not Exist In Master BMTSubSectors Table ");
					masterValueMissedDetails.add(masterMissedDetail);
					customer.setCustSector("");
					customer.setCustSubSector("");
				}
				if(customer.getCustMaritalSts().equals("")){
					customer.setCustMaritalSts(null);
				}else if(!valueExistInMaster(customer.getCustMaritalSts(),maritalStatusCodeMasterList)){
					masterMissedDetail.setFieldName("CustMaritalSts");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustMaritalSts()+"' Value Does Not Exist In Master BMTMaritalStatusCodes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustMaritalSts("");
				}
				if(customer.getCustEmpSts().equals("")){
					customer.setCustEmpSts(null);
				}else if(!valueExistInMaster(customer.getCustEmpSts(),custEmpStsCodeMasterList)){
					masterMissedDetail.setFieldName("CustEmpSts");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustEmpSts()+"' Value Does Not Exist In Master BMTEmpStsCodes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustEmpSts("");
				}
				if(customer.getCustBaseCcy().equals("")){
					customer.setCustBaseCcy(null);
				}else if(!valueExistInMaster(customer.getCustBaseCcy(),currencyCodeMasterList)){
					masterMissedDetail.setFieldName("CustBaseCcy");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustBaseCcy()+"' Value Does Not Exist In Master RMTCurrencies Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustBaseCcy("");
				}
			}
			
			if(cDetails.getAddressList() != null && !cDetails.getAddressList().isEmpty()){
				List<CustomerAddres> saveCustomerAddressList = new ArrayList<CustomerAddres>();
				for (CustomerAddres customerAddres : cDetails.getAddressList()) {
					if(!valueExistInMaster(customerAddres.getCustID(),exisitingCustomerList)){
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("AddressDetails");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("CustID");
						masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" Value Does Not Exist In Customers Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}else if(!valueExistInMaster(customerAddres.getCustAddrType(),addressTypeMasterList)){
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("AddressDetails");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("CustAddrType");
						masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customerAddres.getCustAddrType()+"' Value Does Not Exist In Master BMTAddressTypes Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}else{
						saveCustomerAddressList.add(customerAddres);
					}
				}
				saveCustomerDetail.setAddressList(saveCustomerAddressList);
			}
			
			if(cDetails.getCustomerEMailList() != null && !cDetails.getCustomerEMailList().isEmpty()){
				List<CustomerEMail> saveCustomerEMailList = new ArrayList<CustomerEMail>();
				for (CustomerEMail customerEMail : cDetails.getCustomerEMailList()) {
					if(!valueExistInMaster(customerEMail.getCustID(),exisitingCustomerList)){
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("EmailDetails");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("CustID");
						masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" Value Does Not Exist In Customers Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}else if(!valueExistInMaster(customerEMail.getCustEMailTypeCode(),emailTypeMasterList)){
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("EmailDetails");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("CustEMailTypeCode");
						masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customerEMail.getCustEMailTypeCode()+"' Value Does Not Exist In Master BMTEMailTypes Table ");
						masterValueMissedDetails.add(masterMissedDetail);
					}else{
						saveCustomerEMailList.add(customerEMail);
					}
				}
				saveCustomerDetail.setCustomerEMailList(saveCustomerEMailList);
			}
			saveCustomerDetailsList.add(saveCustomerDetail);
        }
		logger.debug("Leaving");
		return saveCustomerDetailsList;
	}
	
	
	private boolean valueExistInMaster(String field,List<String> list){
		for (String value : list) {
	        if(StringUtils.trimToEmpty(field).equalsIgnoreCase(value.toString())){
	        	return true;
	        }
        }
		return false;
	}
	
	private boolean valueExistInMaster(long field,List<Long> list){
		for (Long value : list) {
	        if(field == value){
	        	return true;
	        }
        }
		return false;
	}
	

	private boolean valueExistInMaster(Customer customer ,List<SubSector> list){
		for (SubSector subSector : list) {
	        if(StringUtils.trimToEmpty(customer.getCustSector()).equalsIgnoreCase(subSector.getSectorCode()) && 
	        		StringUtils.trimToEmpty(customer.getCustSubSector()).equalsIgnoreCase(subSector.getSubSectorCode())){
	        	return true;
	        }
        }
		return false;
	}

	private boolean getBoolean(String string) {
		if (StringUtils.trimToEmpty(string).equalsIgnoreCase("Y") || StringUtils.trimToEmpty(string).equals("1")) {
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
	
	public List<String> fetchBranchCodes() {
    	return  getCoreInterfaceDAO().fetchBranchCodes();
    }
	public List<Long> fetchCustomerGroupCodes() {
    	return  getCoreInterfaceDAO().fetchCustomerGroupCodes();
    }
	public List<String> fetchCountryCodes() {
    	return  getCoreInterfaceDAO().fetchCountryCodes();
    }
	public List<String> fetchSalutationCodes() {
    	return  getCoreInterfaceDAO().fetchSalutationCodes();
    }
	public List<String> fetchRelationshipOfficerCodes() {
    	return  getCoreInterfaceDAO().fetchRelationshipOfficerCodes();
    }
	public List<String> fetchMaritalStatusCodes() {
    	return  getCoreInterfaceDAO().fetchMaritalStatusCodes();
    }
	public List<SubSector> fetchSubSectorCodes() {
    	return  getCoreInterfaceDAO().fetchSubSectorCodes();
    }
	public List<String> fetchEmpStsCodes() {
    	return  getCoreInterfaceDAO().fetchEmpStsCodes();
    }
	public List<String> fetchCurrencyCodes() {
    	return  getCoreInterfaceDAO().fetchCurrencyCodes();
    }
	public List<String> fetchCustTypeCodes() {
    	return  getCoreInterfaceDAO().fetchCustTypeCodes();
    }
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public List<EquationMasterMissedDetail> getMasterMissedDetails(){
		return this.masterValueMissedDetails;
	}
	
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
	    this.customerDetailsService = customerDetailsService;
    }
	public CustomerDetailsService getCustomerDetailsService() {
	    return customerDetailsService;
    }

	public CustomerDataProcess getCustomerDataProcess() {
		return customerDataProcess;
	}
	public void setCustomerDataProcess(CustomerDataProcess customerDataProcess) {
		this.customerDataProcess = customerDataProcess;
	}

	public CoreInterfaceDAO getCoreInterfaceDAO() {
		return coreInterfaceDAO;
	}
	public void setCoreInterfaceDAO(CoreInterfaceDAO coreInterfaceDAO) {
		this.coreInterfaceDAO = coreInterfaceDAO;
	}
	
}
