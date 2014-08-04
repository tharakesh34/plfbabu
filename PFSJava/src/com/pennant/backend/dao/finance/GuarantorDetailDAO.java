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

import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;

public interface GuarantorDetailDAO {

	public GuarantorDetail getGuarantorDetail();
	public GuarantorDetail getNewGuarantorDetail();
	public GuarantorDetail getGuarantorDetailById(long id, String type);
	public void update(GuarantorDetail guarantorDetail, String type);
	public void delete(GuarantorDetail guarantorDetail, String type);
	public long save(GuarantorDetail guarantorDetail, String type);
	public void initialize(GuarantorDetail guarantorDetail);
	public void refresh(GuarantorDetail entity);
	public GuarantorDetail getGuarantorDetailByRefId(String finReference, String CIF, String type);
	public void deleteByFinRef(String finReference, String type);
	public List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String type);	
	public List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail);
	public List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail);
	public List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail);
	public FinanceExposure getOverDueDetails(FinanceExposure exposure);
	public GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail);
	
	

}