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
 * * FileName : LimitGroupDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified
 * Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.limit.limitgroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.limit.LimitGroupService;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Limit/LimitGroup/limitGroupDialog.zul file. <br>
 * ************************************************************<br>
 */
public class LimitGroupDialogCtrl extends GFCBaseCtrl<LimitGroup> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LimitGroupDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */
	protected Window window_LimitGroupDialog;
	protected Row row0;
	protected Label label_GroupCode;
	protected Hlayout hlayout_GroupCode;
	protected Space space_GroupCode;

	protected Textbox groupCode;
	protected Label label_GroupName;
	protected Hlayout hlayout_GroupName;
	protected Space space_GroupName;

	protected Combobox groupOf;
	protected Label label_GroupOf;
	protected Hlayout hlayout_GroupOf;
	protected Space space_GroupOf;

	protected Label groupCategory;
	protected Label label_GroupCategory;
	protected Hlayout hlayout_GroupCategory;
	protected Space space_GroupCategory;

	protected Textbox groupName;
	protected Checkbox active;
	protected Space space_Active;

	private Listheader listheader_LimitLine;
	private Listheader listheader_LimitGroup;
	protected Label window_LimitGroupDialog_title;

	// not auto wired vars
	private LimitGroup limitGroup; // overhanded per param
	private transient LimitGroupListCtrl limitGroupListCtrl; // overhanded per
	// param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.

	// ServiceDAOs / Domain Classes
	private transient LimitGroupService limitGroupService;
	private transient PagedListService pagedListService;

	protected Button btnAddLimitLine;
	protected Button btnAddGroup;
	protected Button btnTop;
	protected Button btnUp;
	protected Button btnDown;
	protected Button btnBottom;
	private static List<ValueLabel> limitLineslist;
	private static List<ValueLabel> limitGroupslist;
	protected Map<String, Rule> ruleCodesMap;
	protected Map<String, LimitGroup> groupCodesMap;
	protected Listbox listBoxLimitGroupLines;
	protected int key = 0;
	protected String indent = null;
	protected boolean isInstitutionType = false;
	List<LimitGroupLines> limitGroupItemsList = new ArrayList<LimitGroupLines>();
	protected Map<Integer, LimitGroupLines> deleteMap = new HashMap<Integer, LimitGroupLines>();
	private PagedListWrapper<LimitGroupLines> assignedLimitGroupLinesPagedListWrapper;

	/**
	 * default constructor.<br>
	 */
	public LimitGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LimitGroupDialog";
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LimitGroup object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LimitGroupDialog(Event event) {
		logger.debug("Entring" + event.toString());
		try {
			setPageComponents(window_LimitGroupDialog);
			// READ OVERHANDED params !

			this.limitGroup = (LimitGroup) arguments.get("limitGroup");
			LimitGroup befImage = new LimitGroup();
			BeanUtils.copyProperties(this.limitGroup, befImage);
			this.limitGroup.setBefImage(befImage);
			setLimitGroupLinesList(limitGroup.getLimitGroupLinesList());

			ruleCodesMap = PennantAppUtil.getLimitLineCodes(RuleConstants.MODULE_LMTLINE, true, "");
			groupCodesMap = PennantAppUtil.getLimitGroup(null, true);

			setAssignedLimitGrouLinesListPagedListWrapper();
			doLoadWorkFlow(this.limitGroup.isWorkflow(), this.limitGroup.getWorkflowId(),
					this.limitGroup.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "LimitGroupDialog");
			} else {
				getUserWorkspace().allocateAuthorities("LimitGroupDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("limitGroupListCtrl")) {
				setLimitGroupListCtrl((LimitGroupListCtrl) arguments.get("limitGroupListCtrl"));
			} else {
				setLimitGroupListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLimitGroup());
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

		displayComponents(ScreenCTL.SCRN_GNEDT);
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
		if (!active.isDisabled()) {
			doDelete();
		} else {
			MessageUtil
					.showError(Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { getLimitGroup().getGroupCode() }));
		}

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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());

		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_LimitGroupDialog);
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.limitGroup);
	}

	/**
	 * when the "Group Of" is selected. <br>
	 * 
	 * @param event
	 */
	public void onSelect$groupOf(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		if (groupOf.getSelectedItem() != null && (StringUtils.equals(LimitConstants.LIMIT_GROUP_LINE,
				groupOf.getSelectedItem().getValue().toString()))) {
			btnAddGroup.setVisible(false);
			btnAddLimitLine.setVisible(true);
			listheader_LimitGroup.setVisible(false);
			listheader_LimitLine.setVisible(true);
		} else {
			listheader_LimitGroup.setVisible(true);
			listheader_LimitLine.setVisible(true);
			btnAddGroup.setVisible(true);
			btnAddLimitLine.setVisible(false);
		}
		doAddRow();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "top" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnTop(Event event) {
		logger.debug("Entering" + event.toString());
		int count = listBoxLimitGroupLines.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Line or Group. Please Add Either Line or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitGroupLines.getSelectedItem();
			if (item != null && listBoxLimitGroupLines.getFirstChild() != null) {
				listBoxLimitGroupLines.insertBefore(item, listBoxLimitGroupLines.getFirstChild());
			} else {
				MessageUtil.showError("Please select one Limit Structure Detail");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "up" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnUp(Event event) {
		logger.debug("Entering" + event.toString());
		int count = listBoxLimitGroupLines.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Line or Group. Please Add Either Line or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitGroupLines.getSelectedItem();
			if (item != null && item.getPreviousSibling() != null) {
				listBoxLimitGroupLines.insertBefore(item, item.getPreviousSibling());
			} else {
				MessageUtil.showError("Please select one Limit Structure Detail");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "down" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDown(Event event) {
		logger.debug("Entering" + event.toString());
		int count = listBoxLimitGroupLines.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Line or Group. Please Add Either Line or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitGroupLines.getSelectedItem();
			if (item != null && item.getNextSibling() != null) {
				listBoxLimitGroupLines.insertBefore(item.getNextSibling(), item);
			} else {
				MessageUtil.showError("Please select one Limit Structure Detail");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "bottom" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnBottom(Event event) {
		logger.debug("Entering" + event.toString());
		int count = listBoxLimitGroupLines.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Line or Group. Please Add Either Line or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitGroupLines.getSelectedItem();
			if (item != null) {
				listBoxLimitGroupLines.insertBefore(item, null);
			} else {
				MessageUtil.showError("Please select one Limit Structure Detail");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To add row
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnAddLimitLine(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		doAddRow();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To add row
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnAddGroup(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		doAddRow();
		logger.debug("Leaving" + event.toString());
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLimitGroup
	 * @throws InterruptedException
	 */

	public void doShowDialog(LimitGroup aLimitGroup) throws InterruptedException {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aLimitGroup);
			// set ReadOnly mode accordingly if the object is new or not.
			displayComponents(ScreenCTL.getMode(enqiryModule, isWorkFlowEnabled(), aLimitGroup.isNewRecord()));
			setDialog(DialogType.EMBEDDED);
		} catch (final Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.groupCode, this.groupName));
		if (getLimitGroup().isNewRecord()) {
			setComponentAccessType("LimitGroupDialog_GroupCode", false, this.groupCode, this.space_GroupCode,
					this.label_GroupCode, this.hlayout_GroupCode, null);
			setComponentAccessType("LimitGroupDialog_GroupCode", false, this.groupOf, space_GroupOf, this.label_GroupOf,
					this.hlayout_GroupOf, null);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;
		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.limitGroup.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("LimitGroupDialog_GroupCode", true, this.groupCode, this.space_GroupCode,
				this.label_GroupCode, this.hlayout_GroupCode, null);
		setComponentAccessType("LimitGroupDialog_GroupName", tempReadOnly, this.groupName, this.space_GroupName,
				this.label_GroupName, this.hlayout_GroupName, null);
		setRowInvisible(this.row0, this.hlayout_GroupCode, this.hlayout_GroupName);
		setComponentAccessType("LimitGroupDialog_GroupCode", true, this.groupOf, space_GroupOf, this.label_GroupOf,
				this.hlayout_GroupOf, null);
		setComponentAccessType("LimitGroupDialog_GroupName",
				tempReadOnly || !getLimitGroupService().validationCheck(getLimitGroup().getGroupCode()), this.active,
				space_Active, this.label_GroupCode, this.hlayout_GroupCode, null);

		logger.debug("Leaving");
	}

	// ****************************************************************+
	// ****************************++ helpers ************************++
	// ****************************************************************+

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
			getUserWorkspace().allocateAuthorities("LimitGroupDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_btnSave"));

			if (!StringUtils.equalsIgnoreCase(getLimitGroup().getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				this.btnAddLimitLine
						.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
				this.btnAddGroup.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
				this.btnUp.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
				this.btnTop.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
				this.btnBottom.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
				this.btnDown.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"));
			} else {
				btnAddLimitLine.setVisible(false);
				btnAddGroup.setVisible(false);
				btnUp.setVisible(false);
				btnTop.setVisible(false);
				btnBottom.setVisible(false);
				btnDown.setVisible(false);
			}
		} else {
			btnAddLimitLine.setVisible(false);
			btnAddGroup.setVisible(false);
			btnUp.setVisible(false);
			btnTop.setVisible(false);
			btnBottom.setVisible(false);
			btnDown.setVisible(false);

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.groupCode.setMaxlength(8);
		this.groupName.setMaxlength(50);

		this.listBoxLimitGroupLines.setWidth(getListBoxWidth(60));
		this.listBoxLimitGroupLines.setHeight(getListBoxHeight(7));
		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Stores the initial values to member variables. <br>
	 */

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLimitGroup LimitGroup
	 */
	public void doWriteBeanToComponents(LimitGroup aLimitGroup) {
		logger.debug("Entering");
		this.groupCode.setValue(aLimitGroup.getGroupCode());
		this.groupName.setValue(aLimitGroup.getGroupName());
		fillComboBox(this.groupOf, aLimitGroup.getGroupOf(), PennantStaticListUtil.getGroupOfList(), "");
		if (aLimitGroup.getLimitGroupLinesList() == null) {
			setLimitGroupLinesList(new ArrayList<LimitGroupLines>());
		}
		List<LimitGroupLines> lmtGrpItemsList = new ArrayList<LimitGroupLines>();
		for (LimitGroupLines detail : getLimitGroupLinesList()) {
			key = key + 1;
			detail.setKey(key);
			if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {
				lmtGrpItemsList.add(detail);
			} else {
				deleteMap.put(key, detail);
			}
		}

		limitLineslist = getValueLabelsFromRuleMap();
		limitGroupslist = getValueLabelsFromGroupMap();
		setLimitGroupLinesList(lmtGrpItemsList);
		doFillListBox();
		this.active.setChecked(aLimitGroup.isActive());

		if (getLimitGroup().isNewRecord()) {
			this.active.setChecked(true);
		} else {
			groupCategory.setValue(Labels.getLabel(aLimitGroup.getLimitCategory()));
			label_GroupCategory.setVisible(true);
		}

		this.recordStatus.setValue(aLimitGroup.getRecordStatus());
		logger.debug("Leaving");
	}

	private List<ValueLabel> getValueLabelsFromGroupMap() {
		List<ValueLabel> groupsList = new ArrayList<ValueLabel>();
		for (LimitGroup limitGroup : groupCodesMap.values()) {
			groupsList.add(new ValueLabel(limitGroup.getId(), limitGroup.getGroupName() + "-" + limitGroup.getId()));
		}
		return groupsList;
	}

	private List<ValueLabel> getValueLabelsFromRuleMap() {
		List<ValueLabel> rulesList = new ArrayList<ValueLabel>();
		for (Rule rule : ruleCodesMap.values()) {
			rulesList.add(new ValueLabel(rule.getRuleCode(), rule.getRuleCodeDesc() + "-" + rule.getRuleCode()));
		}
		return rulesList;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLimitGroup
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(LimitGroup aLimitGroup) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Limit Category
		if (isInstitutionType)
			aLimitGroup.setLimitCategory(LimitConstants.LIMIT_CATEGORY_BANK);
		else
			aLimitGroup.setLimitCategory(LimitConstants.LIMIT_CATEGORY_CUST);

		// Group Code
		try {
			aLimitGroup.setGroupCode(this.groupCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Group Name
		try {
			aLimitGroup.setGroupName(this.groupName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Group of
		try {
			if (this.groupOf.getSelectedItem() != null)
				aLimitGroup.setGroupOf(this.groupOf.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.listBoxLimitGroupLines.getItems().isEmpty()
					&& !PennantConstants.RCD_STATUS_CANCELLED.equals(aLimitGroup.getRecordStatus())) {
				this.listBoxLimitGroupLines.setEmptyMessage("Should not be Empty. Please Add Either Groups or Items");
				if (aLimitGroup.getGroupCode() != null && aLimitGroup.getGroupName() != null) {
					MessageUtil.showError(
							"This Group doesn't Contain any Line or Group. Please Add Either Line or Group.");
					throw new WrongValueException();
				}
			} else {
				// Limit Group Items
				List<LimitGroupLines> tempLimitGrpItemsList = new ArrayList<LimitGroupLines>();
				tempLimitGrpItemsList.addAll(getLimitGroupLinesList());
				for (LimitGroupLines lmtGrpItemsDetails : deleteMap.values()) {
					tempLimitGrpItemsList.add(lmtGrpItemsDetails);
				}
				aLimitGroup.setLimitGroupLinesList(tempLimitGrpItemsList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLimitGroup.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * 
	 * @throws InterruptedException
	 */
	private void doSetValidation() throws InterruptedException {
		logger.debug("Entering");
		doRemoveValidation();
		// Group Code
		if (!this.groupCode.isReadonly()) {
			this.groupCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LimitGroupDialog_GroupCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Group Name
		if (!this.groupName.isReadonly()) {
			this.groupName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LimitGroupDialog_GroupName.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Group OF
		if (!this.groupOf.isDisabled()) {
			this.groupOf.setConstraint(new PTListValidator<ValueLabel>(Labels.getLabel("label_LimitGroupDialog_GroupOf.value"),
					PennantStaticListUtil.getGroupOfList(), true));
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		List<Listitem> items = this.listBoxLimitGroupLines.getItems();
		List<LimitGroupLines> list = new ArrayList<LimitGroupLines>();
		int k = 1;
		for (Listitem item : items) {
			LimitGroupLines lmtGrpItems = (LimitGroupLines) item.getAttribute("Data");
			lmtGrpItems.setItemSeq(k);
			k = k + 1;
			if (this.groupOf.getSelectedItem() != null && StringUtils.equals(LimitConstants.LIMIT_GROUP_LINE,
					this.groupOf.getSelectedItem().getValue().toString())) {
				Combobox lmtItem = (Combobox) item.getChildren().get(1).getFirstChild();
				try {
					lmtItem.setConstraint(
							new StaticListValidator(limitLineslist, Labels.getLabel("listheader_LimitItem.label")));
					String limtlineCode = lmtItem.getSelectedItem().getValue().toString();
					for (LimitGroupLines limitLine : list) {
						if (StringUtils.equals(limtlineCode, limitLine.getLimitLine())) {
							throw new WrongValueException(lmtItem,
									limtlineCode + " " + Labels.getLabel("Limit_Group_LimitLine_Existis"));
						}
					}
					if (!isInstitutionType && ruleCodesMap.containsKey(lmtGrpItems.getLimitLine())) {
						if (StringUtils.equals(LimitConstants.LIMIT_CATEGORY_BANK,
								ruleCodesMap.get(lmtGrpItems.getLimitLine()).getRuleEvent())) {
							isInstitutionType = true;
						}
					}
					String ermsg = null;
					if (active.isDisabled() && lmtGrpItems.isNewRecord())
						ermsg = validateLimitLines(lmtGrpItems.getLimitLine(), getLimitGroup().getGroupCode());
					if (ermsg != null) {
						throw new WrongValueException(lmtItem, ermsg);
					}
					list.add(lmtGrpItems);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			} else {
				Combobox lmtGrp = (Combobox) item.getChildren().get(0).getFirstChild().getChildren().get(0);
				try {
					lmtGrp.setConstraint(
							new StaticListValidator(limitGroupslist, Labels.getLabel("listheader_LimitGroup.label")));
					String limitGrp = lmtGrp.getSelectedItem().getValue().toString();
					if (StringUtils.equals(limitGrp, this.groupCode.getValue())) {
						throw new WrongValueException(lmtGrp, Labels.getLabel("Duplicate_LimitGroup"));
					}
					for (LimitGroupLines limitGroup : list) {
						if (StringUtils.equals(limitGrp, limitGroup.getGroupCode())) {
							throw new WrongValueException(lmtGrp, Labels.getLabel("DATA_ALREADY_EXISTS",
									new String[] { Labels.getLabel("listheader_LimitGroup.label") }));
						}
						if (limitGroup.getLimitLines() != null && lmtGrpItems.getLimitLines() != null) {
							if (!limitGroup.getLimitLines().equals(lmtGrpItems.getLimitLines())) {
								String[] lmtItems = limitGroup.getLimitLines().split("\\|");
								String[] lmtItems2 = lmtGrpItems.getLimitLines().split("\\|");
								for (String lmtItem : lmtItems) {
									for (String lmtItem2 : lmtItems2) {
										if (lmtItem.equals(lmtItem2)) {
											throw new WrongValueException(lmtGrp, Labels.getLabel("DATA_ALREADY_EXISTS",
													new String[] { Labels.getLabel("listheader_LimitGroup.label") }));
										}
									}
								}
							} else {
								throw new WrongValueException(lmtGrp, Labels.getLabel("DATA_ALREADY_EXISTS",
										new String[] { Labels.getLabel("listheader_LimitGroup.label") }));
							}
						} else {
							throw new WrongValueException(lmtGrp, Labels.getLabel("Limit_Group_Empty",
									new String[] { Labels.getLabel("listheader_LimitGroup.label") }));
						}
					}
					if (!isInstitutionType && groupCodesMap.containsKey(lmtGrpItems.getGroupCode())) {
						if (StringUtils.equals(LimitConstants.LIMIT_CATEGORY_BANK,
								groupCodesMap.get(lmtGrpItems.getGroupCode()).getLimitCategory())) {
							isInstitutionType = true;
						}
					}
					String errmsg = null;
					if (active.isDisabled() && lmtGrpItems.isNewRecord())
						errmsg = validateLimitGroups(getLimitGroup().getGroupCode(), lmtGrpItems.getGroupCode());
					if (errmsg != null) {
						throw new WrongValueException(lmtGrp, errmsg);
					}
					list.add(lmtGrpItems);

				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.groupCode.setConstraint("");
		this.groupName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {

	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {

	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	protected void refreshList() {
		getLimitGroupListCtrl().search();
	}

	public void doAddRow() {
		logger.debug("Entering");

		// Group Code

		if (this.groupOf.getSelectedItem() == null
				|| this.groupOf.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			throw new WrongValueException(groupOf, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_LimitGroupDialog_GroupOf.value") }));

		} else {
			LimitGroupLines limitGroupItems = new LimitGroupLines();
			limitGroupItems.setNewRecord(true);
			limitGroupItems.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			limitGroupItems.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			limitGroupItems.setVersion(limitGroupItems.getVersion() + 1);
			getLimitGroupLinesList().add(limitGroupItems);
			doFillListBox();
		}

		logger.debug("Leaving");
	}

	private void doFillListBox() {
		logger.debug("Entering");
		setLimitGroupLinesList(getLimitGroupLinesList());
		setLimitHeaders();
		getAssignedLimitGroupLinesListPagedListWrapper().initList(limitGroupItemsList, this.listBoxLimitGroupLines,
				new Paging());
		listBoxLimitGroupLines.setModel(new ListModelList<>(limitGroupItemsList));
		this.listBoxLimitGroupLines.setItemRenderer(new AssignedLimitGroupItemListModelItemRenderer());
		getLimitGroup().setLimitGroupLinesList(getLimitGroupLinesList());

		if ((getLimitGroupLinesList() != null) && (getLimitGroupLinesList().size() > 0)) {
			setComponentAccessType("LimitGroupDialog_GroupCode", true, this.groupOf, space_GroupOf, this.label_GroupOf,
					this.hlayout_GroupOf, null);
		} else {
			setComponentAccessType("LimitGroupDialog_GroupCode", false, this.groupOf, space_GroupOf, this.label_GroupOf,
					this.hlayout_GroupOf, null);
		}
		if (enqiryModule) {
			btnAddGroup.setVisible(false);
			btnAddLimitLine.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private class AssignedLimitGroupItemListModelItemRenderer implements ListitemRenderer<LimitGroupLines> {

		@Override
		public void render(Listitem item, LimitGroupLines limitGroupItems, int count) {
			logger.debug("Entering");
			Listcell lc;
			Combobox limitGroupCode = new Combobox();
			Combobox limitLineCode = new Combobox();

			if (groupOf.getSelectedItem() != null) {
				if (StringUtils.equals(LimitConstants.LIMIT_GROUP_GROUP,
						groupOf.getSelectedItem().getValue().toString())) {

					lc = getListcell(item);

					limitGroupCode.setWidth("100%");
					fillComboBox(limitGroupCode, limitGroupItems.getGroupCode(), limitGroupslist, "");
					limitGroupCode.addForward("onChange", self, "onChangeLimitGroup");
					/*
					 * limitGroupCode.setConstraint(new StaticListValidator( limitGroupslist,
					 * Labels.getLabel("listheader_LimitGroupDialog.label")));
					 */
					Vbox vbox = new Vbox();
					vbox.setWidth("100%");
					lc.appendChild(vbox);
					vbox.appendChild(limitGroupCode);

					lc = getListcell(item);

					Vbox vboxCell = new Vbox();
					vboxCell.setWidth("100%");
					lc.appendChild(vboxCell);

					addSubGroups(item, limitGroupItems, vbox, vboxCell);
				} else {
					lc = getListcell(item);
					lc = getListcell(item);

					limitLineCode.setWidth("60%");
					fillComboBox(limitLineCode, limitGroupItems.getLimitLine(), limitLineslist, "");
					limitLineCode.addForward("onChange", self, "onChangelimitItem");
					/*
					 * limitLineCode.setConstraint(new StaticListValidator( limitLineslist,
					 * Labels.getLabel("listheader_LimitItemDialog.label")));
					 */
					lc.appendChild(limitLineCode);
				}
			}

			lc = getListcell(item);
			Button delete = new Button();
			delete.setLabel(Labels.getLabel("label_Remove"));
			delete.addForward("onClick", self, "onClickRemove");
			delete.setParent(lc);

			if (!limitGroupItems.isNewRecord()) {
				limitLineCode.setDisabled(true);
				limitGroupCode.setDisabled(true);

				readOnlyComponent(isReadOnly("button_LimitGroupDialog_NewLimitGroupItem") || active.isDisabled()
						|| !(StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, limitGroupItems.getRecordType())),
						delete);
			}
			// readOnlyComponent(!getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem"), delete);

			if (limitGroupItems.getKey() == 0) {
				key += 1;
				limitGroupItems.setKey(key);
			}

			if (enqiryModule || StringUtils.equalsIgnoreCase(getLimitGroup().getRecordType(),
					PennantConstants.RECORD_TYPE_DEL)) {
				limitLineCode.setDisabled(true);
				limitGroupCode.setDisabled(true);
				readOnlyComponent(true, delete);
			}

			item.setAttribute("Data", limitGroupItems);

			logger.debug("Leaving");
		}

		private Listcell getListcell(Listitem item) {
			Listcell lc = new Listcell();
			lc.setParent(item);
			return lc;
		}

		private void addSubGroups(Listitem item, LimitGroupLines limitGroupItems, Vbox vbox, Vbox vboxcell) {
			List<LimitGroupLines> groupItems = PennantAppUtil.getLimitSubGroups(limitGroupItems.getGroupCode(), true,
					false);

			if (groupItems != null && groupItems.size() > 0) {
				Space space = new Space();
				vboxcell.appendChild(space);
				if (indent == null)
					indent = "|___";
				else
					indent = indent + "___";
				for (LimitGroupLines groupItem : groupItems) {
					vbox.appendChild(new Label(indent + groupItem.getGroupName()));
					addSubGroups(item, groupItem, vbox, vboxcell);
				}
				indent = indent.replaceFirst("___", "");
			} else {
				Label label = new Label();
				label.setValue(limitGroupItems.getLimitLines());
				label.setParent(vboxcell);
			}
		}
	}

	public void onChangeLimitGroup(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Combobox limitGroup = (Combobox) event.getOrigin().getTarget();
		LimitGroupLines limitGroupItems = (LimitGroupLines) limitGroup.getParent().getParent().getParent()
				.getAttribute("Data");
		if (limitGroup.getValue() != null && !StringUtils.equals(PennantConstants.List_Select,
				limitGroup.getSelectedItem().getValue().toString())) {
			limitGroupItems.setGroupCode(limitGroup.getSelectedItem().getValue().toString());
			String itemCodes = getLimitGroupService().getLimitLines(limitGroupItems.getGroupCode());
			if (itemCodes != null) {
				limitGroupItems.setLimitLines(itemCodes);

			} else {
				MessageUtil.showError(
						Labels.getLabel("Limit_Group_Empty", new String[] { limitGroupItems.getGroupCode() }));
				return;
			}
			writeValuetoBean(limitGroupItems);

		} else {
			limitGroupItems.setGroupCode("");
			limitGroupItems.setLimitLines("");
		}
		doFillListBox();
		logger.debug("Leaving" + event.toString());
	}

	public void onChangelimitItem(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Combobox limitItem = (Combobox) event.getOrigin().getTarget();
		LimitGroupLines limitGroupItems = (LimitGroupLines) limitItem.getParent().getParent().getAttribute("Data");
		if (limitItem.getValue() != null) {
			limitGroupItems.setLimitLine(limitItem.getSelectedItem().getValue().toString());
			limitGroupItems.setLimitLines(limitItem.getSelectedItem().getValue().toString());
			/*
			 * if(validateLimitLines(limitGroupItems.getLimitLine(),getLimitGroup().getGroupCode())){ return; }
			 */
			writeValuetoBean(limitGroupItems);
		} else {
			limitGroupItems.setLimitLine("");
		}
		logger.debug("Leaving" + event.toString());
	}

	private String validateLimitGroups(String limitgroup, String groupCode) throws InterruptedException {
		String errorMsg = null;
		List<LimitStructureDetail> structureList1 = getLimitGroupService()
				.getStructuredetailsByLimitGroup(getLimitGroup().getLimitCategory(), limitgroup, false, "_View");
		List<LimitStructureDetail> structureList2 = getLimitGroupService()
				.getStructuredetailsByLimitGroup(getLimitGroup().getLimitCategory(), groupCode, false, "_View");

		if (active.isDisabled()) {
			for (LimitStructureDetail structureCode : structureList1) {
				for (LimitStructureDetail groupStructureCode : structureList2) {
					if (StringUtils.equals(structureCode.getLimitStructureCode(),
							groupStructureCode.getLimitStructureCode())) {
						errorMsg = Labels.getLabel("Limit_Group_Str_duplicate",
								new String[] { groupCode, limitgroup, structureCode.getLimitStructureCode() });
						return errorMsg;
					}

				}
			}
		}
		String result = groupCode;
		result = getGroupcodes(null, groupCode, result);
		List<LimitGroupLines> groupsList = getLimitGroupService().getGroupCodesByLimitGroup(result, false);
		for (LimitGroupLines lines : groupsList) {
			if (StringUtils.equals(lines.getGroupCode(), groupCode)) {
				errorMsg = Labels.getLabel("Limit_Group_Duplicate",
						new String[] { groupCode, limitgroup, lines.getLimitGroupCode() });
				return errorMsg;
			}
		}
		return errorMsg;
	}

	public String validateLimitLines(String limitLine, String limitgroup) throws InterruptedException {
		String errorMsg = null;

		List<LimitStructureDetail> structureList1 = getLimitGroupService()
				.getStructuredetailsByLimitGroup(getLimitGroup().getLimitCategory(), limitgroup, false, "_View");
		List<LimitStructureDetail> structureList2 = getLimitGroupService()
				.getStructuredetailsByLimitGroup(getLimitGroup().getLimitCategory(), limitLine, true, "_View");

		if (active.isDisabled()) {
			for (LimitStructureDetail structureCode : structureList1) {
				for (LimitStructureDetail groupStructureCode : structureList2) {
					if (StringUtils.equals(structureCode.getLimitStructureCode(),
							groupStructureCode.getLimitStructureCode())) {
						errorMsg = Labels.getLabel("Limit_Group_Str_duplicate",
								new String[] { limitLine, limitgroup, structureCode.getLimitStructureCode() });
						return errorMsg;
					}

				}
			}
		}

		String result = null;
		result = getGroupcodes(limitLine, null, result);

		List<LimitGroupLines> groupsList = getLimitGroupService().getGroupCodesByLimitGroup(result, false);

		for (LimitGroupLines lines : groupsList) {
			if (StringUtils.equals(lines.getGroupCode(), limitLine)) {
				result = Labels.getLabel("Limit_Group_LimitLine",
						new String[] { limitLine, limitgroup, lines.getLimitGroupCode() });
				return result;
			}
		}
		return errorMsg;
	}

	private String getGroupcodes(String line, String group, String result) {
		String groupcode = null;
		if (line != null) {
			groupcode = getLimitGroupService().getGroupcodes(line, true);
		} else if (group != null) {
			groupcode = getLimitGroupService().getGroupcodes(group, false);
		}

		if (groupcode != null) {

			result = (result != null ? result + "," + groupcode : groupcode);
			result = getGroupcodes(null, groupcode, result);
		}

		return result;
	}

	public void onClickRemove(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Button limitGroup = (Button) event.getOrigin().getTarget();
		LimitGroupLines limitGroups = (LimitGroupLines) limitGroup.getParent().getParent().getAttribute("Data");

		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		if (limitGroups.getLimitLine() != null)
			msg = msg + "\n\n --> "
					+ (limitGroups.getLimitLine() == null ? limitGroups.getGroupCode() : limitGroups.getLimitLine());

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				if (limitGroups.getRecordStatus() != null && limitGroups.getRecordType() != null
						&& limitGroups.getRecordType().equals("")) {
					if (limitGroupService.isLineUsingInUtilization(limitGroups.getLimitLines())) {
						clearChild(limitGroups);
					} else {
						MessageUtil.showError(
								Labels.getLabel("LIMIT_FIELD_MODIFY", new String[] { this.limitGroup.getGroupCode() }));
						return;
					}
				} else {
					clearChild(limitGroups);
				}
			}
		});

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void clearChild(LimitGroupLines limitGroupItems) {
		logger.debug("Entering");
		setRecordTypes(limitGroupItems);
		for (LimitGroupLines list : getLimitGroupLinesList()) {
			if (limitGroupItems.getKey() == list.getKey()) {
				getLimitGroupLinesList().remove(list);
				break;
			}
		}
		if (getLimitGroupLinesList() == null || getLimitGroupLinesList().size() <= 0)
			setComponentAccessType("LimitGroupDialog_GroupCode", true, this.groupOf, space_GroupOf, this.label_GroupOf,
					this.hlayout_GroupOf, null);

		doFillListBox();
		logger.debug("Leaving");
	}

	private void setLimitHeaders() {
		logger.debug("Entering");
		if (getLimitGroupLinesList() != null && groupOf.getSelectedItem() != null && (StringUtils
				.equals(LimitConstants.LIMIT_GROUP_LINE, groupOf.getSelectedItem().getValue().toString()))) {
			listheader_LimitGroup.setVisible(false);
			listheader_LimitLine.setVisible(true);
			this.btnAddLimitLine
					.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem") && true);
			this.btnAddGroup
					.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem") && false);
		} else if (!getLimitGroup().isNewRecord()) {
			this.btnAddLimitLine
					.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem") && false);
			this.btnAddGroup
					.setVisible(getUserWorkspace().isAllowed("button_LimitGroupDialog_NewLimitGroupItem") && true);
			listheader_LimitGroup.setVisible(true);
			listheader_LimitLine.setVisible(true);
		}
		logger.debug("Leaving");
	}

	private void setRecordTypes(LimitGroupLines limitGroupItems) {
		logger.debug("Entering");
		if (limitGroupItems.getRecordType() != null
				&& StringUtils.trimToEmpty(limitGroupItems.getRecordType()).equals("")) {

			limitGroupItems.setNewRecord(true);
			limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			deleteMap.put(limitGroupItems.getKey(), limitGroupItems);
		} else if (PennantConstants.RECORD_TYPE_NEW.equals(limitGroupItems.getRecordType())) {
			if (!limitGroupItems.isNewRecord()) {
				limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				deleteMap.put(limitGroupItems.getKey(), limitGroupItems);
			} else {
				limitGroupItems.setRecordType("");
				limitGroupItems.setNewRecord(false);
			}
		}
		logger.debug("Leaving");
	}

	private void writeValuetoBean(LimitGroupLines limitGroupItems) {
		logger.debug("Entering");

		if (limitGroupItems.getRecordType() != null
				&& StringUtils.trimToEmpty(limitGroupItems.getRecordType()).equals("")) {

			limitGroupItems.setVersion(limitGroupItems.getVersion() + 1);
			if (limitGroupItems.isNewRecord()) {
				limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				limitGroupItems.setNewRecord(true);
			}
		}
		logger.debug("Leaving");
	}

	// *****************************************************************
	// ************************+ crud operations ***********************
	// *****************************************************************

	protected void onDoDelete(final LimitGroup aLimitGroup) {
		String tranType = PennantConstants.TRAN_WF;
		boolean flag = getLimitGroupService().validationCheck(aLimitGroup.getGroupCode());
		if (flag) {
			if (StringUtils.isBlank(aLimitGroup.getRecordType())) {
				aLimitGroup.setVersion(aLimitGroup.getVersion() + 1);
				aLimitGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aLimitGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aLimitGroup.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLimitGroup.getNextTaskId(),
							aLimitGroup);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aLimitGroup, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		} else {
			MessageUtil
					.showError(Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { getLimitGroup().getGroupCode() }));
			return;
		}
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final LimitGroup aLimitGroup = new LimitGroup();
		BeanUtils.copyProperties(getLimitGroup(), aLimitGroup);

		doDelete(aLimitGroup.getGroupCode(), aLimitGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.groupCode.setValue("");
		this.groupName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LimitGroup aLimitGroup = new LimitGroup();
		BeanUtils.copyProperties(getLimitGroup(), aLimitGroup);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aLimitGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLimitGroup.getNextTaskId(), aLimitGroup);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aLimitGroup.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the LimitGroup object with the components data
			doWriteComponentsToBean(aLimitGroup);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aLimitGroup.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLimitGroup.getRecordType()).equals("")) {
				aLimitGroup.setVersion(aLimitGroup.getVersion() + 1);
				if (isNew) {
					aLimitGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLimitGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLimitGroup.setNewRecord(true);
				}
			}
		} else {
			aLimitGroup.setVersion(aLimitGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aLimitGroup, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(LimitGroup aLimitGroup, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aLimitGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLimitGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLimitGroup.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (aLimitGroup.isNewRecord()) {
			aLimitGroup.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aLimitGroup.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		}
		if (isWorkFlowEnabled()) {
			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			aLimitGroup.setTaskId(getTaskId());
			aLimitGroup.setNextTaskId(getNextTaskId());
			aLimitGroup.setRoleCode(getRole());
			aLimitGroup.setNextRoleCode(getNextRoleCode());
			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aLimitGroup, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aLimitGroup, PennantConstants.TRAN_WF);
				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aLimitGroup, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		LimitGroup aLimitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getLimitGroupService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getLimitGroupService().saveOrUpdate(auditHeader);
				}
			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLimitGroupService().doApprove(auditHeader);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLimitGroup.getRecordType())) {
						deleteNotes = true;
					}
				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLimitGroupService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aLimitGroup.getRecordType())) {
						deleteNotes = true;
					}
				} else {
					retValue = ErrorControl.showErrorControl(this.window_LimitGroupDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_LimitGroupDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.limitGroup), true);
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

	@Override
	protected String getReference() {
		return this.groupCode.getValue();
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LimitGroup aLimitGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLimitGroup.getBefImage(), aLimitGroup);
		return new AuditHeader(aLimitGroup.getGroupCode(), null, null, null, auditDetail, aLimitGroup.getUserDetails(),
				getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitGroup getLimitGroup() {
		return this.limitGroup;
	}

	public void setLimitGroup(LimitGroup limitGroup) {
		this.limitGroup = limitGroup;
	}

	public void setLimitGroupService(LimitGroupService limitGroupService) {
		this.limitGroupService = limitGroupService;
	}

	public LimitGroupService getLimitGroupService() {
		return this.limitGroupService;
	}

	public void setLimitGroupListCtrl(LimitGroupListCtrl limitGroupListCtrl) {
		this.limitGroupListCtrl = limitGroupListCtrl;
	}

	public LimitGroupListCtrl getLimitGroupListCtrl() {
		return this.limitGroupListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	@SuppressWarnings("unchecked")
	public void setAssignedLimitGrouLinesListPagedListWrapper() {
		if (this.assignedLimitGroupLinesPagedListWrapper == null) {
			this.assignedLimitGroupLinesPagedListWrapper = (PagedListWrapper<LimitGroupLines>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<LimitGroupLines> getAssignedLimitGroupLinesListPagedListWrapper() {
		return assignedLimitGroupLinesPagedListWrapper;
	}

	public List<LimitGroupLines> getLimitGroupLinesList() {
		return limitGroupItemsList;
	}

	public void setLimitGroupLinesList(List<LimitGroupLines> limitGroupItemsList) {
		this.limitGroupItemsList = limitGroupItemsList;
	}

}
