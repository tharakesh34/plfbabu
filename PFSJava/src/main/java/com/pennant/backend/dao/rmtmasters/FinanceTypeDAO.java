/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified Date
 * : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.rmtmasters.FinanceType;

/**
 * DAO methods declaration for the <b>FinanceType model</b> class.<br>
 * 
 */
public interface FinanceTypeDAO {
	FinanceType getFinanceTypeByID(String id, String type);

	FinanceType getFinanceTypeByFinType(String finType);

	void update(FinanceType financeType, String type);

	void delete(FinanceType financeType, String type);

	String save(FinanceType financeType, String type);

	List<FinanceType> getFinTypeDetailForBatch();

	int getFinanceTypeCountById(String finType);

	List<FinanceType> getFinanceTypeByProduct(String productCode);

	String getAllowedCollateralTypes(String finType);

	int getPromotionTypeCountById(String finType);

	int getProductCountById(String productCode);

	String getFinanceTypeDesc(String productCode);

	FinanceType getOrgFinanceTypeByID(String finType, String type);

	int getFinTypeCount(String finType, String type);

	int getFinanceTypeByRuleCode(long ruleId, String type);

	boolean isStepPolicyExists(String policyCode);

	boolean isDivisionCodeExistsInFinanceTypes(String divisionCode, String type);

	boolean isCostOfFundsExist(String costOfFunds, String type);

	FinanceType getFinLtvCheckByFinType(String finType);

	String getAllowedRepayMethods(String finType, String type);

	List<FinanceType> getAutoRejectionDays();

	String getFinTypeByReference(String finref);

	FinanceType getFinanceType(String finType);

	String getRepayHierarchy(String finType);

	List<String> getAllowedOCRList();

	String getFinDivsion(String finType);

	List<String> getFinanceTypeList();

	boolean isAllowCancelFin(String finType);

	boolean isFinTypeExists(String finType);
}