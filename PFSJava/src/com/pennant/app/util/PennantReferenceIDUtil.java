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

import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.collateral.FacilityDetailDAO;
import com.pennant.backend.dao.facility.FacilityDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TreasuaryFinHeaderDAO;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.util.PennantConstants;


public class PennantReferenceIDUtil implements Serializable {

    private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = Logger.getLogger(PennantReferenceIDUtil.class);
	
	private static NextidviewDAO nextidviewDAO;
	private static FinanceMainDAO financeMainDAO;
	private static TreasuaryFinHeaderDAO treasuaryFinHeaderDAO;
	private static FacilityDAO facilityDAO;
	private static FacilityDetailDAO facilityDetailDAO;

	public static long genNewWhatIfRef(boolean isWIF) {
		logger.debug("Entering");
		
		long   whatIfInqNum=0;
		long   befSeqNumber =getNextidviewDAO().getSeqNumber("SeqWIFFinanceMain");

		String seqNumString=String.valueOf(befSeqNumber).trim();

		long dateYYYYJDay=0;
		long seqNumber=1;

		if(seqNumString.length()<=7){
			try {
				dateYYYYJDay = Long.parseLong(seqNumString);
			} catch (Exception e) {
				seqNumber=1;	
			}
		} else if(seqNumString.length()>7){

			try {
				dateYYYYJDay = Long.parseLong(seqNumString.substring(0,7));
			} catch (Exception e) {
				seqNumber=1;	
			}

			try {
				seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(7)));
			} catch (Exception e) {
				seqNumber=1;	
			}
		}

		if(dateYYYYJDay!=DateUtility.getDateYYYYJDay()){
			dateYYYYJDay = DateUtility.getDateYYYYJDay();
			seqNumber=1;
		}else{
			seqNumber=seqNumber+1;;
		}
		boolean status=true;
		while(status){
			whatIfInqNum = Long.parseLong(String.valueOf(dateYYYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 6, '0')));
			boolean isFinIdExist =getFinanceMainDAO().isFinReferenceExists(String.valueOf(whatIfInqNum), "_View",isWIF);
			if(isFinIdExist){
				seqNumber=seqNumber+1;	
			}else{
				status=false;
			}
		}
		getNextidviewDAO().setSeqNumber("SeqWIFFinanceMain", whatIfInqNum);

		logger.debug("whatIfInqNum--->"+whatIfInqNum);
		logger.debug("Leaving");
		return whatIfInqNum;

	}
	public static String genNewCafRef(String division,String custCtgCode) {
		logger.debug("Entering");
		Date appldate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		StringBuilder caf=new StringBuilder();
		//Division
		if (division.equals(PennantConstants.FACILITY_COMMERCIAL)) {
			caf.append("COM");
        }else if(division.equals(PennantConstants.FACILITY_CORPORATE)){
    		caf.append("IBD");
        }
		caf.append("/");
		//Customer Category
		if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_BANK)) {
			caf.append("FI");
		} else if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_CORP)) {
			caf.append("CC");
		} else  if (custCtgCode.equals(PennantConstants.PFF_CUSTCTG_INDIV)){
			caf.append("IC");
		}
		caf.append("/");

		//Current Year
		String year=String.valueOf(DateUtility.getYear(appldate));
		String yearToAppend=year.substring(year.length()-2);
		//Unique Sequence
		long befSeqNumber = getNextidviewDAO().getSeqNumber("SeqCAFReference");
		befSeqNumber = befSeqNumber + 1;
		if (String.valueOf(befSeqNumber).length()>=6) {
			befSeqNumber=1;
        }
		boolean status=true;
		while(status){
			String tempCaf=caf.toString()+StringUtils.leftPad(String.valueOf(befSeqNumber), 5, '0')+"/"+yearToAppend;
			Facility facility =getFacilityDAO().getFacilityById(tempCaf, "_View");
			if((facility!=null)){
				befSeqNumber=befSeqNumber+1;	
			}else{
				status=false;
			}
		}
		
		caf.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 5, '0'));
		caf.append("/");
		//Current Year
		caf.append(yearToAppend);
		getNextidviewDAO().setSeqNumber("SeqCAFReference", befSeqNumber);

		logger.debug("CAFReferenceNum--->"+caf.toString());
		logger.debug("Leaving");
		return caf.toString();

	}
	public static String genNewFacilityRef(String cafRefrence) {
		logger.debug("Entering");
		StringBuilder facilityref=new StringBuilder(cafRefrence);
		//Unique Sequence
		long   befSeqNumber =getNextidviewDAO().getSeqNumber("SeqFacilityDetails");
		befSeqNumber = befSeqNumber + 1;
		if (String.valueOf(befSeqNumber).length()>=3) {
			befSeqNumber=1;
        }
		boolean status=true;
		while(status){
			String tempfacilityref=cafRefrence+"/"+StringUtils.leftPad(String.valueOf(befSeqNumber), 2, '0');
			FacilityDetail facilityDetail =getFacilityDetailDAO().getFacilityDetailById(tempfacilityref, "_View");
			if((facilityDetail!=null)){
				befSeqNumber=befSeqNumber+1;
			}else{
				status=false;
			}
		}
		facilityref.append("/");
		facilityref.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 2, '0'));
		getNextidviewDAO().setSeqNumber("SeqFacilityDetails", befSeqNumber);
		
		logger.debug("facilityRef--->"+facilityref.toString());
		logger.debug("Leaving");
		return facilityref.toString();
		
	}
	
	
	public static long genInvetmentNewRef() {
		logger.debug("Entering");
		
		long   investmentRef = 0;

		long   befSeqNumber = getNextidviewDAO().getSeqNumber("SeqInvestMent");

		String seqNumString = String.valueOf(befSeqNumber).trim();

		long dateYYYYJDay=0;
		long seqNumber=1;

		if(seqNumString.length()<=7){
			try {
				dateYYYYJDay = Long.parseLong(seqNumString);
			} catch (Exception e) {
				seqNumber=1;	
			}
		} else if(seqNumString.length()>7){

			try {
				dateYYYYJDay = Long.parseLong(seqNumString.substring(0,7));
			} catch (Exception e) {
				seqNumber=1;	
			}

			try {
				seqNumber = Long.parseLong(StringUtils.trim(seqNumString.substring(7)));
			} catch (Exception e) {
				seqNumber=1;	
			}
		}

		if(dateYYYYJDay!=DateUtility.getDateYYYYJDay()){
			dateYYYYJDay = DateUtility.getDateYYYYJDay();
			seqNumber=1;
		}else{
			seqNumber=seqNumber+1;;
		}
		boolean status = true;
	
		while(status){
			investmentRef = Long.parseLong(String.valueOf(dateYYYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 6, '0')));
			InvestmentFinHeader investmentFinHeader = getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(String.valueOf(investmentRef), "_View");
			if ((investmentFinHeader != null)) {
				seqNumber = seqNumber + 1;
			} else {
				status = false;
			}
		}
		getNextidviewDAO().setSeqNumber("SeqInvestMent", investmentRef);

		logger.debug("Back Office Reference --->"+investmentRef);
		logger.debug("Leaving");
		return investmentRef;

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		PennantReferenceIDUtil.nextidviewDAO = nextidviewDAO;
	}
	public static NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		PennantReferenceIDUtil.financeMainDAO = financeMainDAO;
	}
	public static FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public static TreasuaryFinHeaderDAO getTreasuaryFinHeaderDAO() {
		return treasuaryFinHeaderDAO;
	}

	public void setTreasuaryFinHeaderDAO(TreasuaryFinHeaderDAO treasuaryFinHeaderDAO) {
		PennantReferenceIDUtil.treasuaryFinHeaderDAO = treasuaryFinHeaderDAO;
	}
	public  void setFacilityDAO(FacilityDAO facilityDAO) {
	    PennantReferenceIDUtil.facilityDAO = facilityDAO;
    }
	public static FacilityDAO getFacilityDAO() {
	    return facilityDAO;
    }
	public static FacilityDetailDAO getFacilityDetailDAO() {
    	return facilityDetailDAO;
    }
	public  void setFacilityDetailDAO(FacilityDetailDAO facilityDetailDAO) {
    	PennantReferenceIDUtil.facilityDetailDAO = facilityDetailDAO;
    }
	
}
