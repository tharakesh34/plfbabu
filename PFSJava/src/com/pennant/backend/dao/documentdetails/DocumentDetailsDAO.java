package com.pennant.backend.dao.documentdetails;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.documentdetails.DocumentDetails;

public interface DocumentDetailsDAO {

	DocumentDetails getDocumentDetailsById(long id,String type);
	List<DocumentDetails> getDocumentDetailsByRef(String  ref,String type);
	void update(DocumentDetails channelDetail,String type);
	void delete(DocumentDetails channelDetail,String type);
	long save(DocumentDetails channelDetail,String type);
	void deleteList(List<DocumentDetails> docList, String type);
	void saveList(ArrayList<DocumentDetails> docList, String type);
	long generateDocSeq();
}
