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
 * * FileName : ReinstateFinanceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.reinstatefinance;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.ReinstateFinanceService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.FinanceEnquiryListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReinstateFinance/ReinstateFinanceDialog.zul file.
 */
public class ReinstateFinanceDialogCtrl extends GFCBaseCtrl<ReinstateFinance> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(ReinstateFinanceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReinstateFinanceDialog;
	protected ExtendedCombobox finReference;
	protected Textbox custCIF;
	protected Label custShortName;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox finBranch;
	protected ExtendedCombobox finCcy;
	protected CurrencyBox finAmount;
	protected CurrencyBox totDownpayment;
	protected Datebox finStartDate;
	protected Datebox maturityDate;
	protected CurrencyBox totProfit;

	protected Textbox rejectSts;
	protected Textbox rejectRemarks;
	protected Textbox rejectedBy;
	protected Datebox rejectedOn;
	protected Textbox rejectReason;

	protected Groupbox gb_RejectDetails;
	protected Groupbox gb_financeDetails;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanel tabPanel_dialogWindow;

	// not auto wired variables
	private ReinstateFinance reinstateFinance; // overHanded per parameter
	private transient ReinstateFinanceListCtrl reinstateFinanceListCtrl; // overHanded per parameter
	private FinanceEnquiryListCtrl financeEnquiryListCtrl = null;
	private transient boolean rejectedList;
	private Label label_ReinstateFinanceDialog;

	private transient boolean validationOn;

	private boolean enqModule = false;

	// ServiceDAOs / Domain Classes
	private transient ReinstateFinanceService reinstateFinanceService;
	private transient PagedListService pagedListService;
	int finFormatter = 2;
	private FinanceWorkFlowService financeWorkFlowService;
	private FinanceMain financeMain;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	private String moduleDefiner = null;

	/**
	 * default constructor.<br>
	 */
	public ReinstateFinanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReinstateFinanceDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected ReinstateFinance object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ReinstateFinanceDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReinstateFinanceDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("reinstateFinance")) {
				this.reinstateFinance = (ReinstateFinance) arguments.get("reinstateFinance");
				ReinstateFinance befImage = new ReinstateFinance();
				BeanUtils.copyProperties(this.reinstateFinance, befImage);
				this.reinstateFinance.setBefImage(befImage);

				setReinstateFinance(this.reinstateFinance);
			} else {
				setReinstateFinance(null);
			}

			if (!getReinstateFinance().isNewRecord()) {
				doLoadWorkFlow(this.reinstateFinance.isWorkflow(), this.reinstateFinance.getWorkflowId(),
						this.reinstateFinance.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ReinstateFinanceDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(true);
			}
			if (arguments.containsKey("rejectedList")) {
				this.rejectedList = (Boolean) arguments.get("rejectedList");
			}

			// READ OVERHANDED parameters !
			// we get the ReinstateFinanceListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete ReinstateFinance here.
			if (arguments.containsKey("reinstateFinanceListCtrl")) {
				setReinstateFinanceListCtrl((ReinstateFinanceListCtrl) arguments.get("reinstateFinanceListCtrl"));
			} else {
				setReinstateFinanceListCtrl(null);
			}

			if (arguments.containsKey("financeEnquiryListCtrl")) {
				this.setFinanceEnquiryListCtrl((FinanceEnquiryListCtrl) arguments.get("financeEnquiryListCtrl"));
			}

			if (arguments.containsKey("eventCode")) {
				moduleDefiner = (String) arguments.get("eventCode");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// set Field Properties
			doSetFieldProperties();
			if (getReinstateFinance().isNewRecord()) {
				doShowReinstateDialog(financeMain);

			} else {
				doShowDialog(getReinstateFinance());
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReinstateFinanceDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCIF.setWidth("165px");

		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.finAmount.setTextBoxWidth(164);
		this.totDownpayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totDownpayment.setScale(finFormatter);
		this.totDownpayment.setTextBoxWidth(164);
		this.totProfit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totProfit.setScale(finFormatter);
		this.totProfit.setTextBoxWidth(164);

		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finStartDate.setWidth("164px");

		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setWidth("164px");

		this.rejectedOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.rejectedOn.setWidth("164px");

		this.rejectRemarks.setWidth("260px");

		if (!getReinstateFinance().isNewRecord()) {
			if (isWorkFlowEnabled()) {
				this.groupboxWf.setVisible(true);
			} else {
				this.groupboxWf.setVisible(false);
			}
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
		getUserWorkspace().allocateAuthorities("ReinstateFinanceDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public void onClick$btnSave(Event event)
			throws InterruptedException, FileNotFoundException, XMLStreamException, ParseException {
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
		MessageUtil.showHelpWindow(event, window_ReinstateFinanceDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws UnsupportedEncodingException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, FileNotFoundException, XMLStreamException,
			UnsupportedEncodingException, FactoryConfigurationError {
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
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		if (extendedFieldCtrl != null && reinstateFinance.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.reinstateFinance.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReinstateFinance ReinstateFinance
	 */
	public void doWriteBeanToComponents(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");
		doSetFinanceData(aReinstateFinance.getFinID());
		appendPostingDetailsTab();
		appendExtendedFieldDetails(this.moduleDefiner);
		this.recordStatus.setValue(aReinstateFinance.getRecordStatus());
		logger.debug("Leaving");
	}

	private void doSetFinanceData(Long finID) {
		logger.debug("Entering");

		if (finID == null) {
			return;
		}

		ReinstateFinance rf = reinstateFinanceService.getFinanceDetailsById(finID);

		if (rf == null) {
			doClear();
		}

		List<ReasonDetailsLog> reasonDetailsLog = getReinstateFinanceService().getResonDetailsLog(rf.getFinReference());
		finFormatter = CurrencyUtil.getFormat(rf.getFinCcy());
		setCurrencyFieldProperties();
		this.finReference.setValue(rf.getFinReference());
		this.custCIF.setValue(rf.getCustCIF());
		this.custShortName.setValue(rf.getCustShrtName());
		this.finType.setValue(rf.getFinType());
		this.finType.setDescription(rf.getLovDescFinTypeName());
		this.finBranch.setValue(rf.getFinBranch());
		this.finBranch.setDescription(rf.getLovDescFinBranchName());
		this.finCcy.setValue(rf.getFinCcy());
		this.finCcy.setDescription(CurrencyUtil.getCcyDesc(rf.getFinCcy()));
		this.finAmount.setValue(CurrencyUtil.parse(rf.getFinAmount(), finFormatter));
		this.totDownpayment.setValue(CurrencyUtil.parse(rf.getDownPayment(), finFormatter));
		this.finStartDate.setValue(rf.getFinStartDate());
		this.maturityDate.setValue(rf.getMaturityDate());
		this.totProfit.setValue(CurrencyUtil.parse(rf.getTotalProfit(), finFormatter));
		this.rejectSts.setValue(rf.getRejectStatus());
		this.rejectRemarks.setValue(rf.getRejectRemarks());
		this.rejectedBy.setValue(rf.getRejectedBy());
		this.rejectedOn.setValue(rf.getRejectedOn());

		this.gb_RejectDetails.setVisible(true);
		this.gb_financeDetails.setVisible(true);

		String rejectReason = "";
		if (CollectionUtils.isNotEmpty(reasonDetailsLog)) {
			for (ReasonDetailsLog reasonDetailLog : reasonDetailsLog) {
				if (StringUtils.isNotEmpty(rejectReason)) {
					rejectReason = rejectReason.concat(",");
				}
				rejectReason = rejectReason.concat(reasonDetailLog.getRejectReasonDesc());
			}
			this.rejectReason.setValue(rejectReason);
		}
		logger.debug("Leaving");
	}

	private void setCurrencyFieldProperties() {
		logger.debug("Entering");
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.totDownpayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totDownpayment.setScale(finFormatter);
		this.totDownpayment.setTextBoxWidth(164);
		this.totProfit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totProfit.setScale(finFormatter);
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReinstateFinance
	 */
	public void doWriteComponentsToBean(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReinstateFinance.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Adding this field to bean to assign finance workflow based on finance type
			aReinstateFinance.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aReinstateFinance.setFinEvent(this.moduleDefiner);

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aReinstateFinance.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aReinstateFinance
	 */
	public void doShowDialog(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aReinstateFinance.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.finReference.focus();
				if (StringUtils.isNotBlank(aReinstateFinance.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				if (rejectedList) {
					doEdit();
				}
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (rejectedList) {
					this.btnDelete.setVisible(false);
					this.label_ReinstateFinanceDialog.setValue(Labels.getLabel("label_RejectedFinanceDialog.value"));
				}
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aReinstateFinance);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReinstateFinanceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReinstateFinanceDialog_FinReference.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
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
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException, FileNotFoundException, XMLStreamException,
			UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug(Literal.ENTERING);

		final ReinstateFinance aReinstateFinance = new ReinstateFinance();
		BeanUtils.copyProperties(getReinstateFinance(), aReinstateFinance);
		String keyReference = Labels.getLabel("label_ReinstateFinanceDialog_FinReference.value") + " : "
				+ aReinstateFinance.getFinReference();

		doDelete(keyReference, aReinstateFinance);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getReinstateFinance().isNewRecord()) {
			// this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.custCIF.setReadonly(true);
		this.finType.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finAmount.setReadonly(true);
		this.totDownpayment.setReadonly(true);
		this.finStartDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.totProfit.setReadonly(true);
		this.rejectSts.setReadonly(true);
		this.rejectRemarks.setReadonly(true);
		this.rejectedBy.setReadonly(true);
		this.rejectedOn.setDisabled(true);
		this.rejectReason.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.reinstateFinance.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);

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
		this.custCIF.setValue("");
		this.custShortName.setValue("");
		this.finType.setValue("");
		this.finBranch.setValue("");
		this.finCcy.setValue("");
		this.finAmount.setValue(BigDecimal.ZERO);
		this.totDownpayment.setValue(BigDecimal.ZERO);
		this.finStartDate.setText("");
		this.maturityDate.setText("");
		this.totProfit.setValue(BigDecimal.ZERO);
		this.rejectSts.setValue("");
		this.rejectRemarks.setValue("");
		this.rejectedBy.setValue("");
		this.rejectedOn.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public void doSave() throws InterruptedException, FileNotFoundException, XMLStreamException, ParseException {
		logger.debug("Entering");

		final ReinstateFinance aReinstateFinance = new ReinstateFinance();
		BeanUtils.copyProperties(getReinstateFinance(), aReinstateFinance);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ReinstateFinance object with the components data
		doWriteComponentsToBean(aReinstateFinance);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		if (aReinstateFinance.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			aReinstateFinance.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		isNew = aReinstateFinance.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReinstateFinance.getRecordType())) {
				aReinstateFinance.setVersion(aReinstateFinance.getVersion() + 1);
				if (isNew) {
					aReinstateFinance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReinstateFinance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReinstateFinance.setNewRecord(true);
				}
			}
		} else {
			aReinstateFinance.setVersion(aReinstateFinance.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aReinstateFinance, tranType)) {
				refreshList();
				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aReinstateFinance.getNextTaskId())) {
					aReinstateFinance.setNextRoleCode("");
				}
				String msg = "";
				if (StringUtils.isBlank(aReinstateFinance.getNextRoleCode()) && !StringUtils
						.equals(aReinstateFinance.getRecordStatus(), PennantConstants.RCD_STATUS_CANCELLED)) {
					if (StringUtils.isEmpty(aReinstateFinance.getFinPreApprovedRef())) {
						msg = "Finance with Reference : " + aReinstateFinance.getFinReference() + " "
								+ Labels.getLabel("label_ReinstateFinance_Success");
					} else {
						msg = "Finance with Reference : " + aReinstateFinance.getFinReference() + " "
								+ Labels.getLabel("label_ReinstateFinance_PreApproval_Success");
					}
					Clients.showNotification(msg, "info", null, null, -1);
				} else {
					msg = PennantApplicationUtil.getSavingStatus(aReinstateFinance.getRoleCode(),
							aReinstateFinance.getNextRoleCode(), aReinstateFinance.getFinReference(), " Loan ",
							aReinstateFinance.getRecordStatus());
					Clients.showNotification(msg, "info", null, null, -1);
				}

				if (extendedFieldCtrl != null && aReinstateFinance.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		// User Notifications Message/Alert
		publishNotification(Notify.ROLE, aReinstateFinance.getFinReference(), aReinstateFinance);

		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReinstateFinance (ReinstateFinance)
	 * 
	 * @param tranType          (String)
	 * 
	 * @return boolean
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws UnsupportedEncodingException
	 * 
	 */
	protected boolean doProcess(ReinstateFinance aReinstateFinance, String tranType)
			throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aReinstateFinance.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReinstateFinance.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReinstateFinance.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aReinstateFinance.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aReinstateFinance, finishedTasks);

			if (isNotesMandatory(taskId, aReinstateFinance)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aReinstateFinance, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getReinstateFinanceService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.isNotEmpty(FinServiceEvent.REINSTATE)
								&& StringUtils.equals(FinServiceEvent.REINSTATE, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						aReinstateFinance.setScheduleChange(true);
					} else {
						aReinstateFinance.setScheduleChange(false);
					}
				} else {
					ReinstateFinance tReinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aReinstateFinance);
					auditHeader.getAuditDetail().setModelData(tReinstateFinance);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				ReinstateFinance tReinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tReinstateFinance, finishedTasks);

			}

			ReinstateFinance tReinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tReinstateFinance);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aReinstateFinance);
					auditHeader.getAuditDetail().setModelData(tReinstateFinance);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aReinstateFinance, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, ReinstateFinance reinstateFinance, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(reinstateFinance.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, reinstateFinance);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, ReinstateFinance reinstateFinance) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(reinstateFinance.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, reinstateFinance);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		reinstateFinance.setTaskId(taskId);
		reinstateFinance.setNextTaskId(nextTaskId);
		reinstateFinance.setRoleCode(getRole());
		reinstateFinance.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws UnsupportedEncodingException
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReinstateFinance aReinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		if (aReinstateFinance.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aReinstateFinance.getExtendedFieldRender();
			details.setReference(aReinstateFinance.getFinReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aReinstateFinance.getRecordStatus());
			details.setRecordType(aReinstateFinance.getRecordType());
			details.setVersion(aReinstateFinance.getVersion());
			details.setWorkflowId(aReinstateFinance.getWorkflowId());
			details.setTaskId(aReinstateFinance.getTaskId());
			details.setNextTaskId(aReinstateFinance.getNextTaskId());
			details.setRoleCode(aReinstateFinance.getRoleCode());
			details.setNextRoleCode(aReinstateFinance.getNextRoleCode());
			details.setNewRecord(aReinstateFinance.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aReinstateFinance.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aReinstateFinance.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getReinstateFinanceService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getReinstateFinanceService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					String finEvent = "";
					if (StringUtils.equals(FinServiceEvent.PREAPPROVAL, aReinstateFinance.getFinPreApprovedRef())) {
						finEvent = FinServiceEvent.PREAPPROVAL;
					} else {
						finEvent = FinServiceEvent.ORG;
					}
					FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
							aReinstateFinance.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
					if (financeWorkFlow != null) {
						WorkFlowDetails workFlowDetails = WorkFlowUtil
								.getDetailsByType(financeWorkFlow.getWorkFlowType());
						if (workFlowDetails != null) {
							WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
							String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
							aReinstateFinance.setLovDescWorkflowId(workFlowDetails.getWorkFlowId());
							aReinstateFinance.setLovDescRoleCode(workflow.firstTaskOwner());
							aReinstateFinance.setLovDescNextRoleCode(workflow.firstTaskOwner());
							aReinstateFinance.setLovDescTaskId(taskid);
							aReinstateFinance.setLovDescNextTaskId(taskid + ";");
						}
					}

					auditHeader = getReinstateFinanceService().doApprove(auditHeader);

					if (aReinstateFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getReinstateFinanceService().doReject(auditHeader);

					if (aReinstateFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ReinstateFinanceDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ReinstateFinanceDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.reinstateFinance), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aReinstateFinance
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReinstateFinance aReinstateFinance, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReinstateFinance.getBefImage(), aReinstateFinance);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aReinstateFinance.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.reinstateFinance);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getReinstateFinanceListCtrl().search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(getReinstateFinance().getFinReference());
	}

	public void doShowReinstateDialog(FinanceMain details) {
		logger.debug("Entering");

		if (details != null) {
			this.finReference.setValue(details.getFinReference());
			this.finReference.setDescription("");
			doSetFinanceData(details.getFinID());
			this.reinstateFinance.setFinID(details.getFinID());
			getReinstateFinance().setFinPreApprovedRef(details.getFinPreApprovedRef());
			getReinstateFinance().setFinCategory(details.getFinCategory());
			// Workflow Details
			setWorkflowDetails(details.getFinType());
			getReinstateFinance().setWorkflowId(getWorkFlowId());
			appendExtendedFieldDetails(this.moduleDefiner);
			doLoadWorkFlow(this.reinstateFinance.isWorkflow(), this.reinstateFinance.getWorkflowId(),
					this.reinstateFinance.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (getReinstateFinance().isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
				this.groupboxWf.setVisible(true);
			}
			doEdit();
			this.finReference.setReadonly(true);
		} else {
			doClear();
		}
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Postings Enquiry Details.
	 */
	protected void appendPostingDetailsTab() {
		logger.debug("Entering");
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("reinstateFinance", this.reinstateFinance);
			map.put("fromRejectFinance", true);
			map.put("finReference", this.finReference.getValue());
			createTab(AssetConstants.UNIQUE_ID_POSTINGS, true);
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/PostingsEnquiryDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_POSTINGS), map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void appendExtendedFieldDetails(String finEvent) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender extendedFieldRender = null;

		try {

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, reinstateFinance.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());
			extendedFieldCtrl
					.setDataLoadReq((PennantConstants.RCD_STATUS_APPROVED.equals(reinstateFinance.getRecordStatus())
							|| reinstateFinance.getRecordStatus() == null) ? true : false);
			long instructionUID = Long.MIN_VALUE;

			if (CollectionUtils.isNotEmpty(reinstateFinance.getFinServiceInstructions())) {
				if (reinstateFinance.getFinServiceInstruction().getInstructionUID() != Long.MIN_VALUE) {
					instructionUID = reinstateFinance.getFinServiceInstruction().getInstructionUID();
				}
			}
			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(reinstateFinance.getFinReference(),
					instructionUID);

			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			reinstateFinance.setExtendedFieldHeader(extendedFieldHeader);
			reinstateFinance.setExtendedFieldRender(extendedFieldRender);

			if (reinstateFinance.getBefImage() != null) {
				reinstateFinance.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				reinstateFinance.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(reinstateFinance.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_ReinstateFinanceDialog);
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
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<>();
		arrayList.add(0, reinstateFinance.getFinType());
		arrayList.add(1, reinstateFinance.getFinCcy());

		if (StringUtils.isNotEmpty(reinstateFinance.getScheduleMethod())) {
			arrayList.add(2, reinstateFinance.getScheduleMethod());
		} else {
			arrayList.add(2, "");
		}

		arrayList.add(3, reinstateFinance.getFinReference());
		arrayList.add(4, reinstateFinance.getProfitDaysBasis());
		arrayList.add(5, reinstateFinance.getGrcPeriodEndDate());
		arrayList.add(6, reinstateFinance.isAllowGrcPeriod());

		if (StringUtils.isNotEmpty(reinstateFinance.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}

		arrayList.add(8, reinstateFinance.getFinCategory());
		arrayList.add(9, reinstateFinance.getCustShrtName());
		arrayList.add(10, reinstateFinance.isNewRecord());
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");

		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_POSTINGS, moduleID)) {
			tabName = Labels.getLabel("tab_Postings");
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");

		logger.debug("Leaving");
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(FinServiceEvent.REINSTATE)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					FinServiceEvent.REINSTATE, PennantConstants.WORFLOW_MODULE_FINANCE);// TODO : Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
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

	public ReinstateFinance getReinstateFinance() {
		return this.reinstateFinance;
	}

	public void setReinstateFinance(ReinstateFinance reinstateFinance) {
		this.reinstateFinance = reinstateFinance;
	}

	public void setReinstateFinanceService(ReinstateFinanceService reinstateFinanceService) {
		this.reinstateFinanceService = reinstateFinanceService;
	}

	public ReinstateFinanceService getReinstateFinanceService() {
		return this.reinstateFinanceService;
	}

	public void setReinstateFinanceListCtrl(ReinstateFinanceListCtrl reinstateFinanceListCtrl) {
		this.reinstateFinanceListCtrl = reinstateFinanceListCtrl;
	}

	public ReinstateFinanceListCtrl getReinstateFinanceListCtrl() {
		return this.reinstateFinanceListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setFinanceEnquiryListCtrl(FinanceEnquiryListCtrl financeEnquiryListCtrl) {
		this.financeEnquiryListCtrl = financeEnquiryListCtrl;
	}

	public FinanceEnquiryListCtrl getFinanceEnquiryListCtrl() {
		return financeEnquiryListCtrl;
	}

}
