package com.pennanttech.external.gst.dao;

import java.util.List;

import com.pennant.backend.model.finance.Taxes;
import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTReqFile;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.external.gst.model.GSTVoucherDetails;

public interface ExtGSTDao {

	long getSeqNumber(String tableName);

	void extractDetailsFromForGstCalculation();

	long saveExtractedDetailsToRequestTable(long headerId);

	List<GSTRequestDetail> fetchRecords();

	boolean isFileProcessed(String respFileName);

	void saveResponseFile(GSTCompHeader compHeader);

	void updateFileStatus(GSTCompHeader header);

	int saveExtGSTCompRecordsData(List<GSTCompDetail> compDetails);

	void updateGSTRecordDetailStatus(GSTCompDetail detail);

	void updateGSTRequestFileToHeaderId(GSTReqFile gstReqFile);

	public GSTVoucherDetails fetchVoucherDetails(long transactionUID);

	long saveGSTInvoiceDetails(GSTInvoiceDetail gstInvoiceDetail);

	long fetchHeaderIdForProcessing(GSTReqFile gstReqFile);

	long getHeaderIdForFile(GSTReqFile gstReqFile);

	void updateHeaderIdIntoGSTVoucherDetails(long headerId);

	List<Taxes> getTaxDetailsForHeaderId(long taxHeaderId);

	void updateTaxDetails(Taxes taxes);

	void updateFileWriteStatus(int status);
}
