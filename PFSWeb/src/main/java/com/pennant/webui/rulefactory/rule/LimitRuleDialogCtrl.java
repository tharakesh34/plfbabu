/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LimitRuleDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified
 * Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rulefactory.rule;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.LimitFldCriterias;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rulefactory.impl.LimitRuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupfields.BuilderUtilListbox;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/LimitFilterQuery/dedupParmDialog.zul file.
 */
public class LimitRuleDialogCtrl extends GFCBaseCtrl<LimitFilterQuery> implements Serializable {
	private static final long serialVersionUID = -3541636402188022162L;
	private static final Logger logger = LogManager.getLogger(LimitRuleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LimitRuleDialog; // autoWired
	protected Label label_WindowTitle; // autoWired

	protected Textbox queryCode; // autoWired
	protected Textbox queryDesc; // autoWired
	protected Textbox queryModule; // autoWired
	// protected Combobox custCtgCode; // autoWired
	protected Codemirror sQLQuery; // autoWired
	protected Combobox combo;
	protected Row rowCustCtgCode;
	protected Tabpanel tabPanel_tree;
	protected Tabpanel tabPanel_QueryResult;
	protected Tab tab_queryDesign;
	protected Tab tab_textQuery;
	protected Checkbox active; // autoWired
	protected Space space_Active;

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
	private int mapCount = 1;

	boolean addBtnClicked = false;
	boolean operatorVal = false;
	boolean comboSelected = false;
	private String fieldType = null;
	private int fieldLength = 0;
	boolean likeCondition = false;

	// not auto wired vars
	private LimitFilterQuery limitRule; // overhanded per param
	private transient LimitRuleListCtrl limitRuleListCtrl; // overhanded per param

	// ServiceDAOs / Domain Classes
	private transient LimitRuleService limitRuleService;
	private transient PagedListService pagedListService;

	private List<LimitFldCriterias> operatorsList;
	// For Changes in comboBox's depend on Selection Types in Any ComboBox
	private Map<String, String> fieldTypeMap = new HashMap<String, String>();
	private Map<String, String> selectionTypeMap = new HashMap<String, String>();
	private Map<String, String> dbTableMap = new HashMap<String, String>();
	private Map<String, Object> fieldObjectMap = new HashMap<String, Object>();

	private List<GlobalVariable> globalVariableList = new ArrayList<GlobalVariable>();// retrieve values from
																						// table--GlobalVariable
	List<String> sqlQueryValueList = new ArrayList<String>();// list of tree values
	List<Component> itemList = new ArrayList<Component>();// list of items(tree item) in tree
	List<BMTRBFldDetails> objectFieldList = new ArrayList<BMTRBFldDetails>();// retrieve values from

	LinkedHashMap<String, String[]> queryFieldMap;
	protected Button calValueButton; // autowired
	private String moduleName = "";
	List<ValueLabel> custCategoryList = PennantAppUtil.getcustCtgCodeList();

	/**
	 * default constructor.<br>
	 */
	public LimitRuleDialogCtrl() {
		super();
	}

	// Component Events
	@Override
	protected void doSetProperties() {
		super.pageRightName = "RuleDialog";
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected LimitFilterQuery object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LimitRuleDialog(Event event) {
		logger.debug("Entering" + event.toString());

		try {
			setPageComponents(window_LimitRuleDialog);
			this.limitRule = (LimitFilterQuery) arguments.get("LimitParam");

			this.moduleName = (String) arguments.get("queryModule");
			this.limitRuleListCtrl = (LimitRuleListCtrl) arguments.get("limitRuleListCtrl");

			// Store the before image.
			LimitFilterQuery limitRule = new LimitFilterQuery();
			BeanUtils.copyProperties(this.limitRule, limitRule);
			this.limitRule.setBefImage(limitRule);

			if (this.limitRule == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			/* set components visible dependent of the users rights */

			doLoadWorkFlow(this.limitRule.isWorkflow(), this.limitRule.getWorkflowId(), this.limitRule.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			getBorderLayoutHeight();
			int tabPanelboxHeight = borderLayoutHeight - 150;
			tabPanel_tree.setHeight(tabPanelboxHeight + "px");
			tree.setHeight(tabPanelboxHeight - 80 + "px");
			sQLQuery.setHeight(tabPanelboxHeight - 80 + "px");
			tabPanel_QueryResult.setHeight(tabPanelboxHeight + "px");

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getLimitFilterQuery());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LimitRuleDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.queryCode.setMaxlength(8);
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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleEdit"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleSave"));
			this.btnValidation.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleSave"));
		}
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doSave();
		} catch (WrongValueException exp) {
			logger.error(exp);
			throw exp;
		} catch (WrongValuesException exp) {
			logger.error(exp);
			throw exp;
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
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
		MessageUtil.showHelpWindow(event, window_LimitRuleDialog);
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
		if (!active.isDisabled())
			doDelete();
		else {
			MessageUtil.showError(
					Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { getLimitFilterQuery().getQueryCode() }));
		}
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
	 * @param event An event sent to the event handler of a component.
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
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLimitRule LimitFilterQuery
	 */
	public void doWriteBeanToComponents(LimitFilterQuery aLimitRule) {
		logger.debug("Entering");

		custCategoryList = PennantStaticListUtil.getLimitCategories();
		// fillComboBox(this.custCtgCode,aLimitRule.getQuerySubCode(),this.custCategoryList,"");

		this.queryModule.setValue(moduleName);
		this.queryCode.setValue(aLimitRule.getQueryCode());
		this.queryDesc.setValue(aLimitRule.getQueryDesc());
		this.sQLQuery.setValue(aLimitRule.getSQLQuery());
		this.active.setChecked(aLimitRule.isActive());
		actualBlob = aLimitRule.getActualBlock();
		this.recordStatus.setValue(aLimitRule.getRecordStatus());
		if (aLimitRule.getQueryModule() == null) {
			aLimitRule.setQueryModule(this.queryModule.getValue());
		}
		if (aLimitRule.isNewRecord())
			this.active.setChecked(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLimitRule
	 */
	public void doWriteComponentsToBean(LimitFilterQuery aLimitRule) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aLimitRule.setQueryModule(this.queryModule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLimitRule.setQueryCode(this.queryCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLimitRule.setQueryDesc(this.queryDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aLimitRule.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (tab_queryDesign.isVisible()) {
				readButtonClicked();
			}
			if (StringUtils.trimToEmpty(this.sQLQuery.getValue()).equals("")) {
				throw new WrongValueException(sQLQuery, Labels.getLabel("label_LimitRuleDialog_SQLQuery.value"));
			}
			aLimitRule.setSQLQuery(this.sQLQuery.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLimitRule.setActualBlock(actualBlob);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aLimitRule.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLimitRule
	 */
	public void doShowDialog(LimitFilterQuery aLimitRule) {
		logger.debug("Entering");

		// if aLimitRule == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aLimitRule == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aLimitRule = getLimitRuleService().getNewLimitRule();
			setLimitFilterQuery(aLimitRule);
		} else {
			setLimitFilterQuery(aLimitRule);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aLimitRule.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.queryCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aLimitRule.getRecordType()).equals("") && !enqiryModule) {
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
			doWriteBeanToComponents(aLimitRule);

			// getting values from table---GlobalVariable
			JdbcSearchObject<GlobalVariable> searchObj2 = new JdbcSearchObject<GlobalVariable>(GlobalVariable.class);
			globalVariableList = this.pagedListService.getBySearchObject(searchObj2);

			// getting List of Operators depend on FieldType and SelectionType
			operatorsList = getLimitRuleService().getOperatorsList();

			// building tree based on condition
			if (!StringUtils.trimToEmpty(aLimitRule.getActualBlock()).equals("")) {
				String str = aLimitRule.getActualBlock();
				StringTokenizer st = new StringTokenizer(str, "|");
				while (st.hasMoreTokens()) {
					sqlQueryValueList.add(st.nextToken());
				}
			}
			// Fetches the List of DedupFields
			objectFieldList = (List<BMTRBFldDetails>) getLimitRuleService().getFieldList(RuleConstants.MODULE_IRLFILTER,
					RuleConstants.EVENT_BANK);
			// Method for Building tree with /Without existing params
			buildingTree(sqlQueryValueList, aLimitRule);

			isItemSibling = false;

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_LimitRuleDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * This method for clearing tree children and build a new tree
	 */
	public void clearAndBuildTree(String ctgCode) {
		logger.debug("Entering");
		itemCount = 1;
		tree.getChildren().clear();
		objectFieldList = new ArrayList<BMTRBFldDetails>();
		objectFieldList = (List<BMTRBFldDetails>) getLimitRuleService()
				.getFieldList(getLimitFilterQuery().getQueryModule(), getLimitFilterQuery().getQuerySubCode());
		// Method for Building tree with /Without existing params
		buildingTree(sqlQueryValueList, new LimitFilterQuery());
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * Method for Build Sibling TreeItem
	 * 
	 * @param event
	 */
	public void onAddButtonClicked(Event event) {
		logger.debug("Entering");

		this.addBtnClicked = true;
		Component component = (Component) event.getData();

		treeitem = new Treeitem();
		LimitFilterQuery aLimitRule = getLimitFilterQuery();
		buildTreeCell(sqlQueryValueList, aLimitRule);
		addButtonLogic(component);
		logger.debug("Leaving");
	}

	/**
	 * Build Tree using List of component values or for new tree
	 */
	public void buildingTree(List<String> queryValues, LimitFilterQuery aLimitRule) {
		logger.debug("Entering");

		Component component1 = (Component) tree;
		itemList.add(component1);
		treechildren = new Treechildren();
		treeitem = new Treeitem();
		// creating tree
		buildTreeCell(queryValues, aLimitRule);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		if (!this.queryCode.isReadonly()) {
			this.queryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_LimitParmDialog_QueryCode.value"),
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.queryDesc.isReadonly()) {
			this.queryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_LimitParmDialog_QueryDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Enterring");
		this.queryCode.setConstraint("");
		this.queryDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.queryCode.setErrorMessage("");
		this.queryDesc.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		logger.debug("Entering");

		getLimitRuleListCtrl().search();

		logger.debug("Leaving");
	}

	private Combobox getFields() {
		logger.debug("Entering");

		Combobox comboBox = new Combobox();
		Comboitem item;
		item = new Comboitem();
		item.setLabel("--select--");
		item.setValue("#");
		comboBox.appendChild(item);
		fieldObjectMap = new HashMap<String, Object>();
		for (int i = 0; i < objectFieldList.size(); i++) {

			BMTRBFldDetails fldDetails = (BMTRBFldDetails) objectFieldList.get(i);
			fieldObjectMap.put(fldDetails.getRbFldName(), fldDetails);
			item = new Comboitem();
			if (mapCount == 1) {
				fieldTypeMap.put(fldDetails.getRbFldName(), fldDetails.getRbFldType());
				selectionTypeMap.put(fldDetails.getRbFldName(), fldDetails.getRbSTFlds());
				dbTableMap.put(fldDetails.getRbFldName(), fldDetails.getRbFldTableName());
			}
			if (fldDetails.isRbForBldFlds()) {
				item.setLabel(fldDetails.getRbFldName() + " - " + fldDetails.getRbFldDesc());
				item.setValue(fldDetails.getRbFldName());
				item.setTooltiptext(
						"Description :" + fldDetails.getRbFldDesc() + " \n\n Data Type :" + fldDetails.getRbFldType());
				comboBox.appendChild(item);
			}
		}
		if (fieldTypeMap.size() > 0 && selectionTypeMap.size() > 0) {
			mapCount++;
		}
		comboBox.addForward("onChange", window_LimitRuleDialog, "onFieldComboSelected", comboBox);
		comboBox.setWidth("200px");
		logger.debug("Leaving");
		return comboBox;
	}

	/**
	 * Method for generating list of selection field Type
	 */
	private Combobox getSelectionTypeItem(String selectionTypes, Combobox combo) {
		logger.debug("Entering");
		Comboitem item;
		String[] selectionTypeArray = {};
		if (selectionTypes != null && !selectionTypes.equals("")) {
			selectionTypeArray = selectionTypes.split(",");
		}
		for (int i = 0; i < selectionTypeArray.length; i++) {
			if (!StringUtils.equals(LimitConstants.LIMIT_FILTER_GLOBAL, selectionTypeArray[i])) {
				item = new Comboitem();
				item.setLabel(selectionTypeArray[i]);
				item.setValue(selectionTypeArray[i]);
				combo.appendChild(item);
			}
		}
		combo.addForward("onChange", window_LimitRuleDialog, "onComboSelected", combo);
		combo.setWidth("100px");
		logger.debug("Leaving");
		return combo;
	}

	/**
	 * Method for handling ComboBox selection
	 */
	public void onComboSelected(Event event) {
		Component component = (Component) event.getData();
		internalComboFill(component);
		logger.debug("Leaving");
	}

	// Method for Enable or disable Components Depend on selection type
	private void internalComboFill(Component component) {
		logger.debug("Entering");
		Component posCmp = null;
		Combobox optrCombo = null;

		// If field type is already selected then remove the exited component
		Component selectCmp = null;
		// related to field type

		if (component.getNextSibling().getNextSibling().getNextSibling() instanceof Combobox
				|| component.getNextSibling().getNextSibling().getNextSibling() instanceof Textbox) {
			selectCmp = component.getNextSibling().getNextSibling().getNextSibling();
		}
		if ((selectCmp instanceof Textbox) || (selectCmp instanceof Combobox) || (selectCmp instanceof Datebox)
				|| (selectCmp instanceof Decimalbox) || (selectCmp instanceof Longbox)
				|| (selectCmp instanceof Intbox)) {
			component.getParent().removeChild(selectCmp);
		}

		if (component.getNextSibling() instanceof Button) {
			posCmp = component.getNextSibling();
			((Button) posCmp).setDisabled(true);
		} else if (component.getNextSibling() instanceof Combobox) {
			posCmp = component.getNextSibling().getNextSibling();
			((Button) posCmp).setDisabled(true);
		}

		Component preComp = component.getPreviousSibling().getPreviousSibling();
		if (((Combobox) preComp).getSelectedItem() == null
				|| ((Combobox) preComp).getSelectedItem().getValue().equals("#")) {
			((Combobox) component).setValue("");
			tab_queryDesign.setSelected(true);
			throw new WrongValueException(preComp, "please select fieldname before selection type");
		} else {
			// Set the field type of selected field
			for (int j = 0; j < objectFieldList.size(); j++) {
				BMTRBFldDetails fldDetails = objectFieldList.get(j);
				String str = ((Combobox) preComp).getSelectedItem().getValue().toString();
				if (fldDetails.getRbFldName().equals(str)) {
					setFieldType(fldDetails.getRbFldType());
					setFieldLength(fldDetails.getRbFldLen());
					break;
				}
			}
		}

		String selectionType = ((Combobox) component).getSelectedItem().getValue().toString();

		if (component.getNextSibling() instanceof Combobox) {
			optrCombo = (Combobox) component.getNextSibling();
			optrCombo.getItems().clear();
			optrCombo.setValue("");
		} else {
			optrCombo = new Combobox();
			component.getParent().insertBefore(optrCombo, posCmp);
		}
		getCriteria(fieldType, selectionType, optrCombo);

		if (selectionType.equalsIgnoreCase("static")) {
			String fieldType = fieldTypeMap.get(((Combobox) preComp).getSelectedItem().getValue().toString());
			if (fieldType.equalsIgnoreCase("nvarchar")) {
				Textbox textbox = getSelectText();
				textbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(textbox, posCmp.getNextSibling());
			} else if (fieldType.equalsIgnoreCase("nchar")) {
				if (getFieldLength() == 1) {
					component.getParent().insertBefore(getBooleanVariables(), posCmp.getNextSibling());
				} else {
					Textbox textbox = getSelectText();
					textbox.setMaxlength(getFieldLength());
					component.getParent().insertBefore(textbox, posCmp.getNextSibling());
				}
			} else if (fieldType.equalsIgnoreCase("bigint")) {
				Intbox intbox = getIntBox();
				intbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(intbox, posCmp.getNextSibling());
			} else if (fieldType.equalsIgnoreCase("decimal")) {
				Decimalbox decimalbox = getDecimalbox();
				decimalbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(decimalbox, posCmp.getNextSibling());
			} else if (fieldType.equalsIgnoreCase("smalldatetime")) {
				component.getParent().insertBefore(getDateBox(), posCmp.getNextSibling());
			}
		} else if (selectionType.equalsIgnoreCase("global")) {

			component.getParent().insertBefore(getGlobalVariables(), posCmp.getNextSibling());
		} else if (selectionType.equalsIgnoreCase("dbvalue")) {

			Textbox textbox = getSelectText();
			((Button) posCmp).setDisabled(false);
			component.getParent().insertBefore(textbox, posCmp.getNextSibling());
		} else if (selectionType.equalsIgnoreCase("calvalue")) {

			Textbox textbox = getSelectText();
			((Button) posCmp).setDisabled(false);
			component.getParent().insertBefore(textbox, posCmp.getNextSibling());
		}

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
	 * Method for handling ComboBox selection
	 */
	public void onOperatorSelected(Event event) {
		logger.debug("Entering" + event.toString());
		Component component = (Component) event.getData();
		Combobox selectionCombo = (Combobox) component.getPreviousSibling();
		String selectiontype = selectionCombo.getSelectedItem().getValue().toString();
		if (selectiontype.equalsIgnoreCase("dbvalue")) {
			Textbox textComp = (Textbox) component.getNextSibling().getNextSibling();
			textComp.setValue("");
		} else if (selectiontype.equalsIgnoreCase("static")) {
			String oprLabel = ((Combobox) component).getSelectedItem().getValue().toString();
			if (oprLabel.equalsIgnoreCase("IN") || oprLabel.equalsIgnoreCase("NOT IN")) {
				Textbox staticText = (Textbox) component.getNextSibling().getNextSibling();
				staticText.setMaxlength(100);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for creating list of Logical Operators
	 */
	private Combobox getCriteria(String fieldType, String selectionType, Combobox combobox) {
		logger.debug("Entering");
		combobox.setWidth("100px");
		Comboitem item;
		String[] operatorLabels = {};
		String[] operatorValues = {};
		if ((fieldType != null && !fieldType.equals("")) && (selectionType != null && !selectionType.equals(""))) {

			for (int i = 0; i < operatorsList.size(); i++) {
				LimitFldCriterias criterias = operatorsList.get(i);
				if (criterias.getQbFldType().equalsIgnoreCase(fieldType)
						&& criterias.getQbSTFld().equalsIgnoreCase(selectionType)) {
					operatorLabels = criterias.getQbFldCriteriaNames().split(",");
					operatorValues = criterias.getQbFldCriteriaValues().split(",");
				}
			}
		}
		for (int i = 0; i < operatorLabels.length; i++) {
			item = new Comboitem();
			item.setLabel(operatorLabels[i]);
			item.setValue(operatorValues[i]);
			combobox.appendChild(item);
		}
		combobox.addForward("onChange", window_LimitRuleDialog, "onOperatorSelected", combobox);
		logger.debug("Leaving");
		return combobox;
	}

	/**
	 * Logic for creating sibling treeItem
	 * 
	 * @param cmp
	 */
	public void addButtonLogic(Component cmp) {
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
	 */
	public void onAddSubButtonClicked(Event event) {
		logger.debug("Entering");

		this.addBtnClicked = false;
		Component cmp = (Component) event.getData();
		treeitem = new Treeitem();
		LimitFilterQuery aLimitRule = getLimitFilterQuery();
		buildTreeCell(sqlQueryValueList, aLimitRule);
		addSubButtonLogic(cmp);
		logger.debug("Leaving");
	}

	/**
	 * Logic for creating Child treeItem
	 * 
	 * @param cmp
	 */
	public void addSubButtonLogic(Component cmp) {
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
							addSubButton.setVisible(false); // Added
							addButton.setVisible(false); // Added
							break;
						} else if (isItemSibling) {
							treechildren.appendChild(treeitem);
							addSubButton.setVisible(false); // Added
						}
					}
				}
				// TODO Here 3 is hard coded to Restrict the No of Conditions
				// which has to be Parameterized
				else if (child.size() == 3) {
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
	 * Method for calling an Event for both Calculated and for DB selection Values
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onValueButtonClicked(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Button comp = (Button) event.getData();
		Textbox textbox = ((Textbox) comp.getNextSibling());
		textbox.setErrorMessage("");

		Combobox selectionCombo = (Combobox) comp.getPreviousSibling().getPreviousSibling();
		String selectionType = selectionCombo.getSelectedItem().getValue().toString();
		if (selectionType.equals("calvalue")) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("returnValue", textbox.getValue());
			map.put("textbox", textbox);
			map.put("rbModule", queryModule.getValue());
			map.put("rbEvent", getLimitFilterQuery().getQuerySubCode());

			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultDialog.zul", window_LimitRuleDialog,
					map);
		} else if (selectionType.equals("dbvalue")) {
			Combobox operatorCombo = (Combobox) comp.getPreviousSibling();
			if (operatorCombo.getSelectedItem() == null) {
				throw new WrongValueException(operatorCombo, "Please select Operator ");
			}
			String oprLabel = operatorCombo.getSelectedItem().getLabel().toString();
			Combobox fieldCombo = (Combobox) selectionCombo.getPreviousSibling().getPreviousSibling();
			String fieldName = fieldCombo.getSelectedItem().getValue().toString();

			// For Calling Extended ListBox From DB
			if (oprLabel.equalsIgnoreCase("Equal") || oprLabel.equalsIgnoreCase("Not Equal")) {
				Object dataObject = ExtendedSearchListBox.show(this.window_LimitRuleDialog, dbTableMap.get(fieldName));
				if (dataObject == null) {
					// do Nothing
				} else if (dataObject instanceof String) {
					textbox.setValue("");
				} else {
					String textValue = dataObject.getClass().getMethod("getLovValue").invoke(dataObject).toString();
					if (textValue != null) {
						textbox.setValue(textValue);
					}
				}
			} else if (oprLabel.equalsIgnoreCase("IN") || oprLabel.equalsIgnoreCase("Not IN")) {
				// Calling MultiSelection ListBox From DB
				String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_LimitRuleDialog,
						dbTableMap.get(fieldName), textbox.getValue(), new Filter[] {});
				if (selectedValues != null) {
					textbox.setValue(selectedValues);
					textbox.setTooltiptext(selectedValues);
				} else {
					// do Nothing
				}
			}
		}
	}

	/**
	 * Common code for generating treeCell
	 * 
	 * @param queryValues
	 * @param aQuery
	 */
	@SuppressWarnings("deprecation")
	public void buildTreeCell(List<String> queryValues, LimitFilterQuery aLimitRule) {
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

		removeButton.addForward("onClick", window_LimitRuleDialog, "onButtonClicked", treeitem);

		// Create treeCell with in item of tree
		treerow = new Treerow();
		Treecell treecell = new Treecell();
		Hlayout hbox = new Hlayout();

		// Add left padding to TreeCell
		space = new Space();
		space.setSpacing("10px");
		hbox.appendChild(space);
		hbox.appendChild(removeButton);

		// Condition for not generate Criteria comboBox or not
		if (itemCount != 1) {

			space = new Space();
			space.setSpacing("10px");
			hbox.appendChild(space);
			combo = getCondition();

			if (queryValues.size() > 0 && !aLimitRule.isNewRecord()) {
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
			space.setSpacing("10px");
			hbox.appendChild(space);
		}

		/*
		 * space = new Space(); hbox.appendChild(space);
		 */
		combo = getFields();// first comb box
		combo.setReadonly(true);

		Textbox textbox = null;

		String field = "";

		// Set field to ComboBox depend on fieldList and getting query
		if (queryValues.size() > 0 && !aLimitRule.isNewRecord()) {
			this.fldComboSelAtmpts++;
			queryValues.remove(0);
			for (int i = 0; i < combo.getItemCount(); i++) {
				if (combo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
					combo.setSelectedIndex(i);
					for (int j = 0; j < objectFieldList.size(); j++) {
						BMTRBFldDetails builderTables = objectFieldList.get(j);
						String str = combo.getSelectedItem().getValue().toString();
						StringTokenizer st = new StringTokenizer(str, ".");
						while (st.hasMoreTokens()) {
							if (builderTables.getRbFldName().equals(st.nextToken())) {
								setFieldType(builderTables.getRbFldType());
								field = builderTables.getRbSTFlds();
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
		space.setSpacing("10px");
		hbox.appendChild(space);

		String selectType = "";
		Combobox comboType = null;
		// Set fieldType to ComboBox depend on staticList of types and getting rule
		comboType = getSelectionTypeItem(field, new Combobox());// selection type comboBox
		comboType.setReadonly(true);

		if (queryValues.size() > 0 && !aLimitRule.isNewRecord()) {

			for (int i = 0; i < comboType.getItemCount(); i++) {
				if (comboType.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
					comboType.setSelectedIndex(i);
					queryValues.remove(0);
					selectType = comboType.getSelectedItem().getValue().toString();
					break;
				}
			}
		}
		hbox.appendChild(comboType);
		// Set the field type of selected field
		if (!"nvarchar".equalsIgnoreCase(getFieldType())) {
			this.operatorVal = true;
		} else {
			this.operatorVal = false;
		}
		combo = getCriteria(fieldType, selectType, new Combobox());
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

		calValueButton = new Button();
		calValueButton.setStyle("padding:0px;width:80px;");
		calValueButton.setLabel("Add value");
		calValueButton.setTooltiptext("Click Button for Add Value");
		calValueButton.setDisabled(true);
		calValueButton.addForward("onClick", window_LimitRuleDialog, "onValueButtonClicked", calValueButton);
		hbox.appendChild(calValueButton);

		if (comboType.getSelectedItem() != null) {
			String selectionType = (String) comboType.getSelectedItem().getValue();
			if (selectionType.equals("static")) {// depend on type of field
				if (getFieldType().equalsIgnoreCase("nvarchar")) {
					textbox = getSelectText();
					textbox.setMaxlength(getFieldLength());
					textbox.setValue(queryValues.get(0));
					hbox.appendChild(textbox);
				} else if (getFieldType().equalsIgnoreCase("nchar")) {
					if (getFieldLength() == 1) {
						Combobox booleanCombo = getBooleanVariables();
						for (int i = 0; i < booleanCombo.getItemCount(); i++) {
							if (booleanCombo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
								booleanCombo.setSelectedIndex(i);
								break;
							}
						}
						hbox.appendChild(booleanCombo);
					} else {
						textbox = getSelectText();
						textbox.setMaxlength(getFieldLength());
						textbox.setValue(queryValues.get(0));
						hbox.appendChild(textbox);
					}
				} else if (getFieldType().equalsIgnoreCase("bigint")) {
					Intbox intbox = getIntBox();
					intbox.setMaxlength(getFieldLength());
					intbox.setValue(Integer.valueOf(queryValues.get(0)));
					hbox.appendChild(intbox);
				} else if (getFieldType().equalsIgnoreCase("decimal")) {
					Decimalbox decimalbox = getDecimalbox();
					decimalbox.setMaxlength(getFieldLength());
					decimalbox.setValue(BigDecimal.valueOf(Double.valueOf(queryValues.get(0))));
					hbox.appendChild(decimalbox);
				} else if (getFieldType().equalsIgnoreCase("smalldatetime")) {
					Datebox datebox = getDateBox();
					datebox.setValue(DateUtil.parseFullDate(queryValues.get(0)));
					hbox.appendChild(datebox);
				}
				queryValues.remove(0);
				fldComboSelAtmpts++;
			} else if (selectionType.equals("calvalue")) {// Entered field Type

				textbox = getSelectText();
				textbox.setValue(queryValues.get(0));
				hbox.appendChild(textbox);
				calValueButton.setDisabled(false);
				queryValues.remove(0);
				fldComboSelAtmpts++;
			} else if (selectionType.equals("dbvalue")) {// Entered field Type

				textbox = getSelectText();
				textbox.setValue(queryValues.get(0));
				hbox.appendChild(textbox);
				calValueButton.setDisabled(false);
				queryValues.remove(0);
				fldComboSelAtmpts++;
			} else if (comboType.getSelectedItem().getValue().equals("global")) {// Global variable field type
				combo = getGlobalVariables();
				for (int i = 0; i < combo.getItemCount(); i++) {
					if (combo.getItemAtIndex(i).getValue().equals(queryValues.get(0))) {
						combo.setSelectedIndex(i);
						queryValues.remove(0);
						fldComboSelAtmpts++;
						break;
					}
				}
				hbox.appendChild(combo);
			}
		}

		if (queryValues.size() > 0 && !aLimitRule.isNewRecord()) {
			space = new Space();
			space.setSpacing("10px");
			hbox.appendChild(space);

			if (condition.equals("AND")) {
				space = new Space();
				space.setSpacing("10px");
				hbox.appendChild(space);
				hbox.appendChild(addSubButton);
			} else {
				hbox.appendChild(addButton);
				space = new Space();
				space.setSpacing("10px");
				hbox.appendChild(space);
				hbox.appendChild(addSubButton);
			}
		} else {
			hbox.appendChild(addButton);
			space = new Space();
			space.setSpacing("10px");
			hbox.appendChild(space);
			hbox.appendChild(addSubButton);
		}

		hbox.setStyle("display: inline-block; _display: inline; padding:0px;width:1000px;height:22px;");
		treecell.setHflex("100");
		treecell.appendChild(hbox);
		treerow.appendChild(treecell);

		addButton.addForward("onClick", window_LimitRuleDialog, "onAddButtonClicked", treeitem);

		addSubButton.addForward("onClick", window_LimitRuleDialog, "onAddSubButtonClicked", treeitem);

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
		if (queryValues.size() > 0 && !aLimitRule.isNewRecord()) {
			if (queryValues.get(0).equals(")")) {
				int size = queryValues.size();
				for (int i = 0; i < size; i++) {
					if (queryValues.get(0).equals(")")) {
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
					buildTreeCell(sqlQueryValueList, aLimitRule);
				}

			} else if (queryValues.get(0).equals("(")) {// Condition for adding
				// child item
				queryValues.remove(0);
				qryBraketCount++;
				if (queryValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					buildTreeCell(sqlQueryValueList, aLimitRule);
				}
			} else {
				if (queryValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					qryBraketCount++;
					buildTreeCell(sqlQueryValueList, aLimitRule);
				}
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * build a query for values and generate in textBox
	 */
	public void onClick$btnReadValues(Event event) {
		logger.debug("Entering" + event.toString());
		this.tab_queryDesign.setSelected(true);
		readButtonClicked();// calling read button method for generating query
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Validating Builded Query and show result in 'SqlViewResult.zul'
	 */
	public void onClick$btnValidation(Event event) {
		logger.debug("Entering" + event.toString());
		this.tab_queryDesign.setSelected(true);

		readButtonClicked();// calling read button method for generating query

		String resultQuery = "";
		if (moduleName.equals(RuleConstants.MODULE_IRLFILTER)) {
			resultQuery = "select * from FinancemainLimitCheck_Aview";

		}

		resultQuery = resultQuery + "\n" + sQLQuery.getValue();

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultQuery", resultQuery);
		map.put("IRFilter", queryFieldMap);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/DedupParm/SqlViewResult.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for building SQLquery
	 */
	public void readButtonClicked() {
		logger.debug("Entering");
		sQLQuery.setValue("");
		queryFieldMap = new LinkedHashMap<String, String[]>();
		strSqlQuery = "Where(" + "\n" + "(";
		actualBlob = "(" + "|";
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
	 * @param isSimulation
	 */
	public void doBuildQuery(Component selectionComp) {
		logger.debug("Entering");

		if (selectionComp.getChildren() != null) {
			int comboCount = 1;
			boolean inCondition = false;
			String element = "";
			String elementDesc = "";
			for (int i = 0; i < selectionComp.getChildren().size(); i++) {
				Component childComp = (Component) selectionComp.getChildren().get(i);
				if (childComp instanceof Combobox) {
					Component combo = (Combobox) selectionComp.getChildren().get(i);

					// Validate getting value from 'combo' is related to
					// comboBox or not
					boolean validate = false;
					String value = ((Combobox) combo).getValue().toString();
					if (value == null || value.equals("") || value.equals("--select--")) {
						if (!((Combobox) combo).isDisabled()) {
							validate = false;
						} else {
							validate = true;
						}
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
						throw new WrongValueException(combo, Labels.getLabel("STATIC_INVALID", new String[] { "" }));
					}
					String comboValue = "~";
					String comboLabel = "";
					if (((Combobox) combo).isDisabled()) {

					} else {
						comboValue = ((Combobox) combo).getSelectedItem().getValue().toString();
						comboLabel = ((Combobox) combo).getSelectedItem().getLabel().toString();

					}
					// retrieve values from comboBox(s)
					if ("AND".equalsIgnoreCase(comboLabel) || "OR".equalsIgnoreCase(comboLabel)) {

						strSqlQuery = strSqlQuery + "\n" + comboLabel + "\n" + "(";

						actualBlob = actualBlob + comboLabel + "|" + "(" + "|";
					} else if (comboCount == 2) {
						actualBlob = actualBlob + comboValue + "|";
						comboCount++;
					} else if (comboCount == 3) {
						strSqlQuery = strSqlQuery + comboValue;
						if (comboValue.trim().equalsIgnoreCase("LIKE")) {
							likeCondition = true;
						}
						if (StringUtils.strip(comboValue).equalsIgnoreCase("IN")
								|| StringUtils.strip(comboValue).equalsIgnoreCase("NOT IN")) {

							inCondition = true;
						}
						actualBlob = actualBlob + comboValue + "|";
						comboCount++;
					} else {
						if (comboCount == 1) {
							element = comboValue;
							elementDesc = ((Combobox) combo).getSelectedItem().getLabel();
							for (int j = 0; j < objectFieldList.size(); j++) {
								BMTRBFldDetails builderTables = objectFieldList.get(j);
								if (builderTables.getRbFldName().equals(element)) {
									setFieldType(builderTables.getRbFldType());// To Get the Type of selectingField
									if (strSqlQuery.equals("Where(\n(")
											|| (strSqlQuery.endsWith("AND\n(") || strSqlQuery.endsWith("OR\n("))) {
										strSqlQuery = strSqlQuery + element;
									}
									actualBlob = actualBlob + comboValue + "|";
									break;
								}
							}
						} else if (StringUtils.equals(comboValue, "true") || StringUtils.equals(comboValue, "false")) {
							int resultValue = 0;
							if (StringUtils.equals(comboValue, "true")) {
								resultValue = 1;
							}
							strSqlQuery = strSqlQuery + " " + "'" + resultValue + "' )";
						}
						comboCount++;
					}
					if (comboCount == 4) {
						if (likeCondition) {
							likeCondition = false;
						} else {
							if (StringUtils.contains(getFieldType(), "varchar")) {
								if (App.DATABASE == Database.SQL_SERVER) {
								}
							}
						}

						if (!queryFieldMap.containsKey(element)) {
							String[] array = new String[2];
							array[0] = getFieldType();
							array[1] = elementDesc;
							queryFieldMap.put(element, array);
						}

					}
				} else if (childComp instanceof Textbox) {
					// Component related to textBox

					// if(isSimulation){
					Textbox textbox = (Textbox) childComp;
					textbox.setErrorMessage("");
					if (textbox.getValue() == null || textbox.getValue().equals("")) {
						if (!textbox.isReadonly()) {
							tab_queryDesign.setSelected(true);
							throw new WrongValueException(textbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
						}
					}

					if (inCondition == true) {
						String str = textbox.getValue().toString();
						StringTokenizer st = new StringTokenizer(str, ",");
						strSqlQuery = strSqlQuery + "(";

						while (st.hasMoreTokens()) {

							strSqlQuery = strSqlQuery + "'" + st.nextToken() + "'" + " , ";

						}

						strSqlQuery = strSqlQuery.substring(0, strSqlQuery.length() - 3) + ") )";

						inCondition = false;
					} else {
						boolean elsCond = false;
						// condition for string field type
						if (getFieldType().equalsIgnoreCase("nvarchar")) {
							if (!elsCond) {
								strSqlQuery = strSqlQuery + " " + "'" + textbox.getValue().toString() + "' )";
							}
						} else {// condition for Non-String field type
							if (!elsCond) {
								strSqlQuery = strSqlQuery + " " + textbox.getValue().toString() + ")";
							}
						}
					}
					String textValue = textbox.getValue().toString();
					if (textValue.equals("")) {
						textValue = "~";
					}
					actualBlob = actualBlob + textValue + "|";
					comboCount++;
					/*
					 * }else{ if(likeCondition){ strSqlQuery = strSqlQuery + ":like" + element; likeCondition = false;
					 * }else{ strSqlQuery = strSqlQuery + ":" + element ;
					 * if(StringUtils.contains(getFieldType(),"varchar")){ if(App.DATABASE == Database.SQL_SERVER){
					 * strSqlQuery = strSqlQuery + " AND ("+element +" IS NOT NULL AND "+element +" != '') "; }else{
					 * strSqlQuery = strSqlQuery + " AND "+element +" IS NOT NULL "; } } } if
					 * (!queryFieldMap.containsKey(element)) { queryFieldMap.put(element, getFieldType()); }
					 * comboCount++; }
					 */
				} else if (childComp instanceof Datebox) {
					// Component related to dateBox

					Datebox datebox = (Datebox) childComp;
					datebox.setErrorMessage("");
					if (datebox.getValue() == null) {
						tab_queryDesign.setSelected(true);
						throw new WrongValueException(datebox, Labels.getLabel("Label_RuleDialog_select_date"));
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					String date = formatter.format(datebox.getValue());
					strSqlQuery = strSqlQuery + " " + "'" + date + "' ";
					actualBlob = actualBlob + date + "|";
					comboCount++;
				} else if (childComp instanceof Decimalbox) {
					// Component related to decimalBox

					Decimalbox decimalbox = (Decimalbox) childComp;
					decimalbox.setErrorMessage("");
					if (decimalbox.getValue() == null) {
						tab_queryDesign.setSelected(true);
						throw new WrongValueException(decimalbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strSqlQuery = strSqlQuery + " " + decimalbox.getValue().toString();
					actualBlob = actualBlob + decimalbox.getValue().toString() + "|";
					comboCount++;
				} else if (childComp instanceof Intbox) {
					// Component related to intBox

					Intbox intbox = (Intbox) childComp;
					intbox.setErrorMessage("");
					if (intbox.intValue() == 0) {
						tab_queryDesign.setSelected(true);
						throw new WrongValueException(intbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strSqlQuery = strSqlQuery + " " + intbox.getValue().toString();
					actualBlob = actualBlob + intbox.getValue().toString() + "|";
					comboCount++;
				} else if (childComp instanceof Longbox) {
					// Component related to longBox

					Longbox longbox = (Longbox) childComp;
					longbox.setErrorMessage("");
					if (longbox.longValue() == 0) {
						tab_queryDesign.setSelected(true);
						throw new WrongValueException(longbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strSqlQuery = strSqlQuery + " " + longbox.getValue().toString();
					actualBlob = actualBlob + longbox.getValue().toString() + "|";
					comboCount++;
				} else {
					// repeating the logic for getting values from components
					doBuildQuery((Component) selectionComp.getChildren().get(i));
				}
			}
		}
		// Generating Structure for build Condition
		if (selectionComp instanceof Treeitem) {
			actualBlob = actualBlob + ")" + "|";
		}
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LimitFilterQuery aLimitRule = new LimitFilterQuery();
		BeanUtils.copyProperties(getLimitFilterQuery(), aLimitRule);

		String keyReference = Labels.getLabel("label_LimitParmDialog_QueryCode.value") + " : "
				+ aLimitRule.getQueryCode();

		doDelete(keyReference, aLimitRule);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Create a new LimitFilterQuery object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final LimitFilterQuery aLimitRule = getLimitRuleService().getNewLimitRule();
		aLimitRule.setNewRecord(true);
		setLimitFilterQuery(aLimitRule);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.queryCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleDelete") && !enqiryModule) {
			this.btnDelete.setVisible(true);
		} else {
			this.btnDelete.setVisible(false);
		}

		if (getLimitFilterQuery().isNewRecord()) {
			this.queryCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnDelete.setVisible(false);
		} else {
			this.queryCode.setReadonly(true);
			if (getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleDelete")
					&& getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleSave") && !enqiryModule) {
				this.btnReadValues.setVisible(true);
				this.tab_queryDesign.setVisible(true);
			} else {
				this.btnReadValues.setVisible(false);
				this.tab_queryDesign.setVisible(false);
			}

			if (PennantConstants.RECORD_TYPE_DEL.equals(getLimitFilterQuery().getRecordType()) && !enqiryModule) {
				this.tab_queryDesign.setVisible(false);
				this.btnReadValues.setVisible(false);
				if (!getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleMaintain")) {
					this.btnSave.setVisible(true);
					if (getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleDelete")) {
						this.btnDelete.setVisible(false);
					}
				} else {
					this.btnSave.setVisible(false);
					this.btnDelete.setVisible(false);
					if (getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleDelete")) {
						this.btnSave.setVisible(true);
					}
				}
			}
		}
		this.sQLQuery.setReadonly(true);
		this.queryDesc.setReadonly(!getUserWorkspace().isAllowed("RuleDialog_returnType") || enqiryModule);
		this.active.setDisabled(!getUserWorkspace().isAllowed("RuleDialog_returnType") || enqiryModule);

		if (this.tab_queryDesign.isVisible()) {
			this.tab_queryDesign.setSelected(true);
		} else {
			this.tab_textQuery.setSelected(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.limitRule.isNewRecord()) {
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
		this.tab_queryDesign.setVisible(false);
		active.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		// active.setDisabled(!getUserWorkspace().isAllowed("button_RuleDialog_btnLimitDefRuleNew") && enqiryModule);
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
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Enterring");

		final LimitFilterQuery aLimitRule = new LimitFilterQuery();
		BeanUtils.copyProperties(getLimitFilterQuery(), aLimitRule);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the LimitFilterQuery object with the components data
		doWriteComponentsToBean(aLimitRule);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aLimitRule.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLimitRule.getRecordType()).equals("")) {
				aLimitRule.setVersion(aLimitRule.getVersion() + 1);
				if (isNew) {
					aLimitRule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLimitRule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLimitRule.setNewRecord(true);
				}
			}
		} else {
			aLimitRule.setVersion(aLimitRule.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aLimitRule, tranType)) {
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
	 * @param aLimitRule (LimitFilterQuery)
	 * 
	 * @param tranType   (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(LimitFilterQuery aLimitRule, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aLimitRule.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLimitRule.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLimitRule.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLimitRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLimitRule.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLimitRule);
				}

				if (isNotesMandatory(taskId, aLimitRule)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aLimitRule.setTaskId(taskId);
			aLimitRule.setNextTaskId(nextTaskId);
			aLimitRule.setRoleCode(getRole());
			aLimitRule.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aLimitRule, tranType);
			String operationRefs = getServiceOperations(taskId, aLimitRule);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aLimitRule, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aLimitRule, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		LimitFilterQuery aLimitRule = (LimitFilterQuery) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getLimitRuleService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getLimitRuleService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getLimitRuleService().doApprove(auditHeader);
					if (aLimitRule.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getLimitRuleService().doReject(auditHeader);
					if (aLimitRule.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LimitRuleDialog, auditHeader);
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_LimitRuleDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.limitRule), true);
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

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCurrency
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(LimitFilterQuery aLimitRule, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLimitRule.getBefImage(), aLimitRule);
		return new AuditHeader(aLimitRule.getQueryCode(), null, null, null, auditDetail, aLimitRule.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.limitRule);
	}

	/**
	 * Method for generating new textBox
	 */
	private Textbox getSelectText() {
		Textbox text = new Textbox();
		text.setWidth("98px");
		text.setStyle("padding-left: 5px;padding-right: 5px;");
		return text;
	}

	/**
	 * Method for generating Global Variable List
	 */
	private Combobox getGlobalVariables() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		comboBox.setStyle("padding-left: 5px;padding-right: 5px;");
		Comboitem item;

		for (int i = 0; i < globalVariableList.size(); i++) {
			GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
			item = new Comboitem();
			item.setLabel(globalVariable.getName());
			item.setValue(globalVariable.getName());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("100px");
		comboBox.setReadonly(true);
		logger.debug("Leaving");
		return comboBox;
	}

	private Combobox getBooleanVariables() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		comboBox.setStyle("padding-left: 5px;padding-right: 5px;");
		Comboitem item;

		for (int i = 0; i < com.pennant.webui.rulefactory.rule.BuilderUtilListbox.getBooleanOperators()
				.getSize(); i++) {
			item = new Comboitem();
			item.setLabel(com.pennant.webui.rulefactory.rule.BuilderUtilListbox.getBooleanOperators().getElementAt(i)
					.toString());
			item.setValue(com.pennant.webui.rulefactory.rule.BuilderUtilListbox.getBooleanOperators().getElementAt(i)
					.toString());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("100px");
		comboBox.setReadonly(true);
		logger.debug("Leaving");
		return comboBox;
	}

	/**
	 * Methods for returning field component types
	 * 
	 * @return
	 */
	private Decimalbox getDecimalbox() {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setStyle("padding-left: 5px;padding-right: 5px;");
		decimalbox.setWidth("100px");
		return decimalbox;
	}

	private Intbox getIntBox() {
		Intbox intbox = new Intbox(0);
		intbox.setStyle("padding-left: 5px;padding-right: 5px;");
		intbox.setWidth("100px");
		return intbox;
	}

	private Datebox getDateBox() {
		Datebox date = new Datebox();
		date.setStyle("padding-left: 5px;padding-right: 5px;");
		date.setWidth("100px");
		date.setFormat(PennantConstants.DBDateTimeFormat);
		return date;
	}

	/**
	 * Method for handling ComboBox selection
	 */
	public void onFieldComboSelected(Event event) {
		logger.debug("Entering" + event.toString());

		Combobox component = (Combobox) event.getData();
		Combobox selctcombo = (Combobox) component.getNextSibling().getNextSibling();
		Combobox optrCombo = (Combobox) component.getNextSibling().getNextSibling().getNextSibling();

		if (selctcombo != null) {

			selctcombo.getItems().clear();
			selctcombo.setValue("");
			if (optrCombo != null) {

				optrCombo.getItems().clear();
				optrCombo.setValue("");
				Button valueButton = (Button) optrCombo.getNextSibling();
				valueButton.setDisabled(true);
			}

		}

		if (selectionTypeMap.containsKey(component.getSelectedItem().getValue().toString())) {
			String str = selectionTypeMap.get(component.getSelectedItem().getValue().toString());
			getSelectionTypeItem(str, selctcombo);
		}

		logger.debug("Leaving" + event.toString());
	}

	@Override
	protected String getReference() {
		return this.queryCode.getValue();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitFilterQuery getLimitFilterQuery() {
		return this.limitRule;
	}

	public void setLimitFilterQuery(LimitFilterQuery dedupParm) {
		this.limitRule = dedupParm;
	}

	public void setLimitRuleListCtrl(LimitRuleListCtrl limitRuleListCtrl) {
		this.limitRuleListCtrl = limitRuleListCtrl;
	}

	public LimitRuleListCtrl getLimitRuleListCtrl() {
		return this.limitRuleListCtrl;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public LimitRuleService getLimitRuleService() {
		return limitRuleService;
	}

	public void setLimitRuleService(LimitRuleService limitRuleService) {
		this.limitRuleService = limitRuleService;
	}

}
