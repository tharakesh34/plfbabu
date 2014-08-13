package com.pennant.equation.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerInterfaceData;
import com.pennant.coreinterface.model.CustomerInterfaceData.CustomerIdentity;
import com.pennant.coreinterface.model.CustomerInterfaceData.CustomerRating;
import com.pennant.coreinterface.model.CustomerInterfaceData.ShareHolder;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.service.CustomerDataProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.HostConnection;

public class CustomerDataProcessImpl extends GenericProcess implements CustomerDataProcess {
	
	private static Logger logger = Logger.getLogger(CustomerDataProcessImpl.class);
	private HostConnection hostConnection;
	
	@Override
	public CustomerInterfaceData getCustomerFullDetails(String custCIF,String custLoc) throws CustomerNotFoundException {
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
				CustomerInterfaceData customerInterfaceData = new CustomerInterfaceData();
				customerInterfaceData.setCustCIF((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustMemonic"));
				customerInterfaceData.setCustFName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustFName"));
				customerInterfaceData.setDSRSPCPNC((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCPNC"));
				customerInterfaceData.setDefaultAccountSName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DefaultAccountSName"));
				customerInterfaceData.setCustTypeCode((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustTypeCode"));
				customerInterfaceData.setCustIsClosed((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsClosed"));
				customerInterfaceData.setCustIsActive((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsActive"));
				customerInterfaceData.setCustDftBranch((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustDftBranch"));
				customerInterfaceData.setGroupName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPGRP"));
				customerInterfaceData.setDSRSPPDAT(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPPDAT")));
				customerInterfaceData.setCustParentCountry((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustParentCountry"));
				customerInterfaceData.setCustRiskCountry((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustRiskCountry"));
				customerInterfaceData.setCustDOB(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.CustDOB")));
				customerInterfaceData.setCustSalutationCode((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustSalutationCode"));
				customerInterfaceData.setCustGenderCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustGenderCode"));
				customerInterfaceData.setCustPOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPOB"));
				customerInterfaceData.setCustPassportNo((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPassportNo"));
				customerInterfaceData.setCustPassportExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.CustPassportExpiry")));
				customerInterfaceData.setCustIsMinor((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsMinor"));
				customerInterfaceData.setTradeLicensenumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.TradeLicensenumber"));
				customerInterfaceData.setTradeLicenseExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.TradeLicenseExpiry")));
				customerInterfaceData.setVisaNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.VisaNumber"));
				customerInterfaceData.setVisaExpirydate(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.VisaExpirydate")));
				customerInterfaceData.setCustCoreBank((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCoreBank"));
				customerInterfaceData.setCustCtgCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCtgCode"));
				customerInterfaceData.setCustShrtName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShrtName"));
				customerInterfaceData.setCustFNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustFNameLclLng"));
				customerInterfaceData.setCustShrtNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShrtNameLclLng"));
				customerInterfaceData.setCustCOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCOB"));
				customerInterfaceData.setCustRO1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRO1"));
				customerInterfaceData.setCustIsBlocked((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsBlocked"));				
				customerInterfaceData.setCustIsDecease((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsDecease"));
				customerInterfaceData.setCustIsTradeFinCust((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsTradeFinCust"));
				customerInterfaceData.setCustSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustSector"));
				customerInterfaceData.setCustSubSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustSubSector"));
				customerInterfaceData.setCustProfession((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustProfession"));
				customerInterfaceData.setCustTotalIncome(pcmlDoc.getValue(pcml + ".@RSPDTA.CustTotalIncome"));
				customerInterfaceData.setCustMaritalSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustMaritalSts"));
				customerInterfaceData.setCustEmpSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpSts"));
				customerInterfaceData.setCustBaseCcy((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustBaseCcy"));
				customerInterfaceData.setCustResdCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustResdCountry"));
				//customerInterfaceData.setCustNationality((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustNationality"));
				customerInterfaceData.setCustClosedOn(pcmlDoc.getValue(pcml + ".@RSPDTA.CustClosedOn"));
				customerInterfaceData.setCustStmtFrq((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtFrq"));
				customerInterfaceData.setCustIsStmtCombined((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsStmtCombined"));
				customerInterfaceData.setCustStmtLastDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtLastDate"));
				customerInterfaceData.setCustStmtNextDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtNextDate"));
				customerInterfaceData.setCustFirstBusinessDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustFirstBusinessDate"));
				customerInterfaceData.setCustRelation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRelation"));
				//<!-- Address Details-->
				customerInterfaceData.setCustAddrType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrType"));
				customerInterfaceData.setCustAddrHNbr((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrHNbr"));
				customerInterfaceData.setCustFlatNbr((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustFlatNbr"));
				customerInterfaceData.setCustAddrStreet((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrStreet"));
				customerInterfaceData.setCustAddrLine1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrLine1"));
				customerInterfaceData.setCustAddrLine2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrLine2"));
				customerInterfaceData.setCustPOBox((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPOBox"));
				customerInterfaceData.setCustAddrCity((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrCity"));
				customerInterfaceData.setCustAddrProvince((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrProvince"));
				customerInterfaceData.setCustAddrCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrCountry"));
				customerInterfaceData.setCustAddrZIP((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrZIP"));
				customerInterfaceData.setCustAddrPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrPhone"));
				//<!-- customer phone numbers -->	
				customerInterfaceData.setCustOfficePhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustOfficePhone"));
				customerInterfaceData.setCustMobile((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustMobile"));
				customerInterfaceData.setCustResPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustResPhone"));
				customerInterfaceData.setCustOtherPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustOtherPhone"));
				//<!-- Email Details-->
				customerInterfaceData.setCustEMailTypeCode1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMailTypeCode1"));
				customerInterfaceData.setCustEMail1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMail1"));
				customerInterfaceData.setCustEMailTypeCode2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMailTypeCode2"));
				customerInterfaceData.setCustEMail2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMail2"));
				//<!-- Employee Details-->
				customerInterfaceData.setCustEmpName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpName"));
				customerInterfaceData.setCustEmpFrom(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpFrom"));
				customerInterfaceData.setCustEmpDesg((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpDesg"));
				//		<!-- customer ratings-->	
				int dsRspCount=0;
				try {
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatCnt").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<CustomerRating> list=new ArrayList<CustomerRating>(); 
				int[] indices = new int[1]; // Indices for access array value
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++) {
					CustomerRating customerRating=	customerInterfaceData.new CustomerRating();
					customerRating.setCustRatingType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustRatingType",indices));
					customerRating.setCustLongRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustLongRate",indices));
					customerRating.setCustShortRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustShortRate",indices));
					list.add(customerRating);
				}
				customerInterfaceData.setCustomerRatinglist(list);
				//		<!-- customer Identity-->	
				int custIdCount=0;
				try {
					custIdCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIDCount").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<CustomerIdentity> idlist=new ArrayList<CustomerIdentity>(); 
				int[] indices1 = new int[1]; // Indices for access array value
				for (indices1[0] = 0; indices1[0] < custIdCount; indices1[0]++) {
					CustomerIdentity customerIdentity=	customerInterfaceData.new CustomerIdentity();
					customerIdentity.setCustIDType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdType",indices1));
					customerIdentity.setCustIDNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdNum",indices1));
					customerIdentity.setCustIDCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdCna",indices1));
					customerIdentity.setCustIDIssueDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdIssuDt",indices1));
					customerIdentity.setCustIDExpDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdExpDt",indices1));
					idlist.add(customerIdentity);
				}
				customerInterfaceData.setCustomerIdentitylist(idlist);
				
//				<!-- customer ShareHolder-->
				
				int custShareHolderCount = 0;
				try{
					custShareHolderCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.ShareHolderIDCount").toString());
				}catch(Exception e){
					logger.debug(e);
				}
				 List<ShareHolder> shareHolderList = new ArrayList<ShareHolder>();
				 int[] shareholderCount = new int[1]; // Indices for access array value
				 for (shareholderCount[0] = 0; shareholderCount[0] < custShareHolderCount; shareholderCount[0]++) {
					 ShareHolder shareHolder=	customerInterfaceData.new ShareHolder();
					 shareHolder.setShareHolderIDType( pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderIDType",shareholderCount));
					 shareHolder.setShareHolderIDRef((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderIDRef",shareholderCount));
					 shareHolder.setShareHolderPerc(pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderPerc",shareholderCount));
					 shareHolder.setShareHolderRole((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderRole",shareholderCount));
					 shareHolder.setShareHolderName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderName",shareholderCount));
					 shareHolder.setShareHolderNation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderNation",shareholderCount));
					 shareHolder.setShareHolderRisk((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderRisk",shareholderCount));
					 shareHolder.setShareHolderDOB(pcmlDoc.getValue(pcml + ".@RSPDTA.CustShareHolder.ShareHolderDOB",shareholderCount));
					 shareHolderList.add(shareHolder);
					}
					customerInterfaceData.setShareHolderlist(shareHolderList);
				 
				/*customerInterfaceData.setCustEmpHNbr(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpHNbr"));
				customerInterfaceData.setCustEMpFlatNbr(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpFlatNbr"));
				customerInterfaceData.setCustEmpAddrStreet(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrStreet"));
				customerInterfaceData.setCustEMpAddrLine1(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpAddrLine1"));
				customerInterfaceData.setCustEMpAddrLine2(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpAddrLine2"));
				customerInterfaceData.setCustEmpPOBox(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpPOBox"));
				customerInterfaceData.setCustEmpAddrCity(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrCity"));
				customerInterfaceData.setCustEmpAddrProvince(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrProvince"));
				customerInterfaceData.setCustEmpAddrCountry(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrCountry"));
				customerInterfaceData.setCustEmpAddrZIP(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrZIP"));
				customerInterfaceData.setCustEmpAddrPhone(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrPhone"));
				//<!-- customer notes-->	
				customerInterfaceData.setCustNotesTitle(pcmlDoc.getValue(pcml + ".@RSPDTA.CustNotesTitle"));
				customerInterfaceData.setCustNotes(pcmlDoc.getValue(pcml + ".@RSPDTA.CustNotes"));
				//<!-- customer Income-->	
				customerInterfaceData.setCustIncomeType(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIncomeType"));
				customerInterfaceData.setCustIncome(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIncome"));*/
				return customerInterfaceData;
			}	

		}catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new CustomerNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		} finally {
				this.hostConnection.closeConnection(as400);
		}
		return null;

	}
	
	
	@Override
	public List<CustomerCollateral> getCustomerCollateral(String custCIF)
			throws CustomerNotFoundException {

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
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
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
	public CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws CustomerNotFoundException{	
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
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.CASABal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABalSign").toString())) {
					coreCust.setCustActualBal(amount.negate()); 
				} else {
					coreCust.setCustActualBal(amount);
				}
				
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.CASABlkBal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.CASABlkSign").toString())) {
					coreCust.setCustBlockedBal(amount.negate()); 
				} else {
					coreCust.setCustBlockedBal(amount);
				}
				
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkSign").toString())) {
					coreCust.setCustDeposit(amount.negate()); 
				} else {
					coreCust.setCustDeposit(amount);
				}
				
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkBal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.DepoBlkSign").toString())) {
					coreCust.setCustBlockedDeposit(amount.negate()); 
				} else {
					coreCust.setCustBlockedDeposit(amount);
				}
				
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.TotBal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBalSign").toString())) {
					coreCust.setTotalCustBal(amount.negate()); 
				} else {
					coreCust.setTotalCustBal(amount);
				}
							
				amount = new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.TotBlkBal").toString()));
				if("-" .equals(pcmlDoc.getValue(pcml + ".@RSPDTA.TotBlkSign").toString())) {
					coreCust.setTotalCustBlockedBal(amount.negate()); 
				} else {
					coreCust.setTotalCustBlockedBal(amount);
				}
				
				//Limit Details
				CustomerLimit custLimit = null;
				if(!pcmlDoc.getValue(pcml + ".@RSPDTA.LmtCcy").toString().equals("")){
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
				throw new CustomerNotFoundException(getString(pcmlDoc, pcml, ".@ERPRM").toString());
			}

		}catch(CustomerNotFoundException e)	{			
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getErrorMsg());
		}catch(Exception e)	{			
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		}finally{
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return coreCust;
	}
	
	@Override
	public String generateNewCIF(CoreBankNewCustomer customer) throws CustomerNotFoundException {	
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCIF";		
		String custCIF = "";
		
		try{	
			as400 = this.hostConnection.getConnection();
			
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			pcmlDoc.setValue(pcml + ".@REQDTA.Operation", customer.getOperation()); 		//Customer Is Block or Add
			pcmlDoc.setValue(pcml + ".@REQDTA.CustCtgType", customer.getCustCtgType()); 	//Customer Category Type
			pcmlDoc.setValue(pcml + ".@REQDTA.FinReference", customer.getFinReference()); 	//Finance Reference
			
			if("A".equals(customer.getOperation())){
				pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", customer.getCustCIF()); 		//Customer CIF
				pcmlDoc.setValue(pcml + ".@REQDTA.CustType", customer.getCustType()); 		//Customer Type
				pcmlDoc.setValue(pcml + ".@REQDTA.ShortName", customer.getShortName().length() > 15 ? 
						customer.getShortName().subSequence(0, 14): customer.getShortName()); 	//Short Name
				pcmlDoc.setValue(pcml + ".@REQDTA.Country", customer.getCountry()); 		//Parent Country
				pcmlDoc.setValue(pcml + ".@REQDTA.Branch", customer.getBranch()); 			//Default Branch
				pcmlDoc.setValue(pcml + ".@REQDTA.Currency", customer.getCurrency()); 		//Basic Currency
			}
						
			logger.debug(" Before PCML Call");
			this.hostConnection.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				custCIF = getString(pcmlDoc, pcml, ".@RSPDTA.CustCIF"); 					//Customer mnemonic
				if(custCIF.equals("")){
					throw new CustomerNotFoundException("Customer Not Created.");
				}
			} else {
				logger.info("Customer Not Created.");	
				throw new CustomerNotFoundException(getString(pcmlDoc, pcml, ".@ERPRM").toString());
			}

		} catch (CustomerNotFoundException e) {
			logger.error("PCML Parsing exception.");	
			throw e;
		} catch (PcmlException e) {
			logger.error("PCML Parsing exception.");	
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Connection not Created.");	
			e.printStackTrace();
		}finally{
				this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return custCIF;
	}
	
	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTKAS13PR.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
	@Override
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws CustomerNotFoundException{	
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
				// TODO  This Has to be Changed Based on The PCML to get The Core Customer Information
				
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
				throw new CustomerNotFoundException(getString(pcmlDoc, pcml, ".@ERPRM").toString());
			}

		}catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new CustomerNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch(Exception e)	{			
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		}finally{
				this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return coreCust;
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public HostConnection getHostConnection() {
		return hostConnection;
	}
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	
}
