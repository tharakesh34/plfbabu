package com.pennant.equation.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerIdentity;
import com.pennant.coreinterface.model.customer.InterfaceCustomerRating;
import com.pennant.coreinterface.model.customer.InterfaceShareHolder;
import com.pennant.coreinterface.process.CustomerDataProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerDataProcessImpl extends GenericProcess implements CustomerDataProcess {

	private static Logger logger = Logger.getLogger(CustomerDataProcessImpl.class);
	private HostConnection hostConnection;

	public CustomerDataProcessImpl() {
		super();
	}
	
	@Override
	public InterfaceCustomerDetail getCustomerFullDetails(String custCIF,String custLoc) throws InterfaceException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFNC";

		try {

			as400 = this.hostConnection.getConnection();

			// create Document
			pcmlDoc = new ProgramCallDocument(as400, pcml);

			// Set Error code to Empty
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000");
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			// Set Request Data
			pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", custCIF);
			pcmlDoc.setValue(pcml + ".@REQDTA.CustLoc", custLoc);

			// Call To interface
			this.hostConnection.callAPI(pcmlDoc, pcml);

			// if No Error Read The Response Data
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD"))) {
				InterfaceCustomerDetail customerInterfaceData = new InterfaceCustomerDetail();

				InterfaceCustomer customer = new InterfaceCustomer();

				customer.setCustCIF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCpnc").toString());
				customer.setCustFName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCun").toString());
				customer.setCustTypeCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCtp").toString());
				customer.setCustIsClosed(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCuc").toString()));
				customer.setCustIsActive(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCuZ").toString()));
				customer.setCustDftBranch((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspBrnm").toString());
				customer.setCustGroupID(getLong(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspGrp").toString()));
				customer.setCustParentCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCnap").toString());
				customer.setCustRiskCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCnar").toString());
				customer.setCustDOB(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspDob").toString()));
				customer.setCustSalutationCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspSalu").toString());
				customer.setCustGenderCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspGend").toString());
				if ("M".equals(StringUtils.trimToEmpty(customer.getCustGenderCode()))) {
					customer.setCustCtgCode(GENDER_MALE);
				}else if ("F".equals(StringUtils.trimToEmpty(customer.getCustGenderCode()))) {
					customer.setCustCtgCode(GENDER_FEMALE);
				}else {
					customer.setCustCtgCode(GENDER_OTHER);
				}

				customer.setCustPOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPob").toString());
				customer.setCustPassportNo((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPPN").toString());
				customer.setCustPassportExpiry(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPPE").toString()));
				customer.setCustIsMinor(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspMinor").toString()));
				customer.setCustTradeLicenceNum(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTln").toString());
				customer.setCustTradeLicenceExpiry(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTle").toString()));
				customer.setCustVisaNum(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspVisaN").toString());
				customer.setCustVisaExpiry(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspVisaE").toString()));
				customer.setCustCoreBank((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCId").toString());
				customer.setCustCtgCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCod").toString());

				if ("I".equals(StringUtils.trimToEmpty(customer.getCustCtgCode()))) {
					customer.setCustCtgCode("RETAIL");
				}else if ("C".equals(StringUtils.trimToEmpty(customer.getCustCtgCode()))) {
					customer.setCustCtgCode("CORP");
				}else if ("B".equals(StringUtils.trimToEmpty(customer.getCustCtgCode()))) {
					customer.setCustCtgCode("BANK");
				}

				customer.setCustShrtName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCShn").toString());
				customer.setCustFNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspLFNam").toString());
				customer.setCustShrtNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspLSNam").toString());
				customer.setCustCOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCOB").toString());
				customer.setCustRO1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspACO").toString());
				customer.setCustIsBlocked(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCUB").toString()));
				customer.setCustIsDecease(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCUD").toString()));
				customer.setCustIsTradeFinCust(getBoolean(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspYTRI").toString()));
				customer.setCustSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCA2").toString());
				customer.setCustSubSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspSAC").toString());
				customer.setCustProfession((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspProf").toString());
				customer.setCustTotalIncome(getAmount(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTInc").toString()));
				customer.setCustMaritalSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspMSta").toString());
				customer.setCustEmpSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspESta").toString());
				customer.setCustBaseCcy(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCcy").toString());
				customer.setCustResdCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNat").toString());
				customer.setCustClosedOn(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspDCC").toString()));
				customer.setCustStmtFrq((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCFRQ").toString());
				customer.setCustStmtLastDate(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPSTM").toString()));
				customer.setCustStmtNextDate(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNSTM").toString()));
				customer.setCustFirstBusinessDate(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCOD").toString()));
				customer.setCustRelation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspRltn").toString());

				customerInterfaceData.setCustomer(customer);

				//Customer Rating Details
				int dsRspCount=0;
				try {
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatCnt").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<InterfaceCustomerRating> list=new ArrayList<InterfaceCustomerRating>(); 
				int[] indices = new int[1]; // Indices for access array value
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++) {
					InterfaceCustomerRating customerRating=	new InterfaceCustomerRating();
					customerRating.setCustRatingType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustRatingType",indices));
					customerRating.setCustLongRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustLongRate",indices));
					customerRating.setCustShortRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustShortRate",indices));
					customerRating.setRecordType("ADD");
					list.add(customerRating);
				}
				customerInterfaceData.setRatingsList(list);

				//Customer Identity	
				int custIdCount=0;
				try {
					custIdCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIDCount").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<InterfaceCustomerIdentity> idlist=new ArrayList<InterfaceCustomerIdentity>(); 
				int[] indices1 = new int[1]; // Indices for access array value
				for (indices1[0] = 0; indices1[0] < custIdCount; indices1[0]++) {
					InterfaceCustomerIdentity customerIdentity=	new InterfaceCustomerIdentity();
					customerIdentity.setIdType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdType",indices1));
					customerIdentity.setIdRef((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdNum",indices1));
					customerIdentity.setIdIssueCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdCna",indices1));
					customerIdentity.setIdIssuedOn(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdIssuDt",indices1).toString()));
					customerIdentity.setIdExpiresOn(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdExpDt",indices1).toString()));
					customerIdentity.setRecordType("ADD");
					idlist.add(customerIdentity);
				}
				customerInterfaceData.setCustomerIdentityList(idlist);

				//Customer Share holder Details

				int custShareHolderCount = 0;
				try{
					custShareHolderCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.ShareHolderIDCount").toString());
				}catch(Exception e){
					logger.debug("Exception: ", e);
				}
				List<InterfaceShareHolder> shareHolderList = new ArrayList<InterfaceShareHolder>();
				int[] shareholderCount = new int[1]; // Indices for access array value
				for (shareholderCount[0] = 0; shareholderCount[0] < custShareHolderCount; shareholderCount[0]++) {
					InterfaceShareHolder shareHolder= new InterfaceShareHolder();
					shareHolder.setShareHolderIDType(pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderIDType",shareholderCount).toString());
					shareHolder.setShareHolderIDRef((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderIDRef",shareholderCount));
					shareHolder.setShareHolderPerc(pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderPerc",shareholderCount).toString());
					shareHolder.setShareHolderRole((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderRole",shareholderCount));
					shareHolder.setShareHolderName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderName",shareholderCount));
					shareHolder.setShareHolderNation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderNation",shareholderCount));
					shareHolder.setShareHolderRisk((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderRisk",shareholderCount));
					shareHolder.setShareHolderDOB(formatCYMDDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderDOB",shareholderCount).toString()));
					shareHolder.setRecordType("ADD");
					shareHolderList.add(shareHolder);
				}
				customerInterfaceData.setShareHolderList(shareHolderList);

				return customerInterfaceData;
			}	

		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {
			this.hostConnection.closeConnection(as400);
		}
		return null;

	}


	@Override
	public List<CustomerCollateral> getCustomerCollateral(String custCIF) throws InterfaceException {

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCOLL";
		List<CustomerCollateral> list=null;

		try {
			as400 = this.hostConnection.getConnection();
			// create Document
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			// Set Error code to Empty
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000");
			pcmlDoc.setValue(pcml + ".@ERPRM", "");
			// Set Request Data
			pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", custCIF);
			// Call To interface
			this.hostConnection.callAPI(pcmlDoc, pcml);
			// if No Error Read The Response Data
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD"))) {
				int dsRspCount=0;
				try {
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspCNT").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				list=new ArrayList<CustomerCollateral>(dsRspCount); 
				int[] indices = new int[1]; // Indices for access array value
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++) {
					CustomerCollateral customerCollateral = new CustomerCollateral();
					customerCollateral.setCollReference((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCLR",indices));
					customerCollateral.setCollType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCLP",indices));
					customerCollateral.setCollTypeDesc((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCPD",indices));
					customerCollateral.setCollComplete((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCCM",indices));
					customerCollateral.setCollCcy((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCCY",indices));
					customerCollateral.setCollExpDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCXD",indices));
					customerCollateral.setColllastRvwDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspLRD",indices));
					customerCollateral.setCollValue(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCLV",indices));
					customerCollateral.setCollBankVal(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspBKV",indices));
					customerCollateral.setCollBankValMar(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspBVM",indices));
					customerCollateral.setColllocation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCLO",indices));
					customerCollateral.setColllocationDesc((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DSRspDetails.dsRspCLS",indices));
					list.add(customerCollateral);
				}

				return list;
			}	

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {
			this.hostConnection.closeConnection(as400);
		}
		return null;
	}

	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTPFF14R.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
	@Override
	public CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws InterfaceException{	
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFAI";		

		try{	

			as400 = this.hostConnection.getConnection();

			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			pcmlDoc.setValue(pcml + ".@REQDTA.CustMnemonic", coreCust.getCustMnemonic()); 				//Customer Number
			pcmlDoc.setValue(pcml + ".@REQDTA.OffBSRequired", coreCust.getOffBSRequired()); 			//Off Balance Sheet Required
			pcmlDoc.setValue(pcml + ".@REQDTA.AcRcvblRequired", coreCust.getAcRcvblRequired()); 		//Account Receivables Required
			pcmlDoc.setValue(pcml + ".@REQDTA.AcPayblRequired", coreCust.getAcPayblRequired()); 		//Account Payables Required
			pcmlDoc.setValue(pcml + ".@REQDTA.AcUnclsRequired", coreCust.getAcUnclsRequired()); 		//Accounts Un Classified Required
			pcmlDoc.setValue(pcml + ".@REQDTA.CollateralRequired", coreCust.getCollateralRequired()); 	//Collaterals Required

			logger.debug(" Before PCML Call");
			this.hostConnection.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	

				coreCust.setOffBSCount(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.OffBSCount").toString())); 		//Off Balance Sheet Count
				coreCust.setAcRcvblCount(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.AcRcvblCount").toString())); 	//Account Receivables Count
				coreCust.setAcPayblCount(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.AcPayblCount").toString())); 	//Accounts payable Count
				coreCust.setAcUnclsCount(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.AcUnclsCount").toString())); 	//Accounts Unclassified Count
				coreCust.setCollateralCount(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CollateralCount").toString())); //Collateral Count

				BigDecimal amount = BigDecimal.ZERO;
				//Out Standing balance details
				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABalSign").toString())) {
					coreCust.setCustActualBal(amount.negate()); 
				} else {
					coreCust.setCustActualBal(amount);
				}

				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABlkBal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABlkSign").toString())) {
					coreCust.setCustBlockedBal(amount.negate()); 
				} else {
					coreCust.setCustBlockedBal(amount);
				}

				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkSign").toString())) {
					coreCust.setCustDeposit(amount.negate()); 
				} else {
					coreCust.setCustDeposit(amount);
				}

				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkBal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkSign").toString())) {
					coreCust.setCustBlockedDeposit(amount.negate()); 
				} else {
					coreCust.setCustBlockedDeposit(amount);
				}

				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBalSign").toString())) {
					coreCust.setTotalCustBal(amount.negate()); 
				} else {
					coreCust.setTotalCustBal(amount);
				}

				amount = new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBlkBal").toString());
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBlkSign").toString())) {
					coreCust.setTotalCustBlockedBal(amount.negate()); 
				} else {
					coreCust.setTotalCustBlockedBal(amount);
				}

				//Limit Details
				CustomerLimit custLimit = null;
				if(StringUtils.isNotEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtCcy").toString())){
					custLimit = new CustomerLimit();
					custLimit.setLimitCurrency(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtCcy").toString());
					custLimit.setLimitCcyEdit(Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtCcyEdit").toString()));
					custLimit.setRemarks(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtRemark").toString());
					String strDate = StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtExpDt").toString());
					custLimit.setLimitExpiry(DateUtility.getUtilDate(strDate, "dd-MM-yyyy"));

					if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtRiskSign").toString())) {
						custLimit.setRiskAmount(new BigDecimal(pcmlDoc.getValue(pcml +  ".@RSPDTA.LmtRisk").toString()).negate());	
					} else {
						custLimit.setRiskAmount(new BigDecimal(pcmlDoc.getValue(pcml +  ".@RSPDTA.LmtRisk").toString()));	
					}
					custLimit.setLimitAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtSts").toString()));
					if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtAvlSign").toString())) {
						custLimit.setAvailAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtAvl").toString()).negate());	
					} else {
						custLimit.setAvailAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.LmtAvl").toString()));
					}
				}
				coreCust.setCustomerLimit(custLimit);

				coreCust.setCustRspData(getString(pcmlDoc, pcml, ".@RSPDTA.CustRspData")); 		//Customer Response Data

			} else {
				logger.info("Customer Details Not found");	
				throw new InterfaceException("9999",getString(pcmlDoc, pcml, ".@ERPRM"));
			}

		}catch(InterfaceException e)	{			
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getErrorMessage());
		}catch(Exception e)	{			
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}finally{
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return coreCust;
	}

	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTKAS13PR.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
	@Override
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws InterfaceException{	
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFNC";		

		try{	

			as400 = this.hostConnection.getConnection();

			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", coreCust.getCustomerMnemonic()); 		//Customer Number

			logger.debug(" Before PCML Call");
			this.hostConnection.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	

				coreCust.setCustomerMnemonic(getString(pcmlDoc, pcml, ".@RSPDTA.CustMemonic")); 				//Customer mnemonic
				//  This Has to be Changed Based on The PCML to get The Core Customer Information

				/*coreCust.setCustomerFullName(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCUN")); 				//Customer full name
				coreCust.setDefaultAccountShortName(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPDAS")); 		//Default Account Short name
				coreCust.setCustomerType(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCTP")); 					//Customer Type
				coreCust.setCustomerClosed(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCUC")); 					//Customer Closed?
				coreCust.setCustomerInactive(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCUZ")); 				//Customer Inactive?
				//coreCust.setLanguageCode(getString(pcmlDoc, pcml, ".@RSPDTA.dsRspCtp")); 					//Language Code
				coreCust.setParentCountry(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCNAP")); 					//Parent Country
				coreCust.setRiskCountry(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCNAR")); 					//Risk Country
				coreCust.setResidentCountry(coreCust.getRiskCountry()); 									//Resident Country
				//coreCust.setClosedDate(DateUtility.getUtilDate(getEquationDate(pcmlDoc, pcml, "").toString(),"ddMMyyyy")); //Close Date
				coreCust.setCustomerBranchMnemonic(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPBRNM")); 		//Customer Branch mnemonic
				//coreCust.setGroupStatus(getString(pcmlDoc, pcml, ".@RSPDTA.dsRspGrp")); 					//Group Status
				coreCust.setGroupName(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPGRP")); 						//Group name
				//coreCust.setSegmentIdentifier(getString(pcmlDoc, pcml, ".@RSPDTA.dsRspGrp")); 			//Segment Identifier
				coreCust.setSalutation(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPSALU")); 					//Salutation
				if(!getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPDOB").toString().equals("0")){
					coreCust.setCustDOB(DateUtility.getUtilDate(getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPDOB").toString(),"ddMMyyyy")); //Date Of Birth		
				}else{
					coreCust.setCustDOB(null);
				}
				coreCust.setGenderCode(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPGEND")); 					//Gender Code
				coreCust.setCustPOB(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPPOB")); 						//Place Of Birth
				coreCust.setCustPassportNum(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPPPN")); 				//Passport number
				if(!getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPPPE").toString().equals("0")){
					coreCust.setCustPassportExpiry(DateUtility.getUtilDate(getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPPPE").toString(),"ddMMyyyy")); //Passport Expiry Date		
				}else{
					coreCust.setCustDOB(null);
				}
				coreCust.setMinor(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPMINOR")); 						//Is Minor
				coreCust.setTradeLicNumber(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPTLN")); 					//Trade License number
				if(!getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPTLE").toString().equals("0")){
					coreCust.setTradeLicExpiry(DateUtility.getUtilDate(getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPTLE").toString(),"ddMMyyyy")); // Trade License Expiry Date		
				}else{
					coreCust.setCustDOB(null);
				}
				coreCust.setVisaNumber(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPVISAN")); 					//Visa Number
				if(!getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPVISAE").toString().equals("0")){
					coreCust.setVisaExpiry(DateUtility.getUtilDate(getEquationDate(pcmlDoc, pcml, ".@RSPDTA.DSRSPVISAE").toString(),"ddMMyyyy")); //Visa Expiry date		
				}else{
					coreCust.setCustDOB(null);
				}
				coreCust.setNationality(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPNTL")); 					// Nationality
				 */				
			} else {
				logger.info("Customer Details Not found");	
				throw new InterfaceException("9999",getString(pcmlDoc, pcml, ".@ERPRM"));
			}

		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}catch(Exception e)	{			
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}finally{
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return coreCust;
	}
	

	@Override
	public FinanceCustomerDetails fetchFinCustDetails(FinanceCustomerDetails financeCustomerDetails)
			throws InterfaceException {
		return null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public HostConnection getHostConnection() {
		return hostConnection;
	}
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}

}
