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
 * FileName    		:  GSTRateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-05-2019    														*
 *                                                                  						*
 * Modified Date    :  20-05-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-05-2019       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.GSTRateDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.GSTRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>GSTRate</b>.<br>
 */
public class GSTRateServiceImpl extends GenericService<GSTRate> implements GSTRateService {
	private static final Logger logger = LogManager.getLogger(GSTRateServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GSTRateDAO gstRateDAO;

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

	public GSTRateDAO getGstRateDAO() {
		return gstRateDAO;
	}

	public void setGstRateDAO(GSTRateDAO gstRateDAO) {
		this.gstRateDAO = gstRateDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table GST_RATES/GST_RATES_Temp by
	 * using GST_RATESDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using GST_RATESDAO's update method 3) Audit the record in to AuditHeader and AdtGST_RATES by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		GSTRate gSTRate = (GSTRate) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (gSTRate.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (gSTRate.isNewRecord()) {
			gSTRate.setId(Long.parseLong(getGstRateDAO().save(gSTRate, tableType)));
			auditHeader.getAuditDetail().setModelData(gSTRate);
			auditHeader.setAuditReference(String.valueOf(gSTRate.getId()));
		} else {
			getGstRateDAO().update(gSTRate, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * GST_RATES by using GST_RATESDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtGST_RATES by using auditHeaderDAO.addAudit(auditHeader)
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

		GSTRate gSTRate = (GSTRate) auditHeader.getAuditDetail().getModelData();
		getGstRateDAO().delete(gSTRate, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getGST_RATES fetch the details by using GST_RATESDAO's getGST_RATESById method.
	 * 
	 * @param id
	 *            id of the GSTRate.
	 * @return GST_RATES
	 */
	@Override
	public GSTRate getGSTRate(long id) {
		return getGstRateDAO().getGSTRate(id, "_View");
	}

	/**
	 * getApprovedGST_RATESById fetch the details by using GST_RATESDAO's getGST_RATESById method . with parameter id
	 * and type as blank. it fetches the approved records from the GST_RATES.
	 * 
	 * @param id
	 *            id of the GSTRate. (String)
	 * @return GST_RATES
	 */
	public GSTRate getApprovedGSTRate(long id) {
		return getGstRateDAO().getGSTRate(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getGSTRateDAO().delete with parameters
	 * gSTRate,"" b) NEW Add new record in to main table by using getGSTRateDAO().save with parameters gSTRate,"" c)
	 * EDIT Update record in the main table by using getGSTRateDAO().update with parameters gSTRate,"" 3) Delete the
	 * record from the workFlow table by using getGSTRateDAO().delete with parameters gSTRate,"_Temp" 4) Audit the
	 * record in to AuditHeader and AdtGST_RATES by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtGST_RATES by using auditHeaderDAO.addAudit(auditHeader) based on the
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

		GSTRate gSTRate = new GSTRate();
		BeanUtils.copyProperties((GSTRate) auditHeader.getAuditDetail().getModelData(), gSTRate);

		getGstRateDAO().delete(gSTRate, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(gSTRate.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(gstRateDAO.getGSTRate(gSTRate.getId(), ""));
		}

		if (gSTRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getGstRateDAO().delete(gSTRate, TableType.MAIN_TAB);
		} else {
			gSTRate.setRoleCode("");
			gSTRate.setNextRoleCode("");
			gSTRate.setTaskId("");
			gSTRate.setNextTaskId("");
			gSTRate.setWorkflowId(0);

			if (gSTRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				gSTRate.setRecordType("");
				getGstRateDAO().save(gSTRate, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				gSTRate.setRecordType("");
				getGstRateDAO().update(gSTRate, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(gSTRate);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getGSTRateDAO().delete with parameters gSTRate,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtGST_RATES by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		GSTRate gSTRate = (GSTRate) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGstRateDAO().delete(gSTRate, TableType.TEMP_TAB);

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getGSTRateDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		GSTRate gSTRate = (GSTRate) auditDetail.getModelData();

		// Check the unique keys.
		if (gSTRate.isNewRecord() && gstRateDAO.isDuplicateKey(gSTRate.getId(), gSTRate.getFromState(), gSTRate.getToState(),
				gSTRate.getTaxType(), gSTRate.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];

			parameters[0] = PennantJavaUtil.getLabel("label_FromState") + ": " + gSTRate.getFromState();
			parameters[1] = PennantJavaUtil.getLabel("label_ToState") + ": " + gSTRate.getToState();
			parameters[2] = PennantJavaUtil.getLabel("label_TaxType") + ": " + gSTRate.getTaxType();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
		}

		if (StringUtils.equals(gSTRate.getTaxType(), RuleConstants.CODE_CESS)
				&& !(RuleConstants.CALCON_TRANSACTION_AMOUNT.equals(gSTRate.getCalcOn()))) {

			String gstOrInclGst = RuleConstants.CODE_TOTAL_AMOUNT_INCLUDINGGST + "," + RuleConstants.CODE_TOTAL_GST;
			if (gstOrInclGst.contains(gSTRate.getCalcOn())) {
				boolean cgst = !gstRateDAO.isGSTExist(gSTRate.getFromState(), gSTRate.getToState(),
						RuleConstants.CODE_CGST);
				boolean sgst = !gstRateDAO.isGSTExist(gSTRate.getFromState(), gSTRate.getToState(),
						RuleConstants.CODE_SGST);
				boolean igst = !gstRateDAO.isGSTExist(gSTRate.getFromState(), gSTRate.getToState(),
						RuleConstants.CODE_IGST);
				boolean ugst = !gstRateDAO.isGSTExist(gSTRate.getFromState(), gSTRate.getToState(),
						RuleConstants.CODE_UGST);

				String[] parameters = new String[4];

				StringBuilder calcOnMsg = new StringBuilder();
				if (cgst) {
					calcOnMsg.append(RuleConstants.CODE_CGST + " ,");
				}
				if (sgst) {
					calcOnMsg.append(RuleConstants.CODE_SGST + " ,");
				}
				if (igst) {
					calcOnMsg.append(RuleConstants.CODE_IGST + " ,");
				}
				if (ugst) {
					calcOnMsg.append(RuleConstants.CODE_UGST + " ,");
				}

				if (calcOnMsg.length() > 0) {
					parameters[0] = gSTRate.getCalcOn();
					parameters[1] = PennantJavaUtil.getLabel("label_FromState") + ": " + gSTRate.getFromState();
					parameters[2] = PennantJavaUtil.getLabel("label_ToState") + ": " + gSTRate.getToState();
					parameters[3] = PennantJavaUtil.getLabel("label_TaxType") + ": " + calcOnMsg.toString();

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65039", parameters, null));
				}
			} else {
				if (!gstRateDAO.isGSTExist(gSTRate.getFromState(), gSTRate.getToState(), gSTRate.getCalcOn())) {
					String[] parameters = new String[4];

					parameters[0] = gSTRate.getCalcOn();
					parameters[1] = PennantJavaUtil.getLabel("label_FromState") + ": " + gSTRate.getFromState();
					parameters[2] = PennantJavaUtil.getLabel("label_ToState") + ": " + gSTRate.getToState();
					parameters[3] = PennantJavaUtil.getLabel("label_TaxType") + ": " + gSTRate.getCalcOn();

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65039", parameters, null));
				}
			}

		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}