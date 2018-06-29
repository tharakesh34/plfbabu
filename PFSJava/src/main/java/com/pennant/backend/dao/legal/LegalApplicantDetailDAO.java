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
 * FileName    		:  LegalApplicantDetailDAO.java                                                   * 	  
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

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.legal.LegalApplicantDetail;

public interface LegalApplicantDetailDAO extends BasicCrudDao<LegalApplicantDetail> {

	/**
	 * Fetch the Record LegalApplicantDetail by key field
	 * 
	 * @param legalApplicantId
	 *            legalApplicantId of the LegalApplicantDetail.
	 * @param legalId 
	 * @param tableType
	 *            The type of the table.
	 * @return LegalApplicantDetail
	 */
	LegalApplicantDetail getLegalApplicantDetail(long legalApplicantId, long legalId, String type);

	void deleteList(LegalApplicantDetail applicantDetail, String tableType);

	List<LegalApplicantDetail> getApplicantDetailsList(long legalId, String type);

}