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
 * FileName    		:  CommodityBrokerDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.commodity.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.commodity.CommodityBrokerDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CommodityBrokerDetail</b>.<br>
 * 
 */
public class CommodityBrokerDetailServiceImpl extends GenericService<CommodityBrokerDetail> implements CommodityBrokerDetailService {
	private final static Logger logger = Logger.getLogger(CommodityBrokerDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CommodityBrokerDetailDAO commodityBrokerDetailDAO;


	public CommodityBrokerDetail getCommodityBrokerDetail() {
		return getCommodityBrokerDetailDAO().getCommodityBrokerDetail();
	}

	public CommodityBrokerDetail getNewCommodityBrokerDetail() {
		return getCommodityBrokerDetailDAO().getNewCommodityBrokerDetail();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FCMTBrokerDetail/FCMTBrokerDetail_Temp 
	 * 			by using CommodityBrokerDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommodityBrokerDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFCMTBrokerDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
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
		CommodityBrokerDetail commodityBrokerDetail = (CommodityBrokerDetail) auditHeader.getAuditDetail().getModelData();

		if (commodityBrokerDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (commodityBrokerDetail.isNew()) {
			getCommodityBrokerDetailDAO().save(commodityBrokerDetail,tableType);
		}else{
			getCommodityBrokerDetailDAO().update(commodityBrokerDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FCMTBrokerDetail by using CommodityBrokerDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFCMTBrokerDetail by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
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

		CommodityBrokerDetail commodityBrokerDetail = (CommodityBrokerDetail) auditHeader.getAuditDetail().getModelData();
		getCommodityBrokerDetailDAO().delete(commodityBrokerDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommodityBrokerDetailById fetch the details by using CommodityBrokerDetailDAO's getCommodityBrokerDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityBrokerDetail
	 */

	@Override
	public CommodityBrokerDetail getCommodityBrokerDetailById(String id) {
		return getCommodityBrokerDetailDAO().getCommodityBrokerDetailById(id,"_View");
	}
	/**
	 * getApprovedCommodityBrokerDetailById fetch the details by using CommodityBrokerDetailDAO's getCommodityBrokerDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FCMTBrokerDetail.
	 * @param id (String)
	 * @return CommodityBrokerDetail
	 */

	public CommodityBrokerDetail getApprovedCommodityBrokerDetailById(String id) {
		return getCommodityBrokerDetailDAO().getCommodityBrokerDetailById(id,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param CommodityBrokerDetail (commodityBrokerDetail)
	 * @return commodityBrokerDetail
	 */
	@Override
	public CommodityBrokerDetail refresh(CommodityBrokerDetail commodityBrokerDetail) {
		logger.debug("Entering");
		getCommodityBrokerDetailDAO().refresh(commodityBrokerDetail);
		getCommodityBrokerDetailDAO().initialize(commodityBrokerDetail);
		logger.debug("Leaving");
		return commodityBrokerDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCommodityBrokerDetailDAO().delete with parameters commodityBrokerDetail,""
	 * 		b)  NEW		Add new record in to main table by using getCommodityBrokerDetailDAO().save with parameters commodityBrokerDetail,""
	 * 		c)  EDIT	Update record in the main table by using getCommodityBrokerDetailDAO().update with parameters commodityBrokerDetail,""
	 * 3)	Delete the record from the workFlow table by using getCommodityBrokerDetailDAO().delete with parameters commodityBrokerDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFCMTBrokerDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFCMTBrokerDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CommodityBrokerDetail commodityBrokerDetail = new CommodityBrokerDetail();
		BeanUtils.copyProperties((CommodityBrokerDetail) auditHeader.getAuditDetail().getModelData(), commodityBrokerDetail);

		if (commodityBrokerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCommodityBrokerDetailDAO().delete(commodityBrokerDetail,"");

		} else {
			commodityBrokerDetail.setRoleCode("");
			commodityBrokerDetail.setNextRoleCode("");
			commodityBrokerDetail.setTaskId("");
			commodityBrokerDetail.setNextTaskId("");
			commodityBrokerDetail.setWorkflowId(0);

			if (commodityBrokerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				commodityBrokerDetail.setRecordType("");
				getCommodityBrokerDetailDAO().save(commodityBrokerDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				commodityBrokerDetail.setRecordType("");
				getCommodityBrokerDetailDAO().update(commodityBrokerDetail,"");
			}
		}

		getCommodityBrokerDetailDAO().delete(commodityBrokerDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodityBrokerDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCommodityBrokerDetailDAO().delete with parameters commodityBrokerDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFCMTBrokerDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CommodityBrokerDetail commodityBrokerDetail = (CommodityBrokerDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommodityBrokerDetailDAO().delete(commodityBrokerDetail,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getCommodityBrokerDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CommodityBrokerDetail commodityBrokerDetail= (CommodityBrokerDetail) auditDetail.getModelData();

		CommodityBrokerDetail tempCommodityBrokerDetail= null;
		if (commodityBrokerDetail.isWorkflow()){
			tempCommodityBrokerDetail = getCommodityBrokerDetailDAO().getCommodityBrokerDetailById(commodityBrokerDetail.getId(), "_Temp");
		}
		CommodityBrokerDetail befCommodityBrokerDetail= getCommodityBrokerDetailDAO().getCommodityBrokerDetailById(commodityBrokerDetail.getId(), "");

		CommodityBrokerDetail oldCommodityBrokerDetail= commodityBrokerDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=commodityBrokerDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_BrokerCode")+":"+valueParm[0];

		if (commodityBrokerDetail.isNew()){ // for New record or new record into work flow

			if (!commodityBrokerDetail.isWorkflow()){// With out Work flow only new records  
				if (befCommodityBrokerDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commodityBrokerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommodityBrokerDetail !=null || tempCommodityBrokerDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommodityBrokerDetail ==null || tempCommodityBrokerDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commodityBrokerDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCommodityBrokerDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCommodityBrokerDetail!=null && !oldCommodityBrokerDetail.getLastMntOn().equals(befCommodityBrokerDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCommodityBrokerDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldCommodityBrokerDetail!=null && !oldCommodityBrokerDetail.getLastMntOn().equals(tempCommodityBrokerDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !commodityBrokerDetail.isWorkflow()){
			commodityBrokerDetail.setBefImage(befCommodityBrokerDetail);	
		}
		logger.debug("Leaving ");
		return auditDetail;
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CommodityBrokerDetailDAO getCommodityBrokerDetailDAO() {
		return commodityBrokerDetailDAO;
	}

	public void setCommodityBrokerDetailDAO(CommodityBrokerDetailDAO commodityBrokerDetailDAO) {
		this.commodityBrokerDetailDAO = commodityBrokerDetailDAO;
	}

}