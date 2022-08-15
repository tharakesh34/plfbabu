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
 * * FileName : RatingCodeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified Date :
 * 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters;

import com.pennant.backend.model.bmtmasters.RatingCode;

/**
 * DAO methods declaration for the <b>RejectDetail model</b> class.<br>
 * 
 */
public interface RatingCodeDAO {

	RatingCode getRatingCodeById(String ratingType, String ratingCode, String type);

	void update(RatingCode ratingCode, String type);

	void delete(RatingCode ratingCode, String type);

	String save(RatingCode ratingCode, String type);
}