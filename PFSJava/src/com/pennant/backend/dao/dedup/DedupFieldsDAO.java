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
 * FileName    		:  DedupFieldsDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.dedup;
import java.util.List;

import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.dedup.DedupFields;

public interface DedupFieldsDAO {

	public DedupFields getDedupFields();
	public DedupFields getNewDedupFields();
	public DedupFields getDedupFieldsByID(String id,String type);
	public void update(DedupFields dedupFields,String type);
	public void delete(DedupFields dedupFields,String type);
	public String save(DedupFields dedupFields,String type);
	public void initialize(DedupFields dedupFields);
	public void refresh(DedupFields entity);
	public ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters);
	List<BuilderTable> getFieldList(String queryModule);
	
}