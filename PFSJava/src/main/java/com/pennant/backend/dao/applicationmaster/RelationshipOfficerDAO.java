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
 * FileName    		:  RelationshipOfficerDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster;
import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennanttech.pff.core.TableType;

public interface RelationshipOfficerDAO extends BasicCrudDao<RelationshipOfficer>{
	RelationshipOfficer getRelationshipOfficerById(String id, String type);
	
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param rOfficerCode
	 *              rOfficerCode of the relationshipOfficer
	 * @param tableType
	 *              The type of the table.
	 * @return  true if the record exists
	 */
	boolean isDuplicateKey(String rOfficerCode, TableType tableType);
}