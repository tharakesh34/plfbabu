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
 * * FileName : FinanceTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified Date
 * : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.pff.noc.dao;

import java.util.List;

import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface LoanTypeLetterMappingDAO {

	LoanTypeLetterMapping getLetterMapping(LoanTypeLetterMapping letterMapping, String type);

	List<LoanTypeLetterMapping> getLetterMapping(String finType);

	void update(LoanTypeLetterMapping letterMapping, String type);

	long save(LoanTypeLetterMapping letterMapping, String type);

	void delete(LoanTypeLetterMapping letterMapping, String type);

	void delete(String finType, String tableType);

	boolean isDuplicateKey(String finType, TableType tableType);

	boolean isExistLetterType(String letterType, TableType tableType);

	List<LoanTypeLetterMapping> getLoanTypeLetterMapping(List<String> roleCodes);

	List<LoanTypeLetterMapping> getResult(ISearch search);
}