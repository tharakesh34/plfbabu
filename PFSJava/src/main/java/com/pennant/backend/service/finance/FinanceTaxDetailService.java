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
 * * FileName : FinanceTaxDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * *
 * Modified Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public interface FinanceTaxDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	FinanceTaxDetail getFinanceTaxDetail(long finID);

	FinanceTaxDetail getApprovedFinanceTaxDetail(long finID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<GuarantorDetail> getGuarantorDetailByFinRef(long finID, String type);

	List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type);

	FinanceMain getFinanceDetailsForService(long finID, String type, boolean isWIF);

	Customer getCustomerByID(long id);

	AuditDetail gstNumbeValidation(AuditDetail auditDetail, FinanceTaxDetail financeTaxDetail);

	boolean isFinReferenceExitsinLQ(long finID, TableType tempTab, boolean wif);// ### 17-07-2018 - Ticket ID : 127950

	List<ErrorDetail> doGSTValidations(FinanceTaxDetail financeTaxDetail);

	List<ErrorDetail> verifyCoApplicantDetails(FinanceTaxDetail financeTaxDetail);

	int getFinanceTaxDetailsByCount(long finID);

	CustomerAddres getHighPriorityCustAddr(long id);

}