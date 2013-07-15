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
 * FileName    		:  CustomerRatingDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.customermasters.CustomerRating;

/**
 * DAO methods declaration for the <b>CustomerRating model</b> class.<br>
 * 
 */
public interface CustomerRatingDAO {

	public CustomerRating getCustomerRating();
	public CustomerRating getNewCustomerRating();
	public CustomerRating getCustomerRatingByID(long id,String ratingType,String type);
	public List<CustomerRating> getCustomerRatingByCustomer(final long id,String type);
	public void update(CustomerRating customerRating,String type);
	public void delete(CustomerRating customerRating,String type);
	public long save(CustomerRating customerRating,String type);
	public void initialize(CustomerRating customerRating);
	public void refresh(CustomerRating entity);
	public void deleteByCustomer(final long customerId,String type);
	
}