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
 * FileName    		:  ProductAssetDAO.java                                                   * 	  
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

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.rmtmasters.ProductAsset;

/**
 * DAO methods declaration for the <b>ProductAsset model</b> class.<br>
 * 
 */
public interface ProductAssetDAO {

	 ProductAsset getProductAsset();
	 ProductAsset getNewProductAsset();
	 ProductAsset getProductAssetById(long id,String type);
	 void update(ProductAsset productAsset,String type);
	 void delete(ProductAsset productAsset,String type);
	 void deleteByProduct(ProductAsset productAsset,String type);
	 long save(ProductAsset productAsset,String type);
	 List<ProductAsset> getProductAssetByProdCode(final String prodCode,String type);
	 List<ProductAsset>  getFinPurposeByAssetId(ArrayList<String> list,String type);
}
