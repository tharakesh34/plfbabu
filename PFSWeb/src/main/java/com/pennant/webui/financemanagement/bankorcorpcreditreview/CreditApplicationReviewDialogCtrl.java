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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
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
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class CreditApplicationReviewDialogCtrl extends GFCBaseCtrl<FinCreditReviewSummary> {
	private static final long serialVersionUID = 8602015982512929710L;
	private static final Logger logger = Logger.getLogger(CreditApplicationReviewDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	public  Window 			    window_CreditApplicationReviewDialog; // autowired

	protected Borderlayout		borderlayout_CreditApplicationReview;
	protected Grid 				creditApplicationReviewGrid;
	protected Longbox 	 		custID; 						   	// autowired
	protected Textbox 			bankName;                           // autowired
	protected Textbox 			auditors;                           // autowired
	protected Radiogroup 		conSolOrUnConsol;                   // autowired
	protected Radio		        conSolidated;                       // autowired
	protected Radio 		    unConsolidated;                     // autowired
	protected Textbox 			location;                           // autowired
	protected Textbox 			auditedYear;                        // autowired
	protected Datebox 			auditedDate;                        // autowired
	protected Decimalbox 		conversionRate;                     // autowired
	protected ExtendedCombobox 	 		custCIF;							// autowired
	protected Label 	 		custShrtName;						// autowired
	protected Longbox 	 		noOfShares;							// autowired
	protected CurrencyBox 	 	marketPrice;						// autowired
	protected Combobox          auditPeriod;                        // autowired
	protected Decimalbox        totLibAsstDiff;                     // autowired

	protected Groupbox 			gb_CreditReviwDetails;
	protected Tabbox 			tabBoxIndexCenter;
	protected Tabs 				tabsIndexCenter;
	protected Tabpanels 		tabpanelsBoxIndexCenter;
	protected Button 			btnCopyTo;					// autowired

	
	protected Listitem          duplicateItem;
	protected Grid 				grid_Basicdetails;			// autoWired
	protected Label             label_CreditApplicationReviewDialog_BankName; // autowired
	protected Space             space_BankName; // autowired
	protected Groupbox          gb_basicDetails; // autowire


	private   CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;


	// not auto wired vars
	private FinCreditReviewDetails creditReviewDetails; // overhanded per param
	private transient CreditApplicationReviewListCtrl creditApplicationReviewListCtrl; // overhanded per param
	private transient CreditReviewSummaryData creditReviewSummaryData;
	private Map<String,List<FinCreditReviewSummary>> creditReviewSummaryMap;
	public  List<CustomerDocument> custDocList;
	public  List<CustomerDocument> customerDocumentList =new ArrayList<CustomerDocument>();
	public  List<Notes>     notesList = new ArrayList<Notes>();
	public  List<CreditReviewSubCtgDetails> creditReviewSubCtgDetailsList = new ArrayList<CreditReviewSubCtgDetails>();
	public  CreditReviewSubCtgDetails creditReviewSubCtgDetailsHeaders = new CreditReviewSubCtgDetails();
	Date date = DateUtility.getAppDate();

	@SuppressWarnings("unused")
	private boolean ratioFlag= false;

	private transient BigDecimal totLiabilities = BigDecimal.ZERO;
	private transient BigDecimal totAssets = BigDecimal.ZERO;

	private transient boolean validationOn;
	
	int prevAuditPeriod;

	protected Button btnPrint; 	// autowire

	protected Button button_addDetails;
	protected Button btnSearchAccountSetCode; // autowire
	// ServiceDAOs / Domain Classes
	protected List<ValueLabel> listMainSubCategoryCodes = new ArrayList<ValueLabel>();
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private transient FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerDocumentService customerDocumentService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();
	private List<FinCreditReviewSummary> creditReviewSummaryList = new ArrayList<FinCreditReviewSummary>();
	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	private Map<String,BigDecimal> curYearValuesMap = new HashMap<String,BigDecimal>();
	private Map<String,BigDecimal> prvYearValuesMap = null;
	private Map<String,FinCreditReviewSummary> summaryMap = new HashMap<String,FinCreditReviewSummary>();
	public List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory  = null;
	public List<FinCreditRevSubCategory>  modifiedFinCreditRevSubCategoryList = new ArrayList<FinCreditRevSubCategory>();

	private Customer customer = null;
	private Map<String,BigDecimal> prv1YearValuesMap = null;

	private String creditRevCode;
	int listRows;
	// create a script engine manager
	ScriptEngineManager factory = new ScriptEngineManager();

	// create a JavaScript engine
	ScriptEngine engine = factory.getEngineByName("JavaScript");
	int currFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	String amtFormat=PennantApplicationUtil.getAmountFormate(currFormatter);
	List<Filter> filterList = null;

	private String AUDITUNAUDIT_LISTHEADER="auditUnaudit";	
	protected Combobox          auditType;             // autowire
	protected Radiogroup        qualifiedUnQualified;  // autowire
	protected Radio             qualRadio;             // autowire
	protected Radio             unQualRadio;           // autowire
	protected Textbox           lovDescFinCcyName;     // autowire
	protected ExtendedCombobox  currencyType;          // autowire

	private MailUtil mailUtil;

	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CreditApplicationReviewDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinCreditReviewDetails object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CreditApplicationReviewDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CreditApplicationReviewDialog);

		try{

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

			doLoadWorkFlow(this.creditReviewDetails.isWorkflow(), 
					this.creditReviewDetails.getWorkflowId(), this.creditReviewDetails.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CreditApplicationReviewDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the creditApplicationReviewListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete creditReviewDetails here.
			if (arguments.containsKey("creditApplicationReviewListCtrl")) {
				setCreditApplicationReviewListCtrl((CreditApplicationReviewListCtrl) arguments.get("creditApplicationReviewListCtrl"));
			} else {
				setCreditApplicationReviewListCtrl(null);
			}

			doSetFieldProperties();
			if(StringUtils.isNotBlank(this.creditReviewDetails.getLovDescCustCIF())){
				customer = (Customer)PennantAppUtil.getCustomerObject(this.creditReviewDetails.getLovDescCustCIF(), getFilterList());
			}

			if(customer != null){
				custDocList = creditApplicationReviewService.getCustomerDocumentsById(this.customer.getCustID(), "");
			} else {
				custDocList = new ArrayList<CustomerDocument>();
			}

			fillComboBox(this.auditType, "", PennantStaticListUtil.getCreditReviewAuditTypesList(), "");

			doShowDialog(getCreditReviewDetails());
		} catch(Exception e){
			MessageUtil.showError(e);
			closeDialog();
		} 
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
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
		this.conversionRate.setRoundingMode(BigDecimal.ROUND_DOWN);
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

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
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
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$button_addDetails(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if(tabBoxIndexCenter.getSelectedPanel() != null){
			if((Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0) != null && 
					((Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0)).getSelectedItem() != null){
				final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService().getNewFinCreditRevSubCategory();
				addDetails(aFinCreditRevSubCategory);
			} else {
				throw new WrongValueException(this.button_addDetails, Labels.getLabel("FIELD_NO_CELL_EMPTY",
						new String[] {Labels.getLabel("label_addDetails"),
						Labels.getLabel("label_addDetails") }));
			}
		} else {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
					new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value"),
					Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}



	public void addDetails(FinCreditRevSubCategory aFinCreditRevSubCategory) throws InterruptedException{
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCreditRevSubCategory", aFinCreditRevSubCategory);
		map.put("listOfFinCreditRevCategory", listOfFinCreditRevCategory);
		map.put("creditApplicationReviewDialogCtrl", this);
		map.put("listMainSubCategoryCodes", listMainSubCategoryCodes);
		map.put("parentRole", getRole());


		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/FinCreditRevSubCategory/FinCreditRevSubCategoryDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}


	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CreditApplicationReviewDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.creditReviewDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCreditReviewDetails
	 *            (FinCreditReviewDetails)
	 * @throws Exception 
	 */


	public void doWriteBeanToComponents(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug("Entering");
		this.custID.setValue(aCreditReviewDetails.getCustomerId());
		this.custCIF.setValue(aCreditReviewDetails.getLovDescCustCIF()!=null ? 	StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF()):"");
		this.custCIF.setTooltiptext(aCreditReviewDetails.getLovDescCustCIF()!=null ? 
				StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF()):"");
		this.custShrtName.setValue(aCreditReviewDetails.getLovDescCustShrtName());
		this.creditRevCode = aCreditReviewDetails.getCreditRevCode();
		this.bankName.setValue(aCreditReviewDetails.getBankName());
		this.auditedDate.setValue(aCreditReviewDetails.getAuditedDate());
		this.auditedYear.setValue(aCreditReviewDetails.getAuditYear());
		this.auditedYear.setReadonly(true);

		if(aCreditReviewDetails.isNewRecord() && StringUtils.isBlank(aCreditReviewDetails.getCurrency())){
			Currency currency = PennantAppUtil.getCurrencyBycode(SysParamUtil
					.getValueAsString(PennantConstants.LOCAL_CCY));
			this.currencyType.setValue(currency.getCcyCode());
			this.currencyType.setDescription(currency.getCcyDesc());
			this.currFormatter = currency.getCcyEditField();
			doSetFieldProperties();
		}else{
			this.currencyType.setValue(aCreditReviewDetails.getCurrency());
			this.currFormatter = CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency());
		}

		if(aCreditReviewDetails.getConversionRate() == null){

			BigDecimal converstnRate = PennantAppUtil.formateAmount(CalculationUtil.getConvertedAmount(AccountConstants.CURRENCY_USD, this.currencyType.getValue(),
					new BigDecimal(100)), currFormatter);
			this.conversionRate.setValue(converstnRate);
		} else {
			this.conversionRate.setValue(aCreditReviewDetails.getConversionRate());
		}

		if(aCreditReviewDetails.isConsolidated()){
			this.conSolOrUnConsol.setSelectedIndex(0);
		} else  {
			this.conSolOrUnConsol.setSelectedIndex(1);
		}

		this.auditors.setValue(aCreditReviewDetails.getAuditors());
		this.location.setValue(aCreditReviewDetails.getLocation());	
		this.noOfShares.setValue(aCreditReviewDetails.getNoOfShares());
		this.marketPrice.setValue(aCreditReviewDetails.getMarketPrice() == null ? BigDecimal.ZERO : aCreditReviewDetails.getMarketPrice());

		if(aCreditReviewDetails.getAuditPeriod() != 0){
			fillComboBox(this.auditPeriod, String.valueOf(aCreditReviewDetails.getAuditPeriod()), PennantStaticListUtil.getPeriodList(), "");
		}

		fillComboBox(this.auditType, StringUtils.trimToEmpty(aCreditReviewDetails.getAuditType()), 
				PennantStaticListUtil.getCreditReviewAuditTypesList(), "");

		if(aCreditReviewDetails.isQualified()){
			this.qualifiedUnQualified.setSelectedIndex(0);
		} else  {
			this.qualifiedUnQualified.setSelectedIndex(1);
		}
		this.auditPeriod.setReadonly(true);
		setFinCreditReviewSummaryList(aCreditReviewDetails.getCreditReviewSummaryEntries());
		this.recordStatus.setValue(aCreditReviewDetails.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCreditReviewDetails
	 */
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
				throw new WrongValueException( this.auditedYear, Labels.getLabel( "const_NO_FUTURE",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"),
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
			if(this.conSolOrUnConsol.getSelectedItem().getValue().equals(FacilityConstants.CREDITREVIEW_CONSOLIDATED)){
				aCreditReviewDetails.setConsolidated(true);
			} else {
				aCreditReviewDetails.setConsolidated(false);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0){
				throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value")}));
			} else {
				aCreditReviewDetails.setConversionRate((BigDecimal)this.conversionRate.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setMarketPrice(this.marketPrice.getActualValue());
		} catch (WrongValueException we) {
			//wve.add(we);
		}
		try {
			if(this.noOfShares.getValue() != null){
				aCreditReviewDetails.setNoOfShares(this.noOfShares.getValue());
			} else {
				aCreditReviewDetails.setNoOfShares(0);
			}
		} catch (WrongValueException we) {
			//wve.add(we);
		}
		try {
			if("#".equals(this.auditPeriod.getSelectedItem().getValue().toString())){
				throw new WrongValueException();
			} else {
				aCreditReviewDetails.setAuditPeriod(Integer.parseInt(this.auditPeriod.getSelectedItem().getValue().toString()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.auditType.getSelectedItem() != null && !"#".equals(this.auditType.getSelectedItem().getValue().toString())){
				aCreditReviewDetails.setAuditType(this.auditType.getSelectedItem().getValue().toString());
			} else {
				throw new WrongValueException(this.auditType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_AuditType.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.qualifiedUnQualified.getSelectedItem().getValue().toString().equals(FacilityConstants.CREDITREVIEW_QUALIFIED)){
				aCreditReviewDetails.setQualified(true);
			} else {
				aCreditReviewDetails.setQualified(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.currencyType.getValue())) {
				throw new WrongValueException( this.currencyType, Labels.getLabel( "FIELD_NO_INVALID", 
						new String[] { Labels .getLabel("label_CreditApplicationReviewDialog_finCcy.value") }));
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

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCreditReviewDetails
	 * @throws Exception
	 */
	public void doShowDialog(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
		logger.debug("Entering");

		if(customer != null){
			if(customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)){
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_SME);  
			} else {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_CORP);  	
			}
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCreditReviewDetails.isNew()) {
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

			if(customer != null){
				doSetCustomer(customer, newSearchObject);
			}

			if(PennantConstants.PFF_CUSTCTG_SME.equals(customer.getCustCtgCode())){
				this.bankName.setValue(customer.getCustShrtName());
				this.bankName.setReadonly(true);
				this.label_CreditApplicationReviewDialog_BankName.setValue(Labels.getLabel("label_CustomerDialog_CustShrtName.value"));
				this.label_CreditApplicationReviewDialog_BankName.setVisible(false);
				this.bankName.setVisible(false);
				this.space_BankName.setVisible(false);
			}
			setLables();
			setDialog(DialogType.EMBEDDED);
			groupboxWf.setVisible(false); // For Present Requirement
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_CreditApplicationReviewDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.location.isReadonly()) {
			this.location.setConstraint(new PTStringValidator(Labels.getLabel("label_CreditApplicationReviewDialog_Location.value"), null, true ));
		}
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value"), null, true));
		}
		if (!this.conversionRate.isReadonly()){
			this.conversionRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value"),9,true,false,9999));
		}
		if (!this.auditedYear.isReadonly()) {
			this.auditedYear.setConstraint(new PTStringValidator(Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"), null, true));
		}
		if (!this.auditors.isReadonly()) {
			this.auditors.setConstraint(new PTStringValidator(Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value"), null, true));
		}
		if (!this.auditedDate.isReadonly()) {
			this.auditedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CreditApplicationReviewDialog_AuditedDate.value"), true, null, true, true));
		}
		if (!this.auditPeriod.isReadonly()) {
			this.auditPeriod.setConstraint(new PTNumberValidator(Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value"),true,false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value"), null, true));
		if(currencyType.isButtonVisible()) {
			this.currencyType.setConstraint(new PTStringValidator(Labels.getLabel("labelCreditApplicationReviewDialog_FinCcy.value"),null,true,true));
		}
		logger.debug("Leaving");
	}


	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.marketPrice.setConstraint("");
		this.currencyType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Clear the Error Messages
	 */
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

	/**
	 * Method for refreshing the list in ListCtrl
	 */
	private void refreshList() {
        getCreditApplicationReviewListCtrl().search();
	}


	// CRUD operations

	/**
	 * Deletes a FinCreditReviewDetails object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(getCreditReviewDetails(), aCreditReviewDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
				aCreditReviewDetails.getDetailId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCreditReviewDetails.getRecordType())) {
				aCreditReviewDetails.setVersion(aCreditReviewDetails.getVersion() + 1);
				aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCreditReviewDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCreditReviewDetails, tranType)) {
					refreshList();
					// do Close the dialog
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
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

	/**
	 * Clears the components values. <br>
	 */
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

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		final FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(getCreditReviewDetails(), aCreditReviewDetails);
		boolean isNew = false;

		if((this.totLibAsstDiff.getValue() == null ? BigDecimal.ZERO : this.totLibAsstDiff.getValue()).compareTo(BigDecimal.ZERO) != 0
				&& userAction.getSelectedItem().getValue().toString().equals(PennantConstants.RCD_STATUS_SUBMITTED)){
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

		isNew = aCreditReviewDetails.isNew();
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
				//Mail Alert Notification for User
				if(StringUtils.isNotBlank(aCreditReviewDetails.getNextTaskId()) && 
						!StringUtils.trimToEmpty(aCreditReviewDetails.getNextRoleCode()).equals(aCreditReviewDetails.getRoleCode())){
					getMailUtil().sendMail(NotificationConstants.MAIL_MODULE_CREDIT, aCreditReviewDetails,this);
				}
				// do Close the Dialog window
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCreditReviewDetails
	 *            (FinCreditReviewDetails)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
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

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		try {

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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCreditReviewDetails.getBefImage(), aCreditReviewDetails);
		return new AuditHeader(String.valueOf(aCreditReviewDetails.getDetailId()), 
				null, null, null, auditDetail, aCreditReviewDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CreditApplicationReviewDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.creditReviewDetails);
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.creditReviewDetails.getDetailId());
	}




	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setFinCreditReviewSummaryList(List<FinCreditReviewSummary> creditReviewSummaryList) {
		this.creditReviewSummaryList = creditReviewSummaryList;
	}
	/**
	 * This method to read the list items and then add to the list.<br>
	 * @return
	 * @throws InterruptedException 
	 */
	public void setListDetails(FinCreditReviewDetails finCreditReviewDetails) throws InterruptedException {
		logger.debug("Entering");
		boolean isNewSubCategory = false;
		List<FinCreditReviewSummary> listOfCreditReviewSummary = new ArrayList<FinCreditReviewSummary>();
		List<Component> listOfTabPanels = this.tabpanelsBoxIndexCenter.getChildren();

		for(int k=0;k<listOfTabPanels.size();k++){
			if(listOfTabPanels.get(k).getId().startsWith("tabPanel_")){
				Listbox listBox= (Listbox) listOfTabPanels.get(k).getFirstChild().getFirstChild();
				List<Listitem> listItems = listBox.getItems();

				for(int i =0;i<listItems.size();i++){
					Listitem listItem = (Listitem)listItems.get(i);
					if(listItem instanceof Listgroup){
						continue;
					}
					FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) listItem.getAttribute("finData");
					FinCreditReviewSummary creditReviewSummary = null;
					
					if(finCreditRevSubCategory == null){
						continue;
					}

					if(listItem.getAttribute("finSummData") != null){
						creditReviewSummary = (FinCreditReviewSummary) listItem.getAttribute("finSummData");
					}else{
						creditReviewSummary = new FinCreditReviewSummary();
						creditReviewSummary.setNewRecord(true);
						creditReviewSummary.setSubCategoryCode(finCreditRevSubCategory.getSubCategoryCode());
					}
					//**** Credit Review Summary 		
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

					//**** Credit Review Sub Category 		
					if(listItem.getAttribute("NewSubCategory")!=null){
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

					creditReviewSummary.setItemValue(this.curYearValuesMap.get(creditReviewSummary.getSubCategoryCode())==null?BigDecimal.ZERO:
						PennantAppUtil.unFormateAmount(this.curYearValuesMap.get(creditReviewSummary.getSubCategoryCode()),this.currFormatter));
					listOfCreditReviewSummary.add(creditReviewSummary);
					creditReviewSummary = null;
				}
			}
			// if(isNewSubCategory){
			if(modifiedFinCreditRevSubCategoryList != null && !modifiedFinCreditRevSubCategoryList.isEmpty()){
				finCreditReviewDetails.setLovDescFinCreditRevSubCategory(listOfFinCreditRevSubCategory);
			}
			//} 
			finCreditReviewDetails.setCustomerDocumentList(customerDocumentList);
			finCreditReviewDetails.setCreditReviewSummaryEntries(listOfCreditReviewSummary);
		}
		logger.debug("Leaving");
	}


	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onFulfill$currencyType(Event event) throws Exception {
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
				amtFormat=PennantApplicationUtil.getAmountFormate(currFormatter);

				BigDecimal convrtnRate = PennantAppUtil.formateAmount(CalculationUtil.getConvertedAmount(AccountConstants.CURRENCY_USD, this.currencyType.getValue(), 
						new BigDecimal(100)), currFormatter);
				this.conversionRate.setValue(convrtnRate);
				getCreditReviewDetails().setConversionRate(convrtnRate);
				refreshTabs();
				doSetFieldProperties();
			}
		}
		setLables();
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * This method for setting the tabs according to the sheets we define.
	 * @throws Exception
	 */
	public void setTabs() throws Exception{
		logger.debug("Entering");

		this.tabpanelsBoxIndexCenter.setId("tabpanelsBoxIndexCenter");
		int ratioTabCount = 1;
		prevAuditPeriod =getCreditApplicationReviewService().getCreditReviewAuditPeriodByAuditYear(this.custID.getValue(), 
				String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear())-1), this.creditReviewDetails.getAuditPeriod(), false, "_VIew");
		//Create Tabs based on the finCreditReview Category and Customer Category Type
		for(FinCreditRevCategory finCreditRevCategory:listOfFinCreditRevCategory){

			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
			creditReviewSubCtgDetails.setMainGroup("T");
			creditReviewSubCtgDetails.setMainGroupDesc(finCreditRevCategory.getCategoryDesc());
			creditReviewSubCtgDetails.setTabDesc(finCreditRevCategory.getCategoryDesc());
			creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetails);

			if(FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevCategory.getRemarks())){
				this.ratioFlag = false;
			}else{
				this.ratioFlag = true;
			}
			Tab tab = new Tab();
			tab.setId("tab_"+finCreditRevCategory.getCategoryId());
			tab.setLabel(finCreditRevCategory.getCategoryDesc());
			tab.setParent(this.tabsIndexCenter);
			tabsIndexCenter.setId("tabsIndexCenter");
			Tabpanel tabPanel = new Tabpanel();	
			tabPanel.setHeight("100%");// 425px

			tabPanel.setId("tabPanel_"+finCreditRevCategory.getCategoryId());
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			if(ratioTabCount == listOfFinCreditRevCategory.size()){
				tab.setAttribute("isRatioTab", true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectRatiosTab");
			}
			ratioTabCount ++;
			render(finCreditRevCategory,setListToTab(tabPanel,finCreditRevCategory));
		}
		appendDocumentDetailTab();
		appendRecommendDetailTab();
		logger.debug("Leaving");
	}


	public void onSelectRatiosTab(ForwardEvent event) throws Exception{
		logger.debug("Entering");
		Tabpanel tabpanel;
		String ratioPanelId = "tabPanel_"+listOfFinCreditRevCategory.get(listOfFinCreditRevCategory.size()-1).getCategoryId();
		if(tabpanelsBoxIndexCenter.getFellowIfAny(ratioPanelId) != null){
			tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(ratioPanelId);
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
			FinCreditRevCategory finCreditRevCategory = listOfFinCreditRevCategory.get(listOfFinCreditRevCategory.size()-1);
			render(finCreditRevCategory,setListToTab(tabpanel,finCreditRevCategory));
			setLables();
		}
		logger.debug("Leaving");
	}

	public void appendDocumentDetailTab(){
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("documentsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		//tabpanel.setHeight(this.borderLayoutHeight-0 + "px");
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
		int i=0;

		for(CustomerDocument customerDocument : custDocList){

			listItem = new Listitem();
			listItem.setId("li_"+customerDocument.getCustDocType()+i);

			listCell = new Listcell();
			listCell.setId("lc_"+customerDocument.getLovDescCustDocCategory()+i);
			Label label_idType  = new Label(customerDocument.getLovDescCustDocCategory());
			label_idType.setParent(listCell);
			listCell.setParent(listItem);

			listCell = new Listcell();
			listCell.setId("lc_"+customerDocument.getLovDescCustDocIssuedCountry()+i);
			Label label_issuedCuntry  = new Label(customerDocument.getLovDescCustDocIssuedCountry());
			label_issuedCuntry.setParent(listCell);
			listCell.setParent(listItem);

			listCell = new Listcell();
			listCell.setId("lc_"+customerDocument.getCustDocTitle()+i);
			Label label_idNumber  = new Label(customerDocument.getCustDocTitle());
			label_idNumber.setParent(listCell);
			listCell.setParent(listItem);

			/*listCell = new Listcell();
			listCell.setId("lc_"+customerDocument.getRecordStatus()+i);
			Label label_recordStatus  = new Label(customerDocument.getRecordStatus());
			label_recordStatus.setParent(listCell);
			listCell.setParent(listItem);

			listCell = new Listcell();
			listCell.setId("lc_"+customerDocument.getRecordType()+i);
			Label label_recordType  = new Label(customerDocument.getRecordType());
			label_recordType.setParent(listCell);
			listCell.setParent(listItem);*/

			listItem.setParent(lb_custDocsList);
			listItem.setAttribute("docData", customerDocument);
			ComponentsCtrl.applyForward(listItem, "onDoubleClick=onCustomerDocItemDoubleClicked");
			i++;
		}
		logger.debug("Leaving");
	}


	public Listbox getCustDocumentsListBox(Tabpanel tabPanel){

		Div div = new Div();
		//div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 0 + "px");
		div.setHeight("100%");
		Listbox listbox = new Listbox();
		listbox.setVflex(true);
		listbox.setSpan(true);

		//listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 0 + "px");
		listbox.setHeight("100%");

		div.setId("div_custDocsList");
		listbox.setId("lb_custDocsList");

		Listhead listHead = new Listhead();
		listHead.setId("listHead_custDocsList");

		Listheader listheader_CustDocType = new Listheader(Labels.getLabel("listheader_CustDocType.label",""));
		listheader_CustDocType.setHflex("min");
		listheader_CustDocType.setParent(listHead);	

		Listheader listheader_custDocIssuedCountry= new Listheader(Labels.getLabel("listheader_CustDocIssuedCountry.label"));
		listheader_custDocIssuedCountry.setHflex("min");
		listheader_custDocIssuedCountry.setParent(listHead);

		Listheader listheader_custDocTitle = new Listheader(Labels.getLabel("listheader_CustDocTitle.label"));
		listheader_custDocTitle.setHflex("min");
		listheader_custDocTitle.setParent(listHead);

		/*Listheader listheader_recordStatus= new Listheader(Labels.getLabel("label_CustomerDocumentSearch_RecordStatus.value", new String[]{"2012"}));
		listheader_recordStatus.setHflex("min");
		listheader_recordStatus.setParent(listHead);

		Listheader listheader_recordType = new Listheader(Labels.getLabel("label_CustomerDocumentSearch_RecordType.value"));
		listheader_recordType.setHflex("min");
		listheader_recordType.setParent(listHead);	*/

		listHead.setParent(listbox);
		listbox.setParent(div);
		div.setParent(tabPanel);
		listbox.setSizedByContent(true);

		logger.debug("Leaving");
		return listbox;
	}


	public void onCustomerDocItemDoubleClicked(ForwardEvent event) throws InterruptedException{
		logger.debug("Entering");                                                                                                              
		// get the selected invoiceHeader object      

		final Listitem item = (Listitem) event.getOrigin().getTarget();                                                                 
		if (item != null) {                                                                                                                    
			// CAST AND STORE THE SELECTED OBJECT                                                                                              
			final CustomerDocument customerDocument = (CustomerDocument) item.getAttribute("docData"); 
			customerDocument.setLovDescCustCIF(this.custCIF.getValue());
			if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {                                         
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {                                                                                                                           
				final HashMap<String, Object> map = new HashMap<String, Object>();                                                             
				map.put("customerDocument", customerDocument);                                                                                 
				map.put("creditApplicationReviewDialogCtrl", this);                                                                                           
				map.put("roleCode", getRole());                   

				try {                                                                                                                          
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);      
				} catch (Exception e) {                                                                                                  
					MessageUtil.showError(e);
				}                                                                                                                              
			}                                                                                                                                  
		}                                                                                                                                      
		logger.debug("Leaving");                                                                                                               
	}


	public void onClick$addNewDoc(ForwardEvent event){

		CustomerDocument customerDocument = new CustomerDocument();                                                               
		customerDocument.setNewRecord(true);                                                                                      
		customerDocument.setWorkflowId(0);
		customerDocument.setCustID(this.customer.getCustID());                                                             
		customerDocument.setLovDescCustCIF(this.customer.getCustCIF());                                      
		customerDocument.setLovDescCustShrtName(this.customer.getCustShrtName());                            
		final HashMap<String, Object> map = new HashMap<String, Object>();                                                        
		map.put("customerDocument", customerDocument);                                                                            
		map.put("creditApplicationReviewDialogCtrl", this);                                                                                      
		map.put("newRecord", "true");                                                                                             
		map.put("roleCode", getRole());                                                                                           
		try {                                                                                                                     
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map); 
		} catch (Exception e) {                                                                                             
			MessageUtil.showError(e);
		}                                                                                                                         
	}


	public void appendRecommendDetailTab() throws InterruptedException{
		logger.debug("Entering");
		Tab tab = new Tab("Comments & Recommendations");
		tab.setId("memoDetailTab");
		tabsIndexCenter.appendChild(tab);
		ComponentsCtrl.applyForward(tab, "onSelect=onSelectRecommendDetailTab");

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("memoDetailTabPanel");
		tabpanel.setHeight(this.borderLayoutHeight- 0 + "px");		
		tabpanel.setStyle("overflow:auto");
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		final HashMap<String, Object> map = new HashMap<String, Object>();
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


	public void insertSubCategory(FinCreditRevSubCategory aFinCreditRevSubCategory){

		Listbox listBox ;		
		listBox = (Listbox) tabBoxIndexCenter.getSelectedPanel().getFirstChild().getChildren().get(0);

		if(listBox != null){
			Listitem listItemNew ;
			Listitem listItemOld ;
			Listcell listCell ;

			listItemOld = (Listitem)listBox.getSelectedItem();

			if(listItemOld.getListgroup() != null) {
				Listgroup lg = (Listgroup)listItemOld.getListgroup();
				lg.setParent(listBox);
			}

			listItemNew = new Listitem();
			listItemNew.setId("li"+aFinCreditRevSubCategory.getSubCategoryCode());


			listCell = new Listcell();
			listCell.setLabel(aFinCreditRevSubCategory.getSubCategoryDesc());
			listCell.setParent(listItemNew);


			listCell = new Listcell();
			Decimalbox decimalbox = new Decimalbox();
			decimalbox.setId("db"+aFinCreditRevSubCategory.getSubCategoryCode());
			decimalbox.setAttribute("data", aFinCreditRevSubCategory);
			decimalbox.setAttribute("ListBoxdata", listBox);
			decimalbox.setFormat(amtFormat);
			decimalbox.setMaxlength(18);
			if((listOfFinCreditRevCategory.size() > 3 && 
					tabBoxIndexCenter.getSelectedTab().getId().endsWith("4"))
					|| (listOfFinCreditRevCategory.size() == 3 && 
					tabBoxIndexCenter.getSelectedTab().getId().endsWith("7"))){
				decimalbox.setDisabled(true);
				decimalbox.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF;border-width: 0;");
			}
			decimalbox.setParent(listCell);
			ComponentsCtrl.applyForward(decimalbox, "onChange=onChange$auditedValue");
			listCell.setParent(listItemNew);

			listCell = new Listcell();
			if(!tabBoxIndexCenter.getSelectedTab().getId().endsWith("3") 
					|| !tabBoxIndexCenter.getSelectedTab().getId().endsWith("4")
					|| !tabBoxIndexCenter.getSelectedTab().getId().endsWith("7")){
				listCell.setLabel("--");
			} else {
				listCell.setLabel("");
			}

			listCell.setParent(listItemNew);
			listBox.insertBefore(listItemNew, listItemOld);
		}
	}

	/**
	 * This method for building the listbox with dynamic headers.<br>
	 * 
	 */	
	public Listbox setListToTab(Tabpanel tabPanel,FinCreditRevCategory fcrc){
		logger.debug("Entering");

		Div div = new Div();
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 0 + "px");
		//div.setHeight("100%");
		Listbox listbox = new Listbox();
		listbox.setVflex(true);
		listbox.setSpan(true);

		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 0 + "px");
		//listbox.setHeight("100%");
		creditReviewSubCtgDetailsHeaders.setHeader("T");

		div.setId("div_"+fcrc.getCategoryId());
		listbox.setId("lb_"+fcrc.getCategoryId());

		Auxhead auxHead = new Auxhead();
		auxHead.setId("auxHead_"+fcrc.getCategoryId());

		Listhead listHead = new Listhead();
		listHead.setSizable(true);
		listHead.setId("listHead_"+fcrc.getCategoryId());


		Auxheader auxSubCategory = new Auxheader("");
		auxSubCategory.setColspan(2);
		auxSubCategory.setAlign("center");
		auxSubCategory.setParent(auxHead);

		Listheader listheader_addNewRecord = new Listheader();
		listheader_addNewRecord.setHflex("40px");
		listheader_addNewRecord.setAlign("center");
		listheader_addNewRecord.setParent(listHead);

		Listheader listheader_subCategoryCode = new Listheader(Labels.getLabel("listheader_categories.value"));
		listheader_subCategoryCode.setHflex("min");
		listheader_subCategoryCode.setParent(listHead);

		String audQual = "";
		String audQualRePort = "";
		String auxHeaderCurYerLabel = "";
		if(getCreditReviewDetails().getAuditType() != null){
			audQualRePort =Labels.getLabel("UnAudUnQual_Months",new String[]{getCreditReviewDetails().getAuditType(), 
					getCreditReviewDetails().isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED : FacilityConstants.CREDITREVIEW_UNQUALIFIED, 
							String.valueOf(getCreditReviewDetails().getAuditPeriod())});
			audQual = getCreditReviewDetails().getAuditType()+"/"+ (getCreditReviewDetails().isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED : FacilityConstants.CREDITREVIEW_UNQUALIFIED);
			auxHeaderCurYerLabel = getCreditReviewDetails().getAuditYear()+" - " + String.valueOf(getCreditReviewDetails().getAuditPeriod())+FacilityConstants.MONTH;
			creditReviewSubCtgDetailsHeaders.setCurYearAuditValueHeader(audQualRePort);
		} else {
			String qualUnQual= this.qualifiedUnQualified.getSelectedItem().getValue().toString();
			audQualRePort = Labels.getLabel("UnAudUnQual_Months", new String[] { this.auditType.getSelectedItem() == null ?
					FacilityConstants.CREDITREVIEW_AUDITED :
						this.auditType.getSelectedItem().getValue().toString(), qualUnQual, String.valueOf(getCreditReviewDetails().getAuditPeriod()) });

			audQual = this.auditType.getSelectedItem().getValue().toString()+"/"+ qualUnQual;
			auxHeaderCurYerLabel = this.auditedYear.getValue()+" - " +String.valueOf(getCreditReviewDetails().getAuditPeriod())+FacilityConstants.MONTH;
			creditReviewSubCtgDetailsHeaders.setCurYearAuditValueHeader(audQualRePort);
		}


		Label audQualLabel = new Label(audQual);
		audQualLabel.setId(getId(fcrc.getCategoryDesc()));
		audQualLabel.setStyle("font-weight:bold");

		Auxheader auxHeader_curYear = new Auxheader();
		auxHeader_curYear.setColspan(3);
		auxHeader_curYear.setLabel(auxHeaderCurYerLabel);
		auxHeader_curYear.setAlign("center");
		auxHeader_curYear.setParent(auxHead);

		Listheader listheader_audAmt = new Listheader();
		//listheader_audAmt.setId(getId(fcrc.getCategoryDesc()));
		listheader_audAmt.setHflex("min");
		listheader_audAmt.setAlign("center");
		audQualLabel.setMultiline(true);
		audQualLabel.setParent(listheader_audAmt);
		listheader_audAmt.setParent(listHead);	

		Listheader listheader_curUSConvrtn = new Listheader(AccountConstants.CURRENCY_USD);
		listheader_curUSConvrtn.setHflex("85px");
		listheader_curUSConvrtn.setAlign("center");
		listheader_curUSConvrtn.setId("curCnvrtn"+getId(fcrc.getCategoryDesc()));
		listheader_curUSConvrtn.setParent(listHead);

		Listheader listheader_breakDown= new Listheader(Labels.getLabel("listheader_breakDowns.value", ""));
		creditReviewSubCtgDetailsHeaders.setCurYearBreakDownHeader(Labels.getLabel("listheader_breakDowns.value", "")+"_"
				+this.creditReviewDetails.getAuditPeriod()+FacilityConstants.MONTH);

		listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
		listheader_breakDown.setHflex("min");
		listheader_breakDown.setAlign("center");
		listheader_breakDown.setParent(listHead);

		String prevPeriodLabel = "_"+String.valueOf(prevAuditPeriod)+FacilityConstants.MONTH;
		String prevAudOrUnAudReport = "";
		String prevAudOrUnAud = "";

		String qualOrUnqual = "";
		String auxHeadPrevYrLabel = String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear())-1)+prevPeriodLabel;

		FinCreditReviewDetails finCreditReviewDetails = this.creditApplicationReviewService.getCreditReviewDetailsByCustIdAndYear(this.custID.getValue(),
				String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear())-1), "_VIEW");

		if(finCreditReviewDetails != null && StringUtils.isNotBlank(finCreditReviewDetails.getAuditType())){
			qualOrUnqual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED : FacilityConstants.CREDITREVIEW_UNQUALIFIED ;
			prevAudOrUnAud = finCreditReviewDetails.getAuditType()+"/"+ qualOrUnqual;
			prevAudOrUnAudReport = finCreditReviewDetails.getAuditType()+"/"+ qualOrUnqual+" "
					+String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear())-1)+prevPeriodLabel;
		} else {
			prevAudOrUnAudReport = Labels.getLabel("listheader_audAmt1.value", new String[]{
					String.valueOf(Integer.parseInt(this.creditReviewDetails.getAuditYear())-1)})+prevPeriodLabel;
			prevAudOrUnAud =Labels.getLabel("listheader_auxAudAmt1.value");
		}

		Auxheader auxHeader_prevYear = new Auxheader();
		auxHeader_prevYear.setLabel(auxHeadPrevYrLabel);
		auxHeader_prevYear.setAlign("center");
		auxHeader_prevYear.setParent(auxHead);

		if(fcrc.getCategoryId() == 3 || fcrc.getCategoryId() == 4 || fcrc.getCategoryId() == 7){
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

		Listheader listheader_prevcurUSConvrtn = new Listheader(AccountConstants.CURRENCY_USD);
		listheader_prevcurUSConvrtn.setHflex("85px");
		listheader_prevcurUSConvrtn.setAlign("center");
		listheader_prevcurUSConvrtn.setId("prevCnvrtn"+getId(fcrc.getCategoryDesc()));
		listheader_prevcurUSConvrtn.setParent(listHead);

		int currentAuditYear = Integer.parseInt(this.creditReviewDetails.getAuditYear());

		Listheader listheader_previousBreakDown= new Listheader(Labels.getLabel("listheader_breakDowns.value"));
		creditReviewSubCtgDetailsHeaders.setPreYearBreakDownHeader(Labels.getLabel("listheader_breakDown2.value", 
				new String[]{String.valueOf(currentAuditYear-1)})+prevPeriodLabel);
		listheader_previousBreakDown.setHflex("min");
		listheader_previousBreakDown.setAlign("center");
		listheader_previousBreakDown.setVisible(fcrc.isBrkdowndsply());
		listheader_previousBreakDown.setParent(listHead);

		Listheader listheader_percentChange = new Listheader(Labels.getLabel("listheader_percentChange.value"));
		creditReviewSubCtgDetailsHeaders.setCurYearPerHeader(String.valueOf(currentAuditYear)+"_"+String.valueOf(currentAuditYear-1)
				+Labels.getLabel("listheader_percentChange.value"));
		listheader_percentChange.setHflex("min");
		listheader_percentChange.setAlign("center");
		listheader_percentChange.setParent(listHead);	

		auxHead.setParent(listbox);
		listHead.setParent(listbox);
		listbox.setParent(div);
		listbox.setAttribute("isRatio",fcrc.getRemarks());
		div.setParent(tabPanel);

		for (CreditReviewSubCtgDetails creditReviewSubCtgDetails : creditReviewSubCtgDetailsList) {
			if("T".equals(creditReviewSubCtgDetails.getMainGroup())){
				creditReviewSubCtgDetails.setCurYearAuditValueHeader(creditReviewSubCtgDetailsHeaders.getCurYearAuditValueHeader());
				creditReviewSubCtgDetails.setCurYearBreakDownHeader(creditReviewSubCtgDetailsHeaders.getCurYearBreakDownHeader());
				creditReviewSubCtgDetails.setPreYearAuditValueHeader(creditReviewSubCtgDetailsHeaders.getPreYearAuditValueHeader());
				creditReviewSubCtgDetails.setPreYearBreakDownHeader(creditReviewSubCtgDetailsHeaders.getPreYearBreakDownHeader());
				creditReviewSubCtgDetails.setCurYearPerHeader(creditReviewSubCtgDetailsHeaders.getCurYearPerHeader());
			}
		}

		//	creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetailsHeaders);
		logger.debug("Leaving");
		listbox.setSizedByContent(true);
		return listbox;

	}

	/**
	 * onChange get the customer Details
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$custCIF(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*this.custCIF.clearErrorMessage();

		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), getFilterList());

		if(customer == null) {		
			this.custShrtName.setValue("");

			if(this.tabpanelsBoxIndexCenter.getChildren() != null){
				this.tabpanelsBoxIndexCenter.getChildren().clear();
			}
			if(this.tabsIndexCenter.getChildren()!= null){
				this.tabsIndexCenter.getChildren().clear();
			}
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value")}));
		} else {
			doSetCustomer(customer, null);
		}*/

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onFulfill$custCIF(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	

		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject",this.newSearchObject);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws Exception 
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws Exception{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custCIF.setTooltiptext(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.creditRevCode = aCustomer.getLovDescCustCtgType();
		this.newSearchObject = newSearchObject;
		String category="";

		if(aCustomer.getCustCtgCode().equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_SME)){
			category = PennantConstants.PFF_CUSTCTG_SME;
		} else {
			category = PennantConstants.PFF_CUSTCTG_CORP;
		}
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}

		String type = "";
		if(creditReviewDetails.isNew()){
			creditReviewDetails.setConversionRate(this.conversionRate.getValue() == null ? BigDecimal.ZERO : this.conversionRate.getValue());
		}
		if((!StringUtils.trimToEmpty(creditReviewDetails.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED) 
				&& StringUtils.trimToEmpty(creditReviewDetails.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) 
				|| StringUtils.trimToEmpty(creditReviewDetails.getRecordType()).equals(PennantConstants.RECORD_TYPE_UPD)){
			type = "_Temp";
		} 

		//Get FinCredit Categories based on the Customer Category Type 
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.creditRevCode);
		prvYearValuesMap = new HashMap<String, BigDecimal>();

		this.creditReviewSummaryList = this.creditApplicationReviewService.getLatestCreditReviewSummaryByCustId(this.custID.longValue());

		int audityear = Integer.parseInt(creditReviewDetails.getAuditYear());

		creditReviewSummaryMap= getCreditApplicationReviewService().
				getListCreditReviewSummaryByCustId2(aCustomer.getCustID(), 2, audityear-1,  
						category, creditReviewDetails.getAuditPeriod(), false, "_VIEW");

		prv1YearValuesMap = new HashMap<String,BigDecimal>();
		prv1YearValuesMap.put("auditYear", BigDecimal.valueOf(audityear-2));
		creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear-2));
		if(this.creditReviewSummaryList != null && this.creditReviewSummaryList.size() > 0){
			for(int k=0;k<this.creditReviewSummaryList.size(); k++){
				prv1YearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter).setScale(this.currFormatter,RoundingMode.HALF_DOWN));
				engine.put("Y"+(audityear-2)+this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));
			}
		}

		if(prv1YearValuesMap!= null && prv1YearValuesMap.size() >0){
			setData(prv1YearValuesMap);
		}

		//creditReviewSummaryMap= getCreditApplicationReviewService().
		//       getListCreditReviewSummaryByCustId2(aCustomer.getCustID(), 0, audityear-1,  
		//     category, creditReviewDetails.getAuditPeriod(), false, "");

		boolean isPrevYearSummayNull = false;
		this.creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear-1));
		if(this.creditReviewSummaryList == null){
			isPrevYearSummayNull = true;
		}
		prvYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear-1));
		if(!isPrevYearSummayNull && this.creditReviewSummaryList.size() > 0){
			for(int k=0;k<this.creditReviewSummaryList.size(); k++){
				prvYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter).setScale(this.currFormatter,RoundingMode.HALF_DOWN));
				//  summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),this.creditReviewSummaryList.get(k));
				engine.put("Y"+(audityear-1)+this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));
			}
		}

		setData(prvYearValuesMap);
		//if(!this.creditReviewDetails.isNewRecord()){

		creditReviewSummaryMap= getCreditApplicationReviewService().
				getListCreditReviewSummaryByCustId2(aCustomer.getCustID(), 0, audityear, 
						category, creditReviewDetails.getAuditPeriod(), true, type);

		if(creditReviewSummaryMap.get(creditReviewDetails.getAuditYear()) != null && creditReviewSummaryMap.size() > 0) {
			this.creditReviewSummaryList = creditReviewSummaryMap.get(creditReviewDetails.getAuditYear());
			curYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear));
			for(int k=0;k<this.creditReviewSummaryList.size(); k++){
				curYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));
				summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),this.creditReviewSummaryList.get(k));
				engine.put("Y"+audityear+this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));
				if(isPrevYearSummayNull){
					prvYearValuesMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
					engine.put("Y"+(audityear-1)+this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
					if(prvYearValuesMap!= null && prvYearValuesMap.size() >0){
						setData(prvYearValuesMap);
					}
				}
			}
		} else {
			for(int k=0;k<this.listOfFinCreditRevSubCategory.size(); k++){
				curYearValuesMap.put(this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
				engine.put("Y"+audityear+this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
				if(isPrevYearSummayNull){
					prvYearValuesMap.put(this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
					engine.put("Y"+(audityear-1)+this.listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
					if(prvYearValuesMap!= null && prvYearValuesMap.size() >0){
						setData(prvYearValuesMap);
					}
				}
			}
		}
		if(curYearValuesMap != null){
			setData(curYearValuesMap);
		}
		//}
		setTabs();
		logger.debug("Leaving");
	}


	/**
	 * This Method for rendering 
	 * @param categoryId
	 * @param listbox
	 * @throws Exception
	 */

	@SuppressWarnings("unused")
	public void render(FinCreditRevCategory fcrc,Listbox listbox) throws Exception {
		logger.debug("Entering");

		Listitem item = null;
		String recordType=null;
		boolean isRatio = false;

		if(FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrc.getRemarks())){
			isRatio = true;
		}
		listbox.setAttribute("fcrc", fcrc);

		//Get all the SubcategoryCodes for the category ID 
		for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){
			FinCreditRevSubCategory finCreditRevSubCategory = null;
			finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(i);
			if(finCreditRevSubCategory.getCategoryId() == fcrc.getCategoryId() ){
				if(!isRatio){
					finCreditRevSubCategory.setMainSubCategoryCode(fcrc.getCategoryDesc());
				}
				fillRenderer(finCreditRevSubCategory, listbox, item, false,fcrc.getCategoryDesc());
			}
		}
		getTotalLiabAndAssetsDifference();
		logger.debug("Leaving");
	}


	/**
	 * Method to add a new SubCategory
	 * @param event
	 */
	public void onClick$addNewRecord(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		Listitem oldItem =  (Listitem) event.getOrigin().getTarget().getParent().getParent();
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");

		Listbox listbox = (Listbox) oldItem.getParent();         
		getListItemsDisabled(listbox, true);

		//Create a new Item to enter a new SubCategory
		Listitem newItem = getNewItem(finCreditRevSubCategory, oldItem, false);
		listbox.insertBefore(newItem, oldItem.getNextSibling());

		logger.debug("Leaving" + event.toString());

	}

	/**
	 *  Method for getting new Record
	 */

	public Listitem getNewItem(FinCreditRevSubCategory finCreditRevSubCategory, Listitem oldItem, boolean isEdit){
		logger.debug("Leaving");

		Listitem newItem = new Listitem();
		Listcell lc;
		//Add Image
		lc = new Listcell();
		Image lcImage;

		lc.setId("lc_ImgBtnDelete");
		lcImage = new Image("/images/icons/Old/remove.png");
		lcImage.setParent(lc);
		ComponentsCtrl.applyForward(lcImage, "onClick=onClick$removeRecord");
		if(isEdit) {
			Space space = new Space();
			space.setSpacing("8px");
			space.setParent(lc);
			lcImage = new Image("/images/icons/Old/cancel.png");
			lcImage.setParent(lc);
			ComponentsCtrl.applyForward(lcImage, "onClick=onClick$cancelRecord");

		}
		lc.setParent(newItem);

		//Subcategory Code 
		lc = new Listcell();
		Uppercasebox tb_subCategoryCode = new Uppercasebox();
		tb_subCategoryCode.setStyle(" text-transform: uppercase;");
		tb_subCategoryCode.setId("tb_NewSubCategoryCode");
		if(isEdit){
			tb_subCategoryCode.setValue(finCreditRevSubCategory.getSubCategoryCode());
		}
		tb_subCategoryCode.setWidth("80px");
		tb_subCategoryCode.setErrorMessage("");
		tb_subCategoryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"), 
				PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true ));
		tb_subCategoryCode.setParent(lc);

		Label label = new Label("_");
		label.setParent(lc);

		//Subcategory description
		Textbox tb_subCategoryDesc = new Textbox();
		tb_subCategoryDesc.setId("tb_NewSubCategoryDesc");
		if(isEdit){
			tb_subCategoryDesc.setValue(finCreditRevSubCategory.getSubCategoryDesc());
		}
		tb_subCategoryDesc.setWidth("120px");
		tb_subCategoryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryDesc.value"), null, true ));
		tb_subCategoryDesc.setParent(lc);
		Space space=new Space();
		space.setSpacing("60px");
		space.setParent(lc);
		label = new Label("Main Category Code ");
		label.setParent(lc);
		lc.setParent(newItem);

		//Main Category Code 
		lc = new Listcell();
		Combobox cb_MainCategoryCode = new Combobox();
		cb_MainCategoryCode.setId("cb_mainCategoryCode");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		cb_MainCategoryCode.appendChild(comboitem);
		cb_MainCategoryCode.setSelectedItem(comboitem);

		for(int i=0; i < listOfFinCreditRevSubCategory.size(); i++){
			FinCreditRevSubCategory aFinCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (aFinCreditRevSubCategory.getCategoryId() == finCreditRevSubCategory.getCategoryId()
					&& aFinCreditRevSubCategory.getSubCategoryItemType().equals(
							FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
				comboitem = new Comboitem();
				comboitem.setValue(aFinCreditRevSubCategory.getSubCategoryCode());
				comboitem.setLabel(aFinCreditRevSubCategory.getSubCategoryDesc());
				comboitem.setParent(cb_MainCategoryCode);
				if(isEdit && aFinCreditRevSubCategory.getSubCategoryCode().equals(finCreditRevSubCategory.getMainSubCategoryCode())){
					cb_MainCategoryCode.setSelectedItem(comboitem);
				}
				cb_MainCategoryCode.appendChild(comboitem);

			}
		}
		cb_MainCategoryCode.setWidth("180px");
		cb_MainCategoryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value"), null, true ));
		cb_MainCategoryCode.setVisible(!FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks()));
		cb_MainCategoryCode.setParent(lc);
		lc.setParent(newItem);

		lc = new Listcell();
		Combobox cb_MathOperation = new Combobox();
		cb_MathOperation.setId("cb_MathOperation");
		fillComboBox(cb_MathOperation, "", PennantStaticListUtil.getCreditReviewRuleOperator(), "");
		cb_MathOperation.setWidth("180px");
		cb_MathOperation.setConstraint(new PTStringValidator(Labels.getLabel("listheader_Operation"), null, true ));
		cb_MathOperation.setParent(lc);
		lc.setParent(newItem);


		lc = new Listcell();
		Button btnForSave = new Button("Save");
		btnForSave.setId("btnForSave_NewRecord");
		btnForSave.addForward("onClick", this.window_CreditApplicationReviewDialog, "onClick$btnForSave", listOfFinCreditRevSubCategory);
		btnForSave.setParent(lc);
		lc.setParent(newItem);

		lc = new Listcell();
		lc.setParent(newItem);

		lc = new Listcell();
		lc.setParent(newItem);

		newItem.setParent(oldItem.getParent());
		oldItem.getParent().insertBefore(newItem, oldItem.getNextSibling());
		if(isEdit){
			oldItem.getParent().removeChild(oldItem);
			newItem.setAttribute("finData", finCreditRevSubCategory);
		}
		logger.debug("Leaving");

		return newItem;
	}

	/**
	 *  onClick$removeRecord Event For Removing New Item from List box
	 * @throws Exception 
	 */

	public void onClick$removeRecord(ForwardEvent event) throws Exception{
		logger.debug("Entering" + event.toString());

		Listitem oldItem =  (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Listbox listbox = (Listbox) oldItem.getParent();
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");
		listbox.removeItemAt(oldItem.getIndex());
		if(aFinCreditRevSubCategory != null) {
			removeRecordFromFinSubCategoryList(aFinCreditRevSubCategory);
			setData(curYearValuesMap);
			getListboxDetails(listbox);
		}
		getListItemsDisabled(listbox, false);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for getting listbox with newly calculated values
	 * @param listbox
	 */
	public void getListboxDetails(Listbox listbox){
		logger.debug("Entering");

		Decimalbox db_Amount = null;
		BigDecimal breakDown = BigDecimal.ZERO;
		Label label_BreakDown = null; 
		Label label_PrecentChange = null; 
		Label label_convrsnUSD = null; 
		FinCreditRevSubCategory finCreditRevSubCategory;
		for (Listitem item : listbox.getItems()) {
			if(!(item instanceof Listgroup)) {
				
				/***************** Amount ***************/
				finCreditRevSubCategory = (FinCreditRevSubCategory) item.getAttribute("finData");	
				db_Amount = (Decimalbox) item.getFellowIfAny("db"+finCreditRevSubCategory.getSubCategoryCode());
				if(db_Amount != null){
					db_Amount.setValue(curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()));
				}

				/*************************** Current Year US$ Conversion  ************************/
				label_convrsnUSD = (Label) item.getFellowIfAny("uSD1_"+finCreditRevSubCategory.getSubCategoryCode());
				BigDecimal curAmt = db_Amount.getValue() != null ? db_Amount.getValue() : BigDecimal.ZERO;
				BigDecimal convrsnPrice = BigDecimal.ZERO;
				if(creditReviewDetails.getConversionRate() != null && creditReviewDetails.getConversionRate() != BigDecimal.ZERO){
					convrsnPrice = curAmt.divide(creditReviewDetails.getConversionRate(), FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
				} else if(conversionRate.getValue() != null && conversionRate.getValue() != BigDecimal.ZERO){
					convrsnPrice = curAmt.divide(conversionRate.getValue());
				}
				label_convrsnUSD.setValue(PennantAppUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false));


				/*************************** Break Down ************************/
				label_BreakDown = (Label) item.getFellowIfAny("rLabel"+finCreditRevSubCategory.getSubCategoryCode()); 
				if(label_BreakDown != null){
					breakDown = (BigDecimal) (curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode())==null ? BigDecimal.ZERO:
						curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode()));
					label_BreakDown.setValue(PennantAppUtil.formatAmount(breakDown, 2, false));			
				}


				/*************************** % Change In Break Down ************************/
				label_PrecentChange = (Label) item.getFellowIfAny("percent"+finCreditRevSubCategory.getSubCategoryCode()); 

				BigDecimal prevAuditAmt = prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())!= null ? 
						prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) : new BigDecimal(BigInteger.ZERO, this.currFormatter);

						BigDecimal percentChange = getPercChange(prevAuditAmt, db_Amount.getValue()== null ? BigDecimal.ZERO : db_Amount.getValue());

						if(label_PrecentChange != null){ 
							if(percentChange.compareTo(BigDecimal.ZERO) == 0 ){
								label_PrecentChange.setValue( "0.00 %");			
							}else{ 
								label_PrecentChange.setValue( String.valueOf(percentChange) + " %");			
								if(percentChange.compareTo(BigDecimal.ZERO) == -1 ){
									label_PrecentChange.setStyle("color:red");
								} else {
									label_PrecentChange.setStyle("color:green; font-weight:bold");
								}
							}
						}
						if(creditReviewSubCtgDetailsList != null && !creditReviewSubCtgDetailsList.isEmpty()) {
							for (CreditReviewSubCtgDetails creditReviewSubCtgDetails: creditReviewSubCtgDetailsList) {
								if(creditReviewSubCtgDetails.getSubCategoryDesc().equalsIgnoreCase(finCreditRevSubCategory.getSubCategoryDesc())){
									// Current Year Audit Value
									creditReviewSubCtgDetails.setCurYearAuditValue(PennantAppUtil.formatAmount(db_Amount.getValue(), this.currFormatter, false));
									// Current Year Conversation Price
									creditReviewSubCtgDetails.setCurYearUSDConvstn(PennantAppUtil.formatAmount(convrsnPrice, 2,false));
									// Current Year Break Down
									creditReviewSubCtgDetails.setCurYearBreakDown(PennantAppUtil.formatAmount(breakDown, 2, false));
									// Current Year Percentage Change
									creditReviewSubCtgDetails.setCurYearPercentage(PennantAppUtil.formatAmount(percentChange, 2, false));
								}
							}
						}

			}
		}

		logger.debug("Leaving");
	}



	public void getTotalLiabAndAssetsDifference(){
		logger.debug("Entering");

		String totAsst = "";
		String totLibNetWorth = "";

		if(customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)){
			totAsst = FacilityConstants.CREDITREVIEW_BANK_TOTASST;
			totLibNetWorth = FacilityConstants.CREDITREVIEW_BANK_TOTLIBNETWRTH; 
		} else if(customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_CORP)){
			totAsst = FacilityConstants.CREDITREVIEW_CORP_TOTASST;
			totLibNetWorth = FacilityConstants.CREDITREVIEW_CORP_TOTLIBNETWRTH; 	
		}

		this.totAssets = curYearValuesMap.get(totAsst) == null ? BigDecimal.ZERO : curYearValuesMap.get(totAsst);
		this.totLiabilities = curYearValuesMap.get(totLibNetWorth) == null ? BigDecimal.ZERO : curYearValuesMap.get(totLibNetWorth);
		this.totLibAsstDiff.setValue(this.totAssets.subtract(this.totLiabilities));

		if((this.totLibAsstDiff.getValue() == null ? BigDecimal.ZERO : this.totLibAsstDiff.getValue()).compareTo(BigDecimal.ZERO) == 0){
			this.totLibAsstDiff.setStyle("color:green;");
		} else {
			this.totLibAsstDiff.setStyle("color:red;");
		}
		logger.debug("Leaving");
	}

	/**
	 *  Method For Removing FinCreditSubCategory From FinCreditSubCategoryList
	 * @param aFinCreditRevSubCategory
	 */
	public void removeRecordFromFinSubCategoryList(FinCreditRevSubCategory aFinCreditRevSubCategory){
		logger.debug("Entering");

		for(FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory) {
			if(aFinCreditRevSubCategory.getMainSubCategoryCode().equals(finCreditRevSubCategory.getSubCategoryCode())) {
				String mainRule=finCreditRevSubCategory.getItemsToCal();

				String rule="YN."+aFinCreditRevSubCategory.getSubCategoryCode();
				String oprator=String.valueOf(mainRule.charAt( mainRule.indexOf(rule)-1));
				finCreditRevSubCategory.setItemsToCal(mainRule.replace(oprator+rule, ""));

				listOfFinCreditRevSubCategory.remove(aFinCreditRevSubCategory);
				modifiedFinCreditRevSubCategoryList.remove(aFinCreditRevSubCategory); 
				curYearValuesMap.remove(aFinCreditRevSubCategory.getSubCategoryCode()); 
				curYearValuesMap.remove(FacilityConstants.CREDITREVIEW_REMARKS+aFinCreditRevSubCategory.getSubCategoryCode()); 

				break;
			}
		}
		logger.debug("Leaving");
	}

	public Listbox getListItemsDisabled(Listbox listbox, boolean disabled){
		logger.debug("Entering");

		List<Listitem> listItems = listbox.getItems();
		for(Listitem item : listItems){
			List<Component> components = item.getFirstChild().getChildren();
			for(Component component : components){
				component.setVisible(!disabled);
			}
			if(!(item instanceof Listgroup)) {
				Listcell	lc_addNewrecord = (Listcell)item.getFirstChild();        	
				Listcell db_listcell = (Listcell)lc_addNewrecord.getNextSibling().getNextSibling(); 
				Decimalbox db_AuditAmt = (Decimalbox)db_listcell.getChildren().get(0);
				db_AuditAmt.setDisabled(disabled);
				item.setDisabled(disabled);
			}
		}
		logger.debug("Leaving");

		return listbox;
	}


	/**
	 * Method to save a new Sub Category Code 
	 * @param event
	 */
	public void onClick$btnForSave(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		Listitem listitem = (Listitem)event.getOrigin().getTarget().getParent().getParent();
		listitem.setAttribute("NewSubCategory", true);

		Listitem oldListitem = (Listitem)listitem.getPreviousSibling();
		FinCreditRevSubCategory preFinCreditRevSubCategory = (FinCreditRevSubCategory)oldListitem.getAttribute("finData");
		FinCreditRevSubCategory finCreditRevMainCategory;
		Listbox listBox = (Listbox) listitem.getParent();

		Textbox tb_subCategoryCode = (Textbox)listitem.getFellowIfAny("tb_NewSubCategoryCode");		
		Textbox tb_subCategoryDesc = (Textbox)listitem.getFellowIfAny("tb_NewSubCategoryDesc");		
		Combobox cb_mainCategoryCode = (Combobox)listitem.getFellowIfAny("cb_mainCategoryCode");
		Combobox cb_MathOperation = (Combobox)listitem.getFellowIfAny("cb_MathOperation");
		boolean isDataChanged =false;
		for(FinCreditRevSubCategory lfinCreditRevSubCategory : listOfFinCreditRevSubCategory){
			if(modifiedFinCreditRevSubCategoryList.contains(lfinCreditRevSubCategory)){
				try{
					tb_subCategoryCode.getValue();
				} catch (Exception e) {
					throw new WrongValueException(e);
				}
				if(!lfinCreditRevSubCategory.getSubCategoryCode().equals(tb_subCategoryCode.getValue())
						&& lfinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					isDataChanged = false;
				} else if(lfinCreditRevSubCategory.getSubCategoryCode().equals(tb_subCategoryCode.getValue())
						&& lfinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){


					if(!lfinCreditRevSubCategory.getMainSubCategoryCode().equals(cb_mainCategoryCode.getSelectedItem().getValue().toString())){

						Listitem calcListItem = (Listitem) listBox.getFellowIfAny("li"+lfinCreditRevSubCategory.getMainSubCategoryCode());
						finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");

						String mainRule=finCreditRevMainCategory.getItemsToCal();
						String rule="YN."+lfinCreditRevSubCategory.getSubCategoryCode();
						String oprator=String.valueOf(mainRule.charAt( mainRule.indexOf(rule)-1));
						finCreditRevMainCategory.setItemsToCal(mainRule.replace(oprator+rule, ""));

						modifiedFinCreditRevSubCategoryList.remove(finCreditRevMainCategory);  // Removing SubCategory from Old MainCategory

						calcListItem = (Listitem) listBox.getFellowIfAny("li"+cb_mainCategoryCode.getSelectedItem().getValue().toString());
						finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");
						finCreditRevMainCategory.setItemsToCal(finCreditRevMainCategory.getItemsToCal()+
								cb_MathOperation.getSelectedItem().getValue().toString().trim()+"YN."+tb_subCategoryCode.getValue().trim());
						finCreditRevMainCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						modifiedFinCreditRevSubCategoryList.add(finCreditRevMainCategory);  // Adding New Sub Category to  MainCategory

					}

					lfinCreditRevSubCategory.setSubCategoryCode(tb_subCategoryCode.getValue());
					lfinCreditRevSubCategory.setSubCategoryDesc(tb_subCategoryDesc.getValue());
					lfinCreditRevSubCategory.setItemsToCal("");
					lfinCreditRevSubCategory.setItemRule("YN."+lfinCreditRevSubCategory.getSubCategoryCode()+"*100/YN."+lfinCreditRevSubCategory.getMainSubCategoryCode());

					isDataChanged = true;
				}
			} if(!isDataChanged && tb_subCategoryCode.getValue().trim().equalsIgnoreCase(lfinCreditRevSubCategory.getSubCategoryCode().trim())){
				throw new WrongValueException(tb_subCategoryCode, Labels.getLabel("FIELD_NO_DUPLICATE",
						new String[] {Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value")}));
			}
		}

		if(isDataChanged){
			doDeleteItem(listitem);
		} else {
			FinCreditRevSubCategory aFinCreditRevSubCategory = null;

			Checkbox categoryItemType = (Checkbox) listitem.getFellowIfAny("rdCalculated");
			aFinCreditRevSubCategory = new FinCreditRevSubCategory();		
			aFinCreditRevSubCategory.setCategoryId(Long.parseLong(listitem.getParent().getId()
					.substring(listitem.getParent().getId().length() - 1)));
			aFinCreditRevSubCategory.setSubCategoryCode(tb_subCategoryCode.getValue());
			aFinCreditRevSubCategory.setSubCategoryDesc(tb_subCategoryDesc.getValue());
			aFinCreditRevSubCategory.setMainSubCategoryCode(cb_mainCategoryCode.getSelectedItem().getValue().toString());
			aFinCreditRevSubCategory.setSubCategorySeque(preFinCreditRevSubCategory.getSubCategorySeque()+1);
			aFinCreditRevSubCategory.setCalcSeque(String.valueOf(Integer.parseInt(preFinCreditRevSubCategory.getCalcSeque())+1));

			if(categoryItemType != null){
				aFinCreditRevSubCategory.setSubCategoryItemType(categoryItemType.isChecked() ? 
						FacilityConstants.CREDITREVIEW_CALCULATED_FIELD : FacilityConstants.CREDITREVIEW_ENTRY_FIELD);
			} else {
				aFinCreditRevSubCategory.setSubCategoryItemType(FacilityConstants.CREDITREVIEW_ENTRY_FIELD);
			}
			aFinCreditRevSubCategory.setItemsToCal("");
			aFinCreditRevSubCategory.setItemRule("YN."+aFinCreditRevSubCategory.getSubCategoryCode()+"*100/YN."+aFinCreditRevSubCategory.getMainSubCategoryCode());
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


			if(FacilityConstants.CREDITREVIEW_REMARKS.equals(aFinCreditRevSubCategory.getRemarks())){
				fillRenderer(aFinCreditRevSubCategory, listBox, listitem, true,"");
				getListItemsDisabled(listBox, false);
			} else {
				if("#".equals(aFinCreditRevSubCategory.getMainSubCategoryCode())){
					throw new WrongValueException(cb_mainCategoryCode, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] {Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value")}));
				} else {
					Listitem calcListItem = (Listitem) listBox.getFellowIfAny("li"+aFinCreditRevSubCategory.getMainSubCategoryCode());
					finCreditRevMainCategory = (FinCreditRevSubCategory) calcListItem.getAttribute("finData");
				}
				if("#".equals(cb_MathOperation.getSelectedItem().getValue().toString())){
					throw new WrongValueException(cb_MathOperation, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] {Labels.getLabel("listheader_Operation")}));
				} else {
					finCreditRevMainCategory.setItemsToCal(finCreditRevMainCategory.getItemsToCal()+
							cb_MathOperation.getSelectedItem().getValue().toString().trim()+"YN."+tb_subCategoryCode.getValue().trim());
				}
				finCreditRevMainCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				fillRenderer(aFinCreditRevSubCategory, listBox, listitem, true,"");
				getListItemsDisabled(listBox, false);
				modifiedFinCreditRevSubCategoryList.add(aFinCreditRevSubCategory);

				curYearValuesMap.put(aFinCreditRevSubCategory.getSubCategoryCode(), PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));
				engine.put(aFinCreditRevSubCategory.getSubCategoryCode(), PennantAppUtil.formateAmount(BigDecimal.ZERO,this.currFormatter));

				modifiedFinCreditRevSubCategoryList.add(finCreditRevMainCategory);
				listOfFinCreditRevSubCategory.add(aFinCreditRevSubCategory);
			} 

		}

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * 
	 * @param finCreditRevSubCategory
	 * @param listbox
	 * @param oldListItem
	 */
	public void fillRenderer(FinCreditRevSubCategory finCreditRevSubCategory, Listbox listbox, Listitem oldListItem, boolean isNewRecord,String categoryDesc){
		logger.debug("Entering");
		CreditReviewSubCtgDetails  creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
		creditReviewSubCtgDetails.setMainGroupDesc(finCreditRevSubCategory.getMainSubCategoryCode());
		creditReviewSubCtgDetails.setTabDesc(categoryDesc);
		Listitem item = null;
		Listgroup lg= null;
		Listcell lc = null;
		Decimalbox db_Amount = null;
		Image lcImage = null;		
		String mainCategory = "";
		String amtFormat=PennantApplicationUtil.getAmountFormate(currFormatter);
		BigDecimal prvAmt, curAmt, convrsnPrice;
		String uSDConvstnVal="";
		boolean isRatio = false;
		if(FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks())){
			isRatio = true;
			creditReviewSubCtgDetails.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
		}

		//Create ListGroup if the category is Ratio
		if(isRatio && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())){
			mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
			listMainSubCategoryCodes.add(new ValueLabel("mainSubCategoryCode", mainCategory));
			lg =  new Listgroup();
			lg.setId(mainCategory);
			if(!listbox.hasFellow(mainCategory)){
				lg.setLabel(mainCategory);
				lg.setOpen(true);
				lg.setStyle("font-weight:bold;background-color: #ADD8E6;");
				lg.setParent(listbox);
				creditReviewSubCtgDetails.setGroupCode(FacilityConstants.CREDITREVIEW_GRP_CODE_TRUE);
			}
		}

		if(!isRatio && this.creditReviewDetails.isNew()){
			if(!curYearValuesMap.containsKey(finCreditRevSubCategory.getSubCategoryCode())){
				engine.put("Y"+this.auditPeriod.getValue()+finCreditRevSubCategory.getSubCategoryCode(),BigDecimal.ZERO);
			}
		}

		item = new Listitem();
		item.setId(String.valueOf("li"+finCreditRevSubCategory.getSubCategoryCode()));	

		lc = new Listcell();
		lc.setId("lcAdd_"+finCreditRevSubCategory.getSubCategoryCode());

		if(!isRatio){
			lcImage = new Image("/images/icons/Old/add_button.png");
			lcImage.setParent(lc);
			lcImage.setVisible(getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory"));
			ComponentsCtrl.applyForward(lcImage, "onClick=onClick$addNewRecord");
		}

		// Adding Deleting Option For Newly Added Record
		if(isNewRecord){
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
		Label label_SubcategoryCode  = new Label();
		label_SubcategoryCode.setStyle("font-size: 12px;");
		label_SubcategoryCode.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
		label_SubcategoryCode.setParent(lc);
		lc.setParent(item);
		creditReviewSubCtgDetails.setSubCategoryDesc(finCreditRevSubCategory.getSubCategoryDesc());
		/***************** Amount ***************/
		lc = new Listcell();
		db_Amount = new Decimalbox();
		db_Amount.setId("db"+finCreditRevSubCategory.getSubCategoryCode());
		db_Amount.setAttribute("data", finCreditRevSubCategory);
		db_Amount.setAttribute("ListBoxdata", listbox);
		db_Amount.setFormat(amtFormat);
		curAmt = curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())!= null ? 
				curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) : BigDecimal.ZERO;
				db_Amount.setValue(curAmt);
				creditReviewSubCtgDetails.setCurYearAuditValue(PennantAppUtil.formatAmount(curAmt, this.currFormatter, false));

				if(!getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory")){
					db_Amount.setReadonly(true);
					db_Amount.setTabindex(-1);
					db_Amount.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");
				}
				ComponentsCtrl.applyForward(db_Amount, "onChange=onChange$auditedValue");
				db_Amount.setParent(lc);
				lc.setParent(item);

				/*************************** Current Year US$ Conversion  ************************/
				lc = new Listcell();
				Label label_currenYrUSConvrsn = new Label(); 
				label_currenYrUSConvrsn.setId("uSD1_"+finCreditRevSubCategory.getSubCategoryCode());
				if(creditReviewDetails.getConversionRate().compareTo(BigDecimal.ZERO) != 0){
					convrsnPrice = curAmt.divide(creditReviewDetails.getConversionRate(), FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
					uSDConvstnVal= getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
					creditReviewSubCtgDetails.setCurYearUSDConvstn(uSDConvstnVal);
					label_currenYrUSConvrsn.setValue(uSDConvstnVal);
				} else {
					convrsnPrice = BigDecimal.ZERO;
					uSDConvstnVal= getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
					label_currenYrUSConvrsn.setValue(uSDConvstnVal);
					creditReviewSubCtgDetails.setCurYearUSDConvstn(uSDConvstnVal);
				}
				label_currenYrUSConvrsn.setStyle("font-size: 12px;");
				label_currenYrUSConvrsn.setParent(lc);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				/*************************** Break Down ************************/
				lc = new Listcell();
				Label label_BreakDown = new Label(); 
				label_BreakDown.setId("rLabel"+finCreditRevSubCategory.getSubCategoryCode());

				BigDecimal curYrbreakDown = (BigDecimal) (curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode())==null?BigDecimal.ZERO:
					curYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode()));
				label_BreakDown.setValue(PennantAppUtil.formatAmount(curYrbreakDown, 2, false));	

				creditReviewSubCtgDetails.setCurYearBreakDown(PennantAppUtil.formatAmount(curYrbreakDown, 2, false));
				label_BreakDown.setStyle("font-size: 12px;");
				label_BreakDown.setParent(lc);

				lc.setStyle("text-align:right;");
				lc.setParent(item);
				if(!isRatio && StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType()).equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)){
					creditReviewSubCtgDetails.setCalC("C");

					label_BreakDown.setStyle("font-weight:bold; font-size: 12px;");
					label_SubcategoryCode.setStyle("font-weight:bold;font-size: 12px;");
					if(finCreditRevSubCategory.isGrand()){
						item.setStyle("background-color: #CCFF99;");
					} else{
						item.setStyle("background-color: #ADD8E6;");
					}
				}
				//Change the Font of the label to Bold if the sub category is calculated and not a ratio category 
				if(StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType()).equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)){
					creditReviewSubCtgDetails.setCalC("C");
					db_Amount.setReadonly(true);
					db_Amount.setDisabled(true);
					db_Amount.setSclass("decimalToString");
					db_Amount.setTabindex(-1);
					db_Amount.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");
				} else {
					db_Amount.setMaxlength(18);
				}
				//Change the Font of the label to Bold if the sub category is calculated and not a ratio category 
				if(!isRatio && StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryItemType()).equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)){
				}

				/*************************** Previous Audit Amount ************************/
				lc = new Listcell();
				Label label_PrevAmt = new Label(); 
				label_PrevAmt.setZclass(null);
				label_PrevAmt.setId("prevAudit"+finCreditRevSubCategory.getSubCategoryCode());
				prvAmt = prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode())!= null ? 
						prvYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) : new BigDecimal(BigInteger.ZERO, this.currFormatter);
						label_PrevAmt.setValue(PennantAppUtil.formatAmount(prvAmt, this.currFormatter,false));
						creditReviewSubCtgDetails.setPreYearAuditValue(PennantAppUtil.formatAmount(prvAmt, this.currFormatter,false));
						label_PrevAmt.setStyle("font-size: 12px;");
						label_PrevAmt.setParent(lc);
						lc.setStyle("text-align:right; border-width: 2; font-size: 2px;");
						lc.setParent(item);

						/*************************** Previous Year US$ Conversion  ************************/

						lc = new Listcell();
						Label label_prevYrUSConvrsn = new Label(); 
						if(creditReviewDetails.getConversionRate().compareTo(BigDecimal.ZERO) != 0){
							convrsnPrice = prvAmt.divide(creditReviewDetails.getConversionRate(), FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
							uSDConvstnVal= getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
							label_prevYrUSConvrsn.setValue(uSDConvstnVal);
							creditReviewSubCtgDetails.setPreYearUSDConvstn(uSDConvstnVal);
						} else {
							convrsnPrice = prvAmt.divide(BigDecimal.ZERO, FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
							uSDConvstnVal= getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice);
							label_prevYrUSConvrsn.setValue(uSDConvstnVal);
							creditReviewSubCtgDetails.setPreYearUSDConvstn(uSDConvstnVal);
						}
						label_prevYrUSConvrsn.setStyle("font-size: 12px;");
						label_prevYrUSConvrsn.setParent(lc);
						lc.setStyle("text-align:right; border-width: 2;");
						lc.setParent(item);

						/*************************** Previous BreakDown ************************/
						lc = new Listcell();
						Label label_PrevBreakDown = new Label(); 
						label_PrevBreakDown.setId("label_PrevBreakDown"+finCreditRevSubCategory.getSubCategoryCode());

						BigDecimal prevYrbreakDown = (BigDecimal) (prvYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode())==null?BigDecimal.ZERO:
							prvYearValuesMap.get(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode()));
						label_PrevBreakDown.setValue(PennantAppUtil.formatAmount(prevYrbreakDown, 2, false));	
						creditReviewSubCtgDetails.setPreYearBreakDown(PennantAppUtil.formatAmount(prevYrbreakDown, 2, false));
						label_PrevBreakDown.setStyle("font-size: 12px;");
						label_PrevBreakDown.setParent(lc);
						lc.setStyle("text-align:right; border-width: 2;");
						lc.setParent(item);

						/*************************** Percentage Change ************************/
						lc = new Listcell();
						Label label_PrecentChange = new Label(); 
						label_PrecentChange.setId("percent"+finCreditRevSubCategory.getSubCategoryCode());

						BigDecimal percentChange = getPercChange(prvAmt, curAmt);
						if(percentChange.compareTo(BigDecimal.ZERO) == 1 ){
							label_PrecentChange.setValue(String.valueOf(percentChange)+"%");
							creditReviewSubCtgDetails.setCurYearPercentage(String.valueOf(percentChange)+"%");
							label_PrecentChange.setStyle("color:green; font-weight:bold; font-size: 12px;");
						}else{ 
							label_PrecentChange.setValue(String.valueOf(percentChange) + " %");	
							creditReviewSubCtgDetails.setCurYearPercentage(String.valueOf(percentChange) + " %");	
							if(percentChange.compareTo(BigDecimal.ZERO) == -1 ){
								label_PrecentChange.setStyle("color:red; font-size: 12px;");
							} else {
								label_PrecentChange.setStyle("color:green; font-weight:bold; font-size: 12px;");
							}
						}

						label_PrecentChange.setParent(lc);
						lc.setStyle("text-align:right;");
						lc.setParent(item);
						if(isNewRecord){
							item.setStyle("background-color: #ffffcc; ");
							item.setAttribute("NewSubCategory", true);
							listbox.insertBefore(item, oldListItem.getNextSibling());
							listbox.removeItemAt(oldListItem.getIndex());
						}
						item.setAttribute("finData", finCreditRevSubCategory);	
						item.setAttribute("finSummData", summaryMap.size()>0 ?summaryMap.get(finCreditRevSubCategory.getSubCategoryCode()):null);
						item.setParent(listbox);
						creditReviewSubCtgDetailsList.add(creditReviewSubCtgDetails);
						logger.debug("Leaving");
	}


	public void onClick$cancelRecord(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		Listitem listItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		doDeleteItem(listItem);
		logger.debug("Leaving" + event.toString());
	}

	private void doDeleteItem(  Listitem listItem){
		Listbox listbox = (Listbox)listItem.getParent();
		listbox.insertBefore(duplicateItem, listItem.getNextSibling());
		listbox.removeItemAt(listItem.getIndex());
		getListItemsDisabled(listbox, false);
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$editRecord(ForwardEvent event){
		logger.debug("Entering" + event.toString());

		Listitem oldItem  = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		duplicateItem = oldItem;
		Listbox listbox = (Listbox) oldItem.getParent();
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) oldItem.getAttribute("finData");
		getListItemsDisabled(listbox, true);
		getNewItem(finCreditRevSubCategory, oldItem, true);

		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$Calculated(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		Checkbox cbIsCalculated = (Checkbox) event.getOrigin().getTarget();
		Textbox tbForFormula_NewRecord = (Textbox)cbIsCalculated.getParent().getPreviousSibling().getChildren().get(0);
		Label labelFormula = (Label) tbForFormula_NewRecord.getNextSibling();
		Button btnForFormula = (Button)cbIsCalculated.getParent().getPreviousSibling().getChildren().get(2);
		if(cbIsCalculated.isChecked()){
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

	public void getSubcategoryCodeValidation(List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory, Textbox tdsubCategoryCode){
		logger.debug("Entering");
		for (FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory) {
			if(tdsubCategoryCode.getValue().trim().equalsIgnoreCase(finCreditRevSubCategory.getSubCategoryCode().trim())){
				throw new WrongValueException(tdsubCategoryCode, Labels.getLabel("FIELD_NO_DUPLICATE",
						new String[] {Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
						Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
			}
		}
		logger.debug("Entering");
	}


	public void onClick$btnForFormula(ForwardEvent event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		Textbox  tb_NewSubCategoryCode = (Textbox)event.getOrigin().getTarget().getParent().getPreviousSibling().getChildren().get(0);
		Textbox  tb_NewSubCategoryDesc = (Textbox)event.getOrigin().getTarget().getParent().getPreviousSibling().getChildren().get(2);
		if(("").equals(StringUtils.trim(tb_NewSubCategoryCode.getValue()))){
			throw new WrongValueException(tb_NewSubCategoryCode, Labels.getLabel("FIELD_NO_EMPTY",
					new String[] {Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
					Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
		} else if(("").equals(StringUtils.trim(tb_NewSubCategoryDesc.getValue()))){
			throw new WrongValueException(tb_NewSubCategoryDesc, Labels.getLabel("FIELD_NO_EMPTY",
					new String[] {Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),
					Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value") }));
		} else {
			final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService().getNewFinCreditRevSubCategory();
			aFinCreditRevSubCategory.setSubCategoryCode(tb_NewSubCategoryCode.getValue());
			aFinCreditRevSubCategory.setSubCategoryDesc(tb_NewSubCategoryDesc.getValue());
			addDetails(aFinCreditRevSubCategory);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onAddListitem(ForwardEvent event) throws Exception{
		logger.debug("Entering" + event.toString());

		Listitem listitemOld =  (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Listbox listboxOld = (Listbox)listitemOld.getParent();

		Listitem newListItem = new Listitem();
		Listcell lc ;


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

	/**
	 * This method for set the data according to the formulae.<br>
	 * @param listItem
	 * @throws Exception 
	 */
	public void setData(Map<String,BigDecimal> dataMap) throws Exception{
		logger.debug("Entering");

		engine.put("EXCHANGE", this.conversionRate.getValue());
		engine.put("NoOfShares", this.noOfShares.getValue());
		engine.put("MARKETPRICE", this.marketPrice.getActualValue());	

		String year="";

		if(dataMap.get("auditYear") != null){
			year = String.valueOf(dataMap.get("auditYear"));
		}
		if(this.auditedYear.getValue().equals(year)){
			engine.put("Y"+this.auditedYear.getValue()+"DIVCOUNT", BigDecimal.ONE);	
		} else {
			engine.put("Y"+year+"DIVCOUNT", new BigDecimal(2));	
		}

		//total calculation	
		for(int i=0;i<listOfFinCreditRevSubCategory.size();i++){
			FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
			if(finCreditRevSubCategory.getSubCategoryItemType().equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD) &&
					StringUtils.isNotEmpty(finCreditRevSubCategory.getItemsToCal())){		
				BigDecimal value = BigDecimal.ZERO;
				try{
					if("NaN".equals(engine.eval(replaceYear(finCreditRevSubCategory.getItemsToCal(), year)).toString()) ||
							(engine.eval(replaceYear(finCreditRevSubCategory.getItemsToCal(), year)).toString().contains("Infinity"))){
						value = BigDecimal.ZERO;
					}else{
						value =new BigDecimal(engine.eval(replaceYear(finCreditRevSubCategory.getItemsToCal(), year)).toString()).setScale(48,RoundingMode.HALF_DOWN);
					} 
				} catch (Exception e) {
					logger.error("Exception: ", e);
					value =BigDecimal.ZERO;
				}
				dataMap.put(finCreditRevSubCategory.getSubCategoryCode(),value==null?BigDecimal.ZERO :value);
				engine.put("Y"+year+finCreditRevSubCategory.getSubCategoryCode(),value==null?BigDecimal.ZERO:value);
			}
		} 

		//ratio calculation
		for(int i=0;i<listOfFinCreditRevSubCategory.size();i++){
			FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
			if(StringUtils.isNotEmpty(finCreditRevSubCategory.getItemRule())){		
				BigDecimal value = BigDecimal.ZERO;
				try{
					if("NaN".equals(engine.eval(replaceYear(finCreditRevSubCategory.getItemRule(), year)).toString()) ||
							(engine.eval(replaceYear(finCreditRevSubCategory.getItemRule(), year)).toString().contains("Infinity"))){
						value = BigDecimal.ZERO;
					}else{
						if(finCreditRevSubCategory.getRemarks().equals(FacilityConstants.CREDITREVIEW_REMARKS)){
							value =new BigDecimal(engine.eval(replaceYear(finCreditRevSubCategory.getItemRule(), year)).toString()).setScale(48,RoundingMode.HALF_DOWN);
						} else {
							value =new BigDecimal(engine.eval(replaceYear(finCreditRevSubCategory.getItemRule(), year)).toString()).setScale(48,RoundingMode.HALF_DOWN);
						}
					} 
				}catch (Exception e) {
					value = BigDecimal.ZERO;
					logger.error("Exception: ", e);
				}
				if(finCreditRevSubCategory.getCategoryId() == 4 || finCreditRevSubCategory.getCategoryId() == 7){
					dataMap.put(finCreditRevSubCategory.getSubCategoryCode(),value);
					engine.put("Y"+year+finCreditRevSubCategory.getSubCategoryCode(),value==null?BigDecimal.ZERO:value);
				}else{
					dataMap.put(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode(),value==null?BigDecimal.ZERO:value);
					curYearValuesMap.put(FacilityConstants.CREDITREVIEW_REMARKS+finCreditRevSubCategory.getSubCategoryCode(),value==null?BigDecimal.ZERO:value);
				}
			}
		}
	}


	public String replaceYear(String formula,String year){
		String formatedFormula= formula;
		if(StringUtils.isNotEmpty(year)){
			formatedFormula = formatedFormula.replace("YN-1"+".", "Y"+(Integer.parseInt(year)-1));
			formatedFormula = formatedFormula.replace("YN"+".", "Y"+year);
		} 
		return formatedFormula;
	}

	/**
	 * This Method/Event is for calculating values
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$auditedValue(ForwardEvent event) throws Exception {
		logger.debug("Entering"+event.toString());

		Listbox listbox	  = (Listbox) event.getOrigin().getTarget().getAttribute("ListBoxdata");
		FinCreditRevSubCategory aFinCreditRevSubCategory  = (FinCreditRevSubCategory) event.getOrigin().getTarget().getAttribute("data");
		String subCategory = aFinCreditRevSubCategory.getSubCategoryCode();
		Listitem listItem = (Listitem) listbox.getFellowIfAny("li"+subCategory);

		((Tabbox)listbox.getParent().getParent().getParent().getParent()).getSelectedTab().setSelected(true);

		Decimalbox db_Amount = (Decimalbox) listItem.getFellowIfAny("db"+aFinCreditRevSubCategory.getSubCategoryCode());
		curYearValuesMap.put(subCategory,
				db_Amount.getValue() == null? BigDecimal.ZERO: db_Amount.getValue());
		curYearValuesMap.put("auditYear", new BigDecimal(Integer.parseInt(creditReviewDetails.getAuditYear())));
		engine.put("Y"+this.auditedYear.getValue()+subCategory, 
				db_Amount.getValue() == null? BigDecimal.ZERO: db_Amount.getValue());

		engine.put("Y"+this.auditedYear.getValue()+FacilityConstants.CREDITREVIEW_REMARKS+aFinCreditRevSubCategory.getSubCategoryCode(),
				db_Amount.getValue() == null? BigDecimal.ZERO: db_Amount.getValue());

		setData(curYearValuesMap);
		getListboxDetails(listbox);
		getTotalLiabAndAssetsDifference();
		logger.debug("Leaving"+event.toString());
	}

	public BigDecimal getPercChange(BigDecimal previousVal, BigDecimal currentVal){
		if(previousVal.compareTo(BigDecimal.ZERO) != 0 ){
			return currentVal.subtract(previousVal).multiply(new BigDecimal(100)).divide(previousVal, 2, RoundingMode.HALF_DOWN);
		} else {
			return new BigDecimal(BigInteger.ZERO, 2);
		}
	}

	public String getBreakDownAmt(BigDecimal assetValue, BigDecimal totAssetValue){
		if(totAssetValue.compareTo(BigDecimal.ZERO) != 0){
			return assetValue.multiply(new BigDecimal(100)).divide(totAssetValue).toString();
		} else {
			return BigDecimal.ZERO.toString();
		}
	}


	/**
	 * This Method/Event is change the conversion Rate
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$conversionRate(ForwardEvent event) throws Exception {
		logger.debug(event+"Entering");
        doClearMessage();
		
		creditReviewSubCtgDetailsList.clear();
		if(this.conversionRate.getValue() == null || this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0){
			throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
					new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value")}));
		}
		creditReviewDetails.setConversionRate(this.conversionRate.getValue());
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		setData(curYearValuesMap);
		setTabs();
		logger.debug(event+"Leaving");
	}

	public void refreshTabs() throws Exception{

		creditReviewSubCtgDetailsList.clear();
		if(this.conversionRate.getValue().compareTo(BigDecimal.ZERO) == 0){
			throw new WrongValueException(this.conversionRate, Labels.getLabel("FIELD_NO_NEGATIVE",
					new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value")}));
		}
		creditReviewDetails.setConversionRate(this.conversionRate.getValue());
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		setData(curYearValuesMap);
		setTabs();
	}

	public void onClick$btnPrint(Event event) throws InterruptedException{
		logger.debug(event+"Entering");

		CreditReviewMainCtgDetails creditReviewMainCtgDetails = new CreditReviewMainCtgDetails();

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if(this.custCIF.getValue() != null){
				creditReviewMainCtgDetails.setCustomerId(String.valueOf(this.custCIF.getValue())+"_"+String.valueOf(this.custShrtName.getValue()));
			} else {
				throw new WrongValueException( this.custID, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.bankName.isVisible()) {
				creditReviewMainCtgDetails.setAllowBankName("TRUE");
				if(this.bankName.getValue() != null && StringUtils.isNotEmpty(this.bankName.getValue())){
					creditReviewMainCtgDetails.setBankName(this.bankName.getValue());
				} else {
					throw new WrongValueException(this.bankName, Labels.getLabel( "FIELD_NO_EMPTY",
							new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value"),
							Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.auditors.getValue() != null && StringUtils.isNotEmpty(this.auditors.getValue())){
				creditReviewMainCtgDetails.setAuditors(this.auditors.getValue());
			} else {
				throw new WrongValueException( this.auditors, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.location.getValue() != null && StringUtils.isNotEmpty(this.location.getValue())){
				creditReviewMainCtgDetails.setLocation(this.location.getValue());
			} else {
				throw new WrongValueException( this.location, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_Location.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_Location.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.auditedDate.getValue() != null){
				creditReviewMainCtgDetails.setAuditedDate(DateUtility.formatToShortDate(this.auditedDate.getValue()));
			} else {
				throw new WrongValueException( this.auditedDate, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.conversionRate.getValue() != null){
				creditReviewMainCtgDetails.setConversionRate(PennantAppUtil.formatAmount(this.conversionRate.getValue(), this.currFormatter, false));
			} else {
				throw new WrongValueException( this.conversionRate, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.auditedYear.getValue() != null){
				creditReviewMainCtgDetails.setAuditYear(this.auditedYear.getValue());
			} else {
				throw new WrongValueException( this.auditedYear, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.noOfShares.getValue() != null){
				creditReviewMainCtgDetails.setNoOfShares(String.valueOf(this.noOfShares.getValue()));
			} else {
				throw new WrongValueException( this.noOfShares, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_NoOfShares.value"),
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
			if(this.auditPeriod.getValue() != null){
				creditReviewMainCtgDetails.setAuditPeriod(String.valueOf(this.auditPeriod.getValue()));
			} else {
				throw new WrongValueException( this.auditPeriod, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value"),
						Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		creditReviewMainCtgDetails.setConsolOrUnConsol(this.conSolOrUnConsol.getSelectedItem().getValue().
				toString().equals(FacilityConstants.CREDITREVIEW_CONSOLIDATED) ? "True" : "False");

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		} else {
			List<Object> list = new ArrayList<Object>();
			String qualUnQual=this.qualifiedUnQualified.getSelectedItem().getValue().toString();
			String audQual = Labels.getLabel("UnAudUnQual_Months", new String[] { this.auditType.getSelectedItem().getValue().toString(), qualUnQual, String.valueOf(getCreditReviewDetails().getAuditPeriod()) });

			for (CreditReviewSubCtgDetails ctgDetails : creditReviewSubCtgDetailsList) {
				if (StringUtils.isNotEmpty(ctgDetails.getSubCategoryDesc())) {
					continue;
				}
				ctgDetails.setCurYearAuditValueHeader(audQual);
				ctgDetails.setCurrencyConvertion(AccountConstants.CURRENCY_USD);
			}

			list.add(creditReviewSubCtgDetailsList);

			ReportGenerationUtil.generateReport("CreditApplication_Review", creditReviewMainCtgDetails, list, true, 1,
					getUserWorkspace().getLoggedInUser().getFullName(), this.window_CreditApplicationReviewDialog);
		}

		logger.debug("Leaving");
	}

	/*
	 *  Double Clicked Event For SubCategory Item
	 */
	public void onSubCategoryItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected Academic object
		final Listitem item = (Listitem)event.getOrigin().getTarget();
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) item.getAttribute("finData");
		addDetails(aFinCreditRevSubCategory);
		logger.debug("Leaving" + event.toString());
	}


	/*
	 *  onSelect Event For Audit Type Combo box
	 */
	public void onSelect$auditType(Event event){
		logger.debug("Entering" + event.toString());
		setLables();
		logger.debug("Leaving" + event.toString());
	}
	public void onCheck$qualifiedUnQualified(Event event){
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
		filterList.add(new Filter("lovDescCustCtgType", new String[]{PennantConstants.PFF_CUSTCTG_CORP, 
				PennantConstants.PFF_CUSTCTG_SME}, Filter.OP_IN));
		return filterList;
	}

	public void setFinCreditRevSubCategoryService(
			FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
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

	public void setCreditReviewSummaryData(
			CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	private void setLables() {
		String qualUnQual=this.qualifiedUnQualified.getSelectedItem().getValue().toString();
		String audQual = Labels.getLabel("UnAudUnQual_Months", new String[] {"#".equals( this.auditType.getSelectedItem().getValue().toString()) ? 
				FacilityConstants.CREDITREVIEW_AUDITED : this.auditType.getSelectedItem().getValue().toString(), qualUnQual});

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

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	/*
	 * Method for USD Conversion	
	 */
	public String getUsdConVersionValue(FinCreditRevSubCategory finCreditRevSubCategory, BigDecimal convrsnPrice){
		if(FacilityConstants.CREDITREVIEW_REMARKS.equals(finCreditRevSubCategory.getRemarks())){
			if(FacilityConstants.CORP_CRDTRVW_RATIOS_WRKCAP.equals(finCreditRevSubCategory.getSubCategoryCode()) ||
					FacilityConstants.CORP_CRDTRVW_RATIOS_EBITDA4.equals(finCreditRevSubCategory.getSubCategoryCode()) ||
					FacilityConstants.CORP_CRDTRVW_RATIOS_FCF.equals(finCreditRevSubCategory.getSubCategoryCode())){
				return PennantAppUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false);
			} else {
				return "";
			}
		} else {
			return PennantAppUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false);
		}
	}

}
