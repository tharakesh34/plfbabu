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
 * FileName    		:  StepPolicyDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.solutionfactory;


import java.util.List;

import com.pennant.backend.model.solutionfactory.StepPolicyDetail;


/**
 * DAO methods declaration for the <b>StepPolicyDetail model</b> class.<br>
 * 
 */
public interface StepPolicyDetailDAO {

	public StepPolicyDetail getStepPolicyDetail();
	public StepPolicyDetail getNewStepPolicyDetail();
	public StepPolicyDetail getStepPolicyDetailByID(StepPolicyDetail stepPolicyDetail, String type); 
	public List<StepPolicyDetail> getStepPolicyDetailListByID(final String id, String type); 
	public void update(StepPolicyDetail stepPolicyDetail, String type);
	public String save(StepPolicyDetail stepPolicyDetail, String type);
	public void delete(StepPolicyDetail stepPolicyDetail, String type);
	public void deleteByPolicyCode(String finType, String type); 
	public void initialize(StepPolicyDetail stepPolicyDetail);
	public void refresh(StepPolicyDetail entity);

}