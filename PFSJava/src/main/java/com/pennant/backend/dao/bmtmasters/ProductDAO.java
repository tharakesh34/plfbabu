/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ProductDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-08-2011 * * Modified Date :
 * 12-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters;

import com.pennant.backend.model.bmtmasters.Product;

/**
 * DAO methods declaration for the <b>Product model</b> class.<br>
 * 
 */
public interface ProductDAO {

	Product getProductByID(String id, String code, String type);

	void update(Product product, String type);

	void delete(Product product, String type);

	String save(Product product, String type);

	String getProductCtgByProduct(String productCode);

	Product getProductByProduct(String code);
}