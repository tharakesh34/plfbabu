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
 * FileName    		:  ProductFinanceTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2011    														*
 *                                                                  						*
 * Modified Date    :  13-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-08-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.rmtmasters.ProductFinanceType;

public interface ProductFinanceTypeDAO {

	 ProductFinanceType getProductFinanceType();
	 ProductFinanceType getNewProductFinanceType();
	 ProductFinanceType getProductFinanceTypeById(long id, String type);
	 void update(ProductFinanceType productFinanceType, String type);
	 void delete(ProductFinanceType productFinanceType, String type);
	 long save(ProductFinanceType productFinanceType, String type);
	 void initialize(ProductFinanceType productFinanceType);
	 void refresh(ProductFinanceType entity);
	 List<ProductFinanceType> getFinanceType(String productCode,
			boolean selected, String type);
	 boolean checkFinanceType(String fintype,String type);
	 void deleteByProductCode(String productCode, String type);
	
		
}