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
 * FileName    		:  FinanceReferenceDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import java.util.List;

import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface FinanceReferenceDetailDAO {

	FinanceReferenceDetail getFinanceReferenceDetail();
	FinanceReferenceDetail getNewFinanceReferenceDetail();
	FinanceReferenceDetail getFinanceReferenceDetailById(long id,String type);
	void update(FinanceReferenceDetail financeReferenceDetail,String type);
	void delete(FinanceReferenceDetail financeReferenceDetail,String type);
	long save(FinanceReferenceDetail financeReferenceDetail,String type);
	void initialize(FinanceReferenceDetail financeReferenceDetail);
	void refresh(FinanceReferenceDetail entity);
	List<FinanceReferenceDetail> getFinanceReferenceDetail(String financeType,String roleCode, String type);
	List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(String financeType,
			String mandInputInStage, List<String> groupIds, String type);
	void deleteByFinType(String finType, String type);
	List<Long> getMailTemplatesByFinType(String financeType, String roleCode);
	List<FinanceReferenceDetail> getAgreementListByCode(String aggCodes);
}