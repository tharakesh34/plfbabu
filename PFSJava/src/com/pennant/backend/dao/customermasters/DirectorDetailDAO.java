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
 * FileName    		:  DirectorDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.customermasters.DirectorDetail;

/**
 * DAO methods declaration for the <b>DirectorDetail model</b> class.<br>
 * 
 */
public interface DirectorDetailDAO {

	public DirectorDetail getDirectorDetail();
	public DirectorDetail getNewDirectorDetail();
	public DirectorDetail getDirectorDetailById(final long id,final long custID,String type);
	public void update(DirectorDetail directorDetail,String type);
	public void delete(DirectorDetail directorDetail,String type);
	public long save(DirectorDetail directorDetail,String type);
	public void initialize(DirectorDetail directorDetail);
	public void refresh(DirectorDetail entity);
	public List<DirectorDetail> getCustomerDirectorByCustomer(long id, String type);
	public void delete(long custID, String tableType);
	
}