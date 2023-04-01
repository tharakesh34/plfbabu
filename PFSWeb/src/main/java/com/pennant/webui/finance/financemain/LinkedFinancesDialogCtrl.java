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
 * * FileName : LinkedFinancesDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-07-2019 * *
 * Modified Date : 05-07-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/LinkedFinancesDetailDialog.zul file.
 */
public class LinkedFinancesDialogCtrl extends GFCBaseCtrl<LinkedFinances> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = LogManager.getLogger(LinkedFinancesDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LinkedFinancesDialog;
	protected Borderlayout borderlayoutLinkedFinances;
	private Groupbox finBasicdetails;
	// Finance Linked Finances Details Tab
	protected Listbox listBoxLinkedFinances;
	protected Uppercasebox finReference;
	protected Button btnLink;
	protected Button btnRemove;
	protected Button btnAdd;
	protected North north;
	protected South south;

	protected Grid header;
	protected Textbox finReferenceHeader;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox finType;
	protected Textbox currency;
	protected Textbox loanAmount;
	protected Textbox startDate;

	private String roleCode = "";
	private boolean newRecord = false;
	private String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	protected boolean flag = false;

	// ServiceDAOs / Domain Classes
	private FinanceDetail financeDetail = null;
	private FinanceMainBaseCtrl financeMainBaseCtrl = null;
	private transient FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	@SuppressWarnings("unused")
	private Object financeMainDialogCtrl;

	private transient LinkedFinancesService linkedFinancesService;
	private FinMaintainInstruction finMaintainInstruction;

	private List<LinkedFinances> linkedFinList = new ArrayList<>();
	private Map<String, LinkedFinances> linkedFinMap = new HashMap<>();
	private ArrayList<Object> finHeaderList = new ArrayList<>();
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	private FinanceMainService financeMainService;

	/**
	 * default constructor.<br>
	 * 
	 */
	public LinkedFinancesDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LinkedFinancesDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LinkedFinancesDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LinkedFinancesDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			// moduleDefiner : Empty - LOS
			// moduleDefiner : LinkDelink - LMS
			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("financeMainBaseCtrl")) {
				setFinanceMainBaseCtrl((FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl"));
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
				this.financeMainDialogCtrl = (Object) arguments.get("financeSelectCtrl");
			}

			if (arguments.containsKey("finMaintainInstruction")) {
				setFinMaintainInstruction((FinMaintainInstruction) arguments.get("finMaintainInstruction"));
				this.finMaintainInstruction = (FinMaintainInstruction) arguments.get("finMaintainInstruction");
			}

			if (arguments.containsKey("finHeaderList")) {
				this.finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (StringUtils.isNotEmpty(moduleDefiner)) {

				FinScheduleData finSchdData = financeDetail.getFinScheduleData();
				finSchdData.setFinMaintainInstruction(this.finMaintainInstruction);

				// Store the before image.
				FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
				BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
				this.finMaintainInstruction.setBefImage(finMaintainInstruction);

				// Render the page and display the data.
				doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
						this.finMaintainInstruction.getNextTaskId());

				if (StringUtils.isEmpty(this.roleCode)) {
					this.roleCode = getRole();
				}

				if (isWorkFlowEnabled() && !enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
				}
			} else {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(financeDetail);

			this.listBoxLinkedFinances.setHeight(borderLayoutHeight - 300 + "px");

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.header.setVisible(true);
			this.finBasicdetails.setVisible(false);
		} else {
			this.header.setVisible(false);
			this.finBasicdetails.setVisible(true);
		}

		this.finReference.setMaxlength(20);
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.btnRemove.setLabel(Labels.getLabel("label_btnDelink_LinkedFinances.label"));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, this.roleCode);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LinkedFinancesDialog_btnSave"));
		this.btnRemove.setVisible(getUserWorkspace().isAllowed("button_LinkedFinancesDialog_btnRemove"));
		this.btnLink.setVisible(getUserWorkspace().isAllowed("button_LinkedFinancesDialog_btnLink"));
		this.btnAdd.setVisible(getUserWorkspace().isAllowed("button_LinkedFinancesDialog_btnLink"));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "link" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnLink() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.finReference);
		this.finReference.setErrorMessage("");
		this.finReference.setConstraint("");

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		String finref = this.finReference.getValue().trim();

		if (StringUtils.isEmpty(finref)) {
			throw new WrongValueException(this.finReference, Labels.getLabel("label_LinkLoan"));
		} else {
			if (StringUtils.equals(finref, finMain.getFinReference())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("label_LinkItself"));
			}
		}

		Long finID = financeMainService.getFinID(this.finReference.getValue());

		if (finID == null) {
			throw new WrongValueException(this.finReference, Labels.getLabel("invalid_reference"));
		}

		String entityCode = "";
		if (StringUtils.isEmpty(moduleDefiner)) {
			entityCode = finType.getLovDescEntityCode();
		} else {
			entityCode = finMain.getEntityCode();
		}

		FinanceMain financeMain = linkedFinancesService.getFinMainByFinRef(finID);

		if (financeMain == null) {
			throw new WrongValueException(this.finReference, Labels.getLabel("invalid_reference"));
		}

		// Lan Linking should allow to link/De link the closed loans as well.

		if (!financeMain.isFinIsActive()) {
			throw new WrongValueException(this.finReference, Labels.getLabel("label_Loan_Inactive"));
		}

		// Entity Code Validation
		if (!StringUtils.equals(entityCode, financeMain.getEntityCode())) {
			throw new WrongValueException(this.finReference, Labels.getLabel("is_Entity_matching"));
		}

		// Duplicate Reference Validation
		for (LinkedFinances linkedFinance : linkedFinList) {
			if (linkedFinance.getLinkedReference().equals(financeMain.getFinReference())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("label_ExistingLoan"));
			}
		}

		if (linkedFinList.size() >= 5) {
			throw new WrongValueException(this.finReference, Labels.getLabel("label_ExceededList"));
		}

		// Data preparation and rendering.
		LinkedFinances linkedFin = new LinkedFinances();
		linkedFin.setFinID(finMain.getFinID());
		linkedFin.setFinReference(finMain.getFinReference());
		linkedFin.setLinkedReference(financeMain.getFinReference());
		linkedFin.setCustShrtName(financeMain.getLovDescCustShrtName());
		linkedFin.setLinkedFinType(financeMain.getFinType());
		linkedFin.setNewRecord(true);
		linkedFin.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		linkedFin.setStatus(PennantConstants.RCD_ADD);

		linkedFinMap.put(linkedFin.getLinkedReference(), linkedFin);
		doLink(linkedFin);
		this.linkedFinList.add(linkedFin);
		this.finReference.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "remove" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRemove() {
		logger.debug(Literal.ENTERING);

		doRemove();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnAdd() {
		logger.debug(Literal.ENTERING);

		if (!this.listBoxLinkedFinances.getSelectedItems().isEmpty()) {
			doAdd();
		} else {
			MessageUtil.showError("No records are linked or selected for Re-Link.");
		}

		logger.debug(Literal.LEAVING);
	}

	public void doLink(LinkedFinances linkedFinance) {
		logger.debug(Literal.ENTERING);

		Listitem listitem = new Listitem();
		Listcell listcell;

		listcell = new Listcell(linkedFinance.getLinkedReference());
		listitem.appendChild(listcell);

		listcell = new Listcell(linkedFinance.getLinkedFinType());
		listitem.appendChild(listcell);

		listcell = new Listcell(linkedFinance.getCustShrtName());
		listitem.appendChild(listcell);

		listcell = new Listcell(linkedFinance.getStatus());
		listitem.appendChild(listcell);

		listitem.setAttribute("data", linkedFinance);
		this.listBoxLinkedFinances.appendChild(listitem);

		logger.debug(Literal.LEAVING);
	}

	public void doRemove() {
		logger.debug(Literal.ENTERING);

		if (!this.listBoxLinkedFinances.getSelectedItems().isEmpty()) {
			for (Listitem listitem : this.listBoxLinkedFinances.getSelectedItems()) {
				Listcell listcell = (Listcell) listitem.getFirstChild();
				String reference = listcell.getLabel();
				LinkedFinances linkFin = (LinkedFinances) listitem.getAttribute("data");
				if (PennantConstants.RCD_ADD.equals(linkFin.getStatus()) && linkFin.isNewRecord()) {
					linkedFinMap.remove(reference);
					linkedFinList.remove(linkFin);
					// linkFin.setStatus(PennantConstants.RCD_REM);
				} else {
					linkFin.setStatus(PennantConstants.RCD_DEL);
					linkedFinMap.put(reference, linkFin);
				}

			}
		} else {
			MessageUtil.showError("No records are linked or selected for removal.");
		}

		this.listBoxLinkedFinances.getItems().clear();
		for (LinkedFinances linkedFinances : linkedFinMap.values()) {
			doLink(linkedFinances);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doAdd() {
		logger.debug(Literal.ENTERING);

		for (Listitem listitem : this.listBoxLinkedFinances.getSelectedItems()) {
			Listcell listcell = (Listcell) listitem.getFirstChild();
			String reference = listcell.getLabel();
			LinkedFinances linkFin = (LinkedFinances) listitem.getAttribute("data");
			if (PennantConstants.RCD_DEL.equals(linkFin.getStatus())) {
				linkFin.setStatus(PennantConstants.RCD_EDT);
			} else if (linkFin.isNewRecord()) {
				linkFin.setStatus(PennantConstants.RCD_ADD);
			}
			linkedFinMap.put(reference, linkFin);
		}

		this.listBoxLinkedFinances.getItems().clear();
		for (LinkedFinances linkedFinances : linkedFinMap.values()) {
			doLink(linkedFinances);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param LinkedFinances LinkedFinances
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotEmpty(moduleDefiner)) {
			FinScheduleData schData = aFinanceDetail.getFinScheduleData();
			FinanceMain fm = schData.getFinanceMain();
			FinMaintainInstruction fmi = schData.getFinMaintainInstruction();

			appendExtendedFieldDetails(this.moduleDefiner);

			this.custCIF.setValue(fm.getCustCIF());
			this.custShrtName.setValue(fm.getLovDescCustShrtName());
			this.finReferenceHeader.setValue(fm.getFinReference());
			this.finBranch.setValue(fm.getFinBranch());
			this.currency.setValue(StringUtils.trimToEmpty(fm.getFinCcy()));
			this.finType.setValue(fm.getFinType());
			this.loanAmount.setValue(PennantApplicationUtil.amountFormate(fm.getFinAssetValue(),
					CurrencyUtil.getFormat(fm.getFinCcy())));
			this.startDate.setValue(DateUtil.formatToLongDate(fm.getFinStartDate()));
			if (fmi != null) {
				if (fmi.getRecordStatus() == null || fmi.getRecordStatus().equalsIgnoreCase("Approved")) {
					this.recordStatus.setValue("");
				} else {
					this.recordStatus.setValue(fmi.getRecordStatus());
				}
			}
		}

		List<LinkedFinances> list = aFinanceDetail.getLinkedFinancesList();

		if (CollectionUtils.isNotEmpty(list)) {
			Collections.sort(list, new Comparator<LinkedFinances>() {

				@Override
				public int compare(LinkedFinances o1, LinkedFinances o2) {
					if (o1.getLinkedReference().compareTo(o2.getLinkedReference()) > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			});
			this.linkedFinList.clear();
			this.linkedFinList.addAll(list);

			for (LinkedFinances linFin : list) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(linFin.getRecordType())) {
					linFin.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
				linkedFinMap.put(linFin.getLinkedReference(), linFin);
				doLink(linFin);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(String finEvent) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender extendedFieldRender = null;
		FinScheduleData schData = financeDetail.getFinScheduleData();
		FinanceMain fm = schData.getFinanceMain();

		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, fm.getLovDescProductCodeName(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(fm.getFinReference());

			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			financeDetail.setExtendedFieldHeader(extendedFieldHeader);
			financeDetail.setExtendedFieldRender(extendedFieldRender);

			if (financeDetail.getBefImage() != null) {
				financeDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				financeDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(fm.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_LinkedFinancesDialog);
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finMaintainInstruction);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finMaintainInstruction.getFinReference());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param LinkedFinances
	 */
	public void doWriteComponentsToBean(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<LinkedFinances> linkFinList = new ArrayList(linkedFinMap.values());
		if (CollectionUtils.isNotEmpty(linkFinList)) {
			for (int i = 0; i < linkFinList.size(); i++) {
				if (PennantConstants.RCD_REM.equals(linkFinList.get(i).getStatus())) {
					linkFinList.remove(i);
				}
			}
		}
		financeDetail.setLinkedFinancesList(linkFinList);

		flag = false;
		if (StringUtils.isNotEmpty(this.moduleDefiner) && CollectionUtils.isEmpty(linkFinList)) {
			MessageUtil.showError("Please 'Link' atleast 1 Loan Reference to Proceed. ");
			flag = true;
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLinkedFinances
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doShowDialog(FinanceDetail aFinanceDetail)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinMaintainInstruction finMaintainInst = finScheduleData.getFinMaintainInstruction();

		// set ReadOnly mode accordingly if the object is new or not.
		if (finMaintainInst.isNewRecord() || aFinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			if (isWorkFlowEnabled() || StringUtils.isEmpty(moduleDefiner)) {
				this.finReference.focus();
				if (StringUtils.isNotBlank(finMaintainInst.getRecordType())
						|| StringUtils.isNotBlank(finMain.getRecordType())) {
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
			doWriteBeanToComponents(aFinanceDetail);

			if (financeMainBaseCtrl != null) {
				financeMainBaseCtrl.getClass().getMethod("setLinkedFinancesDialogCtrl", this.getClass())
						.invoke(financeMainBaseCtrl, this);
			}

		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_LinkedFinancesDialog.onClose();
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			this.north.setVisible(false);
			this.south.setVisible(false);
			appendFinBasicDetails();
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * /** Disables the Validation by setting empty constraints.
	 */
	public void doRemoveValidation() {
		this.finReference.setConstraint("");
	}

	/**
	 * Method to clear error messages.
	 */
	@Override
	protected void doClearMessage() {
		this.finReference.setErrorMessage("");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = financeSelectCtrl.getSearchObj(true);
		financeSelectCtrl.getPagingFinanceList().setActivePage(0);
		financeSelectCtrl.getPagedListWrapper().setSearchObject(soFinanceMain);
		if (financeSelectCtrl.getListBoxFinance() != null) {
			financeSelectCtrl.getListBoxFinance().getListModel();
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		this.finReference.setReadonly(isReadOnly("LinkedFinancesDialog_finReference"));
		this.listBoxLinkedFinances.setCheckmark(!isReadOnly("LinkedFinancesDialog_checkBox"));

		if (StringUtils.isNotEmpty(moduleDefiner)) {
			FinMaintainInstruction finMainInst = financeDetail.getFinScheduleData().getFinMaintainInstruction();
			if (isWorkFlowEnabled()) {

				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (finMainInst.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || StringUtils.isEmpty(moduleDefiner)) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.finReference.setReadonly(true);
		this.listBoxLinkedFinances.setCheckmark(false);

		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.finReferenceHeader.setReadonly(true);
			this.finBranch.setReadonly(true);
			this.custCIF.setReadonly(true);
			this.custShrtName.setReadonly(true);
			this.finType.setReadonly(true);
			this.currency.setReadonly(true);
			this.loanAmount.setReadonly(true);
			this.startDate.setReadonly(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		// remove validation, if saved before
		this.finReference.setValue("");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final FinMaintainInstruction finMainInst = new FinMaintainInstruction();
		BeanUtils.copyProperties(this.finMaintainInstruction, finMainInst);
		boolean isNew;

		// fill the LinkedFinances object with the components data
		doWriteComponentsToBean(financeDetail);

		if (financeDetail.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			financeDetail.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		if (flag) {
			return;
		}

		isNew = finMainInst.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(finMainInst.getRecordType())) {
				finMainInst.setVersion(finMainInst.getVersion() + 1);
				if (isNew) {
					finMainInst.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					finMainInst.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					finMainInst.setNewRecord(true);
				}
			}
		} else {
			finMainInst.setVersion(finMainInst.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(finMainInst, tranType)) {
				refreshList();
				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(finMainInst.getRoleCode(),
						finMainInst.getNextRoleCode(), finMainInst.getFinReference() + "", " Linking/Delinking ",
						finMainInst.getRecordStatus(), false);
				if (StringUtils.equals(finMainInst.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Linking/Delinking with reference " + finMainInst.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAcademic (Academic)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinMaintainInstruction finMainInst, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		finMainInst.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		finMainInst.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finMainInst.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			finMainInst.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(finMainInst.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, finMainInst);
				}

				if (isNotesMandatory(taskId, finMainInst)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			finMainInst.setTaskId(taskId);
			finMainInst.setNextTaskId(nextTaskId);
			finMainInst.setRoleCode(getRole());
			finMainInst.setNextRoleCode(nextRoleCode);

			financeDetail.getFinScheduleData().setFinMaintainInstruction(finMainInst);
			auditHeader = getAuditHeader(financeDetail, tranType);

			String operationRefs = getServiceOperations(taskId, finMainInst);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(financeDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * 
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinMaintainInstruction fmi = fd.getFinScheduleData().getFinMaintainInstruction();

		if (fd.getExtendedFieldRender() != null) {
			int seqNo = 0;
			ExtendedFieldRender details = fd.getExtendedFieldRender();
			details.setReference(fmi.getFinReference());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(fmi.getRecordStatus());
			details.setRecordType(fmi.getRecordType());
			details.setVersion(fmi.getVersion());
			details.setWorkflowId(fmi.getWorkflowId());
			details.setTaskId(fmi.getTaskId());
			details.setNextTaskId(fmi.getNextTaskId());
			details.setRoleCode(fmi.getRoleCode());
			details.setNextRoleCode(fmi.getNextRoleCode());
			details.setNewRecord(fd.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(fmi.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(fmi.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					// aAuditHeader =
					// linkedFinancesService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = linkedFinancesService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = linkedFinancesService.doApprove(aAuditHeader);

					if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = linkedFinancesService.doReject(aAuditHeader);

					if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LinkedFinancesDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_LinkedFinancesDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finMaintainInstruction), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(aAuditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void doSaveLinkedFinances(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean(financeDetail);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (LinkedFinances linkedFin : financeDetail.getLinkedFinancesList()) {
			linkedFin.setFinID(financeMain.getFinID());
			linkedFin.setFinReference(financeMain.getFinReference());
			linkedFin.setVersion(financeMain.getVersion() + 1);
			linkedFin.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			linkedFin.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			linkedFin.setRecordStatus(financeMain.getRecordStatus());
			linkedFin.setRecordType(financeMain.getRecordType());
			linkedFin.setWorkflowId(financeMain.getWorkflowId());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", this.finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private List<Object> getFinBasicDetails() {
		List<Object> arrayList = new ArrayList<>();
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		String finType = fm.getFinType();

		FinanceType financeType = linkedFinancesService.getFinType(finType);

		arrayList.add(0, fm.getFinType());
		arrayList.add(1, fm.getFinCcy());

		if (StringUtils.equals(fm.getScheduleMethod(), PennantConstants.List_Select)) {
			arrayList.add(2, "");
		} else {
			arrayList.add(2, fm.getScheduleMethod());
		}
		arrayList.add(3, fm.getFinReference());
		arrayList.add(4, fm.getProfitDaysBasis());
		arrayList.add(5, fm.getGrcPeriodEndDate());
		arrayList.add(6, fm.isAllowGrcPeriod());

		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, financeType.getFinCategory());

		String custShrtName = "";
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			custShrtName = financeDetail.getCustomerDetails().getCustomer().getCustShrtName();
		}

		arrayList.add(9, custShrtName);
		arrayList.add(10, fm.isNewRecord());
		arrayList.add(11, moduleDefiner);

		return arrayList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	public void setFinanceMainBaseCtrl(FinanceMainBaseCtrl financeMainBaseCtrl) {
		this.financeMainBaseCtrl = financeMainBaseCtrl;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public void setFinMaintainInstruction(FinMaintainInstruction finMaintainInstruction) {
		this.finMaintainInstruction = finMaintainInstruction;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

}
