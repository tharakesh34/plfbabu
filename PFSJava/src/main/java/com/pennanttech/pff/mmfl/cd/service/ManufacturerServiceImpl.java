package com.pennanttech.pff.mmfl.cd.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.Manufacturer;
import com.pennattech.pff.mmfl.cd.dao.ManufacturerDAO;

public class ManufacturerServiceImpl extends GenericService<Manufacturer> implements ManufacturerService {
	private static final Logger logger = LogManager.getLogger(ManufacturerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ManufacturerDAO manufacturerDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		Manufacturer manufacturer = (Manufacturer) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (manufacturer.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (manufacturer.isNew()) {
			manufacturer.setManufacturerId(Long.parseLong(manufacturerDAO.save(manufacturer, tableType)));
			auditHeader.getAuditDetail().setModelData(manufacturer);
			auditHeader.setAuditReference(String.valueOf(manufacturer.getManufacturerId()));
		} else {
			manufacturerDAO.update(manufacturer, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Manufacturer manufacturer = (Manufacturer) auditHeader.getAuditDetail().getModelData();
		manufacturerDAO.delete(manufacturer, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public Manufacturer getManufacturer(long id) {
		return manufacturerDAO.getManufacturer(id, "_View");
	}

	public Manufacturer getApprovedManufacturer(long id) {
		return manufacturerDAO.getManufacturer(id, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Manufacturer manufacturer = new Manufacturer();
		BeanUtils.copyProperties((Manufacturer) auditHeader.getAuditDetail().getModelData(), manufacturer);

		manufacturerDAO.delete(manufacturer, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(manufacturer.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(manufacturerDAO.getManufacturer(manufacturer.getManufacturerId(), ""));
		}

		if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			manufacturerDAO.delete(manufacturer, TableType.MAIN_TAB);
		} else {
			manufacturer.setRoleCode("");
			manufacturer.setNextRoleCode("");
			manufacturer.setTaskId("");
			manufacturer.setNextTaskId("");
			manufacturer.setWorkflowId(0);

			if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manufacturer.setRecordType("");
				manufacturerDAO.save(manufacturer, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manufacturer.setRecordType("");
				manufacturerDAO.update(manufacturer, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manufacturer);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Manufacturer manufacturer = (Manufacturer) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		manufacturerDAO.delete(manufacturer, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		Manufacturer manufacturer = (Manufacturer) auditDetail.getModelData();
		long code = manufacturer.getManufacturerId();

		// Check the unique keys.
		if (manufacturer.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(manufacturer.getRecordType())
				&& manufacturerDAO.isDuplicateKey(manufacturer,
						manufacturer.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("listheader_ManufacturerName.label") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setManufacturerDAO(ManufacturerDAO manufacturerDAO) {
		this.manufacturerDAO = manufacturerDAO;
	}

}
