/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  VehicleVersionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.service.amtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.VehicleVersionDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VehicleVersion</b>.<br>
 * 
 */
public class VehicleVersionServiceImpl extends GenericService<VehicleVersion> implements VehicleVersionService {

	private static final Logger logger = Logger.getLogger(VehicleVersionServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private VehicleVersionDAO vehicleVersionDAO;

	public VehicleVersionServiceImpl() {
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

	public VehicleVersionDAO getVehicleVersionDAO() {
		return vehicleVersionDAO;
	}
	public void setVehicleVersionDAO(VehicleVersionDAO vehicleVersionDAO) {
		this.vehicleVersionDAO = vehicleVersionDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * AMTVehicleVersion/AMTVehicleVersion_Temp by using VehicleVersionDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using VehicleVersionDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtAMTVehicleVersion by using
	 * auditHeaderDAO.addAudit(auditHeader)
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
		VehicleVersion vehicleVersion = (VehicleVersion) auditHeader
		.getAuditDetail().getModelData();

		if (vehicleVersion.isWorkflow()) {
			tableType = "_Temp";
		}

		if (vehicleVersion.isNew()) {
			vehicleVersion.setId(getVehicleVersionDAO().save(vehicleVersion,
					tableType));
			auditHeader.getAuditDetail().setModelData(vehicleVersion);
			auditHeader.setAuditReference(String.valueOf(vehicleVersion
					.getVehicleVersionId()));
		} else {
			getVehicleVersionDAO().update(vehicleVersion, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table AMTVehicleVersion by using VehicleVersionDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtAMTVehicleVersion by using auditHeaderDAO.addAudit(auditHeader)
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

		VehicleVersion vehicleVersion = (VehicleVersion) auditHeader
		.getAuditDetail().getModelData();
		getVehicleVersionDAO().delete(vehicleVersion, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVehicleVersionById fetch the details by using VehicleVersionDAO's
	 * getVehicleVersionById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VehicleVersion
	 */

	@Override
	public VehicleVersion getVehicleVersionById(long id) {
		return getVehicleVersionDAO().getVehicleVersionById(id, "_View");
	}

	/**
	 * getApprovedVehicleVersionById fetch the details by using
	 * VehicleVersionDAO's getVehicleVersionById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * AMTVehicleVersion.
	 * 
	 * @param id
	 *            (int)
	 * @return VehicleVersion
	 */

	public VehicleVersion getApprovedVehicleVersionById(long id) {
		return getVehicleVersionDAO().getVehicleVersionById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getVehicleVersionDAO().delete with parameters vehicleVersion,"" b)
	 * NEW Add new record in to main table by using getVehicleVersionDAO().save
	 * with parameters vehicleVersion,"" c) EDIT Update record in the main table
	 * by using getVehicleVersionDAO().update with parameters vehicleVersion,""
	 * 3) Delete the record from the workFlow table by using
	 * getVehicleVersionDAO().delete with parameters vehicleVersion,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtAMTVehicleVersion by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtAMTVehicleVersion by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		VehicleVersion vehicleVersion = new VehicleVersion();
		BeanUtils.copyProperties((VehicleVersion) auditHeader.getAuditDetail()
				.getModelData(), vehicleVersion);

		if (vehicleVersion.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getVehicleVersionDAO().delete(vehicleVersion, "");

		} else {
			vehicleVersion.setRoleCode("");
			vehicleVersion.setNextRoleCode("");
			vehicleVersion.setTaskId("");
			vehicleVersion.setNextTaskId("");
			vehicleVersion.setWorkflowId(0);

			if (vehicleVersion.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vehicleVersion.setRecordType("");
				getVehicleVersionDAO().save(vehicleVersion, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vehicleVersion.setRecordType("");
				getVehicleVersionDAO().update(vehicleVersion, "");
			}
		}

		getVehicleVersionDAO().delete(vehicleVersion, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vehicleVersion);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getVehicleVersionDAO().delete with parameters
	 * vehicleVersion,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtAMTVehicleVersion by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
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

		VehicleVersion vehicleVersion = (VehicleVersion) auditHeader
		.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVehicleVersionDAO().delete(vehicleVersion, "_Temp");

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

	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCourseDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		VehicleVersion vehicleVersion = (VehicleVersion) auditDetail
		.getModelData();

		VehicleVersion tempVehicleVersion = null;
		if (vehicleVersion.isWorkflow()) {
			tempVehicleVersion = getVehicleVersionDAO().getVehicleVersionById(
					vehicleVersion.getId(), "_Temp");
		}
		VehicleVersion befVehicleVersion = getVehicleVersionDAO()
		.getVehicleVersionById(vehicleVersion.getId(), "");

		VehicleVersion oldVehicleVersion = vehicleVersion.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(vehicleVersion.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_VehicleVersionId") + ":"
		+ valueParm[0];

		if (vehicleVersion.isNew()) { // for New record or new record into work
			// flow

			if (!vehicleVersion.isWorkflow()) {// With out Work flow only new
				// records

				if (befVehicleVersion != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (vehicleVersion.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befVehicleVersion != null || tempVehicleVersion != null) { // if
						// records already exists
						// in the
						// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befVehicleVersion == null || tempVehicleVersion != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!vehicleVersion.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befVehicleVersion == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldVehicleVersion != null
							&& !oldVehicleVersion.getLastMntOn().equals(
									befVehicleVersion.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempVehicleVersion == null) { // if records not exists in
					// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempVehicleVersion != null && oldVehicleVersion != null
						&& !oldVehicleVersion.getLastMntOn().equals(
								tempVehicleVersion.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		VehicleVersion vehiclevers =  getVehicleVersionDAO().getVehicleVersionByType(vehicleVersion, "_View"); 
		if(vehiclevers != null){
			String[] errParm1= new String[1];
			String[] valueParm1= new String[2];
			valueParm1[0] = vehiclevers.getLovDescVehicleModelDesc();
			valueParm1[1] = vehiclevers.getVehicleVersionCode();
			errParm1[0]=PennantJavaUtil.getLabel("label_VehicleModel")+":"+valueParm1[0]+ ","+PennantJavaUtil.getLabel("label_VehicleVersionCode")+":"+ valueParm1[1];		
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm1,null));
		}
	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !vehicleVersion.isWorkflow()) {
			vehicleVersion.setBefImage(befVehicleVersion);
		}

		return auditDetail;
	}

}