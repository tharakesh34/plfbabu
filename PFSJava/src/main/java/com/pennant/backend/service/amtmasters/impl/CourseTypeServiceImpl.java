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
 * FileName    		:  CourseTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.CourseTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.CourseTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CourseType</b>.<br>
 * 
 */
public class CourseTypeServiceImpl extends GenericService<CourseType> implements CourseTypeService {
	private static final Logger logger = Logger.getLogger(CourseTypeServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private CourseTypeDAO courseTypeDAO;

	public CourseTypeServiceImpl() {
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


	public CourseTypeDAO getCourseTypeDAO() {
		return courseTypeDAO;
	}
	public void setCourseTypeDAO(CourseTypeDAO courseTypeDAO) {
		this.courseTypeDAO = courseTypeDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTCourseType/AMTCourseType_Temp 
	 * 			by using CourseTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CourseTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTCourseType by using auditHeaderDAO.addAudit(auditHeader)
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
		CourseType courseType = (CourseType) auditHeader.getAuditDetail().getModelData();

		if (courseType.isWorkflow()) {
			tableType="_Temp";
		}

		if (courseType.isNew()) {
			getCourseTypeDAO().save(courseType,tableType);
		}else{
			getCourseTypeDAO().update(courseType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTCourseType by using CourseTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTCourseType by using auditHeaderDAO.addAudit(auditHeader)    
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

		CourseType courseType = (CourseType) auditHeader.getAuditDetail().getModelData();
		getCourseTypeDAO().delete(courseType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCourseTypeById fetch the details by using CourseTypeDAO's getCourseTypeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CourseType
	 */

	@Override
	public CourseType getCourseTypeById(String id) {
		return getCourseTypeDAO().getCourseTypeById(id,"_View");
	}
	/**
	 * getApprovedCourseTypeById fetch the details by using CourseTypeDAO's getCourseTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTCourseType.
	 * @param id (String)
	 * @return CourseType
	 */

	public CourseType getApprovedCourseTypeById(String id) {
		return getCourseTypeDAO().getCourseTypeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCourseTypeDAO().delete with parameters courseType,""
	 * 		b)  NEW		Add new record in to main table by using getCourseTypeDAO().save with parameters courseType,""
	 * 		c)  EDIT	Update record in the main table by using getCourseTypeDAO().update with parameters courseType,""
	 * 3)	Delete the record from the workFlow table by using getCourseTypeDAO().delete with parameters courseType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTCourseType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTCourseType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CourseType courseType = new CourseType();
		BeanUtils.copyProperties((CourseType) auditHeader.getAuditDetail().getModelData(), courseType);

		if (courseType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCourseTypeDAO().delete(courseType,"");

		} else {
			courseType.setRoleCode("");
			courseType.setNextRoleCode("");
			courseType.setTaskId("");
			courseType.setNextTaskId("");
			courseType.setWorkflowId(0);

			if (courseType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				courseType.setRecordType("");
				getCourseTypeDAO().save(courseType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				courseType.setRecordType("");
				getCourseTypeDAO().update(courseType,"");
			}
		}

		getCourseTypeDAO().delete(courseType,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(courseType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCourseTypeDAO().delete with parameters courseType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTCourseType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CourseType courseType = (CourseType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCourseTypeDAO().delete(courseType,"_Temp");

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
	 * getCourseTypeDAO().getErrorDetail with Error ID and language as parameters.
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
		CourseType courseType= (CourseType) auditDetail.getModelData();

		CourseType tempCourseType= null;
		if (courseType.isWorkflow()){
			tempCourseType = getCourseTypeDAO().getCourseTypeById(courseType.getId(), "_Temp");
		}
		CourseType befCourseType= getCourseTypeDAO().getCourseTypeById(courseType.getId(), "");

		CourseType oldCourseType= courseType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=courseType.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_CourseTypeCode")+":"+valueParm[0];

		if (courseType.isNew()){ // for New record or new record into work flow

			if (!courseType.isWorkflow()){// With out Work flow only new records  
				if (befCourseType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (courseType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCourseType !=null || tempCourseType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCourseType ==null || tempCourseType!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!courseType.isWorkflow()){	// With out Work flow for update and delete

				if (befCourseType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCourseType!=null && !oldCourseType.getLastMntOn().equals(befCourseType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCourseType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempCourseType!=null  && oldCourseType!=null && !oldCourseType.getLastMntOn().equals(tempCourseType.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !courseType.isWorkflow()){
			courseType.setBefImage(befCourseType);	
		}

		return auditDetail;
	}

}