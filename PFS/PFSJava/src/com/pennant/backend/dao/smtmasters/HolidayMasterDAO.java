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
 * FileName    		:  HolidayMasterDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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
import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.smtmasters.HolidayMaster;

/**
 * DAO methods declaration for the <b>HolidayMaster model</b> class.<br>
 * 
 */
public interface HolidayMasterDAO {

	public HolidayMaster getHolidayMaster();
	public HolidayMaster getNewHolidayMaster();
	public HolidayMaster getHolidayMasterByID(final String id,final BigDecimal year,final String holidayType,String type);
	public void update(HolidayMaster holidayMaster,String type);
	public void delete(HolidayMaster holidayMaster,String type);
	public String save(HolidayMaster holidayMaster,String type);
	public void initialize(HolidayMaster holidayMaster);
	public void refresh(HolidayMaster entity);
	//public ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters);
	public List<HolidayMaster> getHolidayMasterCodeYear(final String holidayCode,final BigDecimal year,String type);
	public List<HolidayMaster> getHolidayMasterCode(String holidayCode);
	
}