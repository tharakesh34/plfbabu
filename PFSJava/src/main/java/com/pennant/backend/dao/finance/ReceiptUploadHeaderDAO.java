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
import java.util.Map;

import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennanttech.pff.core.TableType;

public interface ReceiptUploadHeaderDAO {

	boolean isFileNameExist(String fileName);

	void delete(ReceiptUploadHeader receiptUploadHeader, TableType tempTab);

	long save(ReceiptUploadHeader receiptUploadHeader, TableType mainTab);

	int update(ReceiptUploadHeader receiptUploadHeader, TableType mainTab);

	ReceiptUploadHeader getReceiptHeaderById(long uploadHeaderId, String string);

	void uploadHeaderStatusCnt(long uploadHeaderId, int sucessCount, int failedCount);

	long updateUploadProgress(long id, int receiptDownloaded);

	boolean isFileDownlaoded(long id, int receiptDownloaded);

	long generateSeqId();

	List<Long> getHeaderStatusCnt(long uploadHeaderId);

	Map<Long, ReceiptUploadLog> createAttempLog(List<ReceiptUploadHeader> uploadHeaderList);

	void updateAttemptLog(ReceiptUploadLog uploadAttemptLog);

	long setHeaderAttempNo(long id);

}