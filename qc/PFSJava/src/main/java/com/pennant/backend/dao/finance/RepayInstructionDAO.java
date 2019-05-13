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
 * FileName    		:  RepayInstructionDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.RepayInstruction;

public interface RepayInstructionDAO {

	RepayInstruction getRepayInstructionById(String id, String type, boolean isWIF);

	void update(RepayInstruction repayInstruction, String type, boolean isWIF);

	void deleteByFinReference(String id, String type, boolean isWIF, long logKey);

	String save(RepayInstruction repayInstruction, String type, boolean isWIF);

	List<RepayInstruction> getRepayInstructions(String id, String type, boolean isWIF);

	List<RepayInstruction> getRepayInstructions(String id, String type, boolean isWIF, long logKey);

	void delete(RepayInstruction repayInstruction, String type, boolean isWIF);

	void saveList(List<RepayInstruction> repayInstruction, String type, boolean isWIF);

	void updateList(List<RepayInstruction> repayInstruction, String type, boolean isWIF);

	//EOD
	List<RepayInstruction> getRepayInstrEOD(String id);

	void deleteInEOD(String id);

	void saveListInEOD(List<RepayInstruction> repayInstruction);
}