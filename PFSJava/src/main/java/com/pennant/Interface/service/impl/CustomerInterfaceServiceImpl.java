package com.pennant.Interface.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.impl.NextIdViewSQLServerDaoImpl;
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.customermasters.CoreCustomer;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.reports.AvailAccount;
import com.pennant.backend.model.reports.AvailCollateral;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.backend.model.reports.AvailLimit;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.coreinterface.model.customer.InterfaceCoreCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustEmployeeDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerAddress;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDocument;
import com.pennant.coreinterface.model.customer.InterfaceCustomerEMail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerPhoneNumber;
import com.pennant.coreinterface.model.customer.InterfaceCustomerRating;
import com.pennant.coreinterface.process.CustomerCreationProcess;
import com.pennant.coreinterface.process.CustomerDataProcess;
import com.pennant.equation.dao.CoreInterfaceDAO;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pff.core.TableType;

public class CustomerInterfaceServiceImpl extends NextIdViewSQLServerDaoImpl implements CustomerInterfaceService{
	
	private static Logger logger = Logger.getLogger(CustomerInterfaceServiceImpl.class);
	
	private CustomerDataProcess customerDataProcess;
	private CustomerCreationProcess customerCreationProcess;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private CoreInterfaceDAO coreInterfaceDAO;
	private DesignationDAO designationDAO;

	List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
	
	public CustomerInterfaceServiceImpl(){
		super();
	}
	
	public Customer fetchCustomerDetails(Customer customer) throws InterfaceException {
		logger.debug("Entering");

		CoreBankingCustomer coreCust = new CoreBankingCustomer();
		coreCust.setCustomerMnemonic(customer.getCustCIF());

		try {
			coreCust = getCustomerDataProcess().fetchInformation(coreCust);
			
			//Fill the customer data using Core Customer Banking Object
			customer.setCustCoreBank(coreCust.getCustomerMnemonic());
			
			// TODO This has To be Changed Based on The PCML
		/*	String[] names  = coreCust.getCustomerFullName().split(" ");
			customer.setCustFName(names[0]);
			if(names.length > 2){
				customer.setCustMName(names[1]);
				customer.setCustLName(names[2]);
			}else{
				if(names.length > 1){
					customer.setCustMName("");
					customer.setCustLName(names[1]);
				}else{
					customer.setCustMName("");
					customer.setCustLName("");
				}
			}			
			customer.setCustShrtName(coreCust.getDefaultAccountShortName());
			customer.setCustTypeCode(coreCust.getCustomerType());
			customer.setCustIsBlocked(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsClosed(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsDecease(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsActive(coreCust.getCustomerInactive().equals("N")?true:false);
			//customer.setCustLng(coreCust.getLanguageCode());
			customer.setCustParentCountry(coreCust.getParentCountry());
			customer.setCustCOB(coreCust.getParentCountry());
			customer.setCustRiskCountry(coreCust.getRiskCountry());
			customer.setCustResdCountry(coreCust.getResidentCountry());
			customer.setCustDftBranch(coreCust.getCustomerBranchMnemonic());
			//customer.setCustGroupSts(coreCust.getGroupStatus());
			//customer.setCustGroupID(coreCust.getGroupName());
			//customer.setCustSegment(coreCust.getSegmentIdentifier());
			customer.setCustSalutationCode(coreCust.getSalutation());
			customer.setCustDOB(coreCust.getCustDOB());
			customer.setCustGenderCode(coreCust.getGenderCode());
			customer.setCustPOB(coreCust.getCustPOB());
			customer.setCustPassportNo(coreCust.getCustPassportNum());
			customer.setCustPassportExpiry(coreCust.getCustPassportExpiry());
			customer.setCustIsMinor(coreCust.getMinor().equals("N")?false:true);
			customer.setCustTradeLicenceNum(coreCust.getTradeLicNumber());
			customer.setCustTradeLicenceExpiry(coreCust.getTradeLicExpiry());
			customer.setCustVisaNum(coreCust.getVisaNumber());
			customer.setCustVisaExpiry(coreCust.getVisaExpiry());
			customer.setCustNationality(coreCust.getNationality());*/
			
			customer.setNewRecord(true);
			
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}
		logger.debug("Leaving");
		return customer;
	}
	
	/**
	 * Method for Creating Customer CIF in Core Banking System
	 */
	@Override
	public String generateNewCIF(String operation, Customer customer, String finReference) throws InterfaceException {
		logger.debug("Entering");

		String custCIF = "";
		CoreBankNewCustomer coreCust = new CoreBankNewCustomer();
		coreCust.setOperation(operation);
		coreCust.setCustCtgType(customer.getLovDescCustCtgType());
		coreCust.setFinReference(finReference);
		
		if("A".equals(operation)){
			coreCust.setCustCIF(customer.getCustCIF());
			coreCust.setCustType(customer.getCustTypeCode());
			coreCust.setShortName(customer.getCustShrtName());
			coreCust.setCountry(customer.getCustParentCountry());
			coreCust.setBranch(customer.getCustDftBranch());
			coreCust.setCurrency(customer.getCustBaseCcy());
		}
		
		try {
			custCIF = getCustomerCreationProcess().generateNewCIF(coreCust);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}
		logger.debug("Leaving");
		return custCIF;
	}
	
