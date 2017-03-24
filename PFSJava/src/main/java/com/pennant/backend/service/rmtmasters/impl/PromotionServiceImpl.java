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
 * FileName    		:  PromotionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Promotion</b>.<br>
 * 
 */
public class PromotionServiceImpl extends GenericService<Promotion> implements PromotionService {
	private final static Logger logger = Logger.getLogger(PromotionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private PromotionDAO promotionDAO;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	
	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the promotionDAO
	 */
	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}
	/**
	 * @param promotionDAO the promotionDAO to set
	 */
	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	/**
	 * @return the promotion for New Record
	 */
	@Override
	public Promotion getNewPromotion() {
		return new Promotion();
	}

	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Promotions/Promotions_Temp by using PromotionsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PromotionsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtPromotions by using
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
		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
		
		if (promotion.isWorkflow()) {
			tableType="_TEMP";
		}

		if (promotion.isNew()) {
			getPromotionDAO().save(promotion,tableType);
		}else{
			getPromotionDAO().update(promotion,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table Promotions by using PromotionsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtPromotions by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
		getPromotionDAO().delete(promotion,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPromotionsById fetch the details by using PromotionsDAO's getPromotionsById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Promotions
	 */
	@Override
	public Promotion getPromotionById(String id) {
		return getPromotionDAO().getPromotionById(id,"_View");
	}

	/**
	 * getApprovedPromotionsById fetch the details by using PromotionsDAO's
	 * getPromotionsById method . with parameter id and type as blank. it fetches
	 * the approved records from the Promotions.
	 * 
	 * @param id
	 *            (String)
	 * @return Promotions
	 */
	public Promotion getApprovedPromotionById(String id) {
		return getPromotionDAO().getPromotionById(id,"_AView");
	}
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPromotionDAO().delete with parameters promotion,"" b) NEW Add new
	 * record in to main table by using getPromotionDAO().save with parameters
	 * promotion,"" c) EDIT Update record in the main table by using
	 * getPromotionDAO().update with parameters promotion,"" 3) Delete the record
	 * from the workFlow table by using getPromotionDAO().delete with parameters
	 * promotion,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtPromotions by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtPromotions by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Promotion promotion = new Promotion("");
		BeanUtils.copyProperties((Promotion) auditHeader.getAuditDetail().getModelData(), promotion);

		if (PennantConstants.RECORD_TYPE_DEL.equals(promotion.getRecordType())) {
				tranType=PennantConstants.TRAN_DEL;
				getPromotionDAO().delete(promotion,"");
			} else {
				promotion.setRoleCode("");
				promotion.setNextRoleCode("");
				promotion.setTaskId("");
				promotion.setNextTaskId("");
				promotion.setWorkflowId(0);
				
				if (PennantConstants.RECORD_TYPE_NEW.equals(promotion.getRecordType())) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					promotion.setRecordType("");
					getPromotionDAO().save(promotion,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					promotion.setRecordType("");
					getPromotionDAO().update(promotion,"");
				}
			}
			
			getPromotionDAO().delete(promotion,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(promotion);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getPromotionDAO().delete with parameters
		 * promotion,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtPromotions by using auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		@Override
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove");
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getPromotionDAO().delete(promotion,"_TEMP");
			
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
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		/**
		 * For Validating AuditDetals object getting from Audit Header, if any
		 * mismatch conditions Fetch the error details from
		 * getPromotionDAO().getErrorDetail with Error ID and language as parameters.
		 * if any error/Warnings then assign the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @param method
		 * @return
		 */
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			Promotion promotion= (Promotion) auditDetail.getModelData();
			
			Promotion tempPromotion= null;
			if (promotion.isWorkflow()){
				tempPromotion = getPromotionDAO().getPromotionById(promotion.getId(), "_Temp");
			}
			Promotion befPromotion= getPromotionDAO().getPromotionById(promotion.getId(), "");
			
			Promotion oldPromotion= promotion.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=promotion.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_PromotionCode")+":"+valueParm[0];
			
			if (promotion.isNew()){ // for New record or new record into work flow
				
				if (!promotion.isWorkflow()){// With out Work flow only new records  
					if (befPromotion !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}	
				}else{ // with work flow
					if (PennantConstants.RECORD_TYPE_NEW.equals(promotion.getRecordType())){ // if records type is new
						if (befPromotion !=null || tempPromotion!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
						}
					}else{ // if records not exists in the Main flow table
						if (befPromotion ==null || tempPromotion!=null ){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!promotion.isWorkflow()){	// With out Work flow for update and delete
				
					if (befPromotion ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
					}else{
						if (oldPromotion!=null && !oldPromotion.getLastMntOn().equals(befPromotion.getLastMntOn())){
							if (PennantConstants.TRAN_DEL.equalsIgnoreCase(StringUtils.trimToEmpty(auditDetail.getAuditTranType()))){
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
							}else{
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
							}
						}
					}
				}else{
				
					if (tempPromotion==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
					
					if (tempPromotion!=null && oldPromotion!=null && !oldPromotion.getLastMntOn().equals(tempPromotion.getLastMntOn())){ 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !promotion.isWorkflow()){
				auditDetail.setBefImage(befPromotion);	
			}

			return auditDetail;
		}

}