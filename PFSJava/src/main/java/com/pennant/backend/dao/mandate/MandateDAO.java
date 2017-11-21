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
 * FileName    		:  MandateDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.mandate;

import java.util.List;

import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;

public interface MandateDAO {
	Mandate getMandate();

	Mandate getNewMandate();

	Mandate getMandateById(long id, String type);

	Mandate getMandateByStatus(long id, String status, String type);

	void update(Mandate mandate, String type);

	void delete(Mandate mandate, String type);

	long save(Mandate mandate, String type);

	void updateFinMandate(Mandate mandate, String type);

	void updateStatus(long mandateID, String mandateStatusAwaitcon, String mandateRef, String approvalId, String type);

	void updateActive(long mandateID, String status, boolean active);

	List<FinanceEnquiry> getMandateFinanceDetailById(long id);

	List<Mandate> getApprovedMandatesByCustomerId(long custID, String type);

	void updateOrgReferecne(long mandateID, String orgReference, String type);

	Mandate getMandateByOrgReference(String orgReference, String status, String type);

	int getBranch(long bankBranchID, String type);

	List<Mandate> getMnadateByCustID(long custID, long mandateID);
}