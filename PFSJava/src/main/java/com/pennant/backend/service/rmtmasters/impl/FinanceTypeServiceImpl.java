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
 * * FileName : FinanceTypeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * *
 * Modified Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.rmtmasters.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinTypeReceiptModesDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.ProductAssetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.FinTypeVasDetailValidation;
import com.pennant.backend.service.collateral.impl.IRRFinanceValidation;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.service.rmtmasters.FinTypeExpenseService;
import com.pennant.backend.service.rmtmasters.FinTypeFeesService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;

/**
 * Service implementation for methods that depends on <b>FinanceType</b>.<br>
 */
public class FinanceTypeServiceImpl extends GenericService<FinanceType> implements FinanceTypeService {
	private static final Logger logger = LogManager.getLogger(FinanceTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private ProductAssetDAO productAssetDAO;
	private FinTypeVASProductsDAO finTypeVASProductsDAO;
	private FinTypeReceiptModesDAO finTypeReceiptModesDAO;
	private TransactionEntryDAO transactionEntryDAO;
	// Validation Service Classes
	private FinTypeVasDetailValidation finTypeVasDetailValidation;

	private FinTypeFeesService finTypeFeesService;
	private FinTypeAccountingService finTypeAccountingService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private FinTypeExpenseService finTypeExpenseService;
	private FinanceMainDAO financeMainDAO;
	private IRRFinanceTypeDAO irrFinanceTypeDAO;
	private IRRFinanceValidation irrFinanceValidation;

	@Autowired
	private FinFeeDetailService finFeeDetailService;

	@Autowired
	private FeeTypeService feeTypeService;
	@Autowired
	private RuleDAO ruleDAO;

	public FinanceTypeServiceImpl() {
		super();
	}

	@Override
	public FinanceType getNewFinanceType() {
		logger.debug("Entering");

		FinanceType financeType = new FinanceType();
		financeType.setFinCategory("");
		financeType.setNewRecord(true);

		logger.debug("Leaving");

		return financeType;
	}

	// To get finPurpose details by assestId
	@Override
	public List<ProductAsset> getFinPurposeByAssetId(ArrayList<String> list, String type) {
		return getProductAssetDAO().getFinPurposeByAssetId(list, type);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * RMTFinanceTypes/RMTFinanceTypes_Temp by using FinanceTypeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using FinanceTypeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRMTFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		TableType tableType1 = TableType.MAIN_TAB;
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();

		if (financeType.isWorkflow()) {
			tableType = "_Temp";
			tableType1 = TableType.TEMP_TAB;
		}

		if (financeType.isNewRecord()) {
			financeType.setId(getFinanceTypeDAO().save(financeType, tableType));
			auditHeader.getAuditDetail().setModelData(financeType);
			auditHeader.setAuditReference(financeType.getId());
		} else {
			getFinanceTypeDAO().update(financeType, tableType);
		}

		if (StringUtils.isEmpty(tableType)) {
			FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());
		}

		// Finance Type Fees
		if (financeType.getFinTypeFeesList() != null && financeType.getFinTypeFeesList().size() > 0) {
			List<AuditDetail> feeDetails = financeType.getAuditDetailMap().get("FinTypeFees");
			feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, tableType);
			auditDetails.addAll(feeDetails);
		}

