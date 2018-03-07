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
 * FileName    		:  MandateCheckDigitServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-12-2017    														*
 *                                                                  						*
 * Modified Date    :  11-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-12-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.MandateCheckDigitDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.MandateCheckDigitService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>MandateCheckDigit</b>.<br>
 */
public class MandateCheckDigitServiceImpl extends GenericService<MandateCheckDigit>
		implements MandateCheckDigitService {
	private static final Logger		logger	= Logger.getLogger(MandateCheckDigitServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;
	private MandateCheckDigitDAO	mandateCheckDigitDAO;

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
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the mandateCheckDigitDAO
	 */
	public MandateCheckDigitDAO getMandateCheckDigitDAO() {
		return mandateCheckDigitDAO;
	}

	/**
	 * @param mandateCheckDigitDAO
	 *            the mandateCheckDigitDAO to set
	 */
	public void setMandateCheckDigitDAO(MandateCheckDigitDAO mandateCheckDigitDAO) {
		this.mandateCheckDigitDAO = mandateCheckDigitDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * MandateCheckDigits/MandateCheckDigits_Temp by using MandateCheckDigitsDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using MandateCheckDigitsDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtMandateCheckDigits by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		MandateCheckDigit mandateCheckDigit = (MandateCheckDigit) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (mandateCheckDigit.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (mandateCheckDigit.isNew()) {
			getMandateCheckDigitDAO().save(mandateCheckDigit, tableType);
		} else {
			getMandateCheckDigitDAO().update(mandateCheckDigit, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * MandateCheckDigits by using MandateCheckDigitsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtMandateCheckDigits by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		MandateCheckDigit mandateCheckDigit = (MandateCheckDigit) auditHeader.getAuditDetail().getModelData();
		getMandateCheckDigitDAO().delete(mandateCheckDigit, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getMandateCheckDigits fetch the details by using MandateCheckDigitsDAO's getMandateCheckDigitsById method.
	 * 
	 * @param checkDigitValue
	 *            checkDigitValue of the MandateCheckDigit.
	 * @return MandateCheckDigits
	 */
	@Override
	public MandateCheckDigit getMandateCheckDigit(int checkDigitValue) {
		return getMandateCheckDigitDAO().getMandateCheckDigit(checkDigitValue, "_View");
	}

	/**
	 * getApprovedMandateCheckDigitsById fetch the details by using MandateCheckDigitsDAO's getMandateCheckDigitsById
	 * method . with parameter id and type as blank. it fetches the approved records from the MandateCheckDigits.
	 * 
	 * @param checkDigitValue
	 *            checkDigitValue of the MandateCheckDigit. (String)
	 * @return MandateCheckDigits
	 */
	public MandateCheckDigit getApprovedMandateCheckDigit(int checkDigitValue) {
		return getMandateCheckDigitDAO().getMandateCheckDigit(checkDigitValue, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getMandateCheckDigitDAO().delete with
	 * parameters mandateCheckDigit,"" b) NEW Add new record in to main table by using getMandateCheckDigitDAO().save
	 * with parameters mandateCheckDigit,"" c) EDIT Update record in the main table by using
	 * getMandateCheckDigitDAO().update with parameters mandateCheckDigit,"" 3) Delete the record from the workFlow
	 * table by using getMandateCheckDigitDAO().delete with parameters mandateCheckDigit,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtMandateCheckDigits by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtMandateCheckDigits by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		MandateCheckDigit mandateCheckDigit = new MandateCheckDigit();
		BeanUtils.copyProperties((MandateCheckDigit) auditHeader.getAuditDetail().getModelData(), mandateCheckDigit);

		getMandateCheckDigitDAO().delete(mandateCheckDigit, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(mandateCheckDigit.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(mandateCheckDigitDAO.getMandateCheckDigit(mandateCheckDigit.getCheckDigitValue(), ""));
		}

		if (mandateCheckDigit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getMandateCheckDigitDAO().delete(mandateCheckDigit, TableType.MAIN_TAB);
		} else {
			mandateCheckDigit.setRoleCode("");
			mandateCheckDigit.setNextRoleCode("");
			mandateCheckDigit.setTaskId("");
			mandateCheckDigit.setNextTaskId("");
			mandateCheckDigit.setWorkflowId(0);

			if (mandateCheckDigit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				mandateCheckDigit.setRecordType("");
				getMandateCheckDigitDAO().save(mandateCheckDigit, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				mandateCheckDigit.setRecordType("");
				getMandateCheckDigitDAO().update(mandateCheckDigit, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mandateCheckDigit);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getMandateCheckDigitDAO().delete with parameters mandateCheckDigit,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtMandateCheckDigits by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		MandateCheckDigit mandateCheckDigit = (MandateCheckDigit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMandateCheckDigitDAO().delete(mandateCheckDigit, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(),method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getMandateCheckDigitDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,String method) {
		logger.debug(Literal.ENTERING);
			
		// Get the model object.
		MandateCheckDigit checkDigit = (MandateCheckDigit) auditDetail.getModelData();

		// Check the unique keys.
		if (checkDigit.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(checkDigit.getRecordType())
				&& mandateCheckDigitDAO.isDuplicateKey(checkDigit.getCheckDigitValue(),
						checkDigit.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CheckDigitValue") + ": " + checkDigit.getCheckDigitValue();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		if (!StringUtils.trimToEmpty(checkDigit.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)
				&& !StringUtils.trimToEmpty(method).equals(PennantConstants.method_doReject)) {
			
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(checkDigit.getLookUpValue());
			errParm[0] = PennantJavaUtil.getLabel("label_LookUpValue") + " : " + valueParm[0];

			int  countDuplicate = mandateCheckDigitDAO.getCheckDigit(checkDigit.getCheckDigitValue(), checkDigit.getLookUpValue(), "_View");
			if(countDuplicate > 0){
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}