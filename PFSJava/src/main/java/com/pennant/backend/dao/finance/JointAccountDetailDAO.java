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
 * * FileName : JointAccountDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;

public interface JointAccountDetailDAO {

	JointAccountDetail getJointAccountDetail();

	JointAccountDetail getNewJointAccountDetail();

	JointAccountDetail getJointAccountDetailById(long id, String type);

	void update(JointAccountDetail jointAccountDetail, String type);

	void delete(JointAccountDetail jointAccountDetail, String type);

	long save(JointAccountDetail jointAccountDetail, String type);

	JointAccountDetail getJointAccountDetailByRefId(long finID, long jointAccountId, String type);

	List<JointAccountDetail> getJointAccountDetailByFinRef(long finID);

	List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type);

	List<JointAccountDetail> getJointAccountDetailByFinRef(String finReference, String type);

	List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail);

	List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail);

	List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail);

	FinanceExposure getOverDueDetails(FinanceExposure exposure);

	JointAccountDetail getJointAccountDetailByRef(String finReference, String custCIF, String type);

	JointAccountDetail getJointAccountDetailByRef(long finID, String custCIF, String type);

	List<FinanceExposure> getPrimaryExposureList(List<String> listCIF);

	List<FinanceExposure> getSecondaryExposureList(List<String> listCIF);

	Map<String, Integer> getCustCtgCount(long finID);

	List<FinanceEnquiry> getCoApplicantsFin(String custCif);

	List<Long> getCustIdsByFinID(long finID);

	boolean isCoApplicant(long finID, String custCIF);
}