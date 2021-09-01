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

import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.UploadReceipt;
import com.pennanttech.pff.core.TableType;

public interface UploadHeaderDAO {

	UploadHeader getUploadHeader(long uploadId);

	UploadHeader getUploadHeaderById(long uploadId, String type);

	boolean isFileNameExist(String fileName);

	long save(UploadHeader uploadHeader);

	void updateRecordCounts(UploadHeader uploadHeader);

	void updateRecord(UploadHeader uploadHeader);

	// ManualJVPosting CR
	void updateFileDownload(long uploadId, boolean fileDownload, String type);

	public long save(UploadHeader uploadHeader, TableType tableType);

	void update(UploadHeader uploadHeader, TableType tableType);

	void delete(UploadHeader uploadHeader, TableType tableType);

	boolean isDuplicateKey(long uploadId, String fileName, TableType tableType);

	UploadHeader getUploadHeader();

	boolean isFileDownload(long uploadID, String tableType);

	List<UploadReceipt> getSuccesFailedReceiptCount(long uploadId);

}