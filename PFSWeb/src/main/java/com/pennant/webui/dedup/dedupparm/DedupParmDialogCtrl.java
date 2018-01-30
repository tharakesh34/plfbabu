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
 * FileName    		:  DedupParmDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dedup.dedupparm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.LabelElement;

import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.service.dedup.DedupFieldsService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupfields.BuilderUtilListbox;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/DedupParm/dedupParmDialog.zul file.
 */
public class DedupParmDialogCtrl extends GFCBaseCtrl<DedupParm> {
	private static final long serialVersionUID = -3541636402188022162L;
	private static final Logger logger = Logger.getLogger(DedupParmDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_DedupParmDialog; // autoWired
	protected Label 	label_WindowTitle; 		// autoWired

	protected Textbox 	queryCode; 				// autoWired
	protected Textbox 	queryDesc; 				// autoWired
	protected Textbox 	queryModule;			// autoWired
	protected Combobox 	custCtgCode; 			// autoWired
	protected Codemirror sQLQuery; 				// autoWired
	protected Combobox 	combo;
	protected Row 		rowCustCtgCode;
	protected Tabpanel 	tabPanel_tree;
	protected Tabpanel 	tabPanel_QueryResult;
	protected Tab 		tab_queryDesign;
	protected Tab		tab_textQuery;
	
	// Tree Buttons
	protected Button removeButton;
	protected Button addButton;
	protected Button addSubButton;

	// Tool bar Buttons
	protected Button btnReadValues;
	protected Button btnValidation;

	protected Tree tree;
	protected Treechildren treechildren;
	protected Treeitem treeitem;
	protected Treerow treerow;
	protected Space space;

	int itemCount = 1;
	int qryBraketCount = 1;
	String strSqlQuery = "";
	int fldComboSelAtmpts = 1;
	String actualBlob = "";
	boolean isItemSibling = false;
	int parentCount = 1;

	boolean addBtnClicked = false;
	boolean operatorVal = false;
	boolean comboSelected = false;
	private String fieldType = null;
	boolean likeCondition = false;

	// not auto wired vars
	private DedupParm dedupParm; // overhanded per param
	private transient DedupParmListCtrl dedupParmListCtrl; // overhanded per param

	private transient boolean validationOn;

	
	// ServiceDAOs / Domain Classes
	private transient DedupParmService dedupParmService;
	private transient DedupFieldsService dedupFieldsService;

	List<String> sqlQueryValueList = new ArrayList<String>();// list of tree values
	List<Component> itemList = new ArrayList<Component>();// list of items(tree item) in tree
	List<BuilderTable> objectFieldList = new ArrayList<BuilderTable>();// retrieve values from
																		// table--BuilderTables
	LinkedHashMap<String, String[]> queryFieldMap;
	private boolean selectedField = false;
	private String moduleName = "";
	List<ValueLabel> custCategoryList = PennantAppUtil.getcustCtgCodeList();
	
	/**
	 * default constructor.<br>
	 */
	public DedupParmDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DedupParmDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DedupParm object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DedupParmDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DedupParmDialog);

		try {

			if (arguments.containsKey("queryModule")) {
				moduleName = arguments.get("queryModule").toString();
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("dedupParm")) {
				this.dedupParm = (DedupParm) arguments.get("dedupParm");
				DedupParm befImage = new DedupParm();
				BeanUtils.copyProperties(this.dedupParm, befImage);
				this.dedupParm.setBefImage(befImage);
				setDedupParm(this.dedupParm);
			} else {
				setDedupParm(null);
			}

			doLoadWorkFlow(this.dedupParm.isWorkflow(),
					this.dedupParm.getWorkflowId(),
					this.dedupParm.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"DedupParmDialog");
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			getBorderLayoutHeight();
			int tabPanelboxHeight = borderLayoutHeight - 150;
			tabPanel_tree.setHeight(tabPanelboxHeight + "px");
			tree.setHeight(tabPanelboxHeight - 50 + "px");
			sQLQuery.setHeight(tabPanelboxHeight - 50 + "px");
			tabPanel_QueryResult.setHeight(tabPanelboxHeight + "px");

			// READ OVERHANDED params !
			// we get the dedupParmListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete dedupParm here.
			if (arguments.containsKey("dedupParmListCtrl")) {
				if (moduleName.equals(FinanceConstants.DEDUP_CUSTOMER)
						|| moduleName.equals(FinanceConstants.DEDUP_BLACKLIST)
						|| moduleName.equals(FinanceConstants.DEDUP_POLICE) 
						|| moduleName.equals(FinanceConstants.DEDUP_LIMITS)) {
					this.rowCustCtgCode.setVisible(true);
				}
				if (moduleName.equals(FinanceConstants.DEDUP_FINANCE)) {
					this.rowCustCtgCode.setVisible(false);
				}
				setDedupParmListCtrl((DedupParmListCtrl) arguments
						.get("dedupParmListCtrl"));

			} else {
				setDedupParmListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDedupParm());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DedupParmDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.queryCode.setMaxlength(50);
		this.queryDesc.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"New"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Edit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Delete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Save"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering");
		
