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
 * FileName    		:  FinCovenantTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.finance.FinCovenantType;
import com.pennanttech.pff.core.TableType;


public interface FinCovenantTypeDAO {

	FinCovenantType getFinCovenantType();
	FinCovenantType getNewFinCovenantType();
	FinCovenantType getFinCovenantTypeById(FinCovenantType finCovenantType, String type);
	void update(FinCovenantType finCovenantTypeDAO,String type);
	void delete(FinCovenantType finCovenantTypeDAO,String type);
	String save(FinCovenantType finCovenantTypeDAO,String type);
	List<FinCovenantType> getFinCovenantTypeByFinRef(String id, String type,boolean isEnquiry);
	List<FinCovenantType> getFinCovenantDocTypeByFinRef(String id, String type,boolean isEnquiry);
	void deleteByFinRef(String finReference, String tableType);
	boolean isDuplicateKey(String finReference, String covenantType, TableType tableType);
	void delete(FinCovenantType finCovenantType, TableType mainTab);
	String save(FinCovenantType aFinCovenantType, TableType tableType);
	void update(FinCovenantType aFinCovenantType, TableType tableType);
	FinCovenantType getCovenantTypeById(String finReference,String covenantType, String type);
	boolean isExists(FinCovenantType finCovenantType, String string);
}