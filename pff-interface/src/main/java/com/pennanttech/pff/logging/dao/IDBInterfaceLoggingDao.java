package com.pennanttech.pff.logging.dao;

import com.pennanttech.pff.model.IDBInterfaceLogDetail;

public interface IDBInterfaceLoggingDao {

	int save(IDBInterfaceLogDetail detail);

	void update(IDBInterfaceLogDetail detail);

	long getSequence();

	long getSequence(String seqName);

}
