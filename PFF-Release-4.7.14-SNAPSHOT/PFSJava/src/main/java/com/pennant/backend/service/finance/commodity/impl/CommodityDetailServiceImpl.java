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
 * FileName    		:  CommodityDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.finance.commodity.CommodityDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>CommodityDetail</b>.<br>
 * 
 */
public class CommodityDetailServiceImpl extends GenericService<CommodityDetail> implements CommodityDetailService {
	private static final Logger logger = Logger.getLogger(CommodityDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CommodityDetailDAO commodityDetailDAO;

	public CommodityDetailServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FCMTCommodityDetail/FCMTCommodityDetail_Temp 
	 * 			by using CommodityDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommodityDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		CommodityDetail commodityDetail = (CommodityDetail) auditHeader.getAuditDetail().getModelData();

		if (commodityDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (commodityDetail.isNew()) {
			getCommodityDetailDAO().save(commodityDetail,tableType);
		}else{
			getCommodityDetailDAO().update(commodityDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FCMTCommodityDetail by using CommodityDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		CommodityDetail commodityDetail = (CommodityDetail) auditHeader.getAuditDetail().getModelData();
		boolean commodityExist = getCommodityDetailDAO().getBrokerCommodityDetails(commodityDetail.getCommodityCode());
		if (commodityExist) {
			String[] errParm= new String[2];
			String[] valueParm= new String[2];
			valueParm[0]=commodityDetail.getId();
			valueParm[1]=commodityDetail.getCommodityUnitCode();
			errParm[0]=PennantJavaUtil.getLabel("label_Commodity_Code")+":"+valueParm[0];
			errParm[1]=PennantJavaUtil.getLabel("label_CommodityUnitCode")+":"+valueParm[1];
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm,valueParm), auditHeader.getUsrLanguage()));
		}else{
			getCommodityDetailDAO().delete(commodityDetail,"");
		}
		

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommodityDetailById fetch the details by using CommodityDetailDAO's getCommodityDetailById method.
	 * @param commodityDetail (CommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityDetail
	 */

	@Override
	public CommodityDetail getCommodityDetailById(String id, String commodityUnitCode) {
		return getCommodityDetailDAO().getCommodityDetailById(id, commodityUnitCode, "_View");
	}
	/**
	 * getApprovedCommodityDetailById fetch the details by using CommodityDetailDAO's getCommodityDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FCMTCommodityDetail.
	 * @param id (String)
	 * @return CommodityDetail
	 */

	public CommodityDetail getApprovedCommodityDetailById(String id, String commodityUnitCode) {
		return getCommodityDetailDAO().getCommodityDetailById(id, commodityUnitCode, "_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCommodityDetailDAO().delete with parameters commodityDetail,""
	 * 		b)  NEW		Add new record in to main table by using getCommodityDetailDAO().save with parameters commodityDetail,""
	 * 		c)  EDIT	Update record in the main table by using getCommodityDetailDAO().update with parameters commodityDetail,""
	 * 3)	Delete the record from the workFlow table by using getCommodityDetailDAO().delete with parameters commodityDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFCMTCommodityDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFCMTCommodityDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CommodityDetail commodityDetail = new CommodityDetail();
		BeanUtils.copyProperties((CommodityDetail) auditHeader.getAuditDetail().getModelData(), commodityDetail);

		if (commodityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCommodityDetailDAO().delete(commodityDetail,"");

		} else {
			commodityDetail.setRoleCode("");
			commodityDetail.setNextRoleCode("");
			commodityDetail.setTaskId("");
			commodityDetail.setNextTaskId("");
			commodityDetail.setWorkflowId(0);

			if (commodityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				commodityDetail.setRecordType("");
				getCommodityDetailDAO().save(commodityDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				commodityDetail.setRecordType("");
				getCommodityDetailDAO().update(commodityDetail,"");
			}
		}

		getCommodityDetailDAO().delete(commodityDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodityDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCommodityDetailDAO().delete with parameters commodityDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CommodityDetail commodityDetail = (CommodityDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommodityDetailDAO().delete(commodityDetail,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getCommodityDetailDAO().getErrorDetail with Error ID and language as parameters.
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		CommodityDetail commodityDetail= (CommodityDetail) auditDetail.getModelData();

		CommodityDetail tempCommodityDetail= null;
		if (commodityDetail.isWorkflow()){
			tempCommodityDetail = getCommodityDetailDAO().getCommodityDetailById(commodityDetail.getId(),commodityDetail.getCommodityUnitCode(), "_Temp");
		}
		CommodityDetail befCommodityDetail= getCommodityDetailDAO().getCommodityDetailById(commodityDetail.getId(),commodityDetail.getCommodityUnitCode(), "");

		CommodityDetail oldCommodityDetail= commodityDetail.getBefImage();


		String[] errParm= new String[2];
		String[] valueParm= new String[2];
		valueParm[0]=commodityDetail.getId();
		valueParm[1]=commodityDetail.getCommodityUnitCode();
		errParm[0]=PennantJavaUtil.getLabel("label_Commodity_Code")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_CommodityUnitCode")+":"+valueParm[1];

		if (commodityDetail.isNew()){ // for New record or new record into work flow

			if (!commodityDetail.isWorkflow()){// With out Work flow only new records  
				if (befCommodityDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commodityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommodityDetail !=null || tempCommodityDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommodityDetail ==null || tempCommodityDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commodityDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCommodityDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCommodityDetail!=null && !oldCommodityDetail.getLastMntOn()
							.equals(befCommodityDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								       .equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCommodityDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldCommodityDetail!=null && !oldCommodityDetail.getLastMntOn().equals(tempCommodityDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !commodityDetail.isWorkflow()){
			commodityDetail.setBefImage(befCommodityDetail);	
		}
		logger.debug("Leaving ");
		return auditDetail;
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
	public CommodityDetailDAO getCommodityDetailDAO() {
		return commodityDetailDAO;
	}
	public void setCommodityDetailDAO(CommodityDetailDAO commodityDetailDAO) {
		this.commodityDetailDAO = commodityDetailDAO;
	}
}