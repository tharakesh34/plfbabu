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
 * * FileName : CostOfFundCodeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CostOfFundCodeDAO;
import com.pennant.backend.dao.applicationmaster.CostOfFundDAO;
import com.pennant.backend.dao.applicationmaster.impl.CostOfFundCodeDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CostOfFundCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CostOfFundCode</b>.<br>
 * 
 */
public class CostOfFundCodeServiceaImpl extends GenericService<CostOfFundCode> implements CostOfFundCodeService {
	private static Logger logger = LogManager.getLogger(CostOfFundCodeDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CostOfFundCodeDAO costOfFundCodeDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CostOfFundDAO costOfFundDAO;

	public CostOfFundCodeServiceaImpl() {
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

	public CostOfFundCodeDAO getCostOfFundCodeDAO() {
		return costOfFundCodeDAO;
	}

	public void setCostOfFundCodeDAO(CostOfFundCodeDAO costOfFundCodeDAO) {
		this.costOfFundCodeDAO = costOfFundCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * RMTCostOfFundCodes/RMTCostOfFundCodes_Temp by using CostOfFundCodeDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using CostOfFundCodeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtRMTCostOfFundCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		CostOfFundCode costOfFundCode = (CostOfFundCode) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (costOfFundCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (costOfFundCode.isNewRecord()) {
			costOfFundCode.setCofCode(getCostOfFundCodeDAO().save(costOfFundCode, tableType));
			auditHeader.getAuditDetail().setModelData(costOfFundCode);
			auditHeader.setAuditReference(costOfFundCode.getCofCode());
		} else {
			getCostOfFundCodeDAO().update(costOfFundCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTCostOfFundCodes by using CostOfFundCodeDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtRMTCostOfFundCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		CostOfFundCode costOfFundCode = (CostOfFundCode) auditHeader.getAuditDetail().getModelData();

		// delete from CostOfFunds
		getCostOfFundDAO().deleteByCOFCode(costOfFundCode.getCofCode(), TableType.TEMP_TAB);
		getCostOfFundDAO().deleteByCOFCode(costOfFundCode.getCofCode(), TableType.MAIN_TAB);

		// delete from CostOfFundCodes
		getCostOfFundCodeDAO().delete(costOfFundCode, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCostOfFundCodeById fetch the details by using CostOfFundCodeDAO's getCostOfFundCodeById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CostOfFundCode
	 */
	@Override
	public CostOfFundCode getCostOfFundCodeById(String id) {
		return getCostOfFundCodeDAO().getCostOfFundCodeById(id, "_View");
	}

	/**
	 * getApprovedCostOfFundCodeById fetch the details by using CostOfFundCodeDAO's getCostOfFundCodeById method . with
	 * parameter id and type as blank. it fetches the approved records from the RMTCostOfFundCodes.
	 * 
	 * @param id (String)
	 * @return CostOfFundCode
	 */
	public CostOfFundCode getApprovedCostOfFundCodeById(String id) {
		return getCostOfFundCodeDAO().getCostOfFundCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCostOfFundCodeDAO().delete with
	 * parameters costOfFundCode,"" b) NEW Add new record in to main table by using getCostOfFundCodeDAO().save with
	 * parameters costOfFundCode,"" c) EDIT Update record in the main table by using getCostOfFundCodeDAO().update with
	 * parameters costOfFundCode,"" 3) Delete the record from the workFlow table by using getCostOfFundCodeDAO().delete
	 * with parameters costOfFundCode,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTCostOfFundCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtRMTCostOfFundCodes by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tranType = "";
		CostOfFundCode costOfFundCode = new CostOfFundCode();
		BeanUtils.copyProperties((CostOfFundCode) auditHeader.getAuditDetail().getModelData(), costOfFundCode);

		getCostOfFundCodeDAO().delete(costOfFundCode, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(costOfFundCode.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(costOfFundCodeDAO.getCostOfFundCodeById(costOfFundCode.getCofCode(), ""));
		}

		if (costOfFundCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;

			// delete from CostOfFund Rates (Table : CostOfFunds)
			getCostOfFundDAO().deleteByCOFCode(costOfFundCode.getCofCode(), TableType.TEMP_TAB);
			getCostOfFundDAO().deleteByCOFCode(costOfFundCode.getCofCode(), TableType.MAIN_TAB);

			// delete from CostOfFundCodes
			getCostOfFundCodeDAO().delete(costOfFundCode, TableType.MAIN_TAB);

		} else {
			costOfFundCode.setRoleCode("");
			costOfFundCode.setNextRoleCode("");
			costOfFundCode.setTaskId("");
			costOfFundCode.setNextTaskId("");
			costOfFundCode.setWorkflowId(0);

			if (costOfFundCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				costOfFundCode.setRecordType("");
				getCostOfFundCodeDAO().save(costOfFundCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				costOfFundCode.setRecordType("");
				getCostOfFundCodeDAO().update(costOfFundCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(costOfFundCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCostOfFundCodeDAO().delete with parameters costOfFundCode,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtRMTCostOfFundCodes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CostOfFundCode costOfFundCode = (CostOfFundCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCostOfFundCodeDAO().delete(costOfFundCode, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * from getCostOfFundCodeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		CostOfFundCode costOfFundCode = (CostOfFundCode) auditDetail.getModelData();

		// Check the unique keys.
		if (costOfFundCode != null && costOfFundCode.isNewRecord()
				&& PennantConstants.RECORD_TYPE_NEW.equals(costOfFundCode.getRecordType())
				&& costOfFundCodeDAO.isDuplicateKey(costOfFundCode.getCofCode(),
						costOfFundCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CofCode") + ": " + costOfFundCode.getCofCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		// Dependency Validation : checking COF Code in RMTFinanceTypes
		if (costOfFundCode != null
				&& StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, costOfFundCode.getRecordType())) {
			boolean isExists = getFinanceTypeDAO().isCostOfFundsExist(costOfFundCode.getCofCode(), "_View");
			if (isExists) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_CofCode") + " : " + costOfFundCode.getCofCode();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public CostOfFundDAO getCostOfFundDAO() {
		return costOfFundDAO;
	}

	public void setCostOfFundDAO(CostOfFundDAO costOfFundDAO) {
		this.costOfFundDAO = costOfFundDAO;
	}
}