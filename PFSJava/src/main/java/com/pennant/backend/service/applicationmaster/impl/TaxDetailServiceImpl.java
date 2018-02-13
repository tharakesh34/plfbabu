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
 * FileName    		:  TaxDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2017    														*
 *                                                                  						*
 * Modified Date    :  14-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2017       PENNANT	                 0.1                                            * 
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.applicationmaster.TaxDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>TaxDetail</b>.<br>
 */
public class TaxDetailServiceImpl extends GenericService<TaxDetail> implements TaxDetailService {
	private static final Logger logger = Logger.getLogger(TaxDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private TaxDetailDAO taxDetailDAO;
	private ProvinceDAO  provinceDAO;
	private EntityDAO  entityDAO;

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
	 * @return the taxDetailDAO
	 */
	public TaxDetailDAO getTaxDetailDAO() {
		return taxDetailDAO;
	}
	/**
	 * @param taxDetailDAO the taxDetailDAO to set
	 */
	public void setTaxDetailDAO(TaxDetailDAO taxDetailDAO) {
		this.taxDetailDAO = taxDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * TAXDETAIL/TAXDETAIL_Temp by using TAXDETAILDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using TAXDETAILDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtTAXDETAIL by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TaxDetail taxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (taxDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (taxDetail.isNew()) {
			taxDetail.setId(Long.parseLong(getTaxDetailDAO().save(taxDetail,tableType)));
			auditHeader.getAuditDetail().setModelData(taxDetail);
			auditHeader.setAuditReference(String.valueOf(taxDetail.getId()));
		}else{
			getTaxDetailDAO().update(taxDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table TAXDETAIL by using TAXDETAILDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtTAXDETAIL by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		
		TaxDetail taxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
		getTaxDetailDAO().delete(taxDetail,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getTAXDETAIL fetch the details by using TAXDETAILDAO's getTAXDETAILById
	 * method.
	 * 
	 * @param id
	 *            id of the TaxDetail.
	 * @return TAXDETAIL
	 */
	@Override
	public TaxDetail getTaxDetail(long id) {
		return getTaxDetailDAO().getTaxDetail(id,"_View");
	}

	/**
	 * getApprovedTAXDETAILById fetch the details by using TAXDETAILDAO's
	 * getTAXDETAILById method . with parameter id and type as blank. it fetches
	 * the approved records from the TAXDETAIL.
	 * 
	 * @param id
	 *            id of the TaxDetail.
	 *            (String)
	 * @return TAXDETAIL
	 */
	public TaxDetail getApprovedTaxDetail(long id) {
		return getTaxDetailDAO().getTaxDetail(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getTaxDetailDAO().delete with parameters taxDetail,"" b) NEW Add new
	 * record in to main table by using getTaxDetailDAO().save with parameters
	 * taxDetail,"" c) EDIT Update record in the main table by using
	 * getTaxDetailDAO().update with parameters taxDetail,"" 3) Delete the record
	 * from the workFlow table by using getTaxDetailDAO().delete with parameters
	 * taxDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtTAXDETAIL by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtTAXDETAIL by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TaxDetail taxDetail = new TaxDetail();
		BeanUtils.copyProperties((TaxDetail) auditHeader.getAuditDetail().getModelData(), taxDetail);

		getTaxDetailDAO().delete(taxDetail, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(taxDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(taxDetailDAO.getTaxDetail(taxDetail.getId(), ""));
		}

		if (taxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getTaxDetailDAO().delete(taxDetail, TableType.MAIN_TAB);
		} else {
			taxDetail.setRoleCode("");
			taxDetail.setNextRoleCode("");
			taxDetail.setTaskId("");
			taxDetail.setNextTaskId("");
			taxDetail.setWorkflowId(0);

			if (taxDetail.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				taxDetail.setRecordType("");
				getTaxDetailDAO().save(taxDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				taxDetail.setRecordType("");
				getTaxDetailDAO().update(taxDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(taxDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getTaxDetailDAO().delete with parameters
		 * taxDetail,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtTAXDETAIL by using auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		@Override
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.info(Literal.ENTERING);
			
			auditHeader = businessValidation(auditHeader,"doApprove");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}

			TaxDetail taxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getTaxDetailDAO().delete(taxDetail,TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			
			logger.info(Literal.LEAVING);
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
			logger.debug(Literal.ENTERING);
			
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);

			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getTaxDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	@Override
	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		TaxDetail taxDetail = (TaxDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (this.taxDetailDAO.isDuplicateKey(taxDetail.getId(), taxDetail.getTaxCode(),
				taxDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_TaxCode") + ": " + taxDetail.getTaxCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		boolean gstFlag = provinceDAO.count(taxDetail.getTaxCode().substring(0, 2), taxDetail.getStateCode(),
				taxDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB);

		if (gstFlag) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Gstin") + ": " + taxDetail.getTaxCode().substring(0, 2);
			parameters[1] = PennantJavaUtil.getLabel("label_StateCode") + ": " + taxDetail.getStateCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90701", parameters, null));
		}

		boolean entityFlag = entityDAO.panNumberExist(taxDetail.getTaxCode().substring(2, 12),
				taxDetail.getEntityCode(), taxDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB);

		if (taxDetail.isNew() && !entityFlag) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_pANNumber") + ": "
					+ taxDetail.getTaxCode().substring(2, 12);
			parameters[1] = PennantJavaUtil.getLabel("label_EntityCode") + ": " + taxDetail.getEntityCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90701", parameters, null));
		}

		auditDetail = gstNumbeValidation(auditDetail, taxDetail);

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}
		
		/**
		 * to validate the GST Number
		 */	
	public AuditDetail gstNumbeValidation(AuditDetail auditDetail, TaxDetail taxDetail) {
		logger.debug(Literal.ENTERING);

		String taxCode = taxDetail.getTaxCode();
		String entityCode = taxDetail.getEntityCode();

		String panNumber = "";
		String gstStateCode = "";

		if (StringUtils.isNotBlank(taxCode)) {
			// if GST Number is already exist or not
			int count = taxDetailDAO.getGSTNumberCount(entityCode, taxCode, "_View");
			if (count != 0) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_FinanceTaxDetailDialog_TaxNumber.value") + ": ";
				parameters[1] = taxCode;
				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null)));
			}

			Province province = this.provinceDAO.getProvinceById(taxDetail.getCountry(), taxDetail.getStateCode(), "");
			
			if (province != null) {
				gstStateCode = province.getTaxStateCode();
			}
			
			Entity entity = this.entityDAO.getEntity(entityCode, "");
			if(entity != null) {
				panNumber = entity.getPANNumber();
			}

			if (StringUtils.isNotBlank(gstStateCode)) { // if GST State Code is not available
				if (!StringUtils.equalsIgnoreCase(gstStateCode, taxCode.substring(0, 2))) {
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65023", null, null)));
				}
			}

			if (StringUtils.isNotBlank(panNumber)) { // if PAN number is not available in GST Number
				if (!StringUtils.equalsIgnoreCase(panNumber, taxCode.substring(2, 12))) {
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65024", null, null)));
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

		@Override
		public List<TaxDetail> getTaxDetailbystateCode(String Statecode, String type) {
			return getTaxDetailDAO().getTaxDetailbystateCode(Statecode, type);
		}
		
		public ProvinceDAO getProvinceDAO() {
			return provinceDAO;
		}

		public void setProvinceDAO(ProvinceDAO provinceDAO) {
			this.provinceDAO = provinceDAO;
		}

		public EntityDAO getEntityDAO() {
			return entityDAO;
		}

		public void setEntityDAO(EntityDAO entityDAO) {
			this.entityDAO = entityDAO;
		}


}