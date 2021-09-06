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
 * * FileName : FinCovenantTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.systemmasters.DocumentType;

public interface FinCovenantTypeDAO {

	FinCovenantType getFinCovenantTypeById(FinCovenantType covenantType, String type);

	void update(FinCovenantType covenantType, String type);

	void delete(FinCovenantType covenantType, String type);

	String save(FinCovenantType covenantType, String type);

	List<FinCovenantType> getFinCovenantTypeByFinRef(String finReference, String type, boolean isEnquiry);

	List<FinCovenantType> getFinCovenantDocTypeByFinRef(String finReference, String type, boolean isEnquiry);

	void deleteByFinRef(String finReference, String tableType);

	FinCovenantType getCovenantTypeById(String finReference, String covenantType, String type);

	boolean isExists(FinCovenantType covenantType, String string);

	DocumentType isCovenantTypeExists(String covenantType);

	SecurityRole isMandRoleExists(String mandRole, String[] allowedRoles);

	List<DocumentType> getPddOtcList();
}