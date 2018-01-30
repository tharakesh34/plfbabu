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
 * FileName    		:  StepPolicyHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.solutionfactory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>StepPolicyHeader</b>.<br>
 */
public class StepPolicyServiceImpl extends GenericService<StepPolicyHeader> implements StepPolicyService {
	private static final Logger logger = Logger.getLogger(StepPolicyServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private StepPolicyHeaderDAO stepPolicyHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private StepPolicyDetailDAO stepPolicyDetailDAO;
	
	public StepPolicyServiceImpl() {
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

	public StepPolicyHeaderDAO getStepPolicyHeaderDAO() {
		return stepPolicyHeaderDAO;
	}
	public void setStepPolicyHeaderDAO(StepPolicyHeaderDAO stepPolicyHeaderDAO) {
		this.stepPolicyHeaderDAO = stepPolicyHeaderDAO;
	}
	
	public StepPolicyDetailDAO getStepPolicyDetailDAO() {
    	return stepPolicyDetailDAO;
    }
	public void setStepPolicyDetailDAO(StepPolicyDetailDAO stepPolicyDetailDAO) {
    	this.stepPolicyDetailDAO = stepPolicyDetailDAO;
    }
	
	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Override
	public StepPolicyDetail getStepPolicyDetail() {
		return getStepPolicyDetailDAO().getStepPolicyDetail();
	}

	@Override
	public StepPolicyDetail getNewStepPolicyDetail() {
		return getStepPolicyDetailDAO().getNewStepPolicyDetail();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * StepPolicyHeaders/StepPolicyHeaders_Temp by using StepPolicyHeaderDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using StepPolicyHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTStepPolicyHeaders by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		String tableType = "";
		StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();
		if (stepPolicyHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		if (stepPolicyHeader.isNew()) {
			stepPolicyHeader.setId(getStepPolicyHeaderDAO().save(stepPolicyHeader, tableType));
			auditHeader.getAuditDetail().setModelData(stepPolicyHeader);
			auditHeader.setAuditReference(stepPolicyHeader.getId());
		} else {
			getStepPolicyHeaderDAO().update(stepPolicyHeader, tableType);
		}
		
		//StepPolicyDetail
		if (stepPolicyHeader.getStepPolicyDetails() != null  && stepPolicyHeader.getStepPolicyDetails().size() > 0) {
			List<AuditDetail> details = stepPolicyHeader.getAuditDetailMap().get("StepPolicyDetail");
			details = processStepPolicyDetailDetails(stepPolicyHeader, details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTStepPolicyHeaders by using StepPolicyHeaderDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTStepPolicyHeaders by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();
		getStepPolicyHeaderDAO().delete(stepPolicyHeader, "");
		
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(stepPolicyHeader, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getStepPolicyHeaderById fetch the details by using StepPolicyHeaderDAO's
	 * getStepPolicyHeaderById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return StepPolicyHeader
	 */
	@Override
	public StepPolicyHeader getStepPolicyHeaderById(String id) {
		StepPolicyHeader stepPolicyHeader = getStepPolicyHeaderDAO().getStepPolicyHeaderByID(id, "_View");
		if(stepPolicyHeader != null) {
			stepPolicyHeader.setStepPolicyDetails(getStepPolicyDetailDAO().getStepPolicyDetailListByID(id, "_View"));
		}
		return stepPolicyHeader;
	}
	
	/**
	 * getStepPolicyHeaderById fetch the details by using StepPolicyHeaderDAO's
	 * getStepPolicyHeaderById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return StepPolicyHeader
	 */
	@Override
	public List<StepPolicyDetail> getStepPolicyDetailsById(String id) {
		return getStepPolicyDetailDAO().getStepPolicyDetailListByID(id, "_AView");
	}

	
	/**
	 * getApprovedStepPolicyHeaderById fetch the details by using StepPolicyHeaderDAO's
	 * getStepPolicyHeaderById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTStepPolicyHeaders.
	 * 
	 * @param id
	 *            (String)
	 * @return StepPolicyHeader
	 */
	public StepPolicyHeader getApprovedStepPolicyHeaderById(String id) {
		return getStepPolicyHeaderDAO().getStepPolicyHeaderByID(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getStepPolicyHeaderDAO().delete with parameters stepPolicyHeader,"" b) NEW
	 * Add new record in to main table by using getStepPolicyHeaderDAO().save with
	 * parameters stepPolicyHeader,"" c) EDIT Update record in the main table by
	 * using getStepPolicyHeaderDAO().update with parameters stepPolicyHeader,"" 3) Delete
	 * the record from the workFlow table by using getStepPolicyHeaderDAO().delete
	 * with parameters stepPolicyHeader,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtRMTStepPolicyHeaders by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTStepPolicyHeaders by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		StepPolicyHeader stepPolicyHeader = new StepPolicyHeader();
		BeanUtils.copyProperties((StepPolicyHeader) auditHeader.getAuditDetail().getModelData(), stepPolicyHeader);

		if (stepPolicyHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			//List
			auditDetails.addAll(deleteChilds(stepPolicyHeader, "",tranType));
			getStepPolicyHeaderDAO().delete(stepPolicyHeader, "");

		} else {
			stepPolicyHeader.setRoleCode("");
			stepPolicyHeader.setNextRoleCode("");
			stepPolicyHeader.setTaskId("");
			stepPolicyHeader.setNextTaskId("");
			stepPolicyHeader.setWorkflowId(0);

			if (stepPolicyHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				stepPolicyHeader.setRecordType("");
				getStepPolicyHeaderDAO().save(stepPolicyHeader, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				stepPolicyHeader.setRecordType("");
				getStepPolicyHeaderDAO().update(stepPolicyHeader, "");
			}
			if (stepPolicyHeader.getStepPolicyDetails() != null && stepPolicyHeader.getStepPolicyDetails().size() > 0) {
				List<AuditDetail> details = stepPolicyHeader.getAuditDetailMap().get( "StepPolicyDetail");
				details = processStepPolicyDetailDetails(stepPolicyHeader,details, "");
				auditDetails.addAll(details);
			}
		}

		getStepPolicyHeaderDAO().delete(stepPolicyHeader, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		//List
		auditHeader.setAuditDetails(deleteChilds(stepPolicyHeader, "_Temp", auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new StepPolicyHeader(),stepPolicyHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], stepPolicyHeader.getBefImage(), stepPolicyHeader));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(stepPolicyHeader);
		
		//List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getStepPolicyHeaderDAO().delete with parameters
	 * stepPolicyHeader,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTStepPolicyHeaders by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getStepPolicyHeaderDAO().delete(stepPolicyHeader, "_Temp");
		
		//List
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds( stepPolicyHeader, "_Temp", 
				auditHeader.getAuditTranType())));
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		
		//List
		auditHeader.setErrorList(validateChilds(auditHeader,auditHeader.getUsrLanguage(),method));
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditDetail.getModelData();
		StepPolicyHeader tempStepPolicyHeader = null;
		if (stepPolicyHeader.isWorkflow()) {
			tempStepPolicyHeader = getStepPolicyHeaderDAO().getStepPolicyHeaderByID(stepPolicyHeader.getId(), "_Temp");
		}
		StepPolicyHeader befStepPolicyHeader = getStepPolicyHeaderDAO().getStepPolicyHeaderByID(stepPolicyHeader.getId(), "");

		StepPolicyHeader oldStepPolicyHeader = stepPolicyHeader.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = stepPolicyHeader.getPolicyCode();

		errParm[0] = PennantJavaUtil.getLabel("label_PolicyCode") + ":" + valueParm[0];

		if (stepPolicyHeader.isNew()) { // for New record or new record into work
			// flow

			if (!stepPolicyHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (befStepPolicyHeader != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

				}
			} else { // with work flow

				if (stepPolicyHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
					// is new
					if (befStepPolicyHeader != null || tempStepPolicyHeader != null) { // if
																				// records
																				// already
																				// exists
						// in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

					}
				} else { // if records not exists in the Main flow table
					if (befStepPolicyHeader == null || tempStepPolicyHeader != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));

					}
				}
			}

		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!stepPolicyHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befStepPolicyHeader == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));

				} else {

					if (oldStepPolicyHeader != null
							&& !oldStepPolicyHeader.getLastMntOn().equals(befStepPolicyHeader.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}

					}
				}

			} else {
				if (tempStepPolicyHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempStepPolicyHeader != null && oldStepPolicyHeader != null
						&& !oldStepPolicyHeader.getLastMntOn().equals(tempStepPolicyHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		
		// If Step already utilizing , Not allowed to Delete
		if(StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, stepPolicyHeader.getRecordType())){
			boolean isStepUsed = getFinanceTypeDAO().isStepPolicyExists(stepPolicyHeader.getPolicyCode());
			if(isStepUsed){
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))	|| !stepPolicyHeader.isWorkflow()) {
			auditDetail.setBefImage(befStepPolicyHeader);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (stepPolicyHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (stepPolicyHeader.getStepPolicyDetails() != null && stepPolicyHeader.getStepPolicyDetails().size() > 0) {
			auditDetailMap.put("StepPolicyDetail", setStepPolicyDetailAuditData(stepPolicyHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("StepPolicyDetail"));
		}

		stepPolicyHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(stepPolicyHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}
	
	//=================================== List maintain
		
	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (AuditDetail auditDetail : list) {
				String transType = "";
				String rcdType = "";
				Object object = auditDetail.getModelData();

				if (object instanceof StepPolicyDetail) {
					StepPolicyDetail stepPolicyDetail = (StepPolicyDetail) object;
					rcdType = stepPolicyDetail.getRecordType();
				}

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isNotEmpty(transType)) {
					auditDetailsList.add(new AuditDetail(transType, auditDetail.getAuditSeq(), auditDetail
							.getBefImage(), object));
				}

			}
		}

		logger.debug("Leaving");
		return auditDetailsList;
	}
		
		public List<AuditDetail> deleteChilds(StepPolicyHeader stepPolicyHeader, String tableType,String auditTranType) {
			List<AuditDetail> auditList = new ArrayList<AuditDetail>();
			
			if (stepPolicyHeader.getStepPolicyDetails() != null && stepPolicyHeader.getStepPolicyDetails().size() > 0) {
				String[] fields = PennantJavaUtil.getFieldDetails(new StepPolicyDetail(), new StepPolicyDetail().getExcludeFields());
				
				for (int i = 0; i < stepPolicyHeader.getStepPolicyDetails().size(); i++) {
					StepPolicyDetail stepPolicyDetail = stepPolicyHeader.getStepPolicyDetails().get(i);
					
					if (StringUtils.isNotEmpty(stepPolicyDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
						auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],stepPolicyDetail.getBefImage(), stepPolicyDetail));
					}
				}
				
				getStepPolicyDetailDAO().deleteByPolicyCode(stepPolicyHeader.getPolicyCode(), tableType);
			}    
			
