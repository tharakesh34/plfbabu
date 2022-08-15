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
 * * FileName : SecurityGroupDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 2-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 2-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennanttech.pff.core.TableType;

public interface SecurityGroupDAO {

	SecurityGroup getSecurityGroupById(long id, String type);

	void update(SecurityGroup securityGroup, String type);

	void delete(SecurityGroup securityGroup, String type);

	long save(SecurityGroup securityGroup, String type);

	SecurityGroup getSecurityGroupByCode(final String grpCode, String type);

	boolean isDuplicateKey(String grpCode, TableType tableType);
}