		doSave();
		
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_DedupParmDialog);
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
		doWriteBeanToComponents(this.dedupParm.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDedupParm
	 *            DedupParm
	 */
	public void doWriteBeanToComponents(DedupParm aDedupParm) {
		logger.debug("Entering");
		if(moduleName.equals(FinanceConstants.DEDUP_CUSTOMER) || moduleName.equals(FinanceConstants.DEDUP_BLACKLIST)
				|| moduleName.equals(FinanceConstants.DEDUP_POLICE) ){			
			fillComboBox(this.custCtgCode,aDedupParm.getQuerySubCode(),this.custCategoryList,"");		
		}else if(moduleName.equals(FinanceConstants.DEDUP_LIMITS)){
			custCategoryList=PennantStaticListUtil.getLimitCategories();			
			fillComboBox(this.custCtgCode,aDedupParm.getQuerySubCode(),this.custCategoryList,"");		
		}
		this.queryModule.setValue(moduleName);
		this.queryCode.setValue(aDedupParm.getQueryCode());
		this.queryDesc.setValue(aDedupParm.getQueryDesc());
		this.sQLQuery.setValue(aDedupParm.getSQLQuery());
		actualBlob = aDedupParm.getActualBlock();
		this.recordStatus.setValue(aDedupParm.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDedupParm
	 * @throws Exception 
	 */
	public void doWriteComponentsToBean(DedupParm aDedupParm) throws Exception {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aDedupParm.setQueryModule(this.queryModule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(moduleName.equals(FinanceConstants.DEDUP_CUSTOMER) || moduleName.equals(FinanceConstants.DEDUP_BLACKLIST)
					|| moduleName.equals(FinanceConstants.DEDUP_POLICE) || moduleName.equals(FinanceConstants.DEDUP_LIMITS)){
				if(!this.custCtgCode.isDisabled() && this.custCtgCode.getSelectedIndex()<1){
					this.sQLQuery.setValue("");
					this.tab_queryDesign.setSelected(true);
					throw new WrongValueException(custCtgCode, Labels.getLabel("STATIC_INVALID",
							new String[]{Labels.getLabel("label_DedupParmDialog_CustCtgCode.value")}));
				}
				aDedupParm.setQuerySubCode(this.custCtgCode.getSelectedItem().getValue().toString());
			}else if(moduleName.equals(FinanceConstants.DEDUP_FINANCE)){
				aDedupParm.setQuerySubCode("L");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDedupParm.setQueryCode(this.queryCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDedupParm.setQueryDesc(this.queryDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(tab_queryDesign.isVisible()){
				readButtonClicked();
			}
			/*if (StringUtils.trimToEmpty(this.sQLQuery.getValue()).equals("")){
				throw new WrongValueException(sQLQuery, Labels.getLabel("label_DedupParmDialog_SQLQuery.value"));
			}*/
			aDedupParm.setSQLQuery(this.sQLQuery.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDedupParm.setActualBlock(actualBlob);
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

		aDedupParm.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDedupParm
	 * @throws Exception
	 */
	public void doShowDialog(DedupParm aDedupParm) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aDedupParm.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.queryCode.focus();
		} else {
			// this.queryModule.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aDedupParm.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aDedupParm);

			// building tree based on condition
			if (StringUtils.isNotBlank(aDedupParm.getActualBlock())) {
				String str = aDedupParm.getActualBlock();
				StringTokenizer st = new StringTokenizer(str, ",");
				while (st.hasMoreTokens()) {
					sqlQueryValueList.add(st.nextToken());
				}
			}

			// Fetches the List of DedupFields
			if (StringUtils.equals(aDedupParm.getQueryModule(), FinanceConstants.DEDUP_FINANCE)) {
				objectFieldList = (List<BuilderTable>) getDedupFieldsService().getFieldList(FinanceConstants.DEDUP_FINANCE);
				// Method for Building tree with /Without existing params
				buildingTree(sqlQueryValueList, aDedupParm);
			}
			
			if (StringUtils.equals(aDedupParm.getQueryModule(), FinanceConstants.DEDUP_CUSTOMER) || StringUtils.equals(aDedupParm.getQueryModule(), FinanceConstants.DEDUP_BLACKLIST)
					|| StringUtils.equals(aDedupParm.getQueryModule(), FinanceConstants.DEDUP_POLICE)) {
				if(!aDedupParm.isNewRecord()){
					objectFieldList = (List<BuilderTable>) getDedupFieldsService().getFieldList(this.custCtgCode.getSelectedItem().getValue().toString()
							+aDedupParm.getQueryModule());
					// Method for Building tree with /Without existing params
					buildingTree(sqlQueryValueList, aDedupParm);
				}
			}
			isItemSibling = false;
			this.label_WindowTitle.setValue(Labels.getLabel("window_"+aDedupParm.getQueryModule()+"_DedupParmDialog.title"));
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DedupParmDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This Method/Event for setting the rule modules and it clears tree if we
	 * select another module and it also prompt the message
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$custCtgCode(Event event) throws Exception {		
		logger.debug("Entering" +event.toString());	

		if(custCtgCode.getSelectedIndex() == 0){
			this.sQLQuery.setValue("");
			tree.getChildren().clear();
		}else{
			
			this.sQLQuery.setValue("");
			this.tab_queryDesign.setSelected(true);
			if(treechildren != null){
				Component comp=(Component)combo.getParent().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
				Combobox cb =new Combobox();
				Textbox tb = new Textbox();
				if(comp instanceof Combobox){
					cb= (Combobox)comp;
				}else if(comp instanceof Textbox){
					tb= (Textbox)comp;
				}
				if(StringUtils.isNotEmpty(cb.getValue()) || StringUtils.isNotEmpty(tb.getValue())){
					final String msg = Labels.getLabel("RuleDialog_message_Data_Modified");

					if (MessageUtil.confirm(msg) == MessageUtil.YES) {
						clearAndBuildTree(this.custCtgCode.getSelectedItem().getValue().toString());
						this.sQLQuery.setValue("");
					}
				}else{
					clearAndBuildTree(this.custCtgCode.getSelectedItem().getValue().toString()); 
				}
			}else{
				clearAndBuildTree(this.custCtgCode.getSelectedItem().getValue().toString());
			}
		}

		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * This method for clearing tree children and build a new tree
	 * @throws Exception
	 */
	public void clearAndBuildTree(String ctgCode) throws Exception{
		logger.debug("Entering");
		itemCount = 1;
		tree.getChildren().clear();
		objectFieldList = new ArrayList<BuilderTable>();
		objectFieldList = (List<BuilderTable>) getDedupFieldsService().getFieldList(
				this.custCtgCode.getSelectedItem().getValue().toString()+getDedupParm().getQueryModule());
		// Method for Building tree with /Without existing params
		buildingTree(sqlQueryValueList, new DedupParm());
		logger.debug("Leaving");
	}

	/**
	 * Method for Build Sibling TreeItem
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAddButtonClicked(Event event) throws Exception {
		logger.debug("Entering");

		this.addBtnClicked = true;
		Component component = (Component) event.getData();

		treeitem = new Treeitem();
		DedupParm aDedupParm = getDedupParm();
		buildTreeCell(sqlQueryValueList, aDedupParm);
		addButtonLogic(component);
		logger.debug("Leaving");
	}

	/**
	 * Build Tree using List of component values or for new tree
	 */
	public void buildingTree(List<String> queryValues, DedupParm aDedupParm) throws Exception {
		logger.debug("Entering");

		Component component1 = (Component) tree;
		itemList.add(component1);
		treechildren = new Treechildren();
		treeitem = new Treeitem();
		// creating tree
		buildTreeCell(queryValues, aDedupParm);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		setValidationOn(true);
		
		if (!this.queryCode.isReadonly()) {
			this.queryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DedupParmDialog_QueryCode.value"),PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.queryDesc.isReadonly()) {
			this.queryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DedupParmDialog_QueryDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Enterring");
		setValidationOn(false);
		this.queryCode.setConstraint("");
		this.queryDesc.setConstraint("");
		this.custCtgCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		if (!this.custCtgCode.isReadonly()) {
			this.custCtgCode.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_CustomerCategoryDialog_CustCtgCode.value"), null, true));
		}
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.queryCode.setErrorMessage("");
		this.queryDesc.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDedupParmListCtrl().search();
	}

	/**
	 * Method for generating List of Fields
	 * 
	 * @return
	 */
	private Combobox getFields() {
		logger.debug("Entering");

		Combobox comboBox = new Combobox();
		Comboitem item;
		for (int i = 0; i < objectFieldList.size(); i++) {
			BuilderTable builderTables = (BuilderTable) objectFieldList.get(i);
			item = new Comboitem();
			item.setLabel(builderTables.getFieldDesc());
			item.setValue(builderTables.getFieldName());
			comboBox.appendChild(item);
		}

		comboBox.addForward("onSelect", window_DedupParmDialog, "onComboFieldSelected", comboBox);
		comboBox.setWidth("190px");

		logger.debug("Leaving");
		return comboBox;
	}

	/**
	 * Method for selection Event on Field
	 * 
	 * @param event
	 */
	public void onComboFieldSelected(Event event) {
		logger.debug("Entering");

		Component component = (Component) event.getData();
		String val = ((Combobox) component).getSelectedItem().getValue().toString();

		Component selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();
		Component posCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();

		if (fldComboSelAtmpts > 1) {
			if (this.selectedField) {
				selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
				posCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();
				if (selectCmp instanceof Space) {
					selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();
					posCmp = component.getParent().getLastChild().getPreviousSibling();
					if (selectCmp instanceof Button) {
						selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
						posCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();
					}
				}
			} else {
				selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
				posCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling();
			}
			component.getParent().removeChild(selectCmp);
			fldComboSelAtmpts = 1;
		}

		for (int j = 0; j < objectFieldList.size(); j++) {
			BuilderTable builderTables = objectFieldList.get(j);
			if (builderTables.getFieldName().equals(val)) {
				setFieldType(builderTables.getFieldControl());
			}
		}
		// Set the field type of selected field
		if (!"nvarchar".equalsIgnoreCase(getFieldType())) {
			this.operatorVal = true;
		} else {
			this.operatorVal = false;
		}
		component.getParent().insertBefore(getCriteria(), posCmp);

		fldComboSelAtmpts++;
		logger.debug("Leaving");
	}

	/**
	 * Method for creating list of Criteria Conditions
	 */
	private Combobox getCondition() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		Comboitem item;
		for (int i = 0; i < BuilderUtilListbox.getLogicModel().getSize(); i++) {
			item = new Comboitem();
			item.setLabel(BuilderUtilListbox.getLogicModel().getElementAt(i).toString());
			item.setValue(BuilderUtilListbox.getLogicModel().getElementAt(i).toString());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("50px");
		logger.debug("Leaving");
		return comboBox;
	}

	/**
	 * Method for creating list of Logical Operators
	 * 
	 */
	private Combobox getCriteria() {
		logger.debug("Entering");

		Combobox combobox = new Combobox();
		Comboitem item;
		if (!operatorVal) {
			for (int i = 0; i < BuilderUtilListbox.operatorLabel().getSize(); i++) {
				item = new Comboitem();
				item.setLabel(BuilderUtilListbox.operatorLabel().getElementAt(i).toString());
				item.setValue(BuilderUtilListbox.operatorValue().getElementAt(i).toString());
				combobox.appendChild(item);
			}
			combobox.setSelectedItem(combobox.getItemAtIndex(0));
		} else {
			combobox.getItems().clear();
			item = new Comboitem();
			item.setLabel(BuilderUtilListbox.operatorLabel().getElementAt(0).toString());
			item.setValue(BuilderUtilListbox.operatorValue().getElementAt(0).toString());
			combobox.appendChild(item);
			combobox.setSelectedItem(combobox.getItemAtIndex(0));
			operatorVal = false;
		}
		combobox.setWidth("160px");
		combobox.setReadonly(true);

		logger.debug("Leaving");
		return combobox;
	}

	/**
	 * Logic for creating sibling treeItem
	 * 
	 * @param cmp
	 * @throws InterruptedException
	 */
	public void addButtonLogic(Component cmp) throws InterruptedException {
		logger.debug("Entering");

		treeitem.appendChild(treerow);
		Component parent;

		if (!isItemSibling) {
			parent = cmp.getParent();
		} else {
			parent = (Component) itemList.get(itemList.size() - 2);
		}

		final String msg = Labels.getLabel("message.information.only6Rows");

		// TODO Here 6 is hard coded to Restrict the No of Conditions which has
		// to be Parameterized
		if (parent.getChildren().size() < 6) {

			if (cmp.getNextSibling() != null) {
				parent.insertBefore(treeitem, cmp.getNextSibling());
			} else {
				if ((parent instanceof Tree) && parentCount == 1) {
					if (parent.getChildren().get(0) instanceof Treechildren) {
						((Component) parent.getChildren().get(0)).appendChild(treeitem);
					} else {
						parent.appendChild(treeitem);
						parentCount++;
					}
				} else if (!isItemSibling) {
					parent.appendChild(treeitem);
				}
			}
		} else {
			MessageUtil.showMessage(msg);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for adding child treeItem calling Component
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAddSubButtonClicked(Event event) throws Exception {
		logger.debug("Entering");

		this.addBtnClicked = false;
		Component cmp = (Component) event.getData();
		treeitem = new Treeitem();
		DedupParm aDedupParm = getDedupParm();
		buildTreeCell(sqlQueryValueList, aDedupParm);
		addSubButtonLogic(cmp);
		logger.debug("Leaving");
	}

	/**
	 * Logic for creating Child treeItem
	 * 
	 * @param cmp
	 * @throws InterruptedException
	 */
	public void addSubButtonLogic(Component cmp) throws InterruptedException {
		logger.debug("Entering");

		Component nextcomp = null;
		treeitem.appendChild(treerow);
		int length = 1;
		int found = 0;
		boolean childReq = false;
		List<Component> treeChild = cmp.getChildren();
		final String msg = Labels.getLabel("message.information.only3Rows");

		for (int i = 0; i < treeChild.size(); i++) {
			if (treeChild.get(i) instanceof Treechildren) {
				treechildren = (Treechildren) treeChild.get(i);
				List<Component> child = treechildren.getChildren();
				// TODO Here 3 is hard coded to Restrict the No of Conditions
				// which has to be Parameterized
				if (child.size() < 3) {
					for (int j = 0; j < child.size(); j++) {

						if (child.get(j) instanceof Treeitem) {
							found = 1;
							if (length == 1) {
								nextcomp = (Component) child.get(j);
							}
							length = length + 1;
						} else {
							childReq = false;
						}

						if (nextcomp != null && !isItemSibling) {
							treechildren.appendChild(treeitem);
							//treechildren.insertBefore(treeitem, nextcomp);//remove comment if child add in stack format
							addSubButton.setVisible(false); // Added
							addButton.setVisible(false); // Added
							break;
						} else if (isItemSibling) {
							treechildren.appendChild(treeitem);
							addSubButton.setVisible(false); // Added
						}
					}
					// TODO Here 3 is hard coded to Restrict the No of Conditions
					// which has to be Parameterized
				}else if (child.size() == 3) {
					MessageUtil.showMessage(msg);
					childReq = true;
				}
			} else {
				childReq = false;
			}
		}

		if (found == 0 && !childReq) {

			treechildren = new Treechildren();
			treechildren.appendChild(treeitem);
			cmp.appendChild(treechildren);
			addSubButton.setVisible(false); // Added
			addButton.setVisible(false); // Added
		}
		logger.debug("Leaving");
	}

	// Method for Event on clicking Remove Button
	public void onButtonClicked(Event event) {
		logger.debug("Entering");

		Component childComp = (Component) event.getData();
		List<Component> child = childComp.getParent().getChildren();
		if (child.size() > 1) {
			childComp.detach();
		} else {
			childComp.getParent().detach();
		}
		logger.debug("Leaving");
	}

	/**
	 * Common code for generating treeCell
	 * 
	 * @param queryValues
	 * @param aQuery
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void buildTreeCell(List<String> queryValues, DedupParm aDedupParm) throws Exception {
		logger.debug("Entering");

		// Add treeItem to Component itemList
		Component component = (Component) treeitem;
		String condition = "";
		itemList.add(component);

		// create Buttons and Add Forward Events to buttons
		removeButton = new Button();
		removeButton.setTooltiptext("remove Query Condition");
		removeButton.setImage("/images/icons/delete.png");
		removeButton.setStyle("padding:0px");

		addButton = new Button();
		addButton.setTooltiptext("Add sibling Query Condition");
		addButton.setImage("/images/icons/add.png");
		addButton.setStyle("padding:0px");

		addSubButton = new Button();
		addSubButton.setTooltiptext("Add Child Query Condition");
		addSubButton.setImage("/images/icons/extadd.png");
		addSubButton.setStyle("padding:0px");

		removeButton.addForward("onClick", window_DedupParmDialog, "onButtonClicked", treeitem);

		// Create treeCell with in item of tree
		treerow = new Treerow();
		Treecell treecell = new Treecell();
		Hlayout hbox = new Hlayout();

		// Add left padding to TreeCell
		space = new Space();
		space.setSpacing("25px");
		hbox.appendChild(space);
		hbox.appendChild(removeButton);

		// Condition for not generate Criteria comboBox or not
		if (itemCount != 1) {

			space = new Space();
			hbox.appendChild(space);
			// textBox = getCondition();
			combo = getCondition();

			if (queryValues.size() > 0 && !aDedupParm.isNew()) {
				if ("AND".equalsIgnoreCase(queryValues.get(0)) || "OR".equalsIgnoreCase(queryValues.get(0))) {
					for (int i = 0; i < combo.getItemCount(); i++) {
						condition = (String) combo.getItemAtIndex(i).getValue();
						if (combo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
							combo.setSelectedIndex(i);
							queryValues.remove(0);
							break;
						}
					}
				}
			} else {
				if (this.addBtnClicked) {
					combo.setSelectedItem(combo.getItemAtIndex(1));
				} else {
					combo.setSelectedItem(combo.getItemAtIndex(0));
				}
			}
			hbox.appendChild(combo);

		} else {
			space = new Space();
			space.setSpacing("63px");
			hbox.appendChild(space);
		}

		space = new Space();
		hbox.appendChild(space);
		combo = getFields();// first comb box
		combo.setReadonly(true);

		// Set field to ComboBox depend on fieldList and getting query
		if (queryValues.size() > 0 && !aDedupParm.isNew()) {
			this.selectedField = true;
			this.fldComboSelAtmpts++;
			queryValues.remove(0);
			for (int i = 0; i < combo.getItemCount(); i++) {
				if (combo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
					combo.setSelectedIndex(i);
					for (int j = 0; j < objectFieldList.size(); j++) {
						BuilderTable builderTables = objectFieldList.get(j);
						String str = combo.getSelectedItem().getValue().toString();
						StringTokenizer st = new StringTokenizer(str, ".");
						while (st.hasMoreTokens()) {
							if (builderTables.getFieldName().equals(st.nextToken())) {
								setFieldType(builderTables.getFieldControl());
								break;
							}
						}
					}
					queryValues.remove(0);
					break;
				}
			}
		}
		hbox.appendChild(combo);

		space = new Space();
		hbox.appendChild(space);

		// Set Logical operators for query
		if (queryValues.size() > 0 && !aDedupParm.isNew()) {
			// Set the field type of selected field
			if (!"nvarchar".equalsIgnoreCase(getFieldType())) {
				this.operatorVal = true;
			} else {
				this.operatorVal = false;
			}
			combo = getCriteria();
			this.comboSelected = true;
			for (int i = 0; i < combo.getItemCount(); i++) {
				if (combo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
					combo.setSelectedIndex(i);
					queryValues.remove(0);
					break;
				}
			}
			combo.setReadonly(true);
			hbox.appendChild(combo);
		}

		if (queryValues.size() > 0 && !aDedupParm.isNew()) {

			if ("AND".equals(condition)) {
				space = new Space();
				hbox.appendChild(space);
				hbox.appendChild(addSubButton);
			} else {
				hbox.appendChild(addButton);
				space = new Space();
				hbox.appendChild(space);
				hbox.appendChild(addSubButton);
			}
		} else {
			hbox.appendChild(addButton);
			space = new Space();
			hbox.appendChild(space);
			hbox.appendChild(addSubButton);
		}

		hbox.setStyle("display: inline-block; _display: inline; padding:0px;width:1000px;height:20px;");
		treecell.setHflex("100");
		treecell.appendChild(hbox);
		treerow.appendChild(treecell);

		addButton.addForward("onClick", window_DedupParmDialog, "onAddButtonClicked", treeitem);

		addSubButton.addForward("onClick", window_DedupParmDialog, "onAddSubButtonClicked", treeitem);

		// Setting for generating tree item for new query when creating window
		if (itemCount == 1) {
			removeButton.setDisabled(true);
			treeitem.appendChild(treerow);
			treeitem.setStyle("padding:1px");
			treechildren.appendChild(treeitem);
			tree.appendChild(treechildren);
			tree.setSizedByContent(true);
			isItemSibling = false;
		}

		// condition structure of tree for editing logic
		if (qryBraketCount > 1) {
			if (itemList.size() > 1) {
				component = (Component) itemList.get(itemList.size() - 2);
			} else {
				component = (Component) itemList.get(itemList.size() - 1);
			}
			addSubButtonLogic(component);
		} else if (isItemSibling) {
			if (itemList.size() > 1) {
				component = (Component) itemList.get(itemList.size() - 1);
			} else {
				component = (Component) itemList.get(itemList.size());
			}
			addButtonLogic(component);
		}

		itemCount++;
		// Continue the process of building if size is not zero
		if (queryValues.size() > 0 && !aDedupParm.isNew()) {

			System.out.println("THE QUERY VALUES " + queryValues.get(0));

			if ((")").equals(queryValues.get(0))) {
				int size = queryValues.size();
				for (int i = 0; i < size; i++) {
					if ((")").equals(queryValues.get(0))) {
						queryValues.remove(0);
						itemList.remove(itemList.size() - 1);
						qryBraketCount--;
					} else {
						break;
					}
				}

				// Condition for add sibling item
				if (queryValues.size() != 0) {
					treeitem = new Treeitem();
					qryBraketCount++;
					isItemSibling = true;
					buildTreeCell(sqlQueryValueList, aDedupParm);
				}

			} else if (("(").equals(queryValues.get(0))) {// Condition for adding
				// child item
				queryValues.remove(0);
				qryBraketCount++;
				if (queryValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					buildTreeCell(sqlQueryValueList, aDedupParm);
				}
			} else {
				if (queryValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					qryBraketCount++;
					buildTreeCell(sqlQueryValueList, aDedupParm);
				}
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * build a query for values and generate in textBox
	 */
	public void onClick$btnReadValues(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.tab_queryDesign.setSelected(true);
		readButtonClicked();// calling read button method for generating query
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Validating Builded Query and show result in
	 * 'SqlViewResult.zul'
	 */
	public void onClick$btnValidation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.tab_queryDesign.setSelected(true);
		if(this.custCtgCode.getSelectedIndex() ==0){
			this.sQLQuery.setValue("");
		}else{
			readButtonClicked();// calling read button method for generating query
		}
		String resultQuery = "";
		if (moduleName.equals(FinanceConstants.DEDUP_CUSTOMER) || moduleName.equals(FinanceConstants.DEDUP_BLACKLIST)
				 || moduleName.equals(FinanceConstants.DEDUP_POLICE) || moduleName.equals(FinanceConstants.DEDUP_LIMITS)) {
			resultQuery = "select "+ PennantConstants.CUST_DEDUP_LIST_FIELDS+" from CustomersDedup_View";	
			if(!this.custCtgCode.isDisabled() && this.custCtgCode.getSelectedIndex()<1){
				throw new WrongValueException(custCtgCode, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_DedupParmDialog_CustCtgCode.value")}));
			}
		}else if(moduleName.equals(FinanceConstants.DEDUP_FINANCE)){
			resultQuery = "select * from FinanceDedup_View ";	
		}
		
		resultQuery = resultQuery + "\n" + sQLQuery.getValue();
		//List Checking
		String toReplace="("+PennantConstants.CUST_DEDUP_LISTFILED2+" = :"+PennantConstants.CUST_DEDUP_LISTFILED2+" and "
		+PennantConstants.CUST_DEDUP_LISTFILED3+"=:"+PennantConstants.CUST_DEDUP_LISTFILED3+")";
		if (resultQuery.contains(PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL)) {
			resultQuery=resultQuery.replace(PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL,toReplace );
		}else if (resultQuery.contains(PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE)) {
			resultQuery=resultQuery.replace(PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE,toReplace );
		}
		
		if (moduleName.equals(FinanceConstants.DEDUP_CUSTOMER) || moduleName.equals(FinanceConstants.DEDUP_BLACKLIST)
				 || moduleName.equals(FinanceConstants.DEDUP_POLICE) || moduleName.equals(FinanceConstants.DEDUP_LIMITS)) {
			resultQuery=resultQuery+" and lovDescCustCtgType='"+this.custCtgCode.getSelectedItem().getValue().toString()+"'";
		}
		
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultQuery", resultQuery);
		map.put("fields", queryFieldMap);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/DedupParm/SqlViewResult.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for building SQLquery
	 * 
	 * @throws Exception
	 */
	public void readButtonClicked() throws Exception {
		logger.debug("Entering");
		sQLQuery.setValue("");
		queryFieldMap = new LinkedHashMap<String, String[]>();
		strSqlQuery = "Where(" + "\n" + "(";
		actualBlob = "(" + ",";
		doBuildQuery(tree);// generate Query
		strSqlQuery = strSqlQuery + ")"; // Added
		sQLQuery.setValue(strSqlQuery);
		this.tab_textQuery.setSelected(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for Generating Query by using Builded Structure of Tree
	 * 
	 * @param selectionComp
	 * @throws Exception
	 */
	public void doBuildQuery(Component selectionComp) throws Exception {
		logger.debug("Entering");

		if (selectionComp.getChildren() != null) {
			int comboCount = 1;
			String element = "";
			String elementDesc = "";
			for (int i = 0; i < selectionComp.getChildren().size(); i++) {
				if (selectionComp.getChildren().get(i) instanceof Combobox) {
					Component combo = (Combobox) selectionComp.getChildren().get(i);

					// Validate getting value from 'combo' is related to
					// comboBox or not
					boolean validate = false;
					String value = ((Combobox) combo).getValue();
					if (value == null || StringUtils.isEmpty(value)) {
						validate = false;
					} else {
						for (int i1 = 0; i1 < ((Combobox) combo).getChildren().size(); i1++) {
							if (value.equals(((LabelElement) ((Combobox) combo).getChildren().get(i1)).getLabel())) {
								validate = true;
								break;
							}
						}
					}
					if (!validate) {
						this.tab_queryDesign.setSelected(true);
						throw new WrongValueException(combo, Labels.getLabel("STATIC_INVALID", new String[]{""}));
					}
					String comboValue = ((Combobox) combo).getSelectedItem().getValue().toString();
					// retrieve values from comboBox(s)
					if ("AND".equalsIgnoreCase(comboValue)
							|| "OR".equalsIgnoreCase(comboValue)) {

						strSqlQuery = strSqlQuery + "\n" + comboValue + "\n" + "(";

						actualBlob = actualBlob + comboValue + "," + "(" + ",";
					} else if (comboCount == 2) {
						strSqlQuery = strSqlQuery + comboValue;
						if("LIKE".equalsIgnoreCase(comboValue.trim())){
							likeCondition = true;
						}
						actualBlob = actualBlob + comboValue + ",";
						comboCount++;
					} else {
						if (comboCount == 1) {
							element = comboValue;
							elementDesc = ((Combobox) combo).getSelectedItem().getLabel();
							for (int j = 0; j < objectFieldList.size(); j++) {
								BuilderTable builderTables = objectFieldList.get(j);
								if (builderTables.getFieldName().equals(element)) {
									setFieldType(builderTables.getFieldControl());// To Get the Type of selectingField
									if (("Where(\n(").equals(strSqlQuery) || (strSqlQuery.endsWith("AND\n(") || strSqlQuery.endsWith("OR\n("))) {
										strSqlQuery = strSqlQuery + element;
									}
									actualBlob = actualBlob + comboValue + ",";
									break;
								}
							}
						}
						comboCount++;
					}
				} else if (comboCount == 3) {
					if(likeCondition){
						strSqlQuery = strSqlQuery + ":"+ element;
						likeCondition = false;
					}else{
						strSqlQuery = strSqlQuery + ":" + element ;
						if(StringUtils.contains(getFieldType(),"varchar")){
							if(App.DATABASE == Database.SQL_SERVER){
								strSqlQuery = strSqlQuery + " AND ("+element +" IS NOT NULL AND "+element +" != '') ";
							}else{
								strSqlQuery = strSqlQuery + " AND "+element +" IS NOT NULL ";
							}
						}
					}
					
					if (!queryFieldMap.containsKey(element)) {
						String[] array = new String[2];
						array[0] = getFieldType();
						array[1] = elementDesc;
						queryFieldMap.put(element, array);
					}
					comboCount++;
				} else {
					// repeating the logic for getting values from components
					doBuildQuery((Component) selectionComp.getChildren().get(i));
				}
			}
		}
		// Generating Structure for build Condition
		if (selectionComp instanceof Treeitem) {
			strSqlQuery = strSqlQuery + " " + ")";
			actualBlob = actualBlob + ")" + ",";
		}
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a DedupParm object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final DedupParm aDedupParm = new DedupParm();
		BeanUtils.copyProperties(getDedupParm(), aDedupParm);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_DedupParmDialog_QueryCode.value")+" : "+aDedupParm.getQueryCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDedupParm.getRecordType())) {
				aDedupParm.setVersion(aDedupParm.getVersion() + 1);
				aDedupParm.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDedupParm.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDedupParm, tranType)) {
					refreshList();
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

		if (getDedupParm().isNewRecord()) {
			this.queryCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.custCtgCode.setDisabled(false);
		} else {
			this.queryCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.custCtgCode.setDisabled(true);
			
			if(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Maintain") &&
					getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Save")){
				this.btnReadValues.setVisible(true);
				this.btnValidation.setVisible(true);
				this.tab_queryDesign.setVisible(true);
			}else{
				this.btnReadValues.setVisible(false);
				this.btnValidation.setVisible(false);
				this.tab_queryDesign.setVisible(false);
			}
			
			if(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Deletion") && 
					getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Delete")){
				this.btnDelete.setVisible(true);
			}else{
				this.btnDelete.setVisible(false);
			}
			
			if (PennantConstants.RECORD_TYPE_DEL.equals(getDedupParm()
					.getRecordType())) {
				this.tab_queryDesign.setVisible(false);
				this.btnReadValues.setVisible(false);
				this.btnValidation.setVisible(false);
				if(!getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Maintain")){
					this.btnSave.setVisible(true);
					if(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Deletion")){
						this.btnDelete.setVisible(false);
					}
				}else{
					this.btnSave.setVisible(false);
					this.btnDelete.setVisible(false);
					if(getUserWorkspace().isAllowed("button_DedupParmDialog_btn"+this.moduleName+"Deletion")){
						this.btnSave.setVisible(true);
					}
				}
			}
		}
		this.sQLQuery.setReadonly(true);
		this.queryDesc.setReadonly(isReadOnly("DedupParmDialog_queryDesc"));
		
		if(this.tab_queryDesign.isVisible()){
			this.tab_queryDesign.setSelected(true);
		}else{
			this.tab_textQuery.setSelected(true);
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.dedupParm.isNewRecord()) {
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

		this.queryCode.setReadonly(true);
		this.queryDesc.setReadonly(true);
		this.sQLQuery.setReadonly(true);

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
		this.queryCode.setValue("");
		this.queryDesc.setValue("");
		this.sQLQuery.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Enterring");

		final DedupParm aDedupParm = new DedupParm();
		BeanUtils.copyProperties(getDedupParm(), aDedupParm);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DedupParm object with the components data
		doWriteComponentsToBean(aDedupParm);
		
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDedupParm.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDedupParm.getRecordType())) {
				aDedupParm.setVersion(aDedupParm.getVersion() + 1);
				if (isNew) {
					aDedupParm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDedupParm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDedupParm.setNewRecord(true);
				}
			}
		} else {
			aDedupParm.setVersion(aDedupParm.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aDedupParm, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aDedupParm
	 *            (DedupParm)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(DedupParm aDedupParm, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDedupParm.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDedupParm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDedupParm.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDedupParm.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDedupParm.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDedupParm);
				}

				if (isNotesMandatory(taskId, aDedupParm)) {
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

			aDedupParm.setTaskId(taskId);
			aDedupParm.setNextTaskId(nextTaskId);
			aDedupParm.setRoleCode(getRole());
			aDedupParm.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDedupParm, tranType);
			String operationRefs = getServiceOperations(taskId, aDedupParm);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDedupParm, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDedupParm, tranType);
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
		DedupParm aDedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getDedupParmService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getDedupParmService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getDedupParmService().doApprove(auditHeader);
						if (aDedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDedupParmService().doReject(auditHeader);
						if (aDedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DedupParmDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_DedupParmDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.dedupParm), true);
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
	 * @param aCurrency
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DedupParm aDedupParm, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDedupParm.getBefImage(), aDedupParm);
		return new AuditHeader(aDedupParm.getQueryCode(), null, null, null, auditDetail, aDedupParm.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_DedupParmDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", e);
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
		doShowNotes(this.dedupParm);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.dedupParm.getQueryCode());
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

	public DedupParm getDedupParm() {
		return this.dedupParm;
	}

	public void setDedupParm(DedupParm dedupParm) {
		this.dedupParm = dedupParm;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public DedupParmService getDedupParmService() {
		return this.dedupParmService;
	}

	public DedupFieldsService getDedupFieldsService() {
		return dedupFieldsService;
	}

	public void setDedupFieldsService(DedupFieldsService dedupFieldsService) {
		this.dedupFieldsService = dedupFieldsService;
	}

	public void setDedupParmListCtrl(DedupParmListCtrl dedupParmListCtrl) {
		this.dedupParmListCtrl = dedupParmListCtrl;
	}

	public DedupParmListCtrl getDedupParmListCtrl() {
		return this.dedupParmListCtrl;
	}

	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}
