package com.pennanttech.pff.core.process;

import java.util.List;

import com.pennant.backend.model.finance.ProjectedAccrual;

public interface ProjectedAccrualProcess {
	List<ProjectedAccrual> calculateAccrualsOnMonthEnd(Object... params) throws Exception;
}
