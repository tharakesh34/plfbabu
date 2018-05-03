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
 * FileName    		:  BankBranchDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.bmtmasters;

import com.pennant.backend.model.bmtmasters.BankBranch;

public interface BankBranchDAO {
	BankBranch getBankBranch();

	BankBranch getNewBankBranch();

	BankBranch getBankBranchById(long id, String type);

	int getBankBranchByIFSC(String iFSC, long id, String type);

	void update(BankBranch bankBranch, String type);

	void delete(BankBranch bankBranch, String type);

	long save(BankBranch bankBranch, String type);

	BankBranch getBankBrachByIFSC(String ifsc, String type);

	BankBranch getBankBrachByCode(String bankCode, String branchCode, String type);

	int getBankBrachByBank(String bankCode, String type);

	BankBranch getBankBrachByMicr(String micr, String type);
	
	int getBankBranchByMICR(final String mICR,long id, String type);
}