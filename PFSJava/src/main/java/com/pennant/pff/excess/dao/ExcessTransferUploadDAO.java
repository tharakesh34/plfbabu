package com.pennant.pff.excess.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.pff.excess.model.ExcessTransferUpload;

public interface ExcessTransferUploadDAO {

	List<ExcessTransferUpload> getDetails(long headerID);

	void update(List<ExcessTransferUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isDuplicateExists(String reference, String amountType, long headerID);

	BigDecimal getBalanceAmount(long finID, String amountType);

	List<ExcessTransferUpload> getProcess(long headerID);

	void updateFailure(ExcessTransferUpload detail);

	long getNextValue();

}
