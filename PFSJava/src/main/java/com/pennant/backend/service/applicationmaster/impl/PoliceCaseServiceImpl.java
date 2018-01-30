package com.pennant.backend.service.applicationmaster.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.PoliceCaseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class PoliceCaseServiceImpl extends GenericService<PoliceCaseDetail> implements PoliceCaseService {
	private static Logger logger = Logger.getLogger(PoliceCaseServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PoliceCaseDAO policeCaseDAO;
	
	public PoliceCaseServiceImpl() {
		super();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * PoliceCaseCustomers/PoliceCaseCustomers_Temp by using PoliceCaseDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PoliceCaseDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtPoliceCaseCustomers by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
	
		PoliceCaseDetail policeCaseDetail = (PoliceCaseDetail) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (policeCaseDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (policeCaseDetail.isNew()) {
			policeCaseDetail.setCustCIF(getPoliceCaseDAO().save(policeCaseDetail,tableType));
			auditHeader.getAuditDetail().setModelData(policeCaseDetail);
			auditHeader.setAuditReference(String.valueOf(policeCaseDetail.getCustCIF()));
		} else {
			getPoliceCaseDAO().update(policeCaseDetail, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	/**
	 * getPloliceCaseDetail fetch the details by using PoliceCaseDAO's getPoliceCaseDetailById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PoliceCaseDetail
	 */
	@Override
	public PoliceCaseDetail getPoliceCaseDetailById(String id) {
		return getPoliceCaseDAO().getPoliceCaseDetailById(id, "_View");
	}

	/**
	 * getApprovedAcademicById fetch the details by using PoliceCaseDAO's
	 * getPoliceCaseDetailById method . with parameter id and type as blank. it fetches
	 * the approved records from the PoliceCaseCustomers.
	 * 
	 * @param id
	 *            (String)
	 * @return Academic
	 */
	public PoliceCaseDetail getApprovedPoliceCaseDetailById(String id) {
		return getPoliceCaseDAO().getPoliceCaseDetailById(id, "");
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table PoliceCaseCustomers by using PoliceCaseDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtPoliceCaseCustomers by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PoliceCaseDetail policeCaseDetail = (PoliceCaseDetail) auditHeader.getAuditDetail().getModelData();
		getPoliceCaseDAO().delete(policeCaseDetail, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPoliceCaseDAO().delete with parameters policeCase,"" b) NEW Add new
	 * record in to main table by using getPoliceCaseDAO().save with parameters
	 * policeCase,"" c) EDIT Update record in the main table by using
	 * getPoliceCaseDAO().update with parameters policeCase,"" 3) Delete the record
	 * from the workFlow table by using getPoliceCaseDAO().delete with parameters
	 * policeCase,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTAcademics by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PoliceCaseDetail policeCaseDetail = new PoliceCaseDetail();
		BeanUtils.copyProperties((PoliceCaseDetail) auditHeader.getAuditDetail().getModelData(), policeCaseDetail);
		getPoliceCaseDAO().delete(policeCaseDetail, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(policeCaseDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(policeCaseDAO.getPoliceCaseDetailById(policeCaseDetail.getCustCIF(), ""));
		}
		
		if (policeCaseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPoliceCaseDAO().delete(policeCaseDetail, TableType.MAIN_TAB);
		} else {
			policeCaseDetail.setRoleCode("");
			policeCaseDetail.setNextRoleCode("");
			policeCaseDetail.setTaskId("");
			policeCaseDetail.setNextTaskId("");
			policeCaseDetail.setWorkflowId(0);

			if (policeCaseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				policeCaseDetail.setRecordType("");
				getPoliceCaseDAO().save(policeCaseDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				policeCaseDetail.setRecordType("");
				getPoliceCaseDAO().update(policeCaseDetail, TableType.MAIN_TAB);
			}
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(policeCaseDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getPoliceCaseDAO().delete with parameters
	 *PoliceCase,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtPoliceCaseCustomers by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PoliceCaseDetail policeCaseDetail  = (PoliceCaseDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPoliceCaseDAO().delete(policeCaseDetail, TableType.TEMP_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getPoliceCaseDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug(Literal.ENTERING);

		// Get the model object.
		PoliceCaseDetail policeCaseDetail = (PoliceCaseDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (policeCaseDetail.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(policeCaseDetail.getRecordType())
				&& policeCaseDAO
						.isDuplicateKey(policeCaseDetail.getCustCIF(), policeCaseDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];
			parameters[0] = PennantJavaUtil.getLabel("label_CustCIF") + ": " + policeCaseDetail.getCustCIF();
			
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public PoliceCaseDAO getPoliceCaseDAO() {
		return policeCaseDAO;
	}

	public void setPoliceCaseDAO(PoliceCaseDAO policeCaseDAO) {
		this.policeCaseDAO = policeCaseDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
