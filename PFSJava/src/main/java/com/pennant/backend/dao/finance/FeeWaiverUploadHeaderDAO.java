package com.pennant.backend.dao.finance;

import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennanttech.pff.core.TableType;

public interface FeeWaiverUploadHeaderDAO {

	FeeWaiverUploadHeader getUploadHeader();

	boolean isFileNameExist(String fileName);

	FeeWaiverUploadHeader getUploadHeaderById(long uploadId, String type);

	boolean isDuplicateKey(long uploadId, String fileName, TableType tableType);

	public long save(FeeWaiverUploadHeader uploadHeader, TableType tableType);

	void update(FeeWaiverUploadHeader uploadHeader, TableType tableType);

	void delete(FeeWaiverUploadHeader uploadHeader, TableType tableType);

	boolean isFileDownload(long uploadID, String tableType);

	void updateFileDownload(long uploadId, boolean fileDownload, String type);
}
