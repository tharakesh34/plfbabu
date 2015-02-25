package com.pennant.equation.service.impl;
/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CoreInterfaceCallImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2013    														*
 *                                                                  						*
 * Modified Date    :  31-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.CustomerInterfaceData;
import com.pennant.coreinterface.model.EquationAbuser;
import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationBranch;
import com.pennant.coreinterface.model.EquationCountry;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustStatusCode;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationIdentityType;
import com.pennant.coreinterface.model.EquationIndustry;
import com.pennant.coreinterface.model.EquationInternalAccount;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.EquationTransactionCode;
import com.pennant.coreinterface.model.IncomeAccountTransaction;
import com.pennant.coreinterface.service.DailyDownloadProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.HostConnection;

public class DailyDownloadProcessImpl implements DailyDownloadProcess{
	
	private static Logger logger = Logger.getLogger(DailyDownloadProcessImpl.class);

	public static final String InterestRateBasisCodes_CrRateBasisCode = "A/A_360";
	public static final String InterestRateBasisCodes_DrRateBasisCode = "Actual/360";
	public static final String RelationshipOfficer_ROfficerDeptCode = "MIGR";
	
	private HostConnection hostConnection;

	/**
	 * Method for Importing Currency Details
	 */
	public List<EquationCurrency>  importCurrencyDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC8R"; 		            // Upload Currency Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCurrency  currency = null;
		List<EquationCurrency> currienciesList = new ArrayList<EquationCurrency>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTIPSC8R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						currency = new EquationCurrency();

						currency.setCcyCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCCY",indices).toString());
						currency.setCcyDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCUR",indices).toString());
						currency.setCcyNumber(pcmlDoc.getValue(pcml +".@RSPDTA.DETDTA.DSRSPCCYN",indices).toString());
						currency.setCcySwiftCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSCY",indices).toString());
						currency.setCcyEditField(Double.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCED",indices).toString()));
						currency.setCcyCrRateBasisCode(InterestRateBasisCodes_CrRateBasisCode);
						currency.setCcyDrRateBasisCode(InterestRateBasisCodes_DrRateBasisCode);
						currency.setCcyMinorCcyUnits(Double.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPPWD",indices).toString()));
						currency.setCcySpotRate(Double.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSPT",indices).toString()));
						currency.setCcyIsReceprocal((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSEI",indices).toString()).equals("1") ? true : false);
						currency.setCcyIsIntRounding(false); 
						
						currienciesList.add(currency);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return currienciesList;
	}
	
	/**
	 * Method for Importing Relation Ship Officer Details
	 */
	@Override
	public List<EquationRelationshipOfficer> importRelationShipOfficersDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC2R"; 		            // Upload RelationShipOfficers Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationRelationshipOfficer  relationshipOfficer = null;
		List<EquationRelationshipOfficer> relationshipOfficerList = new ArrayList<EquationRelationshipOfficer>();
		
		try {
			
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFC2R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						relationshipOfficer = new EquationRelationshipOfficer();

						relationshipOfficer.setROfficerCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPACO",indices).toString());
						relationshipOfficer.setROfficerDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPRNM",indices).toString());
						relationshipOfficer.setROfficerDeptCode(RelationshipOfficer_ROfficerDeptCode);
						
						relationshipOfficerList.add(relationshipOfficer);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return relationshipOfficerList;
	}
	
	/**
	 * Method for Importing Customer Type Details
	 */
	@Override
	public List<EquationCustomerType> importCustomerTypeDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC4R"; 		            // Upload CustomerType Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCustomerType  customerType = null;
		List<EquationCustomerType> customerTypeList = new ArrayList<EquationCustomerType>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFC4R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						customerType = new EquationCustomerType();
						
						customerType.setCustTypeCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCTP",indices).toString());
						customerType.setCustTypeCtg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCTG",indices).toString());
						customerType.setCustTypeDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCTD",indices).toString());
						
						customerTypeList.add(customerType);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return customerTypeList;
	}
	
	/**
	 * Method for Importing Department Details
	 */
	@Override
	public List<EquationDepartment> importDepartmentDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFGKR"; 		            // Upload Department Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationDepartment  department = null;
		List<EquationDepartment> departmentList = new ArrayList<EquationDepartment>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFGKR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						department = new EquationDepartment();
						
						department.setDeptCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPC1R",indices).toString());
						department.setDeptDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPC1D",indices).toString());
						
						departmentList.add(department);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return departmentList;
	}
	
	/**
	 * Method for Importing Customer Group Details
	 */
	@Override
	public List<EquationCustomerGroup> importCustomerGroupDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFTAR"; 		            // Upload CustomerGroup Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCustomerGroup  customerGroup = null;
		List<EquationCustomerGroup> customerGroupList = new ArrayList<EquationCustomerGroup>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFTAR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						customerGroup = new EquationCustomerGroup();
						customerGroup.setCustGrpID(new Long(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPGRP",indices).toString()));
						customerGroup.setCustGrpCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPGRP",indices).toString());
						customerGroup.setCustGrpDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPGRD",indices).toString());
						customerGroup.setCustGrpRO1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPACO",indices).toString());
						
						customerGroupList.add(customerGroup);
					}
				}
			}while(dsRspEnd.equals("N"));
		
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return customerGroupList;
	}
	
