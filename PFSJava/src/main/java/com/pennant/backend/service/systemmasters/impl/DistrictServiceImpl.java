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
 * * FileName : DistrictServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-10-2019 * * Modified
 * Date : 16-10-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.systemmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.DistrictDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DistrictService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>District</b>.<br>
 * 
 */
public class DistrictServiceImpl extends GenericService<District> implements DistrictService {
	private static Logger logger = LogManager.getLogger(DistrictServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DistrictDAO districtDAO;

	public DistrictServiceImpl() {
		super();
	}

	/**
	 * getDistrictById fetch the details by using DistrictDAO's getDistrictById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return District
	 */
	@Override
	public District getDistrictById(long id, String type) {
		return getDistrictDAO().getDistrictById(id, type);
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTDistricts by using DistrictDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTDistricts by using auditHeaderDAO.addAudit(auditHeader)
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
		District district = (District) auditHeader.getAuditDetail().getModelData();

		getDistrictDAO().delete(district, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RMTDistricts/RMTDistricts_Temp
	 * by using DistrictDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using DistrictDAO's update method 3) Audit the record in to AuditHeader and AdtRMTDistricts by using
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

		District district = (District) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (district.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (district.isNew()) {
			district.setId(Long.parseLong(getDistrictDAO().save(district, tableType)));
			auditHeader.getAuditDetail().setModelData(district);
			auditHeader.setAuditReference(String.valueOf(district.getId()));
		} else {
			getDistrictDAO().update(district, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDistrictDAO().delete with
	 * parameters country,"" b) NEW Add new record in to main table by using getDistrictDAO().save with parameters
	 * country,"" c) EDIT Update record in the main table by using getDistrictDAO().update with parameters country,"" 3)
	 * Delete the record from the workFlow table by using getDistrictDAO().delete with parameters country,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTDistricts by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtRMTDistricts by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		District district = new District();
		BeanUtils.copyProperties((District) auditHeader.getAuditDetail().getModelData(), district);

		getDistrictDAO().delete(district, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(district.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(getDistrictDAO().getDistrictById(district.getId(), ""));
		}
		if (district.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDistrictDAO().delete(district, TableType.MAIN_TAB);
		} else {
			district.setRoleCode("");
			district.setNextRoleCode("");
			district.setTaskId("");
			district.setNextTaskId("");
			district.setWorkflowId(0);

			if (district.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				district.setRecordType("");
				getDistrictDAO().save(district, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				district.setRecordType("");
				getDistrictDAO().update(district, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(district);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getDistrictDAO().delete with parameters country,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTDistricts by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		District district = (District) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDistrictDAO().delete(district, TableType.TEMP_TAB);

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getDistrictDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		District district = (District) auditDetail.getModelData();
		String code = district.getCode();

		// Check the unique keys.
		if (district.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(district.getRecordType()) && getDistrictDAO()
				.isDuplicateKey(code, district.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_DistrictCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public District getDistrictByCity(String cityCode) {
		return getDistrictDAO().getDistrictByCity(cityCode);
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

	public DistrictDAO getDistrictDAO() {
		return districtDAO;
	}

	public void setDistrictDAO(DistrictDAO districtDAO) {
		this.districtDAO = districtDAO;
	}

}
