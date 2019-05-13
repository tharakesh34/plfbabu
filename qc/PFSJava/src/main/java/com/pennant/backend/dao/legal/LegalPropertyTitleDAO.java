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
 * FileName    		:  LegalPropertyTitleDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.legal.LegalPropertyTitle;

public interface LegalPropertyTitleDAO extends BasicCrudDao<LegalPropertyTitle> {

	/**
	 * Fetch the Record LegalPropertyTitle by key field
	 * 
	 * @param legalPropertyTitleId
	 *            legalPropertyTitleId of the LegalPropertyTitle.
	 * @param tableType
	 *            The type of the table.
	 * @return LegalPropertyTitle
	 */
	LegalPropertyTitle getLegalPropertyTitle(long legalPropertyTitleId, String type);

	void deleteList(LegalPropertyTitle legalPropertyTitle, String tableType);

	List<LegalPropertyTitle> getLegalPropertyTitleList(long legalId, String type);

}