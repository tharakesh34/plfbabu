package com.pennant.equation.process;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CoreBankNewCustomer;
import com.pennant.coreinterface.vo.CoreBankingCustomer;
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
	 * @Return Customer
	 */
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws CustomerNotFoundException{	
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFNC";		
		boolean newConnection = false;
		
		try{	
			if(this.hostConnection==null){
				this.hostConnection= new HostConnection();
				newConnection = true;
			}
			
			as400 = this.hostConnection.getConnection();
			
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			pcmlDoc.setValue(pcml + ".@REQDTA.CustId", coreCust.getCustomerMnemonic()); 		//Customer Number
						
			logger.debug(" Before PCML Call");
			this.hostConnection.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				
				coreCust.setCustomerMnemonic(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCUS")); 				//Customer mnemonic
				coreCust.setCustomerFullName(getString(pcmlDoc, pcml, ".@RSPDTA.DSRSPCUN")); 				//Customer full name
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
				
			} else {
				logger.info("Customer Details Not found");	
				throw new CustomerNotFoundException(getString(pcmlDoc, pcml, ".@ERPRM").toString());
			}

		}catch(Exception e)	{			
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		}finally{
			if(newConnection){
				this.hostConnection.disConnection();
			}
		}
		logger.debug("Leaving");
		return coreCust;
	}
	
	public String generateNewCIF(CoreBankNewCustomer customer) throws CustomerNotFoundException {	
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCIF";		
		boolean newConnection = false;
		String custCIF = "";
		
		try{	
			if(this.hostConnection==null){
				this.hostConnection= new HostConnection();
				newConnection = true;
			}
			
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
			if(newConnection){
				this.hostConnection.disConnection();
			}
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
