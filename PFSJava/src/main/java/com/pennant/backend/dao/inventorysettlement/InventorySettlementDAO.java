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
 * FileName    		:  InventorySettlementDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.inventorysettlement;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.model.inventorysettlement.InventorySettlementDetails;

public interface InventorySettlementDAO {
	InventorySettlement getInventorySettlement();

	InventorySettlement getNewInventorySettlement();

	InventorySettlement getInventorySettlementById(long id, String type);

	void update(InventorySettlement inventorySettlement, String type);

	void delete(InventorySettlement inventorySettlement, String type);

	long save(InventorySettlement inventorySettlement, String type);

	List<InventorySettlementDetails> getSettlementsByBroker(String brokerCode, Date settlementDate);

	void saveInventorySettelmentDetails(List<InventorySettlementDetails> details);
}