	/**
	 * Method for Fetch Customer Availment Ticket Details
	 * @throws CustomerNotFoundException 
	 */
	@Override
    public AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws InterfaceException {
		logger.debug("Entering");

		CoreBankAvailCustomer coreCust = new CoreBankAvailCustomer();
		coreCust.setCustMnemonic(detail.getCustCIF());
		coreCust.setOffBSRequired(detail.isOffBSRequired() ? "Y" : "N");
		coreCust.setAcRcvblRequired(detail.isAcRcvblRequired() ? "Y" : "N");
		coreCust.setAcPayblRequired(detail.isAcPayblRequired() ? "Y" : "N");
		coreCust.setAcUnclsRequired(detail.isAcUnclsRequired() ? "Y" : "N");
		coreCust.setCollateralRequired(detail.isCollateralRequired() ? "Y" : "N");
		
		try {
			
			coreCust = getCustomerDataProcess().fetchAvailInformation(coreCust);
			
			String custRspData = coreCust.getCustRspData();
			String limitCcy = "BHD";
			int limitCcyEdit = 3;
			
			if(coreCust.getCustomerLimit() != null){
				limitCcy = coreCust.getCustomerLimit().getLimitCurrency();
				limitCcyEdit = coreCust.getCustomerLimit().getLimitCcyEdit();
			}
			
			//Preparation of OFF-Balance Sheet Account Details
			int startIndex = 0;
			int acLenth = 51;
			if (coreCust.getOffBSCount() > 0) {
				detail.setOffBSAcList(getAccountList(startIndex, coreCust.getOffBSCount(), custRspData, detail, limitCcy, limitCcyEdit));
				startIndex = acLenth * coreCust.getOffBSCount();
			}
			
			//Preparation of Account Receivable Details
			if (coreCust.getAcRcvblCount() > 0) {
				detail.setAcRcvblList(getAccountList(startIndex, coreCust.getAcRcvblCount(), custRspData, detail, limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcRcvblCount());
			}
			
			//Preparation of Account Payable Details
			if(coreCust.getAcPayblCount() > 0){
				detail.setAcPayblList(getAccountList(startIndex, coreCust.getAcPayblCount(), custRspData, detail,limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcPayblCount());
			}

			//Preparation of Account Unclassified Details
			if(coreCust.getAcUnclsCount() > 0){
				detail.setAcUnclsList(getAccountList(startIndex, coreCust.getAcUnclsCount(), custRspData, detail,limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcUnclsCount());
			}

			//Preparation of Collateral Details
			if(coreCust.getCollateralCount() > 0){
				detail.setColList(getCollateralList(startIndex, coreCust.getCollateralCount(), custRspData));
			}
			
			//Finalized Account Balances
			detail.setCustActualBal(coreCust.getCustActualBal());
			detail.setCustBlockedBal(coreCust.getCustBlockedBal());
			detail.setCustDeposit(coreCust.getCustDeposit());
			detail.setCustBlockedDeposit(coreCust.getCustBlockedDeposit());
			detail.setTotalCustBal(coreCust.getTotalCustBal());
			detail.setTotalCustBlockedBal(coreCust.getTotalCustBlockedBal());
			
			//Set Limit Summary Details
			AvailLimit availLimit = null;
			CustomerLimit custLimit = coreCust.getCustomerLimit();
			if(custLimit != null){
				
				BigDecimal curExposure = CalculationUtil.getConvertedAmount(ccy, custLimit.getLimitCurrency(), newExposure);
				
				availLimit = new AvailLimit();
				availLimit.setLimitExpiry(DateUtility.formatToLongDate(custLimit.getLimitExpiry()));
				availLimit.setLimitAmount(PennantApplicationUtil.amountFormate(custLimit.getLimitAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setRiskAmount(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setLimitAvailAmt(PennantApplicationUtil.amountFormate(custLimit.getAvailAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setLimitCcy(custLimit.getLimitCurrency());
				availLimit.setLimitCcyEdit( custLimit.getLimitCcyEdit());
				availLimit.setCurrentExposureLimit(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setNewExposure(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount().add(curExposure), custLimit.getLimitCcyEdit()));
				availLimit.setAvailableLimit(PennantApplicationUtil.amountFormate(custLimit.getLimitAmount().subtract(custLimit.getRiskAmount().add(curExposure)), custLimit.getLimitCcyEdit()));
				availLimit.setLimitRemarks(custLimit.getRemarks().trim());
				detail.setAvailLimit(availLimit);
			}
			
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}
		logger.debug("Leaving");
		return detail;
    }
	
	private List<AvailAccount> getAccountList(int startIndex, int count, String custRspData, AvailCustomerDetail detail, String limitCcy, int limitCcyEdit){
		
		String accDtlData = custRspData.substring(startIndex, startIndex+(51*count)); 
		ArrayList<AvailAccount> list = new ArrayList<AvailAccount>();
		BigDecimal totAcSum = BigDecimal.ZERO;
		int acLenth = 51;
		int accSize = 0;
		
		for (int i = 0; i < count; i++) {
			
			String accData = accDtlData.substring(0, acLenth);
			if(new BigDecimal(accData.substring(20, 35)).compareTo(BigDecimal.ZERO) == 0){
				accDtlData = accDtlData.substring(acLenth);
				continue;
			}
			
			AvailAccount account = new AvailAccount();
			account.setAccountNum(PennantApplicationUtil.formatAccountNumber(accData.substring(0, 13)));
			account.setAcType(accData.substring(13, 15));
			account.setAccountCcy(accData.substring(15, 18));
			account.setConvertCcy(limitCcy);
			
			int ccyFormatter = Integer.parseInt(accData.substring(18, 19));
			if ("-".equals(accData.substring(19, 20))) {
				account.setAcBalance(PennantApplicationUtil.amountFormate(new BigDecimal(accData.substring(20, 35)).negate(),ccyFormatter));
            }else{
            	account.setAcBalance(PennantApplicationUtil.amountFormate(new BigDecimal(accData.substring(20, 35)),ccyFormatter));
            }
			
			BigDecimal actAmount = new BigDecimal(accData.substring(36, 51));
			actAmount = CalculationUtil.getConvertedAmount("BHD", limitCcy, actAmount);
					
			if ("-".equals(accData.substring(35, 36))) {
				account.setAcBalBHD(PennantApplicationUtil.amountFormate(actAmount.negate(),limitCcyEdit));
				totAcSum = totAcSum.subtract(actAmount);
            }else{
            	account.setAcBalBHD(PennantApplicationUtil.amountFormate(actAmount,limitCcyEdit));
            	totAcSum = totAcSum.add(actAmount);
            }
			list.add(account);
			accDtlData = accDtlData.substring(acLenth);
			accSize = accSize + 1;
			
			detail.getAccTypeList().add(account.getAcType());
        }
		
		//Add Total Summation 
		if(accSize > 0){
			
			AvailAccount account = new AvailAccount();
			account.setAccountNum("Total");
			account.setAcBalBHD(PennantApplicationUtil.amountFormate(totAcSum,limitCcyEdit));
			list.add(account);
		}
		return list;
	}
	
	private List<AvailCollateral> getCollateralList(int startIndex, int count, String custRspData){
		
		custRspData = StringUtils.rightPad(custRspData,  startIndex+(166*count), " ");
		String colDtlData = custRspData.substring(startIndex, startIndex+(166*count)); 
		ArrayList<AvailCollateral> list = new ArrayList<AvailCollateral>();
		for (int i = 0; i < count; i++) {
			
			String colData = colDtlData.substring(0, 166);
			AvailCollateral collateral = new AvailCollateral();
			collateral.setCollateralReference(colData.substring(0, 35));
			collateral.setCollateralType(colData.substring(35, 38)+"-"+colData.substring(38, 73));
			collateral.setCollateralComplete(colData.substring(73, 74));
			collateral.setCollateralCcy(colData.substring(74, 77));
			int ccyFormatter = Integer.parseInt(colData.substring(77, 78));
			if (!("0".equals(colData.substring(78, 85)) || ("").equals(colData.substring(78, 85)))) {
				if("9999999".equals(colData.substring(78, 85))){
					collateral.setCollateralExpiry("Open");
				}else{
					collateral.setCollateralExpiry(DateUtility.formatToLongDate(DateUtility.convertDateFromAS400(
							new BigDecimal(colData.substring(78, 85)))));
				}
			} 
			if (!("0".equals(colData.substring(85, 92))) || ("").equals(colData.substring(85, 92))) {
				if("9999999".equals(colData.substring(85, 92))){
					collateral.setCollateralExpiry("Open");
				}else{
					collateral.setLastReview(DateUtility.formatToLongDate(DateUtility.convertDateFromAS400(
							new BigDecimal(colData.substring(85, 92)))));
				}
			} 
			collateral.setCollateralValue(PennantApplicationUtil.amountFormate(new BigDecimal(colData.substring(92,107)), ccyFormatter));
			collateral.setBankValuation(PennantApplicationUtil.amountFormate(new BigDecimal(colData.substring(107,122)), ccyFormatter));
			collateral.setMargin(PennantApplicationUtil.formatRate(PennantApplicationUtil.formateAmount(new BigDecimal(colData.substring(122, 127)), 3).doubleValue(),2)+"%");
			collateral.setCollateralLoc(colData.substring(127,131));
			collateral.setCollateralDesc(colData.substring(131,166));
			
			list.add(collateral);
			colDtlData = colDtlData.substring(166);
        }
		return list;
	}

	/**
	 * Method for Fetching Customer Details information
	 */
	@Override
	public CustomerDetails getCustomerInfoByInterface(String custCIF, String custLoc) throws InterfaceException {
		logger.debug("Entering");
		CustomerDetails customerDetails = null;
		try {
			logger.debug("Before Customer Data Process Call ");
			InterfaceCustomerDetail interfaceCustomerDetail = getCustomerDataProcess().getCustomerFullDetails(custCIF, custLoc);
			logger.debug("After Customer Data Process Call ");
			if (interfaceCustomerDetail != null) {
				customerDetails = processCustInformation(interfaceCustomerDetail);
				if(customerDetails != null){
					setCustomerStatus(customerDetails);
				}
			}

		} catch(InterfaceException pfe) {  
			throw pfe;
		} catch (Exception e) {
			logger.debug(e);
			throw new InterfaceException("PTI2001", e.getMessage());
		}
		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * Method for Setting customer data
	 * @param interfaceCustomerDetail
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public CustomerDetails processCustInformation(InterfaceCustomerDetail interfaceCustomerDetail) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		CustomerDetails custDetails = null;
		if(interfaceCustomerDetail !=null) {
			custDetails = new CustomerDetails();
			custDetails.setNewRecord(true);

			//Process Customer data
			if(interfaceCustomerDetail.getCustomer() !=null) {

				Customer customer = new Customer();
				BeanUtils.copyProperties(interfaceCustomerDetail.getCustomer(), customer);
				
				//setting descriptions
				customer.setLovDescCustLngName(getCodeDescription("Language",customer.getCustLng()));
				customer.setLovDescCustSectorName(getCodeDescription("Sector",customer.getCustSector()));
				customer.setLovDescCustSegmentName(getCodeDescription("Segment",customer.getCustSegment()));
				customer.setLovDescCustStsName(getCodeDescription("CustomerStatusCode",customer.getCustSts()));
				customer.setLovDescCustIndustryName(getCodeDescription("Industry",customer.getCustIndustry()));
				customer.setLovDescCustNationalityName(getCodeDescription("NationalityCode",customer.getCustNationality()));
				customer.setLovDescCustCOBName(getCodeDescription("Country",customer.getCustCOB()));
				customer.setLovDescCustRO1Name(getCodeDescription("PRelationCode",customer.getCustRO1()));
				if(StringUtils.isBlank(customer.getCustBaseCcy())) {
					String defaultCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
					customer.setCustBaseCcy(defaultCcy);
				}
				customer.setNewRecord(true);
				
				customer.setCustID(Long.parseLong(customer.getCustCIF()));
				custDetails.setCustomer(customer);
			}

			//process CustomerRating Details
			if(interfaceCustomerDetail.getRatingsList() != null) {

				List<CustomerRating> ratingsList = new ArrayList<CustomerRating>();

				for(InterfaceCustomerRating interfaceRating: interfaceCustomerDetail.getRatingsList()) {

					CustomerRating custRating = new CustomerRating();
					BeanUtils.copyProperties(interfaceRating, custRating);
					custRating.setNewRecord(true);
					custRating.setRecordType(PennantConstants.RCD_ADD);
					ratingsList.add(custRating);
				}
				custDetails.setRatingsList(ratingsList);
			}

			//process CustomerEmploymentDetail
			if (interfaceCustomerDetail.getCustEmployeeDetail() != null) {
				
				CustEmployeeDetail custEmployeeDetail = new CustEmployeeDetail();
				BeanUtils.copyProperties(interfaceCustomerDetail.getCustEmployeeDetail(), custEmployeeDetail);
				setCustomerEmployeeDetail(custEmployeeDetail);
				custEmployeeDetail.setNewRecord(true);
				custEmployeeDetail.setRecordType(PennantConstants.RCD_ADD);
				
				//setting descriptions
				custEmployeeDetail.setLovDescEmpStatus(getCodeDescription("CustEmployeeDetail", custEmployeeDetail.getEmpStatus()));
				custEmployeeDetail.setLovDescEmpDesg(getCodeDescription("Designation", custEmployeeDetail.getEmpDesg()));
				custEmployeeDetail.setLovDescEmpDept(getCodeDescription("Department", custEmployeeDetail.getEmpDept()));
				
				custDetails.setCustEmployeeDetail(custEmployeeDetail);
			}

			//process CustomerDocument
			if(interfaceCustomerDetail.getCustomerDocumentsList() != null) {
				List<CustomerDocument> custDocumentList = new ArrayList<CustomerDocument>();

				for(InterfaceCustomerDocument custDocDetails: interfaceCustomerDetail.getCustomerDocumentsList()) {

					CustomerDocument custDocs = new CustomerDocument();
					BeanUtils.copyProperties(custDocDetails, custDocs);
					custDocs.setNewRecord(true);
					custDocs.setRecordType(PennantConstants.RCD_ADD);
					custDocumentList.add(custDocs);
				}
				custDetails.setCustomerDocumentsList(custDocumentList);
			}
			//process CustomerAddres
			if(interfaceCustomerDetail.getAddressList() != null) {
				List<CustomerAddres> custAddresList = new ArrayList<CustomerAddres>();

				for(InterfaceCustomerAddress interfaceCustAddr: interfaceCustomerDetail.getAddressList()) {
					CustomerAddres custAddr = new CustomerAddres();
					BeanUtils.copyProperties(interfaceCustAddr, custAddr);
					custAddr.setNewRecord(true);
					custAddr.setRecordType(PennantConstants.RCD_ADD);
					custAddresList.add(custAddr);
				}
				custDetails.setAddressList(custAddresList);
			}
			//process CustomerPhonenumbers
			if(interfaceCustomerDetail.getCustomerPhoneNumList() != null) {
				List<CustomerPhoneNumber> custPhoneList = new ArrayList<CustomerPhoneNumber>();

				for(InterfaceCustomerPhoneNumber interfacePhoneNum: interfaceCustomerDetail.getCustomerPhoneNumList()) {
					if(!StringUtils.isBlank(interfacePhoneNum.getPhoneNumber())) {
						CustomerPhoneNumber custPhoneNum = new CustomerPhoneNumber();
						BeanUtils.copyProperties(interfacePhoneNum, custPhoneNum);
						custPhoneNum.setNewRecord(true);
						custPhoneNum.setRecordType(PennantConstants.RCD_ADD);
						custPhoneList.add(custPhoneNum);
					}
				}
				custDetails.setCustomerPhoneNumList(custPhoneList);
			}	

			//process CustomerEmail
			if(interfaceCustomerDetail.getCustomerEMailList() != null) {
				List<CustomerEMail> custEmailList = new ArrayList<CustomerEMail>();
				for(InterfaceCustomerEMail interfaceEmail: interfaceCustomerDetail.getCustomerEMailList()) {
					if(!StringUtils.isBlank(interfaceEmail.getCustEMail())) {
						CustomerEMail custEmail = new CustomerEMail();
						BeanUtils.copyProperties(interfaceEmail, custEmail);
						custEmail.setNewRecord(true);
						custEmail.setRecordType(PennantConstants.RCD_ADD);
						custEmailList.add(custEmail);
					}
				}
				custDetails.setCustomerEMailList(custEmailList);
			}	
			
			//process NotAvailable fields
			if(interfaceCustomerDetail.getInterfaceCoreCustomer() != null) {
				CoreCustomer coreCustomer = new CoreCustomer();
				BeanUtils.copyProperties(interfaceCustomerDetail.getInterfaceCoreCustomer(), coreCustomer);
				custDetails.setCoreCustomer(coreCustomer);
			}
		}

		logger.debug("Leaving");

		return custDetails;
	}

	
	private String getCodeDescription(String moduleName, Object val) {
		logger.debug("Entering");

		String code = null;
		String desc = null;
		String tableName = null;

		switch (moduleName) {
		case "Language":
			code = "LngCode";
			desc = "LngDesc";
			break;
		case "Sector":
			code = "SectorCode";
			desc = "SectorDesc";
			break;
		case "Segment":
			code = "SegmentCode";
			desc = "SegmentDesc";
			break;
		case "CustomerStatusCode":
			code = "CustStsCode";
			desc = "CustStsDescription";
			break;
		case "Industry":
			code = "IndustryCode";
			desc = "IndustryDesc";
			break;
		case "NationalityCode":
			code = "NationalityCode";
			desc = "NationalityDesc";
			break;
		case "Country":
			code = "CountryCode";
			desc = "CountryDesc";
			break;
		case "PRelationCode":
			code = "PRelationCode";
			desc = "PRelationDesc";
			break;
		case "CustEmployeeDetail":
			code = "EmpStatus";
			desc = "LovDescEmpStatus";
			tableName = "CustEmployeeDetail_AView";
			break;
		case "Designation":
			code = "DesgCode";
			desc = "DesgDesc";
			break;
		case "Department":
			code = "DeptCode";
			desc = "DeptDesc";
			break;
		default:
			code = "";
			desc = "";
			break;
		}
		logger.debug("Leaving");
		
		return PennantApplicationUtil.getDBDescription(moduleName, tableName, desc, getDescription(code, val));
	}

	public Filter[] getDescription(String filed, Object value) {
		Filter[] masterCodeFiler = new Filter[1];
		masterCodeFiler[0] = new Filter(filed, value, Filter.OP_EQUAL);
		return masterCodeFiler;
	}
	
	

	/**This method will validate whether the customer fields exists in their respective 
	 * master tables or not
	 * @param customerDetails
	 * @param dateValueDate
	 * @return
	 */
	@Override
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
				if(("").equals(customer.getCustDftBranch())){
					customer.setCustDftBranch(null);
				}else if(!valueExistInMaster(customer.getCustDftBranch(),branchCodeMasterList)){
					masterMissedDetail.setFieldName("CustDftBranch");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustDftBranch()+"' Value Does Not Exist In Master RMTBranches Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustDftBranch(""); //Making it empty to ignore the empty field updates in query while updating the record 
				}
				if(("").equals(customer.getCustTypeCode())){
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
				if(("").equals(customer.getCustCOB())){
					customer.setCustCOB(null); 
				}else if(!valueExistInMaster(customer.getCustCOB(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustCOB");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustCOB()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustCOB("");
				}
				if(("").equals(customer.getCustParentCountry())){
					customer.setCustParentCountry(null);
				}else if(!valueExistInMaster(customer.getCustParentCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustParentCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustParentCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustParentCountry("");
				}
				if(("").equals(customer.getCustRiskCountry())){
					customer.setCustRiskCountry(null);
				}else if(!valueExistInMaster(customer.getCustRiskCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustRiskCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRiskCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustRiskCountry("");
				}
				if(("").equals(customer.getCustResdCountry())){
					customer.setCustResdCountry(null);
				}else if(!valueExistInMaster(customer.getCustResdCountry(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustResdCountry");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustResdCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustResdCountry("");
				}
				if(("").equals(customer.getCustNationality())){
					customer.setCustNationality(null);
				}else if(!valueExistInMaster(customer.getCustNationality(),countryCodeMasterList)){
					masterMissedDetail.setFieldName("CustNationality");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustNationality()+"' Value Does Not Exist In Master BMTCountries Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustNationality("");
				}
				if(("").equals(customer.getCustSalutationCode())){
					customer.setCustSalutationCode(null);
				}else if(!valueExistInMaster(customer.getCustSalutationCode(),salutationCodeMasterList)){
					masterMissedDetail.setFieldName("CustSalutationCode");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustSalutationCode()+"' Value Does Not Exist In Master BMTSalutations Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustSalutationCode("");
				}
				if(("").equals(customer.getCustRO1())){
					customer.setCustRO1(0);
				}else if(!valueExistInMaster(String.valueOf(customer.getCustRO1()),rShipOfficerCodeMasterList)){
					masterMissedDetail.setFieldName("CustRO1");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRO1()+"' Value Does Not Exist In Master RelationshipOfficers Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustRO1(0);
				}
				if(("").equals(customer.getCustSector())  ||
						("").equals(customer.getCustSubSector())){
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
				if(("").equals(customer.getCustMaritalSts())){
					customer.setCustMaritalSts(null);
				}else if(!valueExistInMaster(customer.getCustMaritalSts(),maritalStatusCodeMasterList)){
					masterMissedDetail.setFieldName("CustMaritalSts");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustMaritalSts()+"' Value Does Not Exist In Master BMTMaritalStatusCodes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustMaritalSts("");
				}
				if(("").equals(customer.getCustEmpSts())){
					customer.setCustEmpSts(null);
				}else if(!valueExistInMaster(customer.getCustEmpSts(),custEmpStsCodeMasterList)){
					masterMissedDetail.setFieldName("CustEmpSts");
					masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustEmpSts()+"' Value Does Not Exist In Master BMTEmpStsCodes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
					customer.setCustEmpSts("");
				}
				if(("").equals(customer.getCustBaseCcy())){
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
			if(StringUtils.trimToEmpty(field).equalsIgnoreCase(value)){
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


	private void setCustomerStatus(CustomerDetails customerDetails) {
		try {
			if (StringUtils.isBlank(customerDetails.getCustomer().getCustSts())) {
				CustomerStatusCode customerStatusCode = getCustomerStatusCodeDAO().getCustStatusByMinDueDays("");
				if (customerStatusCode != null) {
					customerDetails.getCustomer().setCustSts(customerStatusCode.getCustStsCode());
					customerDetails.getCustomer().setLovDescCustStsName(customerStatusCode.getCustStsDescription());
				}
			}
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
	}
	
	/**
	 * Saving designation if not exist in db  
	 * @param custEmployeeDetail
	 */
	private void setCustomerEmployeeDetail(CustEmployeeDetail custEmployeeDetail){
		if (custEmployeeDetail.getEmpDesg() != null) {
			Designation designation = getDesignationDAO().getDesignationById(custEmployeeDetail.getEmpDesg(), "");
			if (designation == null) {
				Designation tempDesignation = new Designation();
				tempDesignation.setDesgCode(custEmployeeDetail.getEmpDesg());
				tempDesignation.setDesgDesc(custEmployeeDetail.getEmpDesg());
				tempDesignation.setDesgIsActive(true);
		        getDesignationDAO().save(tempDesignation, TableType.MAIN_TAB);
	        }
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
	
	/**
	 * get the duplicate customers from Interface DB based on the rule executed
	 * @throws InterfaceException 
	 */
	@Override
    public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup customerDedup) throws InterfaceException {
		
		CoreCustomerDedup coreCustomerDedup = new CoreCustomerDedup();
		BeanUtils.copyProperties(customerDedup, coreCustomerDedup);
		List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();
		List<CoreCustomerDedup> custDedupList = getCustomerCreationProcess().fetchCustomerDedupDetails(coreCustomerDedup);

		if(custDedupList != null){
			for(CoreCustomerDedup coreCustDedup : custDedupList) {
				CustomerDedup custDedup = new CustomerDedup();
				BeanUtils.copyProperties(coreCustDedup, custDedup);
				custDedup.setFinReference(customerDedup.getFinReference());
				custDedup.setQueryField(InterfaceConstants.DEDUP_CORE);
				custDedup.setDedupRule(custDedup.getQueryField());
				custDedup.setOverride(true);
				customerDedupList.add(custDedup);
			}
		}
	    return customerDedupList;
    }

	/**
	 * Method for Miss Matched Data capturing while on Daily Download Process.
	 * */
	@Override
	public List<EquationMasterMissedDetail> getMasterMissedDetails(){
		return this.masterValueMissedDetails;
	}

	/**
	 * Method for create new Customer by sending request through MQ
	 * */
	@Override
	public String createNewCustomer(CustomerDetails customerDetail) throws InterfaceException {
		logger.debug("Entering");

		InterfaceCustomerDetail interfaceCustomerDetail = processCustInterfaceData(customerDetail);

		//set ReleaseCIF reference number to coreReferenceNum variable
		customerDetail.setCoreReferenceNum(getCustomerCreationProcess().createNewCustomer(interfaceCustomerDetail));

		logger.debug("Leaving");
		return customerDetail.getCoreReferenceNum();
	}
	
	/**
	 * Method for update core customer details
	 * @throws InterfaceException 
	 * 
	 */
	@Override
    public void updateCoreCustomer(CustomerDetails customerDetails) throws InterfaceException {
		logger.debug("Entering");
		
		InterfaceCustomerDetail interfaceCustomerDetail = processCustInterfaceData(customerDetails);
		//set ReserveCIF reference number to coreReferenceNum variable
		getCustomerCreationProcess().updateCoreCustomer(interfaceCustomerDetail);

		logger.debug("Leaving");
    }
	
	private InterfaceCustomerDetail processCustInterfaceData(CustomerDetails customerDetail) {
		logger.debug("Entering");
		
		InterfaceCustomerDetail interfaceCustomerDetail = new InterfaceCustomerDetail();
	    
		//Process Customer data
		if(customerDetail.getCustomer() !=null) {
			InterfaceCustomer interfaceCustomer = new InterfaceCustomer();
			BeanUtils.copyProperties(customerDetail.getCustomer(), interfaceCustomer);
			interfaceCustomerDetail.setCustomer(interfaceCustomer);
		}

		//process CustomerRating Details
		if(customerDetail.getRatingsList() != null) {
			List<InterfaceCustomerRating> interfaceRatingList = new ArrayList<InterfaceCustomerRating>();
			for(CustomerRating customerRating : customerDetail.getRatingsList()) {
				InterfaceCustomerRating interfaceRating = new InterfaceCustomerRating();
				BeanUtils.copyProperties(customerRating, interfaceRating);
				interfaceRatingList.add(interfaceRating);
			}
			interfaceCustomerDetail.setRatingsList(interfaceRatingList);
		}

		//process CustomerEmploymentDetail
		if (customerDetail.getCustEmployeeDetail() != null) {
			InterfaceCustEmployeeDetail custEmployeeDetail = new InterfaceCustEmployeeDetail();
			BeanUtils.copyProperties(customerDetail.getCustEmployeeDetail(), custEmployeeDetail);
			interfaceCustomerDetail.setCustEmployeeDetail(custEmployeeDetail);
		}

		//process CustomerDocument
		if(customerDetail.getCustomerDocumentsList() != null) {
			List<InterfaceCustomerDocument> interfaceCustDocumentList = new ArrayList<InterfaceCustomerDocument>();
			for(CustomerDocument customerDocument : customerDetail.getCustomerDocumentsList()) {
				InterfaceCustomerDocument interfaceCustDocument = new InterfaceCustomerDocument();
				BeanUtils.copyProperties(customerDocument, interfaceCustDocument);
				interfaceCustDocumentList.add(interfaceCustDocument);
			}
			interfaceCustomerDetail.setCustomerDocumentsList(interfaceCustDocumentList);
		}
		//process CustomerAddres
		if(customerDetail.getAddressList() != null) {
			List<InterfaceCustomerAddress> custAddresList = new ArrayList<InterfaceCustomerAddress>();
			for(CustomerAddres addres : customerDetail.getAddressList()) {
				InterfaceCustomerAddress customerAddress = new InterfaceCustomerAddress();
				BeanUtils.copyProperties(addres, customerAddress);
				custAddresList.add(customerAddress);
			}
			interfaceCustomerDetail.setAddressList(custAddresList);
		}
		//process CustomerPhonenumbers
		if(customerDetail.getCustomerPhoneNumList() != null) {
			List<InterfaceCustomerPhoneNumber> custPhoneList = new ArrayList<InterfaceCustomerPhoneNumber>();
			for(CustomerPhoneNumber customerPhoneNumber : customerDetail.getCustomerPhoneNumList()) {
				InterfaceCustomerPhoneNumber interfaceCustPhoneNum = new InterfaceCustomerPhoneNumber();
				BeanUtils.copyProperties(customerPhoneNumber, interfaceCustPhoneNum);
				custPhoneList.add(interfaceCustPhoneNum);
			}
			interfaceCustomerDetail.setCustomerPhoneNumList(custPhoneList);
		}	

		//process CustomerEmail
		if(customerDetail.getCustomerEMailList() != null) {
			List<InterfaceCustomerEMail> custEmailList = new ArrayList<InterfaceCustomerEMail>();
			for(CustomerEMail customerEMail : customerDetail.getCustomerEMailList()) {
				InterfaceCustomerEMail interfaceCustomerEMail = new InterfaceCustomerEMail();
				BeanUtils.copyProperties(customerEMail, interfaceCustomerEMail);
				custEmailList.add(interfaceCustomerEMail);
			}
			interfaceCustomerDetail.setCustomerEMailList(custEmailList);
		}
		
		// process CoreCustomer object
		if(customerDetail.getCoreCustomer() != null) {
			InterfaceCoreCustomer interfaceCoreCustomer = new InterfaceCoreCustomer();
			BeanUtils.copyProperties(customerDetail.getCoreCustomer(), interfaceCoreCustomer);
			interfaceCustomerDetail.setInterfaceCoreCustomer(interfaceCoreCustomer);
		} else {
			interfaceCustomerDetail.setInterfaceCoreCustomer(new InterfaceCoreCustomer());
		}
	
		logger.debug("Leaving");
		
		return interfaceCustomerDetail;
    }
	
	/**
	 * Method for send ReserveCIF request to MDM Interface
	 * 
	 * @param customer
	 * @throws InterfaceException
	 */
	@Override
    public String reserveCIF(Customer customer) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Entering");
		
		InterfaceCustomer coreCustomer = new InterfaceCustomer();
		BeanUtils.copyProperties(customer, coreCustomer);
		return getCustomerCreationProcess().reserveCIF(coreCustomer);
    }
	
	/**
	 * Method for send ReleaseCIF request to MDM Interface
	 * 
	 * @param customer
	 * @param reserveRefNum
	 * @throws InterfaceException
	 */
	@Override
    public String releaseCIF(Customer customer, String reserveRefNum) throws InterfaceException {
		logger.debug("Entering");
	
		InterfaceCustomer coreCustomer = new InterfaceCustomer();
		BeanUtils.copyProperties(customer, coreCustomer);

		logger.debug("Entering");
		return getCustomerCreationProcess().releaseCIF(coreCustomer, reserveRefNum);
    }
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public CoreInterfaceDAO getCoreInterfaceDAO() {
		return coreInterfaceDAO;
	}
	public void setCoreInterfaceDAO(CoreInterfaceDAO coreInterfaceDAO) {
		this.coreInterfaceDAO = coreInterfaceDAO;
	}

	public void setCustomerDataProcess(CustomerDataProcess customerDataProcess) {
	    this.customerDataProcess = customerDataProcess;
    }
	public CustomerDataProcess getCustomerDataProcess() {
	    return customerDataProcess;
    }

	public DesignationDAO getDesignationDAO() {
		return designationDAO;
	}
	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}

	public CustomerCreationProcess getCustomerCreationProcess() {
	    return customerCreationProcess;
    }
	public void setCustomerCreationProcess(CustomerCreationProcess customerCreationProcess) {
	    this.customerCreationProcess = customerCreationProcess;
    }

}
