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
 * FileName    		:  FinCovenantMaintanceServiceImpl.java                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FeeWaiverDetailDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on
 * <b>FeeWaiverHeader</b>.<br>
 * 
 */
public class FeeWaiverHeaderServiceImpl extends GenericService<FeeWaiverHeader> implements FeeWaiverHeaderService {

	private static Logger logger = Logger.getLogger(FeeWaiverHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeWaiverDetailDAO feeWaiverDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	List<ManualAdvise> manualAdviseList;

	public FeeWaiverHeaderServiceImpl() {
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

	public FeeWaiverHeaderDAO getFeeWaiverHeaderDAO() {
		return feeWaiverHeaderDAO;
	}

	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Override
	public FeeWaiverHeader getFeeWaiverByFinRef(FeeWaiverHeader feeWaiverHeader) {
		if (feeWaiverHeader.isNew()) {
			FeeWaiverDetail feeWaiverDetail;
			BigDecimal receivableAmt = BigDecimal.ZERO;
			BigDecimal receivedAmt = BigDecimal.ZERO;
			BigDecimal waivedAmt = BigDecimal.ZERO;
			BigDecimal balAmt = BigDecimal.ZERO;
			List<FeeWaiverDetail> detailList = new ArrayList<FeeWaiverDetail>();
			// get manual advise cases
			List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdvise(feeWaiverHeader.getFinReference());
			if (adviseList != null && !adviseList.isEmpty()) {
				for (ManualAdvise manualAdvise : adviseList) {
					receivableAmt = receivableAmt.add(manualAdvise.getAdviseAmount());
					receivedAmt = receivedAmt.add(manualAdvise.getPaidAmount());
					waivedAmt = waivedAmt.add(manualAdvise.getWaivedAmount());
					balAmt = balAmt.add(manualAdvise.getBalanceAmt());
				}

				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_MANADV);
				feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_MANADV"));
				feeWaiverDetail.setReceivableAmount(receivableAmt);
				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);
				feeWaiverDetail.setBalanceAmount(balAmt);
				detailList.add(feeWaiverDetail);

			}

			/*
			 * if (adviseList != null && !adviseList.isEmpty()) { for
			 * (ManualAdvise manualAdvise : adviseList) { feeWaiverDetail = new
			 * FeeWaiverDetail();
			 * feeWaiverDetail.setAdviseId(manualAdvise.getAdviseID());
			 * feeWaiverDetail.setFeeTypeCode(manualAdvise.getFeeTypeCode());
			 * feeWaiverDetail.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
			 * feeWaiverDetail.setReceivableAmount(manualAdvise.getAdviseAmount(
			 * ));
			 * feeWaiverDetail.setReceivedAmount(manualAdvise.getPaidAmount());
			 * feeWaiverDetail.setWaivedAmount(manualAdvise.getWaivedAmount());
			 * feeWaiverDetail.setBalanceAmount(manualAdvise.getBalanceAmt());
			 * detailList.add(feeWaiverDetail); } }
			 */

			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			balAmt = BigDecimal.ZERO;

			// get Bounce cases

			// List<ManualAdvise> adviseList =
			// manualAdviseDAO.getManualAdvise(feeWaiverHeader.getFinReference());

			// prepare for Late pay penality list
			List<FinODDetails> finODPenaltyList = finODDetailsDAO
					.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), false, true);

