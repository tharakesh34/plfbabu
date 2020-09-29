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
 * FileName    		:  ProvisionDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement;

import java.util.List;

import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennanttech.pff.core.TableType;

public interface ProvisionDAO {

	Provision getProvisionById(long id, TableType type, boolean isMovement);

	Provision getProvisionById(String finReference, TableType type, boolean isMovement);

	void update(Provision provision, TableType type);

	void delete(Provision provision, TableType type);

	long save(Provision provision, TableType type);

	void saveAmounts(List<ProvisionAmount> provisionAmounts, TableType mainTab, boolean isMovement);

	boolean isProvisionExists(String finReference, TableType type);

	Provision getProvision();

	void deleteAmounts(long provisionId, TableType mainTab);

	List<ProvisionAmount> getProvisionAmounts(long id, TableType type);

	void updateAmounts(List<ProvisionAmount> provisionAmounts, TableType type);

	long saveMovements(Provision provision, TableType tableType);

}