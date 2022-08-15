package com.pennanttech.pff.documents.dao;

import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.documents.model.Document;
import com.pennanttech.pff.documents.model.DocumentStatus;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;

public interface DocumentDao {
	List<Document> getDocuments(String finReferece);

	List<DocumentStatusDetail> getDocumentStatus(List<Long> id);

	DocumentStatusDetail getDocumentStatusById(long id, String string);

	int update(DocumentStatusDetail addressType, TableType mainTab);

	long save(DocumentStatus ds, TableType tableType);

	long save(DocumentStatusDetail addressType, TableType mainTab);

	void delete(DocumentStatus ds, TableType tableType);

	void delete(DocumentStatusDetail addressType, TableType tempTab);

	void deleteChildrens(long headerId, TableType tableType);

	DocumentStatus getDocumentStatus(String finReferece);

	void update(DocumentStatus ds, TableType tableType);

	void updateStaus(List<DocumentStatusDetail> list);

	int resetStatus(long docId);

	DocumentStatusDetail getDocumentStatusByDocId(long id, String tableType);

}
