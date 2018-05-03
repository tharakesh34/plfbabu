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
 * FileName    		:  SukukBrokerBondsDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;

public interface SukukBrokerBondsDAO {
	SukukBrokerBonds getSukukBrokerBonds();

	SukukBrokerBonds getNewSukukBrokerBonds();

	SukukBrokerBonds getSukukBrokerBondsById(String id, String bondCode, String type);

	SukukBrokerBonds getUniqueBrokerByBond(String bondCode, String type);

	void update(SukukBrokerBonds sukukBrokerBonds, String type);

	void delete(SukukBrokerBonds sukukBrokerBonds, String type);

	String save(SukukBrokerBonds sukukBrokerBonds, String type);

	List<SukukBrokerBonds> getSukukBrokerBondsByCode(String id, String type);

	void deleteBySukukBrokerCode(String brokerCode, String type);
}
