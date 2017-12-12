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
 * FileName    		:  LimitStructureDialogCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                                          *
 *                                                                  						*
 * Description 		:                                             							*
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * itemsListByrulecode
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
package com.pennant.webui.limit.limitstructure;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
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
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.limit.LimitStructureService;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Limit/LimitStructure/limitStructureDialog.zul file. <br>
 * ************************************************************<br>
 */
public class LimitStructureDialogCtrl extends GFCBaseCtrl<LimitStructure> implements Serializable {
	private static final long						serialVersionUID				= 1L;
	private static final Logger						logger							= Logger.getLogger(LimitStructureDialogCtrl.class);

	/*
	 * ************************************************************************
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ************************************************************************
	 */
	protected Window								window_LimitStructureDialog;
	protected Row									row0;
	protected Label									label_StructureCode;
	protected Hlayout								hlayout_StructureCode;
	protected Space									space_StructureCode;

	protected Textbox								structureCode;
	protected Label									label_StructureName;
	protected Hlayout								hlayout_StructureName;
	protected Space									space_StructureName;

	protected Textbox								structureName;
	protected Row									row1;
	protected Label									window_LimitStructureDialog_title;
	protected Space									space_Active;

	protected Label									label_ShowLimitsIn;
	protected Hlayout								hlayout_ShowLimitsIn;
	protected Space									space_ShowLimitsIn;
	protected Combobox								showLimitsIn;

	protected Checkbox								active;
	int												itemSeq							= 1;

	// not auto wired vars
	private LimitStructure							limitStructure;																	// overhanded per param
	private transient LimitStructureListCtrl		limitStructureListCtrl;															// overhanded

	protected Button								btnGroup;
	protected Button								btnTop;
	protected Button								btnUp;
	protected Button								btnDown;
	protected Button								btnBottom;

	protected Listheader							listheader_Revolving;
	protected Listheader							listheader_Editable;

	private static Map<String, LimitGroup>			limitGroupsMap;
	private static List<ValueLabel>					limitDisplayStyle;
	private List<ValueLabel>						limitGroupslist;

	private int										key								= 0;
	private int										indentLevel						= 0;
	protected String								indent							= null;
	protected boolean								isInstitutionType				= false;
	protected Checkbox								editable;
	protected Checkbox								revolving;
	protected Checkbox								check;
	protected Combobox								displayStyle;
	protected Button								btnRemove;

	private List<LimitStructureDetail>				limitStructureDetailItemsList	= new ArrayList<LimitStructureDetail>();
	protected Map<Integer, LimitStructureDetail>	deleteMap						= new HashMap<Integer, LimitStructureDetail>();
	protected Map<Integer, LimitStructureDetail>	addHashMap						= new HashMap<Integer, LimitStructureDetail>();
	protected Map<String, LimitStructureDetail>		assignedHashMap					= new HashMap<String, LimitStructureDetail>();

	protected Listbox								listBoxLimitStructureDetailItems;
	private PagedListWrapper<LimitStructureDetail>	assignedLimitStructureDetailItemsPagedListWrapper;
	protected Paging								pagingLimitStructureDetail;

	// ServiceDAOs / Domain Classes
	private transient LimitStructureService			limitStructureService;
	private transient PagedListService				pagedListService;
	private List<ValueLabel>						listCurrencyUnits;