			if (finODPenaltyList != null && !finODPenaltyList.isEmpty()) {

				for (FinODDetails finoddetails : finODPenaltyList) {
					receivableAmt = receivableAmt.add(finoddetails.getTotPenaltyAmt());
					receivedAmt = receivedAmt.add(finoddetails.getTotPenaltyPaid());
					waivedAmt = waivedAmt.add(finoddetails.getTotWaived());
					balAmt = balAmt.add(finoddetails.getTotPenaltyBal());
				}
				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setAdviseId(-1);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_ODC);
				feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_ODC"));
				feeWaiverDetail.setReceivableAmount(receivableAmt);
				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);
				feeWaiverDetail.setBalanceAmount(balAmt);
				detailList.add(feeWaiverDetail);
			}

			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			balAmt = BigDecimal.ZERO;
			List<FinODDetails> finODProfitList = finODDetailsDAO
					.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), true, true);

			if (finODProfitList != null && !finODProfitList.isEmpty()) {
				for (FinODDetails finoddetails : finODProfitList) {
					receivableAmt = receivableAmt.add(finoddetails.getLPIAmt());
					receivedAmt = receivedAmt.add(finoddetails.getLPIPaid());
					waivedAmt = waivedAmt.add(finoddetails.getLPIWaived());
					balAmt = balAmt.add(finoddetails.getLPIBal());
				}

				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setAdviseId(-2);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_LPFT);
				feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_LPFT"));
				feeWaiverDetail.setReceivableAmount(receivableAmt);
				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);
				feeWaiverDetail.setBalanceAmount(balAmt);
				detailList.add(feeWaiverDetail);

			}

			feeWaiverHeader.setFeeWaiverDetails(detailList);
		} else {
			feeWaiverHeader = feeWaiverHeaderDAO.getFeeWaiverHeaderByFinRef(feeWaiverHeader.getFinReference(),
					"_TView");
			if (feeWaiverHeader != null) {
				feeWaiverHeader.setFeeWaiverDetails(
						feeWaiverDetailDAO.getFeeWaiverByWaiverId(feeWaiverHeader.getWaiverId(), "_Temp"));
			}
		}

		return feeWaiverHeader;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FeeWaiverDetailDAO getFeeWaiverDetailDAO() {
		return feeWaiverDetailDAO;
	}

	public void setFeeWaiverDetailDAO(FeeWaiverDetailDAO feeWaiverDetailDAO) {
		this.feeWaiverDetailDAO = feeWaiverDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * FeeWaiverHeader/FeeWaiverHeader_Temp by using FeeWaiverHeaderDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using FeeWaiverHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTFeeWaiverHeader by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (feeWaiverHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (feeWaiverHeader.isNew()) {
			feeWaiverHeader.setWaiverId(Long.parseLong(getFeeWaiverHeaderDAO().save(feeWaiverHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
			auditHeader.setAuditReference(String.valueOf(feeWaiverHeader.getWaiverId()));
		} else {
			getFeeWaiverHeaderDAO().update(feeWaiverHeader, tableType);
		}

		if (feeWaiverHeader.getFeeWaiverDetails() != null && !feeWaiverHeader.getFeeWaiverDetails().isEmpty()) {
			List<AuditDetail> details = feeWaiverHeader.getAuditDetailMap().get("FeeWaiverDetails");
			for (FeeWaiverDetail feewaiver : feeWaiverHeader.getFeeWaiverDetails()) {
				feewaiver.setWaiverId(feeWaiverHeader.getWaiverId());
			}
			details = processingFeeWaiverdetails(details, tableType);
			auditDetails.addAll(details);
		}

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List Details
	 * 
	 * @param auditDetails
	 * @param vasRecording
	 * @param tableType
	 * @return
	 */
	private List<AuditDetail> processingFeeWaiverdetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FeeWaiverDetail feeWaiverDetail = (FeeWaiverDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				feeWaiverDetail.setRoleCode("");
				feeWaiverDetail.setNextRoleCode("");
				feeWaiverDetail.setTaskId("");
				feeWaiverDetail.setNextTaskId("");
				feeWaiverDetail.setWorkflowId(0);
			}

			if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (feeWaiverDetail.isNewRecord()) {
				saveRecord = true;
				if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (feeWaiverDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = feeWaiverDetail.getRecordType();
				recordStatus = feeWaiverDetail.getRecordStatus();
				feeWaiverDetail.setRecordType("");
				feeWaiverDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				feeWaiverDetailDAO.save(feeWaiverDetail, type);
			}

			if (updateRecord) {
				feeWaiverDetailDAO.update(feeWaiverDetail, type);
			}

			if (deleteRecord) {
				feeWaiverDetailDAO.delete(feeWaiverDetail, type);
			}

			if (approveRec) {
				feeWaiverDetail.setRecordType(rcdType);
				feeWaiverDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(feeWaiverDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table FeeWaiverHeader by using FeeWaiverHeaderDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader)
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

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFeeWaiverHeaderDAO().delete with parameters FeeWaiverHeader,""
	 * b) NEW Add new record in to main table by using
	 * getFeeWaiverHeaderDAO().save with parameters FeeWaiverHeader,"" c) EDIT
	 * Update record in the main table by using getFeeWaiverHeaderDAO().update
	 * with parameters FeeWaiverHeader,"" 3) Delete the record from the workFlow
	 * table by using getFeeWaiverHeaderDAO().delete with parameters
	 * FeeWaiverHeader,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTFeeWaiverHeader
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tranType = "";

		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		BeanUtils.copyProperties((FeeWaiverHeader) auditHeader.getAuditDetail().getModelData(), feeWaiverHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(feeWaiverHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(feeWaiverHeaderDAO.getFeeWaiverHeaderById(feeWaiverHeader.getWaiverId(), ""));
		}

		if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.MAIN_TAB);

		} else {

			feeWaiverHeader.setRoleCode("");
			feeWaiverHeader.setNextRoleCode("");
			feeWaiverHeader.setTaskId("");
			feeWaiverHeader.setNextTaskId("");
			feeWaiverHeader.setWorkflowId(0);

			// Fee Waivers List
			if (feeWaiverHeader.getFeeWaiverDetails() != null && feeWaiverHeader.getFeeWaiverDetails().size() > 0) {

				for (FeeWaiverDetail details : feeWaiverHeader.getFeeWaiverDetails()) {
					if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						getFeeWaiverDetailDAO().save(details, TableType.MAIN_TAB);
					} else {
						getFeeWaiverDetailDAO().update(details, TableType.MAIN_TAB);
					}
					getFeeWaiverDetailDAO().delete(details, TableType.TEMP_TAB);
				}

			}

			if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feeWaiverHeader.setRecordType("");
				getFeeWaiverHeaderDAO().save(feeWaiverHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeWaiverHeader.setRecordType("");
				getFeeWaiverHeaderDAO().update(feeWaiverHeader, TableType.MAIN_TAB);
			}
			// update the waiver amounts to the respective tables
			allocateWaiverDetails(feeWaiverHeader);

		}

		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void allocateWaiverDetails(FeeWaiverHeader feeWaiverHeader) {
		logger.debug("Entering");

		List<FinODDetails> finodPftdetails = finODDetailsDAO.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(),
				true, false);
		List<FinODDetails> finodPenalitydetails = finODDetailsDAO
				.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), false, false);

		for (FeeWaiverDetail waiverdetail : feeWaiverHeader.getFeeWaiverDetails()) {

			if (waiverdetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_MANADV)) {
				BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
				for (ManualAdvise advise : manualAdviseList) {

					if (advise.getBalanceAmt().compareTo(curwaivedAmt) >= 0) {
						advise.setWaivedAmount(advise.getWaivedAmount().add(curwaivedAmt));
						advise.setBalanceAmt(advise.getBalanceAmt().subtract(curwaivedAmt));
						curwaivedAmt = BigDecimal.ZERO;
					} else {
						advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
						curwaivedAmt = curwaivedAmt.subtract(advise.getBalanceAmt());
						advise.setBalanceAmt(BigDecimal.ZERO);
					}
					advise.setVersion(advise.getVersion() + 1);
					manualAdviseDAO.update(advise, TableType.MAIN_TAB);
					ManualAdviseMovements movement = new ManualAdviseMovements();
					movement.setAdviseID(advise.getAdviseID());
					movement.setMovementDate(DateUtility.getAppDate());
					movement.setMovementAmount(advise.getPaidAmount());
					movement.setPaidAmount(advise.getPaidAmount());
					movement.setWaivedAmount(advise.getWaivedAmount());
					manualAdviseDAO.saveMovement(movement, "");
				}
			}
			// update late pay penalty waived amounts to the Finoddetails table.
			if (waiverdetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_ODC)) {
				BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
				for (FinODDetails oddetail : finodPenalitydetails) {
					if (oddetail.getTotPenaltyBal().compareTo(curwaivedAmt) >= 0) {
						oddetail.setTotWaived(oddetail.getTotWaived().add(curwaivedAmt));
						oddetail.setTotPenaltyBal(oddetail.getTotPenaltyBal().subtract(curwaivedAmt));
						curwaivedAmt = BigDecimal.ZERO;
					} else {
						oddetail.setTotWaived(oddetail.getTotWaived().add(oddetail.getTotPenaltyBal()));
						curwaivedAmt = curwaivedAmt.subtract(oddetail.getTotPenaltyBal());
						oddetail.setTotPenaltyBal(BigDecimal.ZERO);
					}
					finODDetailsDAO.updatePenaltyTotals(oddetail);
				}
			}
			// update late pay profit waived amounts to the Finoddetails table.
			if (waiverdetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_LPFT)) {
				BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
				for (FinODDetails oddetail : finodPftdetails) {
					if (oddetail.getLPIBal().compareTo(curwaivedAmt) >= 0) {
						oddetail.setLPIWaived(curwaivedAmt);
						curwaivedAmt = BigDecimal.ZERO;
					} else {
						oddetail.setLPIWaived(oddetail.getLPIWaived().add(oddetail.getLPIBal()));
						curwaivedAmt = curwaivedAmt.subtract(oddetail.getLPIBal());
						oddetail.setLPIBal(BigDecimal.ZERO);
					}
					finODDetailsDAO.updateLatePftTotals(oddetail.getFinReference(), oddetail.getFinODSchdDate(),
							BigDecimal.ZERO, oddetail.getLPIWaived());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFeeWaiverHeaderDAO().delete with
	 * parameters FeeWaiverHeader,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
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

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, feeWaiverHeader.getBefImage(), feeWaiverHeader));

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
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (feeWaiverHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Collateral Checklist Details
		List<FeeWaiverDetail> feeWaiverDetails = feeWaiverHeader.getFeeWaiverDetails();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (feeWaiverDetails != null && !feeWaiverDetails.isEmpty()) {
				auditDetailMap.put("FeeWaiverDetails", setFeeWaiverAuditData(feeWaiverHeader, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("FeeWaiverDetails"));
			}
		} else {
			String tableType = "_Temp";
			if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}
			feeWaiverDetails = getFeeWaiverDetailDAO().getFeeWaiverByWaiverId(feeWaiverHeader.getWaiverId(), tableType);
			feeWaiverHeader.setFeeWaiverDetails(feeWaiverDetails);

			if (feeWaiverDetails != null && !feeWaiverDetails.isEmpty()) {
				auditDetailMap.put("FeeWaiverDetails", setFeeWaiverAuditData(feeWaiverHeader, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("FeeWaiverDetails"));
			}
		}

		feeWaiverHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setFeeWaiverAuditData(FeeWaiverHeader feeWaiverHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeeWaiverDetail feeWaiver = new FeeWaiverDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(feeWaiver, feeWaiver.getExcludeFields());

		for (int i = 0; i < feeWaiverHeader.getFeeWaiverDetails().size(); i++) {
			FeeWaiverDetail feeWaiverDetail = feeWaiverHeader.getFeeWaiverDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(feeWaiverDetail.getRecordType()))) {
				continue;
			}

			feeWaiverDetail.setWorkflowId(feeWaiverHeader.getWorkflowId());
			boolean isRcdType = false;

			if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (feeWaiverHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				feeWaiverDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			feeWaiverDetail.setRecordStatus(feeWaiverHeader.getRecordStatus());
			feeWaiverDetail.setUserDetails(feeWaiverHeader.getUserDetails());
			feeWaiverDetail.setLastMntOn(feeWaiverHeader.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeWaiverDetail.getBefImage(),
					feeWaiverDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getFeeWaiverHeaderDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditDetail.getModelData();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		BigDecimal totalPenalityBal = BigDecimal.ZERO;
		BigDecimal totalLPIBal = BigDecimal.ZERO;

		// update the waiver amounts to the tables.
		for (FeeWaiverDetail waiverDetail : feeWaiverHeader.getFeeWaiverDetails()) {

			if (waiverDetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_MANADV)) {
				manualAdviseList = manualAdviseDAO.getManualAdvise(feeWaiverHeader.getFinReference());
				for (ManualAdvise manualAdvise : manualAdviseList) {
					// validate the current waived amount against the manual
					// advise.
					if (manualAdvise.getAdviseID() == waiverDetail.getAdviseId()
							&& waiverDetail.getCurrWaiverAmount().compareTo(manualAdvise.getBalanceAmt()) > 0) {
						valueParm[0] = String.valueOf(waiverDetail.getCurrWaiverAmount());
						errParm[0] = waiverDetail.getFeeTypeDesc();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
					}
				}
			} else {

				List<FinODDetails> finodPenalitydetails = finODDetailsDAO
						.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), false, false);
				for (FinODDetails oddetails : finodPenalitydetails) {
					totalPenalityBal = totalPenalityBal.add(oddetails.getTotPenaltyBal());
				}

				List<FinODDetails> finodprofitdetails = finODDetailsDAO
						.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), true, false);
				for (FinODDetails oddetails : finodprofitdetails) {
					totalLPIBal = totalLPIBal.add(oddetails.getLPIBal());
				}

				// validate the current waived amount against Late pay penalty.
				if (waiverDetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_ODC)
						&& waiverDetail.getCurrWaiverAmount().compareTo(totalPenalityBal) > 0) {
					valueParm[0] = String.valueOf(waiverDetail.getCurrWaiverAmount());
					errParm[0] = waiverDetail.getFeeTypeDesc() + ": " + valueParm[0];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
				}
				// validate the current waived amount against the late pay
				// profit
				if (waiverDetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_LPFT)
						&& waiverDetail.getCurrWaiverAmount().compareTo(totalLPIBal) > 0) {
					valueParm[0] = String.valueOf(waiverDetail.getCurrWaiverAmount());
					errParm[0] = waiverDetail.getFeeTypeDesc() + ": " + valueParm[0];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public List<ManualAdvise> getManualAdviseByFinRef(String finReference) {

		return this.manualAdviseDAO.getManualAdvise(finReference);
	}

	@Override
	public List<FinODDetails> getFinODBalByFinRef(String finReference) {
		return getFinODDetailsDAO().getFinODBalByFinRef(finReference);
	}

}