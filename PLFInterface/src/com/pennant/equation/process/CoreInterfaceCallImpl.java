package com.pennant.equation.process;
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.corebanking.interfaces.CoreInterfaceCall;
import com.pennant.equation.util.HostConnection;

public class CoreInterfaceCallImpl implements CoreInterfaceCall{
	
	private static Logger logger = Logger.getLogger(CoreInterfaceCallImpl.class);

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
