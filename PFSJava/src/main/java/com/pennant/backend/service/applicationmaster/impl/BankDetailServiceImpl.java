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
 * * FileName : BankDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinCollateralsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BankDetail</b>.<br>
 * 
 */
public class BankDetailServiceImpl extends GenericService<BankDetail> implements BankDetailService {
	private static Logger logger = LogManager.getLogger(BankDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BankDetailDAO bankDetailDAO;
	private BankBranchDAO bankBranchDAO;
	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinanceMainDAO financeMainDAO;
	private FinCollateralsDAO finCollateralsDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private PartnerBankDAO partnerBankDAO;

	public BankDetailServiceImpl() {
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

	public BankDetailDAO getBankDetailDAO() {
		return bankDetailDAO;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	@Override
	public BankDetail getBankDetailsByIfsc(String ifsc) {
		return bankDetailDAO.getBankDetailByIfsc(ifsc);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table BMTBankDetail/BMTBankDetail_Temp
	 * by using BankDetailDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using BankDetailDAO's update method 3) Audit the record in to AuditHeader and AdtBMTBankDetail
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (bankDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (bankDetail.isNewRecord()) {
			bankDetail.setId(getBankDetailDAO().save(bankDetail, tableType));
			auditHeader.getAuditDetail().setModelData(bankDetail);
			auditHeader.setAuditReference(bankDetail.getId());
		} else {
			getBankDetailDAO().update(bankDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTBankDetail by using BankDetailDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTBankDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();
		getBankDetailDAO().delete(bankDetail, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBankDetailById fetch the details by using BankDetailDAO's getBankDetailById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return BankDetail
	 */
	@Override
	public BankDetail getBankDetailById(String id) {
		return getBankDetailDAO().getBankDetailById(id, "_View");
	}

	/**
	 * getApprovedBankDetailById fetch the details by using BankDetailDAO's getBankDetailById method . with parameter id
	 * and type as blank. it fetches the approved records from the BMTBankDetail.
	 * 
	 * @param id (String)
	 * @return BankDetail
	 */

	public BankDetail getApprovedBankDetailById(String id) {
		return getBankDetailDAO().getBankDetailById(id, "_AView");
	}

	/**
	 * fetch Account No Length by bankCode .
	 * 
	 * @param bankCode
	 * @return AccNoLength
	 */
	@Override
	public BankDetail getAccNoLengthByCode(String bankCode) {
		return getBankDetailDAO().getAccNoLengthByCode(bankCode, "_View");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		BankDetail bankDetail = new BankDetail();

		AuditDetail auditDetail = auditHeader.getAuditDetail();

		BeanUtils.copyProperties((BankDetail) auditDetail.getModelData(), bankDetail);

		bankDetailDAO.delete(bankDetail, TableType.TEMP_TAB);

		BankDetail beforeImage = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(bankDetail.getRecordType())) {
			beforeImage = bankDetailDAO.getBankDetailById(bankDetail.getBankCode(), "");
			auditDetail.setBefImage(beforeImage);
		}

		if (bankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBankDetailDAO().delete(bankDetail, TableType.MAIN_TAB);
		} else {
			bankDetail.setRoleCode("");
			bankDetail.setNextRoleCode("");
			bankDetail.setTaskId("");
			bankDetail.setNextTaskId("");
			bankDetail.setWorkflowId(0);

			if (bankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				bankDetail.setRecordType("");
				getBankDetailDAO().save(bankDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				bankDetail.setRecordType("");

				bankDetailDAO.update(bankDetail, TableType.MAIN_TAB);
				if (bankDetail.isUpdateBranches()) {

					List<AuditDetail> updateBranchInstruments = updateBranchInstruments(bankDetail, beforeImage,
							tranType);

					auditHeader.setAuditDetails(updateBranchInstruments);
				}

			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditDetail.setAuditTranType(tranType);
		auditDetail.setModelData(bankDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private Map<InstrumentType, Boolean> getChangedInstuments(BankDetail bd, BankDetail beforeImage) {
		Map<InstrumentType, Boolean> map = new HashMap<>();

		if (bd.isEcs() != beforeImage.isEcs()) {
			map.put(InstrumentType.ECS, bd.isEcs());
		}

		if (bd.isNach() != beforeImage.isNach()) {
			map.put(InstrumentType.NACH, bd.isNach());
		}

		if (bd.isEmandate() != beforeImage.isEmandate()) {
			map.put(InstrumentType.EMANDATE, bd.isEmandate());
		}

		if (bd.isCheque() != beforeImage.isCheque()) {
			map.put(InstrumentType.CHEQUE, bd.isCheque());
		}

		if (bd.isDd() != beforeImage.isDd()) {
			map.put(InstrumentType.DD, bd.isDd());
		}

		if (bd.isDda() != beforeImage.isDda()) {
			map.put(InstrumentType.DDA, bd.isDda());
		}

		if (bd.isCheque() != beforeImage.isCheque()) {
			map.put(InstrumentType.CHEQUE, bd.isCheque());
		}

		return map;
	}

	private List<AuditDetail> updateBranchInstruments(BankDetail bd, BankDetail beforeImage, String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		Map<InstrumentType, Boolean> map = getChangedInstuments(bd, beforeImage);

		if (map.isEmpty()) {
			return auditDetails;
		}

		String emnadeSource = null;

		if (map.containsKey(InstrumentType.EMANDATE)) {
			emnadeSource = bd.getAllowedSources();
		}

		bankBranchDAO.updateInstruments(bd.getBankCode(), map, emnadeSource);

		List<AuditDetail> branchAuditDetails = getBranchAuditDetails(bd.getBankCode(), map, tranType);
		if (!branchAuditDetails.isEmpty()) {
			auditDetails.addAll(branchAuditDetails);
		}

		return auditDetails;
	}

	private List<AuditDetail> getBranchAuditDetails(String bankCode, Map<InstrumentType, Boolean> map,
			String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<BankBranch> list = bankBranchDAO.getBrancesByCode(bankCode);

		Set<InstrumentType> keySet = map.keySet();
		int i = 0;
		for (BankBranch bankBranch : list) {
			BankBranch befImage = new BankBranch();
			BeanUtils.copyProperties(bankBranch, befImage);

			for (InstrumentType instrumentType : keySet) {
				switch (instrumentType) {
				case ECS:
					bankBranch.setEcs(map.get(InstrumentType.ECS));
					break;
				case NACH:
					bankBranch.setNach(map.get(InstrumentType.NACH));
					break;
				case DDA:
					bankBranch.setDda(map.get(instrumentType.DDA));
					break;
				case DD:
					bankBranch.setDd(map.get(instrumentType.DD));
					break;
				case CHEQUE:
					bankBranch.setCheque(map.get(instrumentType.CHEQUE));
					break;
				case EMANDATE:
					bankBranch.setEmandate(map.get(instrumentType.EMANDATE));
					break;
				default:
					break;
				}
			}

			auditDetails.add(new AuditDetail(tranType, ++i, befImage, bankBranch));
		}

		return auditDetails;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBankDetailDAO().delete with parameters bankDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTBankDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBankDetailDAO().delete(bankDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getBankDetailDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getBankDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BankDetail bankDetail = (BankDetail) auditDetail.getModelData();
		String code = bankDetail.getBankCode();

		// Check the unique keys.
		if (bankDetail.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(bankDetail.getRecordType())
				&& bankDetailDAO.isDuplicateKey(code,
						bankDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_BankCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		if (!bankDetail.isActive()) {
			if (partnerBankDAO.getPartnerBankbyBank(code, "") != 0 || finAdvancePaymentsDAO.getBankCode(code, "") != 0
					|| finCollateralsDAO.getFinCollateralsByBank(code, "") != 0
					|| finReceiptDetailDAO.getReceiptHeaderByBank(code, "") != 0
					|| bankBranchDAO.getBankBrachByBank(code, "") != 0
					|| customerBankInfoDAO.getCustomerBankInfoByBank(code, "") != 0
					|| customerExtLiabilityDAO.getCustomerExtLiabilityByBank(code, "") != 0) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_BankCode") + ": " + code;

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41017", parameters, null));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public BankDetail getAccNoLengths(String bankCode) {
		return bankDetailDAO.getAccNoLengths(bankCode);
	}

	@Override
	public String getBankCodeByName(String bankName) {
		return bankDetailDAO.getBankCodeByName(bankName);
	}

	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public CustomerBankInfoDAO getCustomerBankInfoDAO() {
		return customerBankInfoDAO;
	}

	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinCollateralsDAO getFinCollateralsDAO() {
		return finCollateralsDAO;
	}

	public void setFinCollateralsDAO(FinCollateralsDAO finCollateralsDAO) {
		this.finCollateralsDAO = finCollateralsDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public PartnerBankDAO getPartnerBankDAO() {
		return partnerBankDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Override
	public boolean isBankCodeExits(String bankCode, String string, boolean active) {
		return getBankDetailDAO().isBankCodeExits(bankCode, string, active);
	}

}