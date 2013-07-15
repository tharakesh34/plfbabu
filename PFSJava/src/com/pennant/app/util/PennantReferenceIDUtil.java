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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;


public class PennantReferenceIDUtil implements Serializable {

    private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = Logger.getLogger(PennantReferenceIDUtil.class);
	
	private static NextidviewDAO nextidviewDAO;
	private static FinanceMainDAO financeMainDAO;

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
			FinanceMain financeMain =getFinanceMainDAO().getFinanceMainById(String.valueOf(whatIfInqNum), "_View",isWIF);
			if((financeMain!=null)){
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
}
