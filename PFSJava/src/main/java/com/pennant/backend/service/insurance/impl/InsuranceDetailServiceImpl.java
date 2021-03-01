package com.pennant.backend.service.insurance.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.insurance.InsuranceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.service.systemmasters.VASProviderAccDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class InsuranceDetailServiceImpl extends GenericService<InsuranceDetails> implements InsuranceDetailService {
	private static Logger logger = LogManager.getLogger(InsuranceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InsuranceDetailDAO insuranceDetailDAO;
	private BankBranchDAO bankBranchDAO;
	private VehicleDealerDAO vehicleDealerDAO;
	private VASRecordingService vASRecordingService;
	private VASProviderAccDetailService vASProviderAccDetailService;
	private VehicleDealerService vehicleDealerService;
	private VASConfigurationService vASConfigurationService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private AccountingSetDAO accountingSetDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private VASProviderAccDetailDAO vASProviderAccDetailDAO;
	private VASConfigurationDAO vASConfigurationDAO;
	private VASRecordingDAO vASRecordingDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTInsuranceDetailss/BMTInsuranceDetailss_Temp by using InsuranceDetailsDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using InsuranceDetailsDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTInsuranceDetailss by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		InsuranceDetails insuranceDetails = (InsuranceDetails) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (insuranceDetails.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (insuranceDetails.isNew()) {
			insuranceDetails
					.setId(getInsuranceDetailDAO().saveInsuranceDetails(insuranceDetails, tableType.getSuffix()));
			auditHeader.getAuditDetail().setModelData(insuranceDetails);
			auditHeader.setAuditReference(String.valueOf(insuranceDetails.getId()));
		} else {
			getInsuranceDetailDAO().updateInsuranceDetails(insuranceDetails, tableType.getSuffix());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceDetails(), insuranceDetails.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				insuranceDetails.getBefImage(), insuranceDetails));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		InsuranceDetails insuranceDetails = new InsuranceDetails();
		BeanUtils.copyProperties((InsuranceDetails) auditHeader.getAuditDetail().getModelData(), insuranceDetails);

		getInsuranceDetailDAO().delete(insuranceDetails, TableType.TEMP_TAB.getSuffix());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(insuranceDetails.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(getInsuranceDetailDAO().getInsurenceDetailsById(insuranceDetails.getId(), ""));
		}

		if (insuranceDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getInsuranceDetailDAO().delete(insuranceDetails, TableType.MAIN_TAB.getSuffix());
		} else {
			insuranceDetails.setRoleCode("");
			insuranceDetails.setNextRoleCode("");
			insuranceDetails.setTaskId("");
			insuranceDetails.setNextTaskId("");
			insuranceDetails.setWorkflowId(0);

			// Processing Accounting Details
			if (StringUtils.equals(insuranceDetails.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				long linkedTranId = executeAccountingProcess(insuranceDetails);
				insuranceDetails.setLinkedTranId(linkedTranId);
			}

			if (insuranceDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				insuranceDetails.setRecordType("");
				insuranceDetails.setReconStatus(InsuranceConstants.RECON_STATUS_AUTO);
				getInsuranceDetailDAO().saveInsuranceDetails(insuranceDetails, TableType.MAIN_TAB.getSuffix());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				insuranceDetails.setRecordType("");
				getInsuranceDetailDAO().updateInsuranceDetails(insuranceDetails, TableType.MAIN_TAB.getSuffix());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(insuranceDetails);

		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceDetails(), insuranceDetails.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				insuranceDetails.getBefImage(), insuranceDetails));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		InsuranceDetails insuranceDetails = (InsuranceDetails) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInsuranceDetailDAO().delete(insuranceDetails, TableType.TEMP_TAB.getSuffix());

		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceDetails(), insuranceDetails.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				insuranceDetails.getBefImage(), insuranceDetails));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		InsuranceDetails insuranceDetails = (InsuranceDetails) auditHeader.getAuditDetail().getModelData();
		getInsuranceDetailDAO().delete(insuranceDetails, TableType.MAIN_TAB.getSuffix());

		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceDetails(), insuranceDetails.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				insuranceDetails.getBefImage(), insuranceDetails));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
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
	 * from getInsuranceDetailsDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		InsuranceDetails detail = (InsuranceDetails) auditDetail.getModelData();

		// Check the unique keys.
		if (detail.isNew() && getInsuranceDetailDAO().isDuplicateKey(detail.getId(), detail.getReference(),
				detail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_InsuranceReconciliationList_Reference.value") + ": "
					+ detail.getReference();
			parameters[1] = PennantJavaUtil.getLabel("label_InsuranceReconciliationDialog_LoanRef.value") + ": "
					+ detail.getFinReference();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private long executeAccountingProcess(InsuranceDetails details) throws Exception {
		VASRecording vasRecording = getVASRecordingByRef(details.getReference());
		details.setEntityCode(vasRecording.getEntityCode());
		return executeInsPartnerAccountingProcess(details, vasRecording);
	}

	/**
	 * The premium amount (Vasfee) and the policy amount received during the file upload difference amount Accounting
	 * process
	 */
	@Override
	public long executeInsPartnerAccountingProcess(InsuranceDetails details, VASRecording vASRecording)
			throws Exception {
		logger.debug(Literal.ENTERING);

		// VasconfigurationDetails
		vASRecording.setVasConfiguration(getVASConfigurationByCode(vASRecording.getProductCode()));

		AEEvent aeEvent = new AEEvent();
		aeEvent.setPostingUserBranch(details.getUserDetails().getBranchCode());
		aeEvent.setEntityCode(details.getEntityCode());
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_INSADJ);
		aeEvent.setFinReference(details.getReference());
		aeEvent.setValueDate(DateUtility.getAppDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}
		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
			FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForBatch(vASRecording.getPrimaryLinkRef());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setCustID(financeMain.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())) {
			Customer customer = getCustomerDAO().getCustomerByCIF(vASRecording.getPrimaryLinkRef(), "");
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCcy(customer.getCustBaseCcy());
			aeEvent.setCustID(customer.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())) {
			CollateralSetup collateralSetup = getCollateralSetupDAO()
					.getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(), "");
			Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(), "");
			aeEvent.setCcy(collateralSetup.getCollateralCcy());
			aeEvent.setCustID(collateralSetup.getDepositorId());
			aeEvent.setBranch(customer.getCustDftBranch());
		}

		VehicleDealer vehicleDealer = getVehicleDealerService().getDealerShortCodes(vASRecording.getProductCode());
		amountCodes.setProductCode(vehicleDealer.getProductShortCode());
		amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		details.getDeclaredFieldValues(aeEvent.getDataMap());

		long accountsetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_INSADJ,
				AccountEventConstants.ACCEVENT_INSADJ);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	public void executeVasPaymentsAccountingProcess(InsurancePaymentInstructions instructions) {
		long linkTranId = executeInsPaymentsAccountingProcess(instructions);
		insuranceDetailDAO.updateLinkTranId(instructions.getId(), linkTranId);
	}

	private long executeInsPaymentsAccountingProcess(InsurancePaymentInstructions details) {

		AEEvent aeEvent = new AEEvent();
		if (details.getUserDetails() != null) {
			aeEvent.setPostingUserBranch(details.getUserDetails().getBranchCode());
			aeEvent.setBranch(details.getUserDetails().getBranchCode());
		} else {
			FinanceMain financeMain = financeMainDAO.getDisbursmentFinMainById(details.getFinReference(),
					TableType.VIEW);
			aeEvent.setPostingUserBranch(financeMain.getFinBranch());
			aeEvent.setBranch(financeMain.getFinBranch());// FIXME Branch
		}
		aeEvent.setEntityCode(details.getEntityCode());
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_INSPAY);
		// Setting FinReference instead of provider ID
		aeEvent.setFinReference(details.getVasReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setCcy(details.getPaymentCCy());
		aeEvent.setCcy("INR");
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(details.getFinReference());
		amountCodes.setFinType(financeMain.getFinType());

		VehicleDealer vehicleDealer = getVehicleDealerService().getDealerShortCode(details.getProviderId());
		amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		details.getDeclaredFieldValues(aeEvent.getDataMap());

		long accountsetId = AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
				AccountEventConstants.ACCEVENT_INSPAY, FinanceConstants.MODULEID_FINTYPE);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	private long executePaymentsAccountingProcess(VASRecording vASRecording, InsurancePaymentInstructions details)
			throws InterfaceException {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(vASRecording.getEntityCode());
		aeEvent.setPostingUserBranch(vASRecording.getUserDetails().getBranchCode());
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_INSPAY);
		aeEvent.setFinReference(vASRecording.getVasReference());
		aeEvent.setValueDate(DateUtility.getAppDate());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
			FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(vASRecording.getPrimaryLinkRef());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setCustID(financeMain.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())) {
			Customer customer = getCustomerDAO().getCustomerByCIF(vASRecording.getPrimaryLinkRef(), "");
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCcy(customer.getCustBaseCcy());
			aeEvent.setCustID(customer.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())) {
			CollateralSetup collateralSetup = collateralSetupDAO
					.getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(), "");
			Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(), "");
			aeEvent.setCcy(collateralSetup.getCollateralCcy());
			aeEvent.setCustID(collateralSetup.getDepositorId());
			aeEvent.setBranch(customer.getCustDftBranch());
		}

		if (StringUtils.trimToNull(SysParamUtil.getAppCurrency()) == null) {
			aeEvent.setCcy(SysParamUtil.getAppCurrency());
		}
		// For GL Code
		VehicleDealer vehicleDealer = getVehicleDealerService().getDealerShortCodes(vASRecording.getProductCode());
		amountCodes.setProductCode(vehicleDealer.getProductShortCode());
		amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		details.getDeclaredFieldValues(aeEvent.getDataMap());

		long accountsetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_INSPAY,
				AccountEventConstants.ACCEVENT_INSPAY);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	@Override
	public VASConfiguration getVASConfigurationByCode(String productCode) {
		return getvASConfigurationService().getApprovedVASConfigurationByCode(productCode, false);
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeId(int adviseType, long feeTypeId) {
		return getManualAdviseDAO().getManualAdviseByRefAndFeeId(adviseType, feeTypeId);
	}

	@Override
	public void saveInsurancePayments(InsurancePaymentInstructions details) {
		logger.debug(Literal.ENTERING);

		details.setId(getInsuranceDetailDAO().getSeqNumber());
		if (details.getAdviseRefMap().size() > 0) {
			processMaualAdvisePayments(details);
		}

		//Accounting for Total Amount paid to insurance provider.
		if (details.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
			Cloner cloner = new Cloner();
			InsurancePaymentInstructions payToPatner = cloner.deepClone(details);
			payToPatner.setPayableAmount(BigDecimal.ZERO);
			payToPatner.setReceivableAmount(BigDecimal.ZERO);
			details.setLinkedTranId(executeInsPaymentsAccountingProcess(payToPatner));
		}

		//Accounting for the individual Insurances payment. 
		Cloner cloner = new Cloner();
		InsurancePaymentInstructions newDetails = cloner.deepClone(details);
		if (CollectionUtils.isNotEmpty(newDetails.getVasRecordindList())) {
			for (VASRecording vasRecording : newDetails.getVasRecordindList()) {
				newDetails.setPayableAmount(vasRecording.getPartnerPremiumAmt());
				newDetails.setReceivableAmount(BigDecimal.ZERO);
				newDetails.setPaymentAmount(BigDecimal.ZERO);
				vasRecording.setUserDetails(newDetails.getUserDetails());
				long linkedTranId = executePaymentsAccountingProcess(vasRecording, newDetails);
				updatePaymentLinkedTranId(vasRecording.getVasReference(), linkedTranId);
			}
		}
		getInsuranceDetailDAO().saveInsurancePayments(details, TableType.MAIN_TAB);
		updateVasPaymentId(details.getVasRecordindList(), details.getId());

		logger.debug(Literal.LEAVING);
	}

	private void updatePaymentLinkedTranId(String vasReference, long linkedTranId) {
		getInsuranceDetailDAO().updatePaymentLinkedTranId(vasReference, linkedTranId);
	}

	private void updateVasPaymentId(List<VASRecording> vasRecordings, long paymentInsId) {
		if (CollectionUtils.isNotEmpty(vasRecordings)) {
			for (VASRecording vasRecording : vasRecordings) {
				getvASRecordingService().updateVasPaymentId(vasRecording.getVasReference(), paymentInsId);
			}
		}
	}

	private void processMaualAdvisePayments(InsurancePaymentInstructions instructions) {
		logger.debug(Literal.ENTERING);

		BigDecimal payableAmt = instructions.getPayableAmount();
		BigDecimal receivableAmount = BigDecimal.ZERO;

		LinkedHashMap<Long, String> adviseRefMap = instructions.getAdviseRefMap();
		for (Long feeTypeId : adviseRefMap.keySet()) {
			boolean completed = false;
			List<ManualAdvise> manualAdvises = getManualAdviseDAO()
					.getManualAdviseByRefAndFeeId(FinanceConstants.MANUAL_ADVISE_RECEIVABLE, feeTypeId);

			for (ManualAdvise manualAdvise : manualAdvises) {
				receivableAmount = receivableAmount.add(manualAdvise.getBalanceAmt());

				if (manualAdvise.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
					if (payableAmt.compareTo(receivableAmount) == 0) {
						updateUtilizedAmts(instructions, manualAdvise);
						completed = true;
						break;
					} else if (receivableAmount.compareTo(payableAmt) > 0) {
						BigDecimal reqAmount = receivableAmount.subtract(payableAmt);
						receivableAmount = payableAmt;
						manualAdvise.setBalanceAmt(reqAmount);
						updateUtilizedAmts(instructions, manualAdvise);
						completed = true;
						break;
					} else {
						updateUtilizedAmts(instructions, manualAdvise);
					}
				}
			}
			if (completed) {
				break;
			}
		}
		instructions.setPaymentAmount(payableAmt.subtract(receivableAmount));
		instructions.setReceivableAmount(receivableAmount);

		logger.debug(Literal.LEAVING);
	}

	private void updateUtilizedAmts(InsurancePaymentInstructions instructions, ManualAdvise manualAdvise) {
		// Update PaidAmount
		getManualAdviseDAO().updatePaidAmountOnly(manualAdvise.getAdviseID(), manualAdvise.getBalanceAmt());

		// Payable Advise Movement Creation
		ManualAdviseMovements movement = new ManualAdviseMovements();
		movement.setAdviseID(manualAdvise.getAdviseID());
		movement.setReceiptID(instructions.getId());
		movement.setReceiptSeqID(0);
		movement.setMovementDate(DateUtility.getAppDate());
		movement.setMovementAmount(manualAdvise.getBalanceAmt());
		movement.setPaidAmount(manualAdvise.getBalanceAmt());
		getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());

		//Executing Accounting for partner receivables.
		VASRecording vasRecording = getvASRecordingService().getVASRecordingByReference(manualAdvise.getFinReference());
		if (vasRecording != null) {
			InsurancePaymentInstructions instructionsForRecivable = new InsurancePaymentInstructions();
			instructionsForRecivable.setPayableAmount(BigDecimal.ZERO);
			instructionsForRecivable.setReceivableAmount(manualAdvise.getBalanceAmt());//Receivable amount
			instructionsForRecivable.setPaymentAmount(BigDecimal.ZERO);
			vasRecording.setUserDetails(instructions.getUserDetails());

			executePaymentsAccountingProcess(vasRecording, instructionsForRecivable);
		}
	}

	@Override
	public void doApproveVASInsurance(List<VASRecording> vasRecording, LoggedInUser loginUser) {
		logger.debug(Literal.ENTERING);

		//TODO:GANESH
		if (SysParamUtil.isAllowed(SMTParameterConstants.INSURANCE_INST_ON_DISB)
				&& SysParamUtil.isAllowed(SMTParameterConstants.INSURANCE_INST_ON_INSPAYINST)) {
			for (VASRecording vasDetail : vasRecording) {

				VASConfiguration configuration = vasDetail.getVasConfiguration();

				if (configuration == null) {
					configuration = this.vASConfigurationDAO.getVASConfigurationByCode(vasDetail.getProductCode(),
							"_view");
				}

				VASProviderAccDetail vasProviderAccDetail = vASProviderAccDetailDAO
						.getVASProviderAccDetByPRoviderId(configuration.getManufacturerId(), "");

				if (vasProviderAccDetail != null) {
					InsurancePaymentInstructions payments = new InsurancePaymentInstructions();

					payments.setEntityCode(vasProviderAccDetail.getEntityCode());
					payments.setProviderId(vasProviderAccDetail.getProviderId());
					payments.setPaymentAmount(vasDetail.getFee());
					payments.setPaymentDate(SysParamUtil.getAppDate());
					payments.setPaymentType(vasProviderAccDetail.getPaymentMode());
					payments.setApprovedDate(SysParamUtil.getAppDate());
					payments.setDataEngineStatusId(0);
					payments.setPayableAmount(vasDetail.getFee());
					payments.setPartnerBankId(vasProviderAccDetail.getPartnerBankId());
					payments.setNoOfInsurances(0);
					payments.setNoOfReceivables(0);
					payments.setLinkedTranId(0);
					payments.setReceivableAmount(BigDecimal.ZERO);

					payments.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					payments.setStatus(DisbursementConstants.STATUS_APPROVED);
					payments.setPaymentCCy(SysParamUtil.getAppCurrency());
					payments.setLastMntBy(loginUser.getUserId());
					payments.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					payments.setUserDetails(loginUser);

					long id = this.insuranceDetailDAO.saveInsurancePayments(payments, TableType.MAIN_TAB);
					vASRecordingDAO.updateVasStatus(vasDetail.getVasReference(), id);

				}
			}
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public InsuranceDetails getInsurenceDetailsById(long id) {
		return getInsuranceDetailDAO().getInsurenceDetailsById(id, "_View");
	}

	@Override
	public InsuranceDetails getInsurenceDetailsByRef(String reference, String tableType) {
		return getInsuranceDetailDAO().getInsurenceDetailsByRef(reference, tableType);
	}

	@Override
	public void updateInsuranceDetails(InsuranceDetails insuranceDetail, String tableType) {
		getInsuranceDetailDAO().updateInsuranceDetails(insuranceDetail, tableType);
	}

	@Override
	public void saveInsuranceDetails(InsuranceDetails insuranceDetail, String tableType) {
		getInsuranceDetailDAO().saveInsuranceDetails(insuranceDetail, tableType);
	}

	@Override
	public VASRecording getVASRecordingByRef(String vasReference) {
		return getvASRecordingService().getVASRecordingByReference(vasReference);
	}

	@Override
	public VASRecording getVASRecording(String vasReference, String vasStatus) {
		return getvASRecordingService().getVASRecording(vasReference, vasStatus);
	}

	@Override
	public void updateVasStatus(String status, String vasReference) {
		getvASRecordingService().updateVasStatus(status, vasReference);
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode, String tableType) {
		return getvASProviderAccDetailService().getVASProviderAccDetByPRoviderId(providerId, entityCode, tableType);
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String tableType) {
		return getvASProviderAccDetailService().getVASProviderAccDetByPRoviderId(providerId, tableType);
	}

	@Override
	public VasCustomer getVasCustomerDetails(String finReference, String postingAgainst) {
		return getvASRecordingService().getVasCustomerDetails(finReference, postingAgainst);
	}

	@Override
	public BankBranch getBankBranchById(long bankBranchID, String tableType) {
		return getBankBranchDAO().getBankBranchById(bankBranchID, tableType);
	}

	@Override
	public VehicleDealer getProviderDetails(long dealerId, String tableType) {
		return getVehicleDealerDAO().getVehicleDealerById(dealerId, tableType);
	}

	@Override
	public void updatePaymentStatus(InsurancePaymentInstructions instruction) {
		getInsuranceDetailDAO().updatePaymentStatus(instruction);
	}

	// Getters and setters
	public InsuranceDetailDAO getInsuranceDetailDAO() {
		return insuranceDetailDAO;
	}

	public void setInsuranceDetailDAO(InsuranceDetailDAO insuranceDetailDAO) {
		this.insuranceDetailDAO = insuranceDetailDAO;
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASProviderAccDetailService getvASProviderAccDetailService() {
		return vASProviderAccDetailService;
	}

	public void setvASProviderAccDetailService(VASProviderAccDetailService vASProviderAccDetailService) {
		this.vASProviderAccDetailService = vASProviderAccDetailService;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public VehicleDealerDAO getVehicleDealerDAO() {
		return vehicleDealerDAO;
	}

	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public VASConfigurationService getvASConfigurationService() {
		return vASConfigurationService;
	}

	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}

	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public VASProviderAccDetailDAO getvASProviderAccDetailDAO() {
		return vASProviderAccDetailDAO;
	}

	public void setvASProviderAccDetailDAO(VASProviderAccDetailDAO vASProviderAccDetailDAO) {
		this.vASProviderAccDetailDAO = vASProviderAccDetailDAO;
	}

	public void setvASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

}
