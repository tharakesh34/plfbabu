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
 * FileName    		:  ChequeHeaderDAO.java                                                 * 	  
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

import com.pennant.backend.model.finance.ChequeHeader;
import com.pennanttech.pff.core.TableType;

public interface ChequeHeaderDAO {
	
	ChequeHeader getChequeHeader(long headerId, String type);
	
	ChequeHeader getChequeHeader(String finReference, String type);

	String save(ChequeHeader chequeHeader, TableType tableType);

	void update(ChequeHeader chequeHeader, TableType tableType);

	void delete(ChequeHeader chequeHeader, TableType tableType);
	
	void deleteByFinRef(String finRef, TableType tableType);
	
	boolean isDuplicateKey(long headerID, String finRef, TableType tableType);

	ChequeHeader getChequeHeaderByRef(String finReference, String type);
}