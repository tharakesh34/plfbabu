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
 * FileName    		:  ProductFinanceTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2011    														*
 *                                                                  						*
 * Modified Date    :  13-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.ProductFinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ProductFinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.ProductFinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ProductFinanceType</b>.<br>
 * 
 */
public class ProductFinanceTypeServiceImpl extends GenericService<ProductFinanceType> implements ProductFinanceTypeService {
	private final static Logger logger = Logger
	.getLogger(ProductFinanceTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ProductFinanceTypeDAO productFinanceTypeDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the productFinanceTypeDAO
	 */
	public ProductFinanceTypeDAO getProductFinanceTypeDAO() {
		return productFinanceTypeDAO;
	}

	/**
	 * @param productFinanceTypeDAO
	 *            the productFinanceTypeDAO to set
	 */
	public void setProductFinanceTypeDAO(
			ProductFinanceTypeDAO productFinanceTypeDAO) {
		this.productFinanceTypeDAO = productFinanceTypeDAO;
	}

	/**
	 * @return the productFinanceType
	 */
	@Override
	public ProductFinanceType getProductFinanceType() {
		return getProductFinanceTypeDAO().getProductFinanceType();
	}

	/**
	 * @return the productFinanceType for New Record
	 */
	@Override
	public ProductFinanceType getNewProductFinanceType() {
		return getProductFinanceTypeDAO().getNewProductFinanceType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTProductFinanceTypes/RMTProductFinanceTypes_Temp by using
	 * ProductFinanceTypeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * ProductFinanceTypeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRMTProductFinanceTypes by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		ProductFinanceType productFinanceType = (ProductFinanceType) auditHeader
		.getAuditDetail().getModelData();

		if (productFinanceType.isWorkflow()) {
			tableType = "_TEMP";
		}
		if (productFinanceType.isNew()) {
			productFinanceType.setId(getProductFinanceTypeDAO().save(
					productFinanceType, tableType));
			auditHeader.getAuditDetail().setModelData(productFinanceType);
			auditHeader.setAuditReference(String.valueOf(productFinanceType
					.getPrdFinId()));
		} else {

			getProductFinanceTypeDAO().update(productFinanceType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTProductFinanceTypes by using ProductFinanceTypeDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTProductFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		ProductFinanceType productFinanceType = (ProductFinanceType) auditHeader
		.getAuditDetail().getModelData();
		getProductFinanceTypeDAO().delete(productFinanceType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProductFinanceTypeById fetch the details by using
	 * ProductFinanceTypeDAO's getProductFinanceTypeById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ProductFinanceType
	 */

	@Override
	public ProductFinanceType getProductFinanceTypeById(long id) {
		return getProductFinanceTypeDAO().getProductFinanceTypeById(id, "_View");
	}

	/**
	 * getApprovedProductFinanceTypeById fetch the details by using
	 * ProductFinanceTypeDAO's getProductFinanceTypeById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * RMTProductFinanceTypes.
	 * 
	 * @param id
	 *            (int)
	 * @return ProductFinanceType
	 */

	public ProductFinanceType getApprovedProductFinanceTypeById(long id) {
		return getProductFinanceTypeDAO().getProductFinanceTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param ProductFinanceType
	 *            (productFinanceType)
	 * @return productFinanceType
	 */
	@Override
	public ProductFinanceType refresh(ProductFinanceType productFinanceType) {
		logger.debug("Entering");
		getProductFinanceTypeDAO().refresh(productFinanceType);
		getProductFinanceTypeDAO().initialize(productFinanceType);
		logger.debug("Leaving");
		return productFinanceType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getProductFinanceTypeDAO().delete with parameters
	 * productFinanceType,"" b) NEW Add new record in to main table by using
	 * getProductFinanceTypeDAO().save with parameters productFinanceType,"" c)
	 * EDIT Update record in the main table by using
	 * getProductFinanceTypeDAO().update with parameters productFinanceType,""
	 * 3) Delete the record from the workFlow table by using
	 * getProductFinanceTypeDAO().delete with parameters
	 * productFinanceType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTProductFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow 5) Audit the record in to AuditHeader and
	 * AdtRMTProductFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		ProductFinanceType productFinanceType = new ProductFinanceType();
		BeanUtils.copyProperties((ProductFinanceType) auditHeader
				.getAuditDetail().getModelData(), productFinanceType);

		if (productFinanceType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getProductFinanceTypeDAO().delete(productFinanceType, "");

		} else {
			productFinanceType.setRoleCode("");
			productFinanceType.setNextRoleCode("");
			productFinanceType.setTaskId("");
			productFinanceType.setNextTaskId("");
			productFinanceType.setWorkflowId(0);

			if (productFinanceType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				productFinanceType.setRecordType("");
				getProductFinanceTypeDAO().save(productFinanceType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				productFinanceType.setRecordType("");
				getProductFinanceTypeDAO().update(productFinanceType, "");
			}
		}

		getProductFinanceTypeDAO().delete(productFinanceType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(productFinanceType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getProductFinanceTypeDAO().delete with
	 * parameters productFinanceType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTProductFinanceTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		ProductFinanceType productFinanceType = (ProductFinanceType) auditHeader
		.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProductFinanceTypeDAO().delete(productFinanceType, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getProductFinanceTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
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
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getProductFinanceTypeDAO().getErrorDetail with Error ID and language as parameters.
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

		ProductFinanceType productFinanceType = (ProductFinanceType) auditDetail
		.getModelData();

		ProductFinanceType tempProductFinanceType = null;
		if (productFinanceType.isWorkflow()) {
			tempProductFinanceType = getProductFinanceTypeDAO()
			.getProductFinanceTypeById(productFinanceType.getId(),
					"_Temp");
		}
		ProductFinanceType befProductFinanceType = getProductFinanceTypeDAO()
		.getProductFinanceTypeById(productFinanceType.getId(), "");

		ProductFinanceType old_ProductFinanceType = productFinanceType
		.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];
		
		valueParm[0] = String.valueOf(productFinanceType.getPrdFinId());
		errParm[0] = PennantJavaUtil.getLabel("label_PrdFinId")+ ":"+ valueParm[0];
		
		if (productFinanceType.isNew()) { // for New record or new record into
			// work flow

			if (!productFinanceType.isWorkflow()) {// With out Work flow only
				// new records
				if (befProductFinanceType != null) { // Record Already Exists in
					// the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (productFinanceType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befProductFinanceType != null || tempProductFinanceType != null) { // if records already
						// exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befProductFinanceType == null || tempProductFinanceType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!productFinanceType.isWorkflow()) { // With out Work flow for
				// update and delete

				if (befProductFinanceType == null) { // if records not exists in
					// the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (old_ProductFinanceType != null
							&& !old_ProductFinanceType.getLastMntOn().equals(
									befProductFinanceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempProductFinanceType == null) { // if records not exists
					// in the Work flow
					// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempProductFinanceType != null && old_ProductFinanceType != null
						&& !old_ProductFinanceType.getLastMntOn().equals(
								tempProductFinanceType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")) {
			auditDetail.setBefImage(befProductFinanceType);
		}

		return auditDetail;
	}

}