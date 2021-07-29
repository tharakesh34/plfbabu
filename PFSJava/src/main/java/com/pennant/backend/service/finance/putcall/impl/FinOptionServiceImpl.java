package com.pennant.backend.service.finance.putcall.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.putcall.FinOptionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinOptionServiceImpl extends GenericService<FinOption> implements FinOptionService {
	private static final Logger logger = LogManager.getLogger(FinOptionServiceImpl.class);

	private FinOptionDAO finOptionDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDetailsService customerDetailsService;

	@Override
	public List<FinOption> getFinOptions(String finreference, TableType tableType) {
		return finOptionDAO.getFinOptions(finreference, tableType);
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinOption> FinOptions, TableType tableType, String auditTranType) {
		return doProcess(FinOptions, tableType, auditTranType, false);
	}

	@Override
	public List<AuditDetail> doApprove(List<FinOption> FinOptions, TableType tableType, String auditTranType) {
		return doProcess(FinOptions, tableType, auditTranType, true);
	}

	@Override
	public List<AuditDetail> delete(List<FinOption> FinOptions, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (FinOptions != null && !FinOptions.isEmpty()) {
			int auditSeq = 1;
			for (FinOption finOption : FinOptions) {
				finOptionDAO.delete(finOption, tableType);
				fields = PennantJavaUtil.getFieldDetails(finOption, finOption.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], finOption.getBefImage(),
						finOption));
				auditSeq++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<FinOption> finOptions, long workflowId, String method, String auditTranType,
			String usrLanguage) {
		return doValidation(finOptions, workflowId, method, auditTranType, usrLanguage);
	}

	private List<AuditDetail> doValidation(List<FinOption> finOptions, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(finOptions)) {
			return auditDetails;
		}

		List<AuditDetail> list = getAuditDetails(finOptions, auditTranType, method, workflowId);
		auditDetails.addAll(validateFinOptions(list, method, usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validateFinOptions(List<AuditDetail> auditDetails, String usrLanguage, String method) {
		List<AuditDetail> aAuditDetails = new ArrayList<>();
		logger.debug(Literal.ENTERING);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, usrLanguage, method);
			aAuditDetails.add(auditDetail);
		}

		return aAuditDetails;
	}

	private void validate(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());
		FinOption finOption = (FinOption) auditDetail.getModelData();
		FinOption tempFinOption = null;

		if (finOption.isWorkflow()) {
			tempFinOption = finOptionDAO.getFinOption(finOption.getId(), TableType.TEMP_TAB);
		}
		FinOption befFinOption = finOptionDAO.getFinOption(finOption.getId(), TableType.MAIN_TAB);
		FinOption oldFinOption = finOption.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finOption.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (finOption.isNewRecord()) {
			if (!finOption.isWorkflow()) {
				if (befFinOption != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (finOption.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befFinOption != null || tempFinOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befFinOption == null || tempFinOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!finOption.isWorkflow()) {
				if (befFinOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinOption != null && !oldFinOption.getLastMntOn().equals(befFinOption.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempFinOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinOption != null && oldFinOption != null
						&& !oldFinOption.getLastMntOn().equals(tempFinOption.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finOption.isWorkflow()) {
			auditDetail.setBefImage(befFinOption);
		}
	}

	private List<AuditDetail> getAuditDetails(List<FinOption> finOptions, String auditTranType, String method,
			long workFlowId) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;
		for (FinOption finOption : finOptions) {

			if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finOption.getRecordStatus())
					.equals(PennantConstants.RCD_STATUS_SAVED)) {
				if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finOption.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_DEL)) {
					finOption.setWorkflowId(0);
					finOption.setNewRecord(true);
				} else {
					finOption.setWorkflowId(0);
				}
			} else {
				finOption.setWorkflowId(workFlowId);
			}
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(finOption.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (StringUtils.equalsIgnoreCase(finOption.getRecordType(), PennantConstants.RCD_ADD)) {
				finOption.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finOption.getRecordType())) {
				finOption.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOption.getRecordType())) {
				finOption.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finOption.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finOption, finOption.getExcludeFields());
			if (StringUtils.isNotEmpty(finOption.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finOption.getBefImage(), finOption));
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFinOptions(List<FinOption> FinOptions, TableType tableType, String auditTranType,
			boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(FinOptions)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (FinOption finOption : FinOptions) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(finOption.getRecordType()))) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType.name())) {
				approveRec = true;
				finOption.setRoleCode("");
				finOption.setNextRoleCode("");
				finOption.setTaskId("");
				finOption.setNextTaskId("");
			}

			finOption.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(finOption.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finOption.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(finOption.getRecordType())) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOption.getRecordType())) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finOption.getRecordType())) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(finOption.getRecordType(), (PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(finOption.getRecordType(), (PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(finOption.getRecordType(), (PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finOption.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finOption.getRecordType();
				recordStatus = finOption.getRecordStatus();
				finOption.setRecordType("");
				finOption.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finOptionDAO.delete(finOption, TableType.TEMP_TAB);
			}
			if (saveRecord) {
				finOptionDAO.save(finOption, tableType);
			}

			if (updateRecord) {
				finOptionDAO.update(finOption, tableType);
			}

			if (deleteRecord) {
				finOptionDAO.delete(finOption, tableType);
			}

			if (approveRec) {
				finOption.setRecordType(rcdType);
				finOption.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(finOption, finOption.getExcludeFields());
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finOption.getBefImage(), finOption));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doProcess(List<FinOption> FinOptions, TableType tableType, String auditTranType,
			boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processFinOptions(FinOptions, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	public void setfinOptionDAO(FinOptionDAO finOptionDAO) {
		this.finOptionDAO = finOptionDAO;
	}

	@Override
	public FinanceDetail getFinanceDetailById(String finreference, String type, String userRole, String moduleDefiner,
			String eventCodeRef) {
		logger.debug(Literal.ENTERING);

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finreference);
		scheduleData.setFinanceMain(financeMainDAO.getFinanceMainById(finreference, type, false));
		scheduleData.setFinanceType(
				financeTypeDAO.getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

		//Finance Schedule Details
		scheduleData
				.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finreference, type, false));

		//Finance Customer Details			
		if (scheduleData.getFinanceMain().getCustID() != 0
				&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(customerDetailsService
					.getCustomerDetailsById(scheduleData.getFinanceMain().getCustID(), true, "_View"));
		}

		List<FinOption> finOption = finOptionDAO.getFinOptions(finreference, TableType.VIEW);

		financeDetail.setFinOptions(finOption);

		return financeDetail;
	}

}
