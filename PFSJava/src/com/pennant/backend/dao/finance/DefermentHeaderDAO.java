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
 * FileName    		:  DefermentHeaderDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;
import java.util.List;

import com.pennant.backend.model.finance.DefermentHeader;

public interface DefermentHeaderDAO {

	DefermentHeader getDefermentHeader(boolean isWIF);
	DefermentHeader getNewDefermentHeader(boolean isWIF);
	DefermentHeader getDefermentHeaderById(String id,String type,boolean isWIF);
	void update(DefermentHeader defermentHeader,String type,boolean isWIF);
	void deleteByFinReference(String id,String type,boolean isWIF, long logKey);
	String save(DefermentHeader defermentHeader,String type,boolean isWIF);
	void initialize(DefermentHeader defermentHeader);
	void refresh(DefermentHeader entity);
	List<DefermentHeader> getDefermentHeaders(String id, String type,boolean isWIF);
	List<DefermentHeader> getDefermentHeaders(String id, String type,boolean isWIF, long logKey);
	void delete(DefermentHeader defermentHeader,String type,boolean isWIF);
	int getRpyDfrCount(String finReference);
	void saveList(List<DefermentHeader> defermentHeader, String type, boolean isWIF);
	void updateList(List<DefermentHeader> defermentHeader, String type, boolean isWIF);
}