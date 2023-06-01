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
 * * FileName : FinFeeDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified Date
 * : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;

public interface FinFeeDetailDAO {

	FinFeeDetail getFinFeeDetailById(FinFeeDetail finFeeDetail, boolean isWIF, String type);

	void update(FinFeeDetail finFeeDetailDAO, boolean isWIF, String type);

	void delete(FinFeeDetail finFeeDetailDAO, boolean isWIF, String type);

	long save(FinFeeDetail finFeeDetailDAO, boolean isWIF, String type);

	void refresh(FinFeeDetail entity);

	List<FinFeeDetail> getFinFeeDetailByFinRef(String reference, boolean isWIF, String type);

	List<FinFeeDetail> getFinFeeDetailByFinRef(long finID, boolean isWIF, String type);

	int getFeeSeq(FinFeeDetail finFeeDetail, boolean isWIF, String type);

	List<FinFeeDetail> getFinScheduleFees(long finID, boolean isWIF, String type);

	List<FinFeeDetail> getFinFeeDetailByFinRef(long finID, boolean isWIF, String type, String finEvent);

	List<FinFeeDetail> getPaidFinFeeDetails(String reference, String type);

	FinFeeDetail getVasFeeDetailById(String vasReference, boolean isWIF, String type);

	void statusUpdate(long feeID, String status, boolean isWIF, String type);

	void updateTaxPercent(UploadTaxPercent taxPercent);

	long getFinFeeTypeIdByFeeType(String feeTypeCode, long finID, String type);

	List<FinFeeDetail> getFeeDetailByExtReference(String loanReference, long feeTypeId, String tableType);

	List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type);

	List<FinFeeDetail> getDMFinFeeDetailByFinRef(long finID, String type);

	boolean isFinTypeFeeExists(long feeTypeId, String finType, int moduleId, boolean originationFee);

	public List<FinFeeDetail> getPreviousAdvPayments(long finID);

	List<FinFeeDetail> getFeeDetails(long finID, String feetypeCode, List<String> finEvents);

	List<FinFeeDetail> getFinFeeDetailByReferenceId(long referenceId, String finEvent, String type);

	/**
	 * Method for update the paid and remaining fee details.
	 * 
	 * @param finFeeDetail
	 * @param type
	 */
	void updateFeesFromUpfront(FinFeeDetail finFeeDetail, String type);

	void deleteByTransactionId(String transactionId, boolean isWIF, String tableType);

	boolean isFinFeeDetailExists(FinFeeDetail finFeeDetail, String tableType);

	List<FinFeeDetail> getTotalPaidFees(String reference, String type);

	FinFeeDetail getFinFeeDetail(long feeID);
}