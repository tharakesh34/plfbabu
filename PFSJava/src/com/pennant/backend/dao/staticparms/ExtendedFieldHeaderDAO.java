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
 * FileName    		:  ExtendedFieldHeaderDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.staticparms;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;

public interface ExtendedFieldHeaderDAO {

	public ExtendedFieldHeader getExtendedFieldHeader();
	public ExtendedFieldHeader getNewExtendedFieldHeader();
	public ExtendedFieldHeader getExtendedFieldHeaderById(long id,String type);
	public void update(ExtendedFieldHeader extendedFieldHeader,String type);
	public void delete(ExtendedFieldHeader extendedFieldHeader,String type);
	public long save(ExtendedFieldHeader extendedFieldHeader,String type);
	public void initialize(ExtendedFieldHeader extendedFieldHeader);
	public void refresh(ExtendedFieldHeader entity);
	public ExtendedFieldHeader getExtendedFieldHeaderByModuleName(String moduleName, String subModuleName, String type);
	
}