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
 * * FileName : BankDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified Date :
 * 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>BankDetail model</b> class.<br>
 * 
 */
public interface BankDetailDAO extends BasicCrudDao<BankDetail> {

	BankDetail getBankDetailById(String id, String type);

	BankDetail getBankDetailByIfsc(String ifsc);

	boolean isDuplicateKey(String bankCode, TableType tableType);

	BankDetail getAccNoLengthByCode(String bankCode, String type);

	String getBankCodeByName(String bankName);

	boolean isBankCodeExits(String bankCode, String type, boolean active);

	boolean isBankCodeExits(String bankCode);

	BankDetail getAccNoLengths(String bankCode);
}