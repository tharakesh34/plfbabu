package com.pennant.webui.financemanagement.paymentMode;

import java.math.BigDecimal;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
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
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.SubReceiptPaymentModes;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectReceiptPaymentDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectReceiptPaymentDialogCtrl.class);

	protected Window window_SelectReceiptPaymentDialog;
	protected Textbox custCIF;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox tranBranch;
	protected Combobox receiptMode;
	protected Combobox knockOffFrom;
	protected Combobox receiptChannel;
	protected Combobox subReceiptMode;
	protected Combobox receiptPurpose;
	protected CurrencyBox receiptAmount;
	protected CurrencyBox tDSAmount;
	protected CurrencyBox receiptDues;
	protected Datebox receiptDate;
	protected Datebox valueDate;
	protected ExtendedCombobox referenceId;
	protected Combobox sourceofFund;
	protected Combobox closureType;

	protected Button btnProceed;
	protected Button btnValidate;
	protected Button btnSearchCustCIF;
	protected Button btnSearchFinRef;

	protected Row row_subReceiptMode;
	protected Row row_valueDate;
	protected Row row_ReceiptMode;
	protected Row row_intTillDate;
	protected Row row_ReceiptPurpose;
	protected Row row_ReceiptChannel;
	protected Row row_receiptAmount;
	protected Row row_tDSAmount;
	protected Row row_receiptDues;
	protected Label ReceiptPayment;
	protected Label label_ReceiptPayment_CustomerName;

	protected Row row_KnockOffFrom;
	protected Row row_ReferenceId;

	protected Label label_title;
	protected Row rowClosureType;
	@Autowired
	public transient ReceiptService receiptService;
	public transient SecurityUserDAO securityUserDAO;

	@Autowired
	private transient FinanceMainService financeMainService;

	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	protected long custId = Long.MIN_VALUE;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected JdbcSearchObject<FinanceMain> searchObject;

	protected ReceiptListCtrl receiptListCtrl;

	protected FinReceiptData receiptData = new FinReceiptData();
	private transient WorkFlowDetails workFlowDetails = null;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private boolean isKnockOff = false;
	private boolean isForeClosure = false;

	private SubReceiptPaymentModes subReceiptPaymentModes;

	private FinanceEnquiry financeEnquiry;
	private Customer customer;
	private FinanceMainDAO financeMainDAO;
	private String productCategory;

	// private DueData dueData;
	private int daysBackValueAllowed, daysBackValue;
	private String module;
	private int formatter = 2;
	Date appDate = SysParamUtil.getAppDate();
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	protected FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceType finType;
	private Label label_ReceiptPayment_ReceiptDate;
	private Label label_ReceiptPayment_ValueDate;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;

	/**
	 * default constructor.<br>
	 */
	public SelectReceiptPaymentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	public void onCreate$window_SelectReceiptPaymentDialog(Event event) {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_SelectReceiptPaymentDialog);

		if (arguments.containsKey("receiptListCtrl")) {
			this.receiptListCtrl = (ReceiptListCtrl) arguments.get("receiptListCtrl");
			setReceiptListCtrl(this.receiptListCtrl);
		} else {
			setReceiptListCtrl(null);
		}

		doSetFieldProperties();
		this.valueDate.setDisabled(true);
		this.valueDate.setVisible(false);
		if (!isKnockOff && !isForeClosure) {
			this.btnProceed.setDisabled(true);
		}
		this.btnValidate.setDisabled(true);
		setTranBranch();
		this.window_SelectReceiptPaymentDialog.doModal();
		this.receiptAmount.setSclass("");
		logger.debug("Leaving " + event.toString());
	}

	private void setTranBranch() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().getUserDetails().getSecurityUser().isAccessToAllBranches();

		if (getUserWorkspace().getUserDetails().getSecurityUser().isAccessToAllBranches()) {
			this.tranBranch.setReadonly(false);
		} else {
			this.tranBranch.setReadonly(true);
		}
		this.tranBranch.setValue(getUserWorkspace().getLoggedInUser().getBranchCode());
		this.tranBranch
				.setDescription(getUserWorkspace().getUserDetails().getSecurityUser().getLovDescUsrBranchCodeName());
		logger.debug(Literal.LEAVING);

	}

	public void onChange$receiptPurpose(Event event) {
		loadValueDate();
		this.btnValidate.setDisabled(false);
		if (!isKnockOff && !isForeClosure) {
			this.btnProceed.setDisabled(true);
			this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);

		}
		this.receiptDues.setValue(BigDecimal.ZERO);
		this.receiptData.setExcessAvailable(BigDecimal.ZERO);
		this.rowClosureType.setVisible(false);
		if (FinServiceEvent.EARLYSETTLE.equals(this.receiptPurpose.getSelectedItem().getValue().toString())) {
			this.rowClosureType.setVisible(true);
			fillComboBox(this.closureType, "", ClosureType.getTypes());
			closureType();
		}
	}

	public void loadValueDate() {
		this.row_valueDate.setVisible(false);
		this.valueDate.setValue(this.receiptDate.getValue());

		long finID = ComponentUtil.getFinID(this.finReference);
		Date receiptDt = this.receiptDate.getValue();
		Date appDate = SysParamUtil.getAppDate();
		String recPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();

		if (isKnockOff) {
			fillValueDate(receiptDt, finID, appDate);
		}

		if (!FinServiceEvent.EARLYSETTLE.equals(recPurpose)) {
			return;
		}

		String recMode = this.receiptMode.getSelectedItem().getValue().toString();

		if (!StringUtils.equals(recMode, ReceiptMode.CHEQUE) && !StringUtils.equals(ReceiptMode.DD, recMode)) {
			return;
		}

		int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
		this.valueDate.setValue(DateUtil.addDays(this.receiptDate.getValue(), defaultClearingDays));
		this.valueDate.setVisible(true);
		this.row_valueDate.setVisible(true);
	}

	public void onChange$receiptDate(Event event) {
		loadValueDate();
		this.btnValidate.setDisabled(false);
	}

	public void onChange$knockOffFrom(Event event) {

		this.referenceId.setMandatoryStyle(true);
		this.referenceId.setDescColumn("BalanceAmt");
		this.referenceId.setConstraint("");
		this.referenceId.setValue("", "");
		this.referenceId.setValueType(DataType.LONG);
		this.receiptAmount.setValue(BigDecimal.ZERO);
		Filter filter[] = new Filter[2];
		filter[0] = new Filter("FinReference", this.finReference.getValue(), Filter.OP_EQUAL);
		filter[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
		this.referenceId.setFilters(filter);

		if (StringUtils.equals(getComboboxValue(knockOffFrom), ReceiptMode.EXCESS)) {
			this.referenceId.setModuleName("FinExcess");
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

		this.tranBranch.setMandatoryStyle(true);
		this.tranBranch.setTextBoxWidth(155);
		this.tranBranch.setReadonly(false);
		this.referenceId.setValueType(DataType.LONG);
		this.tranBranch.setModuleName("Branch");
		this.tranBranch.setValueColumn("BranchCode");
		this.tranBranch.setDescColumn("BranchDesc");
		this.tranBranch.setValidateColumns(new String[] { "BranchCode" });

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptDate.setValue(appDate);

		this.receiptAmount.setTextBoxWidth(190);
		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.receiptAmount.setProperties(false, formatter);
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));

		this.tDSAmount.setTextBoxWidth(190);
		this.tDSAmount.setProperties(false, formatter);
		this.tDSAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));

		fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptPaymentModes(), ",PRESENT,");
		fillComboBox(this.receiptChannel, "", PennantStaticListUtil.getReceiptChannels(), "");
		fillComboBox(this.subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), "");
		fillComboBox(this.sourceofFund, "", PennantAppUtil.getFieldCodeList("SOURCE"), "");

		this.module = getArgument("module");
		if (FinanceConstants.CLOSURE_MAKER.equals(this.module)) {
			fillComboBox(this.closureType, "", ClosureType.getTypes());
		}

		if (StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)) {
			isKnockOff = true;
			fillComboBox(this.knockOffFrom, "", PennantStaticListUtil.getKnockOffFromVlaues(), "");
			fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,EarlySettlement,");

			this.row_ReceiptMode.setVisible(false);
			this.row_subReceiptMode.setVisible(false);
			this.row_ReceiptChannel.setVisible(false);
			this.row_KnockOffFrom.setVisible(true);
			this.row_ReferenceId.setVisible(true);
			this.btnProceed.setDisabled(true);
			this.rowClosureType.setVisible(false);
			/*
			 * this.btnValidate.setVisible(false); this.row_receiptDues.setVisible(false);
			 */

			this.referenceId.setButtonDisabled(false);
			this.referenceId.setTextBoxWidth(155);
			this.referenceId.setMandatoryStyle(true);
			this.referenceId.setModuleName("Excess");
			this.referenceId.setValueColumn("FinReference");
			this.referenceId.setDescColumn("FinType");
			this.referenceId.setValidateColumns(new String[] { "FinReference" });

			this.label_title.setValue("Knock Off");

		} else if (StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)
				|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)) {
			isForeClosure = true;
			this.label_title.setValue("Loan Closure");
			this.row_subReceiptMode.setVisible(false);
			this.row_ReceiptChannel.setVisible(false);
			this.row_receiptAmount.setVisible(false);
			this.row_tDSAmount.setVisible(false);
			this.btnValidate.setVisible(false);
			this.row_receiptDues.setVisible(false);
			this.row_ReceiptMode.setVisible(false);
			this.row_ReceiptPurpose.setVisible(false);
			this.rowClosureType.setVisible(true);
			this.label_ReceiptPayment_ValueDate.setValue("Closure Date");
			this.label_ReceiptPayment_ReceiptDate.setValue("Closure Date");
			fillComboBox(this.receiptPurpose, FinServiceEvent.EARLYSETTLE, PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,SchdlRepayment,EarlyPayment,");
		} else {
			fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptPaymentModes(), ",DIGITAL,PRESENT,");
		}
		if (isKnockOff) {
			this.finReference.setFilters((new Filter[] { new Filter("WriteOffLoan", 0, Filter.OP_EQUAL) }));
			this.receiptAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
		} else {
			this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		}
		this.receiptDues.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptDues.setTextBoxWidth(190);
		this.receiptDues.setDisabled(true);

		this.finReference.setButtonDisabled(false);
		this.finReference.setTextBoxWidth(155);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("ReceiptFinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		if (isForeClosure) {
			Filter filter[] = new Filter[2];
			filter[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filter[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
			this.finReference.setFilters(filter);
		}

		if (isForeClosure) {
			this.finReference.setFilters((new Filter[] { new Filter("FinIsActive", 1, Filter.OP_EQUAL) }));
		}
	}

	private void addFilter(Customer customer) {
		logger.debug("Entering ");

		this.finReference.setValue("");
		this.finReference.setObject("");
		this.custCIF.setValue("");
		this.finReference.setFilters(null);

		if (customer == null) {
			return;
		}

		this.custId = customer.getCustID();
		this.custCIF.setValue(customer.getCustCIF());

		Filter[] finreference = new Filter[1];

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			finreference[0] = new Filter("CustCoreBank", customer.getCustCoreBank(), Filter.OP_EQUAL);
		} else {
			finreference[0] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
		}

		if (customer.getCustID() <= 0 || StringUtils.isBlank(customer.getCustCoreBank())) {
			this.finReference.setFilters(null);

		} else {
			this.finReference.setFilters(finreference);

		}

		logger.debug("Leaving ");
	}

	/**
	 * When user changes textbox "custCIF"
	 * 
	 * @param event
	 */
	public void onChange$custCIF(Event event) {
		customer = fetchCustomerDataByCIF();
		addFilter(customer);
	}

	/**
	 * When user changes textbox "custCIF"
	 * 
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		this.row_subReceiptMode.setVisible(false);
		this.row_ReceiptChannel.setVisible(false);

		String receiptMode = this.receiptMode.getSelectedItem().getValue().toString();
		int channelIdx = 0;

		String receiptPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();

		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equals(receiptMode)) {
			this.row_subReceiptMode.setVisible(true);
			String exlcudeValues = SysParamUtil.getValueAsString("EXCLUDE_SUB_RECEIPT_MODE_VALUE");
			if (exlcudeValues == null) {
				exlcudeValues = "";
			}
			if (subReceiptPaymentModes != null) {
				fillComboBox(subReceiptMode, "", subReceiptPaymentModes.getSubReceiptPaymentModes(), exlcudeValues);
			} else if (!FinanceConstants.PRODUCT_CD.equals(productCategory)) {
				fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), ",RTRNGDS,");
			} else {
				if (!FinServiceEvent.EARLYRPY.equals(receiptPurpose)) {
					exlcudeValues = exlcudeValues.concat(",RTRNGDS,");
				}
				fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), exlcudeValues);
			}
		} else {
			fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), ",ESCROW,");
		}
		loadValueDate();
		if (!StringUtils.equals(receiptMode, DisbursementConstants.PAYMENT_TYPE_ONLINE)
				&& !StringUtils.equals(receiptMode, PennantConstants.List_Select)) {
			this.row_ReceiptChannel.setVisible(true);
			channelIdx = this.receiptChannel.getSelectedIndex();
			if (channelIdx > 0) {
				this.receiptChannel.setSelectedIndex(channelIdx);
			} else {
				this.receiptChannel.setSelectedIndex(2);
			}

		}
	}

	public void closureType() {
		int channelIdx = this.closureType.getSelectedIndex();
		Date maturitydate = financeMainDAO.getMaturityDatebyFinID(ComponentUtil.getFinID(this.finReference));

		if (maturitydate != null) {
			this.rowClosureType.setVisible(true);

			String excludeFields = ",CLOSURE,SETTLEMENT,";
			if (maturitydate.compareTo(appDate) <= 0) {
				excludeFields = ",FORE-CLOSURE,CANCEL,SETTLEMENT,";
			}

			fillComboBox(this.closureType, "", ClosureType.getTypes(), excludeFields);
			this.closureType.setSelectedIndex(1);

			if (channelIdx > 0) {
				this.closureType.setSelectedIndex(channelIdx);
			}
		}
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();

		ErrorDetail errorDetail = null;
		ReceiptPurpose receiptPurpose = null;
		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);

		long finID = ComponentUtil.getFinID(this.finReference);

		if (!FinanceConstants.CLOSURE_MAKER.equals(this.module)) {
			errorDetail = receiptService.validateThreshHoldLimit(receiptData.getReceiptHeader(),
					this.receiptDues.getActualValue());

			if (errorDetail != null) {
				MessageUtil.showError(errorDetail.getMessage());
				return;
			}
		}

		if (!((FinanceMain) this.finReference.getObject()).isFinIsActive()) {
			ErrorDetail errorDetails = null;
			String[] valueParm = new String[1];
			String[] errParm = new String[1];
			errParm[0] = ((FinanceMain) this.finReference.getObject()).getFinReference();
			valueParm[0] = "";
			errorDetails = ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "WFEE11", errParm, valueParm), "EN");
			if (errorDetails != null) {
				String errMsgs[] = errorDetails.getError().split("%");
				final String msg = errMsgs[0];
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return;
				}
			}
		}

		if (isKnockOff) {
			BigDecimal availableAmount = BigDecimal.ZERO;
			Date valuedate = null;

			if (!StringUtils.isBlank(this.referenceId.getDescription())) {
				availableAmount = new BigDecimal(this.referenceId.getDescription());
			}
			if (PennantApplicationUtil.formateAmount(receiptAmount, formatter).compareTo(availableAmount) > 0) {
				this.receiptAmount.setValue(BigDecimal.ZERO);
				return;
			}

			if (StringUtils.equals(RepayConstants.PAYTYPE_PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
				ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
				valuedate = ma.getValueDate();
			} else {
				FinExcessAmount fe = (FinExcessAmount) this.referenceId.getObject();
				valuedate = fe.getValueDate();
			}

			if (valuedate != null && this.receiptDate.getValue().compareTo(valuedate) < 0) {
				MessageUtil.showError(Labels.getLabel("label_knockoffValuedate",
						new String[] { DateUtil.formatToShortDate(valuedate) }));
			}
		}
		if (isForeClosure) {
			receiptPurpose = ReceiptPurpose.EARLYSETTLE;

		} else {
			receiptPurpose = ReceiptPurpose.purpose(this.receiptPurpose.getSelectedItem().getValue());
		}

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			int count = finAdvancePaymentsService.getStatusCountByFinRefrence(finID);
			if (count > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = this.finReference.getValue();
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("90508", valueParm)));
				return;
			}
		}

		if (receiptPurpose == ReceiptPurpose.EARLYRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			Date startDate = ((FinanceMain) this.finReference.getObject()).getFinStartDate();
			errorDetail = financeMainService.rescheduleValidation(this.receiptDate.getValue(), finID, startDate);
			errorDetail = receiptService.checkInprocessReceipts(finID, receiptPurpose);
		}

		errorDetail = receiptService.getWaiverValidation(finID, this.receiptPurpose.getSelectedItem().getValue(),
				valueDate.getValue());

		FinanceMain fm = (FinanceMain) this.finReference.getObject();
		if (FinServiceEvent.EARLYRPY.equals(receiptPurpose) && fm.isManualSchedule()) {

			FinReceiptHeader frh = receiptService
					.getInititatedReceipts(StringUtils.trimToEmpty(this.finReference.getValue()));
			if (frh != null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Part payment has already been initiated for this loan number: " + frh.getReference()
						+ ", Receipt ID: " + frh.getReceiptID() + ", Receipt Date: "
						+ DateUtil.formatToShortDate(frh.getReceiptDate()) + ". Please process the initiated receipt.";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("92021", valueParm));
			}
		}

		// Validate Loan is INPROGRESS in WRITEOFF or NOT ?
		String rcdMaintainSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID);
		if (FinServiceEvent.WRITEOFF.equals(rcdMaintainSts)) {
			String[] valueParm = new String[1];
			valueParm[0] = rcdMaintainSts;
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("LMS001", valueParm));
		}

		/*
		 * if (isKnockOff) { BigDecimal receiptDues = this.receiptDues.getActualValue(); BigDecimal knockOffAmount =
		 * this.receiptAmount.getActualValue(); String rcptPurpose = this.receiptPurpose.getSelectedItem().getValue();
		 * if (FinServiceEvent.SCHDRPY.equals(rcptPurpose) && knockOffAmount.compareTo(receiptDues) > 0) {
		 * MessageUtil.showError(Labels.getLabel("label_Allocation_More_Due_KnockedOff")); return; } }
		 */

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

			FinReceiptHeader rch = receiptData.getReceiptHeader();

			if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
				if (!FinanceConstants.CLOSURE_APPROVER.equals(module)
						&& !FinanceConstants.CLOSURE_MAKER.equals(module)) {
					if (receiptService.doProcessTerminationExcess(receiptData)) {
						String msg = "Receipt Amount is insuffient to settle the loan, do you wish to move the receipt amount to termination excess?";
						if (MessageUtil.YES == MessageUtil.confirm(msg)) {
							receiptData.getReceiptHeader().setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_TEXCESS);
							receiptData.setExcessType(RepayConstants.EXCESSADJUSTTO_TEXCESS);
						}
					}
				}
			}

			doShowDialog();
		}
		logger.debug("Leaving " + event.toString());
	}

	private ErrorDetail validateEarlySettle() {
		ErrorDetail errorDetail = null;
		String receiptPurpose = "";
		long finID = ComponentUtil.getFinID(this.finReference);

		if (isForeClosure) {
			receiptPurpose = FinServiceEvent.EARLYSETTLE;
		} else {
			receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();
		}

		if (receiptPurpose.equals(FinServiceEvent.EARLYRPY) || receiptPurpose.equals(FinServiceEvent.EARLYSETTLE)) {
			Date startDate = ((FinanceMain) this.finReference.getObject()).getFinStartDate();
			errorDetail = financeMainService.rescheduleValidation(this.receiptDate.getValue(), finID, startDate);
		}

		return errorDetail;
	}

	/**
	 * When user clicks on button "btnValidate" button
	 * 
	 * @param event
	 */
	public void onClick$btnValidate(Event event) {
		this.btnProceed.setDisabled(true);
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();

		ErrorDetail errorDetail = validateEarlySettle();

		if (errorDetail != null) {
			MessageUtil.showError(ErrorUtil.getErrorDetail(errorDetail));
			return;
		}

		validateReceiptData();
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData fsd = financeDetail.getFinScheduleData();
		FinanceMain finMain = fsd.getFinanceMain();

		if (StringUtils.equals(this.receiptPurpose.getSelectedItem().getValue(), FinServiceEvent.EARLYSETTLE)
				&& DateUtil.compare(valueDate.getValue(), finMain.getMaturityDate()) > 0) {
			MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("RM0001", null)));
			return;
		}

		errorDetail = receiptService.getWaiverValidation(finMain.getFinID(),
				this.receiptPurpose.getSelectedItem().getValue(), valueDate.getValue());

		if (errorDetail != null) {
			MessageUtil.showError(ErrorUtil.getErrorDetail(errorDetail));
			return;
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails() != null
				&& receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().size() > 0)

		{
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().get(0)));
			return;
		}

		try {
			receiptData = receiptService.calcuateDues(receiptData);
		} catch (AppException e) {
			MessageUtil.showError(e.getMessage());
			return;
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal pastDues = rch.getTotalPastDues().getTotalDue();
		BigDecimal totalBounces = rch.getTotalBounces().getTotalDue();
		BigDecimal totalRcvAdvises = rch.getTotalRcvAdvises().getTotalDue();
		BigDecimal totalFees = rch.getTotalFees().getTotalDue();
		BigDecimal excessAvailable = receiptData.getExcessAvailable();
		BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises).add(totalFees).subtract(excessAvailable);

		ErrorDetail error = receiptService.validateThreshHoldLimit(rch, totalDues);
		if (error != null) {
			MessageUtil.showError(error.getMessage());
			return;
		}

		if (BigDecimal.ZERO.compareTo(totalDues) > 0) {
			totalDues = BigDecimal.ZERO;
		}
		this.receiptDues.setValue(PennantApplicationUtil.formateAmount(totalDues, formatter));

		if (isKnockOff) {
			BigDecimal receiptDues = this.receiptDues.getActualValue();
			BigDecimal knockOffAmount = this.receiptAmount.getActualValue();
			String receiptPurpose = this.receiptPurpose.getSelectedItem().getValue();
			Date valuedate = null;
			if (FinServiceEvent.SCHDRPY.equals(receiptPurpose) && knockOffAmount.compareTo(receiptDues) > 0) {
				MessageUtil.showError(Labels.getLabel("label_Allocation_More_Due_KnockedOff"));
				return;
			}

			if (StringUtils.equals(RepayConstants.PAYTYPE_PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
				ManualAdvise ma = (ManualAdvise) this.referenceId.getObject();
				valuedate = ma.getValueDate();
			} else {
				FinExcessAmount fe = (FinExcessAmount) this.referenceId.getObject();
				valuedate = fe.getValueDate();
			}

			if (valuedate != null && this.receiptDate.getValue().compareTo(valuedate) < 0) {
				MessageUtil.showError(Labels.getLabel("label_knockoffValuedate",
						new String[] { DateUtil.formatToShortDate(valuedate) }));
				return;
			}
		}

		this.receiptDues.setDisabled(true);
		this.btnProceed.setDisabled(false);
		this.btnValidate.setDisabled(true);
		this.receiptAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
	}

	public Customer fetchCustomerDataByCIF() {

		customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			this.custId = 0;
			label_ReceiptPayment_CustomerName.setValue("");
		} else {
			customer = this.customerDetailsService.getCustomer(cif);
			if (customer != null) {
				label_ReceiptPayment_CustomerName.setValue(customer.getCustShrtName());
				this.custId = customer.getCustID();
			} else {
				MessageUtil.showError("Invalid Customer Please Select valid Customer");
			}
		}

		return customer;
	}

	public Customer fetchCustomerDataByID(long custID) {
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		Customer customer = this.customerDetailsService.getCustomer(custID);

		this.finReference.setFilters(new Filter[] { new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL) });

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
		this.receiptMode.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.subReceiptMode.setErrorMessage("");
		this.receiptChannel.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.receiptDate.setErrorMessage("");
		this.receiptAmount.setErrorMessage("");
		this.closureType.setErrorMessage("");
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
		this.finReference.setValue("");
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
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
		this.window_SelectReceiptPaymentDialog.onClose();
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

	private void validateReceiptData() {
		String loanReference = null;
		String tranBranch = null;
		loanReference = this.finReference.getValue().toString();
		tranBranch = this.tranBranch.getValue().toString();
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

		long finID = ComponentUtil.getFinID(this.finReference);
		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);

		FinReceiptHeader rch = new FinReceiptHeader();

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainForLMSEvent(finID);
		fm.setAppDate(appDate);
		fm.setReceiptPurpose(rptPurpose.code());
		rch.setValueDate(this.receiptDate.getValue());
		rch.setClosureThresholdLimit(finReceiptHeaderDAO.getClosureAmountByFinType(fm.getFinType()));
		if (isKnockOff) {
			rch.setKnockOffRefId(Long.valueOf(this.referenceId.getValue()));
		}

		receiptData.setReceiptHeader(rch);

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
		if (fm.isUnderSettlement()) {
			fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_SETTLEMENT);
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
		} else {
			rch.setReceiptMode(this.receiptMode.getSelectedItem().getValue());
		}

		if (this.subReceiptMode.getSelectedIndex() >= 0) {
			rch.setSubReceiptMode(this.subReceiptMode.getSelectedItem().getValue().toString());
		} else {
			rch.setSubReceiptMode(PennantConstants.List_Select);
		}

		if (this.receiptChannel.getSelectedIndex() >= 0) {
			rch.setReceiptChannel(this.receiptChannel.getSelectedItem().getValue().toString());
		} else {
			rch.setReceiptChannel(PennantConstants.List_Select);
		}

		if (this.row_tDSAmount.isVisible()) {
			rch.setTdsAmount(PennantApplicationUtil.unFormateAmount(this.tDSAmount.getActualValue(), formatter));
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
		rch.setSourceofFund(getComboboxValue(this.sourceofFund));

		receiptService.validateDual(receiptData);

		if (this.closureType.getSelectedIndex() >= 0) {
			rch.setClosureType(this.closureType.getSelectedItem().getValue());
		} else {
			rch.setClosureType(PennantConstants.List_Select);
		}
	}

	private void doShowDialog() {
		logger.debug("Entering ");

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		final Map<String, Object> map = new HashMap<>();

		boolean isPartPayment = false;
		if (FinServiceEvent.EARLYRPY.equalsIgnoreCase(rch.getReceiptPurpose())) {
			isPartPayment = true;
		}

		// set new record true
		setWorkflowDetails(rch.getFinType(), false);
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		// setting workflow
		rch.setWorkflowId(getWorkFlowId());
		rch.setNewRecord(true);
		rch.setWorkflowId(getWorkFlowId());

		map.put("module", this.module);
		map.put("receiptData", this.receiptData);
		map.put("isKnockOff", isKnockOff);
		map.put("isForeClosure", isForeClosure);
		map.put("isPartPayment", isPartPayment);
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul", null, map);

		this.window_SelectReceiptPaymentDialog.onClose();

		logger.debug("Leaving ");

	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(FinServiceEvent.RECEIPT)) {
			String workflowTye = "";
			if (isKnockOff) {
				workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType,
						FinServiceEvent.RECEIPTKNOCKOFF, isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION
								: PennantConstants.WORFLOW_MODULE_FINANCE);
			} else if (isForeClosure) {
				workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType,
						FinServiceEvent.RECEIPTFORECLOSURE, isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION
								: PennantConstants.WORFLOW_MODULE_FINANCE);
			} else {
				workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType, FinServiceEvent.RECEIPT,
						isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION
								: PennantConstants.WORFLOW_MODULE_FINANCE);
			}
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

	public void onFulfill$finReference(Event event) {
		validateFinReference(event, false);
		this.btnValidate.setDisabled(false);

		Date receiptDt = appDate;
		long finID = ComponentUtil.getFinID(this.finReference);

		this.referenceId.setMandatoryStyle(true);
		this.referenceId.setDescColumn("BalanceAmt");
		this.referenceId.setConstraint("");
		this.referenceId.setValue("", "");
		this.referenceId.setValueType(DataType.LONG);
		this.receiptAmount.setValue(BigDecimal.ZERO);
		Filter filter[] = new Filter[2];
		filter[0] = new Filter("FinReference", this.finReference.getValue(), Filter.OP_EQUAL);
		filter[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
		this.referenceId.setFilters(filter);

		if (FinanceConstants.CLOSURE_MAKER.equals(this.module)) {
			Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, appDate);
			List<FinExcessAmount> excessAmounts = finExcessAmountDAO.getExcessAmountsByRef(finID);
			Date maxValueDate = financeRepaymentsDAO.getMaxValueDate(finID);

			if (CollectionUtils.isNotEmpty(excessAmounts)) {
				FinExcessAmount fea = excessAmounts.get(excessAmounts.size() - 1);
				if (fea != null && fea.getValueDate() != null) {
					receiptDt = fea.getValueDate();

					if (DateUtil.compare(receiptDt, schDate) < 0) {
						receiptDt = schDate;
					}

					if (DateUtil.compare(receiptDt, maxValueDate) < 0) {
						receiptDt = maxValueDate;
					}
					this.receiptDate.setValue(receiptDt);
					this.receiptDate.setDisabled(true);
				}
			}

			closureType();
		}
	}

	public void onFulfill$referenceId(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Date receiptDt = appDate;
		long finID = ComponentUtil.getFinID(this.finReference);

		fillValueDate(receiptDt, finID, appDate);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void validateFinReference(Event event, boolean isShowSearchList) {
		logger.debug("Entering " + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();
		Clients.clearWrongValue(finReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.window_SelectReceiptPaymentDialog, "FinanceMain");
		} else {
			dataObject = this.finReference.getObject();
		}

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
			this.receiptDate.setValue(appDate);
			setFinType(null);
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.finReference.setValue(financeMain.getFinReference());
				String finIsActive = financeMain.isFinIsActive() == true ? "[Active]" : "[InActive]";
				if (StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)
						|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)) {
					this.finReference.setDescription(financeMain.getFinType());
				} else {
					this.finReference.setDescription(financeMain.getFinType() + " - " + finIsActive);
				}
				this.custCIF.setValue(String.valueOf(financeMain.getCustCIF()));
				if (isForeClosure && financeMain.getMaturityDate().compareTo(appDate) <= 0) {
					this.receiptDate.setValue(financeMain.getMaturityDate());
				}
				resetDefaults(financeMain);
				if (FinanceConstants.RECEIPT_MAKER.equals(this.module)
						&& PennantConstants.TDS_MANUAL.equalsIgnoreCase(financeMain.getTdsType())) {
					this.row_tDSAmount.setVisible(true);
					this.tDSAmount.setValue(BigDecimal.ZERO);
				}
			} else {
				this.receiptPurpose.setDisabled(false);
				fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
				this.row_tDSAmount.setVisible(false);
				this.tDSAmount.setValue(BigDecimal.ZERO);
				this.receiptDate.setValue(appDate);
				if (this.closureType.isVisible() && FinanceConstants.RECEIPT_MAKER.equals(this.module)) {
					this.rowClosureType.setVisible(false);
				} else {
					this.closureType.setDisabled(false);
					fillComboBox(this.closureType, "", ClosureType.getTypes());
				}
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	public void doWriteComponentsToBean() {
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
			if (StringUtils.isEmpty(this.finReference.getValue())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_LoanReference.value") }));

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
			if (row_ReceiptMode.isVisible()) {
				if ("#".equals(getComboboxValue(this.receiptMode))) {
					throw new WrongValueException(this.receiptMode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptMode.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_subReceiptMode.isVisible()) {
				if ("#".equals(getComboboxValue(this.subReceiptMode))) {
					throw new WrongValueException(this.subReceiptMode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_SubReceiptMode.value") }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_ReceiptChannel.isVisible()) {
				if ("#".equals(getComboboxValue(this.receiptChannel))) {
					throw new WrongValueException(this.receiptChannel, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptChannel.value") }));
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
					wve.add(new WrongValueException(this.receiptAmount.getCcyTextBox(),
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Receipt Amount" })));
				}

				if ("EarlySettlement".equals(this.receiptPurpose.getSelectedItem().getValue())) {
					if (this.receiptDues.getValidateValue().compareTo(this.receiptAmount.getValidateValue()
							.add(receiptData.getReceiptHeader().getClosureThresholdLimit())) > 0) {
						wve.add(new WrongValueException(this.receiptAmount.getCcyTextBox(),
								"Receipt Amount should greater than or equal to Receipt Dues."));
					}
				}

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_KnockOffFrom.isVisible()) {
				if (StringUtils.isEmpty(this.knockOffFrom.getValue())) {
					throw new WrongValueException(this.knockOffFrom, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_LoanClosurePayment_kncockoffFrom.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rowClosureType.isVisible() && "#".equals(getComboboxValue(this.closureType))) {
				throw new WrongValueException(this.closureType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ReceiptPayment_ClosureType.value") }));
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
	}

	private void validateBasicReceiptDate() {

		if (receiptDate.getValue() == null) {
			receiptDate.setValue(appDate);
		}

		if (receiptDate.getValue().compareTo(appDate) == 0) {
			return;
		}

		List<WrongValueException> wve = new ArrayList<>();
		Object object = this.finReference.getObject();
		boolean isMatured = false;
		Date maturityDate = null;
		if (isForeClosure && object != null) {
			FinanceMain fm = (FinanceMain) object;
			maturityDate = fm.getMaturityDate();
			isMatured = DateUtil.compare(maturityDate, appDate) < 0;
		}

		// Back Value checking will be with Application Date
		try {
			if (this.receiptDate.getValue().compareTo(appDate) > 0) {
				throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
						Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), appDate.toString() }));
			}

			if (!isMatured) {
				daysBackValueAllowed = SysParamUtil.getValueAsInt("ALW_SP_BACK_DAYS");
				daysBackValue = DateUtil.getDaysBetween(this.receiptDate.getValue(), appDate);
				if (daysBackValue >= daysBackValueAllowed) {
					throw new WrongValueException(this.receiptDate,
							Labels.getLabel("DATE_ALLOWED_ON_AFTER",
									new String[] { Labels.getLabel("label_SchedulePayment_ReceiptDate.value"),
											DateUtil.addDays(appDate, -daysBackValueAllowed).toString() }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		long finID = ComponentUtil.getFinID(finReference);

		Date maxValueDate = financeRepaymentsDAO.getMaxValueDate(finID);

		if (DateUtil.compare(maxValueDate, this.receiptDate.getValue()) > 0) {
			throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_AFTER", new String[] {
					Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), maxValueDate.toString() }));
		}

		if (isMatured && maturityDate != null && DateUtil.compare(receiptDate.getValue(), maturityDate) < 0) {
			throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_AFTER", new String[] {
					Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), maturityDate.toString() }));
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
			BigDecimal availableAmount = BigDecimal.ZERO;

			if (!StringUtils.isBlank(this.referenceId.getDescription())) {
				availableAmount = new BigDecimal(this.referenceId.getDescription());
			}
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

		FinanceType ft = getFinType();
		if (ft == null) {
			return;
		}

		if (ft.isFinIsGenRef()) {
			this.finReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_LoanReference.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		} else {
			if (!ImplementationConstants.FINREFERENCE_ALW_FREE_TEXT) {
				this.finReference.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptPayment_LoanReference.value"),
								PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
			}
		}

		if (isKnockOff) {
			this.referenceId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LoanClosurePayment_RefId.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}

		this.knockOffFrom
				.setConstraint(new PTListValidator(Labels.getLabel("label_LoanClosurePayment_kncockoffFrom.value"),
						PennantStaticListUtil.getKnockOffFromVlaues(), true));
		// this.receiptAmount
		// .setConstraint(new
		// PTDecimalValidator(Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO"),
		// 2, true, false));
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.finReference.setConstraint("");
		this.subReceiptMode.setConstraint("");
		this.receiptMode.setConstraint("");
		this.receiptDate.setConstraint("");
		this.receiptAmount.setConstraint("");
		this.knockOffFrom.setConstraint("");
		this.closureType.setConstraint("");
		logger.debug("Leaving");
	}

	public void resetDefaults(FinanceMain financeMain) {
		this.finReference.setValue(financeMain.getFinReference());

		if (this.custCIF.getValue().isEmpty()) {
			customer = fetchCustomerDataByID(financeMain.getCustID());
		}

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (!financeMain.isFinIsActive()
				|| DateUtil.compare(SysParamUtil.getAppDate(), financeMain.getMaturityDate()) > 0) {
			fillComboBox(receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
					",EarlyPayment, EarlySettlement, FeePayment,");
			receiptPurpose.setDisabled(true);
		} else if (StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)) {
			String excludeFields = ",FeePayment,EarlySettlement,";
			if (FinanceConstants.PRODUCT_CD.equals(financeMain.getProductCategory())) {
				excludeFields = ",FeePayment,EarlySettlement,EarlyPayment,";
			} else if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory())) {
				excludeFields = ",FeePayment,EarlySettlement,";
			}
			fillComboBox(this.receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), excludeFields);
		} else {
			String excludeFields = ",FeePayment,";
			if (FinanceConstants.PRODUCT_CD.equals(financeMain.getProductCategory())) {
				excludeFields = ",FeePayment,";
			} else if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory())) {
				excludeFields = ",FeePayment,";
			}
			fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), excludeFields);
		}

		FinanceType financeType = receiptService.getFinanceType(financeMain.getFinType());

		setFinType(financeType);
		productCategory = financeMain.getProductCategory();

		if (financeType.isDeveloperFinance()) {
			fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), "");
		} else if (!FinanceConstants.PRODUCT_CD.equals(financeMain.getProductCategory())) {
			fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), ",ESCROW,RTRNGDS,");
		} else {
			if ("EarlyPayment".equals(receiptPurpose.getValue())) {
				fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), ",ESCROW,");
			} else {
				fillComboBox(subReceiptMode, "", PennantStaticListUtil.getSubReceiptPaymentModes(), ",ESCROW,RTRNGDS,");
			}
		}
	}

	private void fillValueDate(Date receiptDt, long finID, Date appDate) {
		boolean isDisabled = false;
		String knockOff = getComboboxValue(knockOffFrom);
		FinExcessAmount fea = null;

		if (!ReceiptMode.PAYABLE.equals(knockOff) && !PennantConstants.List_Select.equals(knockOff)) {
			fea = (FinExcessAmount) this.referenceId.getObject();
		}

		if (fea != null && fea.getValueDate() != null) {
			receiptDt = fea.getValueDate();
			isDisabled = true;

			Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(finID, appDate);

			if (DateUtil.compare(receiptDt, schDate) < 0) {
				receiptDt = schDate;
			}
		}

		if (getComboboxValue(receiptPurpose).equals(FinServiceEvent.EARLYRPY)) {
			List<Date> dates = finServiceInstrutionDAO.getListDates(finID, receiptDt);

			if (CollectionUtils.isNotEmpty(dates)) {
				dates.sort((d1, d2) -> d1.compareTo(d2));
				receiptDt = dates.get(dates.size() - 1);
			}

			int appmonth = DateUtil.getMonth(appDate);
			int receiptmonth = DateUtil.getMonth(receiptDt);

			if (appmonth != receiptmonth) {
				receiptDt = DateUtil.getMonthStart(appDate);
			}
		}

		Date maxReceiptDt = financeRepaymentsDAO.getMaxValueDate(finID);

		if (DateUtil.compare(receiptDt, maxReceiptDt) <= 0) {
			receiptDt = maxReceiptDt;
		}

		this.receiptDate.setValue(receiptDt);
		this.receiptDate.setDisabled(isDisabled);
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

	@Autowired(required = false)
	@Qualifier(value = "subReceiptPaymentModes")
	public void setPartnerBankCodeValidation(SubReceiptPaymentModes subReceiptPaymentModes) {
		this.subReceiptPaymentModes = subReceiptPaymentModes;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

}