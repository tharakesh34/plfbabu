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
 * FileName    		:  AddressTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.AddressTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.AddressTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>AddressType</b>.<br>
 * 
 */
public class AddressTypeServiceImpl extends GenericService<AddressType>
		implements AddressTypeService {

	private static Logger logger = Logger
			.getLogger(AddressTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AddressTypeDAO addressTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AddressTypeDAO getAddressTypeDAO() {
		return addressTypeDAO;
	}

	public void setAddressTypeDAO(AddressTypeDAO addressTypeDAO) {
		this.addressTypeDAO = addressTypeDAO;
	}

	public AddressType getAddressType() {
		return getAddressTypeDAO().getAddressType();
	}

	public AddressType getNewAddressType() {
		return getAddressTypeDAO().getNewAddressType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTAddressTypes/BMTAddressTypes_Temp by using AddressTypeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using AddressTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTAddressTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * 
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
		AddressType addressType = (AddressType) auditHeader.getAuditDetail()
				.getModelData();

		if (addressType.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (addressType.isNew()) {
			addressType.setId(getAddressTypeDAO().save(addressType, tableType));
			auditHeader.getAuditDetail().setModelData(addressType);
			auditHeader.setAuditReference(addressType.getAddrTypeCode());
		} else {
			getAddressTypeDAO().update(addressType, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTAddressTypes by using AddressTypeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTAddressTypes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		AddressType addressType = (AddressType) auditHeader.getAuditDetail()
				.getModelData();
		getAddressTypeDAO().delete(addressType, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAddressTypeById fetch the details by using AddressTypeDAO's
	 * getAddressTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AddressType
	 */
	@Override
	public AddressType getAddressTypeById(String id) {
		return getAddressTypeDAO().getAddressTypeById(id, "_View");
	}

	/**
	 * getApprovedAddressTypeById fetch the details by using AddressTypeDAO's
	 * getAddressTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTAddressTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return AddressType
	 */
	public AddressType getApprovedAddressTypeById(String id) {
		return getAddressTypeDAO().getAddressTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param AddressType
	 *            (addressType)
	 * @return addressType
	 */
	@Override
	public AddressType refresh(AddressType addressType) {
		logger.debug("Entering");
		getAddressTypeDAO().refresh(addressType);
		getAddressTypeDAO().initialize(addressType);
		logger.debug("Leaving");
		return addressType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAddressTypeDAO().delete with parameters addressType,"" b) NEW
	 * Add new record in to main table by using getAddressTypeDAO().save with
	 * parameters addressType,"" c) EDIT Update record in the main table by
	 * using getAddressTypeDAO().update with parameters addressType,"" 3) Delete
	 * the record from the workFlow table by using getAddressTypeDAO().delete
	 * with parameters addressType,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTAddressTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTAddressTypes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
			logger.debug("Leaving");
			return auditHeader;
		}

		AddressType addressType = new AddressType();
		BeanUtils.copyProperties((AddressType) auditHeader.getAuditDetail()
				.getModelData(), addressType);

		if (addressType.getRecordType()
				.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAddressTypeDAO().delete(addressType, "");
		} else {
			addressType.setRoleCode("");
			addressType.setNextRoleCode("");
			addressType.setTaskId("");
			addressType.setNextTaskId("");
			addressType.setWorkflowId(0);

			if (addressType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				addressType.setRecordType("");
				getAddressTypeDAO().save(addressType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				addressType.setRecordType("");
				getAddressTypeDAO().update(addressType, "");
			}
		}

		getAddressTypeDAO().delete(addressType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(addressType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAddressTypeDAO().delete with parameters
	 * addressType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTAddressTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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
		AddressType addressType = (AddressType) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAddressTypeDAO().delete(addressType, "_TEMP");
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
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
	 * getAddressTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		AddressType addressType = (AddressType) auditDetail.getModelData();
		AddressType tempAddressType = null;

		if (addressType.isWorkflow()) {
			tempAddressType = getAddressTypeDAO().getAddressTypeById(
					addressType.getId(), "_Temp");
		}

		AddressType befAddressType = getAddressTypeDAO().getAddressTypeById(
				addressType.getId(), "");
		AddressType old_AddressType = addressType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = addressType.getAddrTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_AddrTypeCode") + ":"
				+ valueParm[0];

		if (addressType.isNew()) { // for New record or new record into work
									// flow

			if (!addressType.isWorkflow()) {// With out Work flow only new
											// records
				if (befAddressType != null) { // Record Already Exists in the
												// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));

				}
			} else { // with work flow
				if (addressType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befAddressType != null || tempAddressType != null) { 
															// if records 
															// already exists
															// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befAddressType == null || tempAddressType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!addressType.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befAddressType == null) { // if records not exists in the
												// main table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {

					if (old_AddressType != null
							&& !old_AddressType.getLastMntOn().equals(
									befAddressType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003",
									errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004",
									errParm, null));
						}
					}
				}
			} else {
				if (tempAddressType == null) { // if records not exists in the
												// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempAddressType != null
						&& old_AddressType != null
						&& !old_AddressType.getLastMntOn().equals(
								tempAddressType.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !addressType.isWorkflow()) {
			auditDetail.setBefImage(befAddressType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}