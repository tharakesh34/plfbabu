package com.pennant.backend.dao.documentdetails;

import com.pennant.backend.model.documentdetails.DocumentManager;

public interface DocumentManagerDAO {

	long save(DocumentManager documentManager);
	DocumentManager getById(long id);
}