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
 * * FileName : CovenantsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * 08-05-2018 Vinay 0.2 As per mail from Raju , * subject : Daily status call : 19 April *
 * added OTC field with validation from * Document Types master based on * PDC and OTC is required or not. * 16-05-2018
 * Madhu 0.3 added OTC/PDD functionality. * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.covenant;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinCovenantMaintanceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.staticlist.AppStaticList;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FinCovenantTypeDetail/finCovenantTypesDetailDialog.zul
 * file.
 */
public class CovenantsDialogCtrl extends GFCBaseCtrl<Covenant> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CovenantsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding compoonent with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window covenantDialogWindow;
	protected Combobox category;
	protected ExtendedCombobox covenantType;
	protected ExtendedCombobox mandRole;
	protected Textbox description;
	protected Checkbox alwWaiver;
	protected Checkbox pdd;
	protected Checkbox otc;
	protected Checkbox internalUse;
	protected Label recordType;
	protected Groupbox gbStatusDetails;
	protected Label label_FinCovenantTypeDialog_MandRole;
	protected Datebox receivableDate;
	protected Space space_receivableDate;
	protected Label label_FinCovenantTypeDialog_RecvbleDate;
	protected ExtendedCombobox notifyTo;
	protected Datebox loanStartDate;
	protected Checkbox documentRecieved;
	protected Datebox documentRecievedDate;
	protected Combobox covenantFrequency;
	protected Datebox covenantNextFrequencyDate;
	protected Intbox covenantGraceDays;
	protected Datebox covenantGraceDueDate;
	protected Intbox alertDays;
	protected Textbox remarks;
	protected Textbox standardValue;
	protected Textbox actualValue;
	protected Textbox tbreceivabledate;
	protected Textbox tbdocumentdate;
	protected Textbox tbdocumentName;
	protected Listbox listboxDocuments;
	protected Textbox additionalRemarks;
	protected Tab additionalRemarksTab;
	protected Tabpanel additionalRemarksTabPanel;
	protected Tab covenantDetailsTab;

	private Covenant covenant;
	private transient boolean newFinance;
	private boolean newRecord;
	private boolean newCustomer;
	private List<Covenant> covenants;
	private String allowedRoles;
	private FinanceDetail financedetail;
	protected String moduleDefiner = "";
	private LegalDetail legalDetail;
	protected Button btnCovenantReceived;
	protected Checkbox allowPostponment;
	protected Datebox extendedDate;
	protected Row rw_manrole;
	protected Row rw_allowedPostpone;
	protected Checkbox alertsRequired;
	protected Combobox alertType;
	protected Label label_CovenantsDialog_NotifyTo;

	private transient List<Property> covenantCategories = AppStaticList.getCovenantCategories();
	private transient List<Property> listFrequency = AppStaticList.getFrequencies();
	private transient List<Property> listAlertType = AppStaticList.getAlertsFor();
	private CovenantsListCtrl covenantsListCtrl;

	private DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private FinCovenantMaintanceDialogCtrl finCovenantMaintanceDialogCtrl;

	private List<CovenantDocument> covenantDocuments;
	private Date maturityDate;
	private Date loanStartDt;
	private String module = "Organization";

	private CovenantType covenantTypeObject;

	/**
	 * default constructor.<br>
	 */
	public CovenantsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CovenantDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinCovenantTypeDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$covenantDialogWindow(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(covenantDialogWindow);

		try {
			this.covenant = (Covenant) arguments.get("covenant");

			if (this.covenant.isNewRecord()) {
				Covenant befImage = new Covenant();
				BeanUtils.copyProperties(this.covenant, befImage);
				this.covenant.setBefImage(befImage);
				setNewRecord(true);
			}

			setCovenant(this.covenant);

			setCovenantsListCtrl((CovenantsListCtrl) arguments.get("covenantsListCtrl"));

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setNewCustomer(true);
				setNewFinance(true);
				this.covenant.setWorkflowId(0);
			}

			if (arguments.containsKey("allowedRoles")) {
				allowedRoles = (String) arguments.get("allowedRoles");
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), pageRightName);
			}

			if (arguments.containsKey("financeDetail")) {
				financedetail = (FinanceDetail) arguments.get("financeDetail");
			}

			doLoadWorkFlow(this.covenant.isWorkflow(), this.covenant.getWorkflowId(), this.covenant.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), pageRightName);
			}

			if (arguments.containsKey("legalDetail")) {
				legalDetail = (LegalDetail) arguments.get("legalDetail");
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			doCheckRights();

			doSetFieldProperties();

			doShowDialog(getCovenant());

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, covenantDialogWindow);
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
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.covenant);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.covenant.getKeyReference());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinCovenantTypeDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(Covenant aFinCovenantType) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			doWriteBeanToComponents(aFinCovenantType);

			if (aFinCovenantType.isNewRecord()) {
				this.btnCtrl.setInitNew();
				doEdit();
				this.covenantType.focus();
			} else {
				this.covenantType.focus();
				if (isNewFinance()) {
					if (enqiryModule) {
						doReadOnly();
					} else {
						doEdit();
					}
				} else if (isWorkFlowEnabled()) {
					this.btnNotes.setVisible(true);
					doEdit();
				} else {
					this.btnCtrl.setInitEdit();
					doReadOnly();
					btnCancel.setVisible(false);
				}
			}

			oncheckPDD();
			onCheckOTC();
			onCheckALwWaiver();

			if (enqiryModule) {
				this.btnNew.setVisible(false);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			}

			if (ImplementationConstants.COVENANT_ADTNL_REMARKS) {
				additionalRemarksTab.setVisible(true);
				this.additionalRemarksTabPanel.setVisible(true);
			}

			this.covenantDialogWindow.setHeight("85%");
			this.covenantDialogWindow.setWidth("100%");
			this.gbStatusDetails.setVisible(false);
			this.covenantDialogWindow.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		// set ReadOnly mode accordingly if the object is new or not.

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (getCovenant().isNewRecord()) {
			this.covenantType.setReadonly(false);
			this.category.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.covenantType.setReadonly(true);
			this.category.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		if (!"Maintanance".equals(module)) {
			readOnlyComponent(isReadOnly("CovenantDialog_Mandatoryrole"), this.mandRole);
		}
		readOnlyComponent(isReadOnly("CovenantDialog_Category"), this.category);
		readOnlyComponent(isReadOnly("CovenantDialog_Otc"), this.otc);
		readOnlyComponent(isReadOnly("CovenantDialog_Pdd"), this.pdd);
		readOnlyComponent(isReadOnly("CovenantDialog_Receivabledate"), this.receivableDate);
		readOnlyComponent(isReadOnly("CovenantDialog_Allowwaiver"), this.alwWaiver);
		readOnlyComponent(isReadOnly("CovenantDialog_Documentreceived"), this.documentRecieved);
		readOnlyComponent(isReadOnly("CovenantDialog_Allowpostponement"), this.allowPostponment);
		readOnlyComponent(isReadOnly("CovenantDialog_Extendeddate"), this.extendedDate);
		readOnlyComponent(isReadOnly("CovenantDialog_Frequency"), this.covenantFrequency);
		readOnlyComponent(isReadOnly("CovenantDialog_Gracedays"), this.covenantGraceDays);
		readOnlyComponent(isReadOnly("CovenantDialog_Alerttoroles"), this.notifyTo.getButton());
		readOnlyComponent(isReadOnly("CovenantDialog_Alertdays"), this.alertDays);
		readOnlyComponent(isReadOnly("CovenantDialog_Internaluse"), this.internalUse);
		this.description.setReadonly(true);
		// readOnlyComponent(isReadOnly("CovenantDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("CovenantDialog_Remarks"), this.remarks);
		readOnlyComponent(isReadOnly("CovenantDialog_AdditionalRemarks"), this.additionalRemarks);
		readOnlyComponent(isReadOnly("CovenantDialog_StandardValue"), this.standardValue);
		readOnlyComponent(isReadOnly("CovenantDialog_ActualValue"), this.actualValue);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (getCovenant().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if (enqiryModule) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isReadOnly(String rightName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(rightName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.category.setDisabled(true);
		this.covenantType.setReadonly(true);
		this.description.setReadonly(true);
		this.otc.setDisabled(true);
		this.pdd.setDisabled(true);
		this.receivableDate.setDisabled(true);
		this.alwWaiver.setDisabled(true);
		this.documentRecieved.setDisabled(true);
		this.allowPostponment.setDisabled(true);
		this.extendedDate.setDisabled(true);
		this.covenantFrequency.setDisabled(true);
		this.alertsRequired.setDisabled(true);
		this.alertType.setDisabled(true);
		this.covenantGraceDays.setReadonly(true);
		this.alertDays.setReadonly(true);
		this.notifyTo.setReadonly(true);
		this.internalUse.setDisabled(true);
		this.remarks.setReadonly(true);
		this.additionalRemarks.setReadonly(true);
		this.standardValue.setReadonly(true);
		this.actualValue.setReadonly(true);
		this.btnCovenantReceived.setDisabled(true);

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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_btnSave"));
			this.btnCovenantReceived.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_DocReceived"));
		}

		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.mandRole.setMaxlength(100);
		this.mandRole.setTextBoxWidth(151);
		this.mandRole.setModuleName("SecurityRoleEnq");
		this.mandRole.setValueColumn("RoleCd");
		this.mandRole.setDescColumn("RoleDesc");
		this.mandRole.setValidateColumns(new String[] { "RoleCd" });

		if (!"".equals(StringUtils.trimToEmpty(allowedRoles))) {
			String[] roles = allowedRoles.split(";");
			Filter[] fintypeRoles = new Filter[1];
			fintypeRoles[0] = Filter.in("RoleCd", Arrays.asList(roles));
			this.mandRole.setFilters(fintypeRoles);
		}

		this.loanStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receivableDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.documentRecievedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.covenantNextFrequencyDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.covenantGraceDueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.extendedDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.notifyTo.setModuleName("OperationRoles");
		this.notifyTo.setValueColumn("RoleCd");
		this.notifyTo.setDescColumn("RoleDesc");
		this.notifyTo.setValidateColumns(new String[] { "RoleCd" });
		this.notifyTo.setMultySelection(true);
		this.notifyTo.setInputAllowed(false);
		this.notifyTo.setWidth("150px");

		this.description.setMaxlength(500);
		this.alertDays.setMaxlength(3);
		this.covenantGraceDays.setMaxlength(3);
		this.description.setWidth("850px");
		this.remarks.setWidth("850px");
		this.remarks.setMaxlength(500);

		this.additionalRemarks.setWidth("1200px");
		this.additionalRemarks.setMaxlength(10000);

		if ("Maintanance".equals(module) || enqiryModule) {
			this.rw_manrole.setVisible(false);
		} else {
			this.rw_allowedPostpone.setVisible(false);
		}

		setStatusDetails(gbStatusDetails, groupboxWf, south, enqiryModule);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinCovenantType FinCovenantTypeDetail
	 */
	public void doWriteBeanToComponents(Covenant covenant) {
		logger.debug(Literal.ENTERING);

		fillList(this.category, covenantCategories, covenant.getCategory());
		fillList(this.covenantFrequency, listFrequency, covenant.getFrequency());
		fillList(this.alertType, listAlertType, covenant.getAlertType());
		onSelectAlertType();
		if (covenant.getCovenantTypeId() != 0) {
			this.covenantType.setValue(covenant.getCovenantTypeCode());
			this.covenantType.setDescription(covenant.getCovenantTypeDescription());
			CovenantType covenantType = new Covenant();
			covenantType.setId(covenant.getCovenantTypeId());
			covenantType.setCode(covenant.getCovenantTypeCode());
			covenantType.setDescription(covenant.getCovenantTypeDescription());
			covenantType.setGraceDays(covenant.getGraceDays());
			covenantType.setAlertDays(covenant.getAlertDays());
			covenantType.setAlertToRoles(covenant.getAlertToRoles());
			covenantType.setAlertsRequired(covenant.isAlertsRequired());
			covenantType.setAlertType(covenant.getAlertType());
			/*
			 * covenantType.setAllowedPaymentModes(covenant.getAllowedPaymentModes());
			 */
			this.covenantType.setObject(covenantType);
		}

		if (this.mandRole.isVisible()) {
			String mandateRole = StringUtils.trimToEmpty(covenant.getMandatoryRole());

			if (StringUtils.isNotBlank(mandateRole)) {
				this.mandRole.setValue(mandateRole);
				this.mandRole.setDescription(covenant.getMandRoleDescription());
				SecurityRole secRole = new SecurityRole();
				secRole.setRoleCd(covenant.getMandatoryRole());
				this.mandRole.setObject(secRole);
				onFullfillMandRole();
			}

		}

		if (covenant.getAlertToRoles() != null) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder roles = new StringBuilder();
			for (String role : covenant.getAlertToRoles().split(",")) {
				role = StringUtils.trim(role);

				if (roles.length() > 0) {
					roles.append(",");
				}
				roles.append(role);

				SecurityRole securityRole = new SecurityRole();
				securityRole.setRoleCd(role);
				securityRole.setRoleCode(role);

				map.put(role, securityRole);
			}

			this.notifyTo.setValue(roles.toString());
			this.notifyTo.setAttribute("data", map);
			this.notifyTo.setTooltiptext(roles.toString());
			this.notifyTo.setInputAllowed(false);
			this.notifyTo.setSelectedValues(map);
		}

		this.description.setValue(covenantType.getDescription());

		if (covenant.getRemarks1() != null) {
			this.additionalRemarks.setValue(new String(covenant.getRemarks1(), StandardCharsets.UTF_8));
		}
		this.pdd.setChecked(covenant.isPdd());

		this.otc.setChecked(covenant.isOtc());

		this.alwWaiver.setChecked(covenant.isAllowWaiver());

		onCheckALwWaiver();

		if (this.allowPostponment.isVisible()) {
			this.allowPostponment.setChecked(covenant.isAllowPostPonement());
			onCheckAllowPostponement(covenant.isAllowPostPonement());
			this.extendedDate.setValue(covenant.getExtendedDate());
		}

		if (covenant.getReceivableDate() != null) {
			this.receivableDate.setValue(covenant.getReceivableDate());
		}

		this.documentRecieved.setChecked(covenant.isDocumentReceived());

		if (covenant.getDocumentReceivedDate() != null) {
			this.documentRecievedDate.setValue(covenant.getDocumentReceivedDate());
		}

		// onCheckDocumentRecieved();

		if (covenant.getNextFrequencyDate() != null) {
			this.covenantNextFrequencyDate.setValue(covenant.getNextFrequencyDate());
		}

		this.covenantGraceDays.setValue(covenant.getGraceDays());

		if (covenant.getGraceDueDate() != null) {
			this.covenantGraceDueDate.setValue(covenant.getGraceDueDate());
		}

		this.alertDays.setValue(covenant.getAlertDays());
		if (covenant.isAlertsRequired()) {
			this.alertsRequired.setChecked(true);
		} else {
			this.alertsRequired.setChecked(false);
		}

		// onCheckAlertsRequired();

		this.remarks.setValue(covenant.getAdditionalField1());
		this.standardValue.setValue(covenant.getAdditionalField2());
		this.actualValue.setValue(covenant.getAdditionalField3());
		this.recordStatus.setValue(covenant.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(covenant.getRecordType()));

		this.internalUse.setChecked(covenant.isInternalUse());

		if (covenant.isNewRecord()) {
			this.covenantType.setButtonDisabled(true);
		}

		this.loanStartDate.setDisabled(true);

		if (financedetail != null) {
			this.loanStartDate.setValue(financedetail.getFinScheduleData().getFinanceMain().getFinStartDate());
		}

		maturityDate = financedetail.getFinScheduleData().getFinanceMain().getMaturityDate();
		loanStartDt = financedetail.getFinScheduleData().getFinanceMain().getFinStartDate();

		if (maturityDate == null) {
			maturityDate = loanStartDt;
		}

		this.covenantNextFrequencyDate.setValue(covenant.getNextFrequencyDate());

		this.covenantGraceDueDate.setValue(covenant.getGraceDueDate());

		doFillCovenantDocument(covenant.getCovenantDocuments());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinCovenantType
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(Covenant covenant) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			String strCategory = null;
			if (this.category.getSelectedItem() != null) {
				strCategory = this.category.getSelectedItem().getValue().toString();
			}
			if (strCategory != null && !PennantConstants.List_Select.equals(strCategory)) {
				covenant.setCategory(strCategory);

			} else {
				covenant.setCategory(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.covenantType.getValidatedValue();
			Object obj = this.covenantType.getObject();
			if (obj != null) {
				covenant.setCovenantTypeId(((CovenantType) obj).getId());
				covenant.setCode(((CovenantType) obj).getCode());
				covenant.setCovenantTypeCode(((CovenantType) obj).getCode());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if ("Organization".equals(module) && !this.mandRole.isButtonDisabled()) {
			try {
				this.mandRole.getValidatedValue();
				Object obj = this.mandRole.getObject();
				if (obj != null) {
					covenant.setMandatoryRole(((SecurityRole) obj).getRoleCd());
					covenant.setMandRoleDescription(((SecurityRole) obj).getRoleDesc());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			covenant.setMandatoryRole(null);
			covenant.setMandRoleDescription(null);
		}

		try {
			this.notifyTo.getValue();
			Map<String, Object> roles = getSelectedValues(this.notifyTo);
			if (roles != null) {
				if (!this.notifyTo.getButton().isDisabled() && roles.size() > 5) {
					throw new WrongValueException(this.notifyTo.getButton(),
							"The number of roles should not exceed more than 5.");
				}

				StringBuilder data = new StringBuilder();
				for (String role : roles.keySet()) {
					if (data.length() > 0) {
						data.append(",");
					}
					data.append(role);
				}

				covenant.setAlertToRoles(data.toString());
			} else {
				covenant.setAlertToRoles(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setCovenantTypeDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String addRemarks = this.additionalRemarks.getValue();
			covenant.setRemarks1(addRemarks.getBytes(StandardCharsets.UTF_8));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAllowWaiver(this.alwWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setPdd(this.pdd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setOtc(this.otc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setInternalUse(this.internalUse.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setReceivableDate(this.receivableDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setDocumentReceived(this.documentRecieved.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setDocumentReceivedDate(this.documentRecievedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strCategory = null;
			if (this.covenantFrequency.getSelectedItem() != null) {
				strCategory = this.covenantFrequency.getSelectedItem().getValue().toString();
			}
			if (strCategory != null && !PennantConstants.List_Select.equals(strCategory)) {
				covenant.setFrequency(strCategory);

			} else {
				covenant.setFrequency(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setNextFrequencyDate(this.covenantNextFrequencyDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.covenantGraceDays.getValue() != null) {
				covenant.setGraceDays(this.covenantGraceDays.getValue());
			} else {
				covenant.setGraceDays(0);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setGraceDueDate(this.covenantGraceDueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.alertDays.getValue() != null) {
				covenant.setAlertDays(this.alertDays.getValue());
			} else {
				covenant.setAlertDays(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAdditionalField1(this.remarks.getValue());
			covenant.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAdditionalField2(this.standardValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAdditionalField3(this.actualValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAllowPostPonement(this.allowPostponment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setExtendedDate(this.extendedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			covenant.setModule("Loan");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setModule("Loan");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			covenant.setAlertsRequired(this.alertsRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strAlertType = null;
			if (this.alertType.getSelectedItem() != null) {
				strAlertType = this.alertType.getSelectedItem().getValue().toString();
			}
			if (strAlertType != null && !PennantConstants.List_Select.equals(strAlertType)) {
				covenant.setAlertType(strAlertType);

			} else {
				covenant.setAlertType(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			this.covenantDetailsTab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		saveDocumentDetails(getCovenantDocuments());

		covenant.setDocumentDetails(this.covenant.getDocumentDetails());

		covenant.setCovenantDocuments(getCovenantDocuments());

		covenant.setRecordStatus(this.recordStatus.getValue());
		setCovenant(covenant);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		loanStartDt = financedetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		Date appDate = SysParamUtil.getAppDate();
		Date receivableDate = this.receivableDate.getValue();
		String frequency = this.covenantFrequency.getSelectedItem().getValue();

		if (!this.category.isDisabled()) {
			this.category.setConstraint(new StaticListValidator(covenantCategories,
					Labels.getLabel("label_CovenantsDialog_CovenantCategory.value")));
		}

		if (!this.covenantType.isReadonly()) {
			this.covenantType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinCovenantTypeDialog_CovenantType.value"), null, true, true));
		}

		String description = this.description.getValue();
		if (!this.description.isReadonly() && description.length() > 0) {
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_Covenant_Description.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.additionalRemarks.isReadonly()) {
			this.additionalRemarks.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CovenantsDialog_AdditionalRemarks.value"), null, false));
		}

		if (!this.receivableDate.isReadonly() && this.pdd.isChecked()) {
			receivableDate = this.receivableDate.getValue();
			String receivableDateLabel = Labels.getLabel("label_FinCovenantTypeDialog_RecvbleDate.value");
			if (receivableDate == null) {
				this.receivableDate.setConstraint(new PTDateValidator(receivableDateLabel, true));
			} else if (DateUtil.compare(receivableDate, loanStartDt) < 0
					|| DateUtil.compare(receivableDate, maturityDate) > 0) {
				this.receivableDate.setConstraint(
						new PTDateValidator(receivableDateLabel, true, loanStartDt, maturityDate, false));
			}
		}

		if (!this.covenantGraceDays.isDisabled() && this.covenantGraceDays.getValue() != null
				&& this.covenantGraceDays.getValue() != 0) {
			this.covenantGraceDays.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CovenantsDialog_CovenantGraceDays.value"), true, false, 0, 30));
		}

		if (!this.alertDays.isDisabled() && this.alertDays.getValue() != null && this.alertDays.getValue() != 0) {
			int days = 0;
			if (frequency.equals("M"))
				days = 30;
			else if (frequency.equals("Q"))
				days = 90;
			else if (frequency.equals("H"))
				days = 180;
			else if (frequency.equals("A"))
				days = 365;
			this.alertDays.setConstraint(new PTNumberValidator(Labels.getLabel("label_CovenantsDialog_AlertDays.value"),
					true, false, 0, days));
		}

		if (!documentRecieved.isDisabled()
				&& (this.documentRecieved.isChecked() && PennantConstants.List_Select.equals(frequency))) {
			this.covenantFrequency.setConstraint(new PTListValidator<Property>(
					Labels.getLabel("label_CovenantsDialog_CovenantFrequency.value"), listFrequency, true));
		}

		if (this.alertType.getSelectedIndex() == 1) {
			if (this.notifyTo.isVisible() && !this.notifyTo.isButtonDisabled()) {
				if (!this.notifyTo.getButton().isDisabled() && this.covenantNextFrequencyDate.getValue() != null) {
					this.notifyTo.setConstraint(new PTStringValidator(
							Labels.getLabel("label_CovenantTypeDialog_AlertToRoles.value"), null, true));
				}
			}
		}

		if (!this.documentRecievedDate.isReadonly() && this.documentRecieved.isChecked()
				&& !this.documentRecievedDate.isDisabled()) {
			if (this.documentRecievedDate.getValue() == null) {
				this.documentRecievedDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_CovenantsDialog_DocumentRecievedDate.value"), true));
			} else if (DateUtil.compare(this.documentRecievedDate.getValue(), appDate) > 0) {
				throw new WrongValueException(this.documentRecievedDate, Labels.getLabel("DATE_NO_FUTURE",
						new String[] { Labels.getLabel("label_CovenantsDialog_DocumentRecievedDate.value") }));
			}
		}

		if (!this.extendedDate.isReadonly() && this.allowPostponment.isChecked()) {
			if (this.extendedDate.getValue() == null) {
				this.extendedDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_CovenantDocumentDialog_ExtendedDate.value"), true));
			} else if (DateUtil.compare(this.extendedDate.getValue(), receivableDate) <= 0
					&& getComboboxValue(this.covenantFrequency).equals("#")
					&& this.covenantNextFrequencyDate.getValue() == null) {
				this.extendedDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_CovenantDocumentDialog_ExtendedDate.value"), true,
								receivableDate, maturityDate, false));
			} else if (DateUtil.compare(this.extendedDate.getValue(), receivableDate) <= 0 || DateUtil
					.compare(this.extendedDate.getValue(), this.covenantNextFrequencyDate.getValue()) >= 0) {
				this.extendedDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_CovenantDocumentDialog_ExtendedDate.value"), true,
								receivableDate, this.covenantNextFrequencyDate.getValue(), false));
			}
		}

		if (this.covenantNextFrequencyDate.getValue() != null
				&& !this.covenantNextFrequencyDate.getValue().equals("")) {
			if (this.covenantFrequency.getSelectedItem().getValue() == "O" && this.pdd.isChecked()
					&& this.alertDays.getValue() != null && this.alertDays.getValue() != 0) {
				int days = DateUtil.getDaysBetween(loanStartDt, this.receivableDate.getValue());
				this.alertDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_CovenantsDialog_AlertDays.value"), true, false, 0, days));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.category.setConstraint("");
		this.receivableDate.setConstraint("");
		this.covenantFrequency.setConstraint("");
		this.alertDays.setConstraint("");
		this.covenantFrequency.setConstraint("");
		this.notifyTo.setConstraint("");
		this.covenantType.setConstraint("");
		this.description.setConstraint("");
		this.mandRole.setConstraint("");
		this.covenantGraceDays.setConstraint("");
		this.covenantNextFrequencyDate.setConstraint("");
		this.documentRecievedDate.setConstraint("");
		this.extendedDate.setConstraint("");
		this.extendedDate.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);
		if (!this.covenantType.isReadonly()) {
			this.covenantType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinCovenantTypeDialog_CovenantType.value"), null, true, true));
		}

		if (!this.mandRole.isReadonly()
				&& (covenantTypeObject != null && "LOS".equals(covenantTypeObject.getCovenantType()))) {
			if (!(this.pdd.isChecked() || (this.otc.isChecked()))) {
				this.mandRole.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinCovenantTypeDialog_MandRole.value"), null, true, true));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.covenantType.setConstraint("");
		this.mandRole.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.covenantType.setErrorMessage("");
		this.description.setErrorMessage("");
		this.mandRole.setErrorMessage("");
		this.receivableDate.setErrorMessage("");
		this.additionalRemarks.setErrorMessage("");
		this.extendedDate.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final Covenant aCovenant, String tranType) {
		if (isNewCustomer()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFinCovenantTypeProcess(aCovenant, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.covenantDialogWindow, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				covenantsListCtrl.doFillCovenants(this.covenants);
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Covenant aCovenant = new Covenant();
		BeanUtils.copyProperties(getCovenant(), aCovenant);

		if (StringUtils.isBlank(aCovenant.getRecordType())) {
			aCovenant.setNewRecord(true);
		}

		final String keyReference = Labels.getLabel("FinCovenantType_CovenantType") + " : "
				+ aCovenant.getCovenantTypeCode();

		doDelete(keyReference, aCovenant);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// remove validation, if there are a save before
		this.covenantType.setValue("");
		this.description.setValue("");
		this.mandRole.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Covenant aFinCovenantType = new Covenant();
		BeanUtils.copyProperties(getCovenant(), aFinCovenantType);
		boolean isNew = false;
		doClearMessage();

		if (isWorkFlowEnabled()) {
			aFinCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinCovenantType.getNextTaskId(),
					aFinCovenantType);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFinCovenantType.getRecordType()) && isValidation()) {
			doSetValidation();
			doWriteComponentsToBean(aFinCovenantType);
		}

		if (!this.alwWaiver.isChecked()) {
			if (!this.mandRole.isButtonDisabled()) {
				if (!this.pdd.isChecked() && !this.otc.isChecked()
						&& (this.mandRole.getValue() == null || this.mandRole.getValue().equals(""))) {
					MessageUtil.showError("Please select either PDD or OTC or Mandatory Role");
					return;
				}
			} else if (this.mandRole.isButtonDisabled()) {
				if (!this.pdd.isChecked() && !this.otc.isChecked()) {
					MessageUtil.showError("Please select either PDD or OTC");
					return;
				}
			}
		}

		isNew = aFinCovenantType.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				if (isNew) {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinCovenantType.setNewRecord(true);
				}
			}
		} else {
			if (isNewCustomer()) {
				if (isNewRecord()) {
					aFinCovenantType.setVersion(1);
					aFinCovenantType.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
					aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
					aFinCovenantType.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinCovenantType.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinCovenantTypeProcess(aFinCovenantType, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.covenantDialogWindow, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {

					covenantsListCtrl.doFillCovenants(this.covenants);

					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newFinCovenantTypeProcess(Covenant acovenant, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(acovenant, tranType);
		covenants = new ArrayList<>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(acovenant.getCategory());
		valueParm[1] = StringUtils.trimToEmpty(acovenant.getCode());

		errParm[0] = PennantJavaUtil.getLabel("label_CovenantsDialog_CovenantCategory.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Covenant_Code.value") + ":" + valueParm[1];

		List<Covenant> covenantsList = new ArrayList<>();
		covenantsList = covenantsListCtrl.getCovenants();

		CovenantType beforeImage = acovenant.getBefImage();

		if (beforeImage != null) {
			if (beforeImage.getRecordType() != null && beforeImage.getRecordStatus() != null) {
				if (StringUtils.equals(beforeImage.getRecordType(), PennantConstants.RECORD_TYPE_NEW)
						&& StringUtils.isBlank(beforeImage.getRecordStatus())) {
					acovenant.setRecordType(PennantConstants.RCD_ADD);
				}
			} else {
				acovenant.setRecordType(PennantConstants.RCD_ADD);
			}
		}
		acovenant.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		if (CollectionUtils.isEmpty(covenantsList)) {
			if (!recordAdded) {
				covenants.add(acovenant);
			}
			return auditHeader;
		}

		for (Covenant item : covenantsList) {
			if (item.getCovenantTypeId() != acovenant.getCovenantTypeId()) {
				covenants.add(item);
				continue;
			}

			if (isNewRecord()) {
				auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						getUserWorkspace().getUserLanguage()));
				return auditHeader;
			}

			if (tranType == PennantConstants.TRAN_DEL) {
				if (acovenant.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
					acovenant.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					recordAdded = true;
					covenants.add(acovenant);
				} else if (acovenant.getRecordType().equals(PennantConstants.RCD_ADD)) {
					recordAdded = true;
				} else if (acovenant.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					acovenant.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					recordAdded = true;
					covenants.add(acovenant);
				} else if (acovenant.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
					recordAdded = true;
					for (Covenant covenant2 : covenantsList) {
						if (covenant2.getKeyReference() == acovenant.getKeyReference()
								&& covenant2.getId() == acovenant.getId()) {
							covenants.add(covenant2);
						}
					}
				}
			} else if (tranType != PennantConstants.TRAN_UPD) {
				covenants.add(item);
			}

		}

		if (!recordAdded) {
			covenants.add(acovenant);
		}
		return auditHeader;
	}

	/**
	 * Change the branch for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$covenantType(Event event) {
		onChangeCovenantType();
	}

	public void onChangeCovenantType() {
		if (StringUtils.isBlank(this.covenantType.getValue())) {
			clearOnChangeCovenantData();
			return;
		}

		covenantTypeObject = (CovenantType) this.covenantType.getObject();
		if (covenantTypeObject == null) {
			return;
		}

		Search search = new Search(CovenantType.class);
		search.addFilterEqual("Id", covenantTypeObject.getId());

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		covenantTypeObject = (CovenantType) searchProcessor.getResults(search).get(0);

		clearOnChangeCovenantData();

		covenantTypeDetails(covenantTypeObject);
	}

	public void setFrequency(String frequency) {
		if (frequency == null) {
			return;
		}

		if (frequency.equals("M")) {
			this.covenantFrequency.setSelectedIndex(1);
		} else if (frequency.equals("Q")) {
			this.covenantFrequency.setSelectedIndex(2);
		} else if (frequency.equals("H")) {
			this.covenantFrequency.setSelectedIndex(3);
		} else if (frequency.equals("A")) {
			this.covenantFrequency.setSelectedIndex(4);
		} else if (frequency.equals("O")) {
			this.covenantFrequency.setSelectedIndex(5);
		} else {
			this.covenantFrequency.setSelectedIndex(0);
		}

		setFrequencyDateField(frequency);
	}

	/**
	 * Change the branch for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$mandRole(Event event) {
		logger.debug(Literal.ENTERING);
		onFullfillMandRole();
		logger.debug(Literal.LEAVING);
	}

	private void onFullfillMandRole() {
		Object dataObject = this.mandRole.getObject();

		if (dataObject instanceof String) {
			this.mandRole.setValue(dataObject.toString());
			this.mandRole.setDescription("");
			this.mandRole.setAttribute("MandRole", null);
			// this.pdd.setDisabled(false);
			// this.otc.setDisabled(false);
		} else {
			SecurityRole secRole = (SecurityRole) dataObject;
			if (secRole != null) {
				this.mandRole.setValue(secRole.getRoleCd());
			} else {
			}
		}
		onCheckDocumentRecieved();
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Covenant aFinCovenantType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinCovenantType.getBefImage(), aFinCovenantType);
		return new AuditHeader(aFinCovenantType.getKeyReference(), null, null, null, auditDetail,
				aFinCovenantType.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Covenant getCovenant() {
		return this.covenant;
	}

	public void setCovenant(Covenant covenant) {
		this.covenant = covenant;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setCovenantsListCtrl(CovenantsListCtrl covenantsListCtrl) {
		this.covenantsListCtrl = covenantsListCtrl;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

	public List<Covenant> getCovenants() {
		return covenants;
	}

	public LegalDetail getLegalDetail() {
		return legalDetail;
	}

	public void onSelect$category(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectCategory();
		logger.debug(Literal.LEAVING);
	}

	public void onSelectCategory() {
		String category = this.category.getSelectedItem().getValue();

		if (PennantConstants.List_Select.equals(category)) {
			this.covenantType.setButtonDisabled(true);
			this.covenantType.setObject(null);
			this.covenantType.setValue("");
			this.covenantType.setDescription("");
		} else {
			this.covenantType.setButtonDisabled(true);
			this.covenantType.setObject(null);
			this.covenantType.setValue("");
			this.description.setValue("");
			this.covenantType.setDescription("");
			this.alwWaiver.setChecked(false);
			this.internalUse.setChecked(false);
			doSetCovenantTypeProperties(category);
		}
	}

	public void doSetCovenantTypeProperties(String category) {
		this.covenantType.setButtonDisabled(false);
		this.covenantType.setMaxlength(50);
		this.covenantType.setTextBoxWidth(151);
		this.covenantType.setMandatoryStyle(true);
		this.covenantType.setModuleName("CovenantType");
		this.covenantType.setValueColumn("Id");
		this.covenantType.setDescColumn("Description");
		this.covenantType.setValueType(DataType.LONG);
		this.covenantType.setValidateColumns(new String[] { "Id", "Code", "Description" });

		Filter[] filters = new Filter[1];
		filters[0] = Filter.in("Category", category);
		this.covenantType.setFilters(filters);
	}

	public void onCheck$pdd(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckOTC();
		oncheckPDD();
		onCheckDocumentRecieved();
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwWaiver(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckALwWaiver();

		if (this.alwWaiver.isChecked()) {
			return;
		} else {
			this.pdd.setDisabled(false);
			this.otc.setDisabled(false);
			this.receivableDate.setDisabled(false);
			this.documentRecieved.setDisabled(false);
			this.covenantFrequency.setDisabled(false);
			this.covenantGraceDays.setDisabled(false);
			this.notifyTo.setButtonDisabled(false);
			this.alertDays.setDisabled(false);
			this.remarks.setDisabled(false);
			this.standardValue.setDisabled(false);
			this.actualValue.setDisabled(false);
			this.mandRole.setButtonDisabled(false);
			this.allowPostponment.setDisabled(false);
			this.extendedDate.setDisabled(false);
			this.btnCovenantReceived.setDisabled(false);
		}

		if (covenantTypeObject != null) {
			Search search = new Search(CovenantType.class);
			search.addFilterEqual("Id", covenantTypeObject.getId());
			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			covenantTypeObject = (CovenantType) searchProcessor.getResults(search).get(0);

			covenantTypeDetails(covenantTypeObject);
		}
		logger.debug(Literal.LEAVING);
	}

	public void oncheckPDD() {
		if (this.pdd.isChecked()) {
			this.receivableDate.setDisabled(enqiryModule);
			this.allowPostponment.setDisabled(enqiryModule);
			this.documentRecieved.setChecked(false);
			this.documentRecieved.setDisabled(true);
		} else {
			this.receivableDate.setText(null);
			this.receivableDate.setDisabled(true);
			this.allowPostponment.setDisabled(true);
			this.allowPostponment.setChecked(false);
			this.documentRecieved.setDisabled(false);
		}
	}

	public void onCheckALwWaiver() {
		if (this.alwWaiver.isChecked()) {
			this.pdd.setDisabled(true);
			this.pdd.setChecked(false);
			this.otc.setChecked(false);
			this.otc.setDisabled(true);
			this.receivableDate.setText(null);
			this.receivableDate.setDisabled(true);
			this.documentRecieved.setDisabled(true);
			this.documentRecieved.setChecked(false);
			this.documentRecievedDate.setText(null);
			this.covenantFrequency.setDisabled(true);
			this.covenantFrequency.setSelectedIndex(0);
			this.covenantNextFrequencyDate.setText(null);
			this.covenantGraceDays.setValue(0);
			this.covenantGraceDays.setDisabled(true);
			this.covenantGraceDueDate.setText(null);
			this.notifyTo.setButtonDisabled(true);
			this.notifyTo.setValue("");
			this.notifyTo.setDescription("");
			this.alertDays.setValue(0);
			this.alertDays.setDisabled(true);
			this.remarks.setDisabled(true);
			this.remarks.setValue(null);
			this.standardValue.setValue(null);
			this.standardValue.setDisabled(true);
			this.actualValue.setDisabled(true);
			this.actualValue.setValue(null);
			this.mandRole.setButtonDisabled(true);
			this.mandRole.setValue(null);
			this.allowPostponment.setDisabled(true);
			this.extendedDate.setDisabled(true);
			this.extendedDate.setText(null);
			this.btnCovenantReceived.setDisabled(true);
			this.alertsRequired.setChecked(false);
			this.alertType.setSelectedIndex(0);
		} else if (enqiryModule) {
			this.pdd.setDisabled(true);
			this.otc.setDisabled(true);
			readOnlyComponent(true, this.receivableDate);
			this.documentRecieved.setDisabled(true);
			this.covenantFrequency.setDisabled(true);
			this.covenantGraceDays.setDisabled(true);
			this.notifyTo.setButtonDisabled(true);
			this.alertDays.setDisabled(true);
			this.standardValue.setDisabled(true);
			this.actualValue.setDisabled(true);
			this.allowPostponment.setDisabled(true);
			this.mandRole.setButtonDisabled(true);
			readOnlyComponent(true, this.extendedDate);
			this.btnCovenantReceived.setDisabled(true);
			this.alertsRequired.setChecked(false);
			this.alertType.setSelectedIndex(0);
		}
	}

	public void onCheckLOS(String covenantType) {
		if (!module.equals("Maintanance")) {
			if ("LOS".equals(covenantType)) {
				this.mandRole.setButtonDisabled(false);
				this.pdd.setDisabled(true);
				this.pdd.setChecked(false);
				this.otc.setDisabled(true);
				this.otc.setChecked(false);
			} else {
				this.mandRole.setButtonDisabled(true);
				this.pdd.setDisabled(false);
				this.otc.setDisabled(false);
			}
		} else {
			this.pdd.setDisabled(false);
			this.pdd.setChecked(false);
			this.otc.setDisabled(false);
			this.otc.setChecked(false);
		}
	}

	public void onCheck$otc(Event event) {
		onCheckOTC();
	}

	public void onCheckOTC() {
		if (this.alwWaiver.isChecked() || enqiryModule) {
			return;
		}
		if (this.pdd.isChecked()) {
			this.otc.setDisabled(true);
			this.otc.setChecked(false);
			this.mandRole.setButtonDisabled(true);
			this.mandRole.setValue(null);
			this.receivableDate.setDisabled(false);
		} else if (this.otc.isChecked()) {
			this.pdd.setDisabled(true);
			this.pdd.setChecked(false);
			this.mandRole.setButtonDisabled(true);
			this.mandRole.setValue(null);
		} else if (this.mandRole.getValue() != null && !this.mandRole.getValue().equals("")) {
			this.pdd.setChecked(false);
			this.otc.setChecked(false);
		} else {
			this.pdd.setDisabled(false);
			if (!module.equals("Maintanance")) {
				this.mandRole.setButtonDisabled(false);
				this.mandRole.setValue(null);
			}
			this.otc.setDisabled(false);
			this.receivableDate.setDisabled(true);
			this.receivableDate.setText(null);
		}

		oncheckPDD();
		onCheckDocumentRecieved();
	}

	public void onCheck$documentRecieved(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckDocumentRecieved();
		logger.debug(Literal.LEAVING);
	}

	public void onCheckDocumentRecieved() {
		if (isReadOnly("CovenantDialog_Documentreceived")) {
			return;
		}

		if (this.documentRecieved.isChecked() && (this.pdd.isChecked() || this.otc.isChecked()
				|| (this.mandRole.getValue() != null && !this.mandRole.getValue().equals("")))) {
			// this.btnCovenantReceived.setDisabled(false);
		} else {
			// this.btnCovenantReceived.setDisabled(true);
		}
	}

	/**
	 * Do Set DownLoad link Properties. <br>
	 */
	public FinCovenantMaintanceDialogCtrl getFinCovenantMaintanceDialogCtrl() {
		return finCovenantMaintanceDialogCtrl;
	}

	public void setFinCovenantMaintanceDialogCtrl(FinCovenantMaintanceDialogCtrl finCovenantMaintanceDialogCtrl) {
		this.finCovenantMaintanceDialogCtrl = finCovenantMaintanceDialogCtrl;
	}

	public void setFrequencyDateField(String strFrequencyType) {
		if (enqiryModule) {
			return;
		}

		if (PennantConstants.List_Select.equals(strFrequencyType) || StringUtils.isEmpty(strFrequencyType)) {
			disablePDDDetailsGroup();
			disablePDD();
			return;
		}

		Date frequencyDate = this.receivableDate.getValue();
		if (strFrequencyType == null || frequencyDate == null) {
			return;
		}

		enableFrequencyFields();

		if ("#".equals(strFrequencyType)) {
			frequencyDate = null;
			disablePDD();
			disablePDDDetailsGroup();
		}

		if ("M".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 1);
		} else if ("Q".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 3);
		} else if ("H".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 6);
		} else if ("A".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 12);
		} else if ("O".equals(strFrequencyType)) {
			if (covenantTypeObject != null && !covenantTypeObject.isAlertsRequired()) {
				// frequencyDate = null;
				disablePDD();
				disablePDDDetailsGroup();
			}
		} else if ("B".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addYears(frequencyDate, 2);
		} else if ("5".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addYears(frequencyDate, 5);
		} else if ("8".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addYears(frequencyDate, 8);
		}

		if (frequencyDate != null) {
			if ("O".equals(strFrequencyType)) {
				this.covenantNextFrequencyDate.setValue(null);
				this.covenantNextFrequencyDate.setDisabled(true);
			} else {
				this.covenantNextFrequencyDate.setValue(frequencyDate);
				this.covenantNextFrequencyDate.setDisabled(false);
			}

			Date covenantNextFrequencyDate = this.covenantNextFrequencyDate.getValue();

			int covenantGraceDays = 0;
			if (this.covenantGraceDays.getValue() != null) {
				covenantGraceDays = this.covenantGraceDays.getValue();
			}

			if (covenantNextFrequencyDate != null) {
				this.covenantGraceDueDate.setValue(DateUtil.addDays(covenantNextFrequencyDate, covenantGraceDays));
			} else {
				this.covenantGraceDueDate.setValue(null);
				this.covenantGraceDueDate.setDisabled(true);
			}

		} else {
			this.covenantNextFrequencyDate.setText(null);
			this.covenantGraceDueDate.setText(null);
		}
	}

	private void disablePDD() {
		// this.covenantGraceDays.setDisabled(true);
		this.notifyTo.setButtonDisabled(true);
		// this.alertDays.setDisabled(true);
		// this.covenantGraceDays.setValue(0);
		this.notifyTo.setValue(null);
		// this.alertDays.setValue(0);
	}

	public void onSelectFrequency(String frequencyType) {
		setFrequencyDateField(frequencyType);
	}

	public void onSelect$covenantFrequency(Event event) {
		logger.debug(Literal.ENTERING);

		setFrequencyDateField(this.covenantFrequency.getSelectedItem().getValue());

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnCovenantReceived(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Boolean flag = false;
		if (this.pdd.isChecked() || this.otc.isChecked()
				|| this.mandRole.getValue() != null && !this.mandRole.getValue().equals("")) {
			flag = true;
		} else {
			MessageUtil.showError("Please select either PDD or OTC or Mandatory Role");
			return;
		}

		Clients.clearWrongValue(this.listboxDocuments);
		CovenantDocument covenantDocument = new CovenantDocument();
		if (this.pdd.isChecked()) {
			covenantDocument.setCovenantType(PennantConstants.COVENANT_PDD);
		} else if (this.otc.isChecked()) {
			covenantDocument.setCovenantType(PennantConstants.COVENANT_OTC);
		} else if (this.mandRole.getValue() != null && !this.mandRole.getValue().equals("")) {
			covenantDocument.setCovenantType(PennantConstants.COVENANT_LOS);
		}
		covenantDocument.setNewRecord(true);
		showCovenantDocumentView(covenantDocument);

		logger.debug(Literal.LEAVING);
	}

	public void onCovenantDocumentItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getOrigin().getTarget();
		CovenantDocument covenantDocument = (CovenantDocument) item.getAttribute("covenantData");

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, covenantDocument.getRecordType())
				|| StringUtils.trimToEmpty(covenantDocument.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

		} else {
			covenantDocument.setNewRecord(covenantDocument.isNewRecord());

			showCovenantDocumentView(covenantDocument);
		}

		logger.debug(Literal.LEAVING);
	}

	private void showCovenantDocumentView(CovenantDocument covenantDocument) throws InterruptedException {

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("covenantDocument", covenantDocument);
		map.put("covenantsDialogCtrl", this);
		map.put("role", getRole());
		map.put("frequency", this.covenantFrequency.getSelectedItem().getValue());
		map.put("nextFrequecnyDate", this.covenantNextFrequencyDate.getValue());
		map.put("loanStartDate", loanStartDt);
		map.put("loanMaturityDate", maturityDate);
		map.put("receivableDate", this.receivableDate.getValue());
		map.put("originalDocument", covenantDocument.isOriginalDocument());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Covenant/CovenantDocumentDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doFillCovenantDocument(List<CovenantDocument> covenantDocuments) {
		logger.debug(Literal.ENTERING);
		if (covenantDocuments == null) {
			return;
		}

		setCovenantDocuments(covenantDocuments);
		this.listboxDocuments.getItems().clear();

		Listcell lc;
		Listitem item;
		for (CovenantDocument covenantDocument : covenantDocuments) {
			item = new Listitem();
			lc = new Listcell(
					DateUtil.format(covenantDocument.getDocumentReceivedDate(), DateFormat.LONG_DATE.getPattern()));

			lc.setParent(item);
			if (this.pdd.isChecked()) {
				lc = new Listcell(PennantConstants.COVENANT_PDD);
			} else if (this.otc.isChecked()) {
				lc = new Listcell(PennantConstants.COVENANT_OTC);
			} else if (this.mandRole.getValue() != null && !this.mandRole.getValue().equals("")) {
				lc = new Listcell(PennantConstants.COVENANT_LOS);
			}

			lc.setParent(item);
			if (covenantDocument.getDocumentDetail() != null) {
				lc = new Listcell(covenantDocument.getDocumentDetail().getDocCategory() + "-"
						+ StringUtils.trimToEmpty(covenantDocument.getDocumentDetail().getDocName()));
			} else {
				lc = new Listcell();
			}
			lc.setParent(item);

			lc = new Listcell(covenantDocument.getDoctype());
			lc.setParent(item);

			item.setAttribute("covenantData", covenantDocument);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onCovenantDocumentItemDoubleClicked");
			this.listboxDocuments.appendChild(item);
			this.documentRecievedDate.setValue(covenantDocument.getDocumentReceivedDate());
			covenant.setDocumentReceivedDate(covenantDocument.getDocumentReceivedDate());
		}

		logger.debug(Literal.LEAVING);
	}

	private void saveDocumentDetails(List<CovenantDocument> covenantDocuments) {
		logger.debug(Literal.ENTERING);

		ArrayList<DocumentDetails> list = new ArrayList<>();
		Map<String, DocumentDetails> map = new HashMap<>();

		String finreference = financedetail.getFinScheduleData().getFinanceMain().getFinReference();

		for (CovenantDocument covenantDocument : covenantDocuments) {
			if (covenantDocument.isNewRecord()) {
				DocumentDetails details = covenantDocument.getDocumentDetail();
				details.setRecordStatus(covenantDocument.getRecordStatus());
				details.setRecordType(covenantDocument.getRecordType());
				details.setVersion(covenantDocument.getVersion());
				details.setDocModule(FinanceConstants.MODULE_NAME);
				details.setDocName(covenantDocument.getDocName());
				details.setReferenceId(finreference);
				details.setDocReceived(true);
				details.setFinReference(finreference);
				details.setCustId(covenantDocument.getCustId());
				list.add(covenantDocument.getDocumentDetail());
			}
		}

		List<DocumentDetails> documents = null;

		if (documentDetailDialogCtrl != null) {
			documents = documentDetailDialogCtrl.getDocumentDetailsList();
		}

		if (documents == null) {
			documents = new ArrayList<>();
		}

		for (DocumentDetails currentDoc : documents) {
			if (!FinanceConstants.MODULE_NAME.equals(currentDoc.getDocModule())) {
				continue;
			}

			DocumentDetails item = null;
			for (DocumentDetails covenantDoc : list) {
				if (StringUtils.equals(covenantDoc.getDocCategory(), currentDoc.getDocCategory())) {
					item = currentDoc;
					item.setRecordStatus(covenantDoc.getRecordStatus());
					item.setRecordType(covenantDoc.getRecordType());
					item.setVersion(covenantDoc.getVersion());
					item.setDocModule(FinanceConstants.MODULE_NAME);
					item.setDocName(covenantDoc.getDocName());
					item.setReferenceId(finreference);
					item.setCustId(covenantDoc.getCustId());
				} else {
					item = covenantDoc;
				}

				map.put(item.getDocCategory(), item);

			}
		}

		if (map.isEmpty() && !list.isEmpty()) {
			documents.addAll(list);
		}

		Map<String, DocumentDetails> docMap = new HashMap<>();
		for (DocumentDetails documentDetails : documents) {
			docMap.put(documentDetails.getDocCategory(), documentDetails);
		}

		boolean added = false;
		for (Entry<String, DocumentDetails> doc : map.entrySet()) {
			for (DocumentDetails documentDetails : documents) {
				if (StringUtils.equals(documentDetails.getDocCategory(), map.get(doc.getKey()).getDocCategory())) {
					docMap.put(documentDetails.getDocCategory(), map.get(doc.getKey()));
					added = true;
				}
			}
			if (!added) {
				docMap.put(map.get(doc.getKey()).getDocCategory(), map.get(doc.getKey()));
			}
		}

		if (documentDetailDialogCtrl != null) {
			List<DocumentDetails> Listofvalues = new ArrayList<DocumentDetails>(docMap.values());
			documentDetailDialogCtrl.doFillDocumentDetails(Listofvalues);
		} else {
			this.covenant.setDocumentDetails(list);
		}

		logger.debug(Literal.LEAVING);
	}

	public List<CovenantDocument> getCovenantDocuments() {
		return covenantDocuments;
	}

	public void setCovenantDocuments(List<CovenantDocument> covenantDocuments) {
		this.covenantDocuments = covenantDocuments;
	}

	public void onCheck$allowPostponment(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckAllowPostponement(this.allowPostponment.isChecked());
		logger.debug(Literal.LEAVING);
	}

	public void onCheckAllowPostponement(boolean isAllowPostpone) {
		if (isAllowPostpone) {
			this.extendedDate.setDisabled(false);
		} else {
			this.extendedDate.setText(null);
			this.extendedDate.setDisabled(true);
		}
	}

	public void onChange$covenantGraceDays(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectFrequency(this.covenantFrequency.getSelectedItem().getValue().toString());
		logger.debug(Literal.LEAVING);
	}

	public void clearOnChangeCovenantData() {
		this.covenantType.setValue("");
		this.covenantType.setDescription("");
		this.covenantFrequency.setSelectedIndex(0);
		this.covenantGraceDays.setValue(0);
		this.alertDays.setValue(0);
		this.description.setValue("");
		this.notifyTo.setValue("");
		this.alertsRequired.setChecked(false);
		this.alertType.setSelectedIndex(0);
		this.pdd.setChecked(false);
		this.otc.setChecked(false);
		this.mandRole.setValue(null);
		this.alwWaiver.setChecked(false);
		this.internalUse.setChecked(false);
	}

	public void covenantTypeDetails(CovenantType covenantType) {

		this.covenantType.setValue(covenantTypeObject.getCode());
		this.covenantType.setDescription(covenantTypeObject.getDescription());
		// setting the covenant type(master) payment methods to covenant object
		/*
		 * if (this.covenant != null) { this.covenant.setAllowedPaymentModes(covenantType.getAllowedPaymentModes()); }
		 */

		if ("LOS".equals(covenantTypeObject.getCovenantType())) {
			onCheckLOS(covenantTypeObject.getCovenantType());
			onCheckOTC();
		} else if ("PDD".equals(covenantTypeObject.getCovenantType())) {
			this.pdd.setChecked(true);
			this.allowPostponment.setChecked(covenantTypeObject.isAllowPostPonement());
			onCheckAllowPostponement(covenantTypeObject.isAllowPostPonement());
			oncheckPDD();
			onCheckOTC();
		} else if ("OTC".equals(covenantTypeObject.getCovenantType())) {
			this.otc.setChecked(true);
			onCheckOTC();
		}

		setFrequency(covenantType.getFrequency());

		this.covenantGraceDays.setValue(covenantType.getGraceDays());
		this.alertDays.setValue(covenantType.getAlertDays());
		this.description.setValue(covenantType.getDescription());
		fillList(this.alertType, listAlertType, covenantType.getAlertType());

		if (covenantType.getAlertToRoles() != null) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder roles = new StringBuilder();
			for (String role : covenantType.getAlertToRoles().split(",")) {
				role = StringUtils.trim(role);

				if (roles.length() > 0) {
					roles.append(",");
				}
				roles.append(role);

				SecurityRole securityRole = new SecurityRole();
				securityRole.setRoleCd(role);
				securityRole.setRoleCode(role);

				map.put(role, securityRole);
			}

			this.notifyTo.setValue(roles.toString());
			this.notifyTo.setAttribute("data", map);
			this.notifyTo.setTooltiptext(roles.toString());
			this.notifyTo.setInputAllowed(false);
			this.notifyTo.setSelectedValues(map);
		}
		this.alertsRequired.setChecked(covenantType.isAlertsRequired());
		onCheckAlertsRequired();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedValues(ExtendedCombobox extendedCombobox) {
		Object object = extendedCombobox.getAttribute("data");
		if (object instanceof Map<?, ?>) {
			return (Map<String, Object>) object;
		}
		return null;
	}

	public void disablePDDDetailsGroup() {
		this.notifyTo.setValue(null);
		this.notifyTo.setButtonDisabled(true);
		this.covenantNextFrequencyDate.setText(null);
		this.covenantGraceDueDate.setText(null);
		this.covenantNextFrequencyDate.setDisabled(true);
		this.covenantGraceDays.setReadonly(true);
		// this.alertsRequired.setDisabled(true);
		this.alertsRequired.setChecked(false);
		onCheckAlertsRequired();
	}

	public void enableFrequencyFields() {
		this.covenantGraceDays.setDisabled(false);
		this.notifyTo.setButtonDisabled(false);
		this.alertDays.setDisabled(false);
		this.alertsRequired.setDisabled(false);
		this.alertType.setDisabled(false);
	}

	public void onChange$receivableDate(Event event) {
		if (covenantTypeObject != null && covenantTypeObject.isAlertsRequired()
				&& this.covenantFrequency.getSelectedItem().getValue().equals("O")) {
			this.covenantNextFrequencyDate.setValue(this.receivableDate.getValue());
		}
		setFrequencyDateField(this.covenantFrequency.getSelectedItem().getValue());
	}

	public void onCheck$alertsRequired(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckAlertsRequired();
		logger.debug(Literal.LEAVING);
	}

	public void onCheckAlertsRequired() {
		if (this.alertsRequired.isChecked()) {
			this.covenantGraceDays.setDisabled(false);
			this.alertDays.setDisabled(false);
			this.notifyTo.setButtonDisabled(false);
			this.alertType.setDisabled(false);
		} else {
			this.covenantGraceDays.setDisabled(true);
			this.covenantGraceDays.setValue(0);
			this.alertDays.setDisabled(true);
			this.alertDays.setValue(0);
			this.notifyTo.setButtonDisabled(true);
			this.notifyTo.setValue(null);
			this.alertType.setDisabled(true);
			this.alertType.setSelectedIndex(0);
			this.covenantNextFrequencyDate.setText(null);
			this.covenantGraceDueDate.setText(null);
		}

	}

	public void onSelect$alertType(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectAlertType();
		logger.debug(Literal.LEAVING);
	}

	public void onSelectAlertType() {
		if (this.alertType.getSelectedIndex() == 2) {
			this.notifyTo.setValue(null);
			this.notifyTo.setVisible(false);
			this.label_CovenantsDialog_NotifyTo.setVisible(false);
			this.notifyTo.setTooltiptext("");
		} else {
			this.notifyTo.setVisible(true);
			this.label_CovenantsDialog_NotifyTo.setVisible(true);
		}

	}

}
