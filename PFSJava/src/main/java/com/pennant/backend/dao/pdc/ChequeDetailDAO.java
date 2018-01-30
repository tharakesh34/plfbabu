/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ChequeDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.pdc;

import java.util.List;

import com.pennant.backend.model.finance.ChequeDetail;
import com.pennanttech.pff.core.TableType;

public interface ChequeDetailDAO {

	ChequeDetail getChequeDetail(long headerID, String type);

	boolean isDuplicateKey(long chequeDetailsID, long bankBranchID, String accountNo, int chequeSerialNo,
			TableType tableType);

	void delete(ChequeDetail chequeDetail, TableType tableType);

	String save(ChequeDetail chequeDetail, TableType tableType);

	void update(ChequeDetail chequeDetail, TableType tableType);

	List<ChequeDetail> getChequeDetailList(long headerID, String type);

	void deleteByCheqID(final long headerID, String type);

}