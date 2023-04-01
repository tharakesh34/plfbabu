/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ReferenceUtil.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 5-09-2011 * * Modified Date :
 * 6-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ReferenceUtil implements Serializable {
	private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = LogManager.getLogger(ReferenceUtil.class);

	private static SequenceDao<?> sequenceGenetor;
	private static com.pennant.backend.dao.SequenceDao<?> sequenceDAO;

	public ReferenceUtil(SequenceDao<?> sequenceGenetor) {
		ReferenceUtil.sequenceGenetor = sequenceGenetor;
	}

	public static String generateCollateralRef() {
		logger.info("Generating Collateral Reference...");
		long seqNumber = sequenceGenetor.getNextValue("SeqCollateralSetup");
		long YYDayofY = getDateYYJDay();
		String genNo = "";

		genNo = String.valueOf(YYDayofY).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0'));

		String refNo = CollateralConstants.COLL_DIVISION + genNo;

		logger.info(String.format("Collateral Reference >> {}", refNo));

		return refNo;
	}

	/**
	 * Method for Preparing Sequence Reference Number for the Collateral Detail module
	 */
	public static String generateCollateralReference() {
		logger.debug(Literal.ENTERING);
		long generatedSeqNo = 0;
		String referenceNumber = "";
		// retrieve next seq no from sequence
		long befSeqNumber = sequenceGenetor.getNextValue("SeqCollateralSetup");

		String seqNumString = String.valueOf(befSeqNumber).trim();

		long dateYYJDay = 0;
		long seqNumber = 1;
		// Here we will get seqno in the inform ofYYDAYOFYEAR
		if (seqNumString.length() <= 5) {
			try {
				// get first 5 numbers to get the year and day
				dateYYJDay = Long.parseLong(seqNumString);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				seqNumber = 1;
			}
		} else if (seqNumString.length() > 5) {
			try {
				// get first 5 numbers to get the year and day
				dateYYJDay = Long.parseLong(seqNumString.substring(0, 5));
				seqNumber = Long.parseLong(seqNumString.substring(5, seqNumString.length()));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		// below condition will restart the collateral seq with 1 on daily basis(YYDAYOFYEAR+SEQNo
		if (dateYYJDay != getDateYYJDay()) {
			dateYYJDay = getDateYYJDay();
			// preparing new seqno
			String updatedSeq = "00002";
			String seqString = String.valueOf(dateYYJDay).concat(updatedSeq);
			long seqNo = Long.valueOf(seqString);
			// Create a Collateral with Sequence with 1
			seqNumber = 1;
			// call sequence update query
			sequenceDAO.updateSequence("SeqCollateralSetup", seqNo);
		}
		generatedSeqNo = Long
				.parseLong(String.valueOf(dateYYJDay).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0')));
		String moduleCode = CollateralConstants.COLL_DIVISION;
		referenceNumber = moduleCode + generatedSeqNo;
		logger.debug(String.format("Collateral Reference %s", referenceNumber));
		logger.debug(Literal.LEAVING);
		return referenceNumber;

	}

	/**
	 * Method for Preparing Sequence Reference Number for the VAS Module
	 */
	public static String generateVASRef() {
		logger.info("Generating VAS Reference...");
		long seqNumber = sequenceGenetor.getNextValue("SeqVasReference");

		long YYDayofY = getDateYYJDay();
		String genNo = "";

		genNo = String.valueOf(YYDayofY).concat(StringUtils.leftPad(String.valueOf(seqNumber), 5, '0'));

		String refNo = VASConsatnts.VAS_DIVISION + genNo;

		logger.info(String.format("VAS Reference {}", refNo));
		return refNo;
	}

	public static String genNewCafRef(String division, String custCtgCode) {
		logger.info("Generating CAF Reference...");

		Date appldate = SysParamUtil.getAppDate();
		StringBuilder caf = new StringBuilder();

		// Division
		switch (division) {
		case FacilityConstants.FACILITY_COMMERCIAL:
			caf.append("COM/");
			break;
		case FacilityConstants.FACILITY_CORPORATE:
			caf.append("WBG/");
		default:
			caf.append("/");
			break;
		}

		// Customer Category
		switch (custCtgCode) {
		case PennantConstants.PFF_CUSTCTG_SME:
			caf.append("FI/");
			break;
		case PennantConstants.PFF_CUSTCTG_CORP:
			caf.append("CC/");
			break;
		case PennantConstants.PFF_CUSTCTG_INDIV:
			caf.append("IC/");
			break;
		default:
			caf.append("/");
			break;
		}

		String yr = String.valueOf(DateUtil.getYear(appldate)).substring(2);

		long befSeqNumber = sequenceGenetor.getNextValue("SeqCAFReference");
		befSeqNumber = befSeqNumber + 1;

		if (String.valueOf(befSeqNumber).length() >= 6) {
			befSeqNumber = 1;
		}

		caf.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 5, '0'));
		caf.append('/').append(yr);

		logger.info("Generated CAF Reference >> {}...", caf);
		return caf.toString();

	}

	public static String genNewFacilityRef(String cafRefrence) {
		logger.info("Generating Facility Reference...");

		StringBuilder facilityref = new StringBuilder(cafRefrence);

		long befSeqNumber = sequenceGenetor.getNextValue("SeqFacilityDetails");
		befSeqNumber = befSeqNumber + 1;

		if (String.valueOf(befSeqNumber).length() >= 3) {
			befSeqNumber = 1;
		}

		facilityref.append('/');
		facilityref.append(StringUtils.leftPad(String.valueOf(befSeqNumber), 2, '0'));

		logger.info("Generated Facility Reference {}", facilityref);
		return facilityref.toString();

	}

	public static long genInvetmentNewRef() {
		logger.info("Generating Investment Reference...");
		long seqNumber = sequenceGenetor.getNextValue("SeqInvestment");

		long YYDayofY = getDateYYJDay();
		String genNo = "";

		genNo = String.valueOf(YYDayofY).concat(StringUtils.leftPad(String.valueOf(seqNumber), 6, '0'));

		logger.info("Generated Investment Reference {}", genNo);
		return Long.valueOf(genNo);
	}

	private static long getDateYYJDay() {
		Calendar curCalendar = Calendar.getInstance();
		curCalendar.setTime(SysParamUtil.getAppDate());

		return Long.parseLong(String.valueOf(curCalendar.get(Calendar.YEAR)).substring(2)
				.concat(StringUtils.leftPad(String.valueOf(curCalendar.get(Calendar.DAY_OF_YEAR)), 3, "0")));
	}

}
