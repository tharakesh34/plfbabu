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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
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
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Customers/FinCreditRevSubCategory/finCreditRevSubCategoryDialog.zul file.
 */
public class FinCreditRevSubCategoryDialogCtrl extends GFCBaseCtrl<FinCreditRevSubCategory> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinCreditRevSubCategoryDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCreditRevSubCategoryDialog; 
	protected Window window_RuleResultDialog;
	protected Textbox subCategoryCode; 
	protected Textbox subCategoryDesc; 
	//protected Combobox subCategoryItemType; 
	protected Textbox itemRule; 
	protected Checkbox isCreditCCY; 
	//protected Combobox mainSubCategoryCode; 
   	protected Intbox calcSeque; 
	protected Checkbox format; 
	protected Checkbox percentCategory; 
	protected Checkbox grand; 
	public    Codemirror formula;   
	public    Codemirror formula2;   
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_calculationDetails;
	protected Grid     gd_Calculation;
	protected Grid     gd_Calculation2;
	protected Radiogroup subCategoryFolmulaType;
	protected Radio subCategoryFormulaFor_breakDown;
	protected Radio subCategoryFormulaFor_Calculated;
	
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
	private FinCreditRevSubCategory finCreditRevSubCategory; // overhanded per param
	private transient FinCreditRevSubCategoryListCtrl finCreditRevSubCategoryListCtrl; // overhanded per param
	private transient CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl;

	private transient boolean validationOn;
	
	JSONArray variables = new JSONArray();
	HashSet<String> subCategoryCodes = new HashSet<String>();
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

	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	
	/**
	 * default constructor.<br>
	 */
	public FinCreditRevSubCategoryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCreditRevSubCategoryDialog";
	}

	// Component Events

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
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCreditRevSubCategoryDialog);

		try {
			doCheckRights();

			if (arguments.containsKey("finCreditRevSubCategory")) {
				this.finCreditRevSubCategory = (FinCreditRevSubCategory) arguments.get("finCreditRevSubCategory");
				FinCreditRevSubCategory befImage =new FinCreditRevSubCategory();
				BeanUtils.copyProperties(this.finCreditRevSubCategory, befImage);
				this.finCreditRevSubCategory.setBefImage(befImage);
				
				setFinCreditRevSubCategory(this.finCreditRevSubCategory);
			} else {
				setFinCreditRevSubCategory(null);
			}
		
			if(arguments.containsKey("listOfFinCreditRevCategory")){
				listOfFinCreditRevCategory = (List<FinCreditRevCategory>)arguments.get("listOfFinCreditRevCategory");
			}
			
			if(arguments.containsKey("parentRole")){
				role = (String) arguments.get("parentRole");
			}
			
			
			doLoadWorkFlow(this.finCreditRevSubCategory.isWorkflow(),this.finCreditRevSubCategory.getWorkflowId(),this.finCreditRevSubCategory.getNextTaskId());
	
			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinCreditRevSubCategoryDialog");
			}
	
			setListCategoryId();
			doFillListOperators(listboxFeeOperators);
			doFillListOperators(listboxFeeOperators2);
			// READ OVERHANDED params !
			// we get the finCreditRevSubCategoryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete finCreditRevSubCategory here.
			if (arguments.containsKey("finCreditRevSubCategoryListCtrl")) {
				setFinCreditRevSubCategoryListCtrl((FinCreditRevSubCategoryListCtrl) arguments.get("finCreditRevSubCategoryListCtrl"));
			} else {
				setFinCreditRevSubCategoryListCtrl(null);
			}
			
			if (arguments.containsKey("creditApplicationReviewDialogCtrl")) {
				setCreditApplicationReviewDialogCtrl((CreditApplicationReviewDialogCtrl) arguments.get("creditApplicationReviewDialogCtrl"));
				//this.window_FinCreditRevSubCategoryDialog.doModal();
			} else {
				setCreditApplicationReviewDialogCtrl(null);
			}
			
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
			MessageUtil.showError(e);
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
		}else{
			this.groupboxWf.setVisible(false);
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
		
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		
	    // this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryDialog_btnNew"));
		 this.btnEdit.setVisible(true);
		// this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryDialog_btnSave"));
		 this.btnSave.setVisible(true);
		 this.btnCancel.setVisible(false);
		
		logger.debug("Leaving") ;
	}

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
					if (!"Result".equals(variable.get("name"))) {
						if (!subCategoryCodes.contains(variable.get("name"))) {
							// if new variables found throw error message
							noerrors = false;
							MessageUtil.showError("Unknown Variable :" + variable.get("name"));
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
						MessageUtil.showError("Error : At Line " + error.get("line") + ",Position "
								+ error.get("character") + "\n\n" + error.get("reason").toString());
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
		MessageUtil.showHelpWindow(event, window_FinCreditRevSubCategoryDialog);
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
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.finCreditRevSubCategory.getBefImage());
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
		
		if(StringUtils.isNotEmpty(aFinCreditRevSubCategory.getItemsToCal())  && 
				        !"R".equals(aFinCreditRevSubCategory.getRemarks())){
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
		logger.debug("Entering");

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
			/*getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(false);
			getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.getParent().appendChild(this.window_FinCreditRevSubCategoryDialog);*/
			
			this.
			getBorderLayoutHeight();
			setDialog(DialogType.OVERLAPPED);
			
			getUserWorkspace().allocateAuthorities("CreditApplicationReviewDialog",role);
			this.btnSave.setVisible(getUserWorkspace().isAllowed("btn_CreditApplicationReviewDialog_newSubCategory"));
			this.btnDelete.setVisible(false);
			this.btnEdit.setVisible(false);
			
			
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}
	
	
	public void doFillListOperators(Listbox listbox){
		Listitem item;
		Listcell cell;
		List<ValueLabel> listRuleOperators = PennantStaticListUtil.getRuleOperator();
		
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
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	@SuppressWarnings("unused")
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.subCategoryCode.isReadonly()){
			this.subCategoryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryCode.value"),null,true));
		}	
			
		if (!this.subCategoryDesc.isReadonly()){
			this.subCategoryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryDesc.value"),null,true));
		}	
		/*if (!this.subCategoryItemType.isReadonly()){
			this.subCategoryItemType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_SubCategoryItemType.value"),null,true));
		}*/	
		/*if (!this.itemsToCal.isReadonly()){
			this.itemsToCal.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_ItemsToCal.value"),null,true));
		}*/	
		if (!this.itemRule.isReadonly()){
			this.itemRule.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_ItemRule.value"),null,true));
		}	
		/*if (!this.mainSubCategoryCode.isReadonly()){
			this.mainSubCategoryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_mainSubCategoryCode.value"),null,true));
		}*/	
		if (!this.calcSeque.isReadonly()){
			this.calcSeque.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinCreditRevSubCategoryDialog_CalcSeque.value"), true));
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

	// CRUD operations

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
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinCreditRevSubCategory.getRecordType())){
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
					getCreditApplicationReviewDialogCtrl().window_CreditApplicationReviewDialog.setVisible(true);
				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
			
		}
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
						if("R".equals(finCreditRevSubCategory.getRemarks())){
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
		
		aFinCreditRevSubCategory.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinCreditRevSubCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinCreditRevSubCategory.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinCreditRevSubCategory.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinCreditRevSubCategory.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinCreditRevSubCategory);
				}

				if (isNotesMandatory(taskId, aFinCreditRevSubCategory)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			
			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aFinCreditRevSubCategory.setTaskId(taskId);
			aFinCreditRevSubCategory.setNextTaskId(nextTaskId);
			aFinCreditRevSubCategory.setRoleCode(getRole());
			aFinCreditRevSubCategory.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aFinCreditRevSubCategory, tranType);
			
			String operationRefs = getServiceOperations(taskId, aFinCreditRevSubCategory);
			
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
				
				if (StringUtils.isBlank(method)){
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinCreditRevSubCategoryDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_FinCreditRevSubCategoryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.finCreditRevSubCategory),true);
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
			logger.error("Exception: ", e);
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

// ******************************************************//
// ****************** getter / setter *******************//
// ******************************************************//

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
	
	private AuditHeader getAuditHeader(FinCreditRevSubCategory aFinCreditRevSubCategory, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinCreditRevSubCategory.getBefImage(), aFinCreditRevSubCategory);   
		return new AuditHeader(aFinCreditRevSubCategory.getSubCategoryCode(),null,null,null,auditDetail,aFinCreditRevSubCategory.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinCreditRevSubCategoryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}
	
	
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.finCreditRevSubCategory);
	}
	
	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.finCreditRevSubCategory.getSubCategoryCode());
	}

	
	@Override
	protected void doClearMessage() {
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
	private void createSimulationWindow(List<ValueLabel> variablesList) throws InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("fieldsList", variablesList);
		map.put("finCreditRevSubCategoryDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/JavaScriptBuilder/RuleResultView.zul",null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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

		if(StringUtils.isBlank(this.formula.getValue())){
			MessageUtil.showError(Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_Formula") }));
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
		
		if(StringUtils.isBlank(this.formula.getValue())){
			MessageUtil.showError(Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_Formula") }));
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
			List<ValueLabel> variableList =new ArrayList<>();
			String fieldValue="";
			for (int i = 0; i < codeVariables.size(); i++) {

				JSONObject jsonObject = (JSONObject) codeVariables.get(i);
				fieldValue = (String) jsonObject.get("name");
				if(!"Result".equals(fieldValue)){
					variableList.add(new ValueLabel(fieldValue, fieldValue));
				}
			}
			createSimulationWindow(variableList);
		}else{

			if(errors.size() != 0){
				conf = MessageUtil.confirm(errors.size() + PennantJavaUtil.getLabel("message_ErrorCount_CodeMirror"));

				if (conf == MessageUtil.YES) {
					Events.postEvent("onUser$errors", window_RuleResultDialog,errors);
				}else{
					//do Nothing
				}
			}else{
				if(isSaveRecord){
					doSave();
				}else{
					MessageUtil.showMessage(PennantJavaUtil.getLabel("message_NoError_CodeMirror"));
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
		
		if (StringUtils.isEmpty(formula.getValue())) {
			MessageUtil.showError(Labels.getLabel("Code_NotEmpty"));
		} else {
			JSONArray message = (JSONArray) event.getData();

			for (int i = 0; i < message.size(); i++) {

				JSONObject jsonObject = (JSONObject) message.get(i);

				if(jsonObject != null){
					
					String errorMsg = "Error : Line-" + jsonObject.get("line") + ",Character-"
							+ jsonObject.get("character") + "\n\n" + (String) jsonObject.get("reason");
					
					int conf;
					if(message.size()-1 != i+1){
						errorMsg = errorMsg +"\n\n"+
									PennantJavaUtil.getLabel("message_ErrorProcess_Conformation");

						conf = MessageUtil.confirm(errorMsg);
						if (conf == MessageUtil.NO) {
							break;
						}
					}else{
						MessageUtil.showError(errorMsg);
						break;
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setCreditApplicationReviewDialogCtrl(
			CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl) {
		this.creditApplicationReviewDialogCtrl = creditApplicationReviewDialogCtrl;
	}

	public CreditApplicationReviewDialogCtrl getCreditApplicationReviewDialogCtrl() {
		return creditApplicationReviewDialogCtrl;
	}
}
