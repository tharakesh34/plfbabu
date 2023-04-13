/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : TdsReceivableServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tds.receivables.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivableDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.tds.receivables.TdsReceivableCancelService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

/**
 * Service implementation for methods that depends on <b>TdsReceivable</b>.<br>
 */
public class TdsReceivableCancelServiceImpl extends GenericService<TdsReceivable>
		implements TdsReceivableCancelService {
	private static final Logger logger = LogManager.getLogger(TdsReceivableServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private TdsReceivableDAO tdsReceivableDAO;
	private transient TdsReceivablesTxnDAO tdsReceivablesTxnDAO;

	/**
	 * @return the auditHeaderDAO
	 */

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * TDS_RECEIVABLES/TDS_RECEIVABLES_Temp by using TDS_RECEIVABLESDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using TDS_RECEIVABLESDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (tdsReceivable.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (tdsReceivable.isNew()) {
			tdsReceivable.setId(Long.parseLong(tdsReceivableDAO.save(tdsReceivable, tableType)));
			auditHeader.getAuditDetail().setModelData(tdsReceivable);
			auditHeader.setAuditReference(String.valueOf(tdsReceivable.getId()));
		} else {
			tdsReceivableDAO.update(tdsReceivable, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * TDS_RECEIVABLES by using TDS_RECEIVABLESDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		tdsReceivableDAO.delete(tdsReceivable, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getTDSReceivableDAO().delete with
	 * parameters tDSReceivable,"" b) NEW Add new record in to main table by using getTDSReceivableDAO().save with
	 * parameters tDSReceivable,"" c) EDIT Update record in the main table by using getTDSReceivableDAO().update with
	 * parameters tDSReceivable,"" 3) Delete the record from the workFlow table by using getTDSReceivableDAO().delete
	 * with parameters tDSReceivable,"_Temp" 4) Audit the record in to AuditHeader and AdtTDS_RECEIVABLES by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtTDS_RECEIVABLES
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		TdsReceivable tdsReceivable = new TdsReceivable();
		BeanUtils.copyProperties((TdsReceivable) auditHeader.getAuditDetail().getModelData(), tdsReceivable);

		tdsReceivableDAO.delete(tdsReceivable, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(tdsReceivable.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(tdsReceivableDAO.getTdsReceivable(tdsReceivable.getId(), TableType.MAIN_TAB));
		}

		if (tdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			tdsReceivableDAO.delete(tdsReceivable, TableType.MAIN_TAB);
		} else {
			tdsReceivable.setRoleCode("");
			tdsReceivable.setNextRoleCode("");
			tdsReceivable.setTaskId("");
			tdsReceivable.setNextTaskId("");
			tdsReceivable.setWorkflowId(0);

			if (tdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				tdsReceivable.setRecordType("");
				tdsReceivableDAO.save(tdsReceivable, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				tdsReceivable.setRecordType("");
				tdsReceivableDAO.update(tdsReceivable, TableType.MAIN_TAB);
				if (CollectionUtils.isNotEmpty(tdsReceivable.getTdsReceivablesTxnList())) {
					processTdsReceivablesTxn(tdsReceivable, auditDetails);
				}
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(tdsReceivable);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public void processTdsReceivablesTxn(TdsReceivable tdsReceivable, List<AuditDetail> auditDetails) {

		AuditDetail auditdetail = new AuditDetail();
		tdsReceivable.getTdsReceivablesTxnList().forEach(tdsReceivablesTxn -> {

			tdsReceivablesTxnDAO.updateReceivablesTxnStatus(tdsReceivable.getId(),
					TdsReceivablesTxnStatus.RECEIVABLECANCEL);
			auditdetail.setModelData(tdsReceivablesTxn);
			auditDetails.add(auditdetail);
		});

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getTDSReceivableDAO().delete with parameters tDSReceivable,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		tdsReceivableDAO.delete(tdsReceivable, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getTDSReceivableDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		TdsReceivable tdsReceivable = (TdsReceivable) auditDetail.getModelData();

		// Check the unique keys.
		if (tdsReceivable.isNew()) {

			int count = tdsReceivablesTxnDAO.getPendingTransactions(tdsReceivable.getId());

			if (count > 0) {
				String[] parameters = new String[1];

				parameters[0] = PennantJavaUtil.getLabel("label_CertificateNumber") + ": "
						+ tdsReceivable.getCertificateNumber();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41017", parameters, null));
			}

		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setTdsReceivableDAO(TdsReceivableDAO tdsReceivableDAO) {
		this.tdsReceivableDAO = tdsReceivableDAO;
	}

	public void setTdsReceivablesTxnDAO(TdsReceivablesTxnDAO tdsReceivablesTxnDAO) {
		this.tdsReceivablesTxnDAO = tdsReceivablesTxnDAO;
	}

}