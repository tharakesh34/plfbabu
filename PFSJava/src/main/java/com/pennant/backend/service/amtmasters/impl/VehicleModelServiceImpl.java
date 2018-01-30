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
 * FileName    		:  VehicleModelServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.amtmasters.VehicleModelDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>VehicleModel</b>.<br>
 * 
 */
public class VehicleModelServiceImpl extends GenericService<VehicleModel> implements VehicleModelService {
	private static final Logger logger = Logger.getLogger(VehicleModelServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private VehicleModelDAO vehicleModelDAO;

	public VehicleModelServiceImpl() {
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

	public VehicleModelDAO getVehicleModelDAO() {
		return vehicleModelDAO;
	}
	public void setVehicleModelDAO(VehicleModelDAO vehicleModelDAO) {
		this.vehicleModelDAO = vehicleModelDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTVehicleModel/AMTVehicleModel_Temp 
	 * 			by using VehicleModelDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VehicleModelDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleModel by using auditHeaderDAO.addAudit(auditHeader)
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
		VehicleModel vehicleModel = (VehicleModel) auditHeader.getAuditDetail().getModelData();

		if (vehicleModel.isWorkflow()) {
			tableType="_Temp";
		}

		if (vehicleModel.isNew()) {
			vehicleModel.setId(getVehicleModelDAO().save(vehicleModel,tableType));
			auditHeader.getAuditDetail().setModelData(vehicleModel);
			auditHeader.setAuditReference(String.valueOf(vehicleModel.getVehicleModelId()));
		}else{
			getVehicleModelDAO().update(vehicleModel,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTVehicleModel by using VehicleModelDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleModel by using auditHeaderDAO.addAudit(auditHeader)    
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

		VehicleModel vehicleModel = (VehicleModel) auditHeader.getAuditDetail().getModelData();
		getVehicleModelDAO().delete(vehicleModel,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVehicleModelById fetch the details by using VehicleModelDAO's getVehicleModelById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleModel
	 */

	@Override
	public VehicleModel getVehicleModelById(long id,long vehicleManufacturerId) {
		return getVehicleModelDAO().getVehicleModelById(id,vehicleManufacturerId,"_View");
	}
	/**
	 * getApprovedVehicleModelById fetch the details by using VehicleModelDAO's getVehicleModelById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTVehicleModel.
	 * @param id (int)
	 * @return VehicleModel
	 */

	public VehicleModel getApprovedVehicleModelById(long id,long vehicleManufacturerId) {
		return getVehicleModelDAO().getVehicleModelById(id,vehicleManufacturerId,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getVehicleModelDAO().delete with parameters vehicleModel,""
	 * 		b)  NEW		Add new record in to main table by using getVehicleModelDAO().save with parameters vehicleModel,""
	 * 		c)  EDIT	Update record in the main table by using getVehicleModelDAO().update with parameters vehicleModel,""
	 * 3)	Delete the record from the workFlow table by using getVehicleModelDAO().delete with parameters vehicleModel,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTVehicleModel by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTVehicleModel by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		VehicleModel vehicleModel = new VehicleModel();
		BeanUtils.copyProperties((VehicleModel) auditHeader.getAuditDetail().getModelData(), vehicleModel);

		if (vehicleModel.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getVehicleModelDAO().delete(vehicleModel,"");

		} else {
			vehicleModel.setRoleCode("");
			vehicleModel.setNextRoleCode("");
			vehicleModel.setTaskId("");
			vehicleModel.setNextTaskId("");
			vehicleModel.setWorkflowId(0);

			if (vehicleModel.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				vehicleModel.setRecordType("");
				getVehicleModelDAO().save(vehicleModel,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				vehicleModel.setRecordType("");
				getVehicleModelDAO().update(vehicleModel,"");
			}
		}

		getVehicleModelDAO().delete(vehicleModel,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vehicleModel);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVehicleModelDAO().delete with parameters vehicleModel,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleModel by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		VehicleModel vehicleModel = (VehicleModel) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVehicleModelDAO().delete(vehicleModel,"_Temp");

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
	 * 
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

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getVehicleModelDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		VehicleModel vehicleModel= (VehicleModel) auditDetail.getModelData();

		VehicleModel tempVehicleModel= null;
		if (vehicleModel.isWorkflow()){
			tempVehicleModel = getVehicleModelDAO().getVehicleModelById(vehicleModel.getVehicleModelId(),vehicleModel.getVehicleManufacturerId(), "_Temp");
		}
		VehicleModel befVehicleModel= getVehicleModelDAO().getVehicleModelById(vehicleModel.getVehicleModelId(),vehicleModel.getVehicleManufacturerId(), "");

		VehicleModel oldVehicleModel= vehicleModel.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0] =vehicleModel.getLovDescVehicleManufacturerName();
		errParm[0]=PennantJavaUtil.getLabel("label_ManufacturerId")+":"+valueParm[0];
		

		if (vehicleModel.isNew()){ // for New record or new record into work flow

			if (!vehicleModel.isWorkflow()){// With out Work flow only new records  
				if (befVehicleModel !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (vehicleModel.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befVehicleModel !=null || tempVehicleModel!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befVehicleModel ==null || tempVehicleModel!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vehicleModel.isWorkflow()){	// With out Work flow for update and delete

				if (befVehicleModel ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldVehicleModel!=null && !oldVehicleModel.getLastMntOn().equals(befVehicleModel.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempVehicleModel==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempVehicleModel!=null  && oldVehicleModel!=null && !oldVehicleModel.getLastMntOn().equals(tempVehicleModel.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}
		
		//unique key validation check
		VehicleModel vehiclemodel = getVehicleModelDAO().getVehicleModelByModelDesc(vehicleModel, "_View");
		if(vehiclemodel!=null){
			String[] errParm1= new String[1];
			String[] valueParm1= new String[2];
			valueParm1[0]= vehicleModel.getLovDescVehicleManufacturerName();
			valueParm1[1] =vehicleModel.getVehicleModelDesc();
			errParm1[0]=PennantJavaUtil.getLabel("label_ManufacturerId")+":"+valueParm1[0]+","+PennantJavaUtil.getLabel("label_ModelDesc")+":"+valueParm1[1];
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm1,null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !vehicleModel.isWorkflow()){
			vehicleModel.setBefImage(befVehicleModel);	
		}

		return auditDetail;
	}

}