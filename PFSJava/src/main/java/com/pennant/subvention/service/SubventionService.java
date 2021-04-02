package com.pennant.subvention.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

public class SubventionService {

	private static final Logger logger = LogManager.getLogger(SubventionService.class);
	private SubventionDetailDAO subventionDetailDAO;

	/**
	 * Processing the Subvention details
	 * 
	 * @param auditDetails
	 * @param tableType
	 * @param financeDetail
	 * @return
	 */
	public List<AuditDetail> processSubventionDetails(List<AuditDetail> auditDetails, TableType tableType,
			FinanceDetail financeDetail) {
		logger.debug("Entering");

		BigDecimal totalSubVentionAmt = getSubVentionTotalAmt(financeDetail.getFinScheduleData());

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		String type = tableType.getSuffix();

		for (int i = 0; i < auditDetails.size(); i++) {
			SubventionDetail subventionDetail = (SubventionDetail) auditDetails.get(i).getModelData();
			subventionDetail.setSubVentionAmt(totalSubVentionAmt);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				subventionDetail.setRoleCode("");
				subventionDetail.setNextRoleCode("");
				subventionDetail.setTaskId("");
				subventionDetail.setNextTaskId("");
				subventionDetail.setWorkflowId(0);
				subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (subventionDetail.isNewRecord()) {
				saveRecord = true;
				if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (subventionDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = subventionDetail.getRecordType();
				recordStatus = subventionDetail.getRecordStatus();
				subventionDetail.setRecordType("");
				subventionDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				subventionDetailDAO.save(subventionDetail, tableType);
			}

			if (updateRecord) {
				subventionDetailDAO.update(subventionDetail, tableType);
			}

			if (deleteRecord) {
				subventionDetailDAO.delete(subventionDetail, tableType);
			}

			if (approveRec) {
				subventionDetail.setRecordType(rcdType);
				subventionDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(subventionDetail);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> setSubventionDetailsAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		SubventionDetail subventionDetail = financeDetail.getFinScheduleData().getSubventionDetail();

		String[] fields = PennantJavaUtil.getFieldDetails(subventionDetail, subventionDetail.getExcludeFields());

		if (StringUtils.isEmpty(subventionDetail.getRecordType())) {
			return auditDetails;
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		subventionDetail.setWorkflowId(financeMain.getWorkflowId());
		boolean isRcdType = false;

		if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			isRcdType = true;
		} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			subventionDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			isRcdType = true;
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			subventionDetail.setNewRecord(true);
		}

		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| subventionDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}

		subventionDetail.setRecordStatus(financeMain.getRecordStatus());
		subventionDetail.setUserDetails(financeMain.getUserDetails());
		subventionDetail.setLastMntOn(financeMain.getLastMntOn());

		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], subventionDetail.getBefImage(),
				subventionDetail));

		return auditDetails;
	}

	public void delete(SubventionDetail subventionDetail, TableType tableType) {
		subventionDetailDAO.delete(subventionDetail, tableType);
	}

	public void savSubvnetion(FinScheduleData finDetail, String tableType) {
		//SubventionScheduleDetails saving
		if (CollectionUtils.isNotEmpty(finDetail.getDisbursementDetails())
				&& (StringUtils.isBlank(tableType) || "_Temp".equalsIgnoreCase(tableType))) {
			for (FinanceDisbursement financeDisbursement : finDetail.getDisbursementDetails()) {
				if (CollectionUtils.isNotEmpty(financeDisbursement.getSubventionSchedules())) {

					for (SubventionScheduleDetail scheduleDetail : financeDisbursement.getSubventionSchedules()) {
						scheduleDetail.setDisbSeqID(financeDisbursement.getDisbSeq());
						scheduleDetail.setFinReference(financeDisbursement.getFinReference());
						subventionDetailDAO.save(scheduleDetail, tableType);
					}
				}
			}
		}

		// save SubVention Amount
		if (StringUtils.isEmpty(tableType) && CollectionUtils.isNotEmpty(finDetail.getDisbursementDetails())) {

			BigDecimal totalSubVentionAmt = getSubVentionTotalAmt(finDetail);
			subventionDetailDAO.updateSubVebtionAmt(finDetail.getFinReference(), totalSubVentionAmt);
		}
	}

	public void setSubventionData(FinScheduleData scheduleData, String type) {
		setSubventionDetails(scheduleData, type);
		setSubventionScheduleDetails(scheduleData, type);

	}

	public void setSubventionDetails(FinScheduleData scheduleData, String type) {
		FinanceMain finMain = scheduleData.getFinanceMain();
		// SubventionDetails
		if (finMain.isAllowSubvention()) {
			scheduleData.setSubventionDetail(subventionDetailDAO.getSubventionDetail(finMain.getFinReference(), type));
		} else {
			scheduleData.setSubventionDetail(null);
		}
	}

	public void setSubventionScheduleDetails(FinScheduleData scheduleData, String type) {
		//		FinanceMain finMain = scheduleData.getFinanceMain();
		List<FinanceDisbursement> disbDet = scheduleData.getDisbursementDetails();
		setSubventionScheduleDetails(disbDet, type);

	}

	public void setSubventionScheduleDetails(List<FinanceDisbursement> disbDet, String type) {

		// SubventionDetails
		if (CollectionUtils.isNotEmpty(disbDet)) {
			for (FinanceDisbursement disb : disbDet) {
				List<SubventionScheduleDetail> subvnSchs = subventionDetailDAO
						.getSubventionScheduleDetails(disb.getFinReference(), disb.getDisbSeq(), type);
				disb.setSubventionSchedules(subvnSchs);
			}
		}
	}

	public BigDecimal getSubVentionTotalAmt(FinScheduleData finScheduleData) {

		BigDecimal totalSubVentionAmt = BigDecimal.ZERO;
		for (FinanceDisbursement financeDisbursement : finScheduleData.getDisbursementDetails()) {
			totalSubVentionAmt = totalSubVentionAmt.add(financeDisbursement.getSubventionAmount());
		}

		return totalSubVentionAmt;
	}

	public SubventionDetailDAO getSubventionDetailDAO() {
		return subventionDetailDAO;
	}

	public void setSubventionDetailDAO(SubventionDetailDAO subventionDetailDAO) {
		this.subventionDetailDAO = subventionDetailDAO;
	}

	public void deleteByFinReference(String finReference, String tableType) {
		getSubventionDetailDAO().deleteByFinReference(finReference, tableType);
	}
}
