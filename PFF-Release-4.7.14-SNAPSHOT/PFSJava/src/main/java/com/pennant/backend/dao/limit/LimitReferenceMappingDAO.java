package com.pennant.backend.dao.limit;

import java.util.List;

import com.pennant.backend.model.limit.LimitReferenceMapping;

public interface LimitReferenceMappingDAO {

	long save(LimitReferenceMapping limitReferenceMapping);

	boolean deleteReferencemapping(long referenceId);

	LimitReferenceMapping getLimitReferencemapping(String reference, long headerId);

	List<LimitReferenceMapping> getLimitReferences(long headerid,String limitLine);
	
	int isLimitLineExist(String lmtline);

	boolean deleteByHeaderID(long headerID);
	
	// Limit Rebuild
	void saveBatch(List<LimitReferenceMapping> lmtReferenceMapping);

}
