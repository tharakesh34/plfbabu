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
 * * FileName : MandateDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date :
 * 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.mandate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface MandateDAO {

	Mandate getMandateById(Long id, String type);

	Mandate getMandateByFinReference(String finReference, String type);

	Mandate getMandateByStatus(long id, String status, String type);

	void update(Mandate mandate, String type);

	void delete(Mandate mandate, String type);

	long save(Mandate mandate, String type);

	void updateFinMandate(Mandate mandate, String type);

	void updateStatus(long mandateID, String mandateStatusAwaitcon, String mandateRef, String approvalId, String type);

	void updateActive(long mandateID, String status, boolean active);

	List<FinanceEnquiry> getMandateFinanceDetailById(long id);

	List<Mandate> getApprovedMandatesByCustomerId(long custID, String type);

	void updateOrgReferecne(long mandateID, String orgReference, String type);

	Mandate getMandateByOrgReference(String orgReference, boolean isSecurityMandate, String status, String type);

	int getBranch(long bankBranchID, String type);

	List<Mandate> getMnadateByCustID(long custID, long mandateID);

	void updateStatusAfterRegistration(long mandateID, String statusInprocess);

	boolean checkMandateStatus(long mandateID);

	boolean checkMandates(String orgReference, long mandateid, boolean securityMandate);

	int getSecondaryMandateCount(long mandateID);

	int getBarCodeCount(String barCode, long mandateID, String type);

	BigDecimal getMaxRepayAmount(Long mandateID);

	BigDecimal getMaxRepayAmount(String finReference);

	boolean entityExistMandate(String entityCode, String type);

	Mandate getMandateStatusById(String finReference, Long mandateID);

	int getMandateCount(long custID, long mandateID);

	int validateEmandateSource(String eMandateSource);

	int updateMandateStatus(Mandate mandate);

	int getMandateByMandateRef(String mandateRef);

	public List<PresentmentDetail> getPresentmentDetailsList(String finreference, long mandateID, String status);

	List<Mandate> getLoans(long custId, String finRepayMethod);

	Mandate getEmployerDetails(long custID);

	Mandate getLoanInfo(String finReference);

	Mandate getLoanInfo(String orgReference, long custID);

	List<Long> getFinanceMainbyCustId(long custId);

	void holdMandate(long mandateId, String reason);

	void unHoldMandate(long mandateId);

	boolean isValidMandate(Long id);

	long getCustID(Long id);

	Mandate getMandateDetail(long mandateID);

	List<Mandate> getMandatesForAutoSwap(long custID, Date appDate);

	List<Mandate> getMandatesForAutoSwap(long finID);

	PaymentInstruction getBeneficiary(long mandateId);

	Long getMandateId(long finID);

	PaymentInstruction getBeneficiaryForSI(Long mandateId);

	String getMandateTypeById(Long mandateId, String string);

	void updateFinMandateId(Long mandateId, String finreference);

	Long getSecurityMandateIdByRef(String finreference);

	String getMandateStatus(long mandateId);

	int getMandateType(long mandateId, String mandatetype, String reference);

	String getMandateNumber(Long mandateId);
}