	/**
	 * default constructor.<br>
	 */
	public LimitStructureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LimitStructureDetailDialog";
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LimitStructure object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LimitStructureDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		setPageComponents(window_LimitStructureDialog);
		try {
			// Get the required arguments.

			this.limitStructure = (LimitStructure) arguments.get("limitStructure");
			setLimitStructureListCtrl((LimitStructureListCtrl) arguments.get("limitStructureListCtrl"));
			if (this.limitStructure == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			LimitStructure befImage = new LimitStructure();
			BeanUtils.copyProperties(this.limitStructure, befImage);
			this.limitStructure.setBefImage(befImage);

			setLimitStructureDetailItemsList(limitStructure.getLimitStructureDetailItemsList());

			// Render the page and display the data.
			doLoadWorkFlow(this.limitStructure.isWorkflow(), this.limitStructure.getWorkflowId(),
					this.limitStructure.getNextTaskId());

			if (StringUtils.equals(LimitConstants.LIMIT_CATEGORY_CUST, getLimitStructure().getLimitCategory())) {
				limitGroupsMap = PennantAppUtil.getLimitGroup(getLimitStructure().getLimitCategory(), true);
			} else {
				limitGroupsMap = PennantAppUtil.getLimitGroup(null, true);
				listheader_Revolving.setVisible(false);
				listheader_Editable.setVisible(false);
			}
			listCurrencyUnits = PennantStaticListUtil.getCurrencyUnits();
			limitDisplayStyle = PennantStaticListUtil.getLimitDisplayStyle();
			setAssignedLimitStructureDetailItemsPagedListWrapper();

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			this.listBoxLimitStructureDetailItems
					.setItemRenderer(new AssignedLimitStructureDetailItemListModelRenderer());

			// set Field Properties
			doCheckRights();
			doSetFieldProperties();
			doShowDialog(this.limitStructure);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LimitStructureDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Add Group" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnGroup(Event event) {
		logger.debug("Entering" + event.toString());
		LimitStructureDetail aLimitStructureDetail = new LimitStructureDetail();
		aLimitStructureDetail.setGroupCode("");
		doAddNewRow(aLimitStructureDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Add Item" or "Add Group" button is clicked. <br>
	 * 
	 * @param aLimitStructureDetail
	 */
	/*
	 * public void validateInsertingRow(LimitStructureDetail aLimitStructureDetail) { logger.debug("Entering");
	 * doSetValidation(); ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
	 * 
	 * // Structure Code try { if (this.structureCode.getValue().equalsIgnoreCase("")) { wve.add(new
	 * WrongValueException()); } } catch (WrongValueException we) { wve.add(we); } // Structure Name try { if
	 * (this.structureName.getValue().equalsIgnoreCase("")) { wve.add(new WrongValueException()); } } catch
	 * (WrongValueException we) { wve.add(we); }
	 * 
	 * if (!wve.isEmpty()) { WrongValueException[] wvea = new WrongValueException[wve.size()]; for (int i = 0; i <
	 * wve.size(); i++) { wvea[i] = (WrongValueException) wve.get(i); } throw new WrongValuesException(wvea); }
	 * logger.debug("Leaving"); }
	 */

	/**
	 * when the "top" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnTop(Event event) {
		logger.debug("Entering" + event.toString());
		int count = listBoxLimitStructureDetailItems.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Item or Group. Please Add Either Item or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitStructureDetailItems.getSelectedItem();
			if (item != null && listBoxLimitStructureDetailItems.getFirstChild() != null) {
				listBoxLimitStructureDetailItems.insertBefore(item, listBoxLimitStructureDetailItems.getFirstChild());
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
		int count = listBoxLimitStructureDetailItems.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Item or Group. Please Add Either Item or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitStructureDetailItems.getSelectedItem();
			if (item != null && item.getPreviousSibling() != null) {
				listBoxLimitStructureDetailItems.insertBefore(item, item.getPreviousSibling());
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
		int count = listBoxLimitStructureDetailItems.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Item or Group. Please Add Either Item or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitStructureDetailItems.getSelectedItem();
			if (item != null && item.getNextSibling() != null) {
				listBoxLimitStructureDetailItems.insertBefore(item.getNextSibling(), item);
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
		int count = listBoxLimitStructureDetailItems.getItems().size();
		if (count == 0) {
			MessageUtil.showError("This Structure doesn't Contain any Item or Group. Please Add Either Item or Group.");
		} else if (count > 1) {
			Listitem item = this.listBoxLimitStructureDetailItems.getSelectedItem();
			if (item != null) {
				listBoxLimitStructureDetailItems.insertBefore(item, null);
			} else {
				MessageUtil.showError("Please select one Limit Structure Detail");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		if (!active.isDisabled()) {
			doDelete();
		} else {
			MessageUtil.showError(
					Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { getLimitStructure().getStructureCode() }));
		}
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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

		doWriteBeanToComponents(this.limitStructure.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.limitStructure);
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLimitStructure
	 * @throws InterruptedException
	 */
	public void doShowDialog(LimitStructure aLimitStructure) throws InterruptedException {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aLimitStructure.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.structureCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.structureName.focus();
				if (StringUtils.isNotBlank(aLimitStructure.getRecordType()) && !enqiryModule) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aLimitStructure);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_LimitStructureDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void doAddNewRow(LimitStructureDetail limitStructureDetail) {
		logger.debug("Entering");
		limitStructureDetail.setNewRecord(true);
		limitStructureDetail.setEditable(true);
		limitStructureDetail.setRevolving(true);
		limitStructureDetail.setLimitCheck(true);
		limitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		limitStructureDetail.setVersion(limitStructureDetail.getVersion() + 1);
		limitStructureDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		limitStructureDetail.setCreatedBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());

		//validateInsertingRow(limitStructureDetail);
		key = key + 1;
		limitStructureDetail.setItemPriority(key);
		addHashMap.put(key, limitStructureDetail);
		doFillListBox();
		logger.debug("Leaving");
	}

	private void doFillListBox() {
		logger.debug("Entering");
		setLimitStructureDetailItemsList(new ArrayList<>(addHashMap.values()));

		getAssignedLimitStructureDetailItemsPagedListWrapper().initList(getLimitStructureDetailItemsList(),
				this.listBoxLimitStructureDetailItems, new Paging());
		listBoxLimitStructureDetailItems.setModel(new ListModelList<>(getLimitStructureDetailItemsList()));
		this.listBoxLimitStructureDetailItems.setItemRenderer(new AssignedLimitStructureDetailItemListModelRenderer());
		logger.debug("Leaving");
	}

	private List<ValueLabel> getValueLabelsFromGroupMap() {
		List<ValueLabel> groupsList = new ArrayList<ValueLabel>();
		for (LimitGroup limitGroup : limitGroupsMap.values()) {
			groupsList.add(new ValueLabel(limitGroup.getId(), limitGroup.getGroupName() + "-" + limitGroup.getId()));
		}
		return groupsList;
	}

	private class AssignedLimitStructureDetailItemListModelRenderer implements ListitemRenderer<LimitStructureDetail> {

		@Override
		public void render(Listitem item, LimitStructureDetail limitStructureDetail, int count) throws Exception {
			logger.debug("Entering");
			Combobox groupCode;
			Listcell lc;
			itemSeq = 1;
			Vbox vbox = new Vbox();
			vbox.setWidth("100%");

			Vbox vbox2 = new Vbox();
			vbox2.setWidth("100%");

			Vbox vbox3 = new Vbox();
			vbox3.setWidth("100%");

			Vbox vbox4 = new Vbox();
			vbox4.setWidth("100%");

			Vbox vbox5 = new Vbox();
			vbox5.setWidth("100%");

			/* 1. Group Code */
			lc = new Listcell();
			lc.setParent(item);
			groupCode = new Combobox();

			if (limitStructureDetail.getGroupCode() != null) {

				groupCode.setWidth("100%");
				fillComboBox(groupCode, limitStructureDetail.getGroupCode(), limitGroupslist, "");
				groupCode.addForward("onChange", self, "onChangeGroupCode");
				lc.appendChild(groupCode);
				groupCode.setId("GroupCode" + count);
				readOnlyComponent(getUserWorkspace().isReadOnly("LimitStructureDetailDialog_LimitStructureCode")
						|| !limitStructureDetail.isNewRecord(), groupCode);

				setValuesToComponent(limitStructureDetail, vbox2, vbox3, vbox4, vbox5);
				addSubGroups(limitStructureDetail, vbox, vbox2, vbox3, vbox4, vbox5);

				lc.appendChild(vbox);

				lc = getListcell(vbox2, item);

				lc = getListcell(vbox3, item);

				lc = getListcell(vbox4, item);

				lc = getListcell(vbox5, item);

			}

			/* 5. Remove */
			lc = new Listcell();
			lc.setParent(item);
			btnRemove = new Button();
			btnRemove.setLabel(LimitConstants.LIMIT_STRUCTURE_DIALOG_BTN_REMOVE_LABEL);
			btnRemove.addForward("onClick", self, "onClickRemove");
			btnRemove.setParent(lc);
			if (!limitStructureDetail.isNew())
				readOnlyComponent(
						isReadOnly("LimitStructureDetailDialog_Delete")
								|| active.isDisabled()
								|| !(StringUtils.equals(PennantConstants.RECORD_TYPE_NEW,
										limitStructureDetail.getRecordType())), btnRemove);

			if (enqiryModule
					|| StringUtils.equals(getLimitStructure().getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				groupCode.setDisabled(true);
				readOnlyComponent(true, btnRemove);
				displayStyle.setDisabled(true);
				editable.setDisabled(true);
				check.setDisabled(true);
			}

			limitStructureDetail.setKey(count);
			item.setAttribute("Data", limitStructureDetail);

			logger.debug("Leaving");
		}

		private void addSubGroups(LimitStructureDetail limitStructureDetails, Vbox vbox, Vbox vbox2, Vbox vbox3,
				Vbox vbox4, Vbox vbox5) {
			List<LimitGroupLines> groupItems = PennantAppUtil.getLimitSubGroups(limitStructureDetails.getGroupCode(),
					true, false);

			if (indent == null) {
				indent = "|___";
			} else {
				indent = indent + "___";
			}
			indentLevel = indentLevel + 1;
			if (groupItems != null && groupItems.size() > 0) {
				for (LimitGroupLines groupItem : groupItems) {
					itemSeq = itemSeq + 1;
					LimitStructureDetail detail = new LimitStructureDetail();

					if (limitStructureDetails.getSubGroupsMap().containsKey("GGC_" + groupItem.getGroupCode())) {
						detail = limitStructureDetails.getSubGroupsMap().get("GGC_" + groupItem.getGroupCode());
					} else {
						BeanUtils.copyProperties(groupItem, detail);
						detail.setEditable(true);
						detail.setLimitCheck(true);
						detail.setNewRecord(true);
						detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						limitStructureDetails.getSubGroupsMap().put("GGC_" + groupItem.getGroupCode(), detail);
					}
					detail.setItemSeq(itemSeq);
					detail.setItemLevel(indentLevel);
					vbox.setStyle("padding:10px 0px;");
					vbox.appendChild(new Label(indent + detail.getGroupName()));
					setValuesToComponent(detail, vbox2, vbox3, vbox4, vbox5);
					addSubGroups(detail, vbox, vbox2, vbox3, vbox4, vbox5);
				}
			} else {
				List<LimitGroupLines> Items = PennantAppUtil.getLimitSubGroups(limitStructureDetails.getGroupCode(),
						false, true);
				if (Items != null && Items.size() > 0)
					for (LimitGroupLines Item : Items) {
						LimitStructureDetail detail = new LimitStructureDetail();
						itemSeq = itemSeq + 1;

						if (limitStructureDetails.getSubGroupsMap().containsKey("LLC_" + Item.getLimitLine())) {
							detail = limitStructureDetails.getSubGroupsMap().get("LLC_" + Item.getLimitLine());
						} else {
							detail.setLimitLine(Item.getLimitLine());
							detail.setLimitLineDesc(Item.getLimitLineDesc());
							detail.setEditable(true);
							detail.setLimitCheck(true);
							detail.setNewRecord(true);
							detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							limitStructureDetails.getSubGroupsMap().put("LLC_" + Item.getLimitLine(), detail);
						}
						detail.setItemSeq(itemSeq);
						detail.setItemLevel(indentLevel);
						Label label = new Label();
						label.setValue(indent + Item.getLimitLineDesc());
						label.setParent(vbox);
						vbox.setStyle("padding:10px 0px;");
						setValuesToComponent(detail, vbox2, vbox3, vbox4, vbox5);
					}
			}
			indent = indent.replaceFirst("___", "");
			indentLevel = indentLevel - 1;
		}

		private void setValuesToComponent(LimitStructureDetail detail, Vbox vbox2, Vbox vbox3, Vbox vbox4, Vbox vbox5) {

			editable = new Checkbox();
			editable.setChecked(detail.isEditable());
			editable.addForward("onClick", self, "onClickEdiatble");
			readOnlyComponent(getUserWorkspace().isReadOnly("LimitStructureDetailDialog_GroupCode"), editable);
			vbox3.appendChild(editable);
			editable.setAttribute("Data", detail);

			revolving = new Checkbox();
			revolving.setChecked(detail.isRevolving());
			revolving.addForward("onClick", self, "onClickRevolving");
			readOnlyComponent(getUserWorkspace().isReadOnly("LimitStructureDetailDialog_GroupCode"), revolving);
			if (detail.getLimitLine() != null) {
				vbox2.appendChild(revolving);
			} else {
				vbox2.appendChild(new Space());
			}

			revolving.setAttribute("Data", detail);

			displayStyle = new Combobox();
			fillComboBoxWithLabel(displayStyle, detail.getDisplayStyle(), limitDisplayStyle, "");
			/*
			 * if (!displayStyle.isDisabled()) { displayStyle.setConstraint(new StaticListValidator( limitDisplayStyle,
			 * Labels .getLabel("listheader_DisplayStyle.label"))); }
			 */

			displayStyle.addForward("onChange", self, "onChangeDisplayStyle");
			readOnlyComponent(getUserWorkspace().isReadOnly("LimitStructureDetailDialog_GroupCode"), displayStyle);
			vbox4.appendChild(displayStyle);
			displayStyle.setAttribute("Data", detail);

			check = new Checkbox();
			check.setStyle("padding:2px 0px");
			check.setChecked(detail.isLimitCheck());
			check.addForward("onClick", self, "onClickofCheck");
			readOnlyComponent(getUserWorkspace().isReadOnly("LimitStructureDetailDialog_GroupCode"), check);
			vbox5.appendChild(check);
			check.setAttribute("Data", detail);
		}

		private Listcell getListcell(Vbox vbox, Listitem item) {
			Listcell lc = new Listcell();
			lc.setParent(item);
			lc.appendChild(vbox);
			return lc;
		}
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBoxWithLabel(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setStyle(PennantStaticListUtil.getLimitDetailStyle(valueLabel.getValue()));
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	public void onChangeGroupCode(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Combobox groupCode = (Combobox) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetailItems = (LimitStructureDetail) groupCode.getParent().getParent()
				.getAttribute("Data");
		limitStructureDetailItems.setSubGroupsMap(new HashMap<String, LimitStructureDetail>());
		if (groupCode.getValue() != null
				&& !StringUtils.equals(PennantConstants.List_Select, groupCode.getSelectedItem().getValue().toString())) {
			limitStructureDetailItems.setGroupCode(groupCode.getSelectedItem().getValue().toString());
			writeValuetoBean(limitStructureDetailItems);
		}
		doFillListBox();
		logger.debug("Leaving" + event.toString());
	}

	public void onChangeDisplayStyle(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Combobox displayStyle = (Combobox) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetails = (LimitStructureDetail) displayStyle.getAttribute("Data");
		if (displayStyle.getValue() != null) {
			limitStructureDetails.setDisplayStyle(displayStyle.getSelectedItem().getValue().toString());
			writeValuetoBean(limitStructureDetails);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClickEdiatble(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Checkbox editabl = (Checkbox) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetails = (LimitStructureDetail) editabl.getAttribute("Data");
		limitStructureDetails.setEditable(editabl.isChecked());
		writeValuetoBean(limitStructureDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickRevolving(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Checkbox revolving = (Checkbox) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetails = (LimitStructureDetail) revolving.getAttribute("Data");
		limitStructureDetails.setRevolving(revolving.isChecked());
		writeValuetoBean(limitStructureDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickofCheck(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Checkbox limitCheck = (Checkbox) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetails = (LimitStructureDetail) limitCheck.getAttribute("Data");
		limitStructureDetails.setLimitCheck(limitCheck.isChecked());
		writeValuetoBean(limitStructureDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickRemove(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Button remove = (Button) event.getOrigin().getTarget();
		LimitStructureDetail limitStructureDetails = (LimitStructureDetail) remove.getParent().getParent()
				.getAttribute("Data");
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		int count = getLimitStructureService().limitStructureCheck(getLimitStructure().getStructureCode());
		if (count == 0 || limitStructureDetails.isNew()) {
			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				logger.debug("doDelete: Yes");
				if (!limitStructureDetails.isNew()) {
					if (limitStructureDetails.getRecordType() != null
							&& StringUtils.trimToEmpty(limitStructureDetails.getRecordType()).isEmpty()) {
						limitStructureDetails.setNewRecord(true);
						limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW,
							limitStructureDetails.getRecordType())) {
						limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					} else {
						limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}
					deleteMap.put(limitStructureDetails.getItemPriority(), limitStructureDetails);
					doClearChild(limitStructureDetails);
				} else {
					doClearChild(limitStructureDetails);
				}
			}
		} else {
			MessageUtil.showError(
					Labels.getLabel("LIMIT_FIELD_MODIFY", new String[] { getLimitStructure().getStructureCode() }));
			return;
		}
		logger.debug("Leaving" + event.toString());
	}

	private void doClearChild(LimitStructureDetail limitStructureDetails) {
		logger.debug("Entering");

		if (addHashMap.containsKey(limitStructureDetails.getItemPriority())) {
			addHashMap.remove(limitStructureDetails.getItemPriority());

		}
		doFillListBox();
		logger.debug("Leaving");
	}

	private void writeValuetoBean(LimitStructureDetail limitStructureDetails) {
		logger.debug("Entering");
		if (limitStructureDetails.getRecordType() != null
				&& StringUtils.trimToEmpty(limitStructureDetails.getRecordType()).equals("")) {
			limitStructureDetails.setVersion(limitStructureDetails.getVersion() + 1);
			if (limitStructureDetails.isNew()) {
				limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				limitStructureDetails.setNewRecord(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getLimitStructure().isNewRecord()) {
			this.structureCode.setReadonly(false);
			this.structureName.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.structureCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.structureName.setReadonly(isReadOnly("LimitStructureDetailDialog_LimitStructureCode"));
		this.active.setDisabled(isReadOnly("LimitStructureDetailDialog_LimitStructureCode"));
		this.showLimitsIn.setDisabled(isReadOnly("LimitStructureDetailDialog_LimitStructureCode"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.limitStructure.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.structureCode.setReadonly(true);
		this.structureName.setReadonly(true);
		this.active.setDisabled(true);
		this.showLimitsIn.setReadonly(true);

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
			getUserWorkspace().allocateAuthorities("LimitStructureDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_btnSave"));

			if (!StringUtils.equalsIgnoreCase(getLimitStructure().getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				this.btnGroup.setVisible(getUserWorkspace().isAllowed(
						"button_LimitStructureDetailDialog_NewLimitStructureDetails"));
				this.btnUp.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_Up"));
				this.btnTop.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_Top"));
				this.btnBottom.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_Bottom"));
				this.btnDown.setVisible(getUserWorkspace().isAllowed("button_LimitStructureDetailDialog_Down"));
			} else {
				btnGroup.setVisible(false);
				btnUp.setVisible(false);
				btnTop.setVisible(false);
				btnBottom.setVisible(false);
				btnDown.setVisible(false);
			}
		} else {
			btnGroup.setVisible(false);
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
		this.structureCode.setMaxlength(8);
		this.structureName.setMaxlength(50);

		this.listBoxLimitStructureDetailItems.setWidth(getListBoxWidth(50));
		this.listBoxLimitStructureDetailItems.setHeight(getListBoxHeight(5));
		this.window_LimitStructureDialog_title.setValue(Labels.getLabel("window_LimitStructureDialog_title_"
				+ getLimitStructure().getLimitCategory()));

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLimitStructure
	 *            LimitStructure
	 */
	public void doWriteBeanToComponents(LimitStructure aLimitStructure) {
		logger.debug("Entering");
		this.structureCode.setValue(aLimitStructure.getStructureCode());
		this.structureName.setValue(aLimitStructure.getStructureName());
		fillComboBox(this.showLimitsIn, aLimitStructure.getShowLimitsIn(), listCurrencyUnits, "");
		if (aLimitStructure.getLimitStructureDetailItemsList() == null) {
			setLimitStructureDetailItemsList(new ArrayList<LimitStructureDetail>());
		}
		List<LimitStructureDetail> structureCodeList = new ArrayList<LimitStructureDetail>();

		for (LimitStructureDetail detail : getLimitStructureDetailItemsList()) {

			for (LimitStructureDetail structure : getLimitStructureDetailItemsList()) {
				if (!StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, detail.getGroupCode())
						&& !StringUtils.equals(LimitConstants.LIMIT_ITEM_UNCLSFD, detail.getLimitLine()))
					if (detail.getItemPriority() == structure.getItemPriority() && detail.getItemSeq() == 1) {
						if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {

							structureCodeList.add(detail);
							if (key < structure.getItemPriority())
								key = structure.getItemPriority();
							addHashMap.put(structure.getItemPriority(), detail);
							break;
						} else {
							if (key < structure.getItemPriority())
								key = structure.getItemPriority();
							deleteMap.put(structure.getItemPriority(), detail);
							break;
						}
					}
			}
		}

		for (LimitStructureDetail detail : getLimitStructureDetailItemsList()) {
			if (detail.getGroupCode() != null) {
				assignedHashMap.put("G_" + detail.getGroupCode(), detail);
			} else {
				assignedHashMap.put("L_" + detail.getLimitLine(), detail);
			}

		}
		if (addHashMap.size() > 0)
			for (LimitStructureDetail detail : addHashMap.values()) {

				fetchSubGroups(detail);

			}

		setLimitStructureDetailItemsList(structureCodeList);
		limitGroupslist = getValueLabelsFromGroupMap();
		doFillListBox();
		if (getLimitStructure().isNew()) {
			this.active.setChecked(true);
		} else
			this.active.setChecked(aLimitStructure.isActive());

		if ((getLimitStructureService().limitStructureCheck(getLimitStructure().getStructureCode())) > 0) {
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aLimitStructure.getRecordStatus());
		logger.debug("Leaving");
	}

	private void fetchSubGroups(LimitStructureDetail detail) {
		List<LimitGroupLines> groupItems = PennantAppUtil.getLimitSubGroups(detail.getGroupCode(), true, false);

		if (groupItems != null && groupItems.size() > 0) {
			for (LimitGroupLines groupItem : groupItems) {
				if (assignedHashMap.containsKey("G_" + groupItem.getGroupCode())) {
					detail.getSubGroupsMap().put("GGC_" + groupItem.getGroupCode(),
							assignedHashMap.get("G_" + groupItem.getGroupCode()));
					fetchSubGroups(assignedHashMap.get("G_" + groupItem.getGroupCode()));
				}
			}
		} else {
			List<LimitGroupLines> Items = PennantAppUtil.getLimitSubGroups(detail.getGroupCode(), false, true);
			if (Items != null && Items.size() > 0)
				for (LimitGroupLines ruleItems : Items) {
					if (assignedHashMap.containsKey("L_" + ruleItems.getLimitLine())) {
						detail.getSubGroupsMap().put("LLC_" + ruleItems.getLimitLine(),
								assignedHashMap.get("L_" + ruleItems.getLimitLine()));
					}
				}
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLimitStructure
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(LimitStructure aLimitStructure) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		//Limit Category
		if (isInstitutionType)
			aLimitStructure.setLimitCategory(LimitConstants.LIMIT_CATEGORY_BANK);
		else
			aLimitStructure.setLimitCategory(LimitConstants.LIMIT_CATEGORY_CUST);

		// Structure Code
		try {
			aLimitStructure.setLimitCategory(getLimitStructure().getLimitCategory());
			aLimitStructure.setStructureCode(this.structureCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Structure Name
		try {
			aLimitStructure.setStructureName(this.structureName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Type
		try {
			String limitIn = "D";
			if (this.showLimitsIn.getSelectedItem() != null
					&& !StringUtils.equals(PennantConstants.List_Select, this.showLimitsIn.getSelectedItem().getValue()
							.toString())) {
				limitIn = this.showLimitsIn.getSelectedItem().getValue().toString();
			}
			aLimitStructure.setShowLimitsIn(limitIn);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Active
		try {
			aLimitStructure.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Limit Structure Detail
		try {
			if (this.listBoxLimitStructureDetailItems.getItems().isEmpty()
					&& !PennantConstants.RCD_STATUS_CANCELLED.equals(aLimitStructure.getRecordStatus())) {
				this.listBoxLimitStructureDetailItems
						.setEmptyMessage("Should not be Empty. Please Add Either Groups or Items");
				if (aLimitStructure.getStructureCode() != null && aLimitStructure.getStructureName() != null) {
					MessageUtil.showError(
							"This Structure doesn't Contain any Item or Group. Please Add Either Item or Group.");
					throw new WrongValueException();
				}
			} else {
				List<LimitStructureDetail> tempLimitStructureDetailList = new ArrayList<LimitStructureDetail>();
				tempLimitStructureDetailList.addAll(getLimitStructureDetailItemsList());
				for (LimitStructureDetail structureDetails : deleteMap.values()) {
					tempLimitStructureDetailList.add(structureDetails);
				}

				/*
				 * if (StringUtils.equals(PennantConstants.RCD_STATUS_APPROVED, aLimitStructure.getRecordStatus()) &&
				 * StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, aLimitStructure.getRecordType())) {
				 */
				tempLimitStructureDetailList.add(getUnclassifiedLimitStructureDetails(false,
						LimitConstants.LIMIT_ITEM_TOTAL));
				tempLimitStructureDetailList.add(getUnclassifiedLimitStructureDetails(true,
						LimitConstants.LIMIT_ITEM_UNCLSFD));
				//	}

				aLimitStructure.setLimitStructureDetailItemsList(tempLimitStructureDetailList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLimitStructure.setActive(this.active.isChecked());
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

		setLimitStructure(aLimitStructure);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doRemoveValidation();
		// Structure Code
		if (!this.structureCode.isReadonly()) {
			this.structureCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_LimitStructureDialog_StructureCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Structure Name
		if (!this.structureName.isReadonly()) {
			this.structureName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_LimitStructureDialog_StructureName.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Currency Notation
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		List<Listitem> items = this.listBoxLimitStructureDetailItems.getItems();
		List<LimitStructureDetail> list = new ArrayList<LimitStructureDetail>();

		for (Listitem item : items) {

			LimitStructureDetail lmtStructureDetails = (LimitStructureDetail) item.getAttribute("Data");

			if (lmtStructureDetails != null) {
				lmtStructureDetails.setItemSeq(1);
				lmtStructureDetails.setLimitCategory(getLimitStructure().getLimitCategory());
				if (lmtStructureDetails.getGroupCode() != null) {
					Combobox lmtGrpCombobox = (Combobox) (Combobox) item.getChildren().get(0).getFirstChild();
					try {
						if (lmtGrpCombobox != null
								&& lmtGrpCombobox.getValue() != null
								&& !StringUtils.equals(PennantConstants.List_Select, lmtGrpCombobox.getSelectedItem()
										.getValue().toString())) {
							addStructureDetails(lmtStructureDetails, list, lmtGrpCombobox,
									lmtStructureDetails.getGroupCode());
							if (!isInstitutionType && limitGroupsMap.containsKey(lmtStructureDetails.getGroupCode())) {
								if (StringUtils.equals(LimitConstants.LIMIT_CATEGORY_BANK,
										limitGroupsMap.get(lmtStructureDetails.getGroupCode()).getLimitCategory())) {
									isInstitutionType = true;
								}
							}

							lmtStructureDetails.setItemPriority(item.getIndex() + 1);
							list.add(lmtStructureDetails);
							if (key < lmtStructureDetails.getItemPriority())
								key = lmtStructureDetails.getItemPriority();
						} else if (!lmtGrpCombobox.isDisabled()) {
							throw new WrongValueException(lmtGrpCombobox, Labels.getLabel("STATIC_INVALID",
									new String[] { Labels.getLabel("listheader_GroupCode.label") }));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (lmtStructureDetails.getLimitLine() != null) {
					lmtStructureDetails.setItemPriority(item.getIndex() + 1);
					list.add(lmtStructureDetails);
				}
			}
		}
		setLimitStructureDetailItemsList(list);
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	private void addStructureDetails(LimitStructureDetail lmtStructureDetails, List<LimitStructureDetail> list,
			Combobox lmtGrp, String limtGrp) {
		for (LimitStructureDetail structureDetail : lmtStructureDetails.getSubGroupsMap().values()) {
			structureDetail.setLimitCategory(getLimitStructure().getLimitCategory());
			structureDetail.setItemPriority(lmtStructureDetails.getItemPriority());

			for (LimitStructureDetail limitGroup : list) {
				if (limitGroup.getGroupCode() != null && structureDetail.getGroupCode() != null
						&& StringUtils.equals(structureDetail.getGroupCode(), limitGroup.getGroupCode())) {
					throw new WrongValueException(lmtGrp, limitGroup.getGroupCode()
							+ " Already exists. Please Select Another Group.");
				} else if (structureDetail.getLimitLine() != null && limitGroup.getLimitLine() != null
						&& StringUtils.equals(limitGroup.getLimitLine(), structureDetail.getLimitLine())) {
					throw new WrongValueException(lmtGrp, structureDetail.getLimitLine()
							+ " Already exists. Please Select Another Group.");
				}
			}

			list.add(structureDetail);
			if (structureDetail.getSubGroupsMap() != null && structureDetail.getSubGroupsMap().size() > 0) {
				addStructureDetails(structureDetail, list, lmtGrp, limtGrp);
			}
		}
	}

	private LimitStructureDetail getUnclassifiedLimitStructureDetails(boolean item, String limitLine) {
		LimitStructureDetail unclassifiedDetails = null;
		String keyEle = "";
		if (item) {
			keyEle = "L_" + limitLine;
		} else
			keyEle = "G_" + limitLine;

		if (assignedHashMap.containsKey(keyEle)) {
			unclassifiedDetails = assignedHashMap.get(keyEle);
			writeValuetoBean(unclassifiedDetails);
		} else {
			unclassifiedDetails = new LimitStructureDetail();
			unclassifiedDetails.setNewRecord(true);
			unclassifiedDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			unclassifiedDetails.setVersion(unclassifiedDetails.getVersion() + 1);
			unclassifiedDetails.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			unclassifiedDetails.setCreatedBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		}

		key = key + 1;
		if (item) {
			unclassifiedDetails.setLimitLine(limitLine);
			unclassifiedDetails.setItemSeq(1);
			unclassifiedDetails.setItemPriority(key);
		} else {

			unclassifiedDetails.setItemPriority(0);
			unclassifiedDetails.setGroupCode(limitLine);
			unclassifiedDetails.setItemSeq(1);
		}
		unclassifiedDetails.setDisplayStyle("STYLE01");

		unclassifiedDetails.setLimitCheck(true);
		unclassifiedDetails.setEditable(true);
		unclassifiedDetails.setLimitCategory(getLimitStructure().getLimitCategory());

		return unclassifiedDetails;
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.structureCode.setConstraint("");
		this.structureName.setConstraint("");
		this.showLimitsIn.setConstraint("");
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
	 * Remove Error Messages for Fields
	 */

	protected void doClearMessage() {
		logger.debug("Entering");
		this.structureCode.setErrorMessage("");
		this.structureName.setErrorMessage("");
		this.showLimitsIn.setErrorMessage("");
		logger.debug("Leaving");
	}

	// *****************************************************************
	// ************************+ crud operations ***********************
	// *****************************************************************

	/**
	 * Deletes a LimitStructure object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final LimitStructure aLimitStructure = new LimitStructure();
		BeanUtils.copyProperties(getLimitStructure(), aLimitStructure);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aLimitStructure.getStructureCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (getLimitStructureService().limitStructureCheck(getLimitStructure().getStructureCode()) == 0) {
				if (StringUtils.isBlank(aLimitStructure.getRecordType())) {
					aLimitStructure.setVersion(aLimitStructure.getVersion() + 1);
					aLimitStructure.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						aLimitStructure.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						aLimitStructure.setNewRecord(true);
						tranType = PennantConstants.TRAN_WF;
						getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLimitStructure.getNextTaskId(),
								aLimitStructure);
					} else {
						tranType = PennantConstants.TRAN_DEL;
					}
				}
				try {
					if (doProcess(aLimitStructure, tranType)) {
						refreshList();
						closeDialog();
					}
				} catch (DataAccessException e) {
					MessageUtil.showError(e);
				}
			} else {
				MessageUtil.showError(
						Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { getLimitStructure().getStructureCode() }));
				return;
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.structureCode.setValue("");
		this.structureName.setValue("");
		this.showLimitsIn.setSelectedIndex(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LimitStructure aLimitStructure = new LimitStructure();
		BeanUtils.copyProperties(getLimitStructure(), aLimitStructure);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aLimitStructure.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLimitStructure.getNextTaskId(),
					aLimitStructure);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aLimitStructure.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the LimitStructure object with the components data
			doWriteComponentsToBean(aLimitStructure);
		}

		for (LimitStructureDetail list : aLimitStructure.getLimitStructureDetailItemsList()) {
			setWorkFlowDetails(list, false);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aLimitStructure.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLimitStructure.getRecordType()).equals("")) {
				aLimitStructure.setVersion(aLimitStructure.getVersion() + 1);
				if (isNew) {
					aLimitStructure.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLimitStructure.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLimitStructure.setNewRecord(true);
				}
			}
		} else {
			aLimitStructure.setVersion(aLimitStructure.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aLimitStructure, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void setWorkFlowDetails(LimitStructureDetail aLimitStructureDetail, boolean isDelete) {
		logger.debug("Entering");
		boolean isNew = false;
		isNew = aLimitStructureDetail.isNew();
		if (!isDelete) {
			if (isWorkFlowEnabled()) {
				if (StringUtils.trimToEmpty(aLimitStructureDetail.getRecordType()).equals("")) {
					aLimitStructureDetail.setVersion(aLimitStructureDetail.getVersion() + 1);
					if (isNew) {
						aLimitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aLimitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aLimitStructureDetail.setNewRecord(true);
					}
				}
			} else {
				aLimitStructureDetail.setVersion(aLimitStructureDetail.getVersion() + 1);
			}
		} else {
			if (StringUtils.trimToEmpty(aLimitStructureDetail.getRecordType()).equals("")) {
				aLimitStructureDetail.setVersion(aLimitStructureDetail.getVersion() + 1);
				aLimitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aLimitStructureDetail.setNewRecord(true);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(LimitStructure aLimitStructure, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aLimitStructure.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aLimitStructure.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLimitStructure.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (aLimitStructure.isNew()) {
			aLimitStructure.setCreatedBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aLimitStructure.setCreatedOn(new Timestamp(System.currentTimeMillis()));
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
			aLimitStructure.setTaskId(getTaskId());
			aLimitStructure.setNextTaskId(getNextTaskId());
			aLimitStructure.setRoleCode(getRole());
			aLimitStructure.setNextRoleCode(getNextRoleCode());
			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aLimitStructure, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aLimitStructure, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aLimitStructure, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		LimitStructure aLimitStructure = (LimitStructure) auditHeader.getAuditDetail().getModelData();
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getLimitStructureService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getLimitStructureService().saveOrUpdate(auditHeader);
					}
				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getLimitStructureService().doApprove(auditHeader);
						if (PennantConstants.RECORD_TYPE_DEL.equals(aLimitStructure.getRecordType())) {
							deleteNotes = true;
						}
					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getLimitStructureService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aLimitStructure.getRecordType())) {
							deleteNotes = true;
						}
					} else {
						retValue = ErrorControl.showErrorControl(this.window_LimitStructureDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_LimitStructureDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.limitStructure), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
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

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LimitStructure aLimitStructure, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLimitStructure.getBefImage(), aLimitStructure);
		return new AuditHeader(aLimitStructure.getStructureCode(), null, null, null, auditDetail,
				aLimitStructure.getUserDetails(), getOverideMap());
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList() {
		getLimitStructureListCtrl().search();

	}

	@Override
	protected String getReference() {
		return this.structureCode.getValue();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitStructure getLimitStructure() {
		return this.limitStructure;
	}

	public void setLimitStructure(LimitStructure limitStructure) {
		this.limitStructure = limitStructure;
	}

	public void setLimitStructureService(LimitStructureService limitStructureService) {
		this.limitStructureService = limitStructureService;
	}

	public LimitStructureService getLimitStructureService() {
		return this.limitStructureService;
	}

	public void setLimitStructureListCtrl(LimitStructureListCtrl limitStructureListCtrl) {
		this.limitStructureListCtrl = limitStructureListCtrl;
	}

	public LimitStructureListCtrl getLimitStructureListCtrl() {
		return this.limitStructureListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListWrapper<LimitStructureDetail> getAssignedLimitStructureDetailItemsPagedListWrapper() {
		return assignedLimitStructureDetailItemsPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setAssignedLimitStructureDetailItemsPagedListWrapper() {
		if (this.assignedLimitStructureDetailItemsPagedListWrapper == null) {
			this.assignedLimitStructureDetailItemsPagedListWrapper = (PagedListWrapper<LimitStructureDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public List<LimitStructureDetail> getLimitStructureDetailItemsList() {
		return limitStructureDetailItemsList;
	}

	public void setLimitStructureDetailItemsList(List<LimitStructureDetail> limitStructureDetailItemsList) {
		this.limitStructureDetailItemsList = limitStructureDetailItemsList;
	}
}
