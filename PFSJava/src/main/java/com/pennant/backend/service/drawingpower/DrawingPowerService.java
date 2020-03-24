package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;

public interface DrawingPowerService {

	BigDecimal getDrawingPower(String finreference);

	AuditDetail validate(AuditDetail auditDetail, FinanceDetail financeDetail);
}