	/**
	 * Method for Importing Account Type Details
	 */
	@Override
	public List<EquationAccountType> importAccountTypeDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC5R"; 		            
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationAccountType  accountType = null;
		List<EquationAccountType> accountTypeList = new ArrayList<EquationAccountType>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFC5R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						accountType = new EquationAccountType();
						accountType.setAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATP",indices).toString());
						accountType.setAcTypeDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATD",indices).toString());
						accountType.setAcPurpose(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPACP",indices).toString());
						
						accountType.setInternalAc((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSC47",indices).toString()).equalsIgnoreCase("Y") ? true : false);
						accountType.setCustSysAc((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSYSAC",indices).toString()).equalsIgnoreCase("Y") ? true : false);
						accountType.setAcTypeIsActive((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPACTIVE",indices).toString()).equalsIgnoreCase("Y") ? true : false);
						
						accountType.setAcTypeNature1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT1",indices).toString());
						accountType.setAcTypeNature2(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT2",indices).toString());
						accountType.setAcTypeNature3(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT3",indices).toString());
						accountType.setAcTypeNature4(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT4",indices).toString());
						accountType.setAcTypeNature5(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT5",indices).toString());
						accountType.setAcTypeNature6(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT6",indices).toString());
						accountType.setAcTypeNature7(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT7",indices).toString());
						accountType.setAcTypeNature8(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT8",indices).toString());
						accountType.setAcTypeNature9(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATPNAT9",indices).toString());
						accountType.setAcTypeNature10(pcmlDoc.getValue(pcml +".@RSPDTA.DETDTA.DSRSPATPNAT10",indices).toString());
						
						accountTypeList.add(accountType);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return accountTypeList;
	}
	
	
	/**
	 * Method for Importing Customer Rating Details
	 */
	@Override
	public List<EquationCustomerRating> importCustomerRatingDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFRTR"; 		            
		int[] indices = new int[2]; 	    // Indices for access array value
		long custID ;
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCustomerRating  customerRating = null;
		List<EquationCustomerRating> customerRatingList = new ArrayList<EquationCustomerRating>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFRTR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						custID = Long.parseLong((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCpnc",indices));
						for (indices[1] = 0; indices[1] < 10; indices[1]++){
							if(StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCRat.DSRSPRTyp",indices).toString()).equals("")){
								break;
							}
							customerRating = new EquationCustomerRating();
							customerRating.setCustID(custID);
							customerRating.setCustRatingType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCRat.DSRSPRTyp",indices).toString()); 
							customerRating.setCustRatingCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCRat.DSRSPRCod",indices).toString()); 
							customerRating.setCustRating((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCRat.DSRSPRVal",indices).toString()));
							customerRatingList.add(customerRating);
						}
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return customerRatingList;
	}
	
	
	/**
	 * Method for Importing Country Details
	 */
	public List<EquationCountry>  importCountryDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC7R"; 		            // Upload Country Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCountry  country = null;
		List<EquationCountry> countryList = new ArrayList<EquationCountry>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTIPSC7R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						country = new EquationCountry();

						country.setCountryCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCNA",indices).toString());
						country.setCountryDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCNM",indices).toString());
						countryList.add(country);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return countryList;
	}
	
	/**
	 * Method for Importing Customer Status Code Details
	 */
	public List<EquationCustStatusCode>  importCustStausCodeDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFT9R"; 		            // Upload Industry Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationCustStatusCode  custStsCode = null;
		List<EquationCustStatusCode> custStsCodeList = new ArrayList<EquationCustStatusCode>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFT9R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						custStsCode = new EquationCustStatusCode();

						custStsCode.setCustStsCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPLSC",indices).toString());
						custStsCode.setCustStsDescription(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPLSD",indices).toString());
						custStsCode.setDueDays(Integer.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPPRDE",indices).toString()));
						custStsCodeList.add(custStsCode);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return custStsCodeList;
	}
	
	/**
	 * Method for Importing Industry Details
	 */
	public List<EquationIndustry>  importIndustryDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFC6R"; 		            // Upload Industry Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationIndustry  industryCode = null;
		List<EquationIndustry> industryList = new ArrayList<EquationIndustry>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFT9R(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						industryCode = new EquationIndustry();

						industryCode.setIndustryCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPACD",indices).toString());
						industryCode.setIndustryDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPANN",indices).toString());
						industryList.add(industryCode);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return industryList;
	}
	
	
	/**
	 * Method for Importing Branch Details
	 */
	public List<EquationBranch>  importBranchDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFCAR"; 		            // Upload Branch Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationBranch  branchCode = null;
		List<EquationBranch> branchList = new ArrayList<EquationBranch>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFCAR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						branchCode = new EquationBranch();

						branchCode.setBranchCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPBBN",indices).toString());
						branchCode.setBranchDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPBRN",indices).toString());
						branchCode.setBranchAddrLine1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPBAD1",indices).toString());
						branchCode.setBranchAddrLine2(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPBAD2",indices).toString());
						branchCode.setBranchFax(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPTLY",indices).toString());
						branchCode.setBranchTel(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPTPH",indices).toString());
						branchCode.setBranchSwiftBankCde(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSWB",indices).toString());
						branchCode.setBranchSwiftCountry(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCNAS",indices).toString());
						branchCode.setBranchSwiftLocCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSWL",indices).toString());
						branchCode.setBranchSwiftBrnCde(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSWBR",indices).toString());
						branchCode.setBranchSortCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPSORT",indices).toString());
						branchList.add(branchCode);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return branchList;
	}
	
	/**
	 * Method for Importing Internal Account Details
	 */
	public List<EquationInternalAccount>  importInternalAccDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFDHR"; 		            // Upload Internal Account Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationInternalAccount  internalAccCode = null;
		List<EquationInternalAccount> internalAccList = new ArrayList<EquationInternalAccount>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFDHR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						internalAccCode = new EquationInternalAccount();

						internalAccCode.setsIACode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPANMD",indices).toString());
						internalAccCode.setsIAName(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPADES",indices).toString());
						internalAccCode.setsIAShortName(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPDIA",indices).toString());
						internalAccCode.setsIAAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPATP",indices).toString());
						internalAccCode.setsIANumber(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPBNOC",indices).toString());
						internalAccList.add(internalAccCode);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return internalAccList;
	}
	
	
	
	/**
	 * Method for Importing Abuser Details
	 */
	public List<EquationAbuser>  importAbuserDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFABUR"; 		            // Upload Currency Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationAbuser  abuser = null;
		List<EquationAbuser> abuserList = new ArrayList<EquationAbuser>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFABUR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";

					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						abuser = new EquationAbuser();
						abuser.setAbuserIDType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspIDType",indices).toString());
						abuser.setAbuserIDNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspIDNumber",indices).toString());
						abuser.setAbuserExpDate(DateUtility.convertDateFromAS400(new BigDecimal(pcmlDoc.getValue(pcml +".@RSPDTA.DETDTA.dsRspExpDate",indices).toString())));
						abuserList.add(abuser);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return abuserList;
	}
	
	@Override
	public  void processCustomerNumbers(List<String> existingCustomers) throws Exception{
		logger.debug("Entering");
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFCUSR"; 		            // Upload Currency Details
		int[] indices = new int[1]; 	    // Indices for access array value
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", existingCustomers.size());// Account Number
			for (indices[0] = 0; indices[0] < existingCustomers.size(); indices[0]++){
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DSReqCPNC",indices, StringUtils.leftPad(existingCustomers.get(indices[0]),6,"0"));// Account Number
			}
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Importing Customer Details
	 */
	public List<CustomerInterfaceData>  importCustomerDetails() throws Exception{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFGFR"; 		            // Upload Currency Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		CustomerInterfaceData  customerInterfaceData = null;
		List<CustomerInterfaceData> customerList = new ArrayList<CustomerInterfaceData>();
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				
				pcmlDoc = PTPFFGFR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						customerInterfaceData = new CustomerInterfaceData();
						
						customerInterfaceData.setCustCIF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCpnc",indices).toString());
						customerInterfaceData.setCustFName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCun",indices).toString());
						customerInterfaceData.setDefaultAccountSName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspDas",indices).toString());//not using it
						customerInterfaceData.setCustTypeCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCtp",indices).toString());
						customerInterfaceData.setCustIsClosed((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCuc",indices).toString());
						customerInterfaceData.setCustIsActive((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCuZ",indices).toString());
						customerInterfaceData.setCustDftBranch((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspBrnm",indices).toString());
						customerInterfaceData.setGroupName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspGrp",indices).toString());
						customerInterfaceData.setDSRSPPDAT(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPdat",indices).toString()));//not using it
						customerInterfaceData.setCustParentCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCnap",indices).toString());
						customerInterfaceData.setCustRiskCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCnar",indices).toString());
						customerInterfaceData.setCustDOB(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspDob",indices).toString()));
						customerInterfaceData.setCustSalutationCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspSalu",indices).toString());
						customerInterfaceData.setCustGenderCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspGend",indices).toString());
						customerInterfaceData.setCustPOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPob",indices).toString());
						customerInterfaceData.setCustPassportNo((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPPN",indices).toString());
						customerInterfaceData.setCustPassportExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPPE",indices).toString()));
						customerInterfaceData.setCustIsMinor((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspMinor",indices).toString());
						customerInterfaceData.setTradeLicensenumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTln",indices).toString());
						customerInterfaceData.setTradeLicenseExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTle",indices).toString()));
						customerInterfaceData.setVisaNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspVisaN",indices).toString());
						customerInterfaceData.setVisaExpirydate(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspVisaE",indices).toString()));
						customerInterfaceData.setCustCoreBank((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCId",indices).toString());
						customerInterfaceData.setCustCtgCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCod",indices).toString());
						customerInterfaceData.setCustShrtName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCShn",indices).toString());
						customerInterfaceData.setCustFNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspLFNam",indices).toString());
						customerInterfaceData.setCustShrtNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspLSNam",indices).toString());
						customerInterfaceData.setCustCOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCOB",indices).toString());
						customerInterfaceData.setCustRO1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspACO",indices).toString());
						customerInterfaceData.setCustIsBlocked((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCUB",indices).toString());
						customerInterfaceData.setCustIsDecease((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCUD",indices).toString());
						customerInterfaceData.setCustIsTradeFinCust((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspYTRI",indices).toString());
						customerInterfaceData.setCustSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCA2",indices).toString());
						customerInterfaceData.setCustSubSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspSAC",indices).toString());
						customerInterfaceData.setCustProfession((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspProf",indices).toString());
						customerInterfaceData.setCustIncomeType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspITyp",indices).toString());
						customerInterfaceData.setCustTotalIncome((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspTInc",indices).toString());
						customerInterfaceData.setCustMaritalSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspMSta",indices).toString());
						customerInterfaceData.setCustEmpSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspESta",indices).toString());
						customerInterfaceData.setCustBaseCcy((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCCcy",indices).toString());
						customerInterfaceData.setCustResdCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNat",indices).toString());
						customerInterfaceData.setCustClosedOn((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspDCC",indices).toString());
						customerInterfaceData.setCustStmtFrq((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCFRQ",indices).toString());
						customerInterfaceData.setCustStmtLastDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPSTM",indices).toString());
						customerInterfaceData.setCustStmtNextDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNSTM",indices).toString());
						customerInterfaceData.setCustFirstBusinessDate(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCOD",indices).toString());
						customerInterfaceData.setCustRelation((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspRltn",indices).toString());
						//<!-- Address Details-->
						customerInterfaceData.setCustAddrType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPRIM",indices).toString());
						customerInterfaceData.setCustAddrHNbr((String)pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspHNbr",indices).toString());
						customerInterfaceData.setCustFlatNbr(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspFNbr",indices).toString());
						customerInterfaceData.setCustAddrStreet(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNA3",indices).toString());
						customerInterfaceData.setCustAddrLine1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNA4",indices).toString());
						customerInterfaceData.setCustAddrLine2(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspNA5",indices).toString());
						customerInterfaceData.setCustPOBox(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPBOX",indices).toString());
						customerInterfaceData.setCustAddrCity(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCity",indices).toString());
						customerInterfaceData.setCustAddrProvince(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspProv",indices).toString());
						customerInterfaceData.setCustAddrCountry(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspCnty",indices).toString());
						customerInterfaceData.setCustAddrZIP(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspZIP",indices).toString());
						customerInterfaceData.setCustAddrPhone(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPHN",indices).toString());
						//<!-- Phone Details-->
						customerInterfaceData.setCustOfficePhone(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspOPHN",indices).toString());
						customerInterfaceData.setCustMobile(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspMob",indices).toString());
						customerInterfaceData.setCustResPhone(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspRPHN",indices).toString());
						customerInterfaceData.setCustOtherPhone(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspAPHN",indices).toString());
						//<!-- Email Details-->
						customerInterfaceData.setCustEMailTypeCode1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEC01",indices).toString());
						customerInterfaceData.setCustEMail1(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEM01",indices).toString());
						customerInterfaceData.setCustEMailTypeCode2(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEC02",indices).toString());
						customerInterfaceData.setCustEMail2(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEM02",indices).toString());
						/*//<!-- Employee Details-->
						customerInterfaceData.setCustEmpName(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEName",indices).toString());
						customerInterfaceData.setCustEmpFrom(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEDOJ",indices).toString());
						customerInterfaceData.setCustEmpDesg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspEDesg",indices).toString());*/
						
						customerList.add(customerInterfaceData);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return customerList;
	}
	
	
	/**
	 * Method for Importing Transaction Code Details
	 */
	@Override
	public List<EquationTransactionCode> importTransactionCodeDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFCTR"; 		    // Upload CustomerGroup Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationTransactionCode  transactionCode = null;
		List<EquationTransactionCode> transactionCodesList = new ArrayList<EquationTransactionCode>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFCTR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						transactionCode = new EquationTransactionCode();
						transactionCode.setTranCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPTCD",indices).toString());
						transactionCode.setTranDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPTCN",indices).toString());
						transactionCode.setTranType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPDCI",indices).toString());						
						transactionCodesList.add(transactionCode);
					}
				}
			}while(dsRspEnd.equals("N"));
		
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return transactionCodesList;
	}
	
	/**
	 * Method for Importing Transaction Code Details
	 */
	@Override
	public List<EquationIdentityType> importIdentityTypeDetails() throws Exception {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFBHR"; 		    // Upload CustomerGroup Details
		int[] indices = new int[1]; 	    // Indices for access array value
		String dsRspEnd="";
		String requestStart = "Y";
		int recordsTillNow = 0;
		int dsRspCount = 0;
		EquationIdentityType  identityType = null;
		List<EquationIdentityType> identityTypeList = new ArrayList<EquationIdentityType>();
		
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			do{
				pcmlDoc = PTPFFBHR(requestStart, dsRspCount, recordsTillNow, pcmlDoc,pcml);
				if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
					dsRspEnd = pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPEND").toString();
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCURRCDS").toString());
					recordsTillNow = recordsTillNow + dsRspCount;
					requestStart = "N";
					
					for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
						identityType = new EquationIdentityType();
						String idType = StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPCVAL",indices).toString());
						identityType.setIdentityType(idType.length() > 8 ? idType.substring(0,8) : idType);
						identityType.setIdentityDesc(StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSRSPDSC",indices).toString()));
						identityTypeList.add(identityType);
					}
				}
			}while(dsRspEnd.equals("N"));
			
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		
		logger.debug("Leaving");
		return identityTypeList;
	}
	
	private ProgramCallDocument PTPFFGFR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	
	
	private ProgramCallDocument PTPFFABUR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFDHR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFCAR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	
	private ProgramCallDocument PTIPSC7R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFT9R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	
	private ProgramCallDocument PTIPSC8R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFC2R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFC4R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFGKR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFTAR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFC5R(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFRTR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFCTR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	
	private ProgramCallDocument PTPFFBHR(String requestStart, int reqTotal, int recordsTillNow,ProgramCallDocument pcmlDoc,String pcml) throws PcmlException, Exception {
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQSTART", requestStart); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTOTAL", reqTotal); 	
		pcmlDoc.setValue(pcml + ".@REQDTA.DSREQTILLNOW", recordsTillNow); 	
		pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
		pcmlDoc.setValue(pcml + ".@ERPRM", ""); 
		
		logger.debug(" Before PCML Call");
		getHostConnection().callAPI(pcmlDoc, pcml);
		logger.debug(" After PCML Call");
		return pcmlDoc;
	}
	

	
	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//



	/**
	 * Method for Importing Income Account Transactions
	 */
	@Override
	public List<IncomeAccountTransaction>  importIncomeAccTransactions(List<IncomeAccountTransaction> finIncomeAccounts) throws Exception{
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFSAR"; //Get Income Account Details
		List<IncomeAccountTransaction> accountList  = null;
		int[] indices = new int[1]; 	// Indices for access array value

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			IncomeAccountTransaction coreAcct = null;
			pcmlDoc.setValue(pcml + ".@REQDTA.@dsReqCount", finIncomeAccounts.size());// Account Number
			pcmlDoc.setValue(pcml + ".@REQDTA.@dsReqStartDt", DateUtility.formatDate(finIncomeAccounts.get(0).getLastMntOn(),"dd/MM/yyyy").replace("/", ""));// Account Number
			for (indices[0] = 0; indices[0] < finIncomeAccounts.size(); indices[0]++){
				coreAcct = finIncomeAccounts.get(indices[0]);
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.dsReqAccNum",indices, coreAcct.getIncomeAccount());// Account Number
			}
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {			
				indices = new int[1]; // Indices for access array value
				IncomeAccountTransaction account = null;
				accountList  = new ArrayList<IncomeAccountTransaction>();
				int dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());            	// Number of records returned 
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					account = new IncomeAccountTransaction();
					account.setIncomeAccount(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspAccNum",indices).toString()); //Income Account
					account.setProfitAmount(new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPftAma",indices).toString()))); //Profit Amount
					account.setManualAmount(new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspManAma",indices).toString()))); //Manual Amount
					account.setPffPostingAmount(new BigDecimal((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.dsRspPffAma",indices).toString()))); //Pff Posting Amount
					account.setLastMntOn(finIncomeAccounts.get(0).getLastMntOn()); 				//Last Maintained Date 
					accountList.add(account);
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception " + e);
			throw e;
		}  finally {
			getHostConnection().closeConnection(as400);
			pcmlDoc  =  null;
			as400    =  null;
		}
		logger.debug("Leaving");
		return accountList;
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
