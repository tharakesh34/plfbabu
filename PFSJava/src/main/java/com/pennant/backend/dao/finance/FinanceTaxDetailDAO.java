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
 * * FileName : FinanceTaxDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * * Modified
 * Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennanttech.pff.core.TableType;

public interface FinanceTaxDetailDAO extends BasicCrudDao<FinanceTaxDetail> {

	FinanceTaxDetail getFinanceTaxDetail(long finID, String type);

	int getGSTNumberCount(long taxCustId, String taxNumber, String string);

	boolean isReferenceExists(long finID, String custCif);

	void deleteFinTaxDetails(FinanceTaxDetail td, TableType tableType);

	int getFinTaxDetailsCount(long finID);

	List<FinanceTaxDetail> getGSTNumberAndCustCIF(long taxCustId, String taxNumber, String type);

	FinanceTaxDetail getFinanceTaxDetailForLMSEvent(long finID);

}