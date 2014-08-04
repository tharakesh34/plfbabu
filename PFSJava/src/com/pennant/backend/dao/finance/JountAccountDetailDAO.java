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
 * * FileName : JountAccountDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
public interface JountAccountDetailDAO {

	public JointAccountDetail getJountAccountDetail();
	public JointAccountDetail getNewJountAccountDetail();
	public JointAccountDetail getJountAccountDetailById(long id, String type);
	public void update(JointAccountDetail jountAccountDetail, String type);
	public void delete(JointAccountDetail jountAccountDetail, String type);
	public long save(JointAccountDetail jountAccountDetail, String type);
	public void initialize(JointAccountDetail jountAccountDetail);
	public void refresh(JointAccountDetail entity);
	public JointAccountDetail getJountAccountDetailByRefId(String finReference, String custCIF, String type);
	public void deleteByFinRef(String finReference, String type);
	public List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type);	
	public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail);
	public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail);
	public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail);
	public FinanceExposure getOverDueDetails(FinanceExposure exposure);
}