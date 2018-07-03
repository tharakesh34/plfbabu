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
 * FileName    		:  LegalDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennanttech.pff.core.TableType;

public interface LegalDetailDAO extends BasicCrudDao<LegalDetail> {

	/**
	 * Fetch the Record LegalDetail by key field
	 * 
	 * @param legalReference
	 *            legalReference of the LegalDetail.
	 * @param tableType
	 *            The type of the table.
	 * @return LegalDetail
	 */
	LegalDetail getLegalDetail(long legalReference, String type);

	/**
	 * Checks whether another record exists with the key attributes in the
	 * specified table type.
	 * 
	 * @param legalReference
	 *            legalReference of the LegalDetail.
	 * @param loanReference
	 *            loanReference of the LegalDetail.
	 * @param collaterialReference
	 *            collaterialReference of the LegalDetail.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long legalReference, String loanReference, String collaterialReference, TableType tableType);

	boolean isExists(String reference, String collateralRef, String type);

	void updateLegalDeatils(String reference, String collateralRef, boolean active);

	boolean isExists(String finReference, TableType mainTab);

}