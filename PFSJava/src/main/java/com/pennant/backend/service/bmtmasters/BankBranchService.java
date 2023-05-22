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
 * * FileName : BankBranchService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-10-2016 * * Modified
 * Date : 17-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.bmtmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;

public interface BankBranchService {

	BankBranch getBankBranch();

	BankBranch getNewBankBranch();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	BankBranch getBankBranchById(long id);

	BankBranch getApprovedBankBranchById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	BankBranch getBankBrachByIFSC(String ifsc);

	BankBranch getBankBrachByCode(String bankCode, String branchCode);

	BankBranch getBankBrachByMicr(String micr);

	int getAccNoLengthByIFSC(String ifscCode);

	BankBranch getBankBranchByIFSC(String ifsc);

	int getBankBranchCountByIFSC(String iFSC, String type);

	BankBranch getBankBranchByIFSCMICR(String iFSC, String micr);

	BankBranch getBankBranch(String iFSC, String micr, String bankCode, String branchCode);

	boolean validateBranchCode(BankBranch bankBranch, String mandateType);

	BankBranch getBankBrachDetails(String ifsc, String bankName);
}