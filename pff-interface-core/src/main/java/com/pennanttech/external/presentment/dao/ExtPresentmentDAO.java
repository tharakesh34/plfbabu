package com.pennanttech.external.presentment.dao;

import java.util.Date;
import java.util.List;

import com.pennanttech.external.presentment.model.ExtBounceReason;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.external.presentment.model.ExtPresentmentData;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.external.presentment.model.ExtPrmntRespHeader;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public interface ExtPresentmentDAO {
	long getSeqNumber(String tableName);

	List<ExtPresentmentFile> getACHPresentmentDetails(PresentmentHeader presentmentHeader);

	long getSIPresentmentDetailsCount(PresentmentHeader presentmentHeader);

	List<ExtPresentmentFile> getSIPresentmentDetails(PresentmentHeader presentmentHeader);

	List<ExtPresentmentFile> getSiInternalPresentmentDetails(PresentmentHeader presentmentHeader);

	List<ExtPresentmentFile> getExternalPDCPresentmentDetails(PresentmentHeader presentmentHeader);

	int saveExternalRecords(List<Presentment> presentments, long headerId);

	public void saveResponseFile(ExtPresentment extPresentment);

	public boolean isFileProcessed(String fileName, String moduleName);

	public Presentment getPresenementMandateRecord(long p_id);

	public Presentment getPresenementPDCRecord(long p_id);

	public long savePresentment(Presentment pres, long headerId, String clearingStatus);

	public void updateFileStatus(long id, long status);

	public void updateFileExtractionStatus(long id, long extraction);

	public void updateFileExtractionStatusWithError(long id, long extraction, String errCode, String errMessage);

	public int saveExternalPresentmentRecordsData(List<ExtPresentmentData> extPresentmentDataList);

	public void updateExternalPresentmentRecordStatus(long id, long status, String statusCode, String statusMessage);

	public List<ExtBounceReason> fetchBounceReasons();

	public boolean isAnyRecordPending(long headerId);

	public boolean isRecordAlreadyInserted(String record, long headerId);

	public boolean isHeaderFileProcessed(String fileName);

	public void save(ExtPrmntRespHeader PresentmentRespHeader);

	public long getHeaderIdIfExist(String fileName);

	public void updateHeader(ExtPrmntRespHeader presentmentRespHeader);

	public void updateHeaderProgress(long headerId, int progress);

	boolean isRecordInserted(String refernce, long headeID);

	public Presentment getPDCStagingPresentmentDetails(long finId, String chequeNo, Date chequeDate);

}
