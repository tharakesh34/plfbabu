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
 * * FileName : FinTypePartnerBankDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennanttech.pff.core.TableType;

public interface FinTypePartnerBankDAO extends BasicCrudDao<FinTypePartnerBank> {

	FinTypePartnerBank getFinTypePartnerBank(String finType, long iD, TableType tableType);

	void deleteByFinType(String finType, TableType tableType);

	List<FinTypePartnerBank> getFinTypePartnerBanks(String finType, TableType tableType);

	int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID);

	int getAssignedPartnerBankCount(long partnerBankId, TableType type);

	FinTypePartnerBank getFinTypePartnerBankByPartnerBankCode(String partnerBankCode, String finType,
			String paymentMode);

	List<FinTypePartnerBank> getByFinTypeAndPurpose(FinTypePartnerBank fab);

	public List<Long> getClusterByPartnerbankCode(long partnerbankId);

	public List<FinTypePartnerBank> getFintypePartnerBankByBranch(List<String> branchCode, Long clusterId);

	int getPartnerBankCountByCluster(FinTypePartnerBank fpb);

	List<FinTypePartnerBank> getFinTypePartnerBanks(FinTypePartnerBank fab, TableType tableType);

}