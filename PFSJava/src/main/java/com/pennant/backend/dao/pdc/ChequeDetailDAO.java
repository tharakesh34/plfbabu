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
 * * FileName : ChequeDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified Date
 * : 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.pdc;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface ChequeDetailDAO {

	ChequeDetail getChequeDetail(long headerID, String type);

	List<ChequeDetail> getChequeDetailList(long headerID, String type);

	boolean isDuplicateKey(long chequeID, long branchID, String accountNo, String chequeSerial, TableType type);

	String save(ChequeDetail cheque, TableType type);

	void update(ChequeDetail cheque, TableType type);

	void delete(ChequeDetail cheque, TableType type);

	void batchUpdateChequeStatus(List<Long> detailIDs, String status);

	int updateChequeStatus(List<PresentmentDetail> presentments);

	void updateChequeStatus(long detailID, String status);

	boolean isChequeExists(long headerID, Date chequeDate);

	boolean isRelisedAllCheques(long finId);

	Long getChequeDetailID(long finID);

	Long getChequeDetailIDByAppDate(long finID, Date appDate);

	PaymentInstruction getBeneficiary(long id);

	void deleteCheques(ChequeDetail cheque);

	String getChequeStatus(String chequeSerial, String accountNo);

	boolean isDuplicateKeyPresent(String accountNo, String chequeSerial, TableType type);

	List<ChequeDetail> getChequeDetailsByFinReference(String finReference, String type);
}