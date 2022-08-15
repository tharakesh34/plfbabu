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
 * * FileName : UploadHeaderDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified Date
 * : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;

public interface ReceiptResponseDetailDAO {

	long saveReceiptResponseFileHeader(String procName);

	List<ReceiptUploadDetail> getReceiptResponseDetails();

	List<UploadAlloctionDetail> getReceiptResponseAllocationDetails(String rootId);

	void updateReceiptResponseFileHeader(long batchId, int recordCount, int sCount, int fCount, String remarks);

	void updateReceiptResponseDetails(ReceiptUploadDetail receiptresponseDetail, long jobid);

	void updateReceiptResponseId(String rootId, long receiptID);

	void updatePickBatchId(long jobid);

}