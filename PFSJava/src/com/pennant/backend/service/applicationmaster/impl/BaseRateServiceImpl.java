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
 * FileName    		:  BaseRateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.impl.BaseRateDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>BaseRate</b>.<br>
 * 
 */
public class BaseRateServiceImpl extends GenericService<BaseRate> implements BaseRateService {

	private static Logger logger = Logger.getLogger(BaseRateDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private BaseRateDAO baseRateDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	@Override
	public BaseRate getBaseRate() {
		return getBaseRateDAO().getBaseRate();
	}

	@Override
	public BaseRate getNewBaseRate() {
		return getBaseRateDAO().getNewBaseRate();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTBaseRates/RMTBaseRates_Temp by using BaseRateDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BaseRateDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtRMTBaseRates by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		BaseRate baseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		
		if (baseRate.isWorkflow()) {
			tableType="_TEMP";
		}

		if (baseRate.isNew()) {
			getBaseRateDAO().save(baseRate,tableType);
			auditHeader.getAuditDetail().setModelData(baseRate);
			auditHeader.setAuditReference(String.valueOf(baseRate.getBRType()) +PennantConstants.KEY_SEPERATOR
					+ DateUtility.formatDate(baseRate.getBREffDate(), PennantConstants.DBDateFormat));
		}else{
			getBaseRateDAO().update(baseRate,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTBaseRates by using BaseRateDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtRMTBaseRates by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRate baseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		getBaseRateDAO().delete(baseRate,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBaseRateById fetch the details by using BaseRateDAO's getBaseRateById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BaseRate
	 */
	@Override
	public BaseRate getBaseRateById(String bRType, Date bREffDate) {
		return getBaseRateDAO().getBaseRateById(bRType,bREffDate,"_View");
	}

	/**
	 * getApprovedBaseRateById fetch the details by using BaseRateDAO's
	 * getBaseRateById method . with parameter id and type as blank. it fetches
	 * the approved records from the RMTBaseRates.
	 * 
	 * @param id
	 *            (String)
	 * @return BaseRate
	 */
	public BaseRate getApprovedBaseRateById(String bRType, Date bREffDate) {
		return getBaseRateDAO().getBaseRateById(bRType,bREffDate,"_AView");
	}
	
	/**
	 * getBaseRateDelById fetch the details by using BaseRateDAO's getBaseRateDelById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) _View
	 * @return BaseRate
	 */
	@Override
	public boolean getBaseRateListById(String bRType, Date bREffDate) {
		return getBaseRateDAO().getBaseRateListById(bRType,bREffDate,"");
	}

	
	/**
	 * This method refresh the Record.
	 * @param BaseRate (baseRate)
 	 * @return baseRate
	 */
	@Override
	public BaseRate refresh(BaseRate baseRate) {
		logger.debug("Entering");
		getBaseRateDAO().refresh(baseRate);
		getBaseRateDAO().initialize(baseRate);
		logger.debug("Leaving");
		return baseRate;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBaseRateDAO().delete with parameters baseRate,"" b) NEW Add new
	 * record in to main table by using getBaseRateDAO().save with parameters
	 * baseRate,"" c) EDIT Update record in the main table by using
	 * getBaseRateDAO().update with parameters baseRate,"" 3) Delete the record
	 * from the workFlow table by using getBaseRateDAO().delete with parameters
	 * baseRate,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtRMTBaseRates by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRate baseRate = new BaseRate();
		BeanUtils.copyProperties((BaseRate) auditHeader.getAuditDetail().getModelData(), baseRate);

		if (baseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getBaseRateDAO().delete(baseRate,"");
				
			} else {
				baseRate.setRoleCode("");
				baseRate.setNextRoleCode("");
				baseRate.setTaskId("");
				baseRate.setNextTaskId("");
				baseRate.setWorkflowId(0);
				
				if (baseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					baseRate.setRecordType("");
					getBaseRateDAO().save(baseRate,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					baseRate.setRecordType("");
					getBaseRateDAO().update(baseRate,"");
				}
			}
			if(baseRate.isDelExistingRates()){
				getBaseRateDAO().deleteByEffDate(baseRate,"_Temp");
				getBaseRateDAO().deleteByEffDate(baseRate,"");
			}
			getBaseRateDAO().delete(baseRate,"_TEMP");
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
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBaseRateDAO().delete with parameters
	 * baseRate,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTBaseRates by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRate baseRate= (BaseRate) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBaseRateDAO().delete(baseRate,"_TEMP");
		
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
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getBaseRateDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,
			String method) {
		logger.debug("Entering");
		
		BaseRate baseRate = (BaseRate) auditDetail.getModelData();
		BaseRate tempBaseRate = null;
		if (baseRate.isWorkflow()) {
			tempBaseRate = getBaseRateDAO().getBaseRateById(
					baseRate.getBRType(), baseRate.getBREffDate(), "_Temp");
		}
		BaseRate befBaseRate = getBaseRateDAO().getBaseRateById(
				baseRate.getBRType(), baseRate.getBREffDate(), "");

		BaseRate oldBaseRate = baseRate.getBefImage();


		String[] valueParm = new String[2];
		String[] errParm= new String[2];

		valueParm[0] = baseRate.getBRType();
		valueParm[1] = DateUtility.formatDate(baseRate.getBREffDate(),PennantConstants.DBDateFormat);

		errParm[0] = PennantJavaUtil.getLabel("label_BRType") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_BREffDate") + ":"+valueParm[1];

		if (baseRate.isNew()) { // for New record or new record into work flow

			if (!baseRate.isWorkflow()) {// With out Work flow only new records
				if (befBaseRate != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (baseRate.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befBaseRate != null || tempBaseRate != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}
				else { // if records not exists in the Main flow table
					if (befBaseRate == null || tempBaseRate != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
 			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!baseRate.isWorkflow()) { // With out Work flow for update and delete

				if (befBaseRate == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldBaseRate != null
							&& !oldBaseRate.getLastMntOn().equals(
									befBaseRate.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempBaseRate == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempBaseRate != null && oldBaseRate != null
						&& !oldBaseRate.getLastMntOn().equals(
								tempBaseRate.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !baseRate.isWorkflow()) {
			auditDetail.setBefImage(befBaseRate);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}