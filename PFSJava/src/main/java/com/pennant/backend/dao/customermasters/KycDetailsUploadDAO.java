package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.bulkaddressupload.CustomerKycDetail;
import com.pennant.backend.model.finance.FinanceMain;

public interface KycDetailsUploadDAO {

	List<CustomerKycDetail> loadRecordData(long id);

	void update(List<CustomerKycDetail> details);

	void update(List<Long> headerIdList, String errorCode, String errorDesc);

	String getSqlQuery();

	void update(CustomerKycDetail detail);

	boolean isInProgress(long headerID, String custCif);

	boolean isInMaintanance(String custCif);

	boolean isInLoanQueue(long custId);

	List<String> getReceiptQueueList(long custId);

	List<FinanceMain> isInMaintanance(long custId);

	List<String> isInSettlement(long custId);

	List<String> isInlinkingDelinking(long custId);
}