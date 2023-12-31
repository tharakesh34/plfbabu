package com.pennanttech.pff.logging.dao;

import com.pennanttech.logging.model.InterfaceLogDetail;

public interface InterfaceLoggingDAO {

	void save(InterfaceLogDetail interfaceLogDetail);

	String getPreviousDataifAny(String reference, String service, String status);

	void update(InterfaceLogDetail interfaceLogDetail);

	long getSequence();

	long getSequence(String seqName);

}
