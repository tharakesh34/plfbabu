package com.pennant.pff.branchchange.dao;

import java.util.List;

import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;

public interface BranchChangeUploadDAO {
	List<BranchChangeUpload> getDetails(long id);

	void update(List<BranchChangeUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isInSettlement(long finID, String type);

	boolean isInlinkingDelinking(long finID, String type);

	boolean getReceiptQueueList(long finID);
}
