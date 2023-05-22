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
 * * FileName : BeneficiaryDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2016 * * Modified Date
 * : 01-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.beneficiary;

import java.util.List;

import com.pennant.backend.model.beneficiary.Beneficiary;

public interface BeneficiaryDAO {

	Beneficiary getBeneficiaryById(long id, String type);

	int getBeneficiaryByAccNo(Beneficiary beneficiary, String type);

	void update(Beneficiary beneficiary, String type);

	void delete(Beneficiary beneficiary, String type);

	long save(Beneficiary beneficiary, String type);

	List<Beneficiary> getApprovedBeneficiaryByCustomerId(long custID, String type);

	int getBeneficiaryByBankBranchId(String accNumber, long bankBranchId, String type);

	int getBranch(long bankBranchID, String type);

	int getDefaultsBeneficiary(long custID, long id, String type);

	boolean checkCustID(long custID);
}