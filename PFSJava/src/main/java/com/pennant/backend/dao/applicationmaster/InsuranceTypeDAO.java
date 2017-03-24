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
 * FileName    		:  InsuranceTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;

public interface InsuranceTypeDAO {
	InsuranceType getInsuranceTypeById(String id, String type);

	void update(InsuranceType insuranceType, String type);

	void delete(InsuranceType insuranceType, String type);

	String save(InsuranceType insuranceType, String type);

	List<InsuranceTypeProvider> getProvidersByInstype(String insurancetype, String type);

	InsuranceTypeProvider getInsTypeProvider(String insType, String providerCode, String type);

	void deleteList(String insurancetype, String type);

	void deleteByCode(String insType, String type);

	void saveList(InsuranceTypeProvider insTypeProvider, String type);

	void delete(InsuranceTypeProvider insTypeProvider, String type);
	
	 List<InsuranceTypeProvider> getInsuranceType(String providerCode, String type);
}