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
 * FileName    		:  CommodityInventoryDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2015    														*
 *                                                                  						*
 * Modified Date    :  23-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.commodity;

import java.util.List;

import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.model.commodity.FinCommodityInventory;

public interface CommodityInventoryDAO {

	CommodityInventory getCommodityInventoryById(long id, String type);

	void update(CommodityInventory commodityInventory, String type);

	void delete(CommodityInventory commodityInventory, String type);

	long save(CommodityInventory commodityInventory, String type);

	List<FinCommodityInventory> getUsedCommInventory(String brokerCode, String holdCertificateNo);

	int getCommodityFinances(String brokerCode, String holdCertificateNo, String status);
	
	CommodityInventory  getCommodityDetails(String holdCertificateNo,String brokerCode);
	
	int getComInvCountByBrokerAndHoldCertNo(CommodityInventory commodityInventory, String type);

	List<String> getAllocateCmdList(String cmdSts, String type);


}