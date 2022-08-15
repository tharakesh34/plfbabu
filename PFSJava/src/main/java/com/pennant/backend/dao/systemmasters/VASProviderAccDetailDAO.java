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
 * * FileName : VASProviderAccDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-09-2018 * *
 * Modified Date : 24-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennanttech.pff.core.TableType;

public interface VASProviderAccDetailDAO extends BasicCrudDao<VASProviderAccDetail> {

	/**
	 * Fetch the Record VASProviderAccDetail by key field
	 * 
	 * @param id        id of the VASProviderAccDetail.
	 * @param tableType The type of the table.
	 * @return VASProviderAccDetail
	 */
	VASProviderAccDetail getVASProviderAccDetail(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id          id of the VASProviderAccDetail.
	 * @param providerId  providerId of the VASProviderAccDetail.
	 * @param paymentMode paymentMode of the VASProviderAccDetail.
	 * @param tableType   The type of the table.
	 * @return true if the record exists.
	 */

	boolean isDuplicateKey(long id, long providerId, String entityCode, TableType tableType);

	VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode, String type);

	VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String type);
}