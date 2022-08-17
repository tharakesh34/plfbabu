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
 * * FileName : GSTDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennanttech.pff.core.TableType;

public interface GSTDetailDAO {
	GSTDetail getGSTDetailByID(long id, String typeCode, String type);

	void update(GSTDetail gstDetail, String type);

	void delete(GSTDetail gstDetail, String type);

	long save(GSTDetail gstDetail, String type);

	void deleteByCustomer(final long id, String type);

	List<GSTDetail> getGSTDetailById(long id, String type);

	boolean isDuplicateKey(GSTDetail gstDetail, TableType tableType);

	GSTDetail getDefaultGSTDetailById(long id, String type);
}