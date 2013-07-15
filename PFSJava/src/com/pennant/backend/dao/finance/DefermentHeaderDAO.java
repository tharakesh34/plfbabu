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

	public DefermentHeader getDefermentHeader(boolean isWIF);
	public DefermentHeader getNewDefermentHeader(boolean isWIF);
	public DefermentHeader getDefermentHeaderById(String id,String type,boolean isWIF);
	public void update(DefermentHeader defermentHeader,String type,boolean isWIF);
	public void deleteByFinReference(String id,String type,boolean isWIF);
	public String save(DefermentHeader defermentHeader,String type,boolean isWIF);
	public void initialize(DefermentHeader defermentHeader);
	public void refresh(DefermentHeader entity);
	List<DefermentHeader> getDefermentHeaders(String id, String type,boolean isWIF);
	public void delete(DefermentHeader defermentHeader,String type,boolean isWIF);
	public int getRpyDfrCount(String finReference);
	public void saveList(List<DefermentHeader> defermentHeader, String type, boolean isWIF);
	public void updateList(List<DefermentHeader> defermentHeader, String type, boolean isWIF);
}