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
 * FileName    		:  PartnerBankDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.partnerbank;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennanttech.pff.core.TableType;

public interface PartnerBankDAO extends BasicCrudDao<PartnerBank> {
	PartnerBank getPartnerBankById(long id, String type);
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param PartnerBankCode
	 *            PartnerBankCode of the partnerBank.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long partnerBankId,String PartnerBankCode, TableType tableType);
	void saveList(List<PartnerBankModes> list,long id);
	void updateList(List<PartnerBankModes> list);
	void deletePartner(PartnerBank partnerBankModes);
	List<PartnerBankModes> getPartnerBankModesId(long partnerBankId) ;
	int geBankCodeCount(String partnerBankCodeValue, String type);
	List<PartnerBranchModes> getPartnerBranchModesId(long id);
	void deletePartnerBranch(PartnerBank partnerBank);
	void saveBranchList(List<PartnerBranchModes> partnerBranchModesList, long partnerBankId);
}