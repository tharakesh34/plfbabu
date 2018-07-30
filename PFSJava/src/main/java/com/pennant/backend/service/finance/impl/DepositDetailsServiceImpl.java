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
 *
 * FileName    		:  DepositDetailsServiceImpl.java									    *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  10-09-2018															*
 *                                                                  
 * Modified Date    :  10-09-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2018       Pennant	                 0.1                                            * 
 * 																					        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CashManagementConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.receipts.CashDenominationDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cashmanagement.impl.CashManagementAccounting;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public class DepositDetailsServiceImpl extends GenericService<DepositDetails> implements DepositDetailsService {
	private static final Logger			logger	= Logger.getLogger(DepositDetailsServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private DepositDetailsDAO			depositDetailsDAO;
	private CashDenominationDAO			cashDenominationDAO;
	private CashManagementAccounting	cashManagementAccounting;
	private PostingsDAO					postingsDAO;
	private DepositChequesDAO			depositChequesDAO;
	private FinReceiptHeaderDAO			finReceiptHeaderDAO;

	public DepositDetailsServiceImpl() {
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

	public DepositDetailsDAO getDepositDetailsDAO() {
		return depositDetailsDAO;
	}

	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	public CashDenominationDAO getCashDenominationDAO() {
		return cashDenominationDAO;
	}

	public void setCashDenominationDAO(CashDenominationDAO cashDenominationDAO) {
		this.cashDenominationDAO = cashDenominationDAO;
	}

	public CashManagementAccounting getCashManagementAccounting() {
		return cashManagementAccounting;
	}

	public void setCashManagementAccounting(CashManagementAccounting cashManagementAccounting) {
		this.cashManagementAccounting = cashManagementAccounting;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public DepositChequesDAO getDepositChequesDAO() {
		return depositChequesDAO;
	}

	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table BMTCastes/BMTCastes_Temp by
	 * using CasteDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by
	 * using CasteDAO's update method 3) Audit the record in to AuditHeader and AdtBMTCastes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * 
	 * @return auditHeader
	 */
	@SuppressWarnings("unused")
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (depositDetails.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (depositDetails.isNew()) {
			depositDetails.setDepositId(getDepositDetailsDAO().save(depositDetails, tableType));
			auditHeader.getAuditDetail().setModelData(depositDetails);
			auditHeader.setAuditReference(String.valueOf(depositDetails.getDepositId()));
		} else {
			getDepositDetailsDAO().update(depositDetails, tableType);
		}

		// DepositMovements List
		if (CollectionUtils.isNotEmpty(depositDetails.getDepositMovementsList())) {
			List<AuditDetail> movementsList = depositDetails.getAuditDetailMap().get("DepositMovements");
			movementsList = processMovementsList(movementsList, tableType.getSuffix(), depositDetails.getDepositId());
			auditDetails.addAll(movementsList);

			// DenominationList
			for (int i = 0; i < movementsList.size(); i++) {
				DepositMovements movements = (DepositMovements) movementsList.get(i).getModelData();

				if (CollectionUtils.isNotEmpty(movements.getDenominationList())) {
					List<AuditDetail> denominations = depositDetails.getAuditDetailMap().get("Denominations");
					denominations = processCashDenominationsList(denominations, tableType.getSuffix(),
							movements.getMovementId());
					auditDetails.addAll(denominations);
				}

				if (CollectionUtils.isNotEmpty(movements.getDepositChequesList())) {
					List<AuditDetail> depositChequesAuditList = depositDetails.getAuditDetailMap()
							.get("DepositCheques");
					depositChequesAuditList = processDepositChequesList(depositChequesAuditList, tableType.getSuffix(),
							movements.getMovementId());
					auditDetails.addAll(depositChequesAuditList);
				}

				break; // We have only one movement, so we are breaking the loop
			}
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> processMovementsList(List<AuditDetail> auditDetails, String type, long depositId) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			DepositMovements depositMovement = (DepositMovements) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				depositMovement.setRoleCode("");
				depositMovement.setNextRoleCode("");
				depositMovement.setTaskId("");
				depositMovement.setNextTaskId("");
				depositMovement.setWorkflowId(0);
			}
			depositMovement.setDepositId(depositId);
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(depositMovement.getRecordType())) {
				deleteRecord = true;
			} else if (depositMovement.isNewRecord()) {
				saveRecord = true;
				if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					depositMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					depositMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					depositMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (depositMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (depositMovement.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = depositMovement.getRecordType();
				recordStatus = depositMovement.getRecordStatus();
				depositMovement.setRecordType("");
				depositMovement.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				depositMovement.setMovementId(depositDetailsDAO.saveDepositMovements(depositMovement, type));
			}
			if (updateRecord) {
				depositDetailsDAO.updateDepositMovements(depositMovement, type);
			}
			if (deleteRecord) {
				depositDetailsDAO.deleteDepositMovements(depositMovement, type);
			}
			if (approveRec) {
				depositMovement.setRecordType(rcdType);
				depositMovement.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(depositMovement);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<AuditDetail> processCashDenominationsList(List<AuditDetail> auditDetails, String type,
			long movementId) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			CashDenomination cashDenomination = (CashDenomination) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				cashDenomination.setRoleCode("");
				cashDenomination.setNextRoleCode("");
				cashDenomination.setTaskId("");
				cashDenomination.setNextTaskId("");
				cashDenomination.setWorkflowId(0);
			}
			cashDenomination.setMovementId(movementId);
			if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (cashDenomination.isNewRecord()) {
				saveRecord = true;
				if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (cashDenomination.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (cashDenomination.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = cashDenomination.getRecordType();
				recordStatus = cashDenomination.getRecordStatus();
				cashDenomination.setRecordType("");
				cashDenomination.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				cashDenominationDAO.save(cashDenomination, type);
			}
			if (updateRecord) {
				cashDenominationDAO.update(cashDenomination, type);
			}
			if (deleteRecord) {
				cashDenominationDAO.delete(cashDenomination, type);
			}
			if (approveRec) {
				cashDenomination.setRecordType(rcdType);
				cashDenomination.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(cashDenomination);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<AuditDetail> processDepositChequesList(List<AuditDetail> auditDetails, String type, long movementId) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			DepositCheques depositCheques = (DepositCheques) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				depositCheques.setStatus("A");
				depositCheques.setRoleCode("");
				depositCheques.setNextRoleCode("");
				depositCheques.setTaskId("");
				depositCheques.setNextTaskId("");
				depositCheques.setWorkflowId(0);
			}
			depositCheques.setMovementId(movementId);
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(depositCheques.getRecordType())) {
				deleteRecord = true;
			} else if (depositCheques.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(depositCheques.getRecordType())) {
					depositCheques.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(depositCheques.getRecordType())) {
					depositCheques.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(depositCheques.getRecordType())) {
					depositCheques.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(depositCheques.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(depositCheques.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(depositCheques.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (depositCheques.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = depositCheques.getRecordType();
				recordStatus = depositCheques.getRecordStatus();
				depositCheques.setRecordType("");
				depositCheques.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				depositChequesDAO.save(depositCheques, type);
				if (approveRec) {
					finReceiptHeaderDAO.updateDepositProcessByReceiptID(depositCheques.getReceiptId(), false, "_Temp");
				}
			}
			if (updateRecord) {
				depositChequesDAO.update(depositCheques, type);
			}
			if (deleteRecord) {
				depositChequesDAO.delete(depositCheques, type);
			}
			if (approveRec) {
				depositCheques.setRecordType(rcdType);
				depositCheques.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(depositCheques);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTCastes by using CasteDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader)
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
		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		getDepositDetailsDAO().delete(depositDetails, TableType.MAIN_TAB);
		auditHeader
				.setAuditDetails(processChildsAudit(deleteChilds(depositDetails, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> deleteChilds(DepositDetails depositDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		// Fees
		if (CollectionUtils.isNotEmpty(depositDetails.getDepositMovementsList())) {
			auditDetailsList.addAll(deleteDepositMovements(depositDetails.getDepositMovementsList(), tableType,
					auditTranType, depositDetails.getDepositId()));

			for (DepositMovements movements : depositDetails.getDepositMovementsList()) {
				//Denominations
				if (CollectionUtils.isNotEmpty(movements.getDenominationList())) {
					auditDetailsList.addAll(deleteCashDenomination(movements.getDenominationList(), tableType,
							auditTranType, movements.getMovementId()));
				}
				//Deposit Cheques
				if (CollectionUtils.isNotEmpty(movements.getDepositChequesList())) {
					auditDetailsList.addAll(deleteDepositCheques(movements.getDepositChequesList(), tableType,
							auditTranType, movements.getMovementId()));
				}
				break; // We have only one movement, so we are breaking the loop
			}
		}

		logger.debug("Leaving");

		return auditDetailsList;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof DepositMovements) {
				rcdType = ((DepositMovements) object).getRecordType();
			} else if (object instanceof CashDenomination) {
				rcdType = ((CashDenomination) object).getRecordType();
			} else if (object instanceof DepositCheques) {
				rcdType = ((DepositCheques) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<AuditDetail> deleteDepositMovements(List<DepositMovements> denominationsList, String tableType,
			String auditTranType, long depositId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (denominationsList != null && !denominationsList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new DepositMovements(),
					new DepositMovements().getExcludeFields());
			for (int i = 0; i < denominationsList.size(); i++) {
				DepositMovements movement = denominationsList.get(i);
				if (StringUtils.isNotEmpty(movement.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], movement.getBefImage(),
							movement));
				}
			}

			this.depositDetailsDAO.deleteMovementsByDepositId(depositId, tableType);
		}

		return auditDetails;

	}

	private List<AuditDetail> deleteCashDenomination(List<CashDenomination> denominationsList, String tableType,
			String auditTranType, long movementId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (denominationsList != null && !denominationsList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CashDenomination(),
					new CashDenomination().getExcludeFields());
			for (int i = 0; i < denominationsList.size(); i++) {
				CashDenomination denomination = denominationsList.get(i);
				if (StringUtils.isNotEmpty(denomination.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							denomination.getBefImage(), denomination));
				}
			}

			this.cashDenominationDAO.deleteByMovementId(movementId, tableType);
		}

		return auditDetails;

	}

	private List<AuditDetail> deleteDepositCheques(List<DepositCheques> depositChequesList, String tableType,
			String auditTranType, long movementId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (depositChequesList != null && !depositChequesList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new DepositCheques(),
					new DepositCheques().getExcludeFields());
			for (int i = 0; i < depositChequesList.size(); i++) {
				DepositCheques depositCheques = depositChequesList.get(i);
				if (StringUtils.isNotEmpty(depositCheques.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							depositCheques.getBefImage(), depositCheques));
				}
			}

			this.depositChequesDAO.deleteByMovementId(movementId, tableType);
		}

		return auditDetails;

	}

	/**
	 * getCasteById fetch the details by using CasteDAO's getCasteById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Caste
	 */
	@Override
	public DepositDetails getDepositDetailsById(long depositId) {
		DepositDetails depositDetails = getDepositDetailsDAO().getDepositDetailsById(depositId, "_View");

		if (depositDetails != null && StringUtils.isNotBlank(depositDetails.getRecordType())) {
			DepositMovements depositMovements = getDepositDetailsDAO().getDepositMovementsByDepositId(depositId,
					"_TView");
			if (depositMovements != null) {
				List<CashDenomination> denominationsList = getCashDenominationDAO()
						.getCashDenominationList(depositMovements.getMovementId(), "_TView");
				depositMovements.setDenominationList(denominationsList);
				List<DepositCheques> chequesList = getDepositChequesDAO()
						.getDepositChequesList(depositMovements.getMovementId(), "_TView");
				depositMovements.setDepositChequesList(chequesList);
			}
			depositDetails.setDepositMovements(depositMovements);
		}
		return depositDetails;
	}

	/**
	 * getApprovedCasteById fetch the details by using CasteDAO's getCasteById method . with parameter id and type as
	 * blank. it fetches the approved records from the BMTCastes.
	 * 
	 * @param id
	 *            (String)
	 * @return Caste
	 */
	public DepositDetails getApprovedDepositDetailsById(long id) {
		return getDepositDetailsDAO().getDepositDetailsById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCasteDAO().delete with parameters
	 * caste,"" b) NEW Add new record in to main table by using getCasteDAO().save with parameters caste,"" c) EDIT
	 * Update record in the main table by using getCasteDAO().update with parameters caste,"" 3) Delete the record from
	 * the workFlow table by using getCasteDAO().delete with parameters caste,"_Temp" 4) Audit the record in to
	 * AuditHeader and AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@SuppressWarnings("unused")
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		long partnerBankId = 0;
		long movementId = 0;
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		DepositDetails depositDetails = new DepositDetails();
		BeanUtils.copyProperties((DepositDetails) auditHeader.getAuditDetail().getModelData(), depositDetails);
		
		// Accounting Execution Process
		AEEvent aeEvent = null;
		String eventCode = null;
		if (CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH.equals(depositDetails.getDepositType())) {
			eventCode = AccountEventConstants.ACCEVENT_CASHTOBANK;
		} else if (CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD.equals(depositDetails.getDepositType())) {
			eventCode = AccountEventConstants.ACCEVENT_CHEQUETOBANK;
		}
		if (StringUtils.isNotEmpty(eventCode)) {
			aeEvent = this.cashManagementAccounting.generateAccounting(eventCode,
					depositDetails.getUserDetails().getBranchCode(), depositDetails.getBranchCode(),
					depositDetails.getReservedAmount(),
					depositDetails.getDepositMovementsList().get(0).getPartnerBankId(), 
					depositDetails.getDepositMovementsList().get(0).getMovementId(), null);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(depositDetails.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(depositDetailsDAO.getDepositDetailsById(depositDetails.getDepositId(), ""));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(depositDetails.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(processChildsAudit(deleteChilds(depositDetails, "", tranType)));
			getDepositDetailsDAO().delete(depositDetails, TableType.MAIN_TAB);
		} else {
			depositDetails.setRoleCode("");
			depositDetails.setNextRoleCode("");
			depositDetails.setTaskId("");
			depositDetails.setNextTaskId("");
			depositDetails.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(depositDetails.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				depositDetails.setRecordType("");
				getDepositDetailsDAO().save(depositDetails, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				depositDetails.setRecordType("");
				getDepositDetailsDAO().update(depositDetails, TableType.MAIN_TAB);
			}

			// DepositMovements List
			if (CollectionUtils.isNotEmpty(depositDetails.getDepositMovementsList())) {
				List<AuditDetail> movementsList = depositDetails.getAuditDetailMap().get("DepositMovements");
				if (aeEvent != null && aeEvent.getLinkedTranId() > 0) {
					((DepositMovements) movementsList.get(0).getModelData()).setLinkedTranId(aeEvent.getLinkedTranId());
				}
				movementsList = processMovementsList(movementsList, "", depositDetails.getDepositId());
				auditDetails.addAll(movementsList);

				// DenominationList
				for (int i = 0; i < movementsList.size(); i++) {
					DepositMovements movements = (DepositMovements) movementsList.get(i).getModelData();
					partnerBankId = movements.getPartnerBankId();
					movementId = movements.getMovementId();
					if (CollectionUtils.isNotEmpty(movements.getDenominationList())) {
						List<AuditDetail> denominations = depositDetails.getAuditDetailMap().get("Denominations");
						denominations = processCashDenominationsList(denominations, "", movements.getMovementId());
						auditDetails.addAll(denominations);
					}
					if (CollectionUtils.isNotEmpty(movements.getDepositChequesList())) {
						List<AuditDetail> depositChequesAuditDetails = depositDetails.getAuditDetailMap()
								.get("DepositCheques");
						depositChequesAuditDetails = processDepositChequesList(depositChequesAuditDetails, "",
								movements.getMovementId());
						auditDetails.addAll(depositChequesAuditDetails);
					}
					break; // We have only one movement, so we are breaking the loop
				}
			}
		}

		getDepositDetailsDAO().delete(depositDetails, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// List
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(depositDetails, "_Temp", auditHeader.getAuditTranType())));

		String[] fields = PennantJavaUtil.getFieldDetails(new DepositDetails(), depositDetails.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				depositDetails.getBefImage(), depositDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(depositDetails);

		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCasteDAO().delete with parameters caste,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDepositDetailsDAO().delete(depositDetails, TableType.TEMP_TAB);

		// List
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(depositDetails, "_Temp", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

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
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		//Deposit Movements
		if (depositDetails.getAuditDetailMap().get("DepositMovements") != null) {
			auditDetails = depositDetails.getAuditDetailMap().get("DepositMovements");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationMovements(auditDetail, usrLanguage, method).getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		//Cash Denominations
		if (depositDetails.getAuditDetailMap().get("Denominations") != null) {
			auditDetails = depositDetails.getAuditDetailMap().get("Denominations");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationDenominations(auditDetail, usrLanguage, method).getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		//Deposit Cheques
		if (depositDetails.getAuditDetailMap().get("DepositCheques") != null) {
			auditDetails = depositDetails.getAuditDetailMap().get("DepositCheques");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationDepositCheques(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		logger.debug("Leaving");

		return errorDetails;
	}

	private AuditDetail validationMovements(AuditDetail auditDetail, String usrLanguage, String type) {
		logger.debug("Entering");

		// Get the model object.
		DepositMovements depositMovements = (DepositMovements) auditDetail.getModelData();
		String depositSlipNo = depositMovements.getDepositSlipNumber();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_DepositDetailsDialog_DepositSlipNumber.value") + ": "
				+ depositMovements.getDepositSlipNumber();

		// Check the unique keys.
		if (depositMovements.isNew()
				&& (PennantConstants.RECORD_TYPE_NEW.equals(depositMovements.getRecordType())
						|| PennantConstants.RCD_ADD.equals(depositMovements.getRecordType()))
				&& this.depositDetailsDAO.isDuplicateKey(depositSlipNo, TableType.BOTH_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	private AuditDetail validationDenominations(AuditDetail auditDetail, String usrLanguage, String type) {
		logger.debug("Entering");

		// Get the model object.
		/*
		 * CashDenomination cashDenomination = (CashDenomination) auditDetail.getModelData(); long requestId =
		 * cashDenomination.getProcessId(); String[] parameters = new String[1]; parameters[0] =
		 * PennantJavaUtil.getLabel("label_BranchCode") + ": " + cashDenomination.getDenomination();
		 * 
		 * // Check the unique keys. if (cashDenomination.isNew() &&
		 * PennantConstants.RECORD_TYPE_NEW.equals(cashDenomination.getRecordType()) &&
		 * this.cashDenominationDAO.isDuplicateKey(requestId, cashDenomination.isWorkflow() ? TableType.BOTH_TAB :
		 * TableType.MAIN_TAB)) {
		 * 
		 * auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", parameters, null)); }
		 */

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	private AuditDetail validationDepositCheques(AuditDetail auditDetail, String usrLanguage, String type) {
		logger.debug("Entering");

		// Get the model object.
		DepositCheques depositCheques = (DepositCheques) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_Id") + ": " + depositCheques.getId();

		// Check the unique keys.
		if (depositCheques.isNew()
				&& (PennantConstants.RECORD_TYPE_NEW.equals(depositCheques.getRecordType())
						|| PennantConstants.RCD_ADD.equals(depositCheques.getRecordType()))
				&& this.depositChequesDAO.isDuplicateKey(depositCheques.getId(), TableType.BOTH_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

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
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		// String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (depositDetails.isWorkflow()) {
				// auditTranType = PennantConstants.TRAN_WF;
			}
		}

		auditHeader.getAuditDetail().setModelData(depositDetails);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		DepositDetails depositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (depositDetails.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<DepositMovements> depositMovementsList = depositDetails.getDepositMovementsList();
		if (CollectionUtils.isNotEmpty(depositMovementsList)) {
			for (DepositMovements depositMovement : depositMovementsList) {
				depositMovement.setLastMntOn(depositDetails.getLastMntOn());
				depositMovement.setLastMntBy(depositDetails.getLastMntBy());
				depositMovement.setRecordStatus(depositDetails.getRecordStatus());
				depositMovement.setUserDetails(depositDetails.getUserDetails());
				depositMovement.setWorkflowId(depositDetails.getWorkflowId());
				depositMovement.setRoleCode(depositDetails.getRoleCode());
				depositMovement.setNextRoleCode(depositDetails.getNextRoleCode());
				depositMovement.setTaskId(depositDetails.getTaskId());
				depositMovement.setNextTaskId(depositDetails.getNextTaskId());

				// Denominations
				if (CollectionUtils.isNotEmpty(depositMovement.getDenominationList())) {
					for (CashDenomination cashDenomination : depositMovement.getDenominationList()) {
						cashDenomination.setLastMntOn(depositDetails.getLastMntOn());
						cashDenomination.setLastMntBy(depositDetails.getLastMntBy());
						cashDenomination.setRecordStatus(depositDetails.getRecordStatus());
						cashDenomination.setUserDetails(depositDetails.getUserDetails());
						cashDenomination.setWorkflowId(depositDetails.getWorkflowId());
						cashDenomination.setRoleCode(depositDetails.getRoleCode());
						cashDenomination.setNextRoleCode(depositDetails.getNextRoleCode());
						cashDenomination.setTaskId(depositDetails.getTaskId());
						cashDenomination.setNextTaskId(depositDetails.getNextTaskId());
					}

					auditDetailMap.put("Denominations", setCashDenominationsAuditData(
							depositMovement.getDenominationList(), auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("Denominations"));
				}

				// Deposit Cheques
				if (CollectionUtils.isNotEmpty(depositMovement.getDepositChequesList())) {
					for (DepositCheques depositCheque : depositMovement.getDepositChequesList()) {
						depositCheque.setLastMntOn(depositDetails.getLastMntOn());
						depositCheque.setLastMntBy(depositDetails.getLastMntBy());
						depositCheque.setRecordStatus(depositDetails.getRecordStatus());
						depositCheque.setUserDetails(depositDetails.getUserDetails());
						depositCheque.setWorkflowId(depositDetails.getWorkflowId());
						depositCheque.setRoleCode(depositDetails.getRoleCode());
						depositCheque.setNextRoleCode(depositDetails.getNextRoleCode());
						depositCheque.setTaskId(depositDetails.getTaskId());
						depositCheque.setNextTaskId(depositDetails.getNextTaskId());
					}

					auditDetailMap.put("DepositCheques",
							setDepositChequesAuditData(depositMovement.getDepositChequesList(), auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("DepositCheques"));
				}

				break; // We have only one movement, so we are breaking the loop
			}
			auditDetailMap.put("DepositMovements",
					setDepositMovementsAuditData(depositMovementsList, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DepositMovements"));
		}

		depositDetails.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(depositDetails);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> setDepositMovementsAuditData(List<DepositMovements> depositMovementsList,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DepositMovements(),
				new DepositMovements().getExcludeFields());
		for (int i = 0; i < depositMovementsList.size(); i++) {
			DepositMovements depositMovement = depositMovementsList.get(i);

			//TODO for Cash Denominations Maintenance
			/*
			 * if (StringUtils.isEmpty(depositMovement.getRecordType())) { continue; }
			 */

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(depositMovement.getRecordType())) {
				depositMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(depositMovement.getRecordType())) {
				depositMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(depositMovement.getRecordType())) {
				depositMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				depositMovement.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(depositMovement.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(depositMovement.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(depositMovement.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], depositMovement.getBefImage(),
					depositMovement));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<AuditDetail> setCashDenominationsAuditData(List<CashDenomination> cashDenominationsList,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CashDenomination(),
				new CashDenomination().getExcludeFields());
		for (int i = 0; i < cashDenominationsList.size(); i++) {
			CashDenomination cashDenomination = cashDenominationsList.get(i);

			if (StringUtils.isEmpty(cashDenomination.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(cashDenomination.getRecordType())) {
				cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(cashDenomination.getRecordType())) {
				cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(cashDenomination.getRecordType())) {
				cashDenomination.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				cashDenomination.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(cashDenomination.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(cashDenomination.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(cashDenomination.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], cashDenomination.getBefImage(),
					cashDenomination));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<AuditDetail> setDepositChequesAuditData(List<DepositCheques> depositChequesList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DepositCheques(),
				new DepositCheques().getExcludeFields());
		for (int i = 0; i < depositChequesList.size(); i++) {
			DepositCheques depositCheques = depositChequesList.get(i);

			if (StringUtils.isEmpty(depositCheques.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(depositCheques.getRecordType())) {
				depositCheques.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(depositCheques.getRecordType())) {
				depositCheques.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(depositCheques.getRecordType())) {
				depositCheques.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				depositCheques.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(depositCheques.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(depositCheques.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(depositCheques.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], depositCheques.getBefImage(),
					depositCheques));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getCasteDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign the
	 * to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		DepositDetails depositDetails = (DepositDetails) auditDetail.getModelData();
		long depositId = depositDetails.getDepositId();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_DepositId") + ": " + depositId;

		// Check the unique keys.
		if (depositDetails.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(depositDetails.getRecordType())
				&& depositDetailsDAO.isDuplicateKey(depositId,
						depositDetails.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		/*
		 * if (StringUtils.trimToEmpty(depositDetails.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
		 * boolean exist = this.customerDAO.isCasteExist(depositDetails.getCasteId(), "_View"); if (exist) {
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006",
		 * parameters, null), usrLanguage)); } }
		 */

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public DepositMovements getDepositMovementsById(long movementId) {

		DepositMovements depositMovements = getDepositDetailsDAO().getDepositMovementsById(movementId, "_View");

		if (depositMovements != null) {
			List<CashDenomination> denominationsList = getCashDenominationDAO().getCashDenominationList(movementId,
					"_View");
			depositMovements.setDenominationList(denominationsList);

			List<DepositCheques> chequesList = getDepositChequesDAO().getDepositChequesList(movementId, "_View");
			depositMovements.setDepositChequesList(chequesList);
		}

		return depositMovements;
	}

	@Override
	public DepositMovements getApprovedDepositMovementsById(long movementId) {

		DepositMovements depositMovements = getDepositDetailsDAO().getDepositMovementsById(movementId, "_AView");

		if (depositMovements != null) {
			List<CashDenomination> denominationsList = getCashDenominationDAO().getCashDenominationList(movementId,
					"_AView");
			depositMovements.setDenominationList(denominationsList);

			List<DepositCheques> chequesList = getDepositChequesDAO().getDepositChequesList(movementId, "_AView");
			depositMovements.setDepositChequesList(chequesList);
		}

		return depositMovements;
	}

	/**
	 * Method for fetching list of entries executed based on Linked Transaction ID
	 */
	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid) {
		return getPostingsDAO().getPostingsByLinkTransId(linkedTranid);
	}

	/**
	 * Method for fetching list of Deposit Cheques
	 */
	@Override
	public List<DepositCheques> getDepositChequesList(String branchCode) {
		return getDepositChequesDAO().getDepositChequesList(branchCode);
	}
}
