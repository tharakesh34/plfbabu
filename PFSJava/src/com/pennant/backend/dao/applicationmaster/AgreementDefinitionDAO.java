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
 * FileName    		:  AgreementDefinitionDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.model.applicationmaster.AgreementDefinition;

public interface AgreementDefinitionDAO {

	public AgreementDefinition getAgreementDefinition();

	public AgreementDefinition getNewAgreementDefinition();

	public AgreementDefinition getAgreementDefinitionByCode(String aggCode, String type);

	public AgreementDefinition getAgreementDefinitionById(long id, String type);

	public void update(AgreementDefinition agreementDefinition, String type);

	public void delete(AgreementDefinition agreementDefinition, String type);

	public long save(AgreementDefinition agreementDefinition, String type);

	public void initialize(AgreementDefinition agreementDefinition);

	public void refresh(AgreementDefinition entity);
	
}