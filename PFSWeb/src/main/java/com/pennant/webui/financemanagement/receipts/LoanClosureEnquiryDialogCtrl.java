
package com.pennant.webui.financemanagement.receipts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ForeClosureReport;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainListCtrl;
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.financemanagement.paymentMode.ReceiptListCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.util.ReceiptUtil;
import com.pennapps.core.util.ObjectUtil;

public class LoanClosureEnquiryDialogCtrl extends GFCBaseCtrl<ForeClosure> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(LoanClosureEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LoanClosureEnquiryDialog;
	protected Borderlayout borderlayout_Receipt;
	protected Label windowTitle;

	// Loan Summary Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox finType;

	protected Decimalbox priBal;
	protected Decimalbox pftBal;
	protected Decimalbox priDue;
	protected Decimalbox pftDue;
	protected Decimalbox bounceDueAmount;
	protected Decimalbox otnerChargeDue;
	protected Textbox finCcy;
	protected Decimalbox paidByCustomer;

	protected Button btnSearchCustCIF;
	protected Button btnSearchFinreference;
	protected Button btnSearchReceiptInProcess;
	protected Button btnPrintSchedule;

	// Receipt Details
	protected Groupbox gb_ReceiptDetails;
	// protected Combobox receiptPurpose;
	protected Combobox receiptMode;
	protected Label receiptTypeLabel;
	protected Decimalbox remBalAfterAllocation;
	protected Datebox receiptDate;
	protected Combobox closureType;
	protected Datebox interestTillDate;

	protected Datebox LoanClosure_receiptDate;
	protected Datebox LoanClosure_intTillDate;

	// protected Datebox receivedDate;

	// Payable Details
	protected Groupbox gb_Payable;

	// Receipt Due Details
	protected Listbox listBoxPastdues;
	protected Listbox listBoxSchedule;
	protected Listbox listBoxExcess;

	protected Listheader listheader_ScheduleEndBal;
	protected Listheader listheader_ReceiptSchedule_SchFee;

	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;

	// Tds Headers
	protected Listheader listheader_ReceiptDialog_TDS;
	protected Listheader listheader_ReceiptDialog_PaidTDS;

	// Effective Schedule Tab Details
	protected Label finSchType;
	protected Label finSchCcy;
	protected Label finSchMethod;
	protected Label finSchProfitDaysBasis;
	protected Label finSchReference;
	protected Label finSchGracePeriodEndDate;
	protected Label effectiveRateOfReturn;
	protected Label label_ReceiptPayment_InterestTillDate;

	// Hybrid Changes
	protected Label label_FinGracePeriodEndDate;

	// Buttons
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab receiptDetailsTab;
	protected Tab effectiveScheduleTab;
	protected Button btnPrint;
	protected Button btnReceipt;
	protected Button btnChangeReceipt;
	protected Button btnCalcReceipts;

	private CustomerDetailsService customerDetailsService;
	private ReceiptService receiptService;
	private FinanceDetailService financeDetailService;
	private AccountEngineExecution engineExecution;
	private CommitmentService commitmentService;
	private ReceiptCalculator receiptCalculator;
	private AccrualService accrualService;
	private FinanceMainService financeMainService;

	private AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
	private DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private AgreementDetailDialogCtrl agreementDetailDialogCtrl = null;
	private CustomerDialogCtrl customerDialogCtrl = null;
	private StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl = null;
	private FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl = null;
	protected FinanceMainListCtrl financeMainListCtrl = null; // over handed per
	// parameters
	protected ReceiptListCtrl receiptListCtrl = null; // over handed per
	// parameters
	protected ExtendedFieldCtrl extendedFieldCtrl = null;
	private FinScheduleData finSchedData = null;
	private FinReceiptData receiptData = null;
	private FinReceiptData orgReceiptData = null;
	private FinanceDetail financeDetail;
	private FinanceDetail orgFinanceDetail;
	private Map<String, BigDecimal> taxPercMap = null;

	private List<ChartDetail> chartDetailList = new ArrayList<ChartDetail>();

	// Temporary Fix for the User Next role Modification On Submit-Fail & Saving
	// the record
	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected String module = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	private int formatter = 0;
	private String amountFormat = null;
	private int receiptPurposeCtg = 2;

	protected boolean recSave = false;
	protected Component checkListChildWindow = null;
	protected boolean isEnquiry = false;
	protected Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private List<FinanceScheduleDetail> orgScheduleList = new ArrayList<>();
	private boolean isForeClosure = true;
	private boolean isEarlySettle = true;
	@Autowired
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	@Autowired
	protected CollateralAssignmentDAO collateralAssignmentDAO;
	protected boolean isModelWindow = false;
	private boolean isMatured = false;
	private boolean isWIF = false;
	private LinkedFinancesService linkedFinancesService;
	private ManualAdviseService manualAdviseService;
	private int futureManualAdvisescount;

	/**
	 * default constructor.<br>
	 */
	public LoanClosureEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReceiptDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LoanClosureEnquiryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LoanClosureEnquiryDialog);

		if (arguments.containsKey("finReference")) {
			this.finReference.setValue((String) arguments.get("finReference"));
		}

		if (arguments.containsKey("closureType")) {
			this.closureType.setValue((String) arguments.get("closureType"));
		}

		if (arguments.containsKey("enquiryModule")) {
			isEnquiry = (Boolean) arguments.get("enquiryModule");
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("isModelWindow")) {
			isModelWindow = (Boolean) arguments.get("isModelWindow");
		}

		doFillData(finReference.getValue(), SysParamUtil.getAppDate());

		// FinReceiptData receiptData = new FinReceiptData();
		// FinanceMain financeMain = null;
		// FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		try {
			formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
			amountFormat = PennantApplicationUtil.getAmountFormate(formatter);

			// set components visible dependent of the users rights
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			setSummaryData();
			// set Read only mode accordingly if the object is new or not.

			doShowDialog(receiptData.getReceiptHeader());

			// set default data for closed loans
			// setClosedLoanDetails(finReceiptHeader.getReference());

			// Setting tile Name based on Service Action
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			this.listBoxSchedule.setHeight(getListBoxHeight(6));
			this.receiptDetailsTab.setSelected(true);
			this.btnCalcReceipts.setDisabled(false);
			this.btnChangeReceipt.setDisabled(false);
			this.gb_Payable.setVisible(true);

			if (futureManualAdvisescount > 0) {
				MessageUtil.showMessage(Labels.getLabel("label_FutureManualAdvise_ClosureEnq_Alert.Msg"));
			}

			if (isModelWindow) {
				this.window_LoanClosureEnquiryDialog.setWidth("90%");
				this.window_LoanClosureEnquiryDialog.setHeight("90%");
				this.window_LoanClosureEnquiryDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LoanClosureEnquiryDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Showing Customer details on Clicking Customer View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		CustomerDetails customerDetails = getCustomerDetailsService()
				.getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		String pageName = PennantAppUtil.getCustomerPageName();
		map.put("customerDetails", customerDetails);
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");
		Executions.createComponents(pageName, null, map);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Finance details on Clicking Finance View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		// Preparation of Finance Enquiry Data
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		FinanceEnquiry fe = new FinanceEnquiry();
		fe.setFinID(fm.getFinID());
		fe.setFinReference(fm.getFinReference());
		fe.setFinType(fm.getFinType());
		fe.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		fe.setFinCcy(fm.getFinCcy());
		fe.setScheduleMethod(fm.getScheduleMethod());
		fe.setProfitDaysBasis(fm.getProfitDaysBasis());
		fe.setFinBranch(fm.getFinBranch());
		fe.setLovDescFinBranchName(fm.getLovDescFinBranchName());
		fe.setLovDescCustCIF(fm.getCustCIF());
		fe.setFinIsActive(fm.isFinIsActive());

		Map<String, Object> map = new HashMap<>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", fe);
		map.put("ReceiptDialog", this);
		map.put("isModelWindow", true);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.window_LoanClosureEnquiryDialog, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		if (arguments.containsKey("isMatured")) {
			isMatured = (Boolean) arguments.get("isMatured");
		}

		if (isMatured) {
			windowTitle.setValue(App.getLabel("window_MaturedLoanClosureEnquiryDialog.title"));
		}

		this.btnReceipt.setVisible(false);
		fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptPaymentModes(), ",PRESENT,");
		this.receiptDate.setValue(SysParamUtil.getAppDate());

		// isForeClosure = true;
		// Receipts Details
		this.priBal.setFormat(amountFormat);
		this.pftBal.setFormat(amountFormat);
		this.priDue.setFormat(amountFormat);
		this.pftDue.setFormat(amountFormat);
		this.bounceDueAmount.setFormat(amountFormat);
		this.otnerChargeDue.setFormat(amountFormat);
		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.interestTillDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.paidByCustomer.setFormat(amountFormat);

		logger.debug("Leaving");
	}

	public void onChange$receiptDate(Event event) throws InterruptedException {
		logger.debug("Entering");
		changeReceiptDate();
		logger.debug("Leaving");
	}

	public void changeReceiptDate() throws InterruptedException {
		logger.debug("Entering");
		this.btnCalcReceipts.setDisabled(false);
		this.btnChangeReceipt.setDisabled(false);
		this.btnPrint.setVisible(false);
		this.effectiveScheduleTab.setVisible(false);

		FinanceMain financeMain = financeMainService.getFinanceMainByRef(this.finReference.getValue(), false);
		Date maturityDate = financeMain.getMaturityDate();
		if (!isMatured && this.receiptDate.getValue().compareTo(maturityDate) > 0) {
			MessageUtil.showError("Receipt Date is not allowed more than Maturity Date");
			this.receiptDate.setValue(SysParamUtil.getAppDate());
			return;
		}

		int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
		this.interestTillDate.setValue(DateUtil.addDays(this.receiptDate.getValue(), defaultClearingDays));

		receiptData.setValueDate(this.receiptDate.getValue());
		receiptData.getReceiptHeader().setReceiptDate(this.receiptDate.getValue());
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);

		if (ReceiptMode.CHEQUE.equals(this.receiptMode.getSelectedItem().getValue())
				|| ReceiptMode.DD.equals(this.receiptMode.getSelectedItem().getValue())) {
			receiptData.setValueDate(this.interestTillDate.getValue());
			receiptData.getReceiptHeader().setReceiptDate(this.interestTillDate.getValue());
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param Receipt
	 */
	public void doShowDialog(FinReceiptHeader finReceiptHeader) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (finReceiptHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_LoanClosureEnquiryDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadOnly(boolean isUserAction) {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgReceipt
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean setSummaryData() {
		logger.debug("Entering");

		List<ReceiptAllocationDetail> allocationListData = receiptData.getReceiptHeader().getAllocationsSummary();

		receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(receiptData.getReceiptHeader().getReceiptPurpose());
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		orgScheduleList = schdData.getFinanceScheduleDetails();
		RepayMain rpyMain = receiptData.getRepayMain();

		receiptData.setAccruedTillLBD(schdData.getFinanceMain().getLovDescAccruedTillLBD());
		rpyMain.setLovDescFinFormatter(formatter);

		String custCIFname = "";
		if (receiptData.getFinanceDetail().getCustomerDetails() != null
				&& receiptData.getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			Customer customer = receiptData.getFinanceDetail().getCustomerDetails().getCustomer();
			custCIFname = customer.getCustCIF();
			if (StringUtils.isNotBlank(customer.getCustShrtName())) {
				custCIFname = custCIFname + "-" + customer.getCustShrtName();
			}
		}

		receiptData.getReceiptHeader().setAllocationsSummary(allocationListData);

		this.priBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getPrincipalBalance(), formatter));
		this.pftBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getProfitBalance(), formatter));
		this.priDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverduePrincipal(), formatter));
		this.pftDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverdueProfit(), formatter));
		this.bounceDueAmount
				.setValue(PennantApplicationUtil.formateAmount(rch.getTotalBounces().getTotalDue(), formatter));
		this.otnerChargeDue
				.setValue(PennantApplicationUtil.formateAmount(rch.getTotalRcvAdvises().getTotalDue(), formatter));

		// Receipt Basic Details
		this.custCIF.setValue(custCIFname);
		this.finReference.setValue(rpyMain.getFinReference());
		this.finType.setValue(rpyMain.getFinType() + " - " + rpyMain.getLovDescFinTypeName());
		this.finBranch.setValue(rpyMain.getFinBranch() + " - " + rpyMain.getLovDescFinBranchName());
		this.finCcy.setValue(rpyMain.getFinCcy());

		setBalances();
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for setting data for Child Tab Headers
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
		arrayList.add(0, finMain.getFinType());
		arrayList.add(1, finMain.getFinCcy());
		arrayList.add(2, finMain.getScheduleMethod());
		arrayList.add(3, finMain.getFinReference());
		arrayList.add(4, finMain.getProfitDaysBasis());
		arrayList.add(5, finMain.getGrcPeriodEndDate());
		arrayList.add(6, finMain.isAllowGrcPeriod());

		// In case of Promotion Product will be Empty
		if (StringUtils.isEmpty(finType.getProduct())) {
			arrayList.add(7, false);
		} else {
			arrayList.add(7, true);
		}
		arrayList.add(8, finType.getFinCategory());
		arrayList.add(9, this.custCIF.getValue());
		arrayList.add(10, false);
		arrayList.add(11, module);
		return arrayList;
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(true);
	}

	/**
	 * Method for calculation of Schedule Repayment details List of data
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 * @throws WrongValueException
	 */
	public void onClick$btnCalcReceipts(Event event)
			throws InterruptedException, WrongValueException, InterfaceException {
		logger.debug("Entering" + event.toString());
		if (!isValidateData(true)) {
			return;
		}

		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
		FinReceiptData tempReceiptData = ObjectUtil.clone(receiptData);
		tempReceiptData.setForeClosureEnq(true);
		setOrgReceiptData(tempReceiptData);

		boolean isCalcCompleted = recalEarlyPaySchd(true);
		if (isCalcCompleted && !isMatured) {
			this.effectiveScheduleTab.setVisible(true);
			this.btnPrintSchedule.setVisible(true);
		}
		Date valueDate = this.receiptDate.getValue();
		if (ReceiptMode.CHEQUE.equals(this.receiptMode.getSelectedItem().getValue())
				|| ReceiptMode.DD.equals(this.receiptMode.getSelectedItem().getValue())) {
			valueDate = this.interestTillDate.getValue();
		}

		doFillData(finReference.getValue(), valueDate);
		setSummaryData();
		doFillAllocationDetail();
		doFillExcessPayables();

		this.btnPrint.setVisible(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Captured details based on Receipt Purpose
	 * 
	 * @param event
	 * @throws InterruptedException
	 */

	/**
	 * Method for Processing Captured details based on Receipt Mod
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$receiptMode(Event event) throws InterruptedException {
		logger.debug("Entering");
		String dType = this.receiptMode.getSelectedItem().getValue().toString();
		checkByReceiptMode(dType, true);
		changeReceiptDate();
		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");
		this.btnCalcReceipts.setDisabled(false);
		this.btnChangeReceipt.setDisabled(false);
		this.effectiveScheduleTab.setVisible(false);
		if (ReceiptMode.CHEQUE.equals(recMode) || ReceiptMode.DD.equals(recMode)) {
			this.interestTillDate.setVisible(true);
			this.label_ReceiptPayment_InterestTillDate.setVisible(true);
		} else {
			this.interestTillDate.setVisible(false);
			this.label_ReceiptPayment_InterestTillDate.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Allocation Details recalculation
	 */
	private void resetAllocationPayments() {
		logger.debug("Entering");
		// String recPurpose = getComboboxValue(this.receiptPurpose);
		Date valueDate = receiptData.getReceiptHeader().getReceiptDate();

		// FIXME: PV: Resetting receipt data and finschdeduledata was deleted
		receiptData.setBuildProcess("I");
		receiptData.getReceiptHeader().setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		receiptData.getReceiptHeader().getAllocations().clear();
		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();

		if (receiptPurposeCtg == 2) {
			receiptData.getReceiptHeader().setValueDate(null);
			try {
				receiptData.getRepayMain().setEarlyPayOnSchDate(valueDate);
				recalEarlyPaySchd(false);
			} catch (Exception e) {

			}
		}

		// Initiation of Receipt Data object
		receiptData = getReceiptCalculator().initiateReceipt(receiptData, false);

		// Excess Adjustments After calculation of Total Paid's
		BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount();
		receiptData.setTotReceiptAmount(totReceiptAmount);
		receiptData.setAccruedTillLBD(schData.getFinanceMain().getLovDescAccruedTillLBD());

		// Allocation Process start
		/*
		 * if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) { receiptData =
		 * getReceiptCalculator().recalAutoAllocation(receiptData, valueDate, false); }
		 */

		receiptData = getReceiptCalculator().recalAutoAllocation(receiptData, false);

		doFillAllocationDetail();
		setBalances();
		logger.debug("Leaving");
	}

	/**
	 * Method for on Changing Waiver Amounts
	 */
	private void changeWaiver() {
		receiptData = getReceiptCalculator().changeAllocations(receiptData);
		doFillAllocationDetail();
		setBalances();
	}

	/**
	 * Method for on Changing Paid Amounts
	 */
	private void changePaid() {
		receiptData = getReceiptCalculator().setTotals(receiptData, 0);
		setBalances();
		doFillAllocationDetail();
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	public boolean recalEarlyPaySchd(boolean isRecal) throws InterruptedException {
		logger.debug("Entering");

		receiptData.getRepayMain().setEarlyPayOnSchDate(receiptData.getValueDate());
		receiptData = getReceiptService().recalEarlyPaySchedule(receiptData);
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		// Finding Last maturity date after recalculation.
		List<FinanceScheduleDetail> schList = fsd.getFinanceScheduleDetails();
		Date actualMaturity = fsd.getFinanceMain().getCalMaturity();
		for (int i = schList.size() - 1; i >= 0; i--) {
			if (schList.get(i).getClosingBalance().compareTo(BigDecimal.ZERO) > 0) {
				break;
			}
			actualMaturity = schList.get(i).getSchDate();
		}

		// Validation against Future Disbursements, if Closing balance is
		// becoming BigDecimal.ZERO before future disbursement date
		List<FinanceDisbursement> disbList = fsd.getDisbursementDetails();
		String eventDesc = PennantApplicationUtil.getLabelDesc(receiptData.getReceiptHeader().getReceiptPurpose(),
				PennantStaticListUtil.getReceiptPurpose());
		for (int i = 0; i < disbList.size(); i++) {
			FinanceDisbursement curDisb = disbList.get(i);
			if (curDisb.getDisbDate().compareTo(actualMaturity) > 0) {
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30577", new String[] { eventDesc })));
				Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
				logger.debug("Leaving");
				return false;
			}
		}

		getFinanceDetail().setFinScheduleData(fsd);
		FinanceMain aFinanceMain = fsd.getFinanceMain();
		// aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());

		// Object Setting for Future save purpose
		setFinanceDetail(getFinanceDetail());
		receiptData.setFinanceDetail(getFinanceDetail());
		doFillScheduleList(fsd);

		if (isRecal) {

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(aFinanceMain.getProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis()));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtil.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString() + "%");

			// Fill Effective Schedule Details
			if (!isMatured) {
				this.effectiveScheduleTab.setVisible(true);
			}
			setFinSchedData(fsd);
			/*
			 * // Dashboard Details Report doLoadTabsData(); doShowReportChart(fsd);
			 */

			// Repayments Calculation
			/*
			 * receiptData = calculateRepayments(); setRepayDetailData();
			 */
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain financeMain = aFinScheduleData.getFinanceMain();

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory()) || financeMain.isAlwFlexi()) {

			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			this.listheader_LimitChange.setVisible(true);

			if (financeMain.isAlwFlexi()) {

				label_FinGracePeriodEndDate
						.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinPureFlexiPeriodEndDate.value"));
				listheader_ScheduleEndBal.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchdUtilization"));
				listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_DropLineLimit"));
				listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitDrop"));

			} else {

				listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
				listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
			}
			listheader_AvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
		}

		FinanceScheduleDetail prvSchDetail = null;
		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();

		if (sdSize == 0) {
			logger.debug("Leaving");
			return;
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
		aFinScheduleData = getFinanceDetailService().getFinMaintainenceDetails(aFinScheduleData);
		if (aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0) {
			rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

			for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
				if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
					ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				} else {
					ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				}
			}
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
		if (aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0) {
			penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

			for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
				if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
							.get(penaltyDetail.getFinODSchdDate());
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				} else {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				}
			}
		}

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinanceScheduleDetail> schdList = aFinScheduleData.getFinanceScheduleDetails();
		for (int i = 0; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0) {
				isSchdFee = true;
				break;
			}
		}

		if (isSchdFee) {
			this.listheader_ReceiptSchedule_SchFee.setVisible(true);
		} else {
			this.listheader_ReceiptSchedule_SchFee.setVisible(false);
		}

		// Clear all the listitems in listbox
		this.listBoxSchedule.getItems().clear();
		aFinScheduleData.setFinanceScheduleDetails(
				ScheduleCalculator.sortSchdDetails(aFinScheduleData.getFinanceScheduleDetails()));
		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());

		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			boolean showRate = false;
			FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
			if (i == 0) {
				prvSchDetail = aScheduleDetail;
				showRate = true;
			} else {
				prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
				if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
					showRate = true;
				}
			}

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finSchdData", aFinScheduleData);

			map.put("financeScheduleDetail", aScheduleDetail);
			map.put("paymentDetailsMap", rpyDetailsMap);
			map.put("penaltyDetailsMap", penaltyDetailsMap);
			map.put("formatter", formatter);
			map.put("window", this.window_LoanClosureEnquiryDialog);

			finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
					false);
			if (i == sdSize - 1) {
				finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
						false);
				break;
			}
		}

		logger.debug("Leaving");
	}

	private void setRepayDetailData() {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		// Repay Schedule Data rebuild
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		for (int i = 0; i < receiptDetailList.size(); i++) {
			FinRepayHeader rph = receiptDetailList.get(i).getRepayHeader();
			if (rph.getRepayScheduleDetails() != null) {
				rpySchdList.addAll(rph.getRepayScheduleDetails());
			}
		}

		// Making Single Set of Repay Schedule Details and sent to Rendering
		List<RepayScheduleDetail> tempRpySchdList = ObjectUtil.clone(rpySchdList);
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : tempRpySchdList) {

			RepayScheduleDetail curRpySchd = null;
			if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());

				if (curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0) {
					curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
				}

				if (curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0) {
					curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
				}

				curRpySchd.setPrincipalSchdPayNow(
						curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
				rpySchdMap.remove(rpySchd.getSchDate());
			} else {
				curRpySchd = rpySchd;
			}

			// Adding New Repay Schedule Object to Map after Summing data
			rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
		}

		doFillRepaySchedules(sortRpySchdDetails(new ArrayList<>(rpySchdMap.values())));
		if (rpySchdMap.isEmpty()) {
			this.receiptDetailsTab.setSelected(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayments Amount
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnChangeReceipt(Event event)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		// this.btnChangeReceipt.setDisabled(true);
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 */
	private void doWriteBeanToComponents() {
		logger.debug("Entering");

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		// this.finReference.setValue(rch.getReference());
		if (StringUtils.isEmpty(rch.getAllocationType())) {
			rch.setAllocationType(AllocationType.AUTO);
		}

		// fillComboBox(this.receiptPurpose,
		// FinServiceEvent.EARLYSETTLE,
		// PennantStaticListUtil.getReceiptPurpose(),"");
		// this.receiptPurpose.setDisabled(true);

		// Receipt Mode Details , if FinReceiptDetails Exists
		setBalances();

		// Payable Details
		doFillExcessPayables();

		// Render Excess Amount Details
		doFillAllocationDetail();

		// Only In case of partial settlement process, Display details for
		// effective Schedule
		boolean visibleSchdTab = true;
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		finScheduleData.setFinanceMain(getFinanceDetail().getFinScheduleData().getFinanceMain());
		finScheduleData.setFinanceScheduleDetails(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

		// Fill Effective Schedule Details
		doFillScheduleList(finScheduleData);

		// Dashboard Details Report
		/*
		 * doLoadTabsData(); doShowReportChart(finScheduleData);
		 */

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		// On Loading Data Render for Schedule
		if (receiptHeader != null && receiptHeader.getReceiptDetails() != null
				&& !receiptHeader.getReceiptDetails().isEmpty()) {
			setRepayDetailData();
		}

		getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);

		if (visibleSchdTab) {
			appendScheduleDetailTab(true, false);
		}

		this.recordStatus.setValue(receiptHeader.getRecordStatus());
		logger.debug("Leaving");
	}

	private FinReceiptData doFillData(String finReference, Date valueDate) {
		logger.debug(Literal.ENTERING);

		try {
			List<ReceiptAllocationDetail> allocationListData = null;

			if (receiptData != null) {
				allocationListData = receiptData.getReceiptHeader().getAllocations();
			}

			receiptData = receiptService.getFinReceiptDataById(finReference, valueDate, AccountingEvent.EARLYSTL,
					FinServiceEvent.RECEIPT, "");
			futureManualAdvisescount = manualAdviseService.getFutureDatedAdvises(receiptData.getFinID());
			FinReceiptHeader rch = receiptData.getReceiptHeader();
			rch.setFinID(receiptData.getFinID());
			rch.setReference(finReference);
			rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
			rch.setReceiptDate(SysParamUtil.getAppDate());
			rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
			rch.setAllocationType(AllocationType.AUTO);
			rch.setNewRecord(true);

			FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
			// finReceiptDetail.setReceivedDate(detail.getSchDate());
			finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rch.getReceiptDetails().add(finReceiptDetail);

			rch.setValueDate(valueDate);
			FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			financeMain.setClosureType(this.closureType.getValue());

			receiptData.setReceiptHeader(rch);
			receiptData.setFinID(financeMain.getFinID());
			receiptData.setFinReference(financeMain.getFinReference());

			receiptData.setBuildProcess("I");

			receiptData.setValueDate(valueDate);
			receiptData.setReceiptHeader(rch);
			receiptData.setForeClosureEnq(true);

			orgFinanceDetail = ObjectUtil.clone(receiptData.getFinanceDetail());
			// FIXME SMT parameter need to be removed
			if (allocationListData != null
					&& SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_FEE_WAIVER_IN_FORECLOSURE_ENQ)) {
				receiptData.getReceiptHeader().setAllocationsSummary(allocationListData);
			}
			receiptData = receiptService.calcuateDues(receiptData);
			setFinanceDetail(receiptData.getFinanceDetail());
			setOrgReceiptData(receiptData);

			getFinanceDetail().setCustomerDetails(this.customerDetailsService.getCustById(financeMain.getCustID()));

		} catch (Exception e) {
			MessageUtil.showError(e);
			logger.debug(e);
			return null;
		}
		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */

	public void doFillAllocationDetail() {
		logger.debug("Entering");
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();
		this.listBoxPastdues.getItems().clear();

		if (allocationList.isEmpty()) {
			return;
		}
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (TDSCalculator.isTDSApplicable(fm)) {
			this.listheader_ReceiptDialog_TDS.setVisible(true);
			this.listheader_ReceiptDialog_PaidTDS.setVisible(true);
		}

		// Get Receipt Purpose to Make Waiver amount Editable
		String label = Labels.getLabel("label_RecceiptDialog_AllocationType_");
		boolean isManAdv = false;
		doRemoveValidation();
		doClearMessage();

		for (int i = 0; i < allocationList.size(); i++) {
			createAllocateItem(allocationList.get(i), isManAdv, label, i);
		}

		addDueFooter(formatter);
		addExcessAmt();

		logger.debug("Leaving");
	}

	private void addExcessAmt() {
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0) {
			Listitem item = new Listitem();
			Listcell lc = null;
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_EXCESS"));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(receiptData.getRemBal(), formatter));

			lc.setId("ExcessAmount");
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxPastdues.appendChild(item);
		}
	}

	private void createAllocateItem(ReceiptAllocationDetail allocate, boolean isManAdv, String desc, int idx) {
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc = null;
		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);
		if (allocate.getAllocationTo() < 0) {
			for (FinFeeDetail fee : receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
				if (allocate.getAllocationTo() == -(fee.getFeeTypeID())
						&& "PERCENTG".equals(fee.getCalculationType())) {
					lc = (Listcell) item.getChildren().get(0);
					Button button = new Button("Fee Details");
					button.setId(String.valueOf(idx));
					button.addForward("onClick", window_LoanClosureEnquiryDialog, "onFeeDetailsClick", button.getId());
					lc.appendChild(button);

					break;
				}
			}
		}
		addAmountCell(item, allocate.getTotRecv(), ("AllocateActualDue_" + idx), false);
		// FIXME: PV. Pending code to get in process allocations
		addAmountCell(item, allocate.getInProcess(), ("AllocateInProess_" + idx), true);
		addAmountCell(item, allocate.getTdsDue(), ("AllocateTdsDue_" + idx), true);
		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		// Editable Amount - Total Paid
		lc = new Listcell();
		CurrencyBox allocationPaid = new CurrencyBox();
		allocationPaid.setStyle("text-align:right;");
		allocationPaid.setBalUnvisible(true, true);
		setProps(allocationPaid, false, formatter, 120);
		allocationPaid.setId("AllocatePaid_" + idx);
		allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getTotalPaid(), formatter));
		allocationPaid.addForward("onFulfill", this.window_LoanClosureEnquiryDialog, "onAllocatePaidChange", idx);
		allocationPaid.setReadonly(true);

		lc.appendChild(allocationPaid);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		addAmountCell(item, allocate.getTdsPaid(), ("AllocateTdsPaid_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.window_LoanClosureEnquiryDialog, "onAllocateWaivedChange", idx);
		allocationWaived.setReadonly(true);

		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// Balance Due AMount
		addAmountCell(item, allocate.getTotRecv(), ("AllocateBalDue_" + idx), true);

		// if (allocate.isEditable()){
		this.listBoxPastdues.appendChild(item);
		// }

		logger.debug("Leaving");
	}

	public void onFeeDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", receiptData);
		map.put("buttonId", buttonId);
		map.put("loanClosureEnquiryDialogCtrl", this);
		map.put("isLoanClosure", true);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/EventFeeDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("details",
				receiptData.getReceiptHeader().getAllocationsSummary().get(Integer.parseInt(buttonId)).getSubList());
		map.put("buttonId", buttonId);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/BounceDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Adding footer to show Totals
	 * 
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addDueFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		BigDecimal totRecv = BigDecimal.ZERO;
		BigDecimal totDue = BigDecimal.ZERO;
		BigDecimal inProc = BigDecimal.ZERO;
		BigDecimal paid = BigDecimal.ZERO;
		BigDecimal waived = BigDecimal.ZERO;
		BigDecimal tdsDue = BigDecimal.ZERO;
		BigDecimal tdsPaid = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocationsSummary()) {
			String allocationType = allocate.getAllocationType();
			if (Allocation.EMI.equals(allocationType) || Allocation.FUT_PFT.equals(allocationType)) {
				allocate.setEditable(true);
			}
			if (allocate.isEditable()) {
				totRecv = totRecv.add(allocate.getTotRecv());
				totDue = totDue.add(allocate.getTotalDue().add(allocate.getTotalPaid()));// getTotalDue
				inProc = inProc.add(allocate.getInProcess());
				paid = paid.add(allocate.getPaidAmount());
				waived = waived.add(allocate.getWaivedAmount());
				tdsDue = tdsDue.add(allocate.getTdsDue());
				tdsPaid = tdsPaid.add(allocate.getTdsPaid());
			} else {
				if (Allocation.PFT.equals(allocationType)) {
					tdsDue = tdsDue.add(allocate.getTdsDue());
					totRecv = totRecv.add(tdsDue);
					tdsPaid = tdsPaid.add(allocate.getTdsPaid());
				}

			}

		}
		receiptData.setPaidNow(paid);
		receiptData.setTotReceiptAmount(totDue);
		addAmountCell(item, totRecv, null, true);
		addAmountCell(item, inProc, null, true);
		addAmountCell(item, tdsDue, null, true);
		addAmountCell(item, totDue, null, true);
		addAmountCell(item, paid, null, true);
		addAmountCell(item, tdsPaid, null, true);
		addAmountCell(item, waived, null, true);
		addAmountCell(item, totRecv.subtract(paid).subtract(waived), null, true);

		this.listBoxPastdues.appendChild(item);
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 */
	public void onAllocatePaidChange(ForwardEvent event) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocatePaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = rch.getAllocationsSummary().get(idx).getTotalDue();
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}
		rch.getAllocationsSummary().get(idx).setTotalPaid(paidAmount);
		rch.getAllocationsSummary().get(idx).setPaidAmount(paidAmount);

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitAllocSummary(receiptData, idx);
		} else {
			if (Allocation.EMI.equals(allocate.getAllocationType())) {
				allocateEmi(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())) {
						allocteDtl.setTotalPaid(paidAmount);
						allocteDtl.setPaidAmount(paidAmount);
					}
				}
			}

		}

		changePaid();
		logger.debug("Leaving");
	}

	private void allocateEmi(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, paidAmount);
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[1].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[1] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[1]);
				allocteDtl.setPaidAmount(emiSplit[1]);
			}

			if (Allocation.NPFT.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[2]);
				allocteDtl.setPaidAmount(emiSplit[2]);
			}
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[0]);
				allocteDtl.setPaidAmount(emiSplit[0]);
			}
			if (Allocation.TDS.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(emiSplit[1].subtract(emiSplit[2]));
				allocteDtl.setPaidAmount(emiSplit[1].subtract(emiSplit[2]));
			}
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
		}

	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 */
	public void onAllocateWaivedChange(ForwardEvent event) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocations().get(idx);

		CurrencyBox allocationWaived = (CurrencyBox) this.listBoxPastdues.getFellow(id);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(allocationWaived.getValidateValue(),
				formatter);
		BigDecimal dueAmount = rch.getAllocations().get(idx).getTotalDue();
		BigDecimal paidAmount = rch.getAllocations().get(idx).getTotalPaid();
		if (waivedAmount.compareTo(dueAmount.subtract(paidAmount)) > 0) {
			waivedAmount = dueAmount.subtract(paidAmount);
		}
		paidAmount = dueAmount.subtract(waivedAmount);
		allocate.setWaivedAmount(waivedAmount);
		allocate.setTotalPaid(paidAmount);

		/*
		 * if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_NPFT)) { BigDecimal pft =
		 * receiptCalculator.getProfit(receiptData, waivedAmount); for (ReceiptAllocationDetail
		 * allocteDtl:rch.getAllocations()){ if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_PFT) ){
		 * allocteDtl.setWaivedAmount(pft); } if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_TDS)
		 * ){ allocteDtl.setWaivedAmount(pft.subtract(waivedAmount)); } }
		 * 
		 * }
		 */
		if (Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
			List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
			FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
			BigDecimal pftWaived = BigDecimal.ZERO;
			BigDecimal tdsWaived = BigDecimal.ZERO;
			if (lastSchd.isTDSApplicable()) {
				pftWaived = getReceiptCalculator().getNetOffTDS(fsd.getFinanceMain(), waivedAmount);
				tdsWaived = pftWaived.subtract(waivedAmount);
			} else {
				pftWaived = waivedAmount;
			}
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (Allocation.FUT_PFT.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setWaivedAmount(pftWaived);
				}
				if (Allocation.FUT_TDS.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setWaivedAmount(tdsWaived);
				}
			}
		}

		if (Allocation.EMI.equals(allocate.getAllocationType())) {
			BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, waivedAmount);
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
					if (emiSplit[1].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[1] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[1]);
					/*
					 * allocteDtl.setTotalPaid(allocteDtl.getTotalPaid(). subtract(allocteDtl.getWaivedAmount()));
					 * allocteDtl.setPaidAmount(allocteDtl.getPaidAmount(). subtract(allocteDtl.getWaivedAmount()));
					 */

				}
				if (Allocation.NPFT.equals(allocteDtl.getAllocationType())) {
					if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[2]);
					/*
					 * allocteDtl.setTotalPaid(allocteDtl.getTotalPaid(). subtract(allocteDtl.getWaivedAmount()));
					 * allocteDtl.setPaidAmount(allocteDtl.getPaidAmount(). subtract(allocteDtl.getWaivedAmount()));
					 */
				}
				if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
					if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[0]);
					/*
					 * allocteDtl.setTotalPaid(allocteDtl.getTotalPaid(). subtract(allocteDtl.getWaivedAmount()));
					 * allocteDtl.setPaidAmount(allocteDtl.getPaidAmount(). subtract(allocteDtl.getWaivedAmount()));
					 */
				}
				if (Allocation.TDS.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setWaivedAmount(emiSplit[1].subtract(emiSplit[2]));
					/*
					 * allocteDtl.setTotalPaid(allocteDtl.getTotalPaid(). subtract(allocteDtl.getWaivedAmount()));
					 * allocteDtl.setPaidAmount(allocteDtl.getPaidAmount(). subtract(allocteDtl.getWaivedAmount()));
					 */
				}
			}
		}

		changeWaiver();

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (this.receiptDate.isVisible()) {
			this.receiptDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LoanClosureEnquiryDialog_ReceiptDate.value"), true,
							SysParamUtil.getAppDate(), DateUtils.addYears(SysParamUtil.getAppDate(), 50), true));
		}

		if (this.interestTillDate.isVisible()) {
			this.interestTillDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LoanClosureEnquiryDialog_InterestTillDate.value"), true,
							receiptDate.getValue(), DateUtils.addYears(SysParamUtil.getAppDate(), 50), true));
		}

		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPaymentModes(),
					Labels.getLabel("label_ReceiptDialog_ReceiptMode.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.receiptDate.setConstraint("");
		this.interestTillDate.setConstraint("");
		this.receiptMode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * 
	 * @return
	 */
	private void doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();
		FinReceiptHeader header = receiptData.getReceiptHeader();
		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setReceiptDate(this.receiptDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setRealizationDate(this.interestTillDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			this.receiptDetailsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Checklist Details when Check list Tab selected
	 */
	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {
		this.doWriteComponentsToBean();

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(
					receiptData.getFinanceDetail().getCheckList(), receiptData.getFinanceDetail().getFinanceCheckList(),
					false);
		}

	}

	/**
	 * Method for Processing Agreement Details when Agreement list Tab selected
	 */
	public void onSelectAgreementDetailTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean();

		// refresh template tab
		if (getAgreementDetailDialogCtrl() != null) {
			getAgreementDetailDialogCtrl().doSetLabels(getFinBasicDetails());
			getAgreementDetailDialogCtrl().doShowDialog(false);
		}
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public FinanceDetail onExecuteStageAccDetail() {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

			// Finance Accounting Details Execution
			executeAccounting(true);
		} else {
			receiptData.getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);
		}

		logger.debug("Leaving");
		return receiptData.getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(finMain.getFinID());
		Date dateValueDate = SysParamUtil.getAppDate();
		/*
		 * if (this.receivedDate.getValue() != null) { dateValueDate = this.receivedDate.getValue(); }
		 */

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain,
				receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), profitDetail,
				eventCode, dateValueDate, dateValueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		accrualService.calProfitDetails(finMain,
				receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), newProfitDetail,
				dateValueDate);
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();

		// For Bajaj, It should be always positive
		BigDecimal pftchg = totalPftSchdNew.subtract(totalPftSchdOld);
		if (pftchg.compareTo(BigDecimal.ZERO) < 0) {
			pftchg = pftchg.negate();
		}
		amountCodes.setPftChg(pftchg);

		List<ReturnDataSet> returnSetEntries = new ArrayList<>();
		BigDecimal totRpyPri = BigDecimal.ZERO;
		boolean feesExecuted = false;
		boolean pftChgExecuted = false;
		List<FinReceiptDetail> receiptDetails = receiptData.getReceiptHeader().getReceiptDetails();

		boolean payableLoopProcess = false;
		int rcptSize = receiptDetails.size();
		Map<String, BigDecimal> feeMap = new HashMap<>();
		BigDecimal totPayable = BigDecimal.ZERO;
		for (int rcpt = 0; rcpt < rcptSize; rcpt++) {

			FinReceiptDetail rcd = receiptDetails.get(rcpt);

			String feeTypeCode = rcd.getFeeTypeCode();
			if (!payableLoopProcess
					&& !FinServiceEvent.EARLYSETTLE.equals(receiptData.getReceiptHeader().getReceiptPurpose())) {
				feeMap = new HashMap<>();
				totPayable = BigDecimal.ZERO;
			}

			addZeroifNotContains(feeMap, "PA_ReceiptAmount");
			addZeroifNotContains(feeMap, "EX_ReceiptAmount");
			addZeroifNotContains(feeMap, "EA_ReceiptAmount");
			addZeroifNotContains(feeMap, "PB_ReceiptAmount");
			addZeroifNotContains(feeMap, "ET_ReceiptAmount");

			addZeroifNotContains(feeMap, feeTypeCode + "_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_SGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_IGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_UGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_CESS_P");

			totPayable = totPayable.add(rcd.getAmount());
			String paymentType = rcd.getPaymentType();

			if (ReceiptMode.PAYABLE.equals(paymentType)) {
				feeMap.put("PA_ReceiptAmount", totPayable);
			} else if (ReceiptMode.EXCESS.equals(paymentType)) {
				feeMap.put("EX_ReceiptAmount", feeMap.get("EX_ReceiptAmount").add(rcd.getAmount()));
			} else if (ReceiptMode.EMIINADV.equals(paymentType)) {
				feeMap.put("EA_ReceiptAmount", feeMap.get("EA_ReceiptAmount").add(rcd.getAmount()));
			} else if (ReceiptMode.TEXCESS.equals(paymentType)) {
				feeMap.put("ET_ReceiptAmount", feeMap.get("ET_ReceiptAmount").add(rcd.getAmount()));
			} else {
				feeMap.put("PB_ReceiptAmount", feeMap.get("PB_ReceiptAmount").add(rcd.getAmount()));
			}

			if (ReceiptMode.PAYABLE.equals(paymentType)) {
				feeMap.put(feeTypeCode + "_P", feeMap.get(feeTypeCode + "_P").add(rcd.getAmount()));

				if (rcd.getPayAdvMovement() != null) {
					TaxHeader taxHeader = rcd.getPayAdvMovement().getTaxHeader();

					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();

					List<Taxes> taxDetails = taxHeader.getTaxDetails();

					if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							String taxType = taxes.getTaxType();
							if (RuleConstants.CODE_CGST.equals(taxType)) {
								cgstTax = taxes;
							} else if (RuleConstants.CODE_SGST.equals(taxType)) {
								sgstTax = taxes;
							} else if (RuleConstants.CODE_IGST.equals(taxType)) {
								igstTax = taxes;
							} else if (RuleConstants.CODE_UGST.equals(taxType)) {
								ugstTax = taxes;
							} else if (RuleConstants.CODE_CESS.equals(taxType)) {
								cessTax = taxes;
							}
						}
					}

					feeMap.put(feeTypeCode + "_CGST_P", feeMap.get(feeTypeCode + "_CGST_P").add(cgstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_SGST_P", feeMap.get(feeTypeCode + "_SGST_P").add(sgstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_UGST_P", feeMap.get(feeTypeCode + "_UGST_P").add(ugstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_IGST_P", feeMap.get(feeTypeCode + "_IGST_P").add(igstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_CESS_P", feeMap.get(feeTypeCode + "_CESS_P").add(cessTax.getPaidTax()));
				}
			}

			FinRepayHeader repayHeader = rcd.getRepayHeader();

			feeMap.clear();

			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setPaymentType(paymentType);
			amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());

			// FIXME: FIND THE LOGIC TO SET payableLoopProcess = false; AS PER
			// OLD CODE
			payableLoopProcess = false;

			if (!FinServiceEvent.SCHDRPY.equals(repayHeader.getFinEvent())
					&& !FinServiceEvent.EARLYRPY.equals(repayHeader.getFinEvent())
					&& !FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountingEvent.REPAY);
				amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
				amountCodes.setToExcessAmt(BigDecimal.ZERO);
				amountCodes.setToEmiAdvance(BigDecimal.ZERO);
				if (RepayConstants.EXCESSADJUSTTO_EXCESS.equals(repayHeader.getFinEvent())) {
					amountCodes.setToExcessAmt(repayHeader.getRepayAmount());
				} else {
					amountCodes.setToEmiAdvance(repayHeader.getRepayAmount());
				}

				aeEvent.getAcSetIDList().clear();

				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY));

				Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

				if (!feesExecuted
						&& FinServiceEvent.SCHDRPY.equals(receiptData.getReceiptHeader().getReceiptPurpose())) {
					feesExecuted = true;
					prepareFeeRulesMap(dataMap, paymentType);
				}

				// Receipt Detail external usage Fields Insertion into
				// DataMap
				dataMap.putAll(feeMap);

				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

				amountCodes.setToExcessAmt(BigDecimal.ZERO);
				amountCodes.setToEmiAdvance(BigDecimal.ZERO);

				continue;
			}

			List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
			BigDecimal penaltyCGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltySGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyIGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyUGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyCESSPaid = BigDecimal.ZERO;

			// Penality Waiver GST Details
			BigDecimal penaltyCGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltySGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyIGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyUGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyCESSWaived = BigDecimal.ZERO;

			for (RepayScheduleDetail rsd : repaySchdList) {

				// Set Repay Amount Codes
				amountCodes.setRpTot(amountCodes.getRpTot().add(rsd.getPrincipalSchdPayNow())
						.add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
				amountCodes.setRpPft(
						amountCodes.getRpPft().add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
				amountCodes.setRpPri(amountCodes.getRpPri().add(rsd.getPrincipalSchdPayNow()));
				amountCodes.setRpTds(amountCodes.getRpTds().add(rsd.getTdsSchdPayNow()));
				totRpyPri = totRpyPri.add(rsd.getPrincipalSchdPayNow());

				// Penalties
				amountCodes.setPenaltyPaid(amountCodes.getPenaltyPaid().add(rsd.getPenaltyPayNow()));
				amountCodes.setPenaltyWaived(amountCodes.getPenaltyWaived().add(rsd.getWaivedAmt())); // Check here once
																										// for Exclusive
																										// GST case

				TaxHeader taxHeader = rsd.getTaxHeader();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
							penaltyCGSTPaid = penaltyCGSTPaid.add(taxes.getPaidTax());
							penaltyCGSTWaived = penaltyCGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
							penaltySGSTPaid = penaltySGSTPaid.add(taxes.getPaidTax());
							penaltySGSTWaived = penaltySGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
							penaltyIGSTPaid = penaltyIGSTPaid.add(taxes.getPaidTax());
							penaltyIGSTWaived = penaltyIGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
							penaltyUGSTPaid = penaltyUGSTPaid.add(taxes.getPaidTax());
							penaltyUGSTWaived = penaltyUGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
							penaltyCESSPaid = penaltyCESSPaid.add(taxes.getPaidTax());
							penaltyCESSWaived = penaltyCESSWaived.add(taxes.getWaivedTax());
						}
					}
				}

				// Fee Details
				amountCodes.setSchFeePay(amountCodes.getSchFeePay().add(rsd.getSchdFeePayNow()));

				// Waived Amounts
				amountCodes.setPriWaived(amountCodes.getPriWaived().add(rsd.getPriSchdWaivedNow()));
				amountCodes.setPftWaived(amountCodes.getPftWaived().add(rsd.getPftSchdWaivedNow()));
				amountCodes.setLpiWaived(amountCodes.getLpiWaived().add(rsd.getLatePftSchdWaivedNow()));
				amountCodes.setFeeWaived(amountCodes.getFeeWaived().add(rsd.getSchdFeeWaivedNow()));
			}

			amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());

			// If Payable Continue for All Advises
			if (payableLoopProcess) {
				continue;
			}

			// Accrual & Future Paid Details
			if (FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {

				int schSize = receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size();
				FinanceScheduleDetail lastSchd = receiptData.getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails().get(schSize - 1);

				FinanceScheduleDetail oldLastSchd = null;
				if (lastSchd.isFrqDate()) {
					oldLastSchd = getFinanceDetailService().getFinSchduleDetails(finMain.getFinID(),
							lastSchd.getSchDate());
				}

				// If Final Schedule not exists on Approved Schedule details
				if (oldLastSchd == null) {
					// Last Schedule Interest Amounts Paid
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setLastSchPftPaid(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())
								.subtract(amountCodes.getPftWaived()));
					}

					// Last Schedule Interest Amounts Waived
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setLastSchPftWaived(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()));
					} else {
						amountCodes.setLastSchPftWaived(amountCodes.getPftWaived());
					}

					// Profit Due Paid
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setPftDuePaid(amountCodes.getRpPft());
					} else {
						amountCodes.setPftDuePaid(amountCodes.getRpPft()
								.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()))
								.add(amountCodes.getPftWaived()));
					}

					// Profit Due Waived
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setPftDueWaived(amountCodes.getPftWaived()
								.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())));
					} else {
						amountCodes.setPftDueWaived(BigDecimal.ZERO);
					}

					// Principal Due Paid
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setPriDuePaid(amountCodes.getRpPri());
					} else {
						amountCodes.setPriDuePaid(amountCodes.getRpPri()
								.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()))
								.add(amountCodes.getPriWaived()));
					}

					// Principal Due Waived
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setPriDueWaived(amountCodes.getPriWaived()
								.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())));
					} else {
						amountCodes.setPriDueWaived(BigDecimal.ZERO);
					}
				} else {

					// Last Schedule Interest Amounts Paid
					amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
					amountCodes.setLastSchPftWaived(BigDecimal.ZERO);

					// Profit Due Paid
					amountCodes.setPftDuePaid(amountCodes.getRpPft());

					// Profit Due Waived
					amountCodes.setPftDueWaived(amountCodes.getPftWaived());

					BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
							.subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));

					// Principal Due Paid
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setPriDuePaid(amountCodes.getRpPri());
					} else {
						amountCodes.setPriDuePaid(
								amountCodes.getRpPri().subtract(lastSchdPriBal).add(amountCodes.getPriWaived()));
					}

					// Principal Due Waived
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(lastSchdPriBal));
					} else {
						amountCodes.setPriDueWaived(BigDecimal.ZERO);
					}
				}

				Date curMonthStartDate = DateUtil.getMonthStart(lastSchd.getSchDate());

				// UnAccrual Calculation
				BigDecimal unaccrue = BigDecimal.ZERO;

				if (oldLastSchd == null) {
					FinanceScheduleDetail lastPrvSchd = receiptData.getFinanceDetail().getFinScheduleData()
							.getFinanceScheduleDetails().get(schSize - 2);
					if (DateUtil.compare(curMonthStartDate, lastPrvSchd.getSchDate()) <= 0) {

						// Accrual amounts
						amountCodes.setAccruedPaid(BigDecimal.ZERO);
						amountCodes.setAccrueWaived(BigDecimal.ZERO);

						// UnAccrual Amounts
						unaccrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getAmzTillLBD());

						// UnAccrue Paid
						if (amountCodes.getPftWaived().compareTo(unaccrue) > 0) {
							amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
						} else {
							amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
						}

						// UnAccrue Waived
						if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
							amountCodes.setUnAccrueWaived(unaccrue);
						} else {
							amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
						}
					} else {

						// UnAccrual Amounts
						unaccrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getPrvMthAmz());

						// UnAccrue Paid
						if (amountCodes.getPftWaived().compareTo(unaccrue) > 0) {
							amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
						} else {
							amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
						}

						// UnAccrue Waived
						if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
							amountCodes.setUnAccrueWaived(unaccrue);
						} else {
							amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
						}

						// Accrual amounts
						BigDecimal accrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getAmzTillLBD())
								.subtract(unaccrue);

						// Accrual Paid
						if (amountCodes.getPftWaived().compareTo(unaccrue.add(accrue)) >= 0) {
							amountCodes.setAccruedPaid(BigDecimal.ZERO);
						} else {
							if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
								amountCodes.setAccruedPaid(accrue.add(unaccrue).subtract(amountCodes.getPftWaived()));
							} else {
								amountCodes.setAccruedPaid(accrue);
							}
						}

						// Accrual Waived
						if (amountCodes.getPftWaived().compareTo(accrue.add(unaccrue)) >= 0) {
							amountCodes.setAccrueWaived(accrue);
						} else {
							if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
								amountCodes.setAccrueWaived(amountCodes.getPftWaived().subtract(unaccrue));
							} else {
								amountCodes.setAccrueWaived(BigDecimal.ZERO);
							}
						}
					}
					// Future Principal Paid
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setFuturePriPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setFuturePriPaid(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())
								.subtract(amountCodes.getPriWaived()));
					}

					// Future Principal Waived
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setFuturePriWaived(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()));
					} else {
						amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
					}
				} else {

					// Accrual amounts
					amountCodes.setAccruedPaid(BigDecimal.ZERO);
					amountCodes.setAccrueWaived(BigDecimal.ZERO);

					// UnAccrual amounts
					amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
					amountCodes.setUnAccrueWaived(BigDecimal.ZERO);

					BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
							.subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));

					// Future Principal Paid
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setFuturePriPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setFuturePriPaid(lastSchdPriBal.subtract(amountCodes.getPriWaived()));
					}

					// Future Principal Waived
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setFuturePriWaived(lastSchdPriBal);
					} else {
						amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
					}
				}

				if (TDSCalculator.isTDSApplicable(finMain)) {
					// TDS for Last Installment
					BigDecimal tdsPerc = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					amountCodes.setLastSchTds((amountCodes.getLastSchPftPaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));

					// Splitting TDS amount into Accrued and Unaccrued Paid
					// basis
					if (amountCodes.getAccruedPaid().compareTo(BigDecimal.ZERO) > 0) {

						BigDecimal accrueTds = (amountCodes.getAccruedPaid().multiply(tdsPerc))
								.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
						BigDecimal unaccrueTds = amountCodes.getLastSchTds().subtract(accrueTds);

						amountCodes.setAccruedTds(accrueTds);
						amountCodes.setUnAccruedTds(unaccrueTds);

					} else {
						amountCodes.setAccruedTds(BigDecimal.ZERO);
						amountCodes.setUnAccruedTds(amountCodes.getLastSchTds());
					}

					// TDS Due
					amountCodes.setDueTds((amountCodes.getPftDuePaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));
				} else {
					amountCodes.setLastSchTds(BigDecimal.ZERO);
					amountCodes.setDueTds(BigDecimal.ZERO);
				}

			}

			// Accounting Event Code Setting
			aeEvent.getAcSetIDList().clear();
			if (FinServiceEvent.SCHDRPY.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.REPAY;
			} else if (FinServiceEvent.EARLYRPY.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.EARLYPAY;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			} else if (FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.EARLYSTL;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			}

			aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, eventCode));

			aeEvent.setAccountingEvent(eventCode);

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			// Receipt Detail external usage Fields Insertion into DataMap
			dataMap.putAll(feeMap);
			String purpose = receiptData.getReceiptHeader().getReceiptPurpose();
			if (!feesExecuted && (FinServiceEvent.SCHDRPY.equals(purpose)
					|| (!FinServiceEvent.SCHDRPY.equals(purpose) && repayHeader.getFinEvent().equals(purpose)))) {
				feesExecuted = true;
				prepareFeeRulesMap(dataMap, paymentType);
			}
			aeEvent.setDataMap(dataMap);
			engineExecution.getAccEngineExecResults(aeEvent);
			returnSetEntries.addAll(aeEvent.getReturnDataSet());

			if (amountCodes.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
					|| amountCodes.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {

				aeEvent.getAcSetIDList().clear();

				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.LATEPAY));

				aeEvent.setAccountingEvent(AccountingEvent.LATEPAY);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				// LPP GST Amount setting
				aeEvent.getDataMap().put("LPP_CGST_P", penaltyCGSTPaid);
				aeEvent.getDataMap().put("LPP_SGST_P", penaltySGSTPaid);
				aeEvent.getDataMap().put("LPP_UGST_P", penaltyIGSTPaid);
				aeEvent.getDataMap().put("LPP_IGST_P", penaltyUGSTPaid);
				aeEvent.getDataMap().put("LPP_CESS_P", penaltyCESSPaid);

				// GST Waivers Details
				aeEvent.getDataMap().put("LPP_CGST_W", penaltyCGSTWaived);
				aeEvent.getDataMap().put("LPP_SGST_W", penaltySGSTWaived);
				aeEvent.getDataMap().put("LPP_UGST_W", penaltyUGSTWaived);
				aeEvent.getDataMap().put("LPP_IGST_W", penaltyIGSTWaived);
				aeEvent.getDataMap().put("LPP_CESS_W", penaltyCESSWaived);

				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());
			}

			// Reset Payment Details
			amountCodes.setRpTot(BigDecimal.ZERO);
			amountCodes.setRpPft(BigDecimal.ZERO);
			amountCodes.setRpPri(BigDecimal.ZERO);
			amountCodes.setSchFeePay(BigDecimal.ZERO);
			amountCodes.setPriWaived(BigDecimal.ZERO);
			amountCodes.setPftWaived(BigDecimal.ZERO);
			amountCodes.setLpiWaived(BigDecimal.ZERO);
			amountCodes.setFeeWaived(BigDecimal.ZERO);
			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setRpTds(BigDecimal.ZERO);
			amountCodes.setAccruedPaid(BigDecimal.ZERO);
			amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
			amountCodes.setFuturePriPaid(BigDecimal.ZERO);

			// Manual Advise Postings
			List<ManualAdviseMovements> movements = rcd.getAdvMovements();
			if (movements != null && !movements.isEmpty()) {

				// Summing Same Type of Fee Types to Single Field
				Map<String, BigDecimal> movementMap = new HashMap<>();
				for (int i = 0; i < movements.size(); i++) {
					ManualAdviseMovements movement = movements.get(i);

					// Bounce Charges
					BigDecimal amount = BigDecimal.ZERO;
					String keyCode = null;
					if (StringUtils.isEmpty(movement.getFeeTypeCode())) {

						if (movementMap.containsKey("bounceChargePaid")) {
							amount = movementMap.get("bounceChargePaid");
						}
						movementMap.put("bounceChargePaid", amount.add(movement.getPaidAmount()));

						amount = BigDecimal.ZERO;
						if (movementMap.containsKey("bounceChargeWaived")) {
							amount = movementMap.get("bounceChargeWaived");
						}
						movementMap.put("bounceChargeWaived", amount.add(movement.getWaivedAmount()));
						keyCode = "bounceCharge";
					} else {

						// Receivable Advises
						if (movementMap.containsKey(movement.getFeeTypeCode() + "_P")) {
							amount = movementMap.get(movement.getFeeTypeCode() + "_P");
						}
						movementMap.put(movement.getFeeTypeCode() + "_P", amount.add(movement.getPaidAmount()));

						amount = BigDecimal.ZERO;
						if (movementMap.containsKey(movement.getFeeTypeCode() + "_W")) {
							amount = movementMap.get(movement.getFeeTypeCode() + "_W");
						}
						movementMap.put(movement.getFeeTypeCode() + "_W", amount.add(movement.getWaivedAmount()));

						keyCode = movement.getFeeTypeCode();
					}

					TaxHeader taxHeader = movement.getTaxHeader();
					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
								cgstTax = taxes;
							} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
								sgstTax = taxes;
							} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
								igstTax = taxes;
							} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
								ugstTax = taxes;
							} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
								cessTax = taxes;
							}
						}
					}

					// Tax Details
					// Paid GST Details
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CGST_P")) {
						amount = movementMap.get(keyCode + "_CGST_P");
					}
					movementMap.put(keyCode + "_CGST_P", amount.add(cgstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_SGST_P")) {
						amount = movementMap.get(keyCode + "_SGST_P");
					}
					movementMap.put(keyCode + "_SGST_P", amount.add(sgstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_IGST_P")) {
						amount = movementMap.get(keyCode + "_IGST_P");
					}
					movementMap.put(keyCode + "_IGST_P", amount.add(igstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_UGST_P")) {
						amount = movementMap.get(keyCode + "_UGST_P");
					}
					movementMap.put(keyCode + "_UGST_P", amount.add(ugstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CESS_P")) {
						amount = movementMap.get(keyCode + "_CESS_P");
					}
					movementMap.put(keyCode + "_CESS_P", amount.add(cessTax.getPaidTax()));

					// Waiver GST Details
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CGST_W")) {
						amount = movementMap.get(keyCode + "_CGST_W");
					}
					movementMap.put(keyCode + "_CGST_W", amount.add(cgstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_SGST_W")) {
						amount = movementMap.get(keyCode + "_SGST_W");
					}
					movementMap.put(keyCode + "_SGST_W", amount.add(sgstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_IGST_W")) {
						amount = movementMap.get(keyCode + "_IGST_W");
					}
					movementMap.put(keyCode + "_IGST_W", amount.add(igstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_UGST_W")) {
						amount = movementMap.get(keyCode + "_UGST_W");
					}
					movementMap.put(keyCode + "_UGST_W", amount.add(ugstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CESS_W")) {
						amount = movementMap.get(keyCode + "_CESS_W");
					}
					movementMap.put(keyCode + "_CESS_W", amount.add(cessTax.getWaivedTax()));
				}

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountingEvent.REPAY);
				amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
				amountCodes.setPaymentType(paymentType);
				amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
				aeEvent.getAcSetIDList().clear();
				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY));

				addZeroifNotContains(movementMap, "bounceChargePaid");
				addZeroifNotContains(movementMap, "bounceCharge_CGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_IGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_SGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_UGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_CESS_P");

				dataMap = amountCodes.getDeclaredFieldValues();

				dataMap.putAll(movementMap);

				// if Repay headers not exists on the Receipt, then add Excess
				// Detail map
				if (rcd.getRepayHeader() == null) {
					dataMap.putAll(feeMap);
				}
				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

			}
		}

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if (StringUtils.isNotEmpty(finMain.getFinCommitmentRef())) {
				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				int format = CurrencyUtil.getFormat(commitment.getCmtCcy());

				if (commitment != null && commitment.isRevolving()) {
					aeEvent.setAccountingEvent(AccountingEvent.CMTRPY);
					amountCodes.setCmtAmt(BigDecimal.ZERO);
					amountCodes.setChgAmt(BigDecimal.ZERO);
					amountCodes.setDisburse(BigDecimal.ZERO);
					amountCodes.setRpPri(
							CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(), totRpyPri));

					Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

					aeEvent.setDataMap(dataMap);
					engineExecution.getAccEngineExecResults(aeEvent);

					// FIXME: PV: 04MAY17 why separate method is required for
					// commitment dialog show
					getAccountingDetailDialogCtrl().doFillCmtAccounting(aeEvent.getReturnDataSet(), format);
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList()
							.addAll(aeEvent.getReturnDataSet());
				}
			}
		}

		logger.debug("Leaving");
	}

	private void prepareFeeRulesMap(Map<String, Object> dataMap, String payType) {
		List<FinFeeDetail> finFeeDetailList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			return;
		}

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!finFeeDetail.isRcdVisible()) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(finFeeDetail, payType));

		}

	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab() {
		logger.debug("Entering");
		boolean createTab = false;
		if (tabsIndexCenter.getFellowIfAny("accountingTab") == null) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			// ComponentsCtrl.applyForward(tab,
			// "onSelect=onSelectAccountingDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("accountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
				tabpanel.setVisible(true);
			}
		}

		Long accountSetId = Long.MIN_VALUE;
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		accountSetId = AccountingEngine.getAccountSetID(finMain, AccountingEvent.EARLYSTL);

		// Accounting Detail Tab
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", receiptData.getFinanceDetail());
		map.put("finHeaderList", getFinBasicDetails());
		map.put("acSetID", accountSetId);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);

		Tab tab = null;
		if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
			tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
			tab.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		// setRepaySchdList(sortRpySchdDetails(repaySchdList));
		// this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalTds = BigDecimal.ZERO;
		BigDecimal totalLatePft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totSchdFeePaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtil.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getProfitSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getPrincipalSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()), formatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getTdsSchdPayNow(), formatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()), formatter));
				totalLatePft = totalLatePft
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()), formatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil
						.amountFormate(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()), formatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getMaxWaiver(), formatter));
				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getRefundMax(), formatter));
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal refundPft = BigDecimal.ZERO;
				if (repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					if (repaySchd.isAllowRefund()) {
						refundPft = repaySchd.getRefundReq();
						totalRefund = totalRefund.add(refundPft);
					} else if (repaySchd.isAllowWaiver()) {
						refundPft = repaySchd.getWaivedAmt();
						totalWaived = totalWaived.add(refundPft);
					}
				}

				lc = new Listcell(PennantApplicationUtil.amountFormate(refundPft, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Fee Details
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getSchdFeePayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow())
						.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()))
						.add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()))
						.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()).subtract(refundPft));
				lc = new Listcell(PennantApplicationUtil.amountFormate(netPay, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdFeeBal());

				lc = new Listcell(PennantApplicationUtil.amountFormate(
						netBalance.subtract(netPay.subtract(totalCharge).subtract(totalLatePft)), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				// this.listBoxPayment.appendChild(item);
			}

			// Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
			paymentMap.put("totalTds", totalTds);
			paymentMap.put("totalLatePft", totalLatePft);
			paymentMap.put("totalPri", totalPri);

			paymentMap.put("schdFeePaid", totSchdFeePaid);

			doFillSummaryDetails(paymentMap);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Map<String, BigDecimal> paymentMap) {
		Listcell lc;
		Listitem item;
		// Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(15);
		lc.setParent(item);
		// this.listBoxPayment.appendChild(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		/*
		 * if (paymentMap.get("totalRefund").compareTo(BigDecimal.ZERO) > 0) { this.listheader_Refund.setVisible(true);
		 * totalSchAmount = totalSchAmount.subtract(paymentMap.get("totalRefund"));
		 * fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund")); } else {
		 * this.listheader_Refund.setVisible(false); } if (paymentMap.get("totalCharge").compareTo(BigDecimal.ZERO) > 0)
		 * { this.listheader_Penalty.setVisible(true); totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("totalCharge"));
		 * fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge")); } else {
		 * this.listheader_Penalty.setVisible(false); } if (paymentMap.get("totalPft").compareTo(BigDecimal.ZERO) > 0) {
		 * totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
		 * fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft")); } if
		 * (paymentMap.get("totalTds").compareTo(BigDecimal.ZERO) > 0) {
		 * fillListItem(Labels.getLabel("listcell_totalTdsPayNow.label"), paymentMap.get("totalTds"));
		 * this.listheader_Tds.setVisible(true); } else { this.listheader_Tds.setVisible(false); } if
		 * (paymentMap.get("totalLatePft").compareTo(BigDecimal.ZERO) > 0) { totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("totalLatePft")); this.listheader_LatePft.setVisible(true);
		 * fillListItem(Labels.getLabel("listcell_totalLatePftPayNow.label"), paymentMap.get("totalLatePft")); } else {
		 * this.listheader_LatePft.setVisible(false); }
		 */
		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"));
		}

		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalSchAmount);

	}

	/**
	 * Method for Showing List Item
	 * 
	 * @param label
	 * @param fieldValue
	 */
	private void fillListItem(String label, BigDecimal fieldValue) {

		Listcell lc;
		Listitem item;
		item = new Listitem();
		lc = new Listcell();
		lc.setParent(item);
		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;");
		lc.setSpan(2);
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.amountFormate(fieldValue, receiptData.getRepayMain().getLovDescFinFormatter()));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
		// this.listBoxPayment.appendChild(item);

	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param repayScheduleDetails
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		// FIXME: PV: CODE REVIEW PENDING
		if (repayScheduleDetails != null && repayScheduleDetails.size() > 0) {
			Collections.sort(repayScheduleDetails, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail detail1, RepayScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return repayScheduleDetails;
	}

	private void doFillExcessPayables() {
		logger.debug("Entering");
		if (!isForeClosure && !isEarlySettle) {
			return;
		}
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();
		this.listBoxExcess.getItems().clear();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			createXcessPayableItem(xcessPayableList.get(i), i);
		}
		addXcessFooter(formatter);
		logger.debug("Leaving");
	}

	private void createXcessPayableItem(XcessPayables xcessPayable, int idx) {
		// List Item
		Listitem item = new Listitem();
		// FIXME: PV: CODE REVIEW PENDING
		addBoldTextCell(item, xcessPayable.getPayableDesc(), false, idx);
		addAmountCell(item, xcessPayable.getAvailableAmt(), null, false);
		addAmountCell(item, BigDecimal.ZERO, null, false);
		addAmountCell(item, xcessPayable.getTotPaidNow(), null, false);
		addAmountCell(item, xcessPayable.getBalanceAmt(), null, false);
		this.listBoxExcess.appendChild(item);
	}

	private void addXcessFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell("TOTALS");
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		ReceiptAllocationDetail xcess = receiptData.getReceiptHeader().getTotalXcess();

		// Issue Fixed 141140

		addAmountCell(item, xcess.getTotalDue(), null, true);
		addAmountCell(item, BigDecimal.ZERO, null, true);
		addAmountCell(item, xcess.getTotalPaid(), null, true);
		addAmountCell(item, xcess.getBalance(), null, true);

		this.listBoxExcess.appendChild(item);
	}

	/**
	 * Method to validate data
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 */
	private boolean isValidateData(boolean isCalProcess) throws InterruptedException, InterfaceException {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		// Validate Field Details
		if (isCalProcess) {
			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
		}

		/*
		 * if (this.receivedDate.getValue() != null) { receiptValueDate = this.receivedDate.getValue(); }
		 */

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date receiptValueDate = rch.getValueDate();
		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain fm = financeMainService.getFinanceMainByRef(this.finReference.getValue(), false);
		FinanceType financeType = schdData.getFinanceType();
		List<FinanceScheduleDetail> scheduleList = schdData.getFinanceScheduleDetails();

		// in case of early pay,do not allow in subvention period
		if (receiptPurposeCtg == 1 && fm.isAllowSubvention()) {
			boolean isInSubVention = receiptService.isInSubVention(
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), receiptValueDate);
			if (isInSubVention) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_SubVention_EndDates"));
				return false;
			}
		}
		/*
		 * if (receiptData.getPaidNow().compareTo(rch.getReceiptAmount()) > 0) {
		 * MessageUtil.showError(Labels.getLabel("label_Allocation_More_than_receipt")); return false; }
		 */
		// in case of early settlement,do not allow before first installment
		// date(based on AlwEarlySettleBefrFirstInstn in finType )
		if (receiptPurposeCtg == 2 && !financeType.isAlwCloBefDUe()) {
			if (fm.getFinApprovedDate() != null && rch.getValueDate().compareTo(fm.getFinApprovedDate()) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_First_Inst_Date",
						new String[] { fm.getFinApprovedDate().toString() }));
				return false;
			}
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null && receiptPurposeCtg != 1
				&& receiptValueDate.compareTo(fm.getFinStartDate()) == 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Date"));
			return false;
		}

		// Receipt Calculation Value date should not be equal to Any Holiday
		// Schedule Date

		// Entered Receipt Amount Match case test with allocations
		/*
		 * BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount(); if
		 * (totReceiptAmount.compareTo(BigDecimal.ZERO) == 0) { MessageUtil.showError(Labels.getLabel(
		 * "label_ReceiptDialog_Valid_NoReceiptAmount")); return false; }
		 */

		// Past due Details
		BigDecimal balPending = rch.getTotalPastDues().getBalance().add(rch.getTotalRcvAdvises().getBalance())
				.add(rch.getTotalFees().getBalance());

		// User entered Receipt amounts and paid on manual Allocation validation
		if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) < 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_InsufficientAmount"));
			return false;
		}

		/*
		 * if (receiptPurposeCtg == 2 && isForeClosure) {// FIXME ForeClosure if (balPending.compareTo(BigDecimal.ZERO)
		 * != 0) { MessageUtil .showError(Labels.getLabel("label_ReceiptDialog_Valid_Settlement", new String[] {
		 * PennantApplicationUtil .getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
		 * return false; } }
		 */
		if (!isCalProcess) {
			return true;
		}

		// Finance Should not allow for Partial Settlement & Early settlement
		// when Maturity Date reaches Current application Date
		if (receiptPurposeCtg == 1 || receiptPurposeCtg == 2 && !isMatured) {

			if (fm.getMaturityDate().compareTo(receiptValueDate) < 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_MaturityDate", new String[] { PennantApplicationUtil
								.getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}

		// No excess amount validation on partial Settlement
		if (receiptPurposeCtg == 1) {
			if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) <= 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
				return false;
			} /*
				 * else if (rch.getTotalPastDues().getBalance().compareTo(BigDecimal. ZERO) > 0) {
				 * MessageUtil.showError(Labels.getLabel( "label_ReceiptDialog_Valid_PastAmount_PartialSettlement"));
				 * return false; }
				 */ else {

				// Check the max Schedule payment amount
				BigDecimal closingBal = null;
				boolean isValidPPDate = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0
							&& StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
							&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
							&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
						isValidPPDate = false;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}

				if (!isValidPPDate) {
					MessageUtil.showError(Labels.getLabel("RECEIPT_INVALID_VALUEDATE"));
					return false;
				}

				if (closingBal != null) {
					if (receiptData.getRemBal().compareTo(closingBal) >= 0) {
						MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
								new String[] {
										Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
										PennantApplicationUtil.amountFormate(closingBal, formatter) }));
						return false;
					}
				}
			}
		}

		// Early settlement Validation , if entered amount not sufficient with
		// paid and waived amounts
		if (receiptPurposeCtg == 2) {
			BigDecimal earlySettleBal = balPending;
			if (earlySettleBal.compareTo(BigDecimal.ZERO) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_EarlySettlement"));
				return false;
			}

			// Paid amount still not cleared by paid's or waivers amounts
			/*
			 * if (isForeClosure && balPending.compareTo(BigDecimal.ZERO) > 0) {// #FIXME ForeClosure
			 * MessageUtil.showError(Labels.getLabel( "label_ReceiptDialog_Valid_Paids_EarlySettlement")); return false;
			 * }
			 */

			// If Schedule Already Paid, not allowed to do Early settlement on
			// same received date
			// when Date is with in Grace and No Profit Payment case
			if (fm.isAllowGrcPeriod()) {
				boolean isAlwEarlyStl = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0) {
						if (DateUtil.compare(curSchd.getSchDate(), receiptData.getFinanceDetail().getFinScheduleData()
								.getFinanceMain().getGrcPeriodEndDate()) <= 0) {
							BigDecimal pftBal = scheduleList.get(i - 1).getProfitBalance().add(curSchd.getProfitCalc())
									.subtract(curSchd.getSchdPftPaid())
									.subtract(scheduleList.get(i - 1).getCpzAmount());

							if (pftBal.compareTo(BigDecimal.ZERO) > 0 && curSchd.isSchPftPaid()) {
								isAlwEarlyStl = false;
								break;
							}
						}
					} else if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) < 0) {
						break;
					}
				}

				if (!isAlwEarlyStl) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_RePaid_EarlySettlement"));
					return false;
				}
			}
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to show report chart
	 */
	public void doShowReportChart(FinScheduleData finScheduleData) {
		logger.debug("Entering ");
		// FIXME: PV: CODE REVIEW PENDING
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();

		// For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount(finScheduleData, formatter);

		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts",
				"Loan Amount =" + PennantApplicationUtil.amountFormate(PennantApplicationUtil
						.unFormateAmount(finScheduleData.getFinanceMain().getFinAmount(), formatter), formatter),
				"", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks() + " decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.PIE3D.toString());
		chartDetail.setChartHeight("180");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		// For Repayments Chart
		chartsConfig = new ChartsConfig("Payments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments(finScheduleData, formatter));
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig.setRemarks(ChartType.MSLINE.getRemarks() + " decimals='" + formatter + "'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.MSLINE.toString());
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");
		chartDetailList.add(chartDetail);
		logger.debug("Leaving ");
	}

	/**
	 * Method to get report data from repayments table.
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");
		// FIXME: PV: CODE REVIEW PENDING
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();
		int format = CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());
		ChartSetElement chartSetElement;
		if (listScheduleDetail != null) {
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"Payment Amount", PennantApplicationUtil.formateAmount(curSchd.getRepayAmount(), format)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()), "Principal",
							PennantApplicationUtil.formateAmount(curSchd.getPrincipalSchd(), format).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()), "Interest",
							PennantApplicationUtil.formateAmount(curSchd.getProfitSchd(), format).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	/**
	 * This method returns data for Finance vs amount chart
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForFinVsAmount(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");
		// FIXME: PV: CODE REVIEW PENDING
		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		int format = CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();

		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			BigDecimal financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				financeAmount = financeAmount
						.add(PennantApplicationUtil.formateAmount(curSchd.getDisbAmount(), format));
				downPayment = downPayment
						.add(PennantApplicationUtil.formateAmount(curSchd.getDownPaymentAmount(), format));
				capitalized = capitalized.add(PennantApplicationUtil.formateAmount(curSchd.getCpzAmount(), format));

				scheduleProfit = scheduleProfit
						.add(PennantApplicationUtil.formateAmount(curSchd.getProfitSchd(), format));
				schedulePrincipal = schedulePrincipal
						.add(PennantApplicationUtil.formateAmount(curSchd.getPrincipalSchd(), format));

			}
			chartSetElement = new ChartSetElement("Down Payment", downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Capitalized", capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule Interest", scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule Principal", schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	public void onClick$btn_LinkedLoan(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING); // FIXME: PV: CODE
		// REVIEW PENDING
		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		List<FinanceProfitDetail> finpftDetails = new ArrayList<FinanceProfitDetail>();
		financeMains.addAll(getFinanceDetailService().getFinanceMainForLinkedLoans(finReference.getValue()));

		if (CollectionUtils.isNotEmpty(financeMains)) {
			List<Long> finRefList = new ArrayList<>();
			for (FinanceMain finMain : financeMains) {
				if (StringUtils.equals(receiptData.getFinReference(), finMain.getFinReference())) {
					continue;
				}
				finRefList.add(finMain.getFinID());
			}
			if (finRefList.isEmpty()) {
				MessageUtil.showError("No Linked Loans are Available for this Loan Reference");
				return;
			}

			finpftDetails.addAll(getFinanceDetailService().getFinProfitListByFinRefList(finRefList));
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMains", financeMains);
		map.put("finpftDetails", finpftDetails);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LinkedLoansDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method which returns customer document title
	 * 
	 */
	public String getCustomerIDNumber(String docTypeCode) {
		// FIXME: PV: CODE REVIEW PENDING
		if (getFinanceDetail() != null) {
			for (CustomerDocument custDocs : getFinanceDetail().getCustomerDetails().getCustomerDocumentsList()) {
				if (StringUtils.equals(custDocs.getCustDocCategory(), docTypeCode)) {
					return custDocs.getCustDocTitle();
				}
			}
		}
		return null;
	}

	/** new code to display chart by skipping jsps code start */
	public void onSelectDashboardTab(Event event) throws InterruptedException {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		for (ChartDetail chartDetail : chartDetailList) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul",
					(Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"),
					Collections.singletonMap("chartDetail", chartDetail));
		}
		chartDetailList = new ArrayList<ChartDetail>(); // Resetting
		logger.debug("Leaving");
	}

	/**
	 * new code to display chart by skipping jsps code end
	 * 
	 * @throws Exception
	 */
	public void onClick$btnPrint(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		List<String> finReferences = linkedFinancesService.getFinReferences(finReference.getValue());
		if (CollectionUtils.isNotEmpty(finReferences)) {

			String[] args = new String[2];
			StringBuilder ref = new StringBuilder();

			finReferences.forEach(l1 -> ref.append(l1 + "\n"));
			ref.deleteCharAt(ref.length() - 1);

			args[0] = finReference.getValue();
			args[1] = ref.toString();

			String message = args[0] + " is Linked with " + "\n" + args[1] + "\n"
					+ "Please Delink the loan first then Proceed. ";

			if (MessageUtil.confirm(message, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
				ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
				wve.add(new WrongValueException("Please Delink the loan first then Proceed "));
				showErrorDetails(wve);
			}
		}

		// String reportName = "Foreclosure Letter";
		ForeClosureReport closureReport = new ForeClosureReport();

		FinanceMain fm = getFinanceDetail().getFinScheduleData().getFinanceMain();
		closureReport.setProductDesc(getFinanceDetail().getFinScheduleData().getFinanceType().getFinTypeDesc());

		// Setting Actual Percentage in Fore closure Letter Report.
		if (CollectionUtils.isNotEmpty(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList())) {
			FinFeeDetail finFeeDetail = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()
					.get(0);
			closureReport.setActPercentage(finFeeDetail.getActPercentage());
		}

		if (fm != null) {
			Date applDate = SysParamUtil.getAppDate();
			String appDate = DateFormatUtils.format(applDate, "MMM  dd,yyyy");
			String disDate = DateUtil.formatToLongDate(fm.getFinStartDate());

			Date chrgTillDate;

			Date prvEmiDate = receiptData.getOrgFinPftDtls().getPrvRpySchDate();
			Date receiptDate = this.receiptDate.getValue();
			String mode = this.receiptMode.getSelectedItem().getValue();
			if (ReceiptMode.CHEQUE.equals(mode) || ReceiptMode.DD.equals(mode)) {
				chrgTillDate = this.interestTillDate.getValue();
			} else {
				chrgTillDate = receiptDate;
			}
			int noOfIntDays = DateUtil.getDaysBetween(chrgTillDate, prvEmiDate);

			closureReport.setReceiptDate(DateFormatUtils.format(receiptDate, "dd-MMM-yyyy"));
			closureReport.setCalDate(appDate);
			long finID = fm.getFinID();
			String finReference = fm.getFinReference();

			closureReport.setFinID(finID);
			closureReport.setFinReference(finReference);
			closureReport.setVanNumber(fm.getVanCode() == null ? "" : fm.getVanCode());
			closureReport.setFinAmount(PennantApplicationUtil.formateAmount(fm.getFinAmount(), formatter));
			closureReport.setFinAssetValue(PennantApplicationUtil.formateAmount(fm.getFinAssetValue(), formatter));
			closureReport.setDisbursalDate(disDate);
			closureReport.setChrgTillDate(DateFormatUtils.format(chrgTillDate, "MMM  dd,yyyy"));
			closureReport.setFirstInstDate(DateUtil.formatToLongDate(fm.getNextRepayDate()));

			// Fetch Collateral Details
			// Collateral setup details and assignment details
			List<CollateralAssignment> collateralAssignmentList = collateralAssignmentDAO
					.getCollateralAssignmentByFinRef(fm.getFinReference(), FinanceConstants.MODULE_NAME, "_AView");
			String collateralAddress = "";
			if (CollectionUtils.isNotEmpty(collateralAssignmentList)) {
				CollateralAssignment collateralAssignment = collateralAssignmentList.get(0);
				String tableName = CollateralConstants.MODULE_NAME;
				tableName = tableName + "_" + collateralAssignment.getCollateralType() + "_ed";
				List<Map<String, Object>> extMap = extendedFieldRenderDAO
						.getExtendedFieldMap(collateralAssignment.getCollateralRef(), tableName, "_View");

				if (CollectionUtils.isNotEmpty(extMap)) {

					Map<String, Object> mapValues = extMap.get(0);

					if (mapValues != null && !mapValues.isEmpty()) {

						collateralAddress = StringUtils.isNotEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS1")))
								? collateralAddress
										+ StringUtils.trimToEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS1")))
								: collateralAddress;

						collateralAddress = StringUtils.isNotEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS2")))
								? collateralAddress + ","
										+ StringUtils.trimToEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS2")))
								: collateralAddress;

						collateralAddress = StringUtils.isNotEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS3")))
								? collateralAddress + ","
										+ StringUtils.trimToEmpty(String.valueOf(mapValues.get("PROPERTYADDRESS3")))
								: collateralAddress;

						collateralAddress = StringUtils.isNotEmpty(String.valueOf(mapValues.get("PINCODE")))
								? collateralAddress + ","
										+ StringUtils.trimToEmpty(String.valueOf(mapValues.get("PINCODE")))
								: collateralAddress;

						collateralAddress = StringUtils.isNotEmpty(String.valueOf(mapValues.get("CITY")))
								? collateralAddress + ","
										+ StringUtils.trimToEmpty(String.valueOf(mapValues.get("CITY")))
								: collateralAddress;
					}
				}
			}
			closureReport.setCollateralAddress(collateralAddress);

			if (getFinanceDetail().getCustomerDetails() != null
					&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
				closureReport.setCustName(getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
				closureReport.setCustCIF(getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF());
				closureReport.setCustSalutation(
						getFinanceDetail().getCustomerDetails().getCustomer().getCustSalutationCode());
				CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsbyIdandPhoneType(
						getFinanceDetail().getCustomerDetails().getCustID(), "MOBILE");
				CustomerAddres custAdd = customerDetails.getAddressList().stream()
						.filter(addr -> addr.getCustAddrPriority() == 5).findFirst().orElse(new CustomerAddres());

				String combinedString = null;
				String custflatnbr = null;
				if (StringUtils.trimToEmpty(custAdd.getCustFlatNbr()).equals("")) {
					custflatnbr = " ";
				} else {
					custflatnbr = " " + StringUtils.trimToEmpty(custAdd.getCustFlatNbr()) + " ";
				}

				combinedString = StringUtils.trimToEmpty(custAdd.getCustAddrHNbr())
						+ StringUtils.trimToEmpty(custflatnbr) + StringUtils.trimToEmpty(custAdd.getCustAddrStreet())
						+ "\n" + StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCityName()) + "\n"
						+ StringUtils.trimToEmpty(custAdd.getLovDescCustAddrProvinceName()) + "-"
						+ StringUtils.trimToEmpty(custAdd.getCustAddrZIP()) + "\n"
						+ StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCountryName());

				closureReport.setAddress(combinedString);

				int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
				/*
				 * if (getOrgReceiptData() != null) { receiptData = getOrgReceiptData(); }
				 */

				List<ReceiptAllocationDetail> receiptAllocationDetails = receiptData.getReceiptHeader()
						.getAllocationsSummary();
				receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
				FinReceiptData tempReceiptData = ObjectUtil.clone(receiptData);
				tempReceiptData.setForeClosureEnq(true);
				setOrgReceiptData(tempReceiptData);
				receiptAllocationDetails = tempReceiptData.getReceiptHeader().getAllocationsSummary();
				BigDecimal receivableAmt = BigDecimal.ZERO;
				BigDecimal bncCharge = BigDecimal.ZERO;
				BigDecimal profitAmt = BigDecimal.ZERO;
				BigDecimal principleAmt = BigDecimal.ZERO;
				BigDecimal tdsAmt = BigDecimal.ZERO;
				BigDecimal futTdsAmt = BigDecimal.ZERO;

				for (ReceiptAllocationDetail receiptAllocationDetail : receiptAllocationDetails) {

					// Outstanding Principle
					if (Allocation.FUT_PRI.equals(receiptAllocationDetail.getAllocationType())) {
						closureReport.setOutstandingPri(
								PennantApplicationUtil.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
					}

					// Late Payment Charges
					if (Allocation.ODC.equals(receiptAllocationDetail.getAllocationType())) {
						closureReport.setLatePayCharges(
								PennantApplicationUtil.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
					}

					// Late Payment Interest Amount
					if (Allocation.LPFT.equals(receiptAllocationDetail.getAllocationType())) {
						closureReport.setLatePayInterestAmt(
								PennantApplicationUtil.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
					}

					if (Allocation.BOUNCE.equals(receiptAllocationDetail.getAllocationType())) {
						bncCharge = receiptAllocationDetail.getTotRecv();
					}
					// Issue Fixed 141089
					if (Allocation.MANADV.equals(receiptAllocationDetail.getAllocationType())) {
						receivableAmt = receivableAmt.add(receiptAllocationDetail.getTotRecv());
					}

					// Interest for the month
					if (Allocation.FUT_PFT.equals(receiptAllocationDetail.getAllocationType())) {
						closureReport.setInstForTheMonth(
								PennantApplicationUtil.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
					}

					if (Allocation.PFT.equals(receiptAllocationDetail.getAllocationType())) {
						profitAmt = receiptAllocationDetail.getTotRecv();
					}
					if (Allocation.PRI.equals(receiptAllocationDetail.getAllocationType())) {
						principleAmt = receiptAllocationDetail.getTotRecv();
					}

					if (Allocation.TDS.equals(receiptAllocationDetail.getAllocationType())) {
						tdsAmt = receiptAllocationDetail.getTotRecv();
					}
					if (Allocation.FUT_TDS.equals(receiptAllocationDetail.getAllocationType())) {
						futTdsAmt = receiptAllocationDetail.getTotRecv();
					}
					if (Allocation.FEE.equals(receiptAllocationDetail.getAllocationType())) {
						closureReport.setForeClosFees(closureReport.getForeClosFees().add(
								PennantApplicationUtil.formateAmount(receiptAllocationDetail.getTotRecv(), formatter)));
					}

				}

				if (tdsAmt.compareTo(BigDecimal.ZERO) <= 0) {
					for (ReceiptAllocationDetail receiptAllocationDetail : receiptAllocationDetails) {
						tdsAmt = tdsAmt.add(receiptAllocationDetail.getTdsDue());
					}
				}

				setOrgReceiptData(receiptData);
				// Other Charges
				closureReport.setManualAdviceAmt(PennantApplicationUtil.formateAmount(receivableAmt, formatter));

				// Cheque Bounce Charges
				closureReport.setCheqBncCharges(PennantApplicationUtil.formateAmount(bncCharge, formatter));

				// Pending Installments
				closureReport.setPrincipalAmt(PennantApplicationUtil.formateAmount(principleAmt, formatter));
				closureReport.setInterestAmt(PennantApplicationUtil.formateAmount(profitAmt, formatter));
				closureReport
						.setPendingInsts(PennantApplicationUtil.formateAmount(profitAmt.add(principleAmt), formatter));

				// TDS
				closureReport.setTds(PennantApplicationUtil.formateAmount(tdsAmt.add(futTdsAmt), formatter));

				List<FinExcessAmount> excessList = receiptData.getReceiptHeader().getExcessAmounts();

				// Refunds (Excess Amount + EMI in advance)
				BigDecimal refund = BigDecimal.ZERO;
				for (FinExcessAmount finExcessAmount : excessList) {
					refund = refund.add(finExcessAmount.getBalanceAmt());
				}
				closureReport.setRefund(PennantApplicationUtil.formateAmount(refund, formatter));

				// Advance EMI
				for (FinExcessAmount finExcessAmount : excessList) {
					if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(finExcessAmount.getAmountType())) {
						closureReport.setAdvInsts(
								PennantApplicationUtil.formateAmount(finExcessAmount.getBalanceAmt(), formatter));
					}
				}

				// Total Dues(Late Pay Charges + Pending Installment + cheque
				// bounce charges + Outstanding Principle + Interest For the
				// Month + Foreclosure Charges
				// -TDS - Total Waiver)
				closureReport.setTotalDues(closureReport.getLatePayCharges().add(closureReport.getLatePayInterestAmt())
						.add(closureReport.getPendingInsts()).add(closureReport.getCheqBncCharges())
						.add(closureReport.getOutstandingPri()).add(closureReport.getInstForTheMonth())
						.add(closureReport.getForeClosFees().add(closureReport.getManualAdviceAmt()))
						.subtract(closureReport.getTds()).subtract(closureReport.getTotWaiver()));
				if (noOfIntDays > 0) {
					closureReport.setIntPerday((closureReport.getInstForTheMonth().divide(new BigDecimal(noOfIntDays),
							RoundingMode.CEILING)));
				}
				// Issue Fixed 141142
				List<ManualAdvise> payableList = receiptData.getReceiptHeader().getPayableAdvises();
				BigDecimal payableAmt = BigDecimal.ZERO;
				String ccy = fm.getFinCcy();
				for (ManualAdvise ma : payableList) {
					String taxType = null;

					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(ma.getTaxComponent())) {
						taxType = FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE;
					}

					TaxAmountSplit taxSplit = GSTCalculator.calculateGST(finID, ccy, taxType, ma.getBalanceAmt());
					payableAmt = payableAmt.add(ma.getBalanceAmt().add(taxSplit.gettGST()));
				}

				// Other Refunds (All payable Advise)
				closureReport.setOtherRefunds(PennantApplicationUtil.formateAmount(payableAmt, formatter));

				// Refunds + other Refunds
				closureReport.setTotalRefunds(closureReport.getRefund().add(closureReport.getOtherRefunds()));

				// Net Receivable
				closureReport
						.setNetReceivable(closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()).abs());

				if ((closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()))
						.compareTo(BigDecimal.ZERO) < 0) {
					closureReport.setTotal("Net Payable");
				} else {
					closureReport.setTotal("Net Receivable");
				}

				Map<Date, BigDecimal> next7DayMap = new LinkedHashMap<Date, BigDecimal>();

				int defaultDays = 7;
				int noOfdays = DateUtil.getDaysBetween(chrgTillDate, fm.getMaturityDate());
				if (defaultDays >= noOfdays) {
					defaultDays = noOfdays;
				}

				// calculate net recievable amount for next 7 days
				for (int i = 1; i <= defaultDays; i++) {
					Date valueDate = DateUtils.addDays(chrgTillDate, i);
					FinReceiptData localReceiptData = doFillData(finReference, valueDate);
					BigDecimal amount = BigDecimal.ZERO;

					/*
					 * List<Date> presentmentDates = receiptCalculator.getPresentmentDates(localReceiptData, valueDate);
					 * // get presentment dates localReceiptData = receiptCalculator.fetchODPenalties(localReceiptData,
					 * valueDate,presentmentDates); // calculate late pay penalties
					 */ List<ReceiptAllocationDetail> allocationsList = localReceiptData.getReceiptHeader()
							.getAllocations();
					for (ReceiptAllocationDetail receiptAllocationDetail : allocationsList) {
						// Late Payment Charges
						if (receiptAllocationDetail.isEditable()) {
							amount = amount.add(PennantApplicationUtil
									.formateAmount(receiptAllocationDetail.getBalance(), formatter));
						}

					}
					amount = amount.subtract(closureReport.getAdvInsts()).subtract(closureReport.getTotalRefunds());

					next7DayMap.put(valueDate, amount);
				}

				if (next7DayMap != null && next7DayMap.size() > 0) {
					Date[] dates = (Date[]) next7DayMap.keySet().toArray(new Date[0]);
					// setting next 7 days Dates
					closureReport.setValueDate1(DateFormatUtils.format(dates[0], "dd-MMM-yyyy"));
					closureReport.setAmount1(next7DayMap.get(dates[0]));

					if (dates.length > 1) {
						closureReport.setValueDate2(DateFormatUtils.format(dates[1], "dd-MMM-yyyy"));
						closureReport.setAmount2(next7DayMap.get(dates[1]));
					}

					if (dates.length > 2) {
						closureReport.setValueDate3(DateFormatUtils.format(dates[2], "dd-MMM-yyyy"));
						closureReport.setAmount3(next7DayMap.get(dates[2]));
					}

					if (dates.length > 3) {
						closureReport.setValueDate4(DateFormatUtils.format(dates[3], "dd-MMM-yyyy"));
						closureReport.setAmount4(next7DayMap.get(dates[3]));
					}

					if (dates.length > 4) {
						closureReport.setValueDate5(DateFormatUtils.format(dates[4], "dd-MMM-yyyy"));
						closureReport.setAmount4(next7DayMap.get(dates[4]));
					}

					if (dates.length > 5) {
						closureReport.setValueDate6(DateFormatUtils.format(dates[5], "dd-MMM-yyyy"));
						closureReport.setAmount6(next7DayMap.get(dates[5]));
					}

					if (dates.length > 6) {
						closureReport.setValueDate7(DateFormatUtils.format(dates[6], "dd-MMM-yyyy"));
						closureReport.setAmount7(next7DayMap.get(dates[6]));
					}
				}

				List<FinanceMain> financeMainList = financeDetailService.getFinanceMainForLinkedLoans(finReference);
				StringBuilder linkedFinRef = new StringBuilder(" ");
				if (financeMainList != null) {
					for (FinanceMain finance : financeMainList) {
						if (!finance.getFinReference().equals(closureReport.getFinReference())) {
							linkedFinRef.append(" " + finance.getFinReference());
							linkedFinRef.append(",");
						}
					}
					linkedFinRef.setLength(linkedFinRef.length() - 1);
				}

				// Linked Loan Reference
				closureReport.setLinkedFinRef(linkedFinRef.toString());
				closureReport.setValueDate(DateFormatUtils.format(chrgTillDate, "dd-MMM-yyyy"));
				BigDecimal calcIntrstPerDay = getCalcIntrstPerDay(closureReport.getOutstandingPri(),
						fm.getRepayProfitRate(), fm.getCalRoundingMode());
				closureReport.setOneDayInterest(calcIntrstPerDay);

				closureReport.setEntityDesc(fm.getEntityDesc());
			}

			closureReport.setCustMobile("");
			List<CustomerPhoneNumber> phoneList = getFinanceDetail().getCustomerDetails().getCustomerPhoneNumList();
			if (CollectionUtils.isNotEmpty(phoneList)) {
				for (CustomerPhoneNumber cp : phoneList) {
					if (cp.getPhoneTypePriority() == 5) {
						closureReport.setCustMobile(cp.getPhoneNumber());
						break;
					}
				}
			}
		}

		String templatePath = App.getResourcePath(PathUtil.FINANCE_LOANCLOSURE);
		String loanTypeTemplate = templatePath + File.separator + fm.getFinType().concat("_Foreclosure Letter.docx");
		String commonTemplate = templatePath + File.separator.concat("Foreclosure Letter.docx");

		File loanTypeWiseFile = new File(loanTypeTemplate);
		File commonFile = new File(commonTemplate);

		File template = null;

		if (loanTypeWiseFile.exists()) {
			template = loanTypeWiseFile;
		} else if (commonFile.exists()) {
			template = commonFile;
		}

		if (template == null) {
			MessageUtil.showError("Either " + loanTypeWiseFile.getName() + " or " + commonFile.getName()
					+ " template should be configured in " + templatePath + " location");
			return;
		}

		TemplateEngine engine = null;
		try {
			engine = new TemplateEngine(templatePath, templatePath);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError("Path Not Found");
			return;
		}

		try {
			engine.setTemplate(template.getName());
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(template.getName() + " Not Found");
			return;
		}
		engine.loadTemplate();
		engine.mergeFields(closureReport);

		showDocument(this.window_LoanClosureEnquiryDialog, template.getName(), SaveFormat.PDF, false, engine);
		this.btnPrint.setVisible(false);
		logger.debug("Leaving");
	}

	public void showDocument(Window window, String reportName, int format, boolean saved, TemplateEngine engine)
			throws Exception {
		logger.debug("Entering ");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		if (saved) {
			InputStream inputStream = new FileInputStream(engine.getDocumentPath());
			int data;

			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}

			inputStream.close();
			inputStream = null;
		} else {
			engine.getDocument().save(stream, format);
		}

		if ((SaveFormat.DOCX) == format) {
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", stream.toByteArray()));
		} else {

			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("reportBuffer", stream.toByteArray());
			// arg.put("parentWindow", window);
			arg.put("dialogWindow", window);
			arg.put("reportName", reportName.replace(".docx", ".pdf"));
			arg.put("isAgreement", false);
			arg.put("docFormat", format);
			arg.put("isModelWindow", this.isModelWindow);

			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", window, arg);
		}
		stream.close();
		stream = null;
		logger.debug("Leaving");
	}

	public void addAmountCell(Listitem item, BigDecimal value, String cellID, boolean isBold) {
		Listcell lc = new Listcell(PennantApplicationUtil.amountFormate(value, formatter));

		if (isBold) {
			lc.setStyle("text-align:right;font-weight:bold;");
		} else {
			lc.setStyle("text-align:right;");
		}

		if (!StringUtils.isBlank(cellID)) {
			lc.setId(cellID);
		}

		lc.setParent(item);
	}

	public void addSimpleTextCell(Listitem item, String value) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", window_LoanClosureEnquiryDialog, "onDetailsClick", button.getId());
			lc.appendChild(button);
		}
		lc.setParent(item);
	}

	public void getFinFeeTypeList(String eventCode) {
		// FIXME: OV. should be removed. already part of receipt calculator

		eventCode = AccountingEvent.EARLYSTL;

		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		// Finance Type Fee details based on Selected Receipt Purpose Event
		List<FinTypeFees> finTypeFeesList = this.financeDetailService.getFinTypeFees(financeMain.getFinType(),
				eventCode, false, moduleID);
		receiptData.getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
	}

	private void setBalances() {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		ReceiptAllocationDetail xa = rch.getTotalXcess();
		ReceiptAllocationDetail pd = rch.getTotalPastDues();
		ReceiptAllocationDetail adv = rch.getTotalRcvAdvises();
		ReceiptAllocationDetail fee = rch.getTotalFees();

		// Total Net Receivable
		BigDecimal paidByCustomer = pd.getTotalDue().add(adv.getTotalDue()).add(fee.getTotalDue());
		paidByCustomer = paidByCustomer.subtract(pd.getWaivedAmount()).subtract(adv.getWaivedAmount())
				.subtract(fee.getWaivedAmount());
		this.paidByCustomer.setValue(PennantApplicationUtil.formateAmount(paidByCustomer, formatter));

		// To be Paid by Customer = Net Receivable - Excess paid
		BigDecimal custToBePaid = paidByCustomer.subtract(xa.getTotalPaid());

		// Remaining Balance = Receipt Amount + To be Paid by Customer - Paid by
		// Customer (Allocated)
		BigDecimal remBalAfterAllocation = receiptData.getTotReceiptAmount().subtract(pd.getTotalPaid())
				.subtract(adv.getTotalPaid()).subtract(fee.getTotalPaid());
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBalAfterAllocation, formatter));

	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		notes.setRoleCode(getRole());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintSchedule(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		List<Object> list = new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;
		if (getFinSchedData() != null) {

			// Fee Charges List Render For First Disbursement only/Existing
			List<FeeRule> feeRuleList = getFinSchedData().getFeeRules();
			FinanceMain financeMain = getFinSchedData().getFinanceMain();

			// Get Finance Fee Details For Schedule Render Purpose In
			// maintenance Stage
			List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
			if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
					&& !isWIF) {
				approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinID(), "", isWIF);
			}
			approvedFeeRules.addAll(feeRuleList);

			Map<Date, ArrayList<FeeRule>> feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
			for (FeeRule fee : approvedFeeRules) {
				if (feeChargesMap.containsKey(fee.getSchDate())) {
					ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
					int seqNo = 0;
					for (FeeRule feeRule : feeChargeList) {
						if (feeRule.getFeeCode().equals(fee.getFeeCode())) {
							if (seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0) {
								seqNo = feeRule.getSeqNo();
							}
						}
					}
					fee.setSeqNo(seqNo + 1);
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);

				} else {
					ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);
				}
			}

			finRender = new FinScheduleListItemRenderer();
			list.add(finRender.getScheduleGraphData(getFinSchedData()));
			list.add(finRender.getPrintScheduleData(getFinSchedData(), null, null, true, false, false));

			boolean isSchdFee = false;
			List<FinFeeDetail> finFeeList = getFinSchedData().getFinFeeDetailList();
			for (int i = 0; i < finFeeList.size(); i++) {
				FinFeeDetail finFeeDetail = finFeeList.get(i);
				if (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(finFeeDetail.getFeeScheduleMethod())
						|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(finFeeDetail.getFeeScheduleMethod())
						|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS
								.equals(finFeeDetail.getFeeScheduleMethod())) {
					isSchdFee = true;
					break;
				}
			}

			list.add(isSchdFee);
			Map<Object, Object> map = new HashMap<>();
			map.put("isModelWindow", isModelWindow);
			list.add(map);

			// To get Parent Window i.e Finance main based on product
			Component component = this.window_LoanClosureEnquiryDialog;
			Window window = null;
			if (component instanceof Window) {
				window = (Window) component;
			} else {
				window = (Window) this.window_LoanClosureEnquiryDialog.getParent().getParent().getParent().getParent()
						.getParent().getParent().getParent().getParent();
			}
			String reportName = "FINENQ_ScheduleDetail";

			if (FinanceConstants.PRODUCT_CONVENTIONAL.equals(financeMain.getProductCategory())) {
				reportName = "CFINENQ_ScheduleDetail";
			} else if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory())) {
				reportName = "ODFINENQ_ScheduleDetail";
			}

			// Customer CIF && Customer Name Setting
			CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
			if (customerDetails != null) {
				Customer customer = customerDetails.getCustomer();
				financeMain.setLovDescCustCIF(customer.getCustCIF());
				financeMain.setLovDescCustShrtName(customer.getCustShrtName());
			} else {
				financeMain.setLovDescCustCIF("");
			}

			if (isWIF) {
				reportName = "WIFENQ_ScheduleDetail";
				WIFCustomer customerDetailsData = getFinanceDetail().getCustomer();
				if (customerDetailsData != null) {
					financeMain.setLovDescCustCIF(String.valueOf(customerDetailsData.getCustID()));
					financeMain.setLovDescCustShrtName(customerDetailsData.getCustShrtName());
				} else {
					financeMain.setLovDescCustCIF("");
				}
			}

			int months = DateUtil.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate());

			int advTerms = 0;
			if (AdvanceType.hasAdvEMI(financeMain.getAdvType())
					&& AdvanceStage.hasFrontEnd(financeMain.getAdvStage())) {
				advTerms = financeMain.getAdvTerms();
			}

			String noOfTerms = String.valueOf(financeMain.getCalTerms() + financeMain.getGraceTerms());
			financeMain.setLovDescTenorName((months / 12) + " Years " + (months % 12) + " Months / "
					+ (StringUtils.isEmpty(noOfTerms) ? "0" : noOfTerms) + advTerms + " Payments");

			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = PennantApplicationUtil.getFullName(securityUser.getUsrFName(), securityUser.getUsrMName(),
					securityUser.getUsrLName());

			ReportsUtil.generatePDF(reportName, financeMain, list, usrName, window);
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return stageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		this.stageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	private Date getFirstInstDate(List<FinanceScheduleDetail> financeScheduleDetail) {

		// Finding First Installment Date
		Date firstInstDate = null;
		for (FinanceScheduleDetail scheduleDetail : financeScheduleDetail) {

			BigDecimal repayAmt = scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd())
					.subtract(scheduleDetail.getPartialPaidAmt());

			// InstNumber issue with Partial Settlement before first installment
			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				firstInstDate = scheduleDetail.getSchDate();
				break;
			}
		}
		return firstInstDate;
	}

	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final Map<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", true);
			map.put("financeDetail", orgFinanceDetail);
			map.put("isModelWindow", isModelWindow);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				// tab.setSelected(true);
			}
		}
		logger.debug("Leaving");
	}

	public void appendEffectScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final Map<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", true);
			map.put("financeDetail", getFinanceDetail());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				tab.setSelected(true);
			}
		}
		logger.debug("Leaving");
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	private BigDecimal getCalcIntrstPerDay(BigDecimal priOutstanding, BigDecimal roi, String roundingMode) {
		BigDecimal oneDayIntrst = BigDecimal.ZERO;
		oneDayIntrst = priOutstanding.multiply(roi.divide(new BigDecimal(100))).divide(new BigDecimal(365),
				RoundingMode.CEILING);
		oneDayIntrst = oneDayIntrst.setScale(0, RoundingMode.valueOf(roundingMode));
		return oneDayIntrst;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		if (wve.size() > 0) {
			logger.info("Throwing occured Errors By using WrongValueException");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	public Map<String, BigDecimal> getTaxPercMap() {
		return taxPercMap;
	}

	public void setTaxPercMap(Map<String, BigDecimal> taxPercMap) {
		this.taxPercMap = taxPercMap;
	}

	public FinReceiptData getReceiptData() {
		return receiptData;
	}

	public FinReceiptData getOrgReceiptData() {
		return orgReceiptData;
	}

	public void setOrgReceiptData(FinReceiptData orgReceiptData) {
		this.orgReceiptData = orgReceiptData;
	}

	public ReceiptListCtrl getReceiptListCtrl() {
		return receiptListCtrl;
	}

	public void setReceiptListCtrl(ReceiptListCtrl receiptListCtrl) {
		this.receiptListCtrl = receiptListCtrl;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public FinScheduleData getFinSchedData() {
		return finSchedData;
	}

	public void setFinSchedData(FinScheduleData finSchedData) {
		this.finSchedData = finSchedData;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}
}
