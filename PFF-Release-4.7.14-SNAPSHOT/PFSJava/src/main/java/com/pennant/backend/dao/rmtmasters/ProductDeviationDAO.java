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
 * FileName    		:  ProductDeviationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.bmtmasters.ProductDeviation;

/**
 * DAO methods declaration for the <b>ProductAsset model</b> class.<br>
 * 
 */
public interface ProductDeviationDAO {

	ProductDeviation getProductDeviation();

	ProductDeviation getNewProductDeviation();

	ProductDeviation getProductDeviationById(long id, String type);

	void update(ProductDeviation productDeviation, String type);

	void delete(ProductDeviation productDeviation, String type);

	void deleteByProduct(String prodCode, String type);

	long save(ProductDeviation productDeviation, String type);

	List<ProductDeviation> getProductDeviationByProdCode(final String prodCode, String type);
	
	boolean isExistsDeviationID(long deviationID, String type);

}
