package com.pennant.backend.service.insurance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.insurance.InsuranceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.service.systemmasters.VASProviderAccDetailService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class InsuranceDetailServiceImpl extends GenericService<InsuranceDetails> implements InsuranceDetailService {
	private static Logger logger = Logger.getLogger(InsuranceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InsuranceDetailDAO insuranceDetailDAO;
	private BankBranchDAO bankBranchDAO;
	private VASRecordingService vASRecordingService;
	private VASProviderAccDetailService vASProviderAccDetailService;
	@Autowired
	private PostingsPreparationUtil postingsPreparationUtil;
	@Autowired
	private AccountingSetDAO accountingSetDAO;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private CollateralSetupDAO collateralSetupDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTInsuranceDetailss/BMTInsuranceDetailss_Temp by using
	 * InsuranceDetailsDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * InsuranceDetailsDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtBMTInsuranceDetailss by using auditHeaderDAO.addAudit(auditHeader)
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

		// Processing Accounting Details
		if (StringUtils.equals(insuranceDetails.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			long linkedTranId = executeAccountingProcess(insuranceDetails);
			insuranceDetails.setLinkedTranId(linkedTranId);
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
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
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
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getInsuranceDetailsDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
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
		return executeAccountingProcess(details, vasRecording);
	}

	
	/**
	 * Execute accounting and postings.
	 */
	@Override
	public long executeAccountingProcess(InsuranceDetails details, VASRecording vASRecording) throws Exception {
		logger.debug(Literal.ENTERING);

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
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		details.getDeclaredFieldValues(aeEvent.getDataMap());
		
		long accountsetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_INSADJ, AccountEventConstants.ACCEVENT_INSADJ);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		
		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	@Override
	public void saveInsurancePayments(InsurancePaymentInstructions paymentInstructions) {
		 getInsuranceDetailDAO().saveInsurancePayments(paymentInstructions, TableType.MAIN_TAB);
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
	public void updateVasStatus(String status, String vasReference) {
		getvASRecordingService().updateVasStatus(status, vasReference);
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

}
