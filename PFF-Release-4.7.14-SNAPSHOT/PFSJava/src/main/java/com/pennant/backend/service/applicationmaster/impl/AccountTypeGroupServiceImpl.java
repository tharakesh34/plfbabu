package com.pennant.backend.service.applicationmaster.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AccountTypeGroupDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AccountTypeGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public class AccountTypeGroupServiceImpl extends GenericService<AccountTypeGroup> implements AccountTypeGroupService {

	private static Logger		logger	= Logger.getLogger(AccountTypeGroupServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private AccountTypeGroupDAO	accountTypeGroupDAO;

	public AccountTypeGroupServiceImpl() {
		super();
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

	public AccountTypeGroupDAO getAccountTypeGroupDAO() {
		return accountTypeGroupDAO;
	}

	public void setAccountTypeGroupDAO(AccountTypeGroupDAO accountTypeGroupDAO) {
		this.accountTypeGroupDAO = accountTypeGroupDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTAggrementDef/BMTAggrementDef_Temp by using AccountTypeGroupDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using AccountTypeGroupDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader)
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
		TableType tableType = TableType.MAIN_TAB;
		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();

		if (accountTypeGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (accountTypeGroup.isNew()) {
			getAccountTypeGroupDAO().save(accountTypeGroup, tableType);
		} else {
			getAccountTypeGroupDAO().update(accountTypeGroup, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTAggrementDef by using AccountTypeGroupDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader)
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

		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();
		getAccountTypeGroupDAO().delete(accountTypeGroup, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAccountTypeGroupById fetch the details by using AccountTypeGroupDAO's getAccountTypeGroupById method.
	 * 
	 * @param id
	 *            (long)
	 * @param type
	 *            (long) ""/_Temp/_View
	 * @return AccountTypeGroup
	 */
	@Override
	public AccountTypeGroup getAccountTypeGroupById(long id) {
		return getAccountTypeGroupDAO().getAccountTypeGroupById(id, "_View");
	}

	/**
	 * getApprovedAccountTypeGroupById fetch the details by using AccountTypeGroupDAO's getAccountTypeGroupById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTAggrementDef.
	 * 
	 * @param id
	 *            (String)
	 * @return AccountTypeGroup
	 */
	public AccountTypeGroup getApprovedAccountTypeGroupById(long id) {
		return getAccountTypeGroupDAO().getAccountTypeGroupById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAccountTypeGroupDAO().delete with
	 * parameters accountTypeGroup,"" b) NEW Add new record in to main table by using getAccountTypeGroupDAO().save with
	 * parameters accountTypeGroup,"" c) EDIT Update record in the main table by using getAccountTypeGroupDAO().update
	 * with parameters accountTypeGroup,"" 3) Delete the record from the workFlow table by using
	 * getAccountTypeGroupDAO().delete with parameters accountTypeGroup,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
			return auditHeader;
		}

		AccountTypeGroup accountTypeGroup = new AccountTypeGroup();
		BeanUtils.copyProperties((AccountTypeGroup) auditHeader.getAuditDetail().getModelData(), accountTypeGroup);
		
		getAccountTypeGroupDAO().delete(accountTypeGroup, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(accountTypeGroup.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(accountTypeGroupDAO.getAccountTypeGroupById(accountTypeGroup.getGroupId(), ""));
		}

		if (accountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getAccountTypeGroupDAO().delete(accountTypeGroup, TableType.MAIN_TAB);

		} else {
			accountTypeGroup.setRoleCode("");
			accountTypeGroup.setNextRoleCode("");
			accountTypeGroup.setTaskId("");
			accountTypeGroup.setNextTaskId("");
			accountTypeGroup.setWorkflowId(0);

			if (accountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountTypeGroup.setRecordType("");
				getAccountTypeGroupDAO().save(accountTypeGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountTypeGroup.setRecordType("");
				getAccountTypeGroupDAO().update(accountTypeGroup, TableType.MAIN_TAB);
			}
		}
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountTypeGroup);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAccountTypeGroupDAO().delete with parameters accountTypeGroup,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountTypeGroupDAO().delete(accountTypeGroup, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getAccountTypeGroupDAO().getErrorDetail with Error ID
	 * and language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAddressTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditDetail.getModelData();
		// Check the unique keys.
		if (accountTypeGroup.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(accountTypeGroup.getRecordType())
				&& accountTypeGroupDAO.isDuplicateKey(accountTypeGroup.getGroupCode(),
						accountTypeGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_AccountTypeGroupSearch_GroupCode") + ":" + accountTypeGroup.getGroupCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001", parameters, null));
		}
	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}
