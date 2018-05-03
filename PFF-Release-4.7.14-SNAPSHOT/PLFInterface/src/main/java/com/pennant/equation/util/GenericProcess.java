package com.pennant.equation.util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class GenericProcess {
	private static final Logger logger = Logger.getLogger(GenericProcess.class);
	
	public static final String DEFAULT_COUNTRY 	 					= "971";
	public static final String PHONE_AREACODE 	 			 		= "50";
	public static final String PHONE_TYEP_MOBILE 	 				= "MOBILE";
	public static final String PHONE_TYEP_OFFICE 	 				= "OFFICE";
	public static final String PHONE_TYEP_RESIDENCE 				= "WORK";
	public static final String PHONE_TYEP_OTHER 	 				= "GENERAL";
	
	public static final String GENDER_MALE 	 	 					= "MALE";
	public static final String GENDER_FEMALE 	 	 				= "FEMALE";
	public static final String GENDER_OTHER 	 	 				= "OTH";
	
	public static final String InterestRateBasisCodes_CrRateBasisCode 	= "A/A_360";
	public static final String InterestRateBasisCodes_DrRateBasisCode 	= "Actual/360";
	public static final String RelationshipOfficer_ROfficerDeptCode 	= "MIGR";

	public GenericProcess() {
		
	}
	
	protected String getString(ProgramCallDocument doc, String pcml, String name) throws PcmlException {		
		return (String) doc.getValue(pcml + name);		
	}

	protected boolean getBoolean(String value) {
		if ("Y".equalsIgnoreCase(StringUtils.trimToEmpty(value)) || "1".equals(StringUtils.trimToEmpty(value))) {
			return true;
		} else {
			return false;
		}
	}
	
	protected Date formatCYMDDate(String date) {
		try {
			return 	DateUtility.convertDateFromAS400(new BigDecimal(date));
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	protected long getLong(String number) {
		if (StringUtils.isBlank(number)) {
			return 0;
		} else {
			return Long.parseLong(number);
		}
	}

	protected BigDecimal getAmount(String amount) throws PcmlException {
		if (StringUtils.isBlank(amount)) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(amount);
		}
	}
}
