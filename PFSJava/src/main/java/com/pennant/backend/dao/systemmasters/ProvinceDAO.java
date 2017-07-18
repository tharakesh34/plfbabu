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
 * FileName    		:  ProvinceDAO.java                                                   * 	  
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
import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.Province;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>Province model</b> class.<br>
 * 
 */
public interface ProvinceDAO extends BasicCrudDao<Province>  {

	Province getProvinceById(String cPCountry, String cPProvince,String type);
	String getSystemDefaultCount(String cpprovince);
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param phoneTypeCode
	 *            of PhoneType
	 * @param tableType
	 *            of PhoneType
	 * @return
	 */
	boolean isDuplicateKey(String cPCountry, String cPProvince, TableType tableType);
	boolean count(String taxStateCode,String cPProvince, TableType tableType);
	int getBusinessAreaCount(String stateCodeValue, String type);
	int geStateCodeCount(String taxStateCode, String cpProvince, String type);
}