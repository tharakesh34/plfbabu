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
 * * FileName : CustomerAddresDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * * Modified
 * Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerAddres;

/**
 * DAO methods declaration for the <b>CustomerAddres model</b> class.<br>
 */
public interface CustomerAddresDAO {

	CustomerAddres getCustomerAddresById(long id, String addType, String type);

	List<CustomerAddres> getCustomerAddresByCustomer(final long id, String type);

	void update(CustomerAddres customerAddres, String type);

	void delete(CustomerAddres customerAddres, String type);

	long save(CustomerAddres customerAddres, String type);

	void deleteByCustomer(final long id, String type);

	int getAddrTypeCount(String addType);

	int getVersion(long id, String addrType);

	int getcustAddressCount(String addrType);

	boolean isServiceable(long pinCodeId);

	CustomerAddres getHighPriorityCustAddr(long id, String type);

	String getCustHighPriorityAddr(long id);

	boolean isExisiCustPincode(long id);

	CustomerAddres getCustomerAddresById(long id, long priority);
}