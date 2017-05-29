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
 * FileName    		:  OverdueChargeDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rulefactory;
import java.util.List;

import com.pennant.backend.model.rulefactory.OverdueChargeDetail;

/**
 * DAO methods declaration for the <b>OverdueChargeDetail model</b> class.<br>
 * 
 */
public interface OverdueChargeDetailDAO {

	OverdueChargeDetail getOverdueChargeDetail();
	OverdueChargeDetail getNewOverdueChargeDetail();
	OverdueChargeDetail getOverdueChargeDetailById(String ruleCode,String ctgCode,String type);
	void update(OverdueChargeDetail overdueChargeDetail,String type);
	void delete(OverdueChargeDetail overdueChargeDetail,String type);
	String save(OverdueChargeDetail overdueChargeDetail,String type);
	List<OverdueChargeDetail> getListOverdueChargeDetailById(String id,String type);
}