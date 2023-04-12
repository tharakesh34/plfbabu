package com.pennant.webui.finance.financemain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectFinanceCancellationDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -8233696024677792766L;
	private static final Logger logger = LogManager.getLogger(SelectFinanceCancellationDialogCtrl.class);

	protected Window window_SelectFinanceCancellationDialog;
	protected ExtendedCombobox finReference;
	protected Textbox custCIF;

	protected Button btnProceed;
	protected Label customerNameLabel;

	private String moduleDefiner;
	private String workflowCode;
	private String eventCode;
	private String menuItemRightName;
	private List<String> roleList = new ArrayList<String>();
	private transient WorkFlowDetails workFlowDetails = null;

	private transient CustomerDataService customerDataService;
	private transient CustomerDetailsService customerDetailsService;
	private FinanceSelectCtrl financeSelectCtrl;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FinanceCancellationService financeCancellationService;
	private transient FinanceDetailService financeDetailService;
	private transient FinReceiptHeaderDAO finReceiptHeaderDAO;
	private transient PaymentHeaderService paymentHeaderService;
	private transient SettlementService settlementService;
	private transient CrossLoanKnockOffService crossLoanKnockOffService;
	private transient FinServiceInstrutionDAO finServiceInstructionDAO;

	private FinanceMain finMain;

	/**
	 * default constructor.<br>
	 */
	public SelectFinanceCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectFinanceCancellationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_SelectFinanceCancellationDialog);

		try {

			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("workflowCode")) {
				this.workflowCode = (String) arguments.get("workflowCode");
			}

			if (arguments.containsKey("eventCode")) {
				this.eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				this.menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("role")) {
				this.roleList = (ArrayList<String>) arguments.get("role");
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				this.financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");
			}

			doSetFieldProperties();
			showSelectFinanceCancellationDialog();

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the SelectPaymentHeaderDialog window modal.
	 */
	private void showSelectFinanceCancellationDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			this.window_SelectFinanceCancellationDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) nCustomer;

		setCustomerData(customer);

		logger.debug(Literal.LEAVING);
	}

	private void setCustomerData(Customer customer) {

		addFilter(customer);
		if (customer == null || customer.getCustID() == 0) {
			this.custCIF.setValue("");
			return;
		}

		this.custCIF.setValue(customer.getCustCIF());
		this.customerNameLabel.setValue(customer.getCustShrtName());

	}

	public void onChange$custCIF(Event event) {
		Customer customer = fetchCustomerDataByCIF(this.custCIF.getValue());
		setCustomerData(customer);
	}

	private void doSetFieldProperties() {

		this.finReference.setButtonDisabled(false);
		this.finReference.setTextBoxWidth(155);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMaintenance");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		Filter filters[] = new Filter[3];
		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
		Date backValueDate = DateUtil.addDays(SysParamUtil.getAppDate(), backValueDays);
		String backValDate = DateUtil.formatToFullDate(backValueDate);

		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		filters[2] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);
		this.finReference.setFilters(filters);

	}

	public void onClick$btnSearchCustCIF(Event event) {
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
	}

	public CustomerDetails fetchCustomerData(String custCIF) {
		CustomerDetails customerDetails = null;
		try {
			Customer customer = customerDataService.getCheckCustomerByCIF(custCIF);

			if (customer != null) {
				customerDetails = customerDataService.getCustomerDetailsbyID(customer.getId(), true, "_AView");
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return customerDetails;
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);

		validateFinReference(event, false);

		Clients.clearWrongValue(this.finReference);
		Object dataObject = this.finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			finMain = (FinanceMain) dataObject;
			if (finMain != null) {
				this.finReference.setValue(finMain.getFinReference());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {

		logger.debug(Literal.ENTERING + event.toString());

		if (!doFieldValidation()) {
			logger.debug(Literal.LEAVING + event.toString());
			return;
		}

		// Validate Workflow Details
		setWorkflowDetails(finMain.getFinType(), StringUtils.isNotEmpty(finMain.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		long finID = ComponentUtil.getFinID(this.finReference);

		String userRole = finMain.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		// Getting FinanceDetail Data
		final FinanceDetail fd = financeCancellationService.getFinanceDetailById(finID, "_View", userRole,
				moduleDefiner);
		fd.setModuleDefiner(moduleDefiner);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (fm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		fd.getFinScheduleData().setFinanceMain(fm);

		// Role Code State Checking
		String nextroleCode = fm.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			logger.debug(Literal.LEAVING + event.toString());
			return;
		}

		String maintainSts = "";
		if (fm != null) {
			maintainSts = StringUtils.trimToEmpty(fm.getRcdMaintainSts());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
			return;
		}

		// Schedule Date verification, As Installment date crossed or not
		List<FinanceScheduleDetail> schdList = schdData.getFinanceScheduleDetails();
		FinanceScheduleDetail bpiSchedule = null;

		Date appDate = SysParamUtil.getAppDate();

		if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
			for (int i = 1; i < schdList.size(); i++) {
				FinanceScheduleDetail curSchd = schdList.get(i);
				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					bpiSchedule = curSchd;
					continue;
				}

				if (curSchd.getSchDate().compareTo(appDate) <= 0 && curSchd.isRepayOnSchDate()) {
					ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60407", null, null),
							getUserWorkspace().getUserLanguage());
					MessageUtil.showError(errorDetails.getError());

					logger.debug("Leaving");
					return;
				}
			}
		}

		// Check Repayments on Finance when it is not in Maintenance
		if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
			if (StringUtils.isEmpty(maintainSts)) {
				List<FinanceRepayments> listFinanceRepayments = new ArrayList<FinanceRepayments>();
				listFinanceRepayments = financeDetailService.getFinRepayList(finID);
				if (listFinanceRepayments != null && listFinanceRepayments.size() > 0) {
					boolean onlyBPIPayment = true;
					for (FinanceRepayments financeRepayments : listFinanceRepayments) {
						// check for the BPI payment
						if (bpiSchedule != null) {
							if (financeRepayments.getFinSchdDate().compareTo(bpiSchedule.getSchDate()) != 0) {
								onlyBPIPayment = false;
							}
						} else {
							onlyBPIPayment = false;
						}
					}
					if (!onlyBPIPayment) {
						MessageUtil.showError("Repayments done on this Finance. Cannot Proceed Further");
						return;
					}
				}
			}
		}

		// if receipts are in progress then Loan Cancellation is not allowed
		List<FinReceiptHeader> rch = finReceiptHeaderDAO
				.getReceiptHeadersByRef(schdData.getFinanceMain().getFinReference(), "_Temp");
		if (!CollectionUtils.isEmpty(rch)) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_RepaymentsInProgress.label"));
			return;
		}

		// If the disbursements are REALIZED or PAID, we are not allow to cancel the loan until unless those
		// disbursements are REVERSED.
		List<FinAdvancePayments> advancePayments = financeCancellationService.getFinAdvancePaymentsByFinRef(finID);
		if (ImplementationConstants.DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL) {

			if (CollectionUtils.isNotEmpty(advancePayments)) {
				for (FinAdvancePayments payments : advancePayments) {
					if (!(DisbursementConstants.STATUS_REVERSED.equals(payments.getStatus())
							|| DisbursementConstants.STATUS_REJECTED.equals(payments.getStatus())
							|| DisbursementConstants.STATUS_APPROVED.equals(payments.getStatus())
							|| DisbursementConstants.STATUS_CANCEL.equals(payments.getStatus()))) {
						MessageUtil.showError(Labels.getLabel("label_Finance_Cancel_Disbursement_Status_Reversed"));
						return;
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(advancePayments)) {
			for (FinAdvancePayments payments : advancePayments) {

			}
		}

		// Validation for Refunded
		boolean isRefundProvided = paymentHeaderService.isRefundProvided(finID);
		if (isRefundProvided) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_Refund.label"));
			return;
		}

		// Validation For Reschedule
		List<FinServiceInstruction> finSerInst = finServiceInstructionDAO.getFinServiceInstructions(finID, "",
				FinServiceEvent.RESCHD);
		if (!CollectionUtils.isEmpty(finSerInst)) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_ReSchedule.label"));
			return;
		}

		// Validation for matured
		if (DateUtil.compare(appDate, schdData.getFinanceMain().getMaturityDate()) > 0) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_Matured.label"));
			return;
		}

		// validation for Restructured
		finSerInst = finServiceInstructionDAO.getFinServiceInstructions(finID, "", FinServiceEvent.RESTRUCTURE);
		if (!CollectionUtils.isEmpty(finSerInst)) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_Restructure.label"));
			return;
		}

		// Validation For Written Off
		if (schdData.getFinanceMain().isWriteoffLoan()) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_WriteOff.label"));
			return;
		}

		boolean isSettlementInitiated = settlementService.isSettlementInitiated(schdData.getFinanceMain().getFinID(),
				"_Temp");

		if (isSettlementInitiated) {
			MessageUtil.showError(Labels.getLabel("label_FinCancel_Settlement.label"));
			return;
		}

		List<CrossLoanTransfer> crossLoanTransfer = crossLoanKnockOffService.getCrossLoanTransferByFinId(finID,
				"_view");

		if (!CollectionUtils.isEmpty(crossLoanTransfer)) {
			MessageUtil.showError(Labels.getLabel("label_CancelLoan_CrossLoan.label"));
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeSelectCtrl", financeSelectCtrl);
		map.put("financeDetail", fd);
		map.put("moduleDefiner", moduleDefiner);
		map.put("workflowCode", workflowCode);
		map.put("eventCode", eventCode);
		map.put("menuItemRightName", menuItemRightName);
		map.put("role", roleList);
		try {
			this.window_SelectFinanceCancellationDialog.onClose();
			financeSelectCtrl.showCancellationDetailView(fd);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		this.window_SelectFinanceCancellationDialog.onClose();
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {
		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(workflowCode)) {
			String workflowTye = financeWorkFlowService.getFinanceWorkFlowType(finType, workflowCode,
					isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(workflowTye);
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void addFilter(Customer customer) {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue("");
		this.finReference.setObject("");
		this.custCIF.setValue("");
		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
		Date backValueDate = DateUtil.addDays(SysParamUtil.getAppDate(), backValueDays);
		String backValDate = DateUtil.formatToFullDate(backValueDate);

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		filters[2] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);
		this.finReference.setFilters(filters);

		if (customer != null && customer.getCustID() != 0) {
			this.custCIF.setValue(customer.getCustCIF());

			filters = new Filter[4];
			filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
			filters[2] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
			filters[3] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);

			this.finReference.setFilters(filters);
		}

		logger.debug(Literal.LEAVING);
	}

	public Customer fetchCustomerDataByCIF(String custCIF) {
		Customer customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		String cif = StringUtils.trimToEmpty(custCIF);

		if (this.custCIF.getValue().trim().isEmpty()) {
			customerNameLabel.setValue("");

			return null;
		}

		customer = this.customerDetailsService.getCustomer(cif);

		if (customer != null) {
			customerNameLabel.setValue(customer.getCustShrtName());
		} else {
			customerNameLabel.setValue("");
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
		}

		return customer;
	}

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectFinanceCancellation_Finreference.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	private void validateFinReference(Event event, boolean b) {
		logger.debug(Literal.ENTERING + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();

		Clients.clearWrongValue(finReference);

		Object dataObject = this.finReference.getObject();

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		this.finReference.setFilters(filters);

		if (this.custCIF.getValue() != null && !this.custCIF.getValue().isEmpty()) {
			long custID = customerDetailsService.getCustIDByCIF(this.custCIF.getValue());

			filters = new Filter[3];
			filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
			filters[2] = new Filter("CustId", custID, Filter.OP_EQUAL);
			filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
			this.finReference.setFilters(filters);
		}

		if (dataObject == null || dataObject instanceof String) {
			this.finReference.setValue("");
			this.finReference.setDescription("");
		} else {
			FinanceMain fm = (FinanceMain) dataObject;
			if (fm != null) {
				this.finReference.setValue(fm.getFinReference());
				this.finReference.setDescription(fm.getFinType());
				this.custCIF.setValue(String.valueOf(fm.getCustCIF()));

				Customer cust = this.customerDetailsService.getCustomer(fm.getCustCIF());
				if (cust.getCustCoreBank() != null) {
					this.customerNameLabel.setValue(cust.getCustShrtName());
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		this.finReference.setErrorMessage("");
	}

	private void doRemoveValidation() {
		this.finReference.setConstraint("");
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public PaymentHeaderService getPaymentHeaderService() {
		return paymentHeaderService;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public SettlementService getSettlementService() {
		return settlementService;
	}

	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

	public CrossLoanKnockOffService getCrossLoanKnockOffService() {
		return crossLoanKnockOffService;
	}

	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}
