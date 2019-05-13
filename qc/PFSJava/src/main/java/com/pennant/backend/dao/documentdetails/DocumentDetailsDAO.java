package com.pennant.backend.dao.documentdetails;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.documentdetails.DocumentDetails;

public interface DocumentDetailsDAO {

	DocumentDetails getDocumentDetailsById(long id, String type);

	List<DocumentDetails> getDocumentDetailsByRef(String ref, String module, String finEvent, String type);

	void update(DocumentDetails channelDetail, String type);

	void delete(DocumentDetails channelDetail, String type);

	long save(DocumentDetails channelDetail, String type);

	void deleteList(List<DocumentDetails> docList, String type);

	void saveList(ArrayList<DocumentDetails> docList, String type);

	long generateDocSeq();

	List<DocumentDetails> getDocumentDetailsByRef(String ref, String module, String type);

	DocumentDetails getDocumentDetailsById(long id, String type, boolean readAttachment);

	DocumentDetails getDocumentDetails(String ref, String category, String module, String type);

	DocumentDetails getDocumentDetails(long id, String type);

	void deleteList(String referenceId, String docCategory, String docModule, String type);
}
