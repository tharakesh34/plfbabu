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

package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface TechnicalVerificationService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<Long> getTechnicalVerificaationIds(List<Verification> verifications, String keyRef);

	TechnicalVerification getApprovedTechnicalVerification(long id);

	TechnicalVerification getTechnicalVerification(long id, String type);

	void save(Verification item);

	void save(TechnicalVerification technicalVerification, TableType tempTab);

	void saveCollateral(String reference, String collateralType, long verificationId);

	List<TechnicalVerification> getList(String keyReference);

	boolean isCollateralChanged(Verification verification, TableType tempTab);

	TechnicalVerification getVerificationFromRecording(long verificationId);

	List<Verification> getTvValuation(List<Long> verificationIDs, String type);

	Map<String, Object> getCostOfPropertyValue(String collRef, String subModuleName, String docvalue);

	public void getDocumentImage(TechnicalVerification tv);

	String getPropertyCity(String collRef, String subModuleName);

	public String getCollaterlType(long id);

	public AuditDetail validateTVCount(FinanceDetail financeDetail);

}
