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
 * * FileName : InstrumentwiseLimitDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-01-2018 * *
 * Modified Date : 18-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennanttech.pff.core.TableType;

public interface InstrumentwiseLimitDAO extends BasicCrudDao<InstrumentwiseLimit> {

	InstrumentwiseLimit getInstrumentwiseLimit(long id, String type);

	boolean isDuplicateKey(long id, String instrumentMode, TableType tableType);

	InstrumentwiseLimit getInstrumentWiseModeLimit(String paymentMode, String type);

}