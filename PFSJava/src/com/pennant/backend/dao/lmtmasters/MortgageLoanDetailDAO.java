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
 * FileName    		:  MortgageLoanDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;

/**
 * DAO methods declaration for the <b>MortgageLoanDetail model</b> class.<br>
 */
public interface MortgageLoanDetailDAO {

	MortgageLoanDetail getMortgageLoanDetail();
	MortgageLoanDetail getNewMortgageLoanDetail();
	MortgageLoanDetail getMortgageLoanDetailById(String id,String type);
	void update(MortgageLoanDetail mortgageLoanDetail,String type);
	void delete(MortgageLoanDetail mortgageLoanDetail,String type);
	String save(MortgageLoanDetail mortgageLoanDetail,String type);
	void initialize(MortgageLoanDetail mortgageLoanDetail);
	void refresh(MortgageLoanDetail entity);
}