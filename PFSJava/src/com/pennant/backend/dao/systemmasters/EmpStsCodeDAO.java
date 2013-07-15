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
 * FileName    		:  EmpStsCodeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters;
import com.pennant.backend.model.systemmasters.EmpStsCode;

/**
 * DAO methods implementation for the <b>EmpStsCode model</b> class.<br>
 * 
 */
public interface EmpStsCodeDAO {

	public EmpStsCode getEmpStsCode();
	public EmpStsCode getNewEmpStsCode();
	public EmpStsCode getEmpStsCodeById(String id,String type);
	public void update(EmpStsCode empStsCode,String type);
	public void delete(EmpStsCode empStsCode,String type);
	public String save(EmpStsCode empStsCode,String type);
	public void initialize(EmpStsCode empStsCode);
	public void refresh(EmpStsCode entity);
}