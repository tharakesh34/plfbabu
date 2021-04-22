package com.pennant.backend.service.finance;

import java.util.List;

import org.jaxen.JaxenException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennanttech.pennapps.core.InterfaceException;

public interface LoanDownSizingService {

	FinanceDetail getDownSizingFinance(FinanceMain aFinanceMain, String rcdMaintainSts);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, JaxenException;

	FinScheduleData changeGraceEndAfterFullDisb(FinScheduleData finScheduleData);

	List<FinAssetAmtMovement> getFinAssetAmtMovements(String finRef, String movementType);

	AEEvent getChangeGrcEndPostings(FinScheduleData finScheduleData) throws Exception;
}
