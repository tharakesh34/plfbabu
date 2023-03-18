package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectCrossLoanKnockOffDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectCrossLoanKnockOffDialogCtrl.class);

	protected Window windowSelectCrossLoanKnockOffDialog;
	protected Textbox custCIF;
	protected ExtendedCombobox fromFinReference;
	protected ExtendedCombobox toFinReference;
	protected Combobox knockOffFrom;
	protected Combobox receiptPurpose;
	protected CurrencyBox receiptAmount;
	protected CurrencyBox receiptDues;
	protected Datebox receiptDate;
	protected Datebox valueDate;
	protected ExtendedCombobox referenceId;
	protected Button btnProceed;
	protected Button btnValidate;
	protected Button btnSearchCustCIF;
	protected Button btnSearchFinRef;
	protected Row rowValueDate;
	protected Row rowReceiptPurpose;
	protected Row rowReceiptAmount;
	protected Row rowKnockOffFrom;
	protected Row rowReferenceId;
	protected Label labelCustomerName;

	private transient ReceiptService receiptService;
	private transient FinanceMainService financeMainService;
	private transient CustomerDetailsService customerDetailsService;
	private transient FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private transient FinanceMainDAO financeMainDAO;
	private CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl;

	private long custId = Long.MIN_VALUE;
	private FinReceiptData receiptData = new FinReceiptData();
	private String module;
	private int formatter = 2;

	public SelectCrossLoanKnockOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	public void onCreate$windowSelectCrossLoanKnockOffDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowSelectCrossLoanKnockOffDialog);

		if (arguments.containsKey("crossLoanKnockOffListCtrl")) {
			this.crossLoanKnockOffListCtrl = (CrossLoanKnockOffListCtrl) arguments.get("crossLoanKnockOffListCtrl");
			setCrossLoanKnockOffListCtrl(this.crossLoanKnockOffListCtrl);
		} else {
			setCrossLoanKnockOffListCtrl(null);
		}

		doSetFieldProperties();
		this.valueDate.setDisabled(true);
		this.valueDate.setVisible(false);
		this.btnValidate.setDisabled(true);
		this.windowSelectCrossLoanKnockOffDialog.doModal();
		this.receiptAmount.setSclass("");

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onChange$receiptPurpose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		changeReceiptPurpose();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void changeReceiptPurpose() {
		loadValueDate();
		this.btnValidate.setDisabled(false);
		this.receiptDues.setValue(BigDecimal.ZERO);
	}

	public void onChange$excessAdjustTo(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.btnValidate.setDisabled(false);
		this.receiptDues.setValue(BigDecimal.ZERO);
		this.btnProceed.setDisabled(true);
		this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void loadValueDate() {
		this.rowValueDate.setVisible(false);
		this.valueDate.setValue(this.receiptDate.getValue());

		String recPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();
		if (!FinServiceEvent.EARLYSETTLE.equals(recPurpose)) {
			return;
		}

		int defaultClearingDays = SysParamUtil.getValueAsInt(SMTParameterConstants.EARLYSETTLE_CHQ_DFT_DAYS);
		this.valueDate.setValue(DateUtility.addDays(this.receiptDate.getValue(), defaultClearingDays));
		this.valueDate.setVisible(true);
		this.rowValueDate.setVisible(true);
	}

	public void onChange$receiptDate(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		loadValueDate();
		this.btnValidate.setDisabled(false);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onChange$knockOffFrom(Event event) {
		logger.debug(Literal.ENTERING);

		onChangeKnockOffFrom();

		logger.debug(Literal.LEAVING);

	}

	private void onChangeKnockOffFrom() {

		this.toFinReference.setValue("");
		this.toFinReference.setObject("");

		this.referenceId.setMandatoryStyle(true);
		this.referenceId.setDescColumn("BalanceAmt");
		this.referenceId.setConstraint("");
		this.referenceId.setValue("", "");
		this.referenceId.setValueType(DataType.LONG);
		this.receiptAmount.setValue(BigDecimal.ZERO);

		Filter[] filter = new Filter[2];
		filter[0] = new Filter("FinReference", this.fromFinReference.getValue(), Filter.OP_EQUAL);
		filter[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
		this.referenceId.setFilters(filter);

		switch (getComboboxValue(knockOffFrom)) {
		case ReceiptMode.EXCESS:
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });
			this.referenceId.setWhereClause("(ReceiptModeStatus is null or ReceiptModeStatus not in ('C', 'B'))");
			break;
		case ReceiptMode.PAYABLE:
			this.referenceId.setModuleName("PayableAdvise");
			this.referenceId.setValueColumn("AdviseID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "AdviseID" });
			this.referenceId.setWhereClause("(BalanceAmt > 0)");
			break;
		default:
			break;
		}
	}

	public void onFulfill$referenceId(Event event) {
		logger.debug(Literal.ENTERING);

		boolean isDisabled = false;
		Date receiptDt = receiptDate.getValue();

		String knockOff = getComboboxValue(knockOffFrom);
		FinExcessAmount fea = null;
		if (!ReceiptMode.PAYABLE.equals(knockOff) && !PennantConstants.List_Select.equals(knockOff)) {
			fea = (FinExcessAmount) this.referenceId.getObject();
		}

		if (fea != null && fea.getValueDate() != null) {
			receiptDt = fea.getValueDate();
			isDisabled = true;

			long finID = ComponentUtil.getFinID(this.toFinReference);
			Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, receiptDate.getValue());

			if (DateUtil.compare(receiptDt, schDate) < 0) {
				receiptDt = schDate;
			}
		}

		this.receiptDate.setValue(receiptDt);
		this.receiptDate.setDisabled(isDisabled);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetFieldProperties() {
		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptDate.setValue(SysParamUtil.getAppDate());

		this.receiptAmount.setTextBoxWidth(190);

		this.module = getArgument("module");
		if (FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER.equals(this.module)) {
			fillComboBox(this.knockOffFrom, "", PennantStaticListUtil.getKnockOffFromVlaues(),
					",EMIINADV,BOUNCE,SETTLE,TEXCESS,CASHCLT,DSF,");
			fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,EarlySettlement,");

			this.rowKnockOffFrom.setVisible(true);
			this.rowReferenceId.setVisible(true);
			this.referenceId.setButtonDisabled(false);
			this.referenceId.setTextBoxWidth(155);
			this.referenceId.setMandatoryStyle(true);
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("FinType");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });
		}

		this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptDues.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptDues.setTextBoxWidth(190);
		this.receiptDues.setDisabled(true);

		this.fromFinReference.setButtonDisabled(false);
		this.fromFinReference.setTextBoxWidth(155);
		this.fromFinReference.setMandatoryStyle(true);
		this.fromFinReference.setModuleName("ReceiptFinanceMain");
		this.fromFinReference.setValueColumn("FinReference");
		this.fromFinReference.setDescColumn("FinType");
		this.fromFinReference.setValidateColumns(new String[] { "FinReference" });

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("Undersettlement", 1, Filter.OP_NOT_EQUAL);
		this.fromFinReference.setFilters(filters);

		this.toFinReference.setButtonDisabled(false);
		this.toFinReference.setTextBoxWidth(155);
		this.toFinReference.setMandatoryStyle(true);
		this.toFinReference.setModuleName("ReceiptFinanceMain");
		this.toFinReference.setValueColumn("FinReference");
		this.toFinReference.setDescColumn("FinType");
		this.toFinReference.setValidateColumns(new String[] { "FinReference" });

	}

	private void addFilter(Customer customer) {
		logger.debug(Literal.ENTERING);

		this.fromFinReference.setValue("");
		this.fromFinReference.setObject("");
		this.custCIF.setValue("");
		this.fromFinReference.setFilters(null);
		this.toFinReference.setValue("");
		this.toFinReference.setObject("");
		this.custCIF.setValue("");
		this.toFinReference.setFilters(null);

		if (customer == null) {
			return;
		}

		this.custId = customer.getCustID();
		this.custCIF.setValue(customer.getCustCIF());

		Filter[] fromLoan = new Filter[1];
		Filter[] toLoan = new Filter[1];

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			fromLoan[0] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);
			toLoan[0] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);

		} else {
			fromLoan[0] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
			toLoan[0] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
		}

		if (customer.getCustID() <= 0 || StringUtils.isBlank(customer.getCustCoreBank())) {
			this.fromFinReference.setFilters(null);
			this.toFinReference.setFilters(null);

		} else {
			this.fromFinReference.setFilters(fromLoan);
			this.toFinReference.setFilters(toLoan);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$custCIF(Event event) {
		logger.debug(Literal.ENTERING);

		addFilter(fetchCustomerDataByCIF());

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);

		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();

		ErrorDetail errorDetail = null;
		String receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();

		if (receiptPurpose.equals(FinServiceEvent.EARLYRPY) || receiptPurpose.equals(FinServiceEvent.EARLYSETTLE)) {
			Date startDate = ((FinanceMain) this.fromFinReference.getObject()).getFinStartDate();
			errorDetail = financeMainService.rescheduleValidation(this.receiptDate.getValue(),
					Long.valueOf(this.fromFinReference.getId()), startDate);
		}

		BigDecimal receiptDues = this.receiptDues.getActualValue();
		BigDecimal knockOffAmount = this.receiptAmount.getActualValue();
		receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();
		if (FinServiceEvent.SCHDRPY.equals(receiptPurpose) && knockOffAmount.compareTo(receiptDues) > 0) {
			MessageUtil.showError(Labels.getLabel("label_Allocation_More_Due_KnockedOff"));
			return;
		}

		// validating value date for knock off
		Date valuedate = null;
		if (RepayConstants.PAYTYPE_PAYABLE.equals(receiptData.getReceiptHeader().getReceiptMode())) {
			ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
			valuedate = ma.getValueDate();
		} else {
			FinExcessAmount fea = (FinExcessAmount) this.referenceId.getObject();

			long finID = ComponentUtil.getFinID(this.toFinReference);

			valuedate = SysParamUtil.getAppDate();
			if (fea != null && fea.getValueDate() != null) {
				valuedate = fea.getValueDate();

				Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, valuedate);

				if (DateUtil.compare(valuedate, schDate) < 0) {
					valuedate = schDate;
				}
			}
		}

		if (this.receiptDate.getValue().compareTo(valuedate) < 0) {
			MessageUtil.showError(
					Labels.getLabel("label_knockoffValuedate", new String[] { DateUtil.formatToShortDate(valuedate) }));
		}

		if (errorDetail != null) {
			MessageUtil.showError(ErrorUtil.getErrorDetail(errorDetail));
		} else {
			validateReceiptData();
			if (receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails() != null
					&& !receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().isEmpty()) {
				MessageUtil.showError(ErrorUtil
						.getErrorDetail(receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().get(0)));
				return;
			}
			receiptData.setInitiation(true);
			doShowDialog();
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnValidate(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.btnProceed.setDisabled(true);
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();
		validateReceiptData();

		Date valuedate = null;
		if (RepayConstants.PAYTYPE_PAYABLE.equals(receiptData.getReceiptHeader().getReceiptMode())) {
			ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
			valuedate = ma.getValueDate();
		} else {
			FinExcessAmount fea = (FinExcessAmount) this.referenceId.getObject();

			long finID = ComponentUtil.getFinID(this.toFinReference);

			valuedate = SysParamUtil.getAppDate();
			if (fea != null && fea.getValueDate() != null) {
				valuedate = fea.getValueDate();

				Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, valuedate);

				if (DateUtil.compare(valuedate, schDate) < 0) {
					valuedate = schDate;
				}
			}
		}

		if (this.receiptDate.getValue().compareTo(valuedate) < 0) {
			MessageUtil.showError(
					Labels.getLabel("label_knockoffValuedate", new String[] { DateUtil.formatToShortDate(valuedate) }));
			return;
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails() != null
				&& !receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().get(0)));
			return;
		}

		receiptData = receiptService.calcuateDues(receiptData);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal totalDues = rch.getTotalPastDues().getTotalDue().add(rch.getTotalBounces().getTotalDue())
				.add(rch.getTotalRcvAdvises().getTotalDue()).add(rch.getTotalFees().getTotalDue())
				.subtract(receiptData.getExcessAvailable());

		this.receiptDues.setValue(PennantApplicationUtil.formateAmount(totalDues, formatter));
		this.receiptDues.setDisabled(true);
		this.btnProceed.setDisabled(false);
		this.btnValidate.setDisabled(true);
		this.receiptAmount.setProperties(true, PennantConstants.defaultCCYDecPos);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public Customer fetchCustomerDataByCIF() {
		Customer cust = new Customer();

		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		receiptPurpose.setDisabled(false);
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			this.custId = 0;
			labelCustomerName.setValue("");
		} else {
			cust = this.customerDetailsService.getCustomer(cif);

			if (cust != null) {
				labelCustomerName.setValue(cust.getCustShrtName());
				this.custId = cust.getCustID();
			}
		}

		return cust;
	}

	public Customer fetchCustomerDataByID(long custID) {
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		Customer customer = this.customerDetailsService.getCustomer(custID);

		Filter[] fromLoan = new Filter[1];
		Filter[] toLoan = new Filter[1];

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			fromLoan[0] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);
			toLoan[0] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);
		} else {
			fromLoan[0] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
			toLoan[0] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
		}

		this.fromFinReference.setFilters(fromLoan);
		this.toFinReference.setFilters(toLoan);

		this.custId = customer.getCustID();
		this.custCIF.setValue(customer.getCustCIF());
		labelCustomerName.setValue(customer.getCustShrtName());

		return customer;
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.fromFinReference.setErrorMessage("");
		this.toFinReference.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.receiptDate.setErrorMessage("");
		this.receiptAmount.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.fromFinReference.setValue("");
		this.toFinReference.setValue("");
		doClearMessage();
		final HashMap<String, Object> map = new HashMap<>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.windowSelectCrossLoanKnockOffDialog.onClose();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doSetCustomer(Object nCustomer) {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();
		addFilter((Customer) nCustomer);

		logger.debug(Literal.LEAVING);
	}

	public void validateToFinReference(Event event, boolean isShowSearchList) {
		logger.debug(Literal.ENTERING);

		this.toFinReference.setConstraint("");
		this.toFinReference.clearErrorMessage();
		receiptPurpose.setDisabled(false);

		Clients.clearWrongValue(toFinReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.windowSelectCrossLoanKnockOffDialog, "FinanceMain");
		} else {
			dataObject = this.toFinReference.getObject();
		}

		Date appDate = SysParamUtil.getAppDate();

		if (dataObject instanceof String) {
			this.toFinReference.setValue(dataObject.toString());
			this.toFinReference.setDescription("");
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.toFinReference.setValue(financeMain.getFinReference());
				financeMain.setAppDate(appDate);
				resetToLANDefaults(financeMain);
			}
		}

		Date receiptDt = null;
		long finID = ComponentUtil.getFinID(this.toFinReference);
		Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, appDate);

		if (DateUtil.compare(this.receiptDate.getValue(), schDate) < 0) {
			receiptDt = schDate;
			this.receiptDate.setValue(receiptDt);
			this.receiptDate.setDisabled(true);
		} else if (schDate == null) {
			this.receiptDate.setValue(appDate);
			this.receiptDate.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void resetToLANDefaults(FinanceMain financeMain) {
		this.toFinReference.setValue(financeMain.getFinReference());

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (FinanceConstants.PRODUCT_GOLD.equals(financeMain.getProductCategory())) {
			fillComboBox(receiptPurpose, FinServiceEvent.EARLYRPY, PennantStaticListUtil.getReceiptPurpose(),
					",SchdlRepayment, EarlySettlement, FeePayment,");
			receiptPurpose.setDisabled(true);
			this.receiptDate.setValue(financeMain.getAppDate());
			this.receiptDate.setDisabled(true);
		} else {
			if (!financeMain.isFinIsActive()
					|| DateUtil.compare(financeMain.getAppDate(), financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}

			if (!financeMain.isFinIsActive()
					|| DateUtil.compare(financeMain.getAppDate(), financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			}
		}

		long finID = ComponentUtil.getFinID(this.toFinReference);
		Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, financeMain.getAppDate());
		Date receiptDt = receiptDate.getValue();

		if (DateUtil.compare(receiptDt, schDate) < 0) {
			receiptDt = schDate;
			this.receiptDate.setDisabled(false);
		}

		this.receiptDate.setValue(receiptDt);
		this.receiptDate.setDisabled(true);
	}

	private void validateReceiptData() {
		String loanReference = null;
		String tranBranch = null;
		loanReference = this.fromFinReference.getValue().toString();
		String eventCode = null;

		String recPurpose = getComboboxValue(this.receiptPurpose);
		ReceiptPurpose rptPurpose = ReceiptPurpose.purpose(recPurpose);

		switch (rptPurpose) {
		case SCHDRPY:
			eventCode = AccountingEvent.REPAY;
			break;
		case EARLYRPY:
			eventCode = AccountingEvent.EARLYPAY;
			break;
		case EARLYSETTLE:
			eventCode = AccountingEvent.EARLYSTL;
			break;
		default:
			break;
		}

		Date appDate = SysParamUtil.getAppDate();

		long finID = ComponentUtil.getFinID(this.toFinReference);
		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);

		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setValueDate(this.receiptDate.getValue());
		receiptData.setReceiptHeader(rch);

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainForLMSEvent(finID);
		fm.setAppDate(appDate);
		fm.setReceiptPurpose(rptPurpose.code());

		schdData.setFinanceMain(fm);
		schdData.setFeeEvent(eventCode);

		boolean isEnquiry = receiptData.isEnquiry();

		receiptService.setFinanceData(receiptData);
		receiptData.setEnquiry(isEnquiry);
		receiptData.setClosrMaturedLAN(false);
		schdData.setFinServiceInstruction(new FinServiceInstruction());
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		if (!fm.isFinIsActive()) {
			fsi.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		}

		fsi.setReceiptDetail(new FinReceiptDetail());
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(loanReference);

		rch.setFinID(fm.getFinID());
		rch.setReference(loanReference);
		rch.setCashierBranch(tranBranch);

		rch.setFinType(schdData.getFinanceMain().getFinType());
		rch.setReceiptAmount(PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(), formatter));
		rch.setReceiptPurpose(rptPurpose.code());

		String receiptMode = this.knockOffFrom.getSelectedItem().getValue().toString();
		rch.setReceiptMode(receiptMode);

		rch.setKnockOffRefId(Long.valueOf(this.referenceId.getValue()));
		rch.setKnockOffType(KnockOffType.CROSS_LOAN.code());

		schdData.setErrorDetails(new ArrayList<>(1));

		fsi.setReceivedDate(receiptDate.getValue());
		if (this.rowValueDate.isVisible()) {
			fsi.setValueDate(valueDate.getValue());
		} else {
			fsi.setValueDate(receiptDate.getValue());
		}

		fsi.setReceiptPurpose(rptPurpose.code());
		fsi.setFromDate(fsi.getValueDate());

		rch.setReceiptAmount(rch.getReceiptAmount().add(rch.getTdsAmount()));
		rch.setReceiptDate(appDate);
		rch.setValueDate(fsi.getValueDate());
		rch.setReceivedDate(fsi.getReceivedDate());
		rcd.setValueDate(fsi.getValueDate());
		rcd.setReceivedDate(fsi.getReceivedDate());
		rch.setKnockoffFrom(this.knockOffFrom.getSelectedItem().getValue().toString());
		rch.setFromLanReference(this.fromFinReference.getValue().toString());

		receiptData = receiptService.getExcessAndManualAdviseData(receiptData,
				ComponentUtil.getFinID(this.fromFinReference));

		receiptService.validateDual(receiptData);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		CrossLoanKnockOff cLKHeader = new CrossLoanKnockOff();
		cLKHeader.setFinReceiptData(receiptData);

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		final HashMap<String, Object> map = new HashMap<>();

		if (getWorkflowDetails(rch.getFinType(), false) == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		Date appDate = SysParamUtil.getAppDate();

		cLKHeader.setValueDate(this.receiptDate.getValue());
		cLKHeader.setPostDate(appDate);

		CrossLoanTransfer crossLoanTransfer = new CrossLoanTransfer();
		crossLoanTransfer.setCustCif(this.custCIF.getValue());
		crossLoanTransfer.setCustId(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		crossLoanTransfer.setFromFinID(ComponentUtil.getFinID(this.fromFinReference));
		crossLoanTransfer.setFromFinReference(this.fromFinReference.getValidatedValue());
		crossLoanTransfer.setToFinID(ComponentUtil.getFinID(this.toFinReference));
		crossLoanTransfer.setToFinReference(this.toFinReference.getValidatedValue());
		crossLoanTransfer.setTransferAmount(receiptData.getReceiptHeader().getReceiptAmount());

		if (RepayConstants.PAYTYPE_PAYABLE.equals(receiptData.getReceiptHeader().getReceiptMode())) {
			ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
			crossLoanTransfer.setExcessType("P");
			crossLoanTransfer.setExcessAmount(ma.getAdviseAmount());
			crossLoanTransfer.setUtiliseAmount(ma.getPaidAmount());
			crossLoanTransfer.setReserveAmount(ma.getReservedAmt());
			crossLoanTransfer.setAvailableAmount(ma.getBalanceAmt());
			crossLoanTransfer.setExcessId(ma.getAdviseID());
		} else {
			FinExcessAmount fe = (FinExcessAmount) this.referenceId.getObject();
			crossLoanTransfer.setExcessType(fe.getAmountType());
			crossLoanTransfer.setExcessAmount(fe.getAmount());
			crossLoanTransfer.setUtiliseAmount(fe.getUtilisedAmt());
			crossLoanTransfer.setReserveAmount(fe.getReservedAmt());
			crossLoanTransfer.setAvailableAmount(fe.getBalanceAmt());
			crossLoanTransfer.setExcessId(fe.getExcessID());
		}

		cLKHeader.setCrossLoanTransfer(crossLoanTransfer);

		cLKHeader.setWorkflowId(getWorkFlowId());
		cLKHeader.setNewRecord(true);
		cLKHeader.setWorkflowId(getWorkFlowId());

		map.put("module", this.module);
		map.put("isKnockOff", true);
		map.put("isForeClosure", false);
		map.put("isSettlementLoan", false);
		map.put("crossLoanHeader", cLKHeader);
		map.put("crossLoanKnockOffListCtrl", crossLoanKnockOffListCtrl);
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/CrossLoanKnockOffDialog.zul", null,
				map);

		this.windowSelectCrossLoanKnockOffDialog.onClose();

		logger.debug(Literal.LEAVING);
	}

	private WorkFlowDetails getWorkflowDetails(String finType, boolean isPromotion) {
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(FinServiceEvent.RECEIPT)) {
			String workflowTye = "";
			workflowTye = financeWorkFlowService.getFinanceWorkFlowType(finType, FinServiceEvent.CROSS_LOAN_KNOCKOFF,
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

		return workFlowDetails;
	}

	public void onClick$btnSearchFinRef(Event event) {

		validateFinReference(event, true);
	}

	public void onFulfill$fromFinReference(Event event) {

		if (StringUtils.isNotBlank(this.fromFinReference.getValue())) {
			validateFinReference(event, false);
		}

		onChangeKnockOffFrom();

		this.btnValidate.setDisabled(false);
	}

	public void onFulfill$toFinReference(Event event) {
		validateToFinReference(event, false);
		this.btnValidate.setDisabled(false);
	}

	public void validateFinReference(Event event, boolean isShowSearchList) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.fromFinReference.setConstraint("");
		this.fromFinReference.clearErrorMessage();
		receiptPurpose.setDisabled(false);
		Clients.clearWrongValue(fromFinReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.windowSelectCrossLoanKnockOffDialog, "FinanceMain");
		} else {
			dataObject = this.fromFinReference.getObject();
		}

		if (dataObject instanceof String) {
			this.fromFinReference.setValue(dataObject.toString());
			this.fromFinReference.setDescription("");
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.fromFinReference.setValue(financeMain.getFinReference());
				this.fromFinReference.setId(String.valueOf(financeMain.getFinID()));
				this.custCIF.setValue(String.valueOf(financeMain.getCustCIF()));
				this.custId = financeMain.getCustID();
				resetDefaults(financeMain);
			}
		}

		Customer customer = customerDetailsService.getCustomer(this.custId);

		Filter filter[] = new Filter[2];
		filter[0] = new Filter("FinReference", this.fromFinReference.getValue(), Filter.OP_NOT_EQUAL);

		if (customer != null) {

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				filter[1] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);

			} else {
				filter[1] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);

			}
		}

		this.toFinReference.setFilters(filter);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (StringUtils.isEmpty(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_Customer.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.fromFinReference.getValue())) {
				throw new WrongValueException(this.fromFinReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_FromLoanReference.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rowReceiptPurpose.isVisible() && "#".equals(getComboboxValue(this.receiptPurpose))) {
				throw new WrongValueException(this.receiptPurpose, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptPurpose.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rowReceiptAmount.isVisible() && this.receiptAmount.isMandatory()) {
				BigDecimal receiptAmt = this.receiptAmount.getActualValue();
				receiptAmt = PennantApplicationUtil.unFormateAmount(receiptAmt, formatter);

				if (receiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
					throw new WrongValueException(this.receiptAmount.getCcyTextBox(),
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Receipt Amount" }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.toFinReference.getValue())) {
				throw new WrongValueException(this.toFinReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_ToReference.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.knockOffFrom.getValue())) {
				throw new WrongValueException(this.knockOffFrom, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LoanClosurePayment_kncockoffFrom.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		validateBasicReceiptDate();

		doRemoveValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void validateBasicReceiptDate() {
		Date appDate = SysParamUtil.getAppDate();

		if (receiptDate.getValue() == null) {
			receiptDate.setValue(appDate);
		}

		if (receiptDate.getValue().compareTo(appDate) == 0) {
			return;
		}

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (this.receiptDate.getValue().compareTo(appDate) > 0) {
				throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
						Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), appDate.toString() }));
			}

			int daysBackValueAllowed = SysParamUtil.getValueAsInt("ALW_SP_BACK_DAYS");
			int daysBackValue = DateUtility.getDaysBetween(this.receiptDate.getValue(), appDate);
			if (daysBackValue >= daysBackValueAllowed) {
				throw new WrongValueException(this.receiptDate,
						Labels.getLabel("DATE_ALLOWED_ON_AFTER",
								new String[] { Labels.getLabel("label_SchedulePayment_ReceiptDate.value"),
										DateUtility.addDays(appDate, -daysBackValueAllowed).toString() }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
	}

	public void onFulfill$receiptAmount(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);
		this.btnValidate.setDisabled(false);

		if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			MessageUtil.showError(Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Receipt Amount" }));
		}
		BigDecimal availableAmount = new BigDecimal(this.referenceId.getDescription());
		if (PennantApplicationUtil.formateAmount(receiptAmount, formatter).compareTo(availableAmount) > 0) {
			MessageUtil.showError(
					Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] { "Receipt Amount", "KnockOff Amount" }));
			this.receiptAmount.setValue(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetValidation() {
		this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_Customer.value"),
				PennantRegularExpressions.REGEX_NUMERIC, true));

		this.fromFinReference
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_FromLoanReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));

		this.toFinReference
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_ToReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));

		this.referenceId.setConstraint(new PTStringValidator(Labels.getLabel("label_LoanClosurePayment_RefId.value"),
				PennantRegularExpressions.REGEX_NUMERIC, true));

		this.knockOffFrom
				.setConstraint(new PTListValidator(Labels.getLabel("label_LoanClosurePayment_kncockoffFrom.value"),
						PennantStaticListUtil.getKnockOffFromVlaues(), true));

	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.custCIF.setConstraint("");
		this.fromFinReference.setConstraint("");
		this.receiptDate.setConstraint("");
		this.receiptAmount.setConstraint("");
		this.toFinReference.setConstraint("");
		this.knockOffFrom.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	public void resetDefaults(FinanceMain financeMain) {
		this.fromFinReference.setValue(financeMain.getFinReference());

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		Date appDate = SysParamUtil.getAppDate();
		if (financeMain.getAdvanceEMI().compareTo(BigDecimal.ZERO) > 0) {
			if (!financeMain.isFinIsActive() || DateUtil.compare(appDate, financeMain.getCalMaturity()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}

		} else {
			if (!financeMain.isFinIsActive() || DateUtil.compare(appDate, financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}
		}

		if (!financeMain.isFinIsActive() || DateUtil.compare(appDate, financeMain.getMaturityDate()) >= 0) {
			fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
					",EarlyPayment, EarlySettlement, FeePayment,");
			receiptPurpose.setDisabled(true);
		}

	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public void setCrossLoanKnockOffListCtrl(CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl) {
		this.crossLoanKnockOffListCtrl = crossLoanKnockOffListCtrl;
	}

}