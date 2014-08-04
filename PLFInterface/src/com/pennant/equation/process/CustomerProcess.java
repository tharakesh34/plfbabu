package com.pennant.equation.process;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CoreBankAvailCustomer;
import com.pennant.coreinterface.vo.CoreBankNewCustomer;
import com.pennant.coreinterface.vo.CoreBankingCustomer;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.HostConnection;

public class CustomerProcess extends GenericProcess{

	private static Logger logger = Logger.getLogger(CustomerProcess.class);
	
	private HostConnection hostConnection;

	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTKAS13PR.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
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
	
	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTPFF14R.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
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
	
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	public HostConnection getHostConnection() {
		return hostConnection;
	}

	
}
