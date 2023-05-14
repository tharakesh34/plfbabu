package com.pennant.backend.dao.loancancel;

import java.util.List;

import com.pennant.backend.model.finance.FinCancelUploadDetail;

public interface FinanceCancellationUploadDAO {

	String getSqlQuery();

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	List<FinCancelUploadDetail> getDetails(long id);

	void update(List<FinCancelUploadDetail> details);

	void update(FinCancelUploadDetail detail);
}