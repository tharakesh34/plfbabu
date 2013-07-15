package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class CreditApplicationReviewDialogCtrl extends GFCBaseListCtrl<FinCreditReviewSummary> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(CreditApplicationReviewDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_CreditApplicationReviewDialog; // autowired

	protected Borderlayout		borderlayout_CreditApplicationReview;
	protected Grid 				creditApplicationReviewGrid;
	protected Longbox 	 		custID; 						   	// autowired
	protected Textbox 			bankName;
	protected Textbox 			auditors;
	protected Checkbox 			consolOrUnConsol;
	protected Textbox 			location;
	protected Textbox 			auditedYear;
	protected Datebox 			auditedDate;
	protected Decimalbox 		conversionRate;
	protected Textbox 	 		custCIF;							// autowired
	protected Label 	 		custShrtName;						// autowired
	protected Intbox 	 		noOfShares;							// autowired
	protected Decimalbox 	 	marketPrice;						// autowired


	protected Groupbox 			gb_CreditReviwDetails;
	protected Tabbox 			tabBoxIndexCenter;
	protected Tabs 				tabsIndexCenter;
	protected Tabpanels 		tabpanelsBoxIndexCenter;
	protected Button 			btnCopyTo;					// autowired

	protected Label 			recordStatus; 				// autowired
	protected Radiogroup 		userAction;
	protected Groupbox 			groupboxWf;
	
	protected Grid 				grid_Basicdetails;			// autoWired

	//protected Listbox 		listBoxFinCreditReviewSummary;

	protected Button btnSearchPRCustid; // autowire

	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;


	// not auto wired vars
	private FinCreditReviewDetails creditReviewDetails; // overhanded per param
	private transient CreditApplicationReviewListCtrl creditApplicationReviewListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient long 		oldVar_custId;
	//private transient String 	oldVar_lovDescCustCIF;
	//private transient String 	oldVar_lovDescCustCtgCode;
	//private transient String 	oldVar_lovDescCustShrtName;
	private transient String 	oldVar_bankName;
	private transient String 	oldVar_auditedYear;
	private transient String 	oldVar_auditors;
	private transient String 	oldVar_location;
	private transient boolean 	oldVar_consolOrUnConsol;
	private transient Date 		oldVar_auditedDate;
	private transient BigDecimal oldVar_conversionRate;
	private transient int 		 oldVar_noOfShares;
	private transient BigDecimal oldVar_marketPrice;

	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CreditApplicationReviewDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	//protected Button button_FinCreditReviewSummaryList_NewFinCreditReviewSummary; // autowired
	protected Button btnSearchAccountSetCode; // autowire

	// ServiceDAOs / Domain Classes
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<FinCreditReviewSummary> creditReviewSummaryList = new ArrayList<FinCreditReviewSummary>();
	private List<FinCreditReviewSummary> oldVar_creditReviewSummaryList = new ArrayList<FinCreditReviewSummary>();
	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	private Map<String,BigDecimal> itemsValueMap = new HashMap<String,BigDecimal>();
	private Map<String,FinCreditReviewSummary> summaryMap = new HashMap<String,FinCreditReviewSummary>();
	private String creditRevCode;
	int listRows;
	// create a script engine manager
	ScriptEngineManager factory = new ScriptEngineManager();

	// create a JavaScript engine
	ScriptEngine engine = factory.getEngineByName("JavaScript");
	int currFormatter;
	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinCreditReviewDetails object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CreditApplicationReviewDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		//getting currency formatter

		currFormatter = this.creditApplicationReviewService.getCurrencyById(SystemParameterDetails.
				getSystemParameterObject("APP_DFT_CURR").getSysParmValue()).getCcyEditField();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit,this.btnDelete, this.btnSave, this.btnCancel, 
				this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("creditReviewDetails")) {
			this.creditReviewDetails = (FinCreditReviewDetails) args.get("creditReviewDetails");
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
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CreditApplicationReviewDialog");
		}

		// READ OVERHANDED params !
		// we get the creditApplicationReviewListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete creditReviewDetails here.
		if (args.containsKey("creditApplicationReviewListCtrl")) {
			setCreditApplicationReviewListCtrl((CreditApplicationReviewListCtrl) args.get("creditApplicationReviewListCtrl"));
		} else {
			setCreditApplicationReviewListCtrl(null);
		}

		getBorderLayoutHeight();
		doSetFieldProperties();
		doShowDialog(getCreditReviewDetails());

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
		this.auditedDate.setFormat(PennantConstants.dateFormat);
		
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

		getUserWorkspace().alocateAuthorities("CreditApplicationReviewDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnCopyTo"));

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CreditApplicationReviewDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_CreditApplicationReviewDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_CreditApplicationReviewDialog, "FinCreditReviewDetails");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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
	public void doWriteBeanToComponents(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
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
		this.conversionRate.setValue(aCreditReviewDetails.getConversionRate());
		this.consolOrUnConsol.setChecked(aCreditReviewDetails.isConsolOrUnConsol());
		this.auditors.setValue(aCreditReviewDetails.getAuditors());
		this.location.setValue(aCreditReviewDetails.getLocation());	
		this.noOfShares.setValue(aCreditReviewDetails.getNoOfShares());
		this.marketPrice.setValue(aCreditReviewDetails.getMarketPrice());

		this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(creditRevCode);
		if(!this.creditReviewDetails.isNewRecord()){
			for(int k=0;k<this.creditReviewSummaryList.size(); k++){
				itemsValueMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));
				summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),this.creditReviewSummaryList.get(k));
				engine.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),PennantAppUtil.formateAmount(this.creditReviewSummaryList.get(k).getItemValue(),this.currFormatter));

			}
			setData();
			setTabs();
		}

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
		try {
			aCreditReviewDetails.setAuditYear(this.auditedYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
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
			aCreditReviewDetails.setConsolOrUnConsol(this.consolOrUnConsol.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCreditReviewDetails.setConversionRate((BigDecimal)this.conversionRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setMarketPrice((BigDecimal)this.marketPrice.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCreditReviewDetails.setNoOfShares(this.noOfShares.getValue());
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinCreditReviewDetails aCreditReviewDetails) throws InterruptedException {
		logger.debug("Entering");

		// if aCreditReviewDetails == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCreditReviewDetails == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCreditReviewDetails = getCreditApplicationReviewService().getNewCreditReviewDetails();
			setCreditReviewDetails(aCreditReviewDetails);
		} else {
			setCreditReviewDetails(aCreditReviewDetails);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCreditReviewDetails.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.btnSearchPRCustid.focus();
		} else {
			this.btnSearchPRCustid.focus();
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CreditApplicationReviewDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}




	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custId = this.custID.getValue();
		//this.oldVar_lovDescCustCIF = this.custCIF.getValue();
		//this.oldVar_lovDescCustShrtName = this.custShrtName.getValue();
		this.oldVar_auditedDate = this.auditedDate.getValue();
		this.oldVar_auditedYear = this.auditedYear.getValue();
		this.oldVar_auditors = this.auditors.getValue();
		this.oldVar_conversionRate = this.conversionRate.getValue();
		this.oldVar_consolOrUnConsol = this.consolOrUnConsol.isChecked();
		this.oldVar_location = this.location.getValue();
		this.oldVar_noOfShares = this.noOfShares.getValue();
		this.oldVar_marketPrice = this.marketPrice.getValue();
		this.oldVar_bankName = this.bankName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();		
		this.oldVar_creditReviewSummaryList = this.creditReviewSummaryList;
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custId);
		//this.custCIF.setValue(this.oldVar_lovDescCustCIF);
		//this.custShrtName.setValue(this.oldVar_lovDescCustShrtName);
		this.auditedDate.setValue(this.oldVar_auditedDate);
		this.auditedYear.setValue(this.oldVar_auditedYear);
		this.auditors.setValue(this.oldVar_auditors);
		this.conversionRate.setValue(this.oldVar_conversionRate);
		this.location.setValue(this.oldVar_location);
		this.bankName.setValue(this.oldVar_bankName);
		this.consolOrUnConsol.setChecked(this.oldVar_consolOrUnConsol);
		this.marketPrice.setValue(this.oldVar_marketPrice);
		this.noOfShares.setValue(this.oldVar_noOfShares);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.creditReviewSummaryList = this.oldVar_creditReviewSummaryList;

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_custId != this.custID.getValue()) {
			return true;
		}
		if (this.oldVar_auditedDate != this.auditedDate.getValue()) {
			return true;
		}
		if (this.oldVar_auditedYear != this.auditedYear.getValue()) {
			return true;
		}
		if (this.oldVar_auditors != this.auditors.getValue()) {
			return true;
		}
		if (this.oldVar_conversionRate != this.conversionRate.getValue()) {
			return true;
		}
		if (this.oldVar_bankName != this.bankName.getValue()) {
			return true;
		}
		if (this.oldVar_location != this.location.getValue()) {
			return true;
		}
		if (this.oldVar_consolOrUnConsol != this.consolOrUnConsol.isChecked()) {
			return true;
		}
		if(this.oldVar_creditReviewSummaryList != this.creditReviewSummaryList){
			return true;
		}
		if(this.oldVar_marketPrice != this.marketPrice.getValue()){
			return true;
		}
		if(this.oldVar_noOfShares != this.noOfShares.getValue()){
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.location.isReadonly()) {
			this.location.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_Location.value") }));
		}
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value") }));
		}
		if (!this.conversionRate.isReadonly()) {
			this.conversionRate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_ConversionRate.value") }));
		}
		if (!this.auditedYear.isReadonly()) {
			this.auditedYear.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value") }));
		}
		if (!this.auditors.isReadonly()) {
			this.auditors.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value") }));
		}
		if (!this.auditedDate.isReadonly()) {
			this.auditedDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditedDate.value") }));
		}
		if (!this.marketPrice.isReadonly()) {
			this.marketPrice.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_MarketPrice.value") }));
		}
		if (!this.noOfShares.isReadonly()) {
			this.noOfShares.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_NoOfShares.value") }));
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
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustCIF.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Clear the Error Messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.location.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.conversionRate.setErrorMessage("");
		this.auditedYear.setErrorMessage("");
		this.auditors.setErrorMessage("");
		this.auditedDate.setErrorMessage("");
		this.noOfShares.setErrorMessage("");
		this.marketPrice.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for refreshing the list in ListCtrl
	 */
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<FinCreditReviewDetails> soCreditReviewDetails = getCreditApplicationReviewListCtrl().getSearchObj();
		getCreditApplicationReviewListCtrl().pagingCreditApplicationReviewList.setActivePage(0);
		getCreditApplicationReviewListCtrl().getPagedListWrapper().setSearchObject(soCreditReviewDetails);
		if (getCreditApplicationReviewListCtrl().listBoxCreditApplicationReview != null) {
			getCreditApplicationReviewListCtrl().listBoxCreditApplicationReview.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCreditReviewDetails.getRecordType()).equals("")) {
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
					closeDialog(this.window_CreditApplicationReviewDialog, "FinCreditReviewDetails");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinCreditReviewDetails object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();

		final FinCreditReviewDetails aCreditReviewDetails = getCreditApplicationReviewService().getNewCreditReviewDetails();
		setCreditReviewDetails(aCreditReviewDetails);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.btnSearchPRCustid.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCreditReviewDetails().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnSearchPRCustid.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setDisabled(true);
		}
		this.location.setReadonly(isReadOnly("CreditApplicationReviewDialog_location"));
		this.bankName.setReadonly(isReadOnly("CreditApplicationReviewDialog_bankName"));
		this.conversionRate.setReadonly(isReadOnly("CreditApplicationReviewDialog_conversionRate"));
		this.auditedYear.setReadonly(isReadOnly("CreditApplicationReviewDialog_auditedYear"));
		this.auditors.setReadonly(isReadOnly("CreditApplicationReviewDialog_auditors"));
		this.auditedDate.setReadonly(isReadOnly("CreditApplicationReviewDialog_auditedDate"));
		//this.btnSearchPRCustid.setDisabled(isReadOnly("CreditApplicationReviewDialog_custID"));
		this.marketPrice.setReadonly(isReadOnly("CreditApplicationReviewDialog_marketPrice"));
		this.noOfShares.setReadonly(isReadOnly("CreditApplicationReviewDialog_noOfShares"));
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
		this.btnSearchPRCustid.setDisabled(true);
		this.bankName.setReadonly(true);
		this.location.setReadonly(true);
		this.auditedDate.setReadonly(true);
		this.auditedYear.setReadonly(true);
		this.auditors.setReadonly(true);
		this.consolOrUnConsol.setDisabled(true);
		this.conversionRate.setReadonly(true);
		this.marketPrice.setReadonly(true);
		this.noOfShares.setReadonly(true);

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
		this.consolOrUnConsol.setChecked(false);
		this.conversionRate.setValue("0");
		this.noOfShares.setText("0");
		this.marketPrice.setText("0");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(getCreditReviewDetails(), aCreditReviewDetails);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinCreditReviewDetails object with the components data
		doWriteComponentsToBean(aCreditReviewDetails);
		aCreditReviewDetails.setCreditReviewSummaryEntries(getFinCreditReviewSummaryListbox());
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCreditReviewDetails.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCreditReviewDetails.getRecordType()).equals("")) {
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
				// do Close the Dialog window
				closeDialog(this.window_CreditApplicationReviewDialog, "FinCreditReviewDetails");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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

		aCreditReviewDetails.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCreditReviewDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCreditReviewDetails.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCreditReviewDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCreditReviewDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCreditReviewDetails);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, 
						aCreditReviewDetails))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCreditReviewDetails.setTaskId(taskId);
			aCreditReviewDetails.setNextTaskId(nextTaskId);
			aCreditReviewDetails.setRoleCode(getRole());
			aCreditReviewDetails.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCreditReviewDetails, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCreditReviewDetails);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
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
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CreditApplicationReviewDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("FinCreditReviewDetails");
		notes.setReference(String.valueOf(getCreditReviewDetails().getDetailId()));
		notes.setVersion(getCreditReviewDetails().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setFinCreditReviewSummaryList(List<FinCreditReviewSummary> creditReviewSummaryList) {
		this.creditReviewSummaryList = creditReviewSummaryList;
	}
	/**
	 * This method to read the list items and then add to the list.<br>
	 * @return
	 */
	public List<FinCreditReviewSummary> getFinCreditReviewSummaryListbox() {
		logger.debug("Entering");

		List<FinCreditReviewSummary> listOfCreditReviewSummary = new ArrayList<FinCreditReviewSummary>();
		List<Component> listOfTabPanels = this.tabpanelsBoxIndexCenter.getChildren();

		for(int k=0;k<listOfTabPanels.size();k++){
			Listbox listBox= (Listbox) listOfTabPanels.get(k).getFirstChild().getFirstChild();
			List<Listitem> listItems = listBox.getItems();

			for(int i =0;i<listItems.size();i++){
				Listitem listItem = (Listitem)listItems.get(i);
				if(!(listItem instanceof Listgroup)){

					FinCreditReviewSummary creditReviewSummary = 
						((FinCreditReviewSummary)(listItem).getAttribute("finSummData"))!=null?
								((FinCreditReviewSummary)(listItem).getAttribute("finSummData")):new FinCreditReviewSummary();
								if(((FinCreditReviewSummary)(listItem).getAttribute("finSummData"))==null){
									creditReviewSummary.setSubCategoryCode((listItem).getId().substring(2));
									creditReviewSummary.setRecordType(String.valueOf(listItem.getAttribute("recordType")));
								}
								creditReviewSummary.setItemValue(this.itemsValueMap.get(creditReviewSummary.getSubCategoryCode())==null?BigDecimal.ZERO:
									PennantAppUtil.unFormateAmount(this.itemsValueMap.get(creditReviewSummary.getSubCategoryCode()),this.currFormatter));
								//creditReviewSummary.setNewRecord(this.creditReviewDetails.isNewRecord());
								listOfCreditReviewSummary.add(creditReviewSummary);
				}
			}
		}

		logger.debug("Leaving");
		return listOfCreditReviewSummary;
	}



	/**
	 * This method for setting the tabs according to the sheets we define.
	 * @throws Exception
	 */
	public void setTabs() throws Exception{
		logger.debug("Entering");
		for(FinCreditRevCategory fcrc:listOfFinCreditRevCategory){
			Tab tab = new Tab();
			tab.setId("tab_"+fcrc.getCategoryId());
			tab.setLabel(fcrc.getCategoryDesc());
			tab.setParent(this.tabsIndexCenter);
			Tabpanel tabPanel = new Tabpanel();	
			tabPanel.setHeight(this.borderLayoutHeight
					- 285 + "px");// 425px
			tabPanel.setId("tabPanel_"+fcrc.getCategoryId());
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			render(fcrc,setListToTab("tabPanel_"+fcrc.getCategoryId(),tabPanel,fcrc));
		}
		logger.debug("Leaving");
	}


	/**
	 * This method for building the listbox with dynamic headers.<br>
	 * 
	 */	
	public Listbox setListToTab(String tabId,Tabpanel tabPanel,FinCreditRevCategory fcrc){
		logger.debug("Entering");

		Div div = new Div();
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 285 + "px");
		Listbox listbox = new Listbox();
		listbox.setVflex(true);
		listbox.setSpan(true);
		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 285 + "px");

		div.setId("div_"+fcrc.getCategoryId());
		listbox.setId("lb_"+fcrc.getCategoryId());

		Listhead listHead = new Listhead();
		listHead.setId("listHead_"+fcrc.getCategoryId());
		//listHead.setStyle("background:#447294;");
		Listheader listheader_bankName = new Listheader();
		listheader_bankName.setLabel(Labels.getLabel("listheader_bankName.value",new String[]{this.bankName.getValue()}));
		//listheader_bankName.setStyle("color:white;");
		listheader_bankName.setParent(listHead);
		Listheader listheader_audAmt = new Listheader();
		listheader_audAmt.setLabel(Labels.getLabel("listheader_audAmt1.value",new String[]{String.valueOf(this.auditedYear.getValue()==null?"":this.auditedYear.getValue())}));
		//listheader_audAmt.setStyle("color:white;");
		listheader_audAmt.setParent(listHead);		

		Listheader listheader_breakDown= new Listheader();
		listheader_breakDown.setLabel(Labels.getLabel("listheader_breakDown1.value",new String[]{String.valueOf(this.auditedYear.getValue()==null?"":this.auditedYear.getValue())}));
		listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
		//listheader_breakDown.setStyle("color:white;");
		listheader_breakDown.setParent(listHead);
		listHead.setParent(listbox);
		listbox.setParent(div);
		listbox.setAttribute("isRatio",fcrc.getRemarks());
		div.setParent(tabPanel);
		logger.debug("Leaving");
		return listbox;

	}


	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
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

		List<Filter> filtersList=new ArrayList<Filter>();
		Filter filter=new Filter("lovDescCustCtgType", "C", Filter.OP_EQUAL);
		filtersList.add(filter);

		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("filtersList", filtersList);
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

		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.creditRevCode);
		setTabs();
		logger.debug("Leaving");
	}



	/**
	 * This Method for rendering 
	 * @param categoryId
	 * @param listbox
	 * @throws Exception
	 */
	public void render(FinCreditRevCategory fcrc,Listbox listbox) throws Exception {
		logger.debug("Entering");

		Listitem item = null;
		String recordType=null;
		String mainCategory = "";
		Listgroup lg= null;
		String amtFormat=PennantAppUtil.getAmountFormate(currFormatter);
		Listcell lc = null;
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory= 
			this.creditApplicationReviewService.getFinCreditRevSubCategoryByCategoryId(fcrc.getCategoryId());
		listbox.setAttribute("fcrc", fcrc);
		for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){
			FinCreditRevSubCategory finCreditRevSubCategory = null;
			finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(i);	
			if(listbox.getAttribute("isRatio").equals("R") && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())){
				mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
				lg =  new Listgroup();
				lg.setId(mainCategory);
				if(!listbox.hasFellow(mainCategory)){
					lg.setLabel(mainCategory);
					lg.setOpen(true);
					lg.setStyle("font-weight:bold;font-weight:bold;background-color: #ADD8E6;");
					lg.setParent(listbox);
				}
			}
			if(!listbox.getAttribute("isRatio").equals("R") && this.creditReviewDetails.isNew()){
				//itemsValueMap.put(finCreditRevSubCategory.getSubCategoryCode(),new BigDecimal(0));
				if(!itemsValueMap.containsKey(finCreditRevSubCategory.getSubCategoryCode())){
					engine.put(finCreditRevSubCategory.getSubCategoryCode(),BigDecimal.ZERO);
				}
			}
			item = new Listitem();
			item.setId(String.valueOf("li"+finCreditRevSubCategory.getSubCategoryCode()));			
			lc = new Listcell();
			//lc.setSclass("defListcell");
			Label label1  = new Label();
			label1.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			if(!listbox.getAttribute("isRatio").equals("R") && finCreditRevSubCategory.getSubCategoryItemType().equals("Calc")){
				label1.setStyle("font-weight:bold;");
			}
			label1.setParent(lc);
			lc.setParent(item);

			lc = new Listcell();
			//lc.setSclass("defListcell");
			Decimalbox decimalbox = null;
			decimalbox = new Decimalbox();
			if(listbox.getAttribute("isRatio").equals("R") || finCreditRevSubCategory.getSubCategoryItemType().equals("Calc")){
				decimalbox.setReadonly(true);
				decimalbox.setStyle("font-weight:bold;background: none repeat scroll 0 0 #FFFFFF;border-width: 0;");
			}
			decimalbox.setId("db"+finCreditRevSubCategory.getSubCategoryCode());
			decimalbox.setAttribute("data", finCreditRevSubCategory);
			decimalbox.setAttribute("ListBoxdata", listbox);
			decimalbox.setFormat(amtFormat);
			if(listbox.getAttribute("isRatio").equals("R")){
				decimalbox.setValue(itemsValueMap.get(finCreditRevSubCategory.getSubCategoryCode()));

			}else{
				decimalbox.setValue(
						itemsValueMap.get(finCreditRevSubCategory.getSubCategoryCode()));
			}
			FinCreditReviewSummary finCreditReviewSummary= summaryMap.get(finCreditRevSubCategory.getSubCategoryCode());
			if(finCreditReviewSummary == null){
				recordType =PennantConstants.RCD_ADD;

			}else{
				if(finCreditReviewSummary.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					finCreditReviewSummary.setNewRecord(false);
				}else if(StringUtils.trimToEmpty(finCreditReviewSummary.getRecordType()).equals("")){
					finCreditReviewSummary.setRecordType(PennantConstants.RECORD_TYPE_UPD); 
					finCreditReviewSummary.setNewRecord(true);
				}
				//recordType = listOfCreditReviewSummary.get(listItemsSeq-1).getRecordType();
			}
			ComponentsCtrl.applyForward(decimalbox, "onChange=onChange$auditedValue");
			decimalbox.setParent(lc);
			lc.setParent(item);

			lc = new Listcell();
			//lc.setSclass("defListcell");
			Label rLabel = new Label(); 
			rLabel.setId("rLabel"+finCreditRevSubCategory.getSubCategoryCode());
			rLabel.setValue(String.valueOf(itemsValueMap.get("R"+finCreditRevSubCategory.getSubCategoryCode())==null?"--":itemsValueMap.get("R"+finCreditRevSubCategory.getSubCategoryCode())));
			/*if(!finCreditRevSubCategory.isIsCreditCCY() && finCreditRevSubCategory.getSubCategoryItemType().equals("Calc")){
				rLabel.setValue("100");
			}*/
			if(!listbox.getAttribute("isRatio").equals("R") && finCreditRevSubCategory.getSubCategoryItemType().equals("Calc")){
				rLabel.setStyle("font-weight:bold;");
			}
			rLabel.setParent(lc);
			lc.setParent(item);
			item.setAttribute("recordType", recordType);
			item.setAttribute("finData", finCreditRevSubCategory);			
			item.setAttribute("finSummData", summaryMap.size()>0 ?summaryMap.get(finCreditRevSubCategory.getSubCategoryCode()):null);
			item.setParent(listbox);
		}
		logger.debug("Leaving");
	}



	/**
	 * This method for set the data according to the formulae.<br>
	 * @param listItem
	 * @throws Exception 
	 */
	public void setData() throws Exception{
		logger.debug("Entering");


		engine.put("EXCHANGE", this.conversionRate.getValue());
		engine.put("NoOfShares", this.noOfShares.getValue());
		engine.put("MarketPrice", this.marketPrice.getValue());	


		for(FinCreditRevCategory fcrcy:listOfFinCreditRevCategory){
			List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory = this.creditApplicationReviewService.
			getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(fcrcy.getCategoryId());

			//total calculation	
			for(int i=0;i<listOfFinCreditRevSubCategory.size();i++){
				FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
				if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc") && !finCreditRevSubCategory.getItemsToCal().equals("")){							
					BigDecimal value = null;
					try{
						if((engine.eval(finCreditRevSubCategory.getItemsToCal().replace("YN." ,"")).toString().equals("NaN")) ||
								(engine.eval(finCreditRevSubCategory.getItemsToCal().replace("YN." ,"")).toString().equals("Infinity"))){
							value = null;
						}else{
							value =new BigDecimal(engine.eval(finCreditRevSubCategory.getItemsToCal().replace("YN." ,"")).toString()).setScale(2,RoundingMode.HALF_DOWN);
						} 
					} catch (Exception e) {
						logger.error(e);
						value =null;
					}
					itemsValueMap.put(finCreditRevSubCategory.getSubCategoryCode(),value==null?value:value);
					engine.put(finCreditRevSubCategory.getSubCategoryCode(),value==null?"--":value);
				}
			}


			//ratio calculation

			for(int i=0;i<listOfFinCreditRevSubCategory.size();i++){
				FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
				if(!finCreditRevSubCategory.getItemRule().equals("")){							
					BigDecimal value = null;
					try{
						if((engine.eval(finCreditRevSubCategory.getItemRule().replace("YN." ,"")).toString().equals("NaN")) ||
								(engine.eval(finCreditRevSubCategory.getItemRule().replace("YN." ,"")).toString().equals("Infinity"))){
							value = null;
						}else{
							value =new BigDecimal(engine.eval(finCreditRevSubCategory.getItemRule().replace("YN." ,"")).toString()).setScale(2,RoundingMode.HALF_DOWN);
						} 
					}catch (Exception e) {
						value = null;
						logger.error(e);
					}
					if(fcrcy.getRemarks().equals("R")){
						itemsValueMap.put(finCreditRevSubCategory.getSubCategoryCode(),value);
						engine.put(finCreditRevSubCategory.getSubCategoryCode(),value==null?"--":value);
					}else{
						itemsValueMap.put("R"+finCreditRevSubCategory.getSubCategoryCode(),value);
					}
				}
			}
		}
	}


	/**
	 * This Method/Event is for calculating values
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$auditedValue(ForwardEvent event) throws Exception {
		logger.debug("Entering"+event.toString());
		FinCreditRevSubCategory finCreditRevSubCategory  = (FinCreditRevSubCategory) event.getOrigin().getTarget().getAttribute("data");
		Listbox listbox	  = (Listbox) event.getOrigin().getTarget().getAttribute("ListBoxdata");
		Listitem listItem = (Listitem) listbox.getFellowIfAny("li"+finCreditRevSubCategory.getSubCategoryCode());
		((Tabbox)listbox.getParent().getParent().getParent().getParent()).getSelectedTab().setSelected(true);
		itemsValueMap.put(finCreditRevSubCategory.getSubCategoryCode(),
				(((Decimalbox)listItem.getLastChild().getPreviousSibling().getFirstChild()).getValue()==null?
						BigDecimal.ZERO:
							((Decimalbox)listItem.getLastChild().getPreviousSibling().getFirstChild()).getValue()));

		engine.put(finCreditRevSubCategory.getSubCategoryCode(),
				(((Decimalbox)listItem.getLastChild().getPreviousSibling().getFirstChild()).getValue()==null?
						BigDecimal.ZERO:
							((Decimalbox)listItem.getLastChild().getPreviousSibling().getFirstChild()).getValue()));

		setData();
		listbox.getItems().clear();

		render((FinCreditRevCategory) listbox.getAttribute("fcrc"),listbox);
		logger.debug("Leaving"+event.toString());
	};

	/**
	 * This Method/Event is change the conversion Rate
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$conversionRate(ForwardEvent event) throws Exception {
		logger.debug(event+"Entering");
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		setData();
		setTabs();
		logger.debug(event+"Leaving");
	}

	/**
	 * This Method/Event is change the no of Shares
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$noOfShares(ForwardEvent event) throws Exception {
		logger.debug(event+"Entering");
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		setData();
		setTabs();
		logger.debug(event+"Leaving");
	}

	/**
	 * This Method/Event is change the market price
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$marketPrice(ForwardEvent event) throws Exception {
		logger.debug(event+"Entering");
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}
		setData();
		setTabs();
		logger.debug(event+"Leaving");
	}

	/**
	 * This method for validating the record whether record already existed with customerid and audited year.<br>
	 * @param event
	 * @throws WrongValueException
	 * @throws InterruptedException
	 */
	public void onBlur$auditedYear(ForwardEvent event) throws WrongValueException, InterruptedException {
		logger.debug(event+"Entering");
		if(!StringUtils.trimToEmpty(this.auditedYear.getValue()).equals("") && this.custID.getValue()!=null){
			int count = 0;
			count = this.creditApplicationReviewService.isCreditSummaryExists(this.custID.getValue(), this.auditedYear.getValue());
			if(count>0){
				PTMessageUtils.showErrorMessage("Record already existed with the Customer CIF "
						+this.custCIF.getValue()+" Audited Year "+this.auditedYear.getValue()+"\n"
						+"Please enter the data for valid year");
			}

		}
		logger.debug(event+"Leaving");


	}
	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}



}
