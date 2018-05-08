package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;

public interface RiskContainmentUnitDAO extends BasicCrudDao<RiskContainmentUnit> {

	RiskContainmentUnit getRiskContainmentUnit(long id, long documetId, String documentSubId, String type);
	
	void saveDocuments(RCUDocument rcuDocument, String tableType);

	void updateDocuments(RCUDocument rcuDocument, String tableType);

	void deleteRCUDocuments(RCUDocument rcuDocument, String tableType);

	void deleteRCUDocumentsList(List<LVDocument> documents, String tableType);
	
}
