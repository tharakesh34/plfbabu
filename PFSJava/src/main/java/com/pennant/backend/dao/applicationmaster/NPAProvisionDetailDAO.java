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
 * * FileName : NPAProvisionDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennanttech.pff.core.TableType;

public interface NPAProvisionDetailDAO extends BasicCrudDao<NPAProvisionDetail> {

	List<NPAProvisionDetail> getNPAProvisionDetailList(long id, TableType type);

	void deleteProvisionList(long id, TableType tableType);

	NPAProvisionDetail getNPAProvisionDetail(long headerId, TableType tempTab);

	void saveList(List<NPAProvisionDetail> npaProvisionDetailsList, TableType tableType);

}