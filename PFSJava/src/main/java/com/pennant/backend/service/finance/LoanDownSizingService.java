package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;

public interface LoanDownSizingService {

	FinanceDetail getDownSizingFinance(FinanceMain aFinanceMain, String rcdMaintainSts);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader);

	FinScheduleData changeGraceEndAfterFullDisb(FinScheduleData finScheduleData);

	List<FinAssetAmtMovement> getFinAssetAmtMovements(long finID, String movementType);

	AEEvent getChangeGrcEndPostings(FinScheduleData finScheduleData);
}
