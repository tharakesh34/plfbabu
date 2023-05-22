package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.reports.CreditReviewMainCtgDetails;
import com.pennant.backend.model.reports.CreditReviewSubCtgDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

public class CreditApplicationReviewDialogCtrl extends GFCBaseCtrl<FinCreditReviewDetails> {
	private static final long serialVersionUID = 8602015982512929710L;
	private static final Logger logger = LogManager.getLogger(CreditApplicationReviewDialogCtrl.class);

	public Window window_CreditApplicationReviewDialog; // autowired

	protected Borderlayout borderlayout_CreditApplicationReview;
	protected Grid creditApplicationReviewGrid;
	protected Longbox custID; // autowired
	protected Textbox bankName; // autowired
	protected Textbox auditors; // autowired
	protected Radiogroup conSolOrUnConsol; // autowired
	protected Radio conSolidated; // autowired
	protected Radio unConsolidated; // autowired
	protected Textbox location; // autowired
	protected Textbox auditedYear; // autowired
	protected Datebox auditedDate; // autowired
	protected Decimalbox conversionRate; // autowired
	protected ExtendedCombobox custCIF; // autowired
	protected Label custShrtName; // autowired
	protected Longbox noOfShares; // autowired
	protected CurrencyBox marketPrice; // autowired
	protected Combobox auditPeriod; // autowired
	protected Decimalbox totLibAsstDiff; // autowired
	protected Groupbox gb_CreditReviwDetails;
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Button btnCopyTo; // autowired
	protected Listitem duplicateItem;
	protected Grid grid_Basicdetails; // autoWired
	protected Label label_CreditApplicationReviewDialog_BankName; // autowired
	protected Space space_BankName; // autowired
	protected Groupbox gb_basicDetails; // autowire
	protected Button btnPrint; // autowire
	protected Combobox auditType; // autowire
	protected Radiogroup qualifiedUnQualified; // autowire
	protected Radio qualRadio; // autowire
	protected Radio unQualRadio; // autowire
	protected Textbox lovDescFinCcyName; // autowire
	protected ExtendedCombobox currencyType; // autowire
	protected Button button_addDetails;
	protected Button btnSearchAccountSetCode; // autowire

	protected JdbcSearchObject<Customer> newSearchObject;
	protected List<ValueLabel> listMainSubCategoryCodes = new ArrayList<ValueLabel>();

	// not auto wired vars
	private transient CreditApplicationReviewListCtrl creditApplicationReviewListCtrl; // overhanded per param
	private CustomerDialogCtrl customerDialogCtrl;
	public CreditReviewSubCtgDetails creditReviewSubCtgDetailsHeaders = new CreditReviewSubCtgDetails();
	private FinCreditReviewDetails creditReviewDetails; // overhanded per param
	private Customer customer = null;
	private transient CreditReviewSummaryData creditReviewSummaryData;

	public List<CustomerDocument> custDocList;
	public List<CustomerDocument> customerDocumentList = new ArrayList<CustomerDocument>();
	public List<Notes> notesList = new ArrayList<Notes>();
	public List<CreditReviewSubCtgDetails> creditReviewSubCtgDetailsList = new ArrayList<CreditReviewSubCtgDetails>();
	public List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory = null;
	public List<FinCreditRevSubCategory> modifiedFinCreditRevSubCategoryList = new ArrayList<FinCreditRevSubCategory>();
	private List<FinCreditReviewSummary> creditReviewSummaryList = new ArrayList<FinCreditReviewSummary>();
	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;

	@SuppressWarnings("unused")
	private boolean ratioFlag = false;
	private transient BigDecimal totLiabilities = BigDecimal.ZERO;
	private transient BigDecimal totAssets = BigDecimal.ZERO;
	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private transient FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerDocumentService customerDocumentService;
	private NotificationService notificationService;

	private Map<String, List<FinCreditReviewSummary>> creditReviewSummaryMap;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private Map<String, BigDecimal> curYearValuesMap = new HashMap<String, BigDecimal>();
	private Map<String, BigDecimal> extValuesMap = new HashMap<String, BigDecimal>();
	private Map<String, BigDecimal> prvYearValuesMap = null;
	private Map<String, FinCreditReviewSummary> summaryMap = new HashMap<String, FinCreditReviewSummary>();
	private Map<String, BigDecimal> prv1YearValuesMap = null;
	private Map<String, Object> engine = new HashMap<>();

	private String creditRevCode;
	Date date = SysParamUtil.getAppDate();
	int prevAuditPeriod;
	int listRows;
	int currFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	String amtFormat = PennantApplicationUtil.getAmountFormate(currFormatter);
	List<Filter> filterList = null;

	private String AUDITUNAUDIT_LISTHEADER = "auditUnaudit";

	public CreditApplicationReviewDialogCtrl() {
		super();
	}

	// Component Events

	public void onCreate$window_CreditApplicationReviewDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CreditApplicationReviewDialog);

		try {

			if (arguments.containsKey("creditReviewDetails")) {
				this.creditReviewDetails = (FinCreditReviewDetails) arguments.get("creditReviewDetails");
				FinCreditReviewDetails befImage = new FinCreditReviewDetails();
				BeanUtils.copyProperties(this.creditReviewDetails, befImage);
				this.creditReviewDetails.setBefImage(befImage);
				setCreditReviewDetails(this.creditReviewDetails);
				this.creditReviewSummaryList = this.creditReviewDetails.getCreditReviewSummaryEntries();
			} else {
				setCreditReviewDetails(null);
			}

			doLoadWorkFlow(this.creditReviewDetails.isWorkflow(), this.creditReviewDetails.getWorkflowId(),
					this.creditReviewDetails.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CreditApplicationReviewDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the creditApplicationReviewListWindow controller. So we
			// have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete creditReviewDetails here.
			if (arguments.containsKey("creditApplicationReviewListCtrl")) {
				setCreditApplicationReviewListCtrl(
						(CreditApplicationReviewListCtrl) arguments.get("creditApplicationReviewListCtrl"));
			} else {
				setCreditApplicationReviewListCtrl(null);
			}

			doSetFieldProperties();
			if (StringUtils.isNotBlank(this.creditReviewDetails.getLovDescCustCIF())) {
				customer = (Customer) PennantAppUtil.getCustomerObject(this.creditReviewDetails.getLovDescCustCIF(),
						getFilterList());
			}

			if (customer != null) {
				custDocList = creditApplicationReviewService.getCustomerDocumentsById(this.customer.getCustID(), "");
			} else {
				custDocList = new ArrayList<CustomerDocument>();
			}

			fillComboBox(this.auditType, "", PennantStaticListUtil.getCreditReviewAuditTypesList(), "");

			doShowDialog(getCreditReviewDetails());
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}
		logger.debug("Leaving" + event.toString());
	}

	private Map<String, BigDecimal> setExtValuesMap() {
		extValuesMap.put("EXT_CREDITTRANNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CREDITTRANAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CREDITTRANAVG", BigDecimal.ZERO);
		extValuesMap.put("EXT_DEBITTRANNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_DEBITTRANAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHDEPOSITNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHDEPOSITAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHWITHDRAWALNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHWITHDRAWALAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQDEPOSITNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQDEPOSITAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUEN", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUEAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_INWARDCHQBOUNCENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_OUTWARDCHQBOUNCENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALAVG", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALMAX", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALMIN", BigDecimal.ZERO);
		extValuesMap.put("EXT_SUMOFEMI", BigDecimal.ZERO);
		extValuesMap.put("EXT_NUMBEROFTERMS", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_OBLIGATION", BigDecimal.ZERO);
		extValuesMap.put("EXT_OBLIGATION_ALL", BigDecimal.ZERO);
		extValuesMap.put("EXT_REPAYPROFITRATE", BigDecimal.ZERO);
		extValuesMap.put("EXT_ROUNDINGTARGET", BigDecimal.ZERO);
		extValuesMap.put("EXT_FINASSETVALUE", BigDecimal.ZERO);
		extValuesMap.put("EXT_FINAMOUNT", BigDecimal.ZERO);
		extValuesMap.put("EXT_FIRSTREPAY", BigDecimal.ZERO);
		return extValuesMap;
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custID.setMaxlength(8);
		this.bankName.setMaxlength(50);
		this.auditors.setMaxlength(100);
		this.auditedYear.setMaxlength(4);
		this.noOfShares.setMaxlength(18);
		this.location.setMaxlength(50);
		this.marketPrice.setFormat(PennantApplicationUtil.getAmountFormate(this.currFormatter));
		this.marketPrice.setScale(this.currFormatter);
		this.marketPrice.setMandatory(false);
		this.auditedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.conversionRate.setMaxlength(13);
		this.conversionRate.setFormat(PennantConstants.rateFormate9);
		this.conversionRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.conversionRate.setScale(9);
		this.custCIF.setMandatoryStyle(true);
		this.currencyType.setMaxlength(3);
		this.currencyType.setMandatoryStyle(true);
		this.currencyType.setModuleName("Currency");
		this.currencyType.setValueColumn("CcyCode");
		this.currencyType.setDescColumn("CcyDesc");
		this.currencyType.setValidateColumns(new String[] { "CcyCode" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("CreditApplicationReviewDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnCopyTo"));
		this.custCIF.setReadonly(true);
		this.btnPrint.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$button_addDetails(Event event) {
		logger.debug("Entering" + event.toString());
		if (tabBoxIndexCenter.getSelectedPanel() != null) {
			if ((Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0) != null
					&& ((Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0))
							.getSelectedItem() != null) {
				final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService()
						.getNewFinCreditRevSubCategory();
				addDetails(aFinCreditRevSubCategory);
			} else {
				throw new WrongValueException(this.button_addDetails, Labels.getLabel("FIELD_NO_CELL_EMPTY",
						new String[] { Labels.getLabel("label_addDetails"), Labels.getLabel("label_addDetails") }));
			}
		} else {
			throw new WrongValueException(this.custCIF,
					Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value"),
									Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}

	public void addDetails(FinCreditRevSubCategory aFinCreditRevSubCategory) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finCreditRevSubCategory", aFinCreditRevSubCategory);
		map.put("listOfFinCreditRevCategory", listOfFinCreditRevCategory);
		map.put("creditApplicationReviewDialogCtrl", this);
		map.put("listMainSubCategoryCodes", listMainSubCategoryCodes);
		map.put("parentRole", getRole());

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/FinCreditRevSubCategory/FinCreditRevSubCategoryDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CreditApplicationReviewDialog);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.creditReviewDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug("Entering");
		this.custID.setValue(aCreditReviewDetails.getCustomerId());
		this.custCIF.setValue(aCreditReviewDetails.getLovDescCustCIF() != null
				? StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF())
				: "");
		this.custCIF.setTooltiptext(aCreditReviewDetails.getLovDescCustCIF() != null
				? StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF())
				: "");
		this.custShrtName.setValue(aCreditReviewDetails.getLovDescCustShrtName());
		this.creditRevCode = aCreditReviewDetails.getCreditRevCode();
		this.bankName.setValue(aCreditReviewDetails.getBankName());
		this.auditedDate.setValue(aCreditReviewDetails.getAuditedDate());
		this.auditedYear.setValue(aCreditReviewDetails.getAuditYear());
		this.auditedYear.setReadonly(true);

		if (aCreditReviewDetails.isNewRecord() && StringUtils.isBlank(aCreditReviewDetails.getCurrency())) {
			Currency currency = PennantAppUtil.getCurrencyBycode(SysParamUtil.getAppCurrency());
			this.currencyType.setValue(currency.getCcyCode());
			this.currencyType.setDescription(currency.getCcyDesc());
			this.currFormatter = currency.getCcyEditField();
			doSetFieldProperties();
		} else {
			Currency currency = PennantAppUtil.getCurrencyBycode(aCreditReviewDetails.getCurrency());
			this.currencyType.setValue(currency.getCcyCode());
			this.currencyType.setDescription(aCreditReviewDetails.getCurrency());
			this.currFormatter = CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency());
		}

		if (aCreditReviewDetails.getConversionRate() == null) {
			BigDecimal converstnRate = CurrencyUtil.parse(CalculationUtil.getConvertedAmount(
					AccountConstants.CURRENCY_USD, this.currencyType.getValue(), new BigDecimal(100)), currFormatter);
			this.conversionRate.setValue(converstnRate);
		} else {
			this.conversionRate.setValue(aCreditReviewDetails.getConversionRate());
		}

		if (aCreditReviewDetails.isConsolidated()) {
			this.conSolOrUnConsol.setSelectedIndex(0);
		} else {
			this.conSolOrUnConsol.setSelectedIndex(1);
		}

		this.auditors.setValue(aCreditReviewDetails.getAuditors());
		this.location.setValue(aCreditReviewDetails.getLocation());
		this.noOfShares.setValue(aCreditReviewDetails.getNoOfShares());
		this.marketPrice.setValue(aCreditReviewDetails.getMarketPrice() == null ? BigDecimal.ZERO
				: aCreditReviewDetails.getMarketPrice());

		if (aCreditReviewDetails.getAuditPeriod() != 0) {
			fillComboBox(this.auditPeriod, "12", PennantStaticListUtil.getPeriodList(), "");
		}

		fillComboBox(this.auditType, StringUtils.trimToEmpty(aCreditReviewDetails.getAuditType()),
				PennantStaticListUtil.getCreditReviewAuditTypesList(), "");

		if (aCreditReviewDetails.isQualified()) {
			this.qualifiedUnQualified.setSelectedIndex(0);
		} else {
			this.qualifiedUnQualified.setSelectedIndex(1);
		}
		this.auditPeriod.setDisabled(true);
		setFinCreditReviewSummaryList(aCreditReviewDetails.getCreditReviewSummaryEntries());
		this.recordStatus.setValue(aCreditReviewDetails.getRecordStatus());
		logger.debug("Leaving");
	}

	public void doWriteComponentsToBean(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCreditReviewDetails.setCustomerId(this.custID.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setLovDescCustCIF(this.custCIF.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (Integer.parseInt(this.auditedYear.getValue()) > Integer.parseInt(date.toString().substring(0, 4))) {
			try {
				throw new WrongValueException(this.auditedYear,
						Labels.getLabel("const_NO_FUTURE",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value") }));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			try {
				aCreditReviewDetails.setAuditYear(this.auditedYear.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			aCreditReviewDetails.setAuditedDate(this.auditedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCreditReviewDetails.setAuditors(this.auditors.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setLocation(this.location.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setCreditRevCode(this.creditRevCode);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.conSolOrUnConsol.getSelectedItem().getValue()
					.equals(FacilityConstants.CREDITREVIEW_CONSOLIDATED)) {
				aCreditReviewDetails.setConsolidated(true);
			} else {
				aCreditReviewDetails.setConsolidated(false);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Default conversion rate
			this.conversionRate.setValue(new BigDecimal(1));

			if (this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0) {
				throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
			} else {
				aCreditReviewDetails.setConversionRate((BigDecimal) this.conversionRate.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setMarketPrice(this.marketPrice.getActualValue());
		} catch (WrongValueException we) {
			// wve.add(we);
		}
		try {
			if (this.noOfShares.getValue() != null) {
				aCreditReviewDetails.setNoOfShares(this.noOfShares.getValue());
			} else {
				aCreditReviewDetails.setNoOfShares(0);
			}
		} catch (WrongValueException we) {
			// wve.add(we);
		}
		try {
			if ("#".equals(this.auditPeriod.getSelectedItem().getValue().toString())) {
				throw new WrongValueException();
			} else {
				aCreditReviewDetails
						.setAuditPeriod(Integer.parseInt(this.auditPeriod.getSelectedItem().getValue().toString()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.auditType.getSelectedItem() != null
					&& !"#".equals(this.auditType.getSelectedItem().getValue().toString())) {
				aCreditReviewDetails.setAuditType(this.auditType.getSelectedItem().getValue().toString());
			} else {
				throw new WrongValueException(this.auditType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditType.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.qualifiedUnQualified.getSelectedItem().getValue().toString()
					.equals(FacilityConstants.CREDITREVIEW_QUALIFIED)) {
				aCreditReviewDetails.setQualified(true);
			} else {
				aCreditReviewDetails.setQualified(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.currencyType.getValue())) {
				throw new WrongValueException(this.currencyType, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_finCcy.value") }));
			} else {
				aCreditReviewDetails.setCurrency(this.currencyType.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCreditReviewDetails.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	public void doShowDialog(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug("Entering");

		if (customer != null) {
			if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)) {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_SME);
			} else if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_CORP)) {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_CORP);
			} else {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_INDIV);
			}
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCreditReviewDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.totLibAsstDiff.setValue(BigDecimal.ZERO);
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCreditReviewDetails);

			if (customer != null) {

				setExtValuesMap();
				doSetCustomer(customer, newSearchObject);
			}
			if (PennantConstants.PFF_CUSTCTG_SME.equals(customer.getCustCtgCode())) {
				// this.bankName.setValue(customer.getCustShrtName());
				this.bankName.setReadonly(true);
				this.label_CreditApplicationReviewDialog_BankName
						.setValue(Labels.getLabel("label_CustomerDialog_CustShrtName.value"));
				this.label_CreditApplicationReviewDialog_BankName.setVisible(false);
				this.bankName.setVisible(false);
				this.space_BankName.setVisible(false);
			}
			if (enqiryModule) {
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
				this.btnNotes.setVisible(false);
				this.userAction.setVisible(false);
			}
			setLables();
			setDialog(DialogType.EMBEDDED);
			// groupboxWf.setVisible(false); // For Present Requirement
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CreditApplicationReviewDialog.onClose();
		}

		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.location.isReadonly()) {
			this.location.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_Location.value"), null, false));
		}
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value"), null, true));
		}
		if (!this.conversionRate.isReadonly()) {
			this.conversionRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value"), 9, true, false, 9999));
		}
		if (!this.auditedYear.isReadonly()) {
			this.auditedYear.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"), null, true));
		}
		if (!this.auditors.isReadonly()) {
			this.auditors.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value"), null, false));
		}
		if (!this.auditedDate.isReadonly()) {
			this.auditedDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_AuditedDate.value"), true, null, true, true));
		}
		if (!this.auditPeriod.isReadonly()) {
			this.auditPeriod.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value"), true, false));
		}
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.location.setConstraint("");
		this.bankName.setConstraint("");
		this.conversionRate.setConstraint("");
		this.auditedYear.setConstraint("");
		this.auditors.setConstraint("");
		this.auditedDate.setConstraint("");
		this.noOfShares.setConstraint("");
		this.marketPrice.setConstraint("");
		this.currencyType.setConstraint("");
		logger.debug("Leaving");
	}

	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value"), null, true));
		if (currencyType.isButtonVisible()) {
			this.currencyType.setConstraint(new PTStringValidator(
					Labels.getLabel("labelCreditApplicationReviewDialog_FinCcy.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.marketPrice.setConstraint("");
		this.currencyType.setConstraint("");
		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.location.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.conversionRate.setErrorMessage("");
		this.auditedYear.setErrorMessage("");
		this.auditType.setErrorMessage("");
		this.auditors.setErrorMessage("");
		this.auditedDate.setErrorMessage("");
		this.noOfShares.setErrorMessage("");
		this.marketPrice.setErrorMessage("");
		this.currencyType.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected void refreshList() {
		getCreditApplicationReviewListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(getCreditReviewDetails(), aCreditReviewDetails);

		doDelete(String.valueOf(aCreditReviewDetails.getDetailId()), aCreditReviewDetails);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug("Entering");

		if (getCreditReviewDetails().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.custCIF.setReadonly(true);
		}
		this.location.setReadonly(isReadOnly("CreditApplicationReviewDialog_location"));
		this.bankName.setReadonly(isReadOnly("CreditApplicationReviewDialog_bankName"));
		this.conversionRate.setReadonly(isReadOnly("CreditApplicationReviewDialog_conversionRate"));
		this.auditedYear.setReadonly(isReadOnly("CreditApplicationReviewDialog_auditedYear"));
		this.auditors.setReadonly(isReadOnly("CreditApplicationReviewDialog_auditors"));
		this.auditedDate.setDisabled(isReadOnly("CreditApplicationReviewDialog_auditedDate"));
		this.conSolidated.setDisabled(isReadOnly("CreditApplicationReviewDialog_consolOrUnConsol"));
		this.unConsolidated.setDisabled(isReadOnly("CreditApplicationReviewDialog_consolOrUnConsol"));
		this.marketPrice.setReadonly(isReadOnly("CreditApplicationReviewDialog_marketPrice"));
		this.noOfShares.setReadonly(isReadOnly("CreditApplicationReviewDialog_noOfShares"));
		this.auditType.setDisabled(isReadOnly("CreditApplicationReviewDialog_auditType"));
		this.qualRadio.setDisabled(isReadOnly("CreditApplicationReviewDialog_QualUnQual"));
		this.unQualRadio.setDisabled(isReadOnly("CreditApplicationReviewDialog_QualUnQual"));
		readOnlyComponent(isReadOnly("CreditApplicationReviewDialog_auditPeriod"), this.auditPeriod);
		readOnlyComponent(isReadOnly("CreditApplicationReviewDialog_currencyType"), this.currencyType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.creditReviewDetails.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.bankName.setReadonly(true);
		this.location.setReadonly(true);
		this.auditedDate.setReadonly(true);
		this.auditedYear.setReadonly(true);
		this.auditors.setReadonly(true);
		this.conSolidated.setDisabled(true);
		this.unConsolidated.setDisabled(true);
		this.conversionRate.setReadonly(true);
		this.marketPrice.setReadonly(true);
		this.noOfShares.setReadonly(true);
		this.auditPeriod.setReadonly(true);
		readOnlyComponent(true, this.currencyType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.custID.setText("");
		this.bankName.setValue("");
		this.location.setValue("");
		this.auditedDate.setText("");
		this.auditedYear.setValue("");
		this.auditors.setValue("");
		this.currencyType.setValue("");
		this.conversionRate.setValue("0");
		this.noOfShares.setText("0");
		logger.debug("Leaving");
	}

	public void doSave() {
		logger.debug("Entering");

		final FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(getCreditReviewDetails(), aCreditReviewDetails);
		boolean isNew = false;

		if ((this.totLibAsstDiff.getValue() == null ? BigDecimal.ZERO : this.totLibAsstDiff.getValue())
				.compareTo(BigDecimal.ZERO) != 0
				&& userAction.getSelectedItem().getValue().toString().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
			MessageUtil.showError("Total Assets and Total Liabilities & Net Worth not Matched..");

			return;
		}

		setListDetails(aCreditReviewDetails);
		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		doSetValidation();
		// fill the FinCreditReviewDetails object with the components data
		doWriteComponentsToBean(aCreditReviewDetails);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCreditReviewDetails.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCreditReviewDetails.getRecordType())) {
				aCreditReviewDetails.setVersion(aCreditReviewDetails.getVersion() + 1);
				if (isNew) {
					aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCreditReviewDetails.setNewRecord(true);
				}
			}
		} else {
			aCreditReviewDetails.setVersion(aCreditReviewDetails.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCreditReviewDetails, tranType)) {
				refreshList();
				// Mail Alert Notification for User
				if (StringUtils.isNotBlank(aCreditReviewDetails.getNextTaskId())
						&& !StringUtils.trimToEmpty(aCreditReviewDetails.getNextRoleCode())
								.equals(aCreditReviewDetails.getRoleCode())) {
					notificationService.sendNotifications(NotificationConstants.MAIL_MODULE_CREDIT,
							aCreditReviewDetails);
				}
				// do Close the Dialog window
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCreditReviewDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCreditReviewDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCreditReviewDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCreditReviewDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCreditReviewDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCreditReviewDetails);
				}

				if (isNotesMandatory(taskId, aCreditReviewDetails)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aCreditReviewDetails.setTaskId(taskId);
			aCreditReviewDetails.setNextTaskId(nextTaskId);
			aCreditReviewDetails.setRoleCode(getRole());
			aCreditReviewDetails.setNextRoleCode(nextRoleCode);
			aCreditReviewDetails.setNotesList(this.notesList);
			auditHeader = getAuditHeader(aCreditReviewDetails, tranType);

			String operationRefs = getServiceOperations(taskId, aCreditReviewDetails);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCreditReviewDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCreditReviewDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCreditApplicationReviewService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCreditApplicationReviewService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCreditApplicationReviewService().doApprove(auditHeader);

					if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCreditApplicationReviewService().doReject(auditHeader);
					if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CreditApplicationReviewDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CreditApplicationReviewDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.creditReviewDetails), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components
	private AuditHeader getAuditHeader(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCreditReviewDetails.getBefImage(),
				aCreditReviewDetails);
		return new AuditHeader(String.valueOf(aCreditReviewDetails.getDetailId()), null, null, null, auditDetail,
				aCreditReviewDetails.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.creditReviewDetails);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.creditReviewDetails.getDetailId());
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinCreditReviewDetails getCreditReviewDetails() {
		return this.creditReviewDetails;
	}

	public void setCreditReviewDetails(FinCreditReviewDetails creditReviewDetails) {
		this.creditReviewDetails = creditReviewDetails;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return this.creditApplicationReviewService;
	}

	public void setCreditApplicationReviewListCtrl(CreditApplicationReviewListCtrl creditApplicationReviewListCtrl) {
		this.creditApplicationReviewListCtrl = creditApplicationReviewListCtrl;
	}

	public CreditApplicationReviewListCtrl getCreditApplicationReviewListCtrl() {
		return this.creditApplicationReviewListCtrl;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setFinCreditReviewSummaryList(List<FinCreditReviewSummary> creditReviewSummaryList) {
		this.creditReviewSummaryList = creditReviewSummaryList;
	}

	public void setListDetails(FinCreditReviewDetails finCreditReviewDetails) {
		logger.debug("Entering");
		boolean isNewSubCategory = false;
		List<FinCreditReviewSummary> listOfCreditReviewSummary = new ArrayList<FinCreditReviewSummary>();
		List<Component> listOfTabPanels = this.tabpanelsBoxIndexCenter.getChildren();

		for (int k = 0; k < listOfTabPanels.size(); k++) {
			if (listOfTabPanels.get(k).getId().startsWith("tabPanel_")) {
				Listbox listBox = (Listbox) listOfTabPanels.get(k).getFirstChild().getFirstChild();
				List<Listitem> listItems = listBox.getItems();

				for (int i = 0; i < listItems.size(); i++) {
					Listitem listItem = (Listitem) listItems.get(i);
					if (listItem instanceof Listgroup) {
						continue;
					}
					FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) listItem
							.getAttribute("finData");
					FinCreditReviewSummary creditReviewSummary = null;

					if (finCreditRevSubCategory == null) {
						continue;
					}

					if (listItem.getAttribute("finSummData") != null) {
						creditReviewSummary = (FinCreditReviewSummary) listItem.getAttribute("finSummData");
					} else {
						creditReviewSummary = new FinCreditReviewSummary();
						creditReviewSummary.setNewRecord(true);
						creditReviewSummary.setSubCategoryCode(finCreditRevSubCategory.getSubCategoryCode());
					}
					// **** Credit Review Summary
					if (isWorkFlowEnabled()) {
						if (StringUtils.isBlank(creditReviewSummary.getRecordType())) {
							creditReviewSummary.setVersion(finCreditRevSubCategory.getVersion() + 1);
							if (creditReviewSummary.isNewRecord()) {
								creditReviewSummary.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							} else {
								creditReviewSummary.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								creditReviewSummary.setNewRecord(true);
							}
						}
					} else {
						creditReviewSummary.setVersion(creditReviewSummary.getVersion() + 1);
					}

					// **** Credit Review Sub Category
					if (listItem.getAttribute("NewSubCategory") != null) {
						isNewSubCategory = true;

						finCreditRevSubCategory.setNewRecord(true);
						if (isWorkFlowEnabled()) {
							if (StringUtils.isBlank(finCreditRevSubCategory.getRecordType())) {
								finCreditRevSubCategory.setVersion(finCreditRevSubCategory.getVersion() + 1);
								if (isNewSubCategory) {
									finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
								} else {
									finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
									finCreditRevSubCategory.setNewRecord(true);
								}
							}
						} else {
							finCreditRevSubCategory.setVersion(finCreditRevSubCategory.getVersion() + 1);
						}
						finCreditRevSubCategory.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						finCreditRevSubCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					}

					creditReviewSummary.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					creditReviewSummary.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					creditReviewSummary.setRecordStatus(finCreditReviewDetails.getRecordStatus());
					creditReviewSummary.setWorkflowId(finCreditReviewDetails.getWorkflowId());

					creditReviewSummary
							.setItemValue(this.curYearValuesMap.get(creditReviewSummary.getSubCategoryCode()) == null
									? BigDecimal.ZERO
									: CurrencyUtil.unFormat(
											this.curYearValuesMap.get(creditReviewSummary.getSubCategoryCode()),
											this.currFormatter));

					if ("CHECK".equals(creditReviewSummary.getSubCategoryCode())) {
						if (creditReviewSummary.getItemValue().compareTo(BigDecimal.ZERO) != 0 && userAction
								.getSelectedItem().getValue().toString().equals(PennantConstants.RCD_STATUS_APPROVED)) {
							MessageUtil.showError("Total Assets and Total Liabilities & Net Worth not Matched..");
							return;
						}
					}
					listOfCreditReviewSummary.add(creditReviewSummary);
					creditReviewSummary = null;
				}
			}
			// if(isNewSubCategory){
			if (modifiedFinCreditRevSubCategoryList != null && !modifiedFinCreditRevSubCategoryList.isEmpty()) {
				finCreditReviewDetails.setLovDescFinCreditRevSubCategory(listOfFinCreditRevSubCategory);
			}
			// }
			finCreditReviewDetails.setCustomerDocumentList(customerDocumentList);
			finCreditReviewDetails.setCreditReviewSummaryEntries(listOfCreditReviewSummary);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$currencyType(Event event) {
		logger.debug("Entering " + event.toString());

		this.currencyType.setConstraint("");
		Object dataObject = currencyType.getObject();

		if (dataObject instanceof String) {
			this.currencyType.setValue(dataObject.toString());
			this.currencyType.setDescription("");

		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.currencyType.setValue(details.getCcyCode());
				this.currencyType.setDescription(details.getCcyDesc());
				currFormatter = details.getCcyEditField();
				amtFormat = PennantApplicationUtil.getAmountFormate(currFormatter);

				BigDecimal convrtnRate = CurrencyUtil
						.parse(CalculationUtil.getConvertedAmount(AccountConstants.CURRENCY_USD,
								this.currencyType.getValue(), new BigDecimal(100)), currFormatter);
				this.conversionRate.setValue(convrtnRate);
				getCreditReviewDetails().setConversionRate(convrtnRate);
				refreshTabs();
				doSetFieldProperties();
			}
		}
		setLables();
		logger.debug("Leaving " + event.toString());
	}

	public void setTabs() {
		logger.debug("Entering");

		this.tabpanelsBoxIndexCenter.setId("tabpanelsBoxIndexCenter");
		int ratioTabCount = 1;
		prevAuditPeriod = getCreditApplicationReviewService().getCreditReviewAuditPeriodByAuditYear(
				this.custID.getValue(), String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear()) - 1),
				this.creditReviewDetails.getAuditPeriod(), false, "_View");
		// Create Tabs based on the finCreditReview Category and Customer
		// Category Type
		for (FinCreditRevCategory finCreditRevCategory : listOfFinCreditRevCategory) {

			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
			creditReviewSubCtgDetails.setMainGroup("T");
			creditReviewSubCtgDetails.setMainGroupDesc(finCreditRevCategory.getCategoryDesc());
			creditReviewSubCtgDetails.setTabDesc(finCreditRevCategory.getCategoryDesc());
			creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetails);

			if (FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevCategory.getRemarks())) {
				this.ratioFlag = false;
			} else {
				this.ratioFlag = true;
			}
			Tab tab = new Tab();
			tab.setId("tab_" + finCreditRevCategory.getCategoryId());
			tab.setLabel(finCreditRevCategory.getCategoryDesc());
			tab.setParent(this.tabsIndexCenter);
			tabsIndexCenter.setId("tabsIndexCenter");
			Tabpanel tabPanel = new Tabpanel();
			tabPanel.setHeight("100%");// 425px

			tabPanel.setId("tabPanel_" + finCreditRevCategory.getCategoryId());
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			if (ratioTabCount == listOfFinCreditRevCategory.size()) {
				tab.setAttribute("isRatioTab", true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectRatiosTab");
			}
			ratioTabCount++;
			render(finCreditRevCategory, setListToTab(tabPanel, finCreditRevCategory));
		}
		/*
		 * appendDocumentDetailTab(); appendRecommendDetailTab();
		 */
		logger.debug("Leaving");
	}

	public void onSelectRatiosTab(ForwardEvent event) {
		logger.debug("Entering");
		Tabpanel tabpanel;
		String ratioPanelId = "tabPanel_"
				+ listOfFinCreditRevCategory.get(listOfFinCreditRevCategory.size() - 1).getCategoryId();
		if (tabpanelsBoxIndexCenter.getFellowIfAny(ratioPanelId) != null) {
			tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(ratioPanelId);
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
			FinCreditRevCategory finCreditRevCategory = listOfFinCreditRevCategory
					.get(listOfFinCreditRevCategory.size() - 1);
			render(finCreditRevCategory, setListToTab(tabpanel, finCreditRevCategory));
			setLables();
		}
		logger.debug("Leaving");
	}

	public void appendDocumentDetailTab() {
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("documentsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		// tabpanel.setHeight(this.borderLayoutHeight-0 + "px");
		tabpanel.setHeight("100%");

		Toolbar tb_addNewDoc = new Toolbar();
		tb_addNewDoc.setAlign("end");
		Button addNewDocument = new Button("New");
		addNewDocument.setId("button_CreditApplicationReviewDialog_addNewDoc");
		addNewDocument.setAttribute("docData", this.customer);
		addNewDocument.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_addNewDoc"));
		ComponentsCtrl.applyForward(addNewDocument, "onClick=onClick$addNewDoc");
		addNewDocument.setParent(tb_addNewDoc);
		tb_addNewDoc.setParent(tabpanel);
		Listbox lb_custDocsList = getCustDocumentsListBox(tabpanel);

		Listitem listItem = null;
		Listcell listCell = null;
		int i = 0;

		for (CustomerDocument customerDocument : custDocList) {

			listItem = new Listitem();
			listItem.setId("li_" + customerDocument.getCustDocType() + i);

			listCell = new Listcell();
			listCell.setId("lc_" + customerDocument.getLovDescCustDocCategory() + i);
			Label label_idType = new Label(customerDocument.getLovDescCustDocCategory());
			label_idType.setParent(listCell);
			listCell.setParent(listItem);

			listCell = new Listcell();
			listCell.setId("lc_" + customerDocument.getLovDescCustDocIssuedCountry() + i);
			Label label_issuedCuntry = new Label(customerDocument.getLovDescCustDocIssuedCountry());
			label_issuedCuntry.setParent(listCell);
			listCell.setParent(listItem);

			listCell = new Listcell();
			listCell.setId("lc_" + customerDocument.getCustDocTitle() + i);
			Label label_idNumber = new Label(customerDocument.getCustDocTitle());
			label_idNumber.setParent(listCell);
			listCell.setParent(listItem);

			/*
			 * listCell = new Listcell(); listCell.setId("lc_"+customerDocument.getRecordStatus()+i); Label
			 * label_recordStatus = new Label(customerDocument.getRecordStatus());
			 * label_recordStatus.setParent(listCell); listCell.setParent(listItem);
			 * 
			 * listCell = new Listcell(); listCell.setId("lc_"+customerDocument.getRecordType()+i); Label
			 * label_recordType = new Label(customerDocument.getRecordType()); label_recordType.setParent(listCell);
			 * listCell.setParent(listItem);
			 */

			listCell = new Listcell();
			listCell.setId("lc_" + customerDocument.getRecordType() + i);
			Label label_recordType = new Label(customerDocument.getRecordType());
			label_recordType.setParent(listCell);
			listCell.setParent(listItem);

			listItem.setParent(lb_custDocsList);
			listItem.setAttribute("docData", customerDocument);
			ComponentsCtrl.applyForward(listItem, "onDoubleClick=onCustomerDocItemDoubleClicked");
			i++;
		}
		logger.debug("Leaving");
	}

	public Listbox getCustDocumentsListBox(Tabpanel tabPanel) {

		Div div = new Div();
		// div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px")))
		// - 0 + "px");
		div.setHeight("100%");
		Listbox listbox = new Listbox();
		// listbox.setVflex(true);
		listbox.setSpan(true);

		// listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px")))
		// - 0 + "px");
		listbox.setHeight("100%");

		div.setId("div_custDocsList");
		listbox.setId("lb_custDocsList");

		Listhead listHead = new Listhead();
		listHead.setId("listHead_custDocsList");

		Listheader listheader_CustDocType = new Listheader(Labels.getLabel("listheader_CustDocType.label", ""));
		listheader_CustDocType.setHflex("min");
		listheader_CustDocType.setParent(listHead);

		Listheader listheader_custDocIssuedCountry = new Listheader(
				Labels.getLabel("listheader_CustDocIssuedCountry.label"));
		listheader_custDocIssuedCountry.setHflex("min");
		listheader_custDocIssuedCountry.setParent(listHead);

		Listheader listheader_custDocTitle = new Listheader(Labels.getLabel("listheader_CustDocTitle.label"));
		listheader_custDocTitle.setHflex("min");
		listheader_custDocTitle.setParent(listHead);

		/*
		 * Listheader listheader_recordStatus= new Listheader(Labels.getLabel(
		 * "label_CustomerDocumentSearch_RecordStatus.value", new String[]{"2012"}));
		 * listheader_recordStatus.setHflex("min"); listheader_recordStatus.setParent(listHead);
		 * 
		 * Listheader listheader_recordType = new Listheader(Labels.getLabel(
		 * "label_CustomerDocumentSearch_RecordType.value")); listheader_recordType.setHflex("min");
		 * listheader_recordType.setParent(listHead);
		 */

		listHead.setParent(listbox);
		listbox.setParent(div);
		div.setParent(tabPanel);
		listbox.setSizedByContent(true);

		logger.debug("Leaving");
		return listbox;
	}

	public void onCustomerDocItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");
		// get the selected invoiceHeader object

		final Listitem item = (Listitem) event.getOrigin().getTarget();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerDocument customerDocument = (CustomerDocument) item.getAttribute("docData");
			customerDocument.setLovDescCustCIF(this.custCIF.getValue());
			if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerDocument", customerDocument);
				map.put("creditApplicationReviewDialogCtrl", this);
				map.put("roleCode", getRole());

				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$addNewDoc(ForwardEvent event) {

		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setNewRecord(true);
		customerDocument.setWorkflowId(0);
		customerDocument.setCustID(this.customer.getCustID());
		customerDocument.setLovDescCustCIF(this.customer.getCustCIF());
		customerDocument.setLovDescCustShrtName(this.customer.getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerDocument", customerDocument);
		map.put("creditApplicationReviewDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void appendRecommendDetailTab() throws InterruptedException {
		logger.debug("Entering");
		Tab tab = new Tab("Comments & Recommendations");
		tab.setId("memoDetailTab");
		tabsIndexCenter.appendChild(tab);
		ComponentsCtrl.applyForward(tab, "onSelect=onSelectRecommendDetailTab");

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("memoDetailTabPanel");
		tabpanel.setHeight(this.borderLayoutHeight - 0 + "px");
		tabpanel.setStyle("overflow:auto");
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("isFinanceNotes", true);
		map.put("notes", getNotes(this.creditReviewDetails));
		map.put("notesList", notesList);
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", tabpanel, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void insertSubCategory(FinCreditRevSubCategory aFinCreditRevSubCategory) {

		Listbox listBox;
		listBox = (Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0);

		if (listBox != null) {
			Listitem listItemNew;
			Listitem listItemOld;
			Listcell listCell;

			listItemOld = (Listitem) listBox.getSelectedItem();

			if (listItemOld.getListgroup() != null) {
				Listgroup lg = (Listgroup) listItemOld.getListgroup();
				lg.setParent(listBox);
			}

			listItemNew = new Listitem();
			listItemNew.setId("li" + aFinCreditRevSubCategory.getSubCategoryCode());

			listCell = new Listcell();
			listCell.setLabel(aFinCreditRevSubCategory.getSubCategoryDesc());
			listCell.setParent(listItemNew);

			listCell = new Listcell();
			Decimalbox decimalbox = new Decimalbox();
			decimalbox.setId("db" + aFinCreditRevSubCategory.getSubCategoryCode());
			decimalbox.setAttribute("data", aFinCreditRevSubCategory);
			decimalbox.setAttribute("ListBoxdata", listBox);
			decimalbox.setFormat(amtFormat);
			decimalbox.setMaxlength(18);
			if ((listOfFinCreditRevCategory.size() > 3 && tabBoxIndexCenter.getSelectedTab().getId().endsWith("4"))
					|| (listOfFinCreditRevCategory.size() == 3
							&& tabBoxIndexCenter.getSelectedTab().getId().endsWith("7"))) {
				decimalbox.setDisabled(true);
				decimalbox.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF;border-width: 0;");
			}
			decimalbox.setParent(listCell);
			ComponentsCtrl.applyForward(decimalbox, "onChange=onChange$auditedValue");
			listCell.setParent(listItemNew);

			listCell = new Listcell();
			if (!tabBoxIndexCenter.getSelectedTab().getId().endsWith("3")
					|| !tabBoxIndexCenter.getSelectedTab().getId().endsWith("4")
					|| !tabBoxIndexCenter.getSelectedTab().getId().endsWith("7")) {
				listCell.setLabel("--");
			} else {
				listCell.setLabel("");
			}

			listCell.setParent(listItemNew);
			listBox.insertBefore(listItemNew, listItemOld);
		}
	}

	public Listbox setListToTab(Tabpanel tabPanel, FinCreditRevCategory fcrc) {
		logger.debug("Entering");

		Div div = new Div();
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0, getBorderLayoutHeight().indexOf("px"))) - 0
				+ "px");
		// div.setHeight("100%");
		Listbox listbox = new Listbox();
		// listbox.setVflex(true);
		listbox.setSpan(true);

		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0, getBorderLayoutHeight().indexOf("px")))
				- 0 + "px");
		// listbox.setHeight("100%");
		creditReviewSubCtgDetailsHeaders.setHeader("T");

		div.setId("div_" + fcrc.getCategoryId());
		listbox.setId("lb_" + fcrc.getCategoryId());

		Auxhead auxHead = new Auxhead();
		auxHead.setId("auxHead_" + fcrc.getCategoryId());

		Listhead listHead = new Listhead();
		listHead.setSizable(true);
		listHead.setId("listHead_" + fcrc.getCategoryId());

		Auxheader auxSubCategory = new Auxheader("");
		auxSubCategory.setColspan(2);
		auxSubCategory.setAlign("center");
		auxSubCategory.setParent(auxHead);

		Listheader listheader_addNewRecord = new Listheader();
		listheader_addNewRecord.setHflex("40px");
		listheader_addNewRecord.setAlign("center");
		listheader_addNewRecord.setVisible(false);
		listheader_addNewRecord.setParent(listHead);

		Listheader listheader_subCategoryCode = new Listheader(Labels.getLabel("listheader_categories.value"));
		listheader_subCategoryCode.setHflex("min");
		listheader_subCategoryCode.setParent(listHead);

		String audQual = "";
		String audQualRePort = "";
		String auxHeaderCurYerLabel = "";
		if (getCreditReviewDetails().getAuditType() != null) {
			audQualRePort = Labels.getLabel("UnAudUnQual_Months",
					new String[] { getCreditReviewDetails().getAuditType(),
							getCreditReviewDetails().isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED
									: FacilityConstants.CREDITREVIEW_UNQUALIFIED,
							String.valueOf(getCreditReviewDetails().getAuditPeriod()) });
			audQual = getCreditReviewDetails().getAuditType() + "/"
					+ (getCreditReviewDetails().isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED
							: FacilityConstants.CREDITREVIEW_UNQUALIFIED);
			auxHeaderCurYerLabel = getCreditReviewDetails().getAuditYear() + " - "
					+ String.valueOf(getCreditReviewDetails().getAuditPeriod()) + FacilityConstants.MONTH;
			creditReviewSubCtgDetailsHeaders.setCurYearAuditValueHeader(audQualRePort);
		} else {
			String qualUnQual = this.qualifiedUnQualified.getSelectedItem().getValue().toString();
			audQualRePort = Labels.getLabel("UnAudUnQual_Months",
					new String[] {
							this.auditType.getSelectedItem() == null ? FacilityConstants.CREDITREVIEW_AUDITED
									: this.auditType.getSelectedItem().getValue().toString(),
							qualUnQual, String.valueOf(getCreditReviewDetails().getAuditPeriod()) });

			audQual = this.auditType.getSelectedItem().getValue().toString() + "/" + qualUnQual;
			auxHeaderCurYerLabel = this.auditedYear.getValue() + " - "
					+ String.valueOf(getCreditReviewDetails().getAuditPeriod()) + FacilityConstants.MONTH;
			creditReviewSubCtgDetailsHeaders.setCurYearAuditValueHeader(audQualRePort);
		}

		Label audQualLabel = new Label(audQual);
		audQualLabel.setId(getId(fcrc.getCategoryDesc()));
		audQualLabel.setStyle("font-weight:bold");

		Auxheader auxHeader_curYear = new Auxheader();
		auxHeader_curYear.setColspan(2);
		auxHeader_curYear.setLabel(auxHeaderCurYerLabel);
		auxHeader_curYear.setAlign("center");
		auxHeader_curYear.setParent(auxHead);

		Listheader listheader_audAmt = new Listheader();
		// listheader_audAmt.setId(getId(fcrc.getCategoryDesc()));
		listheader_audAmt.setHflex("min");
		listheader_audAmt.setAlign("center");
		audQualLabel.setMultiline(true);
		audQualLabel.setParent(listheader_audAmt);
		listheader_audAmt.setParent(listHead);

		Listheader listheader_curUSConvrtn = new Listheader(AccountConstants.CURRENCY_USD);
		listheader_curUSConvrtn.setHflex("85px");
		listheader_curUSConvrtn.setAlign("center");
		listheader_curUSConvrtn.setId("curCnvrtn" + getId(fcrc.getCategoryDesc()));
		listheader_curUSConvrtn.setParent(listHead);
		listheader_curUSConvrtn.setVisible(false);

		Listheader listheader_breakDown = new Listheader(Labels.getLabel("listheader_breakDowns.value", ""));
		creditReviewSubCtgDetailsHeaders.setCurYearBreakDownHeader(Labels.getLabel("listheader_breakDowns.value", "")
				+ "_" + this.creditReviewDetails.getAuditPeriod() + FacilityConstants.MONTH);

		listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
		listheader_breakDown.setHflex("min");
		listheader_breakDown.setAlign("center");
		listheader_breakDown.setParent(listHead);

		String prevPeriodLabel = "_" + String.valueOf(prevAuditPeriod) + FacilityConstants.MONTH;
		String prevAudOrUnAudReport = "";
		String prevAudOrUnAud = "";

		String qualOrUnqual = "";
		String auxHeadPrevYrLabel = String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear()) - 1)
				+ prevPeriodLabel;

		FinCreditReviewDetails finCreditReviewDetails = this.creditApplicationReviewService
				.getCreditReviewDetailsByCustIdAndYear(this.custID.getValue(),
						String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear()) - 1), "_VIEW");

		if (finCreditReviewDetails != null && StringUtils.isNotBlank(finCreditReviewDetails.getAuditType())) {
			qualOrUnqual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED
					: FacilityConstants.CREDITREVIEW_UNQUALIFIED;
			prevAudOrUnAud = finCreditReviewDetails.getAuditType() + "/" + qualOrUnqual;
			prevAudOrUnAudReport = finCreditReviewDetails.getAuditType() + "/" + qualOrUnqual + " "
					+ String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear()) - 1) + prevPeriodLabel;
		} else {
			prevAudOrUnAudReport = Labels.getLabel("listheader_audAmt1.value",
					new String[] { String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear()) - 1) })
					+ prevPeriodLabel;
			prevAudOrUnAud = Labels.getLabel("listheader_auxAudAmt1.value");
		}

		Auxheader auxHeader_prevYear = new Auxheader();
		auxHeader_prevYear.setLabel(auxHeadPrevYrLabel);
		auxHeader_prevYear.setAlign("center");
		auxHeader_prevYear.setParent(auxHead);

		if (fcrc.getCategoryId() == 3 || fcrc.getCategoryId() == 4 || fcrc.getCategoryId() == 7) {
			auxHeader_curYear.setColspan(2);
			auxHeader_prevYear.setColspan(3);
		} else {
			auxHeader_curYear.setColspan(3);
			auxHeader_prevYear.setColspan(4);
		}

		Listheader listheader_previousAudAmt = new Listheader();
		Label prevAudQualLabel = new Label(prevAudOrUnAud);
		prevAudQualLabel.setStyle("font-weight: bold;font-size: 12px;");
		prevAudQualLabel.setMultiline(true);
		prevAudQualLabel.setParent(listheader_previousAudAmt);
		creditReviewSubCtgDetailsHeaders.setPreYearAuditValueHeader(prevAudOrUnAudReport);

		listheader_previousAudAmt.setHflex("min");
		listheader_previousAudAmt.setAlign("center");
		listheader_previousAudAmt.setParent(listHead);
		listheader_previousAudAmt.setVisible(false);

		Listheader listheader_prevcurUSConvrtn = new Listheader(AccountConstants.CURRENCY_USD);
		listheader_prevcurUSConvrtn.setHflex("85px");
		listheader_prevcurUSConvrtn.setAlign("center");
		listheader_prevcurUSConvrtn.setId("prevCnvrtn" + getId(fcrc.getCategoryDesc()));
		listheader_prevcurUSConvrtn.setParent(listHead);
		listheader_prevcurUSConvrtn.setVisible(false);

		int currentAuditYear = Integer.parseInt(this.creditReviewDetails.getAuditYear());

		Listheader listheader_previousBreakDown = new Listheader(Labels.getLabel("listheader_breakDowns.value"));
		creditReviewSubCtgDetailsHeaders.setPreYearBreakDownHeader(
				Labels.getLabel("listheader_breakDown2.value", new String[] { String.valueOf(currentAuditYear - 1) })
						+ prevPeriodLabel);
		listheader_previousBreakDown.setHflex("min");
		listheader_previousBreakDown.setAlign("center");
		listheader_previousBreakDown.setVisible(fcrc.isBrkdowndsply());
		listheader_previousBreakDown.setParent(listHead);

		Listheader listheader_percentChange = new Listheader(Labels.getLabel("listheader_percentChange.value"));
		creditReviewSubCtgDetailsHeaders.setCurYearPerHeader(String.valueOf(currentAuditYear) + "_"
				+ String.valueOf(currentAuditYear - 1) + Labels.getLabel("listheader_percentChange.value"));
		listheader_percentChange.setHflex("min");
		listheader_percentChange.setAlign("center");
		listheader_percentChange.setParent(listHead);
		listheader_percentChange.setVisible(false);

		auxHead.setParent(listbox);
		listHead.setParent(listbox);
		listbox.setParent(div);
		listbox.setAttribute("isRatio", fcrc.getRemarks());
		div.setParent(tabPanel);

		for (CreditReviewSubCtgDetails creditReviewSubCtgDetails : creditReviewSubCtgDetailsList) {
			if ("T".equals(creditReviewSubCtgDetails.getMainGroup())) {
				creditReviewSubCtgDetails
						.setCurYearAuditValueHeader(creditReviewSubCtgDetailsHeaders.getCurYearAuditValueHeader());
				creditReviewSubCtgDetails
						.setCurYearBreakDownHeader(creditReviewSubCtgDetailsHeaders.getCurYearBreakDownHeader());
				creditReviewSubCtgDetails
						.setPreYearAuditValueHeader(creditReviewSubCtgDetailsHeaders.getPreYearAuditValueHeader());
				creditReviewSubCtgDetails
						.setPreYearBreakDownHeader(creditReviewSubCtgDetailsHeaders.getPreYearBreakDownHeader());
				creditReviewSubCtgDetails.setCurYearPerHeader(creditReviewSubCtgDetailsHeaders.getCurYearPerHeader());
			}
		}

		// creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetailsHeaders);
		logger.debug("Leaving");
		listbox.setSizedByContent(true);
		return listbox;

	}

	public void onChange$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		/*
		 * this.custCIF.clearErrorMessage();
		 * 
		 * Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), getFilterList());
		 * 
		 * if(customer == null) { this.custShrtName.setValue("");
		 * 
		 * if(this.tabpanelsBoxIndexCenter.getChildren() != null){ this.tabpanelsBoxIndexCenter.getChildren().clear(); }
		 * if(this.tabsIndexCenter.getChildren()!= null){ this.tabsIndexCenter.getChildren().clear(); } throw new
		 * WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] {
		 * Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value")}) ); } else { doSetCustomer(customer,
		 * null); }
		 */

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	private void onload() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject", this.newSearchObject);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custCIF.setTooltiptext(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.creditRevCode = aCustomer.getLovDescCustCtgType();
		this.newSearchObject = newSearchObject;
		String category = "";

		if (aCustomer.getCustCtgCode().equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_SME)) {
			category = PennantConstants.PFF_CUSTCTG_SME;
		} else if (aCustomer.getCustCtgCode().equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_CORP)) {
			category = PennantConstants.PFF_CUSTCTG_CORP;
		} else {
			category = PennantConstants.PFF_CUSTCTG_INDIV;
		}
		if (this.tabpanelsBoxIndexCenter.getChildren().size() > 0) {
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if (this.tabsIndexCenter.getChildren().size() > 0) {
			this.tabsIndexCenter.getChildren().clear();
		}

		String type = "";
		if (creditReviewDetails.isNewRecord()) {
			creditReviewDetails.setConversionRate(
					this.conversionRate.getValue() == null ? BigDecimal.ZERO : this.conversionRate.getValue());
		}
		if ((!StringUtils.trimToEmpty(creditReviewDetails.getRecordStatus())
				.equals(PennantConstants.RCD_STATUS_APPROVED)
				&& StringUtils.trimToEmpty(creditReviewDetails.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_NEW))
				|| StringUtils.trimToEmpty(creditReviewDetails.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_UPD)) {
			type = "_Temp";
		}

		// Get FinCredit Categories based on the Customer Category Type
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService
				.getCreditRevCategoryByCreditRevCode(this.creditRevCode);
		prvYearValuesMap = new HashMap<String, BigDecimal>();
		this.creditReviewSummaryList = this.creditApplicationReviewService
				.getLatestCreditReviewSummaryByCustId(this.custID.longValue());

		int audityear = Integer.parseInt(creditReviewDetails.getAuditYear());

		creditReviewSummaryMap = getCreditApplicationReviewService().getListCreditReviewSummaryByCustId2(
				aCustomer.getCustID(), 2, audityear - 1, category, creditReviewDetails.getAuditPeriod(), false,
				"_VIEW");
		// If previous years data map is empty, than get data from current year
		// data and to set that empty map with
		// empty values for to avoid exceptions and improve performance.
		boolean isCurrentDataMapAvil = false;
		if (creditReviewSummaryMap == null || creditReviewSummaryMap.isEmpty()) {
			creditReviewSummaryMap = getCreditApplicationReviewService().getListCreditReviewSummaryByCustId2(
					aCustomer.getCustID(), 0, audityear, category, creditReviewDetails.getAuditPeriod(), true, type);
			isCurrentDataMapAvil = true;
			if (creditReviewSummaryMap != null && creditReviewSummaryMap.size() > 0) {
				creditReviewSummaryMap.put(String.valueOf(audityear - 1),
						creditReviewSummaryMap.get(String.valueOf(audityear)));
				creditReviewSummaryMap.put(String.valueOf(audityear - 2),
						creditReviewSummaryMap.get(String.valueOf(audityear)));
			}
		}
		prv1YearValuesMap = new HashMap<String, BigDecimal>();
		prv1YearValuesMap.put("auditYear", BigDecimal.valueOf(audityear - 2));
		creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear - 2));
		if (this.creditReviewSummaryList != null && this.creditReviewSummaryList.size() > 0) {
			prv1YearValuesMap.putAll(extValuesMap);
			for (int k = 0; k < this.creditReviewSummaryList.size(); k++) {
				// to set map and engine default values for which is not
				// available previous years
				if (isCurrentDataMapAvil) {
					prv1YearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(), BigDecimal.ZERO);
					engine.put("Y" + (audityear - 2) + this.creditReviewSummaryList.get(k).getSubCategoryCode(),
							BigDecimal.ZERO);
				} else {
					// if data available to set data for it
					prv1YearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter)
									.setScale(this.currFormatter, RoundingMode.HALF_DOWN));
					engine.put("Y" + (audityear - 2) + this.creditReviewSummaryList.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter));
				}
			}
			// External fields to put default data for map and engine
			if (extValuesMap != null && extValuesMap.size() > 0) {
				for (Entry<String, BigDecimal> entry : extValuesMap.entrySet()) {
					if (entry.getKey().startsWith("EXT_")) {
						engine.put(entry.getKey(), extValuesMap.get(entry.getKey()));
						prv1YearValuesMap.put(entry.getKey(), extValuesMap.get(entry.getKey()));
					}
				}
			}
		}

		if (prv1YearValuesMap != null && prv1YearValuesMap.size() > 1) {
			setData(prv1YearValuesMap);
		}

		// creditReviewSummaryMap= getCreditApplicationReviewService().
		// getListCreditReviewSummaryByCustId2(aCustomer.getCustID(), 0,
		// audityear-1,
		// category, creditReviewDetails.getAuditPeriod(), false, "");

		boolean isPrevYearSummayNull = false;
		this.creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear - 1));
		if (this.creditReviewSummaryList == null) {
			isPrevYearSummayNull = true;
		}
		prvYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear - 1));
		if (!isPrevYearSummayNull && this.creditReviewSummaryList.size() > 0) {
			prvYearValuesMap.putAll(extValuesMap);
			for (int k = 0; k < this.creditReviewSummaryList.size(); k++) {
				prvYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter)
								.setScale(this.currFormatter, RoundingMode.HALF_DOWN));
				// summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),this.creditReviewSummaryList.get(k));
				engine.put("Y" + (audityear - 1) + this.creditReviewSummaryList.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter));
			}
			// External fields to put default data for map and engine
			if (extValuesMap != null && extValuesMap.size() > 0) {
				for (Entry<String, BigDecimal> entry : extValuesMap.entrySet()) {
					if (entry.getKey().startsWith("EXT_")) {
						engine.put(entry.getKey(), extValuesMap.get(entry.getKey()));
						prvYearValuesMap.put(entry.getKey(), extValuesMap.get(entry.getKey()));
					}
				}
			}
		}

		if (prvYearValuesMap.size() > 28) {
			setData(prvYearValuesMap);
		}
		curYearValuesMap.putAll(extValuesMap);
		// if(!this.creditReviewDetails.isNewRecord()){
		// below flag is previous years data not available.It is true
		if (!isCurrentDataMapAvil) {
			creditReviewSummaryMap = getCreditApplicationReviewService().getListCreditReviewSummaryByCustId2(
					aCustomer.getCustID(), 0, audityear, category, creditReviewDetails.getAuditPeriod(), true, type);
		}

		if (creditReviewSummaryMap.get(creditReviewDetails.getAuditYear()) != null
				&& creditReviewSummaryMap.size() > 0) {
			this.creditReviewSummaryList = creditReviewSummaryMap.get(creditReviewDetails.getAuditYear());
			curYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear));
			for (int k = 0; k < this.creditReviewSummaryList.size(); k++) {
				curYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter));
				summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),
						this.creditReviewSummaryList.get(k));
				engine.put("Y" + audityear + this.creditReviewSummaryList.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(this.creditReviewSummaryList.get(k).getItemValue(), this.currFormatter));
				if (isPrevYearSummayNull) {
					prvYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
					engine.put("Y" + (audityear - 1) + this.creditReviewSummaryList.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
					if (prvYearValuesMap != null && prvYearValuesMap.size() > 0) {
						setData(prvYearValuesMap);
					}
				}
			}
		} else {
			for (int k = 0; k < this.listOfFinCreditRevSubCategory.size(); k++) {
				curYearValuesMap.put(this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
				engine.put("Y" + audityear + this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
						CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
				if (isPrevYearSummayNull) {
					prvYearValuesMap.put(this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
					engine.put("Y" + (audityear - 1) + this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
							CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
					if (prvYearValuesMap != null && prvYearValuesMap.size() > 0 && !creditReviewDetails.isNewRecord()) {
						setData(prvYearValuesMap);
					}
				}
			}
		}
		// If current year data is available and not new than set
		if (curYearValuesMap != null && !creditReviewDetails.isNewRecord()) {
			setData(curYearValuesMap);
		}
		// }
		setTabs();
		logger.debug("Leaving");
	}

	@SuppressWarnings("unused")
	public void render(FinCreditRevCategory fcrc, Listbox listbox) {
		logger.debug("Entering");

		Listitem item = null;
		String recordType = null;
		boolean isRatio = false;

		if (FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrc.getRemarks())) {
			isRatio = true;
		}
		listbox.setAttribute("fcrc", fcrc);

		// Get all the SubcategoryCodes for the category ID
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory finCreditRevSubCategory = null;
			finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (finCreditRevSubCategory.getCategoryId() == fcrc.getCategoryId()) {
				if (!isRatio) {
					finCreditRevSubCategory.setMainSubCategoryCode(fcrc.getCategoryDesc());
				}
				fillRenderer(finCreditRevSubCategory, listbox, item, false, fcrc.getCategoryDesc());
			}
		}
		getTotalLiabAndAssetsDifference();
		logger.debug("Leaving");
	}

	public void onClick$addNewRecord(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem oldItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");

		Listbox listbox = (Listbox) oldItem.getParent();
		getListItemsDisabled(listbox, true);

		// Create a new Item to enter a new SubCategory
		Listitem newItem = getNewItem(finCreditRevSubCategory, oldItem, false);
		listbox.insertBefore(newItem, oldItem.getNextSibling());

		logger.debug("Leaving" + event.toString());

	}

	public Listitem getNewItem(FinCreditRevSubCategory finCreditRevSubCategory, Listitem oldItem, boolean isEdit) {
		logger.debug("Leaving");

		Listitem newItem = new Listitem();
		Listcell lc;
		// Add Image
		lc = new Listcell();
		Image lcImage;

		lc.setId("lc_ImgBtnDelete");
		lcImage = new Image("/images/icons/Old/remove.png");
		lcImage.setParent(lc);
		ComponentsCtrl.applyForward(lcImage, "onClick=onClick$removeRecord");
		if (isEdit) {
			Space space = new Space();
			space.setSpacing("8px");
			space.setParent(lc);
			lcImage = new Image("/images/icons/Old/cancel.png");
			lcImage.setParent(lc);
			ComponentsCtrl.applyForward(lcImage, "onClick=onClick$cancelRecord");

		}
		lc.setParent(newItem);

		// Subcategory Code
		lc = new Listcell();
		Uppercasebox tb_subCategoryCode = new Uppercasebox();
		tb_subCategoryCode.setStyle(" text-transform: uppercase;");
		tb_subCategoryCode.setId("tb_NewSubCategoryCode");
		if (isEdit) {
			tb_subCategoryCode.setValue(finCreditRevSubCategory.getSubCategoryCode());
		}
		tb_subCategoryCode.setWidth("80px");
		tb_subCategoryCode.setErrorMessage("");
		tb_subCategoryCode.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		tb_subCategoryCode.setParent(lc);

		Label label = new Label("_");
		label.setParent(lc);

		// Subcategory description
		Textbox tb_subCategoryDesc = new Textbox();
		tb_subCategoryDesc.setId("tb_NewSubCategoryDesc");
		if (isEdit) {
			tb_subCategoryDesc.setValue(finCreditRevSubCategory.getSubCategoryDesc());
		}
		tb_subCategoryDesc.setWidth("120px");
		tb_subCategoryDesc.setConstraint(new PTStringValidator(
				Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryDesc.value"), null, true));
		tb_subCategoryDesc.setParent(lc);
		Space space = new Space();
		space.setSpacing("60px");
		space.setParent(lc);
		label = new Label("Main Category Code ");
		label.setParent(lc);
		lc.setParent(newItem);

		// Main Category Code
		lc = new Listcell();
		Combobox cb_MainCategoryCode = new Combobox();
		cb_MainCategoryCode.setId("cb_mainCategoryCode");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		cb_MainCategoryCode.appendChild(comboitem);
		cb_MainCategoryCode.setSelectedItem(comboitem);

		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory aFinCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (aFinCreditRevSubCategory.getCategoryId() == finCreditRevSubCategory.getCategoryId()
					&& aFinCreditRevSubCategory.getSubCategoryItemType()
							.equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
				comboitem = new Comboitem();
				comboitem.setValue(aFinCreditRevSubCategory.getSubCategoryCode());
				comboitem.setLabel(aFinCreditRevSubCategory.getSubCategoryDesc());
				comboitem.setParent(cb_MainCategoryCode);
				if (isEdit && aFinCreditRevSubCategory.getSubCategoryCode()
						.equals(finCreditRevSubCategory.getMainSubCategoryCode())) {
					cb_MainCategoryCode.setSelectedItem(comboitem);
				}
				cb_MainCategoryCode.appendChild(comboitem);

			}
		}
		cb_MainCategoryCode.setWidth("180px");
		cb_MainCategoryCode.setConstraint(new PTStringValidator(
				Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value"), null, true));
		cb_MainCategoryCode
				.setVisible(!FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks()));
		cb_MainCategoryCode.setParent(lc);
		lc.setParent(newItem);

		lc = new Listcell();
		Combobox cb_MathOperation = new Combobox();
		cb_MathOperation.setId("cb_MathOperation");
		fillComboBox(cb_MathOperation, "", PennantStaticListUtil.getCreditReviewRuleOperator(), "");
		cb_MathOperation.setWidth("180px");
		cb_MathOperation.setConstraint(new PTStringValidator(Labels.getLabel("listheader_Operation"), null, true));
		cb_MathOperation.setParent(lc);
		lc.setParent(newItem);

		lc = new Listcell();
		Button btnForSave = new Button("Save");
		btnForSave.setId("btnForSave_NewRecord");
		btnForSave.addForward("onClick", this.window_CreditApplicationReviewDialog, "onClick$btnForSave",
				listOfFinCreditRevSubCategory);
		btnForSave.setParent(lc);
		lc.setParent(newItem);

		lc = new Listcell();
		lc.setParent(newItem);

		lc = new Listcell();
		lc.setParent(newItem);

		newItem.setParent(oldItem.getParent());
		oldItem.getParent().insertBefore(newItem, oldItem.getNextSibling());
		if (isEdit) {
			oldItem.getParent().removeChild(oldItem);
			newItem.setAttribute("finData", finCreditRevSubCategory);
		}
		logger.debug("Leaving");

		return newItem;
	}

	public void onClick$removeRecord(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Listitem oldItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Listbox listbox = (Listbox) oldItem.getParent();
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");
		listbox.removeItemAt(oldItem.getIndex());
		if (aFinCreditRevSubCategory != null) {
			removeRecordFromFinSubCategoryList(aFinCreditRevSubCategory);
			setData(curYearValuesMap);
			getListboxDetails(listbox);
		}
		getListItemsDisabled(listbox, false);

		logger.debug("Leaving" + event.toString());
	}

	public void getListboxDetails(Listbox listbox) {
		logger.debug("Entering");

		Decimalbox db_Amount = null;
		BigDecimal breakDown = BigDecimal.ZERO;
		Label label_BreakDown = null;
		Label label_PrecentChange = null;
		Label label_convrsnUSD = null;
		FinCreditRevSubCategory finCreditRevSubCategory;
		for (Listitem item : listbox.getItems()) {
			if (!(item instanceof Listgroup)) {

				/***************** Amount ***************/
				finCreditRevSubCategory = (FinCreditRevSubCategory) item.getAttribute("finData");
				db_Amount = (Decimalbox) item.getFellowIfAny("db" + finCreditRevSubCategory.getSubCategoryCode());
				if (db_Amount != null) {
					db_Amount.setValue(curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()));
				}

				/***************************
				 * Current Year US$ Conversion
				 ************************/
				/*
				 * label_convrsnUSD = (Label) item.getFellowIfAny("uSD1_"+finCreditRevSubCategory.
				 * getSubCategoryCode()); BigDecimal curAmt = db_Amount.getValue() != null ? db_Amount.getValue() :
				 * BigDecimal.ZERO; BigDecimal convrsnPrice = BigDecimal.ZERO;
				 * if(creditReviewDetails.getConversionRate() != null && creditReviewDetails.getConversionRate() !=
				 * BigDecimal.ZERO){ convrsnPrice = curAmt.divide(creditReviewDetails.getConversionRate(),
				 * FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN); } else
				 * if(conversionRate.getValue() != null && conversionRate.getValue() != BigDecimal.ZERO){ convrsnPrice =
				 * curAmt.divide(conversionRate.getValue()); } label_convrsnUSD.setValue(PennantAppUtil.formatAmount(
				 * convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false));
				 */

				/***************************
				 * Break Down
				 ************************/
				label_BreakDown = (Label) item.getFellowIfAny("rLabel" + finCreditRevSubCategory.getSubCategoryCode());
				if (label_BreakDown != null) {
					breakDown = (BigDecimal) (curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS
							+ finCreditRevSubCategory.getSubCategoryCode()) == null ? BigDecimal.ZERO
									: curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS
											+ finCreditRevSubCategory.getSubCategoryCode()));
					label_BreakDown.setValue(CurrencyUtil.format(breakDown, 2));
				}

				/***************************
				 * % Change In Break Down
				 ************************/
				label_PrecentChange = (Label) item
						.getFellowIfAny("percent" + finCreditRevSubCategory.getSubCategoryCode());

				BigDecimal prevAuditAmt = prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) != null
						? prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())
						: new BigDecimal(BigInteger.ZERO, this.currFormatter);

				BigDecimal percentChange = getPercChange(prevAuditAmt,
						db_Amount.getValue() == null ? BigDecimal.ZERO : db_Amount.getValue());

				if (label_PrecentChange != null) {
					if (percentChange.compareTo(BigDecimal.ZERO) == 0) {
						label_PrecentChange.setValue("0.00 %");
					} else {
						label_PrecentChange.setValue(String.valueOf(percentChange) + " %");
						if (percentChange.compareTo(BigDecimal.ZERO) == -1) {
							label_PrecentChange.setStyle("color:red");
						} else {
							label_PrecentChange.setStyle("color:green; font-weight:bold");
						}
					}
				}
				if (creditReviewSubCtgDetailsList != null && !creditReviewSubCtgDetailsList.isEmpty()) {
					for (CreditReviewSubCtgDetails creditReviewSubCtgDetails : creditReviewSubCtgDetailsList) {
						if (creditReviewSubCtgDetails.getSubCategoryDesc()
								.equalsIgnoreCase(finCreditRevSubCategory.getSubCategoryDesc())) {
							// Current Year Audit Value
							creditReviewSubCtgDetails.setCurYearAuditValue(
									CurrencyUtil.format(db_Amount.getValue(), this.currFormatter));
							// Current Year Conversation Price
							// creditReviewSubCtgDetails.setCurYearUSDConvstn(PennantAppUtil.formatAmount(convrsnPrice,
							// 2,false));
							// Current Year Break Down
							creditReviewSubCtgDetails.setCurYearBreakDown(CurrencyUtil.format(breakDown, 2));
							// Current Year Percentage Change
							creditReviewSubCtgDetails.setCurYearPercentage(CurrencyUtil.format(percentChange, 2));
						}
					}
				}

			}
		}

		logger.debug("Leaving");
	}

	public void getTotalLiabAndAssetsDifference() {
		logger.debug("Entering");

		String totAsst = "";
		String totLibNetWorth = "";

		if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)
				|| customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_INDIV)) {
			totAsst = FacilityConstants.CREDITREVIEW_BANK_TOTASST;
			totLibNetWorth = FacilityConstants.CREDITREVIEW_BANK_TOTLIBNETWRTH;
		} else if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_CORP)) {
			totAsst = FacilityConstants.CREDITREVIEW_CORP_TOTASST;
			totLibNetWorth = FacilityConstants.CREDITREVIEW_CORP_TOTLIBNETWRTH;
		}

		this.totAssets = curYearValuesMap.get(totAsst) == null ? BigDecimal.ZERO : curYearValuesMap.get(totAsst);
		this.totLiabilities = curYearValuesMap.get(totLibNetWorth) == null ? BigDecimal.ZERO
				: curYearValuesMap.get(totLibNetWorth);
		this.totLibAsstDiff.setValue(this.totAssets.subtract(this.totLiabilities));

		if ((this.totLibAsstDiff.getValue() == null ? BigDecimal.ZERO : this.totLibAsstDiff.getValue())
				.compareTo(BigDecimal.ZERO) == 0) {
			this.totLibAsstDiff.setStyle("color:green;");
		} else {
			this.totLibAsstDiff.setStyle("color:red;");
		}
		logger.debug("Leaving");
	}

	public void removeRecordFromFinSubCategoryList(FinCreditRevSubCategory aFinCreditRevSubCategory) {
		logger.debug("Entering");

		for (FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory) {
			if (aFinCreditRevSubCategory.getMainSubCategoryCode()
					.equals(finCreditRevSubCategory.getSubCategoryCode())) {
				String mainRule = finCreditRevSubCategory.getItemsToCal();

				String rule = "YN." + aFinCreditRevSubCategory.getSubCategoryCode();
				String oprator = String.valueOf(mainRule.charAt(mainRule.indexOf(rule) - 1));
				finCreditRevSubCategory.setItemsToCal(mainRule.replace(oprator + rule, ""));

				listOfFinCreditRevSubCategory.remove(aFinCreditRevSubCategory);
				modifiedFinCreditRevSubCategoryList.remove(aFinCreditRevSubCategory);
				curYearValuesMap.remove(aFinCreditRevSubCategory.getSubCategoryCode());
				curYearValuesMap
						.remove(FacilityConstants.CREDITREVIEW_REMARKS + aFinCreditRevSubCategory.getSubCategoryCode());

				break;
			}
		}
		logger.debug("Leaving");
	}

	public Listbox getListItemsDisabled(Listbox listbox, boolean disabled) {
		logger.debug("Entering");

		List<Listitem> listItems = listbox.getItems();
		for (Listitem item : listItems) {
			List<Component> components = item.getFirstChild().getChildren();
			for (Component component : components) {
				component.setVisible(!disabled);
			}
			if (!(item instanceof Listgroup)) {
				Listcell lc_addNewrecord = (Listcell) item.getFirstChild();
				Listcell db_listcell = (Listcell) lc_addNewrecord.getNextSibling().getNextSibling();
				Decimalbox db_AuditAmt = (Decimalbox) db_listcell.getChildren().get(0);
				db_AuditAmt.setDisabled(disabled);
				item.setDisabled(disabled);
			}
		}
		logger.debug("Leaving");

		return listbox;
	}

	public void onClick$btnForSave(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem listitem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		listitem.setAttribute("NewSubCategory", true);

		Listitem oldListitem = (Listitem) listitem.getPreviousSibling();
		FinCreditRevSubCategory preFinCreditRevSubCategory = (FinCreditRevSubCategory) oldListitem
				.getAttribute("finData");
		FinCreditRevSubCategory finCreditRevMainCategory;
		Listbox listBox = (Listbox) listitem.getParent();

		Textbox tb_subCategoryCode = (Textbox) listitem.getFellowIfAny("tb_NewSubCategoryCode");
		Textbox tb_subCategoryDesc = (Textbox) listitem.getFellowIfAny("tb_NewSubCategoryDesc");
		Combobox cb_mainCategoryCode = (Combobox) listitem.getFellowIfAny("cb_mainCategoryCode");
		Combobox cb_MathOperation = (Combobox) listitem.getFellowIfAny("cb_MathOperation");
		boolean isDataChanged = false;
		for (FinCreditRevSubCategory lfinCreditRevSubCategory : listOfFinCreditRevSubCategory) {
			if (modifiedFinCreditRevSubCategoryList.contains(lfinCreditRevSubCategory)) {
				try {
					tb_subCategoryCode.getValue();
				} catch (Exception e) {
					throw new WrongValueException(e);
				}
				if (!lfinCreditRevSubCategory.getSubCategoryCode().equals(tb_subCategoryCode.getValue())
						&& lfinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					isDataChanged = false;
				} else if (lfinCreditRevSubCategory.getSubCategoryCode().equals(tb_subCategoryCode.getValue())
						&& lfinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

					if (!lfinCreditRevSubCategory.getMainSubCategoryCode()
							.equals(cb_mainCategoryCode.getSelectedItem().getValue().toString())) {

						Listitem calcListItem = (Listitem) listBox
								.getFellowIfAny("li" + lfinCreditRevSubCategory.getMainSubCategoryCode());
						finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");

						String mainRule = finCreditRevMainCategory.getItemsToCal();
						String rule = "YN." + lfinCreditRevSubCategory.getSubCategoryCode();
						String oprator = String.valueOf(mainRule.charAt(mainRule.indexOf(rule) - 1));
						finCreditRevMainCategory.setItemsToCal(mainRule.replace(oprator + rule, ""));

						modifiedFinCreditRevSubCategoryList.remove(finCreditRevMainCategory); // Removing
																								// SubCategory
																								// from
																								// Old
																								// MainCategory

						calcListItem = (Listitem) listBox
								.getFellowIfAny("li" + cb_mainCategoryCode.getSelectedItem().getValue().toString());
						finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");
						finCreditRevMainCategory.setItemsToCal(finCreditRevMainCategory.getItemsToCal()
								+ cb_MathOperation.getSelectedItem().getValue().toString().trim() + "YN."
								+ tb_subCategoryCode.getValue().trim());
						finCreditRevMainCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						modifiedFinCreditRevSubCategoryList.add(finCreditRevMainCategory); // Adding
																							// New
																							// Sub
																							// Category
																							// to
																							// MainCategory

					}

					lfinCreditRevSubCategory.setSubCategoryCode(tb_subCategoryCode.getValue());
					lfinCreditRevSubCategory.setSubCategoryDesc(tb_subCategoryDesc.getValue());
					lfinCreditRevSubCategory.setItemsToCal("");
					lfinCreditRevSubCategory.setItemRule("YN." + lfinCreditRevSubCategory.getSubCategoryCode()
							+ "*100/YN." + lfinCreditRevSubCategory.getMainSubCategoryCode());

					isDataChanged = true;
				}
			}
			if (!isDataChanged && tb_subCategoryCode.getValue().trim()
					.equalsIgnoreCase(lfinCreditRevSubCategory.getSubCategoryCode().trim())) {
				throw new WrongValueException(tb_subCategoryCode, Labels.getLabel("FIELD_NO_DUPLICATE",
						new String[] { Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
			}
		}

		if (isDataChanged) {
			doDeleteItem(listitem);
		} else {
			FinCreditRevSubCategory aFinCreditRevSubCategory = null;

			Checkbox categoryItemType = (Checkbox) listitem.getFellowIfAny("rdCalculated");
			aFinCreditRevSubCategory = new FinCreditRevSubCategory();
			aFinCreditRevSubCategory.setCategoryId(
					Long.parseLong(listitem.getParent().getId().substring(listitem.getParent().getId().length() - 1)));
			aFinCreditRevSubCategory.setSubCategoryCode(tb_subCategoryCode.getValue());
			aFinCreditRevSubCategory.setSubCategoryDesc(tb_subCategoryDesc.getValue());
			aFinCreditRevSubCategory
					.setMainSubCategoryCode(cb_mainCategoryCode.getSelectedItem().getValue().toString());
			aFinCreditRevSubCategory.setSubCategorySeque(preFinCreditRevSubCategory.getSubCategorySeque() + 1);
			aFinCreditRevSubCategory
					.setCalcSeque(String.valueOf(Integer.parseInt(preFinCreditRevSubCategory.getCalcSeque()) + 1));

			if (categoryItemType != null) {
				aFinCreditRevSubCategory.setSubCategoryItemType(
						categoryItemType.isChecked() ? FacilityConstants.CREDITREVIEW_CALCULATED_FIELD
								: FacilityConstants.CREDITREVIEW_ENTRY_FIELD);
			} else {
				aFinCreditRevSubCategory.setSubCategoryItemType(FacilityConstants.CREDITREVIEW_ENTRY_FIELD);
			}
			aFinCreditRevSubCategory.setItemsToCal("");
			aFinCreditRevSubCategory.setItemRule("YN." + aFinCreditRevSubCategory.getSubCategoryCode() + "*100/YN."
					+ aFinCreditRevSubCategory.getMainSubCategoryCode());
			aFinCreditRevSubCategory.setIsCreditCCY(false);
			aFinCreditRevSubCategory.setFormat(true);
			aFinCreditRevSubCategory.setRemarks(preFinCreditRevSubCategory.getRemarks());
			aFinCreditRevSubCategory.setPercentCategory(true);
			aFinCreditRevSubCategory.setGrand(false);
			aFinCreditRevSubCategory.setUserDetails(getUserWorkspace().getLoggedInUser());
			aFinCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			aFinCreditRevSubCategory.setLastMntOn(preFinCreditRevSubCategory.getLastMntOn());
			FinCreditRevSubCategory befImage = new FinCreditRevSubCategory();
			BeanUtils.copyProperties(aFinCreditRevSubCategory, befImage);
			aFinCreditRevSubCategory.setBefImage(befImage);

			if (FacilityConstants.CREDITREVIEW_REMARKS.equals(aFinCreditRevSubCategory.getRemarks())) {
				fillRenderer(aFinCreditRevSubCategory, listBox, listitem, true, "");
				getListItemsDisabled(listBox, false);
			} else {
				if ("#".equals(aFinCreditRevSubCategory.getMainSubCategoryCode())) {
					throw new WrongValueException(cb_mainCategoryCode, Labels.getLabel("CHECK_NO_EMPTY", new String[] {
							Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value") }));
				} else {
					Listitem calcListItem = (Listitem) listBox
							.getFellowIfAny("li" + aFinCreditRevSubCategory.getMainSubCategoryCode());
					finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");
				}
				if ("#".equals(cb_MathOperation.getSelectedItem().getValue().toString())) {
					throw new WrongValueException(cb_MathOperation, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("listheader_Operation") }));
				} else {
					finCreditRevMainCategory.setItemsToCal(finCreditRevMainCategory.getItemsToCal()
							+ cb_MathOperation.getSelectedItem().getValue().toString().trim() + "YN."
							+ tb_subCategoryCode.getValue().trim());
				}
				finCreditRevMainCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				fillRenderer(aFinCreditRevSubCategory, listBox, listitem, true, "");
				getListItemsDisabled(listBox, false);
				modifiedFinCreditRevSubCategoryList.add(aFinCreditRevSubCategory);

				curYearValuesMap.put(aFinCreditRevSubCategory.getSubCategoryCode(),
						CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));
				engine.put(aFinCreditRevSubCategory.getSubCategoryCode(),
						CurrencyUtil.parse(BigDecimal.ZERO, this.currFormatter));

				modifiedFinCreditRevSubCategoryList.add(finCreditRevMainCategory);
				listOfFinCreditRevSubCategory.add(aFinCreditRevSubCategory);
			}

		}

		logger.debug("Leaving" + event.toString());
	}

	public void fillRenderer(FinCreditRevSubCategory finCreditRevSubCategory, Listbox listbox, Listitem oldListItem,
			boolean isNewRecord, String categoryDesc) {
		logger.debug("Entering");
		CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
		creditReviewSubCtgDetails.setMainGroupDesc(finCreditRevSubCategory.getMainSubCategoryCode());
		creditReviewSubCtgDetails.setTabDesc(categoryDesc);
		Listitem item = null;
		Listgroup lg = null;
		Listcell lc = null;
		Decimalbox db_Amount = null;
		Image lcImage = null;
		String mainCategory = "";
		String amtFormat = PennantApplicationUtil.getAmountFormate(currFormatter);
		BigDecimal prvAmt, curAmt;
		// BigDecimal convrsnPrice;
		// String uSDConvstnVal="";
		boolean isRatio = false;
		if (FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks())) {
			isRatio = true;
			creditReviewSubCtgDetails.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
		}

		// Create ListGroup if the category is Ratio
		if (isRatio && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())) {
			mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
			listMainSubCategoryCodes.add(new ValueLabel("mainSubCategoryCode", mainCategory));
			lg = new Listgroup();
			lg.setId(mainCategory);
			if (!listbox.hasFellow(mainCategory)) {
				lg.setLabel(mainCategory);
				lg.setOpen(true);
				lg.setStyle("font-weight:bold;background-color: #ADD8E6;");
				lg.setParent(listbox);
				creditReviewSubCtgDetails.setGroupCode(FacilityConstants.CREDITREVIEW_GRP_CODE_TRUE);
			}
		}

		if (!isRatio && this.creditReviewDetails.isNewRecord()) {
			if (!curYearValuesMap.containsKey(finCreditRevSubCategory.getSubCategoryCode())) {
				engine.put("Y" + this.auditPeriod.getValue() + finCreditRevSubCategory.getSubCategoryCode(),
						BigDecimal.ZERO);
			}
		}

		item = new Listitem();
		item.setId(String.valueOf("li" + finCreditRevSubCategory.getSubCategoryCode()));

		lc = new Listcell();
		lc.setId("lcAdd_" + finCreditRevSubCategory.getSubCategoryCode());

		if (!isRatio) {
			lcImage = new Image("/images/icons/Old/add_button.png");
			lcImage.setParent(lc);
			lcImage.setVisible(getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory"));
			// ComponentsCtrl.applyForward(lcImage,
			// "onClick=onClick$addNewRecord");
		}

		// Adding Deleting Option For Newly Added Record
		if (isNewRecord) {
			Space space = new Space();
			space.setSpacing("5px");
			space.setParent(lc);
			lcImage = new Image("/images/icons/Old/remove.png");
			lcImage.setParent(lc);
			ComponentsCtrl.applyForward(lcImage, "onClick=onClick$removeRecord");

			space = new Space();
			space.setSpacing("5px");
			space.setParent(lc);
			lcImage = new Image("/images/icons/Old/write.png");
			lcImage.setParent(lc);
			ComponentsCtrl.applyForward(lcImage, "onClick=onClick$editRecord");

		}
		lc.setParent(item);

		/***************** SubCategory Description ***************/
		lc = new Listcell();
		Label label_SubcategoryCode = new Label();
		label_SubcategoryCode.setStyle("font-size: 12px;");
		label_SubcategoryCode.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
		label_SubcategoryCode.setParent(lc);
		lc.setParent(item);
		creditReviewSubCtgDetails.setSubCategoryDesc(finCreditRevSubCategory.getSubCategoryDesc());
		/***************** Amount ***************/
		lc = new Listcell();
		db_Amount = new Decimalbox();
		if (enqiryModule) {
			db_Amount.setDisabled(true);
		}
		db_Amount.setId("db" + finCreditRevSubCategory.getSubCategoryCode());
		db_Amount.setAttribute("data", finCreditRevSubCategory);
		db_Amount.setAttribute("ListBoxdata", listbox);
		db_Amount.setFormat(amtFormat);
		curAmt = curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) != null
				? curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())
				: BigDecimal.ZERO;
		db_Amount.setValue(curAmt);
		creditReviewSubCtgDetails.setCurYearAuditValue(CurrencyUtil.format(curAmt, this.currFormatter));

		if (!getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory")) {
			db_Amount.setReadonly(true);
			db_Amount.setTabindex(-1);
			db_Amount.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");
		}
		ComponentsCtrl.applyForward(db_Amount, "onChange=onChange$auditedValue");
		db_Amount.setParent(lc);
		lc.setParent(item);

		/***************************
		 * Current Year US$ Conversion
		 ************************/
		/*
		 * lc = new Listcell(); Label label_currenYrUSConvrsn = new Label();
		 * label_currenYrUSConvrsn.setId("uSD1_"+finCreditRevSubCategory. getSubCategoryCode());
		 * if(creditReviewDetails.getConversionRate().compareTo(BigDecimal.ZERO) != 0){ convrsnPrice =
		 * curAmt.divide(creditReviewDetails.getConversionRate(), FacilityConstants.CREDIT_REVIEW_USD_SCALE,
		 * RoundingMode.HALF_DOWN); uSDConvstnVal= getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
		 * creditReviewSubCtgDetails.setCurYearUSDConvstn(uSDConvstnVal);
		 * label_currenYrUSConvrsn.setValue(uSDConvstnVal); } else { convrsnPrice = BigDecimal.ZERO; uSDConvstnVal=
		 * getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
		 * label_currenYrUSConvrsn.setValue(uSDConvstnVal);
		 * creditReviewSubCtgDetails.setCurYearUSDConvstn(uSDConvstnVal); }
		 * label_currenYrUSConvrsn.setStyle("font-size: 12px;"); label_currenYrUSConvrsn.setParent(lc);
		 * lc.setStyle("text-align:right;"); lc.setParent(item);
		 */
		/*************************** Break Down ************************/

		/*************************** Break Down ************************/
		lc = new Listcell();
		Label label_BreakDown = new Label();
		label_BreakDown.setId("rLabel" + finCreditRevSubCategory.getSubCategoryCode());

		BigDecimal curYrbreakDown = (BigDecimal) (curYearValuesMap
				.get(FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode()) == null
						? BigDecimal.ZERO
						: curYearValuesMap.get(
								FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode()));
		label_BreakDown.setValue(CurrencyUtil.format(curYrbreakDown, 2));

		creditReviewSubCtgDetails.setCurYearBreakDown(CurrencyUtil.format(curYrbreakDown, 2));
		label_BreakDown.setStyle("font-size: 12px;");
		label_BreakDown.setParent(lc);

		lc.setStyle("text-align:right;");
		lc.setParent(item);
		if (!isRatio && StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType())
				.equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
			creditReviewSubCtgDetails.setCalC("C");

			label_BreakDown.setStyle("font-weight:bold; font-size: 12px;");
			label_SubcategoryCode.setStyle("font-weight:bold;font-size: 12px;");
			if (finCreditRevSubCategory.isGrand()) {
				item.setStyle("background-color: #CCFF99;");
			} else {
				item.setStyle("background-color: #ADD8E6;");
			}
		}
		// Change the Font of the label to Bold if the sub category is
		// calculated and not a ratio category
		if (StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType())
				.equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
			creditReviewSubCtgDetails.setCalC("C");
			db_Amount.setReadonly(true);
			db_Amount.setDisabled(true);
			db_Amount.setSclass("decimalToString");
			db_Amount.setTabindex(-1);
			db_Amount.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");
		} else {
			db_Amount.setMaxlength(18);
		}
		// Change the Font of the label to Bold if the sub category is
		// calculated and not a ratio category
		if (!isRatio && StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType())
				.equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
		}

		/***************************
		 * Previous Audit Amount
		 ************************/
		lc = new Listcell();
		Label label_PrevAmt = new Label();
		label_PrevAmt.setZclass(null);
		label_PrevAmt.setId("prevAudit" + finCreditRevSubCategory.getSubCategoryCode());
		prvAmt = prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) != null
				? prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())
				: new BigDecimal(BigInteger.ZERO, this.currFormatter);
		label_PrevAmt.setValue(CurrencyUtil.format(prvAmt, this.currFormatter));
		creditReviewSubCtgDetails.setPreYearAuditValue(CurrencyUtil.format(prvAmt, this.currFormatter));
		label_PrevAmt.setStyle("font-size: 12px;");
		label_PrevAmt.setParent(lc);
		lc.setStyle("text-align:right; border-width: 2; font-size: 2px;");
		lc.setParent(item);

		/***************************
		 * Previous Year US$ Conversion
		 ************************//*
									 * 
									 * lc = new Listcell(); Label label_prevYrUSConvrsn = new Label();
									 * if(creditReviewDetails. getConversionRate(). compareTo(BigDecimal. ZERO) != 0){
									 * convrsnPrice = prvAmt.divide( creditReviewDetails. getConversionRate(),
									 * FacilityConstants. CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
									 * uSDConvstnVal= getUsdConVersionValue( finCreditRevSubCategory, convrsnPrice);
									 * label_prevYrUSConvrsn. setValue(uSDConvstnVal); creditReviewSubCtgDetails
									 * .setPreYearUSDConvstn( uSDConvstnVal); } else { convrsnPrice =
									 * prvAmt.divide(BigDecimal. ZERO, FacilityConstants. CREDIT_REVIEW_USD_SCALE,
									 * RoundingMode.HALF_DOWN); uSDConvstnVal= getUsdConVersionValue(
									 * finCreditRevSubCategory, convrsnPrice); label_prevYrUSConvrsn.
									 * setValue(uSDConvstnVal); creditReviewSubCtgDetails .setPreYearUSDConvstn(
									 * uSDConvstnVal); } label_prevYrUSConvrsn. setStyle("font-size: 12px;" );
									 * label_prevYrUSConvrsn. setParent(lc); lc.
									 * setStyle("text-align:right; border-width: 2;" ); lc.setParent(item);
									 */

		/***************************
		 * Previous BreakDown
		 ************************/
		lc = new Listcell();
		Label label_PrevBreakDown = new Label();
		label_PrevBreakDown.setId("label_PrevBreakDown" + finCreditRevSubCategory.getSubCategoryCode());

		BigDecimal prevYrbreakDown = (BigDecimal) (prvYearValuesMap
				.get(FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode()) == null
						? BigDecimal.ZERO
						: prvYearValuesMap.get(
								FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode()));
		label_PrevBreakDown.setValue(CurrencyUtil.format(prevYrbreakDown, 2));
		creditReviewSubCtgDetails.setPreYearBreakDown(CurrencyUtil.format(prevYrbreakDown, 2));
		label_PrevBreakDown.setStyle("font-size: 12px;");
		label_PrevBreakDown.setParent(lc);
		lc.setStyle("text-align:right; border-width: 2;");
		lc.setParent(item);

		/*************************** Percentage Change ************************/
		lc = new Listcell();
		Label label_PrecentChange = new Label();
		label_PrecentChange.setId("percent" + finCreditRevSubCategory.getSubCategoryCode());

		BigDecimal percentChange = getPercChange(prvAmt, curAmt);
		if (percentChange.compareTo(BigDecimal.ZERO) == 1) {
			label_PrecentChange.setValue(String.valueOf(percentChange) + "%");
			creditReviewSubCtgDetails.setCurYearPercentage(String.valueOf(percentChange) + "%");
			label_PrecentChange.setStyle("color:green; font-weight:bold; font-size: 12px;");
		} else {
			label_PrecentChange.setValue(String.valueOf(percentChange) + " %");
			creditReviewSubCtgDetails.setCurYearPercentage(String.valueOf(percentChange) + " %");
			if (percentChange.compareTo(BigDecimal.ZERO) == -1) {
				label_PrecentChange.setStyle("color:red; font-size: 12px;");
			} else {
				label_PrecentChange.setStyle("color:green; font-weight:bold; font-size: 12px;");
			}
		}

		label_PrecentChange.setParent(lc);
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		if (isNewRecord) {
			item.setStyle("background-color: #ffffcc; ");
			item.setAttribute("NewSubCategory", true);
			listbox.insertBefore(item, oldListItem.getNextSibling());
			listbox.removeItemAt(oldListItem.getIndex());
		}
		item.setAttribute("finData", finCreditRevSubCategory);
		item.setAttribute("finSummData",
				summaryMap.size() > 0 ? summaryMap.get(finCreditRevSubCategory.getSubCategoryCode()) : null);
		item.setParent(listbox);
		creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetails);
		logger.debug("Leaving");
	}

	public void onClick$cancelRecord(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem listItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		doDeleteItem(listItem);
		logger.debug("Leaving" + event.toString());
	}

	private void doDeleteItem(Listitem listItem) {
		Listbox listbox = (Listbox) listItem.getParent();
		listbox.insertBefore(duplicateItem, listItem.getNextSibling());
		listbox.removeItemAt(listItem.getIndex());
		getListItemsDisabled(listbox, false);
	}

	public void onClick$editRecord(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Listitem oldItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		duplicateItem = oldItem;
		Listbox listbox = (Listbox) oldItem.getParent();
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");
		getListItemsDisabled(listbox, true);
		getNewItem(finCreditRevSubCategory, oldItem, true);

		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$Calculated(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Checkbox cbIsCalculated = (Checkbox) event.getOrigin().getTarget();
		Textbox tbForFormula_NewRecord = (Textbox) cbIsCalculated.getParent().getPreviousSibling().getChildren().get(0);
		Label labelFormula = (Label) tbForFormula_NewRecord.getNextSibling();
		Button btnForFormula = (Button) cbIsCalculated.getParent().getPreviousSibling().getChildren().get(2);
		if (cbIsCalculated.isChecked()) {
			btnForFormula.setVisible(true);
			tbForFormula_NewRecord.setWidth("60px");
			ComponentsCtrl.applyForward(btnForFormula, "onClick=onClick$btnForFormula");
			labelFormula.setVisible(true);
		} else {
			btnForFormula.setVisible(false);
			tbForFormula_NewRecord.setWidth("150px");
			labelFormula.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void getSubcategoryCodeValidation(List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory,
			Textbox tdsubCategoryCode) {
		logger.debug("Entering");
		for (FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory) {
			if (tdsubCategoryCode.getValue().trim()
					.equalsIgnoreCase(finCreditRevSubCategory.getSubCategoryCode().trim())) {
				throw new WrongValueException(tdsubCategoryCode,
						Labels.getLabel("FIELD_NO_DUPLICATE", new String[] {
								Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
								Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
			}
		}
		logger.debug("Entering");
	}

	public void onClick$btnForFormula(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Textbox tb_NewSubCategoryCode = (Textbox) event.getOrigin().getTarget().getParent().getPreviousSibling()
				.getChildren().get(0);
		Textbox tb_NewSubCategoryDesc = (Textbox) event.getOrigin().getTarget().getParent().getPreviousSibling()
				.getChildren().get(2);
		if (("").equals(StringUtils.trim(tb_NewSubCategoryCode.getValue()))) {
			throw new WrongValueException(tb_NewSubCategoryCode,
					Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
									Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
		} else if (("").equals(StringUtils.trim(tb_NewSubCategoryDesc.getValue()))) {
			throw new WrongValueException(tb_NewSubCategoryDesc,
					Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
									Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
		} else {
			final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService()
					.getNewFinCreditRevSubCategory();
			aFinCreditRevSubCategory.setSubCategoryCode(tb_NewSubCategoryCode.getValue());
			aFinCreditRevSubCategory.setSubCategoryDesc(tb_NewSubCategoryDesc.getValue());
			addDetails(aFinCreditRevSubCategory);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onAddListitem(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Listitem listitemOld = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Listbox listboxOld = (Listbox) listitemOld.getParent();

		Listitem newListItem = new Listitem();
		Listcell lc;

		lc = new Listcell();
		lc.setParent(newListItem);

		lc = new Listcell();
		Textbox tb = new Textbox();
		tb.setParent(lc);
		lc.appendChild(tb);
		lc.setParent(newListItem);

		lc = new Listcell();
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setAttribute("ListBoxdata", listboxOld);
		decimalbox.setMaxlength(18);
		decimalbox.setParent(lc);
		lc.setParent(newListItem);

		lc = new Listcell();
		lc.setParent(newListItem);

		lc = new Listcell();
		lc.setParent(newListItem);

		listboxOld.insertBefore(newListItem, listitemOld);
		logger.debug("Leaving" + event.toString());
	}

	public void setData(Map<String, BigDecimal> dataMap) {
		logger.debug("Entering");

		engine.put("EXCHANGE", this.conversionRate.getValue());
		engine.put("NoOfShares", this.noOfShares.getValue());
		engine.put("MARKETPRICE", this.marketPrice.getActualValue());

		String year = "";

		if (dataMap.get("auditYear") != null) {
			year = String.valueOf(dataMap.get("auditYear"));
		}
		if (this.auditedYear.getValue().equals(year)) {
			engine.put("Y" + this.auditedYear.getValue() + "DIVCOUNT", BigDecimal.ONE);
		} else {
			engine.put("Y" + year + "DIVCOUNT", new BigDecimal(2));
		}

		// total calculation
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (finCreditRevSubCategory.getSubCategoryItemType().equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)
					&& StringUtils.isNotEmpty(finCreditRevSubCategory.getItemsToCal())) {
				BigDecimal value = BigDecimal.ZERO;

				value = (BigDecimal) RuleExecutionUtil.executeRule(
						replaceYear(finCreditRevSubCategory.getItemsToCal(), year), engine, RuleReturnType.DECIMAL);

				value = value.setScale(48, RoundingMode.HALF_DOWN);

				dataMap.put(finCreditRevSubCategory.getSubCategoryCode(), value == null ? BigDecimal.ZERO : value);
				engine.put("Y" + year + finCreditRevSubCategory.getSubCategoryCode(),
						value == null ? BigDecimal.ZERO : value);
			}
		}

		// ratio calculation
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (StringUtils.isNotEmpty(finCreditRevSubCategory.getItemRule())) {
				BigDecimal value = BigDecimal.ZERO;
				try {

					value = (BigDecimal) RuleExecutionUtil.executeRule(
							replaceYear(finCreditRevSubCategory.getItemRule(), year), engine, RuleReturnType.DECIMAL);

					if (finCreditRevSubCategory.getRemarks().equals(FacilityConstants.CREDITREVIEW_REMARKS)) {
						value = (BigDecimal) RuleExecutionUtil.executeRule(
								replaceYear(finCreditRevSubCategory.getItemRule(), year), engine,
								RuleReturnType.DECIMAL);
						value = value.setScale(48, RoundingMode.HALF_DOWN);
					} else {
						value = (BigDecimal) RuleExecutionUtil.executeRule(
								replaceYear(finCreditRevSubCategory.getItemRule(), year), engine,
								RuleReturnType.DECIMAL);
						value = value.setScale(48, RoundingMode.HALF_DOWN);
					}
				} catch (Exception e) {
					value = BigDecimal.ZERO;
					logger.error("Exception: ", e);
				}
				if (finCreditRevSubCategory.getCategoryId() == 4 || finCreditRevSubCategory.getCategoryId() == 7) {
					dataMap.put(finCreditRevSubCategory.getSubCategoryCode(), value);
					engine.put("Y" + year + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
				} else {
					dataMap.put(FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
					curYearValuesMap.put(
							FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
				}
			}
		}
	}

	public String replaceYear(String formula, String year) {
		String formatedFormula = formula;
		if (StringUtils.isNotEmpty(year)) {
			formatedFormula = formatedFormula.replace("YN-1" + ".", "Y" + (Integer.parseInt(year) - 1));
			formatedFormula = formatedFormula.replace("YN" + ".", "Y" + year);
		}
		return formatedFormula;
	}

	public void onChange$auditedValue(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Listbox listbox = (Listbox) event.getOrigin().getTarget().getAttribute("ListBoxdata");
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) event.getOrigin().getTarget()
				.getAttribute("data");
		String subCategory = aFinCreditRevSubCategory.getSubCategoryCode();
		Listitem listItem = (Listitem) listbox.getFellowIfAny("li" + subCategory);

		((Tabbox) listbox.getParent().getParent().getParent().getParent()).getSelectedTab().setSelected(true);

		Decimalbox db_Amount = (Decimalbox) listItem
				.getFellowIfAny("db" + aFinCreditRevSubCategory.getSubCategoryCode());
		curYearValuesMap.put(subCategory, db_Amount.getValue() == null ? BigDecimal.ZERO : db_Amount.getValue());
		curYearValuesMap.put("auditYear", new BigDecimal(Integer.parseInt(creditReviewDetails.getAuditYear())));
		engine.put("Y" + this.auditedYear.getValue() + subCategory,
				db_Amount.getValue() == null ? BigDecimal.ZERO : db_Amount.getValue());

		engine.put(
				"Y" + this.auditedYear.getValue() + FacilityConstants.CREDITREVIEW_REMARKS
						+ aFinCreditRevSubCategory.getSubCategoryCode(),
				db_Amount.getValue() == null ? BigDecimal.ZERO : db_Amount.getValue());

		setData(curYearValuesMap);
		getListboxDetails(listbox);
		getTotalLiabAndAssetsDifference();
		logger.debug("Leaving" + event.toString());
	}

	public BigDecimal getPercChange(BigDecimal previousVal, BigDecimal currentVal) {
		if (previousVal.compareTo(BigDecimal.ZERO) != 0) {
			return currentVal.subtract(previousVal).multiply(new BigDecimal(100)).divide(previousVal, 2,
					RoundingMode.HALF_DOWN);
		} else {
			return new BigDecimal(BigInteger.ZERO, 2);
		}
	}

	public String getBreakDownAmt(BigDecimal assetValue, BigDecimal totAssetValue) {
		if (totAssetValue.compareTo(BigDecimal.ZERO) != 0) {
			return assetValue.multiply(new BigDecimal(100)).divide(totAssetValue).toString();
		} else {
			return BigDecimal.ZERO.toString();
		}
	}

	public void onChange$conversionRate(ForwardEvent event) {
		logger.debug(event + "Entering");
		doClearMessage();

		creditReviewSubCtgDetailsList.clear();
		if (this.conversionRate.getValue() == null || this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0) {
			throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
		}
		creditReviewDetails.setConversionRate(this.conversionRate.getValue());
		if (this.tabpanelsBoxIndexCenter.getChildren().size() > 0) {
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if (this.tabsIndexCenter.getChildren().size() > 0) {
			this.tabsIndexCenter.getChildren().clear();
		}
		setData(curYearValuesMap);
		setTabs();
		logger.debug(event + "Leaving");
	}

	public void refreshTabs() {

		creditReviewSubCtgDetailsList.clear();
		if (this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0) {
			throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
		}
		creditReviewDetails.setConversionRate(this.conversionRate.getValue());
		if (this.tabpanelsBoxIndexCenter.getChildren().size() > 0) {
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if (this.tabsIndexCenter.getChildren().size() > 0) {
			this.tabsIndexCenter.getChildren().clear();
		}
		setData(curYearValuesMap);
		setTabs();
	}

	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug(event + "Entering");

		CreditReviewMainCtgDetails creditReviewMainCtgDetails = new CreditReviewMainCtgDetails();

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.custCIF.getValue() != null) {
				creditReviewMainCtgDetails.setCustomerId(
						String.valueOf(this.custCIF.getValue()) + "_" + String.valueOf(this.custShrtName.getValue()));
			} else {
				throw new WrongValueException(this.custID,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.bankName.isVisible()) {
				creditReviewMainCtgDetails.setAllowBankName("TRUE");
				if (this.bankName.getValue() != null && StringUtils.isNotEmpty(this.bankName.getValue())) {
					creditReviewMainCtgDetails.setBankName(this.bankName.getValue());
				} else {
					throw new WrongValueException(this.bankName,
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] {
											Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value"),
											Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.auditors.getValue() != null && StringUtils.isNotEmpty(this.auditors.getValue())) {
				creditReviewMainCtgDetails.setAuditors(this.auditors.getValue());
			} else {
				throw new WrongValueException(this.auditors,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.location.getValue() != null && StringUtils.isNotEmpty(this.location.getValue())) {
				creditReviewMainCtgDetails.setLocation(this.location.getValue());
			} else {
				throw new WrongValueException(this.location,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_Location.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_Location.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.auditedDate.getValue() != null) {
				creditReviewMainCtgDetails.setAuditedDate(DateUtil.formatToShortDate(this.auditedDate.getValue()));
			} else {
				throw new WrongValueException(this.auditedDate,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.conversionRate.getValue() != null) {
				creditReviewMainCtgDetails
						.setConversionRate(CurrencyUtil.format(this.conversionRate.getValue(), this.currFormatter));
			} else {
				throw new WrongValueException(this.conversionRate,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] {
										Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.auditedYear.getValue() != null) {
				creditReviewMainCtgDetails.setAuditYear(this.auditedYear.getValue());
			} else {
				throw new WrongValueException(this.auditedYear,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.noOfShares.getValue() != null) {
				creditReviewMainCtgDetails.setNoOfShares(String.valueOf(this.noOfShares.getValue()));
			} else {
				throw new WrongValueException(this.noOfShares,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_NoOfShares.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_NoOfShares.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			creditReviewMainCtgDetails.setMarketPrice(String.valueOf(this.marketPrice.getValidateValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.auditPeriod.getValue() != null) {
				creditReviewMainCtgDetails.setAuditPeriod(String.valueOf(this.auditPeriod.getValue()));
			} else {
				throw new WrongValueException(this.auditPeriod,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		creditReviewMainCtgDetails.setConsolOrUnConsol(this.conSolOrUnConsol.getSelectedItem().getValue().toString()
				.equals(FacilityConstants.CREDITREVIEW_CONSOLIDATED) ? "True" : "False");

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		} else {
			List<Object> list = new ArrayList<Object>();
			String qualUnQual = this.qualifiedUnQualified.getSelectedItem().getValue().toString();
			String audQual = Labels.getLabel("UnAudUnQual_Months",
					new String[] { this.auditType.getSelectedItem().getValue().toString(), qualUnQual,
							String.valueOf(getCreditReviewDetails().getAuditPeriod()) });

			for (CreditReviewSubCtgDetails ctgDetails : creditReviewSubCtgDetailsList) {
				if (StringUtils.isNotEmpty(ctgDetails.getSubCategoryDesc())) {
					continue;
				}
				ctgDetails.setCurYearAuditValueHeader(audQual);
				ctgDetails.setCurrencyConvertion(AccountConstants.CURRENCY_USD);
			}

			list.add(creditReviewSubCtgDetailsList);

			String userName = getUserWorkspace().getLoggedInUser().getFullName();
			ReportsUtil.generatePDF("CreditApplication_Review", creditReviewMainCtgDetails, list, userName,
					this.window_CreditApplicationReviewDialog);
		}

		logger.debug("Leaving");
	}

	/*
	 * Double Clicked Event For SubCategory Item
	 */
	public void onSubCategoryItemDoubleClicked(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		// get the selected Academic object
		final Listitem item = (Listitem) event.getOrigin().getTarget();
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) item.getAttribute("finData");
		addDetails(aFinCreditRevSubCategory);
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * onSelect Event For Audit Type Combo box
	 */
	public void onSelect$auditType(Event event) {
		logger.debug("Entering" + event.toString());
		setLables();
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$qualifiedUnQualified(Event event) {
		logger.debug("Entering" + event.toString());
		setLables();
		logger.debug("Leaving" + event.toString());
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	private List<Filter> getFilterList() {
		filterList = new ArrayList<Filter>();
		filterList.add(new Filter("lovDescCustCtgType", new String[] { PennantConstants.PFF_CUSTCTG_CORP,
				PennantConstants.PFF_CUSTCTG_SME, PennantConstants.PFF_CUSTCTG_INDIV }, Filter.OP_IN));
		return filterList;
	}

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return finCreditRevSubCategoryService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	public CustomerDocumentService getCustomerDocumentService() {
		return customerDocumentService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	private void setLables() {
		String qualUnQual = this.qualifiedUnQualified.getSelectedItem().getValue().toString();
		String audQual = Labels.getLabel("UnAudUnQual_Months",
				new String[] { "#".equals(this.auditType.getSelectedItem().getValue().toString())
						? FacilityConstants.CREDITREVIEW_AUDITED
						: this.auditType.getSelectedItem().getValue().toString(), qualUnQual });

		for (FinCreditRevCategory revCategory : listOfFinCreditRevCategory) {
			String id = getId(revCategory.getCategoryDesc());
			Label audQualLabel = (Label) this.tabpanelsBoxIndexCenter.getFellowIfAny(id);
			audQualLabel.setStyle("font-weight:bold;font-size: 12px;");
			audQualLabel.setValue(audQual);
		}
	}

	private String getId(String category) {
		return StringUtils.trimToEmpty(category).replace(" ", "") + AUDITUNAUDIT_LISTHEADER;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/*
	 * Method for USD Conversion
	 */
	public String getUsdConVersionValue(FinCreditRevSubCategory finCreditRevSubCategory, BigDecimal convrsnPrice) {
		if (FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks())) {
			if (FacilityConstants.CORP_CRDTRVW_RATIOS_WRKCAP.equals(finCreditRevSubCategory.getSubCategoryCode())
					|| FacilityConstants.CORP_CRDTRVW_RATIOS_EBITDA4
							.equals(finCreditRevSubCategory.getSubCategoryCode())
					|| FacilityConstants.CORP_CRDTRVW_RATIOS_FCF.equals(finCreditRevSubCategory.getSubCategoryCode())) {
				return CurrencyUtil.format(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE);
			} else {
				return "";
			}
		} else {
			return CurrencyUtil.format(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE);
		}
	}

}
