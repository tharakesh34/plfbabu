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
package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;
import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface TechnicalVerificationDAO extends BasicCrudDao<TechnicalVerification> {

	TechnicalVerification getTechnicalVerification(long id, String type);

	List<TechnicalVerification> getList(String keyReference);

	void saveCollateral(String reference, String collateralType, long verificationId);

	List<TechnicalVerification> getList(String[] custCif);

	List<TechnicalVerification> getTvListByCollRef(String collRef);

	List<Verification> getTvValuation(List<Long> verificationIDs, String type);

	void updateValuationAmount(Verification verification, TableType tableType);

	Map<String, Object> getCostOfPropertyValue(String collRef, String subModuleName, String column);

	String getPropertyCity(String collRef, String subModuleName);

	public String getCollaterlType(long id);

	List<Verification> getTvListByCollRefAndFinRef(String collRef, String finRef);
}
