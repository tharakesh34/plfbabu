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
 * FileName    		:  ProvisionMovementDAO.java                                                   * 	  
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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.financemanagement.ProvisionMovement;

public interface ProvisionMovementDAO {

	public ProvisionMovement getProvisionMovement();
	public ProvisionMovement getNewProvisionMovement();
	public ProvisionMovement getProvisionMovementById(String id, Date movementDate ,String type);
	public List<ProvisionMovement> getProvisionMovementListById(String id ,String type);
	public void update(ProvisionMovement provisionMovement,String type);
	public void delete(ProvisionMovement provisionMovement,String type);
	public String save(ProvisionMovement provisionMovement,String type);
	public void initialize(ProvisionMovement provisionMovement);
	public void refresh(ProvisionMovement entity);
}