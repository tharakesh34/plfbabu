/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinTypePartnerBankServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
public class FinTypePartnerBankServiceImpl extends GenericService<FinTypePartnerBank>
		implements FinTypePartnerBankService {
	private static final Logger logger = LogManager.getLogger(FinTypePartnerBankServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinTypePartnerBank fpb = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (fpb.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (fpb.isNewRecord()) {
			fpb.setId(Long.parseLong(finTypePartnerBankDAO.save(fpb, tableType)));
			auditHeader.getAuditDetail().setModelData(fpb);
			auditHeader.setAuditReference(String.valueOf(fpb.getID()));
		} else {
			finTypePartnerBankDAO.update(fpb, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinTypePartnerBank fpb = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();
		finTypePartnerBankDAO.delete(fpb, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypePartnerBank> fpbList, TableType tableType, String auditTranType,
			String finType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (fpbList != null && !fpbList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypePartnerBank(),
					new FinTypePartnerBank().getExcludeFields());
			for (int i = 0; i < fpbList.size(); i++) {
				FinTypePartnerBank finTypePartnerBank = fpbList.get(i);
				if (StringUtils.isNotEmpty(finTypePartnerBank.getRecordType())
						|| StringUtils.isEmpty(tableType.getSuffix())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypePartnerBank.getBefImage(), finTypePartnerBank));
				}
			}
			finTypePartnerBankDAO.deleteByFinType(finType, tableType);
		}

		return auditDetails;

	}

	@Override
	public FinTypePartnerBank getPartnerBank(String finType, long id) {
		return finTypePartnerBankDAO.getFinTypePartnerBank(finType, id, TableType.VIEW);
	}

	public FinTypePartnerBank getApprovedPartnerBank(String finType, long id) {
		return finTypePartnerBankDAO.getFinTypePartnerBank(finType, id, TableType.AVIEW);
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();
		BeanUtils.copyProperties((FinTypePartnerBank) auditHeader.getAuditDetail().getModelData(), fpb);

		finTypePartnerBankDAO.delete(fpb, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fpb.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					finTypePartnerBankDAO.getFinTypePartnerBank(fpb.getFinType(), fpb.getID(), TableType.MAIN_TAB));
		}

		if (fpb.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			finTypePartnerBankDAO.delete(fpb, TableType.MAIN_TAB);
		} else {
			fpb.setRoleCode("");
			fpb.setNextRoleCode("");
			fpb.setTaskId("");
			fpb.setNextTaskId("");
			fpb.setWorkflowId(0);

			if (fpb.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fpb.setRecordType("");
				finTypePartnerBankDAO.save(fpb, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fpb.setRecordType("");
				finTypePartnerBankDAO.update(fpb, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fpb);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

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
		finTypePartnerBankDAO.delete(finTypePartnerBank, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinTypePartnerBank fpb = (FinTypePartnerBank) auditDetail.getModelData();

		FinTypePartnerBank tfpb = null;

		if (fpb.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(fpb.getRecordType())) {
			int count = finTypePartnerBankDAO.getPartnerBankCountByCluster(fpb);
			if (count > 0) {
				String[] parameters = new String[4];
				parameters[0] = PennantJavaUtil.getLabel("label_LoanTypePartnerbankMappingDialogue_FinType.value")
						+ ": " + fpb.getFinType();
				parameters[1] = PennantJavaUtil.getLabel("label_LoanTypePartnerbankMappingDialogue_PaymentType.value")
						+ ": " + fpb.getPaymentMode();
				parameters[2] = PennantJavaUtil.getLabel("label_LoanTypePartnerbankMappingDialogue_Purpose.value")
						+ ": " + fpb.getPurpose();
				parameters[3] = PennantJavaUtil.getLabel("label_LoanTypePartnerbankMappingDialogue_PartnerBank.value")
						+ ": " + fpb.getPartnerBankCode();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
			}
		}

		if (fpb.isWorkflow()) {
			tfpb = finTypePartnerBankDAO.getFinTypePartnerBank(fpb.getFinType(), fpb.getId(), TableType.TEMP_TAB);
		}

		FinTypePartnerBank bfpb = finTypePartnerBankDAO.getFinTypePartnerBank(fpb.getFinType(), fpb.getId(),
				TableType.MAIN_TAB);

		FinTypePartnerBank oldfpb = fpb.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(fpb.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ID") + ":" + valueParm[0];

		if (fpb.isNewRecord()) {
			if (!fpb.isWorkflow()) {
				if (bfpb != null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else {
				if (fpb.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (bfpb != null || tfpb != null) {

						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else {
					if (bfpb == null || tfpb != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			if (!fpb.isWorkflow()) {
				if (bfpb == null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldfpb != null && !oldfpb.getLastMntOn().equals(bfpb.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tfpb == null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tfpb != null && oldfpb != null && !oldfpb.getLastMntOn().equals(tfpb.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !fpb.isWorkflow()) {
			auditDetail.setBefImage(bfpb);
		}

		return auditDetail;
	}

	@Override
	public List<AuditDetail> setAuditData(List<FinTypePartnerBank> fpbList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypePartnerBank(),
				new FinTypePartnerBank().getExcludeFields());

		for (int i = 0; i < fpbList.size(); i++) {
			FinTypePartnerBank fpb = fpbList.get(i);

			if (StringUtils.isEmpty(fpb.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				fpb.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				fpb.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				fpb.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				fpb.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fpb.getBefImage(), fpb));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processDetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypePartnerBank fpb = (FinTypePartnerBank) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			TableType tableType = TableType.TEMP_TAB;

			if (StringUtils.isEmpty(type.getSuffix())) {
				tableType = TableType.MAIN_TAB;
				approveRec = true;
				fpb.setRoleCode("");
				fpb.setNextRoleCode("");
				fpb.setTaskId("");
				fpb.setNextTaskId("");
				fpb.setWorkflowId(0);
			}
			if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (fpb.isNewRecord()) {
				saveRecord = true;
				if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					fpb.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					fpb.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					fpb.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (fpb.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (fpb.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = fpb.getRecordType();
				recordStatus = fpb.getRecordStatus();
				fpb.setRecordType("");
				fpb.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finTypePartnerBankDAO.save(fpb, tableType);
			}
			if (updateRecord) {
				finTypePartnerBankDAO.update(fpb, tableType);
			}
			if (deleteRecord) {
				finTypePartnerBankDAO.delete(fpb, tableType);
			}
			if (approveRec) {
				fpb.setRecordType(rcdType);
				fpb.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(fpb);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return finTypePartnerBankDAO.getPartnerBankCount(finType, paymentType, purpose, partnerBankID);
	}

	@Override
	public List<FinTypePartnerBank> getByFinTypeAndPurpose(FinTypePartnerBank fab) {
		return finTypePartnerBankDAO.getByFinTypeAndPurpose(fab);
	}

	@Override
	public List<FinTypePartnerBank> getPartnerBanks(String finType, TableType tableType) {
		return finTypePartnerBankDAO.getFinTypePartnerBanks(finType, tableType);
	}

	@Override
	public List<FinTypePartnerBank> getPartnerBanksList(FinTypePartnerBank fab, TableType tableType) {
		return finTypePartnerBankDAO.getFinTypePartnerBanks(fab, tableType);
	}

	@Override
	public List<Long> getByClusterAndPartnerbank(long partnerbankId) {
		return finTypePartnerBankDAO.getClusterByPartnerbankCode(partnerbankId);
	}

	@Override
	public List<FinTypePartnerBank> getFintypePartnerBankByBranch(List<String> branchCode, Long clusterId) {
		return finTypePartnerBankDAO.getFintypePartnerBankByBranch(branchCode, clusterId);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

}