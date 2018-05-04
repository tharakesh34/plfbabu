package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pff.core.TableType;

public interface LegalVerificationDAO extends BasicCrudDao<LegalVerification> {
	
	/**
	 * Fetch the Record LegalVerification by key field
	 * 
	 * @param id
	 *            id of the LegalVerification.
	 * @param tableType
	 *            The type of the table.
	 * @return LegalVerification
	 */
	LegalVerification getLegalVerification(long id, String type);

	void saveDocuments(List<LVDocument> lvDocuments, TableType tableType);
	
	void deleteDocuments(String reference, TableType tableType);
	
	LegalVerification getLVFromStage(long verificationId);

	List<LVDocument> getLVDocumentsFromStage(long verificationId);

	List<LegalVerification> getList(String keyRef);
	
}
