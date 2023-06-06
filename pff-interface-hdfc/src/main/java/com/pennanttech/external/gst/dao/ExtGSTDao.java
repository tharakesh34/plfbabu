package com.pennanttech.external.gst.dao;

import java.util.List;

import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.external.gst.model.GSTVoucherDetails;

public interface ExtGSTDao {

	long getSeqNumber(String tableName);

	void extractDetailsFromFinFeeDetail();

	void extractDetailsFromManualadvise();

	void saveExtractedDetailsToRequestTable();

	List<GSTRequestDetail> fetchRecords(int status);

	boolean isFileProcessed(String respFileName);

	void saveResponseFile(GSTCompHeader compHeader);

	void updateFileStatus(long id, int status);

	int saveExtGSTCompRecordsData(List<GSTCompDetail> compDetails);

	void updateGSTRecordDetailStatus(GSTCompDetail detail);

	long saveGSTRequestFileData(String fileName, String fileLocation);

	int updateGSTVoucherWithReqHeaderId(List<Long> txnUidList, long headerId);

	public GSTVoucherDetails fetchVoucherDetails(long transactionUID);

	long saveGSTInvoiceDetails(GSTInvoiceDetail gstInvoiceDetail);

}
