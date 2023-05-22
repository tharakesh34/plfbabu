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
 * * FileName : BaseRateServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.impl.BaseRateDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BaseRate</b>.<br>
 * 
 */
public class BaseRateServiceImpl extends GenericService<BaseRate> implements BaseRateService {
	private static Logger logger = LogManager.getLogger(BaseRateDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BaseRateDAO baseRateDAO;

	public BaseRateServiceImpl() {
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

	public BaseRateDAO getBaseRateDAO() {
		return baseRateDAO;
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RMTBaseRates/RMTBaseRates_Temp
	 * by using BaseRateDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using BaseRateDAO's update method 3) Audit the record in to AuditHeader and AdtRMTBaseRates by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		BaseRate baseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (baseRate.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		baseRate.setCreatedBy(baseRate.getUserDetails().getUserId());
		baseRate.setCreatedOn(new Timestamp(System.currentTimeMillis()));

		if (baseRate.isNewRecord()) {
			getBaseRateDAO().save(baseRate, tableType);
			auditHeader.getAuditDetail().setModelData(baseRate);
			auditHeader.setAuditReference(baseRate.getBRType() + PennantConstants.KEY_SEPERATOR
					+ DateUtil.format(baseRate.getBREffDate(), PennantConstants.DBDateFormat));
		} else {
			getBaseRateDAO().update(baseRate, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTBaseRates by using BaseRateDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader)
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

		BaseRate baseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		getBaseRateDAO().delete(baseRate, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBaseRateById fetch the details by using BaseRateDAO's getBaseRateById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return BaseRate
	 */
	@Override
	public BaseRate getBaseRateById(String bRType, String currency, Date bREffDate) {
		return getBaseRateDAO().getBaseRateById(bRType, currency, bREffDate, "_View");
	}

	/**
	 * getApprovedBaseRateById fetch the details by using BaseRateDAO's getBaseRateById method . with parameter id and
	 * type as blank. it fetches the approved records from the RMTBaseRates.
	 * 
	 * @param id (String)
	 * @return BaseRate
	 */
	public BaseRate getApprovedBaseRateById(String bRType, String currency, Date bREffDate) {
		return getBaseRateDAO().getBaseRateById(bRType, currency, bREffDate, "_AView");
	}

	/**
	 * getBaseRateDelById fetch the details by using BaseRateDAO's getBaseRateDelById method.
	 * 
	 * @param id   (String)
	 * @param type (String) _View
	 * @return BaseRate
	 */
	@Override
	public boolean getBaseRateListById(String bRType, String currency, Date bREffDate) {
		return getBaseRateDAO().getBaseRateListById(bRType, currency, bREffDate, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getBaseRateDAO().delete with
	 * parameters baseRate,"" b) NEW Add new record in to main table by using getBaseRateDAO().save with parameters
	 * baseRate,"" c) EDIT Update record in the main table by using getBaseRateDAO().update with parameters baseRate,""
	 * 3) Delete the record from the workFlow table by using getBaseRateDAO().delete with parameters baseRate,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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

		BaseRate baseRate = new BaseRate();
		BeanUtils.copyProperties((BaseRate) auditHeader.getAuditDetail().getModelData(), baseRate);

		getBaseRateDAO().delete(baseRate, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(baseRate.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(baseRateDAO.getBaseRateById(baseRate.getBRType(),
					baseRate.getCurrency(), baseRate.getBREffDate(), ""));
		}

		if (baseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getBaseRateDAO().delete(baseRate, TableType.MAIN_TAB);

		} else {
			baseRate.setRoleCode("");
			baseRate.setNextRoleCode("");
			baseRate.setTaskId("");
			baseRate.setNextTaskId("");
			baseRate.setWorkflowId(0);

			baseRate.setApprovedBy(baseRate.getUserDetails().getUserId());
			baseRate.setApprovedOn(new Timestamp(System.currentTimeMillis()));

			if (baseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				baseRate.setRecordType("");
				getBaseRateDAO().save(baseRate, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				baseRate.setRecordType("");
				getBaseRateDAO().update(baseRate, TableType.MAIN_TAB);
			}
		}
		if (baseRate.isDelExistingRates()) {
			getBaseRateDAO().deleteByEffDate(baseRate, "_Temp");
			getBaseRateDAO().deleteByEffDate(baseRate, "");
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(baseRate);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBaseRateDAO().delete with parameters baseRate,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		BaseRate baseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBaseRateDAO().delete(baseRate, TableType.TEMP_TAB);

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
	 * from getBaseRateDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BaseRate baseRate = (BaseRate) auditDetail.getModelData();

		// Check the unique keys.
		if (baseRate.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(baseRate.getRecordType())
				&& baseRateDAO.isDuplicateKey(baseRate.getBRType(), baseRate.getBREffDate(), baseRate.getCurrency(),
						baseRate.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_BRType") + ": " + baseRate.getBRType();
			parameters[1] = PennantJavaUtil.getLabel("label_BREffDate") + ": " + baseRate.getBREffDate();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public BaseRate getBaseRateByDate(String repayBaseRate, String finCcy, Date bREffDate) {
		return getBaseRateDAO().getBaseRateByDate(repayBaseRate, finCcy, bREffDate);
	}
}