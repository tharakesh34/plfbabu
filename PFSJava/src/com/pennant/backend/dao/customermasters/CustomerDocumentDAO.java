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
 * FileName    		:  CustomerDocumentDAO.java                                                   * 	  
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

import com.pennant.backend.model.customermasters.CustomerDocument;

/**
 * DAO methods declaration for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public interface CustomerDocumentDAO {

	public CustomerDocument getCustomerDocument();
	public CustomerDocument getNewCustomerDocument();
	public CustomerDocument getCustomerDocumentById(long id,String docType,String type);
	public List<CustomerDocument> getCustomerDocumentByCustomer(final long id,String type);
	public void update(CustomerDocument customerDocument,String type);
	public void delete(CustomerDocument customerDocument,String type);
	public void deleteByCustomer(long custId,String type);
	public long save(CustomerDocument customerDocument,String type);
	public void initialize(CustomerDocument customerDocument);
	public void refresh(CustomerDocument entity);
}