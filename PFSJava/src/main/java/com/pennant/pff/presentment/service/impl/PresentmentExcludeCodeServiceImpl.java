package com.pennant.pff.presentment.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.presentment.dao.PresentmentExcludeCodeDAO;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.presentment.service.PresentmentExcludeCodeService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public class PresentmentExcludeCodeServiceImpl extends GenericService<PresentmentExcludeCode>
		implements PresentmentExcludeCodeService {
	private static Logger logger = LogManager.getLogger(PresentmentExcludeCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PresentmentExcludeCodeDAO presentmentExcludeCodeDAO;

	public PresentmentExcludeCodeServiceImpl() {
		super();
	}

	@Override
	public PresentmentExcludeCode getExcludeCode(long Id) {
		return this.presentmentExcludeCodeDAO.getExcludeCode(Id);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);

		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		PresentmentExcludeCode code = (PresentmentExcludeCode) ah.getAuditDetail().getModelData();

		TableType type = TableType.MAIN_TAB;
		if (code.isWorkflow()) {
			type = TableType.TEMP_TAB;
		}

		if (code.isNewRecord()) {
			code.setCode(String.valueOf(this.presentmentExcludeCodeDAO.save(code, type)));
			ah.getAuditDetail().setModelData(code);
			ah.setAuditReference(String.valueOf(code.getCode()));
		} else {
			this.presentmentExcludeCodeDAO.update(code, type);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new PresentmentExcludeCode(), code.getExcludeFields());
		ah.setAuditDetail(new AuditDetail(ah.getAuditTranType(), 1, fields[0], fields[1], code.getBefImage(), code));

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
			logger.debug("Leaving");
			return ah;
		}

		PresentmentExcludeCode code = new PresentmentExcludeCode();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), code);

		this.presentmentExcludeCodeDAO.delete(code, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(code.getRecordType())) {
			ah.getAuditDetail().setBefImage(presentmentExcludeCodeDAO.getExcludeCode(code.getId()));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(code.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			this.presentmentExcludeCodeDAO.delete(code, TableType.MAIN_TAB);
		} else {
			code.setRoleCode("");
			code.setNextRoleCode("");
			code.setTaskId("");
			code.setNextTaskId("");
			code.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(code.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				code.setRecordType("");
				this.presentmentExcludeCodeDAO.save(code, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				code.setRecordType("");
				this.presentmentExcludeCodeDAO.update(code, TableType.MAIN_TAB);
			}
		}

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(code);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		PresentmentExcludeCode code = (PresentmentExcludeCode) auditHeader.getAuditDetail().getModelData();
		presentmentExcludeCodeDAO.delete(code, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		PresentmentExcludeCode code = (PresentmentExcludeCode) ah.getAuditDetail().getModelData();

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		presentmentExcludeCodeDAO.delete(code, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);
		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditHeader businessValidation(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		AuditDetail ad = validation(ah.getAuditDetail(), ah.getUsrLanguage());
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());
		ah = nextProcess(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditDetail validation(AuditDetail ah, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		PresentmentExcludeCode code = (PresentmentExcludeCode) ah.getModelData();

		String excludeCode = code.getCode();
		String instrumentType = code.getInstrumentType();

		if (code.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(code.getRecordType())
				&& presentmentExcludeCodeDAO.isDuplicateKey(excludeCode, instrumentType,
						code.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantJavaUtil.getLabel("label_PresentmentExcludeDialog_InstrumentType.value").concat(": ")
					.concat(instrumentType);
			valueParm[1] = PennantJavaUtil.getLabel("label_BounceCodeDialog_Code.value").concat(": ")
					.concat(excludeCode);
			ah.setErrorDetail(new ErrorDetail("41018", valueParm));

		}

		ah.setErrorDetails(ErrorUtil.getErrorDetails(ah.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public List<PresentmentExcludeCode> getPresentmentExcludeCodes(List<String> roleCodes) {
		return this.presentmentExcludeCodeDAO.getPresentmentExcludeCodes(roleCodes);
	}

	@Override
	public List<PresentmentExcludeCode> getResult(ISearch search) {
		return this.presentmentExcludeCodeDAO.getResult(search);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setPresentmentExcludeCodeDAO(PresentmentExcludeCodeDAO presentmentExcludeCodeDAO) {
		this.presentmentExcludeCodeDAO = presentmentExcludeCodeDAO;
	}

	@Override
	public List<ReportListDetail> getPrintCodes(List<String> roleCodes) {
		return this.presentmentExcludeCodeDAO.getPrintCodes(roleCodes);
	}

}
