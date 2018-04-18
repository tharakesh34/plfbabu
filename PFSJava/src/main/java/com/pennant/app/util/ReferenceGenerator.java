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
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.ReferenceConstants;
import com.pennant.backend.util.SMTParameterConstants;

public class ReferenceGenerator implements Serializable {
	private static final long		serialVersionUID	= -4965488291173350445L;
	private static Logger			logger				= Logger.getLogger(ReferenceGenerator.class);

	private static NextidviewDAO	nextidviewDAO;
	private static final String		DEFAULT_FORMAT		= "BBBB_PPP_SSSSSSS";
	private static final String		BRANCH				= "B";
	private static final String		DIVISION			= "D";
	private static final String		PRODUCT				= "P";
	private static final String		LOANTYPE			= "L";
	private static final String		SEQUENCE			= "S";

	/**
	 * Method for Generating Sequence Reference Number based on Branch and product code
	 * 
	 * @param financeMain
	 * @param financeType
	 * @return
	 */
	public static String generateFinRef(FinanceMain financeMain, FinanceType financeType) {

		String format = SysParamUtil.getValueAsString(SMTParameterConstants.LOAN_REF_FORMAT);
		if (StringUtils.isBlank(format)) {
			format = DEFAULT_FORMAT;
		}

		StringBuilder lonRef = new StringBuilder("");
		String branchCode = StringUtils.trimToEmpty(financeMain.getFinBranch());
		String productCode = StringUtils.trimToEmpty(financeMain.getFinCategory());
		String divisionCode = StringUtils.trimToEmpty(financeType.getFinDivision());
		String finType = StringUtils.trimToEmpty(financeType.getFinType());

		if (StringUtils.isNotBlank(format)) {
			String[] formetFileds = format.split("_");
			for (String formatCode : formetFileds) {

				if (formatCode.startsWith(BRANCH)) {
					appendLoanRef(lonRef, branchCode, formatCode);
				}
				if (formatCode.startsWith(LOANTYPE)) {
					appendLoanRef(lonRef, finType, formatCode);
				}

				if (formatCode.startsWith(PRODUCT)) {
					appendLoanRef(lonRef, productCode, formatCode);
				}

				if (formatCode.startsWith(DIVISION)) {
					appendLoanRef(lonRef, divisionCode, formatCode);
				}

				if (formatCode.startsWith(SEQUENCE)) {
					int length = formatCode.length();
					long referenceSeqNumber = nextidviewDAO.getNextId("SeqFinReference");
					String sequence = StringUtils.leftPad(String.valueOf(referenceSeqNumber), length, '0');
					lonRef.append(sequence);
				}
			}
		}

		return StringUtils.trimToEmpty(lonRef.toString());

	}

	private static void appendLoanRef(StringBuilder lonRef, String code, String formatCode) {
		String tempLoanRef = "";
		int length = formatCode.length();
		if (code.length() < length) {
			tempLoanRef = StringUtils.rightPad(code, length, '0');
		} else {
			tempLoanRef = code.substring(0, length);
		}
		lonRef.append(tempLoanRef);
	}

	/**
	 * Method for Generating Sequence Reference Number based on Branch and product code
	 * 
	 * @param isWIF
	 * @param finDivision
	 * @return
	 */
	@Deprecated
	public static String generateNewFinRef(boolean isWIF, FinanceMain financeMain) {
		logger.debug("Entering");

		// Product code
		String branch = StringUtils.trimToEmpty(financeMain.getSwiftBranchCode());

		if (branch.length() < 4) {
			branch = StringUtils.leftPad(branch, 4, '0');
		} else {
			branch = branch.substring(0, 4);
		}

		// Product code.
		String product = financeMain.getFinType();

		if (product.length() < 3) {
			product = StringUtils.leftPad(product, 3, '0');
		} else {
			product = product.substring(0, 3);
		}

		// Get the sequence number.
		long referenceSeqNumber = nextidviewDAO.getNextId("SeqFinReference");
		String sequence = StringUtils.leftPad(String.valueOf(referenceSeqNumber), 7, '0');

		logger.debug("Leaving");
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("LOAN_REFERENCE_IDENTIFIER"))) {
			return ReferenceConstants.DIVISION_IDENTIFIER.concat(branch).concat(product).concat(sequence);
		} else {
			return product.concat(branch).concat(sequence);
		}
	}

	public static void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		ReferenceGenerator.nextidviewDAO = nextidviewDAO;
	}

}
