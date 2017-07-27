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
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;

/**
 * DAO methods declaration for the <b>CustomerEmploymentDetail model</b> class.<br>
 * 
 */
public interface CustomerEmploymentDetailDAO {
	// CustomerEmploymentDetail getCustomerEmploymentDetailByID(long id,long custEmpName,String type);
	 CustomerEmploymentDetail getCustomerEmploymentDetailByCustEmpId(long custEmpId,String type);
	 void update(CustomerEmploymentDetail customerEmploymentDetail,String type);
	 void delete(CustomerEmploymentDetail customerEmploymentDetail,String type);
	 long save(CustomerEmploymentDetail customerEmploymentDetail,String type);
	 CustomerEmploymentDetail isEmployeeExistWithCustID(long id,String type);
	 List<CustomerEmploymentDetail> getCustomerEmploymentDetailsByID(long id, String type);
	 void deleteByCustomer(long custID, String tableType);
	 int getVersion(long custID, long custEmpId);
	 int getCustomerEmploymentByCustEmpName(long id,long custEmpName,long custEmpId, String type);
	
}