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
 * * FileName : SplRateServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.applicationmaster.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SplRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Service implementation for methods that depends on <b>SplRate</b>.<br>
 * 
 */
public class SplRateServiceImpl extends GenericService<SplRate> implements SplRateService {

	private static Logger logger = LogManager.getLogger(SplRateServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SplRateDAO splRateDAO;

	public SplRateServiceImpl() {
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

	public SplRateDAO getSplRateDAO() {
		return splRateDAO;
	}

	public void setSplRateDAO(SplRateDAO splRateDAO) {
		this.splRateDAO = splRateDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RMTSplRates/RMTSplRates_Temp by
	 * using SplRateDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by
	 * using SplRateDAO's update method 3) Audit the record in to AuditHeader and AdtRMTSplRates by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		SplRate splRate = (SplRate) auditHeader.getAuditDetail().getModelData();

		if (splRate.isWorkflow()) {
			tableType = "_Temp";
		}

		if (splRate.isNewRecord()) {
			getSplRateDAO().save(splRate, tableType);
			auditHeader.getAuditDetail().setModelData(splRate);
			auditHeader.setAuditReference(splRate.getSRType() + PennantConstants.KEY_SEPERATOR
					+ DateUtil.format(splRate.getSREffDate(), PennantConstants.DBDateFormat));
		} else {
			getSplRateDAO().update(splRate, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTSplRates by using SplRateDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTSplRates by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		SplRate splRate = (SplRate) auditHeader.getAuditDetail().getModelData();
		getSplRateDAO().delete(splRate, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSplRateById fetch the details by using SplRateDAO's getSplRateById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return SplRate
	 */
	@Override
	public SplRate getSplRateById(String id, Date date) {
		return getSplRateDAO().getSplRateById(id, date, "_View");
	}

	/**
	 * getApprovedSplRateById fetch the details by using SplRateDAO's getSplRateById method . with parameter id and type
	 * as blank. it fetches the approved records from the RMTSplRates.
	 * 
	 * @param id (String)
	 * @return SplRate
	 */
	public SplRate getApprovedSplRateById(String id, Date date) {
		return getSplRateDAO().getSplRateById(id, date, "_AView");
	}

	/**
	 * getSplRateByType fetch the details by using SplRateDAO's getSplRateById method.
	 * 
	 * @param id   (String)
	 * @param type (String) _View
	 * @return boolean
	 */
	@Override
	public boolean getSplRateListById(String sRType, Date sREffDate) {
		return getSplRateDAO().getSplRateListById(sRType, sREffDate, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getSplRateDAO().delete with parameters
	 * splRate,"" b) NEW Add new record in to main table by using getSplRateDAO().save with parameters splRate,"" c)
	 * EDIT Update record in the main table by using getSplRateDAO().update with parameters splRate,"" 3) Delete the
	 * record from the workFlow table by using getSplRateDAO().delete with parameters splRate,"_Temp" 4) Audit the
	 * record in to AuditHeader and AdtRMTSplRates by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtRMTSplRates by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SplRate splRate = new SplRate();
		BeanUtils.copyProperties((SplRate) auditHeader.getAuditDetail().getModelData(), splRate);

		if (splRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSplRateDAO().delete(splRate, "");
		} else {
			splRate.setRoleCode("");
			splRate.setNextRoleCode("");
			splRate.setTaskId("");
			splRate.setNextTaskId("");
			splRate.setWorkflowId(0);

			if (splRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				splRate.setRecordType("");
				getSplRateDAO().save(splRate, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				splRate.setRecordType("");
				getSplRateDAO().update(splRate, "");
			}
		}

		if (splRate.isDelExistingRates()) {
			getSplRateDAO().deleteByEffDate(splRate, "_Temp");
			getSplRateDAO().deleteByEffDate(splRate, "");
		}
		getSplRateDAO().delete(splRate, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(splRate);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getSplRateDAO().delete with parameters splRate,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTSplRates by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SplRate splRate = (SplRate) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSplRateDAO().delete(splRate, "_Temp");

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
	 * from getSplRateDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		SplRate splRate = (SplRate) auditDetail.getModelData();
		SplRate tempSplRate = null;
		if (splRate.isWorkflow()) {
			tempSplRate = getSplRateDAO().getSplRateById(splRate.getId(), splRate.getSREffDate(), "_Temp");
		}
		SplRate befSplRate = getSplRateDAO().getSplRateById(splRate.getId(), splRate.getSREffDate(), "");
		SplRate oldSplRate = splRate.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = splRate.getSRType();
		valueParm[1] = splRate.getSREffDate().toString();

		errParm[0] = PennantJavaUtil.getLabel("label_SRType") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SREffDate") + ":" + valueParm[1];

		if (splRate.isNewRecord()) { // for New record or new record into work flow

			if (!splRate.isWorkflow()) {// With out Work flow only new records
				if (befSplRate != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (splRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSplRate != null || tempSplRate != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befSplRate == null || tempSplRate != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!splRate.isWorkflow()) { // With out Work flow for update and
											// delete

				if (befSplRate == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldSplRate != null && !oldSplRate.getLastMntOn().equals(befSplRate.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {
				if (tempSplRate == null) { // if records not exists in the Work
											// flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempSplRate != null && oldSplRate != null
						&& !oldSplRate.getLastMntOn().equals(tempSplRate.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !splRate.isWorkflow()) {
			auditDetail.setBefImage(befSplRate);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}