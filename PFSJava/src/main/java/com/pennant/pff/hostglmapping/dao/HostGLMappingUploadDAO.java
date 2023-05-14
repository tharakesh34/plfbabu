package com.pennant.pff.hostglmapping.dao;

import java.util.List;

import com.pennant.backend.model.hostglmapping.upload.HostGLMappingUpload;
import com.pennanttech.pff.core.TableType;

public interface HostGLMappingUploadDAO {

	List<HostGLMappingUpload> getDetails(long id);

	void update(List<HostGLMappingUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isDuplicateKey(String glcode, TableType tableType);
}
