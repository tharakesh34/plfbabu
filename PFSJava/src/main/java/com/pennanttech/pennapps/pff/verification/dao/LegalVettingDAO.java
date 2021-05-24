package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface LegalVettingDAO extends BasicCrudDao<LegalVetting> {

	LegalVetting getLegalVetting(long verificationId, String type);

	List<LVDocument> getLVDocuments(long id, String type);

	void saveDocuments(List<LVDocument> lvDocuments, TableType tableType);

	void deleteDocuments(long verificationId, TableType tableType);

	LegalVetting getLVFromStage(long verificationId);

	List<LVDocument> getLVDocumentsFromStage(long verificationId);

	List<LegalVetting> getList(String keyRef);

	void updateDocuments(LVDocument lvDocument, String tableType);

	void deleteLVDocuments(LVDocument lvDocument, String tableType);

	void deleteLVDocumentsList(List<LVDocument> documents, String tableType);

	void saveLV(LegalVetting legalVetting, TableType tableType);

	void saveDocuments(LVDocument lvDocument, String tableType);

	List<LVDocument> getLVDocuments(String keyReference, int docTypeKey);

	boolean isVettingExists(long id);

	List<LVDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType);

	List<Verification> getCollateralDocumentsStatus(String collateralReference);

	int getCollateralDocumentCount(String collateralRef);

	int getLVDocumentsCount(String collateralRef);
}
