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
 * FileName    		:  WIFFinanceDisbursementDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.finance.FinanceDisbursement;

public interface WIFFinanceDisbursementDAO {

	FinanceDisbursement getWIFFinanceDisbursement();
	FinanceDisbursement getNewWIFFinanceDisbursement();
	FinanceDisbursement getWIFFinanceDisbursementById(String id,String type);
	void update(FinanceDisbursement wIFFinanceDisbursement,String type);
	void delete(FinanceDisbursement wIFFinanceDisbursement,String type);
	String save(FinanceDisbursement wIFFinanceDisbursement,String type);
	void initialize(FinanceDisbursement wIFFinanceDisbursement);
	void refresh(FinanceDisbursement entity);
	List<FinanceDisbursement> getWIFFinanceDisbursementDetails(String id, String type);
}