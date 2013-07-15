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
 * FileName    		:  FinanceCampaignDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.solutionfactory;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;

public interface FinanceCampaignDAO {

	public FinanceCampaign getFinanceCampaign();
	public FinanceCampaign getNewFinanceCampaign();
	public FinanceCampaign getFinanceCampaignById(String id,String type);
	public void update(FinanceCampaign financeCampaign,String type);
	public void delete(FinanceCampaign financeCampaign,String type);
	public String save(FinanceCampaign financeCampaign,String type);
	public void initialize(FinanceCampaign financeCampaign);
	public void refresh(FinanceCampaign entity);
}