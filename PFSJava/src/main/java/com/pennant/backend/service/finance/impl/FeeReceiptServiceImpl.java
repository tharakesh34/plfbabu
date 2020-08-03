package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class FeeReceiptServiceImpl extends GenericService<FinReceiptHeader> implements FeeReceiptService {
	private static final Logger logger = Logger.getLogger(FeeReceiptServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private AccountingSetDAO accountingSetDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	private PartnerBankDAO partnerBankDAO;
	private PostingsDAO postingsDAO;
	protected transient WorkflowEngine workFlow = null;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceMainDAO financeMainDAO;
	private SecurityUserDAO securityUserDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	public FeeReceiptServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type) {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader receiptHeader = null;
		receiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptID, "_FView");

		// Fetch Receipt Detail List
		if (receiptHeader != null) {
			List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptID,
					"_TView");

			// Fetch Repay Headers List
			List<FinRepayHeader> rpyHeaderList = getFinanceRepaymentsDAO()
					.getFinRepayHeadersByRef(receiptHeader.getReference(), TableType.TEMP_TAB.getSuffix());
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					if (finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()) {
						receiptDetail.getRepayHeaders().add(finRepayHeader);
					}
				}
			}
			receiptHeader.setReceiptDetails(receiptDetailList);

			// Paid Fee Details
			receiptHeader.setPaidFeeList(getPaidFinFeeDetails(receiptHeader.getReference()));
		}

		logger.debug("Leaving");
		return receiptHeader;
	}

	/**
	 * Method for Fetching List of Fee Details for Display purpose
	 */
	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(String finReference) {
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getPaidFinFeeDetails(finReference, "_TView");

		// Finance Fee Schedule Details
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {

				if (finFeeDetail.getTaxHeaderId() != null && finFeeDetail.getTaxHeaderId() > 0) {
					finFeeDetail.setTaxHeader(
							getTaxHeaderDetailsDAO().getTaxHeaderDetailsById(finFeeDetail.getTaxHeaderId(), "_TView"));
					if (finFeeDetail.getTaxHeader() != null) {
						finFeeDetail.getTaxHeader().setTaxDetails(
								taxHeaderDetailsDAO.getTaxDetailById(finFeeDetail.getTaxHeaderId(), "_TView"));
					}
				}
			}
		}

		return finFeeDetails;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinReceiptHeader/FinReceiptHeader_Temp by using FinReceiptHeaderDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using FinReceiptHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		//=======================================
		long receiptID = receiptHeader.getReceiptID();
		if (receiptHeader.isNew()) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);

			// Save Receipt Header
			receiptID = getFinReceiptHeaderDAO().save(receiptHeader, tableType);

		} else {
			getFinReceiptHeaderDAO().update(receiptHeader, tableType);

			// Delete Save Receipt Detail List by Reference
			getFinReceiptDetailDAO().deleteByReceiptID(receiptID, tableType);

			// Delete and Save FinRepayHeader Detail list by Reference
			getFinanceRepaymentsDAO().deleteByRef(receiptHeader.getReference(), tableType);
		}

		Date appDate = SysParamUtil.getAppDate();
		FinServiceInstruction finServInst = new FinServiceInstruction();
		finServInst.setFinReference(receiptHeader.getReference());
		finServInst.setFinEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		finServInst.setAppDate(appDate);
		finServInst.setAmount(receiptHeader.getReceiptAmount());
		finServInst.setSystemDate(DateUtility.getSysDate());
		finServInst.setMaker(auditHeader.getAuditUsrId());
		finServInst.setMakerAppDate(appDate);
		finServInst.setMakerSysDate(DateUtility.getSysDate());
		finServInst.setReference(String.valueOf(receiptHeader.getReceiptID()));
		finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, tableType);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);

				//Save Repay Header details
				getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, tableType);
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader); //TODO audit got issue with invalid column type so we are stopping the audit here

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinReceiptHeaderDAO().delete with parameters finReceiptHeader,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Delete Receipt Header
		getFinanceRepaymentsDAO().deleteByRef(receiptHeader.getReference(), TableType.TEMP_TAB);
		getFinReceiptDetailDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		//finServiceInstructionDAO.deleteFinServInstList(receiptHeader.getReference(), TableType.TEMP_TAB.getSuffix(), String.valueOf(receiptHeader.getReceiptID()));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));
		getAuditHeaderDAO().addAudit(auditHeader); //TODO audit got issue with invalid column type so we are stopping the audit here

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. based on the Record type do
	 * following actions Update record in the main table by using getFinReceiptHeaderDAO().update with parameters
	 * FinReceiptHeader. Audit the record in to AuditHeader and AdtFinReceiptHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Accounting Process Execution
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		aeEvent.setFinReference(receiptHeader.getReference());
		aeEvent.setCustCIF(receiptHeader.getCustCIF());
		aeEvent.setCustID(receiptHeader.getCustID());
		aeEvent.setBranch(receiptHeader.getPostBranch());
		aeEvent.setCcy(receiptHeader.getFinCcy());
		aeEvent.setPostingUserBranch(receiptHeader.getPostBranch());

		//Setting value date from receipt header for backdated receipt 
		//aeEvent.setValueDate(DateUtility.getAppDate());
		aeEvent.setValueDate(receiptHeader.getValueDate());

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(receiptHeader.getCustBaseCcy());
		}

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(SysParamUtil.getAppCurrency());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Fetch Accounting Set ID
		long accountingSetID = 0;
		Map<String, Object> map = null;
		if (RepayConstants.RECEIPTTO_FINANCE.equals(receiptHeader.getRecAgainst())) {
			accountingSetID = AccountingConfigCache.getAccountSetID(receiptHeader.getFinType(),
					AccountEventConstants.ACCEVENT_FEEPAY, FinanceConstants.MODULEID_FINTYPE);
			map = financeMainDAO.getGLSubHeadCodes(receiptHeader.getReference());
		} else {
			accountingSetID = accountingSetDAO.getAccountingSetId(AccountEventConstants.ACCEVENT_FEEPAY,
					AccountEventConstants.ACCEVENT_FEEPAY);
		}
		if (accountingSetID == 0 || accountingSetID == Long.MIN_VALUE) {
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65015", null, null)));
			logger.debug("Leaving");
			return auditHeader;
		}

		FinReceiptDetail finreceiptDetail = receiptHeader.getReceiptDetails().get(0);
		amountCodes.setPartnerBankAc(finreceiptDetail.getPartnerBankAc());
		amountCodes.setPaymentType(finreceiptDetail.getPaymentType());
		amountCodes.setPartnerBankAcType(finreceiptDetail.getPartnerBankAcType());
		amountCodes.setPaidFee(finreceiptDetail.getAmount());
		amountCodes.setFinType(receiptHeader.getFinType());
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (map != null) {
			amountCodes.setBusinessvertical((String) map.get("Businessvertical"));

			BigDecimal alwFlexi = BigDecimal.ZERO;
			if (map.get("AlwFlexi") instanceof Long) {
				long value = (long) map.get("AlwFlexi");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			} else if (map.get("AlwFlexi") instanceof BigDecimal) {
				alwFlexi = (BigDecimal) map.get("AlwFlexi");
				amountCodes.setAlwflexi(alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
			} else if (map.get("AlwFlexi") instanceof Integer) {
				int value = (int) map.get("AlwFlexi");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			}

			amountCodes.setEntitycode((String) map.get("Entitycode"));
			dataMap.put("ae_finbranch", map.get("FinBranch"));
			dataMap.put("emptype", map.get("emptype"));
			dataMap.put("branchcity", map.get("branchcity"));
			dataMap.put("fincollateralreq", map.get("fincollateralreq"));
			dataMap.put("btloan", map.get("btloan"));

		} else {
			amountCodes.setEntitycode(receiptHeader.getEntityCode());
			amountCodes.setFinbranch(receiptHeader.getPostBranch());
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setPostRefId(receiptHeader.getReceiptID());

		aeEvent.getAcSetIDList().add(accountingSetID);

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(receiptHeader.getReference());

		if (gstExecutionMap != null) {
			for (String mapkey : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(mapkey)) {
					aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
				}
			}
		}

		prepareFeeRulesMap(receiptHeader.getPaidFeeList(), aeEvent.getDataMap());
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);

		// Receipt Header Updation
		//=======================================
		tranType = PennantConstants.TRAN_UPD;
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);
		//receiptHeader.setLinkedTranId(aeEvent.getLinkedTranId());
		getFinReceiptHeaderDAO().save(receiptHeader, TableType.MAIN_TAB);

		// Save Receipt Header
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);

			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, TableType.MAIN_TAB);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				rpyHeader.setLinkedTranId(aeEvent.getLinkedTranId());

				//Save Repay Header details
				getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB);
			}
		}

		/*
		 * FinServiceInstruction finServInst=null; List<FinServiceInstruction>
		 * finServInstList=finServiceInstructionDAO.getFinServiceInstructionList(receiptHeader.getReference(),
		 * TableType.TEMP_TAB.getSuffix(), String.valueOf(receiptHeader.getReceiptID())); if (finServInstList!=null &&
		 * finServInstList.size()>0){ finServInst=finServInstList.get(0); } if (finServInst==null){ finServInst=new
		 * FinServiceInstruction(); finServInst.setFinReference(receiptHeader.getReference());
		 * finServInst.setFinEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		 * finServInst.setAmount(receiptHeader.getReceiptAmount()); finServInst.setAppDate(DateUtility.getAppDate());
		 * finServInst.setSystemDate(DateUtility.getSysDate()); finServInst.setMaker(auditHeader.getAuditUsrId());
		 * finServInst.setMakerAppDate(DateUtility.getAppDate()); finServInst.setMakerSysDate(DateUtility.getSysDate());
		 * finServInst.setChecker(auditHeader.getAuditUsrId()); finServInst.setCheckerAppDate(DateUtility.getAppDate());
		 * finServInst.setCheckerSysDate(DateUtility.getSysDate());
		 * finServInst.setLinkedTranId(aeEvent.getLinkedTranId());
		 * finServInst.setReference(String.valueOf(receiptHeader.getReceiptID())); }else{
		 * finServInst.setChecker(auditHeader.getAuditUsrId()); finServInst.setCheckerAppDate(DateUtility.getAppDate());
		 * finServInst.setCheckerSysDate(DateUtility.getSysDate());
		 * finServInst.setLinkedTranId(aeEvent.getLinkedTranId()); }
		 */
		/*
		 * finServiceInstructionDAO.deleteFinServInstList(receiptHeader.getReference(), "_Temp",
		 * String.valueOf(receiptHeader.getReceiptID())); finServiceInstructionDAO.save(finServInst,
		 * TableType.MAIN_TAB.getSuffix());
		 */

		processExcessAmount(receiptHeader);

		// Delete Receipt Header
		getFinanceRepaymentsDAO().deleteByRef(receiptHeader.getReference(), TableType.TEMP_TAB);
		getFinReceiptDetailDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as deleted from TEMP table
		//getAuditHeaderDAO().addAudit(auditHeader); //TODO audit got issue with invalid column type so we are stopping the audit here

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader); //TODO audit got issue with invalid column type so we are stopping the audit here

		logger.debug("Leaving");
		return auditHeader;
	}

	private void processExcessAmount(FinReceiptHeader receiptHeader) {
		List<FinFeeDetail> feeDetails = receiptHeader.getPaidFeeList();

		if (CollectionUtils.isEmpty(feeDetails)) {
			return;
		}

		BigDecimal paidAmt = BigDecimal.ZERO;

		for (FinFeeDetail feeDetail : feeDetails) {
			if (CollectionUtils.isEmpty(feeDetail.getFinFeeReceipts())) {
				continue;
			}

			FinFeeReceipt finFeeReceipts = feeDetail.getFinFeeReceipts().get(0);
			paidAmt = paidAmt.add(finFeeReceipts.getPaidAmount());
		}

		BigDecimal excessAmt = receiptHeader.getReceiptAmount().subtract(paidAmt);
		FinExcessAmount excess = null;

		if (BigDecimal.ZERO.compareTo(excessAmt) >= 0) {
			return;
		}

		String finReference = receiptHeader.getReference();
		String excessAdjustTo = receiptHeader.getExcessAdjustTo();
		excess = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, excessAdjustTo);

		/* Creating Excess */
		if (excess == null) {
			excess = new FinExcessAmount();
			excess.setFinReference(finReference);
			excess.setAmountType(excessAdjustTo);
			excess.setAmount(excessAmt);
			excess.setUtilisedAmt(BigDecimal.ZERO);
			excess.setBalanceAmt(excessAmt);
			excess.setReservedAmt(BigDecimal.ZERO);
			finExcessAmountDAO.saveExcess(excess);
		} else {
			excess.setBalanceAmt(excess.getBalanceAmt().add(excessAmt));
			excess.setAmount(excess.getAmount().add(excessAmt));
			finExcessAmountDAO.updateExcess(excess);
		}

		/* Creating ExcessMoment */
		FinExcessMovement excessMovement = new FinExcessMovement();
		excessMovement.setExcessID(excess.getExcessID());
		excessMovement.setAmount(excessAmt);
		excessMovement.setReceiptID(receiptHeader.getReceiptID());
		excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
		excessMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		excessMovement.setMovementFrom("UPFRONT");
		finExcessAmountDAO.saveExcessMovement(excessMovement);
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinReceiptHeaderDAO().getErrorDetail with Error ID
	 * and language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditDetail.getModelData();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = getFinReceiptHeaderDAO()
				.getReceiptHeaderByID(receiptHeader.getReceiptID(), "");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		String reference = receiptHeader.getReference();
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())) {
			reference = receiptHeader.getCustomerCIF();
		}
		valueParm[0] = reference;
		errParm[0] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[0];
		if (receiptHeader.isNew()) { // for New record or new record into work flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (receiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!receiptHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null
							&& !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Duplicate FEEReceipt reference and purpose
		if (!PennantConstants.RCD_STATUS_RESUBMITTED.equals(receiptHeader.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_REJECTED.equals(receiptHeader.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_CANCELLED.equals(receiptHeader.getRecordStatus())) {
			if (getFeeReceiptExist(receiptHeader.getReference(), receiptHeader.getReceiptPurpose(),
					receiptHeader.getReceiptID())) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65014", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !receiptHeader.isWorkflow()) {
			receiptHeader.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}

	private boolean getFeeReceiptExist(String reference, String receiptPurpose, long receiptId) {
		logger.debug("Entering");

		boolean codeExist = false;

		if (getFinReceiptHeaderDAO().geFeeReceiptCount(reference, receiptPurpose, receiptId) != 0) {
			codeExist = true;
		}

		logger.debug("Leaving");

		return codeExist;
	}

	private void prepareFeeRulesMap(List<FinFeeDetail> finFeeDetailList, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		if (finFeeDetailList != null) {

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if (!finFeeDetail.isRcdVisible()) {
					continue;
				}

				TaxHeader taxHeader = finFeeDetail.getTaxHeader();
				Taxes cgstTax = new Taxes();
				Taxes sgstTax = new Taxes();
				Taxes igstTax = new Taxes();
				Taxes ugstTax = new Taxes();
				Taxes cessTax = new Taxes();
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
								cgstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
								sgstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
								igstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
								ugstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
								cessTax = taxes;
							}
						}
					}
				}
				String feeTypeCode = finFeeDetail.getFeeTypeCode();

				dataMap.put(feeTypeCode + "_C", finFeeDetail.getActualAmount());
				dataMap.put(feeTypeCode + "_P", finFeeDetail.getPaidAmountOriginal());
				dataMap.put(feeTypeCode + "_N", finFeeDetail.getNetAmount());

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					dataMap.put(feeTypeCode + "_W",
							finFeeDetail.getWaivedAmount()
									.subtract(cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
											.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax())
											.add(cessTax.getWaivedTax())));
				} else {
					dataMap.put(feeTypeCode + "_W", finFeeDetail.getWaivedAmount());
				}

				// Calculated Amount
				dataMap.put(feeTypeCode + "_CGST_C", cgstTax.getActualTax());
				dataMap.put(feeTypeCode + "_SGST_C", sgstTax.getActualTax());
				dataMap.put(feeTypeCode + "_IGST_C", igstTax.getActualTax());
				dataMap.put(feeTypeCode + "_UGST_C", ugstTax.getActualTax());
				dataMap.put(feeTypeCode + "_CESS_C", cessTax.getActualTax());

				// Paid Amount
				dataMap.put(feeTypeCode + "_CGST_P", cgstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_SGST_P", sgstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_IGST_P", igstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_UGST_P", ugstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_CESS_P", cessTax.getPaidTax());

				// Net Amount
				dataMap.put(feeTypeCode + "_CGST_N", cgstTax.getNetTax());
				dataMap.put(feeTypeCode + "_SGST_N", sgstTax.getNetTax());
				dataMap.put(feeTypeCode + "_IGST_N", igstTax.getNetTax());
				dataMap.put(feeTypeCode + "_UGST_N", ugstTax.getNetTax());
				dataMap.put(feeTypeCode + "_CESS_N", cessTax.getNetTax());

				// Waiver GST Amounts (GST Waiver Changes)
				dataMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ErrorDetail> processFeePayment(FinServiceInstruction finServInst) throws Exception {
		FinReceiptHeader header = new FinReceiptHeader();
		List<ErrorDetail> errorDetails = new ArrayList<>();

		if (finServInst.getFinReference() == null) {
			header.setReference(null);
		} else {
			header.setReference(finServInst.getFinReference());
		}
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);
		finServInst.setReceiptId(receiptId);

		header.setExtReference(finServInst.getExternalReference());
		header.setModule(finServInst.getModule());
		header.setReceiptDate(finServInst.getValueDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		if (StringUtils.isNotBlank(finServInst.getFinReference())) {
			header.setReference(finServInst.getFinReference());
		}

		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
		header.setExcessAdjustTo(PennantConstants.List_Select);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(finServInst.getAmount());
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(finServInst.getPaymentMode());
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		header.setRemarks(finServInst.getReceiptDetail().getRemarks());
		header.setFinCcy(finServInst.getCurrency());
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		header.setNewRecord(true);
		header.setLastMntBy(userDetails.getUserId());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		if (finServInst.isNonStp()) {
			header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		} else {
			header.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
		}
		header.setUserDetails(userDetails);
		TableType tableType = TableType.MAIN_TAB;
		WorkFlowDetails workFlowDetails = null;
		String roleCode = null;
		String taskid = null;
		String nextTaskId = null;
		long workFlowId = 0;
		if (!finServInst.isNonStp()) {

			workFlowDetails = WorkFlowUtil.getDetailsByType("FEERECEIPT_PROCESS");
			if (workFlowDetails != null) {
				workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
				taskid = workFlow.getUserTaskId(workFlow.firstTaskOwner()); //### 19-07-2018 Ticket ID : 128015
				workFlowId = workFlowDetails.getWorkFlowId();
				roleCode = workFlow.firstTaskOwner();//### 19-07-2018 Ticket ID : 128015

				//nextTaskId = workFlow.getUserTaskId(workFlow.firstTaskOwner());//### 19-07-2018 Ticket ID : 128015
				nextTaskId = workFlow.getNextUserTaskIdsAsString(taskid, header);
				setNextRoleDetails(nextTaskId, workFlow, header);
			}
			header.setTaskId(taskid);
			header.setNextTaskId(nextTaskId);
			header.setRoleCode(roleCode);
			header.setWorkflowId(workFlowId);
			tableType = TableType.TEMP_TAB;
		}
		FinReceiptDetail receiptDetail = new FinReceiptDetail();
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setAmount(header.getReceiptAmount());
		receiptDetail.setPaymentType(header.getReceiptMode());
		receiptDetail.setValueDate(finServInst.getValueDate());
		receiptDetail.setReceiptID(receiptId);
		receiptDetail.setFavourNumber(finServInst.getReceiptDetail().getFavourNumber());
		receiptDetail.setBankCode(finServInst.getReceiptDetail().getBankCode());
		receiptDetail.setFavourName(finServInst.getReceiptDetail().getFavourName());
		receiptDetail.setDepositDate(finServInst.getReceiptDetail().getDepositDate());
		receiptDetail.setDepositNo(finServInst.getReceiptDetail().getDepositNo());
		receiptDetail.setPaymentRef(finServInst.getReceiptDetail().getPaymentRef());
		receiptDetail.setChequeAcNo(finServInst.getReceiptDetail().getChequeAcNo());
		receiptDetail.setFundingAc(finServInst.getReceiptDetail().getFundingAc());
		receiptDetail.setReceivedDate(finServInst.getReceiptDetail().getReceivedDate());
		receiptDetail.setTransactionRef(finServInst.getReceiptDetail().getTransactionRef());

		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(receiptDetail.getFundingAc(), "");
		if (partnerBank != null) {
			receiptDetail.setPartnerBankAc(partnerBank.getAccountNo());
			receiptDetail.setPartnerBankAcType(partnerBank.getAcType());
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "Invalid Funding Account";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return errorDetails;
		}

		FinRepayHeader repayHeader = new FinRepayHeader();
		if (StringUtils.isNotBlank(finServInst.getFinReference())) {
			repayHeader.setFinReference(finServInst.getFinReference());
		} else {
			repayHeader.setFinReference(finServInst.getExternalReference());
		}
		repayHeader.setValueDate(finServInst.getValueDate());
		repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
		repayHeader.setRepayAmount(header.getReceiptAmount());
		receiptDetail.getRepayHeaders().add(repayHeader);
		header.getReceiptDetails().add(receiptDetail);
		// Accounting Process Execution
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		aeEvent.setFinReference(finServInst.getExternalReference());
		aeEvent.setCustCIF(header.getCustCIF());
		aeEvent.setCustID(header.getCustID());
		aeEvent.setBranch(userDetails.getBranchCode());
		aeEvent.setCcy(header.getFinCcy());
		aeEvent.setFinType(finServInst.getFinType() != null ? finServInst.getFinType() : "");
		aeEvent.setPostingUserBranch(header.getCashierBranch());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		long postingId = getPostingsDAO().getPostingId();
		aeEvent.setPostRefId(receiptId);
		aeEvent.setPostingId(postingId);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
		amountCodes.setFinType(finServInst.getFinType() != null ? finServInst.getFinType() : "");

		Map<String, Object> map = financeMainDAO.getGLSubHeadCodes(header.getReference());
		if (map != null && map.size() > 0) {
			amountCodes.setBusinessvertical((String) map.get("Businessvertical"));
			BigDecimal alwFlexi = BigDecimal.ZERO; //(BigDecimal) map.get("AlwFlexi");
			amountCodes.setAlwflexi(alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
			amountCodes.setFinbranch((String) map.get("FinBranch"));
			amountCodes.setEntitycode((String) map.get("Entitycode"));
		}

		Map<String, Object> dataMap = aeEvent.getDataMap();
		if (map != null) {
			dataMap.put("emptype", map.get("emptype"));
			dataMap.put("branchcity", map.get("branchcity"));
			dataMap.put("fincollateralreq", map.get("fincollateralreq"));
			dataMap.put("btloan", (String) map.get("btloan"));
		}
		BigDecimal accountedAMount = prepareFeeRulesMap(amountCodes, dataMap, finServInst.getFinFeeDetails());
		amountCodes.setImdAmount(receiptDetail.getAmount().subtract(accountedAMount));

		amountCodes.setPaidFee(receiptDetail.getAmount());

		// Fetch Accounting Set ID from Fintype ac

		long accountingSetID = getFinTypeAccountingDAO().getAccountSetID(finServInst.getFinType(),
				AccountEventConstants.ACCEVENT_FEEPAY, FinanceConstants.MODULEID_FINTYPE);

		if (accountingSetID == 0 || accountingSetID == Long.MIN_VALUE) {
			errorDetails
					.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65015", null, null)));
			logger.debug("Leaving");
			return errorDetails;
		}
		if (finServInst.isNonStp()) {
			amountCodes.getDeclaredFieldValues(dataMap);
			aeEvent.setDataMap(dataMap);
			aeEvent.getAcSetIDList().add(accountingSetID);
			aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		}

		getFinReceiptHeaderDAO().save(header, tableType);
		if (finServInst.getFinFeeDetails() != null && !finServInst.getFinFeeDetails().isEmpty()) {
			for (FinFeeDetail feeDetail : finServInst.getFinFeeDetails()) {

				// Building fin fee receipts
				FinFeeReceipt feeReceipt = new FinFeeReceipt();
				feeReceipt.setReceiptID(receiptId);
				feeReceipt.setFeeTypeId(feeDetail.getFeeTypeID());
				feeReceipt.setFeeTypeDesc(feeDetail.getFeeTypeDesc());
				feeReceipt.setPaidAmount(feeDetail.getPaidAmount());
				feeReceipt.setFeeTypeCode(feeDetail.getFeeTypeCode());
				feeReceipt.setRecordType(PennantConstants.RCD_ADD);
				feeReceipt.setNewRecord(true);
				feeReceipt.setLastMntBy(userDetails.getUserId());
				feeReceipt.setTaskId(taskid);
				feeReceipt.setNextTaskId(nextTaskId);
				feeReceipt.setRoleCode(roleCode);
				feeReceipt.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				feeReceipt.setWorkflowId(workFlowId);

				feeDetail.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
				feeDetail.setTransactionId(finServInst.getExternalReference());
				feeDetail.setFinReference(finServInst.getFinReference());
				feeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				feeDetail.setLastMntBy(userDetails.getUserId());
				feeDetail.setOriginationFee(true);

				feeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				FinFeeDetail finFeeDetail = getFinFeeDetailDAO().getFeeDetailByExtReference(
						feeDetail.getTransactionId(), feeDetail.getFeeTypeID(), tableType.getSuffix());
				if (finFeeDetail != null) {
					feeDetail.setCalculatedAmount(
							finFeeDetail.getCalculatedAmount().add(feeDetail.getCalculatedAmount()));
					feeDetail.setActualAmount(finFeeDetail.getActualAmount().add(feeDetail.getActualAmount()));
					feeDetail.setRemainingFee(finFeeDetail.getRemainingFee().add(feeDetail.getRemainingFee()));
					feeDetail.setWaivedAmount(finFeeDetail.getWaivedAmount().add(feeDetail.getWaivedAmount()));
					feeDetail.setPaidAmount(finFeeDetail.getPaidAmount().add(feeDetail.getPaidAmount()));
					feeDetail.setFeeID(finFeeDetail.getFeeID());
					feeDetail.setPaidAmountOriginal(
							finFeeDetail.getPaidAmountOriginal().add(feeDetail.getPaidAmountOriginal()));
					feeDetail.setPaidAmountGST(finFeeDetail.getPaidAmountGST().add(feeDetail.getPaidAmountGST()));
					feeDetail.setNetAmount(finFeeDetail.getNetAmount().add(feeDetail.getNetAmount()));
					feeDetail.setNetAmountOriginal(
							finFeeDetail.getNetAmountOriginal().add(feeDetail.getNetAmountOriginal()));
					feeDetail.setNetAmountGST(finFeeDetail.getNetAmountGST().add(feeDetail.getNetAmountGST()));
					feeDetail.setActualAmountOriginal(
							finFeeDetail.getActualAmountOriginal().add(feeDetail.getActualAmountOriginal()));
					feeDetail.setPaidAmountGST(finFeeDetail.getPaidAmountGST().add(feeDetail.getPaidAmountGST()));
					getFinFeeDetailDAO().update(feeDetail, false, tableType.getSuffix());
				} else {
					feeDetail.setFeeID(getFinFeeDetailDAO().save(feeDetail, false, tableType.getSuffix()));
				}
				feeReceipt.setFeeID(feeDetail.getFeeID());
				getFinFeeReceiptDAO().save(feeReceipt, tableType.getSuffix());
			}
		}

		// Save Receipt Header
		for (FinReceiptDetail receiptDetails : header.getReceiptDetails()) {
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);

			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetails, tableType);

			List<FinRepayHeader> rpyHeaderList = receiptDetails.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				rpyHeader.setLinkedTranId(aeEvent.getLinkedTranId());

				//Save Repay Header details
				getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, tableType);
			}
		}

		return errorDetails;

	}

	private void setNextRoleDetails(String nextTaskId, WorkflowEngine workFlow, FinReceiptHeader header) {
		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if (workFlow != null && !StringUtils.isBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");
			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = workFlow.getUserTask(nextTaskId).getActor();
					nextRoleCode += nextRole;

					String baseRole = "";
					baseRole = StringUtils.trimToEmpty(workFlow.getUserTask(nextTasks[i]).getBaseActor());
					baseRoleMap.put(nextRole, baseRole);
				}
			}

			header.setNextRoleCode(nextRoleCode);

			baseRoleMap = null;
		}
	}

	private BigDecimal prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap,
			List<FinFeeDetail> finFeeDetailList) {
		logger.debug("Entering");
		BigDecimal accountedImd = BigDecimal.ZERO;

		if (finFeeDetailList != null) {
			FeeRule feeRule;

			BigDecimal deductFeeDisb = BigDecimal.ZERO;
			BigDecimal addFeeToFinance = BigDecimal.ZERO;
			BigDecimal paidFee = BigDecimal.ZERO;
			BigDecimal feeWaived = BigDecimal.ZERO;

			//VAS
			BigDecimal deductVasDisb = BigDecimal.ZERO;
			BigDecimal addVasToFinance = BigDecimal.ZERO;
			BigDecimal paidVasFee = BigDecimal.ZERO;
			BigDecimal vasFeeWaived = BigDecimal.ZERO;

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeRule = new FeeRule();

				if (finFeeDetail.isAlwPreIncomization()) {

					TaxHeader taxHeader = finFeeDetail.getTaxHeader();
					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();
					if (taxHeader != null) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							for (Taxes taxes : taxDetails) {
								if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
									cgstTax = taxes;
								} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
									sgstTax = taxes;
								} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
									igstTax = taxes;
								} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
									ugstTax = taxes;
								} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
									cessTax = taxes;
								}
							}
						}
					}

					feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
					feeRule.setFeeAmount(finFeeDetail.getActualAmount());
					feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
					feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
					feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
					feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());

					String feeTypeCode = finFeeDetail.getFeeTypeCode();
					dataMap.put(feeTypeCode + "_C", finFeeDetail.getActualAmount());
					if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
						dataMap.put(feeTypeCode + "_W",
								finFeeDetail.getWaivedAmount()
										.subtract(cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
												.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax())
												.add(cessTax.getWaivedTax())));
					} else {
						dataMap.put(feeTypeCode + "_W", finFeeDetail.getWaivedAmount());
					}
					dataMap.put(feeTypeCode + "_P", finFeeDetail.getPaidAmount());
					dataMap.put(feeTypeCode + "_N", finFeeDetail.getNetAmount());

					// Calculated Amount
					dataMap.put(feeTypeCode + "_CGST_C", cgstTax.getActualTax());
					dataMap.put(feeTypeCode + "_SGST_C", sgstTax.getActualTax());
					dataMap.put(feeTypeCode + "_IGST_C", igstTax.getActualTax());
					dataMap.put(feeTypeCode + "_UGST_C", ugstTax.getActualTax());
					dataMap.put(feeTypeCode + "_CESS_C", cessTax.getActualTax());

					// Paid Amount
					dataMap.put(feeTypeCode + "_CGST_P", cgstTax.getPaidTax());
					dataMap.put(feeTypeCode + "_SGST_P", sgstTax.getPaidTax());
					dataMap.put(feeTypeCode + "_IGST_P", igstTax.getPaidTax());
					dataMap.put(feeTypeCode + "_UGST_P", ugstTax.getPaidTax());
					dataMap.put(feeTypeCode + "_CESS_P", cessTax.getPaidTax());

					// Net Amount
					dataMap.put(feeTypeCode + "_CGST_N", cgstTax.getNetTax());
					dataMap.put(feeTypeCode + "_SGST_N", sgstTax.getNetTax());
					dataMap.put(feeTypeCode + "_IGST_N", igstTax.getNetTax());
					dataMap.put(feeTypeCode + "_UGST_N", ugstTax.getNetTax());
					dataMap.put(feeTypeCode + "_CESS_N", cessTax.getNetTax());

					// Waiver GST Amounts (GST Waiver Changes)
					dataMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
					dataMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
					dataMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
					dataMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
					dataMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());

					if (feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
							|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
							|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
						dataMap.put(feeTypeCode + "_SCH", finFeeDetail.getRemainingFeeOriginal());
						// GST
						dataMap.put(feeTypeCode + "_CGST_SCH", cgstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_SGST_SCH", sgstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_IGST_SCH", igstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_UGST_SCH", ugstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_CESS_SCH", cessTax.getRemFeeTax());
					} else {
						dataMap.put(feeTypeCode + "_SCH", 0);
						// GST
						dataMap.put(feeTypeCode + "_CGST_SCH", 0);
						dataMap.put(feeTypeCode + "_SGST_SCH", 0);
						dataMap.put(feeTypeCode + "_IGST_SCH", 0);
						dataMap.put(feeTypeCode + "_UGST_SCH", 0);
						dataMap.put(feeTypeCode + "_CESS_SCH", 0);
					}

					if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
						dataMap.put(feeTypeCode + "_AF", finFeeDetail.getRemainingFeeOriginal());
						// GST
						dataMap.put(feeTypeCode + "_CGST_AF", cgstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_SGST_AF", sgstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_IGST_AF", igstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_UGST_AF", ugstTax.getRemFeeTax());
						dataMap.put(feeTypeCode + "_CESS_AF", cessTax.getRemFeeTax());
					} else {
						dataMap.put(feeTypeCode + "_AF", 0);
						// GST
						dataMap.put(feeTypeCode + "_CGST_AF", 0);
						dataMap.put(feeTypeCode + "_SGST_AF", 0);
						dataMap.put(feeTypeCode + "_IGST_AF", 0);
						dataMap.put(feeTypeCode + "_UGST_AF", 0);
						dataMap.put(feeTypeCode + "_CESS_AF", 0);
					}

					if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
						deductFeeDisb = deductFeeDisb.add(finFeeDetail.getRemainingFee());
						if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
							deductVasDisb = deductVasDisb.add(finFeeDetail.getActualAmount());
						}
					} else if (finFeeDetail.getFeeScheduleMethod()
							.equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
						addFeeToFinance = addFeeToFinance.add(finFeeDetail.getRemainingFee());
						if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
							addVasToFinance = addVasToFinance.add(finFeeDetail.getActualAmount());
						}
					}

					paidFee = paidFee.add(finFeeDetail.getPaidAmount());
					feeWaived = feeWaived.add(finFeeDetail.getWaivedAmount());
					accountedImd = accountedImd
							.add(finFeeDetail.getPaidAmountOriginal().add(cgstTax.getPaidTax().add(sgstTax.getPaidTax())
									.add(igstTax.getPaidTax()).add(ugstTax.getPaidTax()).add(cessTax.getPaidTax())));
					if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
						paidVasFee = paidVasFee.add(finFeeDetail.getPaidAmount());
						vasFeeWaived = vasFeeWaived.add(finFeeDetail.getWaivedAmount());
					}
				}
			}

			amountCodes.setDeductFeeDisb(deductFeeDisb);
			amountCodes.setAddFeeToFinance(addFeeToFinance);
			amountCodes.setFeeWaived(feeWaived);
			amountCodes.setPaidFee(paidFee);

			//VAS
			amountCodes.setDeductVasDisb(deductVasDisb);
			amountCodes.setAddVasToFinance(addVasToFinance);
			amountCodes.setVasFeeWaived(vasFeeWaived);
			amountCodes.setPaidVasFee(paidVasFee);
		}

		logger.debug("Leaving");
		return accountedImd;
	}

	@Override
	public Long getAccountingSetId(String eventCode, String accSetCode) {
		return this.accountingSetDAO.getAccountingSetId(eventCode, accSetCode);
	}

	@Override
	public SecurityUser getSecurityUserById(long userId, String type) {
		return this.securityUserDAO.getSecurityUserById(userId, type);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public PartnerBankDAO getPartnerBankDAO() {
		return partnerBankDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(String finRef) {
		return financeMainDAO.getGLSubHeadCodes(finRef);
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
