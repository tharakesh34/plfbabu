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

import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;

public interface FinTaxUploadDetailDAO {

	List<FinTaxUploadDetail> getFinTaxDetailUploadById(String reference,String type);

	void update(FinTaxUploadHeader finTaxUploadHeader, String type);

	void delete(FinTaxUploadHeader finTaxUploadHeader, String type);

	void save(FinTaxUploadHeader finTaxUploadHeader, String type);

	void saveFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type);

	void updateFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type);

	void deleteFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type);
	
	FinTaxUploadHeader getFinTaxUploadHeaderByRef(long ref,String type);
	
}
