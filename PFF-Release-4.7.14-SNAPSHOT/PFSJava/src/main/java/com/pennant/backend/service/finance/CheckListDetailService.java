package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface CheckListDetailService {
	
	void fetchFinCheckListDetails(FinanceDetail financeDetail, List<FinanceReferenceDetail> finReferenceDetails);
	void setFinanceCheckListDetails(FinanceDetail financeDetail, String finType,String finEvent, String userRole);
	List<FinanceCheckListReference> getCheckListByFinRef(final String id, String tableType);
	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> doApprove(FinanceDetail financeDetail, String recordType);
	List<AuditDetail> delete(FinanceDetail finDetail, String tableType, String auditTranType);
	List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method);
	List<AuditDetail> validate(List<AuditDetail> auditDetails,  String method, String usrLanguage);
	CheckListDetail getCheckListDetailByDocType(String docType, String finType);
	void fetchCollateralCheckLists(CollateralSetup collateralSetup,	List<FinanceReferenceDetail> financeReferenceList);
	void fetchCommitmentCheckLists(Commitment commitment, List<FinanceReferenceDetail> financeReferenceList);
	void fetchVASCheckLists(VASRecording vasRecording, List<FinanceReferenceDetail> financeReferenceList);
	
}
