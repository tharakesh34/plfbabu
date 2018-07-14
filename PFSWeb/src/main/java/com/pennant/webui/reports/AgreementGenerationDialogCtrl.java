package com.pennant.webui.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.AgreementEngine;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class AgreementGenerationDialogCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = Logger.getLogger(AgreementGenerationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AgreementGeneration; 			    // autowired
	
	protected ExtendedCombobox	custCIF;
	protected CurrencyBox 		contractAmt; 					// autowired
	protected ExtendedCombobox 	purchRegOffice;					// autowired
	protected Textbox 			purchaddress; 				    // autowired
	protected Textbox 			fax; 							// autoWired
	protected Textbox       	faxCountryCode;					// autoWired
	protected Textbox			faxAreaCode;					// autoWired
	protected Textbox  			attention;		    			// autowired
	protected Datebox  			contractDate;					// autowired
	protected Textbox  			titleNo;						// autowired
	protected Decimalbox		rate;							// autowired
	
	protected Grid grid_basicDetails;
	protected Groupbox gb_cmtDetails;
	protected Radiogroup numberOfCommits;
	
	protected Groupbox gb_Action;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private transient FinanceDetailService financeDetailService;
	
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected Tabbox         tabbox;
	
	boolean isfinance;
	protected Button btnPrintFromFinance;
	protected Button btnSearchCustCIF;
	protected North north_AgreementGeneration;
	protected Window parentWindow;
	private int ccyformatt=0;
	private AgreementGenerationDialogCtrl agreementGenerationDialogCtrl = null;
	
	/**
	 * default constructor.<br>
	 */
	public AgreementGenerationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AgreementGenerationDialog";
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AgreementGeneration(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AgreementGeneration);
		
		tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent().getParent();
		if (arguments.containsKey("window")) {
			parentWindow=(Window) arguments.get("window");
		
		}

		if (arguments.containsKey("agreementGenerationDialogCtrl")) {
			this.agreementGenerationDialogCtrl = (AgreementGenerationDialogCtrl) arguments
					.get("agreementGenerationDialogCtrl");
		}
		// Hegiht Setting
		
		getBorderLayoutHeight();
		
		doSetFieldProperties();
		this.btnPrintFromFinance.setVisible(true);
		if (isfinance) {
			this.btnPrintFromFinance.setVisible(true);
			this.north_AgreementGeneration.setVisible(false);
			this.window_AgreementGeneration.setHeight(this.borderLayoutHeight - 80 + "px");
		}else{
			this.btnPrintFromFinance.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.custCIF.setMaxlength(6);
		this.custCIF.setMandatoryStyle(true);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[]{"CustCIF"});
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL);
		this.custCIF.setFilters(filters);
		ccyformatt = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		this.purchRegOffice.setMandatoryStyle(true);
		this.purchRegOffice.setTextBoxWidth(161);
		this.purchRegOffice.setModuleName("Province");
		this.purchRegOffice.setValueColumn("CPProvince");
		this.purchRegOffice.setDescColumn("CPProvinceName");
		this.purchRegOffice.setValidateColumns(new String[] { "CPProvince" });
		Filter[] purchRegOffice = new Filter[1];
		purchRegOffice[0] = new Filter("CPCountry", "AE", Filter.OP_EQUAL);
		this.purchRegOffice.setFilters(purchRegOffice);
		this.contractAmt.setMandatory(true);
		this.contractAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		this.contractAmt.setScale(ccyformatt);
		this.contractAmt.setValue(BigDecimal.ZERO);
		this.contractDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.faxCountryCode.setMaxlength(4);
		this.faxAreaCode.setMaxlength(4);
		this.fax.setMaxlength(8);
		this.rate.setMaxlength(13);
		this.rate.setFormat(PennantConstants.rateFormate9);
		this.rate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rate.setScale(9);
		
		logger.debug("Leaving");
	}
	
	public void onFulfill$custCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();
		Clients.clearWrongValue(this.custCIF);
		Object dataObject = this.custCIF.getObject();
		if (dataObject instanceof String) {
			this.custCIF.setValue("");
			this.custCIF.setDescription("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custCIF.setValue(details.getCustCIF());
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		Date appDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		if (!this.custCIF.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementGenerationDialog_CustCIF.value"),null,true,true));
		}
		if (!this.purchRegOffice.isReadonly()){
			this.purchRegOffice.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementGenerationDialog_PurchRegOffice.value"),null,true,true));
		}
		if (!this.purchaddress.isReadonly()){
			this.purchaddress.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementGenerationDialog_Purchaddress.value"),PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if(!this.contractDate.isReadonly()){
			this.contractDate.setConstraint(new PTDateValidator(Labels.getLabel("label_AgreementGenerationDialog_Date.value"), true,appDate,appEndDate,false));
		}
		if (!this.attention.isReadonly()){
			this.attention.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementGenerationDialog_Attention.value"),PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_AgreementGenerationDialog_FaxCountryCode.value"),false,1));
		}
		if (!this.faxAreaCode.isReadonly()) {
			this.faxAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_AgreementGenerationDialog_FaxAreaCode.value"),false,2));
		}
		if (!this.fax.isReadonly()){
			this.fax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_AgreementGenerationDialog_fax.value"),false,3));
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_AgreementGeneration);
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException 
	 * @throws CustomerLimitProcessException 
	 */
	public void onClick$btnPrint(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		printAgreement(event);
		logger.debug("Leaving" + event.toString());
	}
	
	private void printAgreement(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		doSetValidation();
		
		AgreementDetail agreementGeneration = new AgreementDetail();
		
		try {
			agreementGeneration.setContractDate(DateUtility.formatToLongDate(this.contractDate.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			agreementGeneration.setCustCIF(this.custCIF.getValue());
			agreementGeneration.setCustName(this.custCIF.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//SHOULD BE CORRECTED BASED ON FIELDS. MAY REQUIRE NEW BEAN FOR THIS PARTICULAR DETAILS. UNNECESSARY TO USE AGREEMENT DETAILS BEAN
		//========================================
		/*try {
			agreementGeneration.setPurchRegOffice(this.purchRegOffice.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			agreementGeneration.setContractAmt(PennantApplicationUtil.formatAmount(this.contractAmt.getValidateValue(),ccyformatt,false));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			agreementGeneration.setPurchaddress(this.purchaddress.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			agreementGeneration.setAttention(this.attention.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			agreementGeneration.setFax(PennantApplicationUtil.formatPhoneNumber
					(this.faxCountryCode.getValue(),this.faxAreaCode.getValue(),this.fax.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			agreementGeneration.setTitleNo(this.titleNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		
		try {
			agreementGeneration.setRate(String.valueOf(this.rate.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		*/
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		String agreement= PennantConstants.AGREEMENT_GEN;
		String templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		AgreementEngine engine = new AgreementEngine(templatePath, templatePath);
		String refNo = agreementGeneration.getCustCIF();
		String reportName = refNo + "_"+agreement;
		engine.setTemplate(agreement);
		engine.loadTemplate();
		engine.mergeFields(agreementGeneration);
		engine.showDocument(this.window_AgreementGeneration, reportName, SaveFormat.DOCX);
		engine = null;
		logger.debug("Leaving");
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		
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
		this.custCIF.getValue();
		this.purchaddress.setValue("");
		this.purchRegOffice.setValue("");
		this.contractAmt.setValue("");
		this.contractDate.setValue(null);
		this.fax.setValue("");
		this.faxAreaCode.setValue("");
		this.faxCountryCode.setValue("");
		this.titleNo.setValue("");
		this.attention.setValue("");
		
		logger.debug("Leaving");
	}
	
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		
		Customer customer = (Customer) nCustomer;
		if(customer != null){
			this.custCIF.setValue(customer.getCustCIF());
		}else{
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public AgreementGenerationDialogCtrl getAgreementGenerationDialogCtrl() {
		return agreementGenerationDialogCtrl;
	}

	public void setAgreementGenerationDialogCtrl(
			AgreementGenerationDialogCtrl agreementGenerationDialogCtrl) {
		this.agreementGenerationDialogCtrl = agreementGenerationDialogCtrl;
	}

	

	
	


}
