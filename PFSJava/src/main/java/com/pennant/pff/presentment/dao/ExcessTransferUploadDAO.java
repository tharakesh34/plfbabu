package com.pennant.pff.presentment.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.excessheadmaster.ExcessTransferUpload;

public interface ExcessTransferUploadDAO {

	List<ExcessTransferUpload> getDetails(long headerID);

	void update(List<ExcessTransferUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	boolean isDuplicateExists(String reference, String amountType, long headerID);

	BigDecimal getBalanceAmount(long finID, String amountType);

	String getRecords(long headerID, String status);

}
