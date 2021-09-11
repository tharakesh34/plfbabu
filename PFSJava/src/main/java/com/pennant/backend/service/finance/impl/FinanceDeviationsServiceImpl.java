package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class FinanceDeviationsServiceImpl implements FinanceDeviationsService {
	private static final Logger logger = LogManager.getLogger(FinanceDeviationsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceDeviationsDAO deviationDetailsDAO;
	private FinanceMainDAO financeMainDAO;
	private TaskOwnersDAO taskOwnersDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CustomerDetailsService customerDetailsService;
	private EligibilityDetailService eligibilityDetailService;
	private CheckListDetailService checkListDetailService;
	private FinanceScoreHeaderDAO financeScoreHeaderDAO;
	private DeviationHelper deviationHelper;

	public FinanceDeviationsServiceImpl() {
		super();
	}

	@Override
	public List<FinanceDeviations> getApprovedFinanceDeviations(long finID) {
		List<FinanceDeviations> list = deviationDetailsDAO.getFinanceDeviations(finID, "");
		if (list != null && !list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				financeDeviations.setApproved(true);
			}
		}
		return list;
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(long finID) {
		return deviationDetailsDAO.getFinanceDeviations(finID, TableType.TEMP_TAB.getSuffix());
	}

	@Override
	public FinanceMain getFinanceMain(long finID) {
		return financeMainDAO.getFinanceMainById(finID, "_View", false);
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(fm.getFinReference());

		String finType = fm.getFinType();
		long custID = fm.getCustID();
		String finPreApprovedRef = fm.getFinPreApprovedRef();
		BigDecimal finAmount = fm.getFinAmount();
		String finCcy = fm.getFinCcy();
		boolean newRecord = fm.isNewRecord();

		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(finType, "_AView");
		CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(custID, true, "_View");

		fd.setCustomerDetails(customerDetails);
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeType);

		List<FinanceDeviations> financeDeviations = getFinanceDeviations(finID);
		List<FinanceDeviations> approvedFinDeviations = getApprovedFinanceDeviations(finID);
		deviationHelper.setDeviationDetails(fd, financeDeviations, approvedFinDeviations);

		// Finance Eligibility Rule Details List
		// =======================================
		String screenEvent = "";
		if (StringUtils.trimToEmpty(finPreApprovedRef).equals(FinServiceEvent.PREAPPROVAL)) {
			screenEvent = FinServiceEvent.PREAPPROVAL;
		} else {
			screenEvent = FinServiceEvent.ORG;
		}

		fd.setElgRuleList(eligibilityDetailService.setFinanceEligibilityDetails(finID, finCcy, finAmount, newRecord,
				finType, "", screenEvent));

		// Check List Details
		checkListDetailService.setFinanceCheckListDetails(fd, finType, FinServiceEvent.ORG, "");

		// Set Scoring details
		List<FinanceScoreHeader> list = financeScoreHeaderDAO.getFinScoreHeaderList(finID, "_View");

		if (list != null && !list.isEmpty()) {
			fd.setFinScoreHeaderList(list);
			List<Long> headerIds = new ArrayList<Long>(list.size());
			for (FinanceScoreHeader financeScoreHeader : list) {
				headerIds.add(financeScoreHeader.getHeaderId());
			}
			List<FinanceScoreDetail> dellist = financeScoreHeaderDAO.getFinScoreDetailList(headerIds, "_View");

			if (dellist != null) {
				Map<Long, List<FinanceScoreDetail>> map = fd.getScoreDetailListMap();
				for (FinanceScoreDetail financeScoreDetail : dellist) {
					if (map.containsKey(financeScoreDetail.getHeaderId())) {
						map.get(financeScoreDetail.getHeaderId()).add(financeScoreDetail);
					} else {
						List<FinanceScoreDetail> slist = new ArrayList<FinanceScoreDetail>();
						slist.add(financeScoreDetail);
						map.put(financeScoreDetail.getHeaderId(), slist);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public void processDevaitions(long finID, List<FinanceDeviations> newlist, AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		if (newlist == null) {
			return;
		}

		String tableType = "_Temp";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinanceDeviations> oldList = getFinanceDeviations(finID);
		int count = 0;

		// Checking records to save and Update
		for (FinanceDeviations newfindev : newlist) {
			// ### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
			if (!(StringUtils.isEmpty(newfindev.getApprovalStatus())
					|| StringUtils.equals(newfindev.getApprovalStatus(), PennantConstants.List_Select))) {
				continue;
			}
			// ### 01-05-2018 - End
			FinanceDeviations oldFindev = getFinanceDeviationByID(oldList, newfindev);
			if (oldFindev == null) {
				deviationDetailsDAO.save(newfindev, tableType);
				AuditDetail finDeviationAudit = getFinDeviationsAudit(newfindev, ++count, PennantConstants.TRAN_ADD);
				auditDetails.add(finDeviationAudit);
			} else {
				FinanceDeviations befImage = new FinanceDeviations();
				BeanUtils.copyProperties(oldFindev, befImage);
				newfindev.setBefImage(befImage);
				newfindev.setDeviationId(oldFindev.getId());

				if (StringUtils.equals(newfindev.getRecordType(), PennantConstants.RCD_DEL)) {
					deviationDetailsDAO.deleteById(newfindev, tableType);
					AuditDetail finDeviationAudit = getFinDeviationsAudit(newfindev, ++count,
							PennantConstants.TRAN_DEL);
					auditDetails.add(finDeviationAudit);
				} else {
					deviationDetailsDAO.update(newfindev, tableType);
					AuditDetail finDeviationAudit = getFinDeviationsAudit(newfindev, ++count,
							PennantConstants.TRAN_UPD);
					auditDetails.add(finDeviationAudit);
				}
			}
		}

		// Delete the records which are not there in new List
		for (FinanceDeviations finDeviation : oldList) {
			FinanceDeviations delRecod = getFinanceDeviationByID(newlist, finDeviation);
			if (delRecod == null) {
				deviationDetailsDAO.delete(finDeviation, tableType);
				finDeviation.setBefImage(finDeviation);
				AuditDetail finDeviationAudit = getFinDeviationsAudit(finDeviation, ++count, PennantConstants.TRAN_DEL);
				auditDetails.add(finDeviationAudit);
			}
		}
		// ### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
		// if status is approved then processApproval
		for (FinanceDeviations financeDeviations : newlist) {
			if (financeDeviations.isMarkDeleted()) {
				deviationDetailsDAO.updateMarkDeleted(financeDeviations.getDeviationId(),
						financeDeviations.getFinReference());
				financeDeviations.setBefImage(financeDeviations);
				auditDetails.add(getFinDeviationsAudit(financeDeviations, ++count, PennantConstants.TRAN_DEL));
				continue;
			}

			if (!(StringUtils.isEmpty(financeDeviations.getApprovalStatus())
					|| StringUtils.equals(financeDeviations.getApprovalStatus(), PennantConstants.List_Select))) {
				deviationDetailsDAO.save(financeDeviations, "");
				auditDetails.add(getFinDeviationsAudit(financeDeviations, ++count, PennantConstants.TRAN_ADD));

				deviationDetailsDAO.delete(financeDeviations, "_Temp");
				financeDeviations.setBefImage(financeDeviations);
				auditDetails.add(getFinDeviationsAudit(financeDeviations, ++count, PennantConstants.TRAN_DEL));
			}

		}
		// ### 01-05-2018 - End
		// Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}

		AuditHeader devAuditHeader = getAuditHeader(auditHeader);
		devAuditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(devAuditHeader);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processApprovedDevaitions(long finID, List<FinanceDeviations> deviations, AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		if (deviations == null) {
			return;
		}

		for (FinanceDeviations deviation : deviations) {
			if (PennantConstants.RCD_UPD.equals(deviation.getRecordType())) {
				deviationDetailsDAO.updateMarkDeleted(deviation.getDeviationId(), deviation.isMarkDeleted());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/** 
	 * 
	 */
	@Override
	public void processApproval(List<FinanceDeviations> list, AuditHeader auditHeader, long finID) {
		logger.debug(Literal.ENTERING);
		if (list != null && !list.isEmpty()) {

			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			int count = 0;
			for (FinanceDeviations financeDeviations : list) {
				String status = StringUtils.trimToEmpty(financeDeviations.getApprovalStatus());
				if (StringUtils.isEmpty(status) || status.equals(PennantConstants.List_Select)) {
					continue;
				}

				deviationDetailsDAO.save(financeDeviations, "");
				auditDetails.add(getFinDeviationsAudit(financeDeviations, ++count, PennantConstants.TRAN_ADD));

				deviationDetailsDAO.delete(financeDeviations, "_Temp");
				financeDeviations.setBefImage(financeDeviations);
				auditDetails.add(getFinDeviationsAudit(financeDeviations, ++count, PennantConstants.TRAN_DEL));

			}

			// Add audit if any changes
			if (auditDetails.isEmpty()) {
				logger.debug(Literal.LEAVING);
				return;
			}

			auditHeader.setAuditDetails(auditDetails);
			auditHeaderDAO.addAudit(auditHeader);
		}

		// if approval for all records came then remove for the deviation approval
		// update finance reference to remove for approval
		checkFinalApproval(finID);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param finref
	 */
	private void checkFinalApproval(long finID) {
		logger.debug(Literal.ENTERING);
		List<FinanceDeviations> list = deviationDetailsDAO.getFinanceDeviations(finID, TableType.TEMP_TAB.getSuffix());
		if (list != null && !list.isEmpty()) {
			// since there are some deviation which are pending for approval
			return;
		}

		list = deviationDetailsDAO.getFinanceDeviations(finID, false, TableType.MAIN_TAB.getSuffix());

		String finReference = null;
		if (list != null && !list.isEmpty()) {
			boolean deviationApproved = true;
			boolean rejected = false;
			for (FinanceDeviations finDeviations : list) {
				finReference = finDeviations.getFinReference();
				String status = StringUtils.trimToEmpty(finDeviations.getApprovalStatus());
				// Check for at least one reject and then proceed
				if (status.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					rejected = true;
				}

			}

			// if approval for all records came then remove for the deviation approval
			// update finance reference to remove for approval
			if (deviationApproved) {

				FinanceMain finmain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);

				// Load the work-flow engine.
				long workflowId = finmain.getWorkflowId();
				WorkflowEngine engine = new WorkflowEngine(WorkFlowUtil.getWorkflow(workflowId).getWorkFlowXml());

				// Get the delegator's next tasks.
				Map<String, Object> data = new HashMap<>();
				data.put("vo", finmain);

				if (rejected) {
					data.put("deviationApprovalStatus", "REJECTED");
				} else {
					data.put("deviationApprovalStatus", "APPROVED");
				}

				String delegatorNextTasks = engine.getDelegatorNextTasks(data);

				// Remove deviation flag.
				finmain.setDeviationApproval(false);

				boolean queueChanged = false;
				String roleCode = finmain.getRoleCode();
				String nextRoleCode = finmain.getNextRoleCode();
				String taskId = finmain.getTaskId();
				String nextUserid = StringUtils.trimToEmpty(finmain.getNextUserId());
				String newTaskId = null;
				String newRoleCode = "";

				if (StringUtils.isBlank(delegatorNextTasks)) {
					if (rejected) {
						queueChanged = true;

						newTaskId = taskId;
						newRoleCode = roleCode;
					} else {
						//
					}
				} else {
					queueChanged = true;

					newTaskId = delegatorNextTasks;

					String[] nextTasks = newTaskId.split(";");
					String tempRoleCode;

					for (int i = 0; i < nextTasks.length; i++) {
						if (newRoleCode.length() > 1) {
							newRoleCode = newRoleCode.concat(",");
						}
						tempRoleCode = engine.getUserTask(nextTasks[i]).getActor();
						newRoleCode += tempRoleCode;
					}
				}

				if (queueChanged) {
					// if queue assignment process then
					if (StringUtils.isNotEmpty(nextUserid) && Long.parseLong(nextUserid) != 0) {
						/*
						 * 1. get the record with role code and finance reference 2. Update finance with the current
						 * owner and Update the update the record process flag to zero. 3. delete the record which
						 * assigned for next role Code.
						 */
						if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
							finmain.setNextUserId(null);
						} else {
							TaskOwners taskowner = taskOwnersDAO.getTaskOwner(finReference, roleCode);
							if (taskowner != null) {
								finmain.setNextUserId(String.valueOf(taskowner.getCurrentOwner()));
								taskOwnersDAO.deviationReject(finReference, roleCode, nextRoleCode);
							} else {
								finmain.setNextUserId(String.valueOf(0));
							}
						}
					}

					finmain.setNextTaskId(newTaskId + ";");
					finmain.setNextRoleCode(newRoleCode);
				}

				financeMainDAO.updateDeviationApproval(finmain, rejected, "_Temp");
				deviationDetailsDAO.updateDeviProcessed(finID, "");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private FinanceDeviations getFinanceDeviationByID(List<FinanceDeviations> list, FinanceDeviations devNew) {
		for (FinanceDeviations finDeviation : list) {
			if (StringUtils.equals(devNew.getModule(), finDeviation.getModule())
					&& StringUtils.equals(devNew.getDeviationCode(), finDeviation.getDeviationCode())) {
				return finDeviation;
			}
		}
		return null;
	}

	// ### 01-05-2018 - story #361(tuleap server) Manual Deviations
	@Override
	public AuditHeader doCheckDeviationApproval(AuditHeader auditHeader) {
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String userRole = fm.getRoleCode();
		// Get the list of finance deviations that were finalized.
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		List<FinanceDeviations> financedeviations = deviationDetailsDAO.getFinanceDeviations(finID, "");
		List<FinanceDeviations> deviations = new ArrayList<>();
		for (FinanceDeviations financeDeviation : financedeviations) {
			deviations.add(financeDeviation);
		}

		// Add the pending manual deviations.
		List<FinanceDeviations> pendingDeviations = fd.getManualDeviations();

		if (pendingDeviations != null && !pendingDeviations.isEmpty()) {
			deviations.addAll(pendingDeviations);
		}

		// Add the pending auto (along with custom) deviations.
		List<FinanceDeviations> pendingAutoDeviations = fd.getFinanceDeviations();

		if (pendingAutoDeviations != null && !pendingAutoDeviations.isEmpty()) {
			deviations.addAll(pendingAutoDeviations);
		}

		// Check whether any deviations were not approved and add the error.
		for (FinanceDeviations deviation : deviations) {
			if (!StringUtils.equalsIgnoreCase(deviation.getApprovalStatus(), PennantConstants.RCD_STATUS_APPROVED)
					&& !deviation.isMarkDeleted()) {
				if (SysParamUtil.isAllowed(SMTParameterConstants.FINANCE_DEVIATION_CHECK)) {
					if (StringUtils.equalsIgnoreCase(userRole, deviation.getDelegationRole())) {
						auditDetail.setErrorDetail(new ErrorDetail("30901", null));
						auditHeader.setAuditDetail(auditDetail);
						auditHeader.setErrorList(auditDetail.getErrorDetails());
						break;
					}
				} else {
					auditDetail.setErrorDetail(new ErrorDetail("30901", null));
					auditHeader.setAuditDetail(auditDetail);
					auditHeader.setErrorList(auditDetail.getErrorDetails());
					break;
				}

			}
		}

		return auditHeader;
	}

	@Override
	public FinanceDeviations getFinanceDeviationsByIdAndFinRef(long finID, long deviationId, String type) {
		return deviationDetailsDAO.getFinanceDeviationsByIdAndFinRef(finID, deviationId, type);
	}

	private AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(FinanceDeviations.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	private AuditDetail getFinDeviationsAudit(FinanceDeviations financeDeviations, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceDeviations(),
				new FinanceDeviations().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], financeDeviations.getBefImage(),
				financeDeviations);

	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setDeviationDetailsDAO(FinanceDeviationsDAO deviationDetailsDAO) {
		this.deviationDetailsDAO = deviationDetailsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public void setFinanceScoreHeaderDAO(FinanceScoreHeaderDAO financeScoreHeaderDAO) {
		this.financeScoreHeaderDAO = financeScoreHeaderDAO;
	}

	public void setDeviationHelper(DeviationHelper deviationHelper) {
		this.deviationHelper = deviationHelper;
	}

}
