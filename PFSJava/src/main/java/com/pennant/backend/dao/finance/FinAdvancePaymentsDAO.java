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
 * * FileName : FinAdvancePaymentsDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.PaymentInstruction;

public interface FinAdvancePaymentsDAO {

	FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type);

	int getAdvancePaymentsCountByPartnerBank(long partnerBankID, String type);

	void update(FinAdvancePayments finAdvancePaymentsDAO, String type);

	void updateLinkedTranId(FinAdvancePayments finAdvancePaymentsDAO);

	void delete(FinAdvancePayments finAdvancePaymentsDAO, String type);

	String save(FinAdvancePayments finAdvancePaymentsDAO, String type);

	List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(long finID, String type);

	void deleteByFinRef(long finID, String tableType);

	void updateStatus(FinAdvancePayments finAdvancePayments, String type);

	int getBranch(long bankBranchID, String type);

	void update(long paymentId, long linkedTranId);

	int updateDisbursmentStatus(FinAdvancePayments finAdvancePayments);

	int getBankCode(String bankCode, String type);

	int getMaxPaymentSeq(long finID);

	int getFinAdvCountByRef(long finID, String type);

	int getAssignedPartnerBankCount(long partnerBankId, String type);

	int getCountByFinReference(long finID);

	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(long finID, Date toDate, String type);

	void updatePaymentStatus(FinAdvancePayments finAdvancePayments, String type);

	int getCountByPaymentId(long finID, long paymentId);

	int getFinAdvanceByVasRef(long finID, String vasReference, String type);

	void updateLLDate(FinAdvancePayments finAdvancePayments, String type);

	FinAdvancePayments getFinAdvancePaymentsById(long paymentId);

	int getStatusCountByFinRefrence(long finID);

	PaymentInstruction getBeneficiary(long finId);

	PaymentInstruction getBeneficiaryByPrintLoc(long finID);

	List<String> getFinAdvancePaymentsStatus(long finID);
}