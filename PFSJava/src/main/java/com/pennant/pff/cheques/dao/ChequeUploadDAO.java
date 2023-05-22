package com.pennant.pff.cheques.dao;

import java.util.List;

import com.pennant.backend.model.pdc.upload.ChequeUpload;

public interface ChequeUploadDAO {
	List<ChequeUpload> getDetails(long headerID);

	void update(List<ChequeUpload> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();
}
