package com.pennant.backend.dao.documentdetails;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.documentdetails.DocumentDetails;

public interface DocumentDetailsDAO {

	public DocumentDetails getDocumentDetailsById(long id,String type);
	public List<DocumentDetails> getDocumentDetailsByRef(String  ref,String type);
	public void update(DocumentDetails channelDetail,String type);
	public void delete(DocumentDetails channelDetail,String type);
	public long save(DocumentDetails channelDetail,String type);
	public void deleteList(List<DocumentDetails> docList, String type);
	public void saveList(ArrayList<DocumentDetails> docList, String type);
	public long generateDocSeq();
}
