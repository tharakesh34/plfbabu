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
 * * FileName : CustomerBankInfoDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * * Modified
 * Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;

/**
 * DAO methods declaration for the <b>CustomerBankInfo model</b> class.<br>
 * 
 */
public interface CustomerBankInfoDAO {

	/* CustomerBankInfo getCustomerBankInfoById(long id,String typeCode,String type); */
	CustomerBankInfo getCustomerBankInfoById(long id, String type);

	List<CustomerBankInfo> getBankInfoByCustomer(final long id, String type);

	void update(CustomerBankInfo customerBankInfo, String type);

	void delete(CustomerBankInfo customerBankInfo, String type);

	void deleteByCustomer(long custID, String type);

	long save(CustomerBankInfo customerBankInfo, String type);

	int getBankCodeCount(String bankCode);

	int getAccTypeCount(String accType);

	int getVersion(long id);

	int getCustomerBankInfoByCustBankName(long custId, String bankName, String accountNumber, long bankId, String type);

	CustomerBankInfo getCustomerBankInfoByCustId(CustomerBankInfo customerBankInfo, String type);

	int getCustomerBankInfoByBank(String bankCode, String type);

	CustomerBankInfo getSumOfAmtsCustomerBankInfoByCustId(Set<Long> custId);

	List<BankInfoDetail> getBankInfoDetailById(long id, Date monthYear, String type);

	List<BankInfoDetail> getBankInfoDetailById(long id, String type);

	void save(BankInfoDetail bankInfoDetail, String type);

	void update(BankInfoDetail bankInfoDetail, String type);

	void delete(BankInfoDetail bankInfoDetail, String type);

	List<BankInfoSubDetail> getBankInfoSubDetailById(long id, Date monthYear, int day, String type);

	List<BankInfoSubDetail> getBankInfoSubDetailById(long id, Date monthYear, String type);

	void save(List<BankInfoSubDetail> bankInfoSubDetails, String type);

	void update(BankInfoSubDetail bankInfoSubDetail, String type);

	void delete(List<BankInfoSubDetail> bankInfoSubDetails, String type);
}