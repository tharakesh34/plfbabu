package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.ratechangeupload.RateChangeUpload;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface RateChangeUploadDAO {

	List<RateChangeUpload> getRateChangeUploadDetails(long batchId);

	void updateRateChangeDetails(RateChangeUpload rcUpload);

	boolean isFileExists(String name);

	long saveHeader(String fileName, String entityCode);

	void updateRemarks(RateChangeUploadHeader rateChangeUploadHeader);

	int saveRateChangeUpload(List<RateChangeUpload> rateChangeUploads, long id);

	List<FinanceMain> getFinanceMain(long batchId);

	boolean getRateCodes(String brCode);

	int logRcUpload(List<ErrorDetail> errDetail, Long id);

	void updateDeRemarks(RateChangeUploadHeader header, DataEngineStatus deStatus);

}
