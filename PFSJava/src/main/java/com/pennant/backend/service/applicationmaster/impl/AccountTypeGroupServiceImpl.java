package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AccountTypeGroupDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AccountTypeGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

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
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();

		if (accountTypeGroup.isWorkflow()) {
			tableType = "_Temp";
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
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();
		getAccountTypeGroupDAO().delete(accountTypeGroup, "");

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
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		AccountTypeGroup accountTypeGroup = new AccountTypeGroup();
		BeanUtils.copyProperties((AccountTypeGroup) auditHeader.getAuditDetail().getModelData(), accountTypeGroup);

		if (accountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getAccountTypeGroupDAO().delete(accountTypeGroup, "");

		} else {
			accountTypeGroup.setRoleCode("");
			accountTypeGroup.setNextRoleCode("");
			accountTypeGroup.setTaskId("");
			accountTypeGroup.setNextTaskId("");
			accountTypeGroup.setWorkflowId(0);

			if (accountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountTypeGroup.setRecordType("");
				getAccountTypeGroupDAO().save(accountTypeGroup, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountTypeGroup.setRecordType("");
				getAccountTypeGroupDAO().update(accountTypeGroup, "");
			}
		}

		getAccountTypeGroupDAO().delete(accountTypeGroup, "_Temp");
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
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountTypeGroupDAO().delete(accountTypeGroup, "_Temp");

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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		AccountTypeGroup accountTypeGroup = (AccountTypeGroup) auditDetail.getModelData();

		AccountTypeGroup tempAccountTypeGroup = null;
		if (accountTypeGroup.isWorkflow()) {
			tempAccountTypeGroup = getAccountTypeGroupDAO().getAccountTypeGroupById(accountTypeGroup.getGroupId(),
					"_Temp");
		}
		AccountTypeGroup befAccountTypeGroup = getAccountTypeGroupDAO().getAccountTypeGroupById(
				accountTypeGroup.getGroupId(), "");
		AccountTypeGroup oldAccountTypeGroup = accountTypeGroup.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = accountTypeGroup.getGroupCode();
		errParm[0] = PennantJavaUtil.getLabel("label_AccountTypeGroupSearch_GroupCode") + ":" + valueParm[0];

		if (accountTypeGroup.isNew()) { // for New record or new record into work flow

			if (!accountTypeGroup.isWorkflow()) {// With out Work flow only new records  
				if (befAccountTypeGroup != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (accountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befAccountTypeGroup != null || tempAccountTypeGroup != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befAccountTypeGroup == null || tempAccountTypeGroup != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!accountTypeGroup.isWorkflow()) { // With out Work flow for update and delete

				if (befAccountTypeGroup == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldAccountTypeGroup != null
							&& !oldAccountTypeGroup.getLastMntOn().equals(befAccountTypeGroup.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempAccountTypeGroup == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempAccountTypeGroup != null && oldAccountTypeGroup != null
						&& !oldAccountTypeGroup.getLastMntOn().equals(tempAccountTypeGroup.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !accountTypeGroup.isWorkflow()) {
			accountTypeGroup.setBefImage(befAccountTypeGroup);
		}

		return auditDetail;
	}

}
