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
 * FileName    		:  VehicleManufacturerServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.VehicleManufacturerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleManufacturerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VehicleManufacturer</b>.<br>
 * 
 */
public class VehicleManufacturerServiceImpl extends GenericService<VehicleManufacturer> implements VehicleManufacturerService {
	private static final Logger logger = Logger.getLogger(VehicleManufacturerServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private VehicleManufacturerDAO vehicleManufacturerDAO;

	public VehicleManufacturerServiceImpl() {
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

	public VehicleManufacturerDAO getVehicleManufacturerDAO() {
		return vehicleManufacturerDAO;
	}
	public void setVehicleManufacturerDAO(VehicleManufacturerDAO vehicleManufacturerDAO) {
		this.vehicleManufacturerDAO = vehicleManufacturerDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTVehicleManufacturer/AMTVehicleManufacturer_Temp 
	 * 			by using VehicleManufacturerDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VehicleManufacturerDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleManufacturer by using auditHeaderDAO.addAudit(auditHeader)
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
		VehicleManufacturer vehicleManufacturer = (VehicleManufacturer) auditHeader.getAuditDetail().getModelData();

		if (vehicleManufacturer.isWorkflow()) {
			tableType="_Temp";
		}

		if (vehicleManufacturer.isNew()) {
			vehicleManufacturer.setId(getVehicleManufacturerDAO().save(vehicleManufacturer,tableType));
			auditHeader.getAuditDetail().setModelData(vehicleManufacturer);
			auditHeader.setAuditReference(String.valueOf(vehicleManufacturer.getManufacturerId()));
		}else{
			getVehicleManufacturerDAO().update(vehicleManufacturer,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTVehicleManufacturer by using VehicleManufacturerDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleManufacturer by using auditHeaderDAO.addAudit(auditHeader)    
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

		VehicleManufacturer vehicleManufacturer = (VehicleManufacturer) auditHeader.getAuditDetail().getModelData();
		getVehicleManufacturerDAO().delete(vehicleManufacturer,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVehicleManufacturerById fetch the details by using VehicleManufacturerDAO's getVehicleManufacturerById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleManufacturer
	 */

	@Override
	public VehicleManufacturer getVehicleManufacturerByName(long manufacturerId) {
		return getVehicleManufacturerDAO().getVehicleManufacturerByName(manufacturerId,"_View");
	}
	/**
	 * getApprovedVehicleManufacturerById fetch the details by using VehicleManufacturerDAO's getVehicleManufacturerById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTVehicleManufacturer.
	 * @param id (int)
	 * @return VehicleManufacturer
	 */

	public VehicleManufacturer getApprovedVehicleManufacturerByName(long manufacturerId) {
		return getVehicleManufacturerDAO().getVehicleManufacturerByName(manufacturerId,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getVehicleManufacturerDAO().delete with parameters vehicleManufacturer,""
	 * 		b)  NEW		Add new record in to main table by using getVehicleManufacturerDAO().save with parameters vehicleManufacturer,""
	 * 		c)  EDIT	Update record in the main table by using getVehicleManufacturerDAO().update with parameters vehicleManufacturer,""
	 * 3)	Delete the record from the workFlow table by using getVehicleManufacturerDAO().delete with parameters vehicleManufacturer,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTVehicleManufacturer by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTVehicleManufacturer by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		VehicleManufacturer vehicleManufacturer = new VehicleManufacturer();
		BeanUtils.copyProperties((VehicleManufacturer) auditHeader.getAuditDetail().getModelData(), vehicleManufacturer);

		if (vehicleManufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getVehicleManufacturerDAO().delete(vehicleManufacturer,"");

		} else {
			vehicleManufacturer.setRoleCode("");
			vehicleManufacturer.setNextRoleCode("");
			vehicleManufacturer.setTaskId("");
			vehicleManufacturer.setNextTaskId("");
			vehicleManufacturer.setWorkflowId(0);

			if (vehicleManufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				vehicleManufacturer.setRecordType("");
				getVehicleManufacturerDAO().save(vehicleManufacturer,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				vehicleManufacturer.setRecordType("");
				getVehicleManufacturerDAO().update(vehicleManufacturer,"");
			}
		}

		getVehicleManufacturerDAO().delete(vehicleManufacturer,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vehicleManufacturer);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVehicleManufacturerDAO().delete with parameters vehicleManufacturer,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTVehicleManufacturer by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		VehicleManufacturer vehicleManufacturer = (VehicleManufacturer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVehicleManufacturerDAO().delete(vehicleManufacturer,"_Temp");

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
	 * getVehicleManufacturerDAO().getErrorDetail with Error ID and language as parameters.
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
		VehicleManufacturer vehicleManufacturer= (VehicleManufacturer) auditDetail.getModelData();

		VehicleManufacturer tempVehicleManufacturer= null;
		if (vehicleManufacturer.isWorkflow()){
			tempVehicleManufacturer = getVehicleManufacturerDAO().
					getVehicleManufacturerByName(vehicleManufacturer.getManufacturerId(), "_Temp");
		}
		VehicleManufacturer befVehicleManufacturer= getVehicleManufacturerDAO().
				getVehicleManufacturerByName(vehicleManufacturer.getManufacturerId(),"");

		VehicleManufacturer oldVehicleManufacturer= vehicleManufacturer.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=vehicleManufacturer.getManufacturerName();
		errParm[0]=PennantJavaUtil.getLabel("label_ManufacturerName")+":"+valueParm[0];

		if (vehicleManufacturer.isNew()){ // for New record or new record into work flow

			if (!vehicleManufacturer.isWorkflow()){// With out Work flow only new records  
				if (befVehicleManufacturer !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (vehicleManufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befVehicleManufacturer !=null || tempVehicleManufacturer!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befVehicleManufacturer ==null || tempVehicleManufacturer!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vehicleManufacturer.isWorkflow()){	// With out Work flow for update and delete

				if (befVehicleManufacturer ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldVehicleManufacturer!=null && !oldVehicleManufacturer.getLastMntOn().equals(befVehicleManufacturer.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempVehicleManufacturer==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempVehicleManufacturer!=null && oldVehicleManufacturer!=null && !oldVehicleManufacturer.getLastMntOn().equals(tempVehicleManufacturer.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !vehicleManufacturer.isWorkflow()){
			vehicleManufacturer.setBefImage(befVehicleManufacturer);	
		}

		return auditDetail;
	}

}