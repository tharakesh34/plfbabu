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
 * * FileName : GuarantorDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * * Modified
 * Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;

public interface GuarantorDetailDAO {

	GuarantorDetail getGuarantorDetail();

	GuarantorDetail getNewGuarantorDetail();

	GuarantorDetail getGuarantorDetailById(long id, String type);

	void update(GuarantorDetail guarantorDetail, String type);

	void delete(GuarantorDetail guarantorDetail, String type);

	long save(GuarantorDetail guarantorDetail, String type);

	GuarantorDetail getGuarantorDetailByRefId(long finID, long guarantorId, String type);

	List<GuarantorDetail> getGuarantorDetailByFinRef(long finID, String type);

	List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail);

	List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail);

	List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail);

	GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail);

	List<FinanceEnquiry> getGuarantorsFin(String custCIF, String type);

	boolean isGuarantor(long finID, String custCIF);
}