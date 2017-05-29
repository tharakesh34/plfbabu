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
 * FileName    		:  FinContributorDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-01-2013    														*
 *                                                                  						*
 * Modified Date    :  				   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.finance.FinContributorDetail;

/**
 * DAO methods declaration for the <b>FinContributorDetail model</b> class.<br>
 * 
 */
public interface FinContributorDetailDAO {

	FinContributorDetail getFinContributorDetailByID(String finReference,long id,String type);
	List<FinContributorDetail> getFinContributorDetailByFinRef(final String id,String type);
	void update(FinContributorDetail contributorDetail,String type);
	void delete(FinContributorDetail contributorDetail,String type);
	long save(FinContributorDetail contributorDetail,String type);
	void deleteByFinRef(final String finReference,String type);
}