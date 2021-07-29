package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.financemanagement.paymentMode.ReceiptListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.upload.MultiReceiptThreadProcess;

public class SelectReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectReceiptDialogCtrl.class);

	protected Window window_SelectReceiptDialog;
	protected ExtendedCombobox fundingAccount;
	protected Datebox depositDate;
	protected Textbox depositSlipNo;
	protected Combobox receiptStatus;
	protected ExtendedCombobox bounceCode;
	protected ExtendedCombobox cancelReason;
	protected Textbox remarks;

	protected Button btnProceed;
	protected Button btnValidate;

	protected Label ReceiptPayment;
	protected Label label_title;
	protected Label label_DepositDate;
	protected Label label_Msg;

	protected Row row_DepositBank;
	protected Row row_ReceiptStatus;
	protected Row row_DepositDate;
	protected Row row_DepositSlipNo;
	protected Row row_BounceCode;
	protected Row row_CancelReason;
	protected Row row_Remarks;

	private FinReceiptHeader finReceiptHeader = new FinReceiptHeader();
	private FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
	private Map<Long, FinReceiptHeader> recHeaderMap = new HashMap<Long, FinReceiptHeader>();
	private Rule rule;

	@Autowired
	public transient ReceiptService receiptService;
	public transient SecurityUserDAO securityUserDAO;

	@Autowired
	private RuleService ruleService;
	@Autowired
	private MultiReceiptThreadProcess threadprocess;

	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	protected long custId = Long.MIN_VALUE;

	protected ReceiptListCtrl receiptListCtrl;

	protected FinReceiptData receiptData = new FinReceiptData();
	private transient FinanceWorkFlowService financeWorkFlowService;

	private FinanceEnquiry financeEnquiry;
	private Customer customer;

	private String module;
	private String roleCode;
	private String recordAction;
	String tranType = "";
	Thread mainThread;
	Date appDate = SysParamUtil.getAppDate();

	/**
	 * default constructor.<br>
	 */
	public SelectReceiptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectReceiptDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_SelectReceiptDialog);

		if (arguments.containsKey("receiptListCtrl")) {
			this.receiptListCtrl = (ReceiptListCtrl) arguments.get("receiptListCtrl");
			setReceiptListCtrl(this.receiptListCtrl);
		} else {
			setReceiptListCtrl(null);
		}

		if (arguments.containsKey("recHeaderMap")) {
			recHeaderMap = (Map<Long, FinReceiptHeader>) arguments.get("recHeaderMap");
		}

		if (arguments.containsKey("module")) {
			module = (String) arguments.get("module");
		}

		if (arguments.containsKey("roleCode")) {
			roleCode = (String) arguments.get("roleCode");
		}

		if (arguments.containsKey("recordAction")) {
			recordAction = (String) arguments.get("recordAction");
		}

		if (FinanceConstants.DEPOSIT_MAKER.equals(module)) {
			this.row_DepositSlipNo.setVisible(true);
			this.label_DepositDate.setValue(Labels.getLabel("label_SelectReceiptDialog_DepositDate.value"));
			this.row_DepositBank.setVisible(true);
			this.receiptStatus.setValue(RepayConstants.PAYSTATUS_DEPOSITED);
		} else if (FinanceConstants.REALIZATION_MAKER.equals(module)) {
			this.row_ReceiptStatus.setVisible(true);
			this.row_Remarks.setVisible(true);
			this.label_title.setValue(Labels.getLabel("window_ReceiptDialog.title.Realization"));
			this.label_DepositDate.setValue(Labels.getLabel("label_SelectReceiptDialog_RealizatioDate.value"));
		} else {
			this.row_DepositDate.setVisible(false);
		}

		if (FinanceConstants.REALIZATION_APPROVER.equals(module) || FinanceConstants.DEPOSIT_APPROVER.equals(module)) {
			String msg = "label_SelectReceiptDialog_Msg1.value";
			if (MessageUtil.YES == MessageUtil.confirm(Labels.getLabel(msg, new String[] { recordAction }))) {
				doProcess(); //processing records
			} else {
				this.window_SelectReceiptDialog.onClose(); // closing window
				return;
			}
			return;
		}
		doSetFieldProperties();

		this.btnValidate.setVisible(false);
		this.window_SelectReceiptDialog.doModal();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 */
	private void doSetFieldProperties() {

		this.fundingAccount.setMandatoryStyle(true);
		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setValueColumn("PartnerBankCode");
		this.fundingAccount.setDescColumn("PartnerBankName");
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankCode" });

		/*
		 * Filter fundingAcFilters[] = new Filter[4]; fundingAcFilters[0] = new Filter("Purpose",
		 * RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL); fundingAcFilters[1] = new Filter("FinType",
		 * finReceiptHeader.getFinType(), Filter.OP_EQUAL); fundingAcFilters[2] = new Filter("PaymentMode",
		 * finReceiptHeader.getReceiptMode(), Filter.OP_EQUAL); if
		 * (RepayConstants.RECEIPTMODE_ONLINE.equals(finReceiptHeader.getReceiptMode())) { fundingAcFilters[2] = new
		 * Filter("PaymentMode", finReceiptHeader.getSubReceiptMode(), Filter.OP_EQUAL); } fundingAcFilters[3] = new
		 * Filter("EntityCode",
		 * finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescEntityCode(),Filter.
		 * OP_EQUAL); Filter.and(fundingAcFilters); this.fundingAccount.setFilters(fundingAcFilters);
		 */

		this.depositDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.depositDate.setValue(appDate);
		fillComboBox(this.receiptStatus, "", PennantStaticListUtil.getReceiptModeStatus(), "");

		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setMandatoryStyle(true);
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setValueType(DataType.LONG);
		this.bounceCode.setDescColumn("Reason");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID", "BounceCode", "Lovdesccategory", "Reason" });

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(
				new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });

		this.module = getArgument("module");
	}

	public void onFulfill$fundingAccount(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.fundingAccount.getValue() != null) {

		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doProcess();
		logger.debug("Leaving " + event.toString());
	}

	public void doProcess() throws Exception {
		logger.debug("Entering" + System.nanoTime());
		doSetValidation();
		doWriteComponentsToBean();
		prepareMultiReceipt(recHeaderMap);
		logger.debug("Leaving " + System.nanoTime());
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fundingAccount.setErrorMessage("");
		this.depositDate.setErrorMessage("");
		this.depositSlipNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		this.window_SelectReceiptDialog.onClose();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$receiptStatus(Event event) {
		logger.debug("Entering ");

		this.row_DepositDate.setVisible(true);
		this.row_BounceCode.setVisible(false);
		this.row_CancelReason.setVisible(false);

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(this.receiptStatus.getSelectedItem().getValue())) {
			this.row_BounceCode.setVisible(true);
		} else if (RepayConstants.PAYSTATUS_CANCEL.equals(this.receiptStatus.getSelectedItem().getValue())) {
			this.row_CancelReason.setVisible(true);
			this.row_DepositDate.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Selecting Bounce Reason Code in case of Receipt got Bounced
	 * 
	 * @param event
	 */
	public void onFulfill$bounceCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;
			if (bounceReason != null) {
				rule = getRuleService().getRuleById(bounceReason.getRuleID(), "");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doWriteComponentsToBean() throws Exception {
		logger.debug("Entering ");
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.fundingAccount.getObject() != null) {
				FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) this.fundingAccount.getObject();
				finReceiptDetail.setFundingAc(finTypePartnerBank.getID());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_DepositDate.isVisible()) {
				finReceiptDetail.setDepositDate(this.depositDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_DepositSlipNo.isVisible()) {
				finReceiptDetail.setDepositNo(this.depositSlipNo.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_ReceiptStatus.isVisible()) {
				finReceiptHeader.setReceiptModeStatus(getComboboxValue(this.receiptStatus));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_BounceCode.isVisible()) {
				finReceiptHeader.setBounceReason(Integer.valueOf(this.bounceCode.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_CancelReason.isVisible()) {
				finReceiptHeader.setCancelReason(this.cancelReason.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_Remarks.isVisible()) {
				finReceiptDetail.setRemarks(this.remarks.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doClearMessage();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		String label = Labels.getLabel("label_SelectReceiptDialog_DepositDate.value");
		if (row_ReceiptStatus.isVisible()) {
			label = Labels.getLabel("label_SelectReceiptDialog_RealizationDate.value");
		}

		if (this.row_DepositBank.isVisible()) {
			this.fundingAccount.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectReceiptDialog_FundingAccount.value"), null, true, true));
		}

		if (row_DepositDate.isVisible()) {
			if (row_ReceiptStatus.isVisible()) {
				this.depositDate.setConstraint(
						new PTDateValidator(label, true, finReceiptHeader.getDepositDate(), appDate, true));
			} else {
				this.depositDate.setConstraint(
						new PTDateValidator(label, true, finReceiptHeader.getReceiptDate(), appDate, true));
			}
		}

		if (this.row_ReceiptStatus.isVisible()) {
			this.receiptStatus.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModeStatus(),
					Labels.getLabel("label_SelectReceiptDialog_ReceiptStatus.value")));
		}

		if (this.row_BounceCode.isVisible()) {
			this.bounceCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectReceiptDialog_BounceCode.value"), null, true, true));
		}

		if (this.row_CancelReason.isVisible()) {
			this.cancelReason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectReceiptDialog_CancelReson.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.fundingAccount.setConstraint("");
		this.depositDate.setConstraint("");
		logger.debug("Leaving");
	}

	public void prepareMultiReceipt(Map<Long, FinReceiptHeader> finReceiptHeaderMap) {
		logger.debug("Entering");
		boolean flag = true;
		String method = "";
		List<AuditHeader> auditHeaderList = new ArrayList<>();
		Map<String, Object> executeMap = new HashMap<>();
		List<String> receiptIdList = new ArrayList<>();
		List<FinReceiptQueueLog> finReceiptQueueList = new ArrayList<>();
		Collection<FinReceiptHeader> recHeaderColl = finReceiptHeaderMap.values();
		long batchId = receiptService.getUploadSeqId(); // generating Sequence Id from Receipt upload

		for (FinReceiptHeader receiptHeader : recHeaderColl) {
			if (FinanceConstants.DEPOSIT_APPROVER.equals(roleCode)
					|| FinanceConstants.REALIZATION_APPROVER.equals(roleCode)) {
				receiptHeader.setBatchId(batchId);
				receiptHeader.setRecordStatus(recordAction);
				if (FinanceConstants.DEPOSIT_APPROVER.equals(module)) {
					receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
				}
				receiptData = prepareReceiptData(receiptHeader);
			} else {
				receiptHeader.setBatchId(batchId);
				for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
					if (!(RepayConstants.RECEIPTMODE_EMIINADV.equals(receiptDetail.getPaymentType())
							|| RepayConstants.RECEIPTMODE_EXCESS.equals(receiptDetail.getPaymentType())
							|| RepayConstants.RECEIPTMODE_PAYABLE.equals(receiptDetail.getPaymentType()))
							&& FinanceConstants.DEPOSIT_MAKER.equals(roleCode)) {
						receiptDetail.setDepositDate(finReceiptDetail.getDepositDate());
						receiptDetail.setDepositNo(finReceiptDetail.getDepositNo());
						receiptDetail.setFundingAc(finReceiptDetail.getFundingAc());
					}
					// Getting Amount Codes for Calculating Bounce Charges
					executeMap = receiptDetail.getDeclaredFieldValues();
				}
				if (FinanceConstants.DEPOSIT_MAKER.equals(module)) {
					if (DateUtil.compare(this.depositDate.getValue(), appDate) > 0) {
						MessageUtil.showError("Deposit date cannot be greater than " + appDate);
						return;
					}
					Date valueDate = receiptHeader.getValueDate();
					if (DateUtil.compare(appDate, valueDate) > 0) {
						if (DateUtil.compare(this.depositDate.getValue(), valueDate) < 0) {
							MessageUtil.showError("Deposit date cannot be less than " + valueDate + " for receiptId "
									+ receiptHeader.getReceiptID());
							return;
						}
					}
					receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
				}

				if (FinanceConstants.REALIZATION_MAKER.equals(module)) {
					if (DateUtil.compare(this.depositDate.getValue(), appDate) > 1) {
						MessageUtil.showError("Realization date cannot be greater than " + appDate);
						return;
					}
					if (DateUtil.compare(this.depositDate.getValue(), receiptHeader.getDepositDate()) < 0) {
						MessageUtil.showError("Realization date cannot be less than " + receiptHeader.getDepositDate()
								+ " for receiptId " + receiptHeader.getReceiptID());
						return;
					}
					receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
				}
				if (FinanceConstants.REALIZATION_MAKER.equals(roleCode)) {
					if (RepayConstants.PAYSTATUS_REALIZED.equals(finReceiptHeader.getReceiptModeStatus())) {
						receiptHeader.setRealizationDate(finReceiptDetail.getDepositDate());

						// getting Receipts which already been realized 
						if (RepayConstants.PAYSTATUS_REALIZED.equals(receiptHeader.getReceiptModeStatus())) {
							receiptIdList.add(String.valueOf(receiptHeader.getReceiptID()));
							continue;
						}

						// Cash & Online Receipt Mode could not be realized
						if (RepayConstants.RECEIPTMODE_CASH.equals(receiptHeader.getReceiptMode())
								|| RepayConstants.RECEIPTMODE_ONLINE.equals(receiptHeader.getReceiptMode())) {
							MessageUtil.showError(
									"Some selected Receipts' ReceiptMode are 'Cash' Or 'Online', So they could not be Realized");
							return;
						}
					} else if (RepayConstants.PAYSTATUS_BOUNCE.equals(finReceiptHeader.getReceiptModeStatus())) {
						receiptHeader.setBounceDate(finReceiptDetail.getDepositDate());
						receiptHeader.setBounceReason(finReceiptHeader.getBounceReason());
						if (RepayConstants.RECEIPTMODE_CASH.equals(receiptHeader.getReceiptMode())
								|| RepayConstants.RECEIPTMODE_ONLINE.equals(receiptHeader.getReceiptMode())) {
							MessageUtil.showError(
									"Some selected Receipts' ReceiptMode are 'Cash' Or 'Online', So they could not be Bounce");
							return;
						}
					} else {
						receiptHeader.setCancelReason(finReceiptHeader.getCancelReason());
					}
					receiptHeader.setReceiptModeStatus(finReceiptHeader.getReceiptModeStatus());
					receiptHeader.setRemarks(finReceiptHeader.getRemarks());

					// Calculating Bounce Charges based on FinType
					BigDecimal bounceAmt = BigDecimal.ZERO;
					if (rule != null) {
						executeMap.put("br_finType", receiptHeader.getFinType());
						bounceAmt = (BigDecimal) RuleExecutionUtil.executeRule(rule.getSQLRule(), executeMap,
								receiptHeader.getFinCcy(), RuleReturnType.DECIMAL);

						ManualAdvise bounce = new ManualAdvise();
						bounce.setNewRecord(true);

						bounce.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
						bounce.setFinReference(receiptHeader.getReference());
						bounce.setFeeTypeID(0);
						bounce.setSequence(0);

						bounce.setAdviseAmount(PennantApplicationUtil.unFormateAmount(bounceAmt,
								CurrencyUtil.getFormat(receiptHeader.getFinCcy())));

						bounce.setPaidAmount(BigDecimal.ZERO);
						bounce.setWaivedAmount(BigDecimal.ZERO);
						bounce.setValueDate(SysParamUtil.getAppDate());
						bounce.setPostDate(SysParamUtil.getPostDate());

						bounce.setRemarks(receiptHeader.getRemarks());
						bounce.setReceiptID(receiptHeader.getReceiptID());
						bounce.setBounceID(Long.valueOf(receiptHeader.getBounceReason()));
						receiptHeader.setManualAdvise(bounce);
					}
				}
				receiptHeader.setRecordStatus(recordAction);
				receiptData = prepareReceiptData(receiptHeader);
			}

			receiptData.setReceiptHeader(receiptHeader);
			String taskId = getTaskId(roleCode);

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, receiptHeader, finishedTasks);

			setNextTaskDetails(taskId, receiptHeader); // Setting taskId, nextTaskId, roleCode and nextRoleCode

			if (PennantConstants.RCD_STATUS_APPROVED.equals(recordAction) && StringUtils.isNotBlank(serviceTasks)) {
				method = PennantConstants.method_doApprove;
			} else {
				method = PennantConstants.method_saveOrUpdate;
			}

			receiptData.getReceiptHeader().setBatchId(receiptHeader.getBatchId());
			receiptData.getReceiptHeader().setVersion(receiptHeader.getVersion());
			receiptData.getReceiptHeader().setRecordType(receiptHeader.getRecordType());
			receiptData.getReceiptHeader().setReceiptModeStatus(receiptHeader.getReceiptModeStatus());
			receiptData.getReceiptHeader().setNewRecord(receiptHeader.isNewRecord());
			receiptData.getReceiptHeader().setValueDate(receiptHeader.getReceiptDate());
			receiptData.getReceiptHeader().setRoleCode(receiptHeader.getRoleCode());
			receiptData.getReceiptHeader().setNextRoleCode(receiptHeader.getNextRoleCode());
			receiptData.getReceiptHeader().setWorkflowId(receiptHeader.getWorkflowId());

			AuditHeader auditHeader = getAuditHeader(receiptData, tranType);
			auditHeader.setModelData(receiptData);
			auditHeader.getAuditDetail().setModelData(receiptData);
			auditHeader.getAuditDetail().setLovDescRecordStatus(method); // Setting method doApprove/saveOrUpdate
			auditHeaderList.add(auditHeader);

			// Preparing FinReceiptQueueLog
			FinReceiptQueueLog finReceiptQueue = new FinReceiptQueueLog();
			finReceiptQueue.setReceiptId(receiptHeader.getReceiptID());
			finReceiptQueue.setUploadId(receiptHeader.getBatchId());
			finReceiptQueue.setFinReference(receiptHeader.getReference());
			finReceiptQueue.setProgress(EodConstants.PROGRESS_WAIT);
			finReceiptQueue.setErrorLog("");
			finReceiptQueue.setThreadId(0);
			finReceiptQueue.setTransactionDate(appDate);
			finReceiptQueueList.add(finReceiptQueue);
		}
		try {
			if (!receiptIdList.isEmpty()) {
				MessageUtil.showError(
						"Some selected Receipts have already been  'Realized', It could not be Realized Again. ReceiptId : "
								+ String.join(",", receiptIdList));
				return;
			}

			receiptService.saveMultiReceiptLog(finReceiptQueueList); // Saving all selected records in FinReceiptQueueLog

			Thread thread = new Thread(new MultiReceiptRunnable(finReceiptHeaderMap, auditHeaderList, batchId));
			thread.start();
			Thread.sleep(1000);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			MessageUtil.showError(e);
		}

		finReceiptHeaderMap.clear();
		refreshList();
		FinReceiptHeader frh = ((FinReceiptData) auditHeaderList.get(0).getModelData()).getReceiptHeader();
		// Close the Existing Dialog
		closeDialog();

		if (flag) {
			String msg = PennantApplicationUtil.getSavingStatus(frh.getRoleCode(), frh.getNextRoleCode(),
					String.valueOf(frh.getBatchId()), " Receipt Batch ", frh.getRecordStatus());
			Clients.showNotification(msg, "info", null, null, -1);
		}
		logger.debug("Leaving");
	}

	public FinReceiptData prepareReceiptData(FinReceiptHeader receiptHeader) {
		logger.debug("Entering");
		FinReceiptData receiptData = null;

		// Loading WorkFLow based on WorkFLowId
		doLoadWorkFlow(receiptHeader.isWorkflow(), receiptHeader.getWorkflowId(), receiptHeader.getNextTaskId());
		WorkFlowDetails workFlowDetail = WorkFlowUtil.getWorkflow(receiptHeader.getWorkflowId());
		if (workFlowDetail == null) {
			setWorkFlowEnabled(false);
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return receiptData;
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetail.getFirstTaskOwner()));
		}

		receiptHeader.setValueDate(receiptHeader.getReceiptDate());
		// Role Code State Checking
		String userRole = receiptHeader.getNextRoleCode();

		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetail.getFirstTaskOwner();
		}

		String nextroleCode = receiptHeader.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = receiptHeader.getReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetail.getError());

			logger.debug("Leaving");
			return receiptData;
		}

		if (isWorkFlowEnabled()) {
			String eventCode = "";

			if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
				eventCode = AccountingEvent.REPAY;

			} else if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
				eventCode = AccountingEvent.EARLYPAY;

			} else if (StringUtils.equals(receiptHeader.getReceiptPurpose(),
					FinServiceEvent.EARLYSETTLE)) {
				eventCode = AccountingEvent.EARLYSTL;

			}

			receiptData = receiptService.getFinReceiptDataByReceiptId(receiptHeader.getReceiptID(), eventCode,
					FinServiceEvent.RECEIPT, userRole);
			if (!enqiryModule && receiptData.isCalReq()) {
				ErrorDetail errorDetail = receiptService.doInstrumentValidation(receiptData);
				if (errorDetail != null) {
					ErrorDetail errorDtl = ErrorUtil.getErrorDetail(errorDetail);
					MessageUtil.showError(errorDtl.getError());
					logger.debug("Leaving");
					return new FinReceiptData();
				}
			}

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(receiptHeader.getRecordType())) {
					receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					receiptHeader.setVersion(1);
					if (receiptHeader.isNewRecord()) {
						receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						receiptHeader.setNewRecord(true);
					}
				}
			} else {
				receiptHeader.setVersion(receiptHeader.getVersion() + 1);
				tranType = PennantConstants.TRAN_UPD;
			}

		}
		logger.debug("Leaving");
		return receiptData;
	}

	private String getServiceTasks(String taskId, FinReceiptHeader rch, String finishedTasks) {
		logger.debug("Entering");
		String serviceTasks = getServiceOperations(taskId, rch);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinReceiptHeader receiptHeader) {
		logger.debug("Entering");
		// Set the next task id
		String action = recordAction;
		String nextTaskId = StringUtils.trimToEmpty(receiptHeader.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, receiptHeader);
		}

		// Set the role codes for the next tasks
		if (StringUtils.isNotBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}

		receiptHeader.setTaskId(taskId);
		receiptHeader.setNextTaskId(nextTaskId);
		receiptHeader.setRoleCode(roleCode);
		receiptHeader.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		// FIXME: PV: CODE REVIEW PENDING
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail,
				repayData.getReceiptHeader().getUserDetails(), getOverideMap());
	}

	protected void refreshList() {
		getReceiptListCtrl().doRefresh();
	}

	class MultiReceiptRunnable implements Runnable {
		private List<AuditHeader> auditHeaderList;
		Map<Long, FinReceiptHeader> finReceiptHeaderMap;
		long batchId;

		public MultiReceiptRunnable(Map<Long, FinReceiptHeader> finReceiptHeaderMap, List<AuditHeader> auditList,
				long batchId) {
			super();
			this.auditHeaderList = auditList;
			this.finReceiptHeaderMap = finReceiptHeaderMap;
			this.batchId = batchId;
		}

		@Override
		public void run() {
			try {
				threadprocess.processThread(finReceiptHeaderMap, auditHeaderList, batchId);// Initializing Thread Processing
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public FinanceEnquiry getFinanceEnquiry() {
		return financeEnquiry;
	}

	public void setFinanceEnquiry(FinanceEnquiry financeEnquiry) {
		this.financeEnquiry = financeEnquiry;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public ReceiptListCtrl getReceiptListCtrl() {
		return receiptListCtrl;
	}

	public void setReceiptListCtrl(ReceiptListCtrl receiptListCtrl) {
		this.receiptListCtrl = receiptListCtrl;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
}
