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
 * 19-06-2018		Siva			 		 0.2		Auto Receipt Number Generation      * 
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.ReferenceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ReferenceGenerator implements Serializable {
	private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = LogManager.getLogger(ReferenceGenerator.class);

	//private static NextidviewDAO	nextidviewDAO;
	private static final String DEFAULT_FORMAT = "BBBB_PPP_SSSSSSS";
	private static final String BRANCH = "B";
	private static final String SWIFT_BRANCH = "A";
	private static final String DIVISION = "D";
	private static final String PRODUCT = "P";
	private static final String LOANTYPE = "L";
	private static final String SEQUENCE = "S";
	//First 4 letters of the customer and DOB in DDMMYY format 
	private static final String DEFAULT_PWD_FORMAT = "LLLL_ddMMyy";
	private static final String LETTER = "L";
	private static final String DAY = "d";
	private static final String YEAR = "y";
	private static SequenceDao<?> sequenceGenetor;

	/**
	 * Method for Generating Sequence Reference Number based on Branch and product code
	 * 
	 * @param financeMain
	 * @param financeType
	 * @return
	 */
	public static String generateFinRef(FinanceMain financeMain, FinanceType financeType) {

		String format = SysParamUtil.getValueAsString(SMTParameterConstants.LOAN_REF_FORMAT);
		String prefix = SysParamUtil.getValueAsString(SMTParameterConstants.LOAN_REF_PREFIX);
		if (StringUtils.isBlank(format)) {
			format = DEFAULT_FORMAT;
		}

		StringBuilder lonRef = new StringBuilder(StringUtils.trimToEmpty(prefix));
		String branchCode = StringUtils.trimToEmpty(financeMain.getFinBranch());
		String swiftbranchCode = StringUtils.trimToEmpty(financeMain.getSwiftBranchCode());
		String productCode = StringUtils.trimToEmpty(financeMain.getFinCategory());
		String divisionCode = StringUtils.trimToEmpty(financeType.getFinDivision());
		String finType = StringUtils.trimToEmpty(financeType.getFinType());

		if (StringUtils.isNotBlank(format)) {
			String[] formetFileds = format.split("_");
			for (String formatCode : formetFileds) {

				if (formatCode.startsWith(BRANCH)) {
					appendLoanRef(lonRef, branchCode, formatCode);
				}

				if (ImplementationConstants.FINREFERENCE_ALW_SWIFT_CODE && formatCode.startsWith(SWIFT_BRANCH)) {
					appendLoanRef(lonRef, swiftbranchCode, formatCode);
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
					long referenceSeqNumber = sequenceGenetor.getNextValue("SeqFinReference");
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
		long referenceSeqNumber = sequenceGenetor.getNextValue("SeqFinReference");
		String sequence = StringUtils.leftPad(String.valueOf(referenceSeqNumber), 7, '0');

		logger.debug("Leaving");
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("LOAN_REFERENCE_IDENTIFIER"))) {
			return ReferenceConstants.DIVISION_IDENTIFIER.concat(branch).concat(product).concat(sequence);
		} else {
			return product.concat(branch).concat(sequence);
		}
	}

	/**
	 * Method for Generating Sequence Receipt Number based Sequence Object
	 * 
	 * @param isWIF
	 * @param finDivision
	 * @return
	 */
	public static String generateNewReceiptNo() {
		logger.debug("Entering");

		// Get the sequence number.
		long referenceSeqNumber = sequenceGenetor.getNextValue("SeqReceiptNumber");
		String rcptNo = String.valueOf(referenceSeqNumber);
		if (rcptNo.length() < 8) {
			rcptNo = StringUtils.leftPad(rcptNo, 8, '0');
		}

		logger.debug("Leaving");
		return rcptNo;
	}

	/**
	 * Method for Generating Sequence Service Unique ID based Sequence Object
	 * 
	 * @param isWIF
	 * @param finDivision
	 * @return
	 */
	public static String generateNewServiceUID() {
		logger.debug("Entering");

		// Get the sequence number.
		long servUIDSeqNumber = sequenceGenetor.getNextValue("SeqFinInstructionUID");
		String servUIDNo = String.valueOf(servUIDSeqNumber);
		logger.debug("Leaving");
		return servUIDNo;
	}

	public static String generateAPFSequence() {
		logger.debug("Entering");
		// Get the sequence number.
		long referenceSeqNumber = sequenceGenetor.getNextValue("SeqApfNumber");
		String sequence = StringUtils.leftPad(String.valueOf(referenceSeqNumber), 5, '0');
		logger.debug("Leaving");
		return sequence;
	}

	/**
	 * This method will return Agreement Password by using customer details
	 * 
	 * @param customer
	 * @return
	 */
	public static String generateAgreementPassword(Customer customer) {
		logger.debug(Literal.ENTERING);
		StringBuilder password = new StringBuilder("");
		if (customer != null && customer.getCustShrtName() != null) {
			String format = SysParamUtil.getValueAsString(SMTParameterConstants.PDF_PASSWORD_FORMAT);
			if (StringUtils.isBlank(format)) {
				format = DEFAULT_PWD_FORMAT;
			}
			String name = StringUtils.trimToEmpty(customer.getCustShrtName());
			name = StringUtils.upperCase(name);
			Date custDOB = customer.getCustDOB();
			if (StringUtils.isNotBlank(format)) {
				String[] formetFileds = format.split("_");
				for (String formatCode : formetFileds) {

					if (formatCode.startsWith(LETTER)) {
						appendName(password, name, formatCode);
					}
					if (formatCode.startsWith(DAY) && formatCode.endsWith(YEAR)) {
						String dob = DateUtil.format(custDOB, formatCode);
						password = password.append(dob);
					}
				}
			}
			return StringUtils.trimToEmpty(password.toString());
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private static void appendName(StringBuilder password, String name, String formatCode) {
		String tempRef = "";
		int length = formatCode.length();
		if (name.length() < length) {
			tempRef = name;
		} else {
			tempRef = name.substring(0, length);
		}
		password = password.append(tempRef);
	}

	public static void setSequenceGenetor(SequenceDao<?> sequenceGenetor) {
		ReferenceGenerator.sequenceGenetor = sequenceGenetor;
	}

}
