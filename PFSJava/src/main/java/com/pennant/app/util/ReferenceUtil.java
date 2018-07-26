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
 * FileName    		: PennantReferenceIDUtil.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  5-09-2011    														*
 *                                                                  						*
 * Modified Date    :  6-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.collateral.FacilityDetailDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.facility.FacilityDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TreasuaryFinHeaderDAO;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class ReferenceUtil implements Serializable {
    private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = Logger.getLogger(ReferenceUtil.class);
	
	private static FinanceMainDAO financeMainDAO;
	private static TreasuaryFinHeaderDAO treasuaryFinHeaderDAO;
	private static FacilityDAO facilityDAO;
	private static FacilityDetailDAO facilityDetailDAO;
	private static CollateralSetupDAO collateralSetupDAO;
	private static VASRecordingDAO vASRecordingDAO;
	private static SequenceDao<?> sequenceGenetor;

	/**
	 * Method for Generating Sequence Reference Number based on Division code
	 * @param isWIF
	 * @param finDivision
	 * @return
	 */
	@Deprecated
	public static String generateNewFinRef(boolean isWIF, String finDivision) {
		logger.debug("Entering");
		long   generatedSeqNo=0;
		boolean refUpdated = false;
		String referenceNumber = "";
		while(!refUpdated){
			long   befSeqNumber = sequenceGenetor.getSeqNumber("SeqWIFFinanceMain");

			String seqNumString=String.valueOf(befSeqNumber);

			long dateYYJDay=0;
			long seqNumber=1;

			if(seqNumString.length()<=5){
				try {
					dateYYJDay = Long.parseLong(seqNumString);
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber=1;	
				}
			} else if(seqNumString.length()>5){

				try {
					dateYYJDay = Long.parseLong(seqNumString.substring(0,5));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber=1;	
				}

				try {
					seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(5)));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber=1;	
				}
			}

			if(dateYYJDay!=DateUtility.getDateYYJDay()){
				dateYYJDay = DateUtility.getDateYYJDay();
				seqNumber=1;
			}else{
				seqNumber=seqNumber+1;
			}
			boolean status=true;

			referenceNumber = "";

			while(status){
				generatedSeqNo = Long.parseLong(String.valueOf(dateYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0')));
				boolean isFinIdExist =getFinanceMainDAO().isFinReferenceExists(String.valueOf(generatedSeqNo), "_View",isWIF);
				if(isFinIdExist){
					seqNumber=seqNumber+1;	
				}else{
					status=false;
				}
			}

			String divisionCode = "";
			if(StringUtils.equals(finDivision,FinanceConstants.FIN_DIVISION_RETAIL)){
				divisionCode = FinanceConstants.REF_DIVISION_RETAIL;
			}else if(StringUtils.equals(finDivision,FinanceConstants.FIN_DIVISION_CORPORATE)){
				divisionCode = FinanceConstants.REF_DIVISION_CORP;
			}
			referenceNumber = divisionCode + generatedSeqNo;

			refUpdated = getFinanceMainDAO().updateSeqNumber(befSeqNumber, generatedSeqNo);
		}
		logger.debug("Generated Reference Number --->"+referenceNumber);
		logger.debug("Leaving");
		return referenceNumber;

	}
	
	/**
	 * Method for Preparing Sequence Reference Number for the Collateral Detail module
	 * */
	public static String generateCollateralRef() {
		logger.debug("Entering");
		long generatedSeqNo = 0;
		boolean refUpdated = false;
		String referenceNumber = "";
		while (!refUpdated) {
			long befSeqNumber = sequenceGenetor.getSeqNumber("SeqCollateralSetup");

			String seqNumString = String.valueOf(befSeqNumber).trim();

			long dateYYJDay = 0;
			long seqNumber = 1;

			if (seqNumString.length() <= 5) {
				try {
					dateYYJDay = Long.parseLong(seqNumString);
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}
			} else if (seqNumString.length() > 5) {

				try {
					dateYYJDay = Long.parseLong(seqNumString.substring(0, 5));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}

				try {
					seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(5)));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}
			}

			if (dateYYJDay != DateUtility.getDateYYJDay()) {
				dateYYJDay = DateUtility.getDateYYJDay();
				seqNumber = 1;
			} else {
				seqNumber = seqNumber + 1;
			}
			boolean status = true;

			referenceNumber = "";

			while (status) {
				generatedSeqNo = Long.parseLong(String.valueOf(dateYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0')));
				boolean isExist = getCollateralSetupDAO().isCollReferenceExists(String.valueOf(generatedSeqNo), "_View");
				if (isExist) {
					seqNumber = seqNumber + 1;
				} else {
					status = false;
				}
			}
			String moduleCode = CollateralConstants.COLL_DIVISION;
			referenceNumber = moduleCode + generatedSeqNo;

			refUpdated = getCollateralSetupDAO().updateCollReferene(befSeqNumber, generatedSeqNo);
		}
		logger.debug("Generated Reference Number --->" + referenceNumber);
		logger.debug("Leaving");
		return referenceNumber;

	}
	/**
	 * Method for Preparing Sequence Reference Number for the VAS Module
	 * */
	public static String generateVASRef() {
		logger.debug("Entering");
		
		long generatedSeqNo = 0;
		boolean refUpdated = false;
		String referenceNumber = "";
		while (!refUpdated) {
			long befSeqNumber = sequenceGenetor.getSeqNumber("SeqVasReference");
			
			String seqNumString = String.valueOf(befSeqNumber);
			
			long dateYYJDay = 0;
			long seqNumber = 1;
			
			if (seqNumString.length() <= 5) {
				try {
					dateYYJDay = Long.parseLong(seqNumString);
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}
			} else if (seqNumString.length() > 5) {
				
				try {
					dateYYJDay = Long.parseLong(seqNumString.substring(0, 5));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}
				
				try {
					seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(5)));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					seqNumber = 1;
				}
			}
			
			if (dateYYJDay != DateUtility.getDateYYJDay()) {
				dateYYJDay = DateUtility.getDateYYJDay();
				seqNumber = 1;
			} else {
				seqNumber = seqNumber + 1;
			}
			boolean status = true;
			
			referenceNumber = "";
			
			while (status) {
				generatedSeqNo = Long.parseLong(String.valueOf(dateYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0')));
				boolean isExist = getvASRecordingDAO().isVasReferenceExists(String.valueOf(VASConsatnts.VAS_DIVISION+generatedSeqNo), "_View");
				if (isExist) {
					seqNumber = seqNumber + 1;
				} else {
					status = false;
				}
			}
			String moduleCode = VASConsatnts.VAS_DIVISION;
			referenceNumber = moduleCode + generatedSeqNo;
			
			refUpdated = getvASRecordingDAO().updateVasReference(befSeqNumber, generatedSeqNo);
		}
		logger.debug("Generated Reference Number --->" + referenceNumber);
		logger.debug("Leaving");
		return referenceNumber;
		
	}
	
	public static String genNewCafRef(String division,String custCtgCode) {
		logger.debug("Entering");
		Date appldate = DateUtility.getAppDate();
		StringBuilder caf=new StringBuilder();
		//Division
		if (division.equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			caf.append("COM");
        }else if(division.equals(FacilityConstants.FACILITY_CORPORATE)){
    		caf.append("WBG");
        }
		caf.append('/');
		//Customer Category
		if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_SME)) {
			caf.append("FI");
		} else if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_CORP)) {
			caf.append("CC");
		} else  if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_INDIV)){
			caf.append("IC");
		}
		caf.append('/');

		//Current Year
		String year=String.valueOf(DateUtility.getYear(appldate));
		String yearToAppend=year.substring(year.length()-2);
		//Unique Sequence
		long befSeqNumber = sequenceGenetor.getSeqNumber("SeqCAFReference");
		befSeqNumber = befSeqNumber + 1;
		if (String.valueOf(befSeqNumber).length()>=6) {
			befSeqNumber=1;
        }
		boolean status=true;
		while(status){
			String tempCaf=caf.toString()+StringUtils.leftPad(String.valueOf(befSeqNumber), 5, '0')+"/"+yearToAppend;
			Facility facility =getFacilityDAO().getFacilityById(tempCaf, "_View");
			if (facility != null) {
				befSeqNumber = befSeqNumber + 1;
			} else {
				status = false;
			}
		}
		
		caf.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 5, '0'));
		caf.append('/');
		//Current Year
		caf.append(yearToAppend);
		sequenceGenetor.setSeqNumber("SeqCAFReference", befSeqNumber);

		logger.debug("CAFReferenceNum--->"+caf.toString());
		logger.debug("Leaving");
		return caf.toString();

	}
	
	public static String genNewFacilityRef(String cafRefrence) {
		logger.debug("Entering");
		StringBuilder facilityref=new StringBuilder(cafRefrence);
		//Unique Sequence
		long   befSeqNumber = sequenceGenetor.getSeqNumber("SeqFacilityDetails");
		befSeqNumber = befSeqNumber + 1;
		if (String.valueOf(befSeqNumber).length()>=3) {
			befSeqNumber=1;
        }
		boolean status=true;
		while(status){
			String tempfacilityref=cafRefrence+"/"+StringUtils.leftPad(String.valueOf(befSeqNumber), 2, '0');
			FacilityDetail facilityDetail =getFacilityDetailDAO().getFacilityDetailById(tempfacilityref, "_View");
			if (facilityDetail != null) {
				befSeqNumber = befSeqNumber + 1;
			} else {
				status = false;
			}
		}
		facilityref.append('/');
		facilityref.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 2, '0'));
		sequenceGenetor.setSeqNumber("SeqFacilityDetails", befSeqNumber);
		
		logger.debug("facilityRef--->"+facilityref.toString());
		logger.debug("Leaving");
		return facilityref.toString();
		
	}
	
	
	public static long genInvetmentNewRef() {
		logger.debug("Entering");
		
		long   investmentRef = 0;

		long   befSeqNumber = sequenceGenetor.getSeqNumber("SeqInvestment");

		String seqNumString = String.valueOf(befSeqNumber).trim();

		long dateYYJDay=0;
		long seqNumber=1;

		if(seqNumString.length()<=5){
			try {
				dateYYJDay = Long.parseLong(seqNumString);
			} catch (Exception e) {
				logger.error("Exception: ", e);
				seqNumber=1;	
			}
		} else if(seqNumString.length()>5){

			try {
				dateYYJDay = Long.parseLong(seqNumString.substring(0,5));
			} catch (Exception e) {
				logger.error("Exception: ", e);
				seqNumber=1;	
			}

			try {
				seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(5)));
			} catch (Exception e) {
				logger.error("Exception: ", e);
				seqNumber=1;	
			}
		}

		if(dateYYJDay!=DateUtility.getDateYYJDay()){
			dateYYJDay = DateUtility.getDateYYJDay();
			seqNumber=1;
		}else{
			seqNumber=seqNumber+1;
		}
		boolean status = true;
	
		while(status){
			investmentRef = Long.parseLong(String.valueOf(dateYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 6, '0')));
			InvestmentFinHeader investmentFinHeader = getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(String.valueOf(investmentRef), "_View");
			if (investmentFinHeader != null) {
				seqNumber = seqNumber + 1;
			} else {
				status = false;
			}
		}
		sequenceGenetor.setSeqNumber("SeqInvestment", investmentRef);

		logger.debug("Back Office Reference --->"+investmentRef);
		logger.debug("Leaving");
		return investmentRef;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	/*public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		ReferenceUtil.nextidviewDAO = nextidviewDAO;
	}
	public static NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}*/

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		ReferenceUtil.financeMainDAO = financeMainDAO;
	}
	public static FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public static TreasuaryFinHeaderDAO getTreasuaryFinHeaderDAO() {
		return treasuaryFinHeaderDAO;
	}

	public void setTreasuaryFinHeaderDAO(TreasuaryFinHeaderDAO treasuaryFinHeaderDAO) {
		ReferenceUtil.treasuaryFinHeaderDAO = treasuaryFinHeaderDAO;
	}
	public  void setFacilityDAO(FacilityDAO facilityDAO) {
	    ReferenceUtil.facilityDAO = facilityDAO;
    }
	public static FacilityDAO getFacilityDAO() {
	    return facilityDAO;
    }
	public static FacilityDetailDAO getFacilityDetailDAO() {
    	return facilityDetailDAO;
    }
	public  void setFacilityDetailDAO(FacilityDetailDAO facilityDetailDAO) {
    	ReferenceUtil.facilityDetailDAO = facilityDetailDAO;
    }

	public static CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}
	public static void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		ReferenceUtil.collateralSetupDAO = collateralSetupDAO;
	}

	public static VASRecordingDAO getvASRecordingDAO() {
		return vASRecordingDAO;
	}

	public static void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		ReferenceUtil.vASRecordingDAO = vASRecordingDAO;
	}

	public static void setSequenceGenetor(SequenceDao<?> sequenceGenetor) {
		ReferenceUtil.sequenceGenetor = sequenceGenetor;
	}

}
