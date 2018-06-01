package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface RiskContainmentUnitDAO extends BasicCrudDao<RiskContainmentUnit> {

	RiskContainmentUnit getRiskContainmentUnit(long verificationId, String type);

	List<RCUDocument> getRCUDocuments(long id, String type);

	void saveDocuments(RCUDocument rcuDocument, String tableType);

	void updateDocuments(RCUDocument rcuDocument, String tableType);

	void deleteRCUDocuments(RCUDocument rcuDocument, String tableType);

	void deleteRCUDocumentsList(List<RCUDocument> documents, String tableType);

	List<RiskContainmentUnit> getList(String keyReference);

	void saveDocuments(List<RCUDocument> rcuDocuments, TableType tableType);

	void deleteDocuments(long verificationId, TableType tableType);

	List<RCUDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType);

	void updateRemarks(Verification item);

	void updateRCUDocuments(Verification item, TableType table);

	RCUDocument getRCUDocument(long verificationId, RCUDocument rcuDocument);

	int getRCUDocumentsCount(long verificationId);
	
	void deleteRCUDocument(RCUDocument rcuDocument, String tableType);
}