			return auditList;
		}
		
		private List<ErrorDetail> validateChilds(AuditHeader auditHeader,String usrLanguage,String method){
			List<ErrorDetail> errorDetails=new ArrayList<ErrorDetail>();
			StepPolicyHeader stepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();
			List<AuditDetail> auditDetails=null;
			//StepPolicyDetail
			if (stepPolicyHeader.getAuditDetailMap().get("StepPolicyDetail")!=null) {
				auditDetails= stepPolicyHeader.getAuditDetailMap().get("StepPolicyDetail");
				for (AuditDetail auditDetail : auditDetails) {
					List<ErrorDetail> details=validationStepPolicyDetail(auditDetail, usrLanguage, method).getErrorDetails();
					if (details!=null) {
						errorDetails.addAll(details);
					}
				}
			}
			return errorDetails;
		}
	
		/**
		 * Methods for Creating List of Audit Details with detailed fields
		 * 
		 * @param customerDetails
		 * @param auditTranType
		 * @param method
		 * @return
		 */
		private List<AuditDetail> setStepPolicyDetailAuditData(StepPolicyHeader stepPolicyHeader, String auditTranType, String method) {
			logger.debug("Entering");

			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			String[] fields = PennantJavaUtil.getFieldDetails(new StepPolicyDetail());

			for (int i = 0; i < stepPolicyHeader.getStepPolicyDetails().size(); i++) {
				StepPolicyDetail stepPolicyDetail = stepPolicyHeader.getStepPolicyDetails().get(i);
				
				if (StringUtils.isEmpty(stepPolicyDetail.getRecordType())) {
					continue;
				}
				
				stepPolicyDetail.setWorkflowId(stepPolicyHeader.getWorkflowId());
				stepPolicyDetail.setPolicyCode(stepPolicyHeader.getPolicyCode());

				boolean isRcdType = false;

				if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}

				if ("saveOrUpdate".equals(method) && isRcdType ) {
					stepPolicyDetail.setNewRecord(true);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}

				stepPolicyDetail.setRecordStatus(stepPolicyHeader.getRecordStatus());
				stepPolicyDetail.setUserDetails(stepPolicyHeader.getUserDetails());
				stepPolicyDetail.setLastMntOn(stepPolicyHeader.getLastMntOn());

				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], stepPolicyDetail.getBefImage(), stepPolicyDetail));
			}
			
			logger.debug("Leaving");
			return auditDetails;
		}
		
	private List<AuditDetail> processStepPolicyDetailDetails(StepPolicyHeader stepPolicyHeader,List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			StepPolicyDetail stepPolicyDetail = (StepPolicyDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				stepPolicyDetail.setRoleCode("");
				stepPolicyDetail.setNextRoleCode("");
				stepPolicyDetail.setTaskId("");
				stepPolicyDetail.setNextTaskId("");
			}
			stepPolicyDetail.setPolicyCode(stepPolicyHeader.getPolicyCode());
			stepPolicyDetail.setWorkflowId(0);
			if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (stepPolicyDetail.isNewRecord()) {
				saveRecord = true;
				if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					stepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (stepPolicyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (stepPolicyDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = stepPolicyDetail.getRecordType();
				recordStatus = stepPolicyDetail.getRecordStatus();
				stepPolicyDetail.setRecordType("");
				stepPolicyDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getStepPolicyDetailDAO().save(stepPolicyDetail, type);
			}
			if (updateRecord) {
				getStepPolicyDetailDAO().update(stepPolicyDetail, type);
			}
			if (deleteRecord) {
				getStepPolicyDetailDAO().delete(stepPolicyDetail, type);
			}
			if (approveRec) {
				stepPolicyDetail.setRecordType(rcdType);
				stepPolicyDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(stepPolicyDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	
	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getIncomeExpenseDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	
	private AuditDetail validationStepPolicyDetail(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		StepPolicyDetail stepPolicyDetail= (StepPolicyDetail) auditDetail.getModelData();
		
		StepPolicyDetail tempStepPolicyDetail= null;
		if (stepPolicyDetail.isWorkflow()){
			tempStepPolicyDetail = getStepPolicyDetailDAO().getStepPolicyDetailByID(stepPolicyDetail, "_Temp");
		}
		StepPolicyDetail befStepPolicyDetail= getStepPolicyDetailDAO().getStepPolicyDetailByID( stepPolicyDetail, "");
		
		StepPolicyDetail oldStepPolicyDetailReference= stepPolicyDetail.getBefImage();
		
		
		String[] errParm= new String[1];
		String[] valueParm= new String[2];
		valueParm[0]=stepPolicyDetail.getPolicyCode();
		valueParm[1]=String.valueOf(stepPolicyDetail.getStepNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_PolicyCode")+":"+valueParm[0]+","+
				   PennantJavaUtil.getLabel("label_StepNumber")+":"+valueParm[1];
		
		if (stepPolicyDetail.isNew()){ // for New record or new record into work flow
			
			if (!stepPolicyDetail.isWorkflow()){// With out Work flow only new records  
				if (befStepPolicyDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (stepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befStepPolicyDetail !=null || tempStepPolicyDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befStepPolicyDetail ==null || tempStepPolicyDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!stepPolicyDetail.isWorkflow()){	// With out Work flow for update and delete
			
				if (befStepPolicyDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail( PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldStepPolicyDetailReference!=null && !oldStepPolicyDetailReference.getLastMntOn().equals(befStepPolicyDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{
			
				if (tempStepPolicyDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
				
				if (tempStepPolicyDetail!=null && oldStepPolicyDetailReference!=null && !oldStepPolicyDetailReference.getLastMntOn().equals(tempStepPolicyDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !stepPolicyDetail.isWorkflow()){
			auditDetail.setBefImage(befStepPolicyDetail);	
		}

		return auditDetail;
	}
	
}