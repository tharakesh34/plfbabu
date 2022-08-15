package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FeeWaiverUploadHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeWaiverUploadHeaderService;
import com.pennant.backend.service.finance.FeeWaiverUploadService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FeeWaiverUploadHeaderServiceImpl extends GenericService<FeeWaiverUploadHeader>
		implements FeeWaiverUploadHeaderService {
	private static final Logger logger = LogManager.getLogger(FeeWaiverUploadHeaderServiceImpl.class);

	private FeeWaiverUploadService feeWaiverUploadService;
	private AuditHeaderDAO auditHeaderDAO;
	private FeeWaiverUploadHeaderDAO feeWaiverUploadHeaderDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	public boolean isFileNameExist(String fileName) {
		return this.feeWaiverUploadHeaderDAO.isFileNameExist(fileName);
	}

	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String finTypeCode) {
		return this.feeTypeDAO.getApprovedFeeTypeByFeeCode(finTypeCode);
	}

	@Override
	public FeeWaiverUploadHeader getUploadHeader() {
		return feeWaiverUploadHeaderDAO.getUploadHeader();
	}

	@Override
	public String getFinanceMainByRcdMaintenance(long finID) {
		return financeMainDAO.getFinanceMainByRcdMaintenance(finID);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType1 = TableType.MAIN_TAB;
		if (uploadHeader.isWorkflow()) {
			tableType1 = TableType.TEMP_TAB;
		}

		if (uploadHeader.isNewRecord()) {
			uploadHeader.setUploadId(this.feeWaiverUploadHeaderDAO.save(uploadHeader, tableType1));
			auditHeader.getAuditDetail().setModelData(uploadHeader);
			auditHeader.setAuditReference(String.valueOf(uploadHeader.getUploadId()));
		} else {
			this.feeWaiverUploadHeaderDAO.update(uploadHeader, tableType1);
		}

		if (CollectionUtils.isNotEmpty(uploadHeader.getUploadFeeWaivers())) {
			List<AuditDetail> waiverUpload = uploadHeader.getAuditDetailMap().get("WaiverUploads");

			waiverUpload = this.feeWaiverUploadService.processWaiverUploadsDetails(waiverUpload,
					uploadHeader.getUploadId(), TableType.TEMP_TAB.getSuffix());
			auditDetails.addAll(waiverUpload);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeWaiverUploadHeader uh = new FeeWaiverUploadHeader();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		BeanUtils.copyProperties((FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData(), uh);

		if (uh.getTotalRecords() != uh.getFailedCount()) {
			this.feeWaiverUploadHeaderDAO.delete(uh, TableType.TEMP_TAB);
		}

		long uploadId = uh.getUploadId();
		if (!PennantConstants.RECORD_TYPE_NEW.equals(uh.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(this.feeWaiverUploadHeaderDAO.getUploadHeaderById(uploadId, ""));
		}

		if (uh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			this.feeWaiverUploadHeaderDAO.delete(uh, TableType.MAIN_TAB);
		} else {
			uh.setRoleCode("");
			uh.setNextRoleCode("");
			uh.setTaskId("");
			uh.setNextTaskId("");
			uh.setWorkflowId(0);
			uh.setApprovedDate(SysParamUtil.getAppDate());
			uh.setApproverId(auditHeader.getAuditUsrId());

			if (PennantConstants.RECORD_TYPE_NEW.equals(uh.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				uh.setRecordType("");
				this.feeWaiverUploadHeaderDAO.save(uh, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				uh.setRecordType("");
				this.feeWaiverUploadHeaderDAO.update(uh, TableType.MAIN_TAB);
			}

			if (CollectionUtils.isNotEmpty(uh.getUploadFeeWaivers())) {
				List<AuditDetail> waiverUpload = uh.getAuditDetailMap().get("WaiverUploads");
				waiverUpload = this.feeWaiverUploadService.processWaiverUploadsDetails(waiverUpload, uploadId, "");
				auditDetails.addAll(waiverUpload);
			}
			this.feeWaiverUploadHeaderDAO.delete(uh, TableType.TEMP_TAB);

		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FeeWaiverUploadHeader(), uh.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], uh.getBefImage(), uh));
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(uh);

		auditHeaderDAO.addAudit(auditHeader);

		auditHeader = prepareChildsAudit(auditHeader, "doApprove");
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData();
		this.feeWaiverUploadHeaderDAO.delete(uploadHeader, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		this.feeWaiverUploadHeaderDAO.delete(uploadHeader, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(this.feeWaiverUploadService.validateWaiverUploads(auditHeader, method));

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> deleteChilds(FeeWaiverUploadHeader fwuh, String tableType, String aTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		List<FeeWaiverUpload> waivers = fwuh.getUploadFeeWaivers();
		if (CollectionUtils.isNotEmpty(waivers)) {
			auditDetails.addAll(this.feeWaiverUploadService.delete(waivers, tableType, aTranType, fwuh.getUploadId()));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> map = new HashMap<>();

		FeeWaiverUploadHeader uh = (FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData();
		String aTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (uh.isWorkflow()) {
				aTranType = PennantConstants.TRAN_WF;
			}
		}

		List<FeeWaiverUpload> waivers = uh.getUploadFeeWaivers();
		if (CollectionUtils.isNotEmpty(waivers)) {
			for (FeeWaiverUpload fwu : waivers) {
				fwu.setWorkflowId(uh.getWorkflowId());
				fwu.setRecordStatus(uh.getRecordStatus());
				fwu.setUserDetails(uh.getUserDetails());
				fwu.setLastMntOn(uh.getLastMntOn());
				fwu.setLastMntBy(uh.getLastMntBy());
				fwu.setRoleCode(uh.getRoleCode());
				fwu.setNextRoleCode(uh.getNextRoleCode());
				fwu.setTaskId(uh.getTaskId());
				fwu.setNextTaskId(uh.getNextTaskId());
			}

			map.put("WaiverUploads", this.feeWaiverUploadService.setWaiverUploadsAuditData(waivers, aTranType, method));
			auditDetails.addAll(map.get("WaiverUploads"));
		}

		uh.setAuditDetailMap(map);
		auditHeader.getAuditDetail().setModelData(uh);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) auditDetail.getModelData();

		if (uploadHeader.isNewRecord() && this.feeWaiverUploadHeaderDAO.isDuplicateKey(uploadHeader.getUploadId(),
				uploadHeader.getFileName(), uploadHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_MiscPostingUploadDialog_Filename.value") + ": "
					+ uploadHeader.getFileName();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", parameters)));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader.getAuditDetail().setModelData((FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData());
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public boolean isFileDownload(long uploadID, String tableType) {
		return feeWaiverUploadHeaderDAO.isFileDownload(uploadID, tableType);
	}

	@Override
	public FeeWaiverUploadHeader getUploadHeaderById(long uploadId, String type) {
		return this.feeWaiverUploadHeaderDAO.getUploadHeaderById(uploadId, type);
	}

	@Override
	public List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId) {
		return feeWaiverUploadService.getFeeWaiverListByUploadId(uploadId);
	}

	@Override
	public void updateFileDownload(long uploadId, boolean fileDownload, String type) {
		this.feeWaiverUploadHeaderDAO.updateFileDownload(uploadId, fileDownload, type);
	}

	public void setFeeWaiverUploadService(FeeWaiverUploadService feeWaiverUploadService) {
		this.feeWaiverUploadService = feeWaiverUploadService;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFeeWaiverUploadHeaderDAO(FeeWaiverUploadHeaderDAO feeWaiverUploadHeaderDAO) {
		this.feeWaiverUploadHeaderDAO = feeWaiverUploadHeaderDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
