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
 * FileName    		:  PFSParameterDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.smtmasters;
import java.util.List;

import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.smtmasters.PFSParameter;

/**
 * DAO methods declaration for the <b>PFSParameter model</b> class.<br>
 * 
 */
public interface PFSParameterDAO {

	public PFSParameter getPFSParameter();
	public PFSParameter getNewPFSParameter();
	public PFSParameter getPFSParameterById(String id,String type);
	public void update(PFSParameter pFSParameter,String type);
	public void delete(PFSParameter pFSParameter,String type);
	public String save(PFSParameter pFSParameter,String type);
	public void initialize(PFSParameter pFSParameter);
	public void refresh(PFSParameter entity);
	public List<PFSParameter> getAllPFSParameter(); 
	public List<GlobalVariable> getGlobaVariables();
	
}