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
 * * FileName : CustomerEMailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * * Modified
 * Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerEMail;

/**
 * DAO methods declaration for the <b>CustomerEMail model</b> class.<br>
 * 
 */
public interface CustomerEMailDAO {
	CustomerEMail getCustomerEMailById(long id, String typeCode, String type);

	List<CustomerEMail> getCustomerEmailByCustomer(final long id, String type);

	void update(CustomerEMail customerEMail, String type);

	void delete(CustomerEMail customerEMail, String type);

	void deleteByCustomer(long custID, String type);

	long save(CustomerEMail customerEMail, String type);

	List<String> getCustEmailsByCustId(long custId);

	int getEMailTypeCount(String typeCode);

	int getVersion(long id, String typeCode);

	List<CustomerEMail> getCustIDByEmail(String email, String type);

	List<String> getCustEmailsByCustomerId(long custId);

	List<CustomerEMail> getCustomerEMailById(long id, long mailPriority);

	List<CustomerEMail> getCustomerEMailById(long id, String typeCode);
}