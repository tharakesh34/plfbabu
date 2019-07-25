package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class TaxHeaderDetailsServiceImpl extends GenericService<TaxHeader> implements TaxHeaderDetailsService {
	private static final Logger logger = Logger.getLogger(TaxHeaderDetailsServiceImpl.class);

	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	@Override
	public TaxHeader saveOrUpdate(TaxHeader taxHeader, String tableType, String auditTranType) {
		logger.debug("Entering");

		if (taxHeader.isNew()) {
			getTaxHeaderDetailsDAO().save(taxHeader, tableType);
		} else {
			getTaxHeaderDetailsDAO().update(taxHeader, tableType);
		}
		processTaxHeaderDetails(taxHeader, tableType, auditTranType, false);

		logger.debug("Leaving");
		return taxHeader;
	}

	private List<AuditDetail> processTaxHeaderDetails(TaxHeader taxHeader, String tableType, String auditTranType,
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
						taxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
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
					} else if (taxDetail.isNew()) {
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
					getTaxHeaderDetailsDAO().update(taxDetail, tableType);
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
	public void deleteTaxDetails(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		getTaxHeaderDetailsDAO().delete(headerId, type);

		logger.debug(Literal.LEAVING);
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}
}
