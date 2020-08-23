package com.pennant.backend.service.systemmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.systemmasters.PMAYDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.PMAYService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class PMAYServiceImpl extends GenericService<PMAY> implements PMAYService {
	private static final Logger logger = Logger.getLogger(PMAYServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PMAYDAO pmayDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table PMAY/PMAY_Temp by using
	 * PmayDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * PmayDAO's update method 3) Audit the record in to AuditHeader and AdtPMAYp by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		boolean flag = false;

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (pmay.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		//updating the PMAY flag to true if loan fall into any one of the category
		if (pmay != null && StringUtils.isNotBlank(pmay.getPmayCategory())) {
			if (!"NA".equals(pmay.getPmayCategory())) {
				flag = true;
			}
			financeMainDAO.updatePmay(pmay.getFinReference(), flag, TableType.MAIN_TAB.getSuffix());
		}

		if (pmay.isNew()) {
			pmay.setFinReference(getPmayDAO().save(pmay, tableType));
			auditHeader.getAuditDetail().setModelData(pmay);
			auditHeader.setAuditReference(pmay.getFinReference());
		} else {
			getPmayDAO().update(pmay, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditDetail saveOrUpdate(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		pmay.setWorkflowId(0);
		if (pmay.isNewRecord()) {
			getPmayDAO().save(pmay, tableType);
			List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
			if (pmayEligibilityLogList != null) {
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						getPmayDAO().save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						getPmayDAO().update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		} else {
			try {
				getPmayDAO().update(pmay, tableType);
			} catch (ConcurrencyException e) {
				logger.info("ConcurrencyException suppressed");
			}

			List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
			if (pmayEligibilityLogList != null) {
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						getPmayDAO().save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						getPmayDAO().update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);

	}

	@Override
	public AuditDetail doApprove(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		pmay.setRoleCode("");
		pmay.setNextRoleCode("");
		pmay.setTaskId("");
		pmay.setNextTaskId("");
		pmay.setWorkflowId(0);
		pmay.setRecordType("");
		pmay.setRecordStatus("Approved");
		getPmayDAO().save(pmay, tableType);
		List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
		for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
			if (pmayEligibilityLog.isNewRecord()) {
				pmayEligibilityLog.setFinReference(pmay.getFinReference());
				getPmayDAO().save(pmayEligibilityLog, TableType.MAIN_TAB);
			} else {
				getPmayDAO().update(pmayEligibilityLog, TableType.MAIN_TAB);
			}
		}
		//deleting the records  from temp while approve
		getPmayDAO().delete(pmay, TableType.TEMP_TAB);
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);
	}

	@Override
	public AuditDetail delete(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		getPmayDAO().delete(pmay, tableType);
		List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
		for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
			//assetBaseDAO.delete(assetBase, tableType.getSuffix());
		}
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();
		getPmayDAO().delete(pmay, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPmay fetch the details by using PmayDAO's getPMAY method.
	 * 
	 * @param finReference
	 *            finReference of PMAY.
	 * @return pmay
	 */
	@Override
	public PMAY getPMAY(String finReference, String tableType) {
		PMAY pmay = getPmayDAO().getPMAY(finReference, tableType);
		if (pmay != null) {
			List<PmayEligibilityLog> eligibilityLogList = getPmayDAO().getEligibilityLogList(finReference, "");
			pmay.setPmayEligibilityLogList(eligibilityLogList);
		}
		return pmay;
	}

	/**
	 * getApprovedPMAY fetch the details by using PmayDAO's getPMAY method . with parameter finReference and type as
	 * blank. it fetches the approved records from the PMAY.
	 * 
	 * @param finReference
	 *            finReference of the PMAY. (String)
	 * @return pmay
	 */
	@Override
	public PMAY getApprovedPMAY(String finReference) {
		return getPmayDAO().getPMAY(finReference, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPmayDAO().delete with parameters
	 * pmay,"" b) NEW Add new record in to main table by using getPmayDAO().save with parameters pmay,"" c) EDIT Update
	 * record in the main table by using getPmayDAO().update with parameters pmay,"" 3) Delete the record from the
	 * workFlow table by using getPmayDAO().delete with parameters pmay,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtPmayGroup by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtPMAY by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = new PMAY();
		BeanUtils.copyProperties((PMAY) auditHeader.getAuditDetail().getModelData(), pmay);

		getPmayDAO().delete(pmay, TableType.TEMP_TAB);
		PMAY pmayFromLoan = pmayDAO.getPMAY(pmay.getFinReference(), "");
		if (!PennantConstants.RECORD_TYPE_NEW.equals(pmay.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(pmayFromLoan);
		}

		if (pmay.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPmayDAO().delete(pmay, TableType.MAIN_TAB);
		} else {
			pmay.setRoleCode("");
			pmay.setNextRoleCode("");
			pmay.setTaskId("");
			pmay.setNextTaskId("");
			pmay.setWorkflowId(0);

			if (pmay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && pmayFromLoan == null) {
				tranType = PennantConstants.TRAN_ADD;
				pmay.setRecordType("");
				getPmayDAO().save(pmay, TableType.MAIN_TAB);
				List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {

					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						getPmayDAO().save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						getPmayDAO().update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pmay.setRecordType("");
				getPmayDAO().update(pmay, TableType.MAIN_TAB);
				List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {

					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						getPmayDAO().save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						getPmayDAO().update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pmay);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPmayDAO().delete with parameters pmay,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtPMAY by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPmayDAO().delete(pmay, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
	 * from getPmayDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign the
	 * to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		PMAY pmay = (PMAY) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_PmayDialog_FinReference.value") + ": " + pmay.getFinReference();
		// Check the unique keys.
		if (pmay.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(pmay.getRecordType()) && pmayDAO
				.isDuplicateKey(pmay.getFinReference(), pmay.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public long generateDocSeq() {
		return pmayDAO.generateDocSeq();
	}

	@Override
	public List<PmayEligibilityLog> getAllRecordIdForPmay() {
		return pmayDAO.getAllRecordIdForPmay();
	}

	@Override
	public void update(PmayEligibilityLog pmayEligibilityLog) {
		logger.debug(Literal.ENTERING);
		pmayDAO.update(pmayEligibilityLog);
		if (StringUtils.isNotEmpty(pmayEligibilityLog.getApplicantId())) {
			String custCif = pmayDAO.getCustCif(pmayEligibilityLog.getFinReference());
			pmayDAO.update(custCif, pmayEligibilityLog.getApplicantId());
		}

		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PMAYDAO getPmayDAO() {
		return pmayDAO;
	}

	public void setPmayDAO(PMAYDAO pmayDAO) {
		this.pmayDAO = pmayDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
