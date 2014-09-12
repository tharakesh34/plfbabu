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
 * FileName    		:  CollateralitemServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.coremasters.impl;



import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.audit.AuditDetail;

import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.coremasters.CollateralitemService;
import com.pennant.backend.dao.coremasters.CollateralitemDAO;
import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.app.util.ErrorUtil;

/**
 * Service implementation for methods that depends on <b>Collateralitem</b>.<br>
 * 
 */
public class CollateralitemServiceImpl extends GenericService<Collateralitem> implements CollateralitemService {
	private final static Logger logger = Logger.getLogger(CollateralitemServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private CollateralitemDAO collateralitemDAO;

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
	 * @return the collateralitemDAO
	 */
	public CollateralitemDAO getCollateralitemDAO() {
		return collateralitemDAO;
	}
	/**
	 * @param collateralitemDAO the collateralitemDAO to set
	 */
	public void setCollateralitemDAO(CollateralitemDAO collateralitemDAO) {
		this.collateralitemDAO = collateralitemDAO;
	}

	/**
	 * @return the collateralitem
	 */
	@Override
	public Collateralitem getCollateralitem() {
		return getCollateralitemDAO().getCollateralitem();
	}
	/**
	 * @return the collateralitem for New Record
	 */
	@Override
	public Collateralitem getNewCollateralitem() {
		return getCollateralitemDAO().getNewCollateralitem();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table HYPF/HYPF_Temp 
	 * 			by using CollateralitemDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CollateralitemDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtHYPF by using auditHeaderDAO.addAudit(auditHeader)
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
		Collateralitem collateralitem = (Collateralitem) auditHeader.getAuditDetail().getModelData();
		
		if (collateralitem.isWorkflow()) {
			tableType="_TEMP";
		}

		if (collateralitem.isNew()) {
			getCollateralitemDAO().save(collateralitem,tableType);
		}else{
			getCollateralitemDAO().update(collateralitem,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table HYPF by using CollateralitemDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtHYPF by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		Collateralitem collateralitem = (Collateralitem) auditHeader.getAuditDetail().getModelData();
		getCollateralitemDAO().delete(collateralitem,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCollateralitemById fetch the details by using CollateralitemDAO's getCollateralitemById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Collateralitem
	 */
	
	@Override
	public Collateralitem getCollateralitemById(String id) {
		return getCollateralitemDAO().getCollateralitemById(id,"_View");
	}
	/**
	 * getApprovedCollateralitemById fetch the details by using CollateralitemDAO's getCollateralitemById method .
	 * with parameter id and type as blank. it fetches the approved records from the HYPF.
	 * @param id (String)
	 * @return Collateralitem
	 */
	
	public Collateralitem getApprovedCollateralitemById(String id) {
		return getCollateralitemDAO().getCollateralitemById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param Collateralitem (collateralitem)
 	 * @return collateralitem
	 */
	@Override
	public Collateralitem refresh(Collateralitem collateralitem) {
		logger.debug("Entering");
		getCollateralitemDAO().refresh(collateralitem);
		getCollateralitemDAO().initialize(collateralitem);
		logger.debug("Leaving");
		return collateralitem;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCollateralitemDAO().delete with parameters collateralitem,""
	 * 		b)  NEW		Add new record in to main table by using getCollateralitemDAO().save with parameters collateralitem,""
	 * 		c)  EDIT	Update record in the main table by using getCollateralitemDAO().update with parameters collateralitem,""
	 * 3)	Delete the record from the workFlow table by using getCollateralitemDAO().delete with parameters collateralitem,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtHYPF by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtHYPF by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		Collateralitem collateralitem = new Collateralitem();
		BeanUtils.copyProperties((Collateralitem) auditHeader.getAuditDetail().getModelData(), collateralitem);

		if (collateralitem.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getCollateralitemDAO().delete(collateralitem,"");
				
			} else {
				collateralitem.setRoleCode("");
				collateralitem.setNextRoleCode("");
				collateralitem.setTaskId("");
				collateralitem.setNextTaskId("");
				collateralitem.setWorkflowId(0);
				
				if (collateralitem.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					collateralitem.setRecordType("");
					getCollateralitemDAO().save(collateralitem,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					collateralitem.setRecordType("");
					getCollateralitemDAO().update(collateralitem,"");
				}
			}
			
			getCollateralitemDAO().delete(collateralitem,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(collateralitem);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCollateralitemDAO().delete with parameters collateralitem,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtHYPF by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			Collateralitem collateralitem = (Collateralitem) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getCollateralitemDAO().delete(collateralitem,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getCollateralitemDAO().getErrorDetail with Error ID and language as parameters.
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
			Collateralitem collateralitem= (Collateralitem) auditDetail.getModelData();
			
			Collateralitem tempCollateralitem= null;
			if (collateralitem.isWorkflow()){
				tempCollateralitem = getCollateralitemDAO().getCollateralitemById(collateralitem.getId(), "_Temp");
			}
			Collateralitem befCollateralitem= getCollateralitemDAO().getCollateralitemById(collateralitem.getId(), "");
			
			Collateralitem oldCollateralitem= collateralitem.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=collateralitem.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_HYCUS")+":"+valueParm[0];
			
			if (collateralitem.isNew()){ // for New record or new record into work flow
				
				if (!collateralitem.isWorkflow()){// With out Work flow only new records  
					if (befCollateralitem !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (collateralitem.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befCollateralitem !=null || tempCollateralitem!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befCollateralitem ==null || tempCollateralitem!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!collateralitem.isWorkflow()){	// With out Work flow for update and delete
				
					if (befCollateralitem ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldCollateralitem!=null && !oldCollateralitem.getLastMntOn().equals(befCollateralitem.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempCollateralitem==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (oldCollateralitem!=null && !oldCollateralitem.getLastMntOn().equals(tempCollateralitem.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !collateralitem.isWorkflow()){
				collateralitem.setBefImage(befCollateralitem);	
			}

			return auditDetail;
		}

}