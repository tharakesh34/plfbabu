package com.pennanttech.pennapps.pff.service.hook;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.DeviationDetail;

public class FinanceDeviationHook {

	public interface PostDeviationHook {
		List<DeviationDetail> processDeviatiopn(AuditHeader auditHeader);
	}

}
