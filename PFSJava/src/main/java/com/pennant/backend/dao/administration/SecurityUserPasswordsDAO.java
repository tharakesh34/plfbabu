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
 * * FileName : SecurityUser***DAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityUser;

public interface SecurityUserPasswordsDAO {

	/**
	 * Saves the record.
	 * 
	 * @param securityUser The model object that contains the parameters.
	 * @return Identity of the record.
	 */
	long save(SecurityUser securityUser);

	/**
	 * Gets the history of changes for the specified user in descending order.
	 * 
	 * @param secUser The model object that contains the parameters.
	 * @return List of changes for the specified user with the recent change at the top.
	 */
	List<SecurityUser> getUserPreviousPasswords(SecurityUser secUser);

	/**
	 * Deletes the record.
	 * 
	 * @param securityUser The model object that contains the parameters.
	 */
	void delete(SecurityUser securityUser);
}
