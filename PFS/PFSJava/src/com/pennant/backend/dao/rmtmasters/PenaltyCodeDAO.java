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
 * FileName    		:  PenaltyCodeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;
import com.pennant.backend.model.rmtmasters.PenaltyCode;

/**
 * DAO methods declaration for the <b>PenaltyCode model</b> class.<br>
 * 
 */
public interface PenaltyCodeDAO {

	public PenaltyCode getPenaltyCode();
	public PenaltyCode getNewPenaltyCode();
	public PenaltyCode getPenaltyCodeById(String id,String type);
	public void update(PenaltyCode penaltyCode,String type);
	public void delete(PenaltyCode penaltyCode,String type);
	public String save(PenaltyCode penaltyCode,String type);
	public void initialize(PenaltyCode penaltyCode);
	public void refresh(PenaltyCode entity);
}