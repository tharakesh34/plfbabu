/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinCreditRevSubCategoryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-11-2013    														*
 *                                                                  						*
 * Modified Date    :  13-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-11-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.customermasters.fincreditrevsubcategory;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Customers/FinCreditRevSubCategory/finCreditRevSubCategoryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinCreditRevSubCategoryDialogCtrl extends GFCBaseListCtrl<FinCreditRevSubCategory> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinCreditRevSubCategoryDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinCreditRevSubCategoryDialog; // autowired
	protected Window window_RuleResultDialog;
	protected Textbox subCategoryCode; // autowired
	protected Textbox subCategoryDesc; // autowired
	//protected Combobox subCategoryItemType; // autowired
	protected Textbox itemRule; // autowired
	protected Checkbox isCreditCCY; // autowired
	//protected Combobox mainSubCategoryCode; // autowired
   	protected Intbox calcSeque; // autowired
	protected Checkbox format; // autowired
	protected Checkbox percentCategory; // autowired
	protected Checkbox grand; // autowired
	public    Codemirror formula;   // autowired
	public    Codemirror formula2;   // autowired
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_calculationDetails;
	protected Grid     gd_Calculation;
	protected Grid     gd_Calculation2;
	protected Radiogroup subCategoryFolmulaType;
	protected Radio subCategoryFormulaFor_breakDown;
	protected Radio subCategoryFormulaFor_Calculated;
	
	
	
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;
	protected Listbox listboxFieldCodes;
	protected Listbox listboxFeeOperators;
	protected Listbox listboxFeeOperators2;
	protected JavaScriptBuilder Sql_Rule;
	
	protected Groupbox 			gb_CreditReviwDetails;
	protected Tabbox 			tabBoxIndexCenter;
	protected Tabs 				tabsIndexCenter;
	protected Tabpanels 		tabpanelsBoxIndexCenter;
	protected Tabpanels 		tabpanelsParent;
	
    protected Tab 		tab_BalanceSheet;
    protected Tab 		tab_IncomeStatement;
    protected Tab 		tab_CashFlow;
    protected Tab       tab_FinancialRatios;
    protected Tabpanel  tabpanelBalanceSheet;
    protected Tabpanel  tabpanelIncomeStatement;
    protected Tabpanel  tabpanelCashFlow;
    protected Tabpanel  tabpanelRatio;
    protected Listbox   listboxBalanceSheet;
    protected Listbox   listboxIncomeStatement;
    protected Listbox   listboxCashFlow;
    protected Listbox   listboxRatio;
    
    protected Tab 		tab_BalanceSheet2;
    protected Tab 		tab_IncomeStatement2;
    protected Tab 		tab_CashFlow2;
    protected Tab       tab_FinancialRatios2;
    protected Tabpanel  tabpanelBalanceSheet2;
    protected Tabpanel  tabpanelIncomeStatement2;
    protected Tabpanel  tabpanelCashFlow2;
    protected Tabpanel  tabpanelRatio2;
    protected Listbox   listboxBalanceSheet2;
    protected Listbox   listboxIncomeStatement2;
    protected Listbox   listboxCashFlow2;
    protected Listbox   listboxRatio2;
    
    protected Tab      tab_BreakDown;
    protected Tabpanel tabpanelBreakDown;
    protected Tab      tab_Calculated;
    protected Tabpanel tabpanelCalculated;
    protected Row      formulas_Row;
	
	// not auto wired vars
	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	private Set<FinCreditRevSubCategory> finCreditRevSubCategoryList = new HashSet<FinCreditRevSubCategory>();
	//private List<FinCreditRevSubCategory> objectFieldList;
	//private List<ValueLabel> listMainSubCategoryCodes;
	private List<ValueLabel> listRuleOperators = PennantAppUtil.getRuleOperator(); // autoWired
	private FinCreditRevSubCategory finCreditRevSubCategory; // overhanded per param
	private FinCreditRevSubCategory prvFinCreditRevSubCategory; // overhanded per param
	private transient FinCreditRevSubCategoryListCtrl finCreditRevSubCategoryListCtrl; // overhanded per param
	private transient CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_subCategoryCode;
	private transient String  		oldVar_subCategoryDesc;
	private transient String  		oldVar_itemRule;
	private transient boolean  		oldVar_isCreditCCY;
	private transient int  		    oldVar_calcSeque;
	private transient boolean  		oldVar_format;
	private transient boolean  		oldVar_percentCategory;
	private transient boolean  		oldVar_grand;
	private transient String        oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinCreditRevSubCategoryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	JSONArray variables = new JSONArray();
	HashSet<String> subCategoryCodes = new HashSet<String>();
    String role="";
	// ServiceDAOs / Domain Classes
	private transient FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	/**
	 * 
	 */
	private transient CreditApplicationReviewService creditApplicationReviewService;
	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	
	// TODO List Must be Created for Customer CategoryID
	
    //private List<ValueLabel> listCategoryId=PennantAppUtil.Acadamic(); // autowired
	//private List<ValueLabel> subCategoryItemTypelist = PennantStaticListUtil.getSubCategoryTypeList();

	/**
	 * default constructor.<br>
	 */
	public FinCreditRevSubCategoryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinCreditRevSubCategory object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinCreditRevSubCategoryDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */
		
			
			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
			doCheckRights();
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			
			// READ OVERHANDED params !
			if (args.containsKey("finCreditRevSubCategory")) {
				this.finCreditRevSubCategory = (FinCreditRevSubCategory) args.get("finCreditRevSubCategory");
				FinCreditRevSubCategory befImage =new FinCreditRevSubCategory();
				BeanUtils.copyProperties(this.finCreditRevSubCategory, befImage);
				this.finCreditRevSubCategory.setBefImage(befImage);
				
				setFinCreditRevSubCategory(this.finCreditRevSubCategory);
			} else {
				setFinCreditRevSubCategory(null);
			}
		
			if(args.containsKey("listOfFinCreditRevCategory")){
				listOfFinCreditRevCategory = (List<FinCreditRevCategory>)args.get("listOfFinCreditRevCategory");
			}
			
			if(args.containsKey("parentRole")){
				role = (String) args.get("parentRole");
			}
			
			
			doLoadWorkFlow(this.finCreditRevSubCategory.isWorkflow(),this.finCreditRevSubCategory.getWorkflowId(),this.finCreditRevSubCategory.getNextTaskId());
	
			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "FinCreditRevSubCategoryDialog");
			}
	
			setListCategoryId();
			doFillListOperators(listboxFeeOperators);
			doFillListOperators(listboxFeeOperators2);
			// READ OVERHANDED params !
			// we get the finCreditRevSubCategoryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete finCreditRevSubCategory here.
			if (args.containsKey("finCreditRevSubCategoryListCtrl")) {
				setFinCreditRevSubCategoryListCtrl((FinCreditRevSubCategoryListCtrl) args.get("finCreditRevSubCategoryListCtrl"));
			} else {
				setFinCreditRevSubCategoryListCtrl(null);
			}
			
			if (args.containsKey("creditApplicationReviewDialogCtrl")) {
				setCreditApplicationReviewDialogCtrl((CreditApplicationReviewDialogCtrl) args.get("creditApplicationReviewDialogCtrl"));
				//this.window_FinCreditRevSubCategoryDialog.doModal();
			} else {
				setCreditApplicationReviewDialogCtrl(null);
			}
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
			.getValue().intValue()- PennantConstants.borderlayoutMainNorth;
			
			this.gb_basicDetails.setHeight(this.borderLayoutHeight - 75 +"px");
			this.gb_calculationDetails.setHeight(this.borderLayoutHeight - 225+"px");
			this.gd_Calculation.setHeight(this.borderLayoutHeight - 285+"px");
			this.gd_Calculation2.setHeight(this.borderLayoutHeight - 285+"px");
			
			this.listboxBalanceSheet.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxCashFlow.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxIncomeStatement.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxRatio.setHeight(this.borderLayoutHeight - 420+"px");
			this.formula.setHeight(this.borderLayoutHeight - 320+"px");
			this.listboxFeeOperators.setHeight(this.borderLayoutHeight - 320+"px");

			
			this.listboxBalanceSheet2.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxCashFlow2.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxIncomeStatement2.setHeight(this.borderLayoutHeight - 420+"px");
			this.listboxRatio2.setHeight(this.borderLayoutHeight - 420+"px");
			this.formula2.setHeight(this.borderLayoutHeight - 320+"px");
			this.listboxFeeOperators2.setHeight(this.borderLayoutHeight - 320+"px");
			
			// set Field Properties
			this.subCategoryFormulaFor_breakDown.setSelected(true);
			this.tab_Calculated.setVisible(false);
			doSetFieldProperties();
			doShowDialog(getFinCreditRevSubCategory());
			
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_FinCreditRevSubCategoryDialog.onClose();
		}
		logger.debug("Leaving");
	}

	public void onCheck$subCategoryFolmulaType(Event event){
		logger.debug(event+"Entering") ;
		if(this.subCategoryFolmulaType.getSelectedItem().getValue().equals(Labels.getLabel("label_BreakDown.value"))){
			this.tab_BreakDown.setLabel(Labels.getLabel("label_BreakDown.value"));
			this.formula.setValue(finCreditRevSubCategory.getItemRule().replace("YN.", ""));
		} else {
			this.tab_BreakDown.setLabel(Labels.getLabel("label_Calculated.value"));
			this.formula.setValue(finCreditRevSubCategory.getItemsToCal().replace("YN.", ""));
		}
		
		logger.debug(event+"Leaving") ;
	}
	
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.subCategoryCode.setMaxlength(20);
	 	this.subCategoryDesc.setMaxlength(200);
	 	this.itemRule.setMaxlength(300);
		this.calcSeque.setMaxlength(10);
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		
		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		
		getUserWorkspace().alocateAuthorities("FinCreditRevSubCategoryDialog");
		
	    // this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryDialog_btnNew"));
		 this.btnEdit.setVisible(true);
		// this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryDialog_btnSave"));
		 this.btnSave.setVisible(true);
		 this.btnCancel.setVisible(false);
		
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	public void onCheck$subCategoryItemType_Entry(Event event){
		logger.debug("Entering "+event.toString());
		tab_Calculated.setVisible(false);
		logger.debug("Leaving "+event.toString());
	}
	public void onCheck$subCategoryItemType_Calculated(Event event){
		logger.debug("Entering "+event.toString());
		tab_Calculated.setVisible(true);
		logger.debug("Leaving "+event.toString());
	}


	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnSave2(ForwardEvent event) throws InterruptedException {
		logger.debug(event.toString());
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		if (validateFormula(data)) {
			doSave();
		}
	}

	
	public boolean validateFormula(Object[] data){
		boolean noerrors = false;
		// object containing errors and variables
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[1];
			// array of variables
			variables = (JSONArray) data[2];

			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (!variable.get("name").equals("Result")) {
						if (!subCategoryCodes.contains(variable.get("name"))) {
							// if new variables found throw error message
							noerrors = false;
							Messagebox.show("Unknown Variable :" + variable.get("name"), "Unknown", Messagebox.OK, Messagebox.ERROR);
							return noerrors;
						} else {
							noerrors = true;
						}
					}
				}

			} else {
				for (int i = 0; i < errors.size(); i++) {
					JSONObject error = (JSONObject) errors.get(i);
					if (error != null) {
						Messagebox.show(error.get("reason").toString(), "Error : At Line " + error.get("line") + ",Position " + error.get("character"), Messagebox.OK,
								Messagebox.ERROR);
					}
				}
			}
		}
		return noerrors;
	}
	
	
	
	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinCreditRevSubCategoryDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	
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
		boolean close=true;
//		if (isDataChanged()) {
//			logger.debug("isDataChanged : true");
//
//			// Show a confirm box
//			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
//			final String title = Labels.getLabel("message.Information");
//
//			MultiLineMessageBox.doSetTemplate();
//			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);
//
//			if (conf==MultiLineMessageBox.YES){
//				logger.debug("doClose: Yes");
//				doSave();
//				close=false;
//			}else{
//				logger.debug("doClose: No");
//			}
//		}else{
//			logger.debug("isDataChanged : false");
//		}
		
		if(close){
			 //window_FinCreditRevSubCategoryDialog.onClose();
			closeDialog2(window_FinCreditRevSubCategoryDialog, "FinCreditRevSubCategoryDialog");
			//getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(true);
			
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinCreditRevSubCategory
	 *            FinCreditRevSubCategory
	 */
	public void doWriteBeanToComponents(FinCreditRevSubCategory aFinCreditRevSubCategory) {
		logger.debug("Entering") ;
		this.subCategoryCode.setValue(aFinCreditRevSubCategory.getSubCategoryCode());
		//fillComboBox(this.categoryId, String.valueOf(aFinCreditRevSubCategory.getCategoryId()), listCategoryId, "");
		//this.categoryId.setValue(PennantApplicationUtil.getlabelDesc(aFinCreditRevSubCategory.getCategoryId(),listCategoryId));
		this.subCategoryDesc.setValue(aFinCreditRevSubCategory.getSubCategoryDesc());
		//this.subCategoryItemType.setValue(aFinCreditRevSubCategory.getSubCategoryCode());
		//this.subCategoryItemType.setValue(aFinCreditRevSubCategory.getSubCategoryItemType());
		this.itemRule.setValue(aFinCreditRevSubCategory.getItemRule());
		
		if(!aFinCreditRevSubCategory.getItemsToCal().equals("")  && 
				        !aFinCreditRevSubCategory.getRemarks().equals("R")){
			this.formula.setValue(aFinCreditRevSubCategory.getItemsToCal().replace("YN.", ""));
			this.tab_BreakDown.setLabel(Labels.getLabel("label_Calculated.value"));
			this.subCategoryFolmulaType.setSelectedIndex(1);
		} else {
			this.formula.setValue(aFinCreditRevSubCategory.getItemRule().replace("YN.", ""));
			this.tab_BreakDown.setLabel(Labels.getLabel("label_BreakDown.value"));
			this.subCategoryFolmulaType.setSelectedIndex(0);
			this.subCategoryFormulaFor_Calculated.setVisible(false);
		}
		

		
		this.isCreditCCY.setChecked(aFinCreditRevSubCategory.isIsCreditCCY());
		//this.mainSubCategoryCode.setValue(aFinCreditRevSubCategory.getMainSubCategoryCode() != null ? aFinCreditRevSubCategory.getMainSubCategoryCode() : "");
		this.calcSeque.setValue(Integer.parseInt(aFinCreditRevSubCategory.getCalcSeque()!= null ? aFinCreditRevSubCategory.getCalcSeque() : "0"));
		this.format.setChecked(aFinCreditRevSubCategory.isFormat());
		this.percentCategory.setChecked(aFinCreditRevSubCategory.isPercentCategory());
		this.grand.setChecked(aFinCreditRevSubCategory.isGrand());
	
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinCreditRevSubCategory
	 */
	public void doWriteComponentsToBean(FinCreditRevSubCategory aFinCreditRevSubCategory) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aFinCreditRevSubCategory.setSubCategoryCode(this.subCategoryCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
			String categoryId =null; 
			if(StringUtils.trimToEmpty(this.categoryId.getSelectedItem().getValue().toString())!=null){
				categoryId = this.categoryId.getSelectedItem().getValue().toString();	
			}
			if(categoryId==null || categoryId.equals(PennantConstants.List_Select)){
				throw new WrongValueException( this.categoryId,Labels.getLabel("STATIC_INVALID",new String[] { Labels.getLabel("label_FinCreditRevSubCategoryDialog_CategoryId.value") }));
			}
			
			if(categoryId==PennantConstants.List_Select){
				categoryId =null;
			}
			aFinCreditRevSubCategory.setCategoryId(Long.valueOf(categoryId));
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		try {
		    aFinCreditRevSubCategory.setSubCategoryDesc(this.subCategoryDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
		    aFinCreditRevSubCategory.setSubCategoryItemType(this.subCategoryItemType.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		try {
			if(this.itemRule.isVisible()) {
		    aFinCreditRevSubCategory.setItemRule(this.itemRule.getValue());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinCreditRevSubCategory.setIsCreditCCY(this.isCreditCCY.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
		    aFinCreditRevSubCategory.setMainSubCategoryCode(this.mainSubCategoryCode.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		try {
		    aFinCreditRevSubCategory.setCalcSeque(String.valueOf(this.calcSeque.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinCreditRevSubCategory.setFormat(this.format.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinCreditRevSubCategory.setPercentCategory(this.percentCategory.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinCreditRevSubCategory.setGrand(this.grand.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aFinCreditRevSubCategory.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinCreditRevSubCategory
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinCreditRevSubCategory aFinCreditRevSubCategory) throws InterruptedException {
		logger.debug("Entering") ;
		
		
		
		// if aFinCreditRevSubCategory == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinCreditRevSubCategory == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinCreditRevSubCategory = getFinCreditRevSubCategoryService().getNewFinCreditRevSubCategory();
			
			setFinCreditRevSubCategory(aFinCreditRevSubCategory);
		} else {
			setFinCreditRevSubCategory(aFinCreditRevSubCategory);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aFinCreditRevSubCategory.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.subCategoryCode.focus();
		} else {
			this.subCategoryDesc.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			getTabsForBreakDown();
			//getTabsForCalculated();
			/*this.gd_Calculation.setParent(tabpanelBreakDown);
			this.gd_Calculation.setParent(tabpanelCalculated);*/
			//fillComboBox(this.subCategoryItemType, "", subCategoryItemTypelist,"");
		    // fillComboBox(this.mainSubCategoryCode, "", listMainSubCategoryCodes,"");
			doWriteBeanToComponents(aFinCreditRevSubCategory);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			/*getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(false);
			getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.getParent().appendChild(this.window_FinCreditRevSubCategoryDialog);*/
			
			this.
			getBorderLayoutHeight();
			setDialog2(window_FinCreditRevSubCategoryDialog);
			
			getUserWorkspace().alocateAuthorities("CreditApplicationReviewDialog",role);
			this.btnSave.setVisible(getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory"));
			this.btnDelete.setVisible(false);
			this.btnEdit.setVisible(false);
			
			
			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}
	
	
	public void doFillListOperators(Listbox listbox){
		Listitem item;
		Listcell cell;
		if (listRuleOperators != null) {
			for (int i = 0; i < listRuleOperators.size(); i++) {
				item = new Listitem();
				cell = new Listcell(listRuleOperators.get(i).getValue());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				cell = new Listcell(listRuleOperators.get(i).getLabel());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 200 +"px");
				item.setParent(listbox);
			}
		}
	}
	
	
	// Getting Sub Category Tabs  
	public void getTabsForBreakDown() throws Exception{
		logger.debug("Entering");
		    int i=0;
			//this.tab_BalanceSheet.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 200 +"px");
			//this.tab_IncomeStatement.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 200 +"px");
			//this.tab_FinancialRatios.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 200 +"px");
		    
		    this.gb_calculationDetails.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 50 +"px");
		    this.tabpanelsBoxIndexCenter.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 +"px");
		    this.tabsIndexCenter.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 80 +"px");
		    this.tabpanelBreakDown.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 +"px");
		    this.tabpanelsParent.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) -60 +"px");
            this.formula.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 +"px");
			
			for(FinCreditRevCategory fcrc :listOfFinCreditRevCategory){
				 i=i+1;
				 if(i==1) {
				getListboxRender(fcrc, listboxBalanceSheet);
				 } if(i==2) {
					 getListboxRender(fcrc, listboxIncomeStatement);
				 } if(listOfFinCreditRevCategory.size() > 3 && i==3) {
					 getListboxRender(fcrc, listboxCashFlow);
					 this.tab_CashFlow.setVisible(true);
				 } else if(i==3){
					 getListboxRender(fcrc, listboxRatio);
				 }
				 if(i==4) {
					 getListboxRender(fcrc, listboxRatio);
				 } 
			}
		logger.debug("Leaving");
		
	}
	public void getTabsForCalculated() throws Exception{
		logger.debug("Entering");
		int i=0;
		for(FinCreditRevCategory fcrc :listOfFinCreditRevCategory){
			i=i+1;
			if(i==1) {
				getListboxRender(fcrc, listboxBalanceSheet2);
			} if(i==2) {
				getListboxRender(fcrc, listboxIncomeStatement2);
			} if(listOfFinCreditRevCategory.size() > 3 && i==3) {
				getListboxRender(fcrc, listboxCashFlow2);
				this.tab_CashFlow2.setVisible(true);
			} else if(i==3){
				getListboxRender(fcrc, listboxRatio2);
			}
			if(i==4) {
				getListboxRender(fcrc, listboxRatio2);
			} 
		}
		logger.debug("Leaving");
		
	}
	
	
	public void getListboxRender(FinCreditRevCategory fcrc,Listbox listbox){
	logger.debug("Entering");
		Listitem item;
		Listcell cell;
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory= 
		this.creditApplicationReviewService.getFinCreditRevSubCategoryByCategoryId(fcrc.getCategoryId());
		finCreditRevSubCategoryList.addAll(listOfFinCreditRevSubCategory);
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++){
		item = new Listitem();
		cell = new Listcell(listOfFinCreditRevSubCategory.get(i).getSubCategoryCode());
		subCategoryCodes.add(listOfFinCreditRevSubCategory.get(i).getSubCategoryCode());
		cell.setStyle("text-align:left;");
		cell.setParent(item);
		cell = new Listcell(listOfFinCreditRevSubCategory.get(i).getSubCategoryDesc());
		cell.setStyle("text-align:left;");
		cell.setParent(item);
		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 300 +"px");
		item.setParent(listbox);
		 }
		logger.debug("Leaving");
	}
	

	
	/*public void doFillListBoxListCodes(){
		logger.debug("Entering");
		Listitem item;
		Listcell cell;
		for (int i = 0; i < objectFieldList.size(); i++){
		item = new Listitem();
		cell = new Listcell(objectFieldList.get(i).getSubCategoryCode());
		cell.setStyle("text-align:left;");
		cell.setParent(item);
		cell = new Listcell(objectFieldList.get(i).getSubCategoryDesc());
		cell.setStyle("text-align:left;");
		cell.setParent(item);
		item.setParent(listboxFieldCodes);
		}
		logger.debug("Leaving");
	}*/
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_subCategoryCode = this.subCategoryCode.getValue();
	  //	this.oldVar_categoryId = Long.valueOf(this.categoryId.getValue());
		this.oldVar_subCategoryDesc = this.subCategoryDesc.getValue();
	//	this.oldVar_subCategoryItemType = this.subCategoryItemType.getSelectedItem().getValue().toString();
		//this.oldVar_itemsToCal = this.itemsToCal.getValue();
		this.oldVar_itemRule = this.itemRule.getValue();
		this.oldVar_isCreditCCY = this.isCreditCCY.isChecked();
	 //	this.oldVar_mainSubCategoryCode = this.mainSubCategoryCode.getSelectedItem().getValue().toString();
		this.oldVar_calcSeque = this.calcSeque.intValue();	
		this.oldVar_format = this.format.isChecked();
		this.oldVar_percentCategory = this.percentCategory.isChecked();
		this.oldVar_grand = this.grand.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.subCategoryCode.setValue(this.oldVar_subCategoryCode);
		//fillComboBox(this.categoryId, String.valueOf(this.oldVar_categoryId), listCategoryId, "");
		//this.categoryId.setValue(this.oldVar_categoryId);
		this.subCategoryDesc.setValue(this.oldVar_subCategoryDesc);
		//this.subCategoryItemType.setValue(this.oldVar_subCategoryItemType);
		//this.itemsToCal.setValue(this.oldVar_itemsToCal);
		this.itemRule.setValue(this.oldVar_itemRule);
		this.isCreditCCY.setChecked(this.oldVar_isCreditCCY);
		// this.mainSubCategoryCode.setValue(this.oldVar_mainSubCategoryCode);
		this.calcSeque.setValue(this.oldVar_calcSeque);
		this.format.setChecked(this.oldVar_format);
		this.percentCategory.setChecked(this.oldVar_percentCategory);
		this.grand.setChecked(this.oldVar_grand);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled()){
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
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		if (this.oldVar_subCategoryCode != this.subCategoryCode.getValue()) {
			return true;
		}
		
		if (this.oldVar_subCategoryDesc != this.subCategoryDesc.getValue()) {
			return true;
		}
		/*if (this.oldVar_subCategoryItemType != this.subCategoryItemType.getValue()) {
			return true;
		}*/
		/*if (this.oldVar_itemsToCal != this.itemsToCal.getValue()) {
			return true;
		}*/
		if (this.oldVar_itemRule != this.itemRule.getValue()) {
			return true;
		}
		if (this.oldVar_isCreditCCY != this.isCreditCCY.isChecked()) {
			return true;
		}
		/*if (this.oldVar_mainSubCategoryCode != this.mainSubCategoryCode.getSelectedItem().getValue().toString()) {
			return true;
		}*/
		if (this.oldVar_calcSeque != this.calcSeque.intValue()) {
			return  true;
		}
		if (this.oldVar_format != this.format.isChecked()) {
			return true;
		}
		if (this.oldVar_percentCategory != this.percentCategory.isChecked()) {
			return true;
		}
		if (this.oldVar_grand != this.grand.isChecked()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.subCategoryCode.isReadonly()){
			this.subCategoryCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value")}));
		}	
			
		if (!this.subCategoryDesc.isReadonly()){
			this.subCategoryDesc.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryDesc.value")}));
		}	
		/*if (!this.subCategoryItemType.isReadonly()){
			this.subCategoryItemType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryItemType.value")}));
		}*/	
		/*if (!this.itemsToCal.isReadonly()){
			this.itemsToCal.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_ItemsToCal.value")}));
		}*/	
		if (!this.itemRule.isReadonly()){
			this.itemRule.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_ItemRule.value")}));
		}	
		/*if (!this.mainSubCategoryCode.isReadonly()){
			this.mainSubCategoryCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value")}));
		}*/	
		if (!this.calcSeque.isReadonly()){
			this.calcSeque.setConstraint(new IntValidator(10,Labels.getLabel("label_FinCreditRevSubCategoryDialog_CalcSeque.value")));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.subCategoryCode.setConstraint("");
		this.subCategoryDesc.setConstraint("");
		//this.subCategoryItemType.setConstraint("");
		//this.itemsToCal.setConstraint("");
		this.itemRule.setConstraint("");
		//this.mainSubCategoryCode.setConstraint("");
		this.calcSeque.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinCreditRevSubCategory object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinCreditRevSubCategory aFinCreditRevSubCategory = new FinCreditRevSubCategory();
		BeanUtils.copyProperties(getFinCreditRevSubCategory(), aFinCreditRevSubCategory);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinCreditRevSubCategory.getSubCategoryCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinCreditRevSubCategory.getRecordType()).equals("")){
				aFinCreditRevSubCategory.setVersion(aFinCreditRevSubCategory.getVersion()+1);
				aFinCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFinCreditRevSubCategory.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinCreditRevSubCategory,tranType)){
					refreshList();
					this.window_FinCreditRevSubCategoryDialog.onClose();
					getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(true);				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinCreditRevSubCategory object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService().getNewFinCreditRevSubCategory();
		aFinCreditRevSubCategory.setNewRecord(true);
		setFinCreditRevSubCategory(aFinCreditRevSubCategory);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.subCategoryCode.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getFinCreditRevSubCategory().isNewRecord()){
		  	this.subCategoryCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.subCategoryCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		/*readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_subCategorySeque"), this.subCategorySeque);
	 	readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_categoryId"), this.categoryId);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_subCategoryDesc"), this.subCategoryDesc);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_subCategoryItemType"), this.subCategoryItemType);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_itemsToCal"), this.itemsToCal);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_itemRule"), this.itemRule);
	 	readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_isCreditCCY"), this.isCreditCCY);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_mainSubCategoryCode"), this.mainSubCategoryCode);
		readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_calcSeque"), this.calcSeque);
	 	readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_format"), this.format);
	 	readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_percentCategory"), this.percentCategory);
	 	readOnlyComponent(isReadOnly("FinCreditRevSubCategoryDialog_grand"), this.grand);*/

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.finCreditRevSubCategory.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.subCategoryCode.setReadonly(true);
		this.subCategoryDesc.setReadonly(true);
		//this.subCategoryItemType.setReadonly(true);
		this.itemRule.setReadonly(true);
		this.isCreditCCY.setDisabled(true);
		// this.mainSubCategoryCode.setReadonly(true);
		this.calcSeque.setReadonly(true);
		this.format.setDisabled(true);
		this.percentCategory.setDisabled(true);
		this.grand.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
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
		
		this.subCategoryCode.setValue("");
		this.subCategoryDesc.setValue("");
		//this.subCategoryItemType.setValue("");
		this.itemRule.setValue("");
		this.isCreditCCY.setChecked(false);
		// this.mainSubCategoryCode.setValue("");
		this.calcSeque.setText("");
		this.format.setChecked(false);
		this.percentCategory.setChecked(false);
		this.grand.setChecked(false);
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		//final FinCreditRevSubCategory aFinCreditRevSubCategory = new FinCreditRevSubCategory();
		//BeanUtils.copyProperties(getFinCreditRevSubCategory(), aFinCreditRevSubCategory);

		if(this.formula.getValue() != null ){
			
            String sFormula = this.formula.getValue();
            if(!this.finCreditRevSubCategoryList.isEmpty()){
            	
            	for(FinCreditRevSubCategory finCreditRevSubCategory : finCreditRevSubCategoryList){
            		if(sFormula.contains(finCreditRevSubCategory.getSubCategoryCode()) 
            			&& !sFormula.contains("YN."+finCreditRevSubCategory.getSubCategoryCode())){
            			sFormula = sFormula.replace(finCreditRevSubCategory.getSubCategoryCode(), "YN."+finCreditRevSubCategory.getSubCategoryCode());
            		}
            	}
            }
            
            
			if(this.subCategoryFolmulaType.getSelectedItem().getValue().equals(Labels.getLabel("label_BreakDown.value"))){
				this.finCreditRevSubCategory.setItemRule(sFormula);
			} else {
				this.finCreditRevSubCategory.setItemsToCal(sFormula);
			}
			
			
			
			
			this.finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);	
			
				for(int i=0; i < this.getCreditApplicationReviewDialogCtrl().listOfFinCreditRevSubCategory.size(); i++){
					FinCreditRevSubCategory aFinCreditRevSubCategory = this.getCreditApplicationReviewDialogCtrl().listOfFinCreditRevSubCategory.get(i);
					
					if(aFinCreditRevSubCategory.getSubCategoryCode().equals(this.finCreditRevSubCategory.getSubCategoryCode())) {
						if(this.finCreditRevSubCategory.equals(aFinCreditRevSubCategory)){
							boolean isModified = false;
						if(finCreditRevSubCategory.getRemarks().equals("R")){
							aFinCreditRevSubCategory.setItemRule(finCreditRevSubCategory.getItemRule());
							isModified = true;
						} else {
							aFinCreditRevSubCategory.setItemsToCal(finCreditRevSubCategory.getItemsToCal());
							isModified = true;
						}
						if(isModified ) {
						aFinCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						this.getCreditApplicationReviewDialogCtrl().modifiedFinCreditRevSubCategoryList.add(aFinCreditRevSubCategory);
						}
					}
				}
			}
		}
		
		this.window_FinCreditRevSubCategoryDialog.onClose();
		getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(true);
		logger.debug("Leaving");

	}

	private boolean doProcess(FinCreditRevSubCategory aFinCreditRevSubCategory,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aFinCreditRevSubCategory.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinCreditRevSubCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinCreditRevSubCategory.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aFinCreditRevSubCategory.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinCreditRevSubCategory.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinCreditRevSubCategory);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aFinCreditRevSubCategory))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aFinCreditRevSubCategory.setTaskId(taskId);
			aFinCreditRevSubCategory.setNextTaskId(nextTaskId);
			aFinCreditRevSubCategory.setRoleCode(getRole());
			aFinCreditRevSubCategory.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aFinCreditRevSubCategory, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aFinCreditRevSubCategory);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinCreditRevSubCategory, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aFinCreditRevSubCategory, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinCreditRevSubCategoryService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinCreditRevSubCategoryService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinCreditRevSubCategoryService().doApprove(auditHeader);

						if(aFinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinCreditRevSubCategoryService().doReject(auditHeader);
						if(aFinCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinCreditRevSubCategoryDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_FinCreditRevSubCategoryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

   private void setListCategoryId(){
		/*for (int i = 0; i < listCategoryId.size(); i++) {
			   Comboitem comboitem = new Comboitem();
			   comboitem = new Comboitem();
			   comboitem.setLabel(listCategoryId.get(i).getLabel());
			   comboitem.setValue(listCategoryId.get(i).getValue());
			   this.categoryId.appendChild(comboitem);
		}
		this.categoryId.setValue(PennantConstants.List_Select);*/
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

	public FinCreditRevSubCategory getFinCreditRevSubCategory() {
		return this.finCreditRevSubCategory;
	}

	public void setFinCreditRevSubCategory(FinCreditRevSubCategory finCreditRevSubCategory) {
		this.finCreditRevSubCategory = finCreditRevSubCategory;
	}

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return this.finCreditRevSubCategoryService;
	}

	public void setFinCreditRevSubCategoryListCtrl(FinCreditRevSubCategoryListCtrl finCreditRevSubCategoryListCtrl) {
		this.finCreditRevSubCategoryListCtrl = finCreditRevSubCategoryListCtrl;
	}

	public FinCreditRevSubCategoryListCtrl getFinCreditRevSubCategoryListCtrl() {
		return this.finCreditRevSubCategoryListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(FinCreditRevSubCategory aFinCreditRevSubCategory, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinCreditRevSubCategory.getBefImage(), aFinCreditRevSubCategory);   
		return new AuditHeader(aFinCreditRevSubCategory.getSubCategoryCode(),null,null,null,auditDetail,aFinCreditRevSubCategory.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinCreditRevSubCategoryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}
	
	
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("FinCreditRevSubCategory");
		notes.setReference(getFinCreditRevSubCategory().getSubCategoryCode());
		notes.setVersion(getFinCreditRevSubCategory().getVersion());
		return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
			this.subCategoryCode.setErrorMessage("");
			this.subCategoryDesc.setErrorMessage("");
			//this.subCategoryItemType.setErrorMessage("");
			//this.itemsToCal.setErrorMessage("");
			this.itemRule.setErrorMessage("");
			//this.mainSubCategoryCode.setErrorMessage("");
			this.calcSeque.setErrorMessage("");
	   logger.debug("Leaving");
	}
	
	/**
	 * Method for creating Simulated window with Existing Fields
	 * @param event
	 * @throws InterruptedException
	 */
	private void createSimulationWindow(String values) throws InterruptedException {
		logger.debug("Entering");

		String[] variables = values.split(",");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Variables", variables);
		//map.put("ruleResultDialogCtrl", this);
		map.put("finCreditRevSubCategoryDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/JavaScriptBuilder/RuleResultView.zul",null, map);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	

	
	/**	 
	 * This Method/Event is called from the java script function Validate/Simulate.
	 * It will open a new window to execute the rule 
	 * 
	 * @param event
	 */
	public void onUser$ruleResult_btnValidate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(StringUtils.trimToEmpty(this.formula.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_Formula")}));
		}else{
			while (event.getData() == null) {
				event = ((ForwardEvent) event).getOrigin();
			}
			Object[] data = (Object[]) event.getData();
			// Check clicking button is for Validation  or Simulation
			if(validateFormula(data)){
				getvalidateButtonResult(data);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onUser$ruleResult_btnValidate2(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if(StringUtils.trimToEmpty(this.formula.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_Formula")}));
		}else{
			while (event.getData() == null) {
				event = ((ForwardEvent) event).getOrigin();
			}
			Object[] data = (Object[]) event.getData();
			// Check clicking button is for Validation  or Simulation
			if(validateFormula(data)){
				getvalidateButtonResult(data);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void getvalidateButtonResult(Object[] data) throws InterruptedException{
		logger.debug("Entering");

		boolean isSimulated = (Boolean) data[0];
		JSONArray errors = (JSONArray) data[1];
		JSONArray codeVariables = (JSONArray) data[2];
		boolean isSaveRecord = (Boolean) data[3];

		int conf;
		if(isSimulated){
			String values ="";
			for (int i = 0; i < codeVariables.size(); i++) {

				JSONObject jsonObject = (JSONObject) codeVariables.get(i);
				if(!jsonObject.get("name").equals("Result")){
					values = values + jsonObject.get("name")+",";
				}
			}
			createSimulationWindow(values);
		}else{

			if(errors.size() != 0){
				conf =  (MultiLineMessageBox.show(errors.size()+ 
						PennantJavaUtil.getLabel("message_ErrorCount_CodeMirror"),
						PennantJavaUtil.getLabel("Validate_Title"),
						MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

				if (conf==MultiLineMessageBox.YES){
					Events.postEvent("onUser$errors", window_RuleResultDialog,errors);
				}else{
					//do Nothing
				}
			}else{
				if(isSaveRecord){
					doSave();
				}else{
					conf =  MultiLineMessageBox.show(PennantJavaUtil.getLabel("message_NoError_CodeMirror"),
							" Error Details",MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
				}
			}
		}
		
		logger.debug("Leaving");

		
		
	}
	
	
	/**
	 * Method for showing Error Details
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$errors(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if (formula.getValue().equalsIgnoreCase("")) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("Code_NotEmpty"));
		} else {
			JSONArray message = (JSONArray) event.getData();

			for (int i = 0; i < message.size(); i++) {

				JSONObject jsonObject = (JSONObject) message.get(i);

				if(jsonObject != null){
					
					String errorMsg =  (String) jsonObject.get("reason") ;
					String title = " Error : Line-"+jsonObject.get("line") + ",Character-" + 
											jsonObject.get("character");
					
					int conf;
					if(message.size()-1 != i+1){
						errorMsg = errorMsg +"\n\n"+
									PennantJavaUtil.getLabel("message_ErrorProcess_Conformation");

						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.ERROR, true);
					}else{
						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.OK, Messagebox.ERROR, true);
					}

					if (conf==MultiLineMessageBox.NO || conf==MultiLineMessageBox.OK){
						break;
					}else{
						//do Nothing
					}			
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	
	
	
	

private void refreshList(){
		/*final JdbcSearchObject<FinCreditRevSubCategory> soFinCreditRevSubCategory = getFinCreditRevSubCategoryListCtrl().getSearchObj();
		getFinCreditRevSubCategoryListCtrl().pagingFinCreditRevSubCategoryList.setActivePage(0);
		getFinCreditRevSubCategoryListCtrl().getPagedListWrapper().setSearchObject(soFinCreditRevSubCategory);
		if(getFinCreditRevSubCategoryListCtrl().listBoxFinCreditRevSubCategory!=null){
			getFinCreditRevSubCategoryListCtrl().listBoxFinCreditRevSubCategory.getListModel();
		}*/
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinCreditRevSubCategory getPrvFinCreditRevSubCategory() {
		return prvFinCreditRevSubCategory;
	}


	public void setCreditApplicationReviewDialogCtrl(
			CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl) {
		this.creditApplicationReviewDialogCtrl = creditApplicationReviewDialogCtrl;
	}

	public CreditApplicationReviewDialogCtrl getCreditApplicationReviewDialogCtrl() {
		return creditApplicationReviewDialogCtrl;
	}
}
