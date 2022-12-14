package com.pennant.pff.presentment.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.RePresentmentUploadDAO;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.upload.dao.UploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadContants.Status;

public class RePresentmentUploadServiceImpl extends GenericService<FileUploadHeader>
		implements UploadService<RePresentmentUploadDetail> {
	private static final Logger logger = LogManager.getLogger(RePresentmentUploadServiceImpl.class);

	private UploadDAO uploadDAO;
	private RePresentmentUploadDAO representmentUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private EntityDAO entityDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;

	private static final String TABLE_NAME = "REPRESENT_UPLOADS";

	@Override
	public FileUploadHeader getUploadHeader(String moduleCode) {
		WorkFlowDetails workFlow = uploadDAO.getWorkFlow(moduleCode);

		FileUploadHeader header = new FileUploadHeader();

		if (workFlow != null) {
			header.setWorkflowId(workFlow.getWorkFlowId());
		}

		header.setRecordStatus("Submitted");
		header.setAppDate(SysParamUtil.getAppDate());

		return header;
	}

	@Override
	public long saveHeader(FileUploadHeader header, TableType type) {
		return uploadDAO.saveHeader(header, type);
	}

	@Override
	public void importFile(FileUploadHeader header) {
		try {
			read(header);
		} catch (Exception e) {
			int status = Status.IMPORT_FAILED.getValue();
			header.setProgress(status);
			uploadDAO.updateProgress(header.getId(), status, TableType.TEMP_TAB);
			auditHeaderDAO.addAudit(getAuditHeader(header, PennantConstants.TRAN_WF));
			return;
		}

		header.setBefImage(header);

		ExcelUtil.backUpFile(header.getFile());

		updateheader(header);

		auditHeaderDAO.addAudit(getAuditHeader(header, PennantConstants.TRAN_WF));
	}

	@Override
	public void read(FileUploadHeader header) {
		logger.info("Reading the Excel Data...");

		Workbook workBook = header.getWorkBook();
		Sheet rchSheet = workBook.getSheetAt(0);

		int rowCount = rchSheet.getLastRowNum();

		String acBounce = SysParamUtil.getValueAsString(SMTParameterConstants.BOUNCE_CODES_FOR_ACCOUNT_CLOSED);

		for (int i = 1; i <= rowCount; i++) {
			RePresentmentUploadDetail detail = new RePresentmentUploadDetail();

			Row row = rchSheet.getRow(i);

			if (row == null) {
				continue;
			}

			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					detail.setReference(cell.toString());
				}

				if (cell.getColumnIndex() == 1) {
					detail.setStrDueDate(cell.toString());
				}
			}

			detail.setAcBounce(acBounce);

			validate(header, detail);

			detail.setHeaderId(header.getId());
			header.setTotalRecords(header.getTotalRecords() + 1);
			if (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) {
				header.setSuccessRecords(header.getSuccessRecords() + 1);
			} else {
				header.setFailureRecords(header.getFailureRecords() + 1);
			}

			saveDetail(detail);
		}
	}

	@Override
	public void validate(FileUploadHeader header, RePresentmentUploadDetail detail) {
		logger.info("Validating the Excel Data...");

		Date appDate = header.getAppDate();

		int appDateMonth = DateUtil.getMonth(appDate);

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, "[FINREFERENCE] is Mandatary");
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, "[FINREFERENCE] is invalid");
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, "[FINREFERENCE] is not in active");
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		String strDueDate = detail.getStrDueDate();
		if (StringUtils.isBlank(strDueDate)) {
			setError(detail, "[DUEDATE] is Mandatary");
			return;
		}

		detail.setDueDate(DateUtil.parse(strDueDate, DateFormat.LONG_DATE.getPattern()));

		Date dueDate = detail.getDueDate();
		if (DateUtil.compare(dueDate, appDate) > 0) {
			setError(detail, "[DUEDATE] should not be Future Date.");
			return;
		}

		String bounceCode = representmentUploadDAO.getBounceCode(reference, dueDate);

		if (bounceCode == null) {
			setError(detail, "Not a valid representment.");
			return;
		}

		if (detail.getAcBounce().contains(bounceCode)) {
			setError(detail, "Unable to do the re-presenment, since Account is closed");
			return;
		}

		if (representmentUploadDAO.isProcessed(reference, dueDate)) {
			setError(detail, "receipt already proceessed for this schedule.");
			return;
		}

		if (profitDetailsDAO.getCurOddays(fm.getFinID()) == 0) {
			setError(detail, "There is no over dues for the Loan Reference : " + reference);
			return;
		}

		List<String> fileNames = representmentUploadDAO.isDuplicateExists(reference, dueDate, header.getId());

		if (!fileNames.isEmpty()) {
			StringBuilder message = new StringBuilder();

			message.append("Duplicate RePresentMent exists with combination FinReference -");
			message.append(reference);
			message.append(", Due Date -").append(dueDate);
			message.append("with filename").append(fileNames.get(0)).append("already exists");

			setError(detail, message.toString());

			logger.info("Duplicate RePresentMent found in RePresentUpload_Temp table..");
			return;
		}

		int curSchdMonth = DateUtil.getMonth(dueDate);
		if (curSchdMonth != appDateMonth) {
			setError(detail, "Due date should be in current month only");
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setRemarks("");
	}

	@Override
	public void saveDetail(RePresentmentUploadDetail detail) {
		logger.info("Saving RePresentmentUploadDetails...");
		this.representmentUploadDAO.saveDetail(detail);
	}

	@Override
	public void updateheader(FileUploadHeader header) {
		header.setProgress(Status.IMPORTED.getValue());

		logger.info("Updating RePresentMentHeader as Imported...");

		if (uploadDAO.update(header, TableType.TEMP_TAB) == 1) {
			logger.info("RePresentMentHeader {} successfully updated with successcount {}, failurecount {} ",
					header.getId(), header.getSuccessRecords(), header.getFailureRecords());
		}
	}

	@Override
	public boolean isExists(String fileName) {
		return uploadDAO.isExists(fileName);
	}

	@Override
	public boolean isDownloaded(long fileID) {
		return this.uploadDAO.isFileDownlaoded(fileID);
	}

	@Override
	public void updateProgress(long headerID, int status) {
		this.uploadDAO.updateProgress(headerID, status, TableType.MAIN_TAB);
	}

	@Override
	public void updateStatus(List<Long> headerIds) {
		for (Long id : headerIds) {
			int[] statuscount = getHeaderStatusCnt(id);
			uploadDAO.uploadHeaderStatusCnt(id, statuscount[0], statuscount[1]);

			logger.info(
					"RePresentment creation process completed for the Header -{} with Total success count{}, Total failure count{}",
					id, statuscount[0], statuscount[1]);
		}
	}

	@Override
	public FileUploadHeader getUploadHeaderById(long id, Date fromDate, Date toDate) {
		return uploadDAO.getHeaderData(id, fromDate, toDate);
	}

	@Override
	public List<RePresentmentUploadDetail> getUploadDetailById(long headerID) {
		return representmentUploadDAO.loadRecordData(headerID);
	}

	@Override
	public List<Entity> getEntities() {
		return this.entityDAO.getEntites();
	}

	@Override
	public List<RePresentmentUploadDetail> getDataForReport(long fileID) {
		return representmentUploadDAO.getDataForReport(fileID);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		FileUploadHeader header = (FileUploadHeader) ah.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (header.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (header.isNewRecord()) {
			header.setId(uploadDAO.saveHeader(header, tableType));

			ah.getAuditDetail().setModelData(header);
			ah.setAuditReference(String.valueOf(header.getId()));
		} else {
			uploadDAO.update(header, tableType);
		}

		auditHeaderDAO.addAudit(ah);
		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		FileUploadHeader header = (FileUploadHeader) ah.getAuditDetail().getModelData();
		ah.setAuditTranType(PennantConstants.TRAN_WF);

		uploadDAO.deleteDetail(header.getId(), TABLE_NAME);
		uploadDAO.deleteHeader(header, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		FileUploadHeader header = new FileUploadHeader();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), header);

		uploadDAO.deleteHeader(header, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(header.getRecordType())) {
			ah.getAuditDetail().setBefImage(uploadDAO.getHeaderData(header.getId(), null, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(header.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			uploadDAO.deleteDetail(header.getId(), TABLE_NAME);
			uploadDAO.deleteHeader(header, TableType.MAIN_TAB);
		} else {
			header.setRoleCode("");
			header.setNextRoleCode("");
			header.setTaskId("");
			header.setNextTaskId("");
			header.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(header.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				header.setRecordType("");
				uploadDAO.saveHeader(header, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				header.setRecordType("");
				uploadDAO.update(header, TableType.MAIN_TAB);
			}
		}

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(header);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;

	}

	@Override
	public AuditHeader delete(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		FileUploadHeader header = (FileUploadHeader) ah.getAuditDetail().getModelData();
		uploadDAO.deleteDetail(header.getId(), TABLE_NAME);
		uploadDAO.deleteHeader(header, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public void update(FileUploadHeader uploadHeader) {
		this.uploadDAO.update(uploadHeader);
	}

	private AuditHeader businessValidation(AuditHeader ah) {
		AuditDetail ad = validation(ah.getAuditDetail(), ah.getUsrLanguage());
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());
		ah = nextProcess(ah);
		return ah;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FileUploadHeader rpuh = (FileUploadHeader) auditDetail.getModelData();
		String code = rpuh.getFileName();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_FileName") + ": " + code;

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private int[] getHeaderStatusCnt(long uploadID) {
		List<Long> uploadIDs = uploadDAO.getHeaderStatusCnt(uploadID, TABLE_NAME);
		int sucessCount = 0;
		int failCount = 0;

		for (Long receiptId : uploadIDs) {
			if (receiptId == null) {
				failCount++;
			} else {
				sucessCount++;
			}
		}

		int[] val = new int[2];
		val[0] = sucessCount;
		val[1] = failCount;
		return val;
	}

	private AuditHeader getAuditHeader(FileUploadHeader header, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, header.getBefImage(), header);
		return new AuditHeader(String.valueOf(header.getId()), null, null, null, ad, header.getUserDetails(),
				new HashMap<>());
	}

	private void setError(RePresentmentUploadDetail detail, String message) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setRemarks(message);
	}

	@Autowired
	public void setRePresentmentUploadDAO(RePresentmentUploadDAO rePresentmentUploadDAO) {
		this.representmentUploadDAO = rePresentmentUploadDAO;
	}

	@Autowired
	public void setUploadDAO(UploadDAO uploadDAO) {
		this.uploadDAO = uploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

}
