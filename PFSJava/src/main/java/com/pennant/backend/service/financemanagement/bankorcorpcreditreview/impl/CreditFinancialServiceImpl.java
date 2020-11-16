package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.CreditReviewDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditFinancialService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class CreditFinancialServiceImpl extends GenericService<CreditReviewData> implements CreditFinancialService {
	@Autowired
	private CreditReviewDetailDAO creditReviewDetailDAO;
	@Autowired
	private AuditHeaderDAO auditHeaderDAO;

	private static Logger logger = Logger.getLogger(CreditFinancialServiceImpl.class);

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		CreditReviewData creditReviewData = financeDetail.getCreditReviewData();
		creditReviewData.setTaskId(financeMain.getTaskId());
		creditReviewData.setNextTaskId(financeMain.getNextTaskId());
		creditReviewData.setRoleCode(financeMain.getRoleCode());
		creditReviewData.setNextRoleCode(financeMain.getNextRoleCode());
		creditReviewData.setRecordStatus(financeMain.getRecordStatus());
		creditReviewData.setWorkflowId(financeMain.getWorkflowId());
		creditReviewDetailDAO.delete(financeMain.getFinReference(), TableType.MAIN_TAB);
		creditReviewDetailDAO.save(creditReviewData);
		auditDetails.add(getAuditDetails(creditReviewData, 1, PennantConstants.TRAN_ADD));

		addAudit(auditHeader, auditDetails);
		logger.debug(Literal.LEAVING);
	}

	public void doApprove(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		CreditReviewData creditReviewData = financeDetail.getCreditReviewData();
		creditReviewData.setTaskId(financeMain.getTaskId());
		creditReviewData.setNextTaskId(financeMain.getNextTaskId());
		creditReviewData.setRoleCode(financeMain.getRoleCode());
		creditReviewData.setNextRoleCode(financeMain.getNextRoleCode());
		creditReviewData.setRecordStatus(financeMain.getRecordStatus());
		creditReviewData.setWorkflowId(financeMain.getWorkflowId());
		creditReviewDetailDAO.delete(financeMain.getFinReference(), TableType.MAIN_TAB);
		creditReviewDetailDAO.save(creditReviewData);
		auditDetails.add(getAuditDetails(creditReviewData, 1, PennantConstants.TRAN_ADD));

		addAudit(auditHeader, auditDetails);

	}

	public AuditDetail getAuditDetails(CreditReviewData creditReviewData, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new CreditReviewData(),
				new CreditReviewData().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], creditReviewData.getBefImage(),
				creditReviewData);
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		//Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}
		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(header);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(CreditReviewData.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}
}
