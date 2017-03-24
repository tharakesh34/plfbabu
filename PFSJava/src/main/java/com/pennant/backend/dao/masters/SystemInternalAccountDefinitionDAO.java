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
 * FileName    		:  SystemInternalAccountDefinitionDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.masters;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;

public interface SystemInternalAccountDefinitionDAO {

	SystemInternalAccountDefinition getSystemInternalAccountDefinitionById(String id,String type);
	void update(SystemInternalAccountDefinition systemInternalAccountDefinition,String type);
	void delete(SystemInternalAccountDefinition systemInternalAccountDefinition,String type);
	String save(SystemInternalAccountDefinition systemInternalAccountDefinition,String type);
	String getSysIntAccNum(String sIACode);
	List<ValueLabel> getEntrySIANumDetails();
	List<SystemInternalAccountDefinition> getSysIntAccNumList(List<String> subHeadRules);
}