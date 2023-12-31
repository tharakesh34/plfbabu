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
 * * FileName : BlackListCustomerDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified
 * Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>BlackListCustomers model</b> class.<br>
 * 
 */
public interface BlackListCustomerDAO extends BasicCrudDao<BlackListCustomers> {

	void saveList(List<FinBlacklistCustomer> finBlackList, String type);

	List<FinBlacklistCustomer> fetchOverrideBlackListData(String finReference, String queryCode, String sourceCIF);

	List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers blCustData, String watchRule);

	void updateList(List<FinBlacklistCustomer> finBlackList);

	void deleteList(long finID);

	List<FinBlacklistCustomer> fetchFinBlackList(long finID);

	BlackListCustomers getBlackListCustomers();

	BlackListCustomers getNewBlacklistCustomer();

	BlackListCustomers getBlacklistCustomerById(String id, String type);

	void moveData(String finReference, String type);

	boolean isDuplicateKey(String custCIF, TableType tableType);

	void deleteNegativeReasonList(String blacklisCIF, TableType tableType);

	void deleteNegativeReason(long reasonId, TableType tableType);

	void saveNegativeReason(NegativeReasoncodes negativeReasoncodes, TableType tableType);

	List<NegativeReasoncodes> getNegativeReasonList(String blacklisCIF, String type);

}