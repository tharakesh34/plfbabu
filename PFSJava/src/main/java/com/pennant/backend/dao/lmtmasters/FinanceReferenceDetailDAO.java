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
 * 13-06-2018       Siva					 0.2        Stage Accounting Modifications      * 
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
import java.util.Map;

import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;

public interface FinanceReferenceDetailDAO {

	FinanceReferenceDetail getFinanceReferenceDetail();
	FinanceReferenceDetail getNewFinanceReferenceDetail();
	FinanceReferenceDetail getFinanceReferenceDetailById(long id,String type);
	void update(FinanceReferenceDetail financeReferenceDetail,String type);
	void delete(FinanceReferenceDetail financeReferenceDetail,String type);
	long save(FinanceReferenceDetail financeReferenceDetail,String type);
	List<FinanceReferenceDetail> getFinanceReferenceDetail(String financeType, String finEvent,String roleCode, String type);
	List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(String financeType, String finEvent,
			String mandInputInStage, List<String> groupIds, String type);
	void deleteByFinType(String finType,String finEvent, String type);
	List<Long> getRefIdListByFinType(String financeType, String finEvent, String roleCode, String type);
	List<FinanceReferenceDetail> getFinanceProcessEditorDetails(String financeType, String finEvent, String type);
	List<FinanceReferenceDetail> getAgreementListByCode(String aggCodes);
	Map<Long, String> getTemplateIdList(String financeType, String finEvent, String roleCode, List<String> lovCodeList);
	FinanceReferenceDetail getTemplateId(String financeType,String finEvent,String roleCode, String lovCodeList);
	List<FinanceReferenceDetail> getFinanceRefListByFinType(String product, String type);
	
	void saveHandlInstructionDetails(HandlingInstruction handlingInstruction);
	
	FinCollaterals getFinCollaterals(String finReference, String collateralType);
	int getFinanceReferenceDetailByRuleCode(long ruleId, String type);
	String getAllowedRolesByCode(String finType, int finRefType, String quickDisbCode);
// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

	String getWorkflowType(String finType, String finEvent, String module);

	long getWorkflowIdByType(String workflowType);

	long getLimitIdByLimitCode(String limitCode);

	String authorities(String finType, int procedtLimit, long limitid);
// ### 06-05-2018 - End 
	
	List<Long> getRefIdListByRefType(String financeType, String finEvent, String roleCode, int finRefType);

	boolean resendNotification(String finType, String finEvent, String role, List<String> templateTyeList);
}