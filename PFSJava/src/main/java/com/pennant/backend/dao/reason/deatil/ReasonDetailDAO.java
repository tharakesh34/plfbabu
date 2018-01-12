package com.pennant.backend.dao.reason.deatil;

import java.util.List;

import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.model.reason.details.ReasonHeader;

public interface ReasonDetailDAO {
	long save(ReasonHeader reasonHeader);

	List<ReasonDetailsLog>  getReasonDetailsLog(String reference);
	public boolean isreasonCodeExists(long reasonCode);

}
