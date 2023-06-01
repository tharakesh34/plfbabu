package com.pennanttech.external.gst.dao;

import java.util.List;

import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTRequestDetail;

public interface ExtGSTDao {

	long getSeqNumber(String tableName);

	void extractGSTVouchers();

	void saveGSTVouchersToRequestTable(int processStatus, int req_file_id);

	List<GSTRequestDetail> fetchRecords(int status);

	boolean isFileProcessed(String respFileName);

	void saveResponseFile(String fileName, String fileLocation, int fileStatus, int extractStatus, String errorCode,
			String errorMessage);

	void updateFileStatus(long id, int status);

	int saveExtGSTCompRecordsData(List<GSTCompDetail> compDetails);

	void updateGSTRecordDetailStatus(GSTCompDetail detail);

	long saveGSTRequestFileData(String fileName, String fileLocation);

	int updateGSTVoucherWithReqHeaderId(List<Long> txnUidList, long headerId);

	boolean isVoucherFound(long transactionUID);

	long saveGSTInvoiceDetails(GSTInvoiceDetail gstInvoiceDetail);

}
