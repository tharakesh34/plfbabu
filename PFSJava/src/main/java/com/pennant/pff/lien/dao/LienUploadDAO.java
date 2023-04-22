package com.pennant.pff.lien.dao;

import java.util.List;

import com.pennanttech.model.lien.LienUpload;

public interface LienUploadDAO {

	List<LienUpload> getDetails(long headerID);

	long save(LienUpload lu);

	void updateStatus(List<LienUpload> details);

	void update(LienUpload lu, long headerID);

	void updateRejectStatus(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

}
