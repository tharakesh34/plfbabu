package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class TaxHeaderDetailsServiceImpl extends GenericService<TaxHeader> implements TaxHeaderDetailsService {
	private static final Logger logger = LogManager.getLogger(TaxHeaderDetailsServiceImpl.class);

	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	@Override
	public TaxHeader saveOrUpdate(TaxHeader taxHeader, String tableType, String auditTranType) {
		logger.debug("Entering");

		if (taxHeader.isNewRecord()) {
			getTaxHeaderDetailsDAO().save(taxHeader, tableType);
		} else {
			getTaxHeaderDetailsDAO().update(taxHeader, tableType);
		}

		if (taxHeader.getTaxDetails() != null) {
			if (CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
				for (Taxes taxes : taxHeader.getTaxDetails()) {
					taxes.setWorkflowId(0);
					taxes.setNewRecord(taxHeader.isNewRecord());
					taxes.setRecordType(taxHeader.getRecordType());
					taxes.setRecordStatus(taxHeader.getRecordStatus());
					taxes.setLastMntBy(taxHeader.getLastMntBy());
					taxes.setLastMntOn(taxHeader.getLastMntOn());
				}
			}
		}
		processTaxHeaderChildDetails(taxHeader, tableType, auditTranType, false);

		logger.debug("Leaving");
		return taxHeader;
	}

	@Override
	public TaxHeader getTaxHeaderById(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		TaxHeader header = null;
		if (headerId > 0) {
			header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(headerId, type);
			if (header != null) {
				header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(headerId, type));
			}
		}

		logger.debug(Literal.LEAVING);

		return header;
	}

	@Override
	public TaxHeader doApprove(TaxHeader taxHeader, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		if (PennantConstants.RECORD_TYPE_DEL.equals(taxHeader.getRecordType())) {
			delete(taxHeader, tableType);
		} else {
			taxHeader.setRoleCode("");
			taxHeader.setNextRoleCode("");
			taxHeader.setTaskId("");
			taxHeader.setNextTaskId("");
			taxHeader.setWorkflowId(0);

			if (taxHeader.getTaxDetails() != null) {
				if (CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
					for (Taxes taxes : taxHeader.getTaxDetails()) {
						//taxHeaderDetailsDAO.getTaxDetailById(taxes.getId(), tableType);
						taxes.setNewRecord(taxHeader.isNewRecord());
						taxes.setWorkflowId(0);
						taxes.setRecordType(taxHeader.getRecordType());
						taxes.setRecordStatus(taxHeader.getRecordStatus());
						taxes.setLastMntBy(taxHeader.getLastMntBy());
						taxes.setLastMntOn(taxHeader.getLastMntOn());
					}
				}
			}

			if (PennantConstants.RECORD_TYPE_NEW.equals(taxHeader.getRecordType())) {
				taxHeader.setRecordType("");
				taxHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getTaxHeaderDetailsDAO().save(taxHeader, tableType);
			} else {
				taxHeader.setRecordType("");
				getTaxHeaderDetailsDAO().update(taxHeader, tableType);
			}

			List<AuditDetail> auditDetails = new ArrayList<>();
			auditDetails.addAll(processTaxHeaderChildDetails(taxHeader, tableType, auditTranType, true));
		}

		if (!taxHeader.isNewRecord()) {
			delete(taxHeader, TableType.TEMP_TAB.getSuffix());
		}

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	@Override
	public TaxHeader doReject(TaxHeader taxHeader) {
		logger.debug(Literal.ENTERING);

		delete(taxHeader, TableType.TEMP_TAB.getSuffix());

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	private List<AuditDetail> processTaxHeaderChildDetails(TaxHeader taxHeader, String tableType, String auditTranType,
			boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		if (CollectionUtils.isNotEmpty(taxDetails)) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (Taxes taxDetail : taxDetails) {
				taxDetail.setReferenceId(taxHeader.getHeaderId());
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType)) {
					approveRec = true;
					taxDetail.setRoleCode("");
					taxDetail.setNextRoleCode("");
					taxDetail.setTaskId("");
					taxDetail.setNextTaskId("");
				}

				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(taxDetail.getRecordType())) {
					deleteRecord = true;
				} else if (taxDetail.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(taxDetail.getRecordType())) {
						taxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(taxDetail.getRecordType())) {
						taxDetail.setRecordType(PennantConstants.RCD_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(taxDetail.getRecordType())) {
						taxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(taxDetail.getRecordType())) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(taxDetail.getRecordType())) {
					updateRecord = true;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(taxDetail.getRecordType())) {
					if (approveRec) {
						deleteRecord = true;
					} else if (taxDetail.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = taxDetail.getRecordType();
					recordStatus = taxDetail.getRecordStatus();
					taxDetail.setRecordType("");
					taxDetail.setWorkflowId(0);
					taxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				if (saveRecord) {
					taxDetail.setId(getTaxHeaderDetailsDAO().save(taxDetail, tableType));
				}

				if (updateRecord) {
					if (taxDetail.getId() > 0) {
						getTaxHeaderDetailsDAO().update(taxDetail, tableType);
					}
				}

				if (deleteRecord) {
					getTaxHeaderDetailsDAO().delete(taxDetail.getId(), tableType);
				}

				if (approveRec) {
					taxDetail.setRecordType(rcdType);
					taxDetail.setRecordStatus(recordStatus);
				}

				String[] fields = PennantJavaUtil.getFieldDetails(taxDetail, taxDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], taxDetail.getBefImage(),
						taxDetail));
				i++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public void delete(TaxHeader taxHeader, String type) {
		logger.debug(Literal.ENTERING);

		getTaxHeaderDetailsDAO().delete(taxHeader.getHeaderId(), type);
		getTaxHeaderDetailsDAO().delete(taxHeader, type);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		getTaxHeaderDetailsDAO().delete(headerId, type);
		getTaxHeaderDetailsDAO().delete(new TaxHeader(headerId), type);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteTaxDetails(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		getTaxHeaderDetailsDAO().deleteById(headerId, type);

		logger.debug(Literal.LEAVING);
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}
}
