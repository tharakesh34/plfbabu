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
 * FileName    		:  MasterDefServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-05-2018    														*
 *                                                                  						*
 * Modified Date    :  19-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-05-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.masters.impl;

import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.service.masters.MasterDefService;

/**
 * Service implementation for methods that depends on <b>MasterDef</b>.<br>
 * 
 */
public class MasterDefServiceImpl implements MasterDefService {

	private MasterDefDAO masterDefDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public MasterDefDAO getMasterDefDAO() {
		return masterDefDAO;
	}

	public void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		this.masterDefDAO = masterDefDAO;
	}

	/**
	 * getMasterCode fetch the details by using MasterDefDAO getMasterCode method.
	 * @param masterType (String)
	 * @param  keytype (String)
	 * 			
	 * @return KeyCode (String)
	 */
	@Override
	public String getMasterCode(String masterType, String keytype) {
		return masterDefDAO.getMasterCode(masterType, keytype);
	}

	/**
	 * getMasterCode fetch the details by using MasterDefDAO getMasterCode method.
	 * 
	 * @param masterType (String)
	 *            
	 * @param KeyCode (String)
	 * 
	 * @return keyType (String)
	 */
	@Override
	public String getMasterKeyTypeByCode(String masterType, String keyCode) {
		return masterDefDAO.getMasterKeyTypeByCode(masterType, keyCode);
	}
}
