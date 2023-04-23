package com.pennant.pff.receipt.dao;

import java.util.List;

import com.pennant.pff.receipt.model.ReceiptStatusUpload;

public interface ReceiptStatusUploadDAO {

	List<ReceiptStatusUpload> getDetails(long id);

	void update(List<ReceiptStatusUpload> details);

	public void update(List<Long> headerIdList, String errCode, String errDesc, int progressFailed);

	public String getSqlQuery();
}