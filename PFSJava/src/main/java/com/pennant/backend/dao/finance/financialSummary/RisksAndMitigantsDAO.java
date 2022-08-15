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
 * * FileName : CustomerPhoneNumberDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary;

import java.util.List;

import com.pennant.backend.model.finance.financialsummary.DueDiligenceCheckList;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennanttech.logging.model.InterfaceLogDetail;

/**
 * DAO methods declaration for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public interface RisksAndMitigantsDAO {

	void update(RisksAndMitigants risksAndMitigants, String type);

	void delete(RisksAndMitigants risksAndMitigants, String type);

	long save(RisksAndMitigants risksAndMitigants, String type);

	int getVersion(long id, String typeCode);

	List<RisksAndMitigants> getRisksAndMitigants(String finReference);

	List<DueDiligenceCheckList> getDueDiligenceCheckListDetails();

	List<InterfaceLogDetail> getInterfaceLogDetails(String finReference);

}