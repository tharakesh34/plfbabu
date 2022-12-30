package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectCrossLoanKnockOffDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectCrossLoanKnockOffDialogCtrl.class);

	protected Window window_SelectCrossLoanKnockOffDialog;
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

	protected Row row_valueDate;
	protected Row row_intTillDate;
	protected Row row_ReceiptPurpose;
	protected Row row_receiptAmount;
	protected Row row_receiptDues;
	protected Label ReceiptPayment;
	protected Label label_ReceiptPayment_CustomerName;

	protected Row row_KnockOffFrom;
	protected Row row_ReferenceId;

	protected Label label_title;

	@Autowired
	public transient ReceiptService receiptService;
	public transient SecurityUserDAO securityUserDAO;

	@Autowired
	private transient ReceiptCalculator receiptCalculator;

	@Autowired
	private transient FinanceMainService financeMainService;

	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	protected long custId = Long.MIN_VALUE;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected JdbcSearchObject<FinanceMain> searchObject;

	protected CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl;

	protected FinReceiptData receiptData = new FinReceiptData();
	protected FinanceMain finMain = new FinanceMain();
	private transient WorkFlowDetails workFlowDetails = null;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private boolean isKnockOff = true;

	private FinanceEnquiry financeEnquiry;
	private Customer customer;

	// private DueData dueData;
	private int daysBackValueAllowed, daysBackValue;
	private String module;
	private int formatter = 2;
	Date appDate = SysParamUtil.getAppDate();
	private boolean knockOffSettlement = false;
	private FinanceMainDAO financeMainDAO;
	boolean isForeClosure = false;
	private long fromCustId = 0;
	private long toCustId = 0;

	/**
	 * default constructor.<br>
	 */
	public SelectCrossLoanKnockOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	public void onCreate$window_SelectCrossLoanKnockOffDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_SelectCrossLoanKnockOffDialog);

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
		this.window_SelectCrossLoanKnockOffDialog.doModal();
		this.receiptAmount.setSclass("");
		logger.debug("Leaving " + event.toString());
	}

	public void onChange$receiptPurpose(Event event) throws Exception {
		changeReceiptPurpose();
	}

	private void changeReceiptPurpose() {
		loadValueDate();
		this.btnValidate.setDisabled(false);
		this.receiptDues.setValue(BigDecimal.ZERO);
	}

	public void onChange$excessAdjustTo(Event event) throws Exception {
		this.btnValidate.setDisabled(false);
		this.receiptDues.setValue(BigDecimal.ZERO);
		this.btnProceed.setDisabled(true);
		this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
	}

	public void loadValueDate() {
		this.row_valueDate.setVisible(false);
		this.valueDate.setValue(this.receiptDate.getValue());

		String recPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();
		if (!StringUtils.equals(recPurpose, FinServiceEvent.EARLYSETTLE)) {
			return;
		}

		int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
		this.valueDate.setValue(DateUtility.addDays(this.receiptDate.getValue(), defaultClearingDays));
		this.valueDate.setVisible(true);
		this.row_valueDate.setVisible(true);
	}

	public void onChange$receiptDate(Event event) throws Exception {
		loadValueDate();
		this.btnValidate.setDisabled(false);
	}

	public void onChange$knockOffFrom(Event event) throws Exception {

		this.referenceId.setMandatoryStyle(true);
		this.referenceId.setDescColumn("BalanceAmt");
		this.referenceId.setConstraint("");
		this.referenceId.setValue("", "");

		Filter[] filter = new Filter[2];

		/*
		 * if (finMain.isFinIsActive() && StringUtils.equals(getComboboxValue(knockOffFrom),
		 * RepayConstants.RECEIPTMODE_PAYABLE)) { filter = new Filter[3]; filter[2] = new
		 * Filter("AlwKnockOffbefClosure", 1, Filter.OP_EQUAL); }
		 */

		filter[0] = new Filter("FinReference", this.fromFinReference.getValue(), Filter.OP_EQUAL);
		filter[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
		this.referenceId.setFilters(filter);

		if (StringUtils.equals(getComboboxValue(knockOffFrom), RepayConstants.PAYTYPE_EXCESS)) {
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });

		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), RepayConstants.PAYTYPE_EMIINADV)) {

			this.referenceId.setModuleName("EMIInAdvance");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });

		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), RepayConstants.PAYTYPE_PAYABLE)) {

			this.referenceId.setModuleName("PayableAdvise");
			this.referenceId.setValueColumn("AdviseID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "AdviseID" });
		}

		this.referenceId.setMandatoryStyle(true);
		this.referenceId.setDescColumn("BalanceAmt");
		this.referenceId.setConstraint("");
		this.referenceId.setValue("", "");
		this.referenceId.setValueType(DataType.LONG);
		this.receiptAmount.setValue(BigDecimal.ZERO);
		Filter filter1[] = new Filter[2];
		filter1[0] = new Filter("FinReference", this.fromFinReference.getValue(), Filter.OP_EQUAL);
		filter1[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
		this.referenceId.setFilters(filter);

		if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.EXCESS)) {
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });

		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.EMIINADV)) {

			this.referenceId.setModuleName("EMIInAdvance");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });

		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.PAYABLE)) {

			this.referenceId.setModuleName("PayableAdvise");
			this.referenceId.setValueColumn("AdviseID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "AdviseID" });
		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.CASHCLT)) {

			this.referenceId.setModuleName("CASHCLT");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });
		} else if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.DSF)) {

			this.referenceId.setModuleName("DSF");
			this.referenceId.setValueColumn("ExcessID");
			this.referenceId.setDescColumn("BalanceAmt");
			this.referenceId.setValidateColumns(new String[] { "ExcessID" });
		}

	}

	/**
	 * 
	 */
	private void doSetFieldProperties() {

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptDate.setValue(appDate);

		this.receiptAmount.setTextBoxWidth(190);

		this.module = getArgument("module");
		if (StringUtils.equals(this.module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)) {
			isKnockOff = true;
			fillComboBox(this.knockOffFrom, "", PennantStaticListUtil.getKnockOffFromVlaues(),
					",EMIINADV,BOUNCE,SETTLE,TEXCESS,CASHCLT,DSF,");
			fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,EarlySettlement,");

			this.row_KnockOffFrom.setVisible(true);
			this.row_ReferenceId.setVisible(true);
			// this.btnValidate.setVisible(false);
			// this.row_receiptDues.setVisible(false);

			this.referenceId.setButtonDisabled(false);
			this.referenceId.setTextBoxWidth(155);
			this.referenceId.setMandatoryStyle(true);
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("FinReference");
			this.referenceId.setDescColumn("FinType");
			this.referenceId.setValidateColumns(new String[] { "FinReference" });

			this.label_title.setValue("Knock Off");

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

		this.toFinReference.setButtonDisabled(false);
		this.toFinReference.setTextBoxWidth(155);
		this.toFinReference.setMandatoryStyle(true);
		this.toFinReference.setModuleName("ReceiptFinanceMain");
		this.toFinReference.setValueColumn("FinReference");
		this.toFinReference.setDescColumn("FinType");
		this.toFinReference.setValidateColumns(new String[] { "FinReference" });

	}

	private void addFilter(Customer customer) {
		logger.debug("Entering ");

		if (customer != null && customer.getCustID() != 0) {
			this.custId = customer.getCustID();
			this.custCIF.setValue(customer.getCustCIF());
			this.fromFinReference.setValue("");
			this.fromFinReference.setObject("");
			this.fromFinReference.setFilters(new Filter[] { new Filter("CustId", this.custId, Filter.OP_EQUAL) });
		} else {
			this.fromFinReference.setValue("");
			this.fromFinReference.setObject("");
			this.custCIF.setValue("");
			this.fromFinReference.setFilters(null);
		}

		logger.debug("Leaving ");
	}

	/**
	 * When user changes textbox "custCIF"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$custCIF(Event event) throws Exception {
		customer = fetchCustomerDataByCIF();
		addFilter(customer);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();

		ErrorDetail errorDetail = null;
		String receiptPurpose = "";
		receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();

		if (receiptPurpose.equals(FinServiceEvent.EARLYRPY) || receiptPurpose.equals(FinServiceEvent.EARLYSETTLE)) {
			Date startDate = ((FinanceMain) this.fromFinReference.getObject()).getFinStartDate();
			errorDetail = financeMainService.rescheduleValidation(this.receiptDate.getValue(),
					Long.valueOf(this.fromFinReference.getId()), startDate);
		}
		if (isKnockOff) {
			BigDecimal receiptDues = this.receiptDues.getActualValue();
			BigDecimal knockOffAmount = this.receiptAmount.getActualValue();
			receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();
			if (FinServiceEvent.SCHDRPY.equals(receiptPurpose) && knockOffAmount.compareTo(receiptDues) > 0) {
				MessageUtil.showError(Labels.getLabel("label_Allocation_More_Due_KnockedOff"));
				return;
			}

			// validating value date for knock off
			Date valuedate = null;
			if (StringUtils.equals(RepayConstants.PAYTYPE_PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
				ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
				valuedate = ma.getValueDate();
			} else {
				FinExcessAmount fe = (FinExcessAmount) this.referenceId.getObject();
				valuedate = SysParamUtil.getAppDate();
			}

			if (this.receiptDate.getValue().compareTo(this.valueDate.getValue()) < 0) {
				MessageUtil.showError(Labels.getLabel("label_knockoffValuedate",
						new String[] { DateUtility.formatToShortDate(valuedate) }));
			}
		}

		if (errorDetail != null) {
			MessageUtil.showError(ErrorUtil.getErrorDetail(errorDetail));
		} else {
			validateReceiptData();
			if (receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails() != null
					&& receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().size() > 0) {
				MessageUtil.showError(ErrorUtil
						.getErrorDetail(receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().get(0)));
				return;
			}
			receiptData.setInitiation(true);
			doShowDialog();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "btnValidate" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		this.btnProceed.setDisabled(true);
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();
		validateReceiptData();

		if (isKnockOff) {
			Date valuedate = null;
			if (StringUtils.equals(RepayConstants.PAYTYPE_PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
				ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
				valuedate = ma.getValueDate();
			} else {
				FinExcessAmount fe = (FinExcessAmount) this.referenceId.getObject();
				valuedate = SysParamUtil.getAppDate();
			}

			if (this.receiptDate.getValue().compareTo(valuedate) < 0) {
				MessageUtil.showError(Labels.getLabel("label_knockoffValuedate",
						new String[] { DateUtility.formatToShortDate(valuedate) }));
				return;
			}
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails() != null
				&& receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().size() > 0) {
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

		logger.debug("Leaving " + event.toString());
	}

	public Customer fetchCustomerDataByCIF() {

		customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		receiptPurpose.setDisabled(false);
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			this.custId = 0;
			label_ReceiptPayment_CustomerName.setValue("");
		} else {
			customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());
			if (customer != null) {
				label_ReceiptPayment_CustomerName.setValue(customer.getCustShrtName());
				this.custId = customer.getCustID();
			}
		}

		return customer;
	}

	public Customer fetchCustomerDataByID(long custID) {
		customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		customer = this.customerDetailsService.checkCustomerByID(custID, TableType.MAIN_TAB.getSuffix());
		this.fromFinReference.setFilters(new Filter[] { new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL) });
		this.custId = customer.getCustID();
		this.custCIF.setValue(customer.getCustCIF());
		label_ReceiptPayment_CustomerName.setValue(customer.getCustShrtName());
		return customer;
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fromFinReference.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.receiptDate.setErrorMessage("");
		this.receiptAmount.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchCustCIF Search" button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.fromFinReference.setValue("");
		doClearMessage();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving " + event.toString());
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
		this.window_SelectCrossLoanKnockOffDialog.onClose();
		logger.debug("Leaving" + event.toString());
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");

		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		customer = (Customer) nCustomer;
		addFilter(customer);

		logger.debug("Leaving ");
	}

	public void resetFromLANDefaults(FinanceMain financeMain) {
		this.fromFinReference.setValue(financeMain.getFinReference());

		if (this.custCIF.getValue().isEmpty()) {
			customer = fetchCustomerDataByID(financeMain.getCustID());
		}

	}

	public void validateToFinReference(Event event, boolean isShowSearchList) {
		logger.debug("Entering " + event.toString());
		knockOffSettlement = false;
		this.toFinReference.setConstraint("");
		this.toFinReference.clearErrorMessage();
		receiptPurpose.setDisabled(false);
		Clients.clearWrongValue(toFinReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.window_SelectCrossLoanKnockOffDialog, "FinanceMain");
		} else {
			dataObject = this.toFinReference.getObject();
		}

		if (dataObject instanceof String) {
			this.toFinReference.setValue(dataObject.toString());
			this.toFinReference.setDescription("");
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.toFinReference.setValue(financeMain.getFinReference());
				// this.custCIF.setValue(String.valueOf(financeMain.getCustCIF()));
				toCustId = financeMain.getCustID();
				resetToLANDefaults(financeMain);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	public void resetToLANDefaults(FinanceMain financeMain) {
		this.toFinReference.setValue(financeMain.getFinReference());

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (StringUtils.equals(FinanceConstants.PRODUCT_GOLD, financeMain.getProductCategory())) {
			fillComboBox(receiptPurpose, FinServiceEvent.EARLYRPY, PennantStaticListUtil.getReceiptPurpose(),
					",SchdlRepayment, EarlySettlement, FeePayment,");
			receiptPurpose.setDisabled(true);
			this.receiptDate.setValue(appDate);
			this.receiptDate.setDisabled(true);
		} else {

			if (!financeMain.isFinIsActive() || DateUtility.compare(appDate, financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}

			if (!financeMain.isFinIsActive() || DateUtility.compare(appDate, financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			}
		}

	}

	private void validateReceiptData() {
		String loanReference = null;
		String tranBranch = null;
		loanReference = this.fromFinReference.getValue().toString();
		String eventCode = null;

		String recPurpose = getComboboxValue(this.receiptPurpose);
		if (isForeClosure) {
			recPurpose = FinServiceEvent.EARLYSETTLE;
		}

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

		long finID = ComponentUtil.getFinID(this.fromFinReference);
		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);

		FinReceiptHeader rch = new FinReceiptHeader();
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
		boolean isMatured = (DateUtil.compare(fm.getMaturityDate(), appDate) < 0) && fm.isFinIsActive();
		receiptData.setClosrMaturedLAN(isMatured && isForeClosure);
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

		if (isKnockOff) {
			rch.setReceiptMode(this.knockOffFrom.getSelectedItem().getValue().toString());
			rch.setKnockOffRefId(Long.valueOf(this.referenceId.getValue()));
			rch.setKnockOffType(RepayConstants.KNOCKOFF_TYPE_MANUAL);
		}

		schdData.setErrorDetails(new ArrayList<>(1));

		fsi.setReceivedDate(receiptDate.getValue());
		if (this.row_valueDate.isVisible()) {
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
		// Need to reset excess and manual advises whenever cross loan is done
		receiptData = receiptService.getExcessAndManualAdviseData(receiptData,
				ComponentUtil.getFinID(this.fromFinReference));

		receiptService.validateDual(receiptData);
	}

	private void doShowDialog() {
		logger.debug("Entering ");

		CrossLoanKnockOffHeader cLKHeader = new CrossLoanKnockOffHeader();
		cLKHeader.setFinReceiptData(receiptData);

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		final HashMap<String, Object> map = new HashMap<String, Object>();

		// set new record true
		setWorkflowDetails(rch.getFinType(), false);
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		// Cross Loan Bean Creation
		cLKHeader.setValueDate(this.receiptDate.getValue());
		cLKHeader.setPostDate(appDate);

		CrossLoanTransfer crossLoanTransfer = new CrossLoanTransfer();
		crossLoanTransfer.setCustCif(this.custCIF.getValue());
		crossLoanTransfer.setCustId(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		crossLoanTransfer.setFromFinReference(this.fromFinReference.getValidatedValue());
		crossLoanTransfer.setToFinReference(this.toFinReference.getValidatedValue());
		crossLoanTransfer.setTransferAmount(receiptData.getReceiptHeader().getReceiptAmount());

		if (StringUtils.equals(RepayConstants.RECEIPTTYPE_PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
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

		// setting workflow
		cLKHeader.setWorkflowId(getWorkFlowId());
		cLKHeader.setNewRecord(true);
		cLKHeader.setWorkflowId(getWorkFlowId());

		map.put("module", this.module);
		map.put("isKnockOff", isKnockOff);
		map.put("isForeClosure", false);
		map.put("isSettlementLoan", false);
		map.put("crossLoanHeader", cLKHeader);
		map.put("crossLoanKnockOff", crossLoanKnockOffListCtrl);
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/CrossLoanKnockOffDialog.zul", null,
				map);

		this.window_SelectCrossLoanKnockOffDialog.onClose();

		logger.debug("Leaving ");
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(FinServiceEvent.RECEIPT)) {
			String workflowTye = "";
			workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType, FinServiceEvent.CROSSLOANKNOCKOFF,
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

	public void onClick$btnSearchFinRef(Event event) {
		validateFinReference(event, true);
	}

	public void onFulfill$fromFinReference(Event event) {
		validateFinReference(event, false);
		this.btnValidate.setDisabled(false);
	}

	public void validateFinReference(Event event, boolean isShowSearchList) {
		logger.debug("Entering " + event.toString());
		knockOffSettlement = false;
		this.fromFinReference.setConstraint("");
		this.fromFinReference.clearErrorMessage();
		receiptPurpose.setDisabled(false);
		Clients.clearWrongValue(fromFinReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.window_SelectCrossLoanKnockOffDialog, "FinanceMain");
		} else {
			dataObject = this.fromFinReference.getObject();
		}

		if (dataObject instanceof String) {
			this.fromFinReference.setValue(dataObject.toString());
			this.fromFinReference.setDescription("");
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				finMain = financeMain;
				this.fromFinReference.setValue(financeMain.getFinReference());
				this.fromFinReference.setId(String.valueOf(financeMain.getFinID()));
				this.custCIF.setValue(String.valueOf(financeMain.getCustCIF()));
				resetDefaults(financeMain);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	public void doWriteComponentsToBean() throws Exception {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

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
			if (this.row_ReceiptPurpose.isVisible()) {
				if ("#".equals(getComboboxValue(this.receiptPurpose))) {
					throw new WrongValueException(this.receiptPurpose, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptPurpose.value") }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_receiptAmount.isVisible() && this.receiptAmount.isMandatory()) {
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

		validateBasicReceiptDate();

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

	private void validateBasicReceiptDate() {

		if (receiptDate.getValue() == null) {
			receiptDate.setValue(appDate);
		}

		if (receiptDate.getValue().compareTo(appDate) == 0) {
			return;
		}

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// Back Value checking will be with Application Date
		try {
			if (this.receiptDate.getValue().compareTo(appDate) > 0) {
				throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
						Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), appDate.toString() }));
			}

			daysBackValueAllowed = SysParamUtil.getValueAsInt("ALW_SP_BACK_DAYS");
			daysBackValue = DateUtility.getDaysBetween(this.receiptDate.getValue(), appDate);
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

	public void onFulfill$receiptAmount(Event event) throws InterruptedException {
		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);
		this.btnValidate.setDisabled(false);
		if (!isKnockOff) {
			this.btnProceed.setDisabled(true);
		}

		if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			MessageUtil.showError(Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Receipt Amount" }));
		}
		if (isKnockOff) {
			BigDecimal availableAmount = new BigDecimal(this.referenceId.getDescription());
			if (PennantApplicationUtil.formateAmount(receiptAmount, formatter).compareTo(availableAmount) > 0) {
				MessageUtil.showError(
						Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] { "Receipt Amount", "KnockOff Amount" }));
				this.receiptAmount.setValue(BigDecimal.ZERO);
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_Customer.value"),
				PennantRegularExpressions.REGEX_NUMERIC, true));

		this.fromFinReference
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_FromLoanReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));

		this.toFinReference
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_ToReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));

		if (isKnockOff) {
			this.referenceId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LoanClosurePayment_RefId.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.fromFinReference.setConstraint("");
		this.receiptDate.setConstraint("");
		this.receiptAmount.setConstraint("");
		this.toFinReference.setConstraint("");
		logger.debug("Leaving");
	}

	public void resetDefaults(FinanceMain financeMain) {
		this.fromFinReference.setValue(financeMain.getFinReference());

		if (this.custCIF.getValue().isEmpty()) {
			customer = fetchCustomerDataByID(financeMain.getCustID());
		}

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (financeMain.getAdvanceEMI().compareTo(BigDecimal.ZERO) > 0) {
			if (!financeMain.isFinIsActive()
					|| DateUtility.compare(SysParamUtil.getAppDate(), financeMain.getCalMaturity()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}

		} else {
			if (!financeMain.isFinIsActive()
					|| DateUtility.compare(SysParamUtil.getAppDate(), financeMain.getMaturityDate()) >= 0) {
				fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
						",EarlyPayment, EarlySettlement, FeePayment,");
				receiptPurpose.setDisabled(true);
			} else if (StringUtils.equals(this.module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)) {
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
						",FeePayment,EarlySettlement,");
			} else {
				fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			}
		}

		if (!financeMain.isFinIsActive()
				|| DateUtility.compare(SysParamUtil.getAppDate(), financeMain.getMaturityDate()) >= 0) {
			fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
					",EarlyPayment, EarlySettlement, FeePayment,");
			receiptPurpose.setDisabled(true);
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

	public CrossLoanKnockOffListCtrl getCrossLoanKnockOffListCtrl() {
		return crossLoanKnockOffListCtrl;
	}

	public void setCrossLoanKnockOffListCtrl(CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl) {
		this.crossLoanKnockOffListCtrl = crossLoanKnockOffListCtrl;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}