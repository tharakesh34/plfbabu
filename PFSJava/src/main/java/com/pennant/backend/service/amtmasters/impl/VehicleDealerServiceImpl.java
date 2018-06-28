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
 * FileName    		:  VehicleDealerServiceImpl.java                                        * 	  
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VehicleDealer</b>.<br>
 * 
 */
public class VehicleDealerServiceImpl extends GenericService<VehicleDealer> implements VehicleDealerService {
	private static final Logger logger = Logger.getLogger(VehicleDealerServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private VehicleDealerDAO vehicleDealerDAO;
	private CustomerDAO customerDAO;

	public VehicleDealerServiceImpl() {
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

	public VehicleDealerDAO getVehicleDealerDAO() {
		return vehicleDealerDAO;
	}
	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * AMTVehicleDealer/AMTVehicleDealer_Temp by using VehicleDealerDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using VehicleDealerDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtAMTVehicleDealer by using
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
		VehicleDealer vehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();

		if (vehicleDealer.isWorkflow()) {
			tableType="_Temp";
		}

		if (vehicleDealer.isNew()) {
			vehicleDealer.setId(getVehicleDealerDAO().save(vehicleDealer,tableType));
			auditHeader.getAuditDetail().setModelData(vehicleDealer);
			auditHeader.setAuditReference(String.valueOf(vehicleDealer.getDealerId()));
		}else{
			getVehicleDealerDAO().update(vehicleDealer,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table AMTVehicleDealer by using VehicleDealerDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtAMTVehicleDealer by using auditHeaderDAO.addAudit(auditHeader)
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

		VehicleDealer vehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();
		getVehicleDealerDAO().delete(vehicleDealer,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVehicleDealerById fetch the details by using VehicleDealerDAO's
	 * getVehicleDealerById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealerById(long id) {
		return getVehicleDealerDAO().getVehicleDealerById(id,"_View");
	}

	/**
	 * getApprovedVehicleDealerById fetch the details by using
	 * VehicleDealerDAO's getVehicleDealerById method . with parameter id and
	 * type as blank. it fetches the approved records from the AMTVehicleDealer.
	 * 
	 * @param id
	 *            (int)
	 * @return VehicleDealer
	 */
	public VehicleDealer getApprovedVehicleDealerById(long id) {
		return getVehicleDealerDAO().getVehicleDealerById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getVehicleDealerDAO().delete with parameters vehicleDealer,"" b)
	 * NEW Add new record in to main table by using getVehicleDealerDAO().save
	 * with parameters vehicleDealer,"" c) EDIT Update record in the main table
	 * by using getVehicleDealerDAO().update with parameters vehicleDealer,"" 3)
	 * Delete the record from the workFlow table by using
	 * getVehicleDealerDAO().delete with parameters vehicleDealer,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtAMTVehicleDealer by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtAMTVehicleDealer by using
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
			return auditHeader;
		}

		VehicleDealer vehicleDealer = new VehicleDealer();
		BeanUtils.copyProperties((VehicleDealer) auditHeader.getAuditDetail()
				.getModelData(), vehicleDealer);

		if (vehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getVehicleDealerDAO().delete(vehicleDealer,"");

		} else {
			vehicleDealer.setRoleCode("");
			vehicleDealer.setNextRoleCode("");
			vehicleDealer.setTaskId("");
			vehicleDealer.setNextTaskId("");
			vehicleDealer.setWorkflowId(0);

			if (vehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				vehicleDealer.setRecordType("");
				getVehicleDealerDAO().save(vehicleDealer,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				vehicleDealer.setRecordType("");
				getVehicleDealerDAO().update(vehicleDealer,"");
			}
		}

		getVehicleDealerDAO().delete(vehicleDealer,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vehicleDealer);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getVehicleDealerDAO().delete with parameters
	 * vehicleDealer,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtAMTVehicleDealer by using auditHeaderDAO.addAudit(auditHeader) for
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

		VehicleDealer vehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVehicleDealerDAO().delete(vehicleDealer,"_Temp");

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
	 * getVehicleDealerDAO().getErrorDetail with Error ID and language as parameters.
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
		VehicleDealer vehicleDealer= (VehicleDealer) auditDetail.getModelData();

		VehicleDealer tempVehicleDealer= null;
		if (vehicleDealer.isWorkflow()) {
			tempVehicleDealer = getVehicleDealerDAO().getVehicleDealerById(
					vehicleDealer.getId(), "_Temp");
		}
		VehicleDealer befVehicleDealer = getVehicleDealerDAO()
		.getVehicleDealerById(vehicleDealer.getId(), "");

		VehicleDealer oldVehicleDealer= vehicleDealer.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(vehicleDealer.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_DealerId")+":"+valueParm[0];
		
		if (vehicleDealer.isNew()) { // for New record or new record into work
			// flow

			if (!vehicleDealer.isWorkflow()) {// With out Work flow only new
				// records
				if (befVehicleDealer != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (vehicleDealer.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befVehicleDealer != null || tempVehicleDealer != null) { // if
						// records already
						// exists in the
						// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befVehicleDealer == null || tempVehicleDealer != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!vehicleDealer.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befVehicleDealer == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldVehicleDealer != null
							&& !oldVehicleDealer.getLastMntOn().equals(
									befVehicleDealer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempVehicleDealer == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempVehicleDealer != null && oldVehicleDealer != null
						&& !oldVehicleDealer.getLastMntOn().equals(
								tempVehicleDealer.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm, valueParm), usrLanguage));
				}
				
			}
		}
		
		int count = getVehicleDealerDAO().getVehicleDealerByType(vehicleDealer.getDealerType(), vehicleDealer.getDealerName(), vehicleDealer.getDealerId(), "_View");
		if(count!=0){
			
			String[] errParmvendor= new String[2];
			String[] valueParmvendor= new String[2];
			valueParmvendor[0]=String.valueOf(vehicleDealer.getDealerType());
			valueParmvendor[1]=String.valueOf(vehicleDealer.getDealerName());

			errParmvendor[0]=PennantJavaUtil.getLabel("label_DealerType")+":"+valueParmvendor[0];
			errParmvendor[1]=PennantJavaUtil.getLabel("label_DealerName")+":"+valueParmvendor[1];
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
					"41018", errParmvendor, valueParmvendor), usrLanguage));
		}
		if (StringUtils.trimToEmpty(vehicleDealer.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int custCount = getCustomerDAO().getCustCountByDealerId(vehicleDealer.getDealerId());
			if (custCount != 0) {
				String[] errParmcust= new String[1];
				String[] valueParmvcust= new String[1];
				valueParmvcust[0]=String.valueOf(vehicleDealer.getDealerName());
				errParmcust[0]=PennantJavaUtil.getLabel("label_DealerName")+":"+valueParmvcust[0];

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParmcust, valueParmvcust), usrLanguage));
			}
		}
	
		if (!StringUtils.trimToEmpty(vehicleDealer.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)
				&& !StringUtils.trimToEmpty(method).equals(PennantConstants.method_doReject)
				&& StringUtils.isNotBlank(vehicleDealer.getCode())) {
			int cnt = getVehicleDealerDAO().getVehicleDealerByCode(vehicleDealer.getCode(),vehicleDealer.getDealerType(), vehicleDealer.getDealerId(), "_View");
			if (cnt != 0) {
				String[] errParmvendor = new String[1];
				String[] valueParmvendor = new String[1];
				valueParmvendor[0] = String.valueOf(vehicleDealer.getCode());

				errParmvendor[0] = PennantJavaUtil.getLabel("label_Code") + ":" + valueParmvendor[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParmvendor, valueParmvendor),
						usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !vehicleDealer.isWorkflow()) {
			vehicleDealer.setBefImage(befVehicleDealer);
		}
		
		
		return auditDetail;
	}

	@Override
    public boolean SearchByName(String dealerName) {
	    return getVehicleDealerDAO().SearchName(dealerName);
    }

	@Override
	public int getVASManufactureCode(String dealerName) {
		return getVehicleDealerDAO().getVASManufactureCode(dealerName, "_View");
	}

	/**
	 * Method for Fetch the VehicleDealer Based on Given id's.
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public List<VehicleDealer> getVehicleDealerById(List<Long> ids) {
		return vehicleDealerDAO.getVehicleDealerById(ids);
	}
	
	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}