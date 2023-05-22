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
 * * FileName : CostOfFundServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CostOfFundDAO;
import com.pennant.backend.dao.applicationmaster.impl.CostOfFundDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CostOfFundService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CostOfFund</b>.<br>
 * 
 */
public class CostOfFundServiceImpl extends GenericService<CostOfFund> implements CostOfFundService {
	private static Logger logger = LogManager.getLogger(CostOfFundDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CostOfFundDAO costOfFundDAO;
	private FinanceTypeDAO financeTypeDAO;

	public CostOfFundServiceImpl() {
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

	public CostOfFundDAO getCostOfFundDAO() {
		return costOfFundDAO;
	}

	public void setCostOfFundDAO(CostOfFundDAO costOfFundDAO) {
		this.costOfFundDAO = costOfFundDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * RMTCostOfFunds/RMTCostOfFunds_Temp by using CostOfFundDAO's save method b) Update the Record in the table. based
	 * on the module workFlow Configuration. by using CostOfFundDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRMTCostOfFunds by using auditHeaderDAO.addAudit(auditHeader)
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

		CostOfFund costOfFund = (CostOfFund) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (costOfFund.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (costOfFund.isNewRecord()) {
			getCostOfFundDAO().save(costOfFund, tableType);
			auditHeader.getAuditDetail().setModelData(costOfFund);
			auditHeader.setAuditReference(costOfFund.getCofCode() + PennantConstants.KEY_SEPERATOR
					+ DateUtil.format(costOfFund.getCofEffDate(), PennantConstants.DBDateFormat));
		} else {
			getCostOfFundDAO().update(costOfFund, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTCostOfFunds by using CostOfFundDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtRMTCostOfFunds by using auditHeaderDAO.addAudit(auditHeader)
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

		CostOfFund costOfFund = (CostOfFund) auditHeader.getAuditDetail().getModelData();
		getCostOfFundDAO().delete(costOfFund, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCostOfFundById fetch the details by using CostOfFundDAO's getCostOfFundById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CostOfFund
	 */
	@Override
	public CostOfFund getCostOfFundById(String cofCode, String currency, Date cofEffDate) {
		return getCostOfFundDAO().getCostOfFundById(cofCode, currency, cofEffDate, "_View");
	}

	/**
	 * getApprovedCostOfFundById fetch the details by using CostOfFundDAO's getCostOfFundById method . with parameter id
	 * and type as blank. it fetches the approved records from the RMTCostOfFunds.
	 * 
	 * @param id (String)
	 * @return CostOfFund
	 */
	public CostOfFund getApprovedCostOfFundById(String cofCode, String currency, Date cofEffDate) {
		return getCostOfFundDAO().getCostOfFundById(cofCode, currency, cofEffDate, "_AView");
	}

	/**
	 * getCostOfFundDelById fetch the details by using CostOfFundDAO's getCostOfFundDelById method.
	 * 
	 * @param id   (String)
	 * @param type (String) _View
	 * @return CostOfFund
	 */
	@Override
	public boolean getCostOfFundListById(String cofCode, String currency, Date cofEffDate) {
		return getCostOfFundDAO().getCostOfFundListById(cofCode, currency, cofEffDate, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCostOfFundDAO().delete with
	 * parameters costOfFund,"" b) NEW Add new record in to main table by using getCostOfFundDAO().save with parameters
	 * costOfFund,"" c) EDIT Update record in the main table by using getCostOfFundDAO().update with parameters
	 * costOfFund,"" 3) Delete the record from the workFlow table by using getCostOfFundDAO().delete with parameters
	 * costOfFund,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTCostOfFunds by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtRMTCostOfFunds by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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

		CostOfFund costOfFund = new CostOfFund();
		BeanUtils.copyProperties((CostOfFund) auditHeader.getAuditDetail().getModelData(), costOfFund);

		getCostOfFundDAO().delete(costOfFund, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(costOfFund.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(costOfFundDAO.getCostOfFundById(costOfFund.getCofCode(),
					costOfFund.getCurrency(), costOfFund.getCofEffDate(), ""));
		}

		if (costOfFund.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCostOfFundDAO().delete(costOfFund, TableType.MAIN_TAB);

		} else {
			costOfFund.setRoleCode("");
			costOfFund.setNextRoleCode("");
			costOfFund.setTaskId("");
			costOfFund.setNextTaskId("");
			costOfFund.setWorkflowId(0);

			if (costOfFund.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				costOfFund.setRecordType("");
				getCostOfFundDAO().save(costOfFund, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				costOfFund.setRecordType("");
				getCostOfFundDAO().update(costOfFund, TableType.MAIN_TAB);
			}
		}
		if (costOfFund.isDelExistingRates()) {
			getCostOfFundDAO().deleteByEffDate(costOfFund, "_Temp");
			getCostOfFundDAO().deleteByEffDate(costOfFund, "");
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(costOfFund);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCostOfFundDAO().delete with parameters costOfFund,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTCostOfFunds by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CostOfFund costOfFund = (CostOfFund) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCostOfFundDAO().delete(costOfFund, TableType.TEMP_TAB);

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
	 * from getCostOfFundDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		CostOfFund costOfFund = (CostOfFund) auditDetail.getModelData();

		// Check the unique keys.
		if (costOfFund != null && costOfFund.isNewRecord()
				&& PennantConstants.RECORD_TYPE_NEW.equals(costOfFund.getRecordType())
				&& costOfFundDAO.isDuplicateKey(costOfFund.getCofCode(), costOfFund.getCofEffDate(),
						costOfFund.getCurrency(), costOfFund.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_CofCode") + ": " + costOfFund.getCofCode();
			parameters[1] = PennantJavaUtil.getLabel("label_CofEffDate") + ": " + costOfFund.getCofEffDate();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (costOfFund != null
				&& StringUtils.trimToEmpty(costOfFund.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CofCode") + ": " + costOfFund.getCofCode();
			boolean exist = this.financeTypeDAO.isCostOfFundsExist(costOfFund.getCofCode(), "_View");
			if (exist) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null), usrLanguage));
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
}