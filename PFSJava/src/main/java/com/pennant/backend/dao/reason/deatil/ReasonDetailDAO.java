package com.pennant.backend.dao.reason.deatil;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.reason.details.ReasonHeader;

public interface ReasonDetailDAO {
	long save(ReasonHeader reasonHeader);

	List<Map<String, Object>>  getReasonDetailsLog(String reference);
}
