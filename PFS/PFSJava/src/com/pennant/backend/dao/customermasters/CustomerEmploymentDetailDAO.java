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
 * FileName    		:  CustomerEmploymentDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.customermasters;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;

/**
 * DAO methods declaration for the <b>CustomerEmploymentDetail model</b> class.<br>
 * 
 */
public interface CustomerEmploymentDetailDAO {

	public CustomerEmploymentDetail getCustomerEmploymentDetail();
	public CustomerEmploymentDetail getNewCustomerEmploymentDetail();
	public CustomerEmploymentDetail getCustomerEmploymentDetailByID(long id,String type);
	public void update(CustomerEmploymentDetail customerEmploymentDetail,String type);
	public void delete(CustomerEmploymentDetail customerEmploymentDetail,String type);
	public long save(CustomerEmploymentDetail customerEmploymentDetail,String type);
	public void initialize(CustomerEmploymentDetail customerEmploymentDetail);
	public void refresh(CustomerEmploymentDetail entity);
	public CustomerEmploymentDetail isEmployeeExistWithCustID(long id,String type);
	
}