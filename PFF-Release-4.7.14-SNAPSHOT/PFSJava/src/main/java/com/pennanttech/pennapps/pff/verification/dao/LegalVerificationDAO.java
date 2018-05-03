package com.pennanttech.pennapps.pff.verification.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;

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
	

}