		// Finance Type Accounting
		if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
			List<AuditDetail> accountingDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");
			accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(accountingDetails,
					tableType);
			auditDetails.addAll(accountingDetails);
		}

		// FinVasTypeProduct Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
			details = processingVasProductDetailList(details, tableType);
			auditDetails.addAll(details);
		}

		// FinTypeReceiptModes Details
		if (financeType.getFinTypeReceiptModesList() != null && !financeType.getFinTypeReceiptModesList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeReceiptModes");
			details = processingReceiptModesList(details, tableType);
			auditDetails.addAll(details);
		}

		// IRR Code details
		if (financeType.getIrrFinanceTypeList() != null && financeType.getIrrFinanceTypeList().size() > 0) {
			List<AuditDetail> irrCodeDetails = financeType.getAuditDetailMap().get("IRRCode");
			irrCodeDetails = processingIRRCodeDetailList(irrCodeDetails, financeType.getFinType(), tableType1);
			auditDetails.addAll(irrCodeDetails);
		}

		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && financeType.getFinTypePartnerBankList().size() > 0) {
			List<AuditDetail> partnerBankDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");
			partnerBankDetails = this.finTypePartnerBankService.processDetails(partnerBankDetails, tableType1);
			auditDetails.addAll(partnerBankDetails);
		}

		// FinanceTypeExpense
		if (financeType.getFinTypeExpenseList() != null && financeType.getFinTypeExpenseList().size() > 0) {
			List<AuditDetail> expenseDetails = financeType.getAuditDetailMap().get("FinTypeExpense");
			expenseDetails = this.finTypeExpenseService.processFinTypeExpenseDetails(expenseDetails, tableType);
			auditDetails.addAll(expenseDetails);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTFinanceTypes by using FinanceTypeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtRMTFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		getFinanceTypeDAO().delete(financeType, "");
		FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(financeType, TableType.MAIN_TAB, auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	@Override
	public FinanceType getFinanceTypeById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType ft = getFinanceTypeDAO().getFinanceTypeByID(finType, "_View");

		if (ft == null) {
			logger.debug(Literal.LEAVING);
			return ft;
		}

		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		ft.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(finType, "_View"));
		ft.setFinTypeReceiptModesList(finTypeReceiptModesDAO.getReceiptModesByFinType(finType, "_View"));

		ft.setFinTypeFeesList(finTypeFeesService.getFinTypeFeesById(finType, moduleID));
		ft.setFinTypeAccountingList(finTypeAccountingService.getFinTypeAccountingListByID(finType, moduleID));
		/* ft.setFinTypePartnerBankList(finTypePartnerBankService.getPartnerBanks(finType, TableType.VIEW)); */
		ft.setFinTypeExpenseList(finTypeExpenseService.getFinTypeExpenseById(finType));
		ft.setIrrFinanceTypeList(irrFinanceTypeDAO.getIRRFinanceTypeList(finType, "_View"));

		if (ProductUtil.isOverDraft(ft.getProductCategory())) {
			ft.setFeetype(feeTypeService.getApprovedFeeTypeById(ft.getOverdraftTxnChrgFeeType()));
		}

		logger.debug(Literal.LEAVING);

		return ft;
	}

	@Override
	public List<FinTypeVASProducts> getFinTypeVasProducts(String finType) {
		return getFinTypeVASProductsDAO().getVASProductsByFinType(finType, "_AView");
	}

	@Override
	public FinanceType getApprovedFinanceTypeById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType ft = financeTypeDAO.getFinanceTypeByID(finType, "_AView");

		if (ft == null) {
			logger.debug(Literal.LEAVING);
		}

		int moduleId = FinanceConstants.MODULEID_FINTYPE;
		ft.setFinTypeFeesList(finTypeFeesService.getApprovedFinTypeFeesById(finType, moduleId));
		ft.setFinTypeAccountingList(finTypeAccountingService.getApprovedFinTypeAccountingListByID(finType, moduleId));
		ft.setFinTypePartnerBankList(finTypePartnerBankService.getPartnerBanks(finType, TableType.AVIEW));
		ft.setFinTypeExpenseList(finTypeExpenseService.getApprovedFinTypeExpenseById(finType));

		logger.debug(Literal.LEAVING);

		return ft;
	}

	@Override
	public FinanceType getOrgFinanceTypeById(String finType) {
		FinanceType ft = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
		if (ft.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
			ft.setFinTypePartnerBankList(finTypePartnerBankService.getPartnerBanks(ft.getFinType(), TableType.AVIEW));
		}
		return ft;
	}

	/**
	 * It fetches the approved records from RMTFinanceTypes
	 * 
	 * @param String finType
	 * @return FinanceType
	 */
	@Override
	public FinanceType getApprovedFinanceType(String finType) {
		return FinanceConfigCache.getFinanceType(finType);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceTypeDAO().delete with
	 * parameters financeType,"" b) NEW Add new record in to main table by using getFinanceTypeDAO().save with
	 * parameters financeType,"" c) EDIT Update record in the main table by using getFinanceTypeDAO().update with
	 * parameters financeType,"" 3) Delete the record from the workFlow table by using getFinanceTypeDAO().delete with
	 * parameters financeType,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTFinanceTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtRMTFinanceTypes
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		TableType tranType1 = TableType.MAIN_TAB;

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceType financeType = new FinanceType();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), financeType);

		if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// List
			auditDetails.addAll(deleteChilds(financeType, TableType.MAIN_TAB, tranType));
			getFinanceTypeDAO().delete(financeType, "");
		} else {
			financeType.setRoleCode("");
			financeType.setNextRoleCode("");
			financeType.setTaskId("");
			financeType.setNextTaskId("");
			financeType.setWorkflowId(0);

			if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeType.setRecordType("");
				getFinanceTypeDAO().save(financeType, "");

				// Copy of Finance Workflow & Process Editor Details to
				// Promotion by Finance Type(Product Code)
				if (StringUtils.isNotBlank(financeType.getProduct())) {
					// Finance Workflow Details
					List<FinanceWorkFlow> financeWorkFlowList = getFinanceWorkFlowDAO().getFinanceWorkFlowListById(
							financeType.getProduct(), PennantConstants.WORFLOW_MODULE_FINANCE, "");

					if (financeWorkFlowList != null && !financeWorkFlowList.isEmpty()) {
						for (FinanceWorkFlow financeWorkFlow : financeWorkFlowList) {
							financeWorkFlow.setFinType(financeType.getFinType());
							financeWorkFlow.setModuleName(PennantConstants.WORFLOW_MODULE_PROMOTION);
							financeWorkFlow.setVersion(0);
							financeWorkFlow.setLastMntBy(financeType.getLastMntBy());
							financeWorkFlow.setLastMntOn(financeType.getLastMntOn());
						}
						getFinanceWorkFlowDAO().saveList(financeWorkFlowList, "");

						// Process Editor Details
						List<FinanceReferenceDetail> refList = getFinanceReferenceDetailDAO()
								.getFinanceRefListByFinType(financeType.getProduct(), "");
						for (FinanceReferenceDetail refDetail : refList) {
							refDetail.setFinType(financeType.getFinType());
							refDetail.setFinRefDetailId(Long.MIN_VALUE);
							refDetail.setVersion(0);
							refDetail.setLastMntBy(financeType.getLastMntBy());
							refDetail.setLastMntOn(financeType.getLastMntOn());
							getFinanceReferenceDetailDAO().save(refDetail, "");
						}
					}
				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeType.setRecordType("");
				getFinanceTypeDAO().update(financeType, "");
			}
			FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());

			// FinTypeVasProduct Details
			if (financeType.getFinTypeVASProductsList() != null && financeType.getFinTypeVASProductsList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
				details = processingVasProductDetailList(details, "");
				auditDetails.addAll(details);
			}

			// FinTypeReceiptModes Details
			if (financeType.getFinTypeReceiptModesList() != null
					&& financeType.getFinTypeReceiptModesList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeReceiptModes");
				details = processingReceiptModesList(details, "");
				auditDetails.addAll(details);
			}

			// IRRCode Details
			if (financeType.getIrrFinanceTypeList() != null && financeType.getIrrFinanceTypeList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("IRRCode");
				details = processingIRRCodeDetailList(details, financeType.getFinType(), tranType1);
				auditDetails.addAll(details);
			}

			// Fees
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> feeDetails = financeType.getAuditDetailMap().get("FinTypeFees");

				if (feeDetails != null && !feeDetails.isEmpty()) {
					feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, "");
					auditDetails.addAll(feeDetails);
				}
			}
			// FinTypePartnerBank
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> finTypePartnerBankDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");

				if (finTypePartnerBankDetails != null && !finTypePartnerBankDetails.isEmpty()) {
					finTypePartnerBankDetails = this.finTypePartnerBankService.processDetails(finTypePartnerBankDetails,
							TableType.MAIN_TAB);
					auditDetails.addAll(finTypePartnerBankDetails);
				}
			}
			// Accounting
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> accountingDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");

				if (accountingDetails != null && !accountingDetails.isEmpty()) {
					accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(accountingDetails,
							"");
					auditDetails.addAll(accountingDetails);
				}
			}

			// FinTypeExpense
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> expenseDetails = financeType.getAuditDetailMap().get("FinTypeExpense");

				if (expenseDetails != null && !expenseDetails.isEmpty()) {
					expenseDetails = this.finTypeExpenseService.processFinTypeExpenseDetails(expenseDetails, "");
					auditDetails.addAll(expenseDetails);
				}
			}
		}

		getFinanceTypeDAO().delete(financeType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(deleteChilds(financeType, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceType(), financeType.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeType.getBefImage(), financeType));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeType);

		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private void addAdvTypeFeeDetails(FinanceType financeType, List<FinTypeFees> fees) {
		if (fees == null) {
			fees = new ArrayList<>();
		}

		FinTypeFees finTypeFee = null;
		String orgFinEvent = AccountingEvent.ADDDBSP;
		String serFinEvent = AccountingEvent.ADDDBSN;
		boolean exist = false;
		int orgFeeOrder = 0;
		int servFeeOrder = 0;
		int moduleId = FinanceConstants.MODULEID_FINTYPE;

		Long feeTypeId = null;

		String finType = financeType.getFinType();

		for (FinTypeFees finTypeFees : fees) {
			if (finTypeFees.isOriginationFee()) {
				orgFeeOrder = orgFeeOrder + 1;
			} else {
				servFeeOrder = servFeeOrder + 1;
			}
		}

		// ******************** advance interest
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(AdvanceRuleCode.ADVINT.name());

		AdvanceType advanceType = AdvanceType.getType(financeType.getAdvType());

		if (feeTypeId != null) {
			if (financeType.isGrcAdvIntersetReq() || financeType.isAdvIntersetReq() && advanceType != AdvanceType.AE) {
				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.ADVINT.name(), orgFinEvent, true);
					finTypeFee.setFeeOrder(++orgFeeOrder);
					fees.add(finTypeFee);
				}

				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.ADVINT.name(), serFinEvent, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {
					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);

					if (exist && AdvanceRuleCode.ADVINT.name().equals(fee.getFeeTypeCode())) {
						if (fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

					if (exist && AdvanceRuleCode.ADVINT.name().equals(fee.getFeeTypeCode())) {
						if (!fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
			}
		}

		// ***************************advance EMI
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(AdvanceRuleCode.ADVEMI.name());
		if (feeTypeId != null) {
			if (financeType.isAdvIntersetReq() && advanceType == AdvanceType.AE) {
				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.ADVEMI.name(), orgFinEvent, true);
					finTypeFee.setFeeOrder(++orgFeeOrder);
					fees.add(finTypeFee);
				}

				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.ADVEMI.name(), serFinEvent, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {

					if (fee.getFeeTypeID() != feeTypeId) {
						continue;
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);

					if (exist && AdvanceRuleCode.ADVEMI.name().equals(fee.getFeeTypeCode())) {
						if (fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

					if (exist && AdvanceRuleCode.ADVEMI.name().equals(fee.getFeeTypeCode())) {
						if (!fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
			}
		}

		// ************************** DSF
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(AdvanceRuleCode.DSF.name());
		if (feeTypeId != null) {
			if (financeType.isDsfReq()) {
				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.DSF.name(), orgFinEvent, true);
					finTypeFee.setFeeOrder(++orgFeeOrder);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_RULE);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(true);
					finTypeFee.setMaxWaiverPerc(new BigDecimal(100));
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}

				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.DSF.name(), serFinEvent, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_RULE);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(true);
					finTypeFee.setMaxWaiverPerc(new BigDecimal(100));
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {
					if (!(fee.getFeeTypeID().equals(feeTypeId))) {
						continue;
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);

					if (exist && AdvanceRuleCode.DSF.name().equals(fee.getFeeTypeCode())) {
						if (fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

					if (exist && AdvanceRuleCode.DSF.name().equals(fee.getFeeTypeCode())) {
						if (!fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
			}
		}

		// **************************** Cash Collateral
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(AdvanceRuleCode.CASHCLT.name());
		if (feeTypeId != null) {
			if (financeType.isCashCollateralReq()) {
				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.CASHCLT.name(), orgFinEvent, true);
					finTypeFee.setFeeOrder(++orgFeeOrder);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_RULE);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(true);
					finTypeFee.setMaxWaiverPerc(new BigDecimal(100));
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}

				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, AdvanceRuleCode.CASHCLT.name(), serFinEvent, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_RULE);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(true);
					finTypeFee.setMaxWaiverPerc(new BigDecimal(100));
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {

					if (!(fee.getFeeTypeID().equals(feeTypeId))) {
						continue;
					}
					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);

					if (exist && AdvanceRuleCode.CASHCLT.name().equals(fee.getFeeTypeCode())) {
						if (fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}

					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

					if (exist && AdvanceRuleCode.CASHCLT.name().equals(fee.getFeeTypeCode())) {
						if (!fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
			}
		}

		// ************************** SUBVN
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(subventionFeeCode);

		if (feeTypeId != null) {
			boolean feeTypeOrg = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, true);
			boolean feeTypeNonOrg = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);
			if (financeType.isSubventionReq()) {
				if (!feeTypeOrg) {
					finTypeFee = getFinTypeFee(feeTypeId, subventionFeeCode, orgFinEvent, true);
					finTypeFee.setFeeOrder(++orgFeeOrder);
					finTypeFee.setFeeScheduleMethod(CalculationConstants.FEE_SUBVENTION);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(false);
					finTypeFee.setMaxWaiverPerc(BigDecimal.ZERO);
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}

				if (!feeTypeNonOrg) {
					finTypeFee = getFinTypeFee(feeTypeId, subventionFeeCode, serFinEvent, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					finTypeFee.setFeeScheduleMethod(CalculationConstants.FEE_SUBVENTION);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					finTypeFee.setAlwModifyFee(true);
					finTypeFee.setAlwDeviation(false);
					finTypeFee.setMaxWaiverPerc(BigDecimal.ZERO);
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {
					if (!(fee.getFeeTypeID().equals(feeTypeId))) {
						continue;
					}

					boolean feeTypeCode = subventionFeeCode.equals(fee.getFeeTypeCode());
					boolean orgFee = fee.isOriginationFee() && fee.getModuleId() == moduleId;
					boolean serfee = !fee.isOriginationFee() && fee.getModuleId() == moduleId;

					if ((feeTypeOrg || feeTypeNonOrg) && feeTypeCode && (orgFee || serfee)) {
						fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}
				}
			}
		}

		// ************************** Restructure Fee Code
		String restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);
		feeTypeId = feeTypeService.getFinFeeTypeIdByFeeType(restructFeeCode);

		if (feeTypeId != null && !ProductUtil.isCD(financeType) && !ProductUtil.isOverDraft(financeType)) {
			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);
				if (!exist) {
					finTypeFee = getFinTypeFee(feeTypeId, restructFeeCode, AccountingEvent.RESTRUCTURE, false);
					finTypeFee.setFeeOrder(++servFeeOrder);
					finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					finTypeFee.setAlwModifyFee(false);
					finTypeFee.setAlwDeviation(false);
					finTypeFee.setMaxWaiverPerc(BigDecimal.ZERO);
					finTypeFee.setActive(true);
					finTypeFee.setAlwModifyFeeSchdMthd(false);
					finTypeFee.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_SALE_PRICE);
					finTypeFee.setNextRoleCode("");
					fees.add(finTypeFee);
				}
			} else {
				for (FinTypeFees fee : fees) {
					if (fee.getFeeTypeID() != feeTypeId) {
						continue;
					}
					exist = finFeeDetailService.getFeeTypeId(feeTypeId, finType, moduleId, false);

					if (exist && StringUtils.equals(restructFeeCode, fee.getFeeTypeCode())) {
						if (fee.isOriginationFee() && fee.getModuleId() == moduleId) {
							fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
			}
		}
	}

	private FinTypeFees getFinTypeFee(Long feeTypeId, String feeTypeCode, String finEvent, boolean originationFee) {
		FinTypeFees finTypeFee = new FinTypeFees();
		finTypeFee.setFeeTypeID(feeTypeId);
		finTypeFee.setOriginationFee(originationFee);
		finTypeFee.setFeeTypeCode(feeTypeCode);
		finTypeFee.setFinEvent(finEvent);
		finTypeFee.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_DISBURSE);
		finTypeFee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
		finTypeFee.setAlwModifyFeeSchdMthd(false);
		finTypeFee.setAlwModifyFee(false);
		finTypeFee.setAlwDeviation(false);
		finTypeFee.setMaxWaiverPerc(BigDecimal.ZERO);
		finTypeFee.setModuleId(FinanceConstants.MODULEID_FINTYPE);
		finTypeFee.setRecordType(PennantConstants.RCD_ADD);
		finTypeFee.setNewRecord(true);
		finTypeFee.setActive(true);
		finTypeFee.setAlwModifyFeeSchdMthd(false);
		finTypeFee.setNextRoleCode("");

		return finTypeFee;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceTypeDAO().delete with parameters financeType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceTypeDAO().delete(financeType, "_Temp");
		// List
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(financeType, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = financeType.getUserDetails().getLanguage();

		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
			details = getFinTypeVasDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// IRR Data Validation
		if (financeType.getIrrFinanceTypeList() != null && !financeType.getIrrFinanceTypeList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("IRRCode");
			details = getIRRFinanceValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		FinanceType financeType = (FinanceType) auditDetail.getModelData();
		FinanceType tempFinanceType = null;

		if (financeType.isWorkflow()) {
			tempFinanceType = getFinanceTypeDAO().getFinanceTypeByID(financeType.getId(), "_Temp");
		}

		FinanceType befFinanceType = getFinanceTypeDAO().getFinanceTypeByID(financeType.getId(), "");

		FinanceType oldFinanceType = financeType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = financeType.getFinType();

		errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];

		if (financeType.isNewRecord()) { // for New record or new record into work
			// flow
			if (!financeType.isWorkflow()) {// With out Work flow only new
											// records
				if (befFinanceType != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
																							// is
																							// new
					if (befFinanceType != null || tempFinanceType != null) { // if
																				// records
																				// already
																				// exists
																				// in
																				// the
																				// main
																				// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceType == null || tempFinanceType != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeType.isWorkflow()) { // With out Work flow for update
												// and delete
				if (befFinanceType == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {
					if (oldFinanceType != null
							&& !oldFinanceType.getLastMntOn().equals(befFinanceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {
				if (tempFinanceType == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempFinanceType != null && oldFinanceType != null
						&& !oldFinanceType.getLastMntOn().equals(tempFinanceType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		// To Check Whether the finance type is active or not
		if (!financeType.isFinIsActive()) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "81004", errParm, null));// warning
		}

		/*
		 * if (financeType.isPlanEMIHAlw() && financeType.isStepFinance()) { auditDetail.setErrorDetail(new
		 * ErrorDetails(PennantConstants.KEY_FIELD, "30573", errParm, null)); }
		 */

		/*
		 * if(financeType.isPlanEMIHAlw() && financeType.isFinIsAlwMD()){ auditDetail.setErrorDetail(new
		 * ErrorDetails(PennantConstants.KEY_FIELD, "30574", errParm, null)); }
		 */

		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(financeType.getRecordType())) {
			// FinanceMain Details
			boolean isFinTypeExists = getFinanceMainDAO().isFinTypeExistsInFinanceMain(financeType.getFinType(),
					"_View");

			if (isFinTypeExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeType.isWorkflow()) {
			auditDetail.setBefImage(befFinanceType);
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
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// Map<String, List<AuditDetail>> auditDetailMap = new
		// HashMap<String, List<AuditDetail>>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		// String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeType.isWorkflow()) {
				// auditTranType = PennantConstants.TRAN_WF;
			}
		}

		auditHeader.getAuditDetail().setModelData(financeType);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	// =================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeType.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Fees
		List<FinTypeFees> feedetails = financeType.getFinTypeFeesList();

		if ("doApprove".equals(method)) {
			addAdvTypeFeeDetails(financeType, feedetails);
		}

		if (CollectionUtils.isNotEmpty(feedetails)) {
			for (FinTypeFees finTypeFees : financeType.getFinTypeFeesList()) {
				finTypeFees.setFinType(financeType.getFinType());
				finTypeFees.setWorkflowId(financeType.getWorkflowId());
				finTypeFees.setRecordStatus(financeType.getRecordStatus());
				finTypeFees.setUserDetails(financeType.getUserDetails());
				finTypeFees.setLastMntOn(financeType.getLastMntOn());
				finTypeFees.setRoleCode(financeType.getRoleCode());
				finTypeFees.setNextRoleCode(financeType.getNextRoleCode());
				finTypeFees.setTaskId(financeType.getTaskId());
				finTypeFees.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeFees", this.finTypeFeesService
					.setFinTypeFeesAuditData(financeType.getFinTypeFeesList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeFees"));
		}

		// Accounting
		if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
			for (FinTypeAccounting finTypeAccounting : financeType.getFinTypeAccountingList()) {
				finTypeAccounting.setFinType(financeType.getFinType());
				finTypeAccounting.setWorkflowId(financeType.getWorkflowId());
				finTypeAccounting.setRecordStatus(financeType.getRecordStatus());
				finTypeAccounting.setUserDetails(financeType.getUserDetails());
				finTypeAccounting.setLastMntOn(financeType.getLastMntOn());
				finTypeAccounting.setRoleCode(financeType.getRoleCode());
				finTypeAccounting.setNextRoleCode(financeType.getNextRoleCode());
				finTypeAccounting.setTaskId(financeType.getTaskId());
				finTypeAccounting.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeAccounting", finTypeAccountingService
					.setFinTypeAccountingAuditData(financeType.getFinTypeAccountingList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeAccounting"));
		}

		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && financeType.getFinTypePartnerBankList().size() > 0) {
			for (FinTypePartnerBank finTypePartnerBank : financeType.getFinTypePartnerBankList()) {
				finTypePartnerBank.setFinType(financeType.getFinType());
				finTypePartnerBank.setWorkflowId(financeType.getWorkflowId());
				finTypePartnerBank.setRecordStatus(financeType.getRecordStatus());
				finTypePartnerBank.setUserDetails(financeType.getUserDetails());
				finTypePartnerBank.setLastMntOn(financeType.getLastMntOn());
				finTypePartnerBank.setRoleCode(financeType.getRoleCode());
				finTypePartnerBank.setNextRoleCode(financeType.getNextRoleCode());
				finTypePartnerBank.setTaskId(financeType.getTaskId());
				finTypePartnerBank.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypePartnerBank", finTypePartnerBankService
					.setAuditData(financeType.getFinTypePartnerBankList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypePartnerBank"));
		}

		// Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList() != null && financeType.getFinTypeVASProductsList().size() > 0) {
			auditDetailMap.put("FinTypeVASProducts", setFinTypeVasProcuctAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeVASProducts"));
		}

		// FinTypeReceiptModes Details
		if (financeType.getFinTypeReceiptModesList() != null && financeType.getFinTypeReceiptModesList().size() > 0) {
			auditDetailMap.put("FinTypeReceiptModes",
					setFinTypeReceiptModesAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeReceiptModes"));
		}

		// FinTypeExpense
		if (financeType.getFinTypeExpenseList() != null && financeType.getFinTypeExpenseList().size() > 0) {
			for (FinTypeExpense finTypeExpense : financeType.getFinTypeExpenseList()) {
				finTypeExpense.setFinType(financeType.getFinType());
				finTypeExpense.setWorkflowId(financeType.getWorkflowId());
				finTypeExpense.setRecordStatus(financeType.getRecordStatus());
				finTypeExpense.setUserDetails(financeType.getUserDetails());
				finTypeExpense.setLastMntOn(financeType.getLastMntOn());
				finTypeExpense.setRoleCode(financeType.getRoleCode());
				finTypeExpense.setNextRoleCode(financeType.getNextRoleCode());
				finTypeExpense.setTaskId(financeType.getTaskId());
				finTypeExpense.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeExpense", this.finTypeExpenseService
					.setFinTypeExpenseAuditData(financeType.getFinTypeExpenseList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeExpense"));
		}

		// IRRCode Details
		if (financeType.getIrrFinanceTypeList() != null && financeType.getIrrFinanceTypeList().size() > 0) {
			auditDetailMap.put("IRRCode", setIRRCodeAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("IRRCode"));
		}

		financeType.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeType);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
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

			if (object instanceof FinTypeFees) {
				rcdType = ((FinTypeFees) object).getRecordType();
			} else if (object instanceof FinTypeAccounting) {
				rcdType = ((FinTypeAccounting) object).getRecordType();
			} else if (object instanceof FinTypeExpense) {
				rcdType = ((FinTypeExpense) object).getRecordType();
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

	public List<AuditDetail> deleteChilds(FinanceType financeType, TableType tableType, String auditTranType) {
		logger.debug("Entering");

		String table = tableType.getSuffix();

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeVASProducts(),
					new FinTypeVASProducts().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeVASProductsList().size(); i++) {
				FinTypeVASProducts finTypeVASProducts = financeType.getFinTypeVASProductsList().get(i);
				if (StringUtils.isNotEmpty(finTypeVASProducts.getRecordType()) || StringUtils.isEmpty(table)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeVASProducts.getBefImage(), finTypeVASProducts));
				}
			}
			getFinTypeVASProductsDAO().deleteList(financeType.getFinType(), table);
		}

		// Finance Type Receipt Modes
		if (financeType.getFinTypeReceiptModesList() != null && !financeType.getFinTypeReceiptModesList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeReceiptModes(),
					new FinTypeReceiptModes().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeReceiptModesList().size(); i++) {
				FinTypeReceiptModes finTypeReceiptModes = financeType.getFinTypeReceiptModesList().get(i);
				if (StringUtils.isNotEmpty(finTypeReceiptModes.getRecordType()) || StringUtils.isEmpty(table)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeReceiptModes.getBefImage(), finTypeReceiptModes));
				}
			}
			getFinTypeReceiptModesDAO().deleteList(financeType.getFinType(), table);
		}

		// IRRCode Details
		if (financeType.getIrrFinanceTypeList() != null && !financeType.getIrrFinanceTypeList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new IRRFinanceType(),
					new IRRFinanceType().getExcludeFields());
			for (int i = 0; i < financeType.getIrrFinanceTypeList().size(); i++) {
				IRRFinanceType irrFinanceType = financeType.getIrrFinanceTypeList().get(i);
				if (StringUtils.isNotEmpty(irrFinanceType.getRecordType()) || StringUtils.isEmpty(table)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							irrFinanceType.getBefImage(), irrFinanceType));
				}
			}
			getIrrFinanceTypeDAO().deleteList(financeType.getFinType(), table);
		}

		// Fees
		if (financeType.getFinTypeFeesList() != null && !financeType.getFinTypeFeesList().isEmpty()) {
			auditDetails.addAll(this.finTypeFeesService.delete(financeType.getFinTypeFeesList(), table, auditTranType,
					financeType.getFinType(), FinanceConstants.MODULEID_FINTYPE));
		}
		// Accounting Deatails
		if (financeType.getFinTypeAccountingList() != null && !financeType.getFinTypeAccountingList().isEmpty()) {
			auditDetails.addAll(this.finTypeAccountingService.delete(financeType.getFinTypeAccountingList(), table,
					auditTranType, financeType.getFinType(), FinanceConstants.MODULEID_FINTYPE));
		}
		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && !financeType.getFinTypePartnerBankList().isEmpty()) {
			auditDetails.addAll(this.finTypePartnerBankService.delete(financeType.getFinTypePartnerBankList(),
					tableType, auditTranType, financeType.getFinType()));
		}

		// FinTypeExpense
		if (financeType.getFinTypeExpenseList() != null && !financeType.getFinTypeExpenseList().isEmpty()) {
			auditDetails.addAll(this.finTypeExpenseService.delete(financeType.getFinTypeExpenseList(), table,
					auditTranType, financeType.getFinType()));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		if (financeType.getAuditDetailMap().get("FinTypeFees") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeFees");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeFeesService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypeAccounting") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeAccountingService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypePartnerBank") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypePartnerBankService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		// FinTypeExpense
		if (financeType.getAuditDetailMap().get("FinTypeExpense") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeExpense");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeExpenseService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		logger.debug("Leaving");

		return errorDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Finance Type VAS Details
	 * 
	 * @param auditDetails
	 * @param financeType
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingVasProductDetailList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeVASProducts finTypeVASProducts = (FinTypeVASProducts) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeVASProducts.setRoleCode("");
				finTypeVASProducts.setNextRoleCode("");
				finTypeVASProducts.setTaskId("");
				finTypeVASProducts.setNextTaskId("");
			}

			finTypeVASProducts.setWorkflowId(0);

			if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeVASProducts.isNewRecord()) {
				saveRecord = true;
				if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeVASProducts.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeVASProducts.getRecordType();
				recordStatus = finTypeVASProducts.getRecordStatus();
				finTypeVASProducts.setRecordType("");
				finTypeVASProducts.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getFinTypeVASProductsDAO().save(finTypeVASProducts, type);
			}

			if (updateRecord) {
				getFinTypeVASProductsDAO().update(finTypeVASProducts, type);
			}

			if (deleteRecord) {
				getFinTypeVASProductsDAO().delete(finTypeVASProducts.getFinType(), finTypeVASProducts.getVasProduct(),
						type);
			}

			if (approveRec) {
				finTypeVASProducts.setRecordType(rcdType);
				finTypeVASProducts.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeVASProducts);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Finance Type ReceiptModes Details
	 * 
	 * @param auditDetails
	 * @param financeType
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingReceiptModesList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeReceiptModes finTypeReceiptModes = (FinTypeReceiptModes) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeReceiptModes.setRoleCode("");
				finTypeReceiptModes.setNextRoleCode("");
				finTypeReceiptModes.setTaskId("");
				finTypeReceiptModes.setNextTaskId("");
			}

			finTypeReceiptModes.setWorkflowId(0);

			if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeReceiptModes.isNewRecord()) {
				saveRecord = true;
				if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeReceiptModes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeReceiptModes.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeReceiptModes.getRecordType();
				recordStatus = finTypeReceiptModes.getRecordStatus();
				finTypeReceiptModes.setRecordType("");
				finTypeReceiptModes.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getFinTypeReceiptModesDAO().save(finTypeReceiptModes, type);
			}

			if (updateRecord) {
				getFinTypeReceiptModesDAO().update(finTypeReceiptModes, type);
			}

			if (deleteRecord) {
				getFinTypeReceiptModesDAO().delete(finTypeReceiptModes.getFinType(),
						finTypeReceiptModes.getReceiptMode(), type);
			}

			if (approveRec) {
				finTypeReceiptModes.setRecordType(rcdType);
				finTypeReceiptModes.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeReceiptModes);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Methods for Creating List Finance Type VAS Details of Audit Details with detailed fields
	 * 
	 * @param financeType
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinTypeVasProcuctAuditData(FinanceType financeType, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		String[] fields = PennantJavaUtil.getFieldDetails(finTypeVASProducts, finTypeVASProducts.getExcludeFields());

		for (int i = 0; i < financeType.getFinTypeVASProductsList().size(); i++) {
			FinTypeVASProducts finTypeVASProduct = financeType.getFinTypeVASProductsList().get(i);

			if (StringUtils.isEmpty(finTypeVASProduct.getRecordType())) {
				continue;
			}

			finTypeVASProduct.setFinType(financeType.getFinType());
			finTypeVASProduct.setWorkflowId(financeType.getWorkflowId());

			boolean isRcdType = false;

			if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeType.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypeVASProduct.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finTypeVASProduct.setRecordStatus(financeType.getRecordStatus());
			finTypeVASProduct.setUserDetails(financeType.getUserDetails());
			finTypeVASProduct.setLastMntOn(financeType.getLastMntOn());
			finTypeVASProduct.setLastMntBy(financeType.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTypeVASProduct.getBefImage(), finTypeVASProduct));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Methods for Creating List Finance Type ReceiptModes Details of Audit Details with detailed fields
	 * 
	 * @param financeType
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinTypeReceiptModesAuditData(FinanceType financeType, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinTypeReceiptModes finTypeReceiptModes = new FinTypeReceiptModes();
		String[] fields = PennantJavaUtil.getFieldDetails(finTypeReceiptModes, finTypeReceiptModes.getExcludeFields());

		for (int i = 0; i < financeType.getFinTypeReceiptModesList().size(); i++) {
			FinTypeReceiptModes finTypeReceiptMode = financeType.getFinTypeReceiptModesList().get(i);

			if (StringUtils.isEmpty(finTypeReceiptMode.getRecordType())) {
				continue;
			}

			finTypeReceiptMode.setFinType(financeType.getFinType());
			finTypeReceiptMode.setWorkflowId(financeType.getWorkflowId());

			boolean isRcdType = false;

			if (finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeReceiptMode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeReceiptMode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeType.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeReceiptMode.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypeReceiptMode.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeReceiptMode.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finTypeReceiptMode.setRecordStatus(financeType.getRecordStatus());
			finTypeReceiptMode.setUserDetails(financeType.getUserDetails());
			finTypeReceiptMode.setLastMntOn(financeType.getLastMntOn());
			finTypeReceiptMode.setLastMntBy(financeType.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTypeReceiptMode.getBefImage(), finTypeReceiptMode));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Method to get FinanceType based on finance type
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByFinType(String finType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeByFinType(finType);
	}

	@Override
	public String getAllowedCollateralTypes(String finType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getAllowedCollateralTypes(finType);
	}

	/**
	 * Fetch total number of records from FinanceTypes
	 * 
	 * @param finType
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeCountById(finType);
	}

	/**
	 * Fetch the FinanceTypes Based on the Product Code
	 * 
	 * @param productCode
	 */
	@Override
	public List<FinanceType> getFinanceTypeByProduct(String productCode) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeByProduct(productCode);
	}

	/**
	 * Fetch total number of records from FinanceTypes(Promotion)
	 * 
	 * @param finType
	 */
	@Override
	public int getPromotionTypeCountById(String finType) {
		logger.debug("Entering");

		int promotionCount = 0;
		promotionCount = getFinanceTypeDAO().getPromotionTypeCountById(finType);

		logger.debug("Leaving");

		return promotionCount;
	}

	/**
	 * Fetch record count of Promotions by using product code
	 * 
	 * @param productCode
	 */
	@Override
	public int getProductCountById(String productCode) {
		logger.debug("Entering");

		int productCount = 0;
		productCount = getFinanceTypeDAO().getProductCountById(productCode);

		logger.debug("Leaving");

		return productCount;
	}

	/**
	 * Validate finance type fees against fees configured in accounting
	 * 
	 * @param productCode
	 */
	@Override
	public List<String> fetchFeeCodeList(Long accountSetId) {
		logger.debug("Entering");

		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.add(accountSetId);
		List<String> feeCodeList = getTransactionEntryDAO().getFeeCodeList(accSetIdList);

		logger.debug("Leaving");

		return feeCodeList;
	}

	/**
	 * 
	 */
	@Override
	public String getFinanceTypeDesc(String productCode) {
		logger.debug("Entering");

		String finTypeDesc = "";
		finTypeDesc = getFinanceTypeDAO().getFinanceTypeDesc(productCode);

		logger.debug("Leaving");

		return finTypeDesc;
	}

	@Override
	public boolean getFinTypeExist(String finType, String type) {
		logger.debug("Entering");

		boolean finTypeExist = false;

		if (getFinanceTypeDAO().getFinTypeCount(finType, type) != 0) {
			finTypeExist = true;
		}

		logger.debug("Leaving");

		return finTypeExist;
	}

	/**
	 * FinTypeVasDetail Validation
	 * 
	 * @return
	 */
	public FinTypeVasDetailValidation getFinTypeVasDetailValidation() {
		if (finTypeVasDetailValidation == null) {
			this.finTypeVasDetailValidation = new FinTypeVasDetailValidation(finTypeVASProductsDAO);
		}
		return this.finTypeVasDetailValidation;
	}

	/**
	 * Methods for Creating List IRRCode Details of Audit Details with detailed fields
	 * 
	 * @param financeType
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setIRRCodeAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		IRRFinanceType irrFinanceType = new IRRFinanceType();
		String[] fields = PennantJavaUtil.getFieldDetails(irrFinanceType, irrFinanceType.getExcludeFields());

		for (int i = 0; i < financeType.getIrrFinanceTypeList().size(); i++) {
			IRRFinanceType aIRRFinanceType = financeType.getIrrFinanceTypeList().get(i);

			if (StringUtils.isEmpty(aIRRFinanceType.getRecordType())) {
				continue;
			}

			aIRRFinanceType.setFinType(financeType.getFinType());
			aIRRFinanceType.setWorkflowId(financeType.getWorkflowId());

			boolean isRcdType = false;

			if (aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				aIRRFinanceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				aIRRFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeType.isWorkflow()) {
					isRcdType = true;
				}
			} else if (aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				aIRRFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				aIRRFinanceType.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| aIRRFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			aIRRFinanceType.setRecordStatus(financeType.getRecordStatus());
			aIRRFinanceType.setUserDetails(financeType.getUserDetails());
			aIRRFinanceType.setLastMntOn(financeType.getLastMntOn());
			aIRRFinanceType.setLastMntBy(financeType.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], aIRRFinanceType.getBefImage(),
					aIRRFinanceType));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for IRR CODE Details
	 * 
	 * @param auditDetails
	 * @param financeType
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingIRRCodeDetailList(List<AuditDetail> auditDetails, String financeType,
			TableType type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			IRRFinanceType irrFinanceType = (IRRFinanceType) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				irrFinanceType.setRoleCode("");
				irrFinanceType.setNextRoleCode("");
				irrFinanceType.setTaskId("");
				irrFinanceType.setNextTaskId("");
			}

			irrFinanceType.setWorkflowId(0);
			irrFinanceType.setFinType(financeType);

			if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (irrFinanceType.isNewRecord()) {
				saveRecord = true;
				if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (irrFinanceType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (irrFinanceType.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = irrFinanceType.getRecordType();
				recordStatus = irrFinanceType.getRecordStatus();
				irrFinanceType.setRecordType("");
				irrFinanceType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getIrrFinanceTypeDAO().save(irrFinanceType, type);
			}

			if (updateRecord) {
				getIrrFinanceTypeDAO().update(irrFinanceType, type);
			}

			if (deleteRecord) {
				getIrrFinanceTypeDAO().delete(irrFinanceType, type);
			}

			if (approveRec) {
				irrFinanceType.setRecordType(rcdType);
				irrFinanceType.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(irrFinanceType);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * IRRCode Validation
	 * 
	 * @return
	 */
	public IRRFinanceValidation getIRRFinanceValidation() {
		if (irrFinanceValidation == null) {
			this.irrFinanceValidation = new IRRFinanceValidation(irrFinanceTypeDAO);
		}
		return this.irrFinanceValidation;
	}

	@Override
	public FinanceType getFinanceType(String finType) {
		return getFinanceTypeDAO().getFinanceType(finType);
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

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public ProductAssetDAO getProductAssetDAO() {
		return productAssetDAO;
	}

	public void setProductAssetDAO(ProductAssetDAO productAssetDAO) {
		this.productAssetDAO = productAssetDAO;
	}

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public FinTypeFeesService getFinTypeFeesService() {
		return finTypeFeesService;
	}

	public void setFinTypeFeesService(FinTypeFeesService finTypeFeesService) {
		this.finTypeFeesService = finTypeFeesService;
	}

	public FinTypeAccountingService getFinTypeAccountingService() {
		return finTypeAccountingService;
	}

	public void setFinTypeAccountingService(FinTypeAccountingService finTypeAccountingService) {
		this.finTypeAccountingService = finTypeAccountingService;
	}

	public FinTypePartnerBankService getFinTypePartnerBankService() {
		return finTypePartnerBankService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public FinTypeExpenseService getFinTypeExpenseService() {
		return finTypeExpenseService;
	}

	public void setFinTypeExpenseService(FinTypeExpenseService finTypeExpenseService) {
		this.finTypeExpenseService = finTypeExpenseService;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public IRRFinanceTypeDAO getIrrFinanceTypeDAO() {
		return irrFinanceTypeDAO;
	}

	public void setIrrFinanceTypeDAO(IRRFinanceTypeDAO irrFinanceTypeDAO) {
		this.irrFinanceTypeDAO = irrFinanceTypeDAO;
	}

	@Override
	public FinanceType getFinLtvCheckByFinType(String finType) {
		return financeTypeDAO.getFinLtvCheckByFinType(finType);
	}

	@Override
	public String getAllowedRepayMethods(String finType) {
		return getFinanceTypeDAO().getAllowedRepayMethods(finType, " ");

	}

	public FinTypeReceiptModesDAO getFinTypeReceiptModesDAO() {
		return finTypeReceiptModesDAO;
	}

	@Override
	public List<ValueLabel> provisionRules() {
		return ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_PROVSN, RuleConstants.MODULE_PROVSN, "").stream()
				.map(rule -> new ValueLabel(String.valueOf(rule.getId()), rule.getRuleCode()))
				.collect(Collectors.toList());
	}

	public void setFinTypeReceiptModesDAO(FinTypeReceiptModesDAO finTypeReceiptModesDAO) {
		this.finTypeReceiptModesDAO = finTypeReceiptModesDAO;
	}

}
