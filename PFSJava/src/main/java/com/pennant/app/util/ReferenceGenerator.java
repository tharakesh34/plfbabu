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
import com.pennant.backend.util.ReferenceConstants;

public class ReferenceGenerator implements Serializable {
    private static final long serialVersionUID = -4965488291173350445L;
	private static Logger logger = Logger.getLogger(ReferenceGenerator.class);
	
	private static NextidviewDAO nextidviewDAO;

	/**
	 * Method for Generating Sequence Reference Number based on Branch and product code
	 * @param isWIF
	 * @param finDivision
	 * @return
	 */
	public static String generateNewFinRef(boolean isWIF, FinanceMain financeMain) {
		logger.debug("Entering");

		// Product code
		String branch = financeMain.getSwiftBranchCode();

		if (branch.length() < 3) {
			branch = StringUtils.leftPad(branch, 3, '0');
		} else {
			branch = branch.substring(0, 3);
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
			return branch.concat(product).concat(sequence);
		}
	}

	public static void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		ReferenceGenerator.nextidviewDAO = nextidviewDAO;
	}

}
