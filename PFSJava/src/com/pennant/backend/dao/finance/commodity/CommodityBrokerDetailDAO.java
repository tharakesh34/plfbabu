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
 * FileName    		:  CommodityBrokerDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.commodity;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;

public interface CommodityBrokerDetailDAO {

	public CommodityBrokerDetail getCommodityBrokerDetail();
	public CommodityBrokerDetail getNewCommodityBrokerDetail();
	public CommodityBrokerDetail getCommodityBrokerDetailById(String id,String type);
	public void update(CommodityBrokerDetail commodityBrokerDetail,String type);
	public void delete(CommodityBrokerDetail commodityBrokerDetail,String type);
	public String save(CommodityBrokerDetail commodityBrokerDetail,String type);
	public void initialize(CommodityBrokerDetail commodityBrokerDetail);
	public void refresh(CommodityBrokerDetail entity);
}