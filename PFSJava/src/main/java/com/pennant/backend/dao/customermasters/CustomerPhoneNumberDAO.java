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
 * * FileName : CustomerPhoneNumberDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

/**
 * DAO methods declaration for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public interface CustomerPhoneNumberDAO {
	CustomerPhoneNumber getCustomerPhoneNumberByID(long id, String typeCode, String type);

	List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(final long id, String type);

	void update(CustomerPhoneNumber customerPhoneNumber, String type);

	void delete(CustomerPhoneNumber customerPhoneNumber, String type);

	long save(CustomerPhoneNumber customerPhoneNumber, String type);

	void deleteByCustomer(final long id, String type);

	List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomerPhoneType(final long id, String type, String phoneType);

	List<CustomerPhoneNumber> getCustomerPhoneNumberById(long id, String type);

	int getVersion(long id, String typeCode);

	int getPhoneTypeCodeCount(String phoneTypeCode);

	List<CustomerPhoneNumber> getCustIDByPhoneNumber(String phoneNumber, String type);

	String getCustomerPhoneNumberByCustId(long custID);

	List<Customer> getCustomersByPhoneNum(String phoneNum);

	CustomerPhoneNumber getCustomerPhoneNumberByID(long id, long phoneTypePriority);
}