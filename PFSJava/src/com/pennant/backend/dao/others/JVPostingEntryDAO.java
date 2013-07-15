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
 * FileName    		:  JVPostingEntryDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.others;
import java.util.List;

import com.pennant.backend.model.others.JVPostingEntry;

public interface JVPostingEntryDAO {

	public JVPostingEntry getJVPostingEntry();
	public JVPostingEntry getNewJVPostingEntry();
	public JVPostingEntry getJVPostingEntryById(String id, String txnReference, String type);
	public void update(JVPostingEntry jVPostingEntry,String type);
	public void delete(JVPostingEntry jVPostingEntry,String type);
	public String save(JVPostingEntry jVPostingEntry,String type);
	public void initialize(JVPostingEntry jVPostingEntry);
	public void refresh(JVPostingEntry entity);
	public void deleteByBatchRef(String batchReference, String tableType);
	public List<JVPostingEntry> getJVPostingEntryListById(String id, String type);

}