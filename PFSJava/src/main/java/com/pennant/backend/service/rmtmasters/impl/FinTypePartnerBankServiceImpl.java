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
 * FileName    		:  FinTypePartnerBankServiceImpl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinTypePartnerBank</b>.<br>
 */
public class FinTypePartnerBankServiceImpl extends GenericService<FinTypePartnerBank> implements
		FinTypePartnerBankService {
	private static final Logger logger = Logger.getLogger(FinTypePartnerBankServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinTypePartnerBanks/FinTypePartnerBanks_Temp by using FinTypePartnerBanksDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using FinTypePartnerBanksDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtFinTypePartnerBanks by using auditHeaderDAO.addAudit(auditHeader)
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

		FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (finTypePartnerBank.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (finTypePartnerBank.isNew()) {
			finTypePartnerBank.setId(Long.parseLong(getFinTypePartnerBankDAO().save(finTypePartnerBank, tableType)));
			auditHeader.getAuditDetail().setModelData(finTypePartnerBank);
			auditHeader.setAuditReference(String.valueOf(finTypePartnerBank.getID()));
		} else {
			getFinTypePartnerBankDAO().update(finTypePartnerBank, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinTypePartnerBanks by using FinTypePartnerBanksDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinTypePartnerBanks by using auditHeaderDAO.addAudit(auditHeader)
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

		FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();
		getFinTypePartnerBankDAO().delete(finTypePartnerBank, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypePartnerBank> finTypePartnerBankList, String tableType,
			String auditTranType, String finType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypePartnerBankList != null && !finTypePartnerBankList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypePartnerBank(),
					new FinTypePartnerBank().getExcludeFields());
			for (int i = 0; i < finTypePartnerBankList.size(); i++) {
				FinTypePartnerBank finTypePartnerBank = finTypePartnerBankList.get(i);
				if (StringUtils.isNotEmpty(finTypePartnerBank.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypePartnerBank
							.getBefImage(), finTypePartnerBank));
				}
			}
			getFinTypePartnerBankDAO().deleteByFinType(finType, tableType);
		}

		return auditDetails;

	}

	/**
	 * getFinTypePartnerBanks fetch the details by using FinTypePartnerBanksDAO's getFinTypePartnerBanksById method.
	 * 
	 * @param iD
	 *            iD of the FinTypePartnerBank.
	 * @return FinTypePartnerBanks
	 */
	@Override
	public FinTypePartnerBank getFinTypePartnerBank(String finType, long iD) {
		return getFinTypePartnerBankDAO().getFinTypePartnerBank(finType, iD, "_View");
	}

	/**
	 * getApprovedFinTypePartnerBanksById fetch the details by using FinTypePartnerBanksDAO's getFinTypePartnerBanksById
	 * method . with parameter id and type as blank. it fetches the approved records from the FinTypePartnerBanks.
	 * 
	 * @param iD
	 *            iD of the FinTypePartnerBank. (String)
	 * @return FinTypePartnerBanks
	 */
	public FinTypePartnerBank getApprovedFinTypePartnerBank(String finType, long iD) {
		return getFinTypePartnerBankDAO().getFinTypePartnerBank(finType, iD, "_AView");
	}
	
	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBanksList(String finType, String type) {
		return getFinTypePartnerBankDAO().getFinTypePartnerBank(finType, type);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinTypePartnerBankDAO().delete with
	 * parameters finTypePartnerBank,"" b) NEW Add new record in to main table by using getFinTypePartnerBankDAO().save
	 * with parameters finTypePartnerBank,"" c) EDIT Update record in the main table by using
	 * getFinTypePartnerBankDAO().update with parameters finTypePartnerBank,"" 3) Delete the record from the workFlow
	 * table by using getFinTypePartnerBankDAO().delete with parameters finTypePartnerBank,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtFinTypePartnerBanks by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtFinTypePartnerBanks by using auditHeaderDAO.addAudit(auditHeader) based on
	 * the transaction Type.
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

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		BeanUtils.copyProperties((FinTypePartnerBank) auditHeader.getAuditDetail().getModelData(), finTypePartnerBank);

		getFinTypePartnerBankDAO().delete(finTypePartnerBank, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finTypePartnerBank.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					finTypePartnerBankDAO.getFinTypePartnerBank(finTypePartnerBank.getFinType(), finTypePartnerBank.getID(), ""));
		}

		if (finTypePartnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinTypePartnerBankDAO().delete(finTypePartnerBank, TableType.MAIN_TAB);
		} else {
			finTypePartnerBank.setRoleCode("");
			finTypePartnerBank.setNextRoleCode("");
			finTypePartnerBank.setTaskId("");
			finTypePartnerBank.setNextTaskId("");
			finTypePartnerBank.setWorkflowId(0);

			if (finTypePartnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finTypePartnerBank.setRecordType("");
				getFinTypePartnerBankDAO().save(finTypePartnerBank, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTypePartnerBank.setRecordType("");
				getFinTypePartnerBankDAO().update(finTypePartnerBank, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTypePartnerBank);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinTypePartnerBankDAO().delete with parameters finTypePartnerBank,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinTypePartnerBanks by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinTypePartnerBankDAO().delete(finTypePartnerBank, TableType.TEMP_TAB);

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinTypePartnerBankDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) auditDetail.getModelData();

		FinTypePartnerBank tempFinTypePartnerBank = null;
		if (finTypePartnerBank.isWorkflow()) {
			tempFinTypePartnerBank = getFinTypePartnerBankDAO().getFinTypePartnerBank(finTypePartnerBank.getFinType(), finTypePartnerBank.getId(),
					"_Temp");
		}
		FinTypePartnerBank befFinTypePartnerBank = getFinTypePartnerBankDAO().getFinTypePartnerBank(
				finTypePartnerBank.getFinType(), finTypePartnerBank.getId(), "");

		FinTypePartnerBank oldFinTypePartnerBank = finTypePartnerBank.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finTypePartnerBank.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ID") + ":" + valueParm[0];

		if (finTypePartnerBank.isNew()) { // for New record or new record into work flow
			if (!finTypePartnerBank.isWorkflow()) {// With out Work flow only new records
				if (befFinTypePartnerBank != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypePartnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
					if (befFinTypePartnerBank != null || tempFinTypePartnerBank != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypePartnerBank == null || tempFinTypePartnerBank != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypePartnerBank.isWorkflow()) { // With out Work flow for update and delete
				if (befFinTypePartnerBank == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypePartnerBank != null
							&& !oldFinTypePartnerBank.getLastMntOn().equals(befFinTypePartnerBank.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {
				if (tempFinTypePartnerBank == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypePartnerBank != null && oldFinTypePartnerBank != null
						&& !oldFinTypePartnerBank.getLastMntOn().equals(tempFinTypePartnerBank.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !finTypePartnerBank.isWorkflow()) {
			auditDetail.setBefImage(befFinTypePartnerBank);
		}

		return auditDetail;
	}

	@Override
	public List<AuditDetail> setFinTypePartnerBankDetailsAuditData(List<FinTypePartnerBank> finTypePartnerBankList,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypePartnerBank(),
				new FinTypePartnerBank().getExcludeFields());

		for (int i = 0; i < finTypePartnerBankList.size(); i++) {
			FinTypePartnerBank finTypePartnerBank = finTypePartnerBankList.get(i);

			if (StringUtils.isEmpty(finTypePartnerBank.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypePartnerBank.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypePartnerBank
					.getBefImage(), finTypePartnerBank));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFinTypePartnerBankDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			TableType tableType = TableType.TEMP_TAB;
			if (StringUtils.isEmpty(type)) {
				tableType = TableType.MAIN_TAB;
				approveRec = true;
				finTypePartnerBank.setRoleCode("");
				finTypePartnerBank.setNextRoleCode("");
				finTypePartnerBank.setTaskId("");
				finTypePartnerBank.setNextTaskId("");
				finTypePartnerBank.setWorkflowId(0);
			}
			if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypePartnerBank.isNewRecord()) {
				saveRecord = true;
				if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypePartnerBank.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypePartnerBank.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypePartnerBank.getRecordType();
				recordStatus = finTypePartnerBank.getRecordStatus();
				finTypePartnerBank.setRecordType("");
				finTypePartnerBank.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypePartnerBankDAO().save(finTypePartnerBank, tableType);
			}
			if (updateRecord) {
				getFinTypePartnerBankDAO().update(finTypePartnerBank, tableType);
			}
			if (deleteRecord) {
				getFinTypePartnerBankDAO().delete(finTypePartnerBank, tableType);
			}
			if (approveRec) {
				finTypePartnerBank.setRecordType(rcdType);
				finTypePartnerBank.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypePartnerBank);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinTypePartnerBankDAO().getPartnerBankCount(finType, paymentType, purpose, partnerBankID);
	}
	
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
	 * @return the finTypePartnerBankDAO
	 */
	public FinTypePartnerBankDAO getFinTypePartnerBankDAO() {
		return finTypePartnerBankDAO;
	}

	/**
	 * @param finTypePartnerBankDAO
	 *            the finTypePartnerBankDAO to set
	 */
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}
}