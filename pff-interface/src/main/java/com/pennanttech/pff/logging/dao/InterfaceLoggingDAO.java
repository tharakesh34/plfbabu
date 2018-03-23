package com.pennanttech.pff.logging.dao;

import java.util.List;
import java.util.Set;

import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;

public interface InterfaceLoggingDAO {

	void save(InterfaceLogDetail interfaceLogDetail);
	
	List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames);
}
