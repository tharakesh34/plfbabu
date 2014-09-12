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
 * FileName    		:  CarLoanDetailServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.lmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.CarLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CarLoanDetail</b>.<br>
 * 
 */
public class CarLoanDetailServiceImpl extends GenericService<CarLoanDetail> implements CarLoanDetailService {
	
	private final static Logger logger = Logger.getLogger(CarLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CarLoanDetailDAO carLoanDetailDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CarLoanDetailDAO getCarLoanDetailDAO() {
		return carLoanDetailDAO;
	}
	public void setCarLoanDetailDAO(CarLoanDetailDAO carLoanDetailDAO) {
		this.carLoanDetailDAO = carLoanDetailDAO;
	}

	public CarLoanDetail getCarLoanDetail() {
		return getCarLoanDetailDAO().getCarLoanDetail();
	}

	public CarLoanDetail getNewCarLoanDetail() {
		return getCarLoanDetailDAO().getNewCarLoanDetail();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * LMTCarLoanDetail/LMTCarLoanDetail_Temp by using CarLoanDetailDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CarLoanDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtLMTCarLoanDetail by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		CarLoanDetail carLoanDetail = (CarLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (carLoanDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (carLoanDetail.isNew()) {
			carLoanDetail.setLoanRefNumber(getCarLoanDetailDAO().save(carLoanDetail,tableType));
			auditHeader.getAuditDetail().setModelData(carLoanDetail);
			auditHeader.setAuditReference(carLoanDetail.getLoanRefNumber());
		}else{
			getCarLoanDetailDAO().update(carLoanDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table LMTCarLoanDetail by using CarLoanDetailDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtLMTCarLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CarLoanDetail carLoanDetail = (CarLoanDetail) auditHeader.getAuditDetail().getModelData();
		getCarLoanDetailDAO().delete(carLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCarLoanDetailById fetch the details by using CarLoanDetailDAO's
	 * getCarLoanDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CarLoanDetail
	 */
	@Override
	public CarLoanDetail getCarLoanDetailById(String id) {
		return getCarLoanDetailDAO().getCarLoanDetailByID(id,"_View");
	}

	/**
	 * getApprovedCarLoanDetailById fetch the details by using
	 * CarLoanDetailDAO's getCarLoanDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the LMTCarLoanDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return CarLoanDetail
	 */
	public CarLoanDetail getApprovedCarLoanDetailById(String id) {
		return getCarLoanDetailDAO().getCarLoanDetailByID(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param CarLoanDetail (carLoanDetail)
	 * @return carLoanDetail
	 */
	@Override
	public CarLoanDetail refresh(CarLoanDetail carLoanDetail) {
		logger.debug("Entering");
		getCarLoanDetailDAO().refresh(carLoanDetail);
		getCarLoanDetailDAO().initialize(carLoanDetail);
		logger.debug("Leaving");
		return carLoanDetail;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCarLoanDetailDAO().delete with parameters carLoanDetail,"" b)
	 * NEW Add new record in to main table by using getCarLoanDetailDAO().save
	 * with parameters carLoanDetail,"" c) EDIT Update record in the main table
	 * by using getCarLoanDetailDAO().update with parameters carLoanDetail,"" 3)
	 * Delete the record from the workFlow table by using
	 * getCarLoanDetailDAO().delete with parameters carLoanDetail,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtLMTCarLoanDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtLMTCarLoanDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CarLoanDetail carLoanDetail = new CarLoanDetail();
		BeanUtils.copyProperties((CarLoanDetail) auditHeader.getAuditDetail().getModelData(), carLoanDetail);

		if (carLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCarLoanDetailDAO().delete(carLoanDetail,"");
		} else {
			carLoanDetail.setRoleCode("");
			carLoanDetail.setNextRoleCode("");
			carLoanDetail.setTaskId("");
			carLoanDetail.setNextTaskId("");
			carLoanDetail.setWorkflowId(0);

			if (carLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				carLoanDetail.setRecordType("");
				getCarLoanDetailDAO().save(carLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				carLoanDetail.setRecordType("");
				getCarLoanDetailDAO().update(carLoanDetail,"");
			}
		}

		getCarLoanDetailDAO().delete(carLoanDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(carLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCarLoanDetailDAO().delete with parameters
	 * carLoanDetail,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtLMTCarLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CarLoanDetail carLoanDetail = (CarLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCarLoanDetailDAO().delete(carLoanDetail,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getCarLoanDetailDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = doValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
	public AuditDetail validate(CarLoanDetail carLoanDetail, String method, String auditTranType, String  usrLanguage){
		return doValidation(carLoanDetail, auditTranType, method, usrLanguage);
	}

	@Override
	public AuditDetail saveOrUpdate(CarLoanDetail carLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		String[] fields = PennantJavaUtil.getFieldDetails(carLoanDetail, carLoanDetail.getExcludeFields());

		carLoanDetail.setWorkflowId(0);
		if (carLoanDetail.isNewRecord()) {
			getCarLoanDetailDAO().save(carLoanDetail, tableType);
		} else {
			getCarLoanDetailDAO().update(carLoanDetail, tableType);
		}
		
		logger.debug("Leaving");
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], carLoanDetail.getBefImage(), carLoanDetail);

	}
	
	@Override
	public AuditDetail doApprove(CarLoanDetail carLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		String[] fields = PennantJavaUtil.getFieldDetails(carLoanDetail, carLoanDetail.getExcludeFields());
		
		carLoanDetail.setRoleCode("");
		carLoanDetail.setNextRoleCode("");
		carLoanDetail.setTaskId("");
		carLoanDetail.setNextTaskId("");
		carLoanDetail.setWorkflowId(0);

		getCarLoanDetailDAO().save(carLoanDetail, tableType);
		
		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], carLoanDetail.getBefImage(), carLoanDetail);
	}
	
	@Override
	public AuditDetail delete(CarLoanDetail carLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		String[] fields = PennantJavaUtil.getFieldDetails(carLoanDetail, carLoanDetail.getExcludeFields());	
		
		getCarLoanDetailDAO().delete(carLoanDetail, tableType);
		
		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], carLoanDetail.getBefImage(), carLoanDetail);
	}
	
	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public AuditDetail doValidation(CarLoanDetail carLoanDetail, String auditTranType, String method,String  usrLanguage){
		logger.debug("Entering");
		
		String[] fields = PennantJavaUtil.getFieldDetails(carLoanDetail, carLoanDetail.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], carLoanDetail.getBefImage(), carLoanDetail);
		
		logger.debug("Leaving");
		return validate(auditDetail, method, usrLanguage);
	}
	
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){
		logger.debug("Entering");
		
		CarLoanDetail carLoanDetail= (CarLoanDetail) auditDetail.getModelData();
		CarLoanDetail tempCarLoanDetail= null;
		
		if (carLoanDetail.isWorkflow()){
			tempCarLoanDetail = getCarLoanDetailDAO().getCarLoanDetailByID(carLoanDetail.getLoanRefNumber(), "_Temp");
		}
		
		CarLoanDetail befCarLoanDetail= getCarLoanDetailDAO().getCarLoanDetailByID(carLoanDetail.getLoanRefNumber(), "");
		CarLoanDetail oldCarLoanDetail= carLoanDetail.getBefImage();
		
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=carLoanDetail.getLoanRefNumber();
		errParm[0]=PennantJavaUtil.getLabel("label_CarLoanRefNumber")+":"+valueParm[0];
		
		if (carLoanDetail.isNew()) { // for New record or new record into work flow

			if (!carLoanDetail.isWorkflow()) {// With out Work flow only new
				// records
				if (befCarLoanDetail != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (carLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCarLoanDetail != null || tempCarLoanDetail != null) { // if
																// records already
																// exists in the
																// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCarLoanDetail == null || tempCarLoanDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!carLoanDetail.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCarLoanDetail == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldCarLoanDetail != null
							&& !oldCarLoanDetail.getLastMntOn().equals(
									befCarLoanDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempCarLoanDetail == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempCarLoanDetail != null && oldCarLoanDetail != null
						&& !oldCarLoanDetail.getLastMntOn().equals(
								tempCarLoanDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !carLoanDetail.isWorkflow()) {
			auditDetail.setBefImage(befCarLoanDetail);
		}
		return auditDetail;
	}

}