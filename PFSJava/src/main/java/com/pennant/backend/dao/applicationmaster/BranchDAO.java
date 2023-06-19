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
 * * FileName : BranchDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date :
 * 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>Branch model</b> class.<br>
 */
public interface BranchDAO extends BasicCrudDao<Branch> {

	Branch getBranchById(String id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param branchCode branchCode of the branch.
	 * @param tableType  The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String branchCode, TableType tableType);

	void updateFinanceBranch(Branch branch, String type);

	void updateApplicationAccess(String sysParmName, String sysParmValue);

	boolean isPinCodeExists(String pinCode);

	List<Branch> getBrachDetailsByBranchCode(List<String> finBranches);

	boolean getUnionTerrotory(String cpProvince);

	String getBranchDesc(String id, String type);

	boolean isActiveBranch(String branch);

	List<String> getBranchCodeByClusterId(long clusterId);

	List<String> getBranchCodes(String entityCode, String clusterCode);

	List<String> getBranchCodesByEntity(String entityCode);

	List<String> getBranchCodesByClusterID(String entityCode, long clusterID